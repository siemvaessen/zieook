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
package nl.gridline.zieook.data.hbase.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.RecommenderTable;
import nl.gridline.zieook.workflow.model.RecommenderConfig;
import nl.gridline.zieook.workflow.model.Recommenders;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * HBaseRecommenderConfigTest.java created 16 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class RecommenderTableTest
{

	private static final Logger logger = LoggerFactory.getLogger(RecommenderTableTest.class);

	private HBaseManager manager;
	private HBaseConfigurationTable table;

	@Before
	public void setUp() throws Exception
	{
		manager = HBaseManager.getInstance("mulisch");

		table = new HBaseConfigurationTable(manager);
		if (!table.tableExists(null))
		{
			assertTrue(table.create());
			logger.info("create table: {}", table.getTableName(null));
		}
		else
		{
			logger.info("table exists: {}", table.getTableName(null));
		}

	}

	@After
	public void tearDown() throws Exception
	{
		// clean up:
		// HBaseConfigurationTable table = new HBaseConfigurationTable(manager);
		// if (table.tableExists(null))
		// {
		// table.drop(null);
		// logger.info("table drop: {}", table.getTableName(null));
		// }
	}

	@Test
	public void getTest()
	{
		HBaseConfigurationTable configtable = new HBaseConfigurationTable(manager);
		RecommenderTable recTable = new RecommenderTable(configtable);

		// CollectionFilter filter = new CollectionFilter("simple-filter", "category", Arrays.asList(new
		// String[]{"Action",
		// "Adventure"}));
		//
		// recTable.putRecommender(new RecommenderConfig("gridline-movielens", "itembased", "movielens", filter,
		// HBaseConfigurationTable.getDefaultItemTemplate(), 60 * 60 * 24, -1));
		//
		// recTable.putRecommender(new RecommenderConfig("gridline-movielens", "userbased", "movielens", filter,
		// HBaseConfigurationTable.getDefaultUserTemplate(), 60 * 60 * 24, -1));
		//
		// recTable.putRecommender(new RecommenderConfig("gridline-edit", "textbased", "beeldengeluid", filter,
		// HBaseConfigurationTable.getDefaultTextTemplate(), 60 * 60 * 24, -1));

		System.out.println(new Recommenders(recTable.getRecommenders()).toJSON());

	}

	@Test
	@Ignore
	public void putgetTest()
	{
		RecommenderTable recommender = new RecommenderTable(table);

		RecommenderConfig data = Helper.getRecommenderConfig();
		recommender.putRecommender(data);

		RecommenderConfig newdata = recommender.getRecommender(data.getCp(), data.getRecommender());

		assertEquals(data, newdata);

		recommender.deleteRecommender(data.getCp(), data.getRecommender());

		RecommenderConfig deleted = recommender.getRecommender(data.getCp(), data.getRecommender());
		assertEquals(null, deleted.getRecommender());
		assertEquals(null, deleted.getCp());
	}

	@Test
	@Ignore
	public void listRecommenderTest()
	{
		RecommenderTable recommender = new RecommenderTable(table);

		RecommenderConfig data = Helper.getRecommenderConfig();
		recommender.putRecommender(data);

		List<String> list = recommender.getRecommenderList();

		boolean result = false;

		for (String l : list)
		{
			System.out.println(l);
			if (data.getRecommender().equals(l))
			{
				result = true;
				break;
			}
		}
		recommender.deleteRecommender(data.getCp(), data.getRecommender());

		assertTrue(result);
	}

	@Test
	@Ignore
	public void listRecommenderTest2()
	{
		RecommenderTable recommender = new RecommenderTable(table);

		RecommenderConfig data = Helper.getRecommenderConfig();
		recommender.putRecommender(data);

		List<String> list = recommender.getRecommenderList(data.getCp());

		boolean result = false;

		for (String l : list)
		{
			System.out.println(l);
			if (data.getRecommender().equals(l))
			{
				result = true;
				break;
			}
		}
		recommender.deleteRecommender(data.getCp(), data.getRecommender());

		assertTrue(result);
	}
}
