// ---------------------------------------------------------
// Copyright, all rights reserved 2012 GridLine Amsterdam
// ---------------------------------------------------------
package nl.gridline.zieook.runners;

import java.io.IOException;

import nl.gridline.zieook.OAIException;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.inx.movielens.hbase.DeleteAnyMap;
import nl.gridline.zieook.inx.movielens.hbase.DeleteAnyReduce;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * DeleteTool.java created 28 feb. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class DeleteTool extends ZieOokRunnerTool
{

	private static final Logger LOG = LoggerFactory.getLogger(DeleteTool.class);

	private String table;
	private Scan scan;

	/**
	 * @param task
	 */
	public DeleteTool(ZieOokTask task)
	{
		super(task);
	}

	public DeleteTool configure(String table, Scan scan) throws IOException, OAIException
	{
		// add generic support for input format:

		setBasics();
		setJar(Config.getInstance().get(Config.MAPRED_MOVIELENS));

		this.table = table;
		this.scan = scan;

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.runners.ZieOokRunnerTool#execute()
	 */
	@Override
	public boolean execute() throws Exception
	{

		Configuration conf = getConf();
		Job job = new Job(conf);
		job.setJobName(getCustomJobName(job, DeleteAnyMap.class, DeleteAnyReduce.class));

		TableMapReduceUtil.initTableMapperJob(table, scan, DeleteAnyMap.class, ImmutableBytesWritable.class,
				Delete.class, job);

		TableMapReduceUtil.initTableReducerJob(table, DeleteAnyReduce.class, job);

		return task.setCurrentJob(job).waitForCompletion(LOG.isDebugEnabled());
	}

}
