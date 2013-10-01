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
import nl.gridline.zieook.statistics.io.RecommenderItem;

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
 * ViewsPerItemMap.java created 9 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class ViewsPerItemMap extends TableMapper<RecommenderItem, LongWritable>
{
	private static final Log LOG = LogFactory.getLog(SourcesByRecommenderMap.class);
	private final byte[] COLUMN_USERVIEW = Bytes.toBytes(HBaseTableConstants.EVENTLOG_COLUMN_VIEW);

	private final RecommenderItem keyOut = new RecommenderItem();
	private final LongWritable one = new LongWritable(1);

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException,
			InterruptedException
	{

		KeyValue recommender = value.getColumnLatest(COLUMN_USERVIEW, ModelConstants.RECOMMENDER);
		KeyValue source = value.getColumnLatest(COLUMN_USERVIEW, ModelConstants.ITEM);

		if (source != null && recommender != null)
		{

			String rStr = Bytes.toString(recommender.getValue());
			long iStr = Bytes.toLong(source.getValue());

			keyOut.setValues(rStr, iStr);
			LOG.info("map: <[" + rStr + "," + iStr + "]," + one + ">");
			context.write(keyOut, one);
			context.progress();
		}
		else
		{
			LOG.error("source = " + source + " recommender = " + recommender);
		}
	};
}
