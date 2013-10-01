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

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseRecommendationTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.mapreduce.TaskConfig;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataImportExportTest
{

	private static final Logger LOG = LoggerFactory.getLogger(ItemBasedRecommenderTaskTest.class);

	private static TaskConfig inxConfig;
	// private static TaskConfig recommenderConfig;
	private static Path input;
	private static Path output;
	// private static Path output2;

	private static Configuration hdfsConfig;

	private static final String CP = "gridline-movielens";
	private static final String COLLECTION = "movielens";
	private static final String RECOMMENDER = "movielens-tanimo";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		// - create config table
		// - load data in hbase
		Config config = Config.getInstance();

		String hdfs = config.get(Config.ZIEOOK_HDFS_SERVER) + config.get(Config.ZIEOOK_HDFS_PATH);
		output = new Path(hdfs, "datainx/out");
		input = new Path(hdfs, "datainx/in");
		// output2 = new Path(hdfs, "datainx/out2");

		inxConfig = new TaskConfig();
		inxConfig.setProperty(TaskConfig.CP, CP); // the content provider
		inxConfig.setProperty(TaskConfig.COLLECTION, COLLECTION); // the collection name
		inxConfig.setProperty(TaskConfig.RECOMMENDER, RECOMMENDER); // the recommender name
		inxConfig.setProperty(TaskConfig.INPUT_PATH, input.toString());
		inxConfig.setProperty(TaskConfig.OUTPUT_PATH, output.toString());

		//
		//
		// // create TaskConfig
		// recommenderConfig = new TaskConfig();
		// recommenderConfig.setId(0);
		// recommenderConfig.setProperty(TaskConfig.CP, CP); // the content provider
		// recommenderConfig.setProperty(TaskConfig.COLLECTION, COLLECTION); // the collection name
		// recommenderConfig.setProperty(TaskConfig.RECOMMENDER, RECOMMENDER);
		// recommenderConfig.setProperty(TaskConfig.INPUT_PATH, input.toString());
		// recommenderConfig.setProperty(TaskConfig.OUTPUT_PATH, output.toString()); // the source path
		// recommenderConfig.setProperty(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME,
		// SimilarityType.SIMILARITY_COOCCURRENCE.toString());
		// recommenderConfig.setProperty(TaskConfig.NUM_RECOMMENDATIONS, 10);
		// recommenderConfig.setProperty(TaskConfig.BOOLEAN_DATA, "false");
		// recommenderConfig.setProperty(TaskConfig.MAX_PREFS_PER_USER, 10);
		// recommenderConfig.setProperty(TaskConfig.MAX_SIMILARITRIES_PER_ITEM, 100);
		// recommenderConfig.setProperty(TaskConfig.MAX_CO_OCCURRENCES_PER_ITEM, 100);

		hdfsConfig = new Configuration();
		hdfsConfig.set("fs.default.name", Config.getInstance().get("fs.default.name"));

	}

	@Before
	public void clean() throws IOException
	{
		HBaseManager manager = HBaseManager.getInstance(Config.getInstance().getZooKeeperHost());

		// create fresh tables:

		// collection table:
		HBaseCollectionTable collectionTable = new HBaseCollectionTable(manager);
		if (collectionTable.tableExists(CP))
		{
			collectionTable.drop(CP);
		}
		collectionTable.create(CP);

		// user table:
		HBaseUserTable userTable = new HBaseUserTable(manager);
		if (userTable.tableExists(CP))
		{
			userTable.drop(CP);
		}
		userTable.create(CP);

		// recommendation table:
		HBaseRecommendationTable recommendationTable = new HBaseRecommendationTable(manager);
		if (recommendationTable.tableExists(CP))
		{
			recommendationTable.drop(CP);
		}
		recommendationTable.create(CP);

		// clean-up data files:
		FileSystem fs = FileSystem.get(hdfsConfig);
		if (fs.exists(input))
		{
			fs.delete(input, true);
		}
		if (fs.exists(output))
		{
			fs.delete(output, true);
		}

	}

	// @Test
	// public void categoriesImport() throws Exception
	// {
	// CategoriesExtractTask task = new CategoriesExtractTask();
	// task.setConfig(inxConfig);
	// TaskConfig result = task.call();
	// assertTrue(result.isSucceeded());
	// }

	@Test
	@Ignore
	public void recommenderTool() throws Exception
	{
		try
		{
			LOG.info("copy data from local to HDFS");
			FileSystem hdfs = FileSystem.get(hdfsConfig);
			// import:
			hdfs.copyFromLocalFile(new Path("test-data-small/movies.dat"), new Path(input, "movies.dat"));
			hdfs.copyFromLocalFile(new Path("test-data-small/ratings.dat"), new Path(input, "ratings.dat"));
			hdfs.copyFromLocalFile(new Path("test-data-small/users.dat"), new Path(input, "users.dat"));
		}
		catch (IOException e)
		{
			LOG.error("", e);
			fail(e.getMessage());
		}

		// import data into HBase:
		DataFileImportTask imp = new DataFileImportTask();
		imp.setConfig(inxConfig);
		LOG.info("Create data import tasks");

		inxConfig.setProperty(TaskConfig.DATA_PART, TaskConfig.DATA_PART_COLLECTION);
		inxConfig.setProperty(TaskConfig.INPUT_PATH, new Path(input, "movies.dat").toString());
		imp.call();
		LOG.info("COLLECTION data imported");
		inxConfig.setProperty(TaskConfig.DATA_PART, TaskConfig.DATA_PART_RATINGS);
		inxConfig.setProperty(TaskConfig.INPUT_PATH, new Path(input, "ratings.dat").toString());
		imp.call();
		LOG.info("RATINGS data imported");
		inxConfig.setProperty(TaskConfig.DATA_PART, TaskConfig.DATA_PART_USERS);
		inxConfig.setProperty(TaskConfig.INPUT_PATH, new Path(input, "users.dat").toString());
		imp.call();
		LOG.info("USER data imported");

	}

	@Test
	@Ignore
	public void dataPrepareTool() throws Exception
	{

		// put data in hbase:
		try
		{

			LOG.info("copy data from local to HDFS");
			FileSystem hdfs = FileSystem.get(hdfsConfig);
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
		imp.setConfig(inxConfig);
		LOG.info("Create data import tasks");

		inxConfig.setProperty(TaskConfig.DATA_PART, TaskConfig.DATA_PART_COLLECTION);
		inxConfig.setProperty(TaskConfig.INPUT_PATH, new Path(input, "movies.dat").toString());
		imp.call();
		LOG.info("COLLECTION data imported");
		inxConfig.setProperty(TaskConfig.DATA_PART, TaskConfig.DATA_PART_RATINGS);
		inxConfig.setProperty(TaskConfig.INPUT_PATH, new Path(input, "ratings.dat").toString());
		imp.call();
		LOG.info("RATINGS data imported");
		inxConfig.setProperty(TaskConfig.DATA_PART, TaskConfig.DATA_PART_USERS);
		inxConfig.setProperty(TaskConfig.INPUT_PATH, new Path(input, "users.dat").toString());
		imp.call();
		LOG.info("USER data imported");

	}

}
