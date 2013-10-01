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

import java.util.Arrays;
import java.util.NavigableMap;

import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * CollectionItemTest.java created 23 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionItemTest
{

	@Test
	public final void testCollectionItemMapOfStringObject()
	{
		CollectionItem collection = new CollectionItem(100, "beeld:100", "cp", Arrays.asList("category"), "title",
				"description", 100L, Arrays.asList("keyword1", "keyword2"), "location", "image");

		NavigableMap<byte[], byte[]> map = collection.toMap();

		// check if they are written to the map:
		assertEquals(collection.getId(), ModelConstants.getItem(map));
		assertEquals(collection.getCollectionId(), ModelConstants.getCollectionId(map));
		assertEquals(collection.getCp(), ModelConstants.getCp(map));
		assertEquals(collection.getCategory(), Arrays.asList(ModelConstants.getCategory(map).split("\n")));
		assertEquals(collection.getTitle(), ModelConstants.getTitle(map));
		assertEquals(collection.getDescription(), ModelConstants.getDescription(map));
		assertEquals(collection.getCreateDate(), ModelConstants.getStamp(map));
		assertEquals(collection.getLocation(), ModelConstants.getLocation(map));
		assertEquals(collection.getImage(), ModelConstants.getImage(map));
		CollectionItem collectionNew = new CollectionItem(map);
		// check if the objects are equal - all items should have been in the map
		assertEquals(collection, collectionNew);
	}
}
