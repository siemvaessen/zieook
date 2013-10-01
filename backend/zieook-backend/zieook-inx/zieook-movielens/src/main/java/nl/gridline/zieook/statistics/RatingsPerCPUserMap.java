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

import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.model.ModelConstants;

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
 * RatingsPerCPUserMap.java created 4 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class RatingsPerCPUserMap extends TableMapper<LongWritable, LongWritable>
{
	private static final Log LOG = LogFactory.getLog(RatingsPerCPUserMap.class);

	private final byte[] COLUMN_RATING = Bytes.toBytes(HBaseTableConstants.USERTABLE_COLUMN_RATING);

	private final LongWritable outValue = new LongWritable(1);
	private final LongWritable outKey = new LongWritable();

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context) throws java.io.IOException,
			InterruptedException
	{

		// group by rating: value.getColumn(COLUMN_RATING, ModelConstants.RATING);
		KeyValue user = value.getColumnLatest(COLUMN_RATING, ModelConstants.USER);
		if (user != null)
		{
			long userId = Bytes.toLong(user.getValue());
			LOG.debug("map: <" + userId + ",1>");
			outKey.set(userId);
			context.write(outKey, outValue);
			context.progress();
		}
		else
		{
			if (user == null)
			{
				LOG.error("no key-value " + Bytes.toString(ModelConstants.USER) + " for "
						+ Bytes.toStringBinary(value.getRow()));
			}

		}
	};
}
