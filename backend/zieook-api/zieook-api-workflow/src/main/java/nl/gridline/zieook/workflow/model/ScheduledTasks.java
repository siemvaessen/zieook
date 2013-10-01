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

/**
 * [purpose]
 * <p />
 * Project zieook-api-workflow<br />
 * ScheduledTasks.java created 8 feb. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "tasks")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScheduledTasks implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3822355665036523235L;

	@XmlElement(name = "size")
	private long size;

	@XmlElement(name = "start_time")
	private Long startTime;

	@XmlElement(name = "end_time")
	private Long endTime;

	private List<TaskConfigMap> tasks;

	public ScheduledTasks()
	{
		// no-arg constructor
	}

	/**
	 * @param size
	 * @param startTime
	 * @param endTime
	 * @param tasks
	 */
	public ScheduledTasks(long size, Long startTime, Long endTime, List<TaskConfigMap> tasks)
	{
		super();
		this.size = size;
		this.startTime = startTime;
		this.endTime = endTime;
		this.tasks = tasks;
	}

	/**
	 * @return The size.
	 */
	public long getSize()
	{
		return size;
	}

	/**
	 * @param size The size to set.
	 */
	public void setSize(long size)
	{
		this.size = size;
	}

	/**
	 * @return The startTime.
	 */
	public Long getStartTime()
	{
		return startTime;
	}

	/**
	 * @param startTime The startTime to set.
	 */
	public void setStartTime(Long startTime)
	{
		this.startTime = startTime;
	}

	/**
	 * @return The endTime.
	 */
	public Long getEndTime()
	{
		return endTime;
	}

	/**
	 * @param endTime The endTime to set.
	 */
	public void setEndTime(Long endTime)
	{
		this.endTime = endTime;
	}

	/**
	 * @return The tasks.
	 */
	public List<TaskConfigMap> getTasks()
	{
		return tasks;
	}

	/**
	 * @param tasks The tasks to set.
	 */
	public void setTasks(List<TaskConfigMap> tasks)
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
		result = prime * result + (int) (endTime ^ (endTime >>> 32));
		result = prime * result + (int) (size ^ (size >>> 32));
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
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
		ScheduledTasks other = (ScheduledTasks) obj;
		if (endTime != other.endTime)
		{
			return false;
		}
		if (size != other.size)
		{
			return false;
		}
		if (startTime != other.startTime)
		{
			return false;
		}
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

}
