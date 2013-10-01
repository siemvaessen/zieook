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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.NavigableMap;

import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-api-workflow<br />
 * JAXBClassTests.java created 24 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class JAXBClassTests
{

	@Test
	public void recommenderTest()
	{
		RecommenderConfig config = new RecommenderConfig("cp", "recommender", "collection", null, null, 1000, 1000);
		NavigableMap<byte[], byte[]> map = config.toMap();
		RecommenderConfig newconfig = new RecommenderConfig(map);
		System.out.println(config.toJSON());
		assertEquals(config, newconfig);
	}

	@Test
	public void collectionFilterTest()
	{
		CollectionFilter filter = new CollectionFilter("filtername", "key", Arrays.asList("keyword", "keyword2"));
		NavigableMap<byte[], byte[]> map = null;
		try
		{
			map = filter.toMap();
		}
		catch (IOException e)
		{
			fail("could not create filter map");
			e.printStackTrace();
		}

		CollectionFilter newfilter = null;
		try
		{
			newfilter = new CollectionFilter(map);
		}
		catch (IOException e)
		{
			fail("could not create filter map");
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			fail("could not create filter map");
			e.printStackTrace();
		}
		System.out.println(filter.toJSON());

		assertTrue(newfilter != null);
		assertTrue(filter != null);
		assertEquals(newfilter, filter);

	}

	@Test
	public void templateTest()
	{
		Template template = new Template("testtemplate", Arrays.asList(
				new Module("module1", Arrays.asList(new Property("key1", "v1"), new Property("key2", "v2"))), new Module()));

		NavigableMap<byte[], byte[]> map = null;
		try
		{
			map = template.toMap();
		}
		catch (IOException e)
		{
			fail("failed to create map from template");
			e.printStackTrace();
		}

		Template newtemplate = null;
		try
		{
			newtemplate = new Template(map);
		}
		catch (IOException e)
		{
			fail("failed to create template from map");
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			fail("failed to template from map");
			e.printStackTrace();
		}

		System.out.println(template.toJSON());

		assertTrue(newtemplate != null);
		assertEquals(template, newtemplate);

	}

	@Test
	public void collectionTest()
	{
		Collection collection = new Collection("cp", "name", "type", "location", new OAISets(), new OAIMetadata(), 1000);
		Map<byte[], byte[]> map = collection.toMap();

		System.out.println(collection.toJSON());

		assertEquals(collection.getCp(), ModelConstants.getCp(map));
		assertEquals(collection.getName(), ModelConstants.getName(map));
		assertEquals(collection.getType(), ModelConstants.getType(map));
		assertEquals(collection.getLocation(), ModelConstants.getLocation(map));

	}

	@Test
	public void collectionMapTest()
	{
		Collection collection = new Collection("cp", "name", "type", "location", new OAISets(), new OAIMetadata(), 1000);
		NavigableMap<byte[], byte[]> map = collection.toMap();

		Collection newcollection = new Collection(map);
		System.out.println(collection.toJSON());
		assertEquals(collection, newcollection);
	}

	@Test
	public void contentProviderTest()
	{
		long stamp = System.currentTimeMillis() / 1000;

		ContentProvider cp = new ContentProvider("gridline", stamp, true);
		NavigableMap<byte[], byte[]> map = cp.toMap();
		ContentProvider cpNew = new ContentProvider(map);
		System.out.println(cp.toJSON());
		assertEquals(cp, cpNew);

	}
}
