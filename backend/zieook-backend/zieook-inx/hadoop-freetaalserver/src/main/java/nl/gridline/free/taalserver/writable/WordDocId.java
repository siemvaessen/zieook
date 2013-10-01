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
 * word and document id tuple
 * <p />
 * Project hadoop-freetaalserver<br />
 * WordDocId.java created Sep 12, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class WordDocId implements WritableComparable<WordDocId>, Cloneable
{

	private String word;
	private long docid;
	private transient int hash;

	public WordDocId(String word, long docid)
	{
		this.word = word;
		this.docid = docid;
		hash = getHash();
	}

	public WordDocId()
	{
		hash = getHash();
	}

	public String getWord()
	{
		return word;
	}

	public long getDocId()
	{
		return docid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	@Override
	public void write(DataOutput out) throws IOException
	{
		out.writeLong(docid);
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
		docid = in.readLong();
		word = in.readUTF();
		hash = getHash();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		return new WordDocId(word, docid);
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
		result = prime * result + (int) (docid ^ (docid >>> 32));
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
		WordDocId other = (WordDocId) obj;
		if (docid != other.docid)
		{
			return false;
		}
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
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(WordDocId o)
	{
		if (o == null)
		{
			throw new NullPointerException("cannot compare to null object");
		}

		long r = (docid - o.docid);
		if (r == 0)
		{
			return word.compareTo(o.word);
		}
		return r < 0 ? -1 : 1;
	}
}
