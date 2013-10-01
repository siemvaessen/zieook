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
 * The collection table is one over the very large tables:
 * It will contain all collections using a key: <tt>cp.collection.item</tt> And, for now, only two columns: the raw data
 * and the interpreted data, the second column contains all fields that actually handle data.
 * <p />
 * Project zieook-data<br />
 * CollectionTable.java created 9 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class HBaseCollectionTable extends AbstractHBaseTable
{

	// private static final Logger LOG = LoggerFactory.getLogger(HBaseCollectionTable.class);

	public static final String NAME = HBaseTableConstants.COLLECTION_TABLE;
	private static final String COLLECTION_TABLE_COLUMN_RAW = HBaseTableConstants.COLLECTION_TABLE_COLUMN_RAW;
	private static final String COLLECTION_TABLE_COLUMN_INTR = HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR;
	private static final String COLLECTION_CATEGORY_COLLUMN = HBaseTableConstants.COLLECTION_CATEGORY_COLLUMN;

	public HBaseCollectionTable(HBaseManager manager)
	{
		super(manager);

	}

	@Override
	public final String getTableName(String cp)
	{
		return NAME + cp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.data.hbase.TableOperations#getCurrentDescriptor()
	 */
	@Override
	public HTableDescriptor getCurrentDescriptor(String name)
	{
		// table descriptor:
		HTableDescriptor descriptor = new HTableDescriptor(getTableName(name));

		// column family:
		HColumnDescriptor rawdata = new HColumnDescriptor(COLLECTION_TABLE_COLUMN_RAW);
		rawdata.setMaxVersions(1); // imported items don't change, so we only allow one version

		HColumnDescriptor interpreted = new HColumnDescriptor(COLLECTION_TABLE_COLUMN_INTR);
		HColumnDescriptor categories = new HColumnDescriptor(COLLECTION_CATEGORY_COLLUMN);
		// the interpretation does change
		// rawdata.setMaxVersions(3); <- this is the default

		// It is probably handy to have this information available through an extra
		// index. Although it only(!) determines if this column is available, which is nice if we do interpretation
		// after import
		// interpreted.setBloomfilter(true);

		descriptor.addFamily(rawdata);
		descriptor.addFamily(interpreted);
		descriptor.addFamily(categories);

		return descriptor;
	}

}
