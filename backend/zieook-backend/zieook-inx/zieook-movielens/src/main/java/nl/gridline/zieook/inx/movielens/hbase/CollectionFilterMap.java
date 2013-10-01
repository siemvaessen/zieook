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
import java.util.HashSet;
import java.util.List;
import java.util.NavigableMap;

import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.model.CollectionItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * CollectionFilterMap.java created 5 apr. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionFilterMap extends TableMapper<Text, LongWritable>
{
	private static final byte[] COLUMN_INTR = Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR);

	String filterKey;

	HashSet<String> filter;

	long startdate;
	long enddate;

	final LongWritable one = new LongWritable(1);

	private static final Log LOG = LogFactory.getLog(CollectionFilterMap.class);

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{
		// get filter:
		// key / value
		filterKey = context.getConfiguration().get(TaskConfig.FILTER_KEY);

		String values = context.getConfiguration().get(TaskConfig.FILTER_VALUE);
		if (values != null)
		{
			String[] array = values.split("\n");
			filter = new HashSet<String>(array.length);
			for (String v : array)
			{
				filter.add(v.trim());
			}
		}

		String startStr = context.getConfiguration().get(TaskConfig.FILTER_STARTDATE);
		if (startStr != null)
		{
			startdate = Long.parseLong(startStr);
		}
		else
		{
			startdate = Long.MIN_VALUE;
		}
		String endStr = context.getConfiguration().get(TaskConfig.FILTER_ENDDATE);
		if (endStr != null)
		{
			enddate = Long.parseLong(endStr);
		}
		else
		{
			enddate = Long.MAX_VALUE;
		}

		LOG.info("Collection filter map config: filterKey:" + filterKey + " filter:" + filter + " startdate:" + startdate
				+ " enddate:" + enddate);
	};

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException,
			InterruptedException
	{
		NavigableMap<byte[], byte[]> map = value.getFamilyMap(COLUMN_INTR);
		CollectionItem data = new CollectionItem(map);

		// if there there is an item date: check it
		// LOG.info("Item: " + data);
		// LOG.info("start: " + startdate + " enddate: " + enddate);
		Long date = data.getCreateDate();
		if (data.getCreateDate() == null || (date >= startdate && date < enddate))
		{
			// LOG.info("date filter applied");
			// - check filter
			if (filterKey != null)
			{

				if ("category".equals(filterKey) && contains(data.getCategory()))
				{
					// LOG.info("category filter applied");
					context.write(new Text(data.getId() + ""), one);
				}
				else if ("keywords".equals(filterKey) && contains(data.getKeywords()))
				{
					// LOG.info("keyword filter applied");
					context.write(new Text(data.getId() + ""), one);
				}
				else
				{
					LOG.warn("skipped: " + getSkipped(data.getKeywords()));
					LOG.warn("skipped: " + getSkipped(data.getCategory()));
				}
			}
			else
			{
				context.write(new Text(data.getId() + ""), one);
			}
		}

	};

	private String getSkipped(List<String> list)
	{
		StringBuilder b = new StringBuilder();
		for (String s : list)
		{
			b.append(" '").append(s).append("' ");
		}
		return b.toString();
	}

	private boolean contains(List<String> list)
	{
		for (String s : list)
		{
			if (filter.contains(s.trim()))
			{
				return true;
			}
		}
		return false;
	}

}
