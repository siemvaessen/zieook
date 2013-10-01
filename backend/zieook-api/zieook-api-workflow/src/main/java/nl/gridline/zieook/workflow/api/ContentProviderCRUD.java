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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.gridline.zieook.workflow.model.ContentProvider;
import nl.gridline.zieook.workflow.model.ContentProviders;

import org.jboss.resteasy.annotations.GZIP;

/**
 * CRUD on content providers
 * <p />
 * Project zieook-api-workflow<br />
 * ContentProvider.java created 10 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Path(Constants.APIPATH)
public interface ContentProviderCRUD
{

	/**
	 * Create a content provider, if it did not exist yet
	 * @param cp
	 * @return 200 if created, 304 if failed.
	 */
	@POST
	@Path("content-provider")
	@GZIP
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	Response createContentProvider(ContentProvider cp);

	@GET
	@Path("content-provider/{cp}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	ContentProvider readContentProvider(@PathParam("cp") String cp);

	@PUT
	@Path("content-provider/{cp}")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	Response updateContentProvider(@PathParam("cp") String name, ContentProvider cp);

	@DELETE
	@Path("content-provider/{cp}")
	// @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	Response deleteContentProvider(@PathParam("cp") String cp);

	@GET
	@Path("content-provider-list")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	ContentProviders getContentProviders();
}
