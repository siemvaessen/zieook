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
 * When a user views an item from a recommendation, the event is logged
 * <p />
 * Project zieook-api-data<br />
 * UserView.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */

@XmlRootElement(name = "user_view")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserView extends DataLog implements Serializable, JSonParent, StorableHBase
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7651966817394085704L;

	@XmlElement(required = false)
	private String recommender;

	@XmlElement(required = false, name = "source_item")
	private long sourceItem;
	@XmlElement(required = false)
	private long rank;

	@XmlElement(name = "rating")
	private double rating;

	public UserView()
	{

	}

	public UserView(NavigableMap<byte[], byte[]> map)
	{
		super(map);
		recommender = ModelConstants.getRecommender(map);
		sourceItem = ModelConstants.getSourceItem(map);
		rank = ModelConstants.getRank(map);
	}

	/**
	 * creates a user view object
	 * @param user user id
	 * @param item item id
	 * @param sourceItem source item (item where clicked from)
	 * @param rank item rank in item clicked
	 * @param source an arbitrary source key, (e.g. the page clicked from)
	 */
	public UserView(long user, long item, String collection, String source, Long date, String recommender,
			long sourceItem, long rank)
	{
		super(user, item, collection, source, date);
		this.recommender = recommender;
		this.sourceItem = sourceItem;
		this.rank = rank;
	}

	public UserView(long user, long item, String cp, String collection, String source, Long date, String recommender,
			long sourceItem, long rank)
	{
		super(user, item, cp, collection, source, date);
		this.recommender = recommender;
		this.sourceItem = sourceItem;
		this.rank = rank;
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
		ModelConstants.putRecommender(map, recommender);
		ModelConstants.putSourceItem(map, sourceItem);
		ModelConstants.putRank(map, rank);
		return map;
	}

	public void setSourceItem(long sourceItem)
	{
		this.sourceItem = sourceItem;
	}

	public long getSourceItem()
	{
		return sourceItem;
	}

	public long getRank()
	{
		return rank;
	}

	public void setRank(long rank)
	{
		this.rank = rank;
	}

	@Override
	public void setRecommender(String recommender)
	{
		this.recommender = recommender;
	}

	@Override
	public String getRecommender()
	{
		return recommender;
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
		return new StringBuilder(super.toString()).append(" source-item=").append(sourceItem).append(" rank=")
				.append(rank).append(" recommender=").append(recommender).append(" rating=").append(rating).toString();
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
		result = prime * result + (int) (rank ^ (rank >>> 32));
		result = prime * result + ((recommender == null) ? 0 : recommender.hashCode());
		result = prime * result + (int) (sourceItem ^ (sourceItem >>> 32));
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
		UserView other = (UserView) obj;
		if (rank != other.rank)
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
		if (sourceItem != other.sourceItem)
		{
			return false;
		}
		return true;
	}

}
