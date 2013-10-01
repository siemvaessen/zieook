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
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * Popularity.java created 8 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "popularity")
@XmlAccessorType(XmlAccessType.FIELD)
public class Popularity implements Comparable<Popularity>, StorableHBase, JSonParent
{

	/**
	 * @param popularity The popularity to set.
	 */
	public void setPopularity(int popularity)
	{
		this.popularity = popularity;
	}

	@XmlElement(name = "earliest_time")
	private long time;

	@XmlElement(name = "range_name")
	private String timeRangeName;

	@XmlElement(name = "item_id")
	private long item;

	@XmlElement(name = "popularity")
	private int popularity;

	@XmlElement(name = "absolute")
	private long absolute;

	public Popularity()
	{
		// no-arg constructor
	}

	public Popularity(long item, int popularity, long absolute, long time, String timeRangeName)
	{
		this.item = item;
		this.popularity = popularity;
		this.absolute = absolute;
		this.time = time;
		this.timeRangeName = timeRangeName;
	}

	public Popularity(NavigableMap<byte[], byte[]> map)
	{
		time = ModelConstants.getStamp(map);
		item = ModelConstants.getItem(map);
		popularity = ModelConstants.getPopularity(map);
		timeRangeName = ModelConstants.getName(map);
		absolute = ModelConstants.getCount(map);
	}

	/**
	 * @return The time.
	 */
	public long getTime()
	{
		return time;
	}

	/**
	 * @param time The time to set.
	 */
	public void setTime(long time)
	{
		this.time = time;
	}

	/**
	 * @return The timeRangeName.
	 */
	public String getTimeRangeName()
	{
		return timeRangeName;
	}

	/**
	 * @param timeRangeName The timeRangeName to set.
	 */
	public void setTimeRangeName(String timeRangeName)
	{
		this.timeRangeName = timeRangeName;
	}

	/**
	 * @return The item.
	 */
	public long getItem()
	{
		return item;
	}

	/**
	 * @param item The item to set.
	 */
	public void setItem(long item)
	{
		this.item = item;
	}

	/**
	 * @return The popularity.
	 */
	public int getPopularity()
	{
		return popularity;
	}

	/**
	 * @return The absolute.
	 */
	public long getAbsolute()
	{
		return absolute;
	}

	/**
	 * @param absolute The absolute to set.
	 */
	public void setAbsolute(long absolute)
	{
		this.absolute = absolute;
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
		ModelConstants.putItem(map, item);
		ModelConstants.putStamp(map, time);
		ModelConstants.putPopularity(map, popularity);
		ModelConstants.putCount(map, absolute);
		ModelConstants.putName(map, timeRangeName);

		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Popularity o)
	{
		if (o == null)
		{
			throw new NullPointerException("cannot compare to null");
		}

		if (this == o)
		{
			return 0;
		}

		// popularity:
		if (popularity > o.popularity)
		{
			return 1;
		}
		if (popularity < o.popularity)
		{
			return -1;
		}

		if (item < o.item)
		{
			return 1;
		}
		if (item > o.item)
		{
			return 1;
		}

		// time ordering:
		if (time < o.time)
		{
			return -1;
		}
		if (time > o.time)
		{
			return 1;
		}

		// timeRangeName - ignore, this should match directly with the time

		return 0;
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
		result = prime * result + (int) (item ^ (item >>> 32));
		result = prime * result + popularity;
		result = prime * result + (int) (time ^ (time >>> 32));
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
		Popularity other = (Popularity) obj;
		if (popularity != other.popularity)
		{
			return false;
		}

		if (item != other.item)
		{
			return false;
		}

		if (time != other.time)
		{
			return false;
		}
		return true;
	}

}
