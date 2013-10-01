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
import nl.gridline.zieook.model.Popularities;
import nl.gridline.zieook.model.Popularity;
import nl.gridline.zieook.model.PopularityTypes;

import org.jboss.resteasy.annotations.GZIP;

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * CollectionStatistics.java created 1 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@Path(Constants.STATISTICS)
public interface CollectionStatistics
{
	/**
	 * Get the top-n (count) of given ratings
	 * @param cp
	 * @param size
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection/rated-top/{cp}/{collection}")
	Groups topRated(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("start_time") Long startDate, @QueryParam("end_time") Long endDate,
			@QueryParam("size") Integer size);

	/**
	 * Get the top-n (count) of given ratings as collection items
	 * @param cp
	 * @param recommender
	 * @param size
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection/rated-top-collection/{cp}/{collection}")
	CollectionItems topRatedAsCollection(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("size") Integer size);

	/**
	 * Get the number of different sources for a recommender & and show the count
	 * @param cp
	 * @param recommender
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection/sources/{cp}/{collection}")
	Groups getSources(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("from") String from, @QueryParam("start_time") Long startTime,
			@QueryParam("end_time") Long endTime, @QueryParam("size") Integer size);

	/**
	 * The top-n of most used sources.
	 * @param cp
	 * @param collection
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection/sources-top/{cp}/{collection}")
	Groups topSources(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
			@QueryParam("size") Integer size);

	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection/popularity-types")
	PopularityTypes getPopularityTypes();

	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection/popularity/{cp}/{collection}/{type}")
	Popularity getPopularity(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@PathParam("type") String type, @QueryParam("item") Long item);

	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection/popularity-list/{cp}/{collection}/{type}")
	Popularities getPopularityList(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@PathParam("type") String type, @QueryParam("start_item") Long startItem, @QueryParam("size") Integer size);

	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection/most-popular/{cp}/{collection}/{type}")
	Popularity getMostPopular(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@PathParam("type") String type);

}
