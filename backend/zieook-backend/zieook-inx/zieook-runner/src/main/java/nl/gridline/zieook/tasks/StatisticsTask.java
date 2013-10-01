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

import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.runners.statistics.StatisticsTool;

/**
 * This is the task that creates and runs a RecommendationPrepareTool.
 * <p />
 * Project zieook-runner<br />
 * RecommendationViewTask.java created 16 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:daryl@gridline.nl">Daryl</a>
 * @version $Revision$, $Date$
 */
public class StatisticsTask extends ZieOokTask
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.tasks.ZieOokTask#call()
	 */
	@Override
	public void call() throws Exception
	{
		configuration.setStart();

		String eventLogTable = configuration.get(TaskConfig.STATS_EVENTLOG_TABLE);
		String userTable = configuration.get(TaskConfig.STATS_USER_TABLE);
		String outputTable = configuration.get(TaskConfig.OUTPUT_TABLE);

		StatisticsTool tool = new StatisticsTool(this).configure(eventLogTable, userTable, outputTable);
		setProgress();
		setMessage(ZieOokTask.Level.INFO, "starting statistics calculation task");

		boolean success = tool.execute();

		if (success)
		{
			setMessage(ZieOokTask.Level.INFO, "finished statistics calculation task");
			setSucceed();
		}
		else
		{
			setMessage(ZieOokTask.Level.ERROR, "failed statistics calculation task");
			setFailed();
		}
	}

}
