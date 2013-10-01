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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.mapreduce.TaskConfig;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores tasks that will be executed in some future in the database
 * <p />
 * Project zieook-data<br />
 * TaskTable.java created 15 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class TaskTable
{

	private static final Logger LOG = LoggerFactory.getLogger(TaskTable.class);

	private final HBaseConfigurationTable table;

	private static final byte[] COLUMN_ZIEOOK_TASK = Bytes.toBytes(HBaseTableConstants.CONFIGURATION_COLUMN_ZIEOOK_TASK);

	// private static final String TASK_PREFIX = "TASK";

	private static final byte[] TASK_ID_ROW = Bytes.toBytes("000_task_incr_row");
	private static final byte[] TASK_ID_QUALIFIER = Bytes.toBytes("task_id");

	public TaskTable(HBaseConfigurationTable table)
	{
		this.table = table;
	}

	/**
	 * Create a unique id based on a row - columns - qualifier value.
	 * http://whynosql.com/aggregation-with-hbase/
	 * @return
	 * @throws IOException
	 */
	public long getTaskId() throws IOException
	{
		final long result = table.getTable().incrementColumnValue(TASK_ID_ROW, COLUMN_ZIEOOK_TASK, TASK_ID_QUALIFIER, 1L);
		LOG.debug("Generated new task id: {}", result);
		return result;
	}

	/**
	 * Create a task with the given id
	 * @param task
	 */
	public void createTask(TaskConfig task)
	{
		LOG.debug("creating / updating task <type,id> = <{},{}> to HBASE", task.get(TaskConfig.TASK_TYPE),
				task.get(TaskConfig.TASK_ID));
		Put put = new Put(RowKeys.getTaskRow(task.getId()));

		Map<String, String> data = task.toMap();
		for (Map.Entry<String, String> entry : data.entrySet())
		{
			if (entry.getValue() != null) // do not write null values
			{
				put.add(COLUMN_ZIEOOK_TASK, Bytes.toBytes(entry.getKey()), Bytes.toBytes(entry.getValue()));
			}
		}
		table.put(put);
	}

	public void updateTask(TaskConfig task)
	{
		LOG.debug("writing updated task <type,id> = <{},{}> to HBASE", task.get(TaskConfig.TASK_TYPE),
				task.get(TaskConfig.TASK_ID));
		deleteTask(task.getId());
		createTask(task);
	}

	/**
	 * Read a task with the given id
	 * @param id
	 * @return
	 */
	public TaskConfig readTask(long id)
	{
		Get get = new Get(RowKeys.getTaskRow(id)).addFamily(COLUMN_ZIEOOK_TASK);

		Result result = table.get(get);
		if (!result.isEmpty())
		{
			NavigableMap<byte[], byte[]> map = result.getFamilyMap(COLUMN_ZIEOOK_TASK);
			Map<String, String> taskmap = new HashMap<String, String>(4);

			for (Map.Entry<byte[], byte[]> entry : map.entrySet())
			{
				taskmap.put(Bytes.toString(entry.getKey()), Bytes.toString(entry.getValue()));
			}
			return new TaskConfig(taskmap);
		}

		return null;
	}

	public List<Long> searchTasks(String classname)
	{
		final byte[] TASK_ID = Bytes.toBytes(TaskConfig.TASK_ID);
		Scan scan = new Scan();
		SingleColumnValueFilter filter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
				Bytes.toBytes(TaskConfig.CLASSNAME), CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(classname)));
		filter.setFilterIfMissing(true);
		scan.setFilter(filter);

		List<Long> result = new ArrayList<Long>();
		try
		{
			HTableInterface tableInterface = table.getTable();
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				// get the rows from the table:
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					KeyValue value = rr.getColumnLatest(COLUMN_ZIEOOK_TASK, TASK_ID);
					if (value != null)
					{
						// the task id is stored as a string (like the whole taskconfig) - but we only need this particular
						// value: - if the parselong fails, there's something really wrong - a coding bug -
						result.add(Long.parseLong(Bytes.toString(value.getValue())));
					}
				}
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to SCAN table '" + table.getTableName(null) + "' for recommenders", e);
		}
		return result;
	}

	public TaskConfig searchLatestTask(String cp, String type, String value)
	{
		// create a filter, that looks for successfully completed tasks:
		FilterList filters = new FilterList();

		// we are looking only in this family:
		filters.addFilter(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(COLUMN_ZIEOOK_TASK)));

		// for the given cp:
		SingleColumnValueFilter cpfilter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK, Bytes.toBytes(TaskConfig.CP),
				CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(cp)));
		cpfilter.setFilterIfMissing(true);
		filters.addFilter(cpfilter);

		filterTaskType(type, value, filters);

		Scan scan = new Scan().addFamily(COLUMN_ZIEOOK_TASK).setFilter(filters);

		try
		{
			HTableInterface tableInterface = table.getTable();

			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				Result rr = scanner.next();
				if (rr != null && !rr.isEmpty())
				{
					NavigableMap<byte[], byte[]> map = rr.getFamilyMap(COLUMN_ZIEOOK_TASK);
					Map<String, String> taskmap = new HashMap<String, String>(4);
					for (Map.Entry<byte[], byte[]> entry : map.entrySet())
					{
						taskmap.put(Bytes.toString(entry.getKey()), Bytes.toString(entry.getValue()));
					}
					return new TaskConfig(taskmap);
				}
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to SCAN table '" + table.getTableName(null) + "' for recommenders", e);
		}

		return null;
	}

	/**
	 * return the oldest task that is successfully completed
	 * @param cp
	 * @param recommender
	 * @return
	 */
	public TaskConfig searchCompletedTask(String cp, String type, String value)
	{
		// create a filter, that looks for successfully completed tasks:
		FilterList filters = new FilterList();
		// we are looking only in this family:
		filters.addFilter(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(COLUMN_ZIEOOK_TASK)));

		// for the given cp:
		SingleColumnValueFilter cpfilter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK, Bytes.toBytes(TaskConfig.CP),
				CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(cp)));
		cpfilter.setFilterIfMissing(true);
		filters.addFilter(cpfilter);

		filterTaskType(type, value, filters);

		// And the task should be completed:
		SingleColumnValueFilter taskokfilter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
				Bytes.toBytes(TaskConfig.TASK_RESULT), CompareOp.EQUAL, new BinaryComparator(
						Bytes.toBytes(TaskConfig.TASK_RESULT_OK)));
		taskokfilter.setFilterIfMissing(true);
		filters.addFilter(taskokfilter);

		LOG.debug("scanning for task with: <cp,type,value> = <{},{}>", cp + "," + type, value);

		Scan scan = new Scan().addFamily(COLUMN_ZIEOOK_TASK).setFilter(filters);

		try
		{
			HTableInterface tableInterface = table.getTable();

			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				Result rr = scanner.next();
				if (rr != null && !rr.isEmpty())
				{
					NavigableMap<byte[], byte[]> map = rr.getFamilyMap(COLUMN_ZIEOOK_TASK);
					Map<String, String> taskmap = new HashMap<String, String>(4);
					for (Map.Entry<byte[], byte[]> entry : map.entrySet())
					{
						taskmap.put(Bytes.toString(entry.getKey()), Bytes.toString(entry.getValue()));
					}
					return new TaskConfig(taskmap);
				}
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to SCAN table '" + table.getTableName(null) + "' for recommenders", e);
		}

		return null;
	}

	public List<Long> searchFutureTasks(String cp, String type, String value, long startTime, int size)
	{

		// we need a specific filter for TaskConfig.CP and TaskConfig.recommender (key, value)
		// and are only interested in the taskconfig column:

		FilterList filters = new FilterList();

		filters.addFilter(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(COLUMN_ZIEOOK_TASK)));
		SingleColumnValueFilter cpfilter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK, Bytes.toBytes(TaskConfig.CP),
				CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(cp)));
		cpfilter.setFilterIfMissing(true);
		filters.addFilter(cpfilter);

		filterTaskType(type, value, filters);

		// handle open start:
		if (startTime != -1)
		{
			SingleColumnValueFilter filter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
					Bytes.toBytes(TaskConfig.NEXT), CompareOp.GREATER, new BinaryComparator(
							Bytes.toBytes(getLongAsString(startTime))));
			filter.setFilterIfMissing(true);
			filters.addFilter(filter);
		}

		// assign the filter:

		Scan scan = new Scan().addFamily(COLUMN_ZIEOOK_TASK).setFilter(filters);

		final byte[] TASK_ID = Bytes.toBytes(TaskConfig.TASK_ID);

		// scan!!!
		List<Long> result = new ArrayList<Long>();
		try
		{
			HTableInterface tableInterface = table.getTable();

			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				int count = 0;
				// get the rows from the table:
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					KeyValue kv = rr.getColumnLatest(COLUMN_ZIEOOK_TASK, TASK_ID);
					if (kv != null)
					{
						// the task id is stored as a string (like the whole taskconfig) - but we only need this particular
						// value: - if the parselong fails, there's something really wrong - a coding bug -
						result.add(Long.parseLong(Bytes.toString(kv.getValue())));
						count++;
					}
					if (size > 0 && count >= size)
					{
						break;
					}
				}
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to SCAN table '" + table.getTableName(null) + "' for recommenders", e);
		}

		return result;
	}

	public List<Long> searchTaskByClassName(String classname)
	{
		// we need to be able to get the task id from the task:
		final byte[] TASK_ID = Bytes.toBytes(TaskConfig.TASK_ID);
		List<Filter> filterlist = new ArrayList<Filter>(4);
		filterlist.add(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(COLUMN_ZIEOOK_TASK)));

		SingleColumnValueFilter cpFilter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
				Bytes.toBytes(TaskConfig.CLASSNAME), CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(classname)));
		cpFilter.setFilterIfMissing(true);
		filterlist.add(cpFilter);
		FilterList filter = new FilterList(filterlist);

		// assign the filter:
		Scan scan = new Scan().setFilter(filter);

		// scan!!!
		List<Long> result = new ArrayList<Long>();
		try
		{
			HTableInterface tableInterface = table.getTable();

			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				// get the rows from the table:
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					KeyValue kv = rr.getColumnLatest(COLUMN_ZIEOOK_TASK, TASK_ID);
					if (kv != null)
					{
						// the task id is stored as a string (like the whole taskconfig) - but we only need this particular
						// value: - if the parselong fails, there's something really wrong - a coding bug -
						result.add(Long.parseLong(Bytes.toString(kv.getValue())));
					}
				}
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to SCAN table '" + table.getTableName(null) + "' for recommenders", e);
		}

		return result;

	}

	/**
	 * returns all task id's in batches of size
	 * @param start
	 * @param size
	 * @return
	 */
	public List<Long> getAllTasks(long start, long size)
	{
		// we need to be able to get the task id from the task:
		final byte[] TASK_ID = Bytes.toBytes(TaskConfig.TASK_ID);

		//
		Scan scan = new Scan(RowKeys.getTaskRow(start));
		Filter filter = new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(COLUMN_ZIEOOK_TASK));
		scan.setFilter(filter);

		// scan!!!
		List<Long> result = new ArrayList<Long>();
		try
		{
			HTableInterface tableInterface = table.getTable();
			int i = 0;
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				// get the rows from the table:
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					KeyValue kv = rr.getColumnLatest(COLUMN_ZIEOOK_TASK, TASK_ID);
					if (kv != null)
					{
						// the task id is stored as a string (like the whole taskconfig) - but we only need this particular
						// value: - if the parselong fails, there's something really wrong - a coding bug -
						result.add(Long.parseLong(Bytes.toString(kv.getValue())));
					}
					i++;
					if (i == size)
					{
						break;
					}
				}
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to SCAN table '" + table.getTableName(null) + "' for recommenders", e);
		}

		return result;
	}

	/**
	 * Search for a task, executed in the past, or to be executed in the future, or currently running
	 * @param cp - content provider
	 * @param type - task type: statistics-task, collection-task,recommender-task
	 * @param value - a value for the given type, like the recommender or collection
	 * @param start start date in ms
	 * @param end end date in ms
	 * @return a list of task id's
	 */
	public List<Long> searchTasks(String cp, String type, String value, long start, long end)
	{

		// we need to be able to get the task id from the task:
		final byte[] TASK_ID = Bytes.toBytes(TaskConfig.TASK_ID);

		//
		Scan scan = new Scan().addFamily(COLUMN_ZIEOOK_TASK);
		// we need a specific filter for TaskConfig.CP and TaskConfig.recommender (key, value)
		// and are only interested in the taskconfig column:
		FilterList filters = new FilterList();

		filters.addFilter(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(COLUMN_ZIEOOK_TASK)));
		SingleColumnValueFilter cpfilter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK, Bytes.toBytes(TaskConfig.CP),
				CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(cp)));
		cpfilter.setFilterIfMissing(true);

		filters.addFilter(cpfilter);

		filterTaskType(type, value, filters);

		// handle open start:
		if (start != -1)
		{
			SingleColumnValueFilter filter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
					Bytes.toBytes(TaskConfig.TASK_START), CompareOp.GREATER_OR_EQUAL, new BinaryComparator(
							Bytes.toBytes(getLongAsString(start))));
			filter.setFilterIfMissing(true);
			filters.addFilter(filter);
		}

		// handle open end:
		if (end != -1)
		{
			SingleColumnValueFilter filter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
					Bytes.toBytes(TaskConfig.TASK_START), CompareOp.LESS, new BinaryComparator(
							Bytes.toBytes(getLongAsString(end))));
			filter.setFilterIfMissing(true);
			filters.addFilter(filter);
		}

		// assign the filter:
		scan.setFilter(filters);

		// scan!!!
		List<Long> result = new ArrayList<Long>();
		try
		{
			HTableInterface tableInterface = table.getTable();

			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				// get the rows from the table:
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					KeyValue kv = rr.getColumnLatest(COLUMN_ZIEOOK_TASK, TASK_ID);
					if (kv != null)
					{
						// the task id is stored as a string (like the whole taskconfig) - but we only need this particular
						// value: - if the parselong fails, there's something really wrong - a coding bug -
						result.add(Long.parseLong(Bytes.toString(kv.getValue())));
					}
				}
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to SCAN table '" + table.getTableName(null) + "' for recommenders", e);
		}

		return result;
	}

	/**
	 * Delete a task with the given id
	 * @param id
	 */
	public void deleteTask(long id)
	{
		LOG.debug("deleting task with {}", id);
		Delete delete = new Delete(RowKeys.getTaskRow(id));
		table.delete(delete);
	}

	/**
	 * Removes all tasks related to collection import
	 * @param cp content provider
	 * @param collection collection name
	 */
	public void deleteCollectionTasks(String cp, String collection)
	{

		FilterList filter = new FilterList();

		// column family filter:
		filter.addFilter(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(COLUMN_ZIEOOK_TASK)));

		// filter on task type:
		SingleColumnValueFilter tasktype = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
				Bytes.toBytes(TaskConfig.TASK_TYPE), CompareOp.EQUAL, new BinaryComparator(
						Bytes.toBytes(TaskConfig.TASK_TYPE_COLLECTION)));
		tasktype.setFilterIfMissing(true);
		filter.addFilter(tasktype);

		// filter on content provider:
		SingleColumnValueFilter cpfilter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK, Bytes.toBytes(TaskConfig.CP),
				CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(cp)));
		cpfilter.setFilterIfMissing(true);
		filter.addFilter(cpfilter);

		// filter on collection: (this is not really needed for now, because there's only one collection per cp)
		if (collection != null)
		{
			SingleColumnValueFilter coll = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
					Bytes.toBytes(TaskConfig.COLLECTION), CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(collection)));
			cpfilter.setFilterIfMissing(true);
			filter.addFilter(coll);
		}

		Scan scan = new Scan().addFamily(COLUMN_ZIEOOK_TASK).setFilter(filter);

		deleteAll(scan, cp);
	}

	public void deleteRecommenderTasks(String cp, String recommender)
	{
		FilterList filters = new FilterList();

		// column family filter:
		filters.addFilter(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(COLUMN_ZIEOOK_TASK)));

		// filter on task type:
		SingleColumnValueFilter tasktype = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
				Bytes.toBytes(TaskConfig.TASK_TYPE), CompareOp.EQUAL, new BinaryComparator(
						Bytes.toBytes(TaskConfig.TASK_TYPE_RECOMMENDER)));
		tasktype.setFilterIfMissing(true);
		filters.addFilter(tasktype);

		// content provider filter:
		SingleColumnValueFilter cpfilter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK, Bytes.toBytes(TaskConfig.CP),
				CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(cp)));
		cpfilter.setFilterIfMissing(true);
		filters.addFilter(cpfilter);

		// filter on recommender:
		if (recommender != null)
		{
			SingleColumnValueFilter recFilter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
					Bytes.toBytes(TaskConfig.RECOMMENDER), CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(recommender)));
			recFilter.setFilterIfMissing(true);
			filters.addFilter(recFilter);
		}

		Scan scan = new Scan().addFamily(COLUMN_ZIEOOK_TASK).setFilter(filters);

		deleteAll(scan, cp);
	}

	/**
	 * @param cp
	 */
	public void deleteStatisticsTasks(String cp)
	{
		FilterList filters = new FilterList();
		// column family filter:
		filters.addFilter(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(COLUMN_ZIEOOK_TASK)));

		// filter on task type:
		SingleColumnValueFilter tasktype = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
				Bytes.toBytes(TaskConfig.TASK_TYPE), CompareOp.EQUAL, new BinaryComparator(
						Bytes.toBytes(TaskConfig.TASK_TYPE_STATISTICS)));
		tasktype.setFilterIfMissing(true);
		filters.addFilter(tasktype);

		// content provider filter:
		SingleColumnValueFilter cpfilter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK, Bytes.toBytes(TaskConfig.CP),
				CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(cp)));
		cpfilter.setFilterIfMissing(true);
		filters.addFilter(cpfilter);

		Scan scan = new Scan().addFamily(COLUMN_ZIEOOK_TASK).setFilter(filters);

		deleteAll(scan, cp);
	}

	/**
	 * Delete all rows produces by scan
	 * @param scan a scan - be careful to produce a correct scan, it deletes stuf from the table!
	 * @param cp content provider name
	 */
	private void deleteAll(Scan scan, String cp)
	{
		final byte[] TASK_ID = Bytes.toBytes(TaskConfig.TASK_ID);

		// scan!!!
		List<Delete> result = new ArrayList<Delete>();
		long count = 0;
		try
		{
			HTableInterface tableInterface = table.getTable();

			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				// get the rows from the table:
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					KeyValue kv = rr.getColumnLatest(COLUMN_ZIEOOK_TASK, TASK_ID);
					if (kv != null)
					{
						// the task id is stored as a string (like the whole taskconfig) - but we only need this particular
						// value: - if the parselong fails, there's something really wrong - a coding bug -
						result.add(new Delete(rr.getRow()));
					}
					else
					{
						LOG.error("This is a bug: some items are left behind, while cleaning: {}", rr);
					}

					// delete every thousand rows:
					if (result.size() > 0 && result.size() % 1000 == 0)
					{
						count += result.size();
						table.delete(cp, result);
						result.clear();
						LOG.info("intermediate delete <{}> total now <{}>", result.size(), count);
					}
				}
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to SCAN table '" + table.getTableName(null) + "' for recommenders", e);
		}

		// flush all that's left
		count += result.size();
		table.delete(cp, result);
		result.clear();

		LOG.info("Deleted {} old tasks for <{}>", count, cp);
	}

	/**
	 * Filter task type
	 * @param type
	 * @param value
	 * @param filterlist
	 * @return
	 */
	private FilterList filterTaskType(String type, String value, FilterList filterlist)
	{

		if (TaskConfig.TASK_TYPE_RECOMMENDER.equals(type))
		{

			SingleColumnValueFilter filter1 = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
					Bytes.toBytes(TaskConfig.TASK_TYPE), CompareOp.EQUAL, new BinaryComparator(
							Bytes.toBytes(TaskConfig.TASK_TYPE_RECOMMENDER)));
			filter1.setFilterIfMissing(true);
			filterlist.addFilter(filter1);

			if (value != null)
			{
				SingleColumnValueFilter filter2 = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
						Bytes.toBytes(TaskConfig.RECOMMENDER), CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(value)));
				filter2.setFilterIfMissing(true);
				filterlist.addFilter(filter2);
			}
		}
		else if (TaskConfig.TASK_TYPE_COLLECTION.equals(type))
		{
			SingleColumnValueFilter filter1 = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
					Bytes.toBytes(TaskConfig.TASK_TYPE), CompareOp.EQUAL, new BinaryComparator(
							Bytes.toBytes(TaskConfig.TASK_TYPE_COLLECTION)));

			filter1.setFilterIfMissing(true);
			filterlist.addFilter(filter1);
			if (value != null)
			{
				SingleColumnValueFilter filter2 = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
						Bytes.toBytes(TaskConfig.COLLECTION), CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(value)));
				filter2.setFilterIfMissing(true);
				filterlist.addFilter(filter2);
			}
		}
		else if (TaskConfig.TASK_TYPE_STATISTICS.equals(type))
		{
			SingleColumnValueFilter filter = new SingleColumnValueFilter(COLUMN_ZIEOOK_TASK,
					Bytes.toBytes(TaskConfig.TASK_TYPE), CompareOp.EQUAL, new BinaryComparator(
							Bytes.toBytes(TaskConfig.TASK_TYPE_STATISTICS)));
			filter.setFilterIfMissing(true);
			filterlist.addFilter(filter);
		}

		return filterlist;
	}

	private String getLongAsString(long id)
	{
		return String.format("%019d", id);
	}

}
