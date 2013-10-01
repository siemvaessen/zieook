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
package nl.gridline.zieook.workflow;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The workflow scheduler listener
 * <p />
 * Project zieook-api-workflow<br />
 * ImportSchedularListener.java created 3 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class WorkflowSchedulerListener implements ServletContextListener
{

	static
	{
		// set project name to zieook
		System.setProperty("gridline.project.name", "zieook");
	}

	private static final Logger logger = LoggerFactory.getLogger(WorkflowSchedulerListener.class);

	@Override
	public void contextInitialized(ServletContextEvent context)
	{
		WorkflowScheduler scheduler = new WorkflowScheduler();
		try
		{
			scheduler.startup();
		}
		catch (Exception e)
		{
			logger.error("Failed to start the zieook workflow scheduler! ", e);
		}

		context.getServletContext().setAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER, scheduler);
	}

	@Override
	public void contextDestroyed(ServletContextEvent context)
	{
		WorkflowScheduler scheduler = (WorkflowScheduler) context.getServletContext().getAttribute(
				WorkflowScheduler.ZIEOOK_SCHEDULER);
		if (scheduler != null)
		{
			try
			{
				scheduler.shutdown();
			}
			catch (Exception e)
			{
				logger.error("Failed to shutdown the zieook workflow scheduler!", e);
			}
		}
		else
		{
			logger.warn("Failed to shutdown the zieook workflow scheduler, not found in servlet context");
		}
	}
}
