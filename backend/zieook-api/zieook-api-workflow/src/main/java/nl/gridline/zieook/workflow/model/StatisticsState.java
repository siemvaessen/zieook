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
import javax.xml.bind.annotation.XmlTransient;

import nl.gridline.zieook.api.JSonParent;

/**
 * [purpose]
 * <p />
 * Project zieook-api-workflow<br />
 * StatisticsState.java created 31 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "statistics-state")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatisticsState implements Serializable, JSonParent
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3464485692054477480L;

	@XmlTransient
	public enum State
	{
		SCHEDULED, RUNNING, CANCELLED
	};

	@XmlElement(name = "state")
	private String state;

	@XmlElement(name = "next")
	private Long next;

	public StatisticsState()
	{
		// no-arg constructor
	}

	public StatisticsState(String state)
	{
		this.state = state;
	}

	public StatisticsState(String state, long next)
	{
		super();
		this.state = state;
		this.next = next;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public long getNext()
	{
		return next;
	}

	public void setNext(long next)
	{
		this.next = next;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (next ^ (next >>> 32));
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		StatisticsState other = (StatisticsState) obj;
		if (next.equals(other.next))
		{
			return false;
		}
		if (state == null)
		{
			if (other.state != null)
			{
				return false;
			}
		}
		else if (!state.equals(other.state))
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

	@Override
	public String toString()
	{
		return new StringBuilder().append(state).append(" [").append(next).append(']').toString();
	}

}
