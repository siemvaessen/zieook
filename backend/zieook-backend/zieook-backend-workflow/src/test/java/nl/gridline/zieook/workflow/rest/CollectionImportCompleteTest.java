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
import java.io.IOException;

import javax.ws.rs.core.Response;

import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.workflow.api.CollectionImport;
import nl.gridline.zieook.workflow.api.ContentProviderCRUD;
import nl.gridline.zieook.workflow.model.ContentProvider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
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
 * Imports a movielens collection into hbase using a map-reduce job.
 * <p />
 * Project zieook-backend-workflow<br />
 * CollectionImportCompleteTest.java created 21 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionImportCompleteTest
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

	// @Test
	// public void callTest() throws MasterNotRunningException
	// {
	// HBaseManager manager = new HBaseManager("mulisch");
	// new HBaseCollectionTable(manager).create(cpName);
	//
	// TaskConfig task = new TaskConfig();
	//
	// String name = cpName + "/" + collName + "/collection.dat";
	// // 4: process input stream:
	// Path path = new Path("hdfs://mulisch:8020/zieook/temp", name);
	//
	// // two heart beats in the future:
	// task.setProperty(TaskConfig.NEXT, System.currentTimeMillis());
	// task.setProperty(TaskConfig.CP, cpName); // the content provider
	// task.setProperty(TaskConfig.COLLECTION, collName); // the collection name
	// task.setProperty(TaskConfig.INPUT_PATH, path.toString()); // the source path
	// task.setProperty(TaskConfig.DATA_TYPE, "movielens"); // the meta data type
	// task.setProperty(TaskConfig.DATA_PART, "collection");
	//
	// // copy data to hdfs
	//
	// // start map-reduce
	//
	// DataFileImportJob job = new DataFileImportJob();
	// try
	// {
	// job.setConfig(task);
	// job.call();
	// }
	// catch (Exception e)
	// {
	// LOG.error("failed", e);
	// fail("task failed");
	// }
	// }

	/**
	 * This is an extensive test & example
	 * - create content provider
	 * - create collection
	 * - upload movielens dataset collection,users,ratings
	 * - test some items.
	 * @throws InterruptedException
	 */
	@Test
	@Ignore
	public final void testCreateUploadCollection() throws InterruptedException
	{
		// create collection:

		CollectionImport collectionImpert = ProxyFactory.create(CollectionImport.class, uri);

		Response create = collectionImpert.createCollectionMeta(ConstantsTest.CP, ConstantsTest.getCollection());
		assertEquals(201, create.getStatus());

		// NOW, we can upload the data, that's wat this test is all about after all...
		// collectionImport.updateCollectionData(cp, collection) oops, we can't use this method..
		// collectionImport.updateCollectionData(cp, collection, data);

		// collection data:
		File data = new File("test-data/movies.dat");
		int status = collectionUpload(uri + "/api/1.0/collection/" + ConstantsTest.CP + "/" + ConstantsTest.COLLECTION
				+ "/" + TaskConfig.DATA_PART_COLLECTION, data);
		assertEquals(200, status);

		// ratings data:
		data = new File("test-data/ratings.dat");
		status = collectionUpload(uri + "/api/1.0/collection/" + ConstantsTest.CP + "/" + ConstantsTest.COLLECTION + "/"
				+ TaskConfig.DATA_PART_RATINGS, data);
		assertEquals(200, status);

		// user data:
		data = new File("test-data/users.dat");
		status = collectionUpload(uri + "/api/1.0/collection/" + ConstantsTest.CP + "/" + ConstantsTest.COLLECTION + "/"
				+ TaskConfig.DATA_PART_USERS, data);
		assertEquals(200, status);

		// TODO data is imported, check if the data is available through REST
		// this is a long running action - data is imported in HBase right away
		// sleep a while
		// check if data is imported, by requesting a few collection items, users and recommendations

	}

	@Test
	public void testCreateCollectionFail()
	{
		executePOST(uri + "/api/1.0/content-provider", "{some: {garbage : data}}");
	}

	@Ignore
	private HttpResponse executePOST(String url, String data)
	{
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPut post = new HttpPut(url);
		post.setHeader("Accept", "application/json");
		try
		{
			post.setEntity(new StringEntity(data));
			return httpclient.execute(post);
		}
		catch (ClientProtocolException e)
		{
			LOG.error("upload failed", e);
			fail("upload failed");
		}
		catch (IOException e)
		{
			LOG.error("upload failed", e);
			fail("upload failed");
		}

		return null;
	}

	@Ignore
	private int collectionUpload(String url, File file)
	{
		// upload binary collection data

		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpEntity entity = new FileEntity(file, "binary/octet-stream");

		HttpPut post = new HttpPut(url);
		post.setEntity(entity);
		try
		{
			HttpResponse response = httpclient.execute(post);
			return response.getStatusLine().getStatusCode();
		}
		catch (ClientProtocolException e)
		{
			LOG.error("upload failed", e);
			fail("upload failed");
		}
		catch (IOException e)
		{
			LOG.error("upload failed", e);
			fail("upload failed");
		}
		return 500;
	}
}
