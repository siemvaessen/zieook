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

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import nl.gridline.zieook.exceptions.ContentProviderDoesNotExist;
import nl.gridline.zieook.workflow.WorkflowScheduler;
import nl.gridline.zieook.workflow.api.StatisticsScheduler;
import nl.gridline.zieook.workflow.model.StatisticsState;

import org.jboss.resteasy.spi.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Schedule statistics, scheduler
 * <p />
 * Project zieook-backend-workflow<br />
 * StatisticsSchedulerImpl.java created 31 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class StatisticsSchedulerImpl implements StatisticsScheduler
{

	private static final Logger LOG = LoggerFactory.getLogger(StatisticsSchedulerImpl.class);

	@Context
	private ServletContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.StatisticsScheduler#runStatistics(java.lang.String)
	 */
	@Override
	public Response runStatistics(String cp, String date)
	{
		// date is ignored, always NOW

		LOG.debug("run statistics for <cp> = <{}>", cp);
		WorkflowScheduler controller = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);

		// 404 [not found] if the content provider does not exist.
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		// 304 [not modified if the task is already running, like right now
		if (controller.isStatisticsRunning(cp))
		{
			return Response.status(304).build();
		}

		// 202 [accepted] the tasks is started in the background
		try
		{
			controller.scheduleStatistics(cp, true);
		}
		catch (IOException e)
		{
			throw new InternalServerErrorException(e);
		}

		return Response.status(202).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.StatisticsScheduler#schedule(java.lang.String)
	 */
	@Override
	public Response schedule(String cp, String dateStr)
	{
		LOG.debug("schedule statistics for <cp> = <{}>", cp);

		WorkflowScheduler controller = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);
		// 404 [not found] if the content provider does not exist.
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		// try to extract the date:
		// {"start_training":1302618752}

		// 304 [not modified if the task is already scheduled
		if (controller.isStatisticsScheduled(cp))
		{
			return Response.status(304).build();
		}

		try
		{
			controller.scheduleStatistics(cp, false);
		}
		catch (IOException e)
		{
			throw new InternalServerErrorException(e);
		}

		// object created:
		return Response.status(202).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.StatisticsScheduler#cancelStatistiscs(java.lang.String)
	 */
	@Override
	public Response cancelStatistiscs(String cp)
	{
		LOG.debug("cancel statistics for <cp> = <{}> ", cp);
		WorkflowScheduler controller = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);
		// 404 [not found] if the content provider does not exist.
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		// 304 [not modified if the task is already cancelled, !scheduled
		if (!controller.isStatisticsScheduled(cp))
		{
			return Response.status(304).build();
		}

		controller.cancelStatisticsTask(cp);

		return Response.status(202).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.StatisticsScheduler#getStatistics(java.lang.String)
	 */
	@Override
	public StatisticsState getStatistics(String cp)
	{
		LOG.debug("get statistics state for <cp> = <{}>", cp);
		WorkflowScheduler controller = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);
		// 404 [not found] if the content provider does not exist.
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		return controller.getStatisticsState(cp);
	}

}
