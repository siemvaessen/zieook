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
package nl.gridline.zieook.data.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import nl.gridline.zieook.api.CollectionData;
import nl.gridline.zieook.data.controller.CollectionController;
import nl.gridline.zieook.data.controller.DataController;
import nl.gridline.zieook.exceptions.CollectionDoesNotExistException;
import nl.gridline.zieook.exceptions.CollectionItemDoesNotExist;
import nl.gridline.zieook.exceptions.ContentProviderDoesNotExist;
import nl.gridline.zieook.exceptions.DoesNotExists;
import nl.gridline.zieook.model.Categories;
import nl.gridline.zieook.model.CollectionItem;
import nl.gridline.zieook.model.CollectionItems;

import org.jboss.resteasy.spi.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-data<br />
 * CollectionDataImpl.java created 10 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionDataImpl implements CollectionData
{

	private static final Logger LOG = LoggerFactory.getLogger(CollectionDataImpl.class);

	@Context
	private ServletContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.CollectionData#getCollectionCategories(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Categories getCollectionCategories(String cp, String collection, String next)
	{
		LOG.debug("get collection categories <cp,collection> = <{},{}>", cp, collection);
		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		CollectionController ccollection = (CollectionController) context.getAttribute(CollectionController.NAME);

		return ccollection.getCollectionCategories(cp, collection, next);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.CollectionData#getItem(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getItemRaw(String cp, String collection, Long item)
	{
		LOG.debug("get item raw <cp,collection,item> = <{},{}>", cp + "," + collection, item);
		if (item == null)
		{
			throw new BadRequestException("No item given.");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		CollectionController ccollection = (CollectionController) context.getAttribute(CollectionController.NAME);

		String result = ccollection.getItemRaw(cp, collection, item);
		if (result == null)
		{
			throw new CollectionItemDoesNotExist(item);
		}
		return result;
	}

	@Override
	public CollectionItem getItem(String cp, String collection, Long id)
	{
		LOG.debug("get intr item <cp,collection,item> = <{},{}>", cp + "," + collection, id);
		// check the input:
		if (id == null)
		{
			throw new BadRequestException("No item id given");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		// gather results:

		CollectionController ccollection = (CollectionController) context.getAttribute(CollectionController.NAME);

		CollectionItem result = ccollection.getItem(cp, collection, id);

		// test output, throw 404 if nothing found:
		if (result == null)
		{
			throw new CollectionItemDoesNotExist(id);
		}
		return result;
	}

	@Override
	public CollectionItems getItems(String cp, String collection, Integer size, Long start)
	{
		LOG.debug("get items <cp,collection,start> = <{},{}> size=" + size, cp + "," + collection, start);
		if (size == null)
		{
			size = 100;
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		CollectionController ccollection = (CollectionController) context.getAttribute(CollectionController.NAME);

		CollectionItems result = ccollection.getItems(cp, collection, start, size);
		if (result == null)
		{
			// throw a 404
			throw new DoesNotExists("not data for request");
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.CollectionData#getItems(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public CollectionItems getItems(String cp, String collection, String items)
	{
		if (items == null)
		{
			throw new BadRequestException("'items' parameter missing");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		// convert to list of long values:
		List<Long> itemIds = new ArrayList<Long>();
		try
		{
			String[] itemsArr = items.split(",");
			if (itemsArr.length == 0)
			{
				throw new BadRequestException("'items' parameter should be a comma separated list of id's");
			}
			for (String i : itemsArr)
			{
				itemIds.add(Long.parseLong(i));
			}
		}
		catch (NumberFormatException e)
		{
			throw new BadRequestException("'items' parameter should be a comma separated list of id's");
		}

		CollectionController ccollection = (CollectionController) context.getAttribute(CollectionController.NAME);

		CollectionItems result = ccollection.getItems(cp, collection, itemIds);
		if (result == null)
		{
			// throw a 404
			throw new DoesNotExists("not data for request");
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.CollectionData#searchItems(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.Integer)
	 */
	@Override
	public CollectionItems searchItems(String cp, String collection, String titleQuery, String titleRegExp, Integer size)
	{
		if (titleQuery == null && titleRegExp == null)
		{
			throw new BadRequestException("no search query passed");
		}
		// test cp / collection
		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}
		if (!controller.isCollection(cp, collection))
		{
			throw new CollectionDoesNotExistException(cp, collection);
		}

		if (size == null)
		{
			size = 100;
		}

		CollectionController ccollection = (CollectionController) context.getAttribute(CollectionController.NAME);
		CollectionItems result = ccollection.searchItems(cp, collection, titleQuery, titleRegExp, size.intValue());
		if (result == null)
		{
			throw new DoesNotExists("no data for request");
		}
		return result;
	}

}
