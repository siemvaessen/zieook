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

import nl.gridline.zieook.api.CollectionStatistics;
import nl.gridline.zieook.data.controller.DataController;
import nl.gridline.zieook.data.controller.StatisticsDataController;
import nl.gridline.zieook.exceptions.CollectionDoesNotExistException;
import nl.gridline.zieook.exceptions.ContentProviderDoesNotExist;
import nl.gridline.zieook.exceptions.DoesNotExists;
import nl.gridline.zieook.model.CollectionItems;
import nl.gridline.zieook.model.Groups;
import nl.gridline.zieook.model.Popularities;
import nl.gridline.zieook.model.Popularity;
import nl.gridline.zieook.model.PopularityTypes;

import org.jboss.resteasy.spi.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-data<br />
 * CollectionStatisticsImpl.java created 1 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class CollectionStatisticsImpl implements CollectionStatistics
{

	private static final Logger LOG = LoggerFactory.getLogger(CollectionStatisticsImpl.class);

	@Context
	private ServletContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.CollectionStatistics#topRated(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Integer)
	 */
	@Override
	public Groups topRated(String cp, String collection, Long startDate, Long endDate, Integer size)
	{
		LOG.debug("get sources <cp,collection> : <{},{}>", cp, collection);
		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);
		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}
		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		if (size == null)
		{
			size = 100;
		}

		return controller.topRated(cp, collection, startDate, endDate, size.intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.CollectionStatistics#topRatedAsCollection(java.lang.String, java.lang.String,
	 * java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public CollectionItems topRatedAsCollection(String cp, String collection, Long startTime, Long endTime, Integer size)
	{
		LOG.debug("get sources <cp,collection> : <{},{}>", cp, collection);
		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);
		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}
		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		if (size == null)
		{
			size = 100;
		}

		return controller.topRatedAsCollection(cp, collection, startTime, endTime, size.intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.CollectionStatistics#getSources(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Integer)
	 */
	@Override
	public Groups getSources(String cp, String collection, String from, Long startDate, Long endDate, Integer size)
	{
		LOG.debug("get sources <cp,collection> : <{},{}>", cp, collection);
		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);
		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}
		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		if (size == null)
		{
			size = 100;
		}

		return controller.getCollectionSources(cp, collection, from, startDate, endDate, size);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.CollectionStatistics#topSources(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Integer)
	 */
	@Override
	public Groups topSources(String cp, String collection, Long startTime, Long endTime, Integer size)
	{
		LOG.debug("get sources <cp,collection> : <{},{}>", cp, collection);
		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);
		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}
		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		if (size == null)
		{
			size = 100;
		}

		return controller.getCollectionTopSources(cp, collection, startTime, endTime, size.intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.CollectionStatistics#getPopularityTypes()
	 */
	@Override
	public PopularityTypes getPopularityTypes()
	{
		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		return controller.getPopularityTypes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.CollectionStatistics#getPopularity(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.Long)
	 */
	@Override
	public Popularity getPopularity(String cp, String collection, String type, Long item)
	{
		LOG.debug("getting popularity for: cp/collection/type on item = {}/{}/{} on {}", new Object[]{cp, collection,
				type, item});

		if (item == null)
		{
			throw new BadRequestException("'item' parameter missing");
		}

		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);
		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		Popularity result = controller.getPopularity(cp, collection, type, item);
		if (result == null)
		{
			throw new DoesNotExists("no popularity for this item");
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.CollectionStatistics#getPopularityList(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public Popularities getPopularityList(String cp, String collection, String type, Long startItem, Integer size)
	{
		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);
		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		if (size == null)
		{
			size = 100;
		}

		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);

		Popularities result = controller.getPopularities(cp, collection, type, startItem, size.intValue());
		if (result == null)
		{
			throw new DoesNotExists("no popularities found");
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.CollectionStatistics#getMostPopular(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Popularity getMostPopular(String cp, String collection, String type)
	{
		DataController datacontroller = (DataController) context.getAttribute(DataController.NAME);
		if (!datacontroller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!datacontroller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}
		StatisticsDataController controller = (StatisticsDataController) context
				.getAttribute(StatisticsDataController.NAME);
		Popularity result = controller.getMostPopularItem(cp, collection, type);
		if (result == null)
		{
			throw new DoesNotExists("no popularity found");
		}
		return result;

	}

}
