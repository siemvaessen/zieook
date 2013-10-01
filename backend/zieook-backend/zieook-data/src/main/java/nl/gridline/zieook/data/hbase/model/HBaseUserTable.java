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
 * User table row: cp.recommender.user<br />
 * Or for ratings: cp.recommender.user.item<br />
 * The table contains all user centric data.
 * <p />
 * Project zieook-data<br />
 * UserTable.java created 9 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class HBaseUserTable extends AbstractHBaseTable
{

	// private static final Logger LOG = LoggerFactory.getLogger(HBaseUserTable.class);

	private static final String NAME = HBaseTableConstants.USERTABLE_NAME;
	private static final String COLUMN_FOAF = HBaseTableConstants.USERTABLE_COLUMN_FOAF;
	private static final String COLUMN_RATING = HBaseTableConstants.USERTABLE_COLUMN_RATING;

	public HBaseUserTable(HBaseManager manager)
	{
		super(manager);
	}

	public HBaseUserTable()
	{
		super(null);
	}

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
		// table descriptor:
		HTableDescriptor descriptor = new HTableDescriptor(getTableName(name));

		// // column family:
		// HColumnDescriptor name = new HColumnDescriptor(TEMPLATE_TABLE_COLUMN_NAME);
		// name.setMaxVersions(1); // the template name will be a fixed value
		// name.setBloomfilter(true); // we need to be able to find this information quickly

		HColumnDescriptor foaf = new HColumnDescriptor(COLUMN_FOAF);
		// depending on the tests we may need to add some details on these columns:
		HColumnDescriptor rating = new HColumnDescriptor(COLUMN_RATING);

		// It is probably handy to have this information available through an extra
		// index. Although it only(!) determines if this column is available, which is nice if we do interpretation
		// after import
		// collectionTable.addFamily(name);

		descriptor.addFamily(foaf);
		descriptor.addFamily(rating);

		return descriptor;
	}

}
