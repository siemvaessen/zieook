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
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.gridline.zieook.api.JSonParent;
import nl.gridline.zieook.api.StorableHBase;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * A user, currently it only has an id,name and age. Future implementations may extend this. The equals and hashcode
 * methods are based on the <tt>id</tt> only!
 * <p />
 * Project zieook-api-data<br />
 * User.java created 14 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User implements Serializable, JSonParent, StorableHBase
{
	/**
	 *  
	 */
	private static final long serialVersionUID = -865083254385646160L;

	@XmlElement(required = true)
	private Long id;
	@XmlElement(required = false)
	private String name;
	@XmlElement(required = false)
	private String birthday;
	@XmlElement(required = false)
	private String gender;
	@XmlElement(required = false)
	private String occupation;
	@XmlElement(required = false)
	private String zipcode;
	@XmlElement(required = false)
	private String city;
	@XmlElement(required = false)
	private String country;

	@XmlElement(name = "number_of_ratings", required = false)
	private Long ratingCount;

	@XmlElement(name = "last_rated_date", required = false)
	private Long lastRated;

	@XmlElement(name = "number_of_views", required = false)
	private Long viewCount;

	@XmlElement(name = "last_viewed_date", required = false)
	private Long lastViewed;

	@XmlElement(name = "number_of_recommendations", required = false)
	private Long recommendCount;

	@XmlElement(name = "last_recommend_date", required = false)
	private Long lastRecommend;

	public User()
	{

	}

	public User(NavigableMap<byte[], byte[]> map)
	{
		id = ModelConstants.getUser(map);
		name = ModelConstants.getName(map);
		birthday = ModelConstants.getAge(map);
		gender = ModelConstants.getGender(map);
		occupation = ModelConstants.getOccupation(map);
		zipcode = ModelConstants.getZipCode(map);
		city = ModelConstants.getCity(map);
		country = ModelConstants.getCountry(map);
		ratingCount = ModelConstants.getCount(map);
		lastRated = ModelConstants.getLastRatedDate(map);
		viewCount = ModelConstants.getViewCount(map);
		lastViewed = ModelConstants.getLastViewedDate(map);
		recommendCount = ModelConstants.getRecommendCount(map);
		lastRecommend = ModelConstants.getLastRecommendDate(map);
	}

	@Override
	public NavigableMap<byte[], byte[]> toMap()
	{
		return toMap(new TreeMap<byte[], byte[]>(Bytes.BYTES_COMPARATOR));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.StorableHBase#toMap(java.util.NavigableMap)
	 */
	@Override
	public NavigableMap<byte[], byte[]> toMap(NavigableMap<byte[], byte[]> map)
	{
		ModelConstants.putUser(map, id);
		ModelConstants.putName(map, name);
		ModelConstants.putBirthday(map, birthday);
		ModelConstants.putGender(map, gender);
		ModelConstants.putOccupation(map, occupation);
		ModelConstants.putZipCode(map, zipcode);
		ModelConstants.putCity(map, city);
		ModelConstants.putCountry(map, country);
		ModelConstants.putCount(map, ratingCount);
		ModelConstants.putLastRatedDate(map, lastRated);
		ModelConstants.putViewCount(map, viewCount);
		ModelConstants.putLastViewedDate(map, lastViewed);
		ModelConstants.putRecommendCount(map, recommendCount);
		ModelConstants.putLastRecommendDate(map, lastRecommend);
		return map;

	}

	public User(long id, String name, String birthday)
	{
		super();
		this.id = id;
		this.name = name;
		this.birthday = birthday;
	}

	/**
	 * @return The id.
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return The age.
	 */
	public String getBirthday()
	{
		return birthday;
	}

	/**
	 * @param birthday The age to set.
	 */
	public void setBirthday(String birthday)
	{
		this.birthday = birthday;
	}

	/**
	 * @return The gender
	 */
	public String getGender()
	{
		return gender;
	}

	/**
	 * @param gender The gender to set
	 */
	public void setGender(String gender)
	{
		this.gender = gender;
	}

	/**
	 * @return The occupation
	 */
	public String getOccupation()
	{
		return occupation;
	}

	/**
	 * @param occupation The occupation to set
	 */
	public void setOccupation(String occupation)
	{
		this.occupation = occupation;
	}

	/**
	 * @return The zipcode
	 */
	public String getZipCode()
	{
		return zipcode;
	}

	/**
	 * @param zipcode The zipcode to set
	 */
	public void setZipCode(String zipcode)
	{
		this.zipcode = zipcode;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public Long getRatingCount()
	{
		return ratingCount;
	}

	public void setRatingCount(Long ratingCount)
	{
		this.ratingCount = ratingCount;
	}

	public Long getViewCount()
	{
		return viewCount;
	}

	public void setViewCount(Long viewCount)
	{
		this.viewCount = viewCount;
	}

	public Long getRecommendCount()
	{
		return recommendCount;
	}

	public void setRecommendCount(Long recommendCount)
	{
		this.recommendCount = recommendCount;
	}

	/**
	 * @return The zipcode.
	 */
	public String getZipcode()
	{
		return zipcode;
	}

	/**
	 * @param zipcode The zipcode to set.
	 */
	public void setZipcode(String zipcode)
	{
		this.zipcode = zipcode;
	}

	/**
	 * @return The lastRated.
	 */
	public Long getLastRated()
	{
		return lastRated;
	}

	/**
	 * @param lastRated The lastRated to set.
	 */
	public void setLastRated(Long lastRated)
	{
		this.lastRated = lastRated;
	}

	/**
	 * @return The lastViewed.
	 */
	public Long getLastViewed()
	{
		return lastViewed;
	}

	/**
	 * @param lastViewed The lastViewed to set.
	 */
	public void setLastViewed(Long lastViewed)
	{
		this.lastViewed = lastViewed;
	}

	/**
	 * @return The lastRecommend.
	 */
	public Long getLastRecommend()
	{
		return lastRecommend;
	}

	/**
	 * @param lastRecommend The lastRecommend to set.
	 */
	public void setLastRecommend(Long lastRecommend)
	{
		this.lastRecommend = lastRecommend;
	}

	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("id=").append(id);
		stringBuilder.append(" name=").append(name);
		stringBuilder.append(" birthday=").append(birthday);
		stringBuilder.append(" gender=").append(gender);
		stringBuilder.append(" occupation=").append(occupation);
		stringBuilder.append(" zipcode=").append(zipcode);
		stringBuilder.append(" ratingCount=").append(ratingCount);
		stringBuilder.append(" viewCount=").append(viewCount);
		stringBuilder.append(" recommendCount=").append(recommendCount);

		return stringBuilder.toString();
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		User other = (User) obj;
		if (id == null)
		{
			if (other.id != null)
			{
				return false;
			}
		}
		else if (!id.equals(other.id))
		{
			return false;
		}
		return true;
	}
}
