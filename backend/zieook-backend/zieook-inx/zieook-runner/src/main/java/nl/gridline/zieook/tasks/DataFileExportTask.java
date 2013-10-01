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

import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.runners.ZieOokRunnerTool;
import nl.gridline.zieook.runners.movielens.CollectionExportTool;
import nl.gridline.zieook.runners.movielens.RatingExportTool;
import nl.gridline.zieook.runners.movielens.UserExportTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * DataFileExportTask.java created 4 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class DataFileExportTask extends ZieOokTask
{

	private static final Logger LOG = LoggerFactory.getLogger(DataFileImportTask.class);

	public DataFileExportTask()
	{
		// no-arg constructor
	}

	@Override
	public void call() throws Exception
	{
		configuration.setStart();
		// split up in three different type:
		// - collection import
		// - rating import
		// - user data import

		// get the [generic] properties:
		String outputpath = configuration.get(TaskConfig.OUTPUT_PATH);

		String cp = configuration.get(TaskConfig.CP);
		if (cp == null)
		{
			setMessage(ZieOokTask.Level.ERROR, "content provider not set, please set <" + TaskConfig.CP + ">");
			throw new IOException("content provider not set, please set <" + TaskConfig.CP + ">");
		}
		String collection = configuration.get(TaskConfig.COLLECTION);
		if (collection == null)
		{
			setMessage(ZieOokTask.Level.ERROR, "collection not set, please set <" + TaskConfig.COLLECTION + ">");
			throw new IOException("collection not set, please set <" + TaskConfig.COLLECTION + ">");
		}

		LOG.info("Executing import task for: {} from data: {}", cp + collection, outputpath);

		ZieOokRunnerTool mapreduce = null;

		String part = configuration.get(TaskConfig.DATA_PART);

		if (TaskConfig.DATA_PART_COLLECTION.equals(part))
		{
			setMessage(ZieOokTask.Level.INFO, "setting data part collection");
			mapreduce = new CollectionExportTool(this).configure(HBaseTableConstants.COLLECTION_TABLE + cp, outputpath);
		}
		else if (TaskConfig.DATA_PART_RATINGS.equals(part))
		{
			setMessage(ZieOokTask.Level.INFO, "setting data part ratings");
			mapreduce = new RatingExportTool(this).configure(HBaseTableConstants.USERTABLE_NAME + cp, outputpath);
		}
		else if (TaskConfig.DATA_PART_USERS.equals(part))
		{
			setMessage(ZieOokTask.Level.INFO, "setting data part users");
			mapreduce = new UserExportTool(this).configure(HBaseTableConstants.USERTABLE_NAME + cp, outputpath);
		}
		else
		{
			setMessage(ZieOokTask.Level.ERROR, "import task not recognized, see server log for details: " + part);
			LOG.error("The import task is not recognized: {} - should be one of <{}>", part,
					TaskConfig.DATA_PART_COLLECTION + "," + TaskConfig.DATA_PART_RATINGS + "," + TaskConfig.DATA_PART_USERS);
		}

		setProgress();
		setMessage(ZieOokTask.Level.INFO, "starting data file export");

		// check for cancellation:
		if (configuration.isCancelled())
		{
			LOG.info("Received cancelation request");
			setMessage(ZieOokTask.Level.INFO, "user cancelled request");
			setFailed();
			return;
		}

		// create single map-reduce for import
		if (mapreduce != null)
		{
			mapreduce.set(TaskConfig.CP, cp);
			mapreduce.set(TaskConfig.COLLECTION, collection);

			LOG.info("starting map-reduce on  {} from data: {}", cp + "/" + collection, outputpath);

			// Starts the map-reduce, which may take a long time, or fail
			boolean result = mapreduce.execute();

			if (result)
			{
				setMessage(ZieOokTask.Level.INFO, "finished data export");
				setSucceed();
			}
			else
			{
				setMessage(ZieOokTask.Level.ERROR, "failed data export");
				setFailed();
			}
		}

		LOG.warn("map-reduce on {} from {} failed", cp + "/" + collection, outputpath);

		// this task is only executed once
		configuration.remove(TaskConfig.NEXT);
	}
}
