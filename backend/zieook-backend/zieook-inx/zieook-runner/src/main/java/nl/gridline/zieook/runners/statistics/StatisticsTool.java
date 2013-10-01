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
package nl.gridline.zieook.runners.statistics;

import java.io.IOException;
import java.util.Date;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.model.ModelConstants;
import nl.gridline.zieook.runners.ZieOokRunnerTool;
import nl.gridline.zieook.statistics.RatingsPerCPUserMap;
import nl.gridline.zieook.statistics.RatingsPerCPUserReduce;
import nl.gridline.zieook.statistics.RatingsPerCollectionItemMap;
import nl.gridline.zieook.statistics.RatingsPerCollectionItemReduce;
import nl.gridline.zieook.statistics.RecommendedItemToUserMap;
import nl.gridline.zieook.statistics.RecommendedItemToUserReduce;
import nl.gridline.zieook.statistics.RecommendedToUserMap;
import nl.gridline.zieook.statistics.RecommendedToUserReduce;
import nl.gridline.zieook.statistics.SourcesByRecommenderMap;
import nl.gridline.zieook.statistics.SourcesByRecommenderReduce;
import nl.gridline.zieook.statistics.ViewsPerItemMap;
import nl.gridline.zieook.statistics.ViewsPerItemReduce;
import nl.gridline.zieook.statistics.io.RecommenderItem;
import nl.gridline.zieook.statistics.io.RecommenderSource;
import nl.gridline.zieook.statistics.io.UserCollection;
import nl.gridline.zieook.statistics.io.UserCollectionItem;
import nl.gridline.zieook.statistics.popularity.PopularityCountMap;
import nl.gridline.zieook.statistics.popularity.PopularityCountReduce;
import nl.gridline.zieook.statistics.popularity.PopularityMap;
import nl.gridline.zieook.statistics.popularity.PopularityReduce;
import nl.gridline.zieook.statistics.popularity.PopularityScoreMap;
import nl.gridline.zieook.statistics.popularity.PopularityScoreReduce;
import nl.gridline.zieook.statistics.popularity.PopularityStatistics;
import nl.gridline.zieook.statistics.popularity.io.ItemTime;
import nl.gridline.zieook.statistics.popularity.io.PopularityWritable;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This tool extracts statistics about the different tasks, that cannot be generated directly from a scan.
 * The map-reduce for counting sources and importing the results
 * <p />
 * Project zieook-runner<br />
 * RecommendationViewPrepareTool.java created Mar 22, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:daryl@gridline.nl">Daryl</a>
 * @version $Revision$, $Date$
 */
public class StatisticsTool extends ZieOokRunnerTool
{

	private static final Logger LOG = LoggerFactory.getLogger(StatisticsTool.class);

	public static final byte[] EVENTLOG_COLUMN_USERVIEW = Bytes.toBytes(HBaseTableConstants.EVENTLOG_COLUMN_VIEW);
	private static final byte[] EVENTLOG_COLUMN_RECOMMENDED = Bytes
			.toBytes(HBaseTableConstants.EVENTLOG_COLUMN_RECOMMENDED);

	private static final byte[] USERTABLE_COLUMN_RATING = Bytes.toBytes(HBaseTableConstants.USERTABLE_COLUMN_RATING);

	private Long startDate;
	private Long endDate;

	private String cp;

	private String eventLogTable;
	private String userTable;

	private String collection;

	private Path tempDirPath;

	/**
	 * @param task
	 */
	public StatisticsTool(ZieOokTask task)
	{
		super(task);
	}

	/**
	 * This configures this tool with the given table and file
	 * @param inputTable
	 * @param outputFile
	 * @return
	 * @throws IOException
	 */
	public StatisticsTool configure(String eventLogTable, String userTable, String outputTable) throws IOException
	{
		setBasics();
		setJar(Config.getInstance().get(Config.MAPRED_MOVIELENS));

		// Config zieook = Config.getInstance();

		cp = task.getConfig().get(TaskConfig.CP);
		if (cp == null)
		{
			throw new IOException("content provider not set, please set <" + TaskConfig.CP + "> in the task configuration");
		}
		collection = task.getConfig().get(TaskConfig.COLLECTION);
		if (collection == null)
		{
			throw new IOException("collection not set, please set <" + TaskConfig.COLLECTION
					+ "> in the task configuration");
		}

		// tmpFile = new Path(zieook.get(Config.ZIEOOK_HDFS_SERVER) + zieook.get(Config.ZIEOOK_HDFS_PATH), cp
		// + "/statistics/tmp");

		this.eventLogTable = eventLogTable;
		this.userTable = userTable;

		getConf().set(TableOutputFormat.OUTPUT_TABLE, outputTable);

		startDate = task.getConfig().getLong(TaskConfig.STATS_START, null);
		endDate = task.getConfig().getLong(TaskConfig.STATS_END, null);

		Config zieook = Config.getInstance();

		tempDirPath = new Path(zieook.get(Config.ZIEOOK_HDFS_SERVER) + zieook.get(Config.ZIEOOK_HDFS_PATH), cp + "/"
				+ collection + "/statistics");

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
		cleanup(tempDirPath);

		LOG.info("Executing statistics from: <{}> to <{}>", startDate != null ? new Date(startDate.longValue()) : "null",
				endDate != null ? new Date(endDate.longValue()) : "null");

		// run all tasks, but check for cancellation:

		boolean result = processSources();
		if (task.isCancelled())
		{
			return false;
		}
		result = result && processUserRated();
		if (task.isCancelled())
		{
			return false;
		}
		result = result && processRecommendedItem();
		if (task.isCancelled())
		{
			return false;
		}
		result = result && processItemRated();
		if (task.isCancelled())
		{
			return false;
		}
		result = result && processRecommended();
		if (task.isCancelled())
		{
			return false;
		}
		result = result && processViewed();
		if (task.isCancelled())
		{
			return false;
		}

		result = processPopularity();

		return result;
	}

	private boolean processSources() throws IOException, InterruptedException, ClassNotFoundException
	{

		// creates data for:
		// Get the number of different sources for a recommender & and show the count
		// Recommendeds getSources(@PathParam("cp") String cp, @PathParam("recommender") String recommender);

		Configuration conf = getConf();

		Job job = new Job(conf);
		job.setJobName(getCustomJobName(job, SourcesByRecommenderMap.class, SourcesByRecommenderReduce.class));
		// job.getConfiguration().set(TableInputFormat.INPUT_TABLE, eventLogTable);
		job.getConfiguration().set(SourcesByRecommenderReduce.DATE, Long.toString(endDate));
		// maps directly from and to a table:

		// mapper:
		TableMapReduceUtil.initTableMapperJob(eventLogTable, getEventLogViewScanner(startDate, endDate),
				SourcesByRecommenderMap.class, RecommenderSource.class, LongWritable.class, job);

		// reducer:
		TableMapReduceUtil.initTableReducerJob(getOutputTable(), SourcesByRecommenderReduce.class, job);

		boolean result = task.setCurrentJob(job).waitForCompletion(LOG.isDebugEnabled());

		// The top-n of most used sources.
		// Recommendeds topSources(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
		// @QueryParam("startdate") Long startDate, @QueryParam("enddate") Long endDate, @QueryParam("size") Integer
		// size);

		// Get the number of different sources for a recommender & and show the count
		// Recommendeds getSources(@PathParam("cp") String cp, @PathParam("collection") String collection,
		// @QueryParam("from") String from, Integer size);

		// The top-n of most used sources.
		// Recommendeds topSources(@PathParam("cp") String cp, @PathParam("collection") String collection,
		// @QueryParam("start_time") Long startTime, @QueryParam("end_time") Long endTime,
		// @QueryParam("size") Integer size);
		return result;
	}

	private boolean processItemRated() throws IOException, InterruptedException, ClassNotFoundException
	{
		// Get the top-n (count) of given ratings
		// Ratings topRated(@PathParam("cp") String cp, @PathParam("collection") String collection,
		// @QueryParam("startdate") Long startDate, @QueryParam("enddate") Long endDate, @QueryParam("size") Integer
		// size);

		Configuration conf = getConf();

		Job job = new Job(conf);
		job.setJobName(getCustomJobName(job, RatingsPerCollectionItemMap.class, RatingsPerCollectionItemReduce.class));
		// job.getConfiguration().set(TableInputFormat.INPUT_TABLE, userTable);
		job.getConfiguration().set(RatingsPerCollectionItemReduce.DATE, Long.toString(endDate));
		job.getConfiguration().set(TaskConfig.COLLECTION, collection);

		TableMapReduceUtil.initTableMapperJob(userTable, getRatingsScanner(collection, startDate, endDate),
				RatingsPerCollectionItemMap.class, LongWritable.class, LongWritable.class, job);

		// reducer:
		TableMapReduceUtil.initTableReducerJob(getOutputTable(), RatingsPerCollectionItemReduce.class, job);

		return task.setCurrentJob(job).waitForCompletion(LOG.isDebugEnabled());
	}

	private boolean processUserRated() throws IOException, InterruptedException, ClassNotFoundException
	{
		// Get the top-n (count) of given ratings
		// Ratings topRated(@PathParam("cp") String cp, @PathParam("collection") String collection,
		// @QueryParam("startdate") Long startDate, @QueryParam("enddate") Long endDate, @QueryParam("size") Integer
		// size);

		Configuration conf = getConf();

		Job job = new Job(conf);
		job.setJobName(getCustomJobName(job, RatingsPerCPUserMap.class, RatingsPerCPUserReduce.class));
		// job.getConfiguration().set(TableInputFormat.INPUT_TABLE, userTable);
		job.getConfiguration().set(RatingsPerCPUserReduce.DATE, Long.toString(endDate));
		job.getConfiguration().set(TaskConfig.COLLECTION, collection);

		TableMapReduceUtil.initTableMapperJob(userTable, getRatingsScanner(collection, startDate, endDate),
				RatingsPerCPUserMap.class, LongWritable.class, LongWritable.class, job);

		// reducer:
		TableMapReduceUtil.initTableReducerJob(getOutputTable(), RatingsPerCPUserReduce.class, job);

		return task.setCurrentJob(job).waitForCompletion(LOG.isDebugEnabled());
	}

	private boolean processRecommendedItem() throws IOException, InterruptedException, ClassNotFoundException
	{
		// Get the top-n most recommended items to user
		// Recommends topRecommended(@PathParam("cp") String cp, @PathParam("recommender") String recommender,
		// @PathParam("user") long user, @QueryParam("size") Integer size);

		// Get the top-n most recommended items to user and return them as collection items
		// CollectionItems topRecommendedAsCollection(@PathParam("cp") String cp, @PathParam("recommender") String
		// recommender,
		// @PathParam("user") long user, @QueryParam("size") Integer size);

		Configuration conf = getConf();
		Job job = new Job(conf);
		job.setJobName(getCustomJobName(job, RecommendedItemToUserMap.class, RecommendedItemToUserReduce.class));
		// job.getConfiguration().set(TableInputFormat.INPUT_TABLE, eventLogTable);
		job.getConfiguration().set(RecommendedItemToUserReduce.DATE, Long.toString(endDate));
		job.getConfiguration().set(TaskConfig.COLLECTION, collection);

		TableMapReduceUtil.initTableMapperJob(eventLogTable, getEventLogRecommendedScanner(startDate, endDate),
				RecommendedItemToUserMap.class, UserCollectionItem.class, LongWritable.class, job);

		// reducer:
		TableMapReduceUtil.initTableReducerJob(getOutputTable(), RecommendedItemToUserReduce.class, job);

		return task.setCurrentJob(job).waitForCompletion(LOG.isDebugEnabled());
	}

	private boolean processRecommended() throws IOException, InterruptedException, ClassNotFoundException
	{
		Configuration conf = getConf();
		Job job = new Job(conf);
		job.setJobName(getCustomJobName(job, RecommendedToUserMap.class, RecommendedToUserReduce.class));
		// job.getConfiguration().set(TableInputFormat.INPUT_TABLE, eventLogTable);
		job.getConfiguration().set(RecommendedItemToUserReduce.DATE, Long.toString(endDate));
		job.getConfiguration().set(TaskConfig.COLLECTION, collection);

		TableMapReduceUtil.initTableMapperJob(eventLogTable, getEventLogRecommendedScanner(startDate, endDate),
				RecommendedToUserMap.class, UserCollection.class, LongWritable.class, job);

		// reducer:
		TableMapReduceUtil.initTableReducerJob(getOutputTable(), RecommendedToUserReduce.class, job);

		return task.setCurrentJob(job).waitForCompletion(LOG.isDebugEnabled());
	}

	private boolean processViewed() throws IOException, InterruptedException, ClassNotFoundException
	{
		Configuration conf = getConf();
		Job job = new Job(conf);
		job.setJobName(getCustomJobName(job, ViewsPerItemMap.class, ViewsPerItemReduce.class));
		job.getConfiguration().set(ViewsPerItemReduce.DATE, Long.toString(endDate));
		job.getConfiguration().set(TaskConfig.COLLECTION, collection);

		TableMapReduceUtil.initTableMapperJob(eventLogTable, getEventLogViewScanner(startDate, endDate),
				ViewsPerItemMap.class, RecommenderItem.class, LongWritable.class, job);

		TableMapReduceUtil.initTableReducerJob(getOutputTable(), ViewsPerItemReduce.class, job);

		return task.setCurrentJob(job).waitForCompletion(LOG.isDebugEnabled());
	}

	private boolean processPopularity() throws IOException, InterruptedException, ClassNotFoundException
	{

		Path ratingsCount = new Path(tempDirPath, "ratings-count");
		Path populCount = new Path(tempDirPath, "popularity-count");

		// get the settings:
		String types = Config
				.getInstance()
				.get(Config.STATISTICS_POPULARITY_TYPES,
						"day,86400;week,604800;3months,7884000;6months,15768000;year,31536000").trim();

		// get the earliest start date: (don't look back forever, if not needed:
		long end = endDate != null ? endDate.longValue() : System.currentTimeMillis();
		long start = end - PopularityStatistics.getTypes(types).lastKey();

		LOG.info("popularity between: {} and {}", new Date(start), new Date(endDate));
		LOG.info("using spans: {}", types);

		// first: count ratings per item / time-unit:
		Job countRatings = prepareTableMapper(userTable, ratingsCount, getRatingsScanner(collection, start, end),
				PopularityMap.class, ItemTime.class, LongWritable.class, PopularityReduce.class, ItemTime.class,
				LongWritable.class, SequenceFileOutputFormat.class);

		// set the types:
		countRatings.getConfiguration().set(PopularityStatistics.TYPES, types);
		// set end date or now:
		countRatings.getConfiguration().set(PopularityStatistics.NOW,
				(endDate != null ? endDate.toString() : Long.toString(System.currentTimeMillis())));

		boolean result = task.setCurrentJob(countRatings).waitForCompletion(LOG.isDebugEnabled());
		if (!result || task.isCancelled())
		{
			LOG.debug("stopping: result={} cancelled={}", result, task.isCancelled());
			return result;
		}

		Job popularityCount = prepareJob(ratingsCount, populCount, SequenceFileInputFormat.class,
				PopularityCountMap.class, LongWritable.class, LongWritable.class, PopularityCountReduce.class,
				LongWritable.class, LongWritable.class, TextOutputFormat.class);

		result = task.setCurrentJob(popularityCount).waitForCompletion(LOG.isDebugEnabled());
		if (!result || task.isCancelled())
		{
			LOG.debug("stopping: result={} cancelled={}", result, task.isCancelled());
			return result;
		}

		// read the file... and assign to the score task as a single string, or something.

		Job popularityScore = prepareTableReducer(ratingsCount, getOutputTable(), SequenceFileInputFormat.class,
				PopularityScoreMap.class, ItemTime.class, PopularityWritable.class, PopularityScoreReduce.class);

		String typeCount = PopularityStatistics.readData(populCount, getConf());
		popularityScore.getConfiguration().set(PopularityStatistics.TYPES_COUNT, typeCount);
		popularityScore.getConfiguration().set(PopularityStatistics.TYPES, types);

		return task.setCurrentJob(popularityScore).waitForCompletion(LOG.isDebugEnabled());
	}

	/**
	 * return a rating scanner
	 * @param collection - collection name
	 * @param startDate - optional start date
	 * @param endDate - optional end date
	 * @return a scanner
	 */
	public Scan getRatingsScanner(String collection, Long startDate, Long endDate)
	{
		final FilterList filter = new FilterList();

		// filter on collection:
		filter.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRatingKey())));

		// set start date if given:
		if (startDate != null)
		{
			final SingleColumnValueFilter startTime = new SingleColumnValueFilter(USERTABLE_COLUMN_RATING,
					ModelConstants.TIMESTAMP, CompareOp.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes(startDate
							.longValue())));
			startTime.setFilterIfMissing(true);

			filter.addFilter(startTime);
		}

		// set end date if given:
		if (endDate != null)
		{
			final SingleColumnValueFilter endTime = new SingleColumnValueFilter(USERTABLE_COLUMN_RATING,
					ModelConstants.TIMESTAMP, CompareOp.LESS, new BinaryComparator(Bytes.toBytes(endDate.longValue())));
			endTime.setFilterIfMissing(true);

			filter.addFilter(endTime);
		}

		return new Scan().addFamily(USERTABLE_COLUMN_RATING).setFilter(filter);
	}

	/**
	 * return a recommended scanner with an optional start date and end date
	 * @param startDate start date
	 * @param endDate end date
	 * @return
	 */
	public Scan getEventLogRecommendedScanner(Long startDate, Long endDate)
	{
		Scan scan = new Scan().addFamily(EVENTLOG_COLUMN_RECOMMENDED);
		FilterList filters = new FilterList();
		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRecommendedItemKey())));
		// timestamp filter:
		if (startDate != null)
		{
			SingleColumnValueFilter startFilter = new SingleColumnValueFilter(EVENTLOG_COLUMN_RECOMMENDED,
					ModelConstants.TIMESTAMP, CompareOp.GREATER_OR_EQUAL, Bytes.toBytes(startDate.longValue()));
			startFilter.setFilterIfMissing(true);
			filters.addFilter(startFilter);
		}

		if (endDate != null)
		{
			SingleColumnValueFilter endFilter = new SingleColumnValueFilter(EVENTLOG_COLUMN_RECOMMENDED,
					ModelConstants.TIMESTAMP, CompareOp.LESS, Bytes.toBytes(endDate.longValue()));
			endFilter.setFilterIfMissing(true);
			filters.addFilter(endFilter);
		}

		return scan.setFilter(filters);
	}

	/**
	 * create a scanner for user views, within optional start / end range
	 * @return a scanner object
	 * @throws IOException
	 */
	public Scan getEventLogViewScanner(Long startDate, Long endDate)
	{
		FilterList filters = new FilterList();

		// row prefix filter:
		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getUserViewKey())));

		// timestamp filter:
		if (startDate != null)
		{
			SingleColumnValueFilter startFilter = new SingleColumnValueFilter(EVENTLOG_COLUMN_USERVIEW,
					ModelConstants.TIMESTAMP, CompareOp.GREATER_OR_EQUAL, Bytes.toBytes(startDate.longValue()));
			startFilter.setFilterIfMissing(true);
			filters.addFilter(startFilter);
		}

		if (endDate != null)
		{
			SingleColumnValueFilter endFilter = new SingleColumnValueFilter(EVENTLOG_COLUMN_USERVIEW,
					ModelConstants.TIMESTAMP, CompareOp.LESS, Bytes.toBytes(endDate.longValue()));
			endFilter.setFilterIfMissing(true);
			filters.addFilter(endFilter);
		}

		return new Scan().addFamily(EVENTLOG_COLUMN_USERVIEW).setFilter(filters);
	}
}