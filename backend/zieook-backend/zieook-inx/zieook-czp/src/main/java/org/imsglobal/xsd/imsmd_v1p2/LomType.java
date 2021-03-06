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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.10.24 at 02:22:49 PM CEST 
//

package org.imsglobal.xsd.imsmd_v1p2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for lomType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="lomType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}general" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}lifecycle" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}metametadata" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}technical" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}educational" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}rights" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}relation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}annotation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}classification" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lomType", propOrder = {"general", "lifecycle", "metametadata", "technical", "educational", "rights",
		"relation", "annotation", "classification"})
public class LomType
{

	private GeneralType general;
	private LifecycleType lifecycle;
	private MetametadataType metametadata;
	private TechnicalType technical;
	private EducationalType educational;
	private RightsType rights;
	private List<RelationType> relation;
	private List<AnnotationType> annotation;
	private List<ClassificationType> classification;

	/**
	 * Gets the value of the general property.
	 * @return
	 *         possible object is {@link GeneralType }
	 */
	public GeneralType getGeneral()
	{
		return general;
	}

	/**
	 * Sets the value of the general property.
	 * @param value
	 *           allowed object is {@link GeneralType }
	 */
	public void setGeneral(GeneralType value)
	{
		general = value;
	}

	/**
	 * Gets the value of the lifecycle property.
	 * @return
	 *         possible object is {@link LifecycleType }
	 */
	public LifecycleType getLifecycle()
	{
		return lifecycle;
	}

	/**
	 * Sets the value of the lifecycle property.
	 * @param value
	 *           allowed object is {@link LifecycleType }
	 */
	public void setLifecycle(LifecycleType value)
	{
		lifecycle = value;
	}

	/**
	 * Gets the value of the metametadata property.
	 * @return
	 *         possible object is {@link MetametadataType }
	 */
	public MetametadataType getMetametadata()
	{
		return metametadata;
	}

	/**
	 * Sets the value of the metametadata property.
	 * @param value
	 *           allowed object is {@link MetametadataType }
	 */
	public void setMetametadata(MetametadataType value)
	{
		metametadata = value;
	}

	/**
	 * Gets the value of the technical property.
	 * @return
	 *         possible object is {@link TechnicalType }
	 */
	public TechnicalType getTechnical()
	{
		return technical;
	}

	/**
	 * Sets the value of the technical property.
	 * @param value
	 *           allowed object is {@link TechnicalType }
	 */
	public void setTechnical(TechnicalType value)
	{
		technical = value;
	}

	/**
	 * Gets the value of the educational property.
	 * @return
	 *         possible object is {@link EducationalType }
	 */
	public EducationalType getEducational()
	{
		return educational;
	}

	/**
	 * Sets the value of the educational property.
	 * @param value
	 *           allowed object is {@link EducationalType }
	 */
	public void setEducational(EducationalType value)
	{
		educational = value;
	}

	/**
	 * Gets the value of the rights property.
	 * @return
	 *         possible object is {@link RightsType }
	 */
	public RightsType getRights()
	{
		return rights;
	}

	/**
	 * Sets the value of the rights property.
	 * @param value
	 *           allowed object is {@link RightsType }
	 */
	public void setRights(RightsType value)
	{
		rights = value;
	}

	/**
	 * Gets the value of the relation property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the relation property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getRelation().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link RelationType }
	 */
	public List<RelationType> getRelation()
	{
		if (relation == null)
		{
			relation = new ArrayList<RelationType>();
		}
		return relation;
	}

	/**
	 * Gets the value of the annotation property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the annotation property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAnnotation().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link AnnotationType }
	 */
	public List<AnnotationType> getAnnotation()
	{
		if (annotation == null)
		{
			annotation = new ArrayList<AnnotationType>();
		}
		return annotation;
	}

	/**
	 * Gets the value of the classification property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the classification property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getClassification().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link ClassificationType }
	 */
	public List<ClassificationType> getClassification()
	{
		if (classification == null)
		{
			classification = new ArrayList<ClassificationType>();
		}
		return classification;
	}

}
