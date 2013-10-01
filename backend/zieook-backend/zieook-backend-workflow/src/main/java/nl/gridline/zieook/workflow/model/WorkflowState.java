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
package nl.gridline.zieook.workflow.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.gridline.zieook.commons.ServerState;
import nl.gridline.zieook.model.DataManagerSettings;

/**
 * Holds the state of the workflow manager and the generic server state in a POJO serializable to XML,JSON
 * <p />
 * Project zieook-backend-workflow<br />
 * WorkflowState.java created 4 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowState extends ServerState
{

	@XmlElement(name = "zookeeper")
	private String zooKeeper;
	@XmlElement(name = "configuration-file")
	private String configurationFile;
	@XmlElement(name = "settings")
	private DataManagerSettings settings;

	@XmlElement(name = "table_names")
	private List<String> tablenames;

	@XmlElement(name = "content_providers")
	private List<String> contentproviders;

	@XmlElement(name = "recommenders")
	private List<String> recommenders;

	@XmlElement(name = "uptime")
	private String uptime;

	@XmlElement(name = "cp-name-cache-hit")
	private long cacheCpHit;
	@XmlElement(name = "cp-name-cache-miss")
	private long cacheCpMiss;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1454017944479807736L;

	public WorkflowState()
	{
		// no-arg constructor
	}

	/**
	 * @return The zooKeeper.
	 */
	public String getZooKeeper()
	{
		return zooKeeper;
	}

	/**
	 * @param zooKeeper The zooKeeper to set.
	 */
	public void setZooKeeper(String zooKeeper)
	{
		this.zooKeeper = zooKeeper;
	}

	/**
	 * @return The configurationFile.
	 */
	public String getConfigurationFile()
	{
		return configurationFile;
	}

	/**
	 * @param configurationFile The configurationFile to set.
	 */
	public void setConfigurationFile(String configurationFile)
	{
		this.configurationFile = configurationFile;
	}

	/**
	 * @return The settings.
	 */
	public DataManagerSettings getSettings()
	{
		return settings;
	}

	/**
	 * @param settings The settings to set.
	 */
	public void setSettings(DataManagerSettings settings)
	{
		this.settings = settings;
	}

	/**
	 * @return The tablenames.
	 */
	public List<String> getTablenames()
	{
		return tablenames;
	}

	/**
	 * @param tablenames The tablenames to set.
	 */
	public void setTablenames(List<String> tablenames)
	{
		this.tablenames = tablenames;
	}

	public List<String> getContenProviders()
	{
		return contentproviders;
	}

	public void setContentProviders(List<String> contentproviders)
	{
		this.contentproviders = contentproviders;
	}

	public List<String> getRecommenders()
	{
		return recommenders;
	}

	public void setRecommender(List<String> recommenders)
	{
		this.recommenders = recommenders;
	}

	/**
	 * @return The uptime.
	 */
	public String getUptime()
	{
		return uptime;
	}

	/**
	 * @param uptime The uptime to set.
	 */
	public void setUptime(String uptime)
	{
		this.uptime = uptime;
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
