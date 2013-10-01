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


package nl.gridline.zieook.data.hbase.dao;

import java.util.ArrayList;
import java.util.List;

import nl.gridline.zieook.data.hbase.model.HBaseSupportTable;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.RowKeys;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * SupportTable.java created 30 jan. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class SupportTable
{
	// public static final byte[] DC_IDENTIFIER_COLUMN = Bytes.toBytes(HBaseTableConstants.DC_IDENTIFIER_COLUMN);
	public static final byte[] DC_IDENTIFIERS_COLUMN = Bytes.toBytes(HBaseTableConstants.DC_IDENTIFIERS_COLUMN);
	// public static final byte[] ID = Bytes.toBytes("id");
	public static final byte[] IDENTIFIER = Bytes.toBytes("identifier:");
	public static final byte[] IDENFITIER_LENGTH = Bytes.toBytes("id:length");

	private static final Logger LOG = LoggerFactory.getLogger(SupportTable.class);

	private final HBaseSupportTable table;

	public SupportTable(HBaseSupportTable table)
	{
		this.table = table;
	}

	/**
	 * <p>
	 * <b>this method only applies to collections with a dc:relation</b>
	 * </p>
	 * Returns a list of id's that have a dc:relation from the given id
	 * @param cp content provider
	 * @param id item id
	 * @return a list of id
	 */
	public List<Long> getRelations(String cp, long id)
	{
		Result result = table.get(cp, new Get(RowKeys.getAMRelation(id)).addFamily(DC_IDENTIFIERS_COLUMN));
		if (result.isEmpty())
		{
			return null;
		}
		byte[] lb = result.getValue(DC_IDENTIFIERS_COLUMN, IDENFITIER_LENGTH);
		if (lb == null)
		{
			LOG.error("no data length for this {} id", id);
			return null;
		}
		int length = Bytes.toInt(lb);
		List<Long> resultList = new ArrayList<Long>();
		for (int i = 0; i < length; i++)
		{
			byte[] v = result.getValue(DC_IDENTIFIERS_COLUMN, Bytes.add(IDENTIFIER, Bytes.toBytes(i)));
			if (v != null)
			{
				resultList.add(Bytes.toLong(v));
			}
			else
			{
				LOG.error("no value for {}/{}/identifier:{}", new Object[]{cp, id, i});
			}
		}
		return resultList;
	}
}
