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
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.gridline.zieook.api.JSonParent;
import nl.gridline.zieook.api.StorableHBase;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * The collection item model, these are the fields being interpreted. Import modules should map to the fields in this
 * class
 * <p />
 * Project zieook-api-data<br />
 * CollectionItem.java created 22 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionItem implements Serializable, JSonParent, StorableHBase
{
	// WARNING: changes here will have to match changes in
	// nl.gridline.zieook.data.hbase.dao.CollectionTable

	/**
	 * 
	 */
	private static final long serialVersionUID = -3503112110364533322L;

	@XmlElement(required = true)
	private Long id;

	@XmlElement(name = "collection_id", required = true)
	private String collectionId;

	@XmlElement
	private String cp; // content provider
	@XmlElement
	private List<String> category;
	@XmlElement
	private String title;
	@XmlElement
	private String description;
	@XmlElement(name = "create_date")
	private Long createDate;
	@XmlElement
	private List<String> keywords; // import, export access exclusively via String[]

	@XmlElement(name = "location")
	private String location;

	@XmlElement(name = "image_url")
	private String image;

	public CollectionItem()
	{

	}

	public CollectionItem(long id, String collectionId, String cp, List<String> category, String title, String tekst,
			Long createDate, List<String> keywords, String location, String image)
	{
		super();

		this.id = id;
		this.collectionId = collectionId;
		this.cp = cp;
		this.category = category;
		this.title = title;
		description = tekst;
		this.createDate = createDate;
		this.keywords = keywords;
		this.location = location;
		this.image = image;

	}

	public CollectionItem(NavigableMap<byte[], byte[]> map)
	{
		id = ModelConstants.getItem(map);
		collectionId = ModelConstants.getCollectionId(map);
		cp = ModelConstants.getCp(map);
		createDate = ModelConstants.getStamp(map);

		String categories = ModelConstants.getCategory(map);
		if (categories != null)
		{
			category = Arrays.asList(categories.split("\n"));
		}
		title = ModelConstants.getTitle(map);
		description = ModelConstants.getDescription(map);
		String keyword = ModelConstants.getKeyWords(map);
		if (keyword != null)
		{
			keywords = Arrays.asList(keyword.split("\n"));
		}
		location = ModelConstants.getLocation(map);
		image = ModelConstants.getImage(map);
	}

	@Override
	public NavigableMap<byte[], byte[]> toMap()
	{
		return toMap(new TreeMap<byte[], byte[]>(Bytes.BYTES_COMPARATOR));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.StorableHBase#toMap(java.util.NavigableMap)
	 */
	@Override
	public NavigableMap<byte[], byte[]> toMap(NavigableMap<byte[], byte[]> map)
	{
		ModelConstants.putItem(map, id);
		ModelConstants.putCollectionId(map, collectionId);
		ModelConstants.putCp(map, cp);
		ModelConstants.putTitle(map, title);
		ModelConstants.putCategory(map, toString(category));
		ModelConstants.putDescription(map, description);
		ModelConstants.putStamp(map, createDate);
		ModelConstants.putKeyWords(map, toString(keywords));
		ModelConstants.putLocation(map, location);
		ModelConstants.putImage(map, image);

		return map;
	}

	private String toString(List<String> list)
	{
		if (list != null)
		{
			StringBuilder b = new StringBuilder();
			for (String s : list)
			{
				b.append(s).append('\n');
			}
			return b.toString().trim();
		}
		return null;
	}

	/**
	 * @return The id.
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @return The cp.
	 */
	public String getCp()
	{
		return cp;
	}

	/**
	 * @param cp The cp to set.
	 */
	public void setCp(String cp)
	{
		this.cp = cp;
	}

	/**
	 * @return The category.
	 */
	public List<String> getCategory()
	{
		return category;
	}

	/**
	 * @param category The category to set.
	 */
	public void setCategory(List<String> category)
	{
		this.category = category;
	}

	/**
	 * @return The title.
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return The description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return The createDate.
	 */
	public Long getCreateDate()
	{
		return createDate;
	}

	/**
	 * @param createDate The createDate to set.
	 */
	public void setCreateDate(Long createDate)
	{
		this.createDate = createDate;
	}

	/**
	 * @return The createDate = releaseData.
	 */
	public Long getReleaseDate()
	{
		return createDate;
	}

	/**
	 * @param releaseDate The releaseDate/createDate to set.
	 */
	public void setReleaseDate(Long releaseDate)
	{
		setCreateDate(releaseDate);
	}

	/**
	 * @return The keywords as String[].
	 */
	public List<String> getKeywords()
	{
		return keywords;
	}

	/**
	 * @param keywords The keywords (as String[]) to set.
	 */
	public void setKeywords(List<String> keywords)
	{
		this.keywords = keywords;
	}

	/**
	 * @return The collectionId.
	 */
	public String getCollectionId()
	{
		return collectionId;
	}

	/**
	 * @param collectionId The collectionId to set.
	 */
	public void setCollectionId(String collectionId)
	{
		this.collectionId = collectionId;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getImage()
	{
		return image;
	}

	public void setImage(String image)
	{
		this.image = image;
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		b.append("id=").append(id).append(" collection_id=").append(collectionId).append(" cp=").append(cp);
		b.append(" create_date=").append(createDate).append(" title=").append(title);
		b.append(" description.length=").append((description != null ? description.length() : "null"));
		b.append(" keywords=").append(keywords);
		b.append(" category=").append(category);

		return b.toString();
	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
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
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((collectionId == null) ? 0 : collectionId.hashCode());
		result = prime * result + ((cp == null) ? 0 : cp.hashCode());
		result = prime * result + ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		CollectionItem other = (CollectionItem) obj;
		if (category == null)
		{
			if (other.category != null)
			{
				return false;
			}
		}
		else if (!category.equals(other.category))
		{
			return false;
		}
		if (collectionId == null)
		{
			if (other.collectionId != null)
			{
				return false;
			}
		}
		else if (!collectionId.equals(other.collectionId))
		{
			return false;
		}
		if (cp == null)
		{
			if (other.cp != null)
			{
				return false;
			}
		}
		else if (!cp.equals(other.cp))
		{
			return false;
		}
		if (createDate == null)
		{
			if (other.createDate != null)
			{
				return false;
			}
		}
		else if (!createDate.equals(other.createDate))
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
		if (id == null)
		{
			if (other.id != null)
			{
				return false;
			}
		}
		else if (!id.equals(other.id))
		{
			return false;
		}
		if (image == null)
		{
			if (other.image != null)
			{
				return false;
			}
		}
		else if (!image.equals(other.image))
		{
			return false;
		}
		if (keywords == null)
		{
			if (other.keywords != null)
			{
				return false;
			}
		}
		else if (!keywords.equals(other.keywords))
		{
			return false;
		}
		if (location == null)
		{
			if (other.location != null)
			{
				return false;
			}
		}
		else if (!location.equals(other.location))
		{
			return false;
		}
		if (title == null)
		{
			if (other.title != null)
			{
				return false;
			}
		}
		else if (!title.equals(other.title))
		{
			return false;
		}
		return true;
	}

}
