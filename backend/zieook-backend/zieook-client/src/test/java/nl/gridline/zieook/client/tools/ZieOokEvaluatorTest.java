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
package nl.gridline.zieook.client.tools;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-client<br />
 * ZieOokEvaluatorTest.java created 12 apr. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class ZieOokEvaluatorTest
{

	private static final String original = "data/ratings.dat";
	private static final String input = "data/ratings-input.dat";
	private static final String result = "data/result.csv";

	@BeforeClass
	public static void prepare()
	{

		// output: userID,itemID,prefValue
		// input: UserID::MovieID::Rating::Timestamp
		try
		{
			FileReader reader = new FileReader(new File(original));
			FileWriter writer = new FileWriter(new File(input));

			BufferedReader bfr = new BufferedReader(reader);
			try
			{
				String line;
				while ((line = bfr.readLine()) != null)
				{
					String[] elements = line.split("::");
					if (elements.length == 4)
					{
						writer.write(elements[0]);
						writer.write(',');
						writer.write(elements[1]);
						writer.write(',');
						writer.write(elements[3]);
						writer.write('\n');
					}
				}
			}
			finally
			{
				reader.close();
				writer.flush();
				writer.close();
			}
		}
		catch (IOException e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}

	private static final Logger LOG = LoggerFactory.getLogger(ZieOokEvaluatorTest.class);

	@Ignore
	private class UserBasedRecommenderBuilder implements RecommenderBuilder
	{

		String clazz;

		public UserBasedRecommenderBuilder(String clazz)
		{
			this.clazz = clazz;
		}

		@Override
		public Recommender buildRecommender(DataModel model)
		{
			try
			{
				UserSimilarity userSimilarity = null;
				if (EuclideanDistanceSimilarity.class.getCanonicalName().equals(clazz))
				{
					userSimilarity = new EuclideanDistanceSimilarity(model);
				}
				else if (PearsonCorrelationSimilarity.class.getCanonicalName().equals(clazz))
				{
					userSimilarity = new PearsonCorrelationSimilarity(model);
				}
				else if (UncenteredCosineSimilarity.class.getCanonicalName().equals(clazz))
				{
					userSimilarity = new UncenteredCosineSimilarity(model);
				}
				else if (SpearmanCorrelationSimilarity.class.getCanonicalName().equals(clazz))
				{
					userSimilarity = new SpearmanCorrelationSimilarity(model);
				}
				else if (TanimotoCoefficientSimilarity.class.getCanonicalName().equals(clazz))
				{
					userSimilarity = new TanimotoCoefficientSimilarity(model);
				}
				else if (LogLikelihoodSimilarity.class.getCanonicalName().equals(clazz))
				{
					userSimilarity = new LogLikelihoodSimilarity(model);
				}
				// userSimilarity.setPreferenceInferrer(new AveragingPreferenceInferrer());
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, userSimilarity, model);
				Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, userSimilarity);
				return new CachingRecommender(recommender);
			}
			catch (TasteException e)
			{
				LOG.error("failed to build recommender", e);
				fail();
			}
			return null;
		}
	};

	public class ItemBasedRecommenderBuilder implements RecommenderBuilder
	{
		String clazz;

		public ItemBasedRecommenderBuilder(String clazz)
		{
			this.clazz = clazz;
		}

		@Override
		public Recommender buildRecommender(DataModel model) throws TasteException
		{
			try
			{
				ItemSimilarity itemSimilarity = null;
				if (EuclideanDistanceSimilarity.class.getCanonicalName().equals(clazz))
				{
					itemSimilarity = new EuclideanDistanceSimilarity(model);
				}
				else if (PearsonCorrelationSimilarity.class.getCanonicalName().equals(clazz))
				{
					itemSimilarity = new PearsonCorrelationSimilarity(model);
				}
				else if (UncenteredCosineSimilarity.class.getCanonicalName().equals(clazz))
				{
					itemSimilarity = new UncenteredCosineSimilarity(model);
				}
				else if (TanimotoCoefficientSimilarity.class.getCanonicalName().equals(clazz))
				{
					itemSimilarity = new TanimotoCoefficientSimilarity(model);
				}
				else if (LogLikelihoodSimilarity.class.getCanonicalName().equals(clazz))
				{
					itemSimilarity = new LogLikelihoodSimilarity(model);
				}
				// userSimilarity.setPreferenceInferrer(new AveragingPreferenceInferrer());

				Recommender recommender = new GenericItemBasedRecommender(model, itemSimilarity);
				return new CachingRecommender(recommender);
			}
			catch (TasteException e)
			{
				LOG.error("failed to build recommender", e);
				fail();
			}
			return null;
		}
	}

	public class BooleanDataModelBuilder implements DataModelBuilder
	{
		@Override
		public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData)
		{
			try
			{
				return new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(createDataModel(input)));
			}
			catch (TasteException e)
			{
				LOG.error("failed to read data", e);
				fail();
			}
			return null;
		}
	};

	// public class RatingDataModelBuilder implements DataModelBuilder
	// {
	//
	// @Override
	// public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData)
	// {
	// try
	// {
	// return new GenericDataModel(GenericDataModel.toDataMap(createDataModel(input)));
	// }
	// catch (TasteException e)
	// {
	// LOG.error("failed to read data", e);
	// fail();
	// }
	// return null;
	// }
	//
	// }

	@Ignore
	private static DataModel createDataModel(String input)
	{
		try
		{
			return new FileDataModel(new File(input));
		}
		catch (IOException e)
		{
			LOG.error("failed to read data", e);
			fail();
		}
		return null;
	}

	@Ignore
	private static GenericBooleanPrefDataModel createDataBooleanModel(String input)
	{
		try
		{
			return new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(createDataModel(input)));
		}
		catch (TasteException e)
		{
			LOG.error("failed to read data", e);
			fail();
		}
		return null;
	}

	@Ignore
	private void writetofile(String line)
	{
		try
		{
			FileWriter writer = new FileWriter(new File(result), true);
			try
			{
				writer.write(new Date(System.currentTimeMillis()) + "," + line);
			}
			finally
			{
				writer.flush();
				writer.close();
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to write result");
			fail();
		}
	}

	/**
	 * 3x userbased using: AverageAbsoluteDifferenceRecommenderEvaluator
	 */
	@Test
	@Ignore
	public void evaluate1()
	{
		// UserBasedRecommenderBuilder
		// AverageAbsoluteDifferenceRecommenderEvaluator
		try
		{
			DataModel model = createDataModel(input);
			RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
			double evaluation = evaluator.evaluate(
					new UserBasedRecommenderBuilder(EuclideanDistanceSimilarity.class.getCanonicalName()), null, model, 0.9,
					1.0);
			LOG.info("result: " + evaluation);
			writetofile("EuclideanDistanceSimilarity,AverageAbsoluteDifferenceRecommenderEvaluator," + evaluation + "\n");

			evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
			evaluation = evaluator.evaluate(
					new UserBasedRecommenderBuilder(PearsonCorrelationSimilarity.class.getCanonicalName()), null, model,
					0.9, 1.0);
			LOG.info("result: " + evaluation);
			writetofile("PearsonCorrelationSimilarity,AverageAbsoluteDifferenceRecommenderEvaluator," + evaluation + "\n");

			evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
			evaluation = evaluator.evaluate(
					new UserBasedRecommenderBuilder(UncenteredCosineSimilarity.class.getCanonicalName()), null, model, 0.9,
					1.0);
			LOG.info("result: " + evaluation);
			writetofile("UncenteredCosineSimilarity,AverageAbsoluteDifferenceRecommenderEvaluator," + evaluation + "\n");

		}
		catch (TasteException e)
		{
			LOG.error("faild evaulate", e);
			fail();
		}
	}

	/**
	 * 3x userbased using RMSRecommenderEvaluator
	 */
	@Test
	@Ignore
	public void evaulate2()
	{
		// UserBasedRecommenderBuilder
		// RMSRecommenderEvaluator
		try
		{
			DataModel model = createDataModel(input);

			RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
			double evaluation = evaluator.evaluate(
					new UserBasedRecommenderBuilder(EuclideanDistanceSimilarity.class.getCanonicalName()), null, model, 0.9,
					1.0);
			LOG.info("result: " + evaluation);
			writetofile("UserBasedRecommenderBuilder,EuclideanDistanceSimilarity,RMSRecommenderEvaluator," + evaluation
					+ "\n");

			evaluator = new RMSRecommenderEvaluator();
			evaluation = evaluator.evaluate(
					new UserBasedRecommenderBuilder(PearsonCorrelationSimilarity.class.getCanonicalName()), null, model,
					0.9, 1.0);
			LOG.info("result: " + evaluation);
			writetofile("UserBasedRecommenderBuilder,PearsonCorrelationSimilarity,RMSRecommenderEvaluator," + evaluation
					+ "\n");

			evaluator = new RMSRecommenderEvaluator();
			evaluation = evaluator.evaluate(
					new UserBasedRecommenderBuilder(UncenteredCosineSimilarity.class.getCanonicalName()), null, model, 0.9,
					1.0);
			LOG.info("result: " + evaluation);
			writetofile("UserBasedRecommenderBuilder,UncenteredCosineSimilarity,RMSRecommenderEvaluator," + evaluation
					+ "\n");

		}
		catch (TasteException e)
		{
			LOG.error("faild evaulate", e);
			fail();
		}
	}

	@Test
	@Ignore
	public void evaluate3()
	{
		// ItemBasedRecommenderBuilder
		// RMSRecommenderEvaluator
		try
		{
			DataModel model = createDataModel(input);
			RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
			double evaluation = evaluator.evaluate(
					new ItemBasedRecommenderBuilder(EuclideanDistanceSimilarity.class.getCanonicalName()), null, model, 0.9,
					1.0);
			LOG.info("result: " + evaluation);
			writetofile("ItemBasedRecommenderBuilder,EuclideanDistanceSimilarity,AverageAbsoluteDifferenceRecommenderEvaluator,"
					+ evaluation + "\n");

			evaluator = new RMSRecommenderEvaluator();
			evaluation = evaluator.evaluate(
					new ItemBasedRecommenderBuilder(PearsonCorrelationSimilarity.class.getCanonicalName()), null, model,
					0.9, 1.0);
			LOG.info("result: " + evaluation);
			writetofile("ItemBasedRecommenderBuilder,PearsonCorrelationSimilarity,AverageAbsoluteDifferenceRecommenderEvaluator,"
					+ evaluation + "\n");

			evaluator = new RMSRecommenderEvaluator();
			evaluation = evaluator.evaluate(
					new ItemBasedRecommenderBuilder(UncenteredCosineSimilarity.class.getCanonicalName()), null, model, 0.9,
					1.0);
			LOG.info("result: " + evaluation);
			writetofile("ItemBasedRecommenderBuilder,UncenteredCosineSimilarity,AverageAbsoluteDifferenceRecommenderEvaluator,"
					+ evaluation + "\n");
		}
		catch (TasteException e)
		{
			LOG.error("faild evaulate", e);
			fail();
		}
	}

	@Test
	@Ignore
	public void evaluate4()
	{
		// ItemBasedRecommenderBuilder
		// AverageAbsoluteDifferenceRecommenderEvaluator
		try
		{
			DataModel model = createDataModel(input);
			RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
			double evaluation = evaluator.evaluate(
					new ItemBasedRecommenderBuilder(EuclideanDistanceSimilarity.class.getCanonicalName()), null, model, 0.9,
					1.0);
			LOG.info("result: " + evaluation);
			writetofile("ItemBasedRecommenderBuilder,EuclideanDistanceSimilarity,AverageAbsoluteDifferenceRecommenderEvaluator,"
					+ evaluation + "\n");

			evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
			evaluation = evaluator.evaluate(
					new ItemBasedRecommenderBuilder(PearsonCorrelationSimilarity.class.getCanonicalName()), null, model,
					0.9, 1.0);
			LOG.info("result: " + evaluation);
			writetofile("ItemBasedRecommenderBuilder,PearsonCorrelationSimilarity,AverageAbsoluteDifferenceRecommenderEvaluator,"
					+ evaluation + "\n");

			evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
			evaluation = evaluator.evaluate(
					new ItemBasedRecommenderBuilder(UncenteredCosineSimilarity.class.getCanonicalName()), null, model, 0.9,
					1.0);
			LOG.info("result: " + evaluation);
			writetofile("ItemBasedRecommenderBuilder,UncenteredCosineSimilarity,AverageAbsoluteDifferenceRecommenderEvaluator,"
					+ evaluation + "\n");
		}
		catch (TasteException e)
		{
			LOG.error("faild evaulate", e);
			fail();
		}
	}

	@Test
	public void evaluate5()
	{
		// RecommenderIRStatsEvaluator
		// ItemBasedRecommenderBuilder
		// TanimotoCoefficientSimilarity

		// IRStatistics stats =
		// evaluator.evaluate(builder, myModel, null, 3,
		// RecommenderIRStatusEvaluator.CHOOSE_THRESHOLD,
		// &sect;1.0);

		try
		{
			DataModel model = createDataBooleanModel(input);

			RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();
			IRStatistics evaluation = evaluator.evaluate(new ItemBasedRecommenderBuilder(
					TanimotoCoefficientSimilarity.class.getCanonicalName()), new BooleanDataModelBuilder(), model, null, 3,
					GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.9);
			LOG.info("result: " + evaluation);

			writetofile("ItemBasedRecommenderBuilder,TanimotoCoefficientSimilarity,RecommenderIRStatsEvaluator-F1,"
					+ evaluation.getF1Measure() + "\n");
			// getFNMeasure
			// writetofile("ItemBasedRecommenderBuilder,EuclideanDistanceSimilarity,RecommenderIRStatsEvaluator-F1,"
			// + evaluation.getFNMeasure(n) + "\n");
			writetofile("ItemBasedRecommenderBuilder,TanimotoCoefficientSimilarity,RecommenderIRStatsEvaluator-FallOut,"
					+ evaluation.getFallOut() + "\n");
			writetofile("ItemBasedRecommenderBuilder,TanimotoCoefficientSimilarity,RecommenderIRStatsEvaluator-precision,"
					+ evaluation.getPrecision() + "\n");
			writetofile("ItemBasedRecommenderBuilder,TanimotoCoefficientSimilarity,RecommenderIRStatsEvaluator-recall,"
					+ evaluation.getRecall() + "\n");

			evaluator = new GenericRecommenderIRStatsEvaluator();
			evaluation = evaluator.evaluate(
					new UserBasedRecommenderBuilder(TanimotoCoefficientSimilarity.class.getCanonicalName()),
					new BooleanDataModelBuilder(), model, null, 3, 3, 0.9);
			LOG.info("result: " + evaluation);

			writetofile("UserBasedRecommenderBuilder,TanimotoCoefficientSimilarity,RecommenderIRStatsEvaluator-F1,"
					+ evaluation.getF1Measure() + "\n");
			// getFNMeasure
			// writetofile("ItemBasedRecommenderBuilder,EuclideanDistanceSimilarity,RecommenderIRStatsEvaluator-F1,"
			// + evaluation.getFNMeasure(n) + "\n");
			writetofile("UserBasedRecommenderBuilder,TanimotoCoefficientSimilarity,RecommenderIRStatsEvaluator-FallOut,"
					+ evaluation.getFallOut() + "\n");
			writetofile("UserBasedRecommenderBuilder,TanimotoCoefficientSimilarity,RecommenderIRStatsEvaluator-precision,"
					+ evaluation.getPrecision() + "\n");
			writetofile("UserBasedRecommenderBuilder,TanimotoCoefficientSimilarity,RecommenderIRStatsEvaluator-recall,"
					+ evaluation.getRecall() + "\n");

		}
		catch (TasteException e)
		{
			LOG.error("faild evaulate", e);
			fail();
		}

	}
}
