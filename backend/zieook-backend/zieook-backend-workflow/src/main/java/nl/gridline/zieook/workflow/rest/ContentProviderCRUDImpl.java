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

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import nl.gridline.zieook.exceptions.ContentProviderDoesNotExist;
import nl.gridline.zieook.workflow.WorkflowController;
import nl.gridline.zieook.workflow.api.ContentProviderCRUD;
import nl.gridline.zieook.workflow.model.ContentProvider;
import nl.gridline.zieook.workflow.model.ContentProviders;

import org.jboss.resteasy.spi.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CRUD on content providers
 * <p />
 * Project zieook-backend-workflow<br />
 * ContentProviderCRUDImpl.java created 10 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class ContentProviderCRUDImpl implements ContentProviderCRUD
{
	private static final Logger LOG = LoggerFactory.getLogger(ContentProviderCRUDImpl.class);

	@Context
	private ServletContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.ContentProviderCRUD#createContentProvider(nl.gridline.zieook.workflow.model.
	 * ContentProvider)
	 */
	@Override
	public Response createContentProvider(ContentProvider cp)
	{

		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		LOG.debug("creating content provider <{}>", cp);

		if (controller.isContentProvider(cp.getName()))
		{
			return Response.status(304).build();
		}

		LOG.debug("START CREATE CONTENT PROVIDER");

		controller.createContentProvider(cp);

		LOG.debug("DONE CREATE CONTENT PROVIDER: RETURNING 201");

		return Response.status(201).build();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.ContentProviderCRUD#readContentProvider(java.lang.String,
	 * nl.gridline.zieook.workflow.model.ContentProvider)
	 */
	@Override
	public ContentProvider readContentProvider(String cp)
	{
		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		return controller.readContentProvider(cp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.ContentProviderCRUD#updateConentProvider(java.lang.String)
	 */
	@Override
	public Response updateContentProvider(String name, ContentProvider cp)
	{
		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		if (!controller.isContentProvider(name))
		{
			throw new ContentProviderDoesNotExist(name);
		}
		if (!name.equals(cp.getName()))
		{
			throw new BadRequestException("content provider name cannot be changed");
		}

		controller.updateContentProvider(name, cp);

		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.ContentProviderCRUD#deleteContentProvider(java.lang.String)
	 */
	@Override
	public Response deleteContentProvider(String cp)
	{
		LOG.info("deleting content provider: {}", cp);
		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		controller.deleteContentProvider(cp);

		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.ContentProviderCRUD#getContentProviders()
	 */
	@Override
	public ContentProviders getContentProviders()
	{
		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		return controller.getContentProviders();
	}

}
