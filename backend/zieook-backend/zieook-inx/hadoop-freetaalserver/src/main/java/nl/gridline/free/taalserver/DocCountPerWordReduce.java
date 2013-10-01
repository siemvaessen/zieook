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
import java.util.ArrayList;
import java.util.List;

import nl.gridline.free.taalserver.writable.TFIdfWritable;
import nl.gridline.free.taalserver.writable.WordCountWordsPerDocId;
import nl.gridline.free.taalserver.writable.WordDocId;
import nl.gridline.zieook.mapreduce.TaskConfig;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Round 4 - TFIdf calculation
 * <p />
 * Project hadoop-freetaalserver<br />
 * DocCountPerWordReduce.java created Sep 12, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class DocCountPerWordReduce extends Reducer<Text, WordCountWordsPerDocId, WordDocId, TFIdfWritable>
{

	private long totalDocs;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException
	{
		String docCountStr = context.getConfiguration().get(TaskConfig.TEXT_DOCUMENT_COUNT);
		if (docCountStr == null)
		{
			throw new IOException("The <" + TaskConfig.TEXT_DOCUMENT_COUNT
					+ "> is not defined, please set it in the job configurration");
		}
		totalDocs = Long.parseLong(docCountStr);
	};

	@Override
	protected void reduce(Text keyIn, Iterable<WordCountWordsPerDocId> values, Context context) throws IOException,
			InterruptedException
	{
		// collect values, we need to count them:
		List<WordCountWordsPerDocId> list = new ArrayList<WordCountWordsPerDocId>();
		for (WordCountWordsPerDocId value : values)
		{
			list.add(new WordCountWordsPerDocId(value));
		}
		final int docsPerWord = list.size();
		final String word = keyIn.toString();
		for (WordCountWordsPerDocId value : list)
		{
			final int wordCount = value.getWordCount();
			final int wordsPerDoc = value.getWordsPerDoc();
			final long docId = value.getDocId();
			// BAM! - TFIdf value written.
			context.write(new WordDocId(word, docId), new TFIdfWritable(wordCount, wordsPerDoc, docsPerWord, totalDocs));
		}
		context.progress();
	};
}
