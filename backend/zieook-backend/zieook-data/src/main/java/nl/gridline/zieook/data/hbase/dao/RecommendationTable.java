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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;

import nl.gridline.zieook.data.hbase.model.HBaseRecommendationTable;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.model.Recommend;
import nl.gridline.zieook.model.Recommendations;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main recommendations table, this keeps the serialized version of the generated recommender
 * Every row in the table comprises <tt>recommender.id</tt> and has a max number of columns all packed in a single
 * family containing all recommendations. <br />
 * Depending on the type recommender, user based or item based the id is a user-id or an item-id.
 * See {@link nl.gridline.zieook.inx.movielens.hbase.RecommendationsImportMap} for writing this data into HBase
 * <p />
 * Project zieook-data<br />
 * RecommendationTable.java created 5 jan. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class RecommendationTable
{
	private static final Logger LOG = LoggerFactory.getLogger(RecommendationTable.class);

	private static final byte[] RECOMMENDATION_COLUMN = Bytes.toBytes(HBaseRecommendationTable.RECOMMENDATION_COLUMN);

	// public static final byte[] RECOMMENDER_META_COLUMN =
	// Bytes.toBytes(HBaseRecommendationTable.RECOMMENDER_META_COLUMN);

	public static final String NOSPREAD = "no-spread";
	public static final String RANDOM = "random";
	public static final String GAUSS = "gauss";

	public static final String[] TYPES = {NOSPREAD, RANDOM, GAUSS};

	private static final Random rnd = new Random();

	private final HBaseRecommendationTable table;

	public RecommendationTable(HBaseRecommendationTable table)
	{
		this.table = table;
	}

	/**
	 * return the recommendations for the given item of the given recommender from the given content provider
	 * @param cp content provider
	 * @param collection collection
	 * @param recommender the recommender
	 * @param item the item id to get a recommendation for
	 * @param size the number of recommendations to return
	 * @param spread
	 * @return a recommendations object
	 */

	public Recommendations getRecommendationsFor(String cp, String collection, String recommender, long id, int size,
			String spread)
	{
		// GET recommendations for this item:
		Get get = new Get(RowKeys.getRecommendationKey(collection, recommender, id)).addFamily(RECOMMENDATION_COLUMN);
		Result result = table.get(cp, get);

		if (!result.isEmpty())
		{
			NavigableMap<byte[], byte[]> map = result.getFamilyMap(RECOMMENDATION_COLUMN);
			List<Recommend> recommendations = toRecommendations(map, size, spread);
			return new Recommendations(id, cp, collection, recommender, size, recommendations);
		}

		return null;
	}

	/**
	 * Delete a single recommendation
	 * @param cp - content provider
	 * @param recommender - recommender
	 * @param item item identifier
	 */
	public void deleteRecommendationFor(String cp, String collection, String recommender, long item)
	{
		Delete delete = new Delete(RowKeys.getRecommendationKey(collection, recommender, item));
		table.delete(cp, delete);
	}

	/**
	 * Creates a recommendations object directly usable for the front end
	 * @param map
	 * @param size
	 * @return
	 */
	private List<Recommend> toRecommendations(NavigableMap<byte[], byte[]> map, int size, String spread)
	{
		List<Recommend> result = null;

		if (NOSPREAD.equals(spread))
		{
			result = new ArrayList<Recommend>(Math.min(map.size(), size));
			// rank | recommendeditem
			int length = 0;
			for (Map.Entry<byte[], byte[]> entry : map.entrySet())
			{
				result.add(new Recommend(entry.getValue()));
				if (length == size - 1)
				{
					break;
				}
				length++;
			}

		}
		else if (GAUSS.equals(spread))
		{
			// select size random items from the total set: use gauss distribution for the random order
			result = new ArrayList<Recommend>(Math.min(map.size(), size));
			List<Map.Entry<byte[], byte[]>> data = new ArrayList<Map.Entry<byte[], byte[]>>();
			data.addAll(map.entrySet());
			for (int i = 0; i < size; i++)
			{
				double gauss = Math.abs(rnd.nextGaussian() / 2);
				int index = Math.min(data.size() - 1, (int) Math.round(gauss * data.size()));
				LOG.info("index: {} - size {} - gauss = {}", new Object[]{index, data.size(), gauss});
				byte[] value = data.get(index).getValue();
				result.add(new Recommend(value));
				data.remove(index);
			}
		}
		else
		{// select size random items from the total set:
			result = new ArrayList<Recommend>(Math.min(map.size(), size));
			List<Map.Entry<byte[], byte[]>> data = new ArrayList<Map.Entry<byte[], byte[]>>();
			data.addAll(map.entrySet());
			for (int i = 0; i < size; i++)
			{
				int index = rnd.nextInt(data.size());
				byte[] value = data.get(index).getValue();
				result.add(new Recommend(value));
				data.remove(index);
			}

		}
		return result;
	}

}
