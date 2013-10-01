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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.CollectionTable;
import nl.gridline.zieook.data.hbase.dao.RecommendationTable;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseRecommendationTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.model.CollectionItem;
import nl.gridline.zieook.model.Recommendations;
import nl.gridline.zieook.runners.recommender.ItemBasedRecommenderTool;
import nl.gridline.zieook.runners.taalserver.FreeTextVectorizeTool;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.mahout.math.hadoop.similarity.SimilarityType;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * TaalServerTest.java created 22 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@RunWith(SortedSuite.class)
public class TaalServerTest
{

	static
	{
		System.setProperty("gridline.project.name", "zieook");
	}

	private static final Logger LOG = LoggerFactory.getLogger(TaalServerTest.class);
	// zieook_collection_testCP006
	// settings that need a possible change:
	private static final String CP = "testCP006";
	private static final String COLLECTION = "am2";
	private static final String similarityClassName = SimilarityType.SIMILARITY_TANIMOTO_COEFFICIENT.toString();

	private static final String RECOMMENDER_FREE = "test-recommender";
	private static final String RECOMMENDER_TAAL = "test-recommender";

	public static final String taalserverOutput = CP + "/" + COLLECTION + "/" + RECOMMENDER_TAAL + "/output";
	public static final String freetaalOutput = CP + "/" + COLLECTION + "/" + RECOMMENDER_FREE + "/output";

	private static HBaseManager manager;
	private static TaskConfig taskConfig;
	private static Configuration hdfsConfig;

	// init
	// train recommender
	// test results

	@BeforeClass
	public static void init()
	{

	}

	public static class A_LoadData
	{
		// init - check preconditions.

		@BeforeClass
		public static void init() throws MasterNotRunningException, ZooKeeperConnectionException
		{
			manager = HBaseManager.getInstance("mulisch");

			hdfsConfig = new Configuration();
			hdfsConfig.set("fs.default.name", Config.getInstance().get("fs.default.name"));
		}

		@Test
		public void testTables()
		{
			// test if the tables exist
			HBaseCollectionTable table = new HBaseCollectionTable(manager);
			assertTrue(table.tableExists(CP));
		}

		@Test
		public void testData() throws IOException
		{
			cleanup(new Path(taalserverOutput));
			cleanup(new Path(freetaalOutput));
		}

	}

	public static class B_RunRecommenders
	{

		@BeforeClass
		public static void init()
		{
			// some static data:

			String input = new HBaseCollectionTable(manager).getTableName(CP);
			String output = new HBaseRecommendationTable(manager).getTableName(CP);

			// Text based recommender:
			taskConfig = new TaskConfig();
			taskConfig.setId(0);
			taskConfig.setProperty(TaskConfig.CP, CP); // the content provider
			taskConfig.setProperty(TaskConfig.COLLECTION, COLLECTION); // the collection name
			// taskConfig.setProperty(TaskConfig.RECOMMENDER, RECOMMENDER);
			taskConfig.setProperty(TaskConfig.INPUT_TABLE, input.toString());
			taskConfig.setProperty(TaskConfig.OUTPUT_TABLE, output.toString()); // the source path
			taskConfig.setProperty(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME, similarityClassName);
			taskConfig.setProperty(TaskConfig.TEXT_ANALYSIS_USE_DESCRIPTION, "true");
			taskConfig.setProperty(TaskConfig.TEXT_ANALYSIS_USE_TITLE, "true");
			taskConfig.setProperty(TaskConfig.BOOLEAN_DATA, "false");
			taskConfig.setProperty(TaskConfig.MAX_PREFS_PER_USER, 10);
			taskConfig.setProperty(TaskConfig.MAX_SIMILARITRIES_PER_ITEM, 100);
			taskConfig.setProperty(TaskConfig.MAX_CO_OCCURRENCES_PER_ITEM, 100);
		}

		// execute taalserver
		// This part of the code is not open-source, check with GridLine info@gridline.nl for more information
		// @Test
		// public void executeTaalServer() throws IOException, InterruptedException, ClassNotFoundException
		// {
		// TaskConfig config = (TaskConfig) taskConfig.clone();
		// taskConfig.setProperty(TaskConfig.RECOMMENDER, RECOMMENDER_TAAL);
		// String input = config.get(TaskConfig.INPUT_TABLE);
		//
		// TextVectorizeTool tool = new TextVectorizeTool(new EmptyTask(config)).configure(input, taalserverOutput);
		// assertTrue(tool.execute());
		// }

		// execute freetaalserver
		@Test
		public void executeFreeTaalServer() throws IOException, InterruptedException, ClassNotFoundException
		{
			TaskConfig config = (TaskConfig) taskConfig.clone();
			taskConfig.setProperty(TaskConfig.RECOMMENDER, RECOMMENDER_FREE);
			String input = config.get(TaskConfig.INPUT_TABLE);
			FreeTextVectorizeTool tool = new FreeTextVectorizeTool(new EmptyTask(config)).configure(input, freetaalOutput);
			assertTrue(tool.execute());
		}

	}

	public static class C_ExecuteRecommenders
	{

		private static TaskConfig rtaskConfig;
		private static String output;

		@BeforeClass
		public static void init()
		{

			String input = new HBaseUserTable(manager).getTableName(CP);
			output = new HBaseRecommendationTable(manager).getTableName(CP);

			// Itembased recommender task:
			rtaskConfig = new TaskConfig();
			rtaskConfig.setId(0);
			rtaskConfig.setProperty(TaskConfig.CP, CP); // the content provider
			rtaskConfig.setProperty(TaskConfig.COLLECTION, COLLECTION); // the collection name
			// rtaskConfig.setProperty(TaskConfig.RECOMMENDER, RECOMMENDER); // the recommender name
			rtaskConfig.setProperty(TaskConfig.INPUT_TABLE, input); // the output table
			rtaskConfig.setProperty(TaskConfig.OUTPUT_TABLE, output); // the source table
			rtaskConfig.setProperty(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME,
					SimilarityType.SIMILARITY_TANIMOTO_COEFFICIENT.toString());

			rtaskConfig.setProperty(TaskConfig.BOOLEAN_DATA, "false");
			rtaskConfig.setProperty(TaskConfig.MAX_PREFS_PER_USER, 10);
			rtaskConfig.setProperty(TaskConfig.MAX_SIMILARITRIES_PER_ITEM, 100);
			rtaskConfig.setProperty(TaskConfig.MAX_CO_OCCURRENCES_PER_ITEM, 100);

			rtaskConfig.setProperty("skipPrepare", true);
		}

		@Test
		public void freeTaalServer() throws IOException, InterruptedException, ClassNotFoundException
		{
			TaskConfig config = (TaskConfig) rtaskConfig.clone();
			config.setProperty(TaskConfig.RECOMMENDER, RECOMMENDER_FREE); // the recommender name
			ItemBasedRecommenderTool recommenderTool = new ItemBasedRecommenderTool(new EmptyTask(config)).configure(
					freetaalOutput, output);
			assertTrue(recommenderTool.execute());
		}

		@Test
		public void taalServer() throws IOException, InterruptedException, ClassNotFoundException
		{
			TaskConfig config = (TaskConfig) rtaskConfig.clone();
			config.setProperty(TaskConfig.RECOMMENDER, RECOMMENDER_TAAL); // the recommender name
			ItemBasedRecommenderTool recommenderTool = new ItemBasedRecommenderTool(new EmptyTask(config)).configure(
					taalserverOutput, output);
			assertTrue(recommenderTool.execute());
		}
	}

	public static class D_TestResults
	{
		// test if we get recommendations, and some are as we expect.
		private static SortedSet<Long> items;
		private static RecommendationTable recommender;

		@BeforeClass
		public static void init()
		{
			// collect item id's
			CollectionTable table = new CollectionTable(new HBaseCollectionTable(manager));
			items = new TreeSet<Long>();
			List<CollectionItem> list = table.getItems(CP, COLLECTION, 50);
			for (CollectionItem i : list)
			{
				items.add(i.getId());
			}
			long last = items.last();
			while (last != items.last())
			{
				last = items.last();
				list = table.getItems(CP, COLLECTION, last, 50);
				for (CollectionItem i : list)
				{
					items.add(i.getId());
				}

			}

			// check size: assertEquals(item.size(), knownSize);
			recommender = new RecommendationTable(new HBaseRecommendationTable(manager));
		}

		@Test
		public void retrieveResults() throws IOException
		{
			File freeTaal = new File("target/freetaalserver").getCanonicalFile();
			File taal = new File("target/taalserver").getCanonicalFile();
			if (freeTaal.exists() && freeTaal.isDirectory())
			{
				Files.deleteRecursively(freeTaal);
			}
			if (taal.exists() && taal.isDirectory())
			{
				Files.deleteRecursively(taal);
			}
			copyToLocal(new Path(freetaalOutput), new Path("target/freetaalserver"));
			copyToLocal(new Path(taalserverOutput), new Path("target/taalserver"));
		}

		@Test
		public void testFreeRecommendation()
		{
			LOG.info("testing: {} items for data", items.size());
			int count = 0;
			for (long itemID : items)
			{
				Recommendations result = recommender.getRecommendationsFor(CP, COLLECTION, RECOMMENDER_FREE, itemID, 100,
						null);
				count += result.getRecommended().size();
				assertTrue(result.getRecommended().size() > 0);
			}
			LOG.info("found {} recommendations, avg per {}", count, (count / (float) items.size()));
		}

		@Test
		public void testTaalServerRecommendation()
		{
			LOG.info("testing: {} items for data", items.size());
			int count = 0;
			for (long itemID : items)
			{
				Recommendations result = recommender.getRecommendationsFor(CP, COLLECTION, RECOMMENDER_TAAL, itemID, 100,
						null);
				count += result.getRecommended().size();
				assertTrue(result.getRecommended().size() > 0);
			}
			LOG.info("found {} recommendations, avg per {}", count, (count / (float) items.size()));
		}
		// check if there data in the files.. -

		// check for some recommendations...

	}

	// /**
	// * copy a file to hdfs
	// * @param local local Path
	// * @param remote remote (hdfs) path
	// * @throws IOException
	// */
	// @Ignore
	// private static void copy(Path local, Path remote) throws IOException
	// {
	// LOG.info("copy {} to {}", local, remote);
	// FileSystem hdfs = FileSystem.get(hdfsConfig);
	// hdfs.copyFromLocalFile(local, remote);
	// }

	@Ignore
	private static void copyToLocal(Path remote, Path local) throws IOException
	{
		LOG.info("copy {} to {}", remote, local);
		FileSystem hdfs = FileSystem.get(hdfsConfig);
		hdfs.copyToLocalFile(remote, local);
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
