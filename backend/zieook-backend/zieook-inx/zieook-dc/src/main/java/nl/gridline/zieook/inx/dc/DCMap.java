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
package nl.gridline.zieook.inx.dc;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;

import nl.gridline.zieook.mapreduce.HBasePUTFactory;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.model.CollectionItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.xml.sax.SAXException;

/**
 * Creates <tt>CollectionItem</tt> objects from <em>oai_dc</em> xml items.
 * <p />
 * Project zieook-dc<br />
 * DCMap.java created Sep 1, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class DCMap extends Mapper<LongWritable, Text, LongWritable, Put>
{
	private static final Log LOG = LogFactory.getLog(DCMap.class);

	private String cp;
	private String collection;

	@Override
	public void setup(Context context) throws IOException
	{
		Configuration config = context.getConfiguration();
		collection = config.get(TaskConfig.COLLECTION);
		cp = config.get(TaskConfig.CP);
		if (collection == null)
		{
			throw new IOException("collection not set, please set <" + TaskConfig.COLLECTION
					+ "> in the job configuration");
		}
		if (cp == null)
		{
			throw new IOException("content provider not set, please set <" + TaskConfig.CP + "> in the job configuration");
		}
	}

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
	{
		// process the input:
		String object = value.toString();
		long keyLng = key.get();

		if (object.isEmpty() || object.trim().isEmpty())
		{
			LOG.error("the key " + keyLng + " has no data");
			return;
		}
		else
		{
			CollectionItem item = null;
			try
			{
				item = DCRecordParseXML.getRecordFromXmlString(object);
			}
			catch (ParserConfigurationException e)
			{
				LOG.error("failed to parse key" + keyLng, e);
			}
			catch (SAXException e)
			{
				LOG.error("failed to parse key" + keyLng, e);
			}
			catch (ParseException e)
			{
				LOG.error("failed to parse key" + keyLng, e);
			}

			if (item != null)
			{
				item.setCp(cp);

				// create a HBase PUT object:
				Put put = HBasePUTFactory.getCollectionItemCompl(collection, item.getId(), object, item);

				// reuse the key:

				// write the result:
				context.write(key, put);
			}
		}

		context.progress();
	}
}
