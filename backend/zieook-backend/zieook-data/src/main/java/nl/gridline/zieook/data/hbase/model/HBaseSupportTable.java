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

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;

/**
 * A support table, that can contain all kinds of data for a CP, currently it stores 'dc:relations' for AmsterdamMuseum
 * data.
 * <p />
 * Project zieook-data<br />
 * HBaseSupportTable.java created 30 jan. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class HBaseSupportTable extends AbstractHBaseTable
{

	public static final String NAME = HBaseTableConstants.SUPPORT_TABLE_NAME;

	public static final String DC_IDENTIFIER_COLUMN = HBaseTableConstants.DC_IDENTIFIER_COLUMN;
	public static final String DC_RELATIONS_COLUMN = HBaseTableConstants.DC_RELATIONS_COLUMN;
	public static final String DC_IDENTIFIERS_COLUMN = HBaseTableConstants.DC_IDENTIFIERS_COLUMN;

	/**
	 * @param manager
	 */
	public HBaseSupportTable(HBaseManager manager)
	{
		super(manager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.data.hbase.TableOperations#getCurrentDescriptor(java.lang.String)
	 */
	@Override
	public HTableDescriptor getCurrentDescriptor(String name)
	{
		HTableDescriptor descriptor = new HTableDescriptor(getTableName(name));
		HColumnDescriptor identifier = new HColumnDescriptor(DC_IDENTIFIER_COLUMN);
		HColumnDescriptor relations = new HColumnDescriptor(DC_RELATIONS_COLUMN);
		HColumnDescriptor identifiers = new HColumnDescriptor(DC_IDENTIFIERS_COLUMN);

		descriptor.addFamily(identifier);
		descriptor.addFamily(relations);
		descriptor.addFamily(identifiers);

		return descriptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.data.hbase.TableOperations#getTableName(java.lang.String)
	 */
	@Override
	public String getTableName(String cp)
	{
		return NAME + cp;
	}

}
