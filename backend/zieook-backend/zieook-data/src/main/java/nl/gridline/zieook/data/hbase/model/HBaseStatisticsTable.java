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
 * HBaseStatisticsTable.java created 27 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class HBaseStatisticsTable extends AbstractHBaseTable
{
	// private static final Logger LOG = LoggerFactory.getLogger(HBaseStatisticsTable.class);

	public static final String STATS_COLLECTION_SOURCE = HBaseTableConstants.STATS_COLLECTION_SOURCE;
	public static final String STATS_VIEWED_SOURCE = HBaseTableConstants.STATS_VIEWED_SOURCE;
	public static final String STATS_VIEWED_ITEM = HBaseTableConstants.STATS_VIEWED_ITEM;
	public static final String STATS_ITEM_RATINGS = HBaseTableConstants.STATS_ITEM_RATINGS;
	public static final String STATS_USER_RATINGS = HBaseTableConstants.STATS_USER_RATINGS;
	private static final String STATS_RECOMMENDED_USERITEM = HBaseTableConstants.STATS_RECOMMENDED_USERITEM;
	private static final String STATS_RECOMMENDED_USER = HBaseTableConstants.STATS_RECOMMENDED_USER;
	private static final String STATS_ITEM_POPULARITY = HBaseTableConstants.STATS_ITEM_POPULARITY;

	private static final String NAME = HBaseTableConstants.STATISTICS_TABLE;

	/**
	 * @param manager
	 */
	public HBaseStatisticsTable(HBaseManager manager)
	{
		super(manager);
	}

	public HBaseStatisticsTable()
	{
		super(null);
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

		HColumnDescriptor sourcesStats = new HColumnDescriptor(STATS_COLLECTION_SOURCE);
		HColumnDescriptor viewSourcesStats = new HColumnDescriptor(STATS_VIEWED_SOURCE);
		HColumnDescriptor recommendedUserItemStats = new HColumnDescriptor(STATS_RECOMMENDED_USERITEM);
		HColumnDescriptor recommendedUserCollection = new HColumnDescriptor(STATS_RECOMMENDED_USER);
		HColumnDescriptor itemRatingStats = new HColumnDescriptor(STATS_ITEM_RATINGS);
		HColumnDescriptor userRatingStats = new HColumnDescriptor(STATS_USER_RATINGS);
		HColumnDescriptor viewedStats = new HColumnDescriptor(STATS_VIEWED_ITEM);
		HColumnDescriptor popularity = new HColumnDescriptor(STATS_ITEM_POPULARITY);

		descriptor.addFamily(recommendedUserCollection);
		descriptor.addFamily(sourcesStats);
		descriptor.addFamily(viewedStats);
		descriptor.addFamily(viewSourcesStats);
		descriptor.addFamily(recommendedUserItemStats);
		descriptor.addFamily(itemRatingStats);
		descriptor.addFamily(userRatingStats);
		descriptor.addFamily(popularity);

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
