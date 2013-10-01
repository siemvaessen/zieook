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
package nl.gridline.zieook.inx.movielens.hbase;

import java.io.IOException;

import nl.gridline.zieook.mapreduce.HBasePUTFactory;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Reads users from a user file
 * <p />
 * Project zieook-movielens<br />
 * MovielensUsersImportMap.java created 18 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class UsersImportMap extends Mapper<LongWritable, Text, LongWritable, Put>
{

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
	{
		String line = value.toString();

		// This is how a user line looks like:
		// UserID::Gender::Age::Occupation::Zip-code
		// 1:: F:: 1:: 10:: 48067

		String[] elements = line.split("::");
		// size of the array should look like: id::title::genre
		if (elements.length == 5)
		{
			String userid = elements[0]; // id
			try
			{
				Put put = HBasePUTFactory.getUser(userid, line);
				context.write(key, put);
			}
			catch (NumberFormatException e)
			{
				// failed to parse id -> ignore item
				context.setStatus("dropped a user: element[0]=" + userid + " from '" + line + "'");
			}
		}
		else
		{
			context.setStatus("dropped a user: elements!=5 " + line);
		}
		context.progress();
	};
}
