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
import java.util.List;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

/**
 * Because HBase does not have a real ORM we have to create it ourselfs
 * <p />
 * Project zieook-data<br />
 * TableOperations.java created 9 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public interface TableOperations
{

	// /**
	// * Creates the HBase table and it's column descriptors
	// * @return returns true if the table did not exist and it was created
	// */
	// public boolean create();

	/**
	 * Creates the HBase table and it's column descriptors
	 * @param name with a specific name
	 * @return returns true if the table did not exist and it was created
	 */
	public boolean create(String name);

	// /**
	// * Drop the HBase table and ALL of it's data(!)
	// * @return returns true if the table existed and was dropped
	// */
	// public boolean drop();

	/**
	 * Drop the HBase table and ALL of it's data(!)
	 * @param name with a specific name
	 * @return returns true if the table existed and was dropped
	 */
	public boolean drop(String name);

	/**
	 * Adds and deletes column from an existing table, matching the table descriptor with the existing table.
	 * @param name table name
	 * @return true if any updates are applied.
	 */
	public boolean update(String name) throws IOException;

	/**
	 * Return the table descriptor, if changes where made to the table this does not need to match actual table. Use
	 * {@see TableOperations#getTableDescriptor(String)} to get the actual, existing table descriptor.
	 * @return
	 */
	public HTableDescriptor getCurrentDescriptor(String name);

	/**
	 * returns the table descriptor of the existing table.
	 * @param name table name
	 * @return
	 */
	public HTableDescriptor getExistingTableDescriptor(String name) throws IOException;

	// /**
	// * Checks if the table exists.
	// * @return returns true if the table exists
	// */
	// public boolean exists();

	/**
	 * Check if the table with the given name exists
	 * @param name table name
	 * @return returns true if the table exists
	 */
	public boolean tableExists(String name);

	/**
	 * Check if a row exists in the given table
	 * @param name table name
	 * @param get GET that contains the row key to look for
	 * @return true if exists
	 */
	public boolean exists(String name, Get get);

	// /**
	// * Get the HTable instance of the table that is implementing the interface
	// * @return
	// */
	// public HTable getTable();

	/**
	 * Get the HTable instance of the table that is implementing the interface
	 * @param name
	 * @return
	 */
	public HTableInterface getTable(String name);

	/**
	 * Put table back in the pool after usage
	 * @param table
	 */
	public void putTable(HTableInterface table);

	/**
	 * Returns the full table name based on some postfix <em>name</em>
	 * @param name table name
	 * @return the full table name: <tt>fixedpart_<em>name</em></tt>
	 */
	public String getTableName(String name);

	/**
	 * GET data from the table with the given name
	 * @param name full name will be generated using getTableName(name)
	 * @param get data object
	 * @return the result set
	 */
	public Result get(String name, Get get);

	/**
	 * PUT data in the table with the given name
	 * @param name full name will be generated using getTableName(name)
	 * @param put data object
	 */
	public void put(String name, Put put);

	/**
	 * PUT multiple data item in the table with the given name
	 * @param name full name will be generated using getTableName(name)
	 * @param put a list of Put object that contain row data
	 */
	public void put(String name, List<Put> put);

	/**
	 * @param name full name will be generated using getTableName(name)
	 * @param delete hbase delete object
	 */
	public void delete(String name, Delete delete);

	/**
	 * @param name full name will be generated using getTableName(name)
	 * @param deletes hbase list of delete objects
	 */
	public void delete(String name, List<Delete> deletes);

}
