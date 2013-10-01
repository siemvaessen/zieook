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
import static org.junit.Assume.assumeNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.SortedSuite;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.Helper;
import nl.gridline.zieook.model.Recommended;

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
 * RecommendedTest.java created 31 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@RunWith(SortedSuite.class)
public class RecommendedTest
{

	private static final Logger LOG = LoggerFactory.getLogger(RecommendedTest.class);

	private static HBaseManager manager;
	private static HBaseEventLogTable table;
	private static EventLogTable userstatstable;
	public static Set<Recommended> recommendeds;

	@BeforeClass
	public static void setUp() throws Exception
	{
		manager = HBaseManager.getInstance("mulisch");
		table = new HBaseEventLogTable(manager);

		if (!table.tableExists(Helper.CP))
		{
			table.create(Helper.CP);
		}
		userstatstable = new EventLogTable(table);
		recommendeds = Helper.getRecommended();
	}

	@AfterClass
	public static void tearDown() throws Exception
	{
		if (table.tableExists(Helper.CP))
		{
			table.drop(Helper.CP);
		}
	}

	public static class A_PutGet
	{

		public A_PutGet()
		{

		}

		@Test
		public void putGetData() throws IOException
		{
			for (Recommended r : recommendeds)
			{
				userstatstable.putRecommend(Helper.CP, Helper.RECOMMENDER, r, TimeUnit.MILLISECONDS);

				Recommended newr = userstatstable.getRecommended(Helper.CP, Helper.RECOMMENDER, r.getType(), r.getUser(),
						r.getItem(), r.getDate(), TimeUnit.MILLISECONDS);

				assumeNotNull(r);
				assumeNotNull(newr);
				assertEquals(r, newr);
			}
		}
	}

	public static class B_RangeTests
	{
		public B_RangeTests()
		{

		}

		@Test
		public void rangeAll() throws IOException
		{
			List<Recommended> result = new ArrayList<Recommended>();
			for (long user = 0; user < Helper.USER_STATSSIZE; user++)
			{
				result.addAll(userstatstable.getRecommended(Helper.CP, Helper.RECOMMENDER,
						Recommended.RecommenderType.ITEM_BASED, user, null, null, recommendeds.size()));
			}
			assertEquals(recommendeds.size(), result.size());
		}

		@Test
		public void rangeUser() throws IOException
		{
			long user = 0;
			List<Recommended> result = userstatstable.getRecommended(Helper.CP, Helper.RECOMMENDER,
					Recommended.RecommenderType.ITEM_BASED, user, null, null, recommendeds.size());
			LOG.info("{}", result.size());

			assertEquals((Helper.HIGH_TIME - Helper.LOW_TIME) / Helper.USER_STATSSIZE, result.size());
		}

		@Test
		public void rangeUserHalfEnd() throws IOException
		{
			long user = 0;
			List<Recommended> result = userstatstable.getRecommended(Helper.CP, Helper.RECOMMENDER,
					Recommended.RecommenderType.ITEM_BASED, user, null, Helper.HIGH_TIME / 2, recommendeds.size());
			LOG.info("{}", result.size());

			assertEquals((Helper.HIGH_TIME - Helper.LOW_TIME) / Helper.USER_STATSSIZE / 2, result.size());
		}

		@Test
		public void rangeUserHalfStart() throws IOException
		{
			long user = 0;
			List<Recommended> result = userstatstable.getRecommended(Helper.CP, Helper.RECOMMENDER,
					Recommended.RecommenderType.ITEM_BASED, user, (Helper.HIGH_TIME - Helper.LOW_TIME) / 2, null,
					recommendeds.size());
			LOG.info("{}", result.size());

			assertEquals((Helper.HIGH_TIME - Helper.LOW_TIME) / Helper.USER_STATSSIZE / 2, result.size());
		}

		@Test
		public void rangeUserFullTime() throws IOException
		{
			long user = 0;
			List<Recommended> result = userstatstable.getRecommended(Helper.CP, Helper.RECOMMENDER,
					Recommended.RecommenderType.ITEM_BASED, user, Helper.LOW_TIME, Helper.HIGH_TIME, recommendeds.size());
			LOG.info("{}", result.size());

			assertEquals((Helper.HIGH_TIME - Helper.LOW_TIME) / Helper.USER_STATSSIZE, result.size());
		}

	}

	public static class C_Delete
	{
		public C_Delete()
		{

		}

		@Test
		public void delete()
		{
		}
	}

}
