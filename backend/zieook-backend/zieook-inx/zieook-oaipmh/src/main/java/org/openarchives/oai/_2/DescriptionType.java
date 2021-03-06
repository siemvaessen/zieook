//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.10.24 at 04:39:25 PM CEST 
//

package org.openarchives.oai._2;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The descriptionType is used for the description
 * element in Identify and for setDescription element in ListSets.
 * Content must be compliant with an XML Schema defined by a
 * community.
 * <p>
 * Java class for descriptionType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="descriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any namespace='##other'/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "descriptionType", propOrder = {"any"})
public class DescriptionType implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -855729390023222467L;
	@XmlAnyElement(lax = true)
	protected Object any;

	/**
	 * Gets the value of the any property.
	 * @return
	 *         possible object is {@link Object }
	 */
	public Object getAny()
	{
		return any;
	}

	/**
	 * Sets the value of the any property.
	 * @param value
	 *           allowed object is {@link Object }
	 */
	public void setAny(Object value)
	{
		any = value;
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
		result = prime * result + ((any == null) ? 0 : any.hashCode());
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
		DescriptionType other = (DescriptionType) obj;
		if (any == null)
		{
			if (other.any != null)
			{
				return false;
			}
		}
		else if (!any.equals(other.any))
		{
			return false;
		}
		return true;
	}

}
