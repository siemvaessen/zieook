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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.TimeUnit;

import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.model.ModelConstants;
import nl.gridline.zieook.model.Rating;
import nl.gridline.zieook.model.User;

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
 * User table: all user related data:
 * The user object and user ratings.
 * <p />
 * Project zieook-data<br />
 * UserTable.java created 16 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date: 2011-03-03 16:39:30 +0100 (Thu, 03 Mar
 *          2011) $
 */
public class UserTable
{

	private static final byte[] EPOCH = Bytes.toBytes("0000_RATING_EPOCH");

	private static final Logger LOG = LoggerFactory.getLogger(UserTable.class);

	private final HBaseUserTable table;

	private static final byte[] COLUMN_FOAF = Bytes.toBytes(HBaseTableConstants.USERTABLE_COLUMN_FOAF);
	private static final byte[] COLUMN_RATING = Bytes.toBytes(HBaseTableConstants.USERTABLE_COLUMN_RATING);

	public UserTable(HBaseUserTable table)
	{
		this.table = table;

	}

	/**
	 * Return a user
	 * @param cp
	 *           collection provider
	 * @param row
	 *           user id
	 * @return
	 */
	public Map<String, String> getUserMap(String cp, long userid)
	{
		Get get = new Get(RowKeys.getUserKey(userid)).addFamily(COLUMN_FOAF);
		Result result = table.get(cp, get);
		if (!result.isEmpty())
		{
			NavigableMap<byte[], byte[]> map = result.getFamilyMap(COLUMN_FOAF);
			Map<String, String> resultMap = new HashMap<String, String>(map.size());
			for (Map.Entry<byte[], byte[]> entry : map.entrySet())
			{
				resultMap.put(Bytes.toString(entry.getKey()), Bytes.toString(entry.getValue()));
			}
			return resultMap;
		}
		return null;
	}

	public boolean isUser(String cp, long userid)
	{
		Get get = new Get(RowKeys.getUserKey(userid)).addFamily(COLUMN_FOAF);
		return table.exists(cp, get);
	}

	/**
	 * Return a user
	 * @param cp
	 *           collection provider
	 * @param row
	 *           user id
	 * @return
	 */
	public User getUser(String cp, long userid)
	{
		Get get = new Get(RowKeys.getUserKey(userid)).addFamily(COLUMN_FOAF);
		Result result = table.get(cp, get);
		if (!result.isEmpty())
		{
			NavigableMap<byte[], byte[]> map = result.getFamilyMap(COLUMN_FOAF);

			byte[] id = get.getRow();
			User user = new User(map);
			user.setId(Bytes.toLong(id, id.length - Bytes.SIZEOF_LONG));
			return user;
		}
		return null;
	}

	public long getRatingCount(String cp, long userid)
	{
		Result result = table.get(cp, new Get(RowKeys.getUserKey(userid)).addFamily(COLUMN_FOAF));
		if (!result.isEmpty())
		{
			byte[] cnt = result.getValue(COLUMN_FOAF, ModelConstants.COUNT);
			if (cnt != null)
			{
				return Bytes.toLong(cnt);
			}
		}
		return 0;
	}

	/**
	 * Returns a list of users from the collection provider -cp-, starting after the user -start-, with the size of
	 * -size-
	 * @param cp collection provider
	 * @param start the user from which to start, inclusive
	 * @param size size of the returned list
	 * @return
	 */
	public List<User> getUsers(String cp, long start, int size)
	{
		List<User> users = new ArrayList<User>(size);

		Scan scan = new Scan().addFamily(COLUMN_FOAF).setStartRow(RowKeys.getUserKey(start));
		try
		{
			HTableInterface tableInterface = table.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				for (Result result = scanner.next(); result != null; scanner.next())
				{
					users.add(new User(result.getFamilyMap(COLUMN_FOAF)));
					if (users.size() > size)
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
			LOG.error("Error while getting users.", e);
		}

		return users;
	}

	/**
	 * Puts the given user, updated if he already exists, adds if not.
	 * @param cp collection provider
	 * @param user the new/updated user
	 */
	public void putUser(String cp, User user)
	{
		Put put = new Put(RowKeys.getUserKey(user.getId()));
		Map<byte[], byte[]> data = user.toMap();
		for (Map.Entry<byte[], byte[]> entry : data.entrySet())
		{
			put.add(COLUMN_FOAF, entry.getKey(), entry.getValue());
		}
		table.put(cp, put);
	}

	/**
	 * Removes the user with the id.
	 * @param cp collection provider
	 * @param id identity of the user to be removed
	 */
	public void removeUser(String cp, Long id)
	{
		Delete delete = new Delete(RowKeys.getUserKey(id)).deleteFamily(COLUMN_FOAF);
		table.delete(cp, delete);
	}

	/**
	 * Get a particular rating the id is: <tt>collection.user.item-id</tt>
	 * @param cp
	 *           collection provider
	 * @param collection
	 *           the collection
	 * @param userid
	 *           the user id
	 * @param item
	 *           id the item id
	 * @param id
	 * @return
	 */
	public Rating getRating(String cp, String collection, long userid, long itemid)
	{
		Get get = new Get(RowKeys.getRatingKey(collection, userid, itemid)).addFamily(COLUMN_RATING);
		Result result = table.get(cp, get);
		if (!result.isEmpty())
		{
			NavigableMap<byte[], byte[]> map = result.getFamilyMap(COLUMN_RATING);
			return new Rating(map);
		}

		return null;
	}

	/**
	 * a single day in milliseconds
	 */
	private final long DAY = 1000 * 60 * 60 * 24;

	public List<Rating> getRatings(String cp, String collection, long user, Long startdate, Long enddate, int size)
	{
		long epoch = getEpochFor(cp);

		// date range:
		long end, start;
		if (enddate == null)
		{
			// no end date - get data until now:
			end = System.currentTimeMillis();
		}
		else
		{
			// fixed end date
			end = enddate.longValue();
		}

		// set initial start:
		if (startdate == null)
		{
			// open start date - first try one day
			start = end - DAY;
		}
		else
		{
			// fixed start date, but gather in steps:
			start = Math.max(startdate.longValue(), end - DAY);
		}

		if (start < epoch)
		{
			LOG.warn("will not process date <{} !! date was set to {}", epoch, start);
		}

		// we cannot compare < 0 values:
		start = Math.max(epoch, start);

		// Never try to get more ratings than available:
		long ratings = getRatingCount(cp, user);
		size = Math.min(size, (int) ratings);

		LOG.info("ratings: size={} start={} end={}", new Object[]{size, new Date(start), new Date(end)});
		List<Rating> result = getRatings(cp, collection, user, start, end);

		final DateComparator compare = new DateComparator();
		long days = 2;
		long years = 1;

		while (result.size() < size && start > epoch)
		{
			end = start;

			// go back in time but not before start date if there is one:
			start = startdate != null ? Math.max(startdate.longValue(), end - ((DAY * days) * years)) : end
					- ((DAY * days) * years);

			// never gow < 0

			start = Math.max(epoch, start);

			List<Rating> moredata = getRatings(cp, collection, user, start, end);

			// go back in time, if not enough data:

			if (moredata.size() == 0)
			{
				if (days < 365)
				{
					days++;
				}
				else
				{
					if (years > 100)
					{
						LOG.debug("giving up not enough data between <{},{}>", start, enddate);
						break;
					}
					years++;
				}
			}

			// add data to end result:
			result.addAll(moredata);

			if (startdate != null && start <= startdate.longValue())
			{
				LOG.debug("reached earliest timestamp, done size not reached <{},{}>", start, enddate);
				break;
			}

		}
		Collections.sort(result, compare);
		if (result.size() > size)
		{
			// get a sub list
			return result.subList(0, size);
		}
		return result;
	}

	public List<Rating> getRatings(String cp, String collection, long user, Double lowRating, Double highRating,
			int size, Long startItem)
	{

		final FilterList filterlist = new FilterList();

		SingleColumnValueFilter userFilter = new SingleColumnValueFilter(COLUMN_RATING, ModelConstants.USER,
				CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(user)));
		userFilter.setFilterIfMissing(true);
		filterlist.addFilter(userFilter);

		if (lowRating != null)
		{
			SingleColumnValueFilter lowerValue = new SingleColumnValueFilter(COLUMN_RATING, ModelConstants.RATING,
					CompareOp.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes(lowRating.doubleValue())));
			lowerValue.setFilterIfMissing(true);
			filterlist.addFilter(lowerValue);
		}

		if (highRating != null)
		{
			SingleColumnValueFilter upperValue = new SingleColumnValueFilter(COLUMN_RATING, ModelConstants.RATING,
					CompareOp.LESS, new BinaryComparator(Bytes.toBytes(highRating.doubleValue())));
			upperValue.setFilterIfMissing(true);
			filterlist.addFilter(upperValue);
		}

		Scan scan = new Scan().addFamily(COLUMN_RATING).setFilter(filterlist);
		if (startItem != null)
		{
			scan.setStartRow(RowKeys.getRatingKey(collection, user, startItem));
		}

		final List<Rating> result = new ArrayList<Rating>(size);

		int cnt = 0;
		try
		{
			HTableInterface tableInterface = table.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					Rating userRating = new Rating(rr.getFamilyMap(COLUMN_RATING));
					userRating.setCp(cp);
					result.add(userRating);
					cnt++;

					if (cnt >= size)
					{
						// stop when size limit reached
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
			LOG.error("failed to get scanner for request", e);
		}

		return result;
	}

	public void putRating(String cp, String collection, Rating rating, TimeUnit unit)
	{
		if (rating.getDate() == null)
		{
			rating.setDate(System.currentTimeMillis());
		}
		else
		{
			if (TimeUnit.SECONDS == unit)
			{
				rating.setDate(rating.getDate() * 1000);
			}
			// else: already milliseconds, so ignore.
		}
		putRating(cp, collection, rating);
	}

	/**
	 * Put a particular rating the id is: <tt>recommender.user.item-id</tt>
	 * @param cp
	 *           content provider
	 * @param collection
	 *           collection name
	 * @param userid
	 *           user id
	 * @param itemid
	 *           item id
	 * @param rating
	 *           a rating object
	 */
	private void putRating(String cp, String collection, Rating rating)
	{
		Put put = new Put(RowKeys.getRatingKey(collection, rating.getUser(), rating.getItem()));
		Map<byte[], byte[]> data = rating.toMap();
		for (Map.Entry<byte[], byte[]> entry : data.entrySet())
		{
			put.add(COLUMN_RATING, entry.getKey(), entry.getValue());
		}
		put.add(COLUMN_FOAF, ModelConstants.LAST_RECOMMENDED_DATE, Bytes.toBytes(System.currentTimeMillis() / 1000));

		// count ratings:
		try
		{
			table.getTable(cp).incrementColumnValue(RowKeys.getUserKey(rating.getUser()), COLUMN_FOAF,
					ModelConstants.COUNT, 1L);

		}
		catch (IOException e)
		{
			LOG.error("increment rating count failed for {}", rating.getUser());
		}

		table.put(cp, put);
	}

	private void decRating(String cp, String collection, long user, long amount)
	{
		try
		{
			table.getTable(cp).incrementColumnValue(RowKeys.getUserKey(user), COLUMN_FOAF, ModelConstants.COUNT, 1L);
		}
		catch (IOException e)
		{
			LOG.error("increment rating count failed for {}", user);
		}

	}

	/**
	 * increase the number of views for the given {@code user} and {@code cp}
	 * @param cp content provider
	 * @param user user id
	 */
	public void incView(String cp, long user)
	{
		try
		{
			table.getTable(cp).incrementColumnValue(RowKeys.getUserKey(user), COLUMN_FOAF, ModelConstants.VIEW_COUNT, 1L);
			table.put(
					cp,
					new Put(RowKeys.getUserKey(user)).add(COLUMN_FOAF, ModelConstants.LAST_VIEW_DATE,
							Bytes.toBytes(System.currentTimeMillis() / 1000)));
		}
		catch (IOException e)
		{
			LOG.error("increment rating count failed for {}", user);
		}
	}

	public void setEpochFor(String cp, long time)
	{
		try
		{
			table.getTable(cp).put(new Put(EPOCH).add(COLUMN_RATING, EPOCH, Bytes.toBytes(time)));
		}
		catch (IOException e)
		{
			LOG.error("failed to write epoch for " + cp, e);
		}
	}

	public long getEpochFor(String cp)
	{
		try
		{
			Result result = table.getTable(cp).get(new Get(EPOCH).addFamily(COLUMN_RATING));
			KeyValue data = result.getColumnLatest(COLUMN_FOAF, EPOCH);
			if (data != null)
			{
				return Bytes.toLong(data.getValue());
			}
			else
			{
				// try calculating...
				long epoch = calculateEpoch(cp);
				setEpochFor(cp, epoch);
				return epoch;
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to get epoch for " + cp, e);
		}
		return 0L;
	}

	/**
	 * decrease the number of views for the given {@code user} and {@code cp}
	 * @param cp content provider
	 * @param user user id
	 */
	public void decView(String cp, long user)
	{
		try
		{
			table.getTable(cp).incrementColumnValue(RowKeys.getUserKey(user), COLUMN_FOAF, ModelConstants.VIEW_COUNT, -1L);
		}
		catch (IOException e)
		{
			LOG.error("increment rating count failed for {}", user);
		}
	}

	/**
	 * decrease the number of views for the given {@code user} and {@code cp}
	 * @param cp content provider
	 * @param user user id
	 */
	public void decView(String cp, long user, long count)
	{
		try
		{
			table.getTable(cp).incrementColumnValue(RowKeys.getUserKey(user), COLUMN_FOAF, ModelConstants.VIEW_COUNT,
					-count);
		}
		catch (IOException e)
		{
			LOG.error("increment rating count failed for {}", user);
		}
	}

	public void resetViews(String cp, long user)
	{
		try
		{

			Result old = table.getTable(cp).get(new Get(RowKeys.getUserKey(user)).addFamily(COLUMN_FOAF));
			if (!old.isEmpty())
			{
				byte[] count = old.getValue(COLUMN_FOAF, ModelConstants.VIEW_COUNT);
				if (count != null)
				{
					table.getTable(cp).incrementColumnValue(RowKeys.getUserKey(user), COLUMN_FOAF,
							ModelConstants.VIEW_COUNT, -Bytes.toLong(count));
				}
			}
		}
		catch (IOException e)
		{
			LOG.error("increment rating count failed for {}", user);
		}
	}

	public void resetRatingCount(String cp, long user)
	{
		try
		{
			long count = getRatingCount(cp, user);
			table.getTable(cp).incrementColumnValue(RowKeys.getUserKey(user), COLUMN_FOAF, ModelConstants.COUNT, -count);

		}
		catch (IOException e)
		{
			LOG.error("increment rating count failed for {}", user);
		}
	}

	/**
	 * increase the number of recommended items for the given {@code user} and {@code cp}
	 * @param cp content provider
	 * @param user user id
	 */
	public void incRecommends(String cp, long user)
	{
		try
		{
			table.getTable(cp).incrementColumnValue(RowKeys.getUserKey(user), COLUMN_FOAF, ModelConstants.RECOMMEND_COUNT,
					1L);
			table.put(
					cp,
					new Put(RowKeys.getUserKey(user)).add(COLUMN_FOAF, ModelConstants.LAST_RECOMMENDED_DATE,
							Bytes.toBytes(System.currentTimeMillis() / 1000)));
		}
		catch (IOException e)
		{
			LOG.error("increment rating count failed for {}", user);
		}
	}

	/**
	 * decrease the number of recommended items for the given {@code useR} and {@code cp}
	 * @param cp content provider
	 * @param user user id
	 */
	public void decRecommends(String cp, long user)
	{
		try
		{
			table.getTable(cp).incrementColumnValue(RowKeys.getUserKey(user), COLUMN_FOAF, ModelConstants.RECOMMEND_COUNT,
					-1L);
		}
		catch (IOException e)
		{
			LOG.error("increment rating count failed for {}", user);
		}
	}

	/**
	 * Delete a rating
	 * @param cp content provider
	 * @param collection collection name
	 * @param user user id
	 * @param item item id
	 */
	public void deleteRating(String cp, String collection, Long user, Long item)
	{
		Delete delete = new Delete(RowKeys.getRatingKey(collection, user, item));
		table.delete(cp, delete);

		try
		{
			table.getTable(cp).incrementColumnValue(RowKeys.getUserKey(user), COLUMN_FOAF, ModelConstants.COUNT, -1L);
		}
		catch (IOException e)
		{
			LOG.error("increment rating count failed for {}", user);
		}

	}

	/**
	 * delete all ratings for a given {@code user} in a {@code collection}
	 * @param cp content provider
	 * @param collection collection
	 * @param user user id
	 */
	public void deleteRatings(String cp, String collection, long user)
	{
		RowFilter filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRatingKey(collection,
				user)));
		Scan scan = new Scan().addFamily(COLUMN_RATING).setFilter(filter);
		long count = deleteAll(scan, cp, COLUMN_RATING);
		decRating(cp, collection, user, count);
	}

	/**
	 * delete all ratings for a given collection
	 * @param cp
	 * @param collection
	 */
	public void deleteRatings(String cp, String collection)
	{
		FilterList filters = new FilterList();
		// create a filter:

		filters.addFilter(new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(COLUMN_RATING)));
		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRatingKey(collection))));

		Scan scan = new Scan().addFamily(COLUMN_RATING).setFilter(filters);
		deleteAll(scan, cp, COLUMN_RATING);

		// TODO hm... this will fuck up the rating count of a user... & needs a fix - track the users... do a batch
		// decrease on them or something
	}

	/**
	 * Reset all ratings counts - this can - depending on the amount of users in the {@code cp} take quite some time!
	 * @param cp content provider
	 */
	public void updateCounts(String cp)
	{
		// reset all...
		Scan scan = new Scan().addFamily(COLUMN_FOAF);
		long count = 0;
		try
		{

			HTableInterface tableInterface = table.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				// set all user counts to 0
				Result r = null;
				List<Put> putBuffer = new ArrayList<Put>();
				while ((r = scanner.next()) != null)
				{
					byte[] userArr = r.getValue(COLUMN_FOAF, ModelConstants.USER);
					if (userArr != null)
					{
						long user = Bytes.toLong(userArr);
						putBuffer.add(new Put(RowKeys.getUserKey(user)).add(COLUMN_FOAF, ModelConstants.COUNT,
								Bytes.toBytes(0L)));
						count++;
					}
					else
					{
						LOG.warn("no user id, trying row-id", Bytes.toStringBinary(r.getRow()));
						Object[] rowParts = RowKeys.getStrLngRowKey(r.getRow());
						long user = ((Long) rowParts[1]).longValue();
						putBuffer.add(new Put(RowKeys.getUserKey(user)).add(COLUMN_FOAF, ModelConstants.COUNT,
								Bytes.toBytes(0L)));
						count++;
					}

					// flush intermediate:
					if (putBuffer.size() > 0 && putBuffer.size() % 1000 == 0)
					{
						table.put(cp, putBuffer);
						putBuffer.clear();
						LOG.info("intermediate update: {} items", count);
					}
				}
				// flush
				if (putBuffer.size() > 0)
				{
					table.put(cp, putBuffer);
				}
				LOG.info("Rating reset to 0 for {} users", count);
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to update counts", e);
		}

		// count all user ratings, set value..
		scan = new Scan().addFamily(COLUMN_RATING);
		count = 0;
		try
		{
			HTableInterface tableInterface = table.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				Result r = null;
				LOG.info("recounting ratings for all users, this might take some time");
				while ((r = scanner.next()) != null)
				{
					byte[] userArr = r.getValue(COLUMN_RATING, ModelConstants.USER);
					if (userArr != null)
					{
						long user = Bytes.toLong(userArr);
						tableInterface.incrementColumnValue(RowKeys.getUserKey(user), COLUMN_FOAF, ModelConstants.COUNT, 1L);
						count++;
					}
					else
					{
						LOG.error("rating has no user id: {}", Bytes.toStringBinary(r.getRow()));
					}
				}
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}
			LOG.info("updated {} ratings for users", count);
		}
		catch (IOException e)
		{
			LOG.error("failed to update ", e);
		}
	}

	// public List<Rating> getRatings(String cp, String collection, long user, Long startItem, Long startdate, Long
	// enddate, int size)
	private List<Rating> getRatings(String cp, String collection, long user, long startdate, long enddate)
	{
		// get the (reverse date) sort ratings from the table.
		// get data between start/end date
		// sort results...

		LOG.debug("getting ratings: <{},{}> =  <{},{}>", new Object[]{new Date(startdate), new Date(enddate), startdate,
				enddate});

		final FilterList filter = new FilterList();

		// filter user:
		final SingleColumnValueFilter userFilter = new SingleColumnValueFilter(COLUMN_RATING, ModelConstants.USER,
				CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(user)));
		userFilter.setFilterIfMissing(true);
		filter.addFilter(userFilter);

		// set start date if given:
		final SingleColumnValueFilter startTime = new SingleColumnValueFilter(COLUMN_RATING, ModelConstants.TIMESTAMP,
				CompareOp.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes(startdate)));
		startTime.setFilterIfMissing(true);
		filter.addFilter(startTime);

		// set end date if given:
		final SingleColumnValueFilter endTime = new SingleColumnValueFilter(COLUMN_RATING, ModelConstants.TIMESTAMP,
				CompareOp.LESS, new BinaryComparator(Bytes.toBytes(enddate)));
		endTime.setFilterIfMissing(true);

		filter.addFilter(endTime);

		Scan scan = new Scan().addFamily(COLUMN_RATING).setFilter(filter)
				.setStartRow(RowKeys.getRatingKey(collection, user));
		scan.setCaching(1000);

		List<Rating> result = new ArrayList<Rating>();
		try
		{
			HTableInterface tableInterface = table.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				Result r = null;
				while ((r = scanner.next()) != null)
				{
					Rating rating = new Rating(r.getFamilyMap(COLUMN_RATING));
					rating.setCp(cp);
					rating.setCollection(collection);
					result.add(rating);
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
					KeyValue kv = rr.getColumnLatest(family, ModelConstants.USER);
					if (kv != null)
					{
						// the task id is stored as a string (like the whole taskconfig) - but we only need this particular
						// value: - if the parselong fails, there's something really wrong - a coding bug -
						result.add(new Delete(rr.getRow()));
					}
					else
					{
						LOG.error("This is a bug: some items are left behind, while cleaning: {}", rr);
					}

					// delete every thousand rows:
					if (result.size() > 0 && result.size() % 1000 == 0)
					{
						count += result.size();
						table.delete(cp, result);
						LOG.info("intermediate delete <{}> total now <{}>", result.size(), count);
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

	/**
	 * Compare dates from the rating and put newest first.
	 * <p />
	 * Project zieook-data<br />
	 * UserTable.java created 23 dec. 2011
	 * <p />
	 * Copyright, all rights reserved 2011 GridLine Amsterdam
	 * @author <a href="mailto:job@gridline.nl">Job</a>
	 * @version $Revision:$, $Date:$
	 */
	private static class DateComparator implements Comparator<Rating>
	{
		@Override
		public int compare(Rating o1, Rating o2)
		{

			// this handles past ratings, that do not(!) contain a date:
			if (o1.getDate() == null && o2.getDate() == null)
			{
				return 0;
			}

			if (o1.getDate() == null && o2.getDate() != null)
			{
				return -1;
			}

			if (o2.getDate() == null && o1.getDate() != null)
			{
				return 1;
			}

			// compare dates:
			if (o1.getDate().equals(o2.getDate()))
			{
				return 0;
			}

			if (o1.getDate() > o2.getDate())
			{
				return -1;
			}
			return 1;
		}
	}

	/**
	 * @param b
	 */
	private long calculateEpoch(String cp)
	{
		long currentEpoch = Long.MAX_VALUE;
		RowFilter filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRatingKey()));
		Scan scan = new Scan().addFamily(COLUMN_RATING).setFilter(filter);
		try
		{
			HTableInterface tableInterface = table.getTable(cp);
			scan.setCaching(1000);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{

				Result r = null;
				while ((r = scanner.next()) != null)
				{
					KeyValue stamp = r.getColumnLatest(COLUMN_RATING, ModelConstants.TIMESTAMP);
					if (stamp != null)
					{
						currentEpoch = Math.min(currentEpoch, Bytes.toLong(stamp.getValue()));
					}
					else
					{
						LOG.error("rating without timeamp, cannot be right... {}",
								new Rating(r.getFamilyMap(COLUMN_RATING)).toJSON());
					}
				}
				if (currentEpoch == Long.MAX_VALUE)
				{
					LOG.error("no data to determine epoch, setting it to 0");
					return 0;
				}

				return currentEpoch;
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

		return 0;
	}
}
