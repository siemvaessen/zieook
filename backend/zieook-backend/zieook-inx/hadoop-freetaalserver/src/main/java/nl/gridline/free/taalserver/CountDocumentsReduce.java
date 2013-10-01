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
package nl.gridline.free.taalserver;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.mahout.math.VarIntWritable;
import org.apache.mahout.math.VarLongWritable;

/**
 * Count the documents reduce
 * <p />
 * Project hadoop-taalserver<br />
 * CountDocumentsReduce.java created 21 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision: 1341 $, $Date: 2011-03-23 12:16:55 +0100 (wo, 23 mrt 2011) $
 */
public class CountDocumentsReduce extends Reducer<LongWritable, VarLongWritable, VarIntWritable, NullWritable>
{

	@Override
	protected void reduce(LongWritable key, java.lang.Iterable<VarLongWritable> values, Context context)
			throws IOException, InterruptedException
	{
		int numberOfDocuments = 0;
		Iterator<VarLongWritable> i = values.iterator();
		while (i.hasNext()) // VarLongWritable writable : values)
		{
			numberOfDocuments++;
			i.next();
		}
		context.write(new VarIntWritable(numberOfDocuments), NullWritable.get());
		context.progress();
	};
}
