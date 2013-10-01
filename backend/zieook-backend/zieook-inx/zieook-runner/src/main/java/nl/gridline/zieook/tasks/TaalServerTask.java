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
package nl.gridline.zieook.tasks;

import java.io.File;
import java.io.IOException;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.runners.ZieOokRunnerTool;
import nl.gridline.zieook.runners.recommender.ItemBasedRecommenderTool;
import nl.gridline.zieook.runners.taalserver.FreeTextVectorizeTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes a recommender based on text analysis, depending on the setup it will use gridline's advanced linguistics or
 * a simple version.
 * <p />
 * Project hadoop-taalserver<br />
 * TaalServerTask.java created 21 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class TaalServerTask extends ZieOokTask
{

	private static final Logger LOG = LoggerFactory.getLogger(TaalServerTask.class);

	@Override
	public void call() throws Exception
	{
		configuration.setStart(); // set start time:

		// check input data:
		String cp = configuration.get(TaskConfig.CP);
		if (cp == null)
		{
			setMessage(ZieOokTask.Level.ERROR, "content provider <" + TaskConfig.CP + "> not set in task");
			throw new IOException("content provider <" + TaskConfig.CP + "> not set in task");
		}

		String collection = configuration.get(TaskConfig.COLLECTION);
		if (collection == null)
		{
			setMessage(ZieOokTask.Level.ERROR, "collection <" + TaskConfig.COLLECTION + "> not set in task");
			throw new IOException("collection <" + TaskConfig.COLLECTION + "> not set in task");
		}

		String recommender = configuration.get(TaskConfig.RECOMMENDER);
		if (recommender == null)
		{
			setMessage(ZieOokTask.Level.ERROR, "recommender <" + TaskConfig.RECOMMENDER + "> not set in task");
			throw new IOException("recommender <" + TaskConfig.RECOMMENDER + "> not set in task");
		}

		// write data to:
		String outputTable = configuration.get(TaskConfig.OUTPUT_TABLE);
		if (outputTable == null)
		{
			setMessage(ZieOokTask.Level.ERROR, "output table <" + TaskConfig.OUTPUT_TABLE + "> not set in task");
			throw new IOException("output table <" + TaskConfig.OUTPUT_TABLE + "> not set in task");
		}

		boolean result = true;
		boolean skipPrepare = configuration.getBoolean("skipPrepare", false);

		setProgress();
		setMessage(ZieOokTask.Level.INFO, "prepared taalserver task");

		String intermediate;
		String root = Config.getInstance().get(Config.ZIEOOK_HDFS_PATH);
		if (skipPrepare)
		{
			// we assume that the input data is already provided:
			intermediate = configuration.get(TaskConfig.INPUT_PATH);
			if (intermediate == null)
			{
				throw new IOException("input path <" + TaskConfig.INPUT_PATH + "> not set in task");
			}
			LOG.info("prepare of data is skipped, assuming data is available in '{}' - writing to {}", intermediate,
					outputTable);
			setProgress();
			setMessage(ZieOokTask.Level.INFO, "skipped data prepare");
		}
		else
		{
			setProgress();
			setMessage(ZieOokTask.Level.INFO, "starting data prepare");
			// read data from:
			String inputTable = configuration.get(TaskConfig.INPUT_TABLE);
			if (inputTable == null)
			{
				setMessage(ZieOokTask.Level.ERROR, "output table <" + TaskConfig.INPUT_TABLE + "> not set in task");
				throw new IOException("output table <" + TaskConfig.INPUT_TABLE + "> not set in task");
			}

			LOG.info("reading data from: {} writing data to: {}", inputTable, outputTable);

			intermediate = root + "/" + cp + "/" + collection + "/" + recommender + "/input";
			// creates the input data for an item based (or userbased?) recommender:

			// We have to decide which version of the linguistics we are going to use.
			// depending on a configuration setting:
			ZieOokRunnerTool tool;
			boolean taalserver = configuration.getBoolean(TaskConfig.RECOMMENDER_TAALSERVER, false);

			if (taalserver && canUseTaalServer())
			{
				setProgress();
				setMessage(ZieOokTask.Level.INFO, "starting data prepare: with taalserver");
				setFailed();
				LOG.error("this part of the code is not open source, check with GridLine: info@gridline.nl");
				// tool = new TextVectorizeTool(this).configure(inputTable, intermediate);
				return;
			}
			else
			{
				setProgress();
				setMessage(ZieOokTask.Level.INFO, "starting data prepare: with free-taalserver");
				tool = new FreeTextVectorizeTool(this).configure(inputTable, intermediate);
			}

			result = tool.execute();

			if (!result)
			{
				setMessage(ZieOokTask.Level.ERROR, "failed taalserver task");
				setFailed();
				return;
			}
			else
			{
				setMessage(ZieOokTask.Level.INFO, "finished taalserver task");
			}

			if (configuration.isCancelled())
			{
				// process cancelled
				setMessage(ZieOokTask.Level.INFO, "user cancelled taalserver task");
				setFailed();
				return;
			}
		}

		setProgress();
		setMessage(ZieOokTask.Level.INFO, "starting item-based recommender");
		// data is now prepared, or was available, now execute a normal itembased recommender:
		ItemBasedRecommenderTool itembased = new ItemBasedRecommenderTool(this).configure(intermediate, outputTable);

		result = itembased.execute();

		if (result)
		{
			setMessage(ZieOokTask.Level.INFO, "finished item-based recommender");
			setSucceed();
		}
		else
		{
			setMessage(ZieOokTask.Level.ERROR, "failed item-based recommender");
			setFailed();
		}

	}

	/**
	 * Get the taalserver jar from the configuration, depending on the availability and/or setting
	 * @return a path to a mapreduce jar.
	 */
	public static boolean canUseTaalServer()
	{
		final Config config = Config.getInstance();
		boolean result = config.getBoolean(Config.USE_TAALSERVER, false);
		LOG.debug("can use taalserver? : {}", result);
		if (result)
		{
			// check if taalserver jar is available:
			String taalserver = config.get(Config.MAPRED_TAALSERVER);
			if (taalserver != null)
			{
				try
				{
					result = new File(taalserver.trim()).getCanonicalFile().exists();
					LOG.debug("taalserver present? : {}", result);
				}
				catch (IOException e)
				{
					LOG.error("failed to determine taalserver presence", e);
					result = false;
				}

			}
		}
		// use the open version of taalserver:
		return result;
	}
}
