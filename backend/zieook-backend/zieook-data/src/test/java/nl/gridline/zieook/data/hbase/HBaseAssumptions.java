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
package nl.gridline.zieook.data.hbase;

import static org.junit.Assert.assertTrue;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

/**
 * This class tests a few HBase assumptions that may be critical for the functioning of the program
 * <p />
 * Project zieook-data<br />
 * HBaseAssumptions.java created 20 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class HBaseAssumptions
{

	@Test
	public void HBaseClientResultTest()
	{
		// create an empty result:
		Result result = new Result();

		// newly created results should be empty:
		assertTrue(result.isEmpty());
	}

	@Test
	public void HBaseBytes()
	{
		assertTrue(Bytes.toString(null) == null);
	}

	@Test
	public void timeStampCompare()
	{
		long start = Long.MIN_VALUE;
		long now = System.currentTimeMillis();

		byte[] nowb = Bytes.toBytes(now);

		// 0 - abs(start) > 0
		BinaryComparator startcmp1 = new BinaryComparator(Bytes.toBytes(0L));
		BinaryComparator startcmp2 = new BinaryComparator(Bytes.toBytes(Math.abs(start)));

		assertTrue(startcmp1.compareTo(nowb) < 0);
		assertTrue(startcmp2.compareTo(nowb) > 0);

	}

	@Test
	public void compareDoubles()
	{
		final double low = 0.0;
		final double high = 4.0;

		BinaryComparator lowB = new BinaryComparator(Bytes.toBytes(low));
		BinaryComparator highB = new BinaryComparator(Bytes.toBytes(high));

		assertTrue(lowB.compareTo(Bytes.toBytes(high)) < 0);
		assertTrue(highB.compareTo(Bytes.toBytes(low)) > 0);

	}

	@Test
	public void compareTest()
	{
		long low = -9223372036854775808L;
		long date = -9223372036854775808L;
		long high = 9223372036854775807L;

		assertTrue(low >= date);
		assertTrue(date < high);

	}

	@Test
	public void BinaryCompare()
	{
		assertTrue(Bytes.compareTo(Bytes.toBytes(Long.MIN_VALUE), Bytes.toBytes(Long.MAX_VALUE)) > 0);

		assertTrue(Bytes.compareTo(Bytes.toBytes(Long.MAX_VALUE), Bytes.toBytes(Long.MAX_VALUE)) == 0);
		for (long i = 1; i < 100000000; i++)
		{
			if (i % 1000 == 0)
			{
				// System.out.println("compare i=" + i);
			}
			assertTrue(Bytes.compareTo(Bytes.toBytes(100000000), Bytes.toBytes(i)) > 0);
		}
		for (long i = 1; i < 100000000; i++)
		{
			if (i % 1000 == 0)
			{
				// System.out.println("compare i=" + i);
			}
			assertTrue(Bytes.compareTo(Bytes.toBytes(i), Bytes.toBytes(100000000)) < 0);
		}

		// int result2 = Bytes.compareTo(Bytes.toBytes(time), Bytes.toBytes(end));
		// int result3 = Bytes.compareTo(Bytes.toBytes(time), Bytes.toBytes(time));
		// System.out.println(result1 + " " + result2 + " " + result3);
		// 1303467965
	}
}
