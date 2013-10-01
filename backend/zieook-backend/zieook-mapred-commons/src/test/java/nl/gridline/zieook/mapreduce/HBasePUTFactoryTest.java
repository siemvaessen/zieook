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
package nl.gridline.zieook.mapreduce;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import nl.gridline.zieook.model.CollectionItem;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Ignore;
import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-mapred-commons<br />
 * HBasePUTFactoryTest.java created 4 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class HBasePUTFactoryTest
{

	static final String USER_FAIL = "user";
	static final String USER = "2";
	static final String NAME = "ZieOok User";
	static final String COLLECTION = "movielens";
	static final String CP = "gridline";
	static final String TITLE = "Nobody Knows";
	static final String LOCATION = "http://location/1";
	static final String IMAGE = "http://location/1.jpg";
	static final long STAMP = 1113451200;
	static final List<String> CATEGORY = Arrays.asList("Drama");
	static final List<String> KEYWORDS = Arrays.asList("Drama", "Tokyo", "Japan");
	static final String TEXT = "In Tokyo, the reckless single mother Keiko moves to a small apartment with her twelve years old son Akira ";
	static final Long ITEM = new Long(100);

	@Test
	public void getCollectionItemTest()
	{
		CollectionItem item = new CollectionItem(100, "test" + 100, CP, CATEGORY, TITLE, TEXT, STAMP, KEYWORDS, LOCATION,
				IMAGE);
		Put put = HBasePUTFactory.getCollectionItemIntr(COLLECTION, ITEM, item);
		Map<byte[], List<KeyValue>> map = put.getFamilyMap();
		for (Map.Entry<byte[], List<KeyValue>> entry : map.entrySet())
		{
			if (HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR.equals(Bytes.toString(entry.getKey())))
			{
				CollectionItem newItem = new CollectionItem(toMap(entry.getValue()));
				assertEquals(newItem, item); // general
				assertEquals(ITEM, newItem.getId());
				assertEquals(CP, newItem.getCp());
				assertEquals(CATEGORY, newItem.getCategory());
				assertEquals(TITLE, newItem.getTitle());
				assertEquals(TEXT, newItem.getDescription());
				assertEquals(STAMP, (long) newItem.getReleaseDate());
				assertEquals(KEYWORDS, newItem.getKeywords());
			}
		}
	}

	@Test
	public void getCollectionItemRawTest()
	{
		String raw = new CollectionItem(100, "item:" + 100, CP, CATEGORY, TITLE, TEXT, STAMP, KEYWORDS, LOCATION, IMAGE)
				.toJSON();
		Put put = HBasePUTFactory.getCollectionItemRaw(COLLECTION, ITEM, raw);
		Map<byte[], List<KeyValue>> map = put.getFamilyMap();
		for (Map.Entry<byte[], List<KeyValue>> entry : map.entrySet())
		{
			if (HBaseTableConstants.COLLECTION_TABLE_COLUMN_RAW.equals(Bytes.toString(entry.getKey())))
			{
				byte[] value = toMap(entry.getValue()).get(Bytes.toBytes(HBaseTableConstants.COLLECTION_QUALIFIER_RAW));
				assertEquals(Bytes.toString(value), raw);
			}
		}
	}

	@Ignore
	private NavigableMap<byte[], byte[]> toMap(List<KeyValue> list)
	{
		NavigableMap<byte[], byte[]> result = new TreeMap<byte[], byte[]>(Bytes.BYTES_COMPARATOR);
		for (KeyValue kv : list)
		{
			result.put(kv.getQualifier(), kv.getValue());
		}
		return result;
	}
}
