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
import nl.gridline.zieook.inx.movielens.hbase.CollectionExportMap;
import nl.gridline.zieook.inx.movielens.hbase.CollectionExportReduce;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.runners.ZieOokRunnerTool;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * DataFileExportTool.java created 3 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionExportTool extends ZieOokRunnerTool
{

	private static final Logger LOG = LoggerFactory.getLogger(CollectionExportTool.class);
	private String outputFile;

	/**
	 * @param task
	 */
	public CollectionExportTool(ZieOokTask task)
	{
		super(task);
	}

	public CollectionExportTool configure(String inputTable, String outputDir) throws IOException
	{
		setBasics();
		setJar(Config.getInstance().get(Config.MAPRED_MOVIELENS));
		getConf().set(TableInputFormat.INPUT_TABLE, inputTable);
		outputFile = outputDir;
		LOG.info("DataPrepareTool configured; from: <{}> to <{}>", inputTable, outputDir);
		return this;
	}

	@Override
	public boolean execute() throws Exception
	{
		Configuration conf = getConf();
		conf.set(TableInputFormat.SCAN_COLUMN_FAMILY, HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR);

		Job job = new Job(conf);
		job.setJobName("Prepare recommender: <" + getInputTable() + ">");

		// mapper
		TableMapReduceUtil.initTableMapperJob(getInputTable(), getScanner(), CollectionExportMap.class,
				ImmutableBytesWritable.class, Text.class, job);

		// reducer:
		job.setReducerClass(CollectionExportReduce.class);
		job.setOutputKeyClass(ImmutableBytesWritable.class);
		job.setOutputValueClass(Text.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		FileOutputFormat.setOutputPath(job, new Path(outputFile));

		return task.setCurrentJob(job).waitForCompletion(LOG.isDebugEnabled());
	}

	/**
	 * Return a scanner for rating column in the user table. Also filters on
	 * @return
	 * @throws IOException
	 */
	private Scan getScanner() throws IOException
	{
		return new Scan().addFamily(Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR));
	}

}
