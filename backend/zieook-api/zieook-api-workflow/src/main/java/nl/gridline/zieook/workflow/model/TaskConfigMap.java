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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.gridline.zieook.api.JSonParent;

/**
 * Creates a key, value map of the taskconfig in XML
 * <p />
 * Project zieook-api-workflow<br />
 * TaskConfigMap.java created Mar 17, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "task")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskConfigMap implements Serializable, JSonParent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 400869980898811118L;

	public static class SettingsAdapter extends XmlAdapter<HashMapType, TreeMap<String, String>>
	{
		public SettingsAdapter()
		{
			// no-arg constructor
		}

		@Override
		public TreeMap<String, String> unmarshal(HashMapType value)
		{
			TreeMap<String, String> result = new TreeMap<String, String>();
			for (HashMapEntry entry : value.entries)
			{
				result.put(entry.key, entry.value);
			}
			return result;
		}

		@Override
		public HashMapType marshal(TreeMap<String, String> value)
		{
			if (value != null)
			{
				List<HashMapEntry> list = new ArrayList<HashMapEntry>(value.size());

				for (Map.Entry<String, String> entry : value.entrySet())
				{
					list.add(new HashMapEntry(entry.getKey(), entry.getValue()));
				}

				return new HashMapType(list);
			}
			return new HashMapType(new ArrayList<HashMapEntry>());
		}
	}

	public static class HashMapType
	{
		@XmlElement(name = "entry")
		public List<HashMapEntry> entries;

		public HashMapType()
		{

		}

		public HashMapType(List<HashMapEntry> entries)
		{
			this.entries = entries;
		}
	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class HashMapEntry
	{
		public HashMapEntry()
		{
			// no-arg constructor
		}

		public HashMapEntry(String key, String value)
		{
			this.key = key;
			this.value = value;
		}

		@XmlAttribute(name = "key")
		public String key;

		@XmlAttribute(name = "value")
		public String value;

	}

	@XmlElement
	@XmlJavaTypeAdapter(SettingsAdapter.class)
	private TreeMap<String, String> data;

	public TaskConfigMap()
	{
		// no-arg constructor
	}

	/**
	 * assigns the given map to the map in this object
	 * @param data
	 */
	public TaskConfigMap(TreeMap<String, String> data)
	{
		this.data = data;
	}

	/**
	 * creates a new HashMap from the given map.
	 * @param data
	 */
	public TaskConfigMap(Map<String, String> data)
	{
		this.data = new TreeMap<String, String>(data);
	}

	public TreeMap<String, String> getData()
	{
		return data;
	}

	public void setData(TreeMap<String, String> data)
	{
		this.data = data;
	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}
}
