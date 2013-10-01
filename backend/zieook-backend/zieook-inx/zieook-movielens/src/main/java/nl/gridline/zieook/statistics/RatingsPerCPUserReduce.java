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
package nl.gridline.zieook.statistics;

import java.io.IOException;

import nl.gridline.zieook.mapreduce.HBasePUTFactory;
import nl.gridline.zieook.mapreduce.TaskConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.LongWritable;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * RatingsPerCPUserReduce.java created 4 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class RatingsPerCPUserReduce extends TableReducer<LongWritable, LongWritable, LongWritable>
{
	private static final Log LOG = LogFactory.getLog(RatingsPerCPUserReduce.class);

	public static final String DATE = "nl.gridline.zieook.statistics.storage-date";

	private long date;
	private String collection;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{
		Configuration config = context.getConfiguration();

		String dateStr = config.get(DATE);
		if (dateStr == null)
		{
			throw new IOException("no date set, please set '" + DATE + "' in the context");
		}
		date = Long.parseLong(dateStr);
		collection = config.get(TaskConfig.COLLECTION);
	};

	@Override
	protected void reduce(LongWritable key, java.lang.Iterable<LongWritable> values, Context context)
			throws IOException, InterruptedException
	{
		long user = key.get();
		long count = 0;
		for (LongWritable v : values)
		{
			count += v.get();
		}
		Put put = HBasePUTFactory.putStatisticsRatingsUser(collection, user, date, count);
		LOG.debug("reduce  <[" + user + "," + date + "]," + count + ">");
		context.write(key, put);
	};
}
