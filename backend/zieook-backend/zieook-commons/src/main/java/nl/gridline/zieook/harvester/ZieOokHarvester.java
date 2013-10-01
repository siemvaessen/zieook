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
package nl.gridline.zieook.harvester;

/**
 * Interface for harvester classes
 * <p />
 * Project zieook-commons<br />
 * Harvester.java created 23 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public interface ZieOokHarvester
{

	/**
	 * Get the source location from where to harvest, file, url etc.
	 * @return
	 */
	public String getSource();

	/**
	 * Set the source location from where to harvest, file, url etc.
	 * @param source
	 */
	public void setSource(String source);

	/**
	 * Start the harvest method, events will be written to the HarvestEvent object passed
	 * @return returns true if harvesting completed successfully
	 */
	public boolean startHarvest();

	/**
	 * Set the harvester event handler
	 * @param event
	 */
	public void setHarvestEvent(ZieOokHarvesterEvent event);

	/**
	 * Get the harvester event handler
	 * @return
	 */
	public ZieOokHarvesterEvent getHarvestEvent();

}
