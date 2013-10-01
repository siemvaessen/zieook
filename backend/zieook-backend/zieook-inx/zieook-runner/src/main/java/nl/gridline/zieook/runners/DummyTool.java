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

import nl.gridline.zieook.tasks.ZieOokTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An empty task, that will only sleep for a given amount of seconds - this is only used for testing.
 * <p />
 * Project zieook-runner<br />
 * DummyTool.java created 23 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class DummyTool extends ZieOokRunnerTool
{

	private static final Logger LOG = LoggerFactory.getLogger(DummyTool.class);

	private long sleep;

	/**
	 * @param task
	 */
	public DummyTool(ZieOokTask task)
	{
		super(task);
	}

	/**
	 * @param sleep timing in seconds
	 * @return
	 * @throws IOException
	 */
	public DummyTool configure(long sleep) throws IOException
	{
		setBasics();

		LOG.info("The dummy tool <{}> will be sleeping for  {} seconds", task.getConfig().getId(), sleep);
		this.sleep = sleep;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.runners.ZieOokRunnerTool#execute()
	 */
	@Override
	public boolean execute() throws Exception
	{
		LOG.info("running dummy task <{}> will be sleeping for {} seconds", task.getId(), sleep);

		// check every second for interruption, this is useful when testing cancelation.
		for (int i = 0; i < sleep; i++)
		{
			Thread.sleep(1000);
			if (task.isCancelled())
			{
				LOG.info("Task {} recieved a cancel request", task.getId());
				return false; // task failed / interrupted
			}
			LOG.debug("still sleeping: {} seconds", (sleep - i));

		}

		LOG.info("dummy task slept for {} seconds, and is now done", sleep);
		return true;
	}
}
