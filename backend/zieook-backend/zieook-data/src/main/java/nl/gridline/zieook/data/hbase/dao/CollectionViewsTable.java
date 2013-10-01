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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.model.CollectionItem;
import nl.gridline.zieook.model.ModelConstants;
import nl.gridline.zieook.model.Rating;
import nl.gridline.zieook.model.UserView;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * combines calls to eventlog (views) and collection items. In such a way that you can search for
 * <p />
 * Project zieook-data<br />
 * CollectionViewTable.java created 10 jan. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class CollectionViewsTable
{
	private static final int ITEM_BLOCK_SIZE = 20;

	private static final Logger LOG = LoggerFactory.getLogger(CollectionViewsTable.class);

	private static final byte[] EVENTLOG_COLUMN_USERVIEW = Bytes.toBytes(HBaseEventLogTable.EVENTLOG_COLUMN_USERVIEW);
	private static final byte[] COLUMN_INTR = Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR);
	private static final byte[] COLUMN_RATING = Bytes.toBytes(HBaseTableConstants.USERTABLE_COLUMN_RATING);

	private final HBaseCollectionTable collectionTable;
	private final HBaseEventLogTable eventsTable;
	private final HBaseUserTable usersTable;

	public CollectionViewsTable(HBaseEventLogTable events, HBaseCollectionTable collection, HBaseUserTable users)
	{
		collectionTable = collection;
		eventsTable = events;
		usersTable = users;
	}

	public List<UserView> searchViews(String cp, String collection, long user, Long start, Long end, String regexp,
			int size)
	{
		List<UserView> result = new ArrayList<UserView>();
		Set<Long> allIds = new HashSet<Long>();

		Scan scan = new Scan().addFamily(EVENTLOG_COLUMN_USERVIEW).setFilter(getUserViews(user, start, end));

		SingleColumnValueFilter filter = new SingleColumnValueFilter(COLUMN_INTR, ModelConstants.TITLE, CompareOp.EQUAL,
				new RegexStringComparator(regexp));

		try
		{
			HTableInterface tableInterface = eventsTable.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				Result[] rr = null;
				while ((rr = scanner.next(ITEM_BLOCK_SIZE)).length > 0)
				{
					for (int i = 0; i < rr.length; i++)
					{
						// read item id's & search:
						// byte[] srcItem = rr.getValue(EVENTLOG_COLUMN_USERVIEW, ModelConstants.SOURCEITEM);
						byte[] item = rr[i].getValue(EVENTLOG_COLUMN_USERVIEW, ModelConstants.ITEM);
						if (item != null)
						{
							long id = Bytes.toLong(item);
							// only search id's that are not in the result set:
							if (!allIds.contains(id) && accepts(cp, collection, filter, id))
							{
								UserView view = new UserView(rr[i].getFamilyMap(EVENTLOG_COLUMN_USERVIEW));
								view.setCp(cp);
								view.setCollection(collection);
								result.add(view);
							}
							allIds.add(id);
						}
					}
					if (result.size() > size)
					{
						return result.subList(0, size);
					}

				}
			}
			finally
			{
				scanner.close();
				eventsTable.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to get result", e);
		}

		return result;

	}

	public List<CollectionItem> searchViewsAsCollectionItems(String cp, String collection, long user, Long start,
			Long end, String regexp, int size)
	{
		List<CollectionItem> result = new ArrayList<CollectionItem>();
		Set<Long> allIds = new HashSet<Long>();
		SortedSet<Long> items = new TreeSet<Long>();

		Scan scan = new Scan().addFamily(EVENTLOG_COLUMN_USERVIEW).setFilter(getUserViews(user, start, end));
		try
		{
			HTableInterface tableInterface = eventsTable.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				Result[] rr = null;
				while ((rr = scanner.next(ITEM_BLOCK_SIZE)).length > 0)
				{
					for (int i = 0; i < rr.length; i++)
					{
						// read item id's & search:
						// byte[] srcItem = rr.getValue(EVENTLOG_COLUMN_USERVIEW, ModelConstants.SOURCEITEM);
						byte[] item = rr[i].getValue(EVENTLOG_COLUMN_USERVIEW, ModelConstants.ITEM);
						if (item != null)
						{
							long id = Bytes.toLong(item);
							// only search id's that are not in the result set:
							if (!allIds.contains(id))
							{
								items.add(id);
							}
						}
					}
					// keep id's (so we skip double searches)
					allIds.addAll(items);
					// get collection items:
					result.addAll(filterItems(cp, collection, regexp, items));
					if (result.size() > size)
					{
						return result.subList(0, size);
					}
					items.clear();
				}
			}
			finally
			{
				scanner.close();
				eventsTable.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to get result", e);
		}

		return result;
	}

	/*
	 * Possible solutions:
	 * - look in ratings for the ratings (given the cp,collection,user etc)
	 * - use a page filter PageFilter or ColumnPaginationFilter to limit the results.
	 * - check for every collection id (scan) if it the title regexp matches..
	 * - can we create a 'IN' like filter?
	 * - YES: use filterlist and MUST_PASS_ONE - we can search per page.
	 * - is their a smart way to do this?
	 * - remark: using a scan to execute the title match is quicker than retrieving the item and matching it, because
	 * the scan happens in the cluster
	 */

	public List<Rating> searchRatings(String cp, String collection, long user, Long startItem, String regexp, int size)
	{
		List<Rating> result = new ArrayList<Rating>();

		byte[] start;
		if (startItem != null)
		{
			start = RowKeys.getRatingKey(collection, user, startItem.longValue());
		}
		else
		{
			start = RowKeys.getRatingKey(collection, user);
		}

		Scan scan = new Scan(start).addFamily(COLUMN_RATING).setFilter(
				new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRatingKey(collection, user))));

		SingleColumnValueFilter filter = new SingleColumnValueFilter(COLUMN_INTR, ModelConstants.TITLE, CompareOp.EQUAL,
				new RegexStringComparator(regexp));

		Set<Long> allIds = new HashSet<Long>();
		try
		{
			HTableInterface tableInterface = usersTable.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				Result rr = null;
				while ((rr = scanner.next()) != null)
				{
					// Rating userRating = new Rating(rr.getFamilyMap(COLUMN_RATING));
					byte[] item = rr.getValue(COLUMN_RATING, ModelConstants.ITEM);
					if (item != null)
					{
						long id = Bytes.toLong(item);
						if (!allIds.contains(id) && accepts(cp, collection, filter, id))
						{
							Rating rating = new Rating(rr.getFamilyMap(COLUMN_RATING));
							rating.setCp(cp);
							rating.setCollection(collection);
							result.add(rating);
						}
						allIds.add(id);
					}
					if (result.size() > size)
					{
						return result.subList(0, size);
					}
				}
			}
			finally
			{
				scanner.close();
				usersTable.putTable(tableInterface);
			}

		}
		catch (IOException e)
		{
			LOG.error("failed to get scanner for request", e);
		}

		return result;
	}

	public List<CollectionItem> searchRatingsAsCollectionItems(String cp, String collection, long user, Long startItem,
			String regexp, int size)
	{
		List<CollectionItem> result = new ArrayList<CollectionItem>();
		SortedSet<Long> items = new TreeSet<Long>();

		byte[] start;
		if (startItem != null)
		{
			start = RowKeys.getRatingKey(collection, user, startItem.longValue());
		}
		else
		{
			start = RowKeys.getRatingKey(collection, user);
		}

		Scan scan = new Scan(start).addFamily(COLUMN_RATING).setFilter(
				new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRatingKey(collection, user))));

		Set<Long> allIds = new HashSet<Long>();
		try
		{
			HTableInterface tableInterface = usersTable.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				Result[] rr = null;
				while ((rr = scanner.next(ITEM_BLOCK_SIZE)).length > 0)
				{
					for (int i = 0; i < rr.length; i++)
					{
						// Rating userRating = new Rating(rr.getFamilyMap(COLUMN_RATING));
						byte[] item = rr[i].getValue(COLUMN_RATING, ModelConstants.ITEM);
						if (item != null)
						{
							long id = Bytes.toLong(item);
							if (!allIds.contains(id))
							{
								items.add(id);
							}
						}
					}
					allIds.addAll(items);
					result.addAll(filterItems(cp, collection, regexp, items));
					if (result.size() > size)
					{
						return result.subList(0, size);
					}
					items.clear();
				}
			}
			finally
			{
				scanner.close();
				usersTable.putTable(tableInterface);
			}

		}
		catch (IOException e)
		{
			LOG.error("failed to get scanner for request", e);
		}

		return result;
	}

	private boolean accepts(String cp, String collection, Filter filter, long item)
	{
		Scan scan = new Scan(RowKeys.getCollectionKey(collection, item), RowKeys.getCollectionKey(collection, item + 1))
				.addFamily(COLUMN_INTR).setFilter(filter);

		try
		{
			HTableInterface tableInterface = collectionTable.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				return scanner.next() != null;
			}
			finally
			{
				scanner.close();
				eventsTable.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to get result", e);
		}

		return false;
	}

	private Set<CollectionItem> filterItems(String cp, String collection, String regexp, SortedSet<Long> items)
	{
		// sort the array, so we can set a start / stop row more easily:

		Set<CollectionItem> result = new HashSet<CollectionItem>();

		// create filter:
		Scan scan = new Scan(RowKeys.getCollectionKey(collection, items.first()), RowKeys.getCollectionKey(collection,
				items.last() + 1)).addFamily(COLUMN_INTR).setFilter(getItemFilter(collection, regexp, items));

		try
		{
			HTableInterface tableInterface = collectionTable.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				Result[] results = scanner.next(items.size());
				for (Result r : results)
				{
					result.add(new CollectionItem(r.getFamilyMap(COLUMN_INTR)));
					// byte[] item = r.getValue(COLUMN_INTR, ModelConstants.ITEM);
					// result.add(Bytes.toLong(item));
				}
			}
			finally
			{
				scanner.close();
				eventsTable.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to get result", e);
		}

		return result;
	}

	private FilterList getItemFilter(String collection, String regexp, Set<Long> items)
	{

		// Filter on the given items (this is an OR filter on the rows)
		FilterList itemFilter = new FilterList(Operator.MUST_PASS_ONE);
		for (long i : items)
		{
			// row filter is probably faster, (don't know for sure) - otherwise single column-value filter is also
			// possible.
			itemFilter.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryComparator(RowKeys.getCollectionKey(collection,
					i))));

		}

		FilterList filters = new FilterList();
		// filter on collection:
		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(Bytes.toBytes(collection))));
		// filter on items:
		filters.addFilter(itemFilter);

		SingleColumnValueFilter value = new SingleColumnValueFilter(COLUMN_INTR, ModelConstants.TITLE, CompareOp.EQUAL,
				new RegexStringComparator(regexp));

		value.setFilterIfMissing(true);
		filters.addFilter(value);

		return filters;

	}

	private FilterList getUserViews(long user, Long start, Long end)
	{
		FilterList filters = new FilterList();

		// filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getUserViewKey(recommender,
		// user))));

		filters.addFilter(new SingleColumnValueFilter(EVENTLOG_COLUMN_USERVIEW, ModelConstants.USER, CompareOp.EQUAL,
				new BinaryComparator(Bytes.toBytes(user))));

		// timestamp filter:
		if (start != null)
		{
			SingleColumnValueFilter startFilter = new SingleColumnValueFilter(EVENTLOG_COLUMN_USERVIEW,
					ModelConstants.TIMESTAMP, CompareOp.GREATER_OR_EQUAL, Bytes.toBytes(start.longValue()));
			startFilter.setFilterIfMissing(true);
			filters.addFilter(startFilter);
		}

		if (end != null)
		{
			SingleColumnValueFilter endFilter = new SingleColumnValueFilter(EVENTLOG_COLUMN_USERVIEW,
					ModelConstants.TIMESTAMP, CompareOp.LESS, Bytes.toBytes(end.longValue()));
			endFilter.setFilterIfMissing(true);
			filters.addFilter(endFilter);
		}

		return filters;
	}

}
