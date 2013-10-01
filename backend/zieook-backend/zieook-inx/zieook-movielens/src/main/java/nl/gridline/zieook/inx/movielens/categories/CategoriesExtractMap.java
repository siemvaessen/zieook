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
import java.util.List;

import nl.gridline.zieook.mapreduce.HBaseTableConstants;
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
 * CategoriesExtractMap.java created Mar 24, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CategoriesExtractMap extends TableMapper<Text, LongWritable>
{

	private static final Log LOG = LogFactory.getLog(CategoriesExtractMap.class);

	private static final byte[] COLLECTION_TABLE_COLUMN_INTR = Bytes
			.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR);

	private final Text text = new Text();
	private final LongWritable one = new LongWritable(1);

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{
		// also allow for extracting keywords, to filter on keywords
	};

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException,
			InterruptedException
	{
		CollectionItem item = new CollectionItem(value.getFamilyMap(COLLECTION_TABLE_COLUMN_INTR));
		List<String> categories = item.getCategory();
		if (categories != null)
		{
			for (String category : categories)
			{
				text.set(category);
				context.write(text, one);
			}
		}
		else
		{
			LOG.warn("item has no categories: " + item.getCollectionId());
		}
	};
}
