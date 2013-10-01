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
package nl.gridline.zieook.workflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * RunningTasksCollection.java created 24 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class ZieOokTaskExecutor
{

	private final ThreadPoolExecutor executor;
	private final List<TaskRunner> delegate;

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
	private final int size;

	public ZieOokTaskExecutor(int size)
	{
		this.size = size;
		delegate = new ArrayList<TaskRunner>(size);
		executor = new ThreadPoolExecutor(size, size, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public boolean canExecuteNow()
	{
		return executor.getActiveCount() < size;
	}

	/**
	 * get the maximum size of tasks executing
	 * @return
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * return the approximate count of executing tasks
	 * @return the number of threads
	 * @see ThreadPoolExecutor#getActiveCount()
	 */
	public int getActiveCount()
	{
		return executor.getActiveCount();
	}

	/**
	 * shutdown the threadpool
	 */
	public void shutdown()
	{
		executor.shutdown();
	}

	/**
	 * execute a task
	 * @param runnable
	 */
	public void executeTask(ZieOokTask task)
	{
		writeLock.lock();
		try
		{
			TaskRunner runnable = new TaskRunner(task);
			delegate.add(runnable);
			executor.execute(runnable);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public boolean deleteTask(long id)
	{
		writeLock.lock();
		try
		{
			TaskRunner task = findTask(id);
			return delegate.remove(task);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public boolean removeTask(long id)
	{
		boolean result = false;
		writeLock.lock();
		try
		{
			Iterator<TaskRunner> i = delegate.iterator();
			while (i.hasNext())
			{
				final TaskRunner r = i.next();
				if (r.getId() == id)
				{
					i.remove();
					result = r.getConfig().isCancelled() || r.getConfig().isSucceeded() || r.getConfig().isFailed();
					break;
				}
			}
		}
		finally
		{
			writeLock.unlock();
		}
		return result;
	}

	public boolean cancelTask(long id)
	{
		readLock.lock();
		try
		{
			TaskRunner task = findTask(id);
			task.getConfig().setCancelled();

			return task.getConfig().isCancelled();
		}
		finally
		{
			readLock.unlock();
		}
	}

	public boolean killTask(long id) throws IOException
	{
		readLock.lock();
		try
		{
			TaskRunner task = findTask(id);

			// throw interrupt:
			task.stop();

			// this should cancel the task, soon...
			return task.getConfig().isCancelled();
		}
		finally
		{
			readLock.unlock();
		}
	}

	public List<TaskRunner> findTask(String cp)
	{
		Preconditions.checkNotNull(cp, "you need to provide a content provider");

		List<TaskRunner> result = new ArrayList<TaskRunner>(size);
		readLock.lock();
		try
		{
			for (TaskRunner r : delegate)
			{
				if (cp.equals(r.getConfig().get(TaskConfig.CP)))
				{
					result.add(r);
				}
			}
		}
		finally
		{
			readLock.unlock();
		}
		return result;
	}

	public TaskRunner findTask(long id)
	{
		readLock.lock();
		try
		{
			for (TaskRunner r : delegate)
			{
				if (r.getId() == id)
				{
					return r;
				}
			}
		}
		finally
		{
			readLock.unlock();
		}
		return null;
	}

	public List<TaskConfig> getTasks()
	{
		List<TaskConfig> result = new ArrayList<TaskConfig>(size);
		readLock.lock();
		try
		{
			for (TaskRunner r : delegate)
			{
				result.add(r.getConfig());
			}
		}
		finally
		{
			readLock.unlock();
		}
		return result;
	}

	/**
	 * Wraps a ZieOok task in a runnable and catches all exception, if thrown: set task to failed
	 */
	public static class TaskRunner implements Runnable
	{
		private static final Logger LOG = LoggerFactory.getLogger(ZieOokTaskExecutor.TaskRunner.class);

		private final ZieOokTask task;

		public TaskRunner(ZieOokTask task)
		{
			this.task = task;
		}

		@Override
		public void run()
		{
			LOG.debug("TASK START for <{},{}>", task.getConfig().get(TaskConfig.TASK_TYPE), task.getConfig().getId());
			try
			{
				task.call();
				LOG.debug("TASK SUCCEED for: <{},{}>", task.getConfig().get(TaskConfig.TASK_TYPE), task.getConfig().getId());
			}
			catch (Exception e)
			{
				String msg = task.getConfig().get(TaskConfig.TASK_MESSAGE);
				task.getConfig().setProperty(
						TaskConfig.TASK_MESSAGE,
						"task execution failed with error, task message: \"" + msg + "\" error message: \"" + e.getMessage()
								+ "\"");
				task.getConfig().setFailed();

				LOG.error("TASK FAILED for <" + task.getConfig().get(TaskConfig.TASK_TYPE) + "," + task.getConfig().getId()
						+ ">", e);
			}

		}

		public long getId()
		{
			return task.getConfig().getId();
		}

		public void stop() throws IOException
		{
			LOG.debug("Killing hadoop Job: {} in thread", task.getId(), Thread.currentThread().getName());
			task.getCurrentJob().killJob();

			LOG.debug("Interrupting task {} in thread", task.getId(), Thread.currentThread().getName());
			Thread.currentThread().interrupt();
		}

		public TaskConfig getConfig()
		{
			return task.getConfig();
		}

		public ZieOokTask getTask()
		{
			return task;
		}
	}

	/**
	 * @return
	 */
	public int getCorePoolSize()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return
	 */
	public long getCompletedTaskCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
