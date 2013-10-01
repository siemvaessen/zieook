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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

/**
 * [purpose]
 * <p />
 * Project hadoop-freetaalserver<br />
 * WordCountWordsPerDocId.java created Sep 12, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class WordCountWordsPerDocId implements WritableComparable<WordCountWordsPerDocId>, Cloneable
{
	private long docId;
	private int wordCount;
	private int wordsPerDoc;

	private transient int hash;

	public WordCountWordsPerDocId()
	{
		// no-arg constructor
	}

	public WordCountWordsPerDocId(long docId, int wordCount, int wordsPerDoc)
	{
		this.docId = docId;
		this.wordCount = wordCount;
		this.wordsPerDoc = wordsPerDoc;
		hash = getHash();
	}

	public WordCountWordsPerDocId(WordCountWordsPerDocId other)
	{
		docId = other.docId;
		wordCount = other.wordCount;
		wordsPerDoc = other.wordsPerDoc;
		hash = other.hash;
	}

	public long getDocId()
	{
		return docId;
	}

	public int getWordCount()
	{
		return wordCount;
	}

	public int getWordsPerDoc()
	{
		return wordsPerDoc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		return new WordCountWordsPerDocId(docId, wordCount, wordsPerDoc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	@Override
	public void readFields(DataInput in) throws IOException
	{
		docId = in.readLong();
		wordCount = in.readInt();
		wordsPerDoc = in.readInt();
		hash = getHash();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	@Override
	public void write(DataOutput out) throws IOException
	{
		out.writeLong(docId);
		out.writeInt(wordCount);
		out.writeInt(wordsPerDoc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(WordCountWordsPerDocId o)
	{
		if (o == null)
		{
			throw new NullPointerException("cannot compare to null");
		}

		int r = (int) (docId - o.docId);
		if (r == 0)
		{
			r = wordCount - o.wordCount;
			if (r == 0)
			{
				r = wordsPerDoc - o.wordsPerDoc;
			}
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return hash;
	}

	private int getHash()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (docId ^ (docId >>> 32));
		result = prime * result + wordCount;
		result = prime * result + wordsPerDoc;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		WordCountWordsPerDocId other = (WordCountWordsPerDocId) obj;
		if (docId != other.docId)
		{
			return false;
		}
		if (wordCount != other.wordCount)
		{
			return false;
		}
		if (wordsPerDoc != other.wordsPerDoc)
		{
			return false;
		}
		return true;
	}

}
