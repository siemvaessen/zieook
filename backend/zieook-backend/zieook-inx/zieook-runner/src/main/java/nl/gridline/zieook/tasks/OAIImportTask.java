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

import javax.xml.bind.JAXBException;

import nl.gridline.zieook.OAIException;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.runners.OaiImportTool;

import org.apache.zookeeper.KeeperException;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * CZPOAIImportTask.java created 9 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class OAIImportTask extends ZieOokTask
{

	@Override
	public void call() throws IOException, InterruptedException, OAIException, KeeperException, JAXBException,
			ClassNotFoundException
	{
		configuration.setStart();
		String inputUrl = configuration.get(TaskConfig.INPUT_PATH);

		if (inputUrl == null)
		{
			throw new IOException("the given input url is empty");
		}

		// DO NOT set the end date (amsterdam museum does not work)
		// set end-date to now:
		// if (!configuration.contains(TaskConfig.OAI_END_DATE))
		// {
		// configuration.setProperty(TaskConfig.OAI_END_DATE, System.currentTimeMillis() / 1000);
		// }

		String cp = configuration.get(TaskConfig.CP);
		if (cp == null)
		{
			setMessage(ZieOokTask.Level.ERROR, "content provider not provided please set <" + TaskConfig.CP + ">");
			throw new IOException("content provider not provided please set <" + TaskConfig.CP + ">");
		}

		String collection = configuration.get(TaskConfig.COLLECTION);
		if (collection == null)
		{
			setMessage(ZieOokTask.Level.ERROR, "collection not provided please set <" + TaskConfig.COLLECTION + ">");
			throw new IOException("collection not provided please set <" + TaskConfig.COLLECTION + ">");
		}

		OaiImportTool importTool = new OaiImportTool(this).configure(inputUrl, HBaseTableConstants.COLLECTION_TABLE + cp);
		setProgress();
		setMessage(ZieOokTask.Level.INFO, "starting oai import task");

		boolean result = importTool.execute();

		if (result)
		{
			setMessage(ZieOokTask.Level.INFO, "finished oai import task");
			setSucceed();

			// get startdate:
			String startdate = configuration.get(TaskConfig.OAI_END_DATE);
			if (startdate == null)
			{
				// if not set, use task start date:
				configuration.get(TaskConfig.TASK_START);
			}

			if (startdate != null)
			{
				// set startdate if not set:
				configuration.setProperty(TaskConfig.OAI_START_DATE, startdate);
			}
		}
		else
		{
			setMessage(ZieOokTask.Level.ERROR, "failed oai import task");
			setFailed();
		}

	}
}
