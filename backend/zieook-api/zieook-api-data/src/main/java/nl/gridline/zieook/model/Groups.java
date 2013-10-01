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
package nl.gridline.zieook.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.gridline.zieook.api.JSonParent;

@XmlRootElement(name = "groups")
@XmlAccessorType(XmlAccessType.FIELD)
public class Groups implements Serializable, JSonParent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3547322716149219342L;

	@XmlElement(name = "group")
	private List<GroupedData> groups;

	public Groups()
	{

	}

	public Groups(List<GroupedData> sources)
	{
		this.groups = sources;
	}

	/**
	 * @return the sources
	 */
	public List<GroupedData> getGroups()
	{
		return groups;
	}

	/**
	 * @param groups the sources to set
	 */
	public void setGroups(List<GroupedData> groups)
	{
		this.groups = groups;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groups == null) ? 0 : groups.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		Groups other = (Groups) obj;
		if (groups == null)
		{
			if (other.groups != null)
			{
				return false;
			}
		}
		else if (!groups.equals(other.groups))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}

}
