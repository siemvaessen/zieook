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
package nl.gridline.zieook.inx.movielens.items;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * FilterDataMap.java created 5 apr. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class FilterDataMap extends Mapper<Text, Text, Text, Text>
{
	private static final Log LOG = LogFactory.getLog(FilterDataMap.class);

	private final Text outKey = new Text();
	private final Text outValue = new Text();

	@Override
	protected void map(Text key, Text value, Context context) throws IOException, InterruptedException
	{
		//
		// userID,itemID,preference
		String[] data = key.toString().split(",");
		if (data.length == 3)
		{
			outKey.set(data[1]);
			outValue.set(data[0] + "," + data[2]);
			// item, user, preference
			context.write(outKey, outValue);
		}
		else if (data.length == 2)
		{
			outKey.set(data[1]);
			outValue.set(data[0]);
			// item, user (no preference)
			context.write(outKey, outValue);
		}
		else if (data.length == 1)
		{
			outKey.set(data[0]); // item id
			outValue.set("allowed");
			// item, "allowed"
			context.write(outKey, outValue);
		}
		else
		{
			LOG.error("dropping: " + key);
		}

	};
}
