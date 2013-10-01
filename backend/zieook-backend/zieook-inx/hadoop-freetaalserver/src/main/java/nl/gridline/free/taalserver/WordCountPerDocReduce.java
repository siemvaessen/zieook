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

import nl.gridline.free.taalserver.writable.WordCount;
import nl.gridline.free.taalserver.writable.WordCountWordsPerDoc;
import nl.gridline.free.taalserver.writable.WordDocId;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Round - 3 word count per document
 * <p />
 * Project hadoop-freetaalserver<br />
 * WordCountPerDocReduce.java created Sep 12, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class WordCountPerDocReduce extends Reducer<LongWritable, WordCount, WordDocId, WordCountWordsPerDoc>
{
	@Override
	protected void reduce(LongWritable keyIn, Iterable<WordCount> values, Context context) throws IOException,
			InterruptedException
	{
		// IN: docid => ((word1,wc1),(word2,wc2)...)

		// OUT: word,docid => wordCount, wordsPerDoc

		// create local list:
		List<WordCount> wc = new ArrayList<WordCount>();
		for (WordCount v : values)
		{
			wc.add(new WordCount(v.getWord(), v.getCount()));
		}

		final int wordsPerDoc = wc.size();
		final long docId = keyIn.get();

		for (WordCount v : wc)
		{
			context.write(new WordDocId(v.getWord(), docId), new WordCountWordsPerDoc(v.getCount(), wordsPerDoc));
		}
		context.progress();
	};
}
