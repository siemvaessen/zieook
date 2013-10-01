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

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.inx.movielens.hbase.CollectionFilterMap;
import nl.gridline.zieook.inx.movielens.hbase.CollectionFilterReduce;
import nl.gridline.zieook.inx.movielens.hbase.DataPrepareMap;
import nl.gridline.zieook.inx.movielens.hbase.DataPrepareReduce;
import nl.gridline.zieook.inx.movielens.items.FilterDataMap;
import nl.gridline.zieook.inx.movielens.items.FilterDataReduce;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.runners.ZieOokRunnerTool;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes HBase data into a Sequence file that can be read by mahout / taste
 * <p />
 * Project zieook-runner<br />
 * DataPrepareTool.java created 25 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class DataPrepareTool extends ZieOokRunnerTool
{

	private static final Logger LOG = LoggerFactory.getLogger(DataPrepareTool.class);

	private static final byte[] COLUMN_RATING = Bytes.toBytes(HBaseTableConstants.USERTABLE_COLUMN_RATING);
	private Path outputPath;
	private String filterKey;
	private String filterValues;
	private String filterStart;
	private String filterEnd;

	private String userTable;
	private String collectionTable;

	private Path filterPath;

	private Path recommendationPath;

	private boolean needsFilter;

	/**
	 * @param task
	 */
	public DataPrepareTool(ZieOokTask task)
	{
		super(task);
	}

	public DataPrepareTool configure(String userTable, String collectionTable, String outputPath) throws IOException
	{
		setBasics();
		setJar(Config.getInstance().get(Config.MAPRED_MOVIELENS));

		Config zieook = Config.getInstance();

		// this will only set the usertable:
		// getConf().set(TableInputFormat.INPUT_TABLE, userTable);

		String cp = task.getConfig().get(TaskConfig.CP);
		String collection = task.getConfig().get(TaskConfig.COLLECTION);
		String recommender = task.getConfig().get(TaskConfig.RECOMMENDER);

		Path tempDirPath = new Path(zieook.get(Config.ZIEOOK_HDFS_SERVER) + zieook.get(Config.ZIEOOK_HDFS_PATH), cp + "/"
				+ collection + "/" + recommender + "/tmp");

		filterPath = new Path(tempDirPath, "filterOutput");
		recommendationPath = new Path(tempDirPath, "recommendationOutput");
		this.userTable = userTable;
		this.collectionTable = collectionTable;
		this.outputPath = new Path(outputPath);

		filterKey = task.getConfig().get(TaskConfig.FILTER_KEY);
		// set filter key & value to null if empty
		if (filterKey != null && filterKey.trim().isEmpty())
		{
			filterKey = null;
		}

		if (filterKey != null)
		{
			filterValues = task.getConfig().get(TaskConfig.FILTER_VALUE);
			if (filterValues != null && filterValues.trim().isEmpty())
			{
				filterValues = null;
			}
		}

		// set filter to null if value == 0 (this is a not so nice fix to make things easier:
		filterStart = task.getConfig().get(TaskConfig.FILTER_STARTDATE);
		filterEnd = task.getConfig().get(TaskConfig.FILTER_ENDDATE);

		if (filterStart != null && filterEnd != null && filterStart.equals(filterEnd))
		{
			filterStart = null;
			filterEnd = null;
		}

		if ("0".equals(filterStart))
		{
			filterStart = null;
		}

		if ("0".equals(filterEnd))
		{
			filterEnd = null;
		}

		// needsFilter = false;

		needsFilter = filterKey != null || filterStart != null || filterEnd != null;

		LOG.info("DataPrepareTool configured; from: <{}> to <{}>", userTable, this.outputPath);

		return this;
	}

	/**
	 * If the long values of both start & end are 0 -> we are not filtering.
	 * Also
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean equal(String start, String end)
	{
		boolean result = false;

		if (start != null && end != null)
		{
			result = (Long.parseLong(start) == Long.parseLong(end));
		}
		return result;
	}

	@Override
	public boolean execute() throws IOException, InterruptedException, ClassNotFoundException
	{

		Configuration conf = getConf();
		conf.set(TableInputFormat.SCAN_COLUMN_FAMILY, HBaseTableConstants.USERTABLE_COLUMN_RATING);

		cleanup(recommendationPath);
		cleanup(filterPath);
		cleanup(outputPath);

		LOG.info("preparing filter: from <{}> to <{}>", userTable, recommendationPath);

		Job recommendationsMapper = prepareTableMapper(userTable, recommendationPath, getUserDataScanner(),
				DataPrepareMap.class, ImmutableBytesWritable.class, Text.class, DataPrepareReduce.class, Text.class,
				Text.class, SequenceFileOutputFormat.class);

		boolean result = task.setCurrentJob(recommendationsMapper).waitForCompletion(LOG.isDebugEnabled());

		if (needsFilter)
		{
			LOG.info("Setting filters");

			if (!result || task.isCancelled())
			{
				return result;
			}

			Job filterMap = prepareTableMapper(collectionTable, filterPath, getCollectionScanner(),
					CollectionFilterMap.class, Text.class, LongWritable.class, CollectionFilterReduce.class, Text.class,
					Text.class, SequenceFileOutputFormat.class);

			if (filterKey != null)
			{
				LOG.info("Setting filters: <{}> = <{}>", filterKey, filterValues);
				filterMap.getConfiguration().set(TaskConfig.FILTER_KEY, filterKey);
				filterMap.getConfiguration().set(TaskConfig.FILTER_VALUE, filterValues);
			}
			if (filterStart != null)
			{
				LOG.info("Setting filters: <{}> = <{}>", TaskConfig.FILTER_STARTDATE, filterStart);
				filterMap.getConfiguration().set(TaskConfig.FILTER_STARTDATE, filterStart);
			}
			if (filterEnd != null)
			{
				LOG.info("Setting filters: <{}> = <{}>", TaskConfig.FILTER_ENDDATE, filterEnd);
				filterMap.getConfiguration().set(TaskConfig.FILTER_ENDDATE, filterEnd);
			}

			result = task.setCurrentJob(filterMap).waitForCompletion(LOG.isDebugEnabled());
			if (!result || task.isCancelled())
			{
				return result;
			}
		}
		else
		{
			LOG.info("No filters applied");
		}

		LOG.info("writing rating: from <{}> to <{}>", recommendationPath, outputPath);

		// merge!
		Job filterData = prepareJob(null, outputPath, SequenceFileInputFormat.class, FilterDataMap.class, Text.class,
				Text.class, FilterDataReduce.class, Text.class, NullWritable.class, TextOutputFormat.class);
		filterData.getConfiguration().setBoolean(TaskConfig.SHOULD_FILTER, needsFilter);
		FileInputFormat.addInputPath(filterData, recommendationPath);

		if (needsFilter)
		{
			FileInputFormat.addInputPath(filterData, filterPath);
		}

		return task.setCurrentJob(filterData).waitForCompletion(LOG.isDebugEnabled());

	}

	/**
	 * Return a scanner for rating column in the user table. Also filters on
	 * @return
	 * @throws IOException
	 */
	private Scan getUserDataScanner() throws IOException
	{
		String collection = getConf().get(TaskConfig.COLLECTION);
		if (collection == null)
		{
			// fail!
			throw new IOException("collection not set");
		}
		RowFilter filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRatingKey(collection)));
		Scan result = new Scan().addFamily(COLUMN_RATING).setFilter(filter);

		return result;
	}

	private Scan getCollectionScanner() throws IOException
	{

		return new Scan().addFamily(Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR));
	}
}
