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

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.mapreduce.TaskConfig;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.math.hadoop.similarity.SimilarityType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * TinyRecommenderTest.java created 10 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class TinyRecommenderTest
{

	private TaskConfig rtaskConfig;
	private Path input;
	private Path output;
	private Path tmp;
	private Configuration hdfsConfig;

	private static final String CP = "gridline3";
	private static final String COLLECTION = "tiny";
	private static final String RECOMMENDER = "tiny-recommender";

	@Before
	public void setUp() throws Exception
	{
		// create TaskConfig

		Config config = Config.getInstance();
		String hdfs = config.get(Config.ZIEOOK_HDFS_SERVER) + config.get(Config.ZIEOOK_HDFS_PATH);
		output = new Path(hdfs, "recommender-tiny/out");
		input = new Path(hdfs, "recommender-tiny/in");
		tmp = new Path(config.get(Config.ZIEOOK_HDFS_SERVER) + config.get(Config.ZIEOOK_HDFS_PATH), CP + "/" + COLLECTION
				+ "/" + RECOMMENDER + "/tmp");

		rtaskConfig = new TaskConfig();
		rtaskConfig.setId(0);
		rtaskConfig.setProperty(TaskConfig.CP, CP); // the content provider
		rtaskConfig.setProperty(TaskConfig.COLLECTION, COLLECTION); // the collection name
		rtaskConfig.setProperty(TaskConfig.RECOMMENDER, RECOMMENDER);
		rtaskConfig.setProperty(TaskConfig.INPUT_PATH, input.toString());
		rtaskConfig.setProperty(TaskConfig.OUTPUT_PATH, output.toString()); // the source path
		rtaskConfig.setProperty(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME,
				SimilarityType.SIMILARITY_TANIMOTO_COEFFICIENT.toString());
		// rtaskConfig.setProperty(TaskConfig.USERS_FILE, null);
		// rtaskConfig.setProperty(TaskConfig.ITEMS_FILE, null);
		// rtaskConfig.setProperty(TaskConfig.FILTER_FILE, null);
		rtaskConfig.setProperty(TaskConfig.NUM_RECOMMENDATIONS, 10);
		rtaskConfig.setProperty(TaskConfig.BOOLEAN_DATA, "false");
		rtaskConfig.setProperty(TaskConfig.MAX_PREFS_PER_USER, 10);
		rtaskConfig.setProperty(TaskConfig.MAX_SIMILARITRIES_PER_ITEM, 100);
		rtaskConfig.setProperty(TaskConfig.MAX_CO_OCCURRENCES_PER_ITEM, 100);
		rtaskConfig.setProperty("skipPrepare", Boolean.TRUE.toString());

		hdfsConfig = new Configuration();
		hdfsConfig.set("fs.default.name", Config.getInstance().get("fs.default.name"));

	}

	@Test
	@Ignore
	public void tinyItemRecommenderTask() throws Exception
	{
		// first clean-up old data:
		FileSystem hdfs = FileSystem.get(hdfsConfig);
		if (hdfs.exists(output))
		{
			hdfs.delete(output, true);
		}
		if (hdfs.exists(input))
		{
			hdfs.delete(input, true);
		}
		if (hdfs.exists(tmp))
		{
			hdfs.delete(tmp, true);
		}

		// put data in local folder:
		hdfs.copyFromLocalFile(new Path("test-data-small/tiny_ratings.dat"), new Path(input, "tiny_ratings.dat"));

		// run the recommender (without the prepare)
		ItemBasedRecommenderTask task = new ItemBasedRecommenderTask();
		task.setConfig(rtaskConfig);
		task.call();
	}

	@Test
	@Ignore
	public void tinyUserRecommenderTask() throws Exception
	{
		// first clean-up old data:
		FileSystem hdfs = FileSystem.get(hdfsConfig);
		if (hdfs.exists(output))
		{
			hdfs.delete(output, true);
		}
		if (hdfs.exists(input))
		{
			hdfs.delete(input, true);
		}
		if (hdfs.exists(tmp))
		{
			hdfs.delete(tmp, true);
		}

		// put data in local folder:
		hdfs.copyFromLocalFile(new Path("test-data-small/tiny_ratings.dat"), new Path(input, "tiny_ratings.dat"));
		// execute itembased recommender:
		UserBasedRecommenderTask task = new UserBasedRecommenderTask();
		task.setConfig(rtaskConfig);
		task.call();

	}
}
