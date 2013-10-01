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
package nl.gridline.zieook.statistics.popularity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.mahout.common.iterator.sequencefile.PathFilters;

import com.google.common.base.Charsets;

/**
 * Count the total number of ratings for this time unit
 * <p />
 * Project zieook-movielens<br />
 * PopularityCount.java created 12 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class PopularityStatistics
{
	private static final Log LOG = LogFactory.getLog(PopularityStatistics.class);

	public static final String TYPES = PopularityStatistics.class.getName() + ".types";
	public static final String TYPES_COUNT = PopularityStatistics.class.getName() + ".types-count";
	public static final String NOW = PopularityMap.class.getName() + ".now";

	/**
	 * parse a String into a map of long,string:
	 * @param types
	 * @return
	 */
	public static SortedMap<Long, String> getTypes(String types)
	{
		SortedMap<Long, String> result = new TreeMap<Long, String>();
		String[] typeArr = types.trim().split(";");
		for (String t : typeArr)
		{
			String[] kv = t.split(",");
			if (kv.length != 2)
			{
				LOG.error("cannot read: '" + t + "' into key,value");
				continue;
			}
			try
			{
				// read the long value, multiply by 1000 - the settings are stored in seconds, we need milliseconds.
				long v = Long.parseLong(kv[1]);
				result.put(v * 1000, kv[0]);
			}
			catch (NumberFormatException e)
			{
				LOG.error("cannot read: '" + kv[1] + "' into long value from " + t);
			}
		}
		LOG.info("read " + result.size() + " types from input types");
		return result;
	}

	public static Map<Long, Long> getTypeCount(String types)
	{
		Map<Long, Long> result = new HashMap<Long, Long>();
		String[] typeArr = types.trim().split("\n");
		for (String t : typeArr)
		{
			String kv[] = t.split("\t");
			if (kv.length != 2)
			{
				LOG.error("cannot read: '" + t + "' into key,value");
				continue;
			}
			long k = -1, v = -1;
			try
			{
				k = Long.parseLong(kv[0]);
			}
			catch (NumberFormatException e)
			{
				LOG.error("cannot read: '" + kv[0] + "' into long value from " + t);
			}
			try
			{
				v = Long.parseLong(kv[1]);
			}
			catch (NumberFormatException e)
			{
				LOG.error("cannot read: '" + kv[1] + "' into long value from " + t);
			}

			if (k != -1 && v != -1)
			{
				result.put(k, v);
			}
		}
		LOG.debug("read " + result.size() + " from given type counts");
		return result;
	}

	public static String readData(Path path, Configuration conf) throws IOException
	{
		FileSystem fs = path.getFileSystem(conf);
		Path outputFile = fs.listStatus(path, PathFilters.partFilter())[0].getPath();
		InputStream in = null;
		try
		{
			in = fs.open(outputFile);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			IOUtils.copyBytes(in, out, conf);
			return new String(out.toByteArray(), Charsets.UTF_8).trim();
		}
		finally
		{
			IOUtils.closeStream(in);
		}
	}

}
