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
package nl.gridline.zieook.data.hbase;

import static org.junit.Assert.assertTrue;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseRecommendationTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * HBaseTableTest.java created 24 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class HBaseTableTest
{
	private HBaseManager manager;

	private static final String users = "gridline_users";
	private static final String userstats = "gridline_userstats";
	private static final String recommendations = "gridline_recommendations";
	private static final String collection = "gridline_collection";

	@Before
	public void setUp() throws Exception
	{
		manager = HBaseManager.getInstance("mulisch");
	}

	@Test
	public void userStatsTest()
	{
		HBaseEventLogTable table = new HBaseEventLogTable(manager);
		if (table.tableExists(userstats))
		{
			assertTrue(table.drop(userstats));
		}
		assertTrue(table.create(userstats));
		assertTrue(table.drop(userstats));
	}

	@Test
	public void userCreateTest()
	{
		HBaseUserTable table = new HBaseUserTable(manager);
		if (table.tableExists(users))
		{
			assertTrue(table.drop(users));
		}
		assertTrue(table.create(users));
		assertTrue(table.drop(users));
	}

	@Test
	public void recommenationTable()
	{
		HBaseRecommendationTable table = new HBaseRecommendationTable(manager);
		if (table.tableExists(recommendations))
		{
			assertTrue(table.drop(recommendations));
		}
		assertTrue(table.create(recommendations));
		assertTrue(table.drop(recommendations));
	}

	@Test
	@Ignore
	public void configurationTable()
	{
		HBaseConfigurationTable table = new HBaseConfigurationTable(manager);

		if (table.tableExists())
		{
			assertTrue(table.drop());
		}
		assertTrue(table.create());
		assertTrue(table.drop());
	}

	public void collectionTable()
	{
		HBaseCollectionTable table = new HBaseCollectionTable(manager);
		if (table.tableExists(collection))
		{
			assertTrue(table.drop(collection));
		}
		assertTrue(table.create(collection));
		assertTrue(table.drop(collection));
	}
}
