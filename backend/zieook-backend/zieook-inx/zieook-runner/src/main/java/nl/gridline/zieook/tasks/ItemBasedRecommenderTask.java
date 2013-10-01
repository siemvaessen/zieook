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

import java.io.IOException;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.runners.recommender.DataPrepareTool;
import nl.gridline.zieook.runners.recommender.ItemBasedRecommenderTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes a complete Item based recommender based on different settings:
 * <p />
 * Project zieook-runner<br />
 * ItemBasedRecommenderTask.java created 3 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class ItemBasedRecommenderTask extends ZieOokTask
{
	private static final Logger LOG = LoggerFactory.getLogger(ItemBasedRecommenderTask.class);

	@Override
	public void call() throws Exception
	{
		// setup:
		configuration.setStart();

		String cp = configuration.get(TaskConfig.CP);
		if (cp == null)
		{
			setMessage(ZieOokTask.Level.ERROR, "collection <" + TaskConfig.COLLECTION + "> not set in task");
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
			setMessage(ZieOokTask.Level.ERROR, "collection <" + TaskConfig.COLLECTION + "> not set in task");
			throw new IOException("recommender <" + TaskConfig.RECOMMENDER + "> not set in task");
		}

		String outputTable = configuration.get(TaskConfig.OUTPUT_TABLE);
		if (outputTable == null)
		{
			setMessage(ZieOokTask.Level.ERROR, "collection <" + TaskConfig.COLLECTION + "> not set in task");
			throw new IOException("output path <" + TaskConfig.OUTPUT_PATH + "> not set in task");
		}

		// - read data from HBase and put it into the task 'input' directory.
		boolean skipPrepare = configuration.getBoolean("skipPrepare", false);
		boolean result = true;
		String intermediate; // intermediate output

		String root = Config.getInstance().get(Config.ZIEOOK_HDFS_PATH);

		setProgress();
		setMessage(ZieOokTask.Level.INFO, "task initialized");

		// check for cancel, before we really start!
		if (isCancelled())
		{
			setMessage(ZieOokTask.Level.INFO, "task cancelled from outside");
			setFailed();
			return;
		}

		if (skipPrepare)
		{
			setProgress();
			setMessage(ZieOokTask.Level.INFO, "item based recommender prepare skipped");
			// if the prepare is skipped, we need an input
			intermediate = configuration.get(TaskConfig.INPUT_PATH);
			if (intermediate == null)
			{
				throw new IOException("prepare is being skipped, but no input data is defined set <"
						+ TaskConfig.INPUT_PATH + ">");
			}

			LOG.info("prepare of data is skipped, assuming data is available in '{}' writing data to: {}", intermediate,
					outputTable);
		}
		else
		{
			setProgress();
			setMessage(ZieOokTask.Level.INFO, "data prepare start");

			String inputtable = configuration.get(TaskConfig.INPUT_TABLE);
			if (inputtable == null)
			{
				throw new IOException("input table <" + TaskConfig.INPUT_TABLE + "> not set in task");
			}

			intermediate = root + "/" + cp + "/" + collection + "/" + recommender + "/input";

			LOG.info("reading from {} writing to {})", inputtable, outputTable);

			DataPrepareTool prepare = new DataPrepareTool(this);
			prepare.configure(inputtable, HBaseTableConstants.COLLECTION_TABLE + cp, intermediate);
			prepare.set(TaskConfig.COLLECTION, collection);
			prepare.set(TaskConfig.CP, cp);
			// execute
			result = prepare.execute();
			if (!result)
			{
				setMessage(ZieOokTask.Level.ERROR, "data prepare failed");
				setFailed();
				return;
			}

			if (isCancelled())
			{
				setMessage(ZieOokTask.Level.INFO, "task cancelled from outside");
				setFailed();
				return;
			}
			setMessage(ZieOokTask.Level.INFO, "data prepare finished");
		}

		// - create a RecommenderJob & execute
		setProgress();
		setMessage(ZieOokTask.Level.INFO, "starting itembased recommender");
		ItemBasedRecommenderTool recommenderTool = new ItemBasedRecommenderTool(this)
				.configure(intermediate, outputTable);

		// execute:
		result = recommenderTool.execute();

		if (!result)
		{
			setMessage(ZieOokTask.Level.ERROR, "failed itembased recommender");
			setFailed();
		}
		else
		{
			setMessage(ZieOokTask.Level.INFO, "finished itembased recommender");
			setSucceed();
		}
	}
}
