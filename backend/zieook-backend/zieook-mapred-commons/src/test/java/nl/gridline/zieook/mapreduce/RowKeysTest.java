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
 * Project zieook-data<br />
 * RowKeysTest.java created 9 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class RowKeysTest
{

	final String aStr = "a-string";
	final String bStr = "b-string";
	final String cStr = "c-string";
	final Long bLng = 100L;
	final Long cLng = 200L;
	final Long dLng = 300L;
	final Long eLng = 400L;

	@Test
	public void testStr()
	{
		// test: String
		byte[] row = RowKeys.getRowKey(aStr);
		Object[] inv = RowKeys.getStrRowKey(row);
		assertEquals(1, inv.length);
		assertEquals(aStr, inv[0]);
	}

	@Test
	public void testStrStr()
	{
		// test: String,String
		byte[] row = RowKeys.getRowKey(aStr, bStr);
		Object[] inv = RowKeys.getStrStrRowKey(row);
		assertEquals(2, inv.length);
		assertEquals(inv[0], aStr);
		assertEquals(inv[1], bStr);
	}

	@Test
	public void testStrLong()
	{
		// test: String, Long
		byte[] row = RowKeys.getRowKeyNew(aStr, bLng);
		Object[] inv = RowKeys.getStrLngRowKey(row);
		assertEquals(2, inv.length);
		assertEquals(aStr, inv[0]);
		assertEquals(bLng, inv[1]);
	}

	@Test
	public void testStrStrLong()
	{
		// test: String, String, Long
		byte[] row = RowKeys.getRowKey(aStr, bStr, cLng);
		Object[] inv = RowKeys.getStrStrLng(row);
		assertEquals(3, inv.length);
		assertEquals(aStr, inv[0]);
		assertEquals(bStr, inv[1]);
		assertEquals(cLng, inv[2]);
	}

	@Test
	public void testStrLngLng()
	{
		// test: String, Long, Long
		byte[] row = RowKeys.getRowKey(aStr, bLng, cLng);
		Object[] inv = RowKeys.getStrLngLng(row);
		assertEquals(3, inv.length);
		assertEquals(aStr, inv[0]);
		assertEquals(bLng, inv[1]);
		assertEquals(cLng, inv[2]);
	}

	@Test
	public void testStrStrLngLng()
	{
		// test: String, String, Long, Long
		byte[] row = RowKeys.getRowKey(aStr, bStr, cLng, dLng);
		Object[] inv = RowKeys.getStrStrLngLng(row);
		assertEquals(4, inv.length);
		assertEquals(aStr, inv[0]);
		assertEquals(bStr, inv[1]);
		assertEquals(cLng, inv[2]);
		assertEquals(dLng, inv[3]);
	}

	@Test
	public void testStrStrStrLng()
	{
		byte[] row = RowKeys.getRowKey(aStr, bStr, cStr, dLng);
		Object[] inv = RowKeys.getStrStrStrLng(row);
		assertEquals(4, inv.length);
		assertEquals(aStr, inv[0]);
		assertEquals(bStr, inv[1]);
		assertEquals(cStr, inv[2]);
		assertEquals(dLng, inv[3]);
	}

	@Test
	public void testStrStrLngLngLng()
	{
		byte[] row = RowKeys.getRowKey(aStr, bStr, cLng, dLng, eLng);
		Object[] inv = RowKeys.getStrStrLngLngLng(row);
		assertEquals(5, inv.length);
		assertEquals(aStr, inv[0]);
		assertEquals(bStr, inv[1]);
		assertEquals(cLng, inv[2]);
		assertEquals(dLng, inv[3]);
		assertEquals(eLng, inv[4]);
	}

	@Test
	public void testStrLngStrLngLng()
	{
		byte[] row = RowKeys.getRowKey(aStr, bLng, cStr, dLng, eLng);
		Object[] inv = RowKeys.getStrLngStrLngLng(row);
		assertEquals(5, inv.length);
		assertEquals(aStr, inv[0]);
		assertEquals(bLng, inv[1]);
		assertEquals(cStr, inv[2]);
		assertEquals(dLng, inv[3]);
		assertEquals(eLng, inv[4]);
	}

	@Test
	public void testStrLngStrLng()
	{
		byte[] row = RowKeys.getRowKey(aStr, bLng, cStr, dLng);
		Object[] inv = RowKeys.getStrLngStrLng(row);
		assertEquals(4, inv.length);
		assertEquals(aStr, inv[0]);
		assertEquals(bLng, inv[1]);
		assertEquals(cStr, inv[2]);
		assertEquals(dLng, inv[3]);
	}

	@Test
	public void testStrLngStr()
	{
		byte[] row = RowKeys.getRowKey(aStr, bLng, cStr);
		Object[] inv = RowKeys.getStrLngStr(row);
		assertEquals(3, inv.length);
		assertEquals(aStr, inv[0]);
		assertEquals(bLng, inv[1]);
		assertEquals(cStr, inv[2]);
	}
}
