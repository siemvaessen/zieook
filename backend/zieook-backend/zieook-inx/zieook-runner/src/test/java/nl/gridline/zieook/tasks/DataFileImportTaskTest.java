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

import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import nl.gridline.zieook.OAIException;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.mapreduce.TaskConfig;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.zookeeper.KeeperException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests dat import for movielens & edit data
 * <p />
 * Project zieook-runner<br />
 * DataFileImportTaskTest.java created 28 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class DataFileImportTaskTest
{
	static
	{
		System.setProperty("gridline.project.name", "zieook");
	}

	private static final Logger LOG = LoggerFactory.getLogger(DataFileImportTaskTest.class);

	private static final String URL1 = "http://oai2.edit-extra.nu/request";
	private static final String URL2 = "http://ahm.adlibsoft.com/oaix/oai.ashx";

	private static final String SET = "Beeld en Geluid";

	private static final String CP = "gridline-movielens";
	private static final String EDT_CP = "gridline-edit";
	private static final String AM_CP = "gridline-am";

	private static final String COLLECTION = "movielens2";
	private static final String EDT_COLLECTION = "beeldengeluid2";
	private static final String AM_COLLECTION = "am2";

	private Configuration hdfsConfig;

	private TaskConfig importMovielensConfig;
	private TaskConfig importEDTConfig;
	private TaskConfig importAMConfig;
	private TaskConfig exportEditConfig;

	private Path output;
	private Path input;

	@Before
	public void setUp() throws Exception
	{
		// - create config table
		// - load data in hbase
		Config config = Config.getInstance();

		String hdfs = config.get(Config.ZIEOOK_HDFS_SERVER) + config.get(Config.ZIEOOK_HDFS_PATH);
		output = new Path(hdfs, "import/out");
		input = new Path(hdfs, "import/in");

		importMovielensConfig = new TaskConfig();
		importMovielensConfig.setProperty(TaskConfig.CP, CP); // the content provider
		importMovielensConfig.setProperty(TaskConfig.COLLECTION, COLLECTION); // the collection name
		importMovielensConfig.setProperty(TaskConfig.INPUT_PATH, input.toString());

		importEDTConfig = new TaskConfig();
		importEDTConfig.setProperty(TaskConfig.CP, EDT_CP); // the content provider
		importEDTConfig.setProperty(TaskConfig.COLLECTION, EDT_COLLECTION); // the collection name
		importEDTConfig.setProperty(TaskConfig.INPUT_PATH, URL1);
		importEDTConfig.setProperty(TaskConfig.OAI_SETS, SET);
		importEDTConfig.setProperty(TaskConfig.OAI_ITEM_LIMIT, 200);

		importAMConfig = new TaskConfig();
		importAMConfig.setProperty(TaskConfig.CP, AM_CP);
		importAMConfig.setProperty(TaskConfig.COLLECTION, AM_COLLECTION);
		importAMConfig.setProperty(TaskConfig.INPUT_PATH, URL2);
		importAMConfig.setProperty(TaskConfig.OAI_ITEM_LIMIT, 200);

		exportEditConfig = new TaskConfig();
		exportEditConfig.setProperty(TaskConfig.CP, EDT_CP); // content provider name
		exportEditConfig.setProperty(TaskConfig.COLLECTION, EDT_COLLECTION); // the collection name
		exportEditConfig.setProperty(TaskConfig.OUTPUT_PATH, output.toString());

		hdfsConfig = new Configuration();
		hdfsConfig.set("fs.default.name", Config.getInstance().get("fs.default.name"));

		HBaseManager manager = HBaseManager.getInstance(Config.getInstance().getZooKeeperHost());

		// clean:
		new HBaseCollectionTable(manager).drop(AM_CP);
		new HBaseCollectionTable(manager).drop(EDT_CP);
		new HBaseCollectionTable(manager).drop(CP);
		new HBaseUserTable(manager).drop(CP);

	}

	@Test
	@Ignore
	public void executeCZPOaiImport() throws Exception
	{

		HBaseManager manager = HBaseManager.getInstance(Config.getInstance().getZooKeeperHost());

		new HBaseCollectionTable(manager).drop(EDT_CP);
		new HBaseCollectionTable(manager).create(EDT_CP);

		OAIImportTask task = new OAIImportTask();
		task.setConfig(importEDTConfig);
		task.call();

		// new CollectionExportTool().configure(HBaseTableConstants.COLLECTION_TABLE + EDT_CP,
		// output.toString()).execute();

	}

	@Test
	public void executeOAIDCImport() throws IOException, InterruptedException, OAIException, KeeperException,
			JAXBException, ClassNotFoundException
	{
		HBaseManager manager = HBaseManager.getInstance(Config.getInstance().getZooKeeperHost());
		new HBaseCollectionTable(manager).drop(AM_CP);
		new HBaseCollectionTable(manager).create(AM_CP);
		OAIImportTask task = new OAIImportTask();
		task.setConfig(importAMConfig);

		task.call();
	}

	@Test
	@Ignore
	public void executeCZPOaiExport() throws Exception
	{
		LOG.info("copy data from table to text file");
		FileSystem hdfs = FileSystem.get(hdfsConfig);
		// cleanup - output folder:
		hdfs.delete(output, true);
		DataFileExportTask export = new DataFileExportTask();

		exportEditConfig.setProperty(TaskConfig.DATA_PART, TaskConfig.DATA_PART_COLLECTION);

		export.setConfig(exportEditConfig);
		export.call();
	}

	@Test
	@Ignore
	public void executeMovielensImport() throws Exception
	{
		HBaseManager manager = HBaseManager.getInstance(Config.getInstance().getZooKeeperHost());
		new HBaseCollectionTable(manager).drop(CP);
		new HBaseCollectionTable(manager).create(CP);

		new HBaseUserTable(manager).drop(CP);
		new HBaseUserTable(manager).create(CP);

		// drop HBase tables
		// put dat in hbase:
		try
		{

			LOG.info("copy data from local to HDFS");
			FileSystem hdfs = FileSystem.get(hdfsConfig);

			// cleanup:
			hdfs.delete(input, true);
			hdfs.delete(output, true);

			// import:
			hdfs.copyFromLocalFile(new Path("test-data-big/movies.dat"), new Path(input, "movies.dat"));
			hdfs.copyFromLocalFile(new Path("test-data-big/ratings.dat"), new Path(input, "ratings.dat"));
			hdfs.copyFromLocalFile(new Path("test-data-big/users.dat"), new Path(input, "users.dat"));
		}
		catch (IOException e)
		{
			LOG.error("", e);
			fail(e.getMessage());
		}

		// import data into HBase:
		DataFileImportTask imp = new DataFileImportTask();
		imp.setConfig(importMovielensConfig);
		LOG.info("Create data import tasks");
		try
		{
			importMovielensConfig.setProperty(TaskConfig.DATA_PART, TaskConfig.DATA_PART_COLLECTION);
			importMovielensConfig.setProperty(TaskConfig.INPUT_PATH, new Path(input, "movies.dat").toString());
			imp.call();
			LOG.info("COLLECTION data imported");
			importMovielensConfig.setProperty(TaskConfig.DATA_PART, TaskConfig.DATA_PART_RATINGS);
			importMovielensConfig.setProperty(TaskConfig.INPUT_PATH, new Path(input, "ratings.dat").toString());
			imp.call();
			LOG.info("RATINGS data imported");
			importMovielensConfig.setProperty(TaskConfig.DATA_PART, TaskConfig.DATA_PART_USERS);
			importMovielensConfig.setProperty(TaskConfig.INPUT_PATH, new Path(input, "users.dat").toString());
			imp.call();
			LOG.info("USER data imported");
		}
		catch (Exception e)
		{
			LOG.error("", e);
			fail(e.getMessage());
		}

		// DataPrepareTool prepare = new DataPrepareTool();
		// prepare.set(TaskConfig.COLLECTION, COLLECTION);
		// prepare.set(TaskConfig.CP, CP);
		// prepare.configure(HBaseTableConstants.USERTABLE_NAME + CP, output.toString());
		// boolean result = prepare.execute();
		// assertTrue(result);

		// test output...

	}
}
