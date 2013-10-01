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
package nl.gridline.zieook.client.cli;

import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * ZieOok HBase commandline options
 * <p />
 * Project zieook-client<br />
 * HBaseCLI.java created Sep 16, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public interface HBaseCLI extends BasicCLI
{
	/**
	 * <tt>drop-all-tables</tt><br />
	 * will drop all zieook data from the cluster, you will have to start over all again. This
	 * option should be used with great care(!)
	 * @return <tt>true</tt> if set
	 */
	@Option(longName = "drop-all-tables", description = "disable and drop all hbase tables, this will destroy all data in the zieook cluster")
	boolean isDropAllTable();

	/**
	 * <tt>init-zieook</tt><br />
	 * init's the zieook configuration table, and creates basic data.
	 * @return <tt>true</tt> if set.
	 */
	@Option(longName = "init-zieook", description = "init the zieook configuration table, the table that contains basic zieook data")
	boolean isInitZieOok();

	/**
	 * <tt>show-recommenders</tt><br />
	 * will show the recommenders in the system
	 * @return <tt>true</tt> if set.
	 */
	@Option(longName = "show-recommenders", description = "show all recommender available in the system")
	boolean isReadRecommender();

	/**
	 * <tt>create-templates</tt><br />
	 * creates the zieook recommender templates, templates are used to base a recommender on. They contain the default
	 * settings.
	 * @return <tt>true</tt> if set.
	 */
	@Option(longName = "create-templates", description = "create the zieook recommender templates")
	boolean isCreateTemplates();

	/**
	 * <tt>delete-recommender</tt> <br />
	 * Delete a ZieOok recommender, this option can only be used in combination with the <tt>content-provider</tt>,
	 * <tt>collection</tt> and <tt>recommender</tt> options
	 * @return <tt>true</tt> if set.
	 */
	@Option(longName = "delete-recommender", description = "delete a zieook recommender, should be augmented with a content-provider, collection and recommender name")
	boolean isDeleteRecommender();

	/**
	 * <tt>show-tables</tt><br />
	 * Show ZieOok table names from HBase
	 * @return <tt>true</tt> if set
	 */
	@Option(longName = "show-tables", description = "show ZieOok related tables in HBase")
	boolean isShowTable();

}
