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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.model.UserView;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * EventLogTableTest.java created 23 feb. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class EventLogTableTest
{
	private static final Logger LOG = LoggerFactory.getLogger(EventLogTableTest.class);

	private static final String CP = "AmsterdamMuseum3";
	private static final String ZOOKEEPER = "hm.gridline.nl";
	private static final String RECOMMENDER = "textbased";

	// EventLogTable[78]- filtering: cp/recommender/user <start,end> == AmsterdamMuseum3/textbased/2 <null,1325589883148>

	private static HBaseManager manager;

	private static EventLogTable eventlog;

	/**
	 * @throws IOException
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws IOException
	{
		manager = HBaseManager.getInstance(ZOOKEEPER);

		HBaseEventLogTable table = new HBaseEventLogTable(manager);

		assertTrue(table.tableExists(CP));

		eventlog = new EventLogTable(table);

	}

	// @Test
	// public void updateViews()
	// {
	// // assertTrue(table.update(CP));
	// eventlog.updateViews(CP);
	// }

	@Test
	@Ignore
	public void testGetViews()
	{
		Long startdate = 1329993842372L;// 1324380799274L;
		Long enddate = 1330351600701L; // 1330353225277L;
		// String cp, String recommender, long user, Long start, Long end, int size)
		List<UserView> result = eventlog.getViews(CP, RECOMMENDER, 2, startdate, enddate, 100, false);

		LOG.info("input: <{},{}>", new Date(startdate), new Date(enddate));

		assertTrue(result.size() > 0);

		Long end = result.get(0).getDate();
		Long start = result.get(result.size() - 1).getDate();

		assertTrue(end <= enddate);

		// print all:
		for (UserView i : result)
		{
			LOG.info("{} - {}", i.getItem(), i.getDate());
		}

		LOG.info("listing data from {} to {}", new Date(end), new Date(start));
	}

}
