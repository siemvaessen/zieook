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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.ClusterStatus;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the HBASE connection for non map-reduce stuff
 * <p />
 * Project zieook-data<br />
 * HBaseManager.java created 3 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class HBaseManager
{

	private static final Logger LOG = LoggerFactory.getLogger(HBaseManager.class);

	private final HBaseAdmin admin;
	private final Configuration config;
	private final HTablePool pool;

	// re-use the configuration object - this should
	private static HBaseManager manager;

	public static synchronized HBaseManager getInstance(String zookeeper) throws MasterNotRunningException,
			ZooKeeperConnectionException
	{

		if (manager == null)
		{
			manager = new HBaseManager(zookeeper);
		}
		return manager;
	}

	private HBaseManager(String zookeeper) throws MasterNotRunningException, ZooKeeperConnectionException
	{
		config = HBaseConfiguration.create();
		config.set("hbase.zookeeper.quorum", zookeeper);
		admin = new HBaseAdmin(config);
		pool = new HTablePool(config, 20);
		LOG.debug("HBaseManager singleton object created");
	}

	// /**
	// * Shutdown tears down the HBase cluster! - better don't use it :D
	// * @param descriptor
	// * @throws IOException
	// */
	// public void shutdown() throws IOException
	// {
	// admin.shutdown();
	// }

	public ClusterStatus getClusterStatus() throws IOException
	{
		return admin.getClusterStatus();
	}

	public List<String> getTableNames() throws IOException
	{
		HTableDescriptor[] tables = admin.listTables();
		List<String> result = new ArrayList<String>(tables.length);
		for (HTableDescriptor t : tables)
		{
			result.add(t.getNameAsString());
		}
		return result;
	}

	public void createTable(HTableDescriptor descriptor) throws IOException
	{
		LOG.info("creating table: {}", descriptor.getNameAsString());
		admin.createTable(descriptor);
	}

	/**
	 * Checks if the given table descriptor has different columns (names) than the given one, if so return true.
	 * @param updates
	 * @return {@code true} if the table needs to be updated or {@code false} if not
	 * @throws IOException
	 */
	public boolean needsUpdate(final HTableDescriptor updates) throws IOException
	{
		String name = updates.getNameAsString();
		LOG.info("checking to update table: {}", name);
		if (!exists(name))
		{
			LOG.warn("table <{}> does not exists, skipping update", name);
		}

		HTableInterface table = getTable(name);
		HTableDescriptor descriptor = table.getTableDescriptor();

		Collection<HColumnDescriptor> families = descriptor.getFamilies();
		Set<String> famNames = new HashSet<String>();

		Collection<HColumnDescriptor> updateFamilies = updates.getFamilies();
		Set<String> updateNames = new HashSet<String>();

		for (HColumnDescriptor family : families)
		{
			famNames.add(family.getNameAsString());
		}
		for (HColumnDescriptor family : updateFamilies)
		{
			updateNames.add(family.getNameAsString());
		}

		boolean result = false;
		for (String updateName : updateNames)
		{
			if (!famNames.contains(updateName))
			{
				LOG.info("column '{}' needs to be added to <{}>", updateName, name);
				result = true;
			}
		}

		for (String famName : famNames)
		{
			if (!updateNames.contains(famName))
			{
				LOG.info("column '{}' needs to be deleted from <{}>", famName, name);
				result = true;
			}
		}

		return result;
	}

	public void updateTable(final HTableDescriptor updates) throws IOException
	{
		// only do something if the table does exists:
		String name = updates.getNameAsString();
		LOG.info("checking to update table: {}", name);
		if (!exists(name))
		{
			LOG.warn("table <{}> does not exists, skipping update", name);
		}
		// get the existing table:
		HTableInterface table = getTable(name);
		HTableDescriptor descriptor = table.getTableDescriptor();

		// existing columns:
		// collect the names
		Set<String> famNames = new HashSet<String>();
		Collection<HColumnDescriptor> families = descriptor.getFamilies();
		for (HColumnDescriptor family : families)
		{
			famNames.add(family.getNameAsString());
		}

		// new columns:
		// collect the names:
		Set<String> updateNames = new HashSet<String>();
		Collection<HColumnDescriptor> updateFamilies = updates.getFamilies();
		for (HColumnDescriptor family : updateFamilies)
		{
			updateNames.add(family.getNameAsString());
		}

		for (String updateName : updateNames)
		{
			if (!famNames.contains(updateName))
			{
				// add
				LOG.info("Going to add column family '{}' to table: <{}>", updateName, name);
				admin.addColumn(name, getDescriptorFor(updateName, updates));
			}
		}

		for (String famName : famNames)
		{
			if (!updateNames.contains(famName))
			{
				// remove
				LOG.info("Going to remove column family '{}' from table <{}>", famName, name);
				admin.deleteColumn(name, famName);
			}
		}
	}

	private HColumnDescriptor getDescriptorFor(String name, HTableDescriptor tabledescriptor)
	{
		return tabledescriptor.getFamily(Bytes.toBytes(name));
	}

	/**
	 * Disables a table (takes it off-line) If it is being served, the master will tell the servers to stop serving it.
	 * Synchronous operation.
	 * @param name
	 * @throws IOException
	 */
	public void disableTable(String name) throws IOException
	{
		if (name != null)
		{
			if (admin.isTableEnabled(name))
			{
				try
				{
					// this throws nullpointer if now tables are in the pool for the given name...
					pool.closeTablePool(name);
				}
				catch (NullPointerException e)
				{
					LOG.debug("no pool for given table <{}>", name);
				}
				admin.disableTable(name);
			}
			else
			{
				LOG.info("table <{}> is already disabled", name);
			}
		}
		else
		{
			LOG.error("no table named given");
		}
	}

	public void enableTable(String name) throws IOException
	{
		if (name != null)
		{
			if (admin.isTableDisabled(name))
			{
				admin.enableTable(name);
			}
			else
			{
				LOG.info("table <{}> already enabled", name);
			}
		}
		else
		{
			LOG.error("no table named given");
		}
	}

	/**
	 * Deletes a table. Synchronous operation.
	 * This method can only be called on disabled tables, and will destroy all data
	 * @throws IOException
	 */
	public void dropTable(String name) throws IOException
	{
		admin.deleteTable(name);
	}

	/**
	 * Get the table for the given name
	 * <p />
	 * <em>Warning:</em> as the HBase documentation states, HTable is not thread safe!
	 * @param name - table name
	 * @return the table
	 * @throws IOException
	 */
	public HTableInterface getTable(String name) throws IOException
	{
		final HTableInterface result = pool.getTable(name);
		return result;
	}

	public void putTable(HTableInterface table) throws IOException
	{

		pool.putTable(table);
	}

	/**
	 * Returns the hbase configurration used, in order to directly create a HTable instance
	 * @return
	 */
	public Configuration getConfiguration()
	{
		return config;
	}

	/**
	 * Checks if a table exists
	 * @param tablename - table name
	 * @return true if the table exists, false otherwise (and also if an error occurs)
	 */
	public boolean exists(String tablename)
	{
		try
		{
			return admin.tableExists(tablename);
		}
		catch (MasterNotRunningException e)
		{
			LOG.error("failed to determine table existence: '" + tablename + "'", e);
		}
		catch (IOException e)
		{
			LOG.error("failed to determine table existence'" + tablename + "'", e);
		}
		return false;
	}

}
