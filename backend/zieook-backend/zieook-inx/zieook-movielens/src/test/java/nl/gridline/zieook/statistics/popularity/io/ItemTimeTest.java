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
package nl.gridline.zieook.statistics.popularity.io;

import static org.junit.Assert.assertTrue;

import nl.gridline.zieook.statistics.popularity.io.ItemTime;

import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * RatedItemTest.java created 9 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class ItemTimeTest
{

	@Test
	public void testOrder()
	{
		ItemTime ri1 = new ItemTime(1, "c", 0);
		ItemTime ri2 = new ItemTime(2, "c", 0);

		ItemTime ri3 = new ItemTime(1, "c", 1);
		ItemTime ri4 = new ItemTime(2, "c", 1);

		assertTrue(ri2.compareTo(ri1) > 0);

		assertTrue(ri3.compareTo(ri2) > 0);
		assertTrue(ri3.compareTo(ri1) > 0);

		assertTrue(ri4.compareTo(ri3) > 0);
		assertTrue(ri4.compareTo(ri2) > 0);
		assertTrue(ri4.compareTo(ri1) > 0);
	}
}
