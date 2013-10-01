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
 * [purpose]
 * <p />
 * Project zieook-client<br />
 * RecommendersCLI.java created Sep 29, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public interface WorkFlowCLI extends BasicCLI
{

	// /**
	// * <tt>server</tt><br />
	// * the server (url of the form: http://host:port) to connect to
	// * @return the server given
	// */
	// @Option(longName = "server", shortName = "s", description = "zieook workflow server", defaultValue = "")
	// String getServer();

	/**
	 * <tt>collection-type</tt><br />
	 * @return
	 */
	@Option(longName = "collection-type", shortName = "ct", description = "collection type (movielens, oai)", defaultValue = "")
	String getCollectionType();

	/**
	 * <tt>collection-location</tt><br />
	 * @return
	 */
	@Option(longName = "collection-location", shortName = "cl", description = "collection location, if oai this should be a url", defaultValue = "")
	String getCollectionLocation();

	/**
	 * <tt>collection-interval</tt><br />
	 * import update interval in seconds
	 * @return a long value
	 */
	@Option(longName = "collection-interval", shortName = "ci", description = "collection import interval in seconds > 0", defaultValue = "0")
	long getCollectionInterval();

	/**
	 * <tt>recommender-interval</tt><br />
	 * @return
	 */
	@Option(longName = "recommender-interval", shortName = "ri", description = "recommender interval in seconds > 0", defaultValue = "0")
	long getRecommenderInterval();

	/**
	 * <tt>recommender-next</tt><br />
	 * @return
	 */
	@Option(longName = "recommender-next", shortName = "rn", description = "a time in the future seconds the recommender should be executed", defaultValue = "0")
	long getRecommenderNext();

	/**
	 * <tt>create-cp</tt> <br />
	 * create a content provider at the given <em>server</em>
	 * @return <tt>true</tt> is set
	 */
	@Option(longName = "create-cp", description = "create a content provider")
	boolean isContentProvider();

	/**
	 * <tt>drop-cp</tt><br />
	 * delete a content provider
	 * @return
	 */
	@Option(longName = "drop-cp", description = "delete a content provider")
	boolean isDeleteCP();

	/**
	 * <tt>show-cp</tt> <br />
	 * show a content provider, if a content provider name is passed, otherwise show a list of content
	 * providers.
	 * @return
	 */
	@Option(longName = "show-cp", description = "")
	boolean isShowCP();

	/**
	 * <tt>create-collection</tt><br />
	 * create a collection. Needs a content provider and collection name argument
	 * @return
	 */
	@Option(longName = "create-collection", description = "create a collection")
	boolean isCreateCollection();

	/**
	 * <tt>delete-collection</tt><br />
	 * delete a collection. Needs a content provider and collection name argument
	 * @return
	 */
	@Option(longName = "delete-collection", description = "delete a collection")
	boolean isDeleteCollection();

	/**
	 * <tt>show-collection</tt>
	 * @return
	 */
	@Option(longName = "show-collection", description = "delete a collection")
	boolean isShowCollection();

	/**
	 * <tt>create-recommender</tt>
	 * @return
	 */
	@Option(longName = "create-recommender", description = "create a recommender")
	boolean isCreateRecommender();

	/**
	 * <tt>delete-recommender</tt>
	 * @return
	 */
	@Option(longName = "delete-recommender", description = "delete a recommender")
	boolean isDeleteRecommender();

	/**
	 * <tt>show-recommender</tt>
	 * @return
	 */
	@Option(longName = "show-recommender", description = "show a recommender")
	boolean isShowRecommender();

	@Option(longName = "execute-statistics", description = "run statistics task for a content provider, now")
	boolean isExecuteStatistics();

}
