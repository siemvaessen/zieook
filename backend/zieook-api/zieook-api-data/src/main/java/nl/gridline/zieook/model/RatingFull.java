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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.gridline.zieook.api.JSonParent;

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * RatingFull.java created 23 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "user_rating")
@XmlAccessorType(XmlAccessType.FIELD)
public class RatingFull implements Serializable, JSonParent
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8660871604558628788L;

	@XmlElement
	private double rating;

	@XmlElement(required = true)
	private Long user;

	@XmlElement(required = true)
	private CollectionItem item;

	@XmlElement(required = false)
	private String collection;

	@XmlElement(required = false)
	private String cp;

	@XmlElement(required = false)
	private String source;

	@XmlElement(required = false)
	private Long date;

	@XmlElement(required = false)
	private Long count;

	@XmlElement(required = false)
	private String recommender;

	public RatingFull()
	{
		// no-arg constructor
	}

	public RatingFull(Rating rating)
	{
		user = rating.getUser();
		collection = rating.getCollection();
		cp = rating.getCp();
		source = rating.getSource();
		date = rating.getDate();
		count = rating.getCount();
		this.rating = rating.getRating();
		recommender = rating.getRecommender();

	}

	/**
	 * @return The rating.
	 */
	public double getRating()
	{
		return rating;
	}

	/**
	 * @param rating The rating to set.
	 */
	public void setRating(double rating)
	{
		this.rating = rating;
	}

	/**
	 * @return The user.
	 */
	public Long getUser()
	{
		return user;
	}

	/**
	 * @param user The user to set.
	 */
	public void setUser(Long user)
	{
		this.user = user;
	}

	/**
	 * @return The item.
	 */
	public CollectionItem getItem()
	{
		return item;
	}

	/**
	 * @param item The item to set.
	 */
	public void setItem(CollectionItem item)
	{
		this.item = item;
	}

	/**
	 * @return The collection.
	 */
	public String getCollection()
	{
		return collection;
	}

	/**
	 * @param collection The collection to set.
	 */
	public void setCollection(String collection)
	{
		this.collection = collection;
	}

	/**
	 * @return The source.
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * @param source The source to set.
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 * @return The date.
	 */
	public Long getDate()
	{
		return date;
	}

	/**
	 * @param date The date to set.
	 */
	public void setDate(Long date)
	{
		this.date = date;
	}

	/**
	 * @return The count.
	 */
	public Long getCount()
	{
		return count;
	}

	/**
	 * @param count The count to set.
	 */
	public void setCount(Long count)
	{
		this.count = count;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return new StringBuilder(" user=").append(user).append(" collection=").append(collection).append(" source=")
				.append(source).append(" date=").append(date).append(" count=").append(count).append("item=").append(item)
				.toString();
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
		result = prime * result + ((collection == null) ? 0 : collection.hashCode());
		result = prime * result + ((count == null) ? 0 : count.hashCode());
		result = prime * result + ((cp == null) ? 0 : cp.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		long temp;
		temp = Double.doubleToLongBits(rating);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		RatingFull other = (RatingFull) obj;
		if (collection == null)
		{
			if (other.collection != null)
			{
				return false;
			}
		}
		else if (!collection.equals(other.collection))
		{
			return false;
		}
		if (count == null)
		{
			if (other.count != null)
			{
				return false;
			}
		}
		else if (!count.equals(other.count))
		{
			return false;
		}
		if (cp == null)
		{
			if (other.cp != null)
			{
				return false;
			}
		}
		else if (!cp.equals(other.cp))
		{
			return false;
		}
		if (date == null)
		{
			if (other.date != null)
			{
				return false;
			}
		}
		else if (!date.equals(other.date))
		{
			return false;
		}
		if (item == null)
		{
			if (other.item != null)
			{
				return false;
			}
		}
		else if (!item.equals(other.item))
		{
			return false;
		}
		if (Double.doubleToLongBits(rating) != Double.doubleToLongBits(other.rating))
		{
			return false;
		}
		if (source == null)
		{
			if (other.source != null)
			{
				return false;
			}
		}
		else if (!source.equals(other.source))
		{
			return false;
		}
		if (user == null)
		{
			if (other.user != null)
			{
				return false;
			}
		}
		else if (!user.equals(other.user))
		{
			return false;
		}
		return true;
	}

}
