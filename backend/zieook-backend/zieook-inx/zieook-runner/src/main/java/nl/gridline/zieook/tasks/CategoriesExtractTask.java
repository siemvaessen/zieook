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
import nl.gridline.zieook.runners.movielens.CategoriesExtractTool;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * CategoriesExtractTask.java created Mar 24, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CategoriesExtractTask extends ZieOokTask
{

	@Override
	public void call() throws Exception
	{
		configuration.setStart();
		setProgress();
		setMessage(ZieOokTask.Level.INFO, "starting categories extract");
		String cp = configuration.get(TaskConfig.CP);
		if (cp == null)
		{
			throw new IOException("content provider not set, please set <" + TaskConfig.CP + ">");
		}

		CategoriesExtractTool tool = new CategoriesExtractTool(this).configure(HBaseTableConstants.COLLECTION_TABLE + cp);
		boolean result = tool.execute();

		configuration.setEnd();
		if (result)
		{
			setMessage(ZieOokTask.Level.INFO, "finished categories extract");
			setSucceed();
		}
		else
		{
			setMessage(ZieOokTask.Level.ERROR, "failed categories extract");
			setFailed();
		}

	}
}
