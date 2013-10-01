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
 * The output of a generated recommender is put in this table or tables, every cp/collection will get it's own table.
 * <p />
 * Typically these tables will grow large and are only relevant to a cp/collection
 * <p />
 * Project zieook-data<br />
 * HBaseRecommendationTable.java created 5 jan. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class HBaseRecommendationTable extends AbstractHBaseTable
{
	// private static final Logger LOG = LoggerFactory.getLogger(HBaseRecommendationTable.class);

	public static final String NAME = HBaseTableConstants.RECOMMENDATION_NAME;
	public static final String RECOMMENDATION_COLUMN = HBaseTableConstants.RECOMMENDATION_COLUMN;

	// public static final String RECOMMENDER_META_COLUMN = HBaseTableConstants.RECOMMENDER_META_COLUMN;

	public HBaseRecommendationTable(HBaseManager manager)
	{
		super(manager);
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

		// column family:
		HColumnDescriptor recomColumn = new HColumnDescriptor(RECOMMENDATION_COLUMN);
		// HColumnDescriptor metaColumn = new HColumnDescriptor(RECOMMENDER_META_COLUMN);

		// the interpretation does change
		// rawdata.setMaxVersions(3); <- this is the default

		// It is probably handy to have this information available through an extra
		// index. Although it only(!) determines if this column is available, which is nice if we do interpretation
		// after import
		// interpreted.setBloomfilter(true);

		descriptor.addFamily(recomColumn);
		// recommendations.addFamily(metaColumn);

		return descriptor;
	}

}
