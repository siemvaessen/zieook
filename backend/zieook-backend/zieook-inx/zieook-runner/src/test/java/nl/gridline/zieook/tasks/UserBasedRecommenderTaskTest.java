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
import nl.gridline.zieook.mapreduce.TaskConfig;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.mahout.math.hadoop.similarity.SimilarityType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * UserBasedRecommenderTask.java created 10 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class UserBasedRecommenderTaskTest
{

	private TaskConfig rtaskConfig;

	private Path input;
	private Path output;

	private Configuration hdfsConfig;

	private static final String CP = "gridline-movielens";
	private static final String COLLECTION = "movielens";
	private static final String RECOMMENDER = "ub-movielens-tanimo";

	@Before
	public void init() throws MasterNotRunningException, ZooKeeperConnectionException
	{
		// create TaskConfig

		// - create config table
		// - load data in hbase
		Config config = Config.getInstance();

		String hdfs = config.get(Config.ZIEOOK_HDFS_SERVER) + config.get(Config.ZIEOOK_HDFS_PATH);
		output = new Path(hdfs, "recommender-userbased/out");
		input = new Path(hdfs, "recommender-userbased/in");
		// Path tmp = new Path(config.get(Config.ZIEOOK_HDFS_SERVER) + config.get(Config.ZIEOOK_HDFS_PATH), CP + "/"
		// + COLLECTION + "/" + RECOMMENDER + "/tmp");

		rtaskConfig = new TaskConfig();
		rtaskConfig.setId(0);
		rtaskConfig.setProperty(TaskConfig.CP, CP); // the content provider
		rtaskConfig.setProperty(TaskConfig.COLLECTION, COLLECTION); // the collection name
		rtaskConfig.setProperty(TaskConfig.RECOMMENDER, RECOMMENDER);
		rtaskConfig.setProperty(TaskConfig.INPUT_PATH, input.toString());
		rtaskConfig.setProperty(TaskConfig.OUTPUT_PATH, output.toString()); // the source path
		rtaskConfig.setProperty(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME,
				SimilarityType.SIMILARITY_TANIMOTO_COEFFICIENT.toString());

		rtaskConfig.setProperty(TaskConfig.NUM_RECOMMENDATIONS, 100);
		rtaskConfig.setProperty(TaskConfig.BOOLEAN_DATA, "false");
		rtaskConfig.setProperty(TaskConfig.MAX_PREFS_PER_USER, 10);
		rtaskConfig.setProperty(TaskConfig.MAX_SIMILARITRIES_PER_ITEM, 100);
		rtaskConfig.setProperty(TaskConfig.MAX_CO_OCCURRENCES_PER_ITEM, 100);

		hdfsConfig = new Configuration();
		hdfsConfig.set("fs.default.name", Config.getInstance().get("fs.default.name"));

		// HBaseManager manager = new HBaseManager(Config.getInstance().getZooKeeperHost());
		// new HBaseCollectionTable(manager).create(CP);
		// new HBaseRecommendationTable(manager).create(CP);
		// new HBaseUserTable(manager).create(CP);

	}

	@Test
	@Ignore
	public void executeUserRecommender() throws IOException
	{

		// execute itembased recommender:
		UserBasedRecommenderTask task = new UserBasedRecommenderTask();
		task.setConfig(rtaskConfig);

		try
		{
			task.call();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
}
