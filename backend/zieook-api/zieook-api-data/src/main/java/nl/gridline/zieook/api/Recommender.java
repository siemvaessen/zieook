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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import nl.gridline.zieook.model.CollectionItems;
import nl.gridline.zieook.model.Recommendations;

import org.jboss.resteasy.annotations.GZIP;

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * RecommenderStats.java created 2 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Path(Constants.RECOMMENDER)
public interface Recommender
{
	/**
	 * Return a list of recommendations for a given recommender, user and item
	 * @param cp the content provider
	 * @param recommender the recommender to use
	 * @param user the user id
	 * @param item the item id
	 * @param source an arbitrary source given by the content provider to track back the rating
	 * @param size the amount of recommended items to return (the default is 10)
	 * @returnWrapped(nl.gridline.zieook.model.Ratings)
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("/{cp}/{collection}/{recommender}")
	Recommendations getRecommendationFor(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@PathParam("recommender") String recommender, @QueryParam("user") Long user, @QueryParam("item") Long item,
			@QueryParam("source") String source, @QueryParam("size") @DefaultValue("10") int size,
			@QueryParam("spread") String spread);

	/**
	 * @param cp
	 * @param collection
	 * @param recommender
	 * @param user
	 * @param item
	 * @param source
	 * @param size
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("items/{cp}/{collection}/{recommender}")
	CollectionItems getRecommendationAsItems(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@PathParam("recommender") String recommender, @QueryParam("user") Long user, @QueryParam("item") Long item,
			@QueryParam("source") String source, @QueryParam("size") @DefaultValue("10") int size, String spread);
}
