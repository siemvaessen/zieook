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
package nl.gridline.zieook.inx.movielens;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.mahout.math.Varint;

/**
 * an entry in a row vector stored together with a precomputed weight of the row
 * <p />
 * Project zieook-movielens<br />
 * WeightedOccurrence.java created 21 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class WeightedOccurrence implements Writable, Cloneable
{

	private int row;
	private double value;
	private double weight;

	WeightedOccurrence()
	{
	}

	public WeightedOccurrence(int row, double value, double weight)
	{
		this.row = row;
		this.value = value;
		this.weight = weight;
	}

	public int getRow()
	{
		return row;
	}

	public double getValue()
	{
		return value;
	}

	public double getWeight()
	{
		return weight;
	}

	@Override
	public WeightedOccurrence clone()
	{
		return new WeightedOccurrence(row, value, weight);
	}

	@Override
	public void readFields(DataInput in) throws IOException
	{
		row = Varint.readSignedVarInt(in);
		value = in.readDouble();
		weight = in.readDouble();
	}

	@Override
	public void write(DataOutput out) throws IOException
	{
		Varint.writeSignedVarInt(row, out);
		out.writeDouble(value);
		out.writeDouble(weight);
	}

	@Override
	public int hashCode()
	{
		return row;
	}

	@Override
	public boolean equals(Object other)
	{
		return other instanceof WeightedOccurrence && row == ((WeightedOccurrence) other).row;
	}
}
