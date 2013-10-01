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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.EventLogTable;
import nl.gridline.zieook.data.hbase.dao.StatisticsTable;
import nl.gridline.zieook.data.hbase.dao.UserTable;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseStatisticsTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.model.GroupedData;
import nl.gridline.zieook.model.UserView;
import nl.gridline.zieook.runners.statistics.StatisticsTool;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * FrontEndSimulationTest.java created 9 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class FrontEndSimulationTest
{
	static
	{
		System.setProperty("gridline.project.name", "zieook");

	}

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

		manager = HBaseManager.getInstance("mulisch");

		statsTable = new HBaseStatisticsTable(manager);
		// // statsTable.drop(Helper.CP);
		// if (!statsTable.tableExists(Helper.CP))
		// {
		// assertTrue(statsTable.create(Helper.CP));
		// }
		assertTrue(statsTable.tableExists("movielens4"));

		userTable = new HBaseUserTable(manager);
		// userTable.drop(Helper.CP);
		// if (!userTable.tableExists(Helper.CP))
		// {
		// assertTrue(userTable.create(Helper.CP));
		// }
		assertTrue(userTable.tableExists("movielens4"));

		eventsTable = new HBaseEventLogTable(manager);
		// eventsTable.drop(Helper.CP);
		// if (!eventsTable.tableExists(Helper.CP))
		// {
		// assertTrue(eventsTable.create(Helper.CP));
		// }
		// assertTrue(eventsTable.tableExists(Helper.CP));

		statistics = new StatisticsTable(statsTable);
		users = new UserTable(userTable);
		events = new EventLogTable(eventsTable);
	}

	@Test
	@Ignore
	public void testScanner() throws IOException
	{

		StatisticsTool tool = new StatisticsTool(new EmptyTask());
		Scan scan = tool.getEventLogViewScanner(null, 1320836450822L);

		HTableInterface tableInterface = eventsTable.getTable("movielens4");
		ResultScanner scanner = tableInterface.getScanner(scan);
		try
		{
			Result r = null;
			while ((r = scanner.next()) != null)
			{
				System.out.println("data " + new UserView(r.getFamilyMap(StatisticsTool.EVENTLOG_COLUMN_USERVIEW)));
				// stop when size is reached:
			}
		}
		finally
		{
			scanner.close();
			eventsTable.putTable(tableInterface);
		}

	}

	@Test
	@Ignore
	public void testRated()
	{
		List<GroupedData> result = statistics.getTopSources("movielens4", "itembased", null, null, 100);
		for (GroupedData g : result)
		{
			System.out.println(g);
		}

		// result = statistics.getRatedTop("movielens4", "mvlens", null, null, 100);
		// // for (GroupedData g : result)
		// // {
		// // System.out.println(g);
		// // }
		//
		// // statistics.getRecommendedUserItem(cp, collection, user, from, startDate, endDate, size)
		// // statistics.getRecommendedUser("movielens4", "mvlens", 1, startDate, endDate, size);
		// List<UserView> views = events.getViews("movielens4", "itembased", 1, null, null, 100);
		//
		// for (UserView v : views)
		// {
		// System.out.println(v);
		// }
		//
		// result = statistics.getSources("movielens4", "itembased", null, null, null, 100);
		// for (GroupedData g : result)
		// {
		// System.out.println(g);
		// }

	}
}
