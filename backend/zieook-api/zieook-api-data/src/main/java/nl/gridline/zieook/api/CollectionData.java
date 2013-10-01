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

import nl.gridline.zieook.model.Categories;
import nl.gridline.zieook.model.CollectionItem;
import nl.gridline.zieook.model.CollectionItems;

import org.jboss.resteasy.annotations.GZIP;

/**
 * Most requests on collections data are managed by this interface
 * Except the requests that handle OAI-PMH, these will be processed separately
 * <p />
 * Project zieook-api-data<br />
 * CollectionData.java created 2 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Path(Constants.APIPATH)
public interface CollectionData
{
	/**
	 * Get the categories from a given collection for a given key
	 * @param cp the content provider
	 * @param collection the collection
	 * @param form the id for the next set of categories for the given parameters
	 * @returnWrapped(nl.gridline.zieook.model.Categories)
	 * @return a list of categories
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection-categories/{cp}/{collection}")
	Categories getCollectionCategories(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("from") String from);

	/**
	 * Get the meta data from a single item
	 * @param cp the content provider
	 * @param collection the collection
	 * @param item item identifier
	 * @returnWrapped(java.util.String)
	 * @return a String containing the meta data item in the exact same way it was harvested or uploaded
	 */
	@GET
	@GZIP
	@Produces("text/plain")
	@Path("collection-raw/{cp}/{collection}")
	String getItemRaw(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("item") Long item);

	/**
	 * Get the interpreted version of a collection item
	 * @param cp content provider
	 * @param collection collection name
	 * @param id item id
	 * @return
	 */
	@GET
	@GZIP
	@Path("collection-intr/{cp}/{collection}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	CollectionItem getItem(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("item") Long item);

	@GET
	@GZIP
	@Path("collection-items/{cp}/{collection}")
	CollectionItems getItems(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("items") String items);

	/**
	 * return a list of 100 collection items, starting at a given start key
	 * @param cp content provider name
	 * @param collection collection name
	 * @param from start id
	 * @return list of collection items
	 */
	@GET
	@GZIP
	@Path("collection-list/{cp}/{collection}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	CollectionItems getItems(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("size") Integer size, @QueryParam("from") Long from);

	/**
	 * {@code search} collection items using the title. The {@code title-query} is a regexp search wrapped in <em>.*</em>
	 * when you use {@code title-regexp} is used the string is interpreted as a regexp.
	 * @param cp content provider
	 * @param collection collection name
	 * @param titleQuery
	 * @param titleRegExp
	 * @return
	 */
	@GET
	@GZIP
	@Path("collection-search/{cp}/{collection}")
	CollectionItems searchItems(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("title-query") String titleQuery, @QueryParam("title-regexp") String titleRegExp,
			@QueryParam("size") Integer size);
}
