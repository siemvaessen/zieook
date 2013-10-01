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

import java.io.File;

import nl.gridline.zieook.api.RatingEvents;
import nl.gridline.zieook.model.Rating;
import nl.gridline.zieook.model.UserView;

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
 * POST requests do not generate a 404 in the client proxy used, that's weird, but okay. The main testing purpose here
 * is to see if the JAXB objects are correctly marshaled and unmarshaled.
 * <p />
 * Project zieook-backend-data<br />
 * RatingEventImplTest.java created 9 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class RatingEventImplTest
{

	private static final Logger LOG = LoggerFactory.getLogger(RatingEventImplTest.class);

	private static final int port = 20200;
	private static Server server;
	private static String uri;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception
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
	public static void tearDown() throws Exception
	{
		server.stop();
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.data.rest.RatingEventsImpl#setRecommendation(java.lang.String, java.lang.String, java.lang.String, nl.gridline.zieook.model.Rating)}
	 * .
	 */
	@Test
	public final void testSetRecommendation()
	{
		LOG.info("testing POST on ratings");
		RatingEvents proxy = ProxyFactory.create(RatingEvents.class, uri, new ApacheHttpClient4Executor());

		Rating rating = new Rating(ConstantsTest.USER, ConstantsTest.ITEM, ConstantsTest.COLLECTION,
				ConstantsTest.SOURCE, ConstantsTest.TIMESTR, ConstantsTest.RATING);
		proxy.setRating(ConstantsTest.CP, ConstantsTest.COLLECTION, rating);
	}

	/**
	 * Test method for
	 * {@link nl.gridline.zieook.data.rest.RatingEventsImpl#setView(java.lang.String, java.lang.String, java.lang.String, nl.gridline.zieook.model.UserView)}
	 * .
	 */
	@Test
	public final void testSetView()
	{
		LOG.info("testing POST on views");
		RatingEvents proxy = ProxyFactory.create(RatingEvents.class, uri, new ApacheHttpClient4Executor());

		UserView view = new UserView(ConstantsTest.USER, ConstantsTest.ITEM, ConstantsTest.COLLECTION,
				ConstantsTest.SOURCE, ConstantsTest.TIMESTR, ConstantsTest.RECOMMENDER, ConstantsTest.ITEM,
				ConstantsTest.RANK);

		proxy.setView(ConstantsTest.CP, ConstantsTest.RECOMMENDER, view);
	}
}
