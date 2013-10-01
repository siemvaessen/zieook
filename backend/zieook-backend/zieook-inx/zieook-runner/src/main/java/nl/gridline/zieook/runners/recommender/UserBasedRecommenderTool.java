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
package nl.gridline.zieook.runners.recommender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.inx.movielens.hbase.UserRecommendationsStoreMap;
import nl.gridline.zieook.inx.movielens.hbase.UserRecommendationsStoreReduce;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.runners.ZieOokRunnerTool;
import nl.gridline.zieook.runners.cf.RecommenderJobZieOok;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.mahout.math.hadoop.similarity.SimilarityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * ItemBasedRecommenderTool2.java created 1 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class UserBasedRecommenderTool extends ZieOokRunnerTool
{

	private static final Logger LOG = LoggerFactory.getLogger(UserBasedRecommenderTool.class);

	static final String MAX_PREFS_PER_USER_CONSIDERED = "maxPrefsPerUserConsidered";
	static final String ITEMID_INDEX_PATH = "itemIDIndexPath";
	static final String NUM_RECOMMENDATIONS = "numRecommendations";

	private Path inputPath;
	private Path tempDirPath;
	private int numRecommendations;
	private String usersFile;
	private String itemsFile;
	private String filterFile;
	private Boolean booleanData;
	private int maxPrefsPerUser;
	private int maxSimilaritiesPerItem;
	private int maxCooccurrencesPerItem;

	private Path recommendationsPath;

	private String distributedSimilarityClassname;

	/**
	 * @param task
	 */
	public UserBasedRecommenderTool(ZieOokTask task)
	{
		super(task);
	}

	public UserBasedRecommenderTool configure(String input, String outputTable) throws IOException
	{

		String cp = task.getConfig().get(TaskConfig.CP);
		String collection = task.getConfig().get(TaskConfig.COLLECTION);
		String recommender = task.getConfig().get(TaskConfig.RECOMMENDER);

		setBasics();
		setJar(Config.getInstance().get(Config.MAPRED_MOVIELENS));

		Config zieook = Config.getInstance();

		inputPath = new Path(input);

		LOG.info("input: {} output {}", inputPath, outputTable);

		tempDirPath = new Path(zieook.get(Config.ZIEOOK_HDFS_SERVER) + zieook.get(Config.ZIEOOK_HDFS_PATH), cp + "/"
				+ collection + "/" + recommender + "/tmp");

		// cleanup any old data:
		cleanup(tempDirPath);

		numRecommendations = Integer.parseInt(task.getConfig().get(TaskConfig.NUM_RECOMMENDATIONS));
		usersFile = task.getConfig().get(TaskConfig.USERS_FILE);
		itemsFile = task.getConfig().get(TaskConfig.ITEMS_FILE);
		filterFile = task.getConfig().get(TaskConfig.FILTER_FILE);
		booleanData = task.getConfig().getBoolean(TaskConfig.BOOLEAN_DATA, false);
		maxPrefsPerUser = (int) task.getConfig().getLong(TaskConfig.MAX_PREFS_PER_USER, 10);
		maxSimilaritiesPerItem = (int) task.getConfig().getLong(TaskConfig.MAX_SIMILARITRIES_PER_ITEM, 100);
		maxCooccurrencesPerItem = (int) task.getConfig().getLong(TaskConfig.MAX_CO_OCCURRENCES_PER_ITEM, 100);

		recommendationsPath = new Path(tempDirPath, "recommendations");

		String similarityClass = task.getConfig().get(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME);
		distributedSimilarityClassname = SimilarityType.valueOf(similarityClass).getSimilarityImplementationClassName();

		setOutputTable(outputTable);

		return this;
	}

	@Override
	public boolean execute() throws IOException, ClassNotFoundException, InterruptedException
	{
		// --input (path): Directory containing one or more text files with the preference data
		// --output (path): output path where recommender output should go
		// --similarityClassname (classname): Name of distributed similarity class to instantiate or a predefined
		// similarity from SimilarityType
		// --usersFile (path): only compute recommendations for user IDs contained in this file (optional)
		// --itemsFile (path): only include item IDs from this file in the recommendations (optional)
		// --filterFile (path): file containing comma-separated userID,itemID pairs. Used to exclude the item from the
		// recommendations for that user (optional)
		// --numRecommendations (integer): Number of recommendations to compute per user (10)
		// --booleanData (boolean): Treat input data as having no pref values (false)
		// --maxPrefsPerUser (integer): Maximum number of preferences considered per user in final recommendation phase
		// (10)
		// --maxSimilaritiesPerItem (integer): Maximum number of similarities considered per item (100)
		// --maxCooccurrencesPerItem (integer): Maximum number of cooccurrences considered per item (100)

		RecommenderJobZieOok recommenderJob = new RecommenderJobZieOok(task);
		recommenderJob.setConf(getConf());

		List<String> list = new ArrayList<String>();
		list.add("--input");
		list.add(inputPath.toString());

		list.add("--output");
		list.add(recommendationsPath.toString());

		list.add("--tempDir");
		list.add(tempDirPath.toString());

		list.add("--similarityClassname");
		list.add(distributedSimilarityClassname);

		list.add("--numRecommendations");
		list.add(Integer.toString(numRecommendations));

		list.add("--booleanData");
		list.add(booleanData.toString());

		list.add("--maxPrefsPerUser");
		list.add(Integer.toString(maxPrefsPerUser));

		list.add("--maxSimilaritiesPerItem");
		list.add(Integer.toString(maxSimilaritiesPerItem));

		list.add("--maxCooccurrencesPerItem");
		list.add(Integer.toString(maxCooccurrencesPerItem));

		if (usersFile != null)
		{
			list.add("--usersFile");
			list.add(usersFile);
		}
		if (itemsFile != null)
		{
			list.add("--itemsFile");
			list.add(itemsFile);
		}
		if (filterFile != null)
		{
			list.add("--filterFile");
			list.add(filterFile);
		}

		boolean result = 0 == recommenderJob.run(list.toArray(new String[list.size()]));
		if (result)
		{
			Job storage = prepareTableReducer(recommendationsPath, getOutputTable(), SequenceFileInputFormat.class,
					UserRecommendationsStoreMap.class, LongWritable.class, Put.class, UserRecommendationsStoreReduce.class);

			storage.getConfiguration().set(TaskConfig.COLLECTION, task.getConfig().get(TaskConfig.COLLECTION));
			storage.getConfiguration().set(TaskConfig.RECOMMENDER, task.getConfig().get(TaskConfig.RECOMMENDER));

			result = task.setCurrentJob(storage).waitForCompletion(LOG.isDebugEnabled());
		}
		return result;
	}

}
