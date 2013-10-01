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
package nl.gridline.zieook.data.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import nl.gridline.zieook.api.CollectionData;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-data<br />
 * CollectionDataImplTest.java created 11 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionDataImplTest
{

	private static final int port = 20200;
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

		uri = "http://localhost:20200";

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
	 * {@link nl.gridline.zieook.data.rest.CollectionDataImpl#getCollectionCategories(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public final void testGetCollectionCategories()
	{
		CollectionData proxy = ProxyFactory.create(CollectionData.class, uri, new ApacheHttpClient4Executor());
		try
		{
			proxy.getCollectionCategories(ConstantsTest.CP, ConstantsTest.COLLECTION, null);
		}
		catch (org.jboss.resteasy.client.ClientResponseFailure e)
		{
			fail("No exception should have been thrown.");
		};
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.data.rest.CollectionDataImpl#getItem(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public final void testGetItem()
	{
		CollectionData proxy = ProxyFactory.create(CollectionData.class, uri, new ApacheHttpClient4Executor());
		try
		{
			proxy.getItemRaw(ConstantsTest.CP, ConstantsTest.COLLECTION, ConstantsTest.ITEM);
		}
		catch (org.jboss.resteasy.client.ClientResponseFailure e)
		{
			fail("No exception should have been thrown");
		}
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.data.rest.CollectionDataImpl#getItem(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public final void testInvalidGetItem()
	{
		CollectionData proxy = ProxyFactory.create(CollectionData.class, uri, new ApacheHttpClient4Executor());
		try
		{
			proxy.getItemRaw(ConstantsTest.CP, ConstantsTest.COLLECTION, null);
		}
		catch (org.jboss.resteasy.client.ClientResponseFailure e)
		{
			assertEquals(400, e.getResponse().getStatus());
			return;
		}
		fail("A 400 exception should have been thrown.");
	}

}
