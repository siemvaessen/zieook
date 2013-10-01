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
package nl.gridline.zieook.data.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import nl.gridline.zieook.commons.ServerState;
import nl.gridline.zieook.commons.ZieOokCommons;
import nl.gridline.zieook.commons.ZieOokManager;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.EventLogTable;
import nl.gridline.zieook.data.hbase.dao.UserTable;
import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.model.User;
import nl.gridline.zieook.model.Users;

import org.apache.commons.lang.NotImplementedException;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User related requests
 * <p />
 * Project zieook-backend-data<br />
 * UserController.java created 23 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class UserController implements ZieOokManager
{

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	public static final String NAME = "nl.zieook.datamanager.users";

	private boolean state;

	private Config config;

	private HBaseManager manager;

	private UserTable userTable;

	private final DataController dataController;

	private EventLogTable eventLogTable;

	public UserController(DataController controller)
	{
		dataController = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.commons.ZieOokManager#startup()
	 */
	@Override
	public void startup() throws MasterNotRunningException, ZooKeeperConnectionException
	{
		LOG.info(ZieOokCommons.someinformation("UserController"));

		// create configuration object:
		LOG.info("loading configuration from: {}", Config.ZIEOOK_DATAMANAGER_FILE);
		config = Config.getInstance(Config.ZIEOOK_DATAMANAGER_FILE);

		// get configuration setting for zookeeper:
		String zookeeper = config.getZooKeeperHost();
		state = zookeeper != null;

		if (state)
		{
			LOG.info("creating hbase manager using: {}", zookeeper);
			manager = HBaseManager.getInstance(zookeeper);

			HBaseConfigurationTable cTable = new HBaseConfigurationTable(manager);
			if (!cTable.tableExists())
			{
				state = cTable.create();
				if (state)
				{
					LOG.info("Configuration table did not exists, and was created");
				}
				else
				{
					LOG.info("Configuration table did not exist, and could not be created");
				}
			}

			eventLogTable = new EventLogTable(new HBaseEventLogTable(manager));
			userTable = new UserTable(new HBaseUserTable(manager));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.commons.ZieOokManager#shutdown()
	 */
	@Override
	public void shutdown()
	{
		LOG.info("Shutdown UserController");
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
	public ServerState getState()
	{
		return new ServerState();
	}

	/**
	 * check if a user exists
	 * @param cp
	 * @param id
	 * @return
	 */
	public boolean isUser(String cp, long user)
	{
		return !userTable.isUser(cp, user);
	}

	/**
	 * get a user (a user is linked
	 * @param cp
	 * @param id
	 * @return
	 */
	public User getUser(String cp, Long id)
	{
		return userTable.getUser(cp, id);
	}

	public Users getUsers(String cp, Long start)
	{
		List<User> users = userTable.getUsers(cp, start, 100);
		return new Users(users);
	}

	public String getUserRDF(String cp, Long id)
	{
		throw new NotImplementedException();
	}

	public String getUsersRDF(String cp, long start)
	{
		throw new NotImplementedException();
	}

	public void addUser(String cp, User user)
	{
		LOG.info("Add user " + user + " to " + cp);
		userTable.putUser(cp, user);
	}

	/**
	 * update a user.
	 * @param cp
	 * @param id
	 * @param user
	 */
	public void updateUser(String cp, Long id, User user)
	{
		userTable.putUser(cp, user);
	}

	/**
	 * Delete a {@code user}, the user object is removed, but also all of its given ratings and views
	 * @param cp content provider
	 * @param user user id
	 */
	public void removeUser(String cp, Long user)
	{
		// also delete all user views and recommended items
		// delete all user ratings

		SortedMap<String, List<String>> map = dataController.getCollectionsRecommenders(cp);
		for (Map.Entry<String, List<String>> entry : map.entrySet())
		{
			userTable.deleteRatings(cp, entry.getKey(), user);
			for (String r : entry.getValue())
			{
				eventLogTable.deleteViews(cp, r, user);
				eventLogTable.deleteRecommend(cp, r, user);
			}
		}
		userTable.removeUser(cp, user);
	}

	/**
	 * @param recommenders list of <em>cp/recommender</em> pairs
	 * @param user user id
	 * @return the user with it's summed activity
	 */
	public User getActivity(List<String[]> recommenders, Long user)
	{
		User result = null;
		for (String[] cprec : recommenders)
		{
			final String cp = cprec[0];
			// final String collection = dataController.getRecommenderCollection(cp, cprec[1]);
			User u = userTable.getUser(cp, user);
			LOG.debug("user found: {}", u);
			if (u != null)
			{
				if (result == null)
				{
					result = u;
				}
				else
				{
					result.setRatingCount(u.getRatingCount() + result.getRatingCount());
					result.setViewCount(u.getViewCount() + result.getViewCount());
					result.setRecommendCount(u.getRecommendCount() + result.getRecommendCount());
				}
			}
		}

		return result;
	}

	/**
	 * @param recommenderList
	 * @param userList
	 * @return
	 */
	public Users getActivity(List<String[]> recommenders, List<Long> users)
	{
		List<User> results = new ArrayList<User>();

		for (Long user : users)
		{
			User result = null;
			for (String[] cprec : recommenders)
			{
				final String cp = cprec[0];
				User u = userTable.getUser(cp, user);
				if (u != null)
				{
					if (result == null)
					{
						result = u;
					}
					else
					{
						result.setRatingCount(u.getRatingCount() + result.getRatingCount());
						result.setViewCount(u.getViewCount() + result.getViewCount());
						result.setRecommendCount(u.getRecommendCount() + result.getRecommendCount());
					}
				}
			}
			if (users != null)
			{
				results.add(result);
			}
		}

		return new Users(results);
	}
}
