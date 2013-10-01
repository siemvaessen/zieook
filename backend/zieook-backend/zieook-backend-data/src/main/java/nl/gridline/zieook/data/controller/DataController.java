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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import nl.gridline.zieook.commons.ZieOokCommons;
import nl.gridline.zieook.commons.ZieOokManager;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.CollectionMetaTable;
import nl.gridline.zieook.data.hbase.dao.CollectionTable;
import nl.gridline.zieook.data.hbase.dao.CollectionViewsTable;
import nl.gridline.zieook.data.hbase.dao.ContentProviderTable;
import nl.gridline.zieook.data.hbase.dao.EventLogTable;
import nl.gridline.zieook.data.hbase.dao.RecommendationTable;
import nl.gridline.zieook.data.hbase.dao.RecommenderTable;
import nl.gridline.zieook.data.hbase.dao.UserTable;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseRecommendationTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.data.model.DataControllerState;
import nl.gridline.zieook.exceptions.RecommendationException;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.model.CollectionItem;
import nl.gridline.zieook.model.CollectionItems;
import nl.gridline.zieook.model.DataManagerSettings;
import nl.gridline.zieook.model.Rating;
import nl.gridline.zieook.model.RatingFull;
import nl.gridline.zieook.model.Ratings;
import nl.gridline.zieook.model.RatingsFull;
import nl.gridline.zieook.model.Recommend;
import nl.gridline.zieook.model.Recommendations;
import nl.gridline.zieook.model.Recommended;
import nl.gridline.zieook.model.Recommended.RecommenderType;
import nl.gridline.zieook.model.Recommendeds;
import nl.gridline.zieook.model.UserView;
import nl.gridline.zieook.model.UserViewFull;
import nl.gridline.zieook.model.UserViews;
import nl.gridline.zieook.model.UserViewsFull;
import nl.gridline.zieook.workflow.model.Collection;
import nl.gridline.zieook.workflow.model.RecommenderConfig;
import nl.gridline.zieook.workflow.model.Template;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The data controller the C in the MVC (V - is the API, M is the zieook-data
 * lib)
 * <p />
 * Project zieook-backend-data<br />
 * DataManager.java created 15 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class DataController implements ZieOokManager
{

	// TODO this class is growing into a god-class and should probably be refactored into several controller some time
	// soon.

	public static final String NAME = "nl.zieook.datamanager";

	private Config config;

	private static final Logger LOG = LoggerFactory.getLogger(DataController.class);

	private boolean state;

	private CollectionTable collectionTable;
	private RecommendationTable recommendationTable;
	private UserTable userTable;
	private EventLogTable eventLogTable;
	private ContentProviderTable cpTable;
	private RecommenderTable recommenderTable;
	private CollectionMetaTable collectionMeta;
	private CollectionViewsTable collectionViews;

	private HBaseManager manager;
	private final long start;

	/**
	 * create a object instance - you will have to call start-up before using the controlller
	 */
	public DataController()
	{
		start = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.commons.ZieOokManager#startup()
	 */
	@Override
	public void startup() throws MasterNotRunningException, ZooKeeperConnectionException
	{
		LOG.info(ZieOokCommons.someinformation("DataController"));

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

			HBaseUserTable hUserTable = new HBaseUserTable(manager);
			HBaseCollectionTable hCollectionTable = new HBaseCollectionTable(manager);
			HBaseEventLogTable hEventsTable = new HBaseEventLogTable(manager);

			collectionTable = new CollectionTable(hCollectionTable);
			userTable = new UserTable(hUserTable);
			eventLogTable = new EventLogTable(hEventsTable);
			recommendationTable = new RecommendationTable(new HBaseRecommendationTable(manager));
			cpTable = new ContentProviderTable(cTable);
			recommenderTable = new RecommenderTable(cTable);
			collectionMeta = new CollectionMetaTable(cTable);
			collectionViews = new CollectionViewsTable(hEventsTable, hCollectionTable, hUserTable);

		}

		// setup caching:
	}

	@Override
	public DataControllerState getState()
	{
		DataControllerState state = new DataControllerState();

		state.setConfigurationFile(Config.ZIEOOK_DATAMANAGER_FILE);
		state.setZooKeeper(config.getZooKeeperHost());
		state.setSettings(new DataManagerSettings(config.getMap()));
		state.setUptime(new Date(start).toString());
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

	@Override
	public void shutdown()
	{
		// release resources... not much to do yet.
		LOG.info("DataController shutdown");
	}

	/**
	 * check if the given string value is empty or null
	 * @param value
	 * @return {@code true} if <em>value</em> is null or trim.empty
	 */
	public static boolean isEmpty(String value)
	{
		return value == null || value.trim().isEmpty();
	}

	@Override
	public boolean state()
	{
		return state;
	}

	/**
	 * Returns a item or user based recommender. Behavior:
	 * <p />
	 * user == null && id == null => error<br />
	 * user == null && id != null => anonymous item based recommender <br />
	 * user != null && id == null => user based recommender <br />
	 * user != null && id != null => item based recommender for a user <br />
	 * @param cp
	 *           content provider
	 * @param collection
	 *           collection name
	 * @param recommenderId
	 *           recommender identifier
	 * @param user
	 *           user identifier (optional if, but only if no user is logged
	 *           in)
	 * @param item
	 *           item identifier
	 * @param source
	 *           source name (used for logging)
	 * @param size
	 *           the amount of items to return
	 * @param spread
	 * @return a list of recommendations
	 */
	public Recommendations getRecommendationFor(String cp, String collection, String recommenderId, Long user,
			Long item, String source, int size, String spread)
	{

		RecommenderConfig recommender = recommenderTable.getRecommender(cp, recommenderId);
		String type = getRecommenderType(recommender);

		// create results:

		Recommendations result = null; // method result:
		Recommended recommend = null; // log item

		if (TaskConfig.RECOMMENDER_TYPE_ITEMBASED.equals(type) || TaskConfig.RECOMMENDER_TYPE_TEXTBASED.equals(type))
		{

			// for an item based recommender: the user id is optional:
			if (user == null)
			{
				user = Long.MIN_VALUE;
			}

			result = recommendationTable.getRecommendationsFor(cp, collection, recommenderId, item, size, spread);
			if (result != null)
			{
				recommend = new Recommended(user.longValue(), item.longValue(), collection, source,
						System.currentTimeMillis(), recommenderId, RecommenderType.ITEM_BASED, size);
			}
		}
		else if (TaskConfig.RECOMMENDER_TYPE_USERBASED.equals(type))
		{

			// for a user based recommender: the item id is optional
			if (item == null)
			{
				item = Long.MIN_VALUE;
			}

			result = recommendationTable.getRecommendationsFor(cp, collection, recommenderId, user, size, spread);
			if (result != null)
			{
				recommend = new Recommended(user.longValue(), item.longValue(), collection, source,
						System.currentTimeMillis(), recommenderId, RecommenderType.ITEM_BASED, size);
			}
		}
		else
		{
			throw new InternalServerErrorException("the type of the recommender <" + recommenderId
					+ "> could not be determined: " + type);
		}

		// write to debug logger:
		if (LOG.isDebugEnabled())
		{
			LOG.debug("<cp,collection,recommenderId,id,size> = <{},{}>", cp + "," + collection + "," + recommenderId, item
					+ "," + size);
		}

		// log if results are returned
		if (recommend != null)
		{
			eventLogTable.putRecommend(cp, recommenderId, recommend, TimeUnit.MILLISECONDS);
			userTable.incRecommends(cp, user);
		}

		return result;
	}

	/**
	 * same as {@link DataController#getRecommendationFor(String, String, String, Long, Long, String, int)} but return
	 * the results as collection items
	 * @param cp
	 *           content provider
	 * @param collection
	 *           collection name
	 * @param recommenderId
	 *           recommender id
	 * @param user
	 *           user identifier
	 * @param item
	 *           item identifier
	 * @param source
	 *           source name (used for logging the request)
	 * @param size
	 *           the amount of items to return
	 * @return a list of collection items
	 */
	public CollectionItems getRecommendationAsItems(String cp, String collection, String recommenderId, Long user,
			Long item, String source, int size, String spread)
	{
		Recommendations result = getRecommendationFor(cp, collection, recommenderId, user, item, source, size, spread);

		return createItems(result);
	}

	/**
	 * rate an item. A user rates an item
	 * @param cp
	 *           content provider
	 * @param collection
	 *           collection name
	 * @param rating
	 *           rating object
	 */
	public void setRating(String cp, String collection, Rating rating, TimeUnit unit)
	{
		userTable.putRating(cp, collection, rating, unit);
	}

	/**
	 * Retrieve a list of filtered ratings.
	 * @param cp
	 *           content provider
	 * @param collection
	 *           collection name
	 * @param user
	 *           user identifier
	 * @param startItem
	 *           optional start item (the results are ordered by item id)
	 * @param startDate
	 *           filter with an optional start date
	 * @param endDate
	 *           filter with an optional end date
	 * @param size
	 *           the maximum amount of items to return
	 * @return a list of ratings.
	 */
	public Ratings getRatingsByDate(String cp, String collection, long user, Long startDate, Long endDate, int size)
	{
		final List<Rating> ratings = userTable.getRatings(cp, collection, user, startDate, endDate, size);
		return new Ratings(ratings);
	}

	/**
	 * return ratings for multiple recommenders
	 * @param recommenderList a list of <em>cp</em> <em>recommender</em> pairs
	 * @param user user id
	 * @param start start time in millis
	 * @param end end time in millis
	 * @param size the maximum amount of ratings to return.
	 * @return
	 */
	public Ratings getMSrcRatings(List<String[]> recommenderList, long user, Long start, Long end, int size)
	{
		List<Rating> result = new ArrayList<Rating>();
		for (String[] cprec : recommenderList)
		{
			final String cp = cprec[0];
			final String collection = getRecommenderCollection(cp, cprec[1]);
			final String recommender = cprec[1];
			// LOG.debug("getting rating for: {}/{}", cprec[0], collection);

			List<Rating> ratings = userTable.getRatings(cp, collection, user, start, end, size);
			for (Rating r : ratings)
			{
				r.setRecommender(recommender);
			}

			result.addAll(ratings);
			if (result.size() > size * 3)
			{
				sortRatings(result);
				result = result.subList(0, size * 2);
			}
		}

		if (result.size() > size)
		{
			result = result.subList(0, size);
		}

		// sort final result, on time:
		sortRatings(result);

		return new Ratings(result);
	}

	/**
	 * @param recommenderList
	 * @param user
	 * @param start
	 * @param end
	 * @param size
	 * @return
	 */
	public CollectionItems getMSrcRatingsByDateAsCollection(List<String[]> recommenderList, long user, Long start,
			Long end, int size)
	{
		Ratings data = getMSrcRatings(recommenderList, user, start, end, size);
		return createItems(data);
	}

	/**
	 * @param recommenderList
	 * @param user
	 * @param start
	 * @param end
	 * @param size
	 * @return
	 */
	public RatingsFull getMSrcRatingsFull(List<String[]> recommenderList, long user, Long start, Long end, int size)
	{
		Ratings data = getMSrcRatings(recommenderList, user, start, end, size);
		List<RatingFull> result = new ArrayList<RatingFull>(data.getRatings().size());
		for (Rating r : data.getRatings())
		{
			RatingFull f = new RatingFull(r);
			// LOG.debug("rating: {}", r.toJSON());
			f.setItem(collectionTable.getItem(r.getCp(), r.getCollection(), r.getItem()));
			result.add(f);
		}
		return new RatingsFull(result);
	}

	/**
	 * @param cp
	 * @param collection
	 * @param user
	 * @param startItem
	 * @param startTime
	 * @param endTime
	 * @param size
	 * @return
	 */
	public CollectionItems getRatingsByDateAsCollection(String cp, String collection, long user, Long startTime,
			Long endTime, int size)
	{
		Ratings data = getRatingsByDate(cp, collection, user, startTime, endTime, size);

		return createItems(cp, collection, data);
	}

	/**
	 * @param cp
	 * @param collection
	 * @param longValue
	 * @param itemIds
	 * @return
	 */
	public Ratings getRatingsByIds(String cp, String collection, long user, List<Long> itemIds)
	{
		List<Rating> result = new ArrayList<Rating>();
		for (Long item : itemIds)
		{
			Rating r = userTable.getRating(cp, collection, user, item);
			if (r != null)
			{
				result.add(r);
			}
		}
		// return empty list... (not null that results in a 404)
		return new Ratings(result);
	}

	/**
	 * @param cp
	 * @param collection
	 * @param user
	 * @param lowerBound
	 * @param upperBound
	 * @param size
	 * @param startItem
	 * @return
	 */
	public Ratings getRatingsByValue(String cp, String collection, long user, Double lowerBound, Double upperBound,
			int size, Long startItem)
	{
		final List<Rating> ratings = userTable.getRatings(cp, collection, user, lowerBound, upperBound, size, startItem);
		return new Ratings(ratings);
	}

	/**
	 * @param cp
	 * @param collection
	 * @param longValue
	 * @param lowerBound
	 * @param upperBound
	 * @param intValue
	 * @param startItem
	 * @return
	 */
	public CollectionItems getRatingsByValueAsCollection(String cp, String collection, long user, Double lowerBound,
			Double upperBound, int size, Long startItem)
	{
		Ratings data = getRatingsByValue(cp, collection, user, lowerBound, upperBound, size, startItem);

		return createItems(cp, collection, data);
	}

	/**
	 * get a single rating
	 * @param cp
	 *           content provider
	 * @param collection
	 *           collection name
	 * @param user
	 *           user identifier
	 * @param item
	 *           item identifier.
	 * @return the rating of an item, or null if non given
	 */
	public Rating getRating(String cp, String collection, long user, long item)
	{
		return userTable.getRating(cp, collection, user, item);
	}

	/**
	 * delete (unset) a rating from a user on an item
	 * @param cp
	 *           content provider
	 * @param collection
	 *           collection name
	 * @param user
	 *           user identifier
	 * @param item
	 *           item identifier.
	 */
	public void deleteRating(String cp, String collection, Long user, Long item)
	{
		userTable.deleteRating(cp, collection, user, item);
	}

	/**
	 * @param recommenderList
	 * @param longValue
	 */
	public void deleteRatings(List<String[]> recommenderList, long user)
	{
		for (String[] cprec : recommenderList)
		{
			final String cp = cprec[0];
			final String recommender = cprec[1];
			final String collection = getRecommenderCollection(cprec[0], cprec[1]);
			LOG.debug("deleting ratings for user={} in {}/{}/{}", new Object[]{user, cp, recommender, collection});
			userTable.deleteRatings(cp, collection, user);
		}
	}

	/**
	 * returns a list of recommended items, between a start / end time
	 * @param cp content provider
	 * @param collection collection name
	 * @param user user id
	 * @param startTime optional start time
	 * @param endTime optional end time
	 * @param size the amount of items to return
	 * @throws IOException
	 */
	public Recommendeds getRecommended(String cp, String collection, long user, Long startTime, Long endTime, int size)
			throws IOException
	{
		// get the recommender:

		RecommenderConfig recommenderConfig = recommenderTable.getRecommender(cp, collection);
		if (recommenderConfig == null)
		{
			throw new RecommendationException("Recommender does not exist.");
		}

		String typeStr = getRecommenderType(recommenderConfig);
		RecommenderType type;
		if (TaskConfig.RECOMMENDER_TYPE_ITEMBASED.equals(typeStr)
				|| TaskConfig.RECOMMENDER_TYPE_TEXTBASED.equals(typeStr))
		{
			type = RecommenderType.ITEM_BASED;
		}
		else if (TaskConfig.RECOMMENDER_TYPE_USERBASED.equals(typeStr))
		{
			type = RecommenderType.USER_BASED;
		}
		else
		{
			throw new InternalServerErrorException("the type of the recommender <" + collection
					+ "> could not be determined: '" + typeStr + "'");
		}

		final List<Recommended> result = eventLogTable.getRecommended(cp, collection, type, user, startTime, endTime,
				size);

		return new Recommendeds(result);
	}

	/**
	 * Returns a list of views for the given cp/recommender with an optional
	 * user and date range in milliseconds
	 * @param cp
	 *           - content provider
	 * @param recommender
	 *           - recommender
	 * @param user
	 *           - user (optional)
	 * @param start
	 *           - date start in millis
	 * @param end
	 *           - date end in millis
	 * @return
	 * @throws IOException
	 */
	public UserViews getViews(String cp, String recommender, long user, long start, long end, int size)
	{
		return new UserViews(eventLogTable.getViews(cp, recommender, user, start, end, size, false));
	}

	/**
	 * @param recommenderList
	 * @param longValue
	 * @param startTime
	 * @param endTime
	 * @param intValue
	 * @return
	 */
	public UserViews getMSrcViews(List<String[]> recommenderList, long user, Long start, Long end, int size)
	{
		List<UserView> result = new ArrayList<UserView>();
		for (String[] cprec : recommenderList)
		{
			result.addAll(eventLogTable.getViews(cprec[0], cprec[1], user, start, end, size, false));
			if (result.size() > size * 3)
			{
				sortViews(result);
				result = result.subList(0, size * 2);
			}
		}
		if (result.size() > size)
		{
			result = result.subList(0, size);
		}
		return new UserViews(result);
	}

	/**
	 * @param cp
	 * @param recommender
	 * @param user
	 * @param start
	 * @param end
	 * @param size
	 * @return
	 */
	public UserViewsFull getFullViews(String cp, String recommender, long user, long start, long end, int size)
	{
		List<UserView> views = eventLogTable.getViews(cp, recommender, user, start, end, size, false);
		List<UserViewFull> result = new ArrayList<UserViewFull>(views.size());
		String collection = getRecommenderCollection(cp, recommender);
		for (UserView v : views)
		{
			UserViewFull f = new UserViewFull(v);
			f.setItem(collectionTable.getItem(cp, collection, v.getItem()));
			f.setSourceItem(collectionTable.getItem(cp, collection, v.getSourceItem()));
			f.setRating(userTable.getRating(cp, collection, user, v.getItem()));
			f.setSourceRating(userTable.getRating(cp, collection, user, v.getSourceItem()));
			result.add(f);
		}
		return new UserViewsFull(result);
	}

	/**
	 * @param recommenderList
	 * @param longValue
	 * @param startTime
	 * @param endTime
	 * @param intValue
	 * @return
	 */
	public UserViewsFull getMSrcViewsFull(List<String[]> recommenderList, long user, Long start, Long end, int size)
	{
		UserViews views = getMSrcViews(recommenderList, user, start, end, size);
		List<UserViewFull> result = new ArrayList<UserViewFull>(views.getViews().size());
		for (UserView v : views.getViews())
		{
			UserViewFull f = new UserViewFull(v);
			final String cp = v.getCp();
			String collection = v.getCollection() == null ? getRecommenderCollection(cp, v.getRecommender()) : v
					.getCollection();
			f.setItem(collectionTable.getItem(cp, collection, v.getItem()));
			f.setSourceItem(collectionTable.getItem(cp, collection, v.getSourceItem()));
			f.setRating(userTable.getRating(cp, collection, user, v.getItem()));
			f.setSourceRating(userTable.getRating(cp, collection, user, v.getSourceItem()));
			result.add(f);
		}
		return new UserViewsFull(result);
	}

	/**
	 * View an item - this is used for logging use behavior
	 * @param cp
	 * @param recommender
	 * @param view
	 * @param unit
	 */
	public void setView(String cp, String recommender, UserView view, TimeUnit unit)
	{
		// fill...
		if (view.getCollection() == null)
		{
			view.setCollection(getRecommenderCollection(cp, recommender));
		}

		eventLogTable.putView(cp, recommender, view, unit);
		userTable.incView(cp, view.getUser());
	}

	/**
	 * @param cp
	 * @param recommender
	 * @param user
	 * @param start2
	 * @param end
	 */
	public void deleteViews(String cp, String recommender, Long user, Long startTime, Long endTime)
	{
		long count = eventLogTable.deleteViews(cp, recommender, user);
		userTable.decView(cp, user, count);
	}

	/**
	 * @param recommenderList
	 * @param longValue
	 */
	public void deleteViews(List<String[]> recommenderList, long user)
	{
		for (String[] cprec : recommenderList)
		{
			final String cp = cprec[0];
			final String recommender = cprec[1];
			final String collection = getRecommenderCollection(cprec[0], cprec[1]);
			LOG.debug("deleting ratings for user={} in {}/{}/{}", new Object[]{user, cp, recommender, collection});
			long count = eventLogTable.deleteViews(cp, recommender, user);
			userTable.decView(cp, user, count);
		}
	}

	/**
	 * Search in the views of a user to a specific title using a regexp and return the collection items
	 * @param cp content provider
	 * @param recommender recommender name
	 * @param user user id
	 * @param start start date (optional)
	 * @param end end date (optional)
	 * @param regexp regular expression
	 * @param size max number of items to return
	 * @return a wrapped list of collection items
	 */
	public CollectionItems searchViewsAsCollectionItems(String cp, String recommender, long user, Long start, Long end,
			String regexp, int size)
	{
		LOG.warn("This is not working ok yet! - it will return views that are not part of the given recommender");

		String collection = getRecommenderCollection(cp, recommender);

		List<CollectionItem> items = collectionViews.searchViewsAsCollectionItems(cp, collection, user, start, end,
				regexp, size);
		return new CollectionItems(items);
	}

	/**
	 * Search in the views of a user to a specific title using a regexp, and return those views
	 * @param cp content provider
	 * @param recommender recommender name
	 * @param user user id
	 * @param start start time (optional)
	 * @param end end time (optional)
	 * @param regexp regular expression
	 * @param size max number of items to return
	 * @return wrapped list of user views
	 */
	public UserViews searchViews(String cp, String recommender, long user, Long start, Long end, String regexp, int size)
	{
		LOG.warn("This is not working ok yet! - it will return views that are not part of the given recommender");

		String collection = getRecommenderCollection(cp, recommender);

		List<UserView> items = collectionViews.searchViews(cp, collection, user, start, end, regexp, size);
		return new UserViews(items);
	}

	/**
	 * @param recommenderList
	 * @param user
	 * @param startTime
	 * @param endTime
	 * @param regexp
	 * @param size
	 * @return
	 */
	public UserViewsFull searchViewsMSrc(List<String[]> recommenderList, long user, Long start, Long end, String regexp,
			int size)
	{
		// TODO implement this directly in the collectionViews table, this will most likely be a lot faster

		List<UserViewFull> result = new ArrayList<UserViewFull>();
		for (String[] cprec : recommenderList)
		{
			final String cp = cprec[0];
			final String collection = getRecommenderCollection(cp, cprec[1]);

			LOG.debug("searching views {}/{}/{}", new Object[]{cp, collection, cprec[1]});

			List<UserView> views = collectionViews.searchViews(cp, collection, user, start, end, regexp, size);
			for (UserView v : views)
			{
				UserViewFull f = new UserViewFull(v);
				f.setItem(collectionTable.getItem(cp, collection, v.getItem()));
				f.setSourceItem(collectionTable.getItem(cp, collection, v.getSourceItem()));
				f.setRating(userTable.getRating(cp, collection, user, v.getItem()));
				f.setSourceRating(userTable.getRating(cp, collection, user, v.getSourceItem()));
				result.add(f);
			}

			if (result.size() > size * 3)
			{
				sortViewsFull(result);
				result = result.subList(0, size * 2);
			}
		}
		if (result.size() > size)
		{
			result = result.subList(0, size);
		}

		return new UserViewsFull(result);
	}

	/**
	 * Search in the ratings of a user to a specific title using a regexp, and return the ratings
	 * @param cp content provider
	 * @param collection collection name
	 * @param user user id
	 * @param startItem item to start the search
	 * @param regexp regular expression
	 * @param size max number of items to return
	 * @return wrapped list of ratings.
	 */
	public Ratings searchUserRatings(String cp, String collection, long user, Long startItem, String regexp, int size)
	{
		List<Rating> ratings = collectionViews.searchRatings(cp, collection, user, startItem, regexp, size);
		return new Ratings(ratings);
	}

	/**
	 * Search in the ratings of a user to a specific title using a regexp, and return the items
	 * @param cp content provider
	 * @param collection collection name
	 * @param user user id
	 * @param startItem item to start the search
	 * @param regexp regular expression
	 * @param size max number of items to return
	 * @return wrapped list of collection items.
	 */
	public CollectionItems searchUserRatingsAsCollectionItems(String cp, String collection, long user, Long startItem,
			String regexp, int size)
	{
		List<CollectionItem> items = collectionViews.searchRatingsAsCollectionItems(cp, collection, user, startItem,
				regexp, size);
		return new CollectionItems(items);
	}

	/**
	 * @param recommenderList
	 * @param user
	 * @param startItem
	 * @param regexp
	 * @param size
	 * @return
	 */
	public RatingsFull searchUserRatingsMSrc(List<String[]> recommenderList, long user, String regexp, int size)
	{
		// TODO implement this directly in 'collectionViews' table, what will be much quicker...

		// TODO there is no way to 'browse' the results yet...

		List<RatingFull> result = new ArrayList<RatingFull>();
		for (String[] cprec : recommenderList)
		{
			final String collection = getRecommenderCollection(cprec[0], cprec[1]);
			List<Rating> ratings = collectionViews.searchRatings(cprec[0], collection, user, null, regexp, size);

			for (Rating r : ratings)
			{
				r.setRecommender(cprec[1]);
				RatingFull f = new RatingFull(r);
				f.setItem(collectionTable.getItem(r.getCp(), r.getCollection(), r.getItem()));
				result.add(f);
			}

			if (result.size() > size * 3)
			{
				sortFullRatings(result);
				result = result.subList(0, size * 2);
			}
		}
		if (result.size() > size)
		{
			result = result.subList(0, size);
		}

		// sort final result:
		sortFullRatings(result);

		return new RatingsFull(result);
	}

	// -------------------------------------
	// -- public helper methods --
	// -------------------------------------

	/**
	 * Returns true if the {@code cp} exists.
	 * @param cp content provider
	 * @return {@code true} if it exists, {@code false} otherwise
	 */
	public boolean isContentProvider(String cp)
	{
		return cpTable.isContentProvider(cp);
	}

	/**
	 * Test if a {@code cp} {@code recommender} combination exists
	 * @param cp content provider
	 * @param recommender recommender name
	 * @return {@code true} if it exists, {@code false} otherwise
	 */
	public boolean isRecommender(String cp, String recommender)
	{
		return recommenderTable.isRecommender(cp, recommender);
	}

	/**
	 * Test if the given {@code cp} {@code collection} pair exists
	 * @param cp content provider
	 * @param collection collection name
	 * @return {@code true} if it exists, {@code false} otherwise
	 */
	public boolean isCollection(String cp, String collection)
	{
		return collectionMeta.isCollection(cp, collection);
	}

	public boolean isCollectionItem(String cp, String collection, long item)
	{
		return collectionTable.isCollectionItem(cp, collection, item);
	}

	// ----------------------------
	// -- private helper methods --
	// ----------------------------

	/**
	 * return the recommender type given a recommender configuration
	 * @param recommender
	 * @return
	 */
	private String getRecommenderType(RecommenderConfig recommender)
	{
		if (recommender == null)
		{
			return null;
		}
		Template template = recommender.getTemplate();
		if (template == null)
		{
			return null;
		}
		String result = template.getModules().get(0).getName();
		LOG.debug("recommender <{}> is of type <{}>", recommender.getRecommender(), result);
		return result;
	}

	private CollectionItems createItems(String cp, String collection, Ratings rating)
	{
		if (rating == null)
		{
			return null;
		}
		List<Rating> ratings = rating.getRatings();
		List<CollectionItem> result = new ArrayList<CollectionItem>(ratings.size());
		for (Rating r : ratings)
		{
			result.add(collectionTable.getItem(cp, collection, r.getItem()));
		}
		return new CollectionItems(result);
	}

	private CollectionItems createItems(Ratings rating)
	{
		if (rating == null)
		{
			return null;
		}
		List<Rating> ratings = rating.getRatings();
		List<CollectionItem> result = new ArrayList<CollectionItem>(ratings.size());
		for (Rating r : ratings)
		{
			result.add(collectionTable.getItem(r.getCp(), r.getCollection(), r.getItem()));
		}
		return new CollectionItems(result);
	}

	/**
	 * Given a recommendation object, return a list of collection items
	 * Create an item list from a recommendation list.
	 * @param recommendations
	 * @return
	 */
	private CollectionItems createItems(Recommendations recommendations)
	{
		if (recommendations == null)
		{
			return null;
		}
		List<Recommend> recommended = recommendations.getRecommended();
		if (recommended != null)
		{
			List<CollectionItem> result = new ArrayList<CollectionItem>(recommended.size());
			String cp = recommendations.getCp();
			String collection = recommendations.getCollection();
			for (Recommend i : recommended)
			{
				result.add(collectionTable.getItem(cp, collection, i.getItem()));
			}
			return new CollectionItems(result);
		}
		return null;
	}

	private void sortViewsFull(List<UserViewFull> views)
	{
		Collections.sort(views, new Comparator<UserViewFull>()
		{
			@Override
			public int compare(UserViewFull o1, UserViewFull o2)
			{
				int result = (int) (o2.getDate() - o1.getDate());
				if (result == 0)
				{
					result = o1.getRecommender().compareTo(o2.getRecommender());
				}
				if (result == 0)
				{
					result = o1.getSource().compareTo(o2.getSource());
				}
				if (result == 0)
				{
					result = (int) (o1.getRank() - o2.getRank());
				}
				return result;
			}
		});
	}

	private void sortViews(List<UserView> views)
	{
		Collections.sort(views, new Comparator<UserView>()
		{
			@Override
			public int compare(UserView o1, UserView o2)
			{
				int result = (int) (o2.getDate() - o1.getDate());
				if (result == 0)
				{
					result = o1.getRecommender().compareTo(o2.getRecommender());
				}
				if (result == 0)
				{
					result = o1.getSource().compareTo(o2.getSource());
				}
				if (result == 0)
				{
					result = (int) (o1.getRank() - o2.getRank());
				}
				return result;
			}
		});
	}

	private void sortFullRatings(List<RatingFull> ratings)
	{
		Collections.sort(ratings, new Comparator<RatingFull>()
		{

			@Override
			public int compare(RatingFull o1, RatingFull o2)
			{
				int result = (int) (o2.getDate() - o1.getDate());
				if (result == 0)
				{
					result = o1.getCollection().compareTo(o2.getCollection());
				}
				if (result == 0)
				{
					result = o1.getSource().compareTo(o2.getSource());
				}
				return result;
			}
		});
	}

	private void sortRatings(List<Rating> ratings)
	{
		Collections.sort(ratings, new Comparator<Rating>()
		{
			@Override
			public int compare(Rating o1, Rating o2)
			{
				int result = (int) (o2.getDate() - o1.getDate());
				if (result == 0)
				{
					result = o1.getCollection().compareTo(o2.getCollection());
				}
				if (result == 0)
				{
					result = o1.getSource().compareTo(o2.getSource());
				}
				return result;
			}
		});
	}

	/**
	 * return a sorted map of {@code recommenders} per {@code collection}
	 * @param cp content provider
	 * @return a sorted map
	 */
	protected SortedMap<String, List<String>> getCollectionsRecommenders(String cp)
	{
		SortedMap<String, List<String>> result = new TreeMap<String, List<String>>();

		List<Collection> collections = collectionMeta.getCollections(cp);
		for (Collection collection : collections)
		{
			String name = collection.getName();
			List<String> recommenders = recommenderTable.getRecommenderList(name);
			result.put(name, recommenders);
		}

		return result;
	}

	/**
	 * Return the list of recommender names for this {@code cp} & {@code collection}
	 * @param cp content provider
	 * @param collection collection name
	 * @return a list of recommender names
	 */
	protected List<String> getRecommenders(String cp, String collection)
	{
		return recommenderTable.getRecommenderList(cp, collection);
	}

	protected String getRecommenderCollection(String cp, String recommender)
	{
		return recommenderTable.getRecommenderCollection(cp, recommender);
	}

}
