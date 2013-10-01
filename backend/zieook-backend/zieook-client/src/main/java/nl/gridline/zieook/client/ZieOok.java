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
package nl.gridline.zieook.client;

import java.io.IOException;

import nl.gridline.zieook.client.cli.HBaseCLI;
import nl.gridline.zieook.client.tools.AbstractCliExecutor;
import nl.gridline.zieook.client.tools.HBaseOperations;
import nl.gridline.zieook.client.tools.ProcessUpdates;
import nl.gridline.zieook.client.tools.WorkflowManager;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;

/**
 * ZieOok commandline tools - completely self contained does not need a running server. Only a configuration file.
 * <p />
 * Project zieook-commons<br />
 * Tools.java created 25 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class ZieOok
{
	static
	{
		// set project name to zieook
		System.setProperty("gridline.project.name", "zieook");
	}

	public ZieOok() throws IOException
	{
	}

	/**
	 * @param args
	 * @throws ZooKeeperConnectionException
	 * @throws MasterNotRunningException
	 * @throws IOException
	 * @throws ArgumentValidationException
	 */
	public static void main(String[] args) throws MasterNotRunningException, ZooKeeperConnectionException, IOException
	{

		if (args.length == 0)
		{
			System.out.println("Usage: ZieOok [hbase|workflow|data|update] OPTIONS");
			System.exit(-1);
		}

		String process = args[0];
		String[] options = new String[args.length - 1];
		System.arraycopy(args, 1, options, 0, args.length - 1);

		System.out.println("process: " + process);

		if ("hbase".equals(process))
		{
			try
			{
				new HBaseOperations().processRequest(options);
			}
			catch (ArgumentValidationException e)
			{
				System.out.println(AbstractCliExecutor.getHelp(HBaseCLI.class));
				System.exit(-1);
			}
		}
		else if ("workflow".equals(process))
		{
			try
			{
				new WorkflowManager().processRequest(options);
			}
			catch (ArgumentValidationException e)
			{
				System.out.println("no help available yet");
				System.exit(-1);
			}
		}
		else if ("update".equals(process))
		{
			try
			{
				new ProcessUpdates().processRequest(options);
			}
			catch (ArgumentValidationException e)
			{
				System.out.println("no help available yet");
				System.exit(-1);
			}
		}
		// else if ("data".equals(process))
		// {
		// try
		// {
		// tools.data(options);
		// }
		// catch (ArgumentValidationException e)
		// {
		// System.out.println("no help available yet");
		// System.exit(-1);
		// }
		// }
		else
		{
			System.out.println("Usage: ZieOok [hbase|workflow|data] OPTIONS");
			System.exit(-1);
		}
	}
}
