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

import java.io.IOException;
import java.text.ParseException;

import javax.xml.bind.JAXBException;

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

/**
 * Requires a configured content provider: <tt>zieook.czpoai.map.cp</tt> and a collection
 * <tt>zieook.czpoai.map.collection</tt>
 * <p />
 * TODO ASAP - process delete statements(!)
 * <p />
 * Project zieook-czp<br />
 * CZPOaiMapReduce.java created 4 jan. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CZPMap extends Mapper<LongWritable, Text, LongWritable, Put>
{
	private static final Log LOG = LogFactory.getLog(CZPMap.class);

	private String cp;
	private String collection;
	private String language;

	private CzpRecordParseJAXB czpParser;

	@Override
	public void setup(Context context) throws IOException
	{
		Configuration config = context.getConfiguration();
		collection = config.get(TaskConfig.COLLECTION);
		cp = config.get(TaskConfig.CP);
		language = config.get(TaskConfig.COLLECTION_LANGUAGE, "nl");
		if (collection == null)
		{
			throw new IOException("collection not set, please set <" + TaskConfig.COLLECTION
					+ "> in the job configuration");
		}
		if (cp == null)
		{
			throw new IOException("content provider not set, please set <" + TaskConfig.CP + "> in the job configuration");
		}

		try
		{
			czpParser = new CzpRecordParseJAXB(cp, language);
		}
		catch (JAXBException e)
		{
			throw new IOException("failed to initialize JAXB parser", e);
		}

	}

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
	{
		// process the input:
		String object = value.toString();

		LOG.info("line #" + key.get());

		CollectionItem item = null;
		try
		{
			item = czpParser.getCZPfromXML(object);
		}
		catch (JAXBException e)
		{
			LOG.error("failed to parse collection item: " + key.toString(), e);
			LOG.error("object:\n" + object + "\n");
		}
		catch (ParseException e)
		{
			LOG.error("failed to parse key" + key.toString(), e);
			LOG.error("object:\n" + object + "\n");
		}

		LOG.info("writing: " + item.getId());
		if (item != null)
		{
			item.setCp(cp);

			Long itemid = item.getId();
			if (itemid != null)
			{
				// create a HBase PUT object:
				Put put = HBasePUTFactory.getCollectionItemCompl(collection, itemid.longValue(), object, item);
				// reuse the key:

				// write the result:
				context.write(key, put);
			}
			else
			{
				LOG.error("collection item has no identifier: " + item.toString() + "' ");
			}

		}
		context.progress();
	}
}
