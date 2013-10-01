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
package nl.gridline.zieook.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.gridline.zieook.api.JSonParent;

/**
 * Categories are the category types of items, as categories can be nested this structure is a nested xml
 * <p />
 * Project zieook-api-data<br />
 * Categories.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "category")
@XmlAccessorType(XmlAccessType.FIELD)
public class Categories implements Serializable, JSonParent
{
	/**
	 *  
	 */
	private static final long serialVersionUID = 7970840601402857551L;

	@XmlElement
	private String key;

	@XmlElement
	private String category;

	@XmlElement(name = "category")
	private List<Categories> categories;

	@XmlElement
	private String next;

	public Categories()
	{

	}

	/**
	 * @param category
	 * @param categories
	 * @param resumeId
	 */
	public Categories(String key, String category, List<Categories> categories, String next)
	{
		super();
		this.category = category;
		this.categories = categories;
		this.next = next;
	}

	public Categories(List<Categories> categories)
	{
		this.categories = categories;
	}

	public Categories(String category)
	{
		this.category = category;
		categories = null;
		next = null;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public List<Categories> getCategories()
	{
		return categories;
	}

	public void setCategories(List<Categories> categories)
	{
		this.categories = categories;
	}

	public String getNext()
	{
		return next;
	}

	public void setNext(String next)
	{
		this.next = next;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	@Override
	public String toString()
	{
		return new StringBuilder().append("category=").append(category).append(" categories.size=")
				.append(categories == null ? "null" : categories.size()).append(" next=").append(next).toString();
	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}
}
