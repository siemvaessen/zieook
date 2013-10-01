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
package nl.gridline.zieook.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.EventLogTable;
import nl.gridline.zieook.data.hbase.dao.StatisticsTable;
import nl.gridline.zieook.data.hbase.dao.UserTable;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseStatisticsTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.model.GroupedData;
import nl.gridline.zieook.model.Popularity;
import nl.gridline.zieook.model.Rating;
import nl.gridline.zieook.model.Recommended;
import nl.gridline.zieook.model.UserView;
import nl.gridline.zieook.runners.statistics.StatisticsTool;
import nl.gridline.zieook.statistics.popularity.PopularityStatistics;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This tests the map reduce for counting sources
 * <p />
 * Project zieook-runner<br />
 * RecommendationViewTaskTest.java created 22 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:daryl@gridline.nl">Daryl</a>
 * @version $Revision$, $Date$
 */
@RunWith(SortedSuite.class)
public class StatisticsTaskTest
{
	private static final Logger LOG = LoggerFactory.getLogger(StatisticsTaskTest.class);

	static
	{
		System.setProperty("gridline.project.name", "zieook");

	}

	private static TaskConfig task;

	private static HBaseManager manager;

	private static HBaseStatisticsTable statsTable;
	private static HBaseUserTable userTable;
	private static HBaseEventLogTable eventsTable;

	private static EventLogTable events;
	private static StatisticsTable statistics;
	private static UserTable users;

	@BeforeClass
	public static void init() throws MasterNotRunningException, ZooKeeperConnectionException
	{

		String outputTableName = new HBaseStatisticsTable().getTableName(Helper.CP);
		String eventlogTableName = new HBaseEventLogTable().getTableName(Helper.CP);
		String userTableName = new HBaseUserTable().getTableName(Helper.CP);

		task = new TaskConfig();
		task.setProperty(TaskConfig.CLASSNAME, StatisticsTask.class.getCanonicalName());
		task.setProperty(TaskConfig.TASK_TYPE, TaskConfig.TASK_TYPE_STATISTICS);
		task.setProperty(TaskConfig.OUTPUT_TABLE, outputTableName);
		task.setProperty(TaskConfig.STATS_EVENTLOG_TABLE, eventlogTableName);
		task.setProperty(TaskConfig.STATS_USER_TABLE, userTableName);

		// round statistics end to current midnight:
		task.setProperty(TaskConfig.STATS_END, Helper.HIGH_TIME + 1);
		task.setProperty(TaskConfig.CP, Helper.CP);
		task.setProperty(TaskConfig.COLLECTION, Helper.COLLECTION);

		manager = HBaseManager.getInstance("mulisch");

		statsTable = new HBaseStatisticsTable(manager);
		statsTable.drop(Helper.CP);
		if (!statsTable.tableExists(Helper.CP))
		{
			assertTrue(statsTable.create(Helper.CP));
		}
		assertTrue(statsTable.tableExists(Helper.CP));

		userTable = new HBaseUserTable(manager);
		userTable.drop(Helper.CP);
		if (!userTable.tableExists(Helper.CP))
		{
			assertTrue(userTable.create(Helper.CP));
		}
		assertTrue(userTable.tableExists(Helper.CP));

		eventsTable = new HBaseEventLogTable(manager);
		eventsTable.drop(Helper.CP);
		if (!eventsTable.tableExists(Helper.CP))
		{
			assertTrue(eventsTable.create(Helper.CP));
		}
		assertTrue(eventsTable.tableExists(Helper.CP));

		statistics = new StatisticsTable(statsTable);
		users = new UserTable(userTable);
		events = new EventLogTable(eventsTable);
	}

	@AfterClass
	public static void cleanup()
	{
		if (statsTable != null)
		{
			// statsTable.drop(Helper.CP);
		}
		if (eventsTable != null)
		{
			// eventsTable.drop(Helper.CP);
		}
		if (userTable != null)
		{
			// userTable.drop(Helper.CP);
		}
	}

	public static class A_StoreData
	{
		@Test
		public void testPutGetViews() throws IOException
		{
			Set<UserView> views = Helper.getUserViews();
			for (UserView view : views)
			{
				events.putView(Helper.CP, Helper.RECOMMENDER, view, TimeUnit.MILLISECONDS);
				UserView newview = events.getView(Helper.CP, Helper.RECOMMENDER, view.getUser(), view.getItem(),
						view.getDate(), TimeUnit.MILLISECONDS);
				assumeNotNull(view);
				assumeNotNull(newview);
				assertEquals(view, newview);
			}
		}

		@Test
		public void testPutGetRecommended() throws IOException
		{
			Set<Recommended> recommendeds = Helper.getRecommended();
			for (Recommended r : recommendeds)
			{
				events.putRecommend(Helper.CP, Helper.RECOMMENDER, r, TimeUnit.MILLISECONDS);
				Recommended newr = events.getRecommended(Helper.CP, Helper.RECOMMENDER, r.getType(), r.getUser(),
						r.getItem(), r.getDate(), TimeUnit.MILLISECONDS);
				assumeNotNull(r);
				assumeNotNull(newr);
				assertEquals(r, newr);
			}
		}

		@Test
		public void testPutGetRatings()
		{
			Set<Rating> ratings = Helper.getUserRatings();
			for (Rating rating : ratings)
			{
				LOG.info("RATING: {}", rating);
				users.putRating(Helper.CP, Helper.COLLECTION, rating, TimeUnit.MILLISECONDS);
				Rating newrating = users.getRating(Helper.CP, Helper.COLLECTION, rating.getUser(), rating.getItem());
				assumeNotNull(rating);
				assumeNotNull(newrating);
				assertEquals(rating, newrating);
			}
		}
	}

	public static class B_TestScanner
	{

		private static Set<Recommended> recommendeds;
		private static Set<UserView> views;
		private static Set<Rating> ratings;

		private StatisticsTool tool;

		// private final byte[] EVENTLOG_COLUMN_RECOMMENDED =
		// Bytes.toBytes(HBaseTableConstants.EVENTLOG_COLUMN_RECOMMENDED);
		// private final byte[] EVENTLOG_COLUMN_VIEW = Bytes.toBytes(HBaseTableConstants.EVENTLOG_COLUMN_VIEW);
		// private final byte[] USERTABLE_COLUMN_RATING = Bytes.toBytes(HBaseTableConstants.USERTABLE_COLUMN_RATING);

		@BeforeClass
		public static void classInit()
		{
			recommendeds = Helper.getRecommended();
			views = Helper.getUserViews();
			ratings = Helper.getUserRatings();
		}

		@Before
		public void init()
		{
			tool = new StatisticsTool(new EmptyTask());
		}

		@Ignore
		private int count(HTableInterface tableInterface, Scan scan) throws IOException
		{
			int count = 0;
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				while (scanner.next() != null)
				{
					count++;
				}
			}
			finally
			{
				scanner.close();
				eventsTable.putTable(tableInterface);
			}
			return count;
		}

		@Test
		public void testRecommendedScanner() throws IOException
		{
			Scan scan = tool.getEventLogRecommendedScanner(null, null);
			HTableInterface tableInterface = eventsTable.getTable(Helper.CP);
			assertEquals(recommendeds.size(), count(tableInterface, scan));
		}

		@Test
		public void testRecommendedEndTimeScanner() throws IOException
		{
			Scan scan = tool.getEventLogRecommendedScanner(null, Helper.HIGH_TIME);
			HTableInterface tableInterface = eventsTable.getTable(Helper.CP);
			assertEquals(recommendeds.size(), count(tableInterface, scan));
		}

		@Test
		public void testUserViewScanner() throws IOException
		{
			Scan scan = tool.getEventLogViewScanner(null, null);
			HTableInterface tableInterface = eventsTable.getTable(Helper.CP);
			int count = count(tableInterface, scan);

			assertEquals(views.size(), count);
		}

		@Test
		public void testUserViewEndTimeScanner() throws IOException
		{
			Scan scan = tool.getEventLogViewScanner(null, Helper.HIGH_TIME);
			HTableInterface tableInterface = eventsTable.getTable(Helper.CP);
			assertEquals(views.size(), count(tableInterface, scan));
		}

		@Test
		public void testRatingsScanner() throws IOException
		{
			Scan scan = tool.getRatingsScanner(Helper.COLLECTION, null, null);
			HTableInterface tableInterface = userTable.getTable(Helper.CP);
			assertEquals(ratings.size(), count(tableInterface, scan));

			scan = tool.getRatingsScanner(Helper.COLLECTION, 2001L, 5001L);
			tableInterface = userTable.getTable(Helper.CP);
			LOG.info("COUNTING: {}", count(tableInterface, scan));
		}

		@Test
		public void testRatingsEndDateScanner() throws IOException
		{
			Scan scan = tool.getRatingsScanner(Helper.COLLECTION, null, Helper.HIGH_TIME + 1);
			HTableInterface tableInterface = userTable.getTable(Helper.CP);
			assertEquals(ratings.size(), count(tableInterface, scan));
		}
	}

	public static class C_RunTasks
	{

		@Test
		public void testRunTasks() throws IOException, InterruptedException, ClassNotFoundException
		{

			StatisticsTool tool = new StatisticsTool(new EmptyTask(task));

			String outputTableName = task.get(TaskConfig.OUTPUT_TABLE);
			String eventlogTableName = task.get(TaskConfig.STATS_EVENTLOG_TABLE);
			String userTableName = task.get(TaskConfig.STATS_USER_TABLE);

			Config.getInstance().set(Config.STATISTICS_POPULARITY_TYPES, Helper.POPULARITY_TYPES);

			tool.configure(eventlogTableName, userTableName, outputTableName);
			assertTrue(tool.execute());
		}
	}

	public static class D_GetResults
	{

		static Set<Long> recommendedUsers;
		static Set<Long> ratedItems;
		private static Set<Long> ratedUsers;

		static Map<Long, Long> ratingsPerItem;
		static Map<Long, Long> ratingsPerUser;

		private static Set<Recommended> recommended;
		private static Set<Rating> ratings;

		@BeforeClass
		public static void init()
		{
			recommended = Helper.getRecommended();
			recommendedUsers = new TreeSet<Long>();
			for (Recommended r : recommended)
			{
				recommendedUsers.add(r.getUser());
			}

			ratings = Helper.getUserRatings();
			ratedItems = new HashSet<Long>();
			ratedUsers = new HashSet<Long>();
			ratingsPerItem = new HashMap<Long, Long>();
			ratingsPerUser = new HashMap<Long, Long>();
			for (Rating r : ratings)
			{
				ratedUsers.add(r.getUser());
				ratedItems.add(r.getItem());

				Long rts = ratingsPerItem.get(r.getItem());
				if (rts == null)
				{
					ratingsPerItem.put(r.getItem(), 1L);
				}
				else
				{
					ratingsPerItem.put(r.getItem(), rts + 1);
				}

				rts = ratingsPerUser.get(r.getUser());
				if (rts == null)
				{
					ratingsPerUser.put(r.getUser(), 1L);
				}
				else
				{
					ratingsPerUser.put(r.getUser(), rts + 1);
				}
			}

		}

		@Test
		public void getViewed() throws IOException
		{
			List<GroupedData> result = statistics.getViewed(Helper.CP, Helper.RECOMMENDER, null, null, null, 100);
			LOG.info("viewed: {}", result.size());
			// for (GroupedData g : result)
			// {
			// LOG.info("{}", g);
			// }
		}

		@Test
		public void getTopViewed() throws IOException
		{
			List<GroupedData> result = statistics.getTopViewed(Helper.CP, Helper.RECOMMENDER, null, null, 100);
			LOG.info("top-viewed: {}", result.size());
			// for (GroupedData g : result)
			// {
			// // LOG.info("{}", g);
			// }

		}

		@Test
		public void testSourcesPerRecommenderCollection() throws IOException
		{
			List<GroupedData> result = statistics.getSources(Helper.CP, Helper.RECOMMENDER, null, null, null, 100);
			assumeNotNull(result);
			assertEquals(1, result.size());
			for (GroupedData g : result)
			{
				// LOG.info("{}", g);
				assertEquals(Helper.RECOMMENDER, g.getRecommender());
				assumeNotNull(g.getSource());
				assumeNotNull(g.getDate());
				assumeNotNull(g.getCount());
				assertEquals(Helper.HIGH_TIME + 1, g.getDate().longValue());
				assertTrue(g.getCount() == 50 || g.getCount() == 100);
			}
		}

		@Test
		public void testTopSourcesPerRecommenderCollection() throws IOException
		{
			List<GroupedData> result = statistics.getTopSources(Helper.CP, Helper.RECOMMENDER, null, null, 5);
			assumeNotNull(result);
			assertEquals(1, result.size());
			for (GroupedData g : result)
			{
				// LOG.info("{}", g);
				assertEquals(Helper.RECOMMENDER, g.getRecommender());
				assumeNotNull(g.getSource());
				assumeNotNull(g.getDate());
				assumeNotNull(g.getCount());
				assertEquals(Helper.HIGH_TIME + 1, g.getDate().longValue());
				assertTrue(g.getCount() == 50 || g.getCount() == 100);
			}
		}

		@Test
		public void testRecommendedToUserItem()
		{

			// <collection,recommender,source,item,user,count,date><null,R,null,0,0,4,201>
			assertEquals(10, recommendedUsers.size());
			for (Long user : recommendedUsers)
			{
				List<GroupedData> result = statistics.getRecommendedUserItem(Helper.CP, Helper.COLLECTION,
						user.longValue(), null, null, null, recommended.size());
				assumeNotNull(result);
				assertTrue(result.size() > 0);
				for (GroupedData g : result)
				{
					assertEquals(Helper.COLLECTION, g.getCollection());
					assertEquals(Helper.HIGH_TIME + 1, g.getDate().longValue());
					assumeNotNull(g.getCount());
					assertEquals(100, g.getCount().longValue());

				}
			}
		}

		@Test
		public void testRecommendedToUser()
		{
			assertEquals(10, recommendedUsers.size());
			for (Long user : recommendedUsers)
			{
				List<GroupedData> result = statistics.getRecommendedUser(Helper.CP, Helper.COLLECTION, user.longValue(),
						null, null, recommended.size());
				assumeNotNull(result);
				assertTrue(result.size() > 0);
				for (GroupedData g : result)
				{
					assertEquals(Helper.COLLECTION, g.getCollection());
					assertEquals(Helper.HIGH_TIME + 1, g.getDate().longValue());
					assertEquals(500, g.getCount().longValue());
					// LOG.info("{}", g);
				}
			}
		}

		@Test
		public void testUserRated()
		{
			List<GroupedData> rateds = statistics.getUserRated(Helper.CP, Helper.COLLECTION, null, null, null,
					ratings.size());
			assumeNotNull(rateds);
			assertEquals(ratedUsers.size(), rateds.size());
			int total = 0;
			for (GroupedData g : rateds)
			{
				// LOG.info("{}", g);
				assertEquals(Helper.COLLECTION, g.getCollection());
				assertEquals(Helper.HIGH_TIME + 1, g.getDate().longValue());
				assertEquals(ratingsPerUser.get(g.getUser()), g.getCount());
				total += g.getCount();
			}
			// <collection,recommender,source,item,user,count,date><C,null,null,null,99,10,201>
			assertEquals(ratings.size(), total);
		}

		@Test
		public void testItemRated()
		{
			List<GroupedData> rateds = statistics.getItemRated(Helper.CP, Helper.COLLECTION, null, null, null,
					ratings.size());
			assumeNotNull(rateds);
			assertEquals(ratedItems.size(), rateds.size());
			int total = 0;
			for (GroupedData g : rateds)
			{
				assertEquals(Helper.COLLECTION, g.getCollection());
				assertEquals(Helper.HIGH_TIME + 1, g.getDate().longValue());
				assertEquals(ratingsPerItem.get(g.getItem()), g.getCount());
				total += g.getCount();
			}
			assertEquals(ratings.size(), total);
		}

		@Test
		public void testPopularity()
		{
			SortedMap<Long, String> types = PopularityStatistics.getTypes(Helper.POPULARITY_TYPES);
			for (Map.Entry<Long, String> entry : types.entrySet())
			{

				List<Popularity> result = statistics.getPopularity(Helper.CP, Helper.COLLECTION, entry.getValue(), null,
						100);
				assertTrue(result.size() > 0);
				int sum = 0;
				for (Popularity r : result)
				{
					sum += r.getPopularity();
				}
				LOG.info("testing: '{}' - total: {}%", entry.getValue(), sum);

				// sum should be about 100 (between 99 and 101)
				assertTrue(sum >= 98);
				assertTrue(sum <= 101);
			}
		}
	}

}
