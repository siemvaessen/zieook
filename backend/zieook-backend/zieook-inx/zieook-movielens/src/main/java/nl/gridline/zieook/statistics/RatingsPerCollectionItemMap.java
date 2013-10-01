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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;

/**
 * Top rated: de top-n items met de meeste ratings per collectie<br />
 * <b>map</b><br />
 * <b>in:</b> Rating object<br />
 * <b>out:</b> item,1 (for all ratings.<br />
 * <br />
 * <b>reduce</b>
 * <b>in:</b> item,[1,1,...]<br />
 * <b>out:</b> GroupedData(item,date,count)<br />
 * <p />
 * Project zieook-movielens<br />
 * RatingsPerCollectionMap.java created 2 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 * @see RatingsPerCollectionItemReduce
 */
public class RatingsPerCollectionItemMap extends TableMapper<LongWritable, LongWritable>
{
	private static final Log LOG = LogFactory.getLog(RatingsPerCollectionItemMap.class);

	private final byte[] COLUMN_RATING = Bytes.toBytes(HBaseTableConstants.USERTABLE_COLUMN_RATING);

	private final LongWritable outValue = new LongWritable(1);
	private final LongWritable outKey = new LongWritable();

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{
		// read cp

	};

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException,
			InterruptedException
	{

		// group by rating: value.getColumn(COLUMN_RATING, ModelConstants.RATING);
		KeyValue item = value.getColumnLatest(COLUMN_RATING, ModelConstants.ITEM);
		if (item != null)
		{

			long itemId = Bytes.toLong(item.getValue());
			LOG.debug("map: <" + itemId + ",1>");
			outKey.set(itemId);
			context.write(outKey, outValue);
			context.progress();
		}
		else
		{
			LOG.error("no key-value " + Bytes.toString(ModelConstants.ITEM) + " for "
					+ Bytes.toStringBinary(value.getRow()));
		}

	};

}
