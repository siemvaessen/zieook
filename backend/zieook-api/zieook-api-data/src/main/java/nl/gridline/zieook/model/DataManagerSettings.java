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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.gridline.zieook.api.JSonParent;

/**
 * dataManager settings shows server setting in a key / value xml
 * <p />
 * Project zieook-api-data<br />
 * DataManagerSettings.java created 4 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DataManagerSettings implements Serializable, JSonParent
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4345207860829379747L;

	public static class SettingsAdapter extends XmlAdapter<HashMapType, HashMap<String, String>>
	{
		public SettingsAdapter()
		{

		}

		@Override
		public HashMap<String, String> unmarshal(HashMapType value)
		{
			HashMap<String, String> result = new HashMap<String, String>(value.entries.size());
			for (HashMapEntry entry : value.entries)
			{
				result.put(entry.key, entry.value);
			}
			return result;
		}

		@Override
		public HashMapType marshal(HashMap<String, String> value)
		{
			List<HashMapEntry> list = new ArrayList<HashMapEntry>(value.size());

			for (Map.Entry<String, String> entry : value.entrySet())
			{
				list.add(new HashMapEntry(entry.getKey(), entry.getValue()));
			}

			return new HashMapType(list);
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
	private HashMap<String, String> data;

	public DataManagerSettings()
	{
		// no-arg constructor
	}

	public DataManagerSettings(Map<String, String> data)
	{
		this.data = new HashMap<String, String>();
		this.data.putAll(data);
	}

	public HashMap<String, String> getData()
	{
		return data;
	}

	public void setData(HashMap<String, String> data)
	{
		this.data = data;
	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}
}
