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
package nl.gridline.zieook.inx.movielens.items;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.mahout.math.VarIntWritable;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.VectorWritable;

/**
 * maps a row of the similarity matrix to an sorted array of recommendations
 * input row is stored in a sparse vector
 * first, maximum index occurring in the sparse vector is determined
 * (quite often this maximum index is the same for all row vectors)
 * next, store sparse row vector in recommendations array of sufficient length
 * then, sort the recommendations array on the basis of recommendation similarity values
 * output sorted data as recommendations array
 * <p />
 * Project zieook-movielens<br />
 * ItemBased_SortSimilaritiesMapper.java created 4 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:gerlof@gridline.nl">Gerlof</a>
 * @version $Revision:$, $Date:$
 */
public final class ItemBasedSortSimilaritiesMapper
		extends
			Mapper<IntWritable, VectorWritable, VarIntWritable, RecommendationElementArray>
{

	@Override
	protected void map(IntWritable key, VectorWritable value, Context context) throws IOException, InterruptedException
	{
		int maxIndex = -1;

		Vector similarityMatrixRow = value.get();
		/* remove self similarity */

		similarityMatrixRow.set(key.get(), Double.NEGATIVE_INFINITY);

		//
		// determine maximum index
		//
		Iterator<Element> it = similarityMatrixRow.iterateNonZero();

		while (it.hasNext())
		{
			Element e = it.next();

			// e.index() // == item id

			if (e.index() > maxIndex)
			{
				maxIndex = e.index();
			}
		}

		// System.out.println(String.format("key: %d maxIndex: %d", key.get(), maxIndex));

		if (maxIndex > 0)
		{

			RecommendationElement[] itemBasedRecommendations = new RecommendationElement[maxIndex];

			for (int i = 0; i < maxIndex; i++)
			{
				Element element = similarityMatrixRow.getElement(i);

				double similarityValue = Double.NEGATIVE_INFINITY;

				if (element != null)
				{
					similarityValue = element.get();
				}

				itemBasedRecommendations[i] = new RecommendationElement(i, similarityValue);
			}

			Arrays.sort(itemBasedRecommendations, new SimilarityComparator());

			RecommendationElementArray array = new RecommendationElementArray(itemBasedRecommendations);

			context.write(new VarIntWritable(key.get()), array);

		}
	}

	private static class SimilarityComparator implements Comparator<RecommendationElement>
	{

		@Override
		public int compare(RecommendationElement recommendation1, RecommendationElement recommendation2)
		{
			final double epsilon = 0.0001;
			final double value1 = recommendation1.getSimilarityValue();
			final double value2 = recommendation2.getSimilarityValue();

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
