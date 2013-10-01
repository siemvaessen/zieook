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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import nl.gridline.zieook.OAIException;
import nl.gridline.zieook.exceptions.CollectionDoesNotExistException;
import nl.gridline.zieook.exceptions.ContentProviderDoesNotExist;
import nl.gridline.zieook.exceptions.DoesNotExists;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.workflow.CollectionController;
import nl.gridline.zieook.workflow.WorkflowScheduler;
import nl.gridline.zieook.workflow.api.CollectionImport;
import nl.gridline.zieook.workflow.model.Collection;
import nl.gridline.zieook.workflow.model.Collections;
import nl.gridline.zieook.workflow.model.OAIMetadata;
import nl.gridline.zieook.workflow.model.OAISets;
import nl.gridline.zieook.workflow.model.TaskConfigList;
import nl.gridline.zieook.workflow.model.TaskConfigMap;

import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Import collection implementation
 * <p />
 * Project zieook-backend-workflow<br />
 * CollectionImportImpl.java created 7 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionImportImpl implements CollectionImport
{

	private static final Logger LOG = LoggerFactory.getLogger(CollectionImportImpl.class);

	// @Context
	// private HttpServletRequest request;

	@Context
	private ServletContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.CollectionImport#getAvailableMetaData(java.lang.String)
	 */
	@Override
	public OAIMetadata getAvailableMetaData(String url)
	{
		LOG.debug("getting oai metadata for <{}>", url);
		CollectionController controller = (CollectionController) context.getAttribute(CollectionController.NAME);

		try
		{
			return controller.getMetaData(url);
		}
		catch (JAXBException e)
		{
			LOG.error("failed to get metadata for <" + url + ">", e);
			throw new InternalServerErrorException("failed to get metadata for <" + url + ">");
		}
		catch (IOException e)
		{
			LOG.error("failed to get metadata for <" + url + ">", e);
			throw new BadRequestException("failed to get metadata for <" + url + ">");
		}
		catch (OAIException e)
		{
			LOG.error("failed to get metadata for <" + url + ">", e);
			throw new BadRequestException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.CollectionImport#getAvailableSets(java.lang.String)
	 */
	@Override
	public OAISets getAvailableSets(String url)
	{
		LOG.debug("getting oai sets for <{}>", url);
		CollectionController controller = (CollectionController) context.getAttribute(CollectionController.NAME);

		try
		{
			return controller.getOAISets(url);
		}
		catch (JAXBException e)
		{
			LOG.error("failed to get sets for <" + url + ">", e);
			throw new InternalServerErrorException("failed to get sets for <" + url + ">", e);
		}
		catch (IOException e)
		{
			LOG.error("failed to get sets for <" + url + ">", e);
			throw new BadRequestException("failed to get sets for <" + url + ">", e);
		}
		catch (OAIException e)
		{
			throw new BadRequestException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.CollectionImport#createCollectionMeta(java.lang.String,
	 * nl.gridline.zieook.workflow.model.Collection)
	 */
	@Override
	public Response createCollectionMeta(String cp, Collection collection)
	{
		LOG.debug("creating collection meta-data for <{}>", cp);
		CollectionController controller = (CollectionController) context.getAttribute(CollectionController.NAME);

		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (controller.isCollection(cp, collection.getName()))
		{
			// already exists
			return Response.status(304).build();
		}

		controller.createCollection(cp, collection);

		return Response.status(201).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.CollectionImport#readCollectionMeta(java.lang.String, java.lang.String)
	 */
	@Override
	public Collection readCollectionMeta(String cp, String collection)
	{
		LOG.debug("reading collection meta data for <cp,collection> = <{},{}>", cp, collection);
		CollectionController controller = (CollectionController) context.getAttribute(CollectionController.NAME);

		return controller.getCollection(cp, collection);

	}

	@Override
	public Collections readCollectionsMeta(String cp)
	{
		LOG.debug("reading all collections for <{}>", cp);
		CollectionController controller = (CollectionController) context.getAttribute(CollectionController.NAME);

		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		return controller.getCollections(cp);
	}

	@Override
	public Collections readCollectionsMeta()
	{
		LOG.debug("reading all collections");

		CollectionController controller = (CollectionController) context.getAttribute(CollectionController.NAME);

		return controller.getCollections();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.CollectionImport#updateCollectionMeta(java.lang.String, java.lang.String,
	 * nl.gridline.zieook.workflow.model.Collection)
	 */
	@Override
	public Response updateCollectionMeta(String cp, String collectionname, Collection collection)
	{
		LOG.debug("updating collection meta data for <cp,collection> = <{},{}>", cp, collectionname);
		CollectionController controller = (CollectionController) context.getAttribute(CollectionController.NAME);

		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		if (!controller.isCollection(cp, collectionname))
		{
			throw new CollectionDoesNotExistException(cp, collectionname);
		}

		controller.updateCollectionMeta(cp, collectionname, collection);
		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.CollectionImport#updateCollectionData(java.lang.String, java.lang.String)
	 */
	@Override
	public Response updateCollectionData(String cp, String collection, String part, HttpServletRequest request)
	{
		LOG.debug("updating collectiondata <cp,collection,part> = <{},{}>", cp + "," + collection, part);

		if (!TaskConfig.DATA_PART_COLLECTION.equals(part) && !TaskConfig.DATA_PART_USERS.equals(part)
				&& !TaskConfig.DATA_PART_RATINGS.equals(part))
		{
			throw new DoesNotExists("the given part <{}> is not valid", part);
		}

		CollectionController controller = (CollectionController) context.getAttribute(CollectionController.NAME);

		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		// read data from request
		InputStream stream;
		try
		{
			stream = request.getInputStream();
			controller.updateCollectionData(cp, collection, part, stream);
		}
		catch (IOException e)
		{
			LOG.error("Failed to uploaded data for " + cp + "/" + collection + "/" + part, e);
			throw new InternalServerErrorException("data upload failed");
		}
		catch (RuntimeException e)
		{
			LOG.error("data upload failed for " + cp + "/" + collection, e);
			throw e;
		}

		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.workflow.api.CollectionImport#deleteCollectionLink(java.lang.String, java.lang.String)
	 */
	@Override
	public Response deleteCollection(String cp, String collection)
	{
		LOG.debug("deleting collection <cp,collection> = <{},{}>", cp, collection);

		CollectionController controller = (CollectionController) context.getAttribute(CollectionController.NAME);

		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		if (!controller.isCollection(cp, collection))
		{
			return Response.notModified().build();
		}

		controller.getCollection(cp, collection);

		controller.deleteCollection(cp, collection);
		return Response.ok().build();
	}

	@Override
	public TaskConfigList collectionImportList(String cp, String collection, Long start, Long end)
	{
		LOG.debug("getting collection importlist <{}>  <{}>", cp + "," + collection, start + " " + end);

		// throw a 404 if the cp/collection does not exist:
		CollectionController controller = (CollectionController) context.getAttribute(CollectionController.NAME);
		controller.getCollection(cp, collection);

		// now try to list tasks:

		WorkflowScheduler scheduler = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);

		List<Long> list;

		if (start == null && end == null)
		{
			// tasks that need to be executed:
			list = scheduler.getTasks(cp, TaskConfig.TASK_TYPE_COLLECTION, collection, -1, -1);
		}
		else if (end == null)
		{
			// range end not given, set to now
			// multiply start by 1000 - the input is given in seconds:
			list = scheduler.getTasks(cp, TaskConfig.TASK_TYPE_COLLECTION, collection, start,
					System.currentTimeMillis() / 1000);
		}
		else if (start == null)
		{
			// start open - return all until end
			// multiply end by 1000 - the input is given in seconds:
			list = scheduler.getTasks(cp, TaskConfig.TASK_TYPE_COLLECTION, collection, -1, end);
		}
		else
		{
			list = scheduler.getTasks(cp, TaskConfig.TASK_TYPE_COLLECTION, collection, start, end);
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
	public TaskConfigMap collectionImportState(String cp, String collection)
	{
		LOG.debug("getting collection import state <{},{}>", cp, collection);

		CollectionController controller = (CollectionController) context.getAttribute(CollectionController.NAME);

		// throw a 404 if the cp/collection does not exist:
		controller.getCollection(cp, collection);

		WorkflowScheduler scheduler = (WorkflowScheduler) context.getAttribute(WorkflowScheduler.ZIEOOK_SCHEDULER);
		TaskConfig result = scheduler.getCompletedTask(cp, TaskConfig.TASK_TYPE_COLLECTION, collection);
		if (result != null)
		{
			return new TaskConfigMap(result.toMap());
		}

		throw new DoesNotExists("could not find completed task for <cp,collection> = <" + cp + "," + collection + ">");
	}

	@Override
	public Response scheduleCollection(String cp, String collection, String dateStr)
	{
		LOG.debug("scheduling collection for <{}> on {}", cp + "," + collection, dateStr);

		CollectionController controller = (CollectionController) context.getAttribute(CollectionController.NAME);

		// throws an exception if cp or collection does not exist:
		Collection collObject = controller.getCollection(cp, collection);

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(dateStr);
		long date = (System.currentTimeMillis() / 1000) + 4;
		if (element != null)
		{
			JsonElement dateObj = element.getAsJsonObject().get("date");
			if (dateObj != null)
			{
				date = dateObj.getAsLong();
			}
		}

		controller.scheduleCollection(cp, collObject, date);

		return Response.status(200).build();
	}
}
