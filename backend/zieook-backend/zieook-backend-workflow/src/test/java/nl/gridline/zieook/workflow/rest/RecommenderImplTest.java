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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import javax.ws.rs.core.Response;

import nl.gridline.zieook.workflow.api.ContentProviderCRUD;
import nl.gridline.zieook.workflow.api.Recommender;
import nl.gridline.zieook.workflow.model.ContentProvider;
import nl.gridline.zieook.workflow.model.RecommenderConfig;

import org.jboss.resteasy.client.ProxyFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * These test only test for sending data and marshall / unmarshall exceptions, they should fail because they are
 * dependent on a content provider that does not exists in this context.
 * <p />
 * Project zieook-backend-workflow<br />
 * RecommenderImplTest.java created 11 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class RecommenderImplTest
{

	private static final Logger LOG = LoggerFactory.getLogger(RecommenderImplTest.class);

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
		// shutdown the server:
		server.stop();
	}

	@Before
	public void setup()
	{
		// create content provider:
		ContentProviderCRUD cprovider = ProxyFactory.create(ContentProviderCRUD.class, uri);
		cprovider.createContentProvider(ConstantsTest.getContentProvider());

		ContentProvider newcp = cprovider.readContentProvider(ConstantsTest.CP);
		assertEquals(ConstantsTest.getContentProvider(), newcp);
	}

	@After
	public void after()
	{
		ContentProviderCRUD cprovider = ProxyFactory.create(ContentProviderCRUD.class, uri);
		cprovider.deleteContentProvider(ConstantsTest.CP);
		try
		{
			cprovider.readContentProvider(ConstantsTest.CP);
		}
		catch (org.jboss.resteasy.client.ClientResponseFailure e)
		{
			assertEquals(404, e.getResponse().getStatus());
		}
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.workflow.rest.RecommenderImpl#createRecommender(java.lang.String, nl.gridline.zieook.workflow.model.RecommenderConfig)}
	 * .
	 */
	@Test
	public final void testCreateReadDeleteConfiguration()
	{
		LOG.info("CREATE recommenders test");

		Recommender proxy = ProxyFactory.create(Recommender.class, uri);

		Response response = proxy.createRecommender(ConstantsTest.CP, ConstantsTest.getRecommenderConfig());
		assertEquals(201, response.getStatus());

		RecommenderConfig newconfig = proxy.readConfiguration(ConstantsTest.CP, ConstantsTest.RECOMMENDER);
		LOG.info("recommender created: {}", newconfig.toJSON());
		assertEquals(ConstantsTest.getRecommenderConfig(), newconfig);

		Response deleted = proxy.deleteRecommender(ConstantsTest.CP, ConstantsTest.RECOMMENDER);
		assertEquals(200, deleted.getStatus());

		try
		{
			RecommenderConfig empty = proxy.readConfiguration(ConstantsTest.CP, ConstantsTest.RECOMMENDER);

			// this line should not be executed:
			assertTrue(empty == null);
		}
		catch (org.jboss.resteasy.client.ClientResponseFailure e)
		{
			assertEquals(404, e.getResponse().getStatus());
		}
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.workflow.rest.RecommenderImpl#updateRecommender(java.lang.String, java.lang.String, nl.gridline.zieook.workflow.model.RecommenderConfig)}
	 * .
	 */
	@Test
	@Ignore
	public final void testUpdateConfiguration()
	{
		LOG.info("UPDATE recommenders test");

		Recommender proxy = ProxyFactory.create(Recommender.class, uri);

		Response created = proxy.createRecommender(ConstantsTest.CP, ConstantsTest.getRecommenderConfig());
		assertEquals(201, created.getStatus());

		RecommenderConfig recommender = ConstantsTest.getRecommenderConfig();
		recommender.setCp(ConstantsTest.RECOMMENDER2);

		Response updated = proxy.updateRecommender(ConstantsTest.CP, ConstantsTest.RECOMMENDER, recommender);
		assertEquals(200, updated.getStatus());

		RecommenderConfig recommendernew = proxy.readConfiguration(ConstantsTest.CP, ConstantsTest.RECOMMENDER2);
		assertEquals(recommender, recommendernew);

		Response deleted = proxy.deleteRecommender(ConstantsTest.CP, ConstantsTest.RECOMMENDER);
		assertEquals(200, deleted.getStatus());

		try
		{
			RecommenderConfig empty = proxy.readConfiguration(ConstantsTest.CP, ConstantsTest.RECOMMENDER);

			// this line should not be executed, or null
			assertTrue(empty == null);
		}
		catch (org.jboss.resteasy.client.ClientResponseFailure e)
		{
			assertEquals(404, e.getResponse().getStatus());
		}
	}

	@Test
	public final void testInvalidGetTasks()
	{
		Recommender proxy = ProxyFactory.create(Recommender.class, uri);
		try
		{
			proxy.getTasks(ConstantsTest.CP, ConstantsTest.RECOMMENDER, ConstantsTest.STARTTIME, ConstantsTest.ENDTIME);
			fail("A 400 should have been thrown.");
		}
		catch (org.jboss.resteasy.client.ClientResponseFailure e)
		{
			assertEquals(400, e.getResponse().getStatus());
		}
	}
}
