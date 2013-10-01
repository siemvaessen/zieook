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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import nl.gridline.zieook.api.JSonParent;
import nl.gridline.zieook.api.StorableHBase;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * The configuration of a recommender
 * <ul>
 * <li>the recommender name</li>
 * <li>modules the list of recommender modules (currently only one is allowed)</li>
 * </ul>
 * <p />
 * Project zieook-api-workflow<br />
 * MahoutTemplate.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "template")
@XmlAccessorType(XmlAccessType.FIELD)
public class Template implements Serializable, JSonParent, StorableHBase
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2368948175441860606L;
	@XmlElement
	private String name;

	@XmlElement(name = "module")
	@XmlElementWrapper(name = "modules")
	private List<Module> modules;

	public Template()
	{

	}

	public Template(String name, List<Module> modules)
	{
		this.name = name;
		this.modules = new ArrayList<Module>(modules);
	}

	/**
	 * Reads the modules list from the input array
	 * @param name template name
	 * @param modules modules as a serialized object
	 * @throws IOException IOException if reading from the modules byte array goes wrong
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public Template(NavigableMap<byte[], byte[]> template) throws IOException, ClassNotFoundException
	{
		// we have only one key-value in the map, for now
		for (Map.Entry<byte[], byte[]> entry : template.entrySet())
		{
			name = Bytes.toString(entry.getKey());
			ByteArrayInputStream input = new ByteArrayInputStream(entry.getValue());
			ObjectInput in = new ObjectInputStream(input);
			modules = (List<Module>) in.readObject();
			break;
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
	 * @see nl.gridline.zieook.workflow.model.StorableHBase#toMap(java.util.NavigableMap)
	 */
	@Override
	public NavigableMap<byte[], byte[]> toMap(NavigableMap<byte[], byte[]> map) throws IOException
	{

		// serialize the modules to a byte array:
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(result);
		out.writeObject(new ArrayList<Module>(modules));
		out.close();

		// return the data:
		map.put(Bytes.toBytes(name), result.toByteArray());

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
	 * @return The modules.
	 */
	public List<Module> getModules()
	{
		return modules;
	}

	/**
	 * @param modules The modules to set.
	 */
	public void setModules(ArrayList<Module> modules)
	{
		this.modules = modules;
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
		result = prime * result + ((modules == null) ? 0 : modules.hashCode());
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
		Template other = (Template) obj;
		if (modules == null)
		{
			if (other.modules != null)
			{
				return false;
			}
		}
		else if (!modules.equals(other.modules))
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
		StringBuilder b = new StringBuilder().append("name=").append(name).append(" modules=");
		if (modules == null)
		{
			b.append("null");
		}
		else
		{
			b.append('[');
			if (modules.size() > 0)
			{
				for (Module m : modules)
				{
					b.append(m).append(',');
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
