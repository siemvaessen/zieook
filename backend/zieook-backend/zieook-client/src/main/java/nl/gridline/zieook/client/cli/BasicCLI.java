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
 * Basic command-line options that hold for most of the descending classes.
 * <p />
 * Project zieook-client<br />
 * BasicCLI.java created Sep 16, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public interface BasicCLI
{

	/**
	 * <tt>content-provider</tt> or <tt>cp</tt><br />
	 * a content provider name
	 * @return a string containing the name of the content provider
	 */
	@Option(longName = "content-provider", shortName = "cp", description = "content provider name", defaultValue = "")
	String getContentProvider();

	/**
	 * <tt>recommender</tt> or <tt>r</tt><br />
	 * a recommender name
	 * @return a string containing the name of the recommender
	 */
	@Option(longName = "recommender", shortName = "r", description = "recommender name", defaultValue = "")
	String getRecommender();

	/**
	 * <tt>collection</tt> or <tt>c</tt><br />
	 * a collection name
	 * @return a string containing the name of the collection
	 */
	@Option(longName = "collection", shortName = "c", description = "collection name", defaultValue = "")
	String getCollection();

	/**
	 * @return
	 */
	@Option(helpRequest = true, description = "display help", shortName = "h", longName = "help")
	boolean getHelp();

}
