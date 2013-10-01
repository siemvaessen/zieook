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
package nl.gridline.zieook.statistics;

import java.io.IOException;

import nl.gridline.zieook.statistics.io.CollectionSource;

import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.LongWritable;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * SourcesByCollectionReduce.java created 9 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class SourcesByCollectionReduce extends TableReducer<CollectionSource, LongWritable, CollectionSource>
{
	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{
	};

	@Override
	protected void reduce(CollectionSource key, java.lang.Iterable<LongWritable> values, Context context)
			throws IOException, InterruptedException
	{
	};
}
