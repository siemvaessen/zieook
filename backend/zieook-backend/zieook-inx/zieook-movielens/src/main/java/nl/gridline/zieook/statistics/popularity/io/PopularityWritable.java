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
 * Popularity.java created 9 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class PopularityWritable implements WritableComparable<PopularityWritable>, Cloneable
{

	private long count;
	private double percentage;

	public PopularityWritable()
	{

	}

	public PopularityWritable(long count, double percentage)
	{
		this.count = count;
		this.percentage = percentage;
	}

	public PopularityWritable set(long count, double percentage)
	{
		this.count = count;
		this.percentage = percentage;
		return this;
	}

	public long getCount()
	{
		return count;
	}

	public double getPercentage()
	{
		return percentage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	@Override
	public void write(DataOutput out) throws IOException
	{
		out.writeLong(count);
		out.writeDouble(percentage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	@Override
	public void readFields(DataInput in) throws IOException
	{
		count = in.readLong();
		percentage = in.readDouble();
	}

	@Override
	public String toString()
	{
		return count + " " + percentage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PopularityWritable o)
	{
		if (o == null)
		{
			throw new NullPointerException("cannot compare to null");
		}
		if (percentage > o.percentage)
		{
			return 1;
		}
		if (percentage < o.percentage)
		{
			return -1;
		}
		if (count > o.count)
		{
			return 1;
		}
		if (count < o.count)
		{
			return -1;
		}
		return 0;
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
		result = prime * result + (int) (count ^ (count >>> 32));
		long temp;
		temp = Double.doubleToLongBits(percentage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		PopularityWritable other = (PopularityWritable) obj;
		if (count != other.count)
		{
			return false;
		}
		if (Double.doubleToLongBits(percentage) != Double.doubleToLongBits(other.percentage))
		{
			return false;
		}
		return true;
	}

}
