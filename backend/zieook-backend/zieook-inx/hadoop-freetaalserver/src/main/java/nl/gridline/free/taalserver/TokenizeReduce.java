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
import java.util.List;

import nl.gridline.free.taalserver.writable.WordVector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Round - 1 Tokenize
 * <b>in</b> word vector
 * <p />
 * Project hadoop-freetaalserver<br />
 * WordSplitReduce.java created Sep 9, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class TokenizeReduce extends Reducer<WordVector, LongWritable, LongWritable, Text>
{

	private final Log LOG = LogFactory.getLog(TokenizeReduce.class);

	private final Text word = new Text();

	@Override
	protected void reduce(WordVector inKey, Iterable<LongWritable> values, Context context) throws IOException,
			InterruptedException
	{
		for (LongWritable value : values)
		{
			final long docId = value.get();
			List<String> words = inKey.getAsList();
			LOG.info("words: " + words.size());
			LongWritable outKey = new LongWritable(docId);
			for (String w : words)
			{
				word.set(w);
				context.write(outKey, word);
			}
		}
		context.progress();
	};
}
