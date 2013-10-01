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


package nl.gridline.zieook.tasks.quality;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.RandomRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.common.RandomUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This tests the sanity of the recommender evaluator. The code is taken directly from Mahout.
 * <p />
 * Project zieook-runner<br />
 * MahoutEvaluator.java created 7 feb. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class MahoutEvaluatorTest
{

	private static final Logger LOG = LoggerFactory.getLogger(MahoutEvaluatorTest.class);

	private static File testData;

	/**
	 * @throws IOException
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws IOException
	{
		RandomUtils.useTestSeed();

		testData = new File("target/test-data.csv");
		TransformData.transformData(new File("test-data-big/ratings.dat"), testData);

	}

	@Test
	public void testSanity() throws TasteException, IOException
	{
		RandomUtils.useTestSeed();

		LOG.info("testing sanity on dummy data, result should be 1.0");

		DataModel model = new FileDataModel(new File("test-data-small/intro.csv"));
		AverageAbsoluteDifferenceRecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RecommenderBuilder builder = new RecommenderBuilder()
		{

			@Override
			public Recommender buildRecommender(DataModel model) throws TasteException
			{
				PearsonCorrelationSimilarity similarity = new PearsonCorrelationSimilarity(model);
				NearestNUserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
				return new GenericUserBasedRecommender(model, neighborhood, similarity);
			}
		};

		final long start = System.currentTimeMillis();
		double score = evaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("score: {} in {}ms", String.format("%.4f", score), (System.currentTimeMillis() - start));

		assertTrue(1.0 == score);

	}

	@Test
	public void testRandom() throws IOException, TasteException
	{
		RandomUtils.useTestSeed();

		LOG.info("testing Random:");
		DataModel model = new FileDataModel(testData);
		GenericRecommenderIRStatsEvaluator evaluatorIR = new GenericRecommenderIRStatsEvaluator();
		AverageAbsoluteDifferenceRecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RMSRecommenderEvaluator rmsEvaluator = new RMSRecommenderEvaluator();

		RecommenderBuilder builder = new RecommenderBuilder()
		{
			@Override
			public Recommender buildRecommender(DataModel model) throws TasteException
			{
				return new RandomRecommender(model);
			}
		};

		long start = System.currentTimeMillis();
		double score = evaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		score = rmsEvaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("rms score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		IRStatistics stats = evaluatorIR.evaluate(builder, null, model, null, 2,
				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		LOG.info("precision: {} recall: {} in {}s",
				new Object[]{String.format("%.4f", stats.getPrecision()), String.format("%.4f", stats.getRecall()),
						((System.currentTimeMillis() - start) / 1000)});

	}

	@Test
	public void testPearsonCorrelationSimilarity() throws IOException, TasteException
	{
		RandomUtils.useTestSeed();

		LOG.info("testing PearsonCorrelationSimilarity: ");

		DataModel model = new FileDataModel(testData);

		AverageAbsoluteDifferenceRecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RMSRecommenderEvaluator rmsEvaluator = new RMSRecommenderEvaluator();

		GenericRecommenderIRStatsEvaluator evaluatorIR = new GenericRecommenderIRStatsEvaluator();

		RecommenderBuilder builder = new RecommenderBuilder()
		{
			@Override
			public Recommender buildRecommender(DataModel model) throws TasteException
			{
				PearsonCorrelationSimilarity similarity = new PearsonCorrelationSimilarity(model);
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
				return new GenericUserBasedRecommender(model, neighborhood, similarity);
			}
		};

		long start = System.currentTimeMillis();
		double score = evaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		score = rmsEvaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("rms score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		IRStatistics stats = evaluatorIR.evaluate(builder, null, model, null, 2,
				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);

		LOG.info("precision: {} recall: {} in {}s",
				new Object[]{String.format("%.4f", stats.getPrecision()), String.format("%.4f", stats.getRecall()),
						((System.currentTimeMillis() - start) / 1000)});

	}

	@Test
	public void testTanimotoCoefficientSimilarity() throws TasteException, IOException
	{
		RandomUtils.useTestSeed();

		LOG.info("testing: TanimotoCoefficientSimilarity:");

		DataModel model = new FileDataModel(testData);

		AverageAbsoluteDifferenceRecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RMSRecommenderEvaluator rmsEvaluator = new RMSRecommenderEvaluator();

		GenericRecommenderIRStatsEvaluator evaluatorIR = new GenericRecommenderIRStatsEvaluator();

		RecommenderBuilder builder = new RecommenderBuilder()
		{
			@Override
			public Recommender buildRecommender(DataModel model) throws TasteException
			{
				TanimotoCoefficientSimilarity similarity = new TanimotoCoefficientSimilarity(model);
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
				return new GenericUserBasedRecommender(model, neighborhood, similarity);
			}
		};

		long start = System.currentTimeMillis();
		double score = evaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("average score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		score = rmsEvaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("rms score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		IRStatistics stats = evaluatorIR.evaluate(builder, null, model, null, 2,
				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);

		LOG.info("precision: {} recall: {} in {}s",
				new Object[]{String.format("%.4f", stats.getPrecision()), String.format("%.4f", stats.getRecall()),
						((System.currentTimeMillis() - start) / 1000)});
	}

	@Test
	public void testUncenteredCosineSimilarity() throws TasteException, IOException
	{
		RandomUtils.useTestSeed();

		LOG.info("testing: UncenteredCosineSimilarity:");

		DataModel model = new FileDataModel(testData);

		AverageAbsoluteDifferenceRecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RMSRecommenderEvaluator rmsEvaluator = new RMSRecommenderEvaluator();

		GenericRecommenderIRStatsEvaluator evaluatorIR = new GenericRecommenderIRStatsEvaluator();

		RecommenderBuilder builder = new RecommenderBuilder()
		{
			@Override
			public Recommender buildRecommender(DataModel model) throws TasteException
			{
				UncenteredCosineSimilarity similarity = new UncenteredCosineSimilarity(model);
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
				return new GenericUserBasedRecommender(model, neighborhood, similarity);
			}
		};

		long start = System.currentTimeMillis();
		double score = evaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("average score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		score = rmsEvaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("rms score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		IRStatistics stats = evaluatorIR.evaluate(builder, null, model, null, 2,
				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);

		LOG.info("precision: {} recall: {} in {}s",
				new Object[]{String.format("%.4f", stats.getPrecision()), String.format("%.4f", stats.getRecall()),
						((System.currentTimeMillis() - start) / 1000)});
	}

	@Test
	public void testCityBlockSimilarity() throws TasteException, IOException
	{
		RandomUtils.useTestSeed();

		LOG.info("testing CityBlockSimilarity:");
		DataModel model = new FileDataModel(testData);

		AverageAbsoluteDifferenceRecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RMSRecommenderEvaluator rmsEvaluator = new RMSRecommenderEvaluator();

		GenericRecommenderIRStatsEvaluator evaluatorIR = new GenericRecommenderIRStatsEvaluator();

		RecommenderBuilder builder = new RecommenderBuilder()
		{
			@Override
			public Recommender buildRecommender(DataModel model) throws TasteException
			{
				CityBlockSimilarity similarity = new CityBlockSimilarity(model);
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
				return new GenericUserBasedRecommender(model, neighborhood, similarity);
			}
		};

		long start = System.currentTimeMillis();
		double score = evaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("average score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		score = rmsEvaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("rms score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		IRStatistics stats = evaluatorIR.evaluate(builder, null, model, null, 2,
				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);

		LOG.info("precision: {} recall: {} in {}s",
				new Object[]{String.format("%.4f", stats.getPrecision()), String.format("%.4f", stats.getRecall()),
						((System.currentTimeMillis() - start) / 1000)});
	}

	@Test
	public void testLogLikelihoodSimilarity() throws TasteException, IOException
	{
		RandomUtils.useTestSeed();

		LOG.info("testing LogLikelihoodSimilarity: ");
		DataModel model = new FileDataModel(testData);

		AverageAbsoluteDifferenceRecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RMSRecommenderEvaluator rmsEvaluator = new RMSRecommenderEvaluator();

		GenericRecommenderIRStatsEvaluator evaluatorIR = new GenericRecommenderIRStatsEvaluator();

		RecommenderBuilder builder = new RecommenderBuilder()
		{
			@Override
			public Recommender buildRecommender(DataModel model) throws TasteException
			{
				LogLikelihoodSimilarity similarity = new LogLikelihoodSimilarity(model);
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
				return new GenericUserBasedRecommender(model, neighborhood, similarity);
			}
		};

		long start = System.currentTimeMillis();
		double score = evaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("average score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		score = rmsEvaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("rms score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		IRStatistics stats = evaluatorIR.evaluate(builder, null, model, null, 2,
				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);

		LOG.info("precision: {} recall: {} in {}s",
				new Object[]{String.format("%.4f", stats.getPrecision()), String.format("%.4f", stats.getRecall()),
						((System.currentTimeMillis() - start) / 1000)});
	}

	@Test
	public void testEuclideanDistanceSimilarity() throws TasteException, IOException
	{
		RandomUtils.useTestSeed();

		LOG.info("testing EuclideanDistanceSimilarity:");
		DataModel model = new FileDataModel(testData);

		AverageAbsoluteDifferenceRecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RMSRecommenderEvaluator rmsEvaluator = new RMSRecommenderEvaluator();

		GenericRecommenderIRStatsEvaluator evaluatorIR = new GenericRecommenderIRStatsEvaluator();

		RecommenderBuilder builder = new RecommenderBuilder()
		{
			@Override
			public Recommender buildRecommender(DataModel model) throws TasteException
			{
				EuclideanDistanceSimilarity similarity = new EuclideanDistanceSimilarity(model);
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
				return new GenericUserBasedRecommender(model, neighborhood, similarity);
			}
		};

		long start = System.currentTimeMillis();
		double score = evaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("average score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		score = rmsEvaluator.evaluate(builder, null, model, 0.7, 1.0);
		LOG.info("rms score: {} in {}s", String.format("%.4f", score), ((System.currentTimeMillis() - start) / 1000));

		start = System.currentTimeMillis();
		IRStatistics stats = evaluatorIR.evaluate(builder, null, model, null, 2,
				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);

		LOG.info("precision: {} recall: {} in {}s",
				new Object[]{String.format("%.4f", stats.getPrecision()), String.format("%.4f", stats.getRecall()),
						((System.currentTimeMillis() - start) / 1000)});
	}

	@Test
	public void testZieook()
	{
		// now we should replace the recommender with a ZieOok recommender
		// and the datamodel with the zieook database
		// and run the test...
		// ...
		// implementing this is the annoying part.
	}
}
