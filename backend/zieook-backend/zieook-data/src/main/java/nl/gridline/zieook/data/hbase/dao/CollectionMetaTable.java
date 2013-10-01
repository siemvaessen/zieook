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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.workflow.model.Collection;

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
 * Stores the meta data of a collection: the Collection object
 * <p />
 * Project zieook-data<br />
 * CollectionMetaTable.java created 7 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionMetaTable
{

	private static final Logger LOG = LoggerFactory.getLogger(CollectionMetaTable.class);

	/**
	 * collection meta data column family.
	 */
	private static final byte[] COLUMN_COLLECTION = Bytes
			.toBytes(HBaseTableConstants.CONFIGURATION_COLUMN_COLLECTIONMETA);

	private final HBaseConfigurationTable table;

	public CollectionMetaTable(HBaseConfigurationTable table)
	{
		this.table = table;
	}

	/**
	 * Add a collection to the database
	 * @param cp - content provider
	 * @param collection - collection meta data
	 */
	public void putCollection(String cp, Collection collection)
	{
		byte[] row = RowKeys.getCollectionMetaKey(cp, collection.getName());
		Put put = new Put(row);
		LOG.debug("writing row: {}", Bytes.toString(row));
		Map<byte[], byte[]> map = collection.toMap();
		for (Map.Entry<byte[], byte[]> entry : map.entrySet())
		{
			put.add(COLUMN_COLLECTION, entry.getKey(), entry.getValue());
		}
		table.put(put);
	}

	/**
	 * Delete a collection based on the content provider and collection name
	 * @param cp - content provider
	 * @param collection - collection name
	 */
	public void deleteCollection(String cp, String collection)
	{
		Delete delete = new Delete(RowKeys.getCollectionMetaKey(cp, collection));
		table.delete(delete);
	}

	/**
	 * Get the collection meta data based on the content provider and a collection name
	 * @param cp - content provider
	 * @param collection - collection name
	 * @return the Collection meta data, if found
	 */
	public Collection getCollection(String cp, String collection)
	{
		byte[] row = RowKeys.getCollectionMetaKey(cp, collection);
		LOG.debug("reading row: {}", Bytes.toString(row));

		Get get = new Get(row).addFamily(COLUMN_COLLECTION);

		Result result = table.get(get);
		if (!result.isEmpty())
		{
			NavigableMap<byte[], byte[]> map = result.getFamilyMap(COLUMN_COLLECTION);
			Collection item = new Collection(map);
			LOG.debug("found collection: {} : {}", collection, item.toString());
			return item;
		}
		return null;
	}

	public boolean isCollection(String cp, String collection)
	{
		byte[] row = RowKeys.getCollectionMetaKey(cp, collection);
		return table.exists(new Get(row));
	}

	public List<String> getCollectionsList(String cp)
	{
		List<String> result = new ArrayList<String>();

		Scan scan = new Scan();
		scan.addFamily(COLUMN_COLLECTION);
		RowFilter filter;
		if (cp != null)
		{
			filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getCollectionMetaKey(cp)));
		}
		else
		{
			filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getCollectionMetaKey()));
		}
		scan.setFilter(filter);
		try
		{
			HTableInterface tableInterface = table.getTable();
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{

					String row = Bytes.toString(rr.getRow());
					LOG.debug("retrieved row for: {}" + row);
					result.add(row.substring(row.lastIndexOf('/') + 1));
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
			LOG.error("failed to retrieve the collection list for: " + cp + " try again", e);
		}

		return result;
	}

	public List<Collection> getCollections(String cp)
	{
		List<Collection> result = new ArrayList<Collection>();

		Scan scan = new Scan();
		scan.addFamily(COLUMN_COLLECTION);
		RowFilter filter;
		if (cp != null)
		{
			filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getCollectionMetaKey(cp)));
		}
		else
		{
			filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getCollectionMetaKey()));
		}
		scan.setFilter(filter);
		try
		{
			HTableInterface tableInterface = table.getTable();
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					result.add(new Collection(rr.getFamilyMap(COLUMN_COLLECTION)));
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
			LOG.error("failed to retrieve the collection list for: " + cp + " try again", e);
		}

		return result;
	}

	public boolean exists(String cp, String collection)
	{
		Get get = new Get(RowKeys.getCollectionMetaKey(cp, collection));
		return table.exists(null, get);
	}
}
