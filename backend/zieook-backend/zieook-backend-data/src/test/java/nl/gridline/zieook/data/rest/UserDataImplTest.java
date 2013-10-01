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

import javax.ws.rs.core.Response;

import nl.gridline.zieook.api.UserData;
import nl.gridline.zieook.model.User;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
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
 * Project zieook-backend-data<br />
 * UserDataImplTest.java created 21 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:daryl@gridline.nl">Daryl</a>
 * @version $Revision$, $Date$
 */
public class UserDataImplTest
{
	private static final Logger LOG = LoggerFactory.getLogger(UserDataImplTest.class);

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

	@Test
	public final void testGetUser()
	{
		User user = getTestUser();

		UserData proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
		if (proxy.isUser(ConstantsTest.CP, user.getId()).equals("false"))
		{
			proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
			proxy.addUser(ConstantsTest.CP, user);
		}

		proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
		assertEquals(user, proxy.getUser(ConstantsTest.CP, user.getId()));
	}

	@Test
	public final void testInvalidGetUser()
	{
		UserData proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
		try
		{
			proxy.getUser(ConstantsTest.CP, null);
		}
		catch (org.jboss.resteasy.client.ClientResponseFailure e)
		{
			assertEquals(400, e.getResponse().getStatus());
			return;
		}
		fail("A 400 exception should have been thrown.");
	}

	@Test
	public final void testExists()
	{
		User user = getTestUser();

		UserData proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
		if (proxy.isUser(ConstantsTest.CP, user.getId()).equals("false"))
		{
			proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
			proxy.addUser(ConstantsTest.CP, user);
		}

		proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
		assertEquals("true", proxy.isUser(ConstantsTest.CP, user.getId()));
	}

	@Test
	public final void testInvalidExists()
	{
		UserData proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
		try
		{
			assertEquals("true", proxy.isUser(ConstantsTest.CP, null));
		}
		catch (org.jboss.resteasy.client.ClientResponseFailure e)
		{
			assertEquals(400, e.getResponse().getStatus());
			return;
		}
		fail("A 400 exception should have been thrown.");
	}

	// @Test
	// public final void testGetUsers()
	// {
	// UserData proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
	// Users users = proxy.getUsers(ConstantsTest.CP, ConstantsTest.USER);
	// assertEquals(100, users.getUsers().size());
	// }

	@Test
	public final void testAddUser()
	{
		User user = getTestUser();

		UserData proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
		if (proxy.isUser(ConstantsTest.CP, user.getId()).equals("true"))
		{
			proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
			proxy.removeUser(ConstantsTest.CP, user.getId());
		}

		proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
		Response response = proxy.addUser(ConstantsTest.CP, user);
		assertEquals(201, response.getStatus());
	}

	@Test
	public final void testPutUser()
	{
		User user = getTestUser();
		UserData proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());

		if (proxy.isUser(ConstantsTest.CP, user.getId()).equals("false"))
		{
			proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
			proxy.addUser(ConstantsTest.CP, user);
		}

		Response response = proxy.putUser(ConstantsTest.CP, user.getId(), user);
		assertEquals(200, response.getStatus());
	}

	@Test
	public final void testInvalidPutUser()
	{
		User user = getTestUser();
		UserData proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());

		Response r = proxy.putUser(ConstantsTest.CP, null, user);
		assertEquals(400, r.getStatus());
	}

	@Test
	public final void testRemoveUser()
	{
		User user = getTestUser();

		UserData proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
		if (proxy.isUser(ConstantsTest.CP, user.getId()).equals("false"))
		{
			proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
			proxy.addUser(ConstantsTest.CP, user);
		}

		proxy = ProxyFactory.create(UserData.class, uri, new ApacheHttpClient4Executor());
		Response response = proxy.removeUser(ConstantsTest.CP, user.getId());
		assertEquals(200, response.getStatus());
	}

	private User getTestUser()
	{
		User user = new User();
		user.setId(ConstantsTest.USER);
		user.setName("Foo B. Baz");
		LOG.info("Test user is " + user);
		return user;
	}
}
