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


package nl.gridline.zieook.data.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-data<br />
 * RestTools.java created 12 jan. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class RestTools
{
	private static final Logger LOG = LoggerFactory.getLogger(RestTools.class);

	public static List<Long> getUsers(String users)
	{
		List<Long> result = new ArrayList<Long>();
		if (users.startsWith("[") && users.endsWith("]"))
		{
			JsonElement json = new JsonParser().parse(users);
			Iterator<JsonElement> i = json.getAsJsonArray().iterator();
			while (i.hasNext())
			{
				JsonElement el = i.next();
				long u = el.getAsLong();
				result.add(u);
			}
		}
		else
		{
			String[] elems = users.split(",");
			for (String el : elems)
			{
				try
				{
					long u = Long.parseLong(el);
					result.add(u);
				}
				catch (NumberFormatException e)
				{
					LOG.error("failed to parse element into long from input: {}", el);
				}
			}
		}
		LOG.debug("found {} users in '{}' request", result.size(), users);
		return result;
	}

	/**
	 * Parse string as: {@code cp}/{@code recommender},{@code cp}/{@code recommender},... or "{@code cp}/
	 * {@code recommender}" in a JSON array into a list of {@code String[2]}
	 * @param recommenders the <em>cp/recommender</em> string
	 * @return a list of 2-arrays
	 */
	public static List<String[]> getRecommenders(String recommenders)
	{
		List<String[]> result = new ArrayList<String[]>();
		if (recommenders.startsWith("[") && recommenders.endsWith("]"))
		{

			// parse as a safe JSON array:

			JsonElement json = new JsonParser().parse(recommenders);
			Iterator<JsonElement> i = json.getAsJsonArray().iterator();
			while (i.hasNext())
			{
				JsonElement el = i.next();
				String s = el.getAsString();
				String[] cprec = s.split("/");
				if (cprec.length == 2)
				{
					result.add(cprec);
				}
				else
				{
					LOG.error("dropping input element: '{}', should be cp/recommender ", s);
				}
			}
		}
		else
		{
			// parse as a simple comma separated list

			String[] elems = recommenders.split(",");
			for (String el : elems)
			{
				String[] cprec = el.split("/");
				if (cprec.length == 2)
				{
					result.add(cprec);
				}
				else
				{
					LOG.error("dropping input element: '{}', should be cp/recommender ", el);
				}
			}
		}
		return result;
	}
}
