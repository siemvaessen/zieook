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
package nl.gridline.zieook.client.tools;

import java.io.Console;

import uk.co.flamingpenguin.jewel.cli.CliFactory;

/**
 * Some interactive console helper methods
 * <p />
 * Project zieook-client<br />
 * AbstractCliExecutor.java created 16 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public abstract class AbstractCliExecutor implements CliExecutor
{

	/**
	 * get the help message for an option class
	 * @param clazz
	 * @return
	 */
	public static String getHelp(Class<?> clazz)
	{
		return CliFactory.createCli(clazz).getHelpMessage();
	}

	protected void printHelpAndExit(String msg, Class<?> clazz)
	{
		System.out.println(msg);
		System.out.println(CliFactory.createCli(clazz).getHelpMessage());
		System.exit(-1);

	}

	public boolean confirmYesNo(String message)
	{
		Console c = getConsole();
		String msg = message + " [Y/n]?";

		int result = isYesNo(c.readLine(msg));
		do
		{
			result = isYesNo(c.readLine(msg));
		}
		while (result == -1);

		return result == 1;
	}

	public boolean confirmReallyYesNo(String message)
	{
		Console c = getConsole();
		int result = isYesNo(c.readLine(message + " [Yes/no]?"));
		do
		{
			result = isReallyYesNo(message + " type [Yes/no]?");
		}
		while (result == -1);

		return result == 1;
	}

	private Console getConsole()
	{
		Console c = System.console();
		if (c == null)
		{
			System.err.println("No console.");
			System.exit(1);
		}
		return c;
	}

	private int isReallyYesNo(String c)
	{
		if (c == null)
		{
			return -1;
		}

		if ("yes".equals(c))
		{
			return 1;
		}
		if ("no".equals(c))
		{
			return 0;
		}
		return -1;
	}

	/**
	 * return 1 on yes,y return 0 on
	 * @param confirm
	 * @return
	 */
	private int isYesNo(String confirm)
	{
		if (confirm == null)
		{
			return -1;
		}

		String c = confirm.toLowerCase();
		if ("yes".equals(c) || "y".equals(c))
		{
			return 1;
		}
		if ("no".equals(c) || "n".equals(c))
		{
			return 0;
		}
		return -1;
	}
}
