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
/**
 * 
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
 * @author job
 */
@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.FIELD)
public class GroupedData implements Serializable, Comparable<GroupedData>, JSonParent, StorableHBase
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3012367561577793433L;

	@XmlElement(name = "collection")
	private String collection;

	@XmlElement(name = "recommender")
	private String recommender;
	@XmlElement(name = "source")
	private String source;
	@XmlElement(name = "item")
	private Long item;
	@XmlElement(name = "user")
	private Long user;
	//
	@XmlElement(name = "count")
	private Long count;
	@XmlElement(name = "time")
	private Long date;

	public GroupedData()
	{

	}

	public GroupedData(NavigableMap<byte[], byte[]> map)
	{
		collection = ModelConstants.getCollection(map);
		recommender = ModelConstants.getRecommender(map);
		source = ModelConstants.getSource(map);
		item = ModelConstants.getItemLong(map);
		user = ModelConstants.getUserLong(map);
		//
		count = ModelConstants.getCount(map);
		date = ModelConstants.getStamp(map);

	}

	/**
	 * @param collection
	 * @param recommender
	 * @param source
	 * @param item
	 * @param user
	 * @param count
	 * @param date
	 */
	public GroupedData(String collection, String recommender, String source, Long item, Long user, Long count, Long date)
	{
		this.collection = collection;
		this.recommender = recommender;
		this.source = source;
		this.item = item;
		this.user = user;
		this.count = count;
		this.date = date;
	}

	@Override
	public NavigableMap<byte[], byte[]> toMap()
	{
		return toMap(new TreeMap<byte[], byte[]>(Bytes.BYTES_COMPARATOR));
	}

	@Override
	public NavigableMap<byte[], byte[]> toMap(NavigableMap<byte[], byte[]> map)
	{
		ModelConstants.putCollection(map, collection);
		ModelConstants.putRecommender(map, recommender);
		ModelConstants.putSource(map, source);
		ModelConstants.putItemLong(map, item);
		ModelConstants.putUserLong(map, user);
		//
		ModelConstants.putCount(map, count);
		ModelConstants.putStamp(map, date);
		return map;
	}

	/**
	 * @return The item.
	 */
	public Long getItem()
	{
		return item;
	}

	/**
	 * @param item The item to set.
	 */
	public void setItem(Long item)
	{
		this.item = item;
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
	 * @return the date
	 */
	public Long getDate()
	{
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Long date)
	{
		this.date = date;
	}

	/**
	 * @return the recommender
	 */
	public String getRecommender()
	{
		return recommender;
	}

	/**
	 * @param recommender the recommender to set
	 */
	public void setRecommender(String recommender)
	{
		this.recommender = recommender;
	}

	/**
	 * @return the count
	 */
	public Long getCount()
	{
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(Long count)
	{
		this.count = count;
	}

	/**
	 * @return the source
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source)
	{
		this.source = source;
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

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}

	@Override
	public String toString()
	{
		return new StringBuilder("<collection,recommender,source,item,user,count,date>").append('<').append(collection)
				.append(',').append(recommender).append(',').append(source).append(',').append(item).append(',')
				.append(user).append(',').append(count).append(',').append(date).append('>').toString();
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
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((recommender == null) ? 0 : recommender.hashCode());
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
		GroupedData other = (GroupedData) obj;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GroupedData o)
	{
		if (o == null)
		{
			throw new NullPointerException("cannot compare to null");
		}

		int result = 0;
		if (count != null && o.count != null)
		{
			result = count.longValue() > o.count.longValue() ? -1 : 1;
			if (result != 0)
			{
				return result;
			}
		}

		if (date != null && o.date != null)
		{
			result = date.longValue() < o.date.longValue() ? 1 : -1;
			if (result != 0)
			{
				return result;
			}
		}

		// collection,
		if (collection != null && o.collection != null)
		{
			result = collection.compareTo(o.collection);
			if (result != 0)
			{
				return result;
			}
		}

		// recommender,
		if (recommender != null && o.recommender != null)
		{
			result = recommender.compareTo(o.recommender);
			if (result != 0)
			{
				return result;
			}
		}
		// source,
		if (source != null && o.source != null)
		{
			result = source.compareTo(o.source);
			if (result != 0)
			{
				return result;
			}
		}

		// item,
		if (item != null && o.item != null)
		{
			result = item.longValue() < o.item.longValue() ? 1 : -1;
			if (result != 0)
			{
				return result;
			}
		}

		// user,
		if (user != null && o.user != null)
		{
			result = user.longValue() < o.user.longValue() ? 1 : -1;
			if (result != 0)
			{
				return result;
			}
		}

		return result;
	}

}
