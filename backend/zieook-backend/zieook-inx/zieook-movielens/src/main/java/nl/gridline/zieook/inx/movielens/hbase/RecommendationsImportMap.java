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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import nl.gridline.zieook.inx.movielens.items.RecommendationElement;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.model.Recommend;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.VectorWritable;

/**
 * For reading this data into JAXB objects see {@link nl.gridline.zieook.data.hbase.dao.RecommendationTable}
 * <p />
 * Project zieook-movielens<br />
 * RecommendationsImportMap.java created 25 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class RecommendationsImportMap extends Mapper<IntWritable, VectorWritable, LongWritable, Put>
{

	// TODO check this MostSimilarItemPairsMapper class, the implementation does not look correct
	// fix, remove last step of ItemSimilarityJobZieOok and replace it with this storage step...

	private static final Log LOG = LogFactory.getLog(RecommendationsImportMap.class);

	// only store non-zero elements
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

	@Override
	public void map(IntWritable key, VectorWritable value, Context context) throws IOException, InterruptedException
	{

		Vector similarityMatrixRow = value.get();

		// Remove self similarity
		similarityMatrixRow.set(key.get(), Double.NEGATIVE_INFINITY); // from the equation

		// determine max non-zero element: (==item index)
		List<RecommendationElement> recommendations = new ArrayList<RecommendationElement>();

		// collect non-zero items:
		Iterator<Element> it = similarityMatrixRow.iterateNonZero();
		while (it.hasNext())
		{
			Element e = it.next();
			recommendations.add(new RecommendationElement(e.index(), e.get()));
			// LOG.info("created new recommendation for " + e.index());
		}

		// sorted list of recommendations: now we have an item id, and similarity value:
		Collections.sort(recommendations, new SimilarityComparator());
		LOG.info("sorted: " + recommendations.size());

		int rank = 1;
		Put put = new Put(RowKeys.getRecommendationKey(collection, recommender, key.get()));
		for (RecommendationElement el : recommendations)
		{
			// if (el.getSimilarityValue() > 0)
			// {
			byte[] data = Recommend.getRecommendation(el.getItemIndex(), rank, el.getSimilarityValue());
			put.add(RECOMMENDATION_COLUMN, Bytes.toBytes(rank), data);
			rank++;
			// }
		}
		context.write(new LongWritable(key.get()), put);

	}

	static class SimilarityComparator implements Comparator<RecommendationElement>
	{

		@Override
		public int compare(RecommendationElement recommendation1, RecommendationElement recommendation2)
		{
			final double epsilon = 0.0001;
			final double value1 = recommendation1.getSimilarityValue();
			final double value2 = recommendation2.getSimilarityValue();

			// return (s1.similarity == s2.similarity) ? 0 : (s1.similarity < s2.similarity) ? -1 : 1;

			if (Math.abs(value1 - value2) < epsilon)
			{
				return 0;
			}
			if (value1 > value2)
			{
				return -1;
			}
			return 1;
		}

	}
}
