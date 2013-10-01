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
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.SortedSuite;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.Helper;
import nl.gridline.zieook.model.UserView;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * UserStatistics.java created 27 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@RunWith(SortedSuite.class)
public class UserViewTest
{
	private static HBaseManager manager;
	private static HBaseEventLogTable table;
	private static EventLogTable userstatstable;
	public static Set<UserView> views;

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
		views = Helper.getUserViews();
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
		public void putget() throws IOException
		{
			for (UserView view : views)
			{
				userstatstable.putView(Helper.CP, Helper.RECOMMENDER, view, TimeUnit.MILLISECONDS);
				UserView newview = userstatstable.getView(Helper.CP, Helper.RECOMMENDER, view.getUser(), view.getItem(),
						view.getDate(), TimeUnit.MILLISECONDS);
				assumeNotNull(view);
				assumeNotNull(newview);
				assertEquals(view, newview);
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
			List<UserView> result = new ArrayList<UserView>();

			for (long user = 0; user < Helper.USER_STATSSIZE; user++)
			{
				result.addAll(userstatstable.getViews(Helper.CP, Helper.RECOMMENDER, user, null, null, views.size(), true));
			}
			assertEquals(views.size(), result.size());
		}

		@Test
		public void singleUserRange() throws IOException
		{
			List<UserView> result = new ArrayList<UserView>();
			result.addAll(userstatstable.getViews(Helper.CP, Helper.RECOMMENDER, 0, null, null, views.size(), true));

			assertEquals(views.size() / 10, result.size());
		}

		@Test
		public void singleUserTimeRange() throws IOException
		{
			List<UserView> result = new ArrayList<UserView>();
			result.addAll(userstatstable.getViews(Helper.CP, Helper.RECOMMENDER, 0, Helper.LOW_TIME, Helper.HIGH_TIME / 2,
					views.size(), true));

			assertEquals(views.size() / 10 / 2, result.size());
		}

		@Test
		public void singleUserTimeRangeOpenStart() throws IOException
		{
			List<UserView> result = new ArrayList<UserView>();
			result.addAll(userstatstable.getViews(Helper.CP, Helper.RECOMMENDER, 0, null, Helper.HIGH_TIME / 2,
					views.size(), true));

			assertEquals(views.size() / 10 / 2, result.size());
		}

		@Test
		public void singleUserTimeRangeOpenEnd() throws IOException
		{
			List<UserView> result = new ArrayList<UserView>();
			result.addAll(userstatstable.getViews(Helper.CP, Helper.RECOMMENDER, 0, Helper.LOW_TIME
					+ (Helper.HIGH_TIME / 2), null, views.size(), true));
			// for (UserView r : result)
			// {
			// LOG.info("{}", r);
			// }
			assertEquals(views.size() / 10 / 2, result.size());
		}

	}

	public static class C_Delete
	{
		public C_Delete()
		{

		}

		@Test
		public void delete() throws IOException
		{
			for (UserView view : views)
			{
				userstatstable.deleteView(Helper.CP, Helper.RECOMMENDER, view.getUser(), view.getItem(), view.getDate(),
						TimeUnit.MILLISECONDS);

				UserView newview = userstatstable.getView(Helper.CP, Helper.RECOMMENDER, view.getUser(), view.getItem(),
						view.getDate(), TimeUnit.MILLISECONDS);

				assumeTrue(newview == null);
			}
		}

	}
}
