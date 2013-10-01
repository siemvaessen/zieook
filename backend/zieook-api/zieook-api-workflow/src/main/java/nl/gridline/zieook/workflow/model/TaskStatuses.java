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
import javax.xml.bind.annotation.XmlRootElement;

import nl.gridline.zieook.api.JSonParent;

/**
 * [purpose]
 * <p />
 * Project zieook-api-workflow<br />
 * TaskStatuses.java created 23 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement(name = "task_statuses")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskStatuses implements Serializable, JSonParent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7756300284233074728L;
	private List<String> tasks;

	public TaskStatuses()
	{

	}

	/**
	 * @return The tasks.
	 */
	public List<String> getTasks()
	{
		return tasks;
	}

	/**
	 * @param tasks The tasks to set.
	 */
	public void setTasks(List<String> tasks)
	{
		this.tasks = tasks;
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
		result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
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
		TaskStatuses other = (TaskStatuses) obj;
		if (tasks == null)
		{
			if (other.tasks != null)
			{
				return false;
			}
		}
		else if (!tasks.equals(other.tasks))
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
