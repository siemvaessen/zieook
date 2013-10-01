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
import java.util.Arrays;
import java.util.List;
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
 * OAIMetaData.java created 19 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "oai_metadata_prefixes")
@XmlAccessorType(XmlAccessType.FIELD)
public class OAIMetadata implements Serializable, JSonParent, StorableHBase
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6600530550078400374L;

	@XmlElement(name = "prefix")
	private List<String> metadataPrefix;

	@XmlElement(name = "use")
	private String use;

	/**
	 * @param metadataPrefix
	 */
	public OAIMetadata(List<String> metadataPrefix, String use)
	{
		this.use = use;
		this.metadataPrefix = metadataPrefix;
	}

	public OAIMetadata()
	{
		metadataPrefix = null;
	}

	public OAIMetadata(NavigableMap<byte[], byte[]> map)
	{
		use = ModelConstants.getOAIMetadataUse(map);
		String[] data = ModelConstants.getOAIMetadata(map, '\n');
		if (data != null)
		{
			metadataPrefix = new ArrayList<String>(Arrays.asList(data));
		}
		else
		{
			metadataPrefix = null;
		}

	}

	@Override
	public NavigableMap<byte[], byte[]> toMap()
	{
		return toMap(new TreeMap<byte[], byte[]>(Bytes.BYTES_COMPARATOR));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.model.ModelParent#toMap(java.util.NavigableMap)
	 */
	@Override
	public NavigableMap<byte[], byte[]> toMap(NavigableMap<byte[], byte[]> map)
	{
		ModelConstants.putOAIMetadataUse(map, use);
		if (metadataPrefix != null)
		{
			ModelConstants.putOAIMetadata(map, metadataPrefix.toArray(new String[metadataPrefix.size()]), '\n');
		}
		return map;
	}

	@Override
	public String toString()
	{
		if (metadataPrefix != null && metadataPrefix.size() > 0)
		{
			StringBuilder b = new StringBuilder();
			for (String s : metadataPrefix)
			{
				b.append(s).append('\n');
			}
			b.deleteCharAt(b.length() - 1);
		}
		return "";
	}

	/**
	 * @return The metadataPrefix.
	 */
	public List<String> getMetadataPrefix()
	{
		return metadataPrefix;
	}

	/**
	 * @param metadataPrefix The metadataPrefix to set.
	 */
	public void setMetadataPrefix(List<String> metadataPrefix)
	{
		this.metadataPrefix = metadataPrefix;
	}

	/**
	 * @return The use.
	 */
	public String getUse()
	{
		return use;
	}

	/**
	 * @param use The use to set.
	 */
	public void setUse(String use)
	{
		this.use = use;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.model.ModelParent#toJSON()
	 */
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
		result = prime * result + ((metadataPrefix == null) ? 0 : metadataPrefix.hashCode());
		result = prime * result + ((use == null) ? 0 : use.hashCode());
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
		OAIMetadata other = (OAIMetadata) obj;
		if (metadataPrefix == null)
		{
			if (other.metadataPrefix != null)
			{
				return false;
			}
		}
		else if (!metadataPrefix.equals(other.metadataPrefix))
		{
			return false;
		}
		if (use == null)
		{
			if (other.use != null)
			{
				return false;
			}
		}
		else if (!use.equals(other.use))
		{
			return false;
		}
		return true;
	}

}
