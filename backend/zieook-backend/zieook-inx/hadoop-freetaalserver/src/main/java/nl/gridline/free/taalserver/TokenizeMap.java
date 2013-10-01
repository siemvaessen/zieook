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
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import nl.gridline.free.taalserver.writable.WordVector;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.model.CollectionItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;

/**
 * Round - 1 - Tokenize
 * <b>in</b>: table result<br />
 * <b>out</b> words vector, doc id.
 * <p />
 * Project hadoop-freetaalserver<br />
 * WordCountMap.java created Sep 6, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class TokenizeMap extends TableMapper<WordVector, LongWritable>
{
	private static final Log LOG = LogFactory.getLog(TokenizeMap.class);

	private static final byte[] COLLUMN_INTR = Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR);

	private boolean useDescription = true;
	private boolean useTitle = false;
	private boolean useKeywords = false;

	private final LongWritable outKey = new LongWritable();

	private BreakIterator splitter;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{

		String loc = context.getConfiguration().get(TaskConfig.TEXT_LANGUAGE);
		if (loc == null)
		{
			splitter = BreakIterator.getWordInstance(new Locale("nl_NL"));
		}
		else
		{
			splitter = BreakIterator.getWordInstance(new Locale(loc));
		}

		String useDescStr = context.getConfiguration().get(TaskConfig.TEXT_ANALYSIS_USE_DESCRIPTION);
		if (useDescStr != null && !useDescStr.isEmpty())
		{
			useDescription = Boolean.parseBoolean(useDescStr);
		}
		else
		{
			LOG.warn("parameter missing <" + TaskConfig.TEXT_ANALYSIS_USE_DESCRIPTION + "> defaulting to: true");
			useDescription = true;
		}

		String useTitleStr = context.getConfiguration().get(TaskConfig.TEXT_ANALYSIS_USE_TITLE);
		if (useTitleStr != null && !useTitleStr.isEmpty())
		{
			useTitle = Boolean.parseBoolean(useTitleStr);

		}
		else
		{
			LOG.warn("parameter missing <" + TaskConfig.TEXT_ANALYSIS_USE_TITLE + "> defaulting to: true");
			useTitle = true;
		}

		String useKeyWordsStr = context.getConfiguration().get(TaskConfig.TEXT_ANALYSIS_USE_KEYWORDS);
		if (useKeyWordsStr != null && !useKeyWordsStr.isEmpty())
		{
			useKeywords = Boolean.parseBoolean(useKeyWordsStr);
		}

	}

	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException,
			InterruptedException
	{
		CollectionItem item = new CollectionItem(value.getFamilyMap(COLLUMN_INTR));

		StringBuilder data = new StringBuilder();
		if (useTitle && item.getTitle() != null)
		{
			data.append(item.getTitle()).append(' ');
		}
		if (useDescription && item.getDescription() != null)
		{
			data.append(item.getDescription()).append(' ');
		}
		if (useKeywords)
		{
			data.append(getKeywords(item));
		}

		Long itemid = item.getId();

		if (itemid == null)
		{
			throw new IOException("item is invallid: " + item.toString());
		}

		outKey.set(itemid.longValue());

		// get title && description (if requested)

		// temp list for word tokens:
		List<String> result = new ArrayList<String>();

		// split the text & add to list:
		splitter.setText(data.toString());
		int start = splitter.first();
		for (int end = splitter.next(); end != BreakIterator.DONE; start = end, end = splitter.next())
		{
			result.add(data.substring(start, end));
		}
		// LOG.info("text: " + data);
		LOG.info("vector size: " + result.size() + " text length: " + data.length());
		if (result.size() > 0)
		{
			// write new word vector to the output.
			context.write(new WordVector(result), outKey);
			context.progress();
		}

	}

	private String getKeywords(CollectionItem item)
	{
		StringBuilder result = new StringBuilder();

		Set<String> set = new HashSet<String>();
		List<String> list = item.getKeywords();
		if (list != null)
		{
			set.addAll(list);
		}
		list = item.getCategory();
		if (list != null)
		{
			set.addAll(list);
		}

		for (String i : list)
		{
			result.append(i).append('\n');
		}

		return result.toString().trim();
	}
}
