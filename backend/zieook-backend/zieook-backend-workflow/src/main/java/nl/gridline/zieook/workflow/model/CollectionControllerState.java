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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.gridline.zieook.commons.ServerState;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * CollectionControllerState.java created 11 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionControllerState extends ServerState
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2746392184763503335L;

	@XmlElement(name = "uptime")
	private String uptime;

	@XmlElement(name = "cp-name-cache-hit")
	private long cacheCpHit;
	@XmlElement(name = "cp-name-cache-miss")
	private long cacheCpMiss;

	public CollectionControllerState()
	{

	}

	public CollectionControllerState(String uptime, long cacheHit, long cacheMiss)
	{
		this.cacheCpHit = cacheHit;
		this.cacheCpMiss = cacheMiss;
		this.uptime = uptime;
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
