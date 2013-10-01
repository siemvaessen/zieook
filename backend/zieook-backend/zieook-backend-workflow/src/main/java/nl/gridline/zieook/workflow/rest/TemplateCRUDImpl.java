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

import nl.gridline.zieook.workflow.WorkflowController;
import nl.gridline.zieook.workflow.api.TemplateCRUD;
import nl.gridline.zieook.workflow.model.Template;
import nl.gridline.zieook.workflow.model.Templates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CRUD on Templates
 * <p />
 * Project zieook-backend-workflow<br />
 * TemplateCRUDImpl.java created 4 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @see nl.gridline.zieook.workflow.api.TemplateCRUD
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class TemplateCRUDImpl implements TemplateCRUD
{

	private static final Logger LOG = LoggerFactory.getLogger(TemplateCRUDImpl.class);

	@Context
	private ServletContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.TemplateCRUD#createTemplate(nl.gridline.zieook.workflow.model.Template)
	 */
	@Override
	public Response createTemplate(Template template)
	{
		LOG.debug("creating template: <name> = <{}>", template.getName());
		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		controller.createTemplate(template);

		return Response.status(201).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.TemplateCRUD#getTemplate(java.lang.String)
	 */
	@Override
	public Template getTemplate(String name)
	{
		LOG.debug("getting template: <name> = <{}>", name);

		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		return controller.readTemplate(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.TemplateCRUD#updateTemplate(java.lang.String,
	 * nl.gridline.zieook.workflow.model.Template)
	 */
	@Override
	public Response updateTemplate(String name, Template template)
	{
		LOG.debug("update template: <name> = <{}>", name);

		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);

		controller.readTemplate(name);

		controller.updateTemplate(name, template);
		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.TemplateCRUD#deleteTemplate(java.lang.String)
	 */
	@Override
	public Response deleteTemplate(String name)
	{
		LOG.debug("delete template <name> = <{}>", name);
		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);

		controller.readTemplate(name);

		controller.deleteTemplate(name);
		return Response.ok().build();
	}

	@Override
	public Templates listTemplates()
	{
		LOG.debug("listing templates");
		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		return controller.listTemplates();
	}

}
