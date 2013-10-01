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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.RecommendationTable;
import nl.gridline.zieook.data.hbase.model.HBaseRecommendationTable;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.model.Recommendations;
import nl.gridline.zieook.runners.recommender.ItemBasedRecommenderTool;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.math.hadoop.similarity.SimilarityType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * ItemBasedRecommenderTaskTest.java created 28 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@RunWith(SortedSuite.class)
public class ItemBasedRecommenderTaskTest
{
	private static final Logger LOG = LoggerFactory.getLogger(ItemBasedRecommenderTaskTest.class);

	// This is what's going to be tested:
	// - load data
	// - execute recommender
	// - test if recommender has results

	// test input definition:
	private static final File ratingsFile = new File("test-data-big/ratings.dat"); // ratings file
	// similarity type:
	private static final String classname = SimilarityType.SIMILARITY_COOCCURRENCE.toString();

	// Constants that do not need a regular change:
	private static final String CP = "TEST_gridline";
	private static final String COLLECTION = "TEST_movielens";
	private static final String RECOMMENDER = "itembased";

	private static Path output;
	private static Path input;
	private static Configuration hdfsConfig;
	private static File setTest;
	private static HBaseRecommendationTable recommendationsTable;
	private static HBaseManager manager;

	static
	{
		System.setProperty("gridline.project.name", "zieook");
	}

	@BeforeClass
	public static void setup() throws IOException
	{
		hdfsConfig = new Configuration();
		hdfsConfig.set("fs.default.name", Config.getInstance().get("fs.default.name"));

		Config config = Config.getInstance();
		String hdfs = config.get(Config.ZIEOOK_HDFS_SERVER) + config.get(Config.ZIEOOK_HDFS_PATH);
		output = new Path(hdfs, "TEST_recommender-userbased/out");
		input = new Path(hdfs, "TEST_recommender-userbased/in");

		// clean-up old data:
		cleanup(input);
		cleanup(output);
	}

	@AfterClass
	public static void cleanup()
	{
		// cleanup
	}

	public static class A_LoadData
	{
		private static File inputFile;

		@BeforeClass
		public static void setup() throws IOException
		{
			// load some percentage of the data (using a fixed random seed)

			// data prepare / split should be done in the global init.

			// put data in hdfs, the 'input' path

			inputFile = ratingsFile.getCanonicalFile();
			assertTrue(inputFile.exists());
			LOG.info("input data: {}", inputFile);

			setTest = new File("target/test-set.dat");

			// check if exists, delete if so:
			if (setTest.exists())
			{
				LOG.info("cleanup old data: {}", setTest);
				assertTrue(setTest.delete());
			}

			manager = HBaseManager.getInstance("mulisch");
		}

		@AfterClass
		public static void cleanup()
		{

		}

		@Test
		public void recommendationsTable() throws IOException
		{
			recommendationsTable = new HBaseRecommendationTable(manager);
			recommendationsTable.drop(CP);
			if (!recommendationsTable.tableExists(CP))
			{
				assertTrue(recommendationsTable.create(CP));
			}
			assertTrue(recommendationsTable.tableExists(CP));
		}

		@Test
		public void loadData() throws IOException
		{
			BufferedWriter testData = new BufferedWriter(new FileWriter(setTest));
			BufferedReader bin = new BufferedReader(new FileReader(inputFile));

			int count = 0;
			try
			{
				String line = null;
				while ((line = bin.readLine()) != null)
				{
					count++;
					// In: UserID::MovieID::Rating::Timestamp
					String[] elems = line.split("::");
					// Out: userID,itemID[,preferencevalue]
					String out = new StringBuilder().append(elems[0]).append(',').append(elems[1]).append(',')
							.append(elems[2]).append('\n').toString();

					// this is test data:
					testData.write(out);
				}
			}
			finally
			{
				bin.close();
				testData.close();
			}

			LOG.info("input data: {} ratings", count);

			// copy data to hdfs
			copy(new Path(setTest.toURI()), input);
		}
	}

	public static class B_ExecuteRecommender
	{
		// generated constants:
		private static TaskConfig rtaskConfig;

		@BeforeClass
		public static void setup()
		{
			rtaskConfig = new TaskConfig();
			rtaskConfig.setId(0);
			rtaskConfig.setProperty(TaskConfig.CP, CP); // the content provider
			rtaskConfig.setProperty(TaskConfig.COLLECTION, COLLECTION); // the collection name
			rtaskConfig.setProperty(TaskConfig.RECOMMENDER, RECOMMENDER);
			rtaskConfig.setProperty(TaskConfig.INPUT_PATH, input.toString());
			rtaskConfig.setProperty(TaskConfig.OUTPUT_PATH, output.toString()); // the source path
			rtaskConfig.setProperty(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME, classname);
			rtaskConfig.setProperty(TaskConfig.OUTPUT_TABLE, recommendationsTable.getTableName(CP));
			rtaskConfig.setProperty(TaskConfig.NUM_RECOMMENDATIONS, 100);
			rtaskConfig.setProperty(TaskConfig.BOOLEAN_DATA, "false");
			rtaskConfig.setProperty(TaskConfig.MAX_PREFS_PER_USER, 10);
			rtaskConfig.setProperty(TaskConfig.MAX_SIMILARITRIES_PER_ITEM, 100);
			rtaskConfig.setProperty(TaskConfig.MAX_CO_OCCURRENCES_PER_ITEM, 100);

			// hard-skip the prepare step, we dump the ratings directly into the
			rtaskConfig.setProperty("skipPrepare", true);

		}

		@AfterClass
		public static void cleanup()
		{
		}

		@Test
		public void executeRecommender() throws IOException, InterruptedException, ClassNotFoundException
		{
			ItemBasedRecommenderTool recommenderTool = new ItemBasedRecommenderTool(new EmptyTask(rtaskConfig)).configure(
					input.toString(), rtaskConfig.get(TaskConfig.OUTPUT_TABLE));

			assertTrue(recommenderTool.execute());
		}
	}

	public static class C_TestResults
	{

		private static RecommendationTable table;
		private static Map<Long, Integer> items;

		@BeforeClass
		public static void setup() throws IOException
		{
			table = new RecommendationTable(recommendationsTable);

			File inputFile = ratingsFile.getCanonicalFile();
			assertTrue(inputFile.exists());
			LOG.info("input data: {}", inputFile);

			BufferedReader bin = new BufferedReader(new FileReader(inputFile));
			items = new HashMap<Long, Integer>();

			int count = 0;
			try
			{
				String line = null;
				while ((line = bin.readLine()) != null)
				{
					count++;
					// In: UserID::MovieID::Rating::Timestamp
					String[] elems = line.split("::");

					long id = Long.parseLong(elems[1]);

					// Out: itemID
					if (items.containsKey(id))
					{
						items.put(id, items.get(id) + 1);
					}
					else
					{
						items.put(id, 1);
					}
				}
			}
			finally
			{
				bin.close();
			}

			LOG.info("input data: {} ratings", count);

			// copy data to hdfs
			copy(new Path(setTest.toURI()), input);

		}

		@AfterClass
		public static void cleanup()
		{
			items = null;
		}

		@Test
		public void testResults()
		{
			int count = 0;
			for (Map.Entry<Long, Integer> item : items.entrySet())
			{
				Recommendations result = table.getRecommendationsFor(CP, COLLECTION, RECOMMENDER, item.getKey(), 100, null);
				if (result != null)
				{
					assertTrue(result.getSize() > 0);
				}
				else
				{
					if (item.getValue() > 1)
					{
						// LOG.warn("dodgy - there should have been a recommendation for: {} it has {} ratings",
						// item.getKey(),
						// item.getValue());
						count++;
					}
				}
			}
			LOG.info("there where {} out of {} without recommendations", count, items.size());
		}
	}

	/**
	 * copy a file to hdfs
	 * @param local local Path
	 * @param remote remote (hdfs) path
	 * @throws IOException
	 */
	@Ignore
	private static void copy(Path local, Path remote) throws IOException
	{
		LOG.info("copy {} to {}", local, remote);
		FileSystem hdfs = FileSystem.get(hdfsConfig);
		hdfs.copyFromLocalFile(local, remote);
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
