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
package nl.gridline.zieook.mapreduce;

/**
 * Contains all hbase constants shared between ZieOok framework & Hadoop data<br />
 * mostly table names and column family names.
 * <p />
 * Project zieook-mapred-commons<br />
 * HBaseColumnFam.java created 17 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public final class HBaseTableConstants
{

	private HBaseTableConstants()
	{
		// hide constructor
	}

	private static final String PREFIX = "zieook_";

	/**
	 * User table name prefix
	 */
	public static final String USERTABLE_NAME = PREFIX + "userdata_";

	/**
	 * User table column for user profile data
	 */
	public static final String USERTABLE_COLUMN_FOAF = "foaf";

	/**
	 * User table column for user rating data
	 */
	public static final String USERTABLE_COLUMN_RATING = "rating";

	/**
	 * Statistics column for recommendation views
	 */
	public static final String RECOMMENDATION_VIEW = "recommendation_views";

	public static final String STATISTICS_TABLE = PREFIX + "statistics_";

	/**
	 * user statistics table
	 */
	public static final String USERSTATTABLE_NAME = PREFIX + "eventlog_";

	/**
	 * user statistics top recommendations
	 */
	public static final String STATS_RECOMMENDED_TOP = "recommended_top";

	/**
	 * user statistics views data family
	 */
	public static final String EVENTLOG_COLUMN_VIEW = "userdata_view";

	/**
	 * user recommendation are logged in this family
	 */
	public static final String EVENTLOG_COLUMN_RECOMMENDED = "userdata_recommended";

	/**
	 * to keep other indexes of a user view
	 */
	public static final String EVENTLOG_COLUMN_USERVIEW_INDEX = "userdata_view_index";

	/**
	 * collection table name prefix
	 */
	public static final String COLLECTION_TABLE = PREFIX + "collection_";

	/**
	 * raw collection data column
	 */
	public static final String COLLECTION_TABLE_COLUMN_RAW = "raw";

	/**
	 * interpreted collection data column (something that can be read into CollectionItem
	 */
	public static final String COLLECTION_TABLE_COLUMN_INTR = "interpreted";

	public static final String COLLECTION_CATEGORY_COLLUMN = "categories";
	public static final String COLLECTION_CATEGORY_NAME = "name";
	public static final String COLLECTION_CATEGORY_COUNT = "count";

	/**
	 * column qualifier for raw collection data
	 */
	public static final String COLLECTION_QUALIFIER_RAW = "raw-data";

	/**
	 * configuration table name
	 */
	public static final String CONFIGURATION_NAME = PREFIX + "config";
	/**
	 * collection filters column
	 */
	public static final String CONFIGURATION_COLUMN_FILTER = "config_filter";

	/**
	 * configuration recommender configuration
	 */
	public static final String CONFIGURATION_COLUMN_RECOMMENDER = "config_recommender";

	/**
	 * template configuration family
	 */
	public static final String CONFIGURATION_COLUMN_TEMPLATE = "config_template";

	/**
	 * collection meta data family
	 */
	public static final String CONFIGURATION_COLUMN_COLLECTIONMETA = "config_collection";

	/**
	 * content provider meta data family
	 */
	public static final String CONFIGURATION_COLUMN_CONTENTPROVIDER = "config_cp";

	/**
	 * zieook tasks configurations
	 */
	public static final String CONFIGURATION_COLUMN_ZIEOOK_TASK = "config_task";

	/**
	 * recommendations table prefix
	 */
	public static final String RECOMMENDATION_NAME = PREFIX + "recommendations_";

	/**
	 * recommendations column family
	 */
	public static final String RECOMMENDATION_COLUMN = "recommendations";

	/**
	 * The qualifier that holds the data for a recommendations
	 */
	public static final String RECOMMENDATION_QUALIFIER = "recommendation";

	// column names for statistics table:
	public static final String STATS_COLLECTION_SOURCE = "stats-collection-source";
	public static final String STATS_VIEWED_SOURCE = "stats-viewed-source";
	public static final String STATS_VIEWED_ITEM = "stats-viewed-item";
	public static final String STATS_RECOMMENDED_USERITEM = "stats-recommended-useritem";
	public static final String STATS_RECOMMENDED_USER = "stats-recommended-user";
	public static final String STATS_ITEM_RATINGS = "stats-item-ratings";
	public static final String STATS_USER_RATINGS = "stats-user-ratings";
	public static final String STATS_ITEM_POPULARITY = "stats-item-popularity";
	public static final String STATS_RECOMMENDER_ITEM = "recommender-item-stats";

	public static final String TIMESTAMP = "timestamp";

	public static final String SUPPORT_TABLE_NAME = PREFIX + "support_table_";
	public static final String DC_IDENTIFIER_COLUMN = "dcidentifier";
	public static final String DC_RELATIONS_COLUMN = "dcrelations";
	public static final String DC_IDENTIFIERS_COLUMN = "dcidentifiers";

}
