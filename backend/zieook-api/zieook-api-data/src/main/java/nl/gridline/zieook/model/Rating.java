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
 * A user rating event consists of:
 * an item id, a user id and a rating. - depending on the recommender setting the rating will be interpreted
 * <p />
 * Project zieook-api-data<br />
 * Rating.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "user_rating")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rating extends DataLog implements Serializable, JSonParent, StorableHBase
{
	/**
	 *  
	 */
	private static final long serialVersionUID = -4533674205201994238L;

	@XmlElement
	private double rating;

	public Rating()
	{
		// no-arg constructor
	}

	public Rating(NavigableMap<byte[], byte[]> map)
	{
		super(map);
		rating = ModelConstants.getRating(map);

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
		super.toMap(map);
		ModelConstants.putRating(map, rating);
		return map;
	}

	/**
	 * @param user user id
	 * @param item item id
	 * @param source some source
	 * @param date a date (can be null, back-end will generate)
	 * @param rating a rating
	 */
	public Rating(long user, long item, String collection, String source, Long date, double rating)
	{
		super(user, item, collection, source, date);
		this.rating = rating;
	}

	public Rating(long user, long item, String cp, String collection, String source, Long date, double rating)
	{
		super(user, item, cp, collection, source, date);
		this.rating = rating;
	}

	public double getRating()
	{
		return rating;
	}

	public void setRating(double rating)
	{
		this.rating = rating;
	}

	@Override
	public String toString()
	{
		return new StringBuilder(super.toString()).append(" rating=").append(rating).toString();
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
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(rating);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (!super.equals(obj))
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		Rating other = (Rating) obj;
		if (Double.doubleToLongBits(rating) != Double.doubleToLongBits(other.rating))
		{
			return false;
		}
		return true;
	}

}
