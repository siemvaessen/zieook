/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 */
package nl.gridline.zieook.client.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.gridline.zieook.client.cli.HBaseCLI;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.CollectionMetaTable;
import nl.gridline.zieook.data.hbase.dao.ContentProviderTable;
import nl.gridline.zieook.data.hbase.dao.RecommenderTable;
import nl.gridline.zieook.data.hbase.dao.TemplateTable;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseRecommendationTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.workflow.model.Collection;
import nl.gridline.zieook.workflow.model.CollectionFilter;
import nl.gridline.zieook.workflow.model.ContentProvider;
import nl.gridline.zieook.workflow.model.Module;
import nl.gridline.zieook.workflow.model.RecommenderConfig;
import nl.gridline.zieook.workflow.model.Template;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

/**
 * Operates directly on HBase - no ZieOok intermediate.
 * <p />
 * Project zieook-client<br />
 * HBaseOperations.java created 25 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class HBaseOperations extends AbstractCliExecutor
{
	private static final Logger LOG = LoggerFactory.getLogger(HBaseOperations.class);

	private final HBaseManager manager;
	private final Config config;

	public HBaseOperations() throws MasterNotRunningException, ZooKeeperConnectionException
	{
		config = Config.getInstance();
		String zookeeper = config.getZooKeeperHost();
		manager = HBaseManager.getInstance(zookeeper);
	}

	/**
	 * Show all tables in the configured HBase system
	 * @throws IOException
	 */
	private StringBuilder showTables() throws IOException
	{
		List<String> tables = manager.getTableNames();
		StringBuilder b = new StringBuilder();
		b.append("\n\tZieOok tables: \n");
		for (String table : tables)
		{
			b.append("\t\t").append(table).append('\n');
		}
		return b;
	}

	/**
	 * Drops all(!) tables in HBase, data will be gone, deleted and cannot be retrieved anymore.
	 * This method should obviously never be called on a live system and is only used when testing ZieOok itself
	 * @throws IOException
	 */
	private void dropAllTables() throws IOException
	{
		if (!confirmReallyYesNo("delete all tables from HBase"))
		{
			return;
		}

		List<String> tables = manager.getTableNames();
		for (String table : tables)
		{
			long start = System.currentTimeMillis();
			LOG.info("Disable: {}", table);
			manager.disableTable(table);
			LOG.info("Drop: {}", table);
			manager.dropTable(table);
			long time = System.currentTimeMillis() - start;
			LOG.info("Done {} in {}ms", table, time);
		}
	}

	/**
	 * Initialize the ZieOok system on HBase. Creates the configuration table that allow for the creation of:
	 * Collection meta data, Content provider meta data, recommender configurations, tasks, and templates.
	 */
	private void initZieOok()
	{
		HBaseConfigurationTable table = new HBaseConfigurationTable(manager);
		LOG.info("checking ZieOok configuration table");
		if (!table.tableExists())
		{
			table.create();
			LOG.info("table created: {}", table.getTableName());
		}
		else
		{
			LOG.info("table already exists: {}", table.getTableName());
		}
	}

	/**
	 * Create a content provider
	 * @param cp
	 */
	private void createCP(String cp)
	{
		HBaseConfigurationTable table = new HBaseConfigurationTable(manager);
		if (!table.tableExists())
		{
			table.create();
		}

		ContentProviderTable cpTable = new ContentProviderTable(table);
		cpTable.putContentProvider(new ContentProvider(cp, (System.currentTimeMillis() / 1000), true));

		HBaseCollectionTable cptable = new HBaseCollectionTable(manager);
		HBaseRecommendationTable rdtable = new HBaseRecommendationTable(manager);
		HBaseEventLogTable ustable = new HBaseEventLogTable(manager);
		HBaseUserTable utable = new HBaseUserTable(manager);
		if (!cptable.tableExists(cp))
		{
			cptable.create(cp);
			LOG.info("created collection table for {}", cp);
		}
		if (!rdtable.tableExists(cp))
		{
			rdtable.create(cp);
			LOG.info("created recommendation table for {}", cp);
		}
		if (!ustable.tableExists(cp))
		{
			ustable.create(cp);
			LOG.info("created user statisticstable for {}", cp);
		}
		if (!utable.tableExists(cp))
		{
			utable.create(cp);
			LOG.info("created user table for {}", cp);
		}
	}

	/**
	 * create a collection in an existing content provider
	 * @param cp
	 * @param collection
	 * @param type
	 * @param location
	 */
	private void createCollection(String cp, String collection, String type, String location)
	{
		HBaseConfigurationTable table = new HBaseConfigurationTable(manager);
		CollectionMetaTable collectionTable = new CollectionMetaTable(table);
		collectionTable.putCollection(cp, new Collection(cp, collection, "movielens", location, null, null, -1));
	}

	private void createCollectionDataTable(String cp)
	{
		HBaseCollectionTable collectionDataTable = new HBaseCollectionTable(manager);
		collectionDataTable.create(cp);
	}

	/**
	 * create a recommender for a given content provider, and collection
	 * @param cp
	 * @param collection
	 * @param recommender
	 */
	private void createRecommender(String cp, String collection, String recommender)
	{
		HBaseConfigurationTable table = new HBaseConfigurationTable(manager);
		RecommenderTable recommenderTable = new RecommenderTable(table);
		recommenderTable.putRecommender(new RecommenderConfig(cp, recommender, collection, new CollectionFilter("empty",
				"categories", new ArrayList<String>()), new Template("empty", new ArrayList<Module>()), 0, -1));
	}

	/**
	 * get (the configuration) of a given recommender
	 * @param cp
	 * @param recommender
	 * @return
	 */
	private String getRecommender(String cp, String recommender)
	{
		HBaseConfigurationTable table = new HBaseConfigurationTable(manager);
		RecommenderTable recTable = new RecommenderTable(table);

		RecommenderConfig result = recTable.getRecommender(cp, recommender);
		if (result != null)
		{
			return result.toJSON();
		}
		else
		{
			return null;
		}
	}

	// zieook_collection_cpjob

	private void createTemplates()
	{
		LOG.info("creating templates...");
		HBaseConfigurationTable table = new HBaseConfigurationTable(manager);
		TemplateTable templateTable = new TemplateTable(table);
		templateTable.putTemplate(HBaseConfigurationTable.getDefaultItemTemplate());
		LOG.info("default item template created");
		templateTable.putTemplate(HBaseConfigurationTable.getDefaultUserTemplate());
		LOG.info("default user template created");
		templateTable.putTemplate(HBaseConfigurationTable.getDefaultTextTemplate());
		LOG.info("default text template created");

	}

	private void deleteRecommender(String cp, String recommender, String collection)
	{
		HBaseConfigurationTable table = new HBaseConfigurationTable(manager);
		RecommenderTable recommenderTable = new RecommenderTable(table);
		recommenderTable.deleteRecommender(cp, recommender);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.client.tools.CliExecutor#processRequest(java.lang.String[])
	 */
	@Override
	public void processRequest(String[] args) throws ArgumentValidationException, IOException
	{
		HBaseCLI options = CliFactory.parseArguments(HBaseCLI.class, args);

		if (options.isCreateTemplates())
		{
			createTemplates();
		}
		else if (options.isDropAllTable())
		{
			dropAllTables();
		}
		else if (options.isDeleteRecommender())
		{
			String recommender = options.getCollection();
			String collection = options.getRecommender();
			String cp = options.getContentProvider();
			if (recommender.isEmpty() || collection.isEmpty() || cp.isEmpty())
			{
				System.out.println(getHelp(HBaseCLI.class));
				System.exit(-1);
			}
			else
			{
				deleteRecommender(cp, recommender, collection);
			}
		}
		else if (options.isInitZieOok())
		{
			initZieOok();
		}
		// else if (options.isReadRecommender())
		// {
		// // String result = operations.readRecommenders(null, null);
		// // System.out.println(result);
		// }
		else if (options.isShowTable())
		{
			StringBuilder result = showTables();
			System.out.println(result.toString());
		}
		else if (options.getHelp())
		{
			System.out.println(getHelp(HBaseCLI.class));
			System.exit(-1);
		}
		else
		{
			System.out.println(getHelp(HBaseCLI.class));
			System.exit(-1);
		}
		LOG.info("done");
	}

}
