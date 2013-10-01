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
package nl.gridline.free.taalserver;

import java.io.IOException;

import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.model.CollectionItem;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.mahout.math.VarLongWritable;

/**
 * Count the documents
 * <p />
 * Project hadoop-taalserver<br />
 * CountDocuments.java created 21 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision: 1636 $, $Date: 2011-03-31 16:58:53 +0200 (do, 31 mrt 2011) $
 */
public class CountDocumentsMap extends TableMapper<LongWritable, VarLongWritable>
{
	private static final byte[] COLLUMN_INTR = Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR);

	private final LongWritable one = new LongWritable(1);

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException,
			InterruptedException
	{
		// this is easy, every document is a key
		// so we write doc-id, doc-id
		CollectionItem item = new CollectionItem(value.getFamilyMap(COLLUMN_INTR));
		Long itemid = item.getId();
		if (itemid != null)
		{
			context.write(one, new VarLongWritable(itemid.longValue()));
			context.progress();
		}
		else
		{
			throw new IOException("collection item not valid (missing id): " + item.toString());
		}
	};
}
