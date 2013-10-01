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

import java.io.IOException;

import nl.gridline.zieook.workflow.model.QueueItem;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * DistributedQueueTest.java created 20 apr. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class DistributedQueueTest implements Watcher
{

	private static final Logger LOG = LoggerFactory.getLogger(DistributedQueueTest.class);

	private ZooKeeper zk;
	private DistributedQueue q;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		zk = new ZooKeeper("mulisch", 1000, this);
		q = new DistributedQueue(zk, "/test/zieook/queue", null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		q.cleanup("/test");
		zk.close();
	}

	@Test
	public void distributedQueueTest1() throws IOException, KeeperException, InterruptedException
	{
		long i = 0L;
		QueueItem item = new QueueItem(System.currentTimeMillis() + 1000, i++);
		q.offer(item);
		QueueItem itemnew = q.take();
		assertEquals(item, itemnew);

	}

	@Test
	public void distributedQueueTest2() throws IOException, KeeperException, InterruptedException
	{
		QueueItem item = q.take(1000);
		assertEquals(null, item);
	}

	@Test
	public void distributedQueueTest3() throws IOException, KeeperException, InterruptedException
	{
		LOG.debug("putting items in the past");
		long i = 0L;

		QueueItem item = new QueueItem(System.currentTimeMillis() - 10000, i++);
		// offer items in the past:
		q.offer(item);

		LOG.debug("taking item - if any");
		long time = System.currentTimeMillis();
		QueueItem newitem = q.take(1000);
		long duration = (System.currentTimeMillis() - time);
		LOG.info("ITEM: {} - took {}ms", newitem, duration);
		assertEquals(0, item.getId()); // should not block, should return 'directly'
		assertTrue(duration > 0 && duration < 100);
	}

	@Test
	public void distributedQueueTest4() throws IOException, KeeperException, InterruptedException
	{
		long i = 0L;
		LOG.debug("putting items in the future");

		QueueItem item = new QueueItem(System.currentTimeMillis() + 1500, i++);
		// offer items in the past:
		q.offer(item);

		LOG.debug("taking item - if any");
		long time = System.currentTimeMillis();
		QueueItem newitem = q.take();
		long duration = (System.currentTimeMillis() - time);
		LOG.info("item: {}  - took {}ms", newitem, duration);
		assertEquals(newitem.getId(), item.getId()); // should not block & but return 0
		assertTrue(duration > 1400 && duration < 1600);
	}

	@Override
	public void process(WatchedEvent event)
	{
		LOG.debug("watched: " + event);

	}
}
