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

import nl.gridline.zieook.runners.DummyTool;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * DummyTask.java created 23 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class DummyTask extends ZieOokTask
{

	public DummyTask()
	{

	}

	@Override
	public void call() throws Exception
	{
		configuration.setStart();
		DummyTool tool = new DummyTool(this);
		// abuse interval for runtime:
		tool.configure(Long.parseLong(configuration.get("sleep-time")));
		tool.execute(); // skip the result, this task is always successful, for now, but can be in cancelled state

		setSucceed();

	}

}
