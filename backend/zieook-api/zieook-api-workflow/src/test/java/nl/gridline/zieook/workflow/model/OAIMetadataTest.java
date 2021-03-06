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

import java.util.Arrays;
import java.util.NavigableMap;

import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-api-workflow<br />
 * OAIMetadataTest.java created 20 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class OAIMetadataTest
{

	@Test
	public void mappingTest()
	{
		OAIMetadata metadata = new OAIMetadata(Arrays.asList("test1", "test2", "test3"), "test1");
		NavigableMap<byte[], byte[]> map = metadata.toMap();

		OAIMetadata newmetadata = new OAIMetadata(map);
		assertEquals(metadata, newmetadata);
	}

}
