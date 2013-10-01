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
package nl.gridline.zieook.workflow.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.NavigableMap;

import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-api-workflow<br />
 * CollectionTest.java created 21 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class CollectionTest
{

	@Test
	public void testMap1()
	{
		String cp = "cp-test";
		String name = "name";
		String type = "oai";
		String location = "http://blabla";
		OAISets oaiSets = new OAISets(Arrays.asList("test1", "test2"), Arrays.asList("test3", "test3"), Arrays.asList(
				"test4", "test5"));
		OAIMetadata oaiMetaData = new OAIMetadata(Arrays.asList("test6", "test7"), "test7");
		long interval = 10000L;

		Collection collection = new Collection(cp, name, type, location, oaiSets, oaiMetaData, interval);
		NavigableMap<byte[], byte[]> map = collection.toMap();
		Collection newcollection = new Collection(map);

		assertEquals(collection, newcollection);
		assertEquals(cp, newcollection.getCp());
		assertEquals(name, newcollection.getName());
		assertEquals(type, newcollection.getType());
		assertEquals(location, newcollection.getLocation());
		assertEquals(location, newcollection.getLocation());
		assertEquals(oaiSets, newcollection.getOaiSets());
		assertEquals(oaiMetaData, newcollection.getOaiMetadata());

	}

	@Test
	public void testMap2()
	{
		String cp = "cp-test";
		String name = "name";
		String type = "oai";
		String location = "http://blabla";
		OAISets oaiSets = null; // new OAISets(Arrays.asList("test1", "test2"), Arrays.asList("test3", "test3"),
										// Arrays.asList( "test4", "test5"));
		OAIMetadata oaiMetaData = null; // new OAIMetadata(Arrays.asList("test6", "test7"), "test7");
		long interval = 10000L;

		Collection collection = new Collection(cp, name, type, location, oaiSets, oaiMetaData, interval);
		NavigableMap<byte[], byte[]> map = collection.toMap();
		Collection newcollection = new Collection(map);

		assertEquals(collection, newcollection);
		assertEquals(cp, newcollection.getCp());
		assertEquals(name, newcollection.getName());
		assertEquals(type, newcollection.getType());
		assertEquals(location, newcollection.getLocation());
		assertEquals(location, newcollection.getLocation());
		assertEquals(oaiSets, newcollection.getOaiSets());
		assertEquals(oaiMetaData, newcollection.getOaiMetadata());
	}
}
