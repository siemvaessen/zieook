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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.gridline.zieook.api.JSonParent;

/**
 * The state of recommender - when is it going to execute, what the results of the previous execution where etc.
 * <p />
 * Project zieook-api-workflow<br />
 * RecommenderState.java created 7 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RecommenderState implements Serializable, JSonParent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3904224582229973823L;

	@XmlElement(name = "configuration")
	private RecommenderConfig task;

	@XmlElement
	private long next;
	@XmlElement
	private long previous;
	@XmlElement
	private String state;
	@XmlElement
	private String message;

	/**
	 * @return The next.
	 */
	public long getNext()
	{
		return next;
	}

	/**
	 * @param next The next to set.
	 */
	public void setNext(long next)
	{
		this.next = next;
	}

	/**
	 * @return The previous.
	 */
	public long getPrevious()
	{
		return previous;
	}

	/**
	 * @param previous The previous to set.
	 */
	public void setPrevious(long previous)
	{
		this.previous = previous;
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

	/**
	 * @return The message.
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @param message The message to set.
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * @return The task.
	 */
	public RecommenderConfig getTask()
	{
		return task;
	}

	/**
	 * @param task The task to set.
	 */
	public void setTask(RecommenderConfig task)
	{
		this.task = task;
	}

	@Override
	public String toJSON()
	{
		return ModelConstants.toJSON(this);
	}
}
