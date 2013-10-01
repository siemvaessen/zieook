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
package nl.gridline.zieook.workflow.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import java.util.Map;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.tasks.DummyTask;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * ZieOokSchedulerTest.java created 25 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class ZieOokSchedulerTest
{

	private static final Logger LOG = LoggerFactory.getLogger(ZieOokSchedulerTest.class);

	static
	{
		System.setProperty("gridline.project.name", "zieook");
	}
	private static ZieOokScheduler scheduler;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Config config = Config.getInstance();

		final int retries = config.getInt(Config.ZIEOOKID_RETRIES, 5);
		final int timeout = config.getInt(Config.ZIEOOK_SCHEDULER_SESSIONTIMEOUT, 8000); // set default timeout to 8
																													// seconds
		final String zookeeper = config.getZooKeeperHost();

		scheduler = new ZieOokScheduler(zookeeper, retries, timeout);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		scheduler.shutdown();
	}

	@Test
	public void testTaskEquals() throws KeeperException, InterruptedException
	{
		long now = System.currentTimeMillis();
		long post = 100;
		scheduler.offer(now + 1, post);
		long i = scheduler.poll();
		assertEquals(post, i);
	}

	@Test
	// sanity test:
	public void testTaskDataNoZk() throws KeeperException, InterruptedException
	{
		TaskConfig config = new TaskConfig();
		ZieOokTask task = new DummyTask().setConfig(config);
		task.setProgress(); // no-zookeeper - should not throw an error
	}

	@Test
	// sanity test
	public void testTaskData() throws KeeperException, InterruptedException
	{
		final long ID = 100;

		// pre:
		TaskConfig config = new TaskConfig();
		config.setId(ID);
		ZieOokTask task = new DummyTask().setZooKeeper(scheduler.getZooKeeper()).setConfig(config);

		task.setProgress();

		Map<Long, Long> result = ZieOokTask.getProgress(scheduler.getZooKeeper());
		printMap(result);

		assertEquals(1, result.size());
		assumeNotNull(result.get(ID));

		ZieOokTask.cancel(scheduler.getZooKeeper(), ID);
		assertTrue(task.isCancelled());

		result = ZieOokTask.getProgress(scheduler.getZooKeeper());
		assertEquals(1, result.size());

		// this should delete the task progress node:
		ZieOokTask.taskDone(scheduler.getZooKeeper(), ID);

		result = ZieOokTask.getProgress(scheduler.getZooKeeper());
		printMap(result);

		assertEquals(0, result.size());
	}

	@Ignore
	private void printMap(Map<Long, Long> map)
	{
		for (Map.Entry<Long, Long> entry : map.entrySet())
		{
			LOG.info("<{},{}>", entry.getKey(), entry.getValue());
		}
	}

	@Test
	public void testMultiTaskList() throws KeeperException, InterruptedException
	{

		final long ID1 = 100;
		final long ID2 = 200;

		ZooKeeper zk = scheduler.getZooKeeper();

		assumeNotNull(ZieOokTask.getProgress(zk));
		assertEquals(ZieOokTask.getProgress(zk).size(), 0);

		// pre:
		ZieOokTask task1 = new DummyTask().setZooKeeper(zk).setConfig(new TaskConfig().setId(ID1));

		assertEquals(0, ZieOokTask.getProgress(zk).size());

		ZieOokTask task2 = new DummyTask().setZooKeeper(zk).setConfig(new TaskConfig().setId(ID2));

		assertEquals(0, ZieOokTask.getProgress(zk).size());

		task1.setProgress();
		assertEquals(1, ZieOokTask.getProgress(zk).size());

		task2.setProgress();
		assertEquals(2, ZieOokTask.getProgress(zk).size());

		ZieOokTask.taskDone(zk, ID1);
		assertEquals(1, ZieOokTask.getProgress(zk).size());

		// this should not throw an error & not delete other data:
		ZieOokTask.taskDone(zk, ID1);
		assertEquals(1, ZieOokTask.getProgress(zk).size());
		ZieOokTask.taskDone(zk, ID1);
		assertEquals(1, ZieOokTask.getProgress(zk).size());

		// remove the other task:
		ZieOokTask.taskDone(zk, ID2);
		assertEquals(0, ZieOokTask.getProgress(zk).size());

	}
}
