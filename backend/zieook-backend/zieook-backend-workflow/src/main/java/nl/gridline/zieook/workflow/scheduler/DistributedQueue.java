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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import nl.gridline.zieook.workflow.model.QueueItem;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of a distributed queue using ZooKeeper.
 * <p />
 * Project zieook-backend-workflow<br />
 * DistributedQueue.java created 21 apr. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class DistributedQueue
{

	private static final Logger LOG = LoggerFactory.getLogger(DistributedQueue.class);

	private final String dir;

	private final ZooKeeper zookeeper;
	private List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;

	public static final String PREFIX = "qn-";

	public DistributedQueue(ZooKeeper zookeeper, String dir, List<ACL> acl) throws KeeperException, InterruptedException
	{
		this.dir = dir;

		if (acl != null)
		{
			this.acl = acl;
		}
		this.zookeeper = zookeeper;
		initpath();
	}

	private final void initpath() throws KeeperException, InterruptedException
	{
		String[] elements = dir.split("/");
		String path = "";

		for (String el : elements)
		{
			if (!el.isEmpty())
			{
				path = path + "/" + el;
				LOG.debug("checking queue pathelement: <{}> from <{}>", el, dir);
				if (zookeeper.exists(path, null) == null)
				{
					zookeeper.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					LOG.debug("{} created", path);
				}
				else
				{
					LOG.debug("{} exists", path);
				}
			}
		}
	}

	public void cleanup(String path) throws KeeperException, InterruptedException
	{
		if (zookeeper.exists(path, null) != null)
		{
			List<String> result = zookeeper.getChildren(path, null);
			if (result != null && result.size() > 0)
			{
				LOG.debug("deleting children for <{}>", path);
				for (String i : result)
				{
					cleanup(path + "/" + i);
				}
			}

			LOG.debug("delete: <{}>", path);
			zookeeper.delete(path, -1);

		}

	}

	/**
	 * Returns a Map of the children, ordered by id.
	 * @param watcher optional watcher on getChildren() operation.
	 * @return map from id to child name for all children
	 */
	private TreeMap<String, String> orderedChildren(Watcher watcher) throws KeeperException, InterruptedException
	{
		TreeMap<String, String> orderedChildren = new TreeMap<String, String>();

		List<String> childNames = null;
		try
		{
			childNames = zookeeper.getChildren(dir, watcher);
		}
		catch (KeeperException.NoNodeException e)
		{
			throw e;
		}
		catch (KeeperException.SessionExpiredException e1)
		{
			LOG.error("Session expired; {}", e1.getMessage());
			Thread.sleep(4000); // sleep a while, otherwise this method goes crazy
			return orderedChildren;
		}

		for (String childName : childNames)
		{
			try
			{
				// Check format
				if (!childName.regionMatches(0, PREFIX, 0, PREFIX.length()))
				{
					LOG.warn("Found child node with improper name: " + childName);
					continue;
				}
				String suffix = childName.substring(PREFIX.length());
				// Long childId = new Long(suffix);
				orderedChildren.put(suffix, childName);
			}
			catch (NumberFormatException e)
			{
				LOG.warn("Found child node with improper format : " + childName + " " + e, e);
			}
		}

		return orderedChildren;
	}

	public List<QueueItem> getScheduled() throws KeeperException, InterruptedException
	{
		TreeMap<String, String> items = orderedChildren(null);
		List<QueueItem> result = new ArrayList<QueueItem>(items.size());

		for (Map.Entry<String, String> item : items.entrySet())
		{
			byte[] data = zookeeper.getData(dir + "/" + item.getValue(), false, null);
			try
			{
				result.add(new QueueItem(item.getKey(), data));
			}
			catch (IOException e)
			{
				LOG.warn("invalid item: '" + item.getValue() + "' was ignored");
			}
		}

		return result;
	}

	// /**
	// * Find the smallest child node.
	// * @return The name of the smallest child node.
	// */
	// private String smallestChildName() throws KeeperException, InterruptedException
	// {
	// long minId = Long.MAX_VALUE;
	// String minName = "";
	//
	// List<String> childNames = null;
	//
	// try
	// {
	// childNames = zookeeper.getChildren(dir, false);
	// }
	// catch (KeeperException.NoNodeException e)
	// {
	// LOG.warn("Caught: " + e, e);
	// return null;
	// }
	//
	// for (String childName : childNames)
	// {
	// try
	// {
	// // Check format
	// if (!childName.regionMatches(0, PREFIX, 0, PREFIX.length()))
	// {
	// LOG.warn("Found child node with improper name: " + childName);
	// continue;
	// }
	// String suffix = childName.substring(PREFIX.length());
	// long childId = Long.parseLong(suffix);
	// if (childId < minId)
	// {
	// minId = childId;
	// minName = childName;
	// }
	// }
	// catch (NumberFormatException e)
	// {
	// LOG.warn("Found child node with improper format : " + childName + " " + e, e);
	// }
	// }
	//
	// if (minId < Long.MAX_VALUE)
	// {
	// return minName;
	// }
	// else
	// {
	// return null;
	// }
	// }

	/**
	 * Return the head of the queue without modifying the queue.
	 * @return the data at the head of the queue.
	 * @throws NoSuchElementException
	 * @throws KeeperException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public QueueItem element() throws NoSuchElementException, KeeperException, InterruptedException, IOException
	{
		TreeMap<String, String> orderedChildren;

		// element, take, and remove follow the same pattern.
		// We want to return the child node with the smallest sequence number.
		// Since other clients are remove()ing and take()ing nodes concurrently,
		// the child with the smallest sequence number in orderedChildren might be gone by the time we check.
		// We don't call getChildren again until we have tried the rest of the nodes in sequence order.
		while (true)
		{
			try
			{
				orderedChildren = orderedChildren(null);
			}
			catch (KeeperException.NoNodeException e)
			{
				throw new NoSuchElementException();
			}
			if (orderedChildren.size() == 0)
			{
				throw new NoSuchElementException();
			}

			for (String headNode : orderedChildren.values())
			{
				if (headNode != null)
				{
					try
					{
						byte[] data = zookeeper.getData(dir + "/" + headNode, false, null);
						return new QueueItem(headNode, data);
					}
					catch (KeeperException.NoNodeException e)
					{
						// Another client removed the node first, try next
					}
				}
			}

		}
	}

	/**
	 * Attempts to remove the head of the queue and return it.
	 * @return The former head of the queue
	 * @throws NoSuchElementException
	 * @throws KeeperException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public QueueItem remove() throws NoSuchElementException, KeeperException, InterruptedException, IOException
	{
		TreeMap<String, String> orderedChildren;
		// Same as for element. Should refactor this.
		while (true)
		{
			try
			{
				orderedChildren = orderedChildren(null);
			}
			catch (KeeperException.NoNodeException e)
			{
				throw new NoSuchElementException();
			}
			if (orderedChildren.size() == 0)
			{
				throw new NoSuchElementException();
			}

			for (String headNode : orderedChildren.values())
			{
				String path = dir + "/" + headNode;
				try
				{
					byte[] data = zookeeper.getData(path, false, null);
					QueueItem item = new QueueItem(headNode, data);
					if (item.isExpired())
					{
						zookeeper.delete(path, -1);
						return item;
					}
				}
				catch (KeeperException.NoNodeException e)
				{
					// Another client deleted the node first.
				}
			}

		}
	}

	private static class LatchChildWatcher implements Watcher
	{
		private final CountDownLatch latch;

		public LatchChildWatcher()
		{
			latch = new CountDownLatch(1);
		}

		@Override
		public void process(WatchedEvent event)
		{
			latch.countDown();
		}

	}

	/**
	 * Removes the head of the queue and returns is, block until is succeeds
	 * @return a queued item
	 * @throws KeeperException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public QueueItem take() throws KeeperException, InterruptedException, IOException
	{
		return take(-1);
	}

	/**
	 * Removes the head of the queue and returns it, an waits timeout seconds
	 * @param timeout a timeout in ms
	 * @return The former head of the queue
	 * @throws NoSuchElementException
	 * @throws KeeperException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public QueueItem take(long timeout) throws KeeperException, InterruptedException, IOException
	{
		TreeMap<String, String> orderedChildren;
		// Same as for element. Should refactor this.
		long start = System.currentTimeMillis();
		long time = 0;
		if (timeout == -1)
		{
			time = Long.MIN_VALUE;
		}

		while (time < timeout)
		{
			LatchChildWatcher childWatcher = null;

			if (timeout < 0)
			{
				childWatcher = new LatchChildWatcher();
			}
			else
			{
				time = System.currentTimeMillis() - start;
			}

			try
			{
				orderedChildren = orderedChildren(childWatcher);
			}
			catch (KeeperException.NoNodeException e)
			{
				zookeeper.create(dir, new byte[0], acl, CreateMode.PERSISTENT);
				continue;
			}

			if (orderedChildren.size() == 0)
			{
				continue;
			}

			for (String headNode : orderedChildren.values())
			{
				String path = dir + "/" + headNode;
				try
				{
					byte[] data = zookeeper.getData(path, false, null);
					QueueItem item = new QueueItem(headNode, data);
					if (item.isExpired())
					{
						zookeeper.delete(path, -1);
						if (timeout > 0)
						{
							LOG.debug("waited {}ms for ", time, item);
						}
						return item;
					}
				}
				catch (KeeperException.NoNodeException e)
				{
					// Another client deleted the node first.
					LOG.debug("NoNode: node taken by other client");
				}
			}
		}
		return null;
	}

	/**
	 * Inserts data into queue.
	 * @param item
	 * @return true if data was successfully added
	 */
	public boolean offer(QueueItem item) throws KeeperException, InterruptedException
	{
		for (;;)
		{
			try
			{
				zookeeper.create(dir + "/" + PREFIX + item.getTimeoutStr() + "-", item.getIdArray(), acl,
						CreateMode.PERSISTENT_SEQUENTIAL);
				return true;
			}
			catch (KeeperException.NoNodeException e)
			{
				zookeeper.create(dir, new byte[0], acl, CreateMode.PERSISTENT);
			}
		}

	}

	/**
	 * Returns the data at the first element of the queue, or null if the queue is empty.
	 * @return data at the first element of the queue, or null.
	 * @throws KeeperException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public QueueItem peek() throws KeeperException, InterruptedException, IOException
	{
		try
		{
			return element();
		}
		catch (NoSuchElementException e)
		{
			return null;
		}
	}

	/**
	 * Attempts to remove the head of the queue and return it. Returns null if the queue is empty.
	 * @return Head of the queue or null.
	 * @throws KeeperException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public QueueItem poll() throws KeeperException, InterruptedException, IOException
	{
		try
		{
			return remove();
		}
		catch (NoSuchElementException e)
		{
			return null;
		}
	}

}
