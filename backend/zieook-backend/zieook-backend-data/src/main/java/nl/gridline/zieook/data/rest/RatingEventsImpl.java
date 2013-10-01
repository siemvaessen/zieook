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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import nl.gridline.zieook.api.RatingEvents;
import nl.gridline.zieook.data.controller.DataController;
import nl.gridline.zieook.exceptions.CollectionDoesNotExistException;
import nl.gridline.zieook.exceptions.ContentProviderDoesNotExist;
import nl.gridline.zieook.exceptions.DoesNotExists;
import nl.gridline.zieook.exceptions.RatingDoesNotExist;
import nl.gridline.zieook.exceptions.RecommenderDoesNotExist;
import nl.gridline.zieook.model.CollectionItems;
import nl.gridline.zieook.model.Rating;
import nl.gridline.zieook.model.RatingFull;
import nl.gridline.zieook.model.Ratings;
import nl.gridline.zieook.model.RatingsFull;
import nl.gridline.zieook.model.Recommendeds;
import nl.gridline.zieook.model.UserView;
import nl.gridline.zieook.model.UserViews;
import nl.gridline.zieook.model.UserViewsFull;

import org.apache.commons.lang.NotImplementedException;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-data<br />
 * RatingEventsImpl.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class RatingEventsImpl implements RatingEvents
{

	private static final String GETRATING = "get rating[s]  <cp,collection> = <{},{}>";
	private static final String GETVIEW = "get view[s] <cp,recommender> = <{},{}>";

	private static final Logger LOG = LoggerFactory.getLogger(RatingEventsImpl.class);

	@Context
	private ServletContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.RatingEvents#setRecommendation(java.lang.String, java.lang.String,
	 * nl.gridline.zieook.model.UserRating)
	 */
	@Override
	public Response setRating(String cp, String collection, Rating rating)
	{
		LOG.debug("set rating <cp,collection> = <{},{}>", cp, collection);
		if (rating == null)
		{
			throw new BadRequestException("Rating cannot be null.");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		if (rating.getUser() == null)
		{
			throw new BadRequestException("'user'-id is missing in the rating");
		}

		if (rating.getItem() == null)
		{
			throw new BadRequestException("'item'-id is missing in the rating");
		}

		// check if the item exists...
		if (!controller.isCollectionItem(cp, collection, rating.getItem()))
		{
			throw new BadRequestException("item-id [" + rating.getItem() + "] does not exist");
		}

		rating.setDate(System.currentTimeMillis());
		// force the collection, in-case it's not set already:
		rating.setCollection(collection);

		controller.setRating(cp, collection, rating, TimeUnit.MILLISECONDS);

		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.RatingEvents#setView(java.lang.String, java.lang.String,
	 * nl.gridline.zieook.model.UserView)
	 */
	@Override
	public Response setView(String cp, String recommender, UserView view)
	{
		LOG.debug("set view <cp,collection,recommender> = <{},{}>", cp, recommender);
		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		controller.setView(cp, recommender, view, TimeUnit.SECONDS);

		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#getUserRatings(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Integer)
	 */
	@Override
	public Ratings getRatingsByDate(String cp, String collection, Long user, Long startTime, Long endTime, Integer size)
	{
		LOG.debug(GETRATING, cp, collection);

		if (user == null)
		{
			throw new BadRequestException("no <user> argument provided");
		}

		if (size == null)
		{
			size = 100;
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		return controller.getRatingsByDate(cp, collection, user.longValue(), startTime, endTime, size.intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#getMultiSourceRatingsByDate(java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public Ratings getMSrcRatingsByDate(String recommenders, Long user, Long starttime, Long endtime, Integer size)
	{
		LOG.debug("GET multi source ratings");
		if (user == null)
		{
			throw new BadRequestException("no <user> argument provided");
		}
		if (recommenders == null)
		{
			throw new BadRequestException("no <recommenders> argument provided");
		}
		if (size == null)
		{
			size = 100;
		}

		List<String[]> recommenderList = RestTools.getRecommenders(recommenders);
		if (recommenderList.size() == 0)
		{
			throw new BadRequestException(
					"failed to process parameter <recommenders> provider a json array with \"cp/recommender\" strings");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);

		Ratings result = controller
				.getMSrcRatings(recommenderList, user.longValue(), starttime, endtime, size.intValue());
		if (result == null)
		{
			// throw 404 exception?
		}
		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#getMSrcRatingsByDateFull(java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public RatingsFull getMSrcRatingsByDateFull(String recommenders, Long user, Long starttime, Long endtime,
			Integer size)
	{
		LOG.debug("GET multi source ratings");
		if (user == null)
		{
			throw new BadRequestException("no <user> argument provided");
		}
		if (recommenders == null)
		{
			throw new BadRequestException("no <recommenders> argument provided");
		}
		if (size == null)
		{
			size = 100;
		}

		List<String[]> recommenderList = RestTools.getRecommenders(recommenders);
		if (recommenderList.size() == 0)
		{
			throw new BadRequestException(
					"failed to process parameter <recommenders> provider a json array with \"cp/recommender\" strings");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);

		RatingsFull result = controller.getMSrcRatingsFull(recommenderList, user.longValue(), starttime, endtime,
				size.intValue());
		if (result == null)
		{
			// throw 404 exception?
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#getRatingsByDateFull(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public RatingsFull getRatingsByDateFull(String cp, String collection, Long user, Long starttime, Long endtime,
			Integer size)
	{
		throw new NotImplementedException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#getRatingsByValue(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Double, java.lang.Double, java.lang.Integer, java.lang.Long)
	 */
	@Override
	public Ratings getRatingsByValue(String cp, String collection, Long user, Double lowerBound, Double upperBound,
			Integer size, Long startItem)
	{
		LOG.debug(GETRATING, cp, collection);

		if (user == null)
		{
			throw new BadRequestException("no <user> argument provided");
		}

		if (size == null)
		{
			size = 100;
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		return controller.getRatingsByValue(cp, collection, user.longValue(), lowerBound, upperBound, size.intValue(),
				startItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#getUserAsCollection(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Integer)
	 */
	@Override
	public CollectionItems getRatingsByDateAsCollection(String cp, String collection, Long user, Long startTime,
			Long endTime, Integer size)
	{
		LOG.debug(GETRATING, cp, collection);

		if (user == null)
		{
			throw new BadRequestException("no <user> argument provided");
		}

		if (size == null)
		{
			size = 100;
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		return controller.getRatingsByDateAsCollection(cp, collection, user.longValue(), startTime, endTime,
				size.intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#getRatingsByIds(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	public Ratings getRatingsByIds(String cp, String collection, Long user, String items)
	{
		LOG.debug(GETRATING, cp, collection);
		if (user == null)
		{
			throw new BadRequestException("no <user> argument provided");
		}
		if (items == null)
		{
			throw new BadRequestException("no <items> argument provided");
		}
		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		List<Long> itemIds = new ArrayList<Long>();
		try
		{
			String[] itemsArr = items.split(",");
			if (itemsArr.length == 0)
			{
				throw new BadRequestException("'items' parameter should be a comma separated list of id's");
			}
			for (String i : itemsArr)
			{
				itemIds.add(Long.parseLong(i));
			}
		}
		catch (NumberFormatException e)
		{
			throw new BadRequestException("'items' parameter should be a comma separated list of id's");
		}

		Ratings result = controller.getRatingsByIds(cp, collection, user.longValue(), itemIds);
		if (result == null)
		{
			throw new DoesNotExists("no ratings for this request");
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#getRatingsByValueAsCollection(java.lang.String, java.lang.String,
	 * java.lang.Long, java.lang.Double, java.lang.Double, java.lang.Integer, java.lang.Long)
	 */
	@Override
	public CollectionItems getRatingsByValueAsCollection(String cp, String collection, Long user, Double lowerBound,
			Double upperBound, Integer size, Long startItem)
	{
		LOG.debug(GETRATING, cp, collection);

		if (user == null)
		{
			throw new BadRequestException("no <user> argument provided");
		}

		if (size == null)
		{
			size = 100;
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		return controller.getRatingsByValueAsCollection(cp, collection, user.longValue(), lowerBound, upperBound,
				size.intValue(), startItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#searchRatings(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.String, java.lang.Integer)
	 */
	@Override
	public Ratings searchRatings(String cp, String collection, Long user, Long startItem, String regexp, String q,
			Integer size)
	{
		if (user == null)
		{
			throw new BadRequestException("no <user> argument provided");
		}
		if (size == null)
		{
			size = 100;
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		if (q == null && regexp == null)
		{
			regexp = ".*";
		}
		else if (q != null)
		{
			// naive string to regexp converter:
			regexp = ".*" + createCaseInsensitive(q) + ".*";
		}

		return controller.searchUserRatings(cp, collection, user.longValue(), startItem, regexp, size.intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#searchRatingsAsCollection(java.lang.String, java.lang.String,
	 * java.lang.Long, java.lang.Long, java.lang.String, java.lang.Integer)
	 */
	@Override
	public CollectionItems searchRatingsAsCollection(String cp, String collection, Long user, Long startItem,
			String regexp, String q, Integer size)
	{
		if (user == null)
		{
			throw new BadRequestException("no <user> argument provided");
		}
		if (size == null)
		{
			size = 100;
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		if (q == null && regexp == null)
		{
			regexp = ".*";
		}
		else if (q != null)
		{
			// naive string to regexp converter:
			regexp = ".*" + createCaseInsensitive(q) + ".*";
		}

		return controller.searchUserRatingsAsCollectionItems(cp, collection, user.longValue(), startItem, regexp,
				size.intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#searchRatingsFull(java.lang.String, java.lang.Long, java.lang.Long,
	 * java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public RatingsFull searchRatingsFull(String recommenders, Long user, Long startItem, String regexp, String q,
			Integer size)
	{
		if (user == null)
		{
			throw new BadRequestException("no <user> argument provided");
		}
		if (recommenders == null)
		{
			throw new BadRequestException("no <recommenders> provided");
		}
		if (size == null)
		{
			size = 100;
		}

		if (q == null && regexp == null)
		{
			regexp = ".*";
		}
		else if (q != null)
		{
			// naive string to regexp converter:
			regexp = ".*" + createCaseInsensitive(q) + ".*";
		}

		List<String[]> recommenderList = RestTools.getRecommenders(recommenders);

		DataController controller = (DataController) context.getAttribute(DataController.NAME);

		// TODO implement startItem --- this is hard in a multi cp/recommender list. We would need the cp/item combination
		// to get this done. Or again implement a timebased browser, which is nice anyway.

		return controller.searchUserRatingsMSrc(recommenderList, user.longValue(), regexp, size.intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#deleteRating(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public Response deleteRating(String cp, String collection, Long user, Long item)
	{
		LOG.debug(GETRATING, cp, collection);

		if (user == null)
		{
			throw new BadRequestException("get paremeter missing <user>");
		}
		if (item == null)
		{
			throw new BadRequestException("get paremeter missing <item>");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		controller.deleteRating(cp, collection, user, item);

		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#deleteMSrcRatings(java.lang.String, java.lang.Long)
	 */
	@Override
	public Response deleteMSrcRatings(String recommenders, Long user)
	{

		if (user == null)
		{
			throw new BadRequestException("no <user> argument provided");
		}

		List<String[]> recommenderList = RestTools.getRecommenders(recommenders);
		if (recommenderList.size() == 0)
		{
			throw new BadRequestException(
					"failed to process parameter <recommenders> provider a json array with \"cp/recommender\" strings");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		controller.deleteRatings(recommenderList, user.longValue());

		return Response.ok().build();
	}

	@Override
	public Rating getRating(String cp, String collection, Long user, Long item)
	{
		LOG.debug(GETRATING, cp, collection);
		if (user == null)
		{
			throw new BadRequestException("get parameter missing <user>");
		}
		if (item == null)
		{
			throw new BadRequestException("get parameter missing <item>");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		Rating result = controller.getRating(cp, collection, user, item);
		if (result == null)
		{
			throw new RatingDoesNotExist("rating does not exist: {} <{}>", cp + "/" + collection, user + "," + item);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#getRatingFull(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public RatingFull getRatingFull(String cp, String collection, Long user, Long item)
	{
		throw new NotImplementedException();
	}

	@Override
	public UserViews getViews(String cp, String recommender, Long user, Long startdate, Long enddate, Integer size)
	{
		LOG.debug(GETVIEW, cp, recommender);
		DataController controller = (DataController) context.getAttribute(DataController.NAME);

		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		long start, end;
		if (startdate == null)
		{
			start = 0;
		}
		else
		{
			start = startdate * 1000; // row filter goes in milliseconds
		}

		if (enddate == null)
		{
			end = Long.MAX_VALUE;
		}
		else
		{
			end = enddate * 1000; // row filter goes in milliseconds
		}
		if (size == null)
		{
			size = 100;
		}

		UserViews result = controller.getViews(cp, recommender, user, start, end, size);

		if (result == null)
		{
			throw new DoesNotExists("now views for this request");
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#searchViews(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public UserViews searchViews(String cp, String recommender, Long user, Long startTime, Long endTime, String q,
			String regexp, Integer size)
	{
		if (user == null)
		{
			throw new BadRequestException("get parameter missing <user>");
		}
		if (startTime != null)
		{
			startTime = startTime * 1000;
		}
		if (endTime != null)
		{
			endTime = endTime * 1000;
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		if (q == null && regexp == null)
		{
			regexp = ".*";
		}
		// naive string to regexp converter:
		else if (q != null)
		{
			regexp = ".*" + createCaseInsensitive(q) + ".*";
		}

		if (size == null)
		{
			size = 100;
		}

		return controller.searchViews(cp, recommender, user.longValue(), startTime, endTime, regexp, size.intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#searchViewsAsCollection(java.lang.String, java.lang.String,
	 * java.lang.Long, java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public CollectionItems searchViewsAsCollection(String cp, String recommender, Long user, Long startTime,
			Long endTime, String regexp, String q, Integer size)
	{
		if (user == null)
		{
			throw new BadRequestException("get parameter missing <user>");
		}
		if (startTime != null)
		{
			startTime = startTime * 1000;
		}
		if (endTime != null)
		{
			endTime = endTime * 1000;
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		if (q == null && regexp == null)
		{
			regexp = ".*";
		}

		// naive string to regexp converter:
		else if (q != null)
		{
			regexp = ".*" + createCaseInsensitive(q) + ".*";
		}

		if (size == null)
		{
			size = 100;
		}

		return controller.searchViewsAsCollectionItems(cp, recommender, user.longValue(), startTime, endTime, regexp,
				size.intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#searchViewsFull(java.lang.String, java.lang.Long, java.lang.Long,
	 * java.lang.Long, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public UserViewsFull searchViewsFull(String recommenders, Long user, Long startTime, Long endTime, String regexp,
			String q, Integer size)
	{
		if (user == null)
		{
			throw new BadRequestException("get parameter missing <user>");
		}
		if (recommenders == null)
		{
			throw new BadRequestException("get parameter missing <recommender>");
		}

		if (startTime != null)
		{
			startTime = startTime * 1000;
		}
		if (endTime != null)
		{
			endTime = endTime * 1000;
		}

		List<String[]> recommenderList = RestTools.getRecommenders(recommenders);
		if (q == null && regexp == null)
		{
			regexp = ".*";
		}

		// naive string to regexp converter:
		else if (q != null)
		{
			regexp = ".*" + createCaseInsensitive(q) + ".*";
		}

		if (size == null)
		{
			size = 100;
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		return controller.searchViewsMSrc(recommenderList, user.longValue(), startTime, endTime, regexp, size.intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#getMultipleSourceViews(java.lang.String, java.lang.Long, java.lang.Long,
	 * java.lang.Long, java.lang.Integer)
	 */
	@Override
	public UserViews getMSrcViews(String recommenders, Long user, Long startTime, Long endTime, Integer size)
	{
		if (user == null)
		{
			throw new BadRequestException("parameter <user> missing");
		}
		if (recommenders == null)
		{
			throw new BadRequestException("parameter <recommenders> missing");
		}
		if (size == null)
		{
			size = 100;
		}
		List<String[]> recommenderList = RestTools.getRecommenders(recommenders);
		if (recommenderList.size() == 0)
		{
			throw new BadRequestException(
					"failed to process parameter <recommenders> provider a json array with \"cp/recommender\" strings");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);

		UserViews result = controller
				.getMSrcViews(recommenderList, user.longValue(), startTime, endTime, size.intValue());
		if (result == null)
		{
			// throw 404?
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#getFullViews(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public UserViewsFull getViewsFull(String cp, String recommender, Long user, Long startTime, Long endTime,
			Integer size)
	{
		LOG.debug(GETVIEW, cp, recommender);
		DataController controller = (DataController) context.getAttribute(DataController.NAME);

		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		long start, end;
		if (startTime == null)
		{
			start = 0;
		}
		else
		{
			start = startTime * 1000; // row filter goes in milliseconds
		}

		if (endTime == null)
		{
			end = Long.MAX_VALUE;
		}
		else
		{
			end = endTime * 1000; // row filter goes in milliseconds
		}
		if (size == null)
		{
			size = 100;
		}

		UserViewsFull result = controller.getFullViews(cp, recommender, user.longValue(), start, end, size.intValue());
		if (result == null)
		{
			throw new DoesNotExists("now views for this request");
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#getMultipleSourceViewsFull(java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public UserViewsFull getMSrcViewsFull(String recommenders, Long user, Long startTime, Long endTime, Integer size)
	{
		if (user == null)
		{
			throw new BadRequestException("parameter <user> missing");
		}
		if (recommenders == null)
		{
			throw new BadRequestException("parameter <recommenders> missing");
		}
		if (size == null)
		{
			size = 100;
		}
		List<String[]> recommenderList = RestTools.getRecommenders(recommenders);
		if (recommenderList.size() == 0)
		{
			throw new BadRequestException(
					"failed to process parameter <recommenders> provider a json array with \"cp/recommender\" strings");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);

		UserViewsFull result = controller.getMSrcViewsFull(recommenderList, user.longValue(), startTime, endTime,
				size.intValue());
		if (result == null)
		{
			// throw 404?
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#deleteViews(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public Response deleteViews(String cp, String recommender, Long user, Long start, Long end)
	{
		LOG.debug(GETVIEW, cp, recommender);
		if (user == null)
		{
			throw new BadRequestException("no valid <user> argument given");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);

		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		controller.deleteViews(cp, recommender, user, start, end);

		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#deleteMSrcViews(java.lang.String, java.lang.Long)
	 */
	@Override
	public Response deleteMSrcViews(String recommenders, Long user)
	{
		if (user == null)
		{
			throw new BadRequestException("no <user> argument provided");
		}

		List<String[]> recommenderList = RestTools.getRecommenders(recommenders);
		if (recommenderList.size() == 0)
		{
			throw new BadRequestException(
					"failed to process parameter <recommenders> provider a json array with \"cp/recommender\" strings");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		controller.deleteViews(recommenderList, user.longValue());

		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#getRecommends(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public Recommendeds getRecommends(String cp, String collection, Long user, Long startTime, Long endTime, Integer size)
	{

		if (user == null)
		{
			throw new BadRequestException("parameter missing <user>");
		}

		LOG.debug("get views <cp,collection> = <{},{}>", cp, collection);
		DataController controller = (DataController) context.getAttribute(DataController.NAME);

		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isRecommender(cp, collection))
		{
			throw new RecommenderDoesNotExist(cp, collection);
		}

		if (size == null)
		{
			size = 100;
		}

		try
		{
			return controller.getRecommended(cp, collection, user.longValue(), startTime, endTime, size.intValue());
		}
		catch (IOException e)
		{
			throw new InternalServerErrorException("failed to process request", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.RatingEvents#deleteRecommends(java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public Response deleteRecommends(String cp, String collection, Long user, Long start, Long end)
	{
		LOG.debug("delete views <cp,recommender> = <{},{}>", cp, collection);
		DataController controller = (DataController) context.getAttribute(DataController.NAME);

		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		return null;
	}

	private String createCaseInsensitive(String search)
	{
		StringBuilder b = new StringBuilder("(");
		for (int i = 0; i < search.length(); i++)
		{
			char c = search.charAt(i);
			if (Character.isLetter(c))
			{
				b.append('(').append(Character.toUpperCase(c)).append('|').append(Character.toLowerCase(c)).append(')');
			}
			else
			{
				b.append(b);
			}
		}
		b.append(")");
		String result = b.toString();
		LOG.debug("crafted regexp: '{}'", result);
		return result;
	}
}
