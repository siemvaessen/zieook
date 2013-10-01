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

import java.io.IOException;
import java.net.URL;

import javax.xml.bind.JAXBException;

import nl.gridline.zieook.OAIException;
import nl.gridline.zieook.OAITools;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.inx.czp.CZPMap;
import nl.gridline.zieook.inx.czp.CZPReduce;
import nl.gridline.zieook.inx.dc.DCMap;
import nl.gridline.zieook.inx.dc.DCReduce;
import nl.gridline.zieook.inx.movielens.categories.CategoriesExtractMap;
import nl.gridline.zieook.inx.movielens.categories.CategoriesExtractReduce;
import nl.gridline.zieook.inx.movielens.categories.CategoriesImportMap;
import nl.gridline.zieook.inx.movielens.categories.CategoriesImportReduce;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.commons.lang.NotImplementedException;
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
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * CZPOaiImportTool.java created 8 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class OaiImportTool extends ZieOokRunnerTool
{

	public static final String CZP_METADATAPREFIX = "czp";
	private boolean czp = false;

	public static final String OAIDC_METADATAPREFIX = "oai_dc";
	private boolean oaidc = false;
	// other format:
	private String metadataPrefix;

	private final byte[] INTR = Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR);

	private static final Logger LOG = LoggerFactory.getLogger(OaiImportTool.class);
	private String inputUrl;
	private Long startdate;
	private Long enddate;
	private int limit;
	private long wait;
	private String collection;
	private String cp;

	private Path datapath;
	private String loadsets;

	// configuration overrides:

	private String dateformat;
	private Path input;

	/**
	 * @param task
	 */
	public OaiImportTool(ZieOokTask task)
	{
		super(task);
	}

	public OaiImportTool configure(String inputUrl, String outputTable) throws IOException, OAIException
	{
		// add generic support for input format:

		Config zieook = Config.getInstance();

		setBasics();
		// obviously if we are getting more different oai formats this stuff needs to be generalized a little bit:

		setOutputTable(outputTable);

		this.inputUrl = inputUrl;

		metadataPrefix = task.getConfig().get(TaskConfig.OAI_METADATA);
		if (metadataPrefix == null)
		{
			// check both, prefer czp:
			oaidc = OAITools.hasMetadataPrefix(new URL(inputUrl), OAIDC_METADATAPREFIX);
			czp = OAITools.hasMetadataPrefix(new URL(inputUrl), CZP_METADATAPREFIX);
			if (czp)
			{
				final String czpJAR = zieook.get(Config.MAPRED_CZP);
				if (czpJAR == null)
				{
					throw new IOException("configuration error <" + Config.MAPRED_CZP + "> is not set");
				}
				setJar(czpJAR);

				metadataPrefix = CZP_METADATAPREFIX;
			}
			else if (oaidc)
			{
				final String oaidcJAR = zieook.get(Config.MAPRED_OAI_DC);
				if (oaidcJAR == null)
				{
					throw new IOException("configuration error <" + Config.MAPRED_OAI_DC + "> is not set.");
				}
				setJar(oaidcJAR);
				metadataPrefix = OAIDC_METADATAPREFIX;
			}
		}
		else
		{
			setJar(zieook.get(Config.MAPRED_BASE + "." + metadataPrefix));
		}
		LOG.info("metadataPrefix set to '{}'", metadataPrefix);

		// task from / until:
		startdate = task.getConfig().getLong(TaskConfig.OAI_START_DATE, null);
		enddate = task.getConfig().getLong(TaskConfig.OAI_END_DATE, null);

		loadsets = task.getConfig().get(TaskConfig.OAI_SETS);

		limit = (int) task.getConfig()
				.getLong(TaskConfig.OAI_ITEM_LIMIT, zieook.getLong(Config.OAI_GLOBAL_ITEMLIMIT, -1));
		cp = task.getConfig().get(TaskConfig.CP);

		// global override, this will be preferred over the limit in the task, handy for testing.

		collection = task.getConfig().get(TaskConfig.COLLECTION);
		wait = zieook.getLong(Config.EDIT_WAIT_MS, 0);
		datapath = new Path(zieook.get(Config.ZIEOOK_HDFS_SERVER) + zieook.get(Config.ZIEOOK_HDFS_PATH), cp + "/"
				+ collection + "/tmp/categories");

		input = new Path(zieook.get(Config.ZIEOOK_HDFS_SERVER) + zieook.get(Config.ZIEOOK_HDFS_PATH), cp + "/"
				+ collection + "/tmp/input.txt");
		LOG.info("OAI Collection import configured; from: <{}> to <{}>", inputUrl, outputTable);

		// delete the old dataset:
		cleanup(input);

		return this;
	}

	@Override
	public boolean execute() throws IOException, JAXBException, OAIException, InterruptedException,
			ClassNotFoundException
	{

		// Configures the Job & starts it:
		Configuration conf = getConf();

		// create a Job based on the configuration:
		Job job = new Job(conf, "Import data: <" + getOutputTable() + ">");

		job.setInputFormatClass(TextInputFormat.class);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Put.class);
		job.getConfiguration().set("mapred.input.dir", input.toString());

		// harvest the oai server...
		task.setCurrentJob(job);

		try
		{
			importData(input);
		}
		catch (IOException e)
		{
			LOG.error("Import failed with error trying next step anyway", e);
		}
		catch (JAXBException e)
		{
			LOG.error("Import failed with error trying next step anyway", e);
		}
		catch (OAIException e)
		{
			LOG.error("Import failed with error trying next step anyway", e);
		}

		if (oaidc)
		{
			LOG.info("The <{}> has a 'oai_dc' metadataprefix, this will be used to import the data", inputUrl,
					OAIDC_METADATAPREFIX);
			job.setMapperClass(DCMap.class);
			TableMapReduceUtil.initTableReducerJob(getOutputTable(), DCReduce.class, job);
		}
		else if (czp)
		{
			LOG.info("The <{}> has a '{}' metadataprefix, this will be used to import the data", inputUrl,
					CZP_METADATAPREFIX);
			job.setMapperClass(CZPMap.class);
			TableMapReduceUtil.initTableReducerJob(getOutputTable(), CZPReduce.class, job);
		}
		else if (metadataPrefix != null)
		{
			//
			throw new NotImplementedException("we do not support metadata other then oai_dc or czp yet, metadata given: "
					+ metadataPrefix);
		}

		// set cp & collection on the task:
		job.getConfiguration().set(TaskConfig.COLLECTION, collection);
		job.getConfiguration().set(TaskConfig.CP, cp);

		boolean result = task.setCurrentJob(job).waitForCompletion(LOG.isDebugEnabled());
		if (!result || task.isCancelled())
		{
			return result;
		}

		result = extractCategories();

		return result;
	}

	private boolean extractCategories() throws IOException, InterruptedException, ClassNotFoundException
	{
		// cleanup before:
		cleanup(datapath);

		// replace the jar:
		// the name looks strange, but some generic code is included in the movielens jar - it should be moved to a
		// separate jar in the future.
		setJar(Config.getInstance().get(Config.MAPRED_MOVIELENS));

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

	private Long parseDate(String date)
	{
		if (date == null)
		{
			return null;
		}

		try
		{
			return DateTimeFormat.forPattern("yyyy-MM-dd").parseMillis(date);
		}
		catch (IllegalArgumentException e)
		{
			// failed...
			LOG.error("parsing string date into millis: Failed for 'yyyy-MM-dd' format: {}", date);
		}
		try
		{
			return ISODateTimeFormat.date().parseMillis(date);
		}
		catch (IllegalArgumentException e)
		{
			LOG.error("parsing string date into millis: Failed for 'ISODateTimeFormat' format: {}", date);
		}
		return null;
	}

	private void importData(Path output) throws JAXBException, IOException, OAIException, InterruptedException
	{
		OAIImport oaiImport = new OAIImport(task, output);

		Config config = Config.getInstance();

		Long from = parseDate(config.get(Config.OAI_GLOBAL_FROM));
		Long until = parseDate(config.get(Config.OAI_GLOBAL_UNTIL));

		if (from != null)
		{
			oaiImport.setStartdate(from);
		}
		else if (startdate != null)
		{
			oaiImport.setStartdate(startdate * 1000);
		}
		if (until != null)
		{
			oaiImport.setEnddate(until);
		}
		else if (enddate != null)
		{
			oaiImport.setEnddate(enddate * 1000);
		}

		dateformat = config.get(Config.OAI_GLOBAL_FORMATOVERRIDE);
		oaiImport.setDateformat(dateformat);
		oaiImport.setLimit(limit);
		oaiImport.setWait(wait);
		oaiImport.setSets(loadsets);
		oaiImport.setSource(inputUrl);
		oaiImport.setMetadatPrefix(metadataPrefix);
		oaiImport.setFsDefaultName(config.get("fs.default.name"));

		// start import (synchronized call)
		oaiImport.start();

	}
}
