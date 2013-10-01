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
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.model.Recommend;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * RecommendationsExportMap.java created 14 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class RecommendationsExportMap extends TableMapper<ImmutableBytesWritable, Text>
{
	private static final byte[] RECOMMENDATION_COLUMN = Bytes.toBytes(HBaseTableConstants.RECOMMENDATION_COLUMN);

	private final Text text = new Text();

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException,
			InterruptedException
	{
		if (!value.isEmpty())
		{
			NavigableMap<byte[], byte[]> map = value.getFamilyMap(RECOMMENDATION_COLUMN);
			List<Recommend> recommendations = new ArrayList<Recommend>(map.size());
			// rank | recommendeditem
			for (Map.Entry<byte[], byte[]> entry : map.entrySet())
			{
				recommendations.add(new Recommend(entry.getValue()));
			}

			StringBuilder b = new StringBuilder();
			for (Recommend i : recommendations)
			{
				b.append(i.toJSON()).append(" ");
			}
			text.set(b.toString());
			context.write(key, text);
		}
	};
}
