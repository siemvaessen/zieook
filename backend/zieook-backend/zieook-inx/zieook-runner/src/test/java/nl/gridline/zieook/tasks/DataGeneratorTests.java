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
import java.util.Random;

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.CollectionTable;
import nl.gridline.zieook.data.hbase.dao.UserTable;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseStatisticsTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.model.CollectionItem;
import nl.gridline.zieook.model.Rating;
import nl.gridline.zieook.runners.statistics.StatisticsTool;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * DataGeneratorTests.java created 15 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class DataGeneratorTests
{
	private static final Logger LOG = LoggerFactory.getLogger(DataGeneratorTests.class);

	static
	{
		System.setProperty("gridline.project.name", "zieook");

	}

	private static final String CP = "testCP006";
	private static final String COLLECTION = "am2";

	private static final long seed = 100;
	private static final int users = 200;
	private static final long now = System.currentTimeMillis();

	private static Random rnd;
	private static TaskConfig task;
	private static HBaseManager manager;

	@BeforeClass
	public static void init() throws MasterNotRunningException, ZooKeeperConnectionException
	{

		String outputTableName = new HBaseStatisticsTable().getTableName(CP);
		String eventlogTableName = new HBaseEventLogTable().getTableName(CP);
		String userTableName = new HBaseUserTable().getTableName(CP);

		task = new TaskConfig();
		task.setProperty(TaskConfig.CLASSNAME, StatisticsTask.class.getCanonicalName());
		task.setProperty(TaskConfig.TASK_TYPE, TaskConfig.TASK_TYPE_STATISTICS);
		task.setProperty(TaskConfig.OUTPUT_TABLE, outputTableName);
		task.setProperty(TaskConfig.STATS_EVENTLOG_TABLE, eventlogTableName);
		task.setProperty(TaskConfig.STATS_USER_TABLE, userTableName);

		// round statistics end to current midnight:
		task.setProperty(TaskConfig.STATS_END, now);
		task.setProperty(TaskConfig.CP, CP);
		task.setProperty(TaskConfig.COLLECTION, COLLECTION);
		manager = HBaseManager.getInstance("mulisch");
		rnd = new Random(seed);

		// HBaseStatisticsTable table = new HBaseStatisticsTable(manager);
		// table.drop(CP);
		// table.create(CP);

	}

	@Test
	@Ignore
	public void initData()
	{
		CollectionTable collectiontable = new CollectionTable(new HBaseCollectionTable(manager));
		UserTable usertable = new UserTable(new HBaseUserTable(manager));

		for (int user = 1; user < users; user++)
		{
			List<CollectionItem> list = collectiontable.getItems(CP, COLLECTION, 200);
			for (CollectionItem i : list)
			{
				// random date between now and 6 months ago:
				long date = now - (rnd.nextInt(15768000));
				if (rnd.nextDouble() > 0.5)
				{
					Rating rating = new Rating(user, i.getId(), COLLECTION, "no-source", date, 1);
					LOG.info("writing: {}", rating);
					usertable.putRating(CP, COLLECTION, rating, null);
				}
			}
		}
	}

	@Test
	@Ignore
	public void executeStatistics() throws IOException, InterruptedException, ClassNotFoundException
	{
		StatisticsTool tool = new StatisticsTool(new EmptyTask(task));

		String outputTableName = task.get(TaskConfig.OUTPUT_TABLE);
		String eventlogTableName = task.get(TaskConfig.STATS_EVENTLOG_TABLE);
		String userTableName = task.get(TaskConfig.STATS_USER_TABLE);

		tool.configure(eventlogTableName, userTableName, outputTableName);
		assertTrue(tool.execute());
	}
}
