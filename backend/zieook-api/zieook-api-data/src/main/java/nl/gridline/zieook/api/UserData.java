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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.gridline.zieook.model.User;
import nl.gridline.zieook.model.Users;

import org.jboss.resteasy.annotations.GZIP;

/**
 * Zie ook user data requests
 * <p />
 * Project zieook-api-data<br />
 * UserData.java created 2 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Path(Constants.APIPATH)
public interface UserData
{

	@GET
	@Path("user-activity")
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	// returns User or Users
	Object getUserActivity(@QueryParam("recommenders") String recommenders, @QueryParam("user") Long user,
			@QueryParam("users") String users);

	@GET
	@Path("user/{cp}")
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	User getUser(@PathParam("cp") String cp, @QueryParam("id") Long user);

	@GET
	@Path("user/{cp}")
	@GZIP
	@Produces("application/rdf+xml")
	String getUserRDF(@PathParam("cp") String cp, @QueryParam("id") Long user);

	@GET
	@Path("user-list/{cp}")
	@GZIP
	@Produces("application/rdf+xml")
	String getUsersRDF(@PathParam("cp") String cp, @QueryParam("from") Long from);

	@GET
	@Path("user-list/{cp}")
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	Users getUsers(@PathParam("cp") String cp, @QueryParam("from") Long from);

	@GET
	@Path("user-exists/{cp}")
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	String isUser(@PathParam("cp") String cp, @QueryParam("id") Long id);

	@POST
	@Path("user/{cp}")
	@GZIP
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	Response addUser(@PathParam("cp") String cp, User user);

	@PUT
	@Path("user/{cp}")
	@GZIP
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	Response putUser(@PathParam("cp") String cp, @QueryParam("id") Long id, User user);

	@DELETE
	@Path("user/{cp}")
	@GZIP
	Response removeUser(@PathParam("cp") String cp, @QueryParam("id") Long id);
}
