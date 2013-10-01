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
package nl.gridline.zieook.workflow.model;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-api-workflow<br />
 * LongToBytesFallBack.java created 22 jun. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class LongToBytesFallbackTest
{

	@Test
	public void testLong()
	{
		Map<byte[], byte[]> map = new HashMap<byte[], byte[]>(1);
		long time = System.currentTimeMillis();
		ModelConstants.putStamp(map, time);
		assertEquals(time, ModelConstants.getStamp(map));

		// the old way:
		map = new HashMap<byte[], byte[]>(1);
		ModelConstants.putLong(map, ModelConstants.TIMESTAMP, time);
		assertEquals(time, ModelConstants.getStamp(map));
	}
}