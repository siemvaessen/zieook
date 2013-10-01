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
import javax.xml.bind.annotation.XmlRootElement;

import nl.gridline.zieook.api.JSonParent;

/**
 * [purpose]
 * <p />
 * Project zieook-api-workflow<br />
 * Collections.java created 28 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "collections")
@XmlAccessorType(XmlAccessType.FIELD)
public class Collections implements Serializable, JSonParent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 932404511191188145L;

	@XmlElement(name = "collection")
	private List<Collection> collections;

	public Collections()
	{

	}

	public Collections(List<Collection> collections)
	{
		this.collections = collections;
	}

	public List<Collection> getCollections()
	{
		return collections;
	}

	public void setCollections(List<Collection> collections)
	{
		this.collections = collections;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collections == null) ? 0 : collections.hashCode());
		return result;
	}

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
		Collections other = (Collections) obj;
		if (collections == null)
		{
			if (other.collections != null)
			{
				return false;
			}
		}
		else if (!collections.equals(other.collections))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}

}
