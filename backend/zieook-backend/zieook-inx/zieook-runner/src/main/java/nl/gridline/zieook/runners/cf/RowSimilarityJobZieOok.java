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
package nl.gridline.zieook.runners.cf;

import java.io.IOException;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.inx.movielens.RowSimilarityZieOok;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.runners.ZieOokRunnerTool;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.hadoop.fs.Path;
import org.apache.mahout.math.hadoop.similarity.SimilarityType;

/**
 * Because mahout's RecommenderJob does not work well, mainly because setJarByClass does not do what it should.
 * <p />
 * Project zieook-runner<br />
 * RowSimilarityJob.java created 2 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class RowSimilarityJobZieOok extends ZieOokRunnerTool
{
	// private static final Logger LOG = LoggerFactory.getLogger(RowSimilarityJobZieOok.class);

	private static final int DEFAULT_MAX_SIMILARITIES_PER_ROW = 100;
	private Path inputPath;
	private Path outputPath;
	private Path tempDirPath;
	// private Path weightsPath;
	// private Path pairwiseSimilarityPath;
	private int numberOfColumns;
	private String distributedSimilarityClassname;
	private int maxSimilaritiesPerRow;

	// private TaskConfig config;

	/**
	 * @param task
	 */
	public RowSimilarityJobZieOok(ZieOokTask task)
	{
		super(task);
	}

	public RowSimilarityJobZieOok configure(String input, String output) throws IOException
	{
		// this.config = config;
		String cp = task.getConfig().get(TaskConfig.CP);
		String collection = task.getConfig().get(TaskConfig.COLLECTION);
		String recommender = task.getConfig().get(TaskConfig.RECOMMENDER);

		setBasics();
		setJar(Config.getInstance().get(Config.MAPRED_MOVIELENS));

		Config zieook = Config.getInstance();

		inputPath = new Path(input);
		outputPath = new Path(output);
		tempDirPath = new Path(zieook.get(Config.ZIEOOK_HDFS_SERVER) + zieook.get(Config.ZIEOOK_HDFS_PATH), cp + "/"
				+ collection + "/" + recommender + "/tmp");

		// weightsPath = new Path(tempDirPath, "weights");
		// pairwiseSimilarityPath = new Path(tempDirPath, "pairwiseSimilarity");
		numberOfColumns = (int) task.getConfig().getLong(TaskConfig.NUMBER_OF_COLUMNS, -1);
		String similarityClass = task.getConfig().get(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME);
		distributedSimilarityClassname = SimilarityType.valueOf(similarityClass).getSimilarityImplementationClassName();
		maxSimilaritiesPerRow = (int) task.getConfig().getLong(TaskConfig.MAX_SIMILARITIES_PER_ROW,
				DEFAULT_MAX_SIMILARITIES_PER_ROW);

		return this;

	}

	@Override
	public boolean execute() throws IOException, ClassNotFoundException, InterruptedException
	{
		RowSimilarityZieOok task = new RowSimilarityZieOok();
		task.setConf(getConf());

		// This magic, set's the arguments of mahout's row similarity job:
		int result = task.run(new String[]{ // array of mahout arguments
				"--input", inputPath.toString(), // input path
						"--output", outputPath.toString(), // output path
						"--tempDir", tempDirPath.toString(), // use our own tmp dir path.
						"--numberOfColumns", Integer.toString(numberOfColumns), // number of columns
						"--similarityClassname", distributedSimilarityClassname, // the distance class to use
						"--maxSimilaritiesPerRow", Integer.toString(maxSimilaritiesPerRow) // the max nr of similarity

				});
		return result == 0;
	}

}
