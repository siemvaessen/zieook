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
 * The generic parent for data logging: ratings, views and recommendation requests:
 * <tt>DataLog [user,item,source,date,rating]</tt> <br />
 * <tt>UserRating[user,item,source,date,rating]</tt> <br />
 * <tt>UserView [user,item,source,date,sourceItem,rank]</tt> <br />
 * <tt>Recommend [user,item,source,date,recommender,size]</tt> <br />
 * <p />
 * All items are stored under: content provider (<tt>cp</tt>) and uses the collection (<tt>collection</tt>) as key part
 * <p />
 * Project zieook-api-data<br />
 * DataLog.java created 27 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 * @see Rating
 * @see UserView
 * @see Recommended
 */
@XmlRootElement(name = "data_log")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataLog implements Serializable, JSonParent, StorableHBase
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1372273272419400058L;

	@XmlElement(required = true)
	private Long user;

	@XmlElement(required = true)
	private Long item;

	@XmlElement(required = false)
	private String cp;

	@XmlElement(required = false)
	private String collection;

	@XmlElement(required = false)
	private String recommender;

	@XmlElement(required = false)
	private String source;

	@XmlElement(required = false)
	private Long date;

	@XmlElement(required = false)
	private Long count;

	public DataLog()
	{
		// no-arg constructor
	}

	public DataLog(NavigableMap<byte[], byte[]> map)
	{
		user = ModelConstants.getUser(map);
		item = ModelConstants.getItem(map);
		source = ModelConstants.getSource(map);
		date = ModelConstants.getStamp(map);
		count = ModelConstants.getCount(map);
		collection = ModelConstants.getCollection(map);
		cp = ModelConstants.getCp(map);
	}

	public DataLog(Long user, Long item, String collection, String source, Long date)
	{
		this.user = user;
		this.item = item;
		this.collection = collection;
		this.source = source;
		this.date = date;
	}

	public DataLog(Long user, Long item, String cp, String collection, String source, Long date)
	{
		this.user = user;
		this.item = item;
		this.collection = collection;
		this.source = source;
		this.date = date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.StorableHBase#toMap()
	 */
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
		ModelConstants.putUser(map, user);
		ModelConstants.putItem(map, item);
		ModelConstants.putCollection(map, collection);
		ModelConstants.putCp(map, cp);
		ModelConstants.putSource(map, source);
		ModelConstants.putStamp(map, date);
		if (count != null && count.longValue() > 0)
		{
			ModelConstants.putCount(map, count);
		}
		return map;
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

	public String getCollection()
	{
		return collection;
	}

	public void setCollection(String collection)
	{
		this.collection = collection;
	}

	/**
	 * @return The cp.
	 */
	public String getCp()
	{
		return cp;
	}

	/**
	 * @param cp The cp to set.
	 */
	public void setCp(String cp)
	{
		this.cp = cp;
	}

	public void setRecommender(String recommender)
	{
		this.recommender = recommender;
	}

	public String getRecommender()
	{
		return recommender;
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return new StringBuilder().append("user='").append(user).append("' item='").append(item).append("' source='")
				.append(source).append("' date='").append(date).append("'").toString();
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
		DataLog other = (DataLog) obj;
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

}
