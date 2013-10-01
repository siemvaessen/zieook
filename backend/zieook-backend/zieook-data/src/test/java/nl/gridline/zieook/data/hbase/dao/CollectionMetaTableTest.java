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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.data.hbase.model.Helper;
import nl.gridline.zieook.workflow.model.Collection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * CollectionMetaTableTest.java created 7 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class CollectionMetaTableTest
{

	private static HBaseManager manager;
	private static HBaseConfigurationTable table;
	private static CollectionMetaTable collection;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception
	{
		// check content provider meta table exists
		// be very careful inserting data - it run's against the test machine

		manager = HBaseManager.getInstance("mulisch");
		table = new HBaseConfigurationTable(manager);

		if (!table.tableExists(Helper.CP))
		{
			table.create(Helper.CP);
		}
		collection = new CollectionMetaTable(table);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDown() throws Exception
	{

	}

	@Test
	public void testCreateGet()
	{
		Collection c = new Collection("test-cp", "collection-name", "movielens", "type", null, null, -1);

		collection.putCollection("test-cp", c);
		Collection r = collection.getCollection("test-cp", "collection-name");

		assertEquals(c, r);
		assertTrue(collection.exists("test-cp", "collection-name"));

		collection.deleteCollection("test-cp", "collection-name");

		assertEquals(null, collection.getCollection("test-cp", "collection-name"));
		assertTrue(!collection.exists("test-cp", "collection-name"));

	}

}
