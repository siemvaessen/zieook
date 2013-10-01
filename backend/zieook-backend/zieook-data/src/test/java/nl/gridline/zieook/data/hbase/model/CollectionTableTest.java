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
import static org.junit.Assume.assumeTrue;

import java.util.List;

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.CollectionMetaTable;
import nl.gridline.zieook.data.hbase.dao.CollectionTable;
import nl.gridline.zieook.model.Categories;
import nl.gridline.zieook.model.CollectionItem;
import nl.gridline.zieook.workflow.model.Collections;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * CollectionTableTest.java created 16 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionTableTest
{
	private static final Logger LOG = LoggerFactory.getLogger(CollectionTableTest.class);

	private HBaseManager manager;
	private HBaseCollectionTable table;

	private static final String CP = "testCP006";
	private static final String COLLECTION = "am2";

	@Before
	public void setUp() throws Exception
	{
		manager = HBaseManager.getInstance("mulisch");

		table = new HBaseCollectionTable(manager);
		assumeTrue(table.tableExists(CP));
	}

	@Test
	public void listCollection()
	{
		CollectionMetaTable collTable = new CollectionMetaTable(new HBaseConfigurationTable(manager));
		System.out.println("list: " + new Collections(collTable.getCollections(null)).toJSON());
	}

	@Test
	public void readCategories()
	{

		CollectionTable collTable = new CollectionTable(table);
		Categories result = collTable.getCategories(CP, COLLECTION, null);
		LOG.info("Categories JSON\n" + result.toJSON() + "\n");
	}

	private static final String CATCHALL = ".*";
	private static final String GEZICHT = "^[G|g]ezicht.*";
	private static final String[] ABCo = {"^(A|a).*", "^(B|b).*", "^(C|c).*", "^\\W.*"};

	@Test
	public void searchItem()
	{

		CollectionTable collection = new CollectionTable(table);
		List<CollectionItem> catchAll = collection.searchItems(CP, COLLECTION, CATCHALL, 500);
		assertEquals(500, catchAll.size());

		List<CollectionItem> gezicht = collection.searchItems(CP, COLLECTION, GEZICHT, 500);
		System.out.println(gezicht.size());

		assertEquals(70, gezicht.size());
		for (CollectionItem i : gezicht)
		{
			assertTrue(i.getTitle().startsWith("Gezicht"));
		}
	}

	@Test
	public void searchItemA()
	{
		CollectionTable collection = new CollectionTable(table);
		List<CollectionItem> a = collection.searchItems(CP, COLLECTION, ABCo[0], 500);
		assertEquals(248, a.size());
		for (CollectionItem i : a)
		{
			assertTrue(i.getTitle().startsWith("A") || i.getTitle().startsWith("a"));
		}
		System.out.println(a.size());
	}

	@Test
	public void searchItemB()
	{
		CollectionTable collection = new CollectionTable(table);
		List<CollectionItem> b = collection.searchItems(CP, COLLECTION, ABCo[1], 500);
		assertEquals(173, b.size());
		for (CollectionItem i : b)
		{
			// System.out.println(i.getCollectionId() + "\t" + i.getTitle());
			assertTrue(i.getTitle().startsWith("B") || i.getTitle().startsWith("b"));
		}
		// System.out.println(b.size());
	}

	@Test
	public void searchItemC()
	{
		CollectionTable collection = new CollectionTable(table);
		List<CollectionItem> c = collection.searchItems(CP, COLLECTION, ABCo[2], 500);
		assertEquals(47, c.size());
		for (CollectionItem i : c)
		{
			// System.out.println(i.getCollectionId() + "\t" + i.getTitle());
			assertTrue(i.getTitle().startsWith("C") || i.getTitle().startsWith("c"));
		}
		// System.out.println(c.size());
	}

	@Test
	public void searchItemOther()
	{
		CollectionTable collection = new CollectionTable(table);
		List<CollectionItem> other = collection.searchItems(CP, COLLECTION, ABCo[3], 500);
		assertEquals(294, other.size());

		// this is a bit annoying, compare to another regexp, so skip for now.
		// for (CollectionItem i : other)
		// {
		// assertTrue(i.getTitle().startsWith("a"));
		// }
	}

}
