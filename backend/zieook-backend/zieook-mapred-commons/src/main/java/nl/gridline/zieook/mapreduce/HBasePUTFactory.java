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
package nl.gridline.zieook.mapreduce;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import nl.gridline.zieook.model.CollectionItem;
import nl.gridline.zieook.model.GroupedData;
import nl.gridline.zieook.model.Popularity;
import nl.gridline.zieook.model.Rating;
import nl.gridline.zieook.model.User;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * All PUT operations that are both used in Map-Reduce and in the framework are collected here as static factory methods
 * <p />
 * Project zieook-mapred-commons<br />
 * CollectionMap.java created 17 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class HBasePUTFactory
{

	private static final byte[] QUALIFIER_RAW = Bytes.toBytes(HBaseTableConstants.COLLECTION_QUALIFIER_RAW);
	private static final byte[] COLLECTION_COLUMN_RAW = Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_RAW);
	private static final byte[] COLLECTION_COLUMN_INTR = Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR);
	private static final byte[] USER_FOAF = Bytes.toBytes(HBaseTableConstants.USERTABLE_COLUMN_FOAF);
	private static final byte[] USERTABLE_COLUMN_RATING = Bytes.toBytes(HBaseTableConstants.USERTABLE_COLUMN_RATING);

	// statistics columns:
	private static final byte[] STATS_VIEWED_SOURCE = Bytes.toBytes(HBaseTableConstants.STATS_VIEWED_SOURCE);
	private static final byte[] STATS_VIEWED_ITEM = Bytes.toBytes(HBaseTableConstants.STATS_VIEWED_ITEM);
	private static final byte[] STATS_ITEM_RATINGS = Bytes.toBytes(HBaseTableConstants.STATS_ITEM_RATINGS);
	private static final byte[] STATS_USER_RATINGS = Bytes.toBytes(HBaseTableConstants.STATS_USER_RATINGS);
	private static final byte[] STATS_RECOMMENDED_USERITEM = Bytes
			.toBytes(HBaseTableConstants.STATS_RECOMMENDED_USERITEM);
	private static final byte[] STATS_RECOMMENDED_USER = Bytes.toBytes(HBaseTableConstants.STATS_RECOMMENDED_USER);
	private static final byte[] STATS_ITEM_POPULARITY = Bytes.toBytes(HBaseTableConstants.STATS_ITEM_POPULARITY);

	private static Put putAll(Put put, byte[] family, Map<byte[], byte[]> map)
	{
		for (Map.Entry<byte[], byte[]> entry : map.entrySet())
		{
			put.add(family, entry.getKey(), entry.getValue());
		}
		return put;
	}

	/**
	 * put source count per recommender and date
	 * @param recommender
	 * @param source
	 * @param date
	 * @param count
	 * @return
	 * @throws IOException - whenever recommender or source cannot be url encoded
	 */
	public static Put putStatisticsSources(String recommender, String source, long date, long count) throws IOException
	{
		Put put = new Put(RowKeys.getStatSourcesKey(recommender, source, date));;
		return putAll(put, STATS_VIEWED_SOURCE,
				new GroupedData(null, recommender, source, null, null, count, date).toMap());
	}

	public static Put putStatisticsItemViews(String recommender, Long item, long date, long count)
	{
		Put put = new Put(RowKeys.getStatViewedKey(recommender, item, date));
		return putAll(put, STATS_VIEWED_ITEM, new GroupedData(null, recommender, null, item, null, count, date).toMap());
	}

	/**
	 * put count and date per collection/item
	 * @param collection
	 * @param item
	 * @param date
	 * @param count
	 * @return a put object
	 */
	public static Put putStatisticsItemRatings(String collection, long item, long date, long count)
	{
		Put put = new Put(RowKeys.getStatRatingsPerItemKey(collection, item, date));
		return putAll(put, STATS_ITEM_RATINGS, new GroupedData(collection, null, null, item, null, count, date).toMap());
	}

	/**
	 * put count and date per collection/item
	 * @param collection
	 * @param item
	 * @param date
	 * @param count
	 * @return a put object
	 */
	public static Put putStatisticsRatingsUser(String collection, long user, long date, long count)
	{
		Put put = new Put(RowKeys.getStatRatingsPerUserKey(collection, user, date));
		return putAll(put, STATS_USER_RATINGS, new GroupedData(collection, null, null, null, user, count, date).toMap());
	}

	public static Put putStatisticsPopularity(String collection, long item, String name, long date, int popularity,
			long absolute)
	{
		Put put = new Put(RowKeys.getStatsPopularity(name, collection, item));
		return putAll(put, STATS_ITEM_POPULARITY, new Popularity(item, popularity, absolute, date, name).toMap());
	}

	/**
	 * @param user
	 * @param recommender
	 * @param item
	 * @param count
	 * @param date
	 * @return
	 * @throws IOException
	 */
	public static Put putRecommendedUser(String collection, long user, long item, long count, long date)
			throws IOException
	{

		Put put = new Put(RowKeys.getStatRecommendedItemKey(collection, user, item, date));
		return putAll(put, STATS_RECOMMENDED_USERITEM,
				new GroupedData(collection, null, null, item, user, count, date).toMap());
	}

	public static Put putRecommendedCollectionUser(String collection, long user, long count, long date)
	{
		Put put = new Put(RowKeys.getStatRecommendedKey(collection, user, date));
		return putAll(put, STATS_RECOMMENDED_USER,
				new GroupedData(collection, null, null, null, user, count, date).toMap());
	}

	public static Put getRatingPut(String collection, String userStr, String itemStr, String ratingStr, String dateStr,
			String source)
	{
		long user = Long.parseLong(userStr);
		long item = Long.parseLong(itemStr);
		double rating = Double.parseDouble(ratingStr);
		long date = Long.parseLong(dateStr);

		Put put = new Put(RowKeys.getRatingKey(collection, user, item));
		return putAll(put, USERTABLE_COLUMN_RATING, new Rating(user, item, collection, source, date, rating).toMap());
	}

	/**
	 * Return a Put object a row item
	 * @param row
	 * @param object
	 * @return
	 */
	public static Put getCollectionItemRaw(String collection, long item, String object)
	{
		Put put = new Put(RowKeys.getCollectionKey(collection, item));
		put.add(COLLECTION_COLUMN_RAW, QUALIFIER_RAW, Bytes.toBytes(object));
		return put;
	}

	public static Put getCollectionItemIntr(String collection, long item, CollectionItem data)
	{
		Put put = new Put(RowKeys.getCollectionKey(collection, item));
		return putAll(put, COLLECTION_COLUMN_INTR, data.toMap());
	}

	public static Put getCollectionItemCompl(String collection, long item, String object, CollectionItem data)
	{
		Put put = new Put(RowKeys.getCollectionKey(collection, item));
		put.add(COLLECTION_COLUMN_RAW, QUALIFIER_RAW, Bytes.toBytes(object));
		return putAll(put, COLLECTION_COLUMN_INTR, data.toMap());
	}

	public static Put getCollectionItemCompl(String cp, String collection, String itemStr, String title,
			List<String> categories, List<String> keywords, String object, String location, String image)
	{
		long item = Long.parseLong(itemStr);
		return getCollectionItemCompl(collection, item, object, new CollectionItem(item, itemStr, cp, categories, title,
				null, null, keywords, location, image));
	}

	public static Put getUser(String user, String name)
	{
		return getUser(Long.parseLong(user), name);
	}

	public static Put getUser(long user, String name)
	{
		Put put = new Put(RowKeys.getUserKey(user));
		return putAll(put, USER_FOAF, new User(user, name, null).toMap());
	}

}
