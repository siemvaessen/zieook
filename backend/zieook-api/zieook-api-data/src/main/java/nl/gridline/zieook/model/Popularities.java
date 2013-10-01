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

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * Popularities.java created 13 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "popularities")
@XmlAccessorType(XmlAccessType.FIELD)
public class Popularities implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2721185703822309694L;

	@XmlElement(name = "popularity")
	private List<Popularity> popularity;

	public Popularities()
	{

	}

	public Popularities(List<Popularity> popularity)
	{
		this.popularity = popularity;
	}

	/**
	 * @return The popularity.
	 */
	public List<Popularity> getPopularity()
	{
		return popularity;
	}

	/**
	 * @param popularity The popularity to set.
	 */
	public void setPopularity(List<Popularity> popularity)
	{
		this.popularity = popularity;
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
		result = prime * result + ((popularity == null) ? 0 : popularity.hashCode());
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
		Popularities other = (Popularities) obj;
		if (popularity == null)
		{
			if (other.popularity != null)
			{
				return false;
			}
		}
		else if (!popularity.equals(other.popularity))
		{
			return false;
		}
		return true;
	}

}
