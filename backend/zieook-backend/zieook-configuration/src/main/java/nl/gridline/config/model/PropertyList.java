// ---------------------------------------------------------
// Copyright, all rights reserved 2011 GridLine Amsterdam
// ---------------------------------------------------------
package nl.gridline.config.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * a list of properties - the simple version of the Configuration object - this one is only for marshalling xml and json
 * objects through the REST interface
 * <p />
 * Project gridline-config<br />
 * PropertyList.java created Sep 9, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyList implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5266498659555632587L;
	@XmlElement(name = "property")
	private List<Property> properties;

	public PropertyList()
	{
		properties = new ArrayList<Property>();
	}

	public PropertyList(List<Property> properties)
	{
		this.properties = properties;
	}

	public List<Property> getProperties()
	{
		return properties;
	}

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
		PropertyList other = (PropertyList) obj;
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
}
