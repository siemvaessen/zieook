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

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.gridline.zieook.api.Constants;
import nl.gridline.zieook.data.controller.DataController;
import nl.gridline.zieook.data.model.DataControllerState;

import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A very basic status servlet
 * <p />
 * Project zieook-backend-data<br />
 * StatusServlet.java created 15 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Path("/")
public class ZieOokStatus
{

	private static final Logger LOG = LoggerFactory.getLogger(ZieOokStatus.class);

	@Context
	private ServletContext context;

	@GET
	@Path(Constants.APIPATH + "status")
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public DataControllerState getStatus(@QueryParam("page") String page)
	{
		LOG.debug("get data controller state page <page> = <{}>", page);
		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (controller != null)
		{
			return controller.getState();
		}
		else
		{
			return null;
		}

		// org.jboss.resteasy.plugins.stats.RegistryEntry

	}

	/**
	 * Web browsers try to get the favicon, this will at least not cause an error & stacktrace everytime the resource is
	 * requested
	 * @return
	 */
	@GET
	@Path("/favicon.ico")
	public Response getFavicon()
	{
		return Response.noContent().build();
	}

}
