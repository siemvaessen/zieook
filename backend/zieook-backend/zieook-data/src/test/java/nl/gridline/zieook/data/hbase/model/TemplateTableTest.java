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
package nl.gridline.zieook.data.hbase.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.TemplateTable;
import nl.gridline.zieook.workflow.model.Template;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * TemplateTableTest.java created 23 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class TemplateTableTest
{
	private static final Logger logger = LoggerFactory.getLogger(TemplateTableTest.class);

	private HBaseManager manager;
	private HBaseConfigurationTable table;

	@Before
	public void setUp() throws Exception
	{
		manager = HBaseManager.getInstance("mulisch");

		table = new HBaseConfigurationTable(manager);
		if (!table.tableExists(null))
		{
			assertTrue(table.create());
			logger.info("create table: {}", table.getTableName(null));
		}
		else
		{
			logger.info("table exists: {}", table.getTableName(null));
		}

	}

	@After
	public void tearDown() throws Exception
	{
		// clean up:
		// HBaseConfigurationTable table = new HBaseConfigurationTable(manager);
		// if (table.tableExists(null))
		// {
		// table.drop(null);
		// logger.info("table drop: {}", table.getTableName(null));
		// }
	}

	@Test
	public void putgetTest()
	{
		TemplateTable template = new TemplateTable(table);

		Template data = Helper.getTemplate();

		assertTrue(data != null);

		// put data:
		template.putTemplate(data);
		// get data:
		Template newdata = template.getTemplate(data.getName());

		assertEquals(data, newdata);

	}
}
