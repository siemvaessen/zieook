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

import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.Writable;

/**
 * Writes UserRating to the database
 * <p />
 * Project zieook-movielens<br />
 * RatingImportReduce.java created 21 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class RatingImportReduce extends TableReducer<Writable, Writable, Writable>
{

	private long numRecords;

	@Override
	public void setup(Context context)
	{
		numRecords = 0;
	}

	@Override
	public void reduce(Writable key, Iterable<Writable> values, Context context) throws IOException,
			InterruptedException
	{
		for (Writable put : values)
		{
			numRecords++;

			if (numRecords % 10000 == 0)
			{
				context.setStatus(String.valueOf(numRecords) + " records have been loaded.");
				context.progress();
			}
			context.write(key, put);
		}

		context.setStatus(String.valueOf(numRecords) + " records have been loaded!");
	}
}
