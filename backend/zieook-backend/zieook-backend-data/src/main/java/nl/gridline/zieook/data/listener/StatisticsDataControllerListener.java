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
package nl.gridline.zieook.data.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import nl.gridline.zieook.data.controller.StatisticsDataController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsDataControllerListener implements ServletContextListener
{
	private static final Logger LOG = LoggerFactory.getLogger(StatisticsDataControllerListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent context)
	{
		LOG.debug("contextDestroyed");
		StatisticsDataController datamanager = (StatisticsDataController) context.getServletContext().getAttribute(
				StatisticsDataController.NAME);
		if (datamanager != null)
		{
			try
			{
				datamanager.shutdown();
			}
			catch (Exception e)
			{
				LOG.error("Failed to shutdown the statistics data controller!", e);
			}
		}
		else
		{
			LOG.warn("Failed to shutdown the statistics data controller not found in servlet context");
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent context)
	{
		LOG.debug("contextInitialized");
		StatisticsDataController datacontroller = new StatisticsDataController();
		try
		{
			datacontroller.startup();
		}
		catch (Exception e)
		{
			LOG.error("Failed to start the statistics data controller! ", e);
		}
		context.getServletContext().setAttribute(StatisticsDataController.NAME, datacontroller);
	}

}
