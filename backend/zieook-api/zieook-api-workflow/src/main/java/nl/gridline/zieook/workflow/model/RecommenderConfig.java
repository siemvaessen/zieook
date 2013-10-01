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
package nl.gridline.zieook.workflow.model;

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
 * A recommender is the collection of a filter over a collection, and a set of algorithms that will be used to execute
 * the task on a certain interval.
 * Properties:
 * <ul>
 * <li>cp - content provider</li>
 * <li>recommender - the recommender</li>
 * <li>collection - the collection name</li>
 * <li>filter - a filter object, that allows for filtering the input data</li>
 * <li>template - the recommender configuration settings</li>
 * <li>interval - the milliseconds interval to execute the recommmender on, typically this runs once every 24 hours to
 * once a week</li>
 * <li>next - the first time to execute, if the date is in the past, it will execute right away</li>
 * </ul>
 * <p />
 * Project zieook-api-workflow<br />
 * RecommenderConfig.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "recommender_config")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecommenderConfig implements Serializable, JSonParent, StorableHBase
{
	// WARNING: changes here will have to match changes in
	// nl.gridline.zieook.data.hbase.dao.RecommenderTable.getRecommender

	/**
	 * 
	 */
	private static final long serialVersionUID = 5639301147152100406L;

	@XmlElement(required = true)
	private String cp;

	@XmlElement(required = true)
	private String recommender;

	@XmlElement(required = true)
	private String collection;

	@XmlElement
	private CollectionFilter filter;

	@XmlElement
	private Template template;

	@XmlElement
	private long interval;

	@XmlElement
	private long next;

	public RecommenderConfig()
	{

	}

	public RecommenderConfig(String cp, String recommender, String collection, CollectionFilter filter,
			Template template, long interval, long next)
	{
		// WARNING: changes here will have to match changes in
		// nl.gridline.zieook.data.hbase.dao.RecommenderTable.getRecommender

		super();
		this.cp = cp;
		this.recommender = recommender;
		this.collection = collection;
		this.filter = filter;
		this.template = template;
		this.interval = interval;
		this.next = next;
	}

	public RecommenderConfig(NavigableMap<byte[], byte[]> map)
	{
		// WARNING: changes here will have to match changes in
		// nl.gridline.zieook.data.hbase.dao.RecommenderTable.getRecommender
		cp = ModelConstants.getCp(map);
		recommender = ModelConstants.getRecommender(map);
		collection = ModelConstants.getCollection(map);
		// filter = ModelConstants.getFilter(map);
		// template = ModelConstants.getTemplate(map);
		interval = ModelConstants.getInterval(map);
		next = ModelConstants.getNext(map);
	}

	@Override
	public NavigableMap<byte[], byte[]> toMap()
	{
		return toMap(new TreeMap<byte[], byte[]>(Bytes.BYTES_COMPARATOR));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.model.StorableHBase#toMap(java.util.NavigableMap)
	 */
	@Override
	public NavigableMap<byte[], byte[]> toMap(NavigableMap<byte[], byte[]> map)
	{
		// WARNING: changes here will have to match changes in
		// nl.gridline.zieook.data.hbase.dao.RecommenderTable.getRecommender
		ModelConstants.putCp(map, cp);
		ModelConstants.putRecommender(map, recommender);
		ModelConstants.putCollection(map, collection);
		// ModelConstants.putFilter(map, filter);
		// ModelConstants.putTemplate(map, template);
		ModelConstants.putInterval(map, interval);
		ModelConstants.putNext(map, next);
		return map;
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
	 * @return
	 */
	public long getNext()
	{
		return next;
	}

	/**
	 * @param next
	 */
	public void setNext(long next)
	{
		this.next = next;
	}

	/**
	 * @return The filter.
	 */
	public CollectionFilter getFilter()
	{
		return filter;
	}

	/**
	 * @param filter The filter to set.
	 */
	public void setFilter(CollectionFilter filter)
	{
		this.filter = filter;
	}

	/**
	 * @return The template.
	 */
	public Template getTemplate()
	{
		return template;
	}

	/**
	 * @param template The template to set.
	 */
	public void setTemplate(Template template)
	{
		this.template = template;
	}

	/**
	 * @return The interval.
	 */
	public long getInterval()
	{
		return interval;
	}

	/**
	 * @param interval The interval to set.
	 */
	public void setInterval(long interval)
	{
		this.interval = interval;
	}

	@Override
	public String toString()
	{
		return new StringBuilder().append("cp=").append(cp).append(collection).append(" recommender=")
				.append(recommender).append(" filter=").append(filter).append(" template=").append(template).toString();
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
		result = prime * result + ((collection == null) ? 0 : collection.hashCode());
		result = prime * result + ((cp == null) ? 0 : cp.hashCode());
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + (int) (interval ^ (interval >>> 32));
		result = prime * result + (int) (next ^ (next >>> 32));
		result = prime * result + ((recommender == null) ? 0 : recommender.hashCode());
		result = prime * result + ((template == null) ? 0 : template.hashCode());
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
		RecommenderConfig other = (RecommenderConfig) obj;
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
		if (filter == null)
		{
			if (other.filter != null)
			{
				return false;
			}
		}
		else if (!filter.equals(other.filter))
		{
			return false;
		}
		if (interval != other.interval)
		{
			return false;
		}
		if (next != other.next)
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
		if (template == null)
		{
			if (other.template != null)
			{
				return false;
			}
		}
		else if (!template.equals(other.template))
		{
			return false;
		}
		return true;
	}
}
