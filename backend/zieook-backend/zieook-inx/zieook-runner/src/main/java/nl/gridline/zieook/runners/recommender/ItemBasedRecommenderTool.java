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
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.runners.ZieOokRunnerTool;
import nl.gridline.zieook.runners.cf.ItemSimilarityJobZieook;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.hadoop.fs.Path;
import org.apache.mahout.cf.taste.hadoop.similarity.item.ItemSimilarityJob;
import org.apache.mahout.math.hadoop.similarity.SimilarityType;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * ItemBasedRecommenderTool.java created 3 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class ItemBasedRecommenderTool extends ZieOokRunnerTool
{

	// private static final Logger LOG = LoggerFactory.getLogger(ItemBasedRecommenderTool.class);

	private Path inputPath;
	private Path tempDirPath;
	private String outputTable;

	private int maxSimilarItemsPerItem;
	private int maxCooccurrencesPerItem;
	private boolean booleanData;

	private String collection;

	private String recommender;

	private String distributedSimilarityClassname;

	static final String ITEM_ID_INDEX_PATH_STR = ItemSimilarityJob.class.getName() + ".itemIDIndexPathStr";

	/**
	 * @param task
	 */
	public ItemBasedRecommenderTool(ZieOokTask task)
	{
		super(task);

	}

	public ItemBasedRecommenderTool configure(String input, String outputTable) throws IOException
	{
		Config zieook = Config.getInstance();
		String cp = task.getConfig().get(TaskConfig.CP);
		collection = task.getConfig().get(TaskConfig.COLLECTION);
		recommender = task.getConfig().get(TaskConfig.RECOMMENDER);

		if (collection == null)
		{
			throw new IOException("collection not set, please set <" + TaskConfig.COLLECTION + "> in task configuration");
		}
		if (recommender == null)
		{
			throw new IOException("collection not set, please set <" + TaskConfig.COLLECTION + "> in task configuration");
		}

		setBasics();
		setJar(Config.getInstance().get(Config.MAPRED_MOVIELENS));

		String similarityClass = task.getConfig().get(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME);
		distributedSimilarityClassname = SimilarityType.valueOf(similarityClass).getSimilarityImplementationClassName();

		maxSimilarItemsPerItem = (int) task.getConfig().getLong(TaskConfig.MAX_SIMILARITRIES_PER_ITEM, 100);
		maxCooccurrencesPerItem = (int) task.getConfig().getLong(TaskConfig.MAX_CO_OCCURRENCES_PER_ITEM, 100);
		booleanData = task.getConfig().getBoolean(TaskConfig.BOOLEAN_DATA, true);

		inputPath = new Path(input);

		// outputPath = new Path(output);

		setOutputTable(outputTable);

		tempDirPath = new Path(zieook.get(Config.ZIEOOK_HDFS_SERVER) + zieook.get(Config.ZIEOOK_HDFS_PATH), cp + "/"
				+ collection + "/" + recommender + "/tmp");

		// remove old data:
		cleanup(tempDirPath);

		this.outputTable = outputTable;

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.runners.ZieOokRunnerTool#execute()
	 */
	@Override
	public boolean execute() throws IOException, InterruptedException, ClassNotFoundException
	{
		//
		// -Dmapred.input.dir=(path): Directory containing one or more text files with the preference data</li>
		// -Dmapred.output.dir=(path): output path where similarity data should be written</li>
		// --similarityClassname (classname): Name of distributed similarity class to instantiate or a predefined
		// similarity
		// from {@link SimilarityType}</li>
		// --maxSimilaritiesPerItem (integer): Maximum number of similarities considered per item (100)</li>
		// --maxCooccurrencesPerItem (integer): Maximum number of cooccurrences considered per item (100)</li>
		// --booleanData (boolean): Treat input data as having no pref values (false)</li>
		//

		// Path outputPath = new Path(tempDirPath, "output");

		ItemSimilarityJobZieook itemSimilarity = new ItemSimilarityJobZieook(task);
		itemSimilarity.setConf(getConf());

		List<String> args = new ArrayList<String>();
		args.add("--input");
		args.add(inputPath.toString());

		// args.add("--output");
		// args.add(outputPath.toString());
		args.add("--outputtable");
		args.add(outputTable);

		args.add("--tempDir");
		args.add(tempDirPath.toString());

		args.add("--similarityClassname");
		args.add(distributedSimilarityClassname);

		args.add("--maxSimilaritiesPerItem");
		args.add(Integer.toString(maxSimilarItemsPerItem));

		args.add("--maxCooccurrencesPerItem");
		args.add(Integer.toString(maxCooccurrencesPerItem));

		args.add("--booleanData");
		args.add(Boolean.toString(booleanData));

		boolean result = 0 == itemSimilarity.run(args.toArray(new String[args.size()]));

		return result;
	}

}
