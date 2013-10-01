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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import nl.gridline.zieook.commons.ServerState;
import nl.gridline.zieook.commons.ZieOokCommons;
import nl.gridline.zieook.commons.ZieOokManager;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.CollectionTable;
import nl.gridline.zieook.data.hbase.dao.StatisticsTable;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.data.hbase.model.HBaseStatisticsTable;
import nl.gridline.zieook.data.model.DataControllerState;
import nl.gridline.zieook.model.CollectionItem;
import nl.gridline.zieook.model.CollectionItems;
import nl.gridline.zieook.model.DataManagerSettings;
import nl.gridline.zieook.model.GroupedData;
import nl.gridline.zieook.model.Groups;
import nl.gridline.zieook.model.Popularities;
import nl.gridline.zieook.model.Popularity;
import nl.gridline.zieook.model.PopularityTypes;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-data<br />
 * StatisticsDataController.java created 21 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:daryl@gridline.nl">Daryl</a>
 * @version $Revision$, $Date$
 */
public class StatisticsDataController implements ZieOokManager
{
	private static final Logger LOG = LoggerFactory.getLogger(StatisticsDataController.class);
	public static final String NAME = "nl.zieook.statistics_datacontroller";
	private Config config;
	private boolean state;

	private HBaseManager manager;
	private final long startup;

	private StatisticsTable statisticsTable;
	private CollectionTable collectionTable;

	private PopularityTypes popularityTypes;

	public StatisticsDataController()
	{
		startup = System.currentTimeMillis();
	}

	@Override
	public void startup() throws Exception
	{
		LOG.info(ZieOokCommons.someinformation("StatisticsDataController"));

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
				cTable.create();
			}

			statisticsTable = new StatisticsTable(new HBaseStatisticsTable(manager));
			collectionTable = new CollectionTable(new HBaseCollectionTable(manager));
		}

		String types = config.get(Config.STATISTICS_POPULARITY_TYPES,
				"day,86400;week,604800;3months,7884000;6months,15768000;year,31536000");

		String[] typesArr = types.split(";");
		if (typesArr.length == 0)
		{
			LOG.error("configuration error value for <{}> should be splittable with ';' but looks like:  {}",
					Config.STATISTICS_POPULARITY_TYPES, types);
			state = false;
		}

		List<String> list = new ArrayList<String>();
		for (String type : types.split(";"))
		{
			String[] kv = type.split(",");
			if (kv.length == 2)
			{
				list.add(kv[0]);
			}
			else
			{
				LOG.warn("cannot split '{}' into 'key,value' - value dropped", type);
			}
		}
		popularityTypes = new PopularityTypes(list);

	}

	@Override
	public void shutdown() throws Exception
	{
		// TODO cancel task ie they won't be executed in when the server reboots?

		LOG.info("StatisticsDataController shutdown");
	}

	@Override
	public boolean state()
	{
		return state;
	}

	@Override
	public ServerState getState()
	{
		DataControllerState state = new DataControllerState();

		state.setConfigurationFile(Config.ZIEOOK_DATAMANAGER_FILE);
		state.setZooKeeper(config.getZooKeeperHost());
		state.setSettings(new DataManagerSettings(config.getMap()));
		state.setUptime(new Date(startup).toString());
		try
		{
			state.setTableName(manager.getTableNames());
		}
		catch (IOException e)
		{
			state.setTableName(Collections.singletonList("failed to get table names"));
		}

		return state;
	}

	/**
	 * Return the list of sources for a given content proviver and recommender
	 * @param cp content provider
	 * @param recommender recommender name
	 * @param from start at <em>source</em>
	 * @param startDate start of date range
	 * @param endDate end of date range
	 * @param size
	 * @return a list of grouped data elements
	 * @throws IOException
	 */
	public Groups getSources(String cp, String recommender, String from, Long startDate, Long endDate, int size)
	{
		final List<GroupedData> sources = statisticsTable.getSources(cp, recommender, from, startDate, endDate, size);
		return new Groups(sources);
	}

	/**
	 * @param cp
	 * @param recommender
	 * @param startDate
	 * @param endDate
	 * @param size
	 * @return
	 * @throws IOException
	 */
	public Groups getTopSources(String cp, String recommender, Long startDate, Long endDate, int size)
	{
		List<GroupedData> topSources = statisticsTable.getTopSources(cp, recommender, startDate, endDate, size);
		return new Groups(topSources);
	}

	/**
	 * @param cp
	 * @param recommender
	 * @param startDate
	 * @param endDate
	 * @param intValue
	 * @return
	 */
	public Groups getTopViewed(String cp, String recommender, Long startDate, Long endDate, int size)
	{
		List<GroupedData> topSources = statisticsTable.getTopViewed(cp, recommender, startDate, endDate, size);
		return new Groups(topSources);
	}

	/**
	 * @param cp
	 * @param recommender
	 * @param user
	 * @param endTime
	 * @param startTime
	 * @param intValue
	 */
	public Groups topRecommended(String cp, String collection, long user, Long startTime, Long endTime, int size)
	{
		List<GroupedData> result = statisticsTable.getTopRecommended(cp, collection, user, startTime, endTime, size);
		return new Groups(result);
	}

	/**
	 * @param cp
	 * @param recommender
	 * @param user
	 * @param endTime
	 * @param startTime
	 * @param size
	 * @return
	 */
	public CollectionItems topRecommendedAsCollection(String cp, String collection, long user, Long startTime,
			Long endTime, int size)
	{
		List<GroupedData> result = statisticsTable.getTopRecommended(cp, collection, user, startTime, endTime, size);
		return createItems(cp, null, result);
		// return createItems(cp, collection, topRecommended(cp, recommender, user, size));
	}

	/**
	 * @param cp
	 * @param collection
	 * @param from
	 * @param size
	 * @return
	 */
	public Groups getCollectionSources(String cp, String collection, String from, Long startDate, Long endDate,
			Integer size)
	{
		List<GroupedData> result = statisticsTable.getCollectionSources(cp, collection, from, startDate, endDate, size);
		return new Groups(result);
	}

	/**
	 * @param cp
	 * @param collection
	 * @param startTime
	 * @param endTime
	 * @param intValue
	 * @return
	 */
	public Groups getCollectionTopSources(String cp, String collection, Long startTime, Long endTime, int size)
	{
		List<GroupedData> result = statisticsTable.getCollectionTopSources(cp, collection, startTime, endTime, size);
		return new Groups(result);
	}

	/**
	 * @param cp
	 * @param collection
	 * @param startDate
	 * @param endDate
	 * @param intValue
	 * @return
	 */
	public CollectionItems topRatedAsCollection(String cp, String collection, Long startDate, Long endDate, int size)
	{
		List<GroupedData> groups = statisticsTable.getRatedTop(cp, collection, startDate, endDate, size);
		return createItems(cp, collection, groups);
	}

	/**
	 * @param cp
	 * @param collection
	 * @param startDate
	 * @param endDate
	 * @param intValue
	 * @return
	 */
	public Groups topRated(String cp, String collection, Long startDate, Long endDate, int size)
	{
		List<GroupedData> result = statisticsTable.getRatedTop(cp, collection, startDate, endDate, size);
		return new Groups(result);
	}

	/**
	 * @param cp
	 * @param recommender
	 * @param user
	 * @param startTime
	 * @param endTime
	 * @param size
	 * @return
	 */
	public Groups topViews(String cp, String recommender, Long user, Long startTime, Long endTime, Integer size)
	{
		throw new NotImplementedException();
	}

	/**
	 * @param cp
	 * @param recommender
	 * @param user
	 * @param startTime
	 * @param endTime
	 * @param size
	 * @return
	 */
	public CollectionItems topViewsAsCollection(String cp, String collection, Long user, Long startTime, Long endTime,
			Integer size)
	{
		throw new NotImplementedException();
	}

	private CollectionItems createItems(String cp, String collection, List<GroupedData> groups)
	{
		if (groups == null)
		{
			return null;
		}
		List<CollectionItem> result = new ArrayList<CollectionItem>(groups.size());
		for (GroupedData r : groups)
		{
			if (collection == null)
			{
				collection = r.getCollection();
			}
			result.add(collectionTable.getItem(cp, collection, r.getItem()));
		}
		return new CollectionItems(result);
	}

	/**
	 * @return
	 */
	public PopularityTypes getPopularityTypes()
	{
		return popularityTypes;
	}

	/**
	 * @param cp
	 * @param collection
	 * @param type
	 * @param item
	 */
	public Popularity getPopularity(String cp, String collection, String type, Long item)
	{
		return statisticsTable.getPopularity(cp, collection, type, item.longValue());
	}

	/**
	 * @param cp
	 * @param collection
	 * @param type
	 * @param startItem
	 * @param intValue
	 * @return
	 */
	public Popularities getPopularities(String cp, String collection, String type, Long startItem, int size)
	{
		List<Popularity> result = statisticsTable.getPopularity(cp, collection, type, startItem, size);
		if (result == null || result.isEmpty())
		{
			return null;
		}
		return new Popularities(result);
	}

	/**
	 * @param cp
	 * @param collection
	 * @param type
	 * @return
	 */
	public Popularity getMostPopularItem(String cp, String collection, String type)
	{
		return statisticsTable.getMostPopular(cp, collection, type);
	}

}
