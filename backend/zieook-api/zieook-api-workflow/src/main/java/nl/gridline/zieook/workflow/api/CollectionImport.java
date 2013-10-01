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
package nl.gridline.zieook.workflow.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.gridline.zieook.workflow.model.Collection;
import nl.gridline.zieook.workflow.model.Collections;
import nl.gridline.zieook.workflow.model.OAIMetadata;
import nl.gridline.zieook.workflow.model.OAISets;
import nl.gridline.zieook.workflow.model.TaskConfigList;
import nl.gridline.zieook.workflow.model.TaskConfigMap;

import org.jboss.resteasy.annotations.GZIP;

/**
 * CRUD on collections meta data: collection import, update and delete - collection content read is covered in the
 * {@link nl.gridline.zieook.api.CollectionData} interface of the zieook-api-data
 * <p />
 * Project zieook-api-workflow<br />
 * CollectionImport.java created 7 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Path(Constants.APIPATH)
public interface CollectionImport
{
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection/oai_sets")
	OAISets getAvailableSets(@QueryParam("url") String url);

	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection/oai_metadata")
	OAIMetadata getAvailableMetaData(@QueryParam("url") String url);

	/**
	 * Create a collection based on the posted collection object. A collection might point to a local directory, a link
	 * or wait for a data upload
	 * @param cp
	 */
	@POST
	@GZIP
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection/{cp}")
	Response createCollectionMeta(@PathParam("cp") String cp, Collection collection);

	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection/{cp}/{collection}")
	Collection readCollectionMeta(@PathParam("cp") String cp, @PathParam("collection") String collection);

	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection-list/{cp}")
	Collections readCollectionsMeta(@PathParam("cp") String cp);

	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection-list")
	Collections readCollectionsMeta();

	/**
	 * Update collection meta data
	 * @param cp - content provider
	 * @param collectionname - collection name
	 * @param collection - collection meta data
	 */
	@PUT
	@GZIP
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection-meta/{cp}/{collection}")
	Response updateCollectionMeta(@PathParam("cp") String cp, @PathParam("collection") String collectionname,
			Collection collection);

	/**
	 * Update collection data by providing a file upload
	 * @param cp content provider
	 * @param collection collection name
	 * @param type one of [user,rating,collection]
	 */
	@PUT
	@GZIP
	@Consumes({"binary/octet-stream"})
	@Path("collection/{cp}/{collection}/{part}")
	Response updateCollectionData(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@PathParam("part") String part, @Context HttpServletRequest request);

	/**
	 * Delete a collection from the server
	 * @param cp - content provider
	 * @param collection - collection name
	 */
	@DELETE
	@GZIP
	@Path("collection/{cp}/{collection}")
	Response deleteCollection(@PathParam("cp") String cp, @PathParam("collection") String collection);

	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection-import/{cp}/{collection}")
	TaskConfigMap collectionImportState(@PathParam("cp") String cp, @PathParam("collection") String collection);

	/**
	 * @param cp
	 * @param collection
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("collection-implist/{cp}/{collection}")
	TaskConfigList collectionImportList(@PathParam("cp") String cp, @PathParam("collection") String collection,
			@QueryParam("start") Long start, @QueryParam("end") Long end);

	@PUT
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("collection-schedule/{cp}/{collection}")
	Response scheduleCollection(@PathParam("cp") String cp, @PathParam("collection") String collection, String date);

}
