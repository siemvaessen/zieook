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
package nl.gridline.zieook.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.NavigableMap;

import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * GroupDataTest.java created 2 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class GroupDataTest
{

	@Test
	public void mappingTest()
	{
		GroupedData grouped = new GroupedData("collection", "recommender", "source", 10L, 15L, 100L, 200L);
		NavigableMap<byte[], byte[]> map = grouped.toMap();
		assertEquals(grouped.getCollection(), ModelConstants.getCollection(map));
		assertEquals(grouped.getRecommender(), ModelConstants.getRecommender(map));
		assertEquals(grouped.getSource(), ModelConstants.getSource(map));
		assertEquals(grouped.getItem(), ModelConstants.getItemLong(map));
		assertEquals(grouped.getUser(), ModelConstants.getUserLong(map));
		assertEquals(grouped.getCount(), ModelConstants.getCount(map));
		assertEquals(grouped.getDate(), ModelConstants.getStamp(map));
	}

	@Test
	public void mappingTestNullValues()
	{
		GroupedData grouped = new GroupedData("collection", "recommender", "source", null, null, null, null);
		NavigableMap<byte[], byte[]> map = grouped.toMap();
		assertEquals(grouped.getCollection(), ModelConstants.getCollection(map));
		assertEquals(grouped.getRecommender(), ModelConstants.getRecommender(map));
		assertEquals(grouped.getSource(), ModelConstants.getSource(map));
		assertEquals(grouped.getItem(), ModelConstants.getItemLong(map));
		assertEquals(grouped.getUser(), ModelConstants.getUserLong(map));
		assertEquals(grouped.getCount(), ModelConstants.getCount(map));
		assertEquals(grouped.getDate(), ModelConstants.getStamp(map));
	}

	@Test
	public void equalityTest()
	{
		// <collection,recommender,source,item,user,count,date><null,R,null,23,null,8,201>
		// <collection,recommender,source,item,user,count,date><null,R,null,24,null,8,201>

		// public GroupedData(collection, recommender, source, item, user, count, date)
		GroupedData a = new GroupedData(null, "R", null, 1L, null, 8L, 201L);
		GroupedData b = new GroupedData(null, "R", null, 2L, null, 8L, 201L);
		assertTrue(!a.equals(b));
	}
}
