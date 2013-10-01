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

import static org.junit.Assume.assumeTrue;

import java.util.List;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.model.CollectionItem;
import nl.gridline.zieook.model.Rating;
import nl.gridline.zieook.model.UserView;

import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * CollectionViewsTableTest.java created 11 jan. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class CollectionViewsTableTest
{
	private static final Logger LOG = LoggerFactory.getLogger(CollectionViewsTableTest.class);

	private static final String CP = "testCP006";
	private static final String COLLECTION = "am2";

	static
	{
		System.setProperty("gridline.project.name", "zieook");
	}

	private static HBaseManager manager;

	private static CollectionViewsTable views;

	private static HBaseEventLogTable events;

	private static HBaseCollectionTable collections;

	private static HBaseUserTable users;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Config config = Config.getInstance();
		String zookeeper = config.getZooKeeperHost();
		HBaseManager manager = HBaseManager.getInstance(zookeeper);

		events = new HBaseEventLogTable(manager);
		assumeTrue(events.tableExists(CP));

		collections = new HBaseCollectionTable(manager);
		assumeTrue(collections.tableExists(CP));

		users = new HBaseUserTable(manager);
		assumeTrue(users.tableExists(CP));

		views = new CollectionViewsTable(events, collections, users);

	}

	@Test
	public void searchRatings()
	{
		List<Rating> result = views.searchRatings(CP, COLLECTION, 1, null, ".*", 100);
		for (Rating r : result)
		{
			LOG.info("(.*) - {}", r);
		}

		result = views.searchRatings(CP, COLLECTION, 1, null, "^De.*", 100);
		for (Rating r : result)
		{
			LOG.info("(^De.*) - {}", r);
		}
	}

	@Test
	public void searchRatingsAsCollectionItems()
	{
		List<CollectionItem> result = views.searchRatingsAsCollectionItems(CP, COLLECTION, 1, null, ".*", 100);
		for (CollectionItem i : result)
		{
			LOG.info("(.*) -  {}", i);
		}

		result = views.searchRatingsAsCollectionItems(CP, COLLECTION, 1, null, "^De.*", 100);
		for (CollectionItem i : result)
		{
			LOG.info("(^De.*) -  {}", i);
		}
	}

	@Test
	public void searchViewsAsCollection()
	{
		List<CollectionItem> result = views.searchViewsAsCollectionItems(CP, COLLECTION, 2, null, null, ".*", 100);
		for (CollectionItem i : result)
		{
			LOG.info("(.*) -  {}", i);
		}

		result = views.searchViewsAsCollectionItems(CP, COLLECTION, 2, null, null, "^De.*", 100);
		for (CollectionItem i : result)
		{
			LOG.info("(^De.*) - {}", i);
		}
	}

	@Test
	public void searchViews()
	{
		List<UserView> result = views.searchViews(CP, COLLECTION, 2, null, null, ".*", 100);
		for (UserView v : result)
		{
			LOG.info("(.*) - {}", v);
		}

		result = views.searchViews(CP, COLLECTION, 2, null, null, "^De.*", 100);
		for (UserView v : result)
		{
			LOG.info("(^De.*) - {}", v);
		}

	}

	@Test
	public void searchViewsDe()
	{

		final String regexp = ".*((D|d)(E|e)).*";
		List<UserView> result = views.searchViews(CP, COLLECTION, 2, null, null, regexp, 100);
		for (UserView v : result)
		{
			LOG.info("{} - {}", regexp, v);
		}

		List<CollectionItem> result2 = views.searchViewsAsCollectionItems(CP, COLLECTION, 2, null, null, regexp, 100);
		for (CollectionItem i : result2)
		{
			LOG.info("{} - {}", regexp, i);
		}

	}

	@Test
	@Ignore
	public void testItemFilter()
	{
		FilterList itemFilter = new FilterList(Operator.MUST_PASS_ONE);

		// row filter is probably faster, (don't know for sure) - otherwise single column-value filter is also
		// possible.
		itemFilter.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryComparator(RowKeys
				.getCollectionKey(COLLECTION, 402))));
		itemFilter.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryComparator(RowKeys
				.getCollectionKey(COLLECTION, 403))));
		itemFilter.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryComparator(RowKeys.getCollectionKey(COLLECTION,
				1312))));

		Scan scan = new Scan(RowKeys.getCollectionKey(COLLECTION, 402), RowKeys.getCollectionKey(COLLECTION, 1312 + 1))
				.addFamily(Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR)).setFilter(itemFilter);

		List<CollectionItem> result = new CollectionTable(collections).getItems(CP, scan, 100);
		for (CollectionItem i : result)
		{
			LOG.info("{}", i);
		}

	}
}
