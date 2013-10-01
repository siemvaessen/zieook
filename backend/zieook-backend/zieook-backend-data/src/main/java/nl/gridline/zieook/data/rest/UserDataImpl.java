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

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import nl.gridline.zieook.api.UserData;
import nl.gridline.zieook.data.controller.DataController;
import nl.gridline.zieook.data.controller.UserController;
import nl.gridline.zieook.exceptions.ContentProviderDoesNotExist;
import nl.gridline.zieook.exceptions.UserDoesNotExistException;
import nl.gridline.zieook.model.User;
import nl.gridline.zieook.model.Users;

import org.jboss.resteasy.spi.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieving basic user information
 * <p />
 * Project zieook-backend-data<br />
 * UserDataImpl.java created 7 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class UserDataImpl implements UserData
{

	private static final Logger LOG = LoggerFactory.getLogger(UserDataImpl.class);

	private static final String NO_USER = "parameter <user> missing";

	@Context
	private ServletContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.api.UserData#getUserActivity(java.lang.String, java.lang.Long)
	 */
	@Override
	public Object getUserActivity(String recommenders, Long user, String users)
	{
		if (user == null && users == null)
		{
			throw new BadRequestException(NO_USER);
		}

		List<String[]> recommenderList = RestTools.getRecommenders(recommenders);
		List<Long> userList = null;
		if (users != null)
		{
			userList = RestTools.getUsers(users);
		}

		UserController controller = (UserController) context.getAttribute(UserController.NAME);

		if (users != null)
		{
			return controller.getActivity(recommenderList, userList);
		}
		if (user != null)
		{
			return controller.getActivity(recommenderList, user);
		}

		throw new BadRequestException("failed to process input, pass either <user> or <users>");
	}

	@Override
	public User getUser(String cp, Long user)
	{
		LOG.debug("get user <cp,user> = <{},{}>", cp, user);
		// return the user for this cp
		// 404 otherwise (cp does not exist, user does not exist)
		if (user == null)
		{
			throw new BadRequestException(NO_USER);
		}
		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		UserController users = (UserController) context.getAttribute(UserController.NAME);

		User result = users.getUser(cp, user);
		if (result == null)
		{
			throw new UserDoesNotExistException(cp, user);
		}
		return result;
	}

	@Override
	public String isUser(String cp, Long user)
	{
		LOG.debug("exists user <cp,id> = <{},{}>", cp, user);
		// return true if cp & user exist
		// nothing otherwise

		if (user == null)
		{
			throw new BadRequestException(NO_USER);
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		UserController users = (UserController) context.getAttribute(UserController.NAME);

		return users.isUser(cp, user) ? "true" : "false";
	}

	@Override
	public Users getUsers(String cp, Long start)
	{
		LOG.debug("get users <cp,start> = <{},{}>", cp, start);
		// return 100 users starting from start
		// 404 if cp does not exist
		// nothing if users do not exist

		if (start == null)
		{
			start = 0L;
		}
		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		UserController users = (UserController) context.getAttribute(UserController.NAME);

		return users.getUsers(cp, start);
	}

	@Override
	public String getUserRDF(String cp, Long user)
	{
		LOG.debug("get user[RDF] <cp,id> = <{},{}>", cp, user);
		// return the user as an rdf/xml string
		// or 404 if cp || id does not exist
		if (user == null)
		{
			throw new BadRequestException(NO_USER);
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		UserController users = (UserController) context.getAttribute(UserController.NAME);

		String result = users.getUserRDF(cp, user);
		if (result == null)
		{
			throw new UserDoesNotExistException(cp, user);
		}
		return result;
	}

	@Override
	public String getUsersRDF(String cp, Long start)
	{
		LOG.debug("get users[RDF] <cp,start> = <{},{}>", cp, start);
		// return 100 users starting from start
		// 404 if cp does not exist
		// nothing if users do not exist

		if (start == null)
		{
			start = 0L;
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		UserController users = (UserController) context.getAttribute(UserController.NAME);
		return users.getUsersRDF(cp, start);
	}

	@Override
	public Response addUser(String cp, User user)
	{
		LOG.debug("add user <cp,id> = <{},{}>", cp, user != null ? user.getId() : null);
		// Adds a new user
		// 404 if cp does not exist
		// 304 is the user already exists
		// 201 if the user is added

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		UserController users = (UserController) context.getAttribute(UserController.NAME);
		if (users.isUser(cp, user.getId()))
		{
			return Response.status(304).build();
		}

		users.addUser(cp, user);

		return Response.status(201).build();
	}

	@Override
	public Response putUser(String cp, Long id, User user)
	{
		LOG.debug("update user <cp,user> = <{},{}>", cp, id);
		// Updates a user
		// 404 if cp does not exist || user does not exist

		if (id == null)
		{
			throw new BadRequestException("Id cannot be null.");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		UserController users = (UserController) context.getAttribute(UserController.NAME);
		if (!users.isUser(cp, id))
		{
			throw new UserDoesNotExistException(cp, id);
		}

		if (id.longValue() != user.getId().longValue())
		{
			throw new UserDoesNotExistException(cp, id);
		}

		users.updateUser(cp, id, user);

		return Response.ok().build();
	}

	@Override
	public Response removeUser(String cp, Long user)
	{
		LOG.debug("delete user <cp,user> = <{},{}>", cp, user);
		// Removes a user
		// 404 if cp || user does not exist

		if (user == null)
		{
			throw new BadRequestException("Id cannot be null.");
		}

		DataController controller = (DataController) context.getAttribute(DataController.NAME);
		if (!controller.isContentProvider(cp))
		{
			throw new ContentProviderDoesNotExist(cp);
		}

		UserController users = (UserController) context.getAttribute(UserController.NAME);
		if (!users.isUser(cp, user))
		{
			throw new UserDoesNotExistException(cp, user);
		}

		users.removeUser(cp, user);

		return Response.ok().build();
	}

}
