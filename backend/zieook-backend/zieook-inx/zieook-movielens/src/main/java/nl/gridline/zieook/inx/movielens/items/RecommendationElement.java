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
package nl.gridline.zieook.inx.movielens.items;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.mahout.math.Varint;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * RecommendationElement.java created 4 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class RecommendationElement implements Writable, Cloneable
{

	private int _itemIndex;
	private double _similarityValue;

	public RecommendationElement()
	{

	}

	public RecommendationElement(int index, double value)
	{
		this._itemIndex = index;
		this._similarityValue = value;
	}

	public int getItemIndex()
	{
		return _itemIndex;
	}

	public double getSimilarityValue()
	{
		return _similarityValue;
	}

	@Override
	public RecommendationElement clone()
	{
		return new RecommendationElement(_itemIndex, _similarityValue);
	}

	@Override
	public void readFields(DataInput in) throws IOException
	{
		_itemIndex = Varint.readSignedVarInt(in);
		_similarityValue = in.readDouble();
	}

	@Override
	public void write(DataOutput out) throws IOException
	{
		Varint.writeSignedVarInt(_itemIndex, out);
		out.writeDouble(_similarityValue);
	}

	@Override
	public int hashCode()
	{
		return _itemIndex;
	}

	@Override
	public boolean equals(Object other)
	{
		return other instanceof RecommendationElement && _itemIndex == ((RecommendationElement) other)._itemIndex;
	}

	@Override
	public String toString()
	{
		return (String.format("%d:%f", _itemIndex, _similarityValue));
	}

}
