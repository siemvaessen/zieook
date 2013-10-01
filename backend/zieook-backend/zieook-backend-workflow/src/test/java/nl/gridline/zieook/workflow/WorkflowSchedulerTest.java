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
package nl.gridline.zieook.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.tasks.DummyTask;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * WorkflowSchedulerTest.java created 15 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@RunWith(SortedSuite.class)
public class WorkflowSchedulerTest
{
	static
	{
		System.setProperty("gridline.project.name", "zieook");
	}

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowSchedulerTest.class);

	private static WorkflowScheduler ws1;
	private static WorkflowScheduler ws2;
	private static WorkflowScheduler ws3;
	private static WorkflowScheduler ws4;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		ws1 = new WorkflowScheduler();
		ws1.startup();

		ws2 = new WorkflowScheduler();
		ws2.startup();

		ws3 = new WorkflowScheduler();
		ws3.startup();

		ws4 = new WorkflowScheduler();
		ws4.startup();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		ws1.shutdown();
		ws2.shutdown();
		ws3.shutdown();
		ws4.shutdown();
	}

	@Test
	public void testState()
	{
		assertTrue(ws1.state());
		assertTrue(ws2.state());
		assertTrue(ws3.state());
		assertTrue(ws4.state());
	}

	public static class A_SingleScheduler
	{

		@Test
		public void schedule() throws IOException
		{
			// test if we can schedule a task, on any of ws1,ws2,ws3 and see if it get's executed.
			// we need a dummy task -
			TaskConfig config = new TaskConfig();
			config.setProperty(TaskConfig.CLASSNAME, DummyTask.class.getCanonicalName());
			config.setNext(System.currentTimeMillis() + 10000, TimeUnit.MILLISECONDS);
			config.setProperty("sleep-time", "3"); // sleeps 10 seconds

			int tasks = ws1.getTasks(DummyTask.class.getCanonicalName()).size();
			ws1.schedule(config);
			assertEquals(tasks + 1, ws1.getTasks(DummyTask.class.getCanonicalName()).size());
		}

		@Test
		public void testCancel() throws IOException, InterruptedException
		{

			final String cp = "test-content-provider";
			final String collection = "test-collection";

			TaskConfig config = new TaskConfig();
			config.setProperty(TaskConfig.CP, cp);
			config.setProperty(TaskConfig.COLLECTION, collection);
			config.setProperty(TaskConfig.TASK_TYPE, TaskConfig.TASK_TYPE_COLLECTION);
			config.setProperty(TaskConfig.CLASSNAME, DummyTask.class.getCanonicalName());
			config.setProperty("sleep-time", "10");
			config.setNext(System.currentTimeMillis(), TimeUnit.MILLISECONDS);

			LOG.info("scheduling test task");
			config = ws1.schedule(config);

			Thread.sleep(3000);

			LOG.info("cancelling test task");
			ws1.cancelCollectionTask(cp, collection);

			Thread.sleep(1000);

			// task should be cancelled by now...
			LOG.info("test cancel");
			TaskConfig state = ws1.getTask(config.getId());
			Thread.sleep(1000);
			assertTrue(state.isCancelled());

			Thread.sleep(1000);
		}

		@Test
		public void testRunning() throws InterruptedException, IOException
		{
			TaskConfig config = new TaskConfig();
			config.setProperty(TaskConfig.CLASSNAME, DummyTask.class.getCanonicalName());
			config.setProperty("sleep-time", "2");
			config.setNext(System.currentTimeMillis() + 3000, TimeUnit.MILLISECONDS);
			config = ws1.schedule(config);

			for (int i = 0; i < 16; i++)
			{
				Thread.sleep(500);
				TaskConfig state = ws1.getTask(config.getId());
				if (state.isRunning())
				{
					// EXCEPTIION; path in ZieOokTask.getProgress(ZieOokTask.java:242) eindigd op /
					assertTrue(true);
					return;
				}
			}
			fail("The task should have run by now.");
		}

		@Test
		public void testFailure() throws InterruptedException, IOException
		{
			TaskConfig config = new TaskConfig();
			config.setProperty(TaskConfig.CLASSNAME, DummyTask.class.getCanonicalName());
			config.setProperty("sleep-time", "1");
			config = ws1.schedule(config);
			assertTrue(config.isFailed());
		}

		@Test
		public void testSuccess() throws IOException, InterruptedException
		{
			TaskConfig config = new TaskConfig();
			config.setProperty(TaskConfig.CLASSNAME, DummyTask.class.getCanonicalName());
			config.setProperty("sleep-time", "5");
			config.setNext(System.currentTimeMillis() + 2000, TimeUnit.MILLISECONDS);

			config = ws1.schedule(config);
			Thread.sleep(10000);
			// java.lang.IllegalArgumentException: Path must not end with / character
			// at nl.gridline.zieook.tasks.ZieOokTask.getProgress(ZieOokTask.java:242)
			TaskConfig state = ws1.getTask(config.getId());
			LOG.info("succeeed: {}", state.isSucceeded());
			LOG.info("running: ", state.isRunning());

			assertTrue(state.isSucceeded());
		}

		@Test
		public void testMultipleSuccess() throws IOException, InterruptedException
		{
			TaskConfig config0 = new TaskConfig();
			config0.setProperty(TaskConfig.CLASSNAME, DummyTask.class.getCanonicalName());
			config0.setProperty("sleep-time", "1");
			config0.setNext(System.currentTimeMillis() + 3000, TimeUnit.MILLISECONDS);

			TaskConfig config1 = new TaskConfig();
			config1.setProperty(TaskConfig.CLASSNAME, DummyTask.class.getCanonicalName());
			config1.setProperty("sleep-time", "1");
			config1.setNext(System.currentTimeMillis() + 3000, TimeUnit.MILLISECONDS);

			config0 = ws1.schedule(config0);
			config1 = ws1.schedule(config1);
			Thread.sleep(20000);
			// java.lang.IllegalArgumentException: Path must not end with / character
			// at nl.gridline.zieook.tasks.ZieOokTask.getProgress(ZieOokTask.java:242)
			TaskConfig state0 = ws1.getTask(config0.getId());
			assertTrue(state0.isSucceeded());

			TaskConfig state1 = ws1.getTask(config1.getId());
			assertTrue(state1.isSucceeded());
		}
	}

	public static class B_MultipleSchedulers
	{
		@Test
		public void testCancel() throws IOException, InterruptedException
		{
			// create a task & execute on scheduler 1:

			final String cp = "test-content-provider";
			final String collection = "test-collection";

			TaskConfig config = new TaskConfig();
			config.setProperty(TaskConfig.CP, cp);
			config.setProperty(TaskConfig.COLLECTION, collection);
			config.setProperty(TaskConfig.TASK_TYPE, TaskConfig.TASK_TYPE_COLLECTION);
			config.setProperty(TaskConfig.CLASSNAME, DummyTask.class.getCanonicalName());
			config.setProperty("sleep-time", "10");
			config.setNext(System.currentTimeMillis(), TimeUnit.MILLISECONDS);

			LOG.info("scheduling test task");
			config = ws1.schedule(config);

			// wait a little:
			Thread.sleep(3000);

			// use scheduler 2 to cancel the task:
			LOG.info("cancelling test task");
			ws2.cancelCollectionTask(cp, collection);

			Thread.sleep(1000);

			// task should be cancelled by now...
			LOG.info("test cancel");
			TaskConfig state = ws2.getTask(config.getId());
			Thread.sleep(1000);
			assertTrue(state.isCancelled());

			Thread.sleep(1000);
		}
	}
}
