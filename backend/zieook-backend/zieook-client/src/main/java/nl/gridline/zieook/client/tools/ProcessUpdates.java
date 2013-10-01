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
import java.util.List;

import nl.gridline.zieook.client.cli.ProcessUpdatesCLI;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.ContentProviderTable;
import nl.gridline.zieook.data.hbase.dao.UserTable;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseRecommendationTable;
import nl.gridline.zieook.data.hbase.model.HBaseStatisticsTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.workflow.model.ContentProvider;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-client<br />
 * ProcessUpdates.java created 16 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class ProcessUpdates extends AbstractCliExecutor
{

	private static final Logger LOG = LoggerFactory.getLogger(ProcessUpdates.class);

	private final Config config;

	private final HBaseCollectionTable collectionTable;
	private final HBaseEventLogTable eventlogTable;
	private final HBaseRecommendationTable recommendationTable;
	private final HBaseStatisticsTable statisticsTable;
	private final HBaseUserTable userTable;
	private final HBaseConfigurationTable configTable;

	public ProcessUpdates() throws MasterNotRunningException, ZooKeeperConnectionException
	{
		config = Config.getInstance();
		HBaseManager manager = HBaseManager.getInstance(config.getZooKeeperHost());

		// generic table:
		configTable = new HBaseConfigurationTable(manager);

		// cp specific tables:
		collectionTable = new HBaseCollectionTable(manager);
		eventlogTable = new HBaseEventLogTable(manager);
		recommendationTable = new HBaseRecommendationTable(manager);
		statisticsTable = new HBaseStatisticsTable(manager);
		userTable = new HBaseUserTable(manager);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.client.tools.CliExecutor#processRequest(java.lang.String[])
	 */
	@Override
	public void processRequest(String[] args) throws IOException, ArgumentValidationException
	{
		ProcessUpdatesCLI options = CliFactory.parseArguments(ProcessUpdatesCLI.class, args);
		if (options.isUpdateTables())
		{
			updateTables();
		}
		else if (options.isUpdateUserRatingCount())
		{
			updateUserRatingCount();
		}
		else
		{
			printHelpAndExit("no valid argument passed", ProcessUpdatesCLI.class);
		}
	}

	// ----------------------
	// --- cli processors ---
	// ----------------------

	/**
	 * @throws IOException
	 */
	private void updateTables() throws IOException
	{
		// confirm update...

		// check if servers are running, they should not(!)

		// HBase configuration table update:
		if (configTable.update())
		{
			LOG.info("table updated successfuly");
		}

		// get all content providers
		List<ContentProvider> cps = getContentProviders();
		for (ContentProvider cp : cps)
		{
			String name = cp.getName();
			LOG.info("checking if <{}> needs to be updated", cp);
			if (updateTables(name))
			{
				LOG.info("tables updates sucessfully for <{}>", cp);
			}
		}
	}

	public void updateUserRatingCount()
	{
		// check user rating count consistency:

		UserTable users = new UserTable(userTable);

		List<ContentProvider> cps = getContentProviders();

		for (ContentProvider cp : cps)
		{
			String name = cp.getName();
			LOG.info("checking if <{}> needs to be updated", cp);
			if (userTable.tableExists(name))
			{
				users.updateCounts(name);
			}
			else
			{
				LOG.error("Content provider <{}> misses a table: {}", name, userTable.getTableName(name));
			}

		}
	}

	// ----------------------
	// --- helper methods ---
	// ----------------------

	private boolean updateTables(String cp) throws IOException
	{
		boolean result = collectionTable.update(cp);
		result = result || eventlogTable.update(cp);
		result = result || recommendationTable.update(cp);
		result = result || statisticsTable.update(cp);
		result = result || userTable.update(cp);
		return false;
	}

	private List<ContentProvider> getContentProviders()
	{
		ContentProviderTable table = new ContentProviderTable(configTable);
		return table.getContentProviders();
	}

}
