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

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * WeightedOccurrenceGlwArray.java created 25 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:gerlof@gridline.nl">Gerlof</a>
 * @version $Revision$, $Date$
 */
public class WeightedOccurrenceZieOokArray extends org.apache.hadoop.io.ArrayWritable
{

	WeightedOccurrenceZieOokArray()
	{
		super(WeightedOccurrenceZieOok.class);
	}

	public WeightedOccurrenceZieOokArray(WeightedOccurrenceZieOok[] weightedOccurrences)
	{
		super(WeightedOccurrenceZieOok.class);
		set(weightedOccurrences);
	}

	public WeightedOccurrenceZieOok[] getWeightedOccurrences()
	{
		return (WeightedOccurrenceZieOok[]) toArray();
	}
}
