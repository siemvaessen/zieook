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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for requirementType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="requirementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}type" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}name" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}minimumversion" minOccurs="0"/>
 *         &lt;element ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}maximumversion" minOccurs="0"/>
 *         &lt;group ref="{http://www.imsglobal.org/xsd/imsmd_v1p2}grp.any"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "requirementType", propOrder = {"content"})
public class RequirementType
{

	@XmlElementRefs({
			@XmlElementRef(name = "name", namespace = "http://www.imsglobal.org/xsd/imsmd_v1p2", type = JAXBElement.class),
			@XmlElementRef(name = "minimumversion", namespace = "http://www.imsglobal.org/xsd/imsmd_v1p2", type = JAXBElement.class),
			@XmlElementRef(name = "type", namespace = "http://www.imsglobal.org/xsd/imsmd_v1p2", type = JAXBElement.class),
			@XmlElementRef(name = "maximumversion", namespace = "http://www.imsglobal.org/xsd/imsmd_v1p2", type = JAXBElement.class)})
	@XmlMixed
	@XmlAnyElement(lax = true)
	private List<Object> content;

	/**
	 * Gets the value of the content property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the content property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getContent().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Object } {@link JAXBElement }{@code <}
	 * {@link NameType }{@code >} {@link JAXBElement }{@code <}{@link TypeType }{@code >} {@link String } {@link JAXBElement }
	 * {@code <}{@link String }{@code >} {@link JAXBElement }{@code <}{@link String }{@code >}
	 */
	public List<Object> getContent()
	{
		if (content == null)
		{
			content = new ArrayList<Object>();
		}
		return content;
	}

}