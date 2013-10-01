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
import nl.gridline.zieook.model.ModelConstants;
import nl.gridline.zieook.workflow.model.CollectionFilter;
import nl.gridline.zieook.workflow.model.RecommenderConfig;
import nl.gridline.zieook.workflow.model.Template;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CRUD on recommenders: a recommender consists of a template and a collection filter and some information about the
 * instance of this recommender. A recommender is always linked to a content provider, by it's name
 * <p />
 * Project zieook-data<br />
 * RecommenderTable.java created 16 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class RecommenderTable
{
	private static final Logger LOG = LoggerFactory.getLogger(RecommenderTable.class);

	private final HBaseConfigurationTable table;

	/**
	 * Filter column name
	 */
	private static final byte[] COLUMN_FILTER = Bytes.toBytes(HBaseTableConstants.CONFIGURATION_COLUMN_FILTER);

	/**
	 * Configuration column name
	 */
	private static final byte[] COLUMN_CONFIG = Bytes.toBytes(HBaseTableConstants.CONFIGURATION_COLUMN_RECOMMENDER);

	/**
	 * Template column name
	 */
	private static final byte[] COLUMN_TEMPLATE = Bytes.toBytes(HBaseTableConstants.CONFIGURATION_COLUMN_TEMPLATE);

	// recommender config qualifier keys:
	// private static final byte[] Q_CP = Bytes.toBytes("cp");
	// private static final byte[] Q_RECOMMENDER = Bytes.toBytes("recommender");
	// private static final byte[] Q_COLLECTION = Bytes.toBytes("collection");
	// private static final byte[] Q_FILTER = Bytes.toBytes("filter");
	// private static final byte[] Q_TEMPLATE = Bytes.toBytes("template");
	// private static final byte[] Q_INTERVAL = Bytes.toBytes("interval");
	// private static final byte[] Q_RATING_LOW = Bytes.toBytes("rating_low");
	// private static final byte[] Q_RATING_HIGH = Bytes.toBytes("rating_high");

	public RecommenderTable(HBaseConfigurationTable table)
	{
		this.table = table;
	}

	/**
	 * create or update a recommender
	 * @param recommender
	 * @return
	 */
	public void putRecommender(RecommenderConfig recommender)
	{
		// the the map from the recommender:
		Map<byte[], byte[]> map = recommender.toMap();

		// create a row key, based on a prefix, the cp and, recommender:
		byte[] row = RowKeys.getRecommenderKey(recommender.getCp(), recommender.getRecommender());
		Put put = new Put(row);
		if (recommender.getTemplate() != null)
		{
			writeTemplate(put, recommender.getTemplate());
		}
		if (recommender.getFilter() != null)
		{
			writeCollectionFilter(put, recommender.getFilter());
		}

		// go through the map:
		for (Map.Entry<byte[], byte[]> entry : map.entrySet())
		{
			put.add(COLUMN_CONFIG, entry.getKey(), entry.getValue());
		}
		// add data to table:
		table.put(null, put);
	}

	/**
	 * Write the template to the recommender row
	 * @param put
	 * @param template
	 */
	private void writeTemplate(Put put, Template template)
	{
		try
		{
			Map<byte[], byte[]> map = template.toMap();
			for (Map.Entry<byte[], byte[]> entry : map.entrySet())
			{
				put.add(COLUMN_TEMPLATE, entry.getKey(), entry.getValue());
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to write template: '" + template.getName() + "'", e);
		}
	}

	/**
	 * Read the template from a recommender row
	 * @param result
	 * @param key
	 * @return
	 */
	private Template readTemplate(Result result, byte[] key)
	{
		Template template = new Template();
		NavigableMap<byte[], byte[]> map = result.getFamilyMap(COLUMN_TEMPLATE);
		if (map != null && !map.isEmpty())
		{
			try
			{
				template = new Template(map);
			}
			catch (IOException e)
			{
				LOG.error("failed to READ template from data '" + Bytes.toString(key) + "'", e);
			}
			catch (ClassNotFoundException e)
			{
				LOG.error("failed to read template '" + Bytes.toString(key) + "' ", e);
			}
		}
		else
		{
			LOG.warn("could not find family: {} in data for key {}", HBaseTableConstants.CONFIGURATION_COLUMN_TEMPLATE,
					Bytes.toString(key));
		}
		return template;
	}

	/**
	 * Write collection filter
	 * @param put
	 * @param filter
	 */
	private void writeCollectionFilter(Put put, CollectionFilter filter)
	{
		try
		{
			Map<byte[], byte[]> map = filter.toMap();
			for (Map.Entry<byte[], byte[]> entry : map.entrySet())
			{
				put.add(COLUMN_FILTER, entry.getKey(), entry.getValue());
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to write template: '" + filter.getName() + "'", e);
		}
	}

	private CollectionFilter readCollectionFilter(Result result, byte[] key)
	{
		CollectionFilter filter = new CollectionFilter();
		NavigableMap<byte[], byte[]> map = result.getFamilyMap(COLUMN_FILTER);
		if (map != null && !map.isEmpty())
		{
			try
			{
				filter = new CollectionFilter(map);
			}
			catch (IOException e)
			{
				LOG.error("failed to READ template from data '" + Bytes.toString(key) + "' ", e);
			}
			catch (ClassNotFoundException e)
			{
				LOG.error("failed to read template '" + Bytes.toString(key) + "' ", e);
			}
		}
		else
		{
			LOG.warn("could not find family: {} in data for key {}", HBaseTableConstants.CONFIGURATION_COLUMN_FILTER,
					Bytes.toString(key));
		}
		return filter;
	}

	/**
	 * Get a recommender based on it's id: <tt>cp.recommender</tt>
	 * @param cp the content provider
	 * @param recommender the recommender name
	 * @param id
	 * @return
	 */
	public RecommenderConfig getRecommender(String cp, String recommender)
	{

		byte[] rowkey = RowKeys.getRecommenderKey(cp, recommender);
		// LOG.info("trying to get row {}/{} becomes" + Bytes.toString(rowkey), cp, recommender);
		Get get = new Get(rowkey);
		// get all three columns:
		get.addFamily(COLUMN_CONFIG).addFamily(COLUMN_TEMPLATE).addFamily(COLUMN_FILTER);
		Result result = table.get(get);
		// LOG.info("row: {}", Bytes.toString(result.getRow()));
		if (!result.isEmpty())
		{
			Template template = readTemplate(result, rowkey);
			CollectionFilter filter = readCollectionFilter(result, rowkey);
			NavigableMap<byte[], byte[]> map = result.getFamilyMap(COLUMN_CONFIG);
			RecommenderConfig config = new RecommenderConfig(map);
			config.setTemplate(template);
			config.setFilter(filter);
			return config;
		}
		return null;
	}

	public String getRecommenderCollection(String cp, String recommender)
	{
		byte[] rowkey = RowKeys.getRecommenderKey(cp, recommender);
		Get get = new Get(rowkey);
		get.addFamily(COLUMN_CONFIG);
		Result result = table.get(get);
		if (!result.isEmpty())
		{
			byte[] data = result.getValue(COLUMN_CONFIG, ModelConstants.COLLECTION);
			return Bytes.toString(data);
		}
		return null;
	}

	public boolean isRecommender(String cp, String recommender)
	{
		byte[] rowkey = RowKeys.getRecommenderKey(cp, recommender);
		return table.exists(new Get(rowkey));
	}

	/**
	 * Delete a recommender based on it's id: <tt>cp.recommender</tt>
	 * @param cp
	 * @param recommender
	 * @return
	 */
	public void deleteRecommender(String cp, String recommender)
	{
		Delete delete = new Delete(RowKeys.getRecommenderKey(cp, recommender));
		table.delete(null, delete);
	}

	public List<String> getRecommenderList()
	{
		return getRecommenderList(null);
	}

	public List<String> getRecommenderList(String cp, String collection)
	{

		FilterList filters = new FilterList();
		filters.addFilter(new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRecommenderKey(cp))));

		SingleColumnValueFilter collectionFilter = new SingleColumnValueFilter(COLUMN_CONFIG, ModelConstants.COLLECTION,
				CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(collection)));
		collectionFilter.setFilterIfMissing(true);

		// TODO check if we actually need to add the column_config column here...
		Scan scan = new Scan().addFamily(COLUMN_CONFIG).setFilter(filters);

		// start looking:
		List<String> result = new ArrayList<String>();
		try
		{
			HTableInterface tableInterface = table.getTable();
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				// get the rows from the table:
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					String row = Bytes.toString(rr.getRow());
					result.add(row.substring(row.indexOf('/') + 1));
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

		return result;
	}

	/**
	 * Get the recommender list for the given content provider
	 * @param cp
	 * @return
	 */
	public List<String> getRecommenderList(String cp)
	{
		// It is much more efficient (and not very complicated) to track the high - low value of the row key and use that
		// to collect all templates

		// create a list of template names

		// create a row key filter, we know the recommender key prefix, and possible the cp
		RowFilter filter;
		if (cp == null)
		{
			filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRecommenderKey()));
		}
		else
		{
			filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRecommenderKey(cp)));
		}

		Scan scan = new Scan().addFamily(COLUMN_CONFIG).setFilter(filter);

		// start looking:
		List<String> result = new ArrayList<String>();
		try
		{
			HTableInterface tableInterface = table.getTable();
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				// get the rows from the table:
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					String row = Bytes.toString(rr.getRow());
					result.add(row.substring(row.indexOf('/') + 1));
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

		return result;
	}

	public List<RecommenderConfig> getRecommenders()
	{
		return getRecommenders(null);
	}

	public List<RecommenderConfig> getRecommenders(String cp)
	{

		// create a row key filter, we know the recommender key prefix, and possible the cp
		RowFilter filter;
		if (cp == null)
		{
			filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRecommenderKey()));
		}
		else
		{
			filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(RowKeys.getRecommenderKey(cp)));
		}

		Scan scan = new Scan().addFamily(COLUMN_CONFIG).addFamily(COLUMN_TEMPLATE).addFamily(COLUMN_FILTER)
				.setFilter(filter);

		// start looking:
		List<RecommenderConfig> result = new ArrayList<RecommenderConfig>();
		try
		{
			HTableInterface tableInterface = table.getTable();
			ResultScanner scanner = tableInterface.getScanner(scan);
			try
			{
				// get the rows from the table:
				for (Result rr = scanner.next(); rr != null; rr = scanner.next())
				{
					// LOG.debug("row: {}", Bytes.toString(rr.getRow()));
					RecommenderConfig recommender = new RecommenderConfig(rr.getFamilyMap(COLUMN_CONFIG));
					recommender.setTemplate(readTemplate(rr, rr.getRow()));
					recommender.setFilter(readCollectionFilter(rr, rr.getRow()));
					// LOG.debug("recommender: " + recommender.toJSON());
					result.add(recommender);
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

		return result;

	}
}
