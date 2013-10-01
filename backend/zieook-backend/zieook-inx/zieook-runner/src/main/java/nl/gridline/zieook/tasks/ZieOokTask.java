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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import nl.gridline.zieook.mapreduce.TaskConfig;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZieOok task: The basic task callable with convenience methods to for progress, messages and configuration
 * <p />
 * Project zieook-runner<br />
 * RecommendationTask.java created 7 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public abstract class ZieOokTask implements Watcher
{
	private static final Logger LOG = LoggerFactory.getLogger(ZieOokTask.class);

	public enum Level
	{
		DEBUG, INFO, WARN, ERROR, FATAL
	};

	private Job currentJob;

	// paths used within zookeeper:
	private static final String ZIEOOK_TASK = "/gridline/zieook/running";
	private static final String PROGRESS = "progress-";
	private static final String MESSAGE = "message-";
	private static final String LEVEL = "level-";
	private static final String CANCEL = "cancel-";
	private static final String TASK_PROGRESS = ZIEOOK_TASK + "/" + PROGRESS;
	private static final String TASK_LEVEL = ZIEOOK_TASK + "/" + LEVEL;
	private static final String TASK_MESSAGE = ZIEOOK_TASK + "/" + MESSAGE;
	private static final String TASK_CANCEL = ZIEOOK_TASK + "/" + CANCEL;

	// the task configuration:
	protected TaskConfig configuration;

	// a zookeeper connection:
	protected ZooKeeper zk;

	/**
	 * Assign the configuration to the task
	 * @param configuration
	 */
	public ZieOokTask setConfig(TaskConfig configuration)
	{
		this.configuration = configuration;
		return this;
	}

	/**
	 * Set ZooKeeper on this task - if set, it can report progress and messages to zookeeper
	 * @param zookeeper
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public ZieOokTask setZooKeeper(ZooKeeper zookeeper) throws KeeperException, InterruptedException
	{
		zk = zookeeper;
		// init, the zookeeper path, if it does not exist:
		if (zk.exists("/gridline", null) == null)
		{
			zk.create("/gridline", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		if (zk.exists("/gridline/zieook", null) == null)
		{
			zk.create("/gridline/zieook", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		if (zk.exists("/gridline/zieook/running", null) == null)
		{
			zk.create("/gridline/zieook/running", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		return this;
	}

	/**
	 * Sets the given message for this task
	 * @param message
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void setMessage(Level type, String message) throws InterruptedException
	{
		// set message in local configuration:
		configuration.setProperty(TaskConfig.TASK_MESSAGE, message);

		// log message to logger:
		switch (type)
		{
			case DEBUG :
				LOG.debug(message);
				break;
			case WARN :
				LOG.warn(message);
				break;
			case ERROR :
			case FATAL :
				LOG.error(message);
				break;
			case INFO :
			default :
				LOG.info(message);
		}

		// write message to zookeeper, globaly available:
		if (zk != null)
		{
			try
			{
				if (zk.exists(TASK_LEVEL + configuration.getId(), null) == null)
				{
					byte[] data = Bytes.toBytes(type.toString());
					zk.create(TASK_LEVEL + configuration.getId(), data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				}
				else
				{
					byte[] data = Bytes.toBytes(type.toString());
					zk.setData(TASK_LEVEL + configuration.getId(), data, -1);
				}

				if (zk.exists(TASK_MESSAGE + configuration.getId(), null) == null)
				{
					byte[] data = Bytes.toBytes(message);
					zk.create(TASK_MESSAGE + configuration.getId(), data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				}
				else
				{
					byte[] data = Bytes.toBytes(message);
					zk.setData(TASK_MESSAGE + configuration.getId(), data, -1);
				}
			}
			catch (KeeperException e)
			{
				LOG.error("failed to write message to zookeeper", e);
			}
		}
	}

	/**
	 * External cancel request
	 * @param zk
	 * @param id
	 */
	public static void cancel(ZooKeeper zk, long id)
	{
		try
		{
			LOG.debug("Write a cancel request to ZooKeeper for <{}>", id);
			zk.create(TASK_CANCEL + id, "cancel".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		}
		catch (KeeperException e)
		{
			LOG.error("failed to set cancellation request", e);
		}
		catch (InterruptedException e)
		{
			LOG.error("failed to set cancellation request", e);
		}
	}

	// THIS does not work...
	public boolean isCancelled()
	{
		if (zk != null)
		{
			try
			{
				// check for cancel and set a watch:
				boolean cancel = zk.exists(TASK_CANCEL + configuration.getId(), null) != null;
				if (cancel)
				{
					// received
					LOG.debug("Received a cancel request through ZooKeeper <{}> cancelled", configuration.getId());
					// cancel now!
					configuration.setCancelled();
					zk.delete(TASK_CANCEL + configuration.getId(), -1);
				}
			}

			catch (KeeperException e)
			{
				LOG.error("failed to get cancellation state for " + configuration.getId(), e);
			}
			catch (InterruptedException e)
			{
				LOG.error("failed to get cancellation state for " + configuration.getId(), e);
			}
		}
		return configuration.isCancelled();
	}

	@Override
	public void process(WatchedEvent event)
	{

		LOG.debug("got watched event: {}", event);
	}

	/**
	 * Get messages request
	 * Retrieves all messages from currently running tasks
	 * @param zk zookeeper connection
	 * @return a map <id,message>
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public static Map<Long, String> getMessages(ZooKeeper zk) throws KeeperException, InterruptedException
	{
		Map<Long, String> result = new HashMap<Long, String>();

		List<String> children;
		try
		{
			children = zk.getChildren(ZIEOOK_TASK, null);
		}
		catch (KeeperException.NoNodeException e)
		{
			LOG.debug("No active tasks");
			children = new ArrayList<String>(0);
		}

		for (String child : children)
		{
			if (child.startsWith(MESSAGE))
			{
				try
				{
					byte[] data = zk.getData(ZIEOOK_TASK + "/" + child, false, null);
					// extract id & data
					String[] el = child.split("-");
					if (el.length == 2)
					{
						result.put(Long.parseLong(el[1]), Bytes.toString(data));
					}
					else
					{
						LOG.error("failed to recognize path: <{}/{}> skipping", ZIEOOK_TASK, child);
					}
				}
				catch (InterruptedException e)
				{
					// break..
					LOG.error("interrupted", e);
					return result;
				}
				catch (Exception e)
				{
					LOG.error("failed to get node (" + child + "), skipping", e);
				}
			}
		}
		return result;
	}

	/**
	 * Retrieves all progress states for currently running tasks
	 * @param zk zookeeper connection
	 * @return a map <id,progress> progress could be a progress/max
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public static Map<Long, Long> getProgress(ZooKeeper zk) throws KeeperException, InterruptedException
	{
		Map<Long, Long> result = new HashMap<Long, Long>();

		List<String> children;

		try
		{
			children = zk.getChildren(ZIEOOK_TASK, null);
		}
		catch (KeeperException.NoNodeException e)
		{
			// LOG.debug("No active tasks");
			children = new ArrayList<String>(0);
		}

		for (String child : children)
		{
			if (child.startsWith(PROGRESS))
			{
				try
				{
					byte[] data = zk.getData(ZIEOOK_TASK + "/" + child, false, null);
					String[] el = child.split("-");
					if (el.length == 2)
					{
						result.put(Long.parseLong(el[1]), Bytes.toLong(data));
					}
					else
					{
						LOG.error("failed to recognize path: <{}/{}> skipping", ZIEOOK_TASK, child);
					}
				}
				catch (Exception e)
				{
					LOG.error("failed to get node (" + child + "), skipping", e);
				}
			}
		}
		return result;
	}

	/**
	 * Increases the progress of this task by one
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void setProgress() throws KeeperException, InterruptedException
	{
		if (zk != null)
		{
			final String path = TASK_PROGRESS + configuration.getId();

			if (zk.exists(path, null) == null)
			{
				byte[] data = Bytes.toBytes(1L);
				LOG.info("created task progress: <{}>", path);
				zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			}
			else
			{
				byte[] data = zk.getData(path, false, null);
				zk.setData(path, Bytes.toBytes(Bytes.toLong(data) + 1L), -1);
			}
		}
	}

	public static void taskDone(ZooKeeper zk, long id) throws InterruptedException
	{
		// delete running task data
		final String path = TASK_PROGRESS + id;
		try
		{
			LOG.info("deleting <{}>", path);
			zk.delete(path, -1);
		}
		catch (KeeperException.NoNodeException e)
		{
			LOG.debug("no data on <{}> for task <{}>", TASK_PROGRESS, id);
		}
		catch (KeeperException e)
		{
			LOG.error("possibly failed to remove progress state for " + id + " - this is not fatal but inconvenient", e);
		}

		try
		{
			zk.delete(TASK_MESSAGE + id, -1);
		}
		catch (KeeperException.NoNodeException e)
		{
			// nothing, no message:
			LOG.debug("no data on <{}> for task <{}>", TASK_MESSAGE, id);
		}
		catch (KeeperException e)
		{
			LOG.error("possibly failed to remove progress state for " + id + " - this is not fatal but inconvenient", e);
		}

	}

	/**
	 * Task successful, also schedule next run - based on the task_start
	 */
	public void setSucceed()
	{
		configuration.setSucceed();
		configuration.setEnd();
		long start = configuration.getStart(-1);
		long interval = configuration.getInterval(-1);
		if (interval != -1 && start != -1)
		{
			configuration.setNext(interval + start, TimeUnit.SECONDS);
		}
		else
		{
			configuration.setNext(-1, TimeUnit.SECONDS);
		}
	}

	/**
	 * Task failed, it will not schedule the next run
	 */
	public void setFailed()
	{
		configuration.setEnd();
		configuration.setFailed();
		configuration.remove(TaskConfig.NEXT);
	}

	/**
	 * Returns this tasks configuration
	 * @return
	 */
	public TaskConfig getConfig()
	{
		return configuration;
	}

	/**
	 * get the task id
	 * @return
	 */
	public long getId()
	{
		return configuration.getId();
	}

	public synchronized Job setCurrentJob(Job job)
	{
		currentJob = job;
		return job;
	}

	public synchronized Job getCurrentJob()
	{
		return currentJob;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	public abstract void call() throws Exception;

}
