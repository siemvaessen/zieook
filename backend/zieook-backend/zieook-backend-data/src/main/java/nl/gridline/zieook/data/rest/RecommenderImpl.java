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
package nl.gridline.zieook.data.rest;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import nl.gridline.zieook.api.Recommender;
import nl.gridline.zieook.data.controller.DataController;
import nl.gridline.zieook.exceptions.ContentProviderDoesNotExist;
import nl.gridline.zieook.exceptions.RecommendationDoesNotExist;
import nl.gridline.zieook.exceptions.RecommenderDoesNotExist;
import nl.gridline.zieook.model.CollectionItems;
import nl.gridline.zieook.model.Recommendations;

import org.jboss.resteasy.spi.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-data<br />
 * RecommenderImpl.java created 7 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class RecommenderImpl implements Recommender
{

	private static final Logger LOG = LoggerFactory.getLogger(RecommenderImpl.class);

	@Context
	private ServletContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.RatingEvents#getRecommendationFor(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public Recommendations getRecommendationFor(String cp, String collection, String recommender, Long user, Long item,
			String source, int size, String spread)
	{
		LOG.debug(
				"get recommendations for: <cp,collection,recommender,user,id,source,size,spread> = <{},{},{},{},{},{},{},{}>",
				new Object[]{cp, collection, recommender, user, item, source, size, spread});

		// check the parameters:
		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		// not both user & item can be null:
		if (user == null && item == null)
		{
			throw new BadRequestException("No user and/or item id.");
		}

		// source defaults to no-source:
		if (source == null)
		{
			source = "no-source";
		}
		if (spread == null)
		{
			spread = "no-spread";
		}

		Recommendations result = controller.getRecommendationFor(cp, collection, recommender, user, item, source, size,
				spread);

		// no recommendations for the given parameters:
		if (result == null)
		{
			throw new RecommendationDoesNotExist(user, item);
		}
		return result;
	}

	@Override
	public CollectionItems getRecommendationAsItems(String cp, String collection, String recommender, Long user,
			Long item, String source, int size, String spread)
	{
		LOG.debug(
				"get recommendations for: <cp,collection,recommender,user,id,source,size,spread> = <{},{},{},{},{},{},{},{}>",
				new Object[]{cp, collection, recommender, user, item, source, size, spread});
		if (user == null && item == null)
		{
			throw new BadRequestException("No user and/or item id.");
		}

		// if (source == null)
		// {
		// throw new BadRequestException("No source given.");
		// }

		DataController controller = (DataController) context.getAttribute(DataController.NAME);

		CollectionItems result = controller.getRecommendationAsItems(cp, collection, recommender, user, item, source,
				size, spread);
		if (result == null)
		{
			throw new RecommendationDoesNotExist(user, item);
		}
		return result;
	}

}
