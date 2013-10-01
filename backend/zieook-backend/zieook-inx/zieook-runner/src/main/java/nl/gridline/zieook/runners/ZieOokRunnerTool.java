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
package nl.gridline.zieook.runners;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * ZieOokRunnerTool.java created 18 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public abstract class ZieOokRunnerTool extends Configured implements Tool
{

	private static final Logger LOG = LoggerFactory.getLogger(ZieOokRunnerTool.class);

	/**
	 * task takes care of the communication with the world.
	 */
	protected final ZieOokTask task;

	/**
	 * Sets the basics - see below
	 */
	public ZieOokRunnerTool(ZieOokTask task)
	{
		// setBasics();
		super(new Configuration());
		Preconditions.checkNotNull(task);
		this.task = task;
	}

	public TaskConfig getTaskConfiguration()
	{
		return task.getConfig();
	}

	/**
	 * Set an item in the hadoop configuration
	 * @param key
	 * @param value
	 */
	public void set(String key, String value)
	{
		getConf().set(key, value);
	}

	/**
	 * Set the basic properties that are always the same in the zieook context. The items are directly read from the
	 * zieook configuration files.<br />
	 * The following mapreduce related keys items will be set and read: <br />
	 * <ul>
	 * <li><tt>mapred.job.tracker</tt> read from <tt>mapred.job.tracker</tt></li>
	 * <li><tt>hbase.zookeeper.quorum</tt> read from <tt>zieook.zookeeper.host</tt></li>
	 * <li><tt>hbase.cluster.distributed</tt> read from <tt>hbase.cluster.distributed</tt></li>
	 * <li><tt>hbase.rootdir</tt> read from <tt>hbase.rootdir</tt></li>
	 * </ul>
	 */
	protected void setBasics()
	{
		try
		{
			// set zieook configuration on the Job configuration:
			Config zieook = Config.getInstance();
			Configuration conf = getConf();
			String tracker = zieook.get(Config.ZIEOOK_JOBTRACKER);
			conf.set("mapred.job.tracker", tracker);// MapReduceSettings.JOB_TRACKER_HOST_NAME_PORT);
			// settings from hbase-site.xml:
			conf.set("hbase.zookeeper.quorum", zieook.getZooKeeperHost());
			conf.set("hbase.zookeeper.property.clientPort", zieook.getZooKeeperPort());
			conf.set("hbase.rootdir", zieook.get("hbase.rootdir"));
			conf.set("hbase.cluster.distributed", zieook.get("hbase.cluster.distributed"));
			conf.set("fs.default.name", zieook.get(Config.ZIEOOK_HDFS_SERVER));
			// conf.set("user.name", "mapred");
		}
		catch (Exception e)
		{
			LOG.error("Failed", e);
		}
		LOG.info("SETTING BASICS: DONE");
	}

	public void setJar(String jar) throws IOException
	{
		File file = new File(jar.trim()).getCanonicalFile();
		// check the location of the jar file:
		LOG.info("map reduce jar: {}", file);
		//

		if (!file.exists())
		{
			LOG.error("map reduce jar: does not exist '{}'", file);
			throw new IOException("map reduce jar: does not exist '" + file.toString() + "'");
		}
		if (!file.canRead())
		{
			LOG.error("map reduce jar: cannot be read '{}'", file);
			throw new IOException("map reduce jar: cannot be read '" + file.toString() + "'");
		}

		getConf().set("mapred.jar", file.toString());
	}

	public String getJar()
	{
		return getConf().get("mapred.jar");
	}

	public void setOutputTable(String table)
	{
		getConf().set(TableOutputFormat.OUTPUT_TABLE, table);
	}

	public String getOutputTable()
	{
		return getConf().get(TableOutputFormat.OUTPUT_TABLE);
	}

	public void setInputTable(String table)
	{
		getConf().set(TableInputFormat.INPUT_TABLE, table);
	}

	public String getInputTable()
	{
		return getConf().get(TableInputFormat.INPUT_TABLE);
	}

	public abstract boolean execute() throws Exception;

	// public abstract ZieOokRunnerTool configure(String input, String output) throws Exception;

	/**
	 * A convenience function to print the current configuration.
	 */
	public void printConfiguration()
	{
		Configuration conf = getConf();

		for (Entry<String, String> entry : conf)
		{
			LOG.debug("{}={}", entry.getKey(), entry.getValue());
		}
	}

	/**
	 * all options passed through here are ignored for now.
	 */
	@Override
	public int run(String[] arg0) throws Exception
	{
		return 0;
	}

	/**
	 * @param inputPath input path
	 * @param outputPath output path
	 * @param inputFormat input format
	 * @param mapper mapper class
	 * @param mapperKey mapper key
	 * @param mapperValue mapper value
	 * @param reducer reducer class
	 * @param reducerKey reducer key
	 * @param reducerValue reducer value
	 * @param outputFormat output format
	 * @return a ready to run Job
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public Job prepareJob(Path inputPath, Path outputPath, Class<? extends InputFormat> inputFormat,
			Class<? extends Mapper> mapper, Class<? extends Writable> mapperKey, Class<? extends Writable> mapperValue,
			Class<? extends Reducer> reducer, Class<? extends Writable> reducerKey,
			Class<? extends Writable> reducerValue, Class<? extends OutputFormat> outputFormat) throws IOException
	{

		Job job = new Job(new Configuration(getConf()));
		Configuration jobConf = job.getConfiguration();

		// This never really worked for me: job.setJarByClass... in any case, we already know the jar
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
		if (inputPath != null)
		{
			jobConf.set("mapred.input.dir", inputPath.toString());
		}

		job.setMapperClass(mapper);
		job.setMapOutputKeyClass(mapperKey);
		job.setMapOutputValueClass(mapperValue);

		jobConf.setBoolean("mapred.compress.map.output", true);

		job.setReducerClass(reducer);
		job.setOutputKeyClass(reducerKey);
		job.setOutputValueClass(reducerValue);

		final String name = getCustomJobName(job, mapper, reducer);
		job.setJobName(name);

		job.setOutputFormatClass(outputFormat);
		jobConf.set("mapred.output.dir", outputPath.toString());

		LOG.debug("job setup for: {}", name);

		return job;
	}

	/**
	 * @param inputTable input table
	 * @param outputPath output path
	 * @param scanner table row scanner object
	 * @param mapper mapper class
	 * @param mapperKey table mapper key
	 * @param mapperValue table mapper value
	 * @param reducer reducer class
	 * @param reducerKey reducer key
	 * @param reducerValue reducer value
	 * @param outputFormat output (file) format
	 * @return a ready to run job - unless you need to assign more job specific settings
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public Job prepareTableMapper(String inputTable, Path outputPath, Scan scanner, Class<? extends TableMapper> mapper,
			Class<? extends WritableComparable> mapperKey, Class<? extends WritableComparable> mapperValue,
			Class<? extends Reducer> reducer, Class<? extends Writable> reducerKey,
			Class<? extends Writable> reducerValue, Class<? extends OutputFormat> outputFormat) throws IOException
	{
		setInputTable(inputTable);

		// Configures the Job & starts it:

		Configuration conf = getConf();

		Job job = new Job(conf);
		job.setJobName(getCustomJobName(job, mapper, reducer));

		// mapper:
		TableMapReduceUtil.initTableMapperJob(getInputTable(), scanner, mapper, mapperKey, mapperValue, job);

		job.setReducerClass(reducer);
		job.setOutputKeyClass(reducerKey);
		job.setOutputValueClass(reducerValue);

		job.setOutputFormatClass(outputFormat);

		FileOutputFormat.setOutputPath(job, outputPath);

		return job;
	}

	/**
	 * @param inputPath input path
	 * @param outputTable output table
	 * @param inputFormat input format
	 * @param mapper mapper class
	 * @param mapperKey mapper class key
	 * @param mapperValue mapper class value
	 * @param reducer table reducer
	 * @return a ready to execute job, unless you need to set specific job setting for the mapper / reducer
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public Job prepareTableReducer(Path inputPath, String outputTable, Class<? extends InputFormat> inputFormat,
			Class<? extends Mapper> mapper, Class<? extends Writable> mapperKey, Class<? extends Writable> mapperValue,
			Class<? extends TableReducer> reducer) throws IOException
	{
		setOutputTable(outputTable);

		Configuration conf = getConf();
		conf.set("mapred.input.dir", inputPath.toString());

		Job job = new Job(conf);
		job.setJobName(getCustomJobName(job, mapper, reducer));

		job.setInputFormatClass(inputFormat);
		job.setMapperClass(mapper);
		job.setMapOutputKeyClass(mapperKey);
		job.setMapOutputValueClass(mapperValue);

		TableMapReduceUtil.initTableReducerJob(getOutputTable(), reducer, job);

		return job;
	}

	/**
	 * Generates a nice Job name: class-mapper.class-reducer.class
	 * @param job
	 * @param mapper
	 * @param reducer
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected String getCustomJobName(JobContext job, Class<? extends Mapper> mapper, Class<? extends Reducer> reducer)
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

	/**
	 * Delete data from hdfs
	 * @param path
	 * @throws IOException
	 */
	protected void cleanup(Path path) throws IOException
	{
		Configuration hdfsConfig = new Configuration();
		hdfsConfig.set("fs.default.name", Config.getInstance().get("fs.default.name"));
		FileSystem hdfs = FileSystem.get(hdfsConfig);
		hdfs.delete(path, true);
		LOG.debug("cleanup <{}>", path);
	}

}
