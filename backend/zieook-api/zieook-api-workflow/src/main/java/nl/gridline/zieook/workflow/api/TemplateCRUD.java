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

import nl.gridline.zieook.workflow.model.Template;
import nl.gridline.zieook.workflow.model.Templates;

import org.jboss.resteasy.annotations.GZIP;

/**
 * CRUD on templates
 * <p />
 * Project zieook-api-workflow<br />
 * Template.java created 4 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Path(Constants.APIPATH)
public interface TemplateCRUD
{
	/**
	 * Creates a template
	 * @param template
	 */
	@POST
	@Path("template/")
	@GZIP
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	Response createTemplate(Template template);

	/**
	 * Reads a template for the given <tt>name</tt>
	 * @param name
	 * @return
	 */
	@GET
	@Path("template/{name}")
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	Template getTemplate(@PathParam("name") String name);

	/**
	 * Updates a template by the given <tt>name</tt> <em>even the name can be changed</em>
	 * @param name
	 * @param template
	 */
	@PUT
	@Path("template/{name}")
	@GZIP
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	Response updateTemplate(@PathParam("name") String name, Template template);

	/**
	 * Deletes a template by the given <tt>name</tt>
	 * @param name
	 */
	@DELETE
	@Path("template/{name}")
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	Response deleteTemplate(@PathParam("name") String name);

	@GET
	@Path("template/")
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	Templates listTemplates();

}
