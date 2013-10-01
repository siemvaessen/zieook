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


package nl.gridline.zieook.tasks.quality;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * TranformData.java created 7 feb. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public final class TransformData
{
	public static void transformData(File ratings, File output) throws IOException
	{
		// input a movielens file: UserID::MovieID::Rating::Timestamp
		// into UserID,MovieID,Rating

		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		BufferedReader reader = new BufferedReader(new FileReader(ratings));
		try
		{
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				String[] elems = line.split("::");
				writer.write(elems[0]);
				writer.write(",");
				writer.write(elems[1]);
				writer.write(",");
				writer.write(elems[2]);
				writer.write("\n");
			}
		}
		finally
		{
			writer.close();
			reader.close();
		}
	}
}
