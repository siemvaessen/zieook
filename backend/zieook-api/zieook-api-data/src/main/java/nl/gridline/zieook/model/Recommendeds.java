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

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * Recommendeds.java created 1 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "recommended")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recommendeds implements Serializable, JSonParent
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1602676014631492851L;

	@XmlElement(name = "recommended")
	private List<Recommended> recommended;

	public Recommendeds()
	{

	}

	public Recommendeds(List<Recommended> recommended)
	{
		this.recommended = recommended;
	}

	/**
	 * @return The recommended.
	 */
	public List<Recommended> getRecommended()
	{
		return recommended;
	}

	/**
	 * @param recommended The recommended to set.
	 */
	public void setRecommended(List<Recommended> recommended)
	{
		this.recommended = recommended;
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
		result = prime * result + ((recommended == null) ? 0 : recommended.hashCode());
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
		Recommendeds other = (Recommendeds) obj;
		if (recommended == null)
		{
			if (other.recommended != null)
			{
				return false;
			}
		}
		else if (!recommended.equals(other.recommended))
		{
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.JSonParent#toJSON()
	 */
	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}

}
