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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.gridline.zieook.workflow.model.StatisticsState;

import org.jboss.resteasy.annotations.GZIP;

/**
 * [purpose]
 * <p />
 * Project zieook-api-workflow<br />
 * StatisticsScheduler.java created 31 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Path(Constants.APIPATH)
public interface StatisticsScheduler
{
	/**
	 * Run the statistics map/reduce tasks for the given content provider now
	 * @param cp a content provider
	 * @return <tt>202 [accepted]</tt> the tasks is started in the background, <tt>304 [not modified</tt> if the task is
	 *         already running, <tt>404 [not found]</tt> if the content provider does not exist.
	 */
	@POST
	@Path("statistics/run/{cp}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	Response runStatistics(@PathParam("cp") String cp, String date);

	/**
	 * Schedule the statistics map/reduce task for the given content provider once every 24 hours, the actual update time
	 * depends on how busy the machine is
	 * @param cp a content provider
	 * @return <tt>202 [accepted]</tt>, <tt>304 [not modified] </tt> if it is already scheduled, <tt>404 [not found]</tt>
	 *         if the content provider does not exist
	 */
	@POST
	@Path("statistics/schedule/{cp}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	Response schedule(@PathParam("cp") String cp, String date);

	/**
	 * Cancel statistics schedule - the statistics for this content provider will not be updated anymore
	 * @param cp a content provider
	 * @return <tt>202 [accepted]</tt>, <tt>304 [not modified]</tt> if the it is not scheduled, <tt>404 [not found]</tt>
	 *         if the content provider does not exist
	 */
	@DELETE
	@Path("statistics/cancel/{cp}")
	Response cancelStatistiscs(@PathParam("cp") String cp);

	@GET
	@GZIP
	@Path("statistics/state/{cp}")
	StatisticsState getStatistics(@PathParam("cp") String cp);

}
