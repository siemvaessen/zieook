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
import java.util.Collection;
import java.util.Map;

import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.model.ModelConstants;
import nl.gridline.zieook.statistics.popularity.io.ItemTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * PopularityMap.java created 13 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class PopularityMap extends TableMapper<ItemTime, LongWritable>
{
	private static final Log LOG = LogFactory.getLog(PopularityMap.class);

	// find ratings per item within a given time range
	private static final byte[] COLUMN_RATING = Bytes.toBytes(HBaseTableConstants.USERTABLE_COLUMN_RATING);

	private final ItemTime ratedItem = new ItemTime();
	private final LongWritable one = new LongWritable(1);

	private Collection<Long> types;
	private Map<Long, String> typeMap;
	private long now;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{

		String types = context.getConfiguration().get(PopularityStatistics.TYPES);
		if (types == null)
		{
			throw new IOException("failed to read <" + PopularityStatistics.TYPES + "> please set the configuration item");
		}

		now = context.getConfiguration().getLong(PopularityStatistics.NOW, Long.MIN_VALUE);
		if (now == Long.MIN_VALUE)
		{
			throw new IOException("failed to read <" + PopularityStatistics.NOW + "> please set the configuration item");
		}

		typeMap = PopularityStatistics.getTypes(types);

		this.types = typeMap.keySet();
		LOG.info("setting now to: " + now);
		LOG.info("types: '" + types + "' split into " + this.types.size() + " items");
		for (Map.Entry<Long, String> entry : typeMap.entrySet())
		{
			LOG.info(entry.getValue() + " <" + (now - entry.getKey()) + "," + now + ">");
		}

	};

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context) throws java.io.IOException,
			InterruptedException
	{

		long item = 0;
		long date = 0;
		String collection = null;

		KeyValue itemKV = value.getColumnLatest(COLUMN_RATING, ModelConstants.ITEM);
		if (itemKV != null)
		{
			item = Bytes.toLong(itemKV.getValue());
		}
		else
		{
			LOG.error("no key-value " + Bytes.toString(ModelConstants.ITEM) + " for "
					+ Bytes.toStringBinary(value.getRow()));
		}

		KeyValue dateKV = value.getColumnLatest(COLUMN_RATING, ModelConstants.TIMESTAMP);
		if (dateKV != null)
		{
			date = Bytes.toLong(dateKV.getValue());
		}
		else
		{
			LOG.error("no key-value " + Bytes.toString(ModelConstants.TIMESTAMP) + " for "
					+ Bytes.toStringBinary(value.getRow()));
		}

		KeyValue collectionKV = value.getColumnLatest(COLUMN_RATING, ModelConstants.COLLECTION);
		if (collectionKV != null)
		{
			collection = Bytes.toString(collectionKV.getValue());
		}
		else
		{
			// previously we didn't store the collection in separate field.
			// but we can retrieve it using the rowkey
			// this is a fallback:

			Object[] parts = RowKeys.getStrStrLngLng(value.getRow());
			collection = parts[1].toString();

			if (collection != null)
			{
				LOG.warn("no key-value " + Bytes.toString(ModelConstants.COLLECTION)
						+ " using decomposed row key fall-back: " + collection);
			}
			else
			{
				LOG.error("could not determine " + Bytes.toString(ModelConstants.COLLECTION) + " for "
						+ Bytes.toStringBinary(value.getRow()));
			}

		}

		// remark, we check for null-ness of the collection string, as this can be obtained through the fallback:
		if (itemKV != null && dateKV != null && collection != null)
		{
			for (long type : types)
			{
				final long earliest = now - type;
				if (date > earliest && date < now)
				{
					context.write(ratedItem.set(item, collection, type), one);
				}
			}

		}
		context.progress();
		// write ratings with: itemid-timerange
	};
}
