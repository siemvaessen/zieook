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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.mapreduce.TaskConfig;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * TaskTableTest.java created 30 mei 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class TaskTableTest
{

	private static final Logger LOG = LoggerFactory.getLogger(TaskTableTest.class);

	private static HBaseManager manager;
	private static HBaseConfigurationTable table;
	private static TaskTable tasks;

	static final String prefix = TaskTable.class.getPackage().getName();
	static final long date = System.currentTimeMillis() / 1000;
	static final long dateStart = date + 2;
	static final long dateEnd = date + 10;

	static final String CP = prefix + ".cp";
	static final String COLLECTION = prefix + ".collection";

	private TaskConfig getExampleTask()
	{
		TaskConfig task = new TaskConfig();

		task.setProperty(TaskConfig.TASK_TYPE, TaskConfig.TASK_TYPE_COLLECTION);
		task.setProperty(TaskConfig.CLASSNAME, TaskTableTest.class.getCanonicalName());
		task.setProperty(TaskConfig.CP, CP); // the content provider
		task.setProperty(TaskConfig.COLLECTION, COLLECTION); // the collection name
		task.setProperty(TaskConfig.DATA_TYPE, TaskConfig.DATA_TYPE_MOVIELENS);
		task.setNext(date, TimeUnit.SECONDS);
		task.setProperty(TaskConfig.TASK_START, dateStart);
		task.setProperty(TaskConfig.TASK_START, dateEnd);

		return task;
	}

	private void assertEqualsTask(TaskConfig task, TaskConfig other)
	{
		TreeMap<String, String> map = new TreeMap<String, String>(task.toMap());
		TreeMap<String, String> otherMap = new TreeMap<String, String>(other.toMap());
		LOG.info("COMPARING:\n {} \nWITH\n {}", task.toString(), other.toString());
		assertEquals(map.size(), otherMap.size());

		for (Map.Entry<String, String> entry : map.entrySet())
		{
			LOG.info("COMPARING key {} with <{}>", entry.getKey(), entry.getValue() + "," + otherMap.get(entry.getKey()));
			String value = entry.getValue();

			assertTrue(otherMap.containsKey(entry.getKey()));
			assertEquals(value, otherMap.get(entry.getKey()));
		}
	}

	/**
	 * Try to retrieve an id that's not in the task table yet.
	 * start with 10000L
	 * @return an id >= 10000L
	 */
	private long getId()
	{
		long result = 10000L;
		while (tasks.readTask(result) != null)
		{
			result += 100L;
		}
		return result;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		LOG.info("STARTING TEST");

		manager = HBaseManager.getInstance("mulisch");

		table = new HBaseConfigurationTable(manager);
		if (!table.tableExists())
		{
			assertTrue(table.create());
			LOG.info("create table: {}", table.getTableName());
		}
		else
		{
			LOG.info("table exists: {}", table.getTableName());
		}
		tasks = new TaskTable(table);

	}

	@Before
	public void cleanup()
	{
		tasks.deleteCollectionTasks(CP, COLLECTION);
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.data.hbase.dao.TaskTable#createTask(nl.gridline.zieook.mapreduce.TaskConfig)}.
	 */
	@Test
	public void testCreateReadUpdateTask()
	{
		TaskConfig task = getExampleTask();
		long id = getId();
		LOG.info("task id {}", id);

		task.setId(id);

		// create a task:
		tasks.createTask(task);

		// read task:
		TaskConfig newTask = tasks.readTask(id);

		// check if equals:
		assertEqualsTask(task, newTask);

		// delete task:
		tasks.deleteTask(id);

		TaskConfig nullTask = tasks.readTask(id);
		assertTrue(nullTask == null);
	}

	/**
	 * Test method for {@link nl.gridline.zieook.data.hbase.dao.TaskTable#searchTasks(java.lang.String)}.
	 */
	@Test
	public void testSearchTasksString()
	{

		TaskConfig task = getExampleTask();
		Long id = getId();
		LOG.info("task id {}", id);
		task.setId(id);

		// create the task:
		tasks.createTask(task);

		// read task:
		assertEqualsTask(task, tasks.readTask(id));

		List<Long> result = tasks.searchTasks(TaskTableTest.class.getCanonicalName());
		assertEquals(1, result.size());
		assertEquals(id, result.get(0));

		tasks.deleteTask(id);
		assertEquals(null, tasks.readTask(id));
	}

	@Test
	public void testSearchTasksMultipleString()
	{

		TaskConfig task1 = getExampleTask();
		TaskConfig task2 = getExampleTask();
		TaskConfig task3 = getExampleTask();
		List<TaskConfig> list = new ArrayList<TaskConfig>(3);
		list.add(task3);
		list.add(task2);
		list.add(task1);

		long id1 = getId();
		LOG.info("creating TASK ID {}", id1);
		task1.setId(id1);
		task1.setProperty(TaskConfig.CLASSNAME, TaskTableTest.class.getCanonicalName());
		tasks.createTask(task1);

		Long id2 = getId();
		LOG.info("creating TASK ID {}", id2);
		task2.setId(id2);
		task2.setProperty(TaskConfig.CLASSNAME, TaskTableTest.class.getCanonicalName());
		tasks.createTask(task2);

		Long id3 = getId();
		LOG.info("creating TASK ID {}", id3);
		task3.setId(id3);
		task3.setProperty(TaskConfig.CLASSNAME, TaskTableTest.class.getCanonicalName());
		tasks.createTask(task3);

		// read task:
		assertEqualsTask(task1, tasks.readTask(id1));
		assertEqualsTask(task2, tasks.readTask(id2));
		assertEqualsTask(task3, tasks.readTask(id3));

		List<Long> result = tasks.searchTasks(TaskTableTest.class.getCanonicalName());
		assertEquals(3, result.size());
		int j = 0;
		for (Long i : result)
		{
			LOG.info("i={}", i);
			assertEqualsTask(list.get(j), tasks.readTask(i));
			j++;
		}

	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.data.hbase.dao.TaskTable#searchLatestTask(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testSearchLatestTask()
	{
		TaskConfig task = getExampleTask();
		Long id = getId();
		LOG.info("task id {}", id);
		task.setId(id);
		task.setProperty(TaskConfig.CLASSNAME, TaskTableTest.class.getCanonicalName());

		// create the task:
		tasks.createTask(task);

		// TASK_TYPE_COLLECTION
		TaskConfig latest = tasks.searchLatestTask(CP, TaskConfig.TASK_TYPE_COLLECTION, COLLECTION);
		assertEqualsTask(task, latest);

	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.data.hbase.dao.TaskTable#searchCompletedTask(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testSearchCompletedTask()
	{
		TaskConfig task = getExampleTask();
		Long id = getId();
		LOG.info("task id {}", id);
		task.setId(id);
		tasks.createTask(task);
		// there should be no task:
		TaskConfig latest = tasks.searchCompletedTask(CP, TaskConfig.TASK_TYPE_COLLECTION, COLLECTION);
		assertEquals(null, latest);

		task = getExampleTask();
		id = getId();
		LOG.info("task id {}", id);
		task.setId(id);
		task.setProperty(TaskConfig.CLASSNAME, TaskTableTest.class.getCanonicalName());
		// set succeed:
		task.setSucceed();
		// create the task:
		tasks.createTask(task);
		latest = tasks.searchCompletedTask(CP, TaskConfig.TASK_TYPE_COLLECTION, COLLECTION);
		// should return 'task'
		assertEqualsTask(task, latest);

	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.data.hbase.dao.TaskTable#searchFutureTasks(java.lang.String, java.lang.String, java.lang.String, long)}
	 * .
	 */
	@Test
	public void testSearchFutureTasks()
	{
		TaskConfig task = getExampleTask();
		Long id = getId();
		LOG.info("task id {}", id);
		task.setId(id);

		// create the task:
		tasks.createTask(task);

		// TASK_TYPE_COLLECTION
		// task.setNext(date, TimeUnit.SECONDS); --> look for a task that is scheduled in the future:

		// this should result in 0
		List<Long> latest = tasks.searchFutureTasks(CP, TaskConfig.TASK_TYPE_COLLECTION, COLLECTION, (date), -1);
		assertEquals(0, latest.size());

		// this should return 1 item:
		latest = tasks.searchFutureTasks(CP, TaskConfig.TASK_TYPE_COLLECTION, COLLECTION, (date - 1), -1);
		assertEquals(1, latest.size());

		assertEqualsTask(task, tasks.readTask(latest.get(0)));
	}

	/**
	 * Test method for {@link nl.gridline.zieook.data.hbase.dao.TaskTable#searchTaskByClassName(java.lang.String)}.
	 */
	@Test
	public void testSearchTaskByClassName()
	{
		TaskConfig task = getExampleTask();
		Long id = getId();
		LOG.info("task id {}", id);
		task.setId(id);

		tasks.createTask(task);

		List<Long> list = tasks.searchTaskByClassName(TaskTableTest.class.getCanonicalName());
		assertTrue(list != null);
		assertEquals(1, list.size());

		TaskConfig newtask = tasks.readTask(list.get(0));
		assertEqualsTask(task, newtask);
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.data.hbase.dao.TaskTable#searchTasks(java.lang.String, java.lang.String, java.lang.String, long, long)}
	 * .
	 */
	@Test
	public void testSearchTasksStringStringStringLongLong()
	{
		TaskConfig task = getExampleTask();
		Long id = getId();
		LOG.info("task id {}", id);
		task.setId(id);

		task.setProperty(TaskConfig.CLASSNAME, TaskTableTest.class.getCanonicalName());
		task.setSucceed();

		// create the task:
		tasks.createTask(task);

		// TASK_TYPE_COLLECTION
		// task.setNext(date, TimeUnit.SECONDS); --> look for a task that is scheduled in the future:

		// this should result in 0
		List<Long> latest = tasks.searchTasks(CP, TaskConfig.TASK_TYPE_COLLECTION, COLLECTION, dateStart + 1,
				dateStart + 2);
		assertEquals(0, latest.size());

		latest = tasks.searchTasks(CP, TaskConfig.TASK_TYPE_COLLECTION, COLLECTION, dateStart - 1, dateStart + 1);
		assertEquals(1, latest.size());

		assertEqualsTask(task, tasks.readTask(latest.get(0)));
	}

}
