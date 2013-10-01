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

import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.model.Rating;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

/**
 * Reads user ratings from HBase into a sequence file (Map)
 * This is wat the result should look like:
 * userID,itemID[,preferencevalue]
 * <p />
 * Project zieook-movielens<br />
 * PreferenceMap.java created 25 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class DataPrepareMap extends TableMapper<ImmutableBytesWritable, Text>
{
	private final Text text = new Text();

	private static final Log LOG = LogFactory.getLog(DataPrepareMap.class);

	private static final byte[] COLUMN_RATING = Bytes.toBytes(HBaseTableConstants.USERTABLE_COLUMN_RATING);

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context) throws java.io.IOException,
			InterruptedException
	{
		// split the row,
		// Map<String, String> map = RowKeys.getRatingKey(key.get());

		Rating ratingObj = new Rating(value.getFamilyMap(COLUMN_RATING));

		Long user = ratingObj.getUser();
		Long item = ratingObj.getItem();
		double rating = ratingObj.getRating();

		if (user == null || item == null)
		{
			LOG.error("given rating not complete: " + ratingObj.toJSON());
		}
		else
		{
			String data = new StringBuilder().append(user).append(',').append(item).append(',').append(rating).toString();
			LOG.info("data: " + data);
			text.set(data);
			context.write(key, text);
		}
	};
}
