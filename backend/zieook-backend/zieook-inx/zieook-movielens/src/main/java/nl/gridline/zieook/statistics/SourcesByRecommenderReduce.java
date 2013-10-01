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
import nl.gridline.zieook.statistics.io.RecommenderSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.LongWritable;

/**
 * groups sources by recommender name or collection (for some cp)
 * <p />
 * Project zieook-movielens<br />
 * SourcesReduce.java created 1 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 * @see SourcesByRecommenderMap
 */
public class SourcesByRecommenderReduce extends TableReducer<RecommenderSource, LongWritable, RecommenderSource>
{
	private static final Log LOG = LogFactory.getLog(SourcesByRecommenderReduce.class);
	public static final String DATE = "nl.gridline.zieook.statistics.storage-date";

	private long date;

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

	};

	@Override
	protected void reduce(RecommenderSource key, Iterable<LongWritable> values, Context context) throws IOException,
			InterruptedException
	{
		int count = 0;
		for (LongWritable value : values)
		{
			count += value.get();
		}

		Put put = HBasePUTFactory.putStatisticsSources(key.getRecommender(), key.getSource(), date, count);

		LOG.info("reduce <[" + key.getRecommender() + "," + key.getSource() + "]," + count + ">");
		context.write(key, put);
	};

}
