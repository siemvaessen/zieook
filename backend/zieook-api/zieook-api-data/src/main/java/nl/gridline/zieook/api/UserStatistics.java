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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import nl.gridline.zieook.model.CollectionItems;
import nl.gridline.zieook.model.Groups;

import org.jboss.resteasy.annotations.GZIP;

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * UserStatistics.java created 1 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@Path(Constants.STATISTICS)
public interface UserStatistics
{

	/**
	 * Get the top-n most recommended items to user
	 * @param cp content provider
	 * @param recommender recommender identifier
	 * @param user user identifier
	 * @param size number of items to return
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("user/recommended/{cp}/{collection}/{user}")
	Groups topRecommended(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@PathParam("user") long user, @QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("size") Integer size);

	/**
	 * Get the top-n most recommended items to user and return them as collection items
	 * @param cp content provider
	 * @param recommender recommender identifier
	 * @param user user identifier
	 * @param size number of items to return
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("user/recommended-collection/{cp}/{collection}/{user}")
	CollectionItems topRecommendedAsCollection(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@PathParam("user") long user, @QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("size") Integer size);

	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("user/viewed/{cp}/{collection}/{user}")
	Groups getTopViews(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("size") Integer size);

	/**
	 * @param cp
	 * @param collection
	 * @param user
	 * @param startTime
	 * @param endTime
	 * @param size
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("user/viewed-collection/{cp}/{collection}/{user}")
	CollectionItems getTopViewsAsCollection(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("user") Long user, @QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("size") Integer size);

	// TODO: rated -> show how many items a user has rated in a collection

}
