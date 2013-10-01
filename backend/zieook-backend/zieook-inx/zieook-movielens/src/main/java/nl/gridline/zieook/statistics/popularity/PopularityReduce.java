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

import nl.gridline.zieook.statistics.popularity.io.ItemTime;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * PopularityReduce.java created 13 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class PopularityReduce extends Reducer<ItemTime, LongWritable, ItemTime, LongWritable>
{
	// private static final Log LOG = LogFactory.getLog(PopularityReduce.class);

	private final LongWritable valueOut = new LongWritable();

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{
		// nothing
	};

	@Override
	protected void reduce(ItemTime key, java.lang.Iterable<LongWritable> values, Context context) throws IOException,
			InterruptedException
	{
		long count = 0;
		for (LongWritable i : values)
		{
			count += i.get();
		}
		// LOG.info("writing: " + key.getCollection() + "/" + key.getItem() + " date: " + key.getTime() + " count: " +
		// count);
		valueOut.set(count);
		context.write(key, valueOut);
	};
}