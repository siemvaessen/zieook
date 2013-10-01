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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBaseManagerTest
{
	private static final Logger LOG = LoggerFactory.getLogger(HBaseManagerTest.class);

	private HBaseManager manager;

	private static final String testtable = "TEST_ZIEOOK_HBASEMANAGER_TESTTABLE_1";
	private static final String testtable2 = "TEST_ZIEOOK_HBASEMANAGER_TESTTABLE_2";
	private static final byte[] testc1 = Bytes.toBytes("test_column1");
	private static final byte[] testc2 = Bytes.toBytes("test_column2");

	private static final String TEST_TABLE = "ZIEOOK_HBASE_COLUMN_TEST_TABLE";
	private static final String TEST_COLUMN_1 = "ZIEOOK_HBASE_TEST_COLUMN_1";
	private static final String TEST_COLUMN_2 = "ZIEOOK_HBASE_TEST_COLUMN_ADD";

	@Before
	public void setUp() throws Exception
	{
		manager = HBaseManager.getInstance("mulisch");
	}

	@After
	public void tearDown() throws Exception
	{
		// manager.shutdown();

		if (manager.exists(testtable))
		{
			manager.disableTable(testtable);
			manager.dropTable(testtable);
		}

		if (manager.exists(testtable2))
		{
			manager.disableTable(testtable2);
			manager.dropTable(testtable2);
		}
		if (manager.exists("TESTTABLE"))
		{
			manager.disableTable("TESTTABLE");
			manager.dropTable("TESTTABLE");
		}
		if (manager.exists(TEST_TABLE))
		{
			manager.disableTable(TEST_TABLE);
			manager.dropTable(TEST_TABLE);
		}

	}

	@Ignore
	private void createTable2() throws IOException
	{
		LOG.info("creating table");
		HTableDescriptor testTable = new HTableDescriptor(testtable2);
		HColumnDescriptor c1 = new HColumnDescriptor(testc1);
		HColumnDescriptor c2 = new HColumnDescriptor(testc2);
		testTable.addFamily(c1);
		testTable.addFamily(c2);

		manager.createTable(testTable);

	}

	@Ignore
	private void dropTable2() throws IOException
	{

		manager.disableTable(testtable2);
		manager.dropTable(testtable2);

	}

	@Test
	/**
	 * A simple create / drop table tests, this will fail if there are any connection or configuration issues.
	 */
	public final void testCreateDropTable() throws IOException
	{
		// table descriptor:
		HTableDescriptor testTable = new HTableDescriptor(testtable);
		HColumnDescriptor c1 = new HColumnDescriptor(testc1);
		HColumnDescriptor c2 = new HColumnDescriptor(testc2);
		testTable.addFamily(c1);
		testTable.addFamily(c2);

		if (!manager.exists(testtable))
		{
			manager.createTable(testTable);
		}
		else
		{
			LOG.warn("Warning: the table already existed, only test drop!");
		}

		manager.disableTable(testtable);
		manager.dropTable(testtable);

	}

	@Test
	public final void testPutGetMany2() throws IOException
	{
		createTable2();

		LOG.info("putting 1000 rows with 200 columns in 2 families");
		long start = System.currentTimeMillis();
		try
		{
			HTableInterface table = manager.getTable(testtable2);
			try
			{
				for (int i = 0; i < 5000; i++)
				{
					if (i % 200 == 0)
					{
						LOG.info("current row {}", i);
					}

					// create put:
					Put put = new Put(Bytes.toBytes(i));
					for (int j = 0; j < 100; j++)
					{
						put.add(testc1, Bytes.toBytes("q" + j), Bytes.toBytes(j));
					}
					for (int j = 100; j < 200; j++)
					{
						put.add(testc2, Bytes.toBytes("q2" + j), Bytes.toBytes(j));
					}
					table.put(put);
				}
			}
			finally
			{
				manager.putTable(table);

			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}

		LOG.info("putget2: done in " + (System.currentTimeMillis() - start) + "ms");

		LOG.info("getting 1000 rows with 200 columns in 2 families");
		start = System.currentTimeMillis();

		try
		{
			HTableInterface table = manager.getTable(testtable2);
			try
			{
				for (int i = 0; i < 5000; i++)
				{
					if (i % 100 == 0)
					{
						LOG.info("current row {}", i);
					}
					Get get = new Get(Bytes.toBytes(i));
					Result result = table.get(get);
					if (result.isEmpty())
					{
						fail("could not retrieve result: " + i);
					}
				}
			}
			finally
			{
				manager.putTable(table);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail("failed to write data");
		}

		LOG.info("putget2: done in " + (System.currentTimeMillis() - start) + "ms");

		dropTable2();
	}

	@Test
	public final void testPutGetMany1() throws IOException
	{
		createTable2();
		LOG.info("putting 1000 rows with 200 collumns in 2 families");
		long start = System.currentTimeMillis();
		for (int i = 0; i < 5000; i++)
		{
			if (i % 100 == 0)
			{
				LOG.info("current row {}", i);
			}

			// create put:
			Put put = new Put(Bytes.toBytes(i));
			for (int j = 0; j < 100; j++)
			{
				put.add(testc1, Bytes.toBytes("q" + j), Bytes.toBytes(j));
			}
			for (int j = 100; j < 200; j++)
			{
				put.add(testc2, Bytes.toBytes("q2" + j), Bytes.toBytes(j));
			}

			try
			{
				HTableInterface table = manager.getTable(testtable2);
				try
				{
					table.put(put);
				}
				finally
				{
					manager.putTable(table);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
		LOG.info("putget1: done in " + (System.currentTimeMillis() - start) + "ms");

		LOG.info("getting 1000 rows with 200 columns in 2 families");
		start = System.currentTimeMillis();
		for (int i = 0; i < 5000; i++)
		{
			if (i % 200 == 0)
			{
				LOG.info("current row {}", i);
			}
			Get get = new Get(Bytes.toBytes(i));
			try
			{
				HTableInterface table = manager.getTable(testtable2);
				try
				{
					Result result = table.get(get);
					if (result.isEmpty())
					{
						fail("could not retrieve result: " + i);
					}
				}
				finally
				{
					manager.putTable(table);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				fail("failed to write data");
			}
		}
		LOG.info("putget1: done in " + (System.currentTimeMillis() - start) + "ms");

		dropTable2();
	}

	@Test
	public final void testPutGet()
	{
		// prepare a table:
		HTableDescriptor testTable = new HTableDescriptor(testtable2);
		HColumnDescriptor c1 = new HColumnDescriptor(testc1);
		HColumnDescriptor c2 = new HColumnDescriptor(testc2);
		testTable.addFamily(c1);
		testTable.addFamily(c2);

		// prepare a put:
		byte[] testrow = Bytes.toBytes("testrow");
		byte[] q1 = Bytes.toBytes("qualifier-1");
		byte[] q2 = Bytes.toBytes("qualifier-2");
		byte[] v1 = Bytes.toBytes("value-1");
		byte[] v2 = Bytes.toBytes("value-2");
		Put put = new Put(testrow);
		put.add(testc1, q1, v1);
		put.add(testc2, q2, v2);

		// prepare a get:
		Get get = new Get(testrow);

		// create a table:
		try
		{

			manager.createTable(testTable);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail("failed to create table");
		}

		HTableInterface table = null;
		try
		{
			table = manager.getTable(testtable2);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail("failed to get table");
		}

		// put data:
		try
		{
			table.put(put);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail("failed to put data");
		}

		// get data:
		Result result = null;
		try
		{
			result = table.get(get);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail("failed to write data");
		}

		// test results:
		assertEquals(Bytes.toString(v1), Bytes.toString(result.getValue(testc1, q1)));
		assertEquals(Bytes.toString(v2), Bytes.toString(result.getValue(testc2, q2)));

		// droptable:
		try
		{
			manager.disableTable(testtable2);
			manager.dropTable(testtable2);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail("failed to drop table");
		}

	}

	@Test
	public void updateTableTestAddColumn() throws IOException
	{
		// create a discriptor:
		// table descriptor:

		// create single column table:
		HTableDescriptor descriptor = new HTableDescriptor(TEST_TABLE);
		descriptor.addFamily(new HColumnDescriptor(TEST_COLUMN_1));
		manager.createTable(descriptor);

		// test table:
		HTableDescriptor existingDescriptor = manager.getTable(TEST_TABLE).getTableDescriptor();
		assertNotNull(existingDescriptor.getFamily(Bytes.toBytes(TEST_COLUMN_1)));
		assertNull(existingDescriptor.getFamily(Bytes.toBytes(TEST_COLUMN_2)));

		// create new descriptor:
		HTableDescriptor updates = new HTableDescriptor(TEST_TABLE);
		updates.addFamily(new HColumnDescriptor(TEST_COLUMN_1));
		updates.addFamily(new HColumnDescriptor(TEST_COLUMN_2));

		// update table:
		manager.disableTable(TEST_TABLE);
		manager.updateTable(updates);
		manager.enableTable(TEST_TABLE);

		// test if column where added
		HTableInterface table = manager.getTable(TEST_TABLE);
		HTableDescriptor newdescriptor = table.getTableDescriptor();
		assertNotNull(newdescriptor.getFamily(Bytes.toBytes(TEST_COLUMN_1)));
		assertNotNull(newdescriptor.getFamily(Bytes.toBytes(TEST_COLUMN_2)));

	}

	@Test
	public void updateTableTestRemoveColumn() throws IOException
	{
		// create a discriptor:
		// table descriptor:

		// create two column table:
		HTableDescriptor descriptor = new HTableDescriptor(TEST_TABLE);
		descriptor.addFamily(new HColumnDescriptor(TEST_COLUMN_1));
		descriptor.addFamily(new HColumnDescriptor(TEST_COLUMN_2));
		manager.createTable(descriptor);

		// test table:
		HTableDescriptor existingDescriptor = manager.getTable(TEST_TABLE).getTableDescriptor();
		assertNotNull(existingDescriptor.getFamily(Bytes.toBytes(TEST_COLUMN_1)));
		assertNotNull(existingDescriptor.getFamily(Bytes.toBytes(TEST_COLUMN_1)));

		// create new descriptor (one column)
		HTableDescriptor updates = new HTableDescriptor(TEST_TABLE);
		updates.addFamily(new HColumnDescriptor(TEST_COLUMN_1));

		// update table:
		manager.disableTable(TEST_TABLE);
		manager.updateTable(updates);
		manager.enableTable(TEST_TABLE);

		// test results:
		HTableInterface table = manager.getTable(TEST_TABLE);
		HTableDescriptor newdescriptor = table.getTableDescriptor();
		assertNotNull(newdescriptor.getFamily(Bytes.toBytes(TEST_COLUMN_1)));
		assertNull(newdescriptor.getFamily(Bytes.toBytes(TEST_COLUMN_2)));

	}
}
