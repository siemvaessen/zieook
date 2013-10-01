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
package nl.gridline.zieook.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.gridline.zieook.model.CollectionItems;
import nl.gridline.zieook.model.Rating;
import nl.gridline.zieook.model.RatingFull;
import nl.gridline.zieook.model.Ratings;
import nl.gridline.zieook.model.RatingsFull;
import nl.gridline.zieook.model.Recommendeds;
import nl.gridline.zieook.model.UserView;
import nl.gridline.zieook.model.UserViews;
import nl.gridline.zieook.model.UserViewsFull;

import org.jboss.resteasy.annotations.GZIP;

/**
 * The main interface for all rating events
 * data log events:
 * <ul>
 * <li><b>rate</b> a user rates an item</li>
 * <li><b>view</b> a user views a recommended item</li>
 * <li><b>recommend</b> a user got a recommendation</li>
 * <ul>
 * <b>remarks</b><br />
 * The interface does not support complete CRUD on all elements because: <br />
 * <b>recommend</b> is handled in the backend<br />
 * <p />
 * Project zieook-api-data<br />
 * RatingEvents.java created 2 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Path(Constants.APIPATH)
public interface RatingEvents
{

	/**
	 * When an item is recommended, this method will log the event.
	 * @param cp the content provider
	 * @param collection the collection
	 * @param rating the user rating object containing: user,item,rating and an optional source
	 */
	@POST
	@GZIP
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("rating-rate/{cp}/{collection}")
	Response setRating(@PathParam("cp") String cp, @PathParam("collection") String collection, Rating rating);

	/**
	 * List the ratings of a given user starting at a given item
	 * @param cp content provider
	 * @param collection collection
	 * @param user user id
	 * @param from start item id (if null it will start from the beginnning)
	 * @param size number of amounts to return (defaults to 100)
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("ratings-bydate/{cp}/{collection}")
	Ratings getRatingsByDate(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("start_time") Long starttime, @QueryParam("end_time") Long endtime,
			@QueryParam("size") Integer size);

	/**
	 * return ratings from multiple sources within a given time range
	 * @param recommenders a JSON array containing {@code cp/recommender} pairs
	 * @param user user id
	 * @param starttime star time in seconds
	 * @param endtime end time in seconds
	 * @param size max number of items to return
	 * @return a list of ratings (if found)
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("ratings-bydate")
	Ratings getMSrcRatingsByDate(@QueryParam("recommenders") String recommenders, @QueryParam("user") Long user,
			@QueryParam("start_time") Long starttime, @QueryParam("end_time") Long endtime,
			@QueryParam("size") Integer size);

	/**
	 * search ratings given a regular expression or a simple search string, and return ratings
	 * @param cp content provider name
	 * @param collection collection name
	 * @param user user id
	 * @param startItem optional start item
	 * @param regexp regular expression
	 * @param q a search string, the string will be wrapped in a <em>.*q.*</em> and be made case insensitive
	 * @param size max number of items to return (defaults to 100)
	 * @return a list of ratings (if found)
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("ratings-search/{cp}/{collection}")
	Ratings searchRatings(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("start-item") Long startItem, @QueryParam("regexp") String regexp,
			@QueryParam("q") String q, @QueryParam("size") Integer size);

	/**
	 * search ratings given a regular expression or a simple search string, and return collection items
	 * @param cp content provider
	 * @param collection collection name
	 * @param user user id
	 * @param startItem optional start item
	 * @param regexp regular expression
	 * @param q a search string, the string will be wrapped in a <em>.*q.*</em> and be made case insensitive
	 * @param size max number of items to return (defaults to 100)
	 * @return a list of collection items
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("ratings-search-ascollection/{cp}/{collection}")
	CollectionItems searchRatingsAsCollection(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("start-item") Long startItem, @QueryParam("regexp") String regexp,
			@QueryParam("q") String q, @QueryParam("size") Integer size);

	/**
	 * search for ratings for a user in all given recommenders, recommenders is a json array of <em>cp/recommender</em>
	 * pairs
	 * @param recommenders <em>cp/recommender</em> pairs
	 * @param user user id
	 * @param startItem optional item to start from
	 * @param regexp regular expression
	 * @param q a search string, the string will be wrappen in a <em>.*q.*</em> and be made case insensitive
	 * @param size max number of items to return (defaults to 100)
	 * @return a list of ratings containing the items as well.
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("ratings-search")
	RatingsFull searchRatingsFull(@QueryParam("recommenders") String recommenders, @QueryParam("user") Long user,
			@QueryParam("start-item") Long startItem, @QueryParam("regexp") String regexp, @QueryParam("q") String q,
			@QueryParam("size") Integer size);

	/**
	 * search for ratings within a time limit and maximum number of results. Using multiple <em>cp/recommender</em>s for
	 * a single user
	 * @param recommenders <em>cp/recommender</em> pairs as a json array
	 * @param user user id
	 * @param starttime start time
	 * @param endtime end time
	 * @param size max number of items to return (default set to 100)
	 * @return a list of ratings containing the items as well.
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("ratings-bydate-full")
	RatingsFull getMSrcRatingsByDateFull(@QueryParam("recommenders") String recommenders, @QueryParam("user") Long user,
			@QueryParam("start_time") Long starttime, @QueryParam("end_time") Long endtime,
			@QueryParam("size") Integer size);

	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("ratings-bydate-full/{cp}/{collection}")
	RatingsFull getRatingsByDateFull(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("start_time") Long starttime, @QueryParam("end_time") Long endtime,
			@QueryParam("size") Integer size);

	/**
	 * Return ratings by a list of comma separated item id's
	 * @param cp content provider
	 * @param collection collection name
	 * @param user user id
	 * @param items a list of comma separated items
	 * @return a list of ratings, or a 404 if no ratings are found.
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("ratings-by-items/{cp}/{collection}")
	Ratings getRatingsByIds(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("items") String items);

	/**
	 * Return the user ratings as a list of collection items
	 * @param cp content provider
	 * @param collection collection name
	 * @param user user id
	 * @param item item id
	 * @return collection items, ordered by date, with the newest first
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("ratings-bydate-collection/{cp}/{collection}")
	CollectionItems getRatingsByDateAsCollection(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("start_time") Long starttime, @QueryParam("end_time") Long endtime,
			@QueryParam("size") Integer size);

	/**
	 * Return the user ratings limited with an upper / lower bound of the given rating. sorted on item id
	 * @param cp content provider
	 * @param collection collection name
	 * @param user user id
	 * @param lowerBound rating lower bound (optional)
	 * @param upperBound rating uper bount (optional)
	 * @param size the amount of items to return
	 * @param from the item id to start at.
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("ratings-byrating/{cp}/{collection}")
	Ratings getRatingsByValue(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("lower_bound") Double lowerBound,
			@QueryParam("upper_bound") Double upperBound, @QueryParam("size") Integer size, @QueryParam("from") Long from);

	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("ratings-byrating-collection/{cp}/{collection}")
	CollectionItems getRatingsByValueAsCollection(@PathParam("cp") String cp,
			@PathParam("collection") String collection, @QueryParam("user") Long user,
			@QueryParam("lower_bound") Double lowerBound, @QueryParam("upper_bound") Double upperBound,
			@QueryParam("size") Integer size, @QueryParam("from") Long from);

	/**
	 * Get a specific rating
	 * @param cp
	 * @param collection
	 * @param user
	 * @param item
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("rating-rate/{cp}/{collection}")
	Rating getRating(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("item") Long item);

	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("rating-rate-full/{cp}/{collection}")
	RatingFull getRatingFull(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("item") Long item);

	/**
	 * Undo a rating, deletes a record from the database
	 * @param cp
	 * @param collection
	 * @param user
	 * @param item
	 * @return
	 */
	@DELETE
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("rating-rate/{cp}/{collection}")
	Response deleteRating(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("item") Long item);

	/**
	 * delete ratings for the given user from all given cp/recommenders
	 * @param recommenders
	 * @param user
	 * @return
	 */
	@DELETE
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("ratings")
	Response deleteMSrcRatings(@QueryParam("recommenders") String recommenders, @QueryParam("user") Long user);

	/**
	 * When a recommended item is clicked this event is able to log such clicks
	 * @param cp the content provider
	 * @param collection
	 * @param recommender the recommender
	 * @param view the UserView data
	 */
	@POST
	@GZIP
	@Path("item-view/{cp}/{recommender}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	Response setView(@PathParam("cp") String cp, @PathParam("recommender") String recommender, UserView view);

	/**
	 * Delete a set of views between within a given time range
	 * @param cp content provider
	 * @param recommender recommender id
	 * @param user user id
	 * @param startTime start time (in seconds)
	 * @param endTime end time (in seconds)
	 * @return 20x if succeed
	 */
	@DELETE
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("item-view/{cp}/{recommender}")
	Response deleteViews(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
			@QueryParam("user") Long user, @QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime);

	@DELETE
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("item-view")
	Response deleteMSrcViews(@QueryParam("recommenders") String recommenders, @QueryParam("user") Long user);

	/**
	 * Get a list of views within a time range and/or with a given size
	 * @param cp content provider
	 * @param recommender recommender id
	 * @param user user id
	 * @param startTime start time in seconds
	 * @param endTime end time in seconds
	 * @param size maximum amount of elements to return (defaults to 100)
	 * @return list of data log items, sorted by date (newest first)
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("item-view-list/{cp}/{recommender}")
	UserViews getViews(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
			@QueryParam("user") Long user, @QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("size") Integer size);

	/**
	 * Search for views given a regular expression or a simple search string. Results can be further limited with a time
	 * range.
	 * @param cp content provider
	 * @param recommender recommender name
	 * @param user user id
	 * @param startTime optional start time in seconds
	 * @param endTime optional end time in seconds
	 * @param regexp regular expression
	 * @param q search string, will be wrapped in <em>.*q.*</em> and be made case insensitive
	 * @param size maximum number of items to return (defaults to 100)
	 * @return a list over user views
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("item-view-search/{cp}/{recommender}")
	UserViews searchViews(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
			@QueryParam("user") Long user, @QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("q") String q, @QueryParam("regexp") String regexp, @QueryParam("size") Integer size);

	/**
	 * Search for views given a regular expression or a simple search string. Results can be further limited with a time
	 * range.
	 * @param cp content provider
	 * @param recommender recommender name
	 * @param user user id
	 * @param startTime optional start time in seconds
	 * @param endTime optional end time in seconds
	 * @param regexp regular expression
	 * @param q search string, will be wrapped in <em>.*q.*</em> and be made case insensitive
	 * @param size maximum number of items to return (defaults to 100)
	 * @return a list of collection items
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("item-view-search-collection/{cp}/{recommender}")
	CollectionItems searchViewsAsCollection(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
			@QueryParam("user") Long user, @QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("regexp") String regexp, @QueryParam("q") String q, @QueryParam("size") Integer size);

	/**
	 * Search for views given a regular expression or a simple search string. Results can be expanded by adding more
	 * cp/recommender pairs to the {@code recommenders} argument, end limited by adding a start and/or and time
	 * @param recommenders a JSON array of <em>cp/recommender</em> pairs
	 * @param user user id
	 * @param startTime optional start time in seconds
	 * @param endTime optional end time in seconds
	 * @param regexp regular expression
	 * @param q simple search string
	 * @param size maximum number of items to return (defaults to 100)
	 * @return a list of user views with the full collection items included.
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("item-view-search")
	UserViewsFull searchViewsFull(@QueryParam("recommenders") String recommenders, @QueryParam("user") Long user,
			@QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("regexp") String regexp, @QueryParam("q") String q, @QueryParam("size") Integer size);

	/**
	 * Get a list of views within a time range and/or with a given size, for one or more recommenders
	 * @param recommenders list of recommenders, must be a combination of cp/recommender, this can be a comma separated
	 *           list of cp/recommender. Or a json array with cp/recommender - be sure to url encode the string properly
	 * @param user user id
	 * @param startTime start time in seconds
	 * @param endTime end time in seconds
	 * @param size maximum amount of elements to returns (defaults to 100)
	 * @return a list of data log items, sort by date (newest first)
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("item-view-list")
	UserViews getMSrcViews(@QueryParam("recommenders") String recommenders, @QueryParam("user") Long user,
			@QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("size") Integer size);

	/**
	 * Get a list of views within a time range and a given size. results contain the <em>view</em> and the full meta data
	 * of the items involved
	 * @param cp content provider
	 * @param recommender recommender name
	 * @param user user id
	 * @param startTime optional start time
	 * @param endTime optional end time
	 * @param size maximum amount of elements to return (defaults to 100)
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("item-view-list-full/{cp}/{recommender}")
	UserViewsFull getViewsFull(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
			@QueryParam("user") Long user, @QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("size") Integer size);

	/**
	 * Get a list of views within a time range and a given size limit, for one or more recommenders. Results contain the
	 * <em>view</em> data and the full meta data of the items involved.
	 * @param recommenders list of recommenders, must be a combination of cp/recommender, this can be a comma separated
	 *           list of cp/recommender. Or a json array with cp/recommender - be sure to url encode the string properly
	 * @param user user id
	 * @param startTime start time in seconds
	 * @param endTime end time in seconds
	 * @param size maximum amount of elements to returns (defaults to 100)
	 * @return a list of data log items, sort by date (newest first)
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("item-view-list-full")
	UserViewsFull getMSrcViewsFull(@QueryParam("recommenders") String recommenders, @QueryParam("user") Long user,
			@QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("size") Integer size);

	/**
	 * returns the list of given recommendations for a user, within a time range and / or size limit
	 * @param cp content provider
	 * @param collection recommender
	 * @param user user id
	 * @param startTime start time in seconds
	 * @param endTime end time in seconds
	 * @param size maximum amount of elements to return (defaults to 100)
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("item-recommended-list/{cp}/{collection}")
	Recommendeds getRecommends(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("size") Integer size);

	/**
	 * Delete the recommended history log for a user, within a time limit
	 * @param cp content provider
	 * @param collection recommender id
	 * @param user user id
	 * @param startTime start time in seconds
	 * @param endTime end time in seconds
	 * @return
	 */
	@DELETE
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("item-recommended-list/{cp}/{collection}")
	Response deleteRecommends(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime);

}
