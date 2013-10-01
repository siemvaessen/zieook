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
 * The list of recommender identifiers
 * <p />
 * Project zieook-api-workflow<br />
 * RecommenderList.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "recommenders")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recommenders implements Serializable, JSonParent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1175578329925295158L;

	@XmlElement(name = "recommender")
	private List<RecommenderConfig> recommenders;

	public Recommenders()
	{

	}

	public Recommenders(List<RecommenderConfig> recommenders)
	{
		this.recommenders = recommenders;
	}

	public List<RecommenderConfig> getRecommenders()
	{
		return recommenders;
	}

	public void setRecommenders(List<RecommenderConfig> recommenders)
	{
		this.recommenders = recommenders;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((recommenders == null) ? 0 : recommenders.hashCode());
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
		Recommenders other = (Recommenders) obj;
		if (recommenders == null)
		{
			if (other.recommenders != null)
			{
				return false;
			}
		}
		else if (!recommenders.equals(other.recommenders))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder().append(" recommender=");
		if (recommenders == null)
		{
			b.append("null");
		}
		else
		{
			b.append('[');
			if (recommenders.size() > 0)
			{
				for (RecommenderConfig s : recommenders)
				{
					b.append(s).append(',');
				}
				b.deleteCharAt(b.length() - 1);
			}
			b.append(']');
		}
		return b.toString();
	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}
}
