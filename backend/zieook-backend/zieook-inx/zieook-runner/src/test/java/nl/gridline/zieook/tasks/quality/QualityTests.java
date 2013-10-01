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
package nl.gridline.zieook.tasks.quality;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.RecommendationTable;
import nl.gridline.zieook.data.hbase.model.HBaseRecommendationTable;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.model.Recommend;
import nl.gridline.zieook.model.Recommendations;
import nl.gridline.zieook.runners.recommender.UserBasedRecommenderTool;
import nl.gridline.zieook.tasks.EmptyTask;
import nl.gridline.zieook.tasks.SortedSuite;

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
 * Tests the quality of a recommender. using a ratings file (movielens) a train / test set split percentage and a
 * similarity classname. The random seed can also be fixated.
 * <p />
 * Project zieook-runner<br />
 * QualityTests.java created 14 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@RunWith(SortedSuite.class)
public class QualityTests
{

	private static final Logger LOG = LoggerFactory.getLogger(QualityTests.class);

	static
	{
		System.setProperty("gridline.project.name", "zieook");
	}

	/*
	 * set the file, random seed, train percentage and similarity class to get comparable results:
	 */

	// Test input definition:
	private static final File ratingsFile = new File("test-data-big/ratings.dat"); // ratings file

	// Variable random seed - set this to a fixed number to get reproducible results
	private static final Random rnd = new Random();
	private static float train = 0.7f; // train percentage
	private static final String classname = SimilarityType.SIMILARITY_COOCCURRENCE.toString();
	private static final double relevanceThreshold = 2.0; // rating threshold
	private static final int at = 10; // mininmum number of ratings

	// Constants that do not need a regular change:
	private static final String CP = "TEST_gridline";
	private static final String COLLECTION = "TEST_movielens";
	private static final String RECOMMENDER = "userbased";

	// Generated static values, don't touch:
	private static Path output;
	private static Path input;
	private static Configuration hdfsConfig;
	private static File setTest;
	private static File setTrain;
	private static HBaseRecommendationTable recommendationsTable;
	private static HBaseManager manager;

	@BeforeClass
	public static void init() throws IOException
	{
		// set globals

		hdfsConfig = new Configuration();
		hdfsConfig.set("fs.default.name", Config.getInstance().get("fs.default.name"));

		Config config = Config.getInstance();
		String hdfs = config.get(Config.ZIEOOK_HDFS_SERVER) + config.get(Config.ZIEOOK_HDFS_PATH);
		output = new Path(hdfs, "TEST_recommender-userbased/out");
		input = new Path(hdfs, "TEST_recommender-userbased/in");

		// clean-up old data:
		cleanup(input);
		cleanup(output);

		// create recommendations table:

	}

	@AfterClass
	public static void shutdown()
	{
		// clean-up after
	}

	public static class A_LoadData
	{
		private static File inputFile;

		@BeforeClass
		public static void init() throws IOException
		{
			// load some percentage of the data (using a fixed random seed)

			// data prepare / split should be done in the global init.

			// put data in hdfs, the 'input' path

			inputFile = ratingsFile.getCanonicalFile();
			assertTrue(inputFile.exists());
			LOG.info("input data: {}", inputFile);

			setTest = new File("target/test-set.dat");
			LOG.info("test set: {}", setTest);

			setTrain = new File("target/train-set.dat");
			LOG.info("train set: {}", setTrain);

			// check if exists, delete if so:
			if (setTest.exists())
			{
				LOG.info("cleanup old data: {}", setTest);
				assertTrue(setTest.delete());
			}
			if (setTrain.exists())
			{
				LOG.info("cleanup train data: {}", setTrain);
				assertTrue(setTrain.delete());
			}

			manager = HBaseManager.getInstance("mulisch");

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
		public void splitAndLoadData() throws IOException
		{
			LOG.info("splitting data with {} train data", train);
			BufferedWriter trainData = new BufferedWriter(new FileWriter(setTrain));
			BufferedWriter testData = new BufferedWriter(new FileWriter(setTest));
			BufferedReader bin = new BufferedReader(new FileReader(inputFile));

			int count = 0, trainCnt = 0, testCnt = 0;
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
					if (rnd.nextFloat() > train)
					{
						// this is test data:
						testData.write(out);
						testCnt++;
					}
					else
					{
						// this is train data:
						trainData.write(out);
						trainCnt++;
					}
				}
			}
			finally
			{
				bin.close();
				testData.close();
				trainData.close();
			}

			// split done:
			LOG.info("input data: {} ratings", count);
			LOG.info("test data: {} ratings", testCnt);
			LOG.info("train data: {} ratings", trainCnt);

			// copy data to hdfs
			copy(new Path(setTest.toURI()), input);
		}
	}

	public static class B_UserBasedRecommender
	{
		// generated constants:
		private static TaskConfig rtaskConfig;

		@BeforeClass
		public static void init()
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

		@Test
		public void trainRecommender() throws Exception
		{
			UserBasedRecommenderTool recommenderTool = new UserBasedRecommenderTool(new EmptyTask(rtaskConfig)).configure(
					input.toString(), rtaskConfig.get(TaskConfig.OUTPUT_TABLE));

			assertTrue(recommenderTool.execute());

			// a simple f-score test, using the remaining data...
			LOG.info("test recommender");
		}
	}

	public static class C_TestRatedResults
	{

		private static RecommendationTable recommendations;
		private static Map<Long, Set<Long>> testSet;
		private static Map<Long, Set<Long>> trainSet;

		@BeforeClass
		public static void init() throws IOException
		{
			// get the recommender table:
			recommendations = new RecommendationTable(recommendationsTable);

			// gather users from test set:
			testSet = new HashMap<Long, Set<Long>>();
			long count = 0;
			BufferedReader bin = new BufferedReader(new FileReader(setTest));
			try
			{
				String line = null;

				while ((line = bin.readLine()) != null)
				{
					count++;
					// In: userID,itemID[,preferencevalue]
					String elems[] = line.split(",");
					long user = Long.parseLong(elems[0]);
					long item = Long.parseLong(elems[1]);

					if (testSet.containsKey(user))
					{
						testSet.get(user).add(item);
					}
					else
					{
						Set<Long> items = new HashSet<Long>();
						items.add(item);
						testSet.put(user, items);
					}
				}
			}
			finally
			{
				bin.close();
			}
			LOG.info("loaded: {} users with a total of {} ratings", testSet.size(), count);

			trainSet = new HashMap<Long, Set<Long>>();
			count = 0;
			bin = new BufferedReader(new FileReader(setTrain));
			try
			{
				String line = null;
				while ((line = bin.readLine()) != null)
				{
					String[] elems = line.split(",");
					long user = Long.parseLong(elems[0]);
					long item = Long.parseLong(elems[1]);
					double rating = Double.parseDouble(elems[2]);
					if (rating >= relevanceThreshold)
					{
						if (trainSet.containsKey(user))
						{
							trainSet.get(user).add(item);
						}
						else
						{
							Set<Long> items = new HashSet<Long>();
							items.add(item);
							trainSet.put(user, items);
						}
					}
				}
			}
			finally
			{
				bin.close();
			}

		}
		private static long tpTotal = 0, fnTotal = 0;

		@Test
		public void testRecommender()
		{

			for (Map.Entry<Long, Set<Long>> entry : testSet.entrySet())
			{
				long user = entry.getKey();

				Set<Long> expected = entry.getValue();
				Set<Long> recommended = getRecommended(recommendations.getRecommendationsFor(CP, COLLECTION, RECOMMENDER,
						user, 100, null));
				Set<Long> trained = trainSet.get(user);

				if (trained != null)
				{
					if (trained.size() < at)
					{
						LOG.info("skipping {} - not enough data", user);
						continue;
					}

					LOG.debug("trained({}) {}", trained.size(), printItems(trained));
					LOG.debug("recommended({}) {}", recommended.size(), printItems(recommended));
					LOG.debug("expected({}): {}", expected.size(), printItems(entry.getValue()));
					double recall = getRecall(recommended, expected);
					LOG.info("recall: user={} - recall={}", user, recall);
				}
			}

			LOG.info("total tp={} fn={}", tpTotal, fnTotal);

			// this is the information you are really looking for:
			LOG.info("recall {} of {}", (tpTotal / (double) (tpTotal + fnTotal)), classname);
			LOG.info("train percentage: {}", train);
		}

		@Test
		public void testBaseLine()
		{

			LOG.info("test baseline");
		}

		@Ignore
		private double getRecall(Set<Long> observation, Set<Long> expectation)
		{
			// this is useless: as you actually want new items as well:
			// precision: true-positive & false-positive: tp/tp+fp

			// this is more useful, all items that i have rated should occur in the results:
			// recall: true-positive & false-negative: tp/tp+fn

			// tp's
			double tp = 0, fn = 0;
			for (Long i : expectation)
			{
				if (observation.contains(i))
				{
					tp++;
				}
				else
				{
					fn++;
				}
			}
			tpTotal += tp;
			fnTotal += fn;
			LOG.debug("{}/{}", tp, (tp + fn));
			return tp / (tp + fn);
		}

		@Ignore
		private Set<Long> getRecommended(Recommendations recommended)
		{
			Set<Long> result = new HashSet<Long>();
			if (recommended != null)
			{
				List<Recommend> list = recommended.getRecommended();
				for (Recommend i : list)
				{
					if (i.getRating() >= 2)
					{
						result.add(i.getItem());
					}
					else
					{
						LOG.debug("filtered rating: {} with {}", i.getItem(), i.getRating());
					}
				}
				LOG.info("got: {} recommendations for user {}", result.size(), recommended.getUser());

			}
			else
			{
				LOG.info("got: NaN recommendations for user NaN", result.size());
			}

			return result;
		}

		@Ignore
		private String printItems(Set<Long> data)
		{
			if (data == null || data.size() == 0)
			{
				return "[]";
			}

			StringBuilder b = new StringBuilder("[");
			for (Long i : data)
			{
				b.append(i).append(',');
			}
			b.deleteCharAt(b.length() - 1);
			b.append(']');
			return b.toString();
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
