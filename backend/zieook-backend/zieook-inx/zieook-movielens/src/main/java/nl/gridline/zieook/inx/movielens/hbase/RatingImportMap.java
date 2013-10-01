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
import nl.gridline.zieook.mapreduce.TaskConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Reads ratings from a movielens rating file, line by line, creates a UserRating and writes it to a HBase put object
 * <p />
 * Project zieook-movielens<br />
 * RatingImportMap.java created 21 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class RatingImportMap extends Mapper<LongWritable, Text, LongWritable, Put>
{

	@SuppressWarnings("unused")
	private static final Log LOG = LogFactory.getLog(RatingImportMap.class);

	private String collection;

	// private String cp;

	@Override
	public void setup(Context context)
	{
		collection = context.getConfiguration().get(TaskConfig.COLLECTION);
		// cp = context.getConfiguration().get(TaskConfig.CP);
	}

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
	{
		String line = value.toString();

		String[] elements = line.split("::");
		if (elements.length == 4)
		{
			String user = elements[0];
			String item = elements[1];
			String rating = elements[2];
			String date = elements[3];
			Put put = HBasePUTFactory.getRatingPut(collection, user, item, rating, date, "movielens");
			context.write(key, put);
		}
		else
		{
			context.setStatus("dropped a ratings: elements!=4 " + line);
		}
	}
}
