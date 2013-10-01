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
 * Collection controller startup / shutdown
 * <p />
 * Project zieook-backend-workflow<br />
 * CollectionControllerListener.java created 7 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionControllerListener implements ServletContextListener
{

	static
	{
		// set project name to zieook
		System.setProperty("gridline.project.name", "zieook");
	}

	private static final Logger LOG = LoggerFactory.getLogger(CollectionControllerListener.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent context)
	{

		// RecordController recordController = (RecordController) sce.getServletContext().getAttribute(
		// RecordController.DATACONTROLLER);

		WorkflowScheduler scheduler = (WorkflowScheduler) context.getServletContext().getAttribute(
				WorkflowScheduler.ZIEOOK_SCHEDULER);

		CollectionController collection = new CollectionController(scheduler);
		try
		{
			collection.startup();
		}
		catch (Exception e)
		{
			LOG.error("Failed to start the zieook workflow scheduler! ", e);
		}
		context.getServletContext().setAttribute(CollectionController.NAME, collection);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent context)
	{
		// kill the controller:
		CollectionController controller = (CollectionController) context.getServletContext().getAttribute(
				CollectionController.NAME);

		if (controller != null)
		{
			try
			{
				controller.shutdown();
			}
			catch (Exception e)
			{
				LOG.error("Failed to shutdown the zieook workflow controller!", e);
			}
		}
		else
		{
			LOG.warn("Failed to shutdown the zieook workflow engine, not found in servlet context");
		}
	}

}
