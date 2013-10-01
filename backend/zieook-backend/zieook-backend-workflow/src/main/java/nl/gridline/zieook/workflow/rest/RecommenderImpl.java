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
package nl.gridline.zieook.workflow.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import nl.gridline.zieook.exceptions.ContentProviderDoesNotExist;
import nl.gridline.zieook.exceptions.DoesNotExists;
import nl.gridline.zieook.exceptions.RecommenderDoesNotExist;
import nl.gridline.zieook.exceptions.TaskCreationFailure;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.workflow.WorkflowController;
import nl.gridline.zieook.workflow.WorkflowScheduler;
import nl.gridline.zieook.workflow.api.Recommender;
import nl.gridline.zieook.workflow.model.Names;
import nl.gridline.zieook.workflow.model.RecommenderConfig;
import nl.gridline.zieook.workflow.model.Recommenders;
import nl.gridline.zieook.workflow.model.TaskConfigIdList;
import nl.gridline.zieook.workflow.model.TaskConfigList;
import nl.gridline.zieook.workflow.model.TaskConfigMap;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * backend-workflow<br />
 * RecommenderImpl.java created 7 jan. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @see Recommender
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class RecommenderImpl implements Recommender
{

	private static final Logger LOG = LoggerFactory.getLogger(RecommenderImpl.class);

	@Context
	private ServletContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.Recommender#getRecommenders(java.lang.String)
	 */
	@Override
	public Recommenders getRecommenders(String cp)
	{
		LOG.debug("getting recommenders for <cp> = <{}>", cp);

		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		final Recommenders result = controller.getRecommenders(cp);
		return result;
	}

	@Override
	public Recommenders getRecommenders()
	{
		LOG.debug("getting all recommenders");

		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);

		Recommenders result = controller.getRecommenders();
		LOG.debug("result: " + result.toJSON());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.Recommender#createConfiguration(java.lang.String,
	 * nl.gridline.zieook.workflow.model.RecommenderConfig)
	 */
	@Override
	public Response createRecommender(String cp, RecommenderConfig config)
	{
		LOG.debug("create recommender config for <cp> = <{}>", cp);

		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		// check input:

		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		if (!cp.equals(config.getCp()))
		{
			LOG.error("path cp '{}' and posted data cp '{}' should be equal");
			throw new NotFoundException("the given parameters are not correct");
		}

		controller.createRecommender(cp, config);

		return Response.status(201).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.Recommender#readConfiguration(java.lang.String, java.lang.String)
	 */
	@Override
	public RecommenderConfig readConfiguration(String cp, String recommender)
	{
		LOG.debug("reading recommender config <cp,recommender> = <{},{}>", cp, recommender);
		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		return controller.readRecommender(cp, recommender);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.Recommender#updateConfiguration(java.lang.String, java.lang.String,
	 * nl.gridline.zieook.workflow.model.RecommenderConfig)
	 */
	@Override
	public Response updateRecommender(String cp, String recommender, RecommenderConfig config)
	{
		LOG.debug("update recommender config <cp,recommender> = <{},{}>", cp, recommender);
		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);

		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		// check recommender
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		controller.updateRecommender(cp, recommender, config);
		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.Recommender#deleteConfiguration(java.lang.String, java.lang.String)
	 */
	@Override
	public Response deleteRecommender(String cp, String recommender)
	{
		LOG.debug("delete recommender config <cp,recommender> = <{},{}>", cp, recommender);

		// check if the cp recommender exists: (throw a 404 otherwise)
		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);

		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		WorkflowScheduler scheduler = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);

		scheduler.cancelRecommenderTask(cp, recommender);
		scheduler.deleteRecommenderTasks(cp, recommender);

		controller.deleteRecommender(cp, recommender);
		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.Recommender#getTask(java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public TaskConfigMap getTask(String cp, String recommender, Long task)
	{
		LOG.debug("get task configurations <cp,recommender,task> <{},{}>", cp + "," + recommender, task);

		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		WorkflowScheduler scheduler = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);
		if (task == null)
		{
			throw new BadRequestException("no task id found in request");
		}

		TaskConfig result = scheduler.getTask(task);
		if (result == null)
		{
			throw new DoesNotExists("task with id <" + task + "> does not exists");
		}

		return new TaskConfigMap(result.toMap());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.Recommender#getTasks(java.lang.String, java.lang.String)
	 */
	@Override
	public TaskConfigIdList getTaskIds(String cp, String recommender, Long start, Long end)
	{
		LOG.debug("get task list <cp,recommender> = <{}> time limit <start,end> = <{}>", cp + "," + recommender, start
				+ "," + end);

		TaskConfigIdList result = null;

		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		WorkflowScheduler scheduler = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);
		if (start == null && end == null)
		{
			// tasks that need to be executed:
			result = new TaskConfigIdList(scheduler.getTasks(cp, TaskConfig.TASK_TYPE_RECOMMENDER, recommender, -1, -1));
		}
		else if (end == null)
		{
			// range end not given, set to now
			// multiply start by 1000 - the input is given in seconds:
			result = new TaskConfigIdList(scheduler.getTasks(cp, TaskConfig.TASK_TYPE_RECOMMENDER, recommender, start,
					System.currentTimeMillis() / 1000));
		}
		else if (start == null)
		{
			// start open - return all until end
			// multiply end by 1000 - the input is given in seconds:
			result = new TaskConfigIdList(scheduler.getTasks(cp, TaskConfig.TASK_TYPE_RECOMMENDER, recommender, -1, end));
		}
		else
		{
			result = new TaskConfigIdList(
					scheduler.getTasks(cp, TaskConfig.TASK_TYPE_RECOMMENDER, recommender, start, end));
		}

		return result;

	}

	@Override
	public TaskConfigList getTasks(String cp, String recommender, Long start, Long end)
	{
		LOG.debug("get task list <cp,recommender> = <{}> time limit <start,end> = <{}>", cp + "," + recommender, start
				+ "," + end);

		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		WorkflowScheduler scheduler = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);

		List<Long> list;

		if (start == null && end == null)
		{
			// tasks that need to be executed:
			list = scheduler.getTasks(cp, TaskConfig.TASK_TYPE_RECOMMENDER, recommender, -1, -1);
		}
		else if (end == null) // && start !=null
		{
			// range end not given, set to now
			// multiply start by 1000 - the input is given in seconds:
			list = scheduler.getTasks(cp, TaskConfig.TASK_TYPE_RECOMMENDER, recommender, start,
					System.currentTimeMillis() / 1000);
		}
		else if (start == null) // && end !=null
		{
			// start open - return all until end
			// multiply end by 1000 - the input is given in seconds:
			list = scheduler.getTasks(cp, TaskConfig.TASK_TYPE_RECOMMENDER, recommender, -1, end);
		}
		else
		{
			list = scheduler.getTasks(cp, TaskConfig.TASK_TYPE_RECOMMENDER, recommender, start, end);
		}

		ArrayList<TaskConfigMap> result = new ArrayList<TaskConfigMap>(list.size());
		// empty result
		if (!list.isEmpty())
		{
			for (Long task : list)
			{
				TaskConfig taskconfig = scheduler.getTask(task);
				if (taskconfig != null)
				{
					result.add(new TaskConfigMap(taskconfig.toMap()));
				}
				else
				{
					LOG.error("listed task id could not be retrieved {} - ignoring", task);
				}
			}
		}

		return new TaskConfigList(result);
	}

	@Override
	public TaskConfigMap getTaskStatus(String cp, String recommender)
	{
		LOG.debug("getting completed task for <cp,recommender> = <{},{}>", cp, recommender);

		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		WorkflowScheduler scheduler = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);
		TaskConfig result = scheduler.getCompletedTask(cp, TaskConfig.TASK_TYPE_RECOMMENDER, recommender);
		if (result != null)
		{
			return new TaskConfigMap(result.toMap());
		}
		// or 404?
		return new TaskConfigMap();
	}

	@Override
	public Response scheduleRecommender(String cp, String recommender, String date)
	{
		LOG.debug("schedule recommender <cp,recommender> = <{},{}>", cp, recommender);

		LOG.debug("date: " + date);

		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		WorkflowScheduler scheduler = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);

		// try to extract the date:
		// {"start_training":1302618752}
		long next = 0;
		String[] elements = date.split(":");
		if (elements.length != 2)
		{
			next = 0;
		}

		if (elements[0].length() > 3 && elements[0].substring(2, elements[0].length() - 1).equals("start_training"))
		{
			try
			{
				next = Long.parseLong(elements[1].substring(0, elements[1].length() - 1));
			}
			catch (NumberFormatException e)
			{
				throw new BadRequestException("content not acceptable: '" + date + "'");
			}
		}
		else
		{
			throw new BadRequestException("content not acceptable: '" + date + "'");
		}

		RecommenderConfig recommenderConfig = controller.readRecommender(cp, recommender);

		// cannot create a task, fail..
		TaskConfig task;
		try
		{
			task = scheduler.createRecommenderTask(recommenderConfig, next);
			scheduler.schedule(task);
		}
		catch (MasterNotRunningException e)
		{
			throw new TaskCreationFailure(cp + "/" + recommender, e);
		}
		catch (ZooKeeperConnectionException e)
		{
			throw new TaskCreationFailure(cp + "/" + recommender, e);
		}
		catch (IOException e)
		{
			throw new TaskCreationFailure(cp + "/" + recommender, e);
		}

		return null;
	}

	@Override
	public Response cancelRecommender(String cp, String recommender, String data)
	{
		LOG.debug("cancelling recommender <cp,recommender> = <{},{}>", cp, recommender);

		LOG.debug("data: " + data);

		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);
		if (!controller.isRecommender(cp, recommender))
		{
			throw new RecommenderDoesNotExist(cp, recommender);
		}

		WorkflowScheduler scheduler = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);
		scheduler.cancelRecommenderTask(cp, recommender);

		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.Recommender#getRecommenderCP(java.lang.String)
	 */
	@Override
	public Names getRecommenderCP(String recommender)
	{
		WorkflowController controller = (WorkflowController) context.getAttribute(WorkflowController.ZIEOOK_WORKFLOW);

		List<String> result = controller.getRecommenderCP(recommender);
		if (result != null)
		{
			return new Names(result);
		}

		throw new RecommenderDoesNotExist(null, recommender);

	}
}
