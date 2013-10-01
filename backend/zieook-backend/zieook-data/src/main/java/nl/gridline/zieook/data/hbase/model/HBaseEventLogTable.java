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
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * HBaseUserStats.java created 6 jan. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class HBaseEventLogTable extends AbstractHBaseTable
{

	// private static final Logger LOG = LoggerFactory.getLogger(HBaseEventLogTable.class);

	public HBaseEventLogTable(HBaseManager manager)
	{
		super(manager);
	}

	public HBaseEventLogTable()
	{
		super(null);
	}

	public static final String NAME = HBaseTableConstants.USERSTATTABLE_NAME;

	public static final String EVENTLOG_COLUMN_USERVIEW = HBaseTableConstants.EVENTLOG_COLUMN_VIEW;
	public static final String EVENTLOG_COLUMN_RECOMMENDED = HBaseTableConstants.EVENTLOG_COLUMN_RECOMMENDED;
	public static final String EVENTLOG_COLUMN_USERVIEW_INDEX = HBaseTableConstants.EVENTLOG_COLUMN_USERVIEW_INDEX;

	@Override
	public String getTableName(String cp)
	{
		return NAME + cp;
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
		HColumnDescriptor view = new HColumnDescriptor(EVENTLOG_COLUMN_USERVIEW);
		HColumnDescriptor recommended = new HColumnDescriptor(EVENTLOG_COLUMN_RECOMMENDED);
		HColumnDescriptor viewindex = new HColumnDescriptor(EVENTLOG_COLUMN_USERVIEW_INDEX);

		descriptor.addFamily(view);
		descriptor.addFamily(recommended);
		descriptor.addFamily(viewindex);
		return descriptor;
	}

}
