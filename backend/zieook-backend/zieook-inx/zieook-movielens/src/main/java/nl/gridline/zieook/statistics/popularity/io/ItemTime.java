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
package nl.gridline.zieook.statistics.popularity.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * RatedItem.java created 8 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class ItemTime implements WritableComparable<ItemTime>, Cloneable
{

	private long time;

	private String collection;
	private long item;

	public ItemTime()
	{
		// no-arg constructor
	}

	public ItemTime(long item, String collection, long time)
	{
		this.time = time;
		this.item = item;
		this.collection = collection;
	}

	public ItemTime set(long item, String collection, long time)
	{
		this.item = item;
		this.collection = collection;
		this.time = time;
		return this;
	}

	public long getTime()
	{
		return time;
	}

	public long getItem()
	{
		return item;
	}

	public String getCollection()
	{
		return collection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		return new ItemTime(item, collection, time);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	@Override
	public void write(DataOutput out) throws IOException
	{
		out.writeLong(time);
		out.writeLong(item);
		out.writeUTF(collection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	@Override
	public void readFields(DataInput in) throws IOException
	{
		time = in.readLong();
		item = in.readLong();
		collection = in.readUTF();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ItemTime o)
	{
		if (o == null)
		{
			throw new NullPointerException("cannot compare to null");
		}

		if (time < o.time)
		{
			return -1;
		}
		if (time > o.time)
		{
			return 1;
		}

		if (item < o.item)
		{
			return -1;
		}
		if (item > o.item)
		{
			return 1;
		}

		// int result = collection.compareTo(o.collection);
		// if (result != 0)
		// {
		// return result;
		// }

		return collection.compareTo(o.collection);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collection == null) ? 0 : collection.hashCode());
		result = prime * result + (int) (item ^ (item >>> 32));
		result = prime * result + (int) (time ^ (time >>> 32));
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
		ItemTime other = (ItemTime) obj;
		if (collection == null)
		{
			if (other.collection != null)
			{
				return false;
			}
		}
		else if (!collection.equals(other.collection))
		{
			return false;
		}
		if (item != other.item)
		{
			return false;
		}
		if (time != other.time)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return new StringBuilder(collection).append('/').append(item).append(':').append(time).toString();
	}
}
