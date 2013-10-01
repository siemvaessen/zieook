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
package nl.gridline.zieook.mapreduce;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-mapred-commons<br />
 * TaskConfigTest.java created 27 apr. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class TaskConfigTest
{
	@Test
	public void TaskConfigLongTest()
	{
		TaskConfig config = new TaskConfig();
		config.setProperty("test-key", 100L);
		String result = config.get("test-key");
		assertEquals("0000000000000000100", result);
		config.setProperty("test-key", Long.MAX_VALUE);
		assertEquals(Long.toString(Long.MAX_VALUE), config.get("test-key"));
	}
}
