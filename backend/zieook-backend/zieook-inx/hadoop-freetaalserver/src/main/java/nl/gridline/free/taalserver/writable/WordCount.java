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
 * word frequency tuple
 * <p />
 * Project hadoop-freetaalserver<br />
 * WordCount.java created Sep 12, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class WordCount implements WritableComparable<WordCount>, Cloneable
{

	private String word;
	private int count;
	private transient int hash;

	public WordCount(String word, int count)
	{
		this.word = word;
		this.count = count;
		hash = getHash();
	}

	public WordCount()
	{
		hash = getHash();
	}

	public String getWord()
	{
		return word;
	}

	public int getCount()
	{
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	@Override
	public void write(DataOutput out) throws IOException
	{
		out.writeInt(count);
		out.writeUTF(word);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	@Override
	public void readFields(DataInput in) throws IOException
	{
		count = in.readInt();
		word = in.readUTF();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return new WordCount(word, count);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(WordCount o)
	{
		if (o == null)
		{
			throw new NullPointerException("cannot compare to null");
		}
		int r = word.compareTo(o.word);
		if (r == 0)
		{
			return count - o.count;
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

	/**
	 * cache the hash
	 * @return
	 */
	private int getHash()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
		result = prime * result + ((word == null) ? 0 : word.hashCode());
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
		WordCount other = (WordCount) obj;
		if (word == null)
		{
			if (other.word != null)
			{
				return false;
			}
		}
		else if (!word.equals(other.word))
		{
			return false;
		}

		if (count != other.count)
		{
			return false;
		}

		return true;
	}

}
