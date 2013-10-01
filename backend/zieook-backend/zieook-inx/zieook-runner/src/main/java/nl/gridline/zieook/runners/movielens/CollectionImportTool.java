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
package nl.gridline.zieook.runners.movielens;

import java.io.IOException;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.inx.movielens.categories.CategoriesExtractMap;
import nl.gridline.zieook.inx.movielens.categories.CategoriesExtractReduce;
import nl.gridline.zieook.inx.movielens.categories.CategoriesImportMap;
import nl.gridline.zieook.inx.movielens.categories.CategoriesImportReduce;
import nl.gridline.zieook.inx.movielens.hbase.CollectionImportMap;
import nl.gridline.zieook.inx.movielens.hbase.CollectionImportReduce;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.runners.ZieOokRunnerTool;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * DataFileImportTool.java created 18 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionImportTool extends ZieOokRunnerTool
{

	private static final byte[] INTR = Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR);

	private static final Logger LOG = LoggerFactory.getLogger(CollectionImportTool.class);
	private Path inputFile;
	private String cp;
	private String collection;

	private Path datapath;

	/**
	 * @param task
	 */
	public CollectionImportTool(ZieOokTask task)
	{
		super(task);
	}

	public CollectionImportTool configure(String outputTable, String inputFile) throws IOException
	{
		Config zieook = Config.getInstance();

		setBasics();
		setJar(Config.getInstance().get(Config.MAPRED_MOVIELENS));
		setOutputTable(outputTable);

		this.inputFile = new Path(inputFile);

		datapath = new Path(zieook.get(Config.ZIEOOK_HDFS_SERVER) + zieook.get(Config.ZIEOOK_HDFS_PATH), cp + "/"
				+ collection + "/tmp/categories");
		cp = task.getConfig().get(TaskConfig.CP);
		collection = task.getConfig().get(TaskConfig.COLLECTION);

		LOG.info("Movielens(type) data collection configured; from: <{}> to <{}>", inputFile, outputTable);
		return this;
	}

	@Override
	public boolean execute() throws IOException, InterruptedException, ClassNotFoundException
	{

		// Configures the Job & starts it:
		Configuration conf = getConf();

		// create a Job based on the configuration:
		Job job = new Job(conf);
		job.setJobName(getCustomJobName(job, CollectionImportMap.class, CollectionImportReduce.class));
		job.setMapperClass(CollectionImportMap.class);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Put.class);

		// necessary?
		job.setNumReduceTasks(4); // is this needed?

		TableMapReduceUtil.initTableReducerJob(getOutputTable(), CollectionImportReduce.class, job);
		FileInputFormat.addInputPath(job, inputFile);

		// before it's really executed log the configuration to debug:
		// printConfiguration();
		boolean result = task.setCurrentJob(job).waitForCompletion(LOG.isDebugEnabled());

		cleanup(inputFile); // remove the input

		if (!result || task.isCancelled())
		{
			return result;
		}

		// if completed successfully, delete the import file:
		LOG.info("import done, result: {}", result);

		// extract categories from collection:
		result = extractCategories();

		return result;

	}

	private boolean extractCategories() throws IOException, InterruptedException, ClassNotFoundException
	{
		// cleanup before:
		cleanup(datapath);

		Job extractCategories = prepareTableMapper(getOutputTable(), datapath, getScanner(collection),
				CategoriesExtractMap.class, Text.class, LongWritable.class, CategoriesExtractReduce.class, Text.class,
				LongWritable.class, SequenceFileOutputFormat.class);
		boolean result = extractCategories.waitForCompletion(LOG.isDebugEnabled());
		if (!result || task.isCancelled())
		{
			return result;
		}

		// now import it back in to HBase:
		Job importCategories = prepareTableReducer(datapath, getOutputTable(), SequenceFileInputFormat.class,
				CategoriesImportMap.class, Text.class, Put.class, CategoriesImportReduce.class);
		importCategories.getConfiguration().set(TaskConfig.COLLECTION, task.getConfig().get(TaskConfig.COLLECTION));

		result = task.setCurrentJob(importCategories).waitForCompletion(LOG.isDebugEnabled());

		return result;
	}

	private Scan getScanner(String collection)
	{
		RowFilter filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(Bytes.toBytes(collection)));
		return new Scan().addFamily(INTR).setFilter(filter);
	}
}
