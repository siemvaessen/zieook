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
 * ProcessUpdatesCLI.java created 16 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public interface ProcessUpdatesCLI
{
	// @Option(longName = "collection-type", shortName = "ct", description = "collection type (movielens, oai)",
	// defaultValue = "")

	@Option(longName = "update-tables", description = "update hbase tables")
	boolean isUpdateTables();

	@Option(longName = "cleanup-tables", description = "remove all left behin tables")
	boolean isCleanupTables();

	@Option(longName = "user-rating-count", description = "reset user rating count, and count again")
	boolean isUpdateUserRatingCount();
}