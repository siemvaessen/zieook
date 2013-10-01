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
package nl.gridline.zieook.workflow.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.workflow.model.Collection;
import nl.gridline.zieook.workflow.model.CollectionFilter;
import nl.gridline.zieook.workflow.model.ContentProvider;
import nl.gridline.zieook.workflow.model.Module;
import nl.gridline.zieook.workflow.model.OAIMetadata;
import nl.gridline.zieook.workflow.model.OAISets;
import nl.gridline.zieook.workflow.model.Property;
import nl.gridline.zieook.workflow.model.RecommenderConfig;
import nl.gridline.zieook.workflow.model.Template;

import org.junit.Ignore;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-data<br />
 * ConstantsTest.java created 11 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Ignore
public final class ConstantsTest
{

	private ConstantsTest()
	{

	}

	static
	{
		Calendar calendar = Calendar.getInstance();

		// set a arbitrary date:
		calendar.set(2011, 3, 21, 0, 0, 0);
		TIME = calendar.getTimeInMillis() / 1000;

		// set a arbitrary date:
		calendar.set(2011, 3, 22, 0, 0, 0);
		ENDTIME = calendar.getTimeInMillis();

	}

	public static final String CP = "gridline-test-1";
	public static final String RECOMMENDER = "recommender-test-1";
	public static final String RECOMMENDER2 = "recommender-test-2";
	public static final String COLLECTION = "collection-test-1";
	public static final String MOVIELENS = "movielens";
	public static final String USER = "iUser";
	public static final String ITEM = "anItem";
	public static final String SOURCE = "theSource";
	public static final String RATING = "1.0";
	public static final String RANK = "2";
	public static final String KEY = "myCpMetaDatakey";
	public static final long TIME;
	public static final long ENDTIME;

	// Calendar.getInstance().

	public static final int SIZE = 10;
	public static final int INTERVAL = (60 * 60 * 24);
	public static final int NEXT = -1;
	public static final String TEMPLATE_PROPERTY_KEY_1 = "aTemplateKey1";
	public static final String TEMPLATE_PROPERTY_VALUE_1 = "aTemplateValue1";
	public static final String TEMPLATE_PROPERTY_KEY_2 = "aTemplateKey2";
	public static final String TEMPLATE_PROPERTY_VALUE_2 = "aTemplateKey3";
	public static final String MODULE1 = "user-based";
	public static final String TEMPLATENAME1 = "userbased-1";
	public static final String TEMPLATENAME2 = "userbased-2";
	public static final String SIMPLE_FILTER = "simple-filter";
	public static final String CATEGORY = "category";

	public static final List<String> values = Arrays.asList(new String[]{"Action", "Adventure"});

	public static final long STARTTIME = ENDTIME - (60 * 60 * 24);

	public static final ContentProvider getContentProvider()
	{
		return new ContentProvider(CP, TIME, true);
	}

	public static final RecommenderConfig getRecommenderConfig()
	{
		return new RecommenderConfig(CP, RECOMMENDER, COLLECTION, getCollectionFilter(), getTemplate(), INTERVAL, NEXT);
	}

	public static final Template getTemplate()
	{
		return new Template(TEMPLATENAME1, getModules());
	}

	public static final List<Module> getModules()
	{
		return Arrays.asList(new Module[]{new Module(MODULE1, getProperties())});
	}

	public static final List<Property> getProperties()
	{
		List<Property> properties = new ArrayList<Property>();
		properties.add(new Property(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME, "SIMILARITY_TANIMOTO_COEFFICIENT"));
		properties.add(new Property(TaskConfig.NUM_RECOMMENDATIONS, "100"));
		properties.add(new Property(TaskConfig.BOOLEAN_DATA, "true"));
		properties.add(new Property(TaskConfig.MAX_PREFS_PER_USER, "10"));
		properties.add(new Property(TaskConfig.MAX_SIMILARITRIES_PER_ITEM, "100"));
		properties.add(new Property(TaskConfig.MAX_CO_OCCURRENCES_PER_ITEM, "100"));
		return properties;
	}

	public static final CollectionFilter getCollectionFilter()
	{
		return new CollectionFilter(SIMPLE_FILTER, 0, ENDTIME, CATEGORY, values);
	}

	public static Collection getCollection()
	{
		return new Collection(CP, COLLECTION, Collection.Type.MOVIELENS.toString(), null, new OAISets(),
				new OAIMetadata(), -1);
	}
}
