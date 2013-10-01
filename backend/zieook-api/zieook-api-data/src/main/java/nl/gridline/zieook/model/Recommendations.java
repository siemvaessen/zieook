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
 * The list of ratings for a user on a specified recommender and from a specified content provider
 * user-based or item based.
 * If it's user based the <tt>id</tt> is a user, if it's item based the <tt>id</tt> is an item.
 * <p />
 * Project zieook-api-data<br />
 * Ratings.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "recommendations")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recommendations implements Serializable, JSonParent
{
	/** 
	 * 
	 */
	private static final long serialVersionUID = 130342398473542621L;

	@XmlElement
	private long id;
	@XmlElement
	private String cp;
	@XmlElement
	private String recommender;
	@XmlElement
	private String collection;

	@XmlElement(name = "start_date")
	private String startdate;
	@XmlElement(name = "end_date")
	private String enddate;
	@XmlElement
	private String type;

	@XmlElement(name = "date")
	private long date;

	@XmlElement
	private long size;

	@XmlElement(name = "recommended")
	private List<Recommend> recommended;

	public Recommendations()
	{
		// no-arg constructor is required
	}

	public Recommendations(long id, String cp, String collection, String recommender, int size,
			List<Recommend> recommended)
	{
		this.id = id;
		this.cp = cp;
		this.collection = collection;
		this.recommender = recommender;
		this.size = size;
		this.recommended = recommended;
	}

	// public Recommendations(NavigableMap<byte[], byte[]> map)
	// {
	// id = ModelConstants.getUser(map);
	// cp = ModelConstants.getCp(map);
	// recommender = ModelConstants.getRecommender(map);
	// size = ModelConstants.getSize(map);
	// date = ModelConstants.getStamp(map);
	// }
	//
	// public NavigableMap<byte[], byte[]> toMap()
	// {
	// NavigableMap<byte[], byte[]> map = new TreeMap<byte[], byte[]>(Bytes.BYTES_COMPARATOR);
	// ModelConstants.putUser(map, id);
	// ModelConstants.putCp(map, cp);
	// ModelConstants.putRecommender(map, recommender);
	// ModelConstants.putSize(map, size);
	// ModelConstants.putStamp(map, date);
	// return map;
	// }

	public Recommendations(String id, String cp, String collection, String recommender, String startdate,
			String enddate, long date, int size, List<Recommend> recommended)
	{

		this.id = Long.parseLong(id);
		this.cp = cp;
		this.collection = collection;
		this.recommender = recommender;
		this.date = date;
		this.startdate = startdate;
		this.enddate = enddate;
		this.size = size;
		this.recommended = recommended;
	}

	public long getUser()
	{
		return id;
	}

	public void setUser(long user)
	{
		id = user;
	}

	public String getCp()
	{
		return cp;
	}

	public void setCp(String cp)
	{
		this.cp = cp;
	}

	public String getRecommender()
	{
		return recommender;
	}

	public void setRecommender(String recommender)
	{
		this.recommender = recommender;
	}

	public String getStartdate()
	{
		return startdate;
	}

	public void setStartdate(String startdate)
	{
		this.startdate = startdate;
	}

	public String getEnddate()
	{
		return enddate;
	}

	public void setEnddate(String enddate)
	{
		this.enddate = enddate;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

	public long getDate()
	{
		return date;
	}

	public void setDate(long date)
	{
		this.date = date;
	}

	public List<Recommend> getRecommended()
	{
		return recommended;
	}

	public void setRecommended(List<Recommend> recommended)
	{
		this.recommended = recommended;
	}

	public String getCollection()
	{
		return collection;
	}

	public void setCollection(String collection)
	{
		this.collection = collection;
	}

	/**
	 * @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return new StringBuilder().append("user=").append(id).append(" cp=").append(cp).append(" collection=")
				.append(collection).append(" recommender=").append(recommender).append("startdate=").append(startdate)
				.append("enddate=").append(enddate).append(" size=").append(size).toString();

	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
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
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Recommendations other = (Recommendations) obj;
		if (id != other.id)
		{
			return false;
		}
		return true;
	}
}
