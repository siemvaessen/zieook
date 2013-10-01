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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.gridline.zieook.commons.ServerState;
import nl.gridline.zieook.mapreduce.TaskConfig;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * SchedulerState.java created 22 jun. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "scheduler_state")
@XmlAccessorType(XmlAccessType.FIELD)
public class SchedulerState extends ServerState
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3029827229033491808L;

	@XmlElement(name = "workerthread_alive")
	private boolean workerthread;

	@XmlElement(name = "scheduler_state")
	private boolean state;

	@XmlElement(name = "heartbeat_interval")
	private long heartbeatInterval;

	@XmlElement(name = "executor_threads")
	private int executorThreads;

	@XmlElement(name = "executor_executed")
	private long executed;

	@XmlElement(name = "executing_tasks")
	private List<TaskConfig> executing;

	@XmlElement(name = "cp_cache_hit")
	private long cacheCpHit;

	@XmlElement(name = "cp_cache_miss")
	private long cacheCpMiss;

	/**
	 * @return The executed.
	 */
	public long getExecuted()
	{
		return executed;
	}

	/**
	 * @param executed The executed to set.
	 */
	public void setExecuted(long executed)
	{
		this.executed = executed;
	}

	/**
	 * @return The executorThreads.
	 */
	public int getExecutorThreads()
	{
		return executorThreads;
	}

	/**
	 * @return The workerthread.
	 */
	public boolean isWorkerthread()
	{
		return workerthread;
	}

	/**
	 * @param workerthread The workerthread to set.
	 */
	public void setWorkerthread(boolean workerthread)
	{
		this.workerthread = workerthread;
	}

	/**
	 * @return The state.
	 */
	public boolean isState()
	{
		return state;
	}

	/**
	 * @param state The state to set.
	 */
	public void setState(boolean state)
	{
		this.state = state;
	}

	/**
	 * @return The heartbeatInterval.
	 */
	public long isHeartbeatInterval()
	{
		return heartbeatInterval;
	}

	/**
	 * @param heartbeatInterval The heartbeatInterval to set.
	 */
	public void setHeartbeatInterval(long heartbeatInterval)
	{
		this.heartbeatInterval = heartbeatInterval;
	}

	/**
	 * @return The executorThreads.
	 */
	public int isExecutorThreads()
	{
		return executorThreads;
	}

	/**
	 * @param executorThreads The executorThreads to set.
	 */
	public void setExecutorThreads(int executorThreads)
	{
		this.executorThreads = executorThreads;
	}

	/**
	 * @return The executing.
	 */
	public List<TaskConfig> getExecuting()
	{
		return executing;
	}

	/**
	 * @param executing The executing to set.
	 */
	public void setExecuting(List<TaskConfig> executing)
	{
		this.executing = executing;
	}

	/**
	 * @return The cacheCpHit.
	 */
	public long getCacheCpHit()
	{
		return cacheCpHit;
	}

	/**
	 * @param cacheCpHit The cacheCpHit to set.
	 */
	public void setCacheCpHit(long cacheCpHit)
	{
		this.cacheCpHit = cacheCpHit;
	}

	/**
	 * @return The cacheCpMiss.
	 */
	public long getCacheCpMiss()
	{
		return cacheCpMiss;
	}

	/**
	 * @param cacheCpMiss The cacheCpMiss to set.
	 */
	public void setCacheCpMiss(long cacheCpMiss)
	{
		this.cacheCpMiss = cacheCpMiss;
	}

	/**
	 * @return The heartbeatInterval.
	 */
	public long getHeartbeatInterval()
	{
		return heartbeatInterval;
	}

}
