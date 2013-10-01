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
import javax.xml.bind.annotation.XmlTransient;

import nl.gridline.zieook.api.JSonParent;
import nl.gridline.zieook.api.StorableHBase;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * [purpose]
 * <p />
 * Project zieook-api-workflow<br />
 * Collection.java created 24 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "collection")
@XmlAccessorType(XmlAccessType.FIELD)
public class Collection implements Serializable, JSonParent, StorableHBase
{

	@XmlTransient
	public static enum Type
	{
		MOVIELENS
		{
			@Override
			public String toString()
			{
				return "movielens";
			}
		},
		OAI
		{
			@Override
			public String toString()
			{
				return "oai";
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6700987048970710840L;
	@XmlElement
	private String cp;
	@XmlElement
	private String name;
	@XmlElement
	private String type;
	@XmlElement
	private String location;
	@XmlElement(name = "oai_sets")
	private OAISets oaiSets;

	@XmlElement(name = "oai_metadata")
	private OAIMetadata oaiMetadata;

	@XmlElement
	private long interval;

	public Collection()
	{
		cp = null;
		name = null;
		type = null;
		location = null;
		oaiSets = null;
		oaiMetadata = null;
	}

	public Collection(String cp, String name, String type, String location, OAISets oaiSets, OAIMetadata oaiMetadata,
			long interval)
	{
		super();
		this.cp = cp;
		this.name = name;
		this.type = type;
		this.location = location;
		this.interval = interval;
		this.oaiSets = oaiSets;
		this.oaiMetadata = oaiMetadata;
	}

	public Collection(NavigableMap<byte[], byte[]> map)
	{
		cp = ModelConstants.getCp(map);
		name = ModelConstants.getName(map);
		type = ModelConstants.getType(map);
		location = ModelConstants.getLocation(map);
		interval = ModelConstants.getInterval(map);

		oaiSets = new OAISets(map);
		if (oaiSets.getImportSets() == null && oaiSets.getSetNames() == null && oaiSets.getSets() == null)
		{
			oaiSets = null;
		}

		oaiMetadata = new OAIMetadata(map);
		if (oaiMetadata.getMetadataPrefix() == null && oaiMetadata.getUse() == null)
		{
			oaiMetadata = null;
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
		ModelConstants.putCp(map, cp);
		ModelConstants.putName(map, name);
		ModelConstants.putType(map, type);
		ModelConstants.putLocation(map, location);
		ModelConstants.putInterval(map, interval);
		if (oaiSets != null)
		{
			oaiSets.toMap(map);
		}
		if (oaiMetadata != null)
		{
			oaiMetadata.toMap(map);
		}
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
	 * @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * @return The location.
	 */
	public String getLocation()
	{
		return location;
	}

	/**
	 * @param location The location to set.
	 */
	public void setLocation(String location)
	{
		this.location = location;
	}

	public long getInterval()
	{
		return interval;
	}

	public void setInterval(long interval)
	{
		this.interval = interval;
	}

	/**
	 * @return The oaiSets.
	 */
	public OAISets getOaiSets()
	{
		return oaiSets;
	}

	/**
	 * @param oaiSets The oaiSets to set.
	 */
	public void setOaiSets(OAISets oaiSets)
	{
		this.oaiSets = oaiSets;
	}

	/**
	 * @return The oaiMetadata.
	 */
	public OAIMetadata getOaiMetadata()
	{
		return oaiMetadata;
	}

	/**
	 * @param oaiMetadata The oaiMetadata to set.
	 */
	public void setOaiMetadata(OAIMetadata oaiMetadata)
	{
		this.oaiMetadata = oaiMetadata;
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
		result = prime * result + ((cp == null) ? 0 : cp.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		Collection other = (Collection) obj;
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
		if (location == null)
		{
			if (other.location != null)
			{
				return false;
			}
		}
		else if (!location.equals(other.location))
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
		if (type == null)
		{
			if (other.type != null)
			{
				return false;
			}
		}
		else if (!type.equals(other.type))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return new StringBuilder().append("cp=").append(cp).append(" name=").append(name).append(" type=").append(type)
				.append(" location=").append(location).toString();
	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}

}
