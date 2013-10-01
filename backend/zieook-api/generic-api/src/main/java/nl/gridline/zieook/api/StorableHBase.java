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
package nl.gridline.zieook.api;

import java.io.IOException;
import java.util.NavigableMap;

/**
 * [purpose]
 * <p />
 * Project zieook-api-workflow<br />
 * StorableHBase.java created 19 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public interface StorableHBase
{
	/**
	 * return the object in a navigable map, of the type that can be written to HBase directly
	 * @return
	 */
	NavigableMap<byte[], byte[]> toMap() throws IOException;

	/**
	 * add the object to the given map, and return it.
	 * the navigable map should be of the type that can be written to HBAse directly,
	 * this one.
	 * @param map an existing map, to which the object should be appended.
	 * @return the extended map, (same object)
	 */
	NavigableMap<byte[], byte[]> toMap(NavigableMap<byte[], byte[]> map) throws IOException;

	/**
	 * return the hbase row key
	 * @return
	 */
	byte[] getRow();
}
