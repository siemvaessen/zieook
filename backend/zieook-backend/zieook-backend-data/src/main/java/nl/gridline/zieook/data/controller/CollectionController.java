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
package nl.gridline.zieook.data.controller;

import java.util.ArrayList;
import java.util.List;

import nl.gridline.zieook.commons.ServerState;
import nl.gridline.zieook.commons.ZieOokCommons;
import nl.gridline.zieook.commons.ZieOokManager;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.CollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.model.Categories;
import nl.gridline.zieook.model.CollectionItem;
import nl.gridline.zieook.model.CollectionItems;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller that handles reading collection items
 * <p />
 * Project zieook-backend-data<br />
 * CollectionItemController.java created 23 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class CollectionController implements ZieOokManager
{

	private static final Logger LOG = LoggerFactory.getLogger(CollectionController.class);

	public static final String NAME = "nl.zieook.datamanager.collection";

	private boolean state;

	private Config config;

	private HBaseManager manager;

	private CollectionTable collectionTable;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.commons.ZieOokManager#startup()
	 */
	@Override
	public void startup() throws MasterNotRunningException, ZooKeeperConnectionException
	{
		LOG.info(ZieOokCommons.someinformation("CollectionItemController"));

		// create configuration object:
		LOG.info("loading configuration from: {}", Config.ZIEOOK_DATAMANAGER_FILE);
		config = Config.getInstance(Config.ZIEOOK_DATAMANAGER_FILE);

		// get configuration setting for zookeeper:
		String zookeeper = config.getZooKeeperHost();
		state = zookeeper != null;

		if (state)
		{
			LOG.info("creating hbase manager using: {}", zookeeper);
			manager = HBaseManager.getInstance(zookeeper);

			HBaseConfigurationTable cTable = new HBaseConfigurationTable(manager);
			if (!cTable.tableExists())
			{
				state = cTable.create();
				if (state)
				{
					LOG.info("Configuration table did not exists, and was created");
				}
				else
				{
					LOG.info("Configuration table did not exist, and could not be created");
				}
			}

			collectionTable = new CollectionTable(new HBaseCollectionTable(manager));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.commons.ZieOokManager#shutdown()
	 */
	@Override
	public void shutdown()
	{
		LOG.info("CollectionItemController shutdown");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.commons.ZieOokManager#state()
	 */
	@Override
	public boolean state()
	{
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.commons.ZieOokManager#getState()
	 */
	@Override
	public ServerState getState()
	{
		ServerState result = new ServerState();

		// set data...

		return result;
	}

	/**
	 * return the collection categories.
	 * @param cp
	 *           content provider
	 * @param collection
	 *           collection name
	 * @param from
	 *           optional category to start, they are guaranteed to be in
	 *           alphabetical order.
	 * @return a list of categories.
	 */
	public Categories getCollectionCategories(String cp, String collection, String from)
	{
		return collectionTable.getCategories(cp, collection, from);
	}

	/**
	 * get a single collection item
	 * @param cp
	 *           content provider
	 * @param collection
	 *           collection name
	 * @param item
	 *           item id
	 * @return
	 */
	public CollectionItem getItem(String cp, String collection, long item)
	{
		return collectionTable.getItem(cp, collection, item);
	}

	/**
	 * get the <em>raw</em> data of a collection item. This could be a oai
	 * record xml
	 * @param cp
	 *           content provider
	 * @param collection
	 *           collection name
	 * @param item
	 *           item id
	 * @return
	 */
	public String getItemRaw(String cp, String collection, long item)
	{
		return collectionTable.getItemRaw(cp, collection, item);
	}

	/**
	 * return a list of items
	 * @param cp
	 *           content provider
	 * @param collection
	 *           collection name
	 * @param start
	 *           optional start identifier - identifiers go in numerical order.
	 * @param size
	 *           the amount of items to return
	 * @return a list of collection items.
	 */
	public CollectionItems getItems(String cp, String collection, Long start, int size)
	{

		List<CollectionItem> result;
		if (start == null)
		{
			result = collectionTable.getItems(cp, collection, size);
		}
		else
		{
			result = collectionTable.getItems(cp, collection, start, size);
		}
		if (result.size() > 0)
		{
			return new CollectionItems(result);
		}
		return null;
	}

	/**
	 * get a list of collection given a {@code cp} {@code collection} and a list of item identifiers
	 * @param cp content provider
	 * @param collection collection name
	 * @param items a list of item id's
	 * @return a list of collection items
	 */
	public CollectionItems getItems(String cp, String collection, List<Long> itemIds)
	{
		List<CollectionItem> items = new ArrayList<CollectionItem>();

		for (Long i : itemIds)
		{
			CollectionItem item = collectionTable.getItem(cp, collection, i.longValue());
			if (item != null)
			{
				items.add(item);
			}
			else
			{
				LOG.error("user requested non existing item: {}", i);
			}
		}
		return new CollectionItems(items);
	}

	/**
	 * @param cp
	 * @param collection
	 * @param titleQuery
	 * @param titleRegExp
	 * @param longValue
	 * @return
	 */
	public CollectionItems searchItems(String cp, String collection, String titleQuery, String titleRegExp, int size)
	{
		String query = null;
		if (titleQuery != null)
		{
			query = ".*(" + titleQuery + ").*";
		}
		else
		{
			query = titleRegExp;
		}

		List<CollectionItem> result = collectionTable.searchItems(cp, collection, query, size);

		return new CollectionItems(result);
	}
}
