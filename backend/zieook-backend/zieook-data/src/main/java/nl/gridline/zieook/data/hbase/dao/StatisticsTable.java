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
import java.util.List;
import java.util.TreeSet;

import nl.gridline.zieook.data.hbase.model.HBaseStatisticsTable;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.model.GroupedData;
import nl.gridline.zieook.model.ModelConstants;
import nl.gridline.zieook.model.Popularity;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * StatisticsTable.java created 27 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class StatisticsTable
{
	private static final Logger LOG = LoggerFactory.getLogger(StatisticsTable.class);

	private final HBaseStatisticsTable table;

	private final byte[] STATS_VIEWED_SOURCE = Bytes.toBytes(HBaseStatisticsTable.STATS_VIEWED_SOURCE);
	private final byte[] STATS_COLLECTION_SOURCE = Bytes.toBytes(HBaseStatisticsTable.STATS_COLLECTION_SOURCE);
	private final byte[] STATS_ITEM_RATINGS = Bytes.toBytes(HBaseTableConstants.STATS_ITEM_RATINGS);
	private final byte[] STATS_VIEWED_ITEM = Bytes.toBytes(HBaseTableConstants.STATS_VIEWED_ITEM);
	private final byte[] STATS_USER_RATINGS = Bytes.toBytes(HBaseTableConstants.STATS_USER_RATINGS);
	private final byte[] STATS_RECOMMENDED_USERITEM = Bytes.toBytes(HBaseTableConstants.STATS_RECOMMENDED_USERITEM);
	private final byte[] STATS_RECOMMENDED_USER = Bytes.toBytes(HBaseTableConstants.STATS_RECOMMENDED_USER);
	private final byte[] STATS_ITEM_POPULARITY = Bytes.toBytes(HBaseTableConstants.STATS_ITEM_POPULARITY);

	public StatisticsTable(HBaseStatisticsTable table)
	{
		this.table = table;
	}

	public List<GroupedData> getTopRecommended(String cp, String collection, long user, Long startDate, Long endDate,
			int size)
	{

		FilterList filters = new FilterList();
		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getStatRecommendedItemKey(
				collection, user))));
		setDateLimit(STATS_RECOMMENDED_USERITEM, startDate, endDate, filters);

		Scan scan = new Scan().addFamily(STATS_RECOMMENDED_USERITEM).setFilter(filters);

		return getSortedResults(cp, scan, STATS_RECOMMENDED_USERITEM, size);
	}

	public List<GroupedData> getRecommendedUserItem(String cp, String collection, long user, Long from, Long startDate,
			Long endDate, int size)
	{
		FilterList filters = new FilterList();

		if (from == null)
		{
			filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getStatRecommendedItemKey(
					collection, user))));
		}
		else
		{
			filters.addFilter(new RowFilter(CompareOp.GREATER_OR_EQUAL, new BinaryPrefixComparator(RowKeys
					.getStatRecommendedItemKey(collection, user, from))));
		}

		setDateLimit(STATS_RECOMMENDED_USERITEM, startDate, endDate, filters);

		Scan scan = new Scan().addFamily(STATS_RECOMMENDED_USERITEM).setFilter(filters);

		return getResults(cp, scan, STATS_RECOMMENDED_USERITEM, size);
	}

	public List<GroupedData> getRecommendedUser(String cp, String collection, long user, Long startDate, Long endDate,
			int size)
	{
		FilterList filters = new FilterList();

		filters.addFilter(new RowFilter(CompareOp.GREATER_OR_EQUAL, new BinaryPrefixComparator(RowKeys
				.getStatRecommendedKey(collection, user))));
		setDateLimit(STATS_RECOMMENDED_USER, startDate, endDate, filters);
		Scan scan = new Scan().addFamily(STATS_RECOMMENDED_USER).setFilter(filters);
		return getResults(cp, scan, STATS_RECOMMENDED_USER, size);
	}

	public List<GroupedData> getUserRated(String cp, String collection, Long from, Long startDate, Long endDate, int size)
	{
		// Put put = new Put(RowKeys.getStatRatingsPerItemKey(collection, item, date));
		Scan scan = new Scan();
		scan.addFamily(STATS_USER_RATINGS);
		FilterList filters = new FilterList();
		if (from == null)
		{
			filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys
					.getStatRatingsPerUserKey(collection))));
		}
		else
		{
			filters.addFilter(new RowFilter(CompareOp.GREATER_OR_EQUAL, new BinaryPrefixComparator(RowKeys
					.getStatRatingsPerUserKey(collection, from.longValue()))));
		}

		setDateLimit(STATS_USER_RATINGS, startDate, endDate, filters);

		scan.setFilter(filters);

		return getResults(cp, scan, STATS_USER_RATINGS, size);
	}

	public List<GroupedData> getItemRated(String cp, String collection, Long from, Long startDate, Long endDate, int size)
	{
		// Put put = new Put(RowKeys.getStatRatingsPerItemKey(collection, item, date));
		Scan scan = new Scan();
		scan.addFamily(STATS_ITEM_RATINGS);
		FilterList filters = new FilterList();
		if (from == null)
		{
			filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys
					.getStatRatingsPerItemKey(collection))));
		}
		else
		{
			filters.addFilter(new RowFilter(CompareOp.GREATER_OR_EQUAL, new BinaryPrefixComparator(RowKeys
					.getStatRatingsPerItemKey(collection, from.longValue()))));
		}

		setDateLimit(STATS_ITEM_RATINGS, startDate, endDate, filters);

		scan.setFilter(filters);

		return getResults(cp, scan, STATS_ITEM_RATINGS, size);
	}

	public List<GroupedData> getRatedTop(String cp, String collection, Long startDate, Long endDate, int size)
	{

		Scan scan = new Scan();
		scan.addFamily(STATS_ITEM_RATINGS);
		FilterList filters = new FilterList();

		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys
				.getStatRatingsPerItemKey(collection))));

		setDateLimit(STATS_ITEM_RATINGS, startDate, endDate, filters);

		scan.setFilter(filters);

		return getSortedResults(cp, scan, STATS_ITEM_RATINGS, size);
	}

	/**
	 * @param cp
	 * @param collection
	 * @param from
	 * @param startDate
	 * @param endDate
	 * @param size
	 * @return
	 */
	public List<GroupedData> getCollectionSources(String cp, String collection, String from, Long startDate,
			Long endDate, Integer size)
	{

		Scan scan = new Scan().addFamily(STATS_COLLECTION_SOURCE);

		FilterList filters = new FilterList();

		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys
				.getStatSourcesCollectionKey(collection))));

		if (from == null)
		{
			filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys
					.getStatSourcesCollectionKey(collection))));
		}
		else
		{
			filters.addFilter(new RowFilter(CompareOp.GREATER_OR_EQUAL, new BinaryPrefixComparator(RowKeys
					.getStatSourcesCollectionKey(collection, from))));
		}

		setDateLimit(STATS_COLLECTION_SOURCE, startDate, endDate, filters);

		scan.setFilter(filters);

		return getResults(cp, scan, STATS_COLLECTION_SOURCE, size);
	}

	/**
	 * @param cp
	 * @param collection
	 * @param startTime
	 * @param endTime
	 * @param size
	 * @return
	 */
	public List<GroupedData> getCollectionTopSources(String cp, String collection, Long startDate, Long endDate, int size)
	{
		Scan scan = new Scan().addFamily(STATS_COLLECTION_SOURCE);

		FilterList filters = new FilterList();

		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys
				.getStatSourcesCollectionKey(collection))));

		setDateLimit(STATS_COLLECTION_SOURCE, startDate, endDate, filters);

		scan.setFilter(filters);

		return getSortedResults(cp, scan, STATS_COLLECTION_SOURCE, size);
	}

	/**
	 * return the list of content providers
	 * @param cp
	 * @param recommender
	 * @return
	 * @throws IOException
	 * @see nl.gridline.zieook.statistics.SourcesByRecommenderMap
	 * @see nl.gridline.zieook.statistics.SourcesByRecommenderReduce
	 */
	public List<GroupedData> getSources(String cp, String recommender, String from, Long startDate, Long endDate,
			int size)
	{
		Scan scan = new Scan();
		scan.addFamily(STATS_VIEWED_SOURCE);
		FilterList filters = new FilterList();

		if (from == null)
		{
			filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys
					.getStatSourcesKey(recommender))));
		}
		else
		{
			filters.addFilter(new RowFilter(CompareOp.GREATER_OR_EQUAL, new BinaryPrefixComparator(RowKeys
					.getStatSourcesKey(recommender, from))));
		}

		setDateLimit(STATS_VIEWED_SOURCE, startDate, endDate, filters);

		scan.setFilter(filters);

		return getResults(cp, scan, STATS_VIEWED_SOURCE, size);
	}

	public List<GroupedData> getTopSources(String cp, String recommender, Long startDate, Long endDate, int size)
	{

		Scan scan = new Scan();
		scan.addFamily(STATS_VIEWED_SOURCE);

		FilterList filters = new FilterList();

		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys
				.getStatSourcesKey(recommender))));

		setDateLimit(STATS_VIEWED_SOURCE, startDate, endDate, filters);

		scan.setFilter(filters);

		// use a scanner, and add only if map.size < size

		return getSortedResults(cp, scan, STATS_VIEWED_SOURCE, size);
	}

	/**
	 * @param cp
	 * @param recommender
	 * @param startDate
	 * @param endDate
	 * @param size
	 * @return
	 */
	public List<GroupedData> getTopViewed(String cp, String recommender, Long startDate, Long endDate, int size)
	{
		Scan scan = new Scan();
		scan.addFamily(STATS_VIEWED_ITEM);

		FilterList filters = new FilterList();

		filters.addFilter(new RowFilter(CompareOp.EQUAL,
				new BinaryPrefixComparator(RowKeys.getStatViewedKey(recommender))));

		setDateLimit(STATS_VIEWED_ITEM, startDate, endDate, filters);

		scan.setFilter(filters);

		// use a scanner, and add only if map.size < size

		return getSortedResults(cp, scan, STATS_VIEWED_ITEM, size);
	}

	public List<GroupedData> getViewed(String cp, String recommender, Long from, Long startDate, Long endDate, int size)
	{
		Scan scan = new Scan();
		scan.addFamily(STATS_VIEWED_ITEM);

		FilterList filters = new FilterList();

		if (from == null)
		{
			filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys
					.getStatViewedKey(recommender))));
		}
		else
		{
			filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getStatViewedKey(
					recommender, from.longValue()))));
		}

		setDateLimit(STATS_VIEWED_ITEM, startDate, endDate, filters);

		scan.setFilter(filters);

		return getResults(cp, scan, STATS_VIEWED_ITEM, size);
	}

	public Popularity getPopularity(String cp, String collection, String name, long item)
	{
		Get get = new Get(RowKeys.getStatsPopularity(name, collection, item)).addFamily(STATS_ITEM_POPULARITY);
		Result result = table.get(cp, get);
		if (!result.isEmpty())
		{
			return new Popularity(result.getFamilyMap(STATS_ITEM_POPULARITY));
		}
		return null;
	}

	/**
	 * @param cp
	 * @param collection
	 * @param type
	 * @return
	 */
	public Popularity getMostPopular(String cp, String collection, String type)
	{
		// TODO this is the very dirty version of the implementation
		// create a proper scan, or use a fi

		List<Popularity> list = getPopularity(cp, collection, type, null, 1000);
		if (list == null || list.size() == 0)
		{
			return null;
		}

		Popularity result = list.get(0);
		for (Popularity p : list)
		{
			if (p.getPopularity() > result.getPopularity())
			{
				result = p;
			}
		}
		return result;
	}

	public List<Popularity> getPopularity(String cp, String collection, String name, Long startItem, int size)
	{
		// create a scanner / filter:
		Scan scan;
		if (startItem != null)
		{
			scan = new Scan(RowKeys.getStatsPopularity(name, collection, startItem.longValue()));
		}
		else
		{
			scan = new Scan();
		}
		scan.setFilter(
				new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getStatsPopularity(name, collection))))
				.addFamily(STATS_ITEM_POPULARITY);

		// list data:
		List<Popularity> result = new ArrayList<Popularity>();
		try
		{
			HTableInterface tableInterface = table.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				Result[] scanResult = scanner.next(size);
				for (Result r : scanResult)
				{
					result.add(new Popularity(r.getFamilyMap(STATS_ITEM_POPULARITY)));
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
			return null;
		}

		return result;

	}

	private void setDateLimit(final byte[] family, final Long startDate, final Long endDate, final FilterList filters)
	{
		if (startDate != null)
		{
			final SingleColumnValueFilter startTime = new SingleColumnValueFilter(family, ModelConstants.TIMESTAMP,
					CompareOp.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes(startDate.longValue())));
			startTime.setFilterIfMissing(true);
			filters.addFilter(startTime);
		}
		// set end date
		if (endDate != null)
		{
			final SingleColumnValueFilter startTime = new SingleColumnValueFilter(family, ModelConstants.TIMESTAMP,
					CompareOp.LESS, new BinaryComparator(Bytes.toBytes(endDate.longValue())));
			startTime.setFilterIfMissing(true);
			filters.addFilter(startTime);
		}
	}

	private List<GroupedData> getResults(final String cp, final Scan scan, final byte[] family, final int size)
	{
		List<GroupedData> result = new ArrayList<GroupedData>();
		try
		{
			HTableInterface tableInterface = table.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				Result[] scanResult = scanner.next(size);
				for (Result r : scanResult)
				{
					result.add(new GroupedData(r.getFamilyMap(family)));
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
			return null;
		}

		return result;
	}

	private List<GroupedData> getSortedResults(final String cp, final Scan scan, byte[] family, final int size)
	{
		TreeSet<GroupedData> result = getSortedSet();
		try
		{
			HTableInterface tableInterface = table.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				Result scanResult;
				while ((scanResult = scanner.next()) != null)
				{
					// TODO: check for double items id's - they should be skipped, somehow.
					GroupedData data = new GroupedData(scanResult.getFamilyMap(family));
					LOG.info("adding: {}", data);
					boolean old = result.add(data);
					if (!old)
					{
						LOG.debug("item was already in the set");
					}

					if (result.size() > size)
					{
						result.remove(result.last());
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
			return null;
		}
		return new ArrayList<GroupedData>(result);
	}

	private TreeSet<GroupedData> getSortedSet()
	{
		return new TreeSet<GroupedData>();
	}

}
