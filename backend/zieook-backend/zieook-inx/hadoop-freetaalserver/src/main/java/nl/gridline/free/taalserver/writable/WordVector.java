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
package nl.gridline.free.taalserver.writable;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

/**
 * array of words per document
 * <p />
 * Project hadoop-freetaalserver<br />
 * WordVector.java created Sep 6, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class WordVector extends ArrayWritable implements WritableComparable<WordVector>
{
	private static final Log LOG = LogFactory.getLog(WordVector.class);

	/**
	 * a vector of words in UTF 8
	 * @param words
	 */
	public WordVector(Text[] words)
	{
		super(Text.class);
		set(words);
	}

	public WordVector()
	{
		super(Text.class);
	}

	public WordVector(List<String> words)
	{
		super(Text.class);
		List<Text> text = new ArrayList<Text>(words.size());
		for (String s : words)
		{
			text.add(new Text(s));
		}
		set(text.toArray(new Text[words.size()]));
	}

	public List<String> getAsList()
	{
		Writable[] data = get();
		List<String> result = new ArrayList<String>(data.length);
		for (Writable t : data)
		{
			if (t instanceof Text)
			{
				result.add(((Text) t).toString());
			}
			else
			{
				LOG.error("conversion of text failed");
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(WordVector o)
	{
		if (o == null)
		{
			throw new NullPointerException("cannot compare to null");
		}
		int result = get().length - o.get().length;
		if (result == 0)
		{
			for (int i = 0; i < get().length; i++)
			{
				result = get()[i].toString().compareTo(o.get()[i].toString());
				if (result != 0)
				{
					return result;
				}
			}
		}

		return result;
	}
}
