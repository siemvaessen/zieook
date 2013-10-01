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
package nl.gridline.zieook.runners.taalserver;

import java.io.IOException;

import nl.gridline.free.taalserver.CountDocumentsMap;
import nl.gridline.free.taalserver.CountDocumentsReduce;
import nl.gridline.free.taalserver.DocCountPerWordMap;
import nl.gridline.free.taalserver.DocCountPerWordReduce;
import nl.gridline.free.taalserver.TFIdfSerializeMap;
import nl.gridline.free.taalserver.TFIdfSerializeReduce;
import nl.gridline.free.taalserver.TokenizeMap;
import nl.gridline.free.taalserver.TokenizeReduce;
import nl.gridline.free.taalserver.WordCountMap;
import nl.gridline.free.taalserver.WordCountPerDocMap;
import nl.gridline.free.taalserver.WordCountPerDocReduce;
import nl.gridline.free.taalserver.WordCountReduce;
import nl.gridline.free.taalserver.writable.TFIdfWritable;
import nl.gridline.free.taalserver.writable.WordCount;
import nl.gridline.free.taalserver.writable.WordCountWordsPerDoc;
import nl.gridline.free.taalserver.writable.WordCountWordsPerDocId;
import nl.gridline.free.taalserver.writable.WordDocId;
import nl.gridline.free.taalserver.writable.WordVector;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.runners.ZieOokRunnerTool;
import nl.gridline.zieook.tasks.ZieOokTask;
import nl.gridline.zieook.tasks.ZieOokTask.Level;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.mahout.cf.taste.hadoop.TasteHadoopUtils;
import org.apache.mahout.math.VarIntWritable;
import org.apache.mahout.math.VarLongWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-runner<br />
 * FreeTextVectorizeTool.java created 20 jun. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class FreeTextVectorizeTool extends ZieOokRunnerTool
{

	private static final Logger LOG = LoggerFactory.getLogger(FreeTextVectorizeTool.class);

	private String collection;
	private String cp;
	private String recommender;

	private Path documentCountPath;
	private Path vectorizePath;
	private Path wordCountPath;
	private Path wordCountPerDocPath;
	private Path docCountPerWordPath;
	private Path outputPath;

	/**
	 * @param task
	 */
	public FreeTextVectorizeTool(ZieOokTask task)
	{
		super(task);
	}

	public FreeTextVectorizeTool configure(String inputTable, String outputPath) throws IOException
	{
		setBasics();
		setJar(Config.getInstance().get(Config.MAPRED_FREETAALSERVER));
		setInputTable(inputTable);
		this.outputPath = new Path(outputPath);

		Config zieook = Config.getInstance();

		cp = task.getConfig().get(TaskConfig.CP);
		collection = task.getConfig().get(TaskConfig.COLLECTION);
		recommender = task.getConfig().get(TaskConfig.RECOMMENDER);

		// intermediate results:
		Path tempDirPath = new Path(zieook.get(Config.ZIEOOK_HDFS_SERVER) + zieook.get(Config.ZIEOOK_HDFS_PATH), cp + "/"
				+ collection + "/" + recommender + "/tmp");

		// first cleanup:
		cleanup(tempDirPath);
		cleanup(this.outputPath);

		documentCountPath = new Path(tempDirPath, "documentCount");
		vectorizePath = new Path(tempDirPath, "vectorize");
		wordCountPath = new Path(tempDirPath, "wordCount");
		wordCountPerDocPath = new Path(tempDirPath, "wordCountPerDoc");
		docCountPerWordPath = new Path(tempDirPath, "docCountPerWord");

		return this;
	}

	@Override
	public boolean execute() throws IOException, InterruptedException, ClassNotFoundException
	{

		// TODO we can start round 0 & 1 in one go, and check the number of docs later on, when we need it.
		// though like this we can stop processing as soon as we know there are no documents.

		// round 0: count the number of documents:
		Job countDocuments = prepareTableMapper(getInputTable(), documentCountPath, getInputScanner(),
				CountDocumentsMap.class, LongWritable.class, VarLongWritable.class, CountDocumentsReduce.class,
				VarIntWritable.class, NullWritable.class, TextOutputFormat.class);

		boolean result = task.setCurrentJob(countDocuments).waitForCompletion(LOG.isDebugEnabled());
		if (!result || task.isCancelled())
		{
			return result;
		}

		int numberOfDocuments = 0;
		try
		{
			numberOfDocuments = TasteHadoopUtils.readIntFromFile(getConf(), documentCountPath);
		}
		catch (NumberFormatException e)
		{
			LOG.error("probably no data in <{}>", documentCountPath);
			task.setMessage(Level.ERROR, "no documents to train recommender");
			return false;
		}

		LOG.info("Number of documents: {}", numberOfDocuments);

		// TODO if the number of documents is 0 or this throws an error we should stop!

		// round 1: vectorize text:
		Job vectorizeDocument = prepareTableMapper(getInputTable(), vectorizePath, getInputScanner(), TokenizeMap.class,
				WordVector.class, LongWritable.class, TokenizeReduce.class, LongWritable.class, Text.class,
				SequenceFileOutputFormat.class);

		// set text analysis parts:
		vectorizeDocument.getConfiguration().set(TaskConfig.TEXT_ANALYSIS_USE_DESCRIPTION,
				task.getConfig().get(TaskConfig.TEXT_ANALYSIS_USE_DESCRIPTION));
		vectorizeDocument.getConfiguration().set(TaskConfig.TEXT_ANALYSIS_USE_TITLE,
				task.getConfig().get(TaskConfig.TEXT_ANALYSIS_USE_TITLE));
		vectorizeDocument.getConfiguration().set(TaskConfig.TEXT_ANALYSIS_USE_KEYWORDS,
				task.getConfig().get(TaskConfig.TEXT_ANALYSIS_USE_KEYWORDS));

		result = task.setCurrentJob(vectorizeDocument).waitForCompletion(LOG.isDebugEnabled());
		if (!result || task.isCancelled())
		{
			return result;
		}

		// round 2: word count
		Job wordCount = prepareJob(vectorizePath, wordCountPath, SequenceFileInputFormat.class, WordCountMap.class,
				WordDocId.class, LongWritable.class, WordCountReduce.class, WordDocId.class, IntWritable.class,
				SequenceFileOutputFormat.class);
		result = task.setCurrentJob(wordCount).waitForCompletion(LOG.isDebugEnabled());
		if (!result || task.isCancelled())
		{
			return result;
		}

		// round 3: word count per document
		Job wordCountPerDoc = prepareJob(wordCountPath, wordCountPerDocPath, SequenceFileInputFormat.class,
				WordCountPerDocMap.class, LongWritable.class, WordCount.class, WordCountPerDocReduce.class,
				WordDocId.class, WordCountWordsPerDoc.class, SequenceFileOutputFormat.class);
		result = task.setCurrentJob(wordCountPerDoc).waitForCompletion(LOG.isDebugEnabled());
		if (!result || task.isCancelled())
		{
			return result;
		}

		// round 4: doc count per word:
		Job docCountPerWord = prepareJob(wordCountPerDocPath, docCountPerWordPath, SequenceFileInputFormat.class,
				DocCountPerWordMap.class, Text.class, WordCountWordsPerDocId.class, DocCountPerWordReduce.class,
				WordDocId.class, TFIdfWritable.class, SequenceFileOutputFormat.class);

		// write the number of documents in the config (needed to calculate the td-idf value)
		docCountPerWord.getConfiguration().setLong(TaskConfig.TEXT_DOCUMENT_COUNT, numberOfDocuments);
		result = task.setCurrentJob(docCountPerWord).waitForCompletion(LOG.isDebugEnabled());
		if (!result || task.isCancelled())
		{
			return result;
		}

		// round 5: serialize to text, to prepare for collaborative filtering:
		Job tfIdfSerialize = prepareJob(docCountPerWordPath, outputPath, SequenceFileInputFormat.class,
				TFIdfSerializeMap.class, Text.class, NullWritable.class, TFIdfSerializeReduce.class, Text.class,
				NullWritable.class, TextOutputFormat.class);
		result = task.setCurrentJob(tfIdfSerialize).waitForCompletion(LOG.isDebugEnabled());

		return result;
	}

	public Scan getInputScanner()
	{
		Scan scan = new Scan();
		RowFilter filter = new RowFilter(CompareOp.EQUAL, new BinaryPrefixComparator(Bytes.toBytes(collection)));
		scan.setFilter(filter);
		return scan;
	}

}
