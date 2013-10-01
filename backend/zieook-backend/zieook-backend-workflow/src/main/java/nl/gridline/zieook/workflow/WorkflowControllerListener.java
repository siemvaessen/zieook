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
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * WorkflowEngineListener.java created 15 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class WorkflowControllerListener implements ServletContextListener
{
	private static final Logger logger = LoggerFactory.getLogger(WorkflowControllerListener.class);

	static
	{
		// set project name to zieook
		System.setProperty("gridline.project.name", "zieook");
	}

	@Override
	public void contextInitialized(ServletContextEvent context)
	{

		WorkflowScheduler scheduler = (WorkflowScheduler) context.getServletContext().getAttribute(
				WorkflowScheduler.ZIEOOK_SCHEDULER);

		// start the controller:
		WorkflowController controller = new WorkflowController(scheduler);
		try
		{
			controller.startup();
		}
		catch (Exception e)
		{
			logger.error("Failed to start the zieook workflow controller! ", e);
		}
		context.getServletContext().setAttribute(WorkflowController.ZIEOOK_WORKFLOW, controller);
	}

	@Override
	public void contextDestroyed(ServletContextEvent context)
	{
		// kill the controller:
		WorkflowController controller = (WorkflowController) context.getServletContext().getAttribute(
				WorkflowController.ZIEOOK_WORKFLOW);
		if (controller != null)
		{
			try
			{
				controller.shutdown();
			}
			catch (Exception e)
			{
				logger.error("Failed to shutdown the zieook workflow controller!", e);
			}
		}
		else
		{
			logger.warn("Failed to shutdown the zieook workflow engine, not found in servlet context");
		}
	}
}
