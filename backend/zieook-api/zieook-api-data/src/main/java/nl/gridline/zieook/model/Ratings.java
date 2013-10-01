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
 * Ratings.java created 1 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "ratings")
@XmlAccessorType(XmlAccessType.FIELD)
public class Ratings implements Serializable, JSonParent
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6009577966476199067L;

	@XmlElement(name = "user_rating")
	private List<Rating> ratings;

	public Ratings()
	{

	}

	public Ratings(List<Rating> users)
	{
		ratings = users;
	}

	/**
	 * @return The users.
	 */
	public List<Rating> getRatings()
	{
		return ratings;
	}

	/**
	 * @param users The users to set.
	 */
	public void setRatings(List<Rating> ratings)
	{
		this.ratings = ratings;
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
		result = prime * result + ((ratings == null) ? 0 : ratings.hashCode());
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
		Ratings other = (Ratings) obj;
		if (ratings == null)
		{
			if (other.ratings != null)
			{
				return false;
			}
		}
		else if (!ratings.equals(other.ratings))
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

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder("[");
		for (Rating r : ratings)
		{
			b.append(r).append(",");
		}
		b.deleteCharAt(b.length() - 1);
		return b.toString();
	}

}
