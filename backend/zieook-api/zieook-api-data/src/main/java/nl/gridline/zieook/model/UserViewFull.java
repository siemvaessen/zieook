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

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * UserViewFull.java created 23 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "user_view")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserViewFull implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5553691091101144680L;

	@XmlElement(required = false)
	private String cp;

	@XmlElement(required = false)
	private String recommender;

	@XmlElement(required = false, name = "source_item")
	private CollectionItem sourceItem;

	@XmlElement(required = false)
	private long rank;

	@XmlElement(name = "rating")
	private Rating rating;

	@XmlElement(name = "source_rating")
	private Rating sourceRating;

	@XmlElement(required = true)
	private Long user;

	@XmlElement(required = true)
	private CollectionItem item;

	@XmlElement(required = false)
	private String collection;

	@XmlElement(required = false)
	private String source;

	@XmlElement(required = false)
	private Long date;

	@XmlElement(required = false)
	private Long count;

	public UserViewFull()
	{

	}

	public UserViewFull(UserView view)
	{
		recommender = view.getRecommender();
		collection = view.getCollection();
		rank = view.getRank();
		user = view.getUser();
		source = view.getSource();
		date = view.getDate();
		count = view.getCount();
		cp = view.getCp();
	}

	/**
	 * @return The recommender.
	 */
	public String getRecommender()
	{
		return recommender;
	}

	/**
	 * @param recommender The recommender to set.
	 */
	public void setRecommender(String recommender)
	{
		this.recommender = recommender;
	}

	/**
	 * @return The sourceItem.
	 */
	public CollectionItem getSourceItem()
	{
		return sourceItem;
	}

	/**
	 * @param sourceItem The sourceItem to set.
	 */
	public void setSourceItem(CollectionItem sourceItem)
	{
		this.sourceItem = sourceItem;
	}

	/**
	 * @return The rank.
	 */
	public long getRank()
	{
		return rank;
	}

	/**
	 * @param rank The rank to set.
	 */
	public void setRank(long rank)
	{
		this.rank = rank;
	}

	/**
	 * @return The rating.
	 */
	public Rating getRating()
	{
		return rating;
	}

	/**
	 * @param rating The rating to set.
	 */
	public void setRating(Rating rating)
	{
		this.rating = rating;
	}

	public Rating getSourceRating()
	{
		return sourceRating;
	}

	public void setSourceRating(Rating rating)
	{
		sourceRating = rating;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return new StringBuilder().append("recommender=").append(recommender).append(" sourceItem=").append(sourceItem)
				.append(" rank=").append(rank).append(" rating=").append(rating).append(" sourceRating=")
				.append(sourceRating).append(" user=").append(user).append(" item=").append(item).append(" collection=")
				.append(collection).append(" source=").append(source).append(" date=").append(date).append(" count=")
				.append(count).toString();
	}

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
		result = prime * result + (int) (rank ^ (rank >>> 32));
		result = prime * result + ((rating == null) ? 0 : rating.hashCode());
		result = prime * result + ((recommender == null) ? 0 : recommender.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((sourceItem == null) ? 0 : sourceItem.hashCode());
		result = prime * result + ((sourceRating == null) ? 0 : sourceRating.hashCode());
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
		UserViewFull other = (UserViewFull) obj;
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
		if (rank != other.rank)
		{
			return false;
		}
		if (rating == null)
		{
			if (other.rating != null)
			{
				return false;
			}
		}
		else if (!rating.equals(other.rating))
		{
			return false;
		}
		if (recommender == null)
		{
			if (other.recommender != null)
			{
				return false;
			}
		}
		else if (!recommender.equals(other.recommender))
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
		if (sourceItem == null)
		{
			if (other.sourceItem != null)
			{
				return false;
			}
		}
		else if (!sourceItem.equals(other.sourceItem))
		{
			return false;
		}
		if (sourceRating == null)
		{
			if (other.sourceRating != null)
			{
				return false;
			}
		}
		else if (!sourceRating.equals(other.sourceRating))
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
