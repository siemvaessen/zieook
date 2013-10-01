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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import nl.gridline.zieook.workflow.api.TemplateCRUD;
import nl.gridline.zieook.workflow.model.Template;

import org.jboss.resteasy.client.ProxyFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * TemplateCRUDImplTest.java created 11 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class TemplateCRUDImplTest
{

	private static final int port = 20100;
	private static Server server;
	private static String uri;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		// start the server:
		server = new Server(port);
		WebAppContext context = new WebAppContext();

		uri = "http://localhost:20100";

		// see if this can be set more dynamically:
		context.setContextPath("/");
		context.setResourceBase(new File("src/main/webapp/").getCanonicalPath());
		context.setDescriptor(new File("src/main/webapp/").getCanonicalPath() + "/WEB-INF/web.xml");

		server.setHandler(context);
		server.start();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		server.stop();
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.workflow.rest.TemplateCRUDImpl#createTemplate(nl.gridline.zieook.workflow.model.Template)}
	 * .
	 */
	@Test
	public final void testCreateGetDeleteTemplate()
	{
		TemplateCRUD proxy = ProxyFactory.create(TemplateCRUD.class, uri);

		// template:

		// a template should now have been created:
		proxy.createTemplate(ConstantsTest.getTemplate());

		Template newtemplate = proxy.getTemplate(ConstantsTest.TEMPLATENAME1);

		// delete the template:
		proxy.deleteTemplate(ConstantsTest.TEMPLATENAME1);

		// new template & template should be equal:
		assertEquals(ConstantsTest.getTemplate(), newtemplate);

		// the delete template should no longer exist:
		try
		{
			proxy.getTemplate(ConstantsTest.TEMPLATENAME1); // should return an empty template:
			fail("404 should have been thrown.");
		}
		catch (org.jboss.resteasy.client.ClientResponseFailure e)
		{
			assertEquals(404, e.getResponse().getStatus());
		}
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.workflow.rest.TemplateCRUDImpl#updateTemplate(java.lang.String, nl.gridline.zieook.workflow.model.Template)}
	 * .
	 */
	@Test
	public final void testUpdateTemplate()
	{
		// only name change is test here, this is a little more complicated that the normal test

		TemplateCRUD proxy = ProxyFactory.create(TemplateCRUD.class, uri);
		proxy.createTemplate(ConstantsTest.getTemplate());

		// change the template name
		Template template = ConstantsTest.getTemplate();
		template.setName(ConstantsTest.TEMPLATENAME2);

		proxy.updateTemplate(ConstantsTest.TEMPLATENAME1, template);

		// now the old template should no longer exist:
		try
		{
			proxy.getTemplate(ConstantsTest.TEMPLATENAME1); // should return an empty template:
			fail("404 should have been thrown.");
		}
		catch (org.jboss.resteasy.client.ClientResponseFailure e)
		{
			assertEquals(404, e.getResponse().getStatus());
		}

		// the new template should be equals to, the existing one
		Template newtemplate = proxy.getTemplate(ConstantsTest.TEMPLATENAME2);
		assertEquals(newtemplate, template);
	}
}
