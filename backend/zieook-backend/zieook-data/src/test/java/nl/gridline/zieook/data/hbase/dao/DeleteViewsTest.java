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


package nl.gridline.zieook.data.hbase.dao;

import static org.junit.Assume.assumeTrue;

import java.util.List;

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.model.HBaseEventLogTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.model.Rating;
import nl.gridline.zieook.model.UserView;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * DeleteViewsTest.java created 17 jan. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class DeleteViewsTest
{
	static final String CP = "testCP006";
	static final String COLLECTION = "am2";
	static final String RECOMMENDER = "textbased";

	private static HBaseManager manager;
	private static HBaseEventLogTable table;
	private static HBaseUserTable usertable;

	@BeforeClass
	public static void init() throws MasterNotRunningException, ZooKeeperConnectionException
	{
		manager = HBaseManager.getInstance("mulisch");
		table = new HBaseEventLogTable(manager);
		usertable = new HBaseUserTable(manager);
		assumeTrue(table.tableExists(CP));
		assumeTrue(usertable.tableExists(CP));
	}

	@Test
	@Ignore
	public void deleteData()
	{

		EventLogTable eventlog = new EventLogTable(table);
		List<UserView> views = eventlog.getViews(CP, RECOMMENDER, 10, null, null, 100, true);
		System.out.println("before: " + views.size());
		eventlog.deleteViews(CP, RECOMMENDER, 10);
		views = eventlog.getViews(CP, RECOMMENDER, 10, null, null, 100, true);
		System.out.println("after: " + views.size());
	}

	@Test
	@Ignore
	public void deleteRatings()
	{

		UserTable users = new UserTable(usertable);
		List<Rating> ratings = users.getRatings(CP, COLLECTION, 10, null, null, 200);
		System.out.println("before: " + ratings.size());
		users.deleteRatings(CP, COLLECTION, 10);
		ratings = users.getRatings(CP, COLLECTION, 10, null, null, 200);
		System.out.println("after: " + ratings.size());
		users.resetRatingCount(CP, 10);
	}
}
