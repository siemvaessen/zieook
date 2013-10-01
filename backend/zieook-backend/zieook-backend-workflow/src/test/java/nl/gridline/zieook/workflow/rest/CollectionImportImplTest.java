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

import nl.gridline.zieook.workflow.api.CollectionImport;
import nl.gridline.zieook.workflow.api.ContentProviderCRUD;
import nl.gridline.zieook.workflow.model.Collection;
import nl.gridline.zieook.workflow.model.ContentProvider;
import nl.gridline.zieook.workflow.model.OAIMetadata;
import nl.gridline.zieook.workflow.model.OAISets;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * CollectionImportImplTest.java created 11 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionImportImplTest
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

		ContentProviderCRUD proxy1 = ProxyFactory.create(ContentProviderCRUD.class, uri);
		proxy1.createContentProvider(new ContentProvider(ConstantsTest.CP, System.currentTimeMillis() / 1000, true));

		CollectionImport proxy2 = ProxyFactory.create(CollectionImport.class, uri);
		proxy2.deleteCollection(ConstantsTest.CP, ConstantsTest.COLLECTION);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		CollectionImport proxy2 = ProxyFactory.create(CollectionImport.class, uri);
		proxy2.deleteCollection(ConstantsTest.CP, ConstantsTest.COLLECTION);

		ContentProviderCRUD proxy1 = ProxyFactory.create(ContentProviderCRUD.class, uri);
		proxy1.deleteContentProvider(ConstantsTest.CP);

		server.stop();
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.workflow.rest.CollectionImportImpl#createCollectionMeta(java.lang.String, nl.gridline.zieook.workflow.model.Collection)}
	 * .
	 */
	@Test
	public final void testCreateReadDeleteCollectionMeta()
	{
		CollectionImport proxy2 = ProxyFactory.create(CollectionImport.class, uri);

		Collection collection = new Collection(ConstantsTest.CP, ConstantsTest.COLLECTION,
				Collection.Type.MOVIELENS.toString(), "", new OAISets(), new OAIMetadata(), -1);

		Response response2 = proxy2.createCollectionMeta(ConstantsTest.CP, collection);
		assertEquals(201, response2.getStatus());

		// read:
		Collection newcollection = proxy2.readCollectionMeta(ConstantsTest.CP, ConstantsTest.COLLECTION);
		assertEquals(collection, newcollection);

		Response response3 = proxy2.deleteCollection(ConstantsTest.CP, ConstantsTest.COLLECTION);
		assertEquals(200, response3.getStatus());

		try
		{
			proxy2.readCollectionMeta(ConstantsTest.CP, ConstantsTest.COLLECTION);
		}
		catch (org.jboss.resteasy.client.ClientResponseFailure e)
		{
			assertEquals(404, e.getResponse().getStatus());
			return;
		}
		fail("a 404 exception should have been thrown");
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.workflow.rest.CollectionImportImpl#readCollectionMeta(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	@Ignore
	public final void testReadCollectionMeta()
	{
		CollectionImport proxy = ProxyFactory.create(CollectionImport.class, uri, new ApacheHttpClient4Executor());

		// should throw a 404 - the cp does not exist:
		try
		{
			proxy.readCollectionMeta(ConstantsTest.CP, ConstantsTest.COLLECTION);
		}
		catch (org.jboss.resteasy.client.ClientResponseFailure e)
		{
			assertEquals(e.getResponse().getStatus(), 404);
			return;
		}
		fail("a 404 exception should have been thrown");
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.workflow.rest.CollectionImportImpl#updateCollectionMeta(java.lang.String, java.lang.String, nl.gridline.zieook.workflow.model.Collection)}
	 * .
	 */
	@Test
	@Ignore
	public final void testUpdateCollectionMeta()
	{
		CollectionImport proxy = ProxyFactory.create(CollectionImport.class, uri, new ApacheHttpClient4Executor());

		Collection collection = new Collection(ConstantsTest.CP, ConstantsTest.COLLECTION,
				Collection.Type.MOVIELENS.toString(), "", new OAISets(), new OAIMetadata(), -1);

		// Should throw a 404 - the cp does not exist, which it does, but no exception is thrown

		Response response = proxy.updateCollectionMeta(ConstantsTest.CP, ConstantsTest.COLLECTION, collection);
		assertEquals(404, response.getStatus());
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.workflow.rest.CollectionImportImpl#updateCollectionData(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	@Ignore
	public final void testUpdateCollectionData()
	{
		// This method is actually skipped, it's a file upload, that cannot be handled by this interface
		// the test will be handled somewhere else... hopefully

		// CollectionImport proxy = ProxyFactory.create(CollectionImport.class, uri, new ApacheHttpClient4Executor());
		//
		// proxy.updateCollectionData(cp, collection);
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.workflow.rest.CollectionImportImpl#deleteCollection(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	@Ignore
	public final void testDeleteCollectionLink()
	{
		CollectionImport proxy = ProxyFactory.create(CollectionImport.class, uri, new ApacheHttpClient4Executor());

		// Should throw a 404 - the cp does not exist, which it does, but no exception is thrown
		Response response = proxy.deleteCollection(ConstantsTest.CP, ConstantsTest.COLLECTION);
		assertEquals(404, response.getStatus());
	}

}
