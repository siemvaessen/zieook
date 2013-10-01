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

import nl.gridline.zieook.model.Groups;

import org.jboss.resteasy.annotations.GZIP;

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * RecommenderStatistics.java created 1 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@Path(Constants.STATISTICS)
public interface RecommenderStatistics
{
	/**
	 * Get the number of different sources for a recommender & and show the count
	 * @param cp
	 * @param recommender
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("recommender/sources/{cp}/{recommender}")
	Groups getSources(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
			@QueryParam("from") String from, @QueryParam("start_date") Long startDate,
			@QueryParam("end_date") Long endDate, @QueryParam("size") Integer size);

	/**
	 * The top-n of most used sources.
	 * @param cp
	 * @param recommender
	 * @param startDate
	 * @param endDate
	 * @param size
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("recommender/sources-top/{cp}/{recommender}")
	Groups topSources(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
			@QueryParam("start_date") Long startDate, @QueryParam("end_date") Long endDate,
			@QueryParam("size") Integer size);

	/**
	 * The top-n of most used sources.
	 * @param cp
	 * @param recommender
	 * @param startDate
	 * @param endDate
	 * @param size
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("recommender/viewed-top/{cp}/{recommender}")
	Groups topViewed(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
			@QueryParam("start_date") Long startDate, @QueryParam("end_date") Long endDate,
			@QueryParam("size") Integer size);

}
