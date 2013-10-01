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
package nl.gridline.zieook.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import nl.gridline.zieook.commons.ZieOokCommons;
import nl.gridline.zieook.commons.ZieOokManager;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.TableOperations;
import nl.gridline.zieook.data.hbase.dao.CollectionMetaTable;
import nl.gridline.zieook.data.hbase.dao.ContentProviderTable;
import nl.gridline.zieook.data.hbase.dao.EventLogTable;
import nl.gridline.zieook.data.hbase.dao.RecommenderTable;
import nl.gridline.zieook.data.hbase.dao.TemplateTable;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseRecommendationTable;
import nl.gridline.zieook.data.hbase.model.HBaseStatisticsTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.exceptions.TemplateDoesNotExist;
import nl.gridline.zieook.model.DataManagerSettings;
import nl.gridline.zieook.workflow.model.ContentProvider;
import nl.gridline.zieook.workflow.model.ContentProviders;
import nl.gridline.zieook.workflow.model.RecommenderConfig;
import nl.gridline.zieook.workflow.model.Recommenders;
import nl.gridline.zieook.workflow.model.Template;
import nl.gridline.zieook.workflow.model.Templates;
import nl.gridline.zieook.workflow.model.WorkflowState;

import org.apache.commons.lang.NotImplementedException;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The workflow engine main class
 * <p />
 * Project zieook-api-workflow<br />
 * WorkflowEngine.java created 14 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date: 2011-03-16 17:24:56 +0100 (Wed, 16 Mar
 *          2011) $
 */
public class WorkflowController implements ZieOokManager
{
	public static final String ZIEOOK_WORKFLOW = "nl.zieook.workflow.controller";

	private final long start; // controller start name

	private Config config;
	private boolean state;

	private HBaseManager manager;
	private RecommenderTable recommenderTable;
	private EventLogTable eventLogTable;
	private TemplateTable templateTable;
	private ContentProviderTable cpTable;

	private final WorkflowScheduler scheduler;

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowController.class);

	/**
	 * Start the workflow engine
	 */
	public WorkflowController(WorkflowScheduler scheduler)
	{
		start = System.currentTimeMillis();
		this.scheduler = scheduler;
	}

	@Override
	public void startup() throws Exception
	{
		LOG.info(ZieOokCommons.someinformation("WorkflowController"));
		config = Config.getInstance(Config.ZIEOOK_WORKFLOW_FILE);
		String zookeeper = config.getZooKeeperHost();

		state = zookeeper != null;
		if (state)
		{
			LOG.info("creating hbase manager using: {}", zookeeper);
			manager = HBaseManager.getInstance(zookeeper);

			HBaseConfigurationTable table = new HBaseConfigurationTable(manager);
			if (!table.tableExists())
			{
				state = table.create();
				if (state)
				{
					LOG.info("Configuration table did not exists, and was created");
				}
				else
				{
					LOG.info("Configuration table did not exist, and could not be created");
				}
			}

			recommenderTable = new RecommenderTable(table);
			templateTable = new TemplateTable(table);
			cpTable = new ContentProviderTable(table);
			eventLogTable = new EventLogTable(new HBaseEventLogTable(manager));
		}

	}

	@Override
	public void shutdown() throws Exception
	{

	}

	@Override
	public boolean state()
	{
		return state;
	}

	@Override
	public WorkflowState getState()
	{
		WorkflowState state = new WorkflowState();

		// set the configuration filename:
		state.setConfigurationFile(Config.ZIEOOK_WORKFLOW_FILE);
		// set the zookeeper
		state.setZooKeeper(config.getZooKeeperHost());
		// assign settings
		state.setSettings(new DataManagerSettings(config.getMap()));
		// start time:
		state.setUptime(new Date(start).toString());

		// add the content providers names to the state:
		List<ContentProvider> providers = cpTable.getContentProviders();
		List<String> providersStr = new ArrayList<String>(providers.size());
		for (ContentProvider cp : providers)
		{
			providersStr.add(cp.getName());
		}
		Collections.sort(providersStr);
		state.setContentProviders(providersStr);

		// get the recommenders per cp:

		List<String> recommenders = new ArrayList<String>();
		for (String cp : providersStr)
		{
			Recommenders recommenderObj = getRecommenders(cp);
			for (RecommenderConfig r : recommenderObj.getRecommenders())
			{
				recommenders.add(r.getCp() + '/' + r.getCollection() + '/' + r.getRecommender());
			}
		}
		Collections.sort(recommenders);
		state.setRecommender(recommenders);

		return state;
	}

	/**
	 * Return a list of available recommenders for the given content provider
	 * @param cp
	 *           - content provider name
	 * @return a string list
	 */
	public Recommenders getRecommenders(String cp)
	{
		List<RecommenderConfig> result = recommenderTable.getRecommenders(cp);
		LOG.debug("listing recommenders {}", result);
		return new Recommenders(result);
	}

	public Recommenders getRecommenders()
	{
		List<RecommenderConfig> result = recommenderTable.getRecommenders();
		LOG.debug("returning: {}", result);
		return new Recommenders(result);
	}

	/**
	 * Get the configuration for a recommender instance
	 * @param cp
	 *           - content provider name
	 * @param recommender
	 *           - recommender name
	 * @return the recommender configuration
	 */
	public RecommenderConfig readRecommender(String cp, String recommender)
	{
		RecommenderConfig result = recommenderTable.getRecommender(cp, recommender);
		LOG.debug("returning: {}", result);
		return result;
	}

	/**
	 * Create a recommender (and schedule the it as a task)
	 * @param cp content provider
	 * @param recommender recommender
	 */
	public void createRecommender(String cp, RecommenderConfig recommender)
	{
		recommenderTable.putRecommender(recommender);
	}

	/**
	 * Update a recommender configuration
	 * @param cp a content provider
	 * @param recommender a recommender
	 * @param update the updated recommender configuration
	 */
	public void updateRecommender(String cp, String recommender, RecommenderConfig update)
	{
		if (!recommender.equals(update.getRecommender()))
		{
			// delete the old one, if the named changed!
			recommenderTable.deleteRecommender(cp, recommender);
		}
		recommenderTable.putRecommender(update);
	}

	/**
	 * Delete a recommender configuration
	 * @param cp
	 * @param recommender
	 */
	public void deleteRecommender(String cp, String recommender)
	{
		// cancel any scheduled tasks:
		scheduler.cancelRecommenderTask(cp, recommender);
		scheduler.cancelRecommenderTask(cp, recommender);

		eventLogTable.deleteViews(cp, recommender);
		eventLogTable.deleteRecommend(cp, recommender);
	}

	/**
	 * create a new template - a recommender configuration
	 * @param template
	 */
	public void createTemplate(Template template)
	{
		if (!templateTable.exists(template.getName()))
		{
			templateTable.putTemplate(template);
		}
	}

	/**
	 * retrieve a template given it's name
	 * @param name
	 * @return
	 */
	public Template readTemplate(String name)
	{
		Template result = templateTable.getTemplate(name);
		if (result == null)
		{
			throw new TemplateDoesNotExist(name);
		}
		return result;
	}

	/**
	 * Update a template given it's name
	 * @param name template name
	 * @param template template data
	 */
	public void updateTemplate(String name, Template template)
	{
		// overwrite existing one, or create a new one if the name changed - ie
		// a new row
		if (!name.equals(template.getName()))
		{
			templateTable.deleteTemplate(name);
		}
		templateTable.putTemplate(template);
	}

	/**
	 * Delete a template - a recommender configuration template
	 * @param name
	 */
	public void deleteTemplate(String name)
	{
		templateTable.deleteTemplate(name);
	}

	public Templates listTemplates()
	{
		return templateTable.listTemplates();
	}

	/**
	 * Create a new content provider - it will insert a meta data row ant create all the content provider related tables
	 * in HBase
	 * @param cp
	 * @return http response code (200, if successful)
	 */
	public void createContentProvider(ContentProvider cp)
	{

		// first do a delete: (in case there is any existing data left)
		deleteContentProvider(cp.getName());

		// create cp related table:
		boolean result = createTables(cp.getName());

		// only if that succeeded, create a content provider element in the
		// database:
		if (result)
		{
			cpTable.putContentProvider(cp);
		}
		else
		{
			// or throw an error, if it failed:
			LOG.error("failed to create content provider for " + cp.getName()
					+ " -  you may need to check the server logs");

			throw new InternalServerErrorException("failed to create content provider for " + cp.getName()
					+ " -  you may need to check the server logs");
		}

	}

	/**
	 * Retrieve the information about a content provider
	 * @param cp a content provider
	 * @return
	 */
	public ContentProvider readContentProvider(String cp)
	{
		return cpTable.getContentProvider(cp);
	}

	/**
	 * Modify the given content provider name
	 * @param name content provider name
	 * @param cp the updated information about this content provider
	 */
	public void updateContentProvider(String name, ContentProvider cp)
	{
		// this is not something we support for now... - a cp can only be
		// renamed, and that's very hard to accomplish. it
		// means we would have to rename a lot of stuff!
		cpTable.putContentProvider(cp);
	}

	/**
	 * Delete the given content provider & all of it's data - if there is a lot of data, this may take some time
	 * @param cp a content provider
	 */
	public void deleteContentProvider(String cp)
	{
		// cancel and delete all tasks:
		scheduler.cancelAllTask(cp);
		scheduler.deleteAllTasks(cp);

		// first delete the table
		boolean result = destroyTables(cp);
		if (!result)
		{
			LOG.error("Some table may still exists, this can cause inconsistency in the database - in the long run");
		}

		// remove all recommender- / collection- metadata:
		destroyConfiguration(cp);

		// delete all tables:
		destroyTables(cp);

		// delete meta data for this content provider:
		cpTable.deleteContentProvider(cp);

	}

	/**
	 * Return the list of all known content providers. - for now we simply return them all (as we are probably running in
	 * a simple system)
	 * @return
	 */
	public ContentProviders getContentProviders()
	{
		return new ContentProviders(cpTable.getContentProviders());
	}

	// public List<Long> getTasks(String cp, String type, String value, long start, long end)
	// {
	// return WorkflowScheduler.getInstance().getTasks(cp, type, value, start, end);
	// }
	//
	// public TaskConfig getTask(long id)
	// {
	// return WorkflowScheduler.getInstance().getTask(id);
	// }
	//
	// public TaskConfig getCompletedTask(String cp, String type, String value)
	// {
	// return WorkflowScheduler.getInstance().getCompletedTask(cp, type, value);
	// }

	/**
	 * check existence of a content provider - we may need to add a local cache.
	 * @param cp
	 *           - the content provider name
	 * @return <tt>true</tt> if it exists, <tt>false</tt> otherwise
	 */
	public boolean isContentProvider(String cp)
	{
		return cpTable.isContentProvider(cp);
	}

	public boolean isRecommender(String cp, String recommender)
	{
		return recommenderTable.isRecommender(cp, recommender);
	}

	/**
	 * @param recommender
	 * @return
	 */
	public List<String> getRecommenderCP(String recommender)
	{
		throw new NotImplementedException();
	}

	// ----------------------------------------------
	// ---------- Internal helper methods -----------
	// ----------------------------------------------

	// /**
	// * return a sorted map of {@code recommenders} per {@code collection}
	// * @param cp content provider
	// * @return a sorted map
	// */
	// private SortedMap<String, List<String>> getCollectionsRecommenders(String cp)
	// {
	// SortedMap<String, List<String>> result = new TreeMap<String, List<String>>();
	//
	// List<Collection> collections = collectionMeta.getCollections(cp);
	// for (Collection collection : collections)
	// {
	// String name = collection.getName();
	// List<String> recommenders = recommenderTable.getRecommenderList(name);
	// result.put(name, recommenders);
	// }
	//
	// return result;
	// }

	// /**
	// * Return the list of recommender names for this {@code cp} & {@code collection}
	// * @param cp content provider
	// * @param collection collection name
	// * @return a list of recommender names
	// */
	// private List<String> getRecommenders(String cp, String collection)
	// {
	// return recommenderTable.getRecommenderList(cp, collection);
	// }
	//
	// private String getRecommenderCollection(String cp, String recommender)
	// {
	// return recommenderTable.getRecommenderCollection(cp, recommender);
	// }

	private boolean createTables(String cp)
	{
		// create tables that are related to a specific content provider
		HBaseCollectionTable cptable = new HBaseCollectionTable(manager);
		HBaseRecommendationTable rdtable = new HBaseRecommendationTable(manager);
		HBaseEventLogTable ustable = new HBaseEventLogTable(manager);
		HBaseStatisticsTable stable = new HBaseStatisticsTable(manager);
		HBaseUserTable utable = new HBaseUserTable(manager);

		// check existence:
		boolean result = !exists(cptable, cp) && !exists(rdtable, cp) && !exists(ustable, cp) && !exists(utable, cp)
				&& !(exists(stable, cp));

		if (!result)
		{
			// fail:
			LOG.error("Some of the tables already exist, please consult a system admin, zieook is inconsistent");
			LOG.error("{}/collection  exists? ={}", cp, exists(cptable, cp));
			LOG.error("{}/recommendation exists?={}", cp, exists(rdtable, cp));
			LOG.error("{}/event-log exists?={}", cp, exists(ustable, cp));
			LOG.error("{}/users exists?={}", cp, !exists(utable, cp));
			LOG.error("{}/statistics exists?={}", cp, !exists(stable, cp));
			// stop!
			return result;
		}

		// continue:
		result = create(cptable, cp) && create(rdtable, cp) && create(ustable, cp) && create(utable, cp)
				&& create(stable, cp);

		if (!result)
		{
			// fail!! - try to undo
			LOG.error("Failed to create tables for content provider '{}' - trying to undo", cp);

			// undo! -> which does not really really fix the problem
			boolean undo = dropIfExist(cptable, cp) && dropIfExist(rdtable, cp) && dropIfExist(ustable, cp)
					&& dropIfExist(utable, cp);

			// complete ... failure!!
			if (!undo)
			{
				LOG.error("Failed to undo incomplete table create for '{}' - Can't handle it any further - A system admin need to fix this problem!!");
			}
			else
			{
				LOG.error("Damage undone, table for {} are removed again", cp);
			}
		}
		else
		{
			LOG.info("All related table for '{}' created", cp);
		}

		return result;
	}

	/**
	 * removes all configuration data for the given content provider,i.e. <br />
	 * <ul>
	 * <li>collection meta data</li>
	 * <li>configured recommenders</li>
	 * </ul>
	 * @param cp
	 */
	private void destroyConfiguration(String cp)
	{
		HBaseConfigurationTable table = new HBaseConfigurationTable(manager);

		// clear collection meta data:
		CollectionMetaTable collection = new CollectionMetaTable(table);
		List<String> collections = collection.getCollectionsList(cp);
		LOG.info("removing {} collection meta-data elements for {}", collections.size(), cp);
		for (String c : collections)
		{
			collection.deleteCollection(cp, c);
			LOG.debug("removed collection='{}' for cp='{}'", c, cp);
		}

		// clear recommenders:
		RecommenderTable recommender = new RecommenderTable(table);
		List<String> recommenders = recommender.getRecommenderList(cp);
		LOG.info("removing {} recommenders for {}", recommenders.size(), cp);
		for (String r : recommenders)
		{
			recommender.deleteRecommender(cp, r);
			LOG.debug("removed recommender='{}' for cp='{}'", r, cp);
		}

	}

	/**
	 * This will destroy a content provider and all of it's related data - when
	 * running in fully distributed mode on a large set of data, this may take
	 * some time.
	 * @param cp
	 * @return <tt>true</tt> if successful
	 */
	private boolean destroyTables(String cp)
	{
		LOG.info("destroying content data provider: {}", cp);
		HBaseCollectionTable ctable = new HBaseCollectionTable(manager);
		HBaseRecommendationTable rdtable = new HBaseRecommendationTable(manager);
		HBaseEventLogTable ustable = new HBaseEventLogTable(manager);
		HBaseUserTable utable = new HBaseUserTable(manager);

		boolean result = dropIfExist(ctable, cp);
		result = result && dropIfExist(rdtable, cp);
		result = result && dropIfExist(ustable, cp);
		result = result && dropIfExist(utable, cp);

		return result;
	}

	private boolean create(TableOperations table, String name)
	{
		LOG.info("about to create a table for: '{}' name <{}>", name, table.getTableName(name));
		boolean result = table.create(name);
		if (!result)
		{
			LOG.error("Failed to create table <{}> for '{}'", table.getTableName(name), name);
		}

		return result;
	}

	/**
	 * Drops the table if it exists, and show errors if it failed
	 * @param table
	 *           - the table operations table
	 * @param name
	 *           - the table instance name
	 * @return <tt>true</tt> if success, <tt>false</tt> otherwise
	 */
	private boolean dropIfExist(TableOperations table, String name)
	{
		boolean result = true;
		if (table.tableExists(name))
		{
			result = table.drop(name);
			if (!result)
			{
				LOG.error("Failed to drop <{}> table for '{}' - HBase might be inconsistent now! ",
						table.getTableName(name), name);
			}
		}
		else
		{
			LOG.info("Table <{}> does not exist, drop skipped for '{}'", table.getTableName(name), name);
			result = true;
		}

		return result;
	}

	/**
	 * check if a table instance for the given name exists
	 * @param table
	 *           - the table
	 * @param name
	 *           - the table name
	 * @return <tt>true</tt> if it exists, <tt>false</tt> otherwiseF
	 */
	private boolean exists(TableOperations table, String name)
	{
		boolean result = table.tableExists(name);
		if (result)
		{
			LOG.error("The table <{}> already exists for this content provider '{}', creation failed: {}",
					table.getTableName(name), name);
		}
		return result;
	}

}
