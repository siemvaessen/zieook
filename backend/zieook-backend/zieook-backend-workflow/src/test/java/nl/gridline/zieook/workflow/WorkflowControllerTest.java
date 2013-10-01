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
package nl.gridline.zieook.workflow;

import static org.junit.Assert.assertTrue;
import nl.gridline.zieook.workflow.model.ContentProvider;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * WorkflowControllerTest.java created 28 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class WorkflowControllerTest
{

	private static final String CP = "UNIT_TEST_CP";

	private static WorkflowController controller;
	private static WorkflowScheduler scheduler;

	static
	{
		System.setProperty("gridline.project.name", "zieook");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		scheduler = new WorkflowScheduler();
		scheduler.startup();

		controller = new WorkflowController(scheduler);
		controller.startup();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		controller.deleteContentProvider(CP);
		Thread.sleep(2000);
		controller.shutdown();
		Thread.sleep(2000);
		scheduler.shutdown();
	}

	@Test
	public void testCreateDrop()
	{

		// should not exist:
		System.out.println(controller.isContentProvider(CP));
		assertTrue(!controller.isContentProvider(CP));
		// create:
		controller.createContentProvider(new ContentProvider(CP, 100, true));
		// should exist:
		System.out.println(controller.isContentProvider(CP));
		assertTrue(controller.isContentProvider(CP));
		// delete:
		controller.deleteContentProvider(CP);
		// should not exist:
		assertTrue(!controller.isContentProvider(CP));
		System.out.println(controller.isContentProvider(CP));
		// create
		controller.createContentProvider(new ContentProvider(CP, 100, true));
		// should exist:
		assertTrue(controller.isContentProvider(CP));
		System.out.println(controller.isContentProvider(CP));
		// delete:
		controller.deleteContentProvider(CP);
		// should not exist:
		assertTrue(!controller.isContentProvider(CP));
		System.out.println(controller.isContentProvider(CP));
	}
}
