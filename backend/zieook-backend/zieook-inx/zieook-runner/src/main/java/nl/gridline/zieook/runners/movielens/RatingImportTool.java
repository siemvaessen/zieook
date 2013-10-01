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
import nl.gridline.zieook.inx.movielens.hbase.RatingImportMap;
import nl.gridline.zieook.inx.movielens.hbase.RatingImportReduce;
import nl.gridline.zieook.runners.ZieOokRunnerTool;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Imports a movielens rating file
 * <p />
 * Project zieook-runner<br />
 * MovielensRatingHBaseImportRunnerTool.java created 10 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:gerlof@gridline.nl">Gerlof</a>
 * @version $Revision$, $Date$
 */
public class RatingImportTool extends ZieOokRunnerTool
{

	private static final Logger LOG = LoggerFactory.getLogger(RatingImportTool.class);

	private Path inputFile;

	/**
	 * @param task
	 */
	public RatingImportTool(ZieOokTask task)
	{
		super(task);
	}

	public RatingImportTool configure(String outputTable, String inputFile) throws IOException
	{
		setBasics();
		setJar(Config.getInstance().get(Config.MAPRED_MOVIELENS));
		setOutputTable(outputTable);
		this.inputFile = new Path(inputFile);
		LOG.info("DataFileImport configured; from: <{}> to <{}>", inputFile, outputTable);

		return this;
	}

	@Override
	public boolean execute() throws Exception
	{

		Configuration conf = getConf();

		Job job = new Job(conf);
		job.setJobName(getCustomJobName(job, RatingImportMap.class, RatingImportReduce.class));
		job.setMapperClass(RatingImportMap.class);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Put.class);
		job.setNumReduceTasks(4);
		TableMapReduceUtil.initTableReducerJob(getOutputTable(), RatingImportReduce.class, job);
		FileInputFormat.addInputPath(job, inputFile);

		// printConfiguration();
		boolean result = task.setCurrentJob(job).waitForCompletion(LOG.isDebugEnabled());
		LOG.info("import done, result: {}", result);
		if (result)
		{
			cleanup(inputFile);
		}
		return result;
	}

}
