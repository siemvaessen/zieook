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
import nl.gridline.zieook.workflow.model.Template;
import nl.gridline.zieook.workflow.model.Templates;

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
 * CRUD on templates, these are generic templates, not assigned to any content provider or recommender
 * <p />
 * Project zieook-data<br />
 * TemplateTable.java created 16 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class TemplateTable
{
	private static final Logger LOG = LoggerFactory.getLogger(TemplateTable.class);

	private final HBaseConfigurationTable table;

	// TODO move to rowkeys
	private static final String TEMPLATE_KEY_PREFIX = "template";

	// public static final byte[] COLUMN_FILTER = Bytes.toBytes(HBaseConfigurationTable.CONFIGURATION_COLUMN_FILTER);
	// public static final byte[] COLUMN_CONFIG = Bytes.toBytes(HBaseConfigurationTable.CONFIGURATION_COLUMN_CONFIG);
	private static final byte[] COLUMN_TEMPLATE = Bytes.toBytes(HBaseTableConstants.CONFIGURATION_COLUMN_TEMPLATE);

	public TemplateTable(HBaseConfigurationTable table)
	{
		this.table = table;
	}

	private static byte[] getKey(Template template)
	{
		return getKey(template.getName());
	}

	private static byte[] getKey(String name)
	{
		return Bytes.toBytes(TEMPLATE_KEY_PREFIX + "/" + name);
	}

	/**
	 * Create or Update a template
	 * @param template
	 * @return
	 */
	public void putTemplate(Template template)
	{
		try
		{
			Map<byte[], byte[]> map = template.toMap();
			byte[] rowkey = getKey(template);
			Put put = new Put(rowkey);
			for (Map.Entry<byte[], byte[]> entry : map.entrySet())
			{
				put.add(COLUMN_TEMPLATE, entry.getKey(), entry.getValue());
			}
			table.put(null, put);
		}
		catch (IOException e)
		{
			LOG.error("failed to write template: '" + template.getName() + "'", e);
		}

	}

	/**
	 * Retrieve the template with the given template name
	 * @param name template name
	 * @return the given template, or an empty template object
	 */
	public Template getTemplate(String name)
	{
		Get get = new Get(getKey(name)).addFamily(COLUMN_TEMPLATE);
		Result result = table.get(null, get);
		if (!result.isEmpty())
		{
			NavigableMap<byte[], byte[]> map = result.getFamilyMap(COLUMN_TEMPLATE);
			try
			{
				return new Template(map);
			}
			catch (IOException e)
			{
				LOG.error("failed to READ template from data '" + name + "' ", e);
			}
			catch (ClassNotFoundException e)
			{
				LOG.error("failed to read template '" + name + "' ", e);
			}
		}

		return null;
	}

	public boolean exists(String name)
	{
		Get get = new Get(getKey(name)).addFamily(COLUMN_TEMPLATE);
		Result result = table.get(null, get);

		return !result.isEmpty();
	}

	/**
	 * Remove a template - this will probably be a soft-delete.
	 * @param name
	 * @return
	 */
	public void deleteTemplate(String name)
	{
		Delete delete = new Delete(getKey(name));
		table.delete(null, delete);
	}

	/**
	 * Return the list of templates
	 * <b>note</b>
	 * The implementation is not very efficient, but should do if the amount of templates stays limited.
	 * @return a list of strings with the template names
	 */
	public List<String> getTemplates()
	{

		// It is much more efficient (and not very complicated) to track the high - low value of the row key and use that
		// to collect all templates

		// create a list of template names
		Scan scan = new Scan().addFamily(COLUMN_TEMPLATE);

		// create a row key filter, we know the template key prefix.
		RowFilter filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(Bytes.toBytes(TEMPLATE_KEY_PREFIX)));
		scan.setFilter(filter);

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
					NavigableMap<byte[], byte[]> map = rr.getFamilyMap(COLUMN_TEMPLATE);
					// the key in this map is the name of the template:
					for (Map.Entry<byte[], byte[]> entry : map.entrySet())
					{
						result.add(Bytes.toString(entry.getKey()));
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
			LOG.error("failed to SCAN table '" + table.getTableName(null) + "' for templates", e);
		}

		return result;
	}

	/**
	 * Return all templates in a list.
	 * @return
	 */
	public Templates listTemplates()
	{
		List<String> templates = getTemplates();
		List<Template> result = new ArrayList<Template>(templates.size());
		for (String template : templates)
		{
			result.add(getTemplate(template));
		}
		return new Templates(result);
	}
}
