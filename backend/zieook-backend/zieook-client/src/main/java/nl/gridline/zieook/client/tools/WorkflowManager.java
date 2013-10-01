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

import java.util.Date;

import javax.ws.rs.core.Response;

import nl.gridline.zieook.client.cli.WorkFlowCLI;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.workflow.api.CollectionImport;
import nl.gridline.zieook.workflow.api.ContentProviderCRUD;
import nl.gridline.zieook.workflow.api.Recommender;
import nl.gridline.zieook.workflow.api.StatisticsScheduler;
import nl.gridline.zieook.workflow.model.Collection;
import nl.gridline.zieook.workflow.model.CollectionFilter;
import nl.gridline.zieook.workflow.model.Collections;
import nl.gridline.zieook.workflow.model.ContentProvider;
import nl.gridline.zieook.workflow.model.ContentProviders;
import nl.gridline.zieook.workflow.model.RecommenderConfig;
import nl.gridline.zieook.workflow.model.Recommenders;
import nl.gridline.zieook.workflow.model.Template;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-client<br />
 * WorkflowManager.java created Sep 30, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class WorkflowManager extends AbstractCliExecutor
{

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowManager.class);

	private final String server;
	private String recommender;
	private String collection;
	private String cp;

	private WorkFlowCLI options;

	private final Config config;

	// private String type;
	//
	// private String location;
	//
	// private long interval;

	public WorkflowManager() throws ArgumentValidationException
	{
		// -Dzieook.data.port=\"DATA_PORT\" -Dzieook.workflow.port=\"WORKFLOW_PORT\" "-Dzieook.server=\"$ZIEOOK_SERVER\"

		config = Config.getInstance();
		String host = System.getProperty("zieook.server");
		String port = System.getProperty("zieook.workflow.port");
		server = "http://" + host + ":" + port;
	}

	private void processContentProvider()
	{
		if (cp.isEmpty() && !options.isShowCP())
		{
			printHelpAndExit("cannot operate on content provider without the content provider argument", WorkFlowCLI.class);
		}

		ContentProviderCRUD client = getContentProviderCRUD();

		if (options.isContentProvider())
		{
			final Response result = client.createContentProvider(new ContentProvider(cp, new Date().getTime(), true));
			System.out.println(result.getStatus());
		}
		else if (options.isDeleteCP())
		{
			final Response result = client.deleteContentProvider(cp);
			System.out.println(result.getStatus());
		}
		else if (options.isShowCP())
		{
			if (cp.isEmpty())
			{
				final ContentProviders result = client.getContentProviders();
				System.out.println(result.toJSON());
			}
			else
			{
				final ContentProvider result = client.readContentProvider(cp);
				System.out.println(result.toJSON());
			}
		}
	}

	private void processRecommender()
	{
		if (cp.isEmpty() && !options.isShowCollection())
		{
			printHelpAndExit("cannot operate on collection without the content provider argument", WorkFlowCLI.class);
		}

		if (recommender.isEmpty() && !options.isShowRecommender())
		{
			printHelpAndExit("cannot operate on recommender without the recommender argument", WorkFlowCLI.class);
		}

		Recommender client = getRecommender();

		if (options.isCreateRecommender())
		{

			CollectionFilter filter = new CollectionFilter(); // an easy way to define filters.
			Template template = new Template(); // .. get template by name..
			long interval = options.getRecommenderInterval();
			long next = options.getRecommenderNext();

			client.createRecommender(cp, new RecommenderConfig(cp, recommender, collection, filter, template, interval,
					next));
		}
		else if (options.isDeleteRecommender())
		{
			Response result = client.deleteRecommender(cp, recommender);
			System.out.println(result.getStatus());
		}
		else if (options.isShowRecommender())
		{
			if (cp.isEmpty() && recommender.isEmpty())
			{
				final Recommenders result = client.getRecommenders();
				System.out.println(result.toJSON());
			}
			else if (recommender.isEmpty())
			{
				final Recommenders result = client.getRecommenders(cp);
				System.out.println(result.toJSON());
			}
			else
			{
				final RecommenderConfig result = client.readConfiguration(cp, recommender);
				System.out.println(result.toJSON());
			}
		}
	}

	private void processCollection()
	{
		if (cp.isEmpty() && !options.isShowCollection())
		{
			printHelpAndExit("cannot operate on collection without the content provider argument", WorkFlowCLI.class);
		}

		if (collection.isEmpty() && !options.isShowCollection())
		{
			printHelpAndExit("cannot operate on collection without the collection argument", WorkFlowCLI.class);
		}

		CollectionImport client = getCollectionImport();

		if (options.isCreateCollection())
		{
			String type = options.getCollectionType();
			String location = options.getCollectionLocation();
			long interval = options.getCollectionInterval();

			Response result = client.createCollectionMeta(cp, new Collection(cp, collection, type, location, null, null,
					interval));

			System.out.println(result.getStatus());
		}
		else if (options.isDeleteCollection())
		{
			Response result = client.deleteCollection(cp, collection);
			System.out.println(result.getStatus());
		}
		else if (options.isShowCollection())
		{
			if (cp.isEmpty() && collection.isEmpty())
			{
				final Collections result = client.readCollectionsMeta();
				System.out.println(result.toJSON());
			}
			else if (collection.isEmpty())
			{
				final Collections result = client.readCollectionsMeta(cp);
				System.out.println(result.toJSON());
			}
			else
			{
				final Collection result = client.readCollectionMeta(cp, collection);
				System.out.println(result.toJSON());
			}
		}
	}

	/**
	 * get collection import interface
	 * @return
	 */
	private CollectionImport getCollectionImport()
	{
		return ProxyFactory.create(CollectionImport.class, server);
	}

	private ContentProviderCRUD getContentProviderCRUD()
	{
		return ProxyFactory.create(ContentProviderCRUD.class, server);
	}

	private Recommender getRecommender()
	{
		return ProxyFactory.create(Recommender.class, server);
	}

	private StatisticsScheduler getStatisticsScheduler()
	{
		return ProxyFactory.create(StatisticsScheduler.class, server);
	}

	private void runStatistics(String cp)
	{
		StatisticsScheduler scheduler = getStatisticsScheduler();
		Response result = scheduler.runStatistics(cp, "now");
		LOG.info("server says: {}", result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.client.tools.CliExecutor#processRequest(java.lang.String[])
	 */
	@Override
	public void processRequest(String[] args) throws ArgumentValidationException
	{
		if (!super.confirmYesNo("using server: <" + server + "> is this correct? [yes/no]"))
		{
			return;
		}

		options = CliFactory.parseArguments(WorkFlowCLI.class, args);

		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		recommender = options.getCollection();
		collection = options.getRecommender();
		cp = options.getContentProvider();

		// type = options.getCollectionType();
		// location = options.getCollectionLocation();
		// interval = options.getCollectionInterval();

		if (options.isContentProvider() || options.isDeleteCP() || options.isShowCP())
		{
			// content provider stuff:
			processContentProvider();
		}
		else if (options.isCreateCollection() || options.isDeleteCollection() || options.isShowCollection())
		{
			// collection stuff:
			processCollection();
		}
		else if (options.isCreateRecommender() || options.isDeleteRecommender() || options.isShowRecommender())
		{
			// recommender stuff:
			processRecommender();
		}
		else if (options.isExecuteStatistics())
		{
			if (cp.isEmpty())
			{
				printHelpAndExit("please provide a content provider", WorkFlowCLI.class);
			}
			else
			{
				runStatistics(cp);
			}
		}
		{
			printHelpAndExit("no valid argument passed", WorkFlowCLI.class);
		}

	}

	// private StatisticsScheduler getStatisticsScheduler()
	// {
	// return ProxyFactory.create(StatisticsScheduler.class, server);
	// }

	// private TemplateCRUD getTemplateCRUD()
	// {
	// return ProxyFactory.create(TemplateCRUD.class, server);
	// }

}
