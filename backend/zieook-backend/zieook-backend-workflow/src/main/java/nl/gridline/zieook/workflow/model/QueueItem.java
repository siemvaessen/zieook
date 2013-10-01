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
package nl.gridline.zieook.workflow.model;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Item with id and timeout, designed for use with the DistributedQueue
 * <p />
 * Project zieook-backend-workflow<br />
 * QueueItem.java created 15 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class QueueItem
{
	private final long id;
	private final long timeout;

	public QueueItem(long timeout, long id)
	{
		this.id = id;
		this.timeout = timeout;
	}

	public QueueItem(String node, byte[] id) throws IOException
	{
		String[] path = node.split("-");
		if (path.length >= 1)
		{
			timeout = Long.parseLong(path[1]);
		}
		else
		{
			throw new IOException("Node name is not a valid queue item: " + node);
		}
		this.id = ByteBuffer.wrap(id).getLong();
	}

	public long getId()
	{
		return id;
	}

	public long getTimeout()
	{
		return timeout;
	}

	public byte[] getIdArray()
	{
		return ByteBuffer.allocate(8).putLong(id).array();
	}

	public String getTimeoutStr()
	{
		return Long.toString(timeout);
	}

	public boolean isExpired()
	{
		return timeout <= System.currentTimeMillis();
	}

	@Override
	public String toString()
	{
		return "id=" + id + " id" + timeout;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (timeout ^ (timeout >>> 32));
		return result;
	}

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
		QueueItem other = (QueueItem) obj;
		if (id != other.id)
		{
			return false;
		}
		if (timeout != other.timeout)
		{
			return false;
		}
		return true;
	}

}
