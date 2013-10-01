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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * ZooKeeperElement.java created 22 jun. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ZooKeeperElement
{
	@XmlElement(name = "path")
	private String path;

	@XmlElement(name = "value")
	private Long value;

	/**
	 * @return The value.
	 */
	public Long getValue()
	{
		return value;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(Long value)
	{
		this.value = value;
	}

	@XmlElement(name = "children")
	private List<ZooKeeperElement> children;

	public ZooKeeperElement()
	{
		children = new ArrayList<ZooKeeperElement>();
	}

	public ZooKeeperElement(String path)
	{
		children = new ArrayList<ZooKeeperElement>();
		this.path = path;
	}

	/**
	 * @return The path.
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * @param path The path to set.
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * @return The children.
	 */
	public List<ZooKeeperElement> getChildren()
	{
		return children;
	}

	/**
	 * @param children The children to set.
	 */
	public void setChildren(List<ZooKeeperElement> children)
	{
		this.children = children;
	}

}
