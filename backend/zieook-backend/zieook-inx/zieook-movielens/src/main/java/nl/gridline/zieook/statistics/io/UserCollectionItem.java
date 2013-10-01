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
package nl.gridline.zieook.statistics.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * UserRecommenderItem.java created 2 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class UserCollectionItem implements WritableComparable<UserCollectionItem>, Cloneable
{

	private long user;
	private long item;
	private String collection;

	public UserCollectionItem()
	{

	}

	/**
	 * @param user
	 * @param item
	 * @param collection
	 */
	public UserCollectionItem(long user, long item, String collection)
	{
		this.user = user;
		this.item = item;
		this.collection = collection;
	}

	/**
	 * assign user, item recommender
	 * @param user
	 * @param item
	 * @param collection
	 */
	public UserCollectionItem setData(long user, long item, String collection)
	{
		this.user = user;
		this.item = item;
		this.collection = collection;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		return new UserCollectionItem(user, item, collection);
	}

	public long getUser()
	{
		return user;
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
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	@Override
	public void write(DataOutput out) throws IOException
	{
		out.writeLong(user);
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
		user = in.readLong();
		item = in.readLong();
		collection = in.readUTF();
	}

	@Override
	public String toString()
	{
		return new StringBuilder().append('<').append(user).append(',').append(collection).append(',').append(item)
				.append('>').toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(UserCollectionItem o)
	{
		if (o == null)
		{
			throw new NullPointerException("cannot compare to null");
		}

		long r = user - o.user;
		if (r == 0)
		{
			r = item - o.item;
		}
		if (r == 0)
		{
			return collection.compareTo(o.collection);
		}
		if (r < 0)
		{
			return -1;
		}
		else
		{
			return 1;
		}

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
		result = prime * result + (int) (item ^ (item >>> 32));
		result = prime * result + ((collection == null) ? 0 : collection.hashCode());
		result = prime * result + (int) (user ^ (user >>> 32));
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
		UserCollectionItem other = (UserCollectionItem) obj;
		if (item != other.item)
		{
			return false;
		}
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
		if (user != other.user)
		{
			return false;
		}
		return true;
	}

}
