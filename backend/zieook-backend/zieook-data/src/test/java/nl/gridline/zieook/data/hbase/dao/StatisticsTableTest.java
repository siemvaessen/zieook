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
import nl.gridline.zieook.api.CollectionStatistics;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.model.HBaseStatisticsTable;
import nl.gridline.zieook.model.Popularities;
import nl.gridline.zieook.model.Popularity;
import nl.gridline.zieook.workflow.api.StatisticsScheduler;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * StatisticsTableTest.java created 23 feb. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class StatisticsTableTest
{

	private static final Logger LOG = LoggerFactory.getLogger(StatisticsTableTest.class);

	private static final String CP = "AmsterdamMuseum3";
	private static final String COLLECTION = "am2";
	private static final String ZOOKEEPER = "hm.gridline.nl";

	private static final String RECOMMENDER = "textbased";

	// EventLogTable[78]- filtering: cp/recommender/user <start,end> == AmsterdamMuseum3/textbased/2 <null,1325589883148>

	private static HBaseManager manager;

	private static StatisticsTable statistics;

	/**
	 * @throws ZooKeeperConnectionException
	 * @throws MasterNotRunningException
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws MasterNotRunningException, ZooKeeperConnectionException
	{
		manager = HBaseManager.getInstance(ZOOKEEPER);

		HBaseStatisticsTable table = new HBaseStatisticsTable(manager);
		assertTrue(table.tableExists(CP));
		statistics = new StatisticsTable(table);

		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
	}

	@Test
	@Ignore
	public void executeStatistics()
	{
		StatisticsScheduler scheduler = ProxyFactory.create(StatisticsScheduler.class, "http://zieook.gridline.nl:20200");

		LOG.info("\n\nresponse: {}", scheduler.runStatistics(CP, "now").getStatus());
	}

	@Test
	@Ignore
	public void popularity()
	{

		CollectionStatistics collectionStats = ProxyFactory.create(CollectionStatistics.class,
				"http://zieook.gridline.nl:20100");
		// >day,86400;week,604800;3months,7884000;6months,15768000;year,31536000

		// Popularity result = collectionStats.getMostPopular(CP, COLLECTION, "6months");
		// if (result != null)
		// {
		// LOG.info("{}", result.toJSON());
		// }
		// else
		// {
		// LOG.info("null");
		// }

		Popularities result = collectionStats.getPopularityList(CP, COLLECTION, "6months", null, 1000);
		LOG.info("popularity-size: {}", result.getPopularity().size());
		int total = 0;
		for (Popularity p : result.getPopularity())
		{
			total += p.getPopularity();
		}
		LOG.info("total: {}", total);
		//
		// boolean found = false;
		// for (int i = 0; i < 87000; i++)
		// {
		// Popularity result = statistics.getPopularity(CP, COLLECTION, "week", i);
		// if (result != null)
		// {
		// found = true;
		// LOG.info("found!! {}", result);
		// }
		// else
		// {
		// LOG.info("{} - empty", i);
		// }
		// }
		// if (found)
		// {
		// LOG.info("some items found...");
		// }
	}
}
