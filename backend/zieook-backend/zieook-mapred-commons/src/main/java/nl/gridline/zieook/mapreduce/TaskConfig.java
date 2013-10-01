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
package nl.gridline.zieook.mapreduce;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.LoggerFactory;

/**
 * ZieOok task configuration object wraps an HadhMap and provides a bunch of key constants.
 * <p />
 * Project zieook-configuration<br />
 * TaskConfig.java created 16 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class TaskConfig implements Serializable, Cloneable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5325300873391715636L;

	// basic task information:
	public static final String TASK_ID = "task.id";
	public static final String NEXT = "task.next";
	public static final String INTERVAL = "task.interval";
	public static final String CP = "task.cp";
	public static final String RECOMMENDER = "task.recommender";

	public static final String COLLECTION_LANGUAGE = "collection.language";

	public static final String CLASSNAME = "task.classname";
	public static final String COLLECTION = "task.collection";
	public static final String INPUT_PATH = "task.path.input";
	public static final String OUTPUT_PATH = "task.path.output";
	public static final String OUTPUT_TABLE = "task.table.output";
	public static final String INPUT_TABLE = "task.table.input";

	// statistics tasks look a bit different:
	public static final String STATISTICS_KEY = "task.statistics";
	public static final String STATISTICS_VALUE = "statistics";

	// progress information, the actual progress and message is stored in zookeeper and included upon request:
	public static final String TASK_MESSAGE = "task.message";
	public static final String TASK_PROGRESS = "task.progress";
	public static final String TASK_RUNNING = "task.running";
	public static final String TASK_CANCELLED = "task.cancelled";

	public static final String TASK_TYPE = "task.type";
	public static final String TASK_TYPE_STATISTICS = "statistics-task";
	public static final String TASK_TYPE_COLLECTION = "collection-task";
	public static final String TASK_TYPE_RECOMMENDER = "recommender-task";

	// task statistics:
	public static final String TASK_START = "task.start.stamp";
	public static final String TASK_END = "task.end.stamp";
	public static final String TASK_RESULT = "task.result";
	public static final String TASK_RESULT_OK = "true";
	public static final String TASK_RESULT_FAILED = "false";

	// different data types, e.g. movielens:
	public static final String DATA_TYPE = "task.collection.datatype";
	public static final String DATA_TYPE_MOVIELENS = "movielens";
	public static final String DATA_TYPE_EDIT = "edit";
	public static final String DATA_TYPE_OAI = "oai";

	// the recommender tasks:
	public static final String RECOMMENDER_TYPE = "recommender.type";
	public static final String RECOMMENDER_TYPE_ITEMBASED = "item-based";
	public static final String RECOMMENDER_TYPE_USERBASED = "user-based";
	public static final String RECOMMENDER_TYPE_TEXTBASED = "text-based";
	public static final String RECOMMENDER_TAALSERVER = "use-taalserver";
	public static final String[] RECOMMENDER_TYPES = {RECOMMENDER_TYPE_ITEMBASED, RECOMMENDER_TYPE_USERBASED,
			RECOMMENDER_TYPE_TEXTBASED};

	// the different parts of a data set
	public static final String DATA_PART = "task.collection.part";
	public static final String DATA_PART_COLLECTION = "collection";
	public static final String DATA_PART_USERS = "users";
	public static final String DATA_PART_RATINGS = "ratings";

	// known recommender settings:
	// !!!DO NOT!!! change these values, some of them are directly derived from Mahout 0.4 constants:
	public static final String NUM_RECOMMENDATIONS = "numRecommendations";
	public static final String USERS_FILE = "usersFile";
	public static final String ITEMS_FILE = "itemsFile";
	public static final String FILTER_FILE = "filterFile";
	public static final String BOOLEAN_DATA = "booleanData";
	public static final String MAX_PREFS_PER_USER = "maxPrefsPerUser";
	public static final String MAX_SIMILARITRIES_PER_ITEM = "maxSimilaritiesPerItem";
	public static final String MAX_CO_OCCURRENCES_PER_ITEM = "maxCooccurrencesPerItem";
	public static final String NUMBER_OF_COLUMNS = "numberOfColumns";
	public static final String MAX_SIMILARITIES_PER_ROW = "maxSimilaritiesPerRow";
	public static final String DISTRIBUTED_SIMILARITY_CLASSNAME = "distributedSimilarityClassname";
	public static final String RECOMMENDATIONS_TO_FILE = "recommendations.fileoutput";
	public static final String[] RECOMMENDER_PROPERTIES = {NUM_RECOMMENDATIONS, BOOLEAN_DATA, MAX_PREFS_PER_USER,
			MAX_SIMILARITRIES_PER_ITEM, MAX_SIMILARITIES_PER_ROW, MAX_CO_OCCURRENCES_PER_ITEM, NUMBER_OF_COLUMNS,
			DISTRIBUTED_SIMILARITY_CLASSNAME};

	// text analysis settings:
	public static final String TEXT_ANALYSIS_USE_DESCRIPTION = "text.analysis.usedescription";
	public static final String TEXT_ANALYSIS_USE_TITLE = "text.analysis.usetitle";
	public static final String TEXT_ANALYSIS_USE_KEYWORDS = "text.analysis.usekeywords";

	public static final String TEXT_LANGUAGE = "text.language";
	public static final String TEXT_DOCUMENT_COUNT = "text.document.count";

	// OAI settings, allow limits on date range & amount of items:
	public static final String OAI_START_DATE = "oai.date.start";
	public static final String OAI_END_DATE = "oai.date.end";
	public static final String OAI_ITEM_LIMIT = "oai.item.length.limit";
	public static final String OAI_SETS = "oai.collection.sets";
	public static final String OAI_METADATA = "oai.collection.metadata";

	// filtering (collection) input
	public static final String FILTER_KEY = "filter.collection.key";
	public static final String FILTER_VALUE = "filter.collection.values";
	public static final String FILTER_STARTDATE = "filter.collection.startdate";
	public static final String FILTER_ENDDATE = "filter.collection.enddate";
	public static final String SHOULD_FILTER = "filter.collection.should-filter";

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

	public static final String STATS_START = "statistics.filter.start";
	public static final String STATS_END = "statistics.filter.end";

	public static final String STATS_EVENTLOG_TABLE = "statistics.evenlog.table";
	public static final String STATS_USER_TABLE = "statistics.user.table";

	private final Map<String, String> properties;

	public TaskConfig()
	{
		writeLock.lock();
		try
		{
			properties = new HashMap<String, String>();
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public TaskConfig(Map<String, String> map)
	{
		writeLock.lock();
		try
		{
			properties = map;
		}
		finally
		{
			writeLock.unlock();
		}
	}

	/**
	 * Merge the right task into the left. ie all existing item from right are written into left.
	 * @param left
	 * @param right
	 * @return
	 */
	public static TaskConfig merge(TaskConfig left, TaskConfig right)
	{
		TaskConfig result = (TaskConfig) left.clone();

		left.writeLock.lock();
		right.readLock.lock();
		try
		{
			result.properties.putAll(right.properties);
		}
		finally
		{
			left.writeLock.unlock();
			right.readLock.unlock();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */

	@Override
	/**
	 * returns a copy of the taskconfig object with a new set of properties
	 */
	public Object clone()
	{
		readLock.lock();
		try
		{
			Map<String, String> props = new HashMap<String, String>(properties.size());
			for (Map.Entry<String, String> entry : properties.entrySet())
			{
				props.put(entry.getKey(), entry.getValue());
			}
			return new TaskConfig(props);
		}
		finally
		{
			readLock.unlock();
		}

	}

	/**
	 * remove all runtime information of a task, like this a task can be reused again.
	 */
	public void cleanup()
	{
		writeLock.lock();
		try
		{
			properties.remove(TASK_ID);
			properties.remove(TASK_START);
			properties.remove(TASK_END);
			properties.remove(TASK_RESULT);
			properties.remove(TASK_RUNNING);
			properties.remove(TASK_CANCELLED);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	/**
	 * Helper method to get the ID of this task
	 * @return
	 */
	public long getId()
	{
		readLock.lock();
		try
		{
			return Long.parseLong(properties.get(TASK_ID));
		}
		finally
		{
			readLock.unlock();
		}
	}

	public void setStart()
	{
		setProperty(TASK_START, System.currentTimeMillis() / 1000);
	}

	public long getStart(long defaultValue)
	{
		return getLong(TASK_START, defaultValue);
	}

	public void setEnd()
	{
		setProperty(TASK_END, System.currentTimeMillis() / 1000);
	}

	public long getEnd(long defaultValue)
	{
		return getLong(TASK_END, defaultValue);
	}

	public long getNext(long defaultValue)
	{
		return getLong(NEXT, defaultValue);
	}

	private long getAsSeconds(long value, TimeUnit unit)
	{
		switch (unit)
		{
			case SECONDS :
				return value;
			case MILLISECONDS :
				return value / 1000;
			default :
				throw new NotImplementedException();
		}
	}

	/**
	 * stores the NEXT value in seconds
	 * @param value
	 * @param unit
	 */
	public void setNext(long value, TimeUnit unit)
	{
		setProperty(NEXT, getAsSeconds(value, unit));
	}

	public long getInterval(long defaultValue)
	{
		return getLong(INTERVAL, defaultValue);
	}

	public void setInterval(long value, TimeUnit unit)
	{
		setProperty(INTERVAL, getAsSeconds(value, unit));
	}

	/**
	 * Check if the executed tasks has failed
	 * @return
	 */
	public boolean isFailed()
	{
		readLock.lock();
		try
		{
			return TaskConfig.TASK_RESULT_FAILED.equals(properties.get(TaskConfig.TASK_RESULT));
		}
		finally
		{
			readLock.unlock();
		}
	}

	public boolean isExecuted()
	{
		readLock.lock();
		try
		{
			return properties.containsKey(TaskConfig.TASK_RESULT);
		}
		finally
		{
			readLock.unlock();
		}
	}

	/**
	 * set the task in failure state
	 */
	public void setFailed()
	{
		writeLock.lock();
		try
		{
			properties.put(TaskConfig.TASK_RESULT, TaskConfig.TASK_RESULT_FAILED);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public void setSucceed()
	{
		writeLock.lock();
		try
		{
			properties.put(TaskConfig.TASK_RESULT, TaskConfig.TASK_RESULT_OK);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public boolean isSucceeded()
	{
		readLock.lock();
		try
		{
			return TaskConfig.TASK_RESULT_OK.equals(properties.get(TaskConfig.TASK_RESULT));
		}
		finally
		{
			readLock.unlock();
		}
	}

	public void setCancelled()
	{
		writeLock.lock();
		try
		{
			properties.put(TaskConfig.TASK_CANCELLED, "true");
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public boolean isCancelled()
	{
		readLock.lock();
		try
		{
			return properties.containsKey(TASK_CANCELLED);
		}
		finally
		{
			readLock.unlock();
		}
	}

	public boolean isRunning()
	{
		readLock.lock();
		try
		{
			return properties.containsKey(TASK_RUNNING);
		}
		finally
		{
			readLock.unlock();
		}
	}

	public void setRunning()
	{
		writeLock.lock();
		try
		{
			properties.put(TASK_RUNNING, "true");
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public void stopRunning()
	{
		writeLock.lock();
		try
		{
			properties.remove(TASK_RUNNING);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	/**
	 * Checks if the tasks needs to be re-scheduled
	 * @return
	 */
	public boolean needSchedule()
	{
		return getLong(TaskConfig.NEXT, -1) != -1;
	}

	/**
	 * Helper method the set the ID of this task
	 * @param id
	 */
	public TaskConfig setId(long id)
	{
		writeLock.lock();
		try
		{
			properties.put(TASK_ID, String.format("%019d", id));
		}
		finally
		{
			writeLock.unlock();
		}
		return this;
	}

	/**
	 * Set a string property
	 * @param key
	 * @param value
	 * @return the old value if any
	 */
	public String setProperty(String key, String value)
	{
		writeLock.lock();
		try
		{
			return properties.put(key, value);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public boolean setProperty(String key, boolean value)
	{
		writeLock.lock();
		try
		{
			String result = properties.put(key, Boolean.toString(value));
			if (result != null)
			{
				return Boolean.parseBoolean(result);
			}
			return false;
		}
		finally
		{
			writeLock.unlock();
		}
	}

	/**
	 * Sets the <tt>key</tt> with <tt>value</tt> iff <tt>value</tt> != null
	 * @param key
	 * @param value
	 * @return
	 */
	public String setPropertyIgnoreNull(String key, String value)
	{
		if (value != null)
		{
			writeLock.lock();
			try
			{
				return properties.put(key, value);
			}
			finally
			{
				writeLock.unlock();
			}
		}
		return null;
	}

	/**
	 * Remove a key from the task
	 * @param key
	 * @return
	 */
	public String remove(String key)
	{
		writeLock.lock();
		try
		{
			return properties.remove(key);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	/**
	 * Set a long value. It throws an
	 * @param key
	 * @param value
	 * @return the previous value if any
	 * @throws NumberFormatException
	 */
	public Long setProperty(String key, long value)
	{
		writeLock.lock();
		try
		{
			LoggerFactory.getLogger(TaskConfig.class).debug("write: {} {}", key, value);
			String result = properties.put(key, String.format("%019d", value));
			if (result != null)
			{
				return Long.parseLong(result);
			}
		}
		finally
		{
			writeLock.unlock();
		}
		return null;

	}

	/**
	 * get a property string
	 * @param key
	 * @return the value for the key, or null if non found
	 */
	public String get(String key)
	{
		readLock.lock();
		try
		{
			return properties.get(key);
		}
		finally
		{
			readLock.unlock();
		}
	}

	public boolean getBoolean(String key, boolean defaultValue)
	{
		String result = get(key);
		if (result == null)
		{
			return defaultValue;
		}
		return Boolean.parseBoolean(result);
	}

	/**
	 * get a string property for the given key
	 * @param key
	 * @param defaultValue
	 * @return the value for the key, or defaultValue if non found
	 */
	public String get(String key, String defaultValue)
	{
		String result = get(key);
		if (result == null)
		{
			return defaultValue;
		}
		return result;
	}

	// /**
	// * get the integer property for the given key
	// * @param key
	// * @param defaultValue
	// * @return the integer value, or the defaultValue if non found
	// * @throws NumberFormatException
	// */
	// public int getInt(String key, int defaultValue)
	// {
	// String result = get(key);
	// if (result == null)
	// {
	// return defaultValue;
	// }
	// return Integer.parseInt(result);
	// }

	/**
	 * get the long property for the given key
	 * @param key
	 * @param defaultValue
	 * @return the long value, or defaultValue if non found
	 * @throws NumberFormatException
	 */
	public long getLong(String key, long defaultValue)
	{
		String result = get(key);
		if (result == null)
		{
			return defaultValue;
		}
		return Long.parseLong(result);
	}

	public Long getLong(String key, Long defaultValue)
	{
		String result = get(key);
		if (result == null)
		{
			return defaultValue;
		}
		return Long.parseLong(result);
	}

	public boolean contains(String key)
	{
		readLock.lock();
		try
		{
			return properties.containsKey(key);
		}
		finally
		{
			readLock.unlock();
		}
	}

	/**
	 * Returns an unmodifiable map of all the settings
	 * @return
	 */
	public Map<String, String> toMap()
	{
		Map<String, String> result = new TreeMap<String, String>();
		readLock.lock();
		try
		{
			result.putAll(properties);
		}
		finally
		{
			readLock.unlock();
		}
		return result;
	}

	@Override
	public String toString()
	{

		StringBuilder b = new StringBuilder("[");
		readLock.lock();
		try
		{
			for (Map.Entry<String, String> entry : properties.entrySet())
			{
				b.append("\n\t{'").append(entry.getKey()).append("':'").append(entry.getValue() + "'},");
			}
		}
		finally
		{
			readLock.unlock();
		}
		b.append("\n]");
		return b.toString();
	}
}
