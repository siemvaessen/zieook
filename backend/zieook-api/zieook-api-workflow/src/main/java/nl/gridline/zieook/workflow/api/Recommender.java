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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.gridline.zieook.workflow.model.Names;
import nl.gridline.zieook.workflow.model.RecommenderConfig;
import nl.gridline.zieook.workflow.model.Recommenders;
import nl.gridline.zieook.workflow.model.TaskConfigIdList;
import nl.gridline.zieook.workflow.model.TaskConfigList;
import nl.gridline.zieook.workflow.model.TaskConfigMap;

import org.jboss.resteasy.annotations.GZIP;

/**
 * CRUD on recommenders: Create, Read, Update, Delete <br />
 * and list
 * <p />
 * Project zieook-api-workflow<br />
 * Recommender.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Path(Constants.APIPATH)
public interface Recommender
{

	/**
	 * List the known recommenders for the given content provider
	 * @param cp a content provider
	 * @return A list of recommenders.
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("recommenders-list/{cp}")
	Recommenders getRecommenders(@PathParam("cp") String cp);

	/**
	 * list all known recommenders - this should not really be used in the front end
	 * @return
	 */
	@GET
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("recommenders-list")
	Recommenders getRecommenders();

	/**
	 * Create a recommender given the configuration and a content provider
	 * @param cp a content provider
	 * @param config a recommender
	 * @return
	 */
	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("recommender/{cp}")
	Response createRecommender(@PathParam("cp") String cp, RecommenderConfig config);

	/**
	 * Update recommender configuration for the given content provider and recommender
	 * @param cp the content provider
	 * @param recommender the recommender
	 * @param config the updated configuration
	 * @return the updated configuration, or the old one if it failed
	 */
	@PUT
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("recommender/{cp}/{recommender}")
	Response updateRecommender(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
			RecommenderConfig config);

	/**
	 * Delete recommender for the given configuration
	 * @param cp the content provider
	 * @param recommender the recommender
	 * @return the recommender that was removed
	 */
	@DELETE
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("recommender/{cp}/{recommender}")
	Response deleteRecommender(@PathParam("cp") String cp, @PathParam("recommender") String recommender);

	/**
	 * schedule a recommender
	 * @param cp content provider
	 * @param recommender recommender name
	 * @return
	 */
	@PUT
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("recommender-schedule/{cp}/{recommender}")
	Response scheduleRecommender(@PathParam("cp") String cp, @PathParam("recommender") String recommender, String date);

	/**
	 * Cancel a scheduled or running recommender
	 * @param cp content provider name
	 * @param recommender recommender name
	 * @return
	 */
	@PUT
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("recommender-cancel/{cp}/{recommender}")
	Response cancelRecommender(@PathParam("cp") String cp, @PathParam("recommender") String recommender, String data);

	/**
	 * Read a recommender given the content provider and the recommender
	 * @param cp a content provider
	 * @param recommender a recommender
	 * @return
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("recommender-config/{cp}/{recommender}")
	RecommenderConfig readConfiguration(@PathParam("cp") String cp, @PathParam("recommender") String recommender);

	/**
	 * Return the list of known task id's - this can be a rather long list, and as such can be limited by time a time
	 * range that is matched against the start time of the task. <br />
	 * A few more properties<br />
	 * <ul>
	 * <li><em>start not passed</em> all tasks until end are returned</li>
	 * <li><em>end not passed</em> all tasks until <tt>now</tt> are returned</li>
	 * <li><em>end and start not passed</em> all tasks to be executed are returned</li>
	 * </ul>
	 * @param cp a content provider
	 * @param recommender a recommender
	 * @param start begin of the range in seconds <em>matched against the task start time</em>
	 * @param end end of the range in seconds <em>matched against the task start time</em>
	 * @return return a list of task id's
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("recommender-taskids/{cp}/{recommender}")
	TaskConfigIdList getTaskIds(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
			@QueryParam("start") Long start, @QueryParam("end") Long end);

	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("recommender-tasks/{cp}/{recommender}")
	TaskConfigList getTasks(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
			@QueryParam("start") Long start, @QueryParam("end") Long end);

	/**
	 * Return the configuration of the given task id - executed by this cp and recommender.
	 * Although the id is globally unique the cp and recommender are checked against the id and recommender in the task
	 * that's being retrieved.
	 * @param cp - content provider
	 * @param recommender - recommender
	 * @param task - task id
	 * @return
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("recommender/{cp}/{recommender}/{task}")
	TaskConfigMap getTask(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
			@PathParam("task") Long task);

	/**
	 * Returns the taskconfig map, if it's successfully finished
	 * @param cp content provider
	 * @param recommender recommender name
	 * @return that task result, or 404 if there is no successfully finished task.
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("recommender-ready/{cp}/{recommender}")
	TaskConfigMap getTaskStatus(@PathParam("cp") String cp, @PathParam("recommender") String recommender);

	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("recommender-cp")
	Names getRecommenderCP(@QueryParam("recommender") String recommender);

}
