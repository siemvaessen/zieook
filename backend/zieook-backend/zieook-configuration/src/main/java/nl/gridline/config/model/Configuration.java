// ---------------------------------------------------------
// Copyright, all rights reserved 2011 GridLine Amsterdam
// ---------------------------------------------------------
package nl.gridline.config.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * [purpose]
 * <p />
 * Project zieook-configuration<br />
 * Configuration.java created 14 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 296444524721658990L;

	@XmlElement(name = "property")
	private List<Property> properties;

	public Configuration()
	{

	}

	public Configuration(List<Property> properties)
	{
		this.properties = properties;
	}

	public static Configuration create(File file) throws JAXBException, FileNotFoundException
	{
		JAXBContext context = JAXBContext.newInstance(Configuration.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (Configuration) unmarshaller.unmarshal(new FileReader(file));
	}

	public static Configuration create(URL url) throws JAXBException
	{
		JAXBContext context = JAXBContext.newInstance(Configuration.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (Configuration) unmarshaller.unmarshal(url);
	}

	public static Configuration create(String content) throws JAXBException
	{
		JAXBContext context = JAXBContext.newInstance(Configuration.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (Configuration) unmarshaller.unmarshal(new StringReader(content));
	}

	public String marshall() throws JAXBException
	{
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(Configuration.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.marshal(this, writer);
		writer.flush();
		return writer.toString();
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
		Configuration other = (Configuration) obj;
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
