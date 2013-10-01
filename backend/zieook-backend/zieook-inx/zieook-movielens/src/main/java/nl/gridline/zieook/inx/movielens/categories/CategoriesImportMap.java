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
package nl.gridline.zieook.inx.movielens.categories;

import java.io.IOException;

import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.mapreduce.TaskConfig;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * CategoriesImportMap.java created Mar 24, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class CategoriesImportMap extends Mapper<Text, LongWritable, Text, Put>
{

	// simple write all categories to the database
	// we would love to have this in a single row...
	// with many columns... let's see if that is possible.

	private static final byte[] COLUMN = Bytes.toBytes(HBaseTableConstants.COLLECTION_CATEGORY_COLLUMN);
	private static final byte[] NAME = Bytes.toBytes(HBaseTableConstants.COLLECTION_CATEGORY_NAME);
	private static final byte[] COUNT = Bytes.toBytes(HBaseTableConstants.COLLECTION_CATEGORY_COUNT);

	private String collection;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{
		collection = context.getConfiguration().get(TaskConfig.COLLECTION);
	};

	@Override
	protected void map(Text key, LongWritable value, Context context) throws IOException, InterruptedException
	{
		String category = key.toString();
		Put put = new Put(RowKeys.getCategoriesKey(collection, category));
		put.add(COLUMN, NAME, Bytes.toBytes(category));
		put.add(COLUMN, COUNT, Bytes.toBytes(value.get()));
		// add category & count (count
		context.write(key, put);
		context.progress();
	};
}
