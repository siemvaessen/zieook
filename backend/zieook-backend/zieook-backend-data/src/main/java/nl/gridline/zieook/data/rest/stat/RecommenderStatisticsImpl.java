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

import nl.gridline.zieook.api.RecommenderStatistics;
import nl.gridline.zieook.data.controller.DataController;
import nl.gridline.zieook.data.controller.StatisticsDataController;
import nl.gridline.zieook.exceptions.ContentProviderDoesNotExist;
import nl.gridline.zieook.exceptions.RecommenderDoesNotExist;
import nl.gridline.zieook.model.Groups;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-data<br />
 * RecommenderStatisticsImpl.java created 1 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class RecommenderStatisticsImpl implements RecommenderStatistics
{

	private static final Logger LOG = LoggerFactory.getLogger(RecommenderStatisticsImpl.class);

	@Context
	private ServletContext context;

	@Override
	public Groups getSources(String cp, String recommender, String from, Long startDate, Long endDate, Integer size)
	{
		LOG.debug("get sources <cp,recommender> : <{},{}>", cp, recommender);
		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);
		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		if (size == null)
		{
			size = 100;
		}

		return controller.getSources(cp, recommender, from, startDate, endDate, size.intValue());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RecommenderStatistics#topSources(java.lang.String, java.lang.String,
	 * java.lang.Integer, java.lang.Long, java.lang.Long)
	 */
	@Override
	public Groups topSources(String cp, String recommender, Long startDate, Long endDate, Integer size)
	{
		LOG.debug("get top sources <cp,recommender> : <{},{}>", cp, recommender);

		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);
		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		if (size == null)
		{
			size = 100;
		}

		return controller.getTopSources(cp, recommender, startDate, endDate, size.intValue());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RecommenderStatistics#topViewed(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Integer)
	 */
	@Override
	public Groups topViewed(String cp, String recommender, Long startDate, Long endDate, Integer size)
	{
		LOG.debug("get top views <cp,recommender> : <{},{}>", cp, recommender);

		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);
		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		if (size == null)
		{
			size = 100;
		}

		return controller.getTopViewed(cp, recommender, startDate, endDate, size.intValue());

	}
}
