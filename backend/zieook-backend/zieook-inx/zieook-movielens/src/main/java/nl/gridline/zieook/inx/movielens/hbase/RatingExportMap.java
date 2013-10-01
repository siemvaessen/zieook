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
import java.util.NavigableMap;

import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.model.Rating;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * RatingExportMap.java created 25 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:gerlof@gridline.nl">Gerlof</a>
 * @version $Revision$, $Date$
 */
public class RatingExportMap extends TableMapper<ImmutableBytesWritable, Text>
{
	private static final byte[] COLUMN_RATING = Bytes.toBytes(HBaseTableConstants.USERTABLE_COLUMN_RATING);

	private final Text text = new Text();

	@Override
	public void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException
	{
		NavigableMap<byte[], byte[]> map = value.getFamilyMap(COLUMN_RATING);
		Rating data = new Rating(map);

		text.set(data.toJSON());
		context.write(key, text);
	}
}
