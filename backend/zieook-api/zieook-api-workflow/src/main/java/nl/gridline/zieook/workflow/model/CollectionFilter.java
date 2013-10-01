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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * CollectionFilter a key, multi value filter: usually a key that maps directly to a field to the content of the cp and
 * a subset of values that are used.
 * <p />
 * Project zieook-api-workflow<br />
 * CollectionFilter.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "collection_filter")
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionFilter implements Serializable, JSonParent, StorableHBase
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6762110359275414918L;

	@XmlElement
	private String name;

	@XmlElement
	private String key;

	@XmlElement(name = "start_date")
	private Long startdate;

	@XmlElement(name = "end_date")
	private Long enddate;

	@XmlElement(name = "categories")
	private List<String> values;

	public CollectionFilter()
	{

	}

	public CollectionFilter(String name, String key, List<String> values)
	{
		this.name = name;
		this.key = key;
		this.values = values;
	}

	public CollectionFilter(String name, long startdate, long enddate, String key, List<String> values)
	{
		this.name = name;
		this.startdate = startdate;
		this.enddate = enddate;
		this.key = key;
		this.values = values;
	}

	@SuppressWarnings("unchecked")
	public CollectionFilter(NavigableMap<byte[], byte[]> filter) throws IOException, ClassNotFoundException
	{
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<byte[], byte[]> entry : filter.entrySet())
		{
			String entryKey = Bytes.toString(entry.getKey());
			if ("startdate".equals(entryKey))
			{
				startdate = Bytes.toLong(entry.getValue());
			}
			else if ("enddate".equals(entryKey))
			{
				enddate = Bytes.toLong(entry.getValue());
			}
			else
			{
				name = entryKey;
				ByteArrayInputStream input = new ByteArrayInputStream(entry.getValue());
				ObjectInput in = new ObjectInputStream(input);
				list = (ArrayList<String>) in.readObject();
			}
		}

		// get key from the first index of the values list:
		if (!list.isEmpty())
		{
			key = list.get(0);
			list.remove(0);
			values = new ArrayList<String>(list);
		}
		else
		{
			values = new ArrayList<String>(0);
		}
	}

	@Override
	public NavigableMap<byte[], byte[]> toMap() throws IOException
	{
		return toMap(new TreeMap<byte[], byte[]>(Bytes.BYTES_COMPARATOR));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.model.ModelParent#toMap(java.util.NavigableMap)
	 */
	@Override
	public NavigableMap<byte[], byte[]> toMap(NavigableMap<byte[], byte[]> map) throws IOException
	{
		// write "key" to the first index of the values list:
		ArrayList<String> list;
		if (values != null)
		{
			list = new ArrayList<String>(values);
			list.add(0, key);
		}
		else
		{
			list = new ArrayList<String>(0);
		}

		// create a byte array from the list:
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(result);
		out.writeObject(list);
		out.close();

		if (name != null)
		{
			map.put(Bytes.toBytes(name), result.toByteArray());
		}
		map.put(Bytes.toBytes("startdate"), Bytes.toBytes(startdate));
		map.put(Bytes.toBytes("enddate"), Bytes.toBytes(enddate));

		return map;
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
	 * @return The key.
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * @param key The key to set.
	 */
	public void setKey(String key)
	{
		this.key = key;
	}

	/**
	 * @return The values.
	 */
	public List<String> getValues()
	{
		return values;
	}

	public String getValuesString()
	{
		StringBuilder b = new StringBuilder();
		for (String v : values)
		{
			b.append(v).append('\n');
		}

		return b.toString().trim();
	}

	/**
	 * @param values The values to set.
	 */
	public void setValues(List<String> values)
	{
		this.values = values;
	}

	/**
	 * @return The startDate.
	 */
	public Long getStartDate()
	{
		return startdate;
	}

	/**
	 * @param startDate The startDate to set.
	 */
	public void setStartDate(Long startDate)
	{
		startdate = startDate;
	}

	/**
	 * @return The endDate.
	 */
	public Long getEndDate()
	{
		return enddate;
	}

	/**
	 * @param endDate The endDate to set.
	 */
	public void setEndDate(Long endDate)
	{
		enddate = endDate;
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
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		CollectionFilter other = (CollectionFilter) obj;
		if (key == null)
		{
			if (other.key != null)
			{
				return false;
			}
		}
		else if (!key.equals(other.key))
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
		if (values == null)
		{
			if (other.values != null)
			{
				return false;
			}
		}
		else if (!values.equals(other.values))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder().append("name=").append(name).append(" key=").append(key).append(" values=");
		if (values == null)
		{
			b.append("null");
		}
		else
		{
			b.append('[');
			if (values.size() > 0)
			{
				for (String v : values)
				{
					b.append(v).append(',');
				}
				b.deleteCharAt(b.length() - 1);
			}
			b.append(']');
		}
		return b.toString();
	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}

}
