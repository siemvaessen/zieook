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
package nl.gridline.zieook.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class that generates random ratings and write it to a file.
 * <p />
 * Project zieook-movielens<br />
 * GenerateRatings.java created 7 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:gerlof@gridline.nl">Gerlof</a>
 * @version $Revision$, $Date$
 */
public class GenerateRatings
{
	private static final Logger LOG = LoggerFactory.getLogger(GenerateRatings.class);

	private final String filename; // = "/home/gerlof/ratingsgen.dat";
	private final int userCount;// = 4;
	private final int itemCount;// = 6;

	public GenerateRatings(String filename, int usercount, int itemcount)
	{
		this.filename = filename;
		userCount = usercount;
		itemCount = itemcount;
	}

	public void generate()
	{
		Random rn = new Random();
		LOG.info("Start writing random ratings to: {}", filename);
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			try
			{
				for (int i = 0; i < 20; i++)
				{

					int user = rn.nextInt(userCount);
					int item = rn.nextInt(itemCount);
					int value = rn.nextInt(5) + 1;
					out.write(user + "," + item + "," + value + "\n");
				}
			}
			finally
			{
				out.close();
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to write data", e);
		}
		LOG.info("Done; stored data in: {}", filename);
	}

	/**
	 * Main method. Runs the application.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String... args) throws Exception
	{

	}

}
