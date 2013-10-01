// ---------------------------------------------------------
// Copyright, all rights reserved 2012 GridLine Amsterdam
// ---------------------------------------------------------
package nl.gridline.zieook.inx.movielens.hbase;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * DeleteAnyMap.java created 28 feb. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class DeleteAnyMap extends TableMapper<ImmutableBytesWritable, Delete>
{
	@Override
	protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException,
			InterruptedException
	{
		context.write(key, new Delete(value.getRow()));
	};
}
