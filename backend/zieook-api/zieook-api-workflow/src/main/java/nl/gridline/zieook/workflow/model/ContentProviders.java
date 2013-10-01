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
 * ContentProviders.java created 21 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:daryl@gridline.nl">Daryl</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "content_providers")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContentProviders implements Serializable, JSonParent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4198655306680536207L;

	@XmlElement(name = "content_provider")
	private List<ContentProvider> contentProviders;

	public ContentProviders()
	{
		// not arg constructor
	}

	public ContentProviders(List<ContentProvider> contentProviders)
	{
		super();
		this.contentProviders = contentProviders;
	}

	public List<ContentProvider> getContentProviders()
	{
		return contentProviders;
	}

	public void setContentProviders(List<ContentProvider> contentProviders)
	{
		this.contentProviders = contentProviders;
	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}

	@Override
	public String toString()
	{
		if (contentProviders != null)
		{
			StringBuilder result = new StringBuilder();
			for (ContentProvider cp : contentProviders)
			{
				result.append(cp.toString()).append("\n");
			}
			return result.toString();
		}
		return "\"empty\"";

	}
}
