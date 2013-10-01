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

import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.model.ModelConstants;
import nl.gridline.zieook.statistics.io.UserCollection;

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
 * RecommendedToUserMap.java created 4 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class RecommendedToUserMap extends TableMapper<UserCollection, LongWritable>
{

	private static final Log LOG = LogFactory.getLog(RecommendedItemToUserMap.class);
	private static final byte[] EVENTLOG_COLUMN_RECOMMENDED = Bytes
			.toBytes(HBaseTableConstants.EVENTLOG_COLUMN_RECOMMENDED);

	private final UserCollection outKey = new UserCollection();
	private final LongWritable outValue = new LongWritable(1);

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{
	};

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context) throws java.io.IOException,
			InterruptedException
	{
		KeyValue userKV = value.getColumnLatest(EVENTLOG_COLUMN_RECOMMENDED, ModelConstants.USER);
		KeyValue collectionKV = value.getColumnLatest(EVENTLOG_COLUMN_RECOMMENDED, ModelConstants.COLLECTION);
		if (userKV != null && collectionKV != null)
		{
			long user = Bytes.toLong(userKV.getValue());
			String collection = Bytes.toString(collectionKV.getValue());

			LOG.debug("map <[" + user + "," + collection + "],1>");
			context.write(outKey.setData(collection, user), outValue);
			context.progress();
		}
		else
		{
			if (userKV == null)
			{
				LOG.error("no key-value " + Bytes.toString(ModelConstants.USER) + " for "
						+ Bytes.toStringBinary(value.getRow()));
			}
			if (collectionKV == null)
			{
				LOG.error("no key-value " + Bytes.toString(ModelConstants.COLLECTION) + " for "
						+ Bytes.toStringBinary(value.getRow()));
			}
		}
	};
}
