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
 * [purpose]
 * <p />
 * Project zieook-api-workflow<br />
 * ContentProvider.java created 10 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "content_provider")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContentProvider implements Serializable, JSonParent, StorableHBase
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1609696408901254200L;

	@XmlElement(required = true)
	private String name;
	@XmlElement
	private long date;

	@XmlElement(name = "taalserver")
	private boolean taalserver;

	public ContentProvider()
	{

	}

	public ContentProvider(String name, long date, boolean taalserver)
	{
		this.name = name;
		this.date = date;
		this.taalserver = taalserver;
	}

	public ContentProvider(NavigableMap<byte[], byte[]> map)
	{
		name = ModelConstants.getName(map);
		date = ModelConstants.getStamp(map);
		taalserver = ModelConstants.getTaalserver(map);
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
		ModelConstants.putName(map, name);
		ModelConstants.putStamp(map, date);
		ModelConstants.putTaalserver(map, taalserver);
		return map;
	}

	/**
	 * @return The taalserver.
	 */
	public boolean isTaalserver()
	{
		return taalserver;
	}

	/**
	 * @param taalserver The taalserver to set.
	 */
	public void setTaalserver(boolean taalserver)
	{
		this.taalserver = taalserver;
	}

	/**
	 * @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return The date.
	 */
	public long getDate()
	{
		return date;
	}

	/**
	 * @param date The date to set.
	 */
	public void setDate(long date)
	{
		this.date = date;
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
		result = prime * result + (int) (date ^ (date >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ContentProvider other = (ContentProvider) obj;
		if (date != other.date)
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return new StringBuilder().append("name=").append(name).append(" date=").append(date).toString();
	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}
}
