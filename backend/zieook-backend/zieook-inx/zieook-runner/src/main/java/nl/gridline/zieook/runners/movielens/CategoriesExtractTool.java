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
package nl.gridline.zieook.runners.movielens;

import java.io.IOException;

import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.inx.movielens.categories.CategoriesExtractMap;
import nl.gridline.zieook.inx.movielens.categories.CategoriesExtractReduce;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.runners.ZieOokRunnerTool;
import nl.gridline.zieook.tasks.ZieOokTask;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * CategoriesExtractTool.java created Mar 24, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CategoriesExtractTool extends ZieOokRunnerTool
{

	private static final Logger LOG = LoggerFactory.getLogger(CategoriesExtractTool.class);
	private static final byte[] INTR = Bytes.toBytes(HBaseTableConstants.COLLECTION_TABLE_COLUMN_INTR);

	private String cp;
	private String collection;

	/**
	 * @param task
	 */
	public CategoriesExtractTool(ZieOokTask task)
	{
		super(task);
	}

	public CategoriesExtractTool configure(String table) throws IOException
	{
		setBasics();
		setJar(Config.getInstance().get(Config.MAPRED_MOVIELENS));
		setOutputTable(table);

		cp = task.getConfig().get(TaskConfig.CP);
		collection = task.getConfig().get(TaskConfig.COLLECTION);

		LOG.info("DataFileImport configured; from: <{}> to <{}>", table, table);
		return this;
	}

	@Override
	public boolean execute() throws Exception
	{
		return extractCategories();
	}

	private boolean extractCategories() throws IOException, InterruptedException, ClassNotFoundException
	{
		Config zieook = Config.getInstance();
		zieook.get(Config.ZIEOOK_HOME);

		// config.get(TaskConfig.COLLECTION);

		Path datapath = new Path(zieook.get(Config.ZIEOOK_HDFS_SERVER) + zieook.get(Config.ZIEOOK_HDFS_PATH), cp + "/"
				+ collection + "/tmp/categories");

		Job extractCategories = prepareTableMapper(getOutputTable(), datapath, getScanner(collection),
				CategoriesExtractMap.class, Text.class, LongWritable.class, CategoriesExtractReduce.class, Text.class,
				LongWritable.class, TextOutputFormat.class);
		boolean result = task.setCurrentJob(extractCategories).waitForCompletion(LOG.isDebugEnabled());
		if (!result || task.isCancelled())
		{
			return result;
		}

		// // now import it back in to HBase:
		// 1 Job importCategories = prepareTableReducer(datapath, getOutputTable(), SequenceFileInputFormat.class,
		// CategoriesImportMap.class, Text.class, Writable.class, CategoriesImportReduce.class);
		// importCategories.getConfiguration().set(TaskConfig.COLLECTION, config.get(TaskConfig.COLLECTION));
		//
		// result = importCategories.waitForCompletion(true);

		return result;
	}

	private Scan getScanner(String collection)
	{
		RowFilter filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(Bytes.toBytes(collection)));
		return new Scan().addFamily(INTR).setFilter(filter);
	}
}
