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
package nl.gridline.zieook.configuration.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import nl.gridline.config.model.Configuration;
import nl.gridline.config.model.Property;

import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-configuration<br />
 * ConfigurationTest.java created 14 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class ConfigurationTest
{

	@Test
	public final void testConfigurationWriteRead()
	{
		try
		{
			File file = File.createTempFile("configuration-test", ".xml");
			file.deleteOnExit();

			System.out.println("writing to: " + file.toString());

			Configuration configuration = new Configuration(Arrays.asList(new Property("testname", "testvalue")));

			String result = configuration.marshall();
			FileWriter writer = new FileWriter(file);
			writer.write(result);
			writer.flush();
			writer.close();

			// read:
			Configuration newconfiguration = Configuration.create(file);

			assertEquals(configuration, newconfiguration);

		}
		catch (JAXBException e)
		{
			e.printStackTrace();

		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
}
