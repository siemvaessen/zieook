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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.model.ModelConstants;
import nl.gridline.zieook.model.Recommended;
import nl.gridline.zieook.model.Recommended.RecommenderType;
import nl.gridline.zieook.model.UserView;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User centric statistics aka the {@link nl.gridline.zieook.model.UserView} data
 * TODO:
 * the user viewekey is faulty. The order now is
 * <p />
 * Project zieook-data<br />
 * UserStatsTable.java created 6 jan. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @author <a href="mailto:daryl@gridline.nl">Daryl</a>
 * @version $Revision$, $Date$
 */
public class EventLogTable
{
	private static final Logger LOG = LoggerFactory.getLogger(EventLogTable.class);

	private final HBaseEventLogTable table;

	private static final byte[] EVENTLOG_COLUMN_USERVIEW = Bytes.toBytes(HBaseEventLogTable.EVENTLOG_COLUMN_USERVIEW);
	private static final byte[] EVENTLOG_COLUMN_USERVIEW_INDEX = Bytes
			.toBytes(HBaseEventLogTable.EVENTLOG_COLUMN_USERVIEW_INDEX);
	private static final byte[] EVENTLOG_COLUMN_RECOMMENDED = Bytes
			.toBytes(HBaseEventLogTable.EVENTLOG_COLUMN_RECOMMENDED);

	public EventLogTable(HBaseEventLogTable table)
	{
		this.table = table;
	}

	/**
	 * Returns a list of size views starting from 'start' where start is the complete key
	 * @param cp content provider
	 * @param recommender recommender
	 * @param start start key: recommender/user/item/stamp
	 * @param size number of items to return
	 * @param itemDuplicates add duplicate items or not.
	 * @return a list of UserView items
	 * @throws IOException
	 */
	public List<UserView> getViews(String cp, String recommender, long user, Long start, Long end, int size,
			boolean itemDuplicates)

	{

		LOG.debug("filtering: cp/recommender/user <start,end> - size == {}/{}/{}  <{},{}>[{}]", new Object[]{cp,
				recommender, user, start, end, size});

		FilterList filters = new FilterList();

		// basic filter comes first: only look for the given user:

		if (start == null && end == null)
		{
			filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getUserViewIndexKey(
					recommender, user))));
		}

		// NOTE:
		// timestamp's are sorted in inverted order: like Long.MAX_VALUE-time
		// so if we look for a range within <start,end> we actaully look for:
		// <max-end,max-start> so, any value greater than max-end and smaller than max-start
		//

		if (end != null)
		{
			filters.addFilter(new RowFilter(CompareOp.GREATER, new BinaryPrefixComparator(RowKeys.getUserViewIndexKey(
					recommender, user, end))));

		}
		if (start != null)
		{
			filters.addFilter(new RowFilter(CompareOp.LESS_OR_EQUAL, new BinaryPrefixComparator(RowKeys
					.getUserViewIndexKey(recommender, user, start))));
		}

		List<UserView> result = new ArrayList<UserView>();

		Set<Long> items = new HashSet<Long>(); // track items, so we only add them once.

		Scan scan = new Scan().addFamily(EVENTLOG_COLUMN_USERVIEW_INDEX).setFilter(filters);

		try
		{

			HTableInterface tableInterface = table.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			byte[] qualifier = RowKeys.getUserViewIndexKey();

			try
			{
				int count = 0;
				Result r = null;

				while ((r = scanner.next()) != null)
				{

					// when we do not allow duplicate items in the results:
					// check if we need to continue:
					// TODO can this be done using the scanner?
					if (!itemDuplicates)
					{
						byte[] rowi = r.getRow();
						long itemid = Bytes.toLong(rowi, rowi.length - Bytes.SIZEOF_LONG, Bytes.SIZEOF_LONG);
						if (items.contains(itemid))
						{
							// already added, continue
							continue;
						}
						items.add(itemid);
					}

					// convert!

					// get the row... that is indexed
					byte[] row = r.getValue(EVENTLOG_COLUMN_USERVIEW_INDEX, qualifier);
					if (row == null)
					{
						LOG.error("index corrupt for {} - skipping", Bytes.toStringBinary(r.getRow()));
						continue;
					}
					// .. and convert it to the view:
					Result view = table.get(cp, new Get(row).addFamily(EVENTLOG_COLUMN_USERVIEW));
					UserView v = new UserView(view.getFamilyMap(EVENTLOG_COLUMN_USERVIEW));
					v.setCp(cp);
					v.setRecommender(recommender);
					result.add(v);

					// track amount of results:
					count++;
					// stop when size is reached:
					if (count >= size)
					{
						break;
					}
				}
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}

		}
		catch (IOException e)
		{
			LOG.error("failed to get result", e);
		}

		// sort! - this should not be necessary any more... if the above stuff is fixed..
		// sortViews(result);

		return result;
	}

	/**
	 * Get a list of items for which recommendations where requested.
	 * @param cp
	 * @param collection
	 * @param user
	 * @param startTime
	 * @param endTime
	 * @param size
	 * @return
	 * @throws IOException
	 */
	public List<Recommended> getRecommended(String cp, String recommender, RecommenderType type, long user,
			Long startTime, Long endTime, int size)
	{
		List<Recommended> result = new ArrayList<Recommended>();

		Scan scan = new Scan();
		scan.addFamily(EVENTLOG_COLUMN_RECOMMENDED);

		FilterList filters = new FilterList();

		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRecommendedItemKey(
				recommender, user))));

		// time stamp filter:
		if (startTime != null)
		{
			SingleColumnValueFilter startFilter = new SingleColumnValueFilter(EVENTLOG_COLUMN_RECOMMENDED,
					ModelConstants.TIMESTAMP, CompareOp.GREATER_OR_EQUAL, Bytes.toBytes(startTime.longValue()));
			startFilter.setFilterIfMissing(true);
			filters.addFilter(startFilter);
		}
		if (endTime != null)
		{
			SingleColumnValueFilter endFilter = new SingleColumnValueFilter(EVENTLOG_COLUMN_RECOMMENDED,
					ModelConstants.TIMESTAMP, CompareOp.LESS, Bytes.toBytes(endTime.longValue()));
			endFilter.setFilterIfMissing(true);
			filters.addFilter(endFilter);
		}

		scan.setFilter(filters);

		try
		{
			HTableInterface tableInterface = table.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				int count = 0;
				Result r = null;
				while ((r = scanner.next()) != null)
				{
					result.add(new Recommended(r.getFamilyMap(EVENTLOG_COLUMN_RECOMMENDED)));

					// stop when size is reached:
					count++;
					if (count >= size)
					{
						break;
					}
				}
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to get result", e);
		}
		return result;
	}

	/**
	 * @param cp
	 * @param recommender
	 * @param user
	 * @param item
	 * @param stamp
	 * @param unit
	 * @return
	 * @throws IOException
	 */
	public UserView getView(String cp, String recommender, long user, long item, long stamp, TimeUnit unit)
	{
		if (unit == TimeUnit.MILLISECONDS)
		{
			return getView(cp, recommender, user, item, stamp);
		}
		else if (unit == TimeUnit.SECONDS)
		{
			return getView(cp, recommender, user, item, stamp * 1000);
		}
		else
		{
			throw new IllegalArgumentException("Only MILLISECONDS and SECONDS time unit is accepted");
		}
	}

	/**
	 * GET a user view given quite given the precise data
	 * @param cp content provider
	 * @param recommender recommender name
	 * @param user a user
	 * @param item an item
	 * @param stamp a timestamp in milliseconds
	 * @return
	 * @throws IOException
	 */
	private UserView getView(String cp, String recommender, long user, long item, long stamp)
	{
		// is it in seconds or milliseconds?

		byte[] row = RowKeys.getUserViewKey(recommender, user, item, stamp);

		Get get = new Get(row).addFamily(EVENTLOG_COLUMN_USERVIEW);
		Result result = table.get(cp, get);
		if (!result.isEmpty())
		{
			NavigableMap<byte[], byte[]> map = result.getFamilyMap(EVENTLOG_COLUMN_USERVIEW);
			return new UserView(map);
		}

		return null;
	}

	public Recommended getRecommended(String cp, String recommender, RecommenderType type, long user, long item,
			long stamp, TimeUnit unit)
	{
		if (unit == TimeUnit.MILLISECONDS)
		{
			return getRecommended(cp, recommender, user, item, stamp);
		}
		else if (unit == TimeUnit.SECONDS)
		{
			return getRecommended(cp, recommender, user, item, stamp * 1000);
		}
		else
		{
			throw new IllegalArgumentException("Only MILLISECONDS and SECONDS time unit is accepted");
		}
	}

	private Recommended getRecommended(String cp, String recommender, long user, long item, long stamp)
	{
		byte[] row = RowKeys.getRecommendedItemKey(recommender, user, item, stamp);
		Get get = new Get(row).addFamily(EVENTLOG_COLUMN_RECOMMENDED);
		Result result = table.get(cp, get);
		if (!result.isEmpty())
		{
			NavigableMap<byte[], byte[]> map = result.getFamilyMap(EVENTLOG_COLUMN_RECOMMENDED);
			return new Recommended(map);
		}
		else
		{
			LOG.warn("no result for: {}", Bytes.toString(row));
		}
		return null;
	}

	/**
	 * PUT a userview
	 * @param cp
	 * @param recommender
	 * @param userid
	 * @param itemid
	 * @param view
	 * @throws IOException
	 */
	public void putView(String cp, String recommender, UserView view, TimeUnit unit)
	{
		if (view.getDate() == null)
		{
			view.setDate(System.currentTimeMillis());
		}
		else
		{
			if (TimeUnit.SECONDS == unit)
			{
				view.setDate(view.getDate() * 1000);
			}
			// else: already milliseconds, so ignore.
		}
		putView(cp, recommender, view);
	}

	private void putView(String cp, String recommender, UserView view)
	{
		// set recommender to view!
		view.setRecommender(recommender);

		byte[] row = RowKeys.getUserViewKey(recommender, view);
		byte[] rowi = RowKeys.getUserViewIndexKey(recommender, view);
		// LOG.debug("Storing view with: {}", Bytes.toString(row));
		Put put = new Put(row);
		Put puti = new Put(rowi).add(EVENTLOG_COLUMN_USERVIEW_INDEX, RowKeys.getUserViewKey(), row);

		Map<byte[], byte[]> data = view.toMap();
		for (Map.Entry<byte[], byte[]> entry : data.entrySet())
		{
			put.add(EVENTLOG_COLUMN_USERVIEW, entry.getKey(), entry.getValue());
		}
		table.put(cp, put);
		table.put(cp, puti);
	}

	public void putRecommend(String cp, String recommender, Recommended recommend, TimeUnit unit)
	{
		if (recommend.getDate() == null)
		{
			recommend.setDate(System.currentTimeMillis());
		}
		else
		{
			if (TimeUnit.SECONDS == unit)
			{
				recommend.setDate(recommend.getDate() * 1000);
			}
			// else: already milliseconds, so ignore.
		}
		putRecommend(cp, recommender, recommend);
	}

	private void putRecommend(String cp, String recommender, Recommended recommend)
	{
		// type needs to be added to the key...
		// if user_based: use user_id in the row key
		// if item_based: use item_id int the row key

		byte[] row = RowKeys.getRecommendedItemKey(recommender, recommend.getUser(), recommend.getItem(),
				recommend.getDate());
		Put put = new Put(row);
		Map<byte[], byte[]> data = recommend.toMap();
		for (Map.Entry<byte[], byte[]> entry : data.entrySet())
		{
			put.add(EVENTLOG_COLUMN_RECOMMENDED, entry.getKey(), entry.getValue());
		}
		table.put(cp, put);
	}

	public long deleteView(String cp, String recommender, long user, long item, long stamp, TimeUnit unit)

	{
		long time;
		if (TimeUnit.SECONDS == unit)
		{
			time = stamp * 1000;
		}
		else
		{
			time = stamp;
		}
		return deleteView(cp, recommender, user, item, time);
	}

	private long deleteView(String cp, String recommender, long user, long item, long stamp)
	{
		byte[] row = RowKeys.getUserViewKey(recommender, user, item, stamp);
		byte[] rowi = RowKeys.getUserViewIndexKey(recommender, user, item, stamp);
		table.delete(cp, Arrays.asList(new Delete(row), new Delete(rowi)));
		return 1L;
	}

	public long deleteViews(String cp, String recommender)
	{
		FilterList filters = new FilterList();

		// filter column-family & recommender name
		filters.addFilter(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(EVENTLOG_COLUMN_USERVIEW)));
		filters
				.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getUserViewKey(recommender))));

		Scan scan = new Scan().addFamily(EVENTLOG_COLUMN_USERVIEW).setFilter(filters);

		// TODO put this in a map-reduce delete.. that executes in background..
		// we only need to pass the table & a scan object. should be quite easy

		long count = deleteAll(scan, cp, EVENTLOG_COLUMN_RECOMMENDED);

		return count;
	}

	public long deleteViews(String cp, String recommender, long user)
	{
		FilterList filters = new FilterList();
		filters.addFilter(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(EVENTLOG_COLUMN_USERVIEW)));
		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getUserViewKey(recommender,
				user))));
		Scan scan = new Scan().addFamily(EVENTLOG_COLUMN_USERVIEW).setFilter(filters);
		return deleteAll(scan, cp, EVENTLOG_COLUMN_USERVIEW);
	}

	public long deleteView(String cp, String recommender, long user, Long startDate, Long endDate)
	{
		FilterList filters = new FilterList();
		filters.addFilter(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(EVENTLOG_COLUMN_USERVIEW)));
		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getUserViewKey(recommender,
				user))));
		Scan scan = new Scan().addFamily(EVENTLOG_COLUMN_USERVIEW).setFilter(filters);
		return deleteAll(scan, cp, EVENTLOG_COLUMN_USERVIEW);
	}

	public long deleteRecommend(String cp, String recommender, long user, long item, long stamp, TimeUnit unit)

	{
		long time;
		if (TimeUnit.SECONDS == unit)
		{
			time = stamp * 1000;
		}
		else
		{
			time = stamp;
		}
		return deleteRecommend(cp, recommender, user, item, time);
	}

	private long deleteRecommend(String cp, String recommender, long user, long item, long stamp)
	{
		byte[] row = RowKeys.getRecommendedItemKey(recommender, user, item, stamp);
		Delete delete = new Delete(row);
		table.delete(cp, delete);
		return 1L;
	}

	public long deleteRecommend(String cp, String recommender)
	{
		FilterList filters = new FilterList();

		filters.addFilter(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(EVENTLOG_COLUMN_RECOMMENDED)));
		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys
				.getRecommendedItemKey(recommender))));

		Scan scan = new Scan().addFamily(EVENTLOG_COLUMN_RECOMMENDED).setFilter(filters);
		return deleteAll(scan, cp, EVENTLOG_COLUMN_RECOMMENDED);
	}

	public long deleteRecommend(String cp, String recommender, long user)
	{
		FilterList filters = new FilterList();

		filters.addFilter(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(EVENTLOG_COLUMN_RECOMMENDED)));
		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRecommendedItemKey(
				recommender, user))));

		Scan scan = new Scan().addFamily(EVENTLOG_COLUMN_RECOMMENDED).setFilter(filters);
		return deleteAll(scan, cp, EVENTLOG_COLUMN_RECOMMENDED);
	}

	/**
	 * scan all views, and create a new index with a different key that allows for scanning view based on a time range.
	 * @param cp
	 */
	public void updateViews(String cp)
	{
		// scan all views, create the 'new' indexed version of that view...

		// scan all views:
		Scan scan = new Scan().addFamily(EVENTLOG_COLUMN_USERVIEW);
		scan.setCaching(1000);

		List<Put> puts = new ArrayList<Put>(1000);
		long count = 0;
		ResultScanner scanner = null;
		try
		{
			HTableInterface tableInterface = table.getTable(cp);
			scanner = tableInterface.getScanner(scan);
			for (Result rr = scanner.next(); rr != null; rr = scanner.next())
			{
				// decompose the existing row
				// create a new row and put! - (cached put)

				Object[] row = RowKeys.getStrStrLngLngLng(rr.getRow());
				String recommender = row[1].toString();
				long user = (Long) row[2];
				long item = (Long) row[3];
				long stamp = (Long) row[4]; // this is an inverted stamp... Long.MAX_VALUE - stamp
				byte[] rowi = RowKeys.getUserViewIndexKeyNoInvert(recommender, user, item, stamp);
				if (!table.exists(cp, new Get(rowi)))
				{
					puts.add(new Put(rowi).add(EVENTLOG_COLUMN_USERVIEW_INDEX, RowKeys.getUserViewIndexKey(), rr.getRow()));
					count++;
				}

				if (puts.size() >= 1000)
				{
					LOG.info("intermediate put, reached 1000 updates, total at {} ", count);
					table.put(cp, puts);
					puts.clear();
				}
			}

			if (puts.size() > 0)
			{
				LOG.info("putting {} indexes", count);
				table.put(cp, puts);
				puts.clear();
			}

		}
		catch (IOException e)
		{
			LOG.error("failed to SCAN table '" + table.getTableName(cp) + "' for recommenders", e);
		}
		finally
		{
			if (scanner != null)
			{
				scanner.close();
			}
		}

		// etRowKey(USER_VIEW_INDEX, recommender, user, Long.MAX_VALUE - stamp, item);

		// byte[] row = RowKeys.getUserViewKey(recommender, view);
		// byte[] rowi = RowKeys.getUserViewIndexKey(recommender, view);
		// // LOG.debug("Storing view with: {}", Bytes.toString(row));
		// Put put = new Put(row);
		// Put puti = new Put(rowi).add(EVENTLOG_COLUMN_USERVIEW_INDEX, RowKeys.getUserViewKey(), row);

	}

	/**
	 * Delete all rows produces by scan
	 * @param scan a scan - be careful to produce a correct scan, it deletes stuf from the table!
	 * @param cp content provider name
	 */
	private long deleteAll(Scan scan, String cp, byte[] family)
	{

		// scan!!!
		List<Delete> result = new ArrayList<Delete>();
		long count = 0;
		try
		{
			HTableInterface tableInterface = table.getTable(cp);

			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				// get the rows from the table:
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					KeyValue kv = rr.getColumnLatest(family, ModelConstants.RECOMMENDER);
					if (kv != null)
					{
						// the task id is stored as a string (like the whole taskconfig) - but we only need this particular
						// value: - if the parselong fails, there's something really wrong - a coding bug -
						result.add(new Delete(rr.getRow()));
						count++;
					}
					else
					{
						LOG.error("This is a bug: some items are left behind, while cleaning: {}", rr);
					}

					// delete every thousand rows:
					if (result.size() > 0 && result.size() % 1000 == 0)
					{
						LOG.info("intermediate delete <{}> total now <{}>", result.size(), count);
						table.delete(cp, result);
						result.clear();
					}
				}
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to SCAN table '" + table.getTableName(null) + "' for recommenders", e);
		}

		// flush all that's left
		count += result.size();
		table.delete(cp, result);
		result.clear();

		LOG.info("Deleted {} old data for <{}>", count, cp);

		return count;
	}

}
