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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * WorkflowControllerTest.java created 10 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class FileReadTest
{

	@Test
	public void fileXMLTest()
	{
		System.out.println(System.getProperty("java.version"));
		File file = new File("zieook-default.xml");
		System.out.print("checking: " + file);

		try
		{
			file = file.getCanonicalFile();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertTrue(file.isFile());
		assertTrue(file.canRead());
		assertTrue(file.exists());
		System.out.println("\t ok");
	}

	@Test
	public void urlCuttofftest()
	{
		String url = "http://62.221.199.163:4296/?verb=ListRecords&metadataPrefix=oai_dc";
		int querystart = url.indexOf('?');

		System.out.println(url.substring(0, querystart));

		assertEquals("http://62.221.199.163:4296/", url.substring(0, querystart));

	}

}