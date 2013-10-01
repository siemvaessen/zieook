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
package nl.gridline.zieook.inx.movielens.hbase;

import java.io.IOException;
import java.util.List;

import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.mapreduce.TaskConfig;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.mahout.cf.taste.hadoop.RecommendedItemsWritable;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.math.VarLongWritable;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * UserRecommendationsStoreMap.java created Mar 24, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class UserRecommendationsStoreMap extends Mapper<VarLongWritable, RecommendedItemsWritable, LongWritable, Put>
{

	private static final byte[] RECOMMENDATION_COLUMN = Bytes.toBytes(HBaseTableConstants.RECOMMENDATION_COLUMN);
	private String collection;
	private String recommender;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{
		collection = context.getConfiguration().get(TaskConfig.COLLECTION);
		recommender = context.getConfiguration().get(TaskConfig.RECOMMENDER);
		if (collection == null)
		{
			throw new IOException("collection is missing please set <" + TaskConfig.COLLECTION
					+ "> key in job configuration");
		}

		if (recommender == null)
		{
			throw new IOException("recommender is missing please set <" + TaskConfig.RECOMMENDER
					+ "> key in job configuration");
		}

	};

	protected void map(VarLongWritable key, RecommendedItemsWritable value, Context context) throws IOException,
			InterruptedException
	{
		// <UserId, [item,value]>
		// the in the recommendations are already sorted! - this makes writing them easy

		List<RecommendedItem> recommendations = value.getRecommendedItems();
		int rank = 1;
		Put put = new Put(RowKeys.getRecommendationKey(collection, recommender, key.get()));
		for (RecommendedItem el : recommendations)
		{
			byte[] data = nl.gridline.zieook.model.Recommend.getRecommendation(el.getItemID(), rank, el.getValue());
			put.add(RECOMMENDATION_COLUMN, Bytes.toBytes(rank), data);
			rank++;
		}
		context.write(new LongWritable(key.get()), put);

	};
}
