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
package nl.gridline.zieook.data.hbase.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.SortedSuite;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.data.hbase.model.Helper;
import nl.gridline.zieook.model.Rating;
import nl.gridline.zieook.model.User;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * UserRatingStatistics.java created 27 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@RunWith(SortedSuite.class)
public class UserRatingTest
{

	private static HBaseManager manager;
	private static HBaseUserTable table;
	private static UserTable usertable;
	public static Set<Rating> ratings;

	@BeforeClass
	public static void setUp() throws Exception
	{
		manager = HBaseManager.getInstance("mulisch");
		table = new HBaseUserTable(manager);

		// reset:
		if (table.tableExists(Helper.CP))
		{
			table.drop(Helper.CP);
		}

		if (!table.tableExists(Helper.CP))
		{
			table.create(Helper.CP);
		}
		usertable = new UserTable(table);
		ratings = Helper.getUserRatings();
	}

	@AfterClass
	public static void tearDown() throws Exception
	{
		if (table.tableExists(Helper.CP))
		{
			table.drop(Helper.CP);
		}
	}

	public static class A_PutGetTests
	{
		private static final Logger LOG = LoggerFactory.getLogger(UserRatingTest.A_PutGetTests.class);

		public A_PutGetTests()
		{

		}

		@Test
		public void putget()
		{
			for (Rating rating : ratings)
			{
				usertable.putRating(Helper.CP, Helper.COLLECTION, rating, TimeUnit.MILLISECONDS);
				Rating newrating = usertable.getRating(Helper.CP, Helper.COLLECTION, rating.getUser(), rating.getItem());
				assertTrue(null != rating);
				assertTrue(null != newrating);
				assertEquals(rating, newrating);
			}
		}
	}

	public static class B_RangeTest
	{
		private static final Logger LOG = LoggerFactory.getLogger(UserRatingTest.B_RangeTest.class);

		private static Set<Rating> rangeRatings;
		private static Set<Rating> timedRatings;

		@BeforeClass
		public static void init()
		{
			// get some test data:
			rangeRatings = Helper.getSingleUserRating();
			for (Rating r : rangeRatings)
			{
				usertable.putRating(Helper.CP, Helper.COLLECTION, r, TimeUnit.MILLISECONDS);
			}

			// get some test data:
			timedRatings = Helper.getTimedRatings();
			for (Rating r : timedRatings)
			{
				usertable.putRating(Helper.CP, Helper.COLLECTION, r, TimeUnit.MILLISECONDS);
			}

		}

		@Test
		public void sanity()
		{
			assertEquals(usertable.getRatingCount(Helper.CP, Helper.TIMED_USERRATING), timedRatings.size());
		}

		@Test
		public void ratingCounts()
		{
			List<User> userdata = usertable.getUsers(Helper.CP, 0, 1000);
			LOG.info("count ratings per user, found {} users", userdata.size());
			for (User u : userdata)
			{
				if (u.getRatingCount() == null)
				{
					LOG.info("user {} has no ratings?", u);
					continue;
				}
				assertTrue(10 == u.getRatingCount() || 20 == u.getRatingCount());
			}

		}

		@Test
		public void singleUserRatingContent()
		{
			List<Rating> userratings = usertable.getRatings(Helper.CP, Helper.COLLECTION, Helper.USER_LOW, null, null, 1,
					0L);

			assertTrue(userratings.size() > 0);

			for (Rating r : ratings)
			{
				if (r.getUser() == Helper.USER_LOW)
				{
					LOG.info("DATA: {}", r.toJSON());
				}
			}
			for (Rating r : userratings)
			{
				LOG.info("RATING: {}", r.toJSON());
				assertTrue(ratings.contains(r));
			}

		}

		@Test
		public void allRatings()
		{
			Set<Rating> result = new HashSet<Rating>();
			for (int i = Helper.USER_LOW; i < Helper.USER_LOW + Helper.USER_SIZE; i++)
			{
				result.addAll(usertable.getRatings(Helper.CP, Helper.COLLECTION, i, null, null, ratings.size(), 0L));
			}
			assertEquals(ratings.size(), result.size());
		}

		@Test
		public void allRatingsRange()
		{

			// test if all data is stored:
			List<Rating> result = usertable.getRatings(Helper.CP, Helper.COLLECTION, Helper.SINGLE_USERRATING, null, null,
					rangeRatings.size(), null);
			assertEquals(rangeRatings.size(), result.size());
		}

		@Test
		public void rangeZeroToOneRange()
		{
			// get rating range: limiting the rating between: 0<rating<1
			List<Rating> result = usertable.getRatings(Helper.CP, Helper.COLLECTION, Helper.SINGLE_USERRATING, 0.0, 1.0,
					rangeRatings.size(), null);

			// for (Rating r : result)
			// {
			// LOG.info("{}", r);
			// }

			assertEquals(4, result.size());

		}

		@Test
		public void rangeOneToThreeRange()
		{
			// get rating range: limiting the rating between: 1<rating<3
			List<Rating> result = usertable.getRatings(Helper.CP, Helper.COLLECTION, Helper.SINGLE_USERRATING, 1.0, 3.0,
					rangeRatings.size(), null);
			assertEquals(8, result.size());
		}

		@Test
		public void itemStartRange()
		{
			// get a range of items: start from item 10 upwards
			List<Rating> result = usertable.getRatings(Helper.CP, Helper.COLLECTION, Helper.SINGLE_USERRATING, null, null,
					rangeRatings.size(), 10L);
			assertEquals(11, result.size());
		}

		@Test
		public void dateRanges()
		{

			// get all within time range range:
			List<Rating> result = usertable.getRatings(Helper.CP, Helper.COLLECTION, Helper.TIMED_USERRATING,
					Helper.TIMED_ITEMLOW, Helper.TIMED_ITEMHIGH + 1, timedRatings.size());

			LOG.info("expect: {} got {}", timedRatings.size(), result.size());
			assertEquals(timedRatings.size(), result.size());

			// add a time limit to the request:
			long duration = (Helper.TIMED_ITEMHIGH - Helper.TIMED_ITEMLOW) / 2;
			result = usertable.getRatings(Helper.CP, Helper.COLLECTION, Helper.TIMED_USERRATING, Helper.TIMED_ITEMLOW,
					Helper.TIMED_ITEMLOW + duration, timedRatings.size());

			LOG.info("expect: {} got {}", timedRatings.size() / 2, result.size());
			assertEquals(timedRatings.size() / 2, result.size());

			result = usertable.getRatings(Helper.CP, Helper.COLLECTION, Helper.TIMED_USERRATING, -100L, null,
					timedRatings.size());

			LOG.info("expect: {} got {}", timedRatings.size(), result.size());
			assertEquals(timedRatings.size(), result.size());

			// check if all data was loaded:
			result = usertable.getRatings(Helper.CP, Helper.COLLECTION, Helper.TIMED_USERRATING, null, null,
					timedRatings.size());

			LOG.info("expect: {} got {}", timedRatings.size(), result.size());
			assertEquals(timedRatings.size(), result.size());

		}
	}

	public static class C_DeleteTest
	{
		public C_DeleteTest()
		{

		}

		@Test
		public void deleteTest()
		{
			for (Rating rating : ratings)
			{
				Rating oldRating = usertable.getRating(Helper.CP, Helper.COLLECTION, rating.getUser(), rating.getItem());
				assumeNotNull(rating);
				assumeNotNull(oldRating);
				assertEquals(rating, oldRating);

				usertable.deleteRating(Helper.CP, Helper.COLLECTION, rating.getUser(), rating.getItem());
				Rating newrating = usertable.getRating(Helper.CP, Helper.COLLECTION, rating.getUser(), rating.getItem());
				assumeTrue(newrating == null);
			}
		}

	}

}
