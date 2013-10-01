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

import nl.gridline.free.taalserver.writable.WordCount;
import nl.gridline.free.taalserver.writable.WordDocId;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Round - 3 word count per document
 * <p />
 * Project hadoop-freetaalserver<br />
 * WordCountPerDocMap.java created Sep 12, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class WordCountPerDocMap extends Mapper<WordDocId, IntWritable, LongWritable, WordCount>
{
	@Override
	protected void map(WordDocId key, IntWritable value, Context context) throws IOException, InterruptedException
	{
		long docid = key.getDocId();
		String word = key.getWord();
		int count = value.get();
		context.write(new LongWritable(docid), new WordCount(word, count));
		context.progress();
	};
}
