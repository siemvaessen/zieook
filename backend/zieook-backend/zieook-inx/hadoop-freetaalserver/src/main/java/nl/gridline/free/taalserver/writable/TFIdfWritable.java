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
 * TFIdfWritable.java created Sep 13, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class TFIdfWritable implements WritableComparable<TFIdfWritable>, Cloneable
{

	private int wordCount;
	private int wordsPerDoc;
	private int docsPerWord;
	private long totalDocs;

	private transient int hash;
	private transient double tfidf;

	public TFIdfWritable()
	{
		// no-arg constructor
	}

	public TFIdfWritable(int wordCount, int wordsPerDoc, int docsPerWord, long totalDocs)
	{
		this.wordCount = wordCount;
		this.wordsPerDoc = wordsPerDoc;
		this.docsPerWord = docsPerWord;
		this.totalDocs = totalDocs;
		hash = getHash();
		tfidf = calcTfIdf();
	}

	public int getWordCount()
	{
		return wordCount;
	}

	public int getWordsPerDoc()
	{
		return wordsPerDoc;
	}

	public int getDocsPerWord()
	{
		return docsPerWord;
	}

	public long getTotalDocs()
	{
		return totalDocs;
	}

	private double calcTfIdf()
	{
		return (wordCount / (double) wordsPerDoc) / Math.log(totalDocs / (double) docsPerWord);
	}

	public double getTdIdf()
	{
		return tfidf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TFIdfWritable o)
	{
		if (o == null)
		{
			throw new NullPointerException("cannot compare to null");
		}

		int r = docsPerWord - o.docsPerWord;
		if (r == 0)
		{
			r = (int) (totalDocs - o.totalDocs);
			if (r == 0)
			{
				r = wordCount - o.wordCount;
				if (r == 0)
				{
					r = wordsPerDoc - o.wordsPerDoc;
				}
			}
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	@Override
	public void write(DataOutput out) throws IOException
	{
		out.writeInt(wordCount);
		out.writeInt(wordsPerDoc);
		out.writeInt(docsPerWord);
		out.writeLong(totalDocs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	@Override
	public void readFields(DataInput in) throws IOException
	{
		wordCount = in.readInt();
		wordsPerDoc = in.readInt();
		docsPerWord = in.readInt();
		totalDocs = in.readLong();
		hash = getHash();
		tfidf = calcTfIdf();
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
		result = prime * result + docsPerWord;
		result = prime * result + (int) (totalDocs ^ (totalDocs >>> 32));
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
		TFIdfWritable other = (TFIdfWritable) obj;
		if (docsPerWord != other.docsPerWord)
		{
			return false;
		}
		if (totalDocs != other.totalDocs)
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
