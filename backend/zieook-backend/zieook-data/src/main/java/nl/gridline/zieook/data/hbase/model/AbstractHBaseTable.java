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
package nl.gridline.zieook.data.hbase.model;

import java.io.IOException;
import java.util.List;

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.TableOperations;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract table operations implementation.
 * Provides all basic row operations for a HBase table, or actually forwards them, and creates a new HTable instance for
 * every put / get / delete operation
 * <p />
 * Project zieook-data<br />
 * AbstractHBaseTable.java created 24 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public abstract class AbstractHBaseTable implements TableOperations
{

	private static final Logger LOG = LoggerFactory.getLogger(AbstractHBaseTable.class);

	protected HBaseManager manager;

	public AbstractHBaseTable(HBaseManager manager)
	{
		this.manager = manager;
	}

	@Override
	public boolean drop(String name)
	{
		final String fullname = getTableName(name);
		if (fullname == null)
		{
			throw new IllegalArgumentException("table name not set");
		}
		boolean result = true;
		try
		{
			if (manager.exists(fullname))
			{
				manager.disableTable(fullname);
				manager.dropTable(fullname);
			}
			else
			{
				LOG.error("table does not exists '{}' - delete omitted", fullname);
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to drop: " + fullname, e);
			result = false;
		}

		return result;
	}

	@Override
	public boolean tableExists(String name)
	{
		final String fullname = getTableName(name);
		if (fullname == null)
		{
			throw new IllegalArgumentException("table name not set");
		}
		final boolean result = manager.exists(fullname);
		LOG.debug("table '{}' exists? ={}", fullname, result);
		return result;

	}

	@Override
	public HTableInterface getTable(String name)
	{
		final String fullname = getTableName(name);

		if (fullname == null)
		{
			throw new IllegalArgumentException("table name not set");
		}
		try
		{
			return manager.getTable(fullname);
		}
		catch (IOException e)
		{
			LOG.error("failed to get table: " + fullname, e);
		}

		return null;
	}

	@Override
	public void putTable(HTableInterface table)
	{
		if (table != null)
		{
			try
			{
				manager.putTable(table);
			}
			catch (IOException e)
			{
				LOG.error("failed to put table back in pool: " + Bytes.toString(table.getTableName()), e);
			}
		}
		else
		{
			LOG.warn("no table given, ignoring");
		}
	}

	@Override
	public boolean exists(String name, Get get)
	{
		if (get == null)
		{
			return false;
		}

		boolean result = false;
		HTableInterface table = getTable(name);
		try
		{
			result = table.exists(get);
		}
		catch (IOException e)
		{
			LOG.error("Failed to EXISTS data from table: '" + getTableName(name) + "'", e);
		}
		finally
		{
			putTable(table);
		}
		return result;
	}

	@Override
	public Result get(String name, Get get)
	{
		if (get == null)
		{
			return null;
		}

		HTableInterface table = getTable(name);
		try
		{
			if (table != null)
			{
				return table.get(get);
			}
			else
			{
				LOG.error("table not found <{}> ignoring put", name);
			}
		}
		catch (IOException e)
		{
			LOG.error("Failed to GET data from table: '" + getTableName(name) + "'", e);
		}
		finally
		{
			putTable(table);
		}

		return new Result();
	}

	@Override
	public void put(String name, Put put)
	{
		if (put == null)
		{
			return;
		}

		HTableInterface table = getTable(name);
		try
		{
			if (table != null)
			{
				table.put(put);
			}
			else
			{
				LOG.error("table not found <{}> ignoring put", name);
			}
		}
		catch (IOException e)
		{
			LOG.error("Failed to PUT data in table '" + getTableName(name) + "'", e);
		}
		finally
		{
			putTable(table);
		}
	}

	@Override
	public void put(String name, List<Put> put)
	{
		if (put == null)
		{
			return;
		}

		HTableInterface table = getTable(name);
		try
		{
			if (table != null)
			{
				table.put(put);
			}
			else
			{
				LOG.error("table not found <{}> ignoring put", name);
			}
		}
		catch (IOException e)
		{
			LOG.error("Failed to PUT data in table '" + getTableName(name) + "'", e);
		}
		finally
		{
			putTable(table);
		}
	}

	@Override
	public void delete(String name, Delete delete)
	{
		if (delete == null)
		{
			return;
		}

		HTableInterface table = getTable(name);
		try
		{
			if (table != null)
			{
				table.delete(delete);
			}
			else
			{
				LOG.error("table not found <{}> ignored delete", name);
			}
		}
		catch (IOException e)
		{
			LOG.error("Failed to DELETE data from table '" + getTableName(name) + "'", e);
		}
		finally
		{
			putTable(table);
		}

	}

	@Override
	public void delete(String name, List<Delete> deletes)
	{
		if (deletes == null)
		{
			return;
		}

		HTableInterface table = getTable(name);
		try
		{
			if (table != null)
			{
				table.delete(deletes);
			}
			else
			{
				LOG.error("table not found <{}> ignore deletes", name);
			}
		}
		catch (IOException e)
		{
			LOG.error("Failed to DELETE data from table '" + getTableName(name) + "'", e);
		}
		finally
		{
			putTable(table);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.data.hbase.TableOperations#getExistingTableDescriptor(java.lang.String)
	 */
	@Override
	public HTableDescriptor getExistingTableDescriptor(String name) throws IOException
	{
		if (tableExists(name))
		{
			return getTable(name).getTableDescriptor();
		}
		return null;
	}

	@Override
	public boolean create(String name)
	{
		boolean result = true;
		if (!tableExists(name))
		{
			try
			{
				manager.createTable(getCurrentDescriptor(name));
			}
			catch (IOException e)
			{
				LOG.error("failed to create the " + getTableName(name) + "", e);
				result = false;
			}
		}
		else
		{
			LOG.error("table already exists: {}", name);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.data.hbase.TableOperations#update(java.lang.String)
	 */
	@Override
	public boolean update(String name) throws IOException
	{
		if (tableExists(name))
		{
			HTableDescriptor updates = getCurrentDescriptor(name);
			String tablename = getTableName(name);
			if (manager.needsUpdate(updates))
			{
				manager.disableTable(tablename);
				try
				{
					manager.updateTable(updates);
				}
				finally
				{
					manager.enableTable(tablename);
				}

				return true;
			}
		}
		return false;
	}
}
