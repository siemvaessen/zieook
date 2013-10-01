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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.gridline.zieook.exceptions.DistrQueueException;
import nl.gridline.zieook.tasks.ZieOokTask;
import nl.gridline.zieook.workflow.model.QueueItem;
import nl.gridline.zieook.workflow.model.ZooKeeperElement;
import nl.gridline.zieook.workflow.model.ZooKeeperState;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * ZkQueue.java created 15 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class ZieOokScheduler implements Watcher
{
	public static final String ZIEOOK_QUEUE = "/gridline/zieook/queue";

	private static final Logger LOG = LoggerFactory.getLogger(ZieOokScheduler.class);

	private ZooKeeper zk;
	private DistributedQueue dq;

	/**
	 * Creates a ZieOok distributed scheduler
	 * Tasks put on the queue will only be executed once, independently of the amount of instances running
	 * @param connectString comma separated host:port pairs, each corresponding to a zk server. e.g.
	 *           "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002" If the optional chroot suffix is used the example would
	 *           look like: "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002/app/a" where the client would be rooted at
	 *           "/app/a" and all paths would be relative to this root - ie getting/setting/etc... "/foo/bar" would
	 *           result in operations being run on "/app/a/foo/bar" (from the server perspective).
	 * @param sessionTimeout session timeout in milliseconds
	 * @throws InterruptedException
	 * @throws KeeperException
	 * @throws DistrQueueException
	 * @throws Exception
	 */
	public ZieOokScheduler(String connectString, int retries, int sessionTimeout) throws KeeperException,
			InterruptedException, DistrQueueException
	{
		try
		{
			LOG.info("creating zookeeper connection to <" + connectString + ">");
			// create zookeeper instance:
			zk = new ZooKeeper(connectString, sessionTimeout, this);

			// create distributed queue instance:
			dq = new DistributedQueue(zk, ZIEOOK_QUEUE, null);

			LOG.info("zookeeper instance created");

		}
		catch (IOException e)
		{
			throw new DistrQueueException("failed to itialize zookeeper queue", e);
		}
	}

	public ZooKeeper getZooKeeper()
	{
		return zk;
	}

	private ZooKeeperElement getTree(String path)
	{
		String element = path.substring(path.lastIndexOf('/'));
		ZooKeeperElement result = new ZooKeeperElement(element);
		try
		{
			List<String> children = zk.getChildren(path, false);
			if (children != null && children.size() > 0)
			{
				Collections.sort(children);
				for (String child : children)
				{
					LOG.debug("getting path: <{}/{}>", path, child);
					result.getChildren().add(getTree(path + "/" + child));
				}
			}
			else
			{
				Stat nodeStat = new Stat();
				byte[] data = zk.getData(path, false, nodeStat);
				if (data != null)
				{
					ByteBuffer buf = ByteBuffer.wrap(data);
					result.setValue(buf.getLong());
				}
			}
		}
		catch (KeeperException e)
		{
			LOG.error("failed to get tree", e);
		}
		catch (InterruptedException e)
		{
			LOG.error("failed to get tree", e);
		}
		return result;
	}

	public ZooKeeperState getState()
	{
		ZooKeeperState result = new ZooKeeperState();

		result.setState(zk.getState().toString());
		result.setTree(getTree("/gridline"));
		return result;
	}

	/**
	 * shutdown the scheduler
	 */
	public void shutdown()
	{
		LOG.info("closing down zookeeper connection");
		try
		{
			zk.close();
		}
		catch (InterruptedException e)
		{
			LOG.info("zookeeper shutdown interrupted", e);
		}
	}

	/**
	 * Offers an id to be released at a certain time in the future, or immediately if the release time < current time
	 * @param release time in milliseconds from epoch
	 * @param id an id
	 * @return true if succefully offered
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public boolean offer(long release, long id) throws KeeperException, InterruptedException
	{
		if (id != -1)
		{
			return dq.offer(new QueueItem(release, id));
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns the next task-id when available of the task that needs to be executed,it will wait forever
	 * @return task id or -1 if failed
	 * @throws InterruptedException
	 */
	public long poll() throws InterruptedException
	{
		return poll(-1);
	}

	/**
	 * Returns the next task-id when available of the task that needs to be executed, it will wait for <tt>timeout</tt>
	 * milliseconds
	 * @param timeout milliseconds to wait.
	 * @return task id or -1 if failed
	 * @throws InterruptedException
	 */
	public long poll(long timeout) throws InterruptedException
	{
		long time = System.currentTimeMillis();
		try
		{
			QueueItem item = dq.take(timeout);
			time = System.currentTimeMillis() - time;
			if (item != null)
			{
				LOG.debug("found: {} in {}ms", item.getId(), time);
				return item.getId();
			}
		}
		catch (KeeperException e)
		{
			LOG.error("failed to poll item", e);
		}
		catch (IOException e)
		{
			LOG.error("failed to poll item", e);
		}
		return -1;
	}

	public List<Long> getScheduled(long start, long end, int size) throws InterruptedException, KeeperException
	{
		List<Long> result = new ArrayList<Long>(size);
		List<QueueItem> scheduled = dq.getScheduled();
		for (QueueItem i : scheduled)
		{
			// handle timeout:
			if (i.getTimeout() > start && i.getTimeout() < end)
			{
				result.add(i.getId());
				// handle max-size:
				if (size == result.size())
				{
					break;
				}
			}
		}
		return result;
	}

	@Override
	public void process(WatchedEvent event)
	{
		// nothing to do...
		LOG.info(event.toString());
	}

	/**
	 * Retrieve the list of active task progresses. In case of an error an empty map is returned.
	 * @return
	 */
	public Map<Long, Long> getActiveTasks()
	{
		try
		{
			return ZieOokTask.getProgress(zk);
		}
		catch (KeeperException e)
		{
			LOG.error("failed to retrieve progress for any task", e);
		}
		catch (InterruptedException e)
		{
			LOG.error("failed to retrieve progress for any task - maybe we're shutting down", e);
		}

		return new HashMap<Long, Long>(0);
	}

	/**
	 * Retrieve the list of active task messages
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public Map<Long, String> getMessage()
	{
		try
		{
			return ZieOokTask.getMessages(zk);
		}
		catch (KeeperException e)
		{
			LOG.error("failed to retrieve messages for any task", e);
		}
		catch (InterruptedException e)
		{
			LOG.error("failed to retrieve messages for any task - maybe we're shutting down", e);
		}

		return new HashMap<Long, String>(0);
	}

}
