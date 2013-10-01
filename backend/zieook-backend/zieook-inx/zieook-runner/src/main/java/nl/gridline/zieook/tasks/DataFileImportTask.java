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
import nl.gridline.zieook.runners.movielens.CollectionImportTool;
import nl.gridline.zieook.runners.movielens.RatingImportTool;
import nl.gridline.zieook.runners.movielens.UsersImportTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Imports data from an HDFS file location into HBase - this is mostly designed for the MovieLens format and involves
 * several map-reduce steps
 * <p />
 * Project zieook-runner<br />
 * DataUploadTask.java created 16 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class DataFileImportTask extends ZieOokTask
{

	private static final Logger LOG = LoggerFactory.getLogger(DataFileImportTask.class);

	public DataFileImportTask()
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
		String inputpath = configuration.get(TaskConfig.INPUT_PATH);

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

		LOG.info("Executing import task for: {} from data: {}", cp + collection, inputpath);

		ZieOokRunnerTool mapreduce = null;

		String part = configuration.get(TaskConfig.DATA_PART);

		if (TaskConfig.DATA_PART_COLLECTION.equals(part))
		{
			setMessage(ZieOokTask.Level.INFO, "setting data part collection");
			mapreduce = new CollectionImportTool(this).configure(HBaseTableConstants.COLLECTION_TABLE + cp, inputpath);
		}
		else if (TaskConfig.DATA_PART_RATINGS.equals(part))
		{
			setMessage(ZieOokTask.Level.INFO, "setting data part ratings");
			mapreduce = new RatingImportTool(this).configure(HBaseTableConstants.USERTABLE_NAME + cp, inputpath);
		}
		else if (TaskConfig.DATA_PART_USERS.equals(part))
		{
			setMessage(ZieOokTask.Level.INFO, "setting data part users");
			mapreduce = new UsersImportTool(this).configure(HBaseTableConstants.USERTABLE_NAME + cp, inputpath);
		}
		else
		{
			setMessage(ZieOokTask.Level.ERROR, "import task not recognized, see server log for details: " + part);
			LOG.error("The import task is not recognized: {} - should be one of <{}>", part,
					TaskConfig.DATA_PART_COLLECTION + "," + TaskConfig.DATA_PART_RATINGS + "," + TaskConfig.DATA_PART_USERS);
		}

		setProgress();
		setMessage(ZieOokTask.Level.INFO, "starting data file import");

		// create single map-reduce for import
		if (mapreduce != null)
		{
			mapreduce.set(TaskConfig.CP, cp);
			mapreduce.set(TaskConfig.COLLECTION, collection);

			LOG.info("starting map-reduce on  {} from data: {}", cp + "/" + collection, inputpath);

			// Starts the map-reduce, which may take a long time, or fail
			boolean result = mapreduce.execute();
			if (result)
			{
				setMessage(ZieOokTask.Level.INFO, "finished data file import");
				setSucceed();
			}
			else
			{
				setMessage(ZieOokTask.Level.ERROR, "failed data file import");
				setFailed();
			}
		}
		else
		{
			LOG.error("map-reduce on '{}' from '{}' failed", cp + "/" + collection, inputpath);
		}

		configuration.setEnd();
	}
}
