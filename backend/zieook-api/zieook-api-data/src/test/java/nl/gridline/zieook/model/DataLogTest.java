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

import nl.gridline.zieook.model.Recommended.RecommenderType;

import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * RecommendationViewTest.java created 27 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class DataLogTest
{

	@Test
	public final void testDataLogObject()
	{
		DataLog datalog = new DataLog(10L, 10L, "collection", "some-source", 1000L);
		NavigableMap<byte[], byte[]> map = datalog.toMap();

		assertEquals(datalog.getUser(), ModelConstants.getUser(map));
		assertEquals(datalog.getItem(), ModelConstants.getItem(map));
		assertEquals(datalog.getSource(), ModelConstants.getSource(map));
		assertEquals(datalog.getDate(), ModelConstants.getStamp(map));
		assertEquals(datalog.getCollection(), ModelConstants.getCollection(map));

		assertEquals(datalog, new DataLog(map));
	}

	@Test
	public final void testRecommedationViewObject()
	{
		// long user, long item, String source, long date, long recommender, long size)

		Recommended view = new Recommended(10L, 100L, "collection", "some-source", 200L, "recommender",
				RecommenderType.USER_BASED, 1);
		NavigableMap<byte[], byte[]> map = view.toMap();

		assertEquals(view.getUser(), ModelConstants.getUser(map));
		assertEquals(view.getItem(), ModelConstants.getItem(map));
		assertEquals(view.getSource(), ModelConstants.getSource(map));
		assertEquals(view.getDate(), ModelConstants.getStamp(map));
		assertEquals(view.getRecommender(), ModelConstants.getRecommender(map));
		assertEquals(view.getSize(), ModelConstants.getSize(map));
		assertEquals(view.getCollection(), ModelConstants.getCollection(map));

		assertEquals(view, new Recommended(map));
	}

	@Test
	public final void testUserRatingMapOfStringString()
	{
		Rating rating = new Rating(2L, 10L, "collection", "source-item", 100L, 1L);
		// long rating, long user, long item, String source, long date)
		NavigableMap<byte[], byte[]> map = rating.toMap();

		// check if they are written to the map:
		assertTrue(rating.getRating() == ModelConstants.getRating(map));
		assertEquals(rating.getUser(), ModelConstants.getUser(map));
		assertEquals(rating.getItem(), ModelConstants.getItem(map));
		assertEquals(rating.getSource(), ModelConstants.getSource(map));
		assertEquals(rating.getDate(), ModelConstants.getStamp(map));
		assertEquals(rating.getCollection(), ModelConstants.getCollection(map));
		Rating ratingNew = new Rating(map);
		// check if the objects are equal - all items should have been in the map
		assertEquals(rating, ratingNew);

	}

	@Test
	public final void testUserViewMapOfStringString()
	{
		// public UserView(long user, long item, String source, Long date, long sourceItem, long rank)

		UserView user = new UserView(10L, 50L, "collection", "source-item", 30L, "recommender", 2L, 1);

		NavigableMap<byte[], byte[]> map = user.toMap();

		// check if they are written to the map:

		assertEquals(user.getUser(), ModelConstants.getUser(map));
		assertEquals(user.getItem(), ModelConstants.getItem(map));
		assertEquals(user.getSource(), ModelConstants.getSource(map));
		assertEquals(user.getDate(), ModelConstants.getStamp(map));
		assertEquals(user.getSourceItem(), ModelConstants.getSourceItem(map));
		assertEquals(user.getRank(), ModelConstants.getRank(map));
		assertEquals(user.getCollection(), ModelConstants.getCollection(map));

		UserView ratingNew = new UserView(map);
		// check if the objects are equal - all items should have been in the map
		assertEquals(user, ratingNew);
	}
}
