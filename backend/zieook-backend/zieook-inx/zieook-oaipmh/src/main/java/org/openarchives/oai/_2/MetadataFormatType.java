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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for metadataFormatType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="metadataFormatType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="metadataPrefix" type="{http://www.openarchives.org/OAI/2.0/}metadataPrefixType"/>
 *         &lt;element name="schema" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="metadataNamespace" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "metadataFormatType", propOrder = {"metadataPrefix", "schema", "metadataNamespace"})
public class MetadataFormatType implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1444479073374108852L;
	@XmlElement(required = true)
	protected String metadataPrefix;
	@XmlElement(required = true)
	@XmlSchemaType(name = "anyURI")
	protected String schema;
	@XmlElement(required = true)
	@XmlSchemaType(name = "anyURI")
	protected String metadataNamespace;

	/**
	 * Gets the value of the metadataPrefix property.
	 * @return
	 *         possible object is {@link String }
	 */
	public String getMetadataPrefix()
	{
		return metadataPrefix;
	}

	/**
	 * Sets the value of the metadataPrefix property.
	 * @param value
	 *           allowed object is {@link String }
	 */
	public void setMetadataPrefix(String value)
	{
		metadataPrefix = value;
	}

	/**
	 * Gets the value of the schema property.
	 * @return
	 *         possible object is {@link String }
	 */
	public String getSchema()
	{
		return schema;
	}

	/**
	 * Sets the value of the schema property.
	 * @param value
	 *           allowed object is {@link String }
	 */
	public void setSchema(String value)
	{
		schema = value;
	}

	/**
	 * Gets the value of the metadataNamespace property.
	 * @return
	 *         possible object is {@link String }
	 */
	public String getMetadataNamespace()
	{
		return metadataNamespace;
	}

	/**
	 * Sets the value of the metadataNamespace property.
	 * @param value
	 *           allowed object is {@link String }
	 */
	public void setMetadataNamespace(String value)
	{
		metadataNamespace = value;
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
		result = prime * result + ((metadataNamespace == null) ? 0 : metadataNamespace.hashCode());
		result = prime * result + ((metadataPrefix == null) ? 0 : metadataPrefix.hashCode());
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
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
		MetadataFormatType other = (MetadataFormatType) obj;
		if (metadataNamespace == null)
		{
			if (other.metadataNamespace != null)
			{
				return false;
			}
		}
		else if (!metadataNamespace.equals(other.metadataNamespace))
		{
			return false;
		}
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
		if (schema == null)
		{
			if (other.schema != null)
			{
				return false;
			}
		}
		else if (!schema.equals(other.schema))
		{
			return false;
		}
		return true;
	}

}
