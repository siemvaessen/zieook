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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * ZooKeeperState.java created 22 jun. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "zookeeper_state")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZooKeeperState
{
	@XmlElement(name = "state")
	private String state;

	@XmlElement(name = "tree")
	private ZooKeeperElement tree;

	/**
	 * @return The tree.
	 */
	public ZooKeeperElement getTree()
	{
		return tree;
	}

	/**
	 * @param tree The tree to set.
	 */
	public void setTree(ZooKeeperElement tree)
	{
		this.tree = tree;
	}

	/**
	 * @return The state.
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * @param state The state to set.
	 */
	public void setState(String state)
	{
		this.state = state;
	}

}
