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
package nl.gridline.zieook.workflow.api;

/**
 * A set of contants to define some common paths for the REST interface
 * <p />
 * Project zieook-api-data<br />
 * Constants.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public final class Constants
{
	private Constants()
	{

	}

	// ---------------------
	// ---- API Version ----
	// ---------------------

	/**
	 * API base path
	 */
	public static final String APIPATH = "/api/1.0/";

	// ----------------
	// ---- basics ----
	// ----------------

	/**
	 * a content provider
	 */
	public static final String CONTENT_PROVIDER = "cp";

	/**
	 * path variable for content provider
	 */
	public static final String CONTENT_PROVIDER_VAR = "/{" + CONTENT_PROVIDER + "}";

	/**
	 * a recommender
	 */
	public static final String RECOMMENDER = "recommender";

	/**
	 * path variable for recommender
	 */
	public static final String RECOMMENDER_VAR = "/{" + RECOMMENDER + "}";

	/**
	 * a collection
	 */
	public static final String COLLECTION = "collection";

	/**
	 * path variable for collection
	 */
	public static final String COLLECTION_VAR = "/{" + COLLECTION + "}";

	/**
	 * a part - used for uploading collections (userdata, collectiondata, recommendations)
	 */
	public static final String PART = "part";

	/**
	 * path variable for part - used for uploading collections
	 */
	public static final String PART_VAR = "/{" + PART + "}";

	/**
	 * listing resources - start with <tt>list</tt>
	 */
	public static final String LIST = "/list";
	// --------------------
	// ---- base paths ----
	// --------------------

	/**
	 * Collection import methods, base path
	 */
	public static final String COLLECTION_IMPORT = APIPATH + "collection";

}
