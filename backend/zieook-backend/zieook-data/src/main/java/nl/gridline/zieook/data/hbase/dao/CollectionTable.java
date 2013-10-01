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
import java.util.NavigableMap;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.mapreduce.HBasePUTFactory;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;
import nl.gridline.zieook.model.Categories;
import nl.gridline.zieook.model.CollectionItem;
import nl.gridline.zieook.model.ModelConstants;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operations on a the collection table:
 * - CRUD on collections: operations on complete collections
 * - CRUD on items in collections, although all of CRUD will be implemented here, it will depend on the type of import
 * whether we will actually implement this.
 * <p />
 * Project zieook-data<br />
 * CollectionTable.java created 16 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionTable
{

	private static final Logger LOG = LoggerFactory.getLogger(CollectionTable.class);

	private static final byte[] COLUMN_RAW = Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_RAW);
	private static final byte[] QUALIFIER_RAW = Bytes.toBytes(HBaseTableConstants.COLLECTION_QUALIFIER_RAW);
	private static final byte[] COLUMN_INTR = Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR);
	private static final byte[] COLLECTION_CATEGORY_COLLUMN = Bytes
			.toBytes(HBaseTableConstants.COLLECTION_CATEGORY_COLLUMN);
	private static final byte[] COLLECTION_CATEGORY_NAME = Bytes.toBytes(HBaseTableConstants.COLLECTION_CATEGORY_NAME);

	private final HBaseCollectionTable table;

	private final int maxcategories;

	// private final String split;

	public CollectionTable(HBaseCollectionTable table)
	{
		this.table = table;

		Config conf = Config.getInstance(Config.ZIEOOK_DATAMANAGER_FILE);

		int max = 100;
		try
		{
			max = Integer.parseInt(conf.get(Config.ZIEOOK_COLLECTION_CATEGORIES_MAX_SIZE, "100"));
		}
		catch (NumberFormatException e)
		{
			LOG.error("failed to parse setting: '" + Config.ZIEOOK_COLLECTION_CATEGORIES_MAX_SIZE
					+ "' - using default: 100", e);

		}

		maxcategories = max;
		// this.split = conf.get(Config.ZIEOOK_COLLECTION_CATEGORIES_SPLIT, "/");
	}

	/**
	 * Returns a single item from a collection
	 * @param cp - the content provider
	 * @param collection - collection name
	 * @param item - the item id
	 * @return the original item in whatever format it was imported
	 */
	public String getItemRaw(String cp, String collection, long item)
	{
		Get get = new Get(RowKeys.getCollectionKey(collection, item)).addColumn(COLUMN_RAW, QUALIFIER_RAW);

		Result result = table.get(cp, get);
		if (!result.isEmpty())
		{
			byte[] bytes = result.getValue(COLUMN_RAW, QUALIFIER_RAW);
			return Bytes.toString(bytes);
		}
		return null;
	}

	public CollectionItem getItem(String cp, String collection, long item)
	{
		Get get = new Get(RowKeys.getCollectionKey(collection, item)).addFamily(COLUMN_INTR);
		Result result = table.get(cp, get);
		if (!result.isEmpty())
		{
			return new CollectionItem(result.getFamilyMap(COLUMN_INTR));
		}
		return null;
	}

	public boolean isCollectionItem(String cp, String collection, long item)
	{
		return table.exists(cp, new Get(RowKeys.getCollectionKey(collection, item)));
	}

	/**
	 * Add raw item data
	 * @param cp content provider
	 * @param collection collection name
	 * @param item id
	 * @param object
	 */
	public void putItem(String cp, String collection, long item, String object)
	{
		Put put = HBasePUTFactory.getCollectionItemRaw(collection, item, object);
		table.put(cp, put);
	}

	/**
	 * Delete an item, the raw data
	 * @param cp - content provider
	 * @param collection collection name
	 * @param item - item id
	 */
	public void deleteItem(String cp, String collection, long item)
	{
		Delete delete = new Delete(RowKeys.getCollectionKey(collection, item)).deleteFamily(COLUMN_RAW);
		table.delete(cp, delete);
	}

	/**
	 * Returns the known elements of a single item from the collections
	 * @param cp content provider
	 * @param collection collection name
	 * @param item item id
	 * @return a key value map
	 */
	public CollectionItem getProcessedItem(String cp, String collection, long item)
	{
		Get get = new Get(RowKeys.getCollectionKey(collection, item)).addFamily(COLUMN_INTR);
		Result result = table.get(cp, get);
		if (!result.isEmpty())
		{
			NavigableMap<byte[], byte[]> map = result.getFamilyMap(COLUMN_INTR);
			return new CollectionItem(map);
		}
		return null;
	}

	/**
	 * put a processed item in the database
	 * Accepts: Boolean, Double, Float, Integer, Long, Short, String values
	 * @param cp content provider
	 * @param collection collection name
	 * @param item item id
	 * @param item collection item
	 */
	public void putProcessedItem(String cp, String collection, long item, CollectionItem data)
	{
		Put put = HBasePUTFactory.getCollectionItemIntr(collection, item, data);
		if (put != null)
		{
			table.put(cp, put);
		}
	}

	/**
	 * Delete a processed item from the database
	 * @param cp
	 * @param collection collection name
	 * @param item item name
	 */
	public void deleteProcessedItem(String cp, String collection, long item)
	{
		Delete delete = new Delete(RowKeys.getCollectionKey(collection, item)).deleteFamily(COLUMN_INTR);
		table.delete(cp, delete);
	}

	/**
	 * PUT a full item in the database
	 * @param cp collection provider
	 * @param collection collection name
	 * @param item item id
	 * @param data the item
	 * @param object the raw data
	 */
	public void putFullItem(String cp, String collection, long item, CollectionItem data, String object)
	{
		Put put = HBasePUTFactory.getCollectionItemCompl(collection, item, object, data);
		table.put(cp, put);
	}

	/**
	 * Get the full item from the collection
	 * @param cp content provider
	 * @param collection collection name
	 * @param item item id
	 * @return [raw,collection item] array
	 */
	public Object[] getFullItem(String cp, String collection, long item)
	{
		String raw = null;
		Get get = new Get(RowKeys.getCollectionKey(collection, item)).addFamily(COLUMN_INTR).addFamily(COLUMN_RAW);
		Result result = table.get(cp, get);
		if (!result.isEmpty())
		{
			NavigableMap<byte[], byte[]> map = result.getFamilyMap(COLUMN_INTR);
			CollectionItem data = new CollectionItem(map);
			byte[] bytes = result.getValue(COLUMN_RAW, QUALIFIER_RAW);
			raw = Bytes.toString(bytes);
			return new Object[]{raw, data};
		}
		return new Object[]{null, null};
	}

	/**
	 * Delete the whole row!
	 * @param cp
	 * @para collection
	 * @param itemlogback access
	 */
	public void deleteFullItem(String cp, String collection, long item)
	{
		Delete delete = new Delete(RowKeys.getCollectionKey(collection, item));
		table.delete(cp, delete);
	}

	/**
	 * Returns the sorted list of categories from the collection table.<br />
	 * The results list is limited to <tt>zieook.categories.search.maxsize</tt> items
	 * @param cp content provider
	 * @param collection collection name
	 * @param qualifier column name to search for
	 * @param from start from the given category - the list of categories is sorted
	 * @return
	 */
	public Categories getCategories(String cp, String collection, String from)
	{
		// COLLECTION_CATEGORY_COLLUMN
		// currently categories are quite flat.... this is what is implemented for now.

		// dummy result:
		List<Categories> categories = new ArrayList<Categories>();
		Categories result = new Categories(categories);

		// setup a scan:
		Scan scan = new Scan();

		Filter filter = new RowFilter(CompareOp.EQUAL,
				new BinaryPrefixComparator(RowKeys.getCategoriesPrefix(collection)));

		scan.setFilter(filter);
		if (from != null)
		{
			scan.setStartRow(RowKeys.getCategoriesKey(collection, from));
		}
		scan.addColumn(COLLECTION_CATEGORY_COLLUMN, COLLECTION_CATEGORY_NAME);
		try
		{
			HTableInterface tableInterface = table.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				int cnt = 0;
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					byte[] value = rr.getValue(COLLECTION_CATEGORY_COLLUMN, COLLECTION_CATEGORY_NAME);
					if (value != null)
					{
						categories.add(new Categories(Bytes.toString(value)));
						cnt++;
						if (cnt == maxcategories)
						{
							// set the last key:
							result.setNext(Bytes.toString(value));
							// stop, limit results to 100
							break;
						}
					}
					else
					{
						LOG.error("corrupted data entry found for row {}", Bytes.toString(rr.getRow()));
					}
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
			LOG.error("failed to retrieve categories", e);

		}
		return result;
	}

	protected List<CollectionItem> getItems(String cp, Scan scan, int size)
	{
		if (size <= 0)
		{
			size = 100;
		}

		List<CollectionItem> result = new ArrayList<CollectionItem>(100);
		try
		{
			HTableInterface tableInterface = table.getTable(cp);
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				int cnt = 0;
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					cnt++;
					result.add(new CollectionItem(rr.getFamilyMap(COLUMN_INTR)));
					if (cnt == size)
					{
						break;
					}
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
			LOG.error("failed to retrieve items", e);
		}

		return result;
	}

	/**
	 * Return the first 100 collection items for the given content provider and collection
	 * @param cp content provider name
	 * @param collection collection name
	 * @return list of collection items
	 */
	public List<CollectionItem> getItems(String cp, String collection, int size)
	{
		Filter filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(Bytes.toBytes(collection)));
		Scan scan = new Scan().addFamily(COLUMN_INTR).setFilter(filter);
		return getItems(cp, scan, size);

	}

	public List<CollectionItem> searchItems(String cp, String collection, String regexTitle, int size)
	{
		FilterList filters = new FilterList();

		filters
				.addFilter(new RowFilter(CompareOp.GREATER_OR_EQUAL, new BinaryPrefixComparator(Bytes.toBytes(collection))));
		SingleColumnValueFilter value = new SingleColumnValueFilter(COLUMN_INTR, ModelConstants.TITLE, CompareOp.EQUAL,
				new RegexStringComparator(regexTitle));
		value.setFilterIfMissing(true);
		filters.addFilter(value);

		Scan scan = new Scan().addFamily(COLUMN_INTR).setFilter(filters);
		return getItems(cp, scan, size);
	}

	/**
	 * Return the 100 collection items starting from <tt>item</tt>
	 * @param cp content provider name
	 * @param collection collection name
	 * @param item id
	 * @return return list of collection items
	 */
	public List<CollectionItem> getItems(String cp, String collection, long item, int size)
	{
		Scan scan = new Scan(RowKeys.getCollectionKey(collection, item)).addFamily(COLUMN_INTR);
		Filter filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(Bytes.toBytes(collection)));
		scan.setFilter(filter);
		return getItems(cp, scan, size);
	}

	/**
	 * @param cp
	 * @param collection
	 */
	public void deleteCollection(String cp, String collection)
	{
		FilterList filters = new FilterList();
		filters
				.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getCollectionKey(collection))));

		Scan scan = new Scan().addFamily(COLUMN_INTR).setFilter(filters);
		deleteAll(scan, cp, COLUMN_INTR);

		scan = new Scan().addFamily(COLUMN_RAW).setFilter(filters);
		deleteAll(scan, cp, COLUMN_RAW);

	}

	/**
	 * Delete all rows produces by scan
	 * @param scan a scan - be careful to produce a correct scan, it deletes stuf from the table!
	 * @param cp content provider name
	 */
	private void deleteAll(Scan scan, String cp, byte[] family)
	{

		// scan!!!
		List<Delete> result = new ArrayList<Delete>();
		long count = 0;
		try
		{
			HTableInterface tableInterface = table.getTable(cp);

			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				// get the rows from the table:
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					KeyValue kv = rr.getColumnLatest(family, ModelConstants.ITEM);
					if (kv != null)
					{
						// the task id is stored as a string (like the whole taskconfig) - but we only need this particular
						// value: - if the parselong fails, there's something really wrong - a coding bug -
						result.add(new Delete(rr.getRow()));
					}
					else
					{
						LOG.error("This is a bug: some items are left behind, while cleaning: {}", rr);
					}

					// delete every thousand rows:
					if (result.size() > 0 && result.size() % 1000 == 0)
					{
						count += result.size();
						LOG.info("intermediate delete <{}> total now <{}>", result.size(), count);
						table.delete(cp, result);
						result.clear();
					}
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
			LOG.error("failed to SCAN table '" + table.getTableName(null) + "' for recommenders", e);
		}

		// flush all that's left
		count += result.size();
		table.delete(cp, result);
		result.clear();

		LOG.info("Deleted {} old data for <{}>", count, cp);
	}

}
