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
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import nl.gridline.zieook.inx.movielens.RowSimilarityZieOok;
import nl.gridline.zieook.inx.movielens.hbase.RecommendationsImportMap;
import nl.gridline.zieook.inx.movielens.hbase.RecommendationsImportReduce;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.cf.taste.hadoop.EntityPrefWritable;
import org.apache.mahout.cf.taste.hadoop.MaybePruneRowsMapper;
import org.apache.mahout.cf.taste.hadoop.TasteHadoopUtils;
import org.apache.mahout.cf.taste.hadoop.ToItemPrefsMapper;
import org.apache.mahout.cf.taste.hadoop.item.ItemIDIndexMapper;
import org.apache.mahout.cf.taste.hadoop.item.ItemIDIndexReducer;
import org.apache.mahout.cf.taste.hadoop.item.RecommenderJob;
import org.apache.mahout.cf.taste.hadoop.item.ToUserVectorReducer;
import org.apache.mahout.cf.taste.hadoop.similarity.item.CountUsersKeyWritable;
import org.apache.mahout.cf.taste.hadoop.similarity.item.CountUsersMapper;
import org.apache.mahout.cf.taste.hadoop.similarity.item.CountUsersReducer;
import org.apache.mahout.cf.taste.hadoop.similarity.item.ItemSimilarityJob;
import org.apache.mahout.cf.taste.hadoop.similarity.item.ToItemVectorsReducer;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.math.VarIntWritable;
import org.apache.mahout.math.VarLongWritable;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.math.hadoop.DistributedRowMatrix;
import org.apache.mahout.math.hadoop.similarity.SimilarityType;
import org.mortbay.log.Log;

/**
 * <p>
 * Distributed precomputation of the item-item-similarities for Itembased Collaborative Filtering
 * </p>
 * <p>
 * Preferences in the input file should look like {@code userID,itemID[,preferencevalue]}
 * </p>
 * <p>
 * Preference value is optional to accommodate applications that have no notion of a preference value (that is, the user
 * simply expresses a preference for an item, but no degree of preference).
 * </p>
 * <p>
 * The preference value is assumed to be parseable as a {@code double}. The user IDs and item IDs are parsed as
 * {@code long}s.
 * </p>
 * <p>
 * Command line arguments specific to this class are:
 * </p>
 * <ol>
 * <li>-Dmapred.input.dir=(path): Directory containing one or more text files with the preference data</li>
 * <li>-Dmapred.output.dir=(path): output path where similarity data should be written</li>
 * <li>--similarityClassname (classname): Name of distributed similarity class to instantiate or a predefined similarity
 * from {@link SimilarityType}</li>
 * <li>--maxSimilaritiesPerItem (integer): Maximum number of similarities considered per item (100)</li>
 * <li>--maxCooccurrencesPerItem (integer): Maximum number of cooccurrences considered per item (100)</li>
 * <li>--booleanData (boolean): Treat input data as having no pref values (false)</li>
 * </ol>
 * <p>
 * General command line options are documented in {@link AbstractJob}.
 * </p>
 * <p>
 * Note that because of how Hadoop parses arguments, all "-D" arguments must appear before all other arguments.
 * </p>
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * ItemSimilarityJobZieook.java created 13 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class ItemSimilarityJobZieook extends AbstractJob
{

	static final String ITEM_ID_INDEX_PATH_STR = ItemSimilarityJob.class.getName() + ".itemIDIndexPathStr";
	static final String MAX_SIMILARITIES_PER_ITEM = ItemSimilarityJob.class.getName() + ".maxSimilarItemsPerItem";

	private static final int DEFAULT_MAX_SIMILAR_ITEMS_PER_ITEM = 100;
	private static final int DEFAULT_MAX_COOCCURRENCES_PER_ITEM = 100;
	private static final int DEFAULT_MIN_PREFS_PER_USER = 1;

	private final ZieOokTask task;

	public ItemSimilarityJobZieook(ZieOokTask task)
	{
		this.task = task;
	}

	@Override
	public int run(String[] args) throws IOException, InterruptedException, ClassNotFoundException
	{

		addInputOption();

		// addOutputOption(); // no output path, we use a table!
		addOption("outputtable", "ot", "Output table name");

		addOption("similarityClassname", "s", "Name of distributed similarity class to instantiate, alternatively use "
				+ "one of the predefined similarities (" + SimilarityType.listEnumNames() + ')');
		addOption("maxSimilaritiesPerItem", "m", "try to cap the number of similar items per item to this number "
				+ "(default: " + DEFAULT_MAX_SIMILAR_ITEMS_PER_ITEM + ')',
				String.valueOf(DEFAULT_MAX_SIMILAR_ITEMS_PER_ITEM));
		addOption("maxCooccurrencesPerItem", "mo", "try to cap the number of cooccurrences per item to this number "
				+ "(default: " + DEFAULT_MAX_COOCCURRENCES_PER_ITEM + ')',
				String.valueOf(DEFAULT_MAX_COOCCURRENCES_PER_ITEM));
		addOption("minPrefsPerUser", "mp", "ignore users with less preferences than this " + "(default: "
				+ DEFAULT_MIN_PREFS_PER_USER + ')', String.valueOf(DEFAULT_MIN_PREFS_PER_USER));
		addOption("booleanData", "b", "Treat input as without pref values", Boolean.FALSE.toString());

		Map<String, String> parsedArgs = parseArguments(args);
		if (parsedArgs == null)
		{
			return -1;
		}

		String similarityClassName = parsedArgs.get("--similarityClassname");
		int maxSimilarItemsPerItem = Integer.parseInt(parsedArgs.get("--maxSimilaritiesPerItem"));
		int maxCooccurrencesPerItem = Integer.parseInt(parsedArgs.get("--maxCooccurrencesPerItem"));
		int minPrefsPerUser = Integer.parseInt(parsedArgs.get("--minPrefsPerUser"));
		boolean booleanData = Boolean.valueOf(parsedArgs.get("--booleanData"));

		Path inputPath = getInputPath();
		// Path outputPath = getOutputPath();
		String outputTable = parsedArgs.get("--outputtable");
		Path tempDirPath = new Path(parsedArgs.get("--tempDir"));

		Path itemIDIndexPath = new Path(tempDirPath, "itemIDIndex");
		Path countUsersPath = new Path(tempDirPath, "countUsers");
		Path userVectorPath = new Path(tempDirPath, "userVectors");
		Path itemUserMatrixPath = new Path(tempDirPath, "itemUserMatrix");
		Path similarityMatrixPath = new Path(tempDirPath, "similarityMatrix");

		AtomicInteger currentPhase = new AtomicInteger();

		if (shouldRunNextPhase(parsedArgs, currentPhase))
		{
			Job itemIDIndex = prepareJob(inputPath, itemIDIndexPath, TextInputFormat.class, ItemIDIndexMapper.class,
					VarIntWritable.class, VarLongWritable.class, ItemIDIndexReducer.class, VarIntWritable.class,
					VarLongWritable.class, SequenceFileOutputFormat.class);
			itemIDIndex.setCombinerClass(ItemIDIndexReducer.class);
			task.setCurrentJob(itemIDIndex).waitForCompletion(true);
		}

		if (shouldRunNextPhase(parsedArgs, currentPhase))
		{
			Job toUserVector = prepareJob(inputPath, userVectorPath, TextInputFormat.class, ToItemPrefsMapper.class,
					VarLongWritable.class, booleanData ? VarLongWritable.class : EntityPrefWritable.class,
					ToUserVectorReducer.class, VarLongWritable.class, VectorWritable.class, SequenceFileOutputFormat.class);
			toUserVector.getConfiguration().setBoolean(RecommenderJob.BOOLEAN_DATA, booleanData);
			toUserVector.getConfiguration().setInt(ToUserVectorReducer.MIN_PREFERENCES_PER_USER, minPrefsPerUser);
			task.setCurrentJob(toUserVector).waitForCompletion(true);
		}

		if (shouldRunNextPhase(parsedArgs, currentPhase))
		{
			Job countUsers = prepareJob(userVectorPath, countUsersPath, SequenceFileInputFormat.class,
					CountUsersMapper.class, CountUsersKeyWritable.class, VarLongWritable.class, CountUsersReducer.class,
					VarIntWritable.class, NullWritable.class, TextOutputFormat.class);
			countUsers.setPartitionerClass(CountUsersKeyWritable.CountUsersPartitioner.class);
			countUsers.setGroupingComparatorClass(CountUsersKeyWritable.CountUsersGroupComparator.class);
			task.setCurrentJob(countUsers).waitForCompletion(true);
		}

		if (shouldRunNextPhase(parsedArgs, currentPhase))
		{
			Job maybePruneAndTransponse = prepareJob(userVectorPath, itemUserMatrixPath, SequenceFileInputFormat.class,
					MaybePruneRowsMapper.class, IntWritable.class, DistributedRowMatrix.MatrixEntryWritable.class,
					ToItemVectorsReducer.class, IntWritable.class, VectorWritable.class, SequenceFileOutputFormat.class);
			maybePruneAndTransponse.getConfiguration().setInt(MaybePruneRowsMapper.MAX_COOCCURRENCES,
					maxCooccurrencesPerItem);
			task.setCurrentJob(maybePruneAndTransponse).waitForCompletion(true);
		}

		int numberOfUsers = TasteHadoopUtils.readIntFromFile(getConf(), countUsersPath);

		/*
		 * Once DistributedRowMatrix uses the hadoop 0.20 API, we should refactor this call to something like
		 * new DistributedRowMatrix(...).rowSimilarity(...)
		 */
		try
		{
			ToolRunner.run(
					getConf(),
					new RowSimilarityZieOok(),
					new String[]{"-Dmapred.input.dir=" + itemUserMatrixPath, "-Dmapred.output.dir=" + similarityMatrixPath,
							"--numberOfColumns", String.valueOf(numberOfUsers), "--similarityClassname", similarityClassName,
							"--maxSimilaritiesPerRow", String.valueOf(maxSimilarItemsPerItem + 1), "--tempDir",
							tempDirPath.toString()});
		}
		catch (Exception e)
		{
			throw new IllegalStateException("item-item-similarity computation failed", e);
		}

		// This step writes the data to a file, we don't want that, it should be written in HBase directly:
		if (shouldRunNextPhase(parsedArgs, currentPhase))
		{
			Job mostSimilarItems = prepareMostSimilarItems(similarityMatrixPath, outputTable);

			// Configuration mostSimilarItemsConf = mostSimilarItems.getConfiguration();

			// mostSimilarItemsConf.set(ITEM_ID_INDEX_PATH_STR, itemIDIndexPath.toString());
			// mostSimilarItemsConf.setInt(MAX_SIMILARITIES_PER_ITEM, maxSimilarItemsPerItem);

			// mostSimilarItems.waitForCompletion(true);

			task.setCurrentJob(mostSimilarItems).waitForCompletion(Log.isDebugEnabled());

			// Job mostSimilarItems = prepareJob(similarityMatrixPath, outputPath, SequenceFileInputFormat.class,
			// MostSimilarItemPairsMapper.class, EntityEntityWritable.class, DoubleWritable.class,
			// MostSimilarItemPairsReducer.class, EntityEntityWritable.class, DoubleWritable.class,
			// TextOutputFormat.class);
			// Configuration mostSimilarItemsConf = mostSimilarItems.getConfiguration();
			// mostSimilarItemsConf.set(ITEM_ID_INDEX_PATH_STR, itemIDIndexPath.toString());
			// mostSimilarItemsConf.setInt(MAX_SIMILARITIES_PER_ITEM, maxSimilarItemsPerItem);
			// mostSimilarItems.setCombinerClass(MostSimilarItemPairsReducer.class);
			// mostSimilarItems.waitForCompletion(true);
		}

		return 0;
	}

	protected Job prepareMostSimilarItems(Path inputFile, String outputTable) throws IOException
	{

		Job job = new Job(new Configuration(getConf()));
		job.setJobName(getCustomJobName(job, RecommendationsImportMap.class, RecommendationsImportReduce.class));

		job.getConfiguration().set(TaskConfig.COLLECTION, task.getConfig().get(TaskConfig.COLLECTION));
		job.getConfiguration().set(TaskConfig.RECOMMENDER, task.getConfig().get(TaskConfig.RECOMMENDER));

		job.setMapperClass(RecommendationsImportMap.class);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Put.class);
		job.setNumReduceTasks(4);

		TableMapReduceUtil.initTableReducerJob(outputTable, RecommendationsImportReduce.class, job);

		// job.setCombinerClass(RecommendationsImportReduce.class);

		FileInputFormat.addInputPath(job, inputFile);
		return job;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Job prepareJob(Path inputPath, Path outputPath, Class<? extends InputFormat> inputFormat,
			Class<? extends Mapper> mapper, Class<? extends Writable> mapperKey, Class<? extends Writable> mapperValue,
			Class<? extends Reducer> reducer, Class<? extends Writable> reducerKey,
			Class<? extends Writable> reducerValue, Class<? extends OutputFormat> outputFormat) throws IOException
	{

		Job job = new Job(new Configuration(getConf()));
		Configuration jobConf = job.getConfiguration();

		// This is not working: - we set the jar directly:
		// if (reducer.equals(Reducer.class))
		// {
		// if (mapper.equals(Mapper.class))
		// {
		// throw new IllegalStateException("Can't figure out the user class jar file from mapper/reducer");
		// }
		// job.setJarByClass(mapper);
		// }
		// else
		// {
		// job.setJarByClass(reducer);
		// }

		job.setInputFormatClass(inputFormat);
		jobConf.set("mapred.input.dir", inputPath.toString());

		job.setMapperClass(mapper);
		job.setMapOutputKeyClass(mapperKey);
		job.setMapOutputValueClass(mapperValue);

		jobConf.setBoolean("mapred.compress.map.output", true);

		job.setReducerClass(reducer);
		job.setOutputKeyClass(reducerKey);
		job.setOutputValueClass(reducerValue);

		job.setJobName(getCustomJobName(job, mapper, reducer));

		job.setOutputFormatClass(outputFormat);
		jobConf.set("mapred.output.dir", outputPath.toString());

		return job;
	}

	@SuppressWarnings("rawtypes")
	private String getCustomJobName(JobContext job, Class<? extends Mapper> mapper, Class<? extends Reducer> reducer)
	{
		StringBuilder name = new StringBuilder(100);
		String customJobName = job.getJobName();
		if (customJobName == null || customJobName.trim().length() == 0)
		{
			name.append(getClass().getSimpleName());
		}
		else
		{
			name.append(customJobName);
		}
		name.append('-').append(mapper.getSimpleName());
		name.append('-').append(reducer.getSimpleName());
		return name.toString();
	}

}
