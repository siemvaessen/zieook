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
package nl.gridline.zieook.workflow.rest;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.gridline.zieook.workflow.WorkflowController;
import nl.gridline.zieook.workflow.WorkflowScheduler;
import nl.gridline.zieook.workflow.api.Constants;
import nl.gridline.zieook.workflow.model.SchedulerState;
import nl.gridline.zieook.workflow.model.TaskConfigList;
import nl.gridline.zieook.workflow.model.TaskConfigMap;
import nl.gridline.zieook.workflow.model.WorkflowState;
import nl.gridline.zieook.workflow.model.ZooKeeperState;

import org.jboss.resteasy.annotations.GZIP;

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

	@Context
	private ServletContext context;

	@GET
	@Path(Constants.APIPATH + "status")
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public WorkflowState getState()
	{
		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
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

	@GET
	@Path(Constants.APIPATH + "scheduler_status")
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public SchedulerState getSchedulerState()
	{
		WorkflowScheduler controller = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);
		if (controller != null)
		{
			return controller.getState();
		}
		else
		{
			return null;
		}
	}

	@GET
	@Path(Constants.APIPATH + "zookeeper_status")
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public ZooKeeperState getZooKeeperState()
	{
		WorkflowScheduler controller = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);
		if (controller != null)
		{
			return controller.getZooKeeperState();
		}
		else
		{
			return null;
		}
	}

	@GET
	@Path(Constants.APIPATH + "future-tasks")
	@GZIP
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public TaskConfigList getFutureTasks(@QueryParam("cp") String cp,
			@QueryParam("start_id") @DefaultValue("100") Integer size)
	{
		WorkflowScheduler controller = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);
		if (controller != null)
		{
			List<TaskConfigMap> result = controller.futureTasks(cp, size);
			return new TaskConfigList(result);
		}
		else
		{
			return null;
		}
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
