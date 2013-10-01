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

import java.util.HashSet;
import java.util.Set;

import nl.gridline.zieook.mapreduce.TaskConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * FilterDataReduce.java created 5 apr. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class FilterDataReduce extends Reducer<Text, Text, Text, NullWritable>
{
	private static final Log LOG = LogFactory.getLog(FilterDataReduce.class);

	private final Text outKey = new Text();
	private boolean shouldFilter;

	@Override
	protected void setup(Context context)
	{
		shouldFilter = context.getConfiguration().getBoolean(TaskConfig.SHOULD_FILTER, false);
	}

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws java.io.IOException,
			InterruptedException
	{
		// key == itemID
		// value == [allowed,<userID,preference>]
		// output key = userID,itemID,preference

		// put values in a list:
		Set<String> valueSet = new HashSet<String>();
		String itemId = key.toString();
		for (Text s : values)
		{
			valueSet.add(s.toString());
		}
		if (!shouldFilter || valueSet.contains("allowed"))
		{
			// write all but 'allowed'
			for (String s : valueSet)
			{
				if (!"allowed".equals(s))
				{
					String[] data = s.split(",");
					if (data.length == 2)
					{
						outKey.set(data[0] + "," + itemId + "," + data[1]);
					}
					else if (data.length == 1)
					{
						outKey.set(data[0] + "," + itemId);
					}
					else
					{
						LOG.error("Failed to read item, this is probably an error: value = '" + s + "' key = '" + itemId
								+ "'");
					}
					context.write(outKey, NullWritable.get());
				}
			}
		}
	};
}
