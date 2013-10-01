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
package nl.gridline.zieook.data.hbase.dao;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.workflow.model.ContentProvider;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * ContentProviderTable.java created 10 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class ContentProviderTable
{

	private static final Logger LOG = LoggerFactory.getLogger(ContentProviderTable.class);

	private static final byte[] COLUMN_CONTENTPROVIDER = Bytes
			.toBytes(HBaseTableConstants.CONFIGURATION_COLUMN_CONTENTPROVIDER);
	private static final String CONTENT_PROVIDER = "cp_";
	private static final byte[] CONTENT_PRIVIDER_BYTES = Bytes.toBytes(CONTENT_PROVIDER);

	private final HBaseConfigurationTable table;

	public ContentProviderTable(HBaseConfigurationTable table)
	{
		this.table = table;
	}

	private static byte[] getKey(ContentProvider cp)
	{
		return getKey(cp.getName());
	}

	private static byte[] getKey(String name)
	{
		return Bytes.toBytes(CONTENT_PROVIDER + name);
	}

	public ContentProvider getContentProvider(String cp)
	{
		ContentProvider cpresult = new ContentProvider();
		byte[] row = getKey(cp);
		LOG.debug("reading row: {}", Bytes.toString(row));
		Get get = new Get(row).addFamily(COLUMN_CONTENTPROVIDER);

		Result result = table.get(get);

		if (!result.isEmpty())
		{
			NavigableMap<byte[], byte[]> map = result.getFamilyMap(COLUMN_CONTENTPROVIDER);
			cpresult = new ContentProvider(map);
			LOG.debug("returning {}", cpresult);
		}

		return cpresult;
	}

	public List<ContentProvider> getContentProviders()
	{
		List<ContentProvider> providers = new LinkedList<ContentProvider>();

		Scan scan = new Scan();
		scan.addFamily(COLUMN_CONTENTPROVIDER);
		scan.setFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(CONTENT_PRIVIDER_BYTES)));

		try
		{
			HTableInterface tableInterface = table.getTable();
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				Result result = null;
				while ((result = scanner.next()) != null)
				{
					providers.add(new ContentProvider(result.getFamilyMap(COLUMN_CONTENTPROVIDER)));
				}
			}
			finally
			{
				scanner.close();
				table.putTable(tableInterface);
			}
		}
		catch (IOException e)
		{
			LOG.error("Error while getting content providers.", e);
		}

		return providers;
	}

	public boolean isContentProvider(String cp)
	{
		Get get = new Get(getKey(cp));
		return table.exists(get);
	}

	public void putContentProvider(ContentProvider cp)
	{
		if (cp.getDate() == 0)
		{
			cp.setDate(System.currentTimeMillis() / 1000);
		}

		byte[] row = getKey(cp);
		Put put = new Put(row);
		LOG.debug("writing row: {}", Bytes.toString(row));
		Map<byte[], byte[]> map = cp.toMap();
		for (Map.Entry<byte[], byte[]> entry : map.entrySet())
		{
			put.add(COLUMN_CONTENTPROVIDER, entry.getKey(), entry.getValue());
		}

		table.put(put);
	}

	public void deleteContentProvider(String cp)
	{
		Delete delete = new Delete(getKey(cp));
		table.delete(null, delete);
	}

}
