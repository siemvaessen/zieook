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
 * OAISets.java created 19 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "oai_sets")
@XmlAccessorType(XmlAccessType.FIELD)
public class OAISets implements Serializable, JSonParent, StorableHBase
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8770412901242928548L;

	@XmlElement(name = "set")
	private final List<String> sets;

	@XmlElement(name = "set_names")
	private final List<String> setNames;

	@XmlElement(name = "import")
	private final List<String> importSets;

	public OAISets()
	{
		sets = null;
		setNames = null;
		importSets = null;
	}

	public OAISets(NavigableMap<byte[], byte[]> map)
	{
		String[] data = ModelConstants.getOAISets(map, '\n');
		if (data != null)
		{
			sets = new ArrayList<String>(Arrays.asList(data));
		}
		else
		{
			sets = null;
		}

		data = ModelConstants.getOAISetNames(map, '\n');
		if (data != null)
		{
			setNames = new ArrayList<String>(Arrays.asList(data));
		}
		else
		{
			setNames = null;
		}

		data = ModelConstants.getOAISetsUse(map, '\n');
		if (data != null)
		{
			importSets = new ArrayList<String>(Arrays.asList(data));
		}
		else
		{
			importSets = null;
		}
	}

	public OAISets(List<String> sets, List<String> importSets, List<String> setNames)
	{
		if (sets.size() != setNames.size())
		{
			throw new IllegalArgumentException("setnames and sets need to be of the same size");
		}
		this.sets = sets;
		this.importSets = importSets;
		this.setNames = setNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.model.ModelParent#toMap()
	 */
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
		if (sets != null)
		{
			ModelConstants.putOAISets(map, sets.toArray(new String[sets.size()]), '\n');
		}
		if (importSets != null)
		{
			ModelConstants.putOAISetsUse(map, importSets.toArray(new String[importSets.size()]), '\n');
		}
		if (setNames != null)
		{
			ModelConstants.putOAISetNames(map, setNames.toArray(new String[setNames.size()]), '\n');
		}
		return map;
	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}

	/**
	 * @return The sets.
	 */
	public List<String> getSets()
	{
		return sets;
	}

	public List<String> getSetNames()
	{
		return setNames;
	}

	/**
	 * @return The importSets.
	 */
	public List<String> getImportSets()
	{
		return importSets;
	}

	public String getImportSetsAsString()
	{
		if (importSets != null && importSets.size() > 0)
		{
			StringBuilder b = new StringBuilder();
			for (String s : importSets)
			{
				b.append(s).append('\n');
			}
			b.deleteCharAt(b.length() - 1);
			return b.toString();
		}
		return null;
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
		result = prime * result + ((importSets == null) ? 0 : importSets.hashCode());
		result = prime * result + ((setNames == null) ? 0 : setNames.hashCode());
		result = prime * result + ((sets == null) ? 0 : sets.hashCode());
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
		OAISets other = (OAISets) obj;
		if (importSets == null)
		{
			if (other.importSets != null)
			{
				return false;
			}
		}
		else if (!importSets.equals(other.importSets))
		{
			return false;
		}
		if (setNames == null)
		{
			if (other.setNames != null)
			{
				return false;
			}
		}
		else if (!setNames.equals(other.setNames))
		{
			return false;
		}
		if (sets == null)
		{
			if (other.sets != null)
			{
				return false;
			}
		}
		else if (!sets.equals(other.sets))
		{
			return false;
		}
		return true;
	}

}
