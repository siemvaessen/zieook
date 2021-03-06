//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.10.24 at 04:39:25 PM CEST 
//

package org.openarchives.oai._2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for IdentifyType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IdentifyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="repositoryName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="baseURL" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="protocolVersion" type="{http://www.openarchives.org/OAI/2.0/}protocolVersionType"/>
 *         &lt;element name="adminEmail" type="{http://www.openarchives.org/OAI/2.0/}emailType" maxOccurs="unbounded"/>
 *         &lt;element name="earliestDatestamp" type="{http://www.openarchives.org/OAI/2.0/}UTCdatetimeType"/>
 *         &lt;element name="deletedRecord" type="{http://www.openarchives.org/OAI/2.0/}deletedRecordType"/>
 *         &lt;element name="granularity" type="{http://www.openarchives.org/OAI/2.0/}granularityType"/>
 *         &lt;element name="compression" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.openarchives.org/OAI/2.0/}descriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentifyType", propOrder = {"repositoryName", "baseURL", "protocolVersion", "adminEmail",
		"earliestDatestamp", "deletedRecord", "granularity", "compression", "description"})
public class IdentifyType implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6477023624639260669L;
	@XmlElement(required = true)
	protected String repositoryName;
	@XmlElement(required = true)
	@XmlSchemaType(name = "anyURI")
	protected String baseURL;
	@XmlElement(required = true)
	protected String protocolVersion;
	@XmlElement(required = true)
	protected List<String> adminEmail;
	@XmlElement(required = true)
	protected String earliestDatestamp;
	@XmlElement(required = true)
	protected DeletedRecordType deletedRecord;
	@XmlElement(required = true)
	protected GranularityType granularity;
	protected List<String> compression;
	protected List<DescriptionType> description;

	/**
	 * Gets the value of the repositoryName property.
	 * @return
	 *         possible object is {@link String }
	 */
	public String getRepositoryName()
	{
		return repositoryName;
	}

	/**
	 * Sets the value of the repositoryName property.
	 * @param value
	 *           allowed object is {@link String }
	 */
	public void setRepositoryName(String value)
	{
		repositoryName = value;
	}

	/**
	 * Gets the value of the baseURL property.
	 * @return
	 *         possible object is {@link String }
	 */
	public String getBaseURL()
	{
		return baseURL;
	}

	/**
	 * Sets the value of the baseURL property.
	 * @param value
	 *           allowed object is {@link String }
	 */
	public void setBaseURL(String value)
	{
		baseURL = value;
	}

	/**
	 * Gets the value of the protocolVersion property.
	 * @return
	 *         possible object is {@link String }
	 */
	public String getProtocolVersion()
	{
		return protocolVersion;
	}

	/**
	 * Sets the value of the protocolVersion property.
	 * @param value
	 *           allowed object is {@link String }
	 */
	public void setProtocolVersion(String value)
	{
		protocolVersion = value;
	}

	/**
	 * Gets the value of the adminEmail property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the adminEmail property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAdminEmail().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 */
	public List<String> getAdminEmail()
	{
		if (adminEmail == null)
		{
			adminEmail = new ArrayList<String>();
		}
		return adminEmail;
	}

	/**
	 * Gets the value of the earliestDatestamp property.
	 * @return
	 *         possible object is {@link String }
	 */
	public String getEarliestDatestamp()
	{
		return earliestDatestamp;
	}

	/**
	 * Sets the value of the earliestDatestamp property.
	 * @param value
	 *           allowed object is {@link String }
	 */
	public void setEarliestDatestamp(String value)
	{
		earliestDatestamp = value;
	}

	/**
	 * Gets the value of the deletedRecord property.
	 * @return
	 *         possible object is {@link DeletedRecordType }
	 */
	public DeletedRecordType getDeletedRecord()
	{
		return deletedRecord;
	}

	/**
	 * Sets the value of the deletedRecord property.
	 * @param value
	 *           allowed object is {@link DeletedRecordType }
	 */
	public void setDeletedRecord(DeletedRecordType value)
	{
		deletedRecord = value;
	}

	/**
	 * Gets the value of the granularity property.
	 * @return
	 *         possible object is {@link GranularityType }
	 */
	public GranularityType getGranularity()
	{
		return granularity;
	}

	/**
	 * Sets the value of the granularity property.
	 * @param value
	 *           allowed object is {@link GranularityType }
	 */
	public void setGranularity(GranularityType value)
	{
		granularity = value;
	}

	/**
	 * Gets the value of the compression property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the compression property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getCompression().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 */
	public List<String> getCompression()
	{
		if (compression == null)
		{
			compression = new ArrayList<String>();
		}
		return compression;
	}

	/**
	 * Gets the value of the description property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the description property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDescription().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link DescriptionType }
	 */
	public List<DescriptionType> getDescription()
	{
		if (description == null)
		{
			description = new ArrayList<DescriptionType>();
		}
		return description;
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
		result = prime * result + ((adminEmail == null) ? 0 : adminEmail.hashCode());
		result = prime * result + ((baseURL == null) ? 0 : baseURL.hashCode());
		result = prime * result + ((compression == null) ? 0 : compression.hashCode());
		result = prime * result + ((deletedRecord == null) ? 0 : deletedRecord.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((earliestDatestamp == null) ? 0 : earliestDatestamp.hashCode());
		result = prime * result + ((granularity == null) ? 0 : granularity.hashCode());
		result = prime * result + ((protocolVersion == null) ? 0 : protocolVersion.hashCode());
		result = prime * result + ((repositoryName == null) ? 0 : repositoryName.hashCode());
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
		IdentifyType other = (IdentifyType) obj;
		if (adminEmail == null)
		{
			if (other.adminEmail != null)
			{
				return false;
			}
		}
		else if (!adminEmail.equals(other.adminEmail))
		{
			return false;
		}
		if (baseURL == null)
		{
			if (other.baseURL != null)
			{
				return false;
			}
		}
		else if (!baseURL.equals(other.baseURL))
		{
			return false;
		}
		if (compression == null)
		{
			if (other.compression != null)
			{
				return false;
			}
		}
		else if (!compression.equals(other.compression))
		{
			return false;
		}
		if (deletedRecord != other.deletedRecord)
		{
			return false;
		}
		if (description == null)
		{
			if (other.description != null)
			{
				return false;
			}
		}
		else if (!description.equals(other.description))
		{
			return false;
		}
		if (earliestDatestamp == null)
		{
			if (other.earliestDatestamp != null)
			{
				return false;
			}
		}
		else if (!earliestDatestamp.equals(other.earliestDatestamp))
		{
			return false;
		}
		if (granularity != other.granularity)
		{
			return false;
		}
		if (protocolVersion == null)
		{
			if (other.protocolVersion != null)
			{
				return false;
			}
		}
		else if (!protocolVersion.equals(other.protocolVersion))
		{
			return false;
		}
		if (repositoryName == null)
		{
			if (other.repositoryName != null)
			{
				return false;
			}
		}
		else if (!repositoryName.equals(other.repositoryName))
		{
			return false;
		}
		return true;
	}

}
