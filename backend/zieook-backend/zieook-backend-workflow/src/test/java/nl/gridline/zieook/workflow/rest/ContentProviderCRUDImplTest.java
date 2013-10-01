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

import javax.ws.rs.core.Response;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseRecommendationTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.workflow.api.ContentProviderCRUD;
import nl.gridline.zieook.workflow.model.ContentProvider;

import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * ContentProviderCRUDImplTest.java created 11 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class ContentProviderCRUDImplTest
{

	private static final Logger LOG = LoggerFactory.getLogger(ContentProviderCRUDImplTest.class);

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

		ContentProviderCRUD proxy = ProxyFactory.create(ContentProviderCRUD.class, uri);
		proxy.deleteContentProvider(ConstantsTest.CP);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		ContentProviderCRUD proxy = ProxyFactory.create(ContentProviderCRUD.class, uri);
		proxy.deleteContentProvider(ConstantsTest.CP);

		server.stop();

		// cleanup:
		HBaseManager manager = HBaseManager.getInstance(Config.getInstance().getZooKeeperHost());

		// drop all related table:
		new HBaseCollectionTable(manager).drop(ConstantsTest.CP);
		new HBaseRecommendationTable(manager).drop(ConstantsTest.CP);
		new HBaseEventLogTable(manager).drop(ConstantsTest.CP);
		new HBaseUserTable(manager).drop(ConstantsTest.CP);

		// wait a while, so all the cluster is back to normal
		Thread.sleep(10000);
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.workflow.rest.ContentProviderCRUDImpl#createContentProvider(nl.gridline.zieook.workflow.model.ContentProvider)}
	 * .
	 */
	@Test
	public final void testCreateReadDeleteContentProvider()
	{
		// remember, deleting content providers is not implemented
		ContentProviderCRUD proxy = ProxyFactory.create(ContentProviderCRUD.class, uri);
		ContentProvider cp = new ContentProvider(ConstantsTest.CP, ConstantsTest.TIME, true);

		LOG.info("CREATE content provider {}", ConstantsTest.CP);
		Response result = proxy.createContentProvider(cp);
		assertEquals(201, result.getStatus());

		LOG.info("READ content provider: {}", ConstantsTest.CP);
		ContentProvider newCp = proxy.readContentProvider(ConstantsTest.CP);
		assertEquals(cp, newCp);

		LOG.info("DELETE content provider {}", ConstantsTest.CP);
		result = proxy.deleteContentProvider(ConstantsTest.CP);
		assertEquals(200, result.getStatus());

		try
		{
			proxy.readContentProvider(ConstantsTest.CP);
			fail("a 404 should have been thrown");
		}
		catch (ClientResponseFailure e)
		{
			assertEquals(404, e.getResponse().getStatus());
		}

	}
	// REMARK: Update to content provider (meta data) is not supported... yet

	// /**
	// *
	// * Test method for
	// * {@link nl.gridline.zieook.workflow.rest.ContentProviderCRUDImpl#updateConentProvider(java.lang.String,
	// nl.gridline.zieook.workflow.model.ContentProvider)}
	// * .
	// */
	// @Test
	// public final void testUpdateConentProvider()
	// {
	// fail("Not yet implemented"); // TODO
	// }

}
