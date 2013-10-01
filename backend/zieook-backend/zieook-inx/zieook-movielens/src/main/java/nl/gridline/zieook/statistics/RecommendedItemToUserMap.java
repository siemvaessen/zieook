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
import nl.gridline.zieook.statistics.io.UserCollectionItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;

/**
 * top recommended: de top-n meest recommended voor een gebruiker<br />
 * <b>map</b><br />
 * <b>in: </b> Recommended object<br />
 * <b>out: </b> user/recommender/item - 1<br />
 * <b>reduce</b><br />
 * <b>in: </b>user/recommender/item - [1,1,1,...]
 * <b>out: </b>user/recommender/item - count
 * <p />
 * Project zieook-movielens<br />
 * RecommendedTop.java created 2 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 * @see RecommendedItemToUserReduce
 */
public class RecommendedItemToUserMap extends TableMapper<UserCollectionItem, LongWritable>
{
	private static final Log LOG = LogFactory.getLog(RecommendedItemToUserMap.class);
	private static final byte[] EVENTLOG_COLUMN_RECOMMENDED = Bytes
			.toBytes(HBaseTableConstants.EVENTLOG_COLUMN_RECOMMENDED);

	private final UserCollectionItem outKey = new UserCollectionItem();
	private final LongWritable outValue = new LongWritable(1);

	// byte[] row = RowKeys.getRecommendKey(recommender, recommend.getType().value(), recommend.getUser(),
	// recommend.getItem(), recommend.getDate());

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{

	}

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException,
			InterruptedException
	{
		KeyValue itemKV = value.getColumnLatest(EVENTLOG_COLUMN_RECOMMENDED, ModelConstants.ITEM);
		KeyValue userKV = value.getColumnLatest(EVENTLOG_COLUMN_RECOMMENDED, ModelConstants.USER);
		KeyValue collectionKV = value.getColumnLatest(EVENTLOG_COLUMN_RECOMMENDED, ModelConstants.COLLECTION);
		if (itemKV != null && userKV != null && collectionKV != null)
		{
			long user = Bytes.toLong(userKV.getValue());
			long item = Bytes.toLong(itemKV.getValue());
			String collection = Bytes.toString(collectionKV.getValue());
			outKey.setData(user, item, collection);
			LOG.debug("map <[" + item + "," + user + "," + collection + "],1>");
			context.write(outKey, outValue);
			context.progress();
		}
		else
		{

			if (userKV == null)
			{
				LOG.error("no key-value " + Bytes.toString(ModelConstants.USER) + " for "
						+ Bytes.toStringBinary(value.getRow()));
			}
			if (itemKV == null)
			{
				LOG.error("no key-value " + Bytes.toString(ModelConstants.ITEM) + " for "
						+ Bytes.toStringBinary(value.getRow()));
			}

			if (collectionKV == null)
			{
				LOG.error("no key-value " + Bytes.toString(ModelConstants.COLLECTION) + " for "
						+ Bytes.toStringBinary(value.getRow()));
			}

		}
	}
}
