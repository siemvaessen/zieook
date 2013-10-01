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
package nl.gridline.zieook.commons;

/**
 * ZieOok manager interface
 * <p />
 * Project zieook-commons<br />
 * Manager.java created 15 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public interface ZieOokManager
{
	/**
	 * life-cycle startup of a manager
	 * @throws Exception
	 */
	public void startup() throws Exception;

	/**
	 * life-cycle shutdown of a manager
	 * @throws Exception
	 */
	public void shutdown() throws Exception;

	/**
	 * state of a manager: true if ok, false is in error (or startup has to be called)
	 * @return true if startup successfully called
	 */
	public boolean state();

	/**
	 * Return a String with that reports the state of this manager.
	 * @return
	 */
	@Override
	public String toString();

	/**
	 * Returns a map that contains status information about the manager.
	 * @return
	 */
	public ServerState getState();
}
