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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A recommender module.
 * <ul>
 * <li>name - the name of the module: one off <tt>item-based</tt>, <tt>user-based</tt> or <tt>text-based</tt></li>
 * <li>properties the list of properties to configure the recommender, e.g. the distance measure</li>
 * </ul>
 * <p />
 * Project zieook-api-workflow<br />
 * Module.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "module")
@XmlAccessorType(XmlAccessType.FIELD)
public class Module implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7751785165284852998L;

	@XmlElement
	private String name;

	@XmlElement(name = "property")
	@XmlElementWrapper(name = "properties")
	private List<Property> properties;

	public Module()
	{

	}

	public Module(String name, List<Property> properties)
	{
		super();
		this.name = name;
		this.properties = properties;
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
	 * @return The properties.
	 */
	public List<Property> getProperties()
	{
		return properties;
	}

	/**
	 * @param properties The properties to set.
	 */
	public void setProperties(List<Property> properties)
	{
		this.properties = properties;
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
		Module other = (Module) obj;
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
		if (properties == null)
		{
			if (other.properties != null)
			{
				return false;
			}
		}
		else if (!properties.equals(other.properties))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder().append("name=").append(name).append(" properties=");
		if (properties == null)
		{
			b.append("null");
		}
		else
		{
			b.append('[');
			if (properties.size() > 0)
			{
				for (Property p : properties)
				{
					b.append(p).append(',');
				}
				b.deleteCharAt(b.length() - 1);
			}
			b.append(']');
		}
		return b.toString();
	}

}
