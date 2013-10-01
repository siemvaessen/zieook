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

import org.apache.hadoop.hbase.util.Bytes;

/**
 * The rating of an item on some date, the user is specified in the {@link nl.gridline.zieook.model.Recommendations}
 * class
 * <p />
 * Project zieook-api-data<br />
 * Rating.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recommend implements Serializable, Comparable<Recommend>, JSonParent
{

	/** 
	 * 
	 */
	private static final long serialVersionUID = 7912962179058366970L;

	@XmlElement(name = "id")
	private long item;
	@XmlElement
	private long rank;
	@XmlElement
	private double rating;

	public Recommend()
	{

	}

	/**
	 * Convenience constructor to create rating with fields in a map
	 * @param map
	 */
	public Recommend(byte[] data)
	{
		rating = Bytes.toDouble(data, 0);
		rank = Bytes.toLong(data, Bytes.SIZEOF_DOUBLE);
		item = Bytes.toLong(data, Bytes.SIZEOF_DOUBLE + Bytes.SIZEOF_LONG);
		// item = ModelConstants.getItem(map);
		// rank = ModelConstants.getRank(map);
		// rating = ModelConstants.getRating(map);
	}

	/**
	 * Convenience constructor to create rating with all fields
	 * @param item
	 * @param rank
	 * @param rating
	 */
	public Recommend(long item, long rank, double rating)
	{
		this.item = item;
		this.rank = rank;
		this.rating = rating;
	}

	public byte[] toBytes()
	{
		return Bytes.add(Bytes.toBytes(rating), Bytes.toBytes(rank), Bytes.toBytes(item));
	}

	public static byte[] getRecommendation(long item, long rank, double rating)
	{
		return Bytes.add(Bytes.toBytes(rating), Bytes.toBytes(rank), Bytes.toBytes(item));
	}

	public long getItem()
	{
		return item;
	}

	public void setItem(long item)
	{
		this.item = item;
	}

	public long getRank()
	{
		return rank;
	}

	public void setRank(long rank)
	{
		this.rank = rank;
	}

	public double getRating()
	{
		return rating;
	}

	public void setRating(double rating)
	{
		this.rating = rating;
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
		Recommend other = (Recommend) obj;
		if (item != other.item)
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
	public int compareTo(Recommend o)
	{
		if (o.item == item)
		{
			return 0;
		}
		return (int) (o.rank - rank);
	}
}
