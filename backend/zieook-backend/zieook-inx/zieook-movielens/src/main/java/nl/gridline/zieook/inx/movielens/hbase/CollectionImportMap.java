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
import java.util.Arrays;
import java.util.List;

import nl.gridline.zieook.mapreduce.HBasePUTFactory;
import nl.gridline.zieook.mapreduce.TaskConfig;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Reads collection data movielens file
 * <p />
 * Project zieook-movielens<br />
 * MovielensImportMap.java created 17 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionImportMap extends Mapper<LongWritable, Text, LongWritable, Put>
{

	private String collection;
	private String cp;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{
		collection = context.getConfiguration().get(TaskConfig.COLLECTION);
		cp = context.getConfiguration().get(TaskConfig.CP);

	};

	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, LongWritable, Put>.Context context)
			throws IOException, InterruptedException
	{
		String line = value.toString();

		String[] elements = line.split("::");
		// size of the array should look like: id::title::genre
		if (elements.length == 3)
		{
			String item = elements[0]; // id
			try
			{
				String title = elements[1]; // title
				List<String> keywords = Arrays.asList(elements[2].split("\\|")); // genres -> keywords & categories
				// categories -> keywords
				Put put = HBasePUTFactory.getCollectionItemCompl(cp, collection, item, title, keywords, keywords, line,
						null, null);
				context.write(key, put);
			}
			catch (NumberFormatException e)
			{
				context.setStatus("dropped a user: element[0]=" + item + " from '" + line + "'");
			}
		}
		else
		{
			context.setStatus("dropped a user: elements!=3 " + line);
		}
		context.progress();
	};
}
