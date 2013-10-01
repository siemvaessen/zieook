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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import nl.gridline.zieook.commons.ZieOokCommons;
import nl.gridline.zieook.commons.ZieOokManager;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.CollectionMetaTable;
import nl.gridline.zieook.data.hbase.dao.ContentProviderTable;
import nl.gridline.zieook.data.hbase.dao.TaskTable;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseRecommendationTable;
import nl.gridline.zieook.data.hbase.model.HBaseStatisticsTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.exceptions.DistrQueueException;
import nl.gridline.zieook.exceptions.PropertyConfigurationException;
import nl.gridline.zieook.exceptions.RecommenderModuleUnknownException;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.tasks.DataFileImportTask;
import nl.gridline.zieook.tasks.ItemBasedRecommenderTask;
import nl.gridline.zieook.tasks.OAIImportTask;
import nl.gridline.zieook.tasks.StatisticsTask;
import nl.gridline.zieook.tasks.TaalServerTask;
import nl.gridline.zieook.tasks.UserBasedRecommenderTask;
import nl.gridline.zieook.tasks.ZieOokTask;
import nl.gridline.zieook.tasks.ZieOokTask.Level;
import nl.gridline.zieook.workflow.model.Collection;
import nl.gridline.zieook.workflow.model.CollectionFilter;
import nl.gridline.zieook.workflow.model.Module;
import nl.gridline.zieook.workflow.model.Property;
import nl.gridline.zieook.workflow.model.RecommenderConfig;
import nl.gridline.zieook.workflow.model.SchedulerState;
import nl.gridline.zieook.workflow.model.StatisticsState;
import nl.gridline.zieook.workflow.model.TaskConfigMap;
import nl.gridline.zieook.workflow.model.ZooKeeperState;
import nl.gridline.zieook.workflow.scheduler.ZieOokScheduler;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.mahout.math.hadoop.similarity.SimilarityType;
import org.apache.zookeeper.KeeperException;
import org.jboss.resteasy.spi.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZieOok has it's own scheduler to schedule map reduce tasks (recommender tasks, import tasks & export tasks)
 * <p />
 * Project zieook-backend-workflow<br />
 * WorkflowScheduler.java created 15 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class WorkflowScheduler implements ZieOokManager, Runnable
{

	private static final int DEFAULT_THREAD_COUNT = 6;

	public static final String ZIEOOK_SCHEDULER = "nl.zieook.workflow.scheduler";

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowScheduler.class);

	// scheduler state after init
	private boolean state;

	// configuration object from zieook
	private Config config;

	// heartbeat interval in ms
	private long heartbeat;

	// distributed scheduler & unique id provider backed by zookeeper
	private ZieOokScheduler scheduler;

	// table that contains all task configurations:
	private TaskTable taskTable;

	// content provider table, to check if a cp exists
	private ContentProviderTable cpTable;

	private CollectionMetaTable collectionTable;

	// worker thread, that allows scheduling:
	private Thread worker;

	// stop flag, in case the system if going to shutdown:
	private boolean stop;

	private ZieOokTaskExecutor executor;

	// how many times the distributed id should be retried
	private int retries;

	@Override
	public void startup() throws DistrQueueException, KeeperException, InterruptedException, MasterNotRunningException,
			ZooKeeperConnectionException
	{
		LOG.info(ZieOokCommons.someinformation("Workflowscheduler"));

		config = Config.getInstance(Config.ZIEOOK_WORKFLOW_FILE);

		String zookeeper = config.getZooKeeperHost();
		state = zookeeper != null;

		if (state)
		{
			int timeout = config.getInt(Config.ZIEOOK_SCHEDULER_SESSIONTIMEOUT, 8000); // set default timeout to 8 seconds
			heartbeat = config.getLong(Config.ZIEOOK_HEARTBEAT, 2) * 1000;
			retries = config.getInt(Config.ZIEOOKID_RETRIES, 5);
			int threadCnt = config.getInt(Config.ZIEOOK_SCHEDULER_TASKTHREADS, DEFAULT_THREAD_COUNT); // default threads to
																																	// 6

			HBaseConfigurationTable table = new HBaseConfigurationTable(HBaseManager.getInstance(zookeeper));
			if (!table.tableExists())
			{
				table.create();
			}

			executor = new ZieOokTaskExecutor(threadCnt);

			// table contains task configuration:
			taskTable = new TaskTable(table);
			// contains content provider meta data:
			cpTable = new ContentProviderTable(table);

			// scheduler knows when tasks need to be executed:
			scheduler = new ZieOokScheduler(zookeeper, retries, timeout);

			// executor executes tasks:

			collectionTable = new CollectionMetaTable(table);

			// start he scheduler:
			stop = false;
			worker = new Thread(this, "worker");
			worker.start();

		}
		else
		{
			LOG.error("failed to startup the Workflow scheduler, zookeeper not available");
		}

	}

	@Override
	public void shutdown() throws Exception
	{
		LOG.info("Shutting down workflow scheduler");
		// shutdown the scheduler: (which will cause exceptions)

		if (scheduler != null)
		{
			scheduler.shutdown();
		}

		// shutdown the executor:
		if (executor != null)
		{
			executor.shutdown();
		}

		// shutdown the worker thread:
		LOG.info("stopping heartbeat monitor");
		stop = true;

		// wait: at least three heart beats
		Thread.sleep(heartbeat * 3);

		if (worker != null)
		{
			if (worker.isAlive())
			{
				LOG.info("worker thread is still alive, try to kill it");
				// interrupt the thread:
				worker.interrupt();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.commons.ZieOokManager#state()
	 */
	@Override
	public boolean state()
	{
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.commons.ZieOokManager#getState()
	 */
	@Override
	public SchedulerState getState()
	{
		SchedulerState result = new SchedulerState();

		result.setHeartbeatInterval(heartbeat);
		result.setWorkerthread(worker.isAlive());
		result.setState(state);
		result.setExecutorThreads(executor.getCorePoolSize());
		result.setExecuted(executor.getCompletedTaskCount());
		result.setCacheCpHit(0);
		result.setCacheCpMiss(0);
		result.setExecuting(executor.getTasks());
		return result;
	}

	public ZooKeeperState getZooKeeperState()
	{
		return scheduler.getState();
	}

	/**
	 * retrieve the configuration of a task, augmented with information about it's current state.
	 * @param id
	 * @return
	 */
	public TaskConfig getTask(long id)
	{
		// read from hbase:
		TaskConfig result = taskTable.readTask(id);

		// synchronize running tasks with ZooKeeper data:
		if (result != null && result.isRunning())
		{
			Long progress = scheduler.getActiveTasks().get(id);
			if (progress != null)
			{
				result.setProperty(TaskConfig.TASK_PROGRESS, progress.longValue());
			}
			String message = scheduler.getMessage().get(id);
			if (message != null)
			{
				result.setProperty(TaskConfig.TASK_MESSAGE, message);
			}

		}
		return result;
	}

	/**
	 * Return the set of currently executing task id's
	 * @return
	 */
	public Set<Long> getActiveTasks()
	{
		return scheduler.getActiveTasks().keySet();
	}

	public List<Long> getTasks(String cp, String type, String value, long start, long end)
	{
		return taskTable.searchTasks(cp, type, value, start, end);
	}

	public List<Long> getTasks(String classname)
	{
		return taskTable.searchTaskByClassName(classname);
	}

	public TaskConfig getCompletedTask(String cp, String type, String value)
	{
		return taskTable.searchCompletedTask(cp, type, value);
	}

	public List<Long> getScheduledTasks(long start, long end, int size)
	{
		try
		{
			return scheduler.getScheduled(start, end, size);
		}
		catch (InterruptedException e)
		{
			LOG.error("failed to get scheduled tasks", e);
		}
		catch (KeeperException e)
		{
			LOG.error("failed to get scheduled tasks", e);
		}
		return null;
	}

	/**
	 * Create statistics task for the given content provider
	 * @param cp - content provider
	 * @return a new {@code TaskConfig} configuration.
	 * @throws IOException
	 */
	public TaskConfig createStatistics(String cp) throws IOException
	{
		String outputTable = new HBaseStatisticsTable().getTableName(cp);
		String eventlogTable = new HBaseEventLogTable().getTableName(cp);
		String userTable = new HBaseUserTable().getTableName(cp);
		String collection = null;

		List<Collection> collections = collectionTable.getCollections(cp);
		if (collections.size() > 0)
		{
			collection = collections.get(0).getName();
		}
		else
		{
			throw new IOException("cp has no collections? - data corruption! or nothing to do yet");
		}

		TaskConfig task = new TaskConfig();
		Config config = Config.getInstance();

		task.setProperty(TaskConfig.CLASSNAME, StatisticsTask.class.getCanonicalName());
		task.setProperty(TaskConfig.TASK_TYPE, TaskConfig.TASK_TYPE_STATISTICS);

		task.setProperty(TaskConfig.OUTPUT_TABLE, outputTable);
		task.setProperty(TaskConfig.STATS_EVENTLOG_TABLE, eventlogTable);
		task.setProperty(TaskConfig.STATS_USER_TABLE, userTable);

		// round statistics end to current midnight:
		task.setProperty(TaskConfig.STATS_END, getMidnight());

		task.setProperty(TaskConfig.CP, cp);
		task.setProperty(TaskConfig.COLLECTION, collection);

		// one day interval (by default)
		long interval = config.getLong(Config.STATISTICS_INTERVAL, 24 * 60 * 60);
		task.setInterval(interval, TimeUnit.SECONDS);

		// run every midnight:
		task.setNext(getNextMidnight(), TimeUnit.MILLISECONDS);

		LOG.debug("created statistics task for <{}> : {}", cp, task);
		return task;
	}

	/**
	 * Cancel the statistics running and scheduled task for the given content provider
	 * @param cp content provider name
	 */
	public void cancelStatisticsTask(String cp)
	{
		cancelTask(cp, TaskConfig.TASK_TYPE_STATISTICS, null);
	}

	/**
	 * Delete complete history of all ever created statistics tasks for this content provider
	 * @param cp content provider
	 */
	public void deleteStatisticsTasks(String cp)
	{
		taskTable.deleteStatisticsTasks(cp);
	}

	public TaskConfig createCollectionTask(String cp, Collection collection, long date)
			throws MasterNotRunningException, ZooKeeperConnectionException
	{
		return createCollectionTask(cp, collection, null, null, date);
	}

	public TaskConfig createCollectionTask(String cp, Collection collection, String part, String path, long date)
			throws MasterNotRunningException, ZooKeeperConnectionException
	{

		// this is to be handled on a certain interval...
		TaskConfig task = new TaskConfig();
		task.setProperty(TaskConfig.TASK_TYPE, TaskConfig.TASK_TYPE_COLLECTION);
		task.setProperty(TaskConfig.CP, cp); // the content provider
		task.setProperty(TaskConfig.COLLECTION, collection.getName()); // the collection name
		task.setProperty(TaskConfig.DATA_TYPE, collection.getType());
		task.setNext(date, TimeUnit.SECONDS);

		// set interval:
		if (collection.getInterval() > 0)
		{
			LOG.debug("task interval set to: {} seconds ", collection.getInterval());
			task.setInterval(collection.getInterval(), TimeUnit.SECONDS);
		}

		if (TaskConfig.DATA_TYPE_MOVIELENS.equals(collection.getType()))
		{
			task.setProperty(TaskConfig.CLASSNAME, DataFileImportTask.class.getCanonicalName());
			task.setProperty(TaskConfig.DATA_PART, part);
			task.setProperty(TaskConfig.INPUT_PATH, path); // the source path
		}
		else if (TaskConfig.DATA_TYPE_EDIT.equals(collection.getType())
				|| TaskConfig.DATA_TYPE_OAI.equals(collection.getType()))
		{
			// OAI data comes in OAI-PMH the task will figure out the metadata format. ('oai_dc' and 'czp' are supported)
			task.setProperty(TaskConfig.INPUT_PATH, collection.getLocation()); // the collection url
			task.setProperty(TaskConfig.CLASSNAME, OAIImportTask.class.getCanonicalName());

			if (collection.getOaiSets() != null)
			{
				task.setProperty(TaskConfig.OAI_SETS, collection.getOaiSets().getImportSetsAsString());
			}
			if (collection.getOaiMetadata() != null)
			{
				task.setProperty(TaskConfig.OAI_METADATA, collection.getOaiMetadata().getUse());
			}
		}
		// TODO extend this to allow direct setting of a classname, we just have to check if the class want's to load.
		// the client can set a classname directly. If all fails, throw a BadRequestException...
		else
		{
			throw new BadRequestException("task not valid: " + task.toString());
		}
		LOG.debug("created collection task for <{}>: {}", cp + "/" + collection, task);
		return task;
	}

	/**
	 * Cancel running an scheduled collection tasks
	 * @param cp
	 * @param collection
	 */
	public void cancelCollectionTask(String cp, String collection)
	{
		// cancel all tasks related to this collection:
		cancelTask(cp, TaskConfig.TASK_TYPE_COLLECTION, collection);
	}

	/**
	 * delete task history for all executed tasks
	 * @param cp
	 * @param collection
	 */
	public void deleteCollectionTasks(String cp, String collection)
	{
		taskTable.deleteCollectionTasks(cp, collection);
	}

	/**
	 * Create a task from a recommender config, and schedule it.
	 * @param recommenderConfig
	 * @throws ZooKeeperConnectionException
	 * @throws MasterNotRunningException
	 */
	public TaskConfig createRecommenderTask(RecommenderConfig recommenderConfig, long next)
			throws MasterNotRunningException, ZooKeeperConnectionException
	{
		TaskConfig task = new TaskConfig();

		// fixed data:
		final String cp = recommenderConfig.getCp();
		final String recommender = recommenderConfig.getRecommender();
		final String collection = recommenderConfig.getCollection();
		boolean taalserver = cpTable.getContentProvider(cp).isTaalserver();

		task.setProperty(TaskConfig.TASK_TYPE, TaskConfig.TASK_TYPE_RECOMMENDER);
		task.setProperty(TaskConfig.COLLECTION, collection);
		task.setProperty(TaskConfig.RECOMMENDER, recommender);
		task.setProperty(TaskConfig.CP, cp);
		task.setInterval(recommenderConfig.getInterval(), TimeUnit.SECONDS); // ms -> s
		if (next <= 0)
		{
			task.setNext(System.currentTimeMillis() + (heartbeat * 2), TimeUnit.MILLISECONDS);
		}
		else
		{
			task.setNext((next * 1000) + (heartbeat * 2), TimeUnit.MILLISECONDS);
		}

		// in fact, the current implementation only accepts a single module:
		// we will simply try to get the first element in the list:
		List<Module> modules = recommenderConfig.getTemplate().getModules();
		if (modules.size() == 0)
		{
			throw new RecommenderModuleUnknownException();
		}

		Module module = modules.get(0);
		task.setProperty(TaskConfig.RECOMMENDER_TYPE, module.getName());
		task.setProperty(TaskConfig.RECOMMENDER_TAALSERVER, taalserver);
		task.setProperty(TaskConfig.CLASSNAME, getRecommenderClass(module.getName()));

		// properties are, slightly more complex, but most recommenders have the
		// same set of properties:
		List<Property> properties = module.getProperties();
		for (Property property : properties)
		{
			// check against the allow keys & values.

			// check if the similarity class is a known class:
			if (TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME.equals(property.getKey()))
			{
				// check value... it needs to be a known class:
				if (SimilarityType.listEnumNames().indexOf(property.getValue()) == -1)
				{
					throw new PropertyConfigurationException(property.getKey(), property.getValue());
				}
			}
			task.setProperty(property.getKey(), property.getValue());
		}

		// automatically add for text based recommenders
		if (TaskConfig.RECOMMENDER_TYPE_TEXTBASED.equals(module.getName()))
		{
			handleTextBasedRecommender(task);
		}

		// handle collection filtering:
		CollectionFilter filter = recommenderConfig.getFilter();
		if (filter != null)
		{
			task.setPropertyIgnoreNull(TaskConfig.FILTER_KEY, filter.getKey());
			task.setPropertyIgnoreNull(TaskConfig.FILTER_VALUE, filter.getValuesString());
			task.setPropertyIgnoreNull(TaskConfig.FILTER_STARTDATE, filter.getStartDate() != null ? filter.getStartDate()
					.toString() : null);
			task.setPropertyIgnoreNull(TaskConfig.FILTER_ENDDATE, filter.getEndDate() != null ? filter.getEndDate()
					.toString() : null);
		}

		// set input and output:

		// obviously every task implementation may override these settings, or ignore them

		// the input depends on the type of recommender:
		String input = getRecommenderInputTable(cp, module.getName());
		task.setProperty(TaskConfig.INPUT_TABLE, input);

		// The output is fairly simple:
		HBaseRecommendationTable table = new HBaseRecommendationTable(HBaseManager.getInstance(config.getZooKeeperHost()));
		String output = table.getTableName(cp);
		task.setProperty(TaskConfig.OUTPUT_TABLE, output);
		LOG.debug("created recommenderconfig: {}", task);
		return task;
	}

	/**
	 * cancel all outstanding and running recommender tasks
	 * @param cp
	 * @param recommender
	 */
	public void cancelRecommenderTask(String cp, String recommender)
	{
		cancelTask(cp, TaskConfig.TASK_TYPE_RECOMMENDER, recommender);
	}

	/**
	 * delete recommender task history
	 * @param cp
	 * @param recommender
	 */
	public void deleteRecommenderTasks(String cp, String recommender)
	{
		taskTable.deleteRecommenderTasks(cp, recommender);
	}

	/**
	 * cancel all tasks related to the given content provider
	 * @param cp content provider
	 */
	public void cancelAllTask(String cp)
	{
		cancelTask(cp, TaskConfig.TASK_TYPE_COLLECTION, null);
		cancelTask(cp, TaskConfig.TASK_TYPE_RECOMMENDER, null);
		cancelTask(cp, TaskConfig.TASK_TYPE_STATISTICS, null);
	}

	/**
	 * delete all tasks related to the given content provider
	 * @param cp content provider
	 */
	public void deleteAllTasks(String cp)
	{
		taskTable.deleteCollectionTasks(cp, null);
		taskTable.deleteRecommenderTasks(cp, null);
		taskTable.deleteStatisticsTasks(cp);
	}

	/**
	 * Cancel task by type for the given content provider
	 * @param cp - content provider
	 * @param type - task type (TaskConfig.TASK_TYPE...)
	 * @param value
	 */
	private void cancelTask(String cp, String type, String value)
	{

		LOG.info("Trying to cancel future {} tasks for {}", (type + ":" + value), cp);
		// value: collection or recommender
		List<Long> list = taskTable.searchFutureTasks(cp, type, value, System.currentTimeMillis(), -1);
		for (Long id : list)
		{
			TaskConfig task = taskTable.readTask(id);
			// to check if we got a valid result:
			if (task.getId() == id.longValue() && !task.isExecuted())
			{
				LOG.info("found task <{}:{}> for {}", (type + ":" + value), cp);
				// cancel the task's future execution
				task.setCancelled();
				// write the changes:
				taskTable.updateTask(task);
				// to be sure, also set a watch for this task's cancellation:
				if (task.isRunning())
				{
					ZieOokTask.cancel(scheduler.getZooKeeper(), id);
					LOG.info("found a task {} for {}, cancel request sent", (type + ":" + value), cp);
				}
			}
			else
			{
				LOG.error("row id and task id don't match - this is a bug");
			}
		}

		// 2: get all running tasks, and set a cancellation watch:
		// this is only an extra, if there is a task in the process of being scheduled we might miss it, it set's a
		// cancellation watch - so it's created, it will immediately be canceled (see ZieOokTask code)

		Set<Long> running = scheduler.getActiveTasks().keySet();
		LOG.info("Try to cancel active tasks {} for {} - active: " + running.size(), (type + ":" + value), cp);
		for (Long id : running)
		{
			TaskConfig task = taskTable.readTask(id);
			String taskType = task.get(TaskConfig.TASK_TYPE);
			LOG.info("Running: <cp,recommender,id> = <{},{}>", task.get(TaskConfig.CP) + "," + taskType, id);

			if (cp.equals(task.get(TaskConfig.CP)) && type.equals(taskType))
			{
				String recommender = task.get(TaskConfig.RECOMMENDER);
				String collection = task.get(TaskConfig.COLLECTION);

				if ((TaskConfig.TASK_TYPE_RECOMMENDER.equals(taskType) && (value == null || value.equals(recommender))))
				{
					LOG.info("cancelling recommender task: <cp,recommender,id> = <{},{}>", cp + "," + recommender, id);
					task.setCancelled();
					ZieOokTask.cancel(scheduler.getZooKeeper(), id);
				}
				if (value != null && TaskConfig.TASK_TYPE_COLLECTION.equals(taskType) && value.equals(collection))
				{
					LOG.info("cancelling collection task: <cp,collection,id> = <{},{}>", cp + "," + collection, id);
					task.setCancelled();
					ZieOokTask.cancel(scheduler.getZooKeeper(), id);
				}
				if (TaskConfig.TASK_TYPE_STATISTICS.equals(taskType))
				{
					LOG.info("cancelling statistics task: <cp,id> = <{},{}>", cp, id);
					task.setCancelled();
					ZieOokTask.cancel(scheduler.getZooKeeper(), id);
				}

				taskTable.updateTask(task);
			}
		}

		// 3: kill active tasks, that shoul work if they are running on this instance - and will kill tasks quicker
		List<TaskConfig> runningTasks = executor.getTasks();
		for (TaskConfig task : runningTasks)
		{
			String taskType = task.get(TaskConfig.TASK_TYPE);
			long id = task.getId();

			if (cp.equals(task.get(TaskConfig.CP)) && type.equals(taskType))
			{
				String recommender = task.get(TaskConfig.RECOMMENDER);
				String collection = task.get(TaskConfig.COLLECTION);
				if ((TaskConfig.TASK_TYPE_RECOMMENDER.equals(taskType) && (value == null || value.equals(recommender))))
				{
					LOG.info("killing recommender task: <cp,recommender,id> = <{},{}>", cp + "," + recommender, id);
					task.setCancelled();
					try
					{
						executor.killTask(id);
					}
					catch (IOException e)
					{
						LOG.error("Killing the task threw an error ", e);
					}
				}
				if (value != null && TaskConfig.TASK_TYPE_COLLECTION.equals(taskType) && value.equals(collection))
				{
					LOG.info("killing collection task: <cp,collection,id> = <{},{}>", cp + "," + collection, id);
					task.setCancelled();
					try
					{
						executor.killTask(id);
					}
					catch (IOException e)
					{
						LOG.error("Killing the task threw an error ", e);
					}
				}
				if (TaskConfig.TASK_TYPE_STATISTICS.equals(taskType))
				{
					LOG.info("killing statistics task: <cp,id> = <{},{}>", cp, id);
					task.setCancelled();
					try
					{
						executor.killTask(id);
					}
					catch (IOException e)
					{
						LOG.error("Killing the task threw an error ", e);
					}
				}

			}
		}

	}

	/**
	 * Schedule a future task, see {@link TaskConfig} for configuration details
	 * @param task
	 * @return the task configuration updated with state (possibly failed) and id and written to HBase
	 */
	public TaskConfig schedule(final TaskConfig task) throws IOException
	{
		// schedule a task
		// 1: put the task in hbase
		// 2: write the task to scheduler
		// 3: report what's done

		long next = task.getNext(-1) * 1000;
		// possible schedule: task should be in the near future
		// otherwise it was probably a one time task, or a failed task
		if (next > (System.currentTimeMillis() - 5000))
		{

			long id = taskTable.getTaskId();

			LOG.info("Assigned id {} to task {}", id, task);

			// got an id, set it:
			task.setId(id);

			// store in hbase:
			taskTable.createTask(task);

			// next hard step, schedule it:
			try
			{
				scheduler.offer(next, task.getId());
			}
			catch (KeeperException e)
			{
				// failed to offer, this needs a little better handling:
				LOG.error("failed to offer task to queue", e);
				task.setFailed();
			}
			catch (InterruptedException e)
			{
				LOG.error("interupted", e);
				task.setFailed();
				Thread.currentThread().interrupt(); // stop
			}

			LOG.info("A new task ({}) has been scheduled to be executed at {}",
					task.get(TaskConfig.CP) + "." + task.get(TaskConfig.TASK_TYPE), new Date(next).toString());

		}
		else
		{
			LOG.error("Failed to retrieve id for task {}.{}", task.get(TaskConfig.CP), task.get(TaskConfig.RECOMMENDER));
			task.setFailed();
		}

		return task;
	}

	/**
	 * returns <tt>true</tt> if the statistics for this content provider are already running
	 * @param cp a content provider
	 * @return
	 */
	public boolean isStatisticsRunning(String cp)
	{
		// check if there is a running task that is statistics for this

		Set<Long> tasks = getActiveTasks();
		for (Long taskid : tasks)
		{
			TaskConfig task = getTask(taskid);
			if (task != null && task.contains(TaskConfig.STATISTICS_KEY) && cp.equals(task.get(TaskConfig.CP))
					&& task.isRunning())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * returns <tt>true</tt> if the statistics for this content provider is already scheduled
	 * @param cp a content provider
	 * @return
	 */
	public boolean isStatisticsScheduled(String cp)
	{
		// getStatisticsState(cp) getStatisticsTasks(cp,now, -1);
		List<Long> result = taskTable.searchTasks(cp, TaskConfig.TASK_TYPE_STATISTICS, null, System.currentTimeMillis(),
				-1);

		return result.size() > 0;
	}

	/**
	 * Run statistics now
	 * @param cp
	 * @throws IOException
	 * @throws MasterNotRunningException
	 * @throws ZooKeeperConnectionException
	 */
	public void scheduleStatistics(String cp, boolean now) throws IOException
	{
		LOG.debug("scheduling statistics for <{}> cp now={}", now);
		// create a statistics task and execute it now!
		TaskConfig task = createStatistics(cp);

		if (now) // override!
		{
			final long next = System.currentTimeMillis() + (heartbeat * 2);
			task.setNext(next, TimeUnit.MILLISECONDS);
			task.setInterval(-1, TimeUnit.SECONDS);
		}
		schedule(task);

	}

	public StatisticsState getStatisticsState(String cp)
	{
		TaskConfig task = taskTable.searchLatestTask(cp, TaskConfig.TASK_TYPE_STATISTICS, null);
		if (task.isRunning())
		{
			return new StatisticsState(StatisticsState.State.RUNNING.toString());
		}
		else if (task.needSchedule())
		{
			return new StatisticsState(StatisticsState.State.SCHEDULED.toString(), task.getNext(-1));
		}
		else if (task.isCancelled())
		{
			return new StatisticsState(StatisticsState.State.CANCELLED.toString());
		}

		return null;
	}

	public List<TaskConfigMap> futureTasks(String cp, int size)
	{
		List<TaskConfigMap> result = new ArrayList<TaskConfigMap>();

		List<Long> ids = taskTable.searchFutureTasks(cp, null, null, System.currentTimeMillis(), size);
		for (long id : ids)
		{
			TaskConfig task = taskTable.readTask(id);
			if (task != null)
			{
				result.add(new TaskConfigMap(task.toMap()));
			}
		}
		return result;
	}

	@Override
	public void run()
	{
		LOG.info("Starting ZieOok scheduler worker thread");
		try
		{
			while (!stop || !Thread.interrupted())
			{

				LOG.debug("HeartBeat - (every {}ms)", heartbeat);
				// execute pending tasks, if the executor has space:

				if (executor.canExecuteNow())
				{
					execute();
				}
				else
				{
					LOG.debug("no space on executor ({}/{}) waiting", executor.getActiveCount(), executor.getSize());
				}

				// clean-up & report executed tasks:
				cleanup();

				Thread.sleep(heartbeat);
			}
		}
		catch (InterruptedException e)
		{
			LOG.info("Stopping worker thread, interrupted");
			Thread.currentThread().interrupt();
		}
		catch (Exception e)
		{
			LOG.error("an error occurred - that might be fatal", e);
		}

		LOG.info("WORKER THREAD STOP - System scheduler halt - if you're not running a shutdown, the system has failed and should be restarted");
	}

	private void execute() throws InterruptedException
	{
		// poll for new tasks:
		long id = scheduler.poll(heartbeat / 3);
		if (id == -1)
		{
			return;
		}

		LOG.info("Executing task with id {}", id);

		// read the task from the hbase:
		TaskConfig task = taskTable.readTask(id);

		if (task == null)
		{
			LOG.error("Non existing task with id {}, aborting.", id);
			return;
		}

		// test is it still needs to be executed, or is canceled:
		if (task.isCancelled())
		{
			// task is canceled by the user
			LOG.info("A previously scheduled task was cancelled and will not be executed");
			return;
		}

		String classname = task.get(TaskConfig.CLASSNAME);
		if (classname != null)
		{
			// show what's going on:
			LOG.info("submitting task to executor: <classname,id> = <{},{}>", task.get(TaskConfig.CLASSNAME),
					task.get(TaskConfig.TASK_ID));

			// execute task by id... the id should map to a row key in the database. issuing
			ZieOokTask runnable = create(task.get(TaskConfig.CLASSNAME));
			if (runnable != null)
			{
				// set data:
				try
				{
					runnable.setZooKeeper(scheduler.getZooKeeper()).setConfig(task);

					// Set an init message, and first progress, otherwise zookeeper does not know the task is executing.
					runnable.setProgress();
					runnable.setMessage(Level.INFO, "task inited");
				}
				catch (KeeperException e)
				{
					LOG.error("failed to schedule task", e);
					task.setFailed();
					task.setNext(-1, TimeUnit.SECONDS);
				}
				// runnable.setConfig(task); // allows to schedule for next task

				task.setRunning();
				executor.executeTask(runnable);
			}
			else
			{
				LOG.error("unable to create a task for <{}> failed", task.get(TaskConfig.CLASSNAME));
				task.setFailed();
				task.setNext(-1, TimeUnit.SECONDS);
			}
			taskTable.updateTask(task);
		}
		else
		{
			LOG.error("Task id <{}> is not a valid taskconfiguration:\n{}", id, task.toString());
		}
	}
	// final lists for the clean-up method (so we don't have to recreate them every time)
	private final List<TaskConfig> schedule = new ArrayList<TaskConfig>();
	private final List<Long> remove = new ArrayList<Long>();

	/**
	 * <ol>
	 * <li>Checks the {@code executor} list for completed tasks.
	 * <li>Writes the result to the database
	 * <li>Removes them from the {@code futures} list
	 * <li>Schedules tasks that have a {@code TaskConfig.NEXT != TaskConfig.SINGLE_RUN}
	 * </ol>
	 * @throws InterruptedException
	 */
	private void cleanup() throws IOException, InterruptedException
	{
		// schedule.clear();
		// remove.clear();

		// cleanup tasks from local executor:
		List<TaskConfig> tasks = executor.getTasks();

		for (TaskConfig t : tasks)
		{
			LOG.debug(
					"task <type,id> = <{},{}> state: [running,cancelled,failed] [" + t.isRunning() + "," + t.isCancelled()
							+ "," + t.isFailed() + "]", t.get(TaskConfig.TASK_TYPE), t.get(TaskConfig.TASK_ID));

			if (t.isSucceeded() || t.isFailed() || t.isCancelled())
			{

				// reschedule: even if failed - it might work the next time...
				LOG.debug("task <type,id> = <{},{}> is done", t.get(TaskConfig.TASK_TYPE), t.get(TaskConfig.TASK_ID));
				t.stopRunning();

				// check if rescheduling is needed:
				if (t.isSucceeded() && t.needSchedule())
				{
					LOG.debug("task <type,id> = <{}> needs reschedule @{}",
							t.get(TaskConfig.TASK_TYPE) + "," + t.get(TaskConfig.TASK_ID), new Date(
									t.getNext(Long.MIN_VALUE) * 1000));
					// schedule the task for the next run:
					schedule.add(t);
				}
				else
				{
					LOG.debug("task <type,id> = <{},{}> not rescheduled", t.get(TaskConfig.TASK_TYPE),
							t.get(TaskConfig.TASK_ID));
				}

				// write to task table:
				taskTable.updateTask(t);

				// schedule remove from executor:
				remove.add(t.getId());
			}
			else
			{
				LOG.debug("task <type,id> = <{},{}> is still running", t.get(TaskConfig.TASK_TYPE), t.getId());
			}
		}

		// remove finished tasks:
		for (Long id : remove)
		{
			// executor cleanup:
			executor.removeTask(id);

			// zookeeper cleanup:
			ZieOokTask.taskDone(scheduler.getZooKeeper(), id);
		}

		// re-schedule some tasks: - if there are not disabled yet:
		for (TaskConfig config : schedule)
		{
			// read updates from HBase and merge into a new task:
			TaskConfig newconfig = TaskConfig.merge(config, taskTable.readTask(config.getId()));
			if (newconfig.needSchedule())
			{
				config.cleanup(); // remove all running task info, and re-schedule the configuration
				schedule(config);
			}
		}

		schedule.clear();
		remove.clear();

	}

	private ZieOokTask create(String name)
	{
		ZieOokTask result = null;

		if (name == null || name.trim().isEmpty())
		{
			LOG.error("failed to create task for <> - no classname given");
			return result;
		}

		try
		{
			Class<?> clazz = Class.forName(name.trim());
			Object instance = clazz.newInstance();
			result = (ZieOokTask) instance;
		}
		catch (ClassNotFoundException e)
		{
			LOG.error("failed to create task for: <{}> - classname not found", name);
			LOG.debug("failed to create task", e);
		}
		catch (InstantiationException e)
		{
			LOG.error("failed to create task for: <{}> - could not instantiate", name);
			LOG.debug("failed to create task", e);
		}
		catch (IllegalAccessException e)
		{
			LOG.error("failed to create task for: <{}> - illegal access", name);
			LOG.debug("failed to create task", e);
		}
		catch (ClassCastException e)
		{
			LOG.error("failed to create task for: <{}> - class is not an instance of RecommendationTask", name);
			LOG.debug("failed to create task", e);
		}

		LOG.info("Created task for <{}>", name);
		return result;
	}

	/**
	 * check if the content provider exists (in the meta table) <br />
	 * the method has a naive caching mechanism utilizing a weakhashmap
	 * @param name
	 *           - the content provider name
	 * @return <tt>true</tt> if it exists, <tt>false</tt> otherwise
	 */
	public boolean isContentProvider(String name)
	{
		return cpTable.isContentProvider(name);
	}

	private void handleTextBasedRecommender(TaskConfig task)
	{

		if (task.get(TaskConfig.TEXT_ANALYSIS_USE_DESCRIPTION) == null)
		{
			String def = config.get(Config.TEXT_ANALYSIS_USE_DESCRIPTION_DEFAULT);
			if (def != null)
			{
				LOG.warn("no setting for <{}> overriding with default from <{}>", TaskConfig.TEXT_ANALYSIS_USE_DESCRIPTION,
						Config.TEXT_ANALYSIS_USE_DESCRIPTION_DEFAULT);
			}
			else
			{
				LOG.warn("no setting for <{}> overriding and non set in configuration <{}> using system default <true>",
						TaskConfig.TEXT_ANALYSIS_USE_DESCRIPTION, Config.TEXT_ANALYSIS_USE_DESCRIPTION_DEFAULT);
				def = "true";
			}

			task.setProperty(TaskConfig.TEXT_ANALYSIS_USE_DESCRIPTION, def);
		}
		if (task.get(TaskConfig.TEXT_ANALYSIS_USE_TITLE) == null)
		{
			String def = config.get(Config.TEXT_ANALYSIS_USE_TITLE_DEFAULT);
			if (def != null)
			{
				LOG.warn("no setting for <{}> overriding with default from <{}>", TaskConfig.TEXT_ANALYSIS_USE_TITLE,
						Config.TEXT_ANALYSIS_USE_TITLE_DEFAULT);
			}
			else
			{
				LOG.warn("no setting for <{}> and non set in configuration <{}> using the system default <true>",
						TaskConfig.TEXT_ANALYSIS_USE_TITLE, Config.TEXT_ANALYSIS_USE_TITLE_DEFAULT);
				def = "true";
			}

			task.setProperty(TaskConfig.TEXT_ANALYSIS_USE_TITLE, def);
		}
		if (task.get(TaskConfig.TEXT_ANALYSIS_USE_KEYWORDS) == null)
		{
			String def = config.get(Config.TEXT_ANALYSIS_USE_KEYWORDS_DEFAULT);
			if (def != null)
			{
				LOG.warn("no setting for <{}> overriding with default from <{}>", TaskConfig.TEXT_ANALYSIS_USE_KEYWORDS,
						Config.TEXT_ANALYSIS_USE_KEYWORDS_DEFAULT);
			}
			else
			{
				LOG.warn("no setting for <{}> and non set in configuration <{}> using the system default <false>",
						TaskConfig.TEXT_ANALYSIS_USE_KEYWORDS, Config.TEXT_ANALYSIS_USE_KEYWORDS_DEFAULT);
				def = "false";
			}

			task.setProperty(TaskConfig.TEXT_ANALYSIS_USE_KEYWORDS, def);
		}

	}

	/**
	 * return closest midnight
	 * @return
	 */
	private long getMidnight()
	{
		Date date = new Date(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance(); // get calendar instance
		cal.setTime(date); // set cal to date
		cal.set(Calendar.HOUR_OF_DAY, 0); // reset hour to midnight
		cal.set(Calendar.MINUTE, 0); // reset minute in hour
		cal.set(Calendar.SECOND, 0); // reset second in minute
		cal.set(Calendar.MILLISECOND, 0); // reset millis in second
		return cal.getTime().getTime();
	}

	/**
	 * return midnight, next day
	 * @return
	 */
	private long getNextMidnight()
	{
		Date date = new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)); // timestamp now + day
		Calendar cal = Calendar.getInstance(); // get calendar instance
		cal.setTime(date); // set cal to date
		cal.set(Calendar.HOUR_OF_DAY, 0); // reset hour to midnight
		cal.set(Calendar.MINUTE, 0); // reset minute in hour
		cal.set(Calendar.SECOND, 0); // reset second in minute
		cal.set(Calendar.MILLISECOND, 0); // reset millis in second
		return cal.getTime().getTime();
	}

	/**
	 * Extract the recommender classname from known names - if it's not found assume the name itself is a classname (this
	 * allows for plugable mapreduce libs)
	 * @param name - recommender name or classname
	 * @return a classname
	 */
	private String getRecommenderClass(String name)
	{
		if (TaskConfig.RECOMMENDER_TYPE_ITEMBASED.equals(name))
		{
			return ItemBasedRecommenderTask.class.getCanonicalName();
		}
		else if (TaskConfig.RECOMMENDER_TYPE_USERBASED.equals(name))
		{
			return UserBasedRecommenderTask.class.getCanonicalName();
		}
		else if (TaskConfig.RECOMMENDER_TYPE_TEXTBASED.equals(name))
		{
			return TaalServerTask.class.getCanonicalName();
		}
		else
		{
			// assume it's a class name
			return name;
		}
	}

	/**
	 * Get the input table for the recommender
	 * @param cp
	 * @return
	 * @throws MasterNotRunningException
	 * @throws ZooKeeperConnectionException
	 */
	private String getRecommenderInputTable(String cp, String type) throws MasterNotRunningException,
			ZooKeeperConnectionException
	{
		// Is it a text based recommender, than we need the collection table
		// if it's a rating based recommender, we need the user table

		if (TaskConfig.RECOMMENDER_TYPE_ITEMBASED.equals(type) || TaskConfig.RECOMMENDER_TYPE_USERBASED.equals(type))
		{
			HBaseUserTable table = new HBaseUserTable(HBaseManager.getInstance(config.getZooKeeperHost()));
			final String tableName = table.getTableName(cp);
			LOG.info("recommender type <{}> input table <{}>", type, tableName);

			return tableName;
		}
		else if (TaskConfig.RECOMMENDER_TYPE_TEXTBASED.equals(type))
		{
			HBaseCollectionTable table = new HBaseCollectionTable(HBaseManager.getInstance(config.getZooKeeperHost()));
			final String tableName = table.getTableName(cp);
			LOG.info("recommender type <{}> input table <{}>", type, tableName);

			return tableName;
		}
		return null;
	}
}
