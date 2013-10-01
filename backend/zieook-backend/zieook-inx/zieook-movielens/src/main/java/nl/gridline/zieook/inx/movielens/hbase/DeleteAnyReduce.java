// ---------------------------------------------------------
// Copyright, all rights reserved 2012 GridLine Amsterdam
// ---------------------------------------------------------
package nl.gridline.zieook.inx.movielens.hbase;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;

/**
 * [purpose]
 * <p />
 * Project zieook-movielens<br />
 * DeleteAnyReduce.java created 28 feb. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class DeleteAnyReduce extends TableReducer<ImmutableBytesWritable, Delete, ImmutableBytesWritable>
{

	@Override
	protected void reduce(ImmutableBytesWritable keyIn, Iterable<Delete> values, Context context) throws IOException,
			InterruptedException
	{
		for (Delete value : values)
		{
			context.write(keyIn, value);
		}
	};
}
