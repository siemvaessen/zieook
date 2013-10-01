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

import nl.gridline.zieook.data.controller.CollectionController;
import nl.gridline.zieook.data.controller.DataController;
import nl.gridline.zieook.data.controller.UserController;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The data manager listener, takes care of starting and stopping the data manager, and needs to be added to the web.xml
 * listeners
 * <p />
 * Project zieook-backend-data<br />
 * DataManagerListener.java created 15 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class DataManagerListener implements ServletContextListener
{

	// TODO split the listeners...

	static
	{
		// set project name to zieook
		System.setProperty("gridline.project.name", "zieook");
	}

	private static final Logger LOG = LoggerFactory.getLogger(DataManagerListener.class);

	@Override
	public void contextInitialized(ServletContextEvent context)
	{
		DataController datamanager = new DataController();
		try
		{
			datamanager.startup();
		}
		catch (MasterNotRunningException e)
		{
			LOG.error("Failed to start the zieook data manager! ", e);
		}
		catch (ZooKeeperConnectionException e)
		{
			LOG.error("Failed to start the zieook data manager! ", e);
		}
		context.getServletContext().setAttribute(DataController.NAME, datamanager);

		// start the collection items controller:

		CollectionController collectionItem = new CollectionController();
		try
		{
			collectionItem.startup();
		}
		catch (MasterNotRunningException e)
		{
			LOG.error("Failed to start the CollectionItemController", e);
		}
		catch (ZooKeeperConnectionException e)
		{
			LOG.error("Failed to start the CollectionItemController", e);
		}
		context.getServletContext().setAttribute(CollectionController.NAME, collectionItem);

		UserController user = new UserController(datamanager);
		try
		{
			user.startup();
		}
		catch (MasterNotRunningException e)
		{
			LOG.error("Failed to start the UserController", e);
		}
		catch (ZooKeeperConnectionException e)
		{
			LOG.error("Failed to start the UserController", e);
		}
		context.getServletContext().setAttribute(UserController.NAME, user);

	}

	@Override
	public void contextDestroyed(ServletContextEvent context)
	{
		DataController datamanager = (DataController) context.getServletContext().getAttribute(DataController.NAME);
		if (datamanager != null)
		{
			datamanager.shutdown();
		}
		else
		{
			LOG.warn("Failed to shutdown the zieook data controller not found in servlet context");
		}

		CollectionController collectionItem = (CollectionController) context.getServletContext().getAttribute(
				CollectionController.NAME);
		if (collectionItem != null)
		{
			collectionItem.shutdown();
		}
		else
		{
			LOG.warn("Failed to shutdown the collection item controller not found in servlet context");
		}

		UserController user = (UserController) context.getServletContext().getAttribute(UserController.NAME);
		if (user != null)
		{
			user.shutdown();
		}
		else
		{
			LOG.warn("Failed to shutdown the collection user controller not found in servlet context");
		}
	}

}
