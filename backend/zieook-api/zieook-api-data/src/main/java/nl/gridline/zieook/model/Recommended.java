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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.gridline.zieook.api.JSonParent;
import nl.gridline.zieook.api.StorableHBase;

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * Recommend.java created 27 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "recommended")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recommended extends DataLog implements Serializable, JSonParent, StorableHBase
{

	@XmlType
	@XmlEnum
	public enum RecommenderType
	{

		@XmlEnumValue("user_based")
		USER_BASED("user_based"), @XmlEnumValue("item_based")
		ITEM_BASED("item_based");
		private final String type;

		RecommenderType(String type)
		{
			this.type = type;
		}

		public String value()
		{
			return type;
		}

		public static RecommenderType fromValue(String v)
		{
			for (RecommenderType t : RecommenderType.values())
			{
				if (t.value().equals(v))
				{
					return t;
				}
			}
			throw new IllegalArgumentException(v);
		}

	}

	@XmlElement(required = true)
	private RecommenderType type;

	@XmlElement
	private String recommender;
	@XmlElement
	private int size;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1428956045506577583L;

	public Recommended()
	{
		// no-arg constructor
	}

	public Recommended(NavigableMap<byte[], byte[]> map)
	{
		super(map);
		recommender = ModelConstants.getRecommender(map);
		size = ModelConstants.getSize(map);
		type = RecommenderType.fromValue(ModelConstants.getRecommenderType(map));
	}

	/**
	 * @param user user id
	 * @param item item id
	 * @param source arbitrary source
	 * @param date date stamp
	 * @param recommender recommender name
	 * @param type recommender type
	 * @param size the amount of recommendations requested.
	 */
	public Recommended(long user, long item, String collection, String source, Long date, String recommender,
			RecommenderType type, int size)
	{
		super(user, item, collection, source, date);
		this.recommender = recommender;
		this.size = size;
		this.type = type;
	}

	public Recommended(long user, long item, String cp, String collection, String source, Long date, String recommender,
			RecommenderType type, int size)
	{
		super(user, item, cp, collection, source, date);
		this.recommender = recommender;
		this.size = size;
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.model.DataLog#toMap(java.util.NavigableMap)
	 */
	@Override
	public NavigableMap<byte[], byte[]> toMap(NavigableMap<byte[], byte[]> map)
	{
		super.toMap(map);
		ModelConstants.putRecommender(map, recommender);
		ModelConstants.putSize(map, size);
		ModelConstants.putRecommenderType(map, type.value());
		return map;
	}

	/**
	 * @return The recommender.
	 */
	@Override
	public String getRecommender()
	{
		return recommender;
	}

	/**
	 * @param recommender The recommender to set.
	 */
	@Override
	public void setRecommender(String recommender)
	{
		this.recommender = recommender;
	}

	/**
	 * @return The size.
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * @param size The size to set.
	 */
	public void setSize(int size)
	{
		this.size = size;
	}

	/**
	 * @return The type.
	 */
	public RecommenderType getType()
	{
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(RecommenderType type)
	{
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return new StringBuilder(super.toString()).append(" recommender=").append(recommender).append(" size=")
				.append(size).toString();
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
		result = prime * result + ((recommender == null) ? 0 : recommender.hashCode());
		result = prime * result + size;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Recommended other = (Recommended) obj;
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
		if (size != other.size)
		{
			return false;
		}
		if (type != other.type)
		{
			return false;
		}
		return true;
	}

}
