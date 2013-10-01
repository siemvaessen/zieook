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
package nl.gridline.zieook.data.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import nl.gridline.zieook.commons.ServerState;
import nl.gridline.zieook.model.DataManagerSettings;

/**
 * Data controller state contains a whole bunch information about the data controller in XML or JSON
 * <p />
 * Project zieook-backend-data<br />
 * DataControllerState.java created 3 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DataControllerState extends ServerState
{

	@XmlElement(name = "zookeeper")
	private String zooKeeper;
	@XmlElement(name = "configuration-file")
	private String configurationFile;
	@XmlElement(name = "settings")
	private DataManagerSettings settings;

	@XmlElementWrapper(name = "tables")
	@XmlElement(name = "name")
	private List<String> tablenames;

	@XmlElement(name = "uptime")
	private String uptime;

	@XmlElement(name = "cp-name-cache-hit")
	private long cacheCpHit;
	@XmlElement(name = "cp-name-cache-miss")
	private long cacheCpMiss;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2948529852580809757L;

	public DataControllerState()
	{

	}

	public List<String> getTableNames()
	{
		return tablenames;
	}

	public void setTableName(List<String> tablenames)
	{
		this.tablenames = tablenames;
	}

	public String getUptime()
	{
		return uptime;
	}

	public String setUptime(String uptime)
	{
		return uptime;
	}

	public String getConfigurationFile()
	{
		return configurationFile;
	}

	public void setConfigurationFile(String configurationFile)
	{
		this.configurationFile = configurationFile;
	}

	public String getZooKeeper()
	{
		return zooKeeper;
	}

	public void setZooKeeper(String zooKeeper)
	{
		this.zooKeeper = zooKeeper;
	}

	public DataManagerSettings getSettings()
	{
		return settings;
	}

	public void setSettings(DataManagerSettings settings)
	{
		this.settings = settings;
	}

	/**
	 * @return The cacheCpHit.
	 */
	public long getCacheCpHit()
	{
		return cacheCpHit;
	}

	/**
	 * @param cacheCpHit The cacheCpHit to set.
	 */
	public void setCacheCpHit(long cacheCpHit)
	{
		this.cacheCpHit = cacheCpHit;
	}

	/**
	 * @return The cacheCpMiss.
	 */
	public long getCacheCpMiss()
	{
		return cacheCpMiss;
	}

	/**
	 * @param cacheCpMiss The cacheCpMiss to set.
	 */
	public void setCacheCpMiss(long cacheCpMiss)
	{
		this.cacheCpMiss = cacheCpMiss;
	}

}
