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
package nl.gridline.zieook.inx.czp;

import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;

/**
 * Very simple reducer step, that writes all values directly to the context, the final record writer will write de data
 * to the database.
 * <p />
 * Project zieook-czp<br />
 * CZPOaiRedcue.java created 4 jan. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CZPReduce extends TableReducer<LongWritable, Writable, LongWritable>
{

	@Override
	protected void reduce(LongWritable key, Iterable<Writable> values, Context context) throws java.io.IOException,
			InterruptedException
	{
		for (Writable putOrDelete : values)
		{
			context.write(key, putOrDelete);
		}
		context.progress();
	};
}
