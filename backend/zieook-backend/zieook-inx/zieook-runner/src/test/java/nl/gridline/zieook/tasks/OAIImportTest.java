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
package nl.gridline.zieook.tasks;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import nl.gridline.zieook.OAIException;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.runners.OAIImport;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.joda.time.format.DateTimeFormat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * OAIImportTest.java created 5 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class OAIImportTest
{

	private static final Logger LOG = LoggerFactory.getLogger(OAIImportTest.class);

	static
	{
		System.setProperty("gridline.project.name", "zieook");
	}

	private static final String url = "http://ahm.adlibsoft.com/oaix/oai.ashx";
	private static OAIImport oaiImport;

	// private static final String url = "http://oai2.edit-extra.nu/request";

	/**
	 * @throws JAXBException
	 * @throws IOException
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws JAXBException, IOException
	{
		Config config = Config.getInstance();

		DummyTask task = new DummyTask();
		task.setConfig(new TaskConfig());

		Path output = new Path("/zieook/oai-import-test");

		oaiImport = new OAIImport(task, output);

		// Long from = parseDate(config.get(Config.OAI_GLOBAL_FROM));
		// Long until = parseDate(config.get(Config.OAI_GLOBAL_UNTIL));
		//

		oaiImport.setStartdate(DateTimeFormat.forPattern("yyyy-MM-dd").parseMillis("2011-01-01"));

		// else if (startdate != null)
		// {
		// oaiImport.setStartdate(startdate * 1000);
		// }
		// if (until != null)
		// {
		// oaiImport.setEnddate(until);
		// }
		// else if (enddate != null)
		// {
		// oaiImport.setEnddate(enddate * 1000);
		// }

		// dateformat = config.get(Config.OAI_GLOBAL_FORMATOVERRIDE);
		oaiImport.setDateformat("yyyy-MM-dd");
		oaiImport.setLimit(100);
		oaiImport.setWait(0);
		oaiImport.setSets(null);
		oaiImport.setSource(url);
		oaiImport.setMetadatPrefix("oai_dc");
		oaiImport.setFsDefaultName(config.get("fs.default.name"));

		cleanup(output);

	}

	@AfterClass
	public static void after()
	{

	}

	@Test
	public void testImport() throws JAXBException, IOException, OAIException, InterruptedException
	{
		oaiImport.start();
	}

	@Ignore
	private static void cleanup(Path path) throws IOException
	{
		Configuration hdfsConfig = new Configuration();
		hdfsConfig.set("fs.default.name", Config.getInstance().get("fs.default.name"));
		FileSystem hdfs = FileSystem.get(hdfsConfig);
		if (hdfs.exists(path))
		{
			LOG.info("deleting <{}>", path);
			assertTrue(hdfs.delete(path, true));
		}
		else
		{
			LOG.info("nothing to delete <{}>", path);
		}

	}

}
