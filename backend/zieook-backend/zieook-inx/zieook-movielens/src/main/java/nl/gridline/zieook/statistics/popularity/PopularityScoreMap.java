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

import nl.gridline.zieook.statistics.popularity.io.ItemTime;
import nl.gridline.zieook.statistics.popularity.io.PopularityWritable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * PopularityScoreMap.java created 13 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class PopularityScoreMap extends Mapper<ItemTime, LongWritable, ItemTime, PopularityWritable>
{

	private static final Log LOG = LogFactory.getLog(PopularityScoreMap.class);

	private Map<Long, Long> types;
	private final PopularityWritable popularity = new PopularityWritable();

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{

		// get the map of time (types) and

		String types = context.getConfiguration().get(PopularityStatistics.TYPES_COUNT);
		this.types = PopularityStatistics.getTypeCount(types);
		for (Map.Entry<Long, Long> entry : this.types.entrySet())
		{
			LOG.info("data <type,count>: <" + entry.getKey() + "," + entry.getValue() + ">");
		}
	};

	@Override
	protected void map(ItemTime key, LongWritable value, Context context) throws IOException, InterruptedException
	{
		Long total = types.get(key.getTime());
		long count = value.get();
		if (total == null)
		{
			throw new IOException("no total for the given time-unit: '" + key.getTime() + "'");
		}
		// LOG.info("writing popularity: total,percentage: " + total + " " + ((count / (double) total) * 100));
		context.write(key, popularity.set(count, (count / (double) total) * 100));
	};
}
