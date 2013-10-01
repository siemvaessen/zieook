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
package nl.gridline.zieook.data.hbase.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nl.gridline.zieook.model.Rating;
import nl.gridline.zieook.model.Recommended;
import nl.gridline.zieook.model.UserView;
import nl.gridline.zieook.workflow.model.CollectionFilter;
import nl.gridline.zieook.workflow.model.Module;
import nl.gridline.zieook.workflow.model.Property;
import nl.gridline.zieook.workflow.model.RecommenderConfig;
import nl.gridline.zieook.workflow.model.Template;

import org.junit.Ignore;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * TestHelper.java created 24 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Ignore
public class Helper
{

	private Helper()
	{
		// hide constructor
	}

	public static final long HIGH_TIME = System.currentTimeMillis();

	// 10 days: days * hours * minutes * seconds * milliseconds:
	public static final long LOW_TIME = HIGH_TIME - (10 * 24 * 60 * 60 * 1000);

	public static final String CP = "zieook_integration_tests";

	public static final String COLLECTION = "zieook_integration_tests_collection";

	public static final String RECOMMENDER = "zieook_integration_tests_itembased";

	public static final String[] SOURCES = {"zieook_integration_tests_source1", "zieook_integration_tests_source2",
			"zieook_integration_tests_source3", "zieook_integration_tests_source4"};

	public static Template getTemplate()
	{
		return new Template("test-template", Arrays.asList(
				new Module("module1", Arrays.asList(new Property("test-key", "test-value"))), new Module()));
	}

	public static CollectionFilter getCollectionFilter()
	{
		return new CollectionFilter("test-filter", "filter-key", Arrays.asList("keyword1", "keyword2"));
	}

	public static RecommenderConfig getRecommenderConfig()
	{
		return new RecommenderConfig("gridline-test-cp", "gridline-test-rec", "gridline-test-col", getCollectionFilter(),
				getTemplate(), 10000, 1000);
	}

	public static final int USER_STATSSIZE = 10;

	public static Set<Recommended> getRecommended()
	{
		Set<Recommended> data = new HashSet<Recommended>();

		int itemNorm = 25;
		long user = 0, item = 0;

		String source = "";
		for (long i = LOW_TIME; i < HIGH_TIME; i++)
		{
			user = i % USER_STATSSIZE;
			item = i % itemNorm;
			source = SOURCES[(int) (i % SOURCES.length)];
			Recommended recommended = new Recommended(user, item, CP, COLLECTION, source, i, RECOMMENDER,
					Recommended.RecommenderType.ITEM_BASED, 10);
			data.add(recommended);
		}

		return data;
	}

	public static Set<UserView> getUserViews()
	{
		Set<UserView> data = new HashSet<UserView>();

		int itemNorm = 25;
		int sourceNorm = SOURCES.length;
		int sourceItemNorm = 22;
		int rankNorm = 10;

		long user = 0, item = 0, sourceItem = 0, rank = 0;
		String source = "";
		for (long i = LOW_TIME; i < HIGH_TIME; i++)
		{
			user = i % USER_STATSSIZE;
			item = i % itemNorm;
			sourceItem = i % sourceItemNorm;
			rank = i % rankNorm;
			source = SOURCES[(int) (i % sourceNorm)];
			UserView view = new UserView(user, item, CP, COLLECTION, source, i, RECOMMENDER, sourceItem, rank);
			data.add(view);
		}

		return data;
	}

	public static final int RATING_NORM = 5;
	public static final int ITEM_SIZE = 10;
	public static final int USER_SIZE = 100;
	public static final int USER_LOW = 1000;

	public static Set<Rating> getUserRatings()
	{
		Set<Rating> data = new HashSet<Rating>();

		int sourceNorm = SOURCES.length;
		long time = 0;
		double rating = 0;
		String source = "";
		for (long user = USER_LOW; user < USER_LOW + USER_SIZE; user++)
		{
			for (long item = 0; item < ITEM_SIZE; item++)
			{
				source = SOURCES[(int) (user % sourceNorm)];
				rating = user % RATING_NORM;
				time = LOW_TIME + ((HIGH_TIME - LOW_TIME) % user);
				data.add(new Rating(user, item, CP, COLLECTION, source, time, rating));
			}
		}

		return data;
	}
	public static long SINGLE_USERRATING = 2100;

	public static Set<Rating> getSingleUserRating()
	{
		Set<Rating> data = new HashSet<Rating>();

		long time = System.currentTimeMillis();

		for (long item = 1; item < 21; item++)
		{
			data.add(new Rating(SINGLE_USERRATING, item, CP, COLLECTION, "no-source", time, item % RATING_NORM));
		}

		return data;
	}

	public static long TIMED_USERRATING = 2500;
	public static long TIMED_ITEMLOW = 1;
	public static long TIMED_ITEMHIGH = 21;

	public static Set<Rating> getTimedRatings()
	{
		Set<Rating> data = new HashSet<Rating>();

		for (long item = TIMED_ITEMLOW; item < TIMED_ITEMHIGH; item++)
		{
			data.add(new Rating(TIMED_USERRATING, item, CP, COLLECTION, "no-source", item, item % RATING_NORM));
		}

		return data;
	}
}
