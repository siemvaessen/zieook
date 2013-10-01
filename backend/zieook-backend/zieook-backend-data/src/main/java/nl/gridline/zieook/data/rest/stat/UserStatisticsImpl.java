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
package nl.gridline.zieook.data.rest.stat;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import nl.gridline.zieook.api.UserStatistics;
import nl.gridline.zieook.data.controller.DataController;
import nl.gridline.zieook.data.controller.StatisticsDataController;
import nl.gridline.zieook.exceptions.ContentProviderDoesNotExist;
import nl.gridline.zieook.exceptions.RecommenderDoesNotExist;
import nl.gridline.zieook.model.CollectionItems;
import nl.gridline.zieook.model.Groups;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-data<br />
 * UserStatisticsImpl.java created 1 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class UserStatisticsImpl implements UserStatistics
{

	private static final Logger LOG = LoggerFactory.getLogger(UserStatisticsImpl.class);

	@Context
	private ServletContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.UserStatistics#topRecommended(java.lang.String, java.lang.String, long,
	 * java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public Groups topRecommended(String cp, String collection, long user, Long startTime, Long endTime, Integer size)
	{
		LOG.debug("get recommended <cp,recommender> : <{},{}>", cp, collection);
		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);
		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isRecommender(cp, collection))
		{
			throw new RecommenderDoesNotExist(cp, collection);
		}

		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		int sizeValue;
		if (size == null)
		{
			sizeValue = 100;
		}
		else
		{
			sizeValue = size.intValue();
		}

		return controller.topRecommended(cp, collection, user, startTime, endTime, sizeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.UserStatistics#topRecommendedAsCollection(java.lang.String, java.lang.String, long,
	 * java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public CollectionItems topRecommendedAsCollection(String cp, String collection, long user, Long startTime,
			Long endTime, Integer size)
	{
		LOG.debug("get recommended <cp,recommender> : <{},{}>", cp, collection);
		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);
		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isRecommender(cp, collection))
		{
			throw new RecommenderDoesNotExist(cp, collection);
		}

		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		return controller.topRecommendedAsCollection(cp, collection, user, startTime, endTime, size);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.UserStatistics#getTopViews(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public Groups getTopViews(String cp, String collection, Long user, Long startTime, Long endTime, Integer size)
	{
		LOG.debug("get top views <cp,recommender> : <{},{}>", cp, collection);

		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);
		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isRecommender(cp, collection))
		{
			throw new RecommenderDoesNotExist(cp, collection);
		}

		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		return controller.topViews(cp, collection, user, startTime, endTime, size);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.UserStatistics#getTopViewsAsCollection(java.lang.String, java.lang.String,
	 * java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public CollectionItems getTopViewsAsCollection(String cp, String collection, Long user, Long startTime,
			Long endTime, Integer size)
	{
		LOG.debug("get top views <cp,recommender> : <{},{}>", cp, collection);
		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);

		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isRecommender(cp, collection))
		{
			throw new RecommenderDoesNotExist(cp, collection);
		}

		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		return controller.topViewsAsCollection(cp, collection, user, startTime, endTime, size);

	}

}
