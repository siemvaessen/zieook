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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import nl.gridline.zieook.workflow.model.ScheduledTasks;

import org.jboss.resteasy.annotations.GZIP;

/**
 * collection of rest requests that show the state of the system
 * <p />
 * Project zieook-api-workflow<br />
 * SystemState.java created 8 feb. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@Path(Constants.APIPATH)
public interface SystemState
{

	@GET
	@GZIP
	@Path("/executing")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	ScheduledTasks getExecutingTasks(@QueryParam("start_time") Long start, @QueryParam("end_time") Long end,
			@QueryParam("size") Integer size);

	@GET
	@GZIP
	@Path("/scheduled")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	ScheduledTasks getScheduledTasks(@QueryParam("start_time") Long start, @QueryParam("end_time") Long end,
			@QueryParam("size") Integer size);

	@GET
	@GZIP
	@Path("/executed")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	ScheduledTasks getExecutedTasks(@QueryParam("start_time") Long start, @QueryParam("end_time") Long end,
			@QueryParam("size") Integer size);

}