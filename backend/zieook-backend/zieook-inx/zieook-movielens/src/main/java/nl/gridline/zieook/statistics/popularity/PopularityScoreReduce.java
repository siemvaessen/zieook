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
package nl.gridline.zieook.statistics.popularity;

import java.io.IOException;
import java.util.Map;

import nl.gridline.zieook.mapreduce.HBasePUTFactory;
import nl.gridline.zieook.statistics.popularity.io.ItemTime;
import nl.gridline.zieook.statistics.popularity.io.PopularityWritable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * PopularityScoreReduce.java created 13 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class PopularityScoreReduce extends TableReducer<ItemTime, PopularityWritable, ItemTime>
{
	private static final Log LOG = LogFactory.getLog(PopularityScoreReduce.class);

	private Map<Long, String> types;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{

		String types = context.getConfiguration().get(PopularityStatistics.TYPES);
		if (types == null)
		{
			throw new IOException("failed to read <" + PopularityStatistics.TYPES + "> please set the configuration item");
		}

		this.types = PopularityStatistics.getTypes(types);
		LOG.info("types: '" + types + "' split into " + this.types.size() + " items");
		for (Map.Entry<Long, String> entry : this.types.entrySet())
		{
			LOG.info("<" + entry.getValue() + "," + entry.getKey() + ">");
		}
	};

	@Override
	protected void reduce(ItemTime key, Iterable<PopularityWritable> values, Context context) throws IOException,
			InterruptedException
	{
		// put itemtime w/ popularity writable in a Put object and write...

		for (PopularityWritable p : values)
		{
			String name = types.get(key.getTime());
			int popularity = (int) Math.round(p.getPercentage());

			// LOG.info("writing: " + key.getCollection() + "/" + key.getItem() + " " + name + " " + popularity);

			Put put = HBasePUTFactory.putStatisticsPopularity(key.getCollection(), key.getItem(), name, key.getTime(),
					popularity, p.getCount());

			context.write(key, put);
		}

	};
}
