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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.gridline.zieook.inx.movielens.AggregateAndRecommendReducer;
import nl.gridline.zieook.inx.movielens.RowSimilarityZieOok;
import nl.gridline.zieook.inx.movielens.UserVectorSplitterMapper;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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
import org.apache.mahout.cf.taste.hadoop.RecommendedItemsWritable;
import org.apache.mahout.cf.taste.hadoop.TasteHadoopUtils;
import org.apache.mahout.cf.taste.hadoop.ToItemPrefsMapper;
import org.apache.mahout.cf.taste.hadoop.item.ItemFilterAsVectorAndPrefsReducer;
import org.apache.mahout.cf.taste.hadoop.item.ItemFilterMapper;
import org.apache.mahout.cf.taste.hadoop.item.ItemIDIndexMapper;
import org.apache.mahout.cf.taste.hadoop.item.ItemIDIndexReducer;
import org.apache.mahout.cf.taste.hadoop.item.PartialMultiplyMapper;
import org.apache.mahout.cf.taste.hadoop.item.PrefAndSimilarityColumnWritable;
import org.apache.mahout.cf.taste.hadoop.item.RecommenderJob;
import org.apache.mahout.cf.taste.hadoop.item.SimilarityMatrixRowWrapperMapper;
import org.apache.mahout.cf.taste.hadoop.item.ToUserVectorReducer;
import org.apache.mahout.cf.taste.hadoop.item.ToVectorAndPrefReducer;
import org.apache.mahout.cf.taste.hadoop.item.VectorAndPrefsWritable;
import org.apache.mahout.cf.taste.hadoop.item.VectorOrPrefWritable;
import org.apache.mahout.cf.taste.hadoop.similarity.item.CountUsersKeyWritable;
import org.apache.mahout.cf.taste.hadoop.similarity.item.CountUsersMapper;
import org.apache.mahout.cf.taste.hadoop.similarity.item.CountUsersReducer;
import org.apache.mahout.cf.taste.hadoop.similarity.item.ToItemVectorsReducer;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.math.VarIntWritable;
import org.apache.mahout.math.VarLongWritable;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.math.hadoop.DistributedRowMatrix;
import org.apache.mahout.math.hadoop.similarity.SimilarityType;

/**
 * <p>
 * Runs a completely distributed recommender job as a series of mapreduces.
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
 * <li>-Dmapred.output.dir=(path): output path where recommender output should go</li>
 * <li>--similarityClassname (classname): Name of distributed similarity class to instantiate or a predefined similarity
 * from {@link SimilarityType}</li>
 * <li>--usersFile (path): only compute recommendations for user IDs contained in this file (optional)</li>
 * <li>--itemsFile (path): only include item IDs from this file in the recommendations (optional)</li>
 * <li>--filterFile (path): file containing comma-separated userID,itemID pairs. Used to exclude the item from the
 * recommendations for that user (optional)</li>
 * <li>--numRecommendations (integer): Number of recommendations to compute per user (10)</li>
 * <li>--booleanData (boolean): Treat input data as having no pref values (false)</li>
 * <li>--maxPrefsPerUser (integer): Maximum number of preferences considered per user in final recommendation phase (10)
 * </li>
 * <li>--maxSimilaritiesPerItem (integer): Maximum number of similarities considered per item (100)</li>
 * <li>--maxCooccurrencesPerItem (integer): Maximum number of cooccurrences considered per item (100)</li>
 * </ol>
 * <p>
 * General command line options are documented in {@link AbstractJob}.
 * </p>
 * <p>
 * Note that because of how Hadoop parses arguments, all "-D" arguments must appear before all other arguments.
 * </p>
 * Project zieook-runner<br />
 * RecommenderJobZieOok.java created 13 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class RecommenderJobZieOok extends AbstractJob
{
	public static final String BOOLEAN_DATA = "booleanData";

	private static final int DEFAULT_MAX_SIMILARITIES_PER_ITEM = 100;
	private static final int DEFAULT_MAX_COOCCURRENCES_PER_ITEM = 100;
	private static final int DEFAULT_MIN_PREFS_PER_USER = 1;

	private final ZieOokTask task;

	public RecommenderJobZieOok(ZieOokTask task)
	{
		this.task = task;
	}

	@Override
	public int run(String[] args) throws IOException, ClassNotFoundException, InterruptedException
	{
		addInputOption();
		addOutputOption();
		addOption("numRecommendations", "n", "Number of recommendations per user",
				String.valueOf(AggregateAndRecommendReducer.DEFAULT_NUM_RECOMMENDATIONS));
		addOption("usersFile", "u", "File of users to recommend for", null);
		addOption("itemsFile", "i", "File of items to recommend for", null);
		addOption("filterFile", "f",
				"File containing comma-separated userID,itemID pairs. Used to exclude the item from "
						+ "the recommendations for that user (optional)", null);
		addOption("booleanData", "b", "Treat input as without pref values", Boolean.FALSE.toString());
		addOption("maxPrefsPerUser", "mp",
				"Maximum number of preferences considered per user in final recommendation phase",
				String.valueOf(UserVectorSplitterMapper.DEFAULT_MAX_PREFS_PER_USER_CONSIDERED));
		addOption("minPrefsPerUser", "mp", "ignore users with less preferences than this in the similarity computation "
				+ "(default: " + DEFAULT_MIN_PREFS_PER_USER + ')', String.valueOf(DEFAULT_MIN_PREFS_PER_USER));
		addOption("maxSimilaritiesPerItem", "m", "Maximum number of similarities considered per item ",
				String.valueOf(DEFAULT_MAX_SIMILARITIES_PER_ITEM));
		addOption("maxCooccurrencesPerItem", "mo", "try to cap the number of cooccurrences per item to this "
				+ "number (default: " + DEFAULT_MAX_COOCCURRENCES_PER_ITEM + ')',
				String.valueOf(DEFAULT_MAX_COOCCURRENCES_PER_ITEM));
		addOption("similarityClassname", "s", "Name of distributed similarity class to instantiate, alternatively use "
				+ "one of the predefined similarities (" + SimilarityType.listEnumNames() + ')',
				String.valueOf(SimilarityType.SIMILARITY_COOCCURRENCE));

		Map<String, String> parsedArgs = parseArguments(args);
		if (parsedArgs == null)
		{
			return -1;
		}

		Path inputPath = getInputPath();
		Path outputPath = getOutputPath();
		Path tempDirPath = new Path(parsedArgs.get("--tempDir"));
		int numRecommendations = Integer.parseInt(parsedArgs.get("--numRecommendations"));
		String usersFile = parsedArgs.get("--usersFile");
		String itemsFile = parsedArgs.get("--itemsFile");
		String filterFile = parsedArgs.get("--filterFile");
		boolean booleanData = Boolean.valueOf(parsedArgs.get("--booleanData"));
		int maxPrefsPerUser = Integer.parseInt(parsedArgs.get("--maxPrefsPerUser"));
		int minPrefsPerUser = Integer.parseInt(parsedArgs.get("--minPrefsPerUser"));
		int maxSimilaritiesPerItem = Integer.parseInt(parsedArgs.get("--maxSimilaritiesPerItem"));
		int maxCooccurrencesPerItem = Integer.parseInt(parsedArgs.get("--maxCooccurrencesPerItem"));
		String similarityClassname = parsedArgs.get("--similarityClassname");

		Path userVectorPath = new Path(tempDirPath, "userVectors");
		Path itemIDIndexPath = new Path(tempDirPath, "itemIDIndex");
		Path countUsersPath = new Path(tempDirPath, "countUsers");
		Path itemUserMatrixPath = new Path(tempDirPath, "itemUserMatrix");
		Path similarityMatrixPath = new Path(tempDirPath, "similarityMatrix");
		Path prePartialMultiplyPath1 = new Path(tempDirPath, "prePartialMultiply1");
		Path prePartialMultiplyPath2 = new Path(tempDirPath, "prePartialMultiply2");
		Path explicitFilterPath = new Path(tempDirPath, "explicitFilterPath");
		Path partialMultiplyPath = new Path(tempDirPath, "partialMultiply");

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
			toUserVector.getConfiguration().setBoolean(BOOLEAN_DATA, booleanData);
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

		if (shouldRunNextPhase(parsedArgs, currentPhase))
		{
			/*
			 * Once DistributedRowMatrix uses the hadoop 0.20 API, we should refactor this call to something like
			 * new DistributedRowMatrix(...).rowSimilarity(...)
			 */
			try
			{
				ToolRunner.run(getConf(), new RowSimilarityZieOok(), new String[]{ //
						"--input", itemUserMatrixPath.toString(), //
								"--output", similarityMatrixPath.toString(),//
								"--numberOfColumns", String.valueOf(numberOfUsers), //
								"--similarityClassname", similarityClassname,//
								"--maxSimilaritiesPerRow", String.valueOf(maxSimilaritiesPerItem + 1),//
								"--tempDir", tempDirPath.toString()});
			}
			catch (Exception e)
			{
				throw new IllegalStateException("item-item-similarity computation failed", e);
			}
		}

		if (shouldRunNextPhase(parsedArgs, currentPhase))
		{
			Job prePartialMultiply1 = prepareJob(similarityMatrixPath, prePartialMultiplyPath1,
					SequenceFileInputFormat.class, SimilarityMatrixRowWrapperMapper.class, VarIntWritable.class,
					VectorOrPrefWritable.class, Reducer.class, VarIntWritable.class, VectorOrPrefWritable.class,
					SequenceFileOutputFormat.class);
			task.setCurrentJob(prePartialMultiply1).waitForCompletion(true);

			Job prePartialMultiply2 = prepareJob(userVectorPath, prePartialMultiplyPath2, SequenceFileInputFormat.class,
					UserVectorSplitterMapper.class, VarIntWritable.class, VectorOrPrefWritable.class, Reducer.class,
					VarIntWritable.class, VectorOrPrefWritable.class, SequenceFileOutputFormat.class);
			if (usersFile != null)
			{
				prePartialMultiply2.getConfiguration().set(UserVectorSplitterMapper.USERS_FILE, usersFile);
			}
			prePartialMultiply2.getConfiguration().setInt(UserVectorSplitterMapper.MAX_PREFS_PER_USER_CONSIDERED,
					maxPrefsPerUser);
			task.setCurrentJob(prePartialMultiply2).waitForCompletion(true);

			Job partialMultiply = prepareJob(new Path(prePartialMultiplyPath1 + "," + prePartialMultiplyPath2),
					partialMultiplyPath, SequenceFileInputFormat.class, Mapper.class, VarIntWritable.class,
					VectorOrPrefWritable.class, ToVectorAndPrefReducer.class, VarIntWritable.class,
					VectorAndPrefsWritable.class, SequenceFileOutputFormat.class);

			/* necessary to make this job (having a combined input path) work on Amazon S3 */
			Configuration partialMultiplyConf = partialMultiply.getConfiguration();
			FileSystem fs = FileSystem.get(tempDirPath.toUri(), partialMultiplyConf);
			prePartialMultiplyPath1 = prePartialMultiplyPath1.makeQualified(fs);
			prePartialMultiplyPath2 = prePartialMultiplyPath2.makeQualified(fs);
			FileInputFormat.setInputPaths(partialMultiply, prePartialMultiplyPath1, prePartialMultiplyPath2);
			task.setCurrentJob(partialMultiply).waitForCompletion(true);
		}

		if (shouldRunNextPhase(parsedArgs, currentPhase))
		{

			/* convert the user/item pairs to filter if a filterfile has been specified */
			if (filterFile != null)
			{
				Job itemFiltering = prepareJob(new Path(filterFile), explicitFilterPath, TextInputFormat.class,
						ItemFilterMapper.class, VarLongWritable.class, VarLongWritable.class,
						ItemFilterAsVectorAndPrefsReducer.class, VarIntWritable.class, VectorAndPrefsWritable.class,
						SequenceFileOutputFormat.class);
				task.setCurrentJob(itemFiltering).waitForCompletion(true);
			}

			String aggregateAndRecommendInput = partialMultiplyPath.toString();
			if (filterFile != null)
			{
				aggregateAndRecommendInput += "," + explicitFilterPath;
			}

			Job aggregateAndRecommend = prepareJob(new Path(aggregateAndRecommendInput), outputPath,
					SequenceFileInputFormat.class, PartialMultiplyMapper.class, VarLongWritable.class,
					PrefAndSimilarityColumnWritable.class, AggregateAndRecommendReducer.class, VarLongWritable.class,
					RecommendedItemsWritable.class, SequenceFileOutputFormat.class);
			Configuration aggregateAndRecommendConf = aggregateAndRecommend.getConfiguration();
			if (itemsFile != null)
			{
				aggregateAndRecommendConf.set(AggregateAndRecommendReducer.ITEMS_FILE, itemsFile);
			}

			if (filterFile != null)
			{
				/* necessary to make this job (having a combined input path) work on Amazon S3 */
				FileSystem fs = FileSystem.get(tempDirPath.toUri(), aggregateAndRecommendConf);
				partialMultiplyPath = partialMultiplyPath.makeQualified(fs);
				explicitFilterPath = explicitFilterPath.makeQualified(fs);
				FileInputFormat.setInputPaths(aggregateAndRecommend, partialMultiplyPath, explicitFilterPath);
			}
			setIOSort(aggregateAndRecommend);
			aggregateAndRecommendConf.set(AggregateAndRecommendReducer.ITEMID_INDEX_PATH, itemIDIndexPath.toString());
			aggregateAndRecommendConf.setInt(AggregateAndRecommendReducer.NUM_RECOMMENDATIONS, numRecommendations);
			aggregateAndRecommendConf.setBoolean(BOOLEAN_DATA, booleanData);
			task.setCurrentJob(aggregateAndRecommend).waitForCompletion(true);
		}

		return 0;
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

	private static void setIOSort(JobContext job)
	{
		Configuration conf = job.getConfiguration();
		conf.setInt("io.sort.factor", 100);
		int assumedHeapSize = 512;
		String javaOpts = conf.get("mapred.child.java.opts");
		if (javaOpts != null)
		{
			Matcher m = Pattern.compile("-Xmx([0-9]+)([mMgG])").matcher(javaOpts);
			if (m.find())
			{
				assumedHeapSize = Integer.parseInt(m.group(1));
				String megabyteOrGigabyte = m.group(2);
				if ("g".equalsIgnoreCase(megabyteOrGigabyte))
				{
					assumedHeapSize *= 1024;
				}
			}
		}
		conf.setInt("io.sort.mb", assumedHeapSize / 2);
		// For some reason the Merger doesn't report status for a long time; increase
		// timeout when running these jobs
		conf.setInt("mapred.task.timeout", 60 * 60 * 1000);
	}

	public static void main(String[] args) throws Exception
	{
		ToolRunner.run(new Configuration(), new RecommenderJob(), args);
	}
}
