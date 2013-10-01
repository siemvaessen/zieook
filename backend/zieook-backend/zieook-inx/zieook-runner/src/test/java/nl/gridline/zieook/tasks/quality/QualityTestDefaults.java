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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import nl.gridline.zieook.tasks.SortedSuite;

import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.OpenMapRealMatrix;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculate the recommendations using normal matrix multiplications
 * <p />
 * Project zieook-runner<br />
 * QualityTestDefaults.java created 18 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@RunWith(SortedSuite.class)
public class QualityTestDefaults
{

	private static final Logger LOG = LoggerFactory.getLogger(QualityTestDefaults.class);

	// Test input definition, adapt settings to you're need:
	private static final File ratingsFile = new File("test-data-big/ratings.dat"); // ratings file
	private static final Random rnd = new Random(100);
	private static float train = 0.1f; // train percentage

	//
	private static File setTest;
	private static File setTrain;
	private static File distanceFile;

	private static int itemHigh = 0, userHigh = 0;

	public static class A_LoadData
	{
		private static File inputFile;

		@BeforeClass
		public static void init() throws IOException
		{
			// load some percentage of the data (using a fixed random seed)

			// data prepare / split should be done in the global init.

			// put data in hdfs, the 'input' path

			inputFile = ratingsFile.getCanonicalFile();
			assertTrue(inputFile.exists());
			LOG.info("input data: {}", inputFile);

			setTest = new File("target/test-set.dat");
			LOG.info("test set: {}", setTest);

			setTrain = new File("target/train-set.dat");
			LOG.info("train set: {}", setTrain);

			distanceFile = new File("test-data-big/distance-matrix.dat");
			LOG.info("distance matrix: {}", distanceFile);

			// check if exists, delete if so:
			if (setTest.exists())
			{
				LOG.info("cleanup old data: {}", setTest);
				assertTrue(setTest.delete());
			}
			if (setTrain.exists())
			{
				LOG.info("cleanup train data: {}", setTrain);
				assertTrue(setTrain.delete());
			}
			if (distanceFile.exists())
			{
				LOG.info("cleanup old data {}", distanceFile);
				// assertTrue(distanceFile.delete());
			}
		}

		@Test
		public void splitAndLoadData() throws IOException
		{
			LOG.info("splitting data with {} train data", train);
			BufferedWriter trainData = new BufferedWriter(new FileWriter(setTrain));
			BufferedWriter testData = new BufferedWriter(new FileWriter(setTest));
			BufferedReader bin = new BufferedReader(new FileReader(inputFile));

			int count = 0, trainCnt = 0, testCnt = 0;
			try
			{
				String line = null;
				while ((line = bin.readLine()) != null)
				{
					count++;
					// In: UserID::MovieID::Rating::Timestamp
					String[] elems = line.split("::");
					// Out: userID,itemID[,preferencevalue]
					String out = new StringBuilder().append(elems[0]).append(',').append(elems[1]).append(',')
							.append(elems[2]).append('\n').toString();
					if (rnd.nextFloat() > train)
					{
						// this is test data:
						testData.write(out);
						testCnt++;
					}
					else
					{
						// this is train data:
						trainData.write(out);
						trainCnt++;
					}

					itemHigh = Math.max(itemHigh, Integer.parseInt(elems[1]));
					userHigh = Math.max(userHigh, Integer.parseInt(elems[0]));
				}
			}
			finally
			{
				bin.close();
				testData.close();
				trainData.close();
			}

			// split done:
			LOG.info("input data: {} ratings", count);
			LOG.info("test data: {} ratings", testCnt);
			LOG.info("train data: {} ratings", trainCnt);
			LOG.info("highest user id {}", userHigh);
			LOG.info("highest item id {}", itemHigh);
		}
	}

	public static class B_CalculateManual
	{
		// do the multiplication manually using simple matrix math.

		private static OpenMapRealMatrix r;
		private static OpenMapRealMatrix s;

		@BeforeClass
		public static void init() throws NumberFormatException, MatrixIndexException, IOException
		{
			// load rating matrix:

			// create a rating matrix: N_user by M_item:
			BufferedReader bin = new BufferedReader(new FileReader(setTrain));
			r = new OpenMapRealMatrix(userHigh + 1, itemHigh + 1);
			try
			{
				String line = null;
				while ((line = bin.readLine()) != null)
				{
					String elems[] = line.split(",");
					int user = Integer.parseInt(elems[0]);
					int item = Integer.parseInt(elems[1]);

					double rating = Double.parseDouble(elems[2]);
					// r.setEntry(row, column, value);
					r.setEntry(user, item, rating);
				}
			}
			finally
			{
				bin.close();
			}
			LOG.info("loaded [sparse] rating matrix: users x items = {} x {}", r.getRowDimension(), r.getColumnDimension());
		}

		static class TanimotoDistance implements Runnable
		{
			private int k;

			public TanimotoDistance setRows(int k)
			{
				this.k = k;
				return this;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				for (int l = 0; l < itemHigh; l++)
				{
					if (l != k)
					{
						double[] vK = r.getColumn(k);
						double[] vL = s.getColumn(l);
						s.setEntry(k, l, tanimoto(vK, vL));
					}
				}
			}
		}

		@Test
		public void similarityMatrix() throws NumberFormatException, IOException, InterruptedException
		{
			if (distanceFile.exists())
			{
				LOG.info("loading existing distance file: {}", distanceFile);
				s = new OpenMapRealMatrix(userHigh + 1, itemHigh + 1);
				BufferedReader brr = new BufferedReader(new FileReader(distanceFile));

				String rowsStr = brr.readLine();
				String colsStr = brr.readLine();
				String line = null;
				int rows = Integer.parseInt(rowsStr);
				int cols = Integer.parseInt(colsStr);
				s = new OpenMapRealMatrix(rows, cols);
				int row = 0;

				while ((line = brr.readLine()) != null)
				{
					String[] elems = line.split(",");
					for (int col = 0; col < cols; col++)
					{
						s.setEntry(row, col, Double.parseDouble(elems[col]));
					}
					row++;
				}
				LOG.info("loaded: users x items: {}x{}", rows, cols);
				return;
			}

			// calculate distance file: (takes some time)
			s = new OpenMapRealMatrix(userHigh + 1, itemHigh + 1);
			LOG.info("calculating co-occurrences");

			Thread t1;
			Thread t2;
			Thread t3;
			long start = System.currentTimeMillis();
			for (int k = 0; k < itemHigh + 1; k++)
			{
				if (k < itemHigh + 1)
				{
					t1 = new Thread(new TanimotoDistance().setRows(k), "tanimoto-1");
					t1.start();
				}
				else
				{
					t1 = null;
				}
				if (k + 1 < itemHigh + 1)
				{
					t2 = new Thread(new TanimotoDistance().setRows(++k), "tanimoto-2");
					t2.start();
				}
				else
				{
					t2 = null;
				}
				if (k + 2 < itemHigh + 1)
				{
					t3 = new Thread(new TanimotoDistance().setRows(++k), "tanimoto-3");
					t3.start();
				}
				else
				{
					t3 = null;

				}

				if (t1 != null)
				{
					t1.join();
				}
				if (t2 != null)
				{
					t2.join();
				}

				if (t3 != null)
				{
					t3.join();
				}

				if (k % 50 == 0)
				{
					LOG.info("co-occurence of k={}/{} done: " + ((System.currentTimeMillis() - start) / 1000) + "s", k,
							itemHigh);
					start = System.currentTimeMillis();
				}
			}
			LOG.info("calculated co-occurence matrix, now writing to disk: {}", distanceFile);
			// store matrix:
			BufferedWriter bwr = new BufferedWriter(new FileWriter(distanceFile));
			try
			{
				bwr.write(s.getRowDimension());
				bwr.write('\n');
				bwr.write(s.getColumnDimension());
				bwr.write('\n');

				StringBuilder b = new StringBuilder();
				for (int r = 0; r < s.getRowDimension(); r++)
				{
					double[] row = s.getRow(r);
					b.setLength(0);
					for (int c = 0; c < row.length; c++)
					{
						b.append(row[c]).append(',');
					}
					b.deleteCharAt(b.length() - 1);
					bwr.write(b.toString());
					bwr.write('\n');
					if (r % 100 == 0)
					{
						bwr.flush();
					}
				}
			}
			finally
			{
				bwr.close();
			}
		}

		// TANIMOTO
		private static double tanimoto(double[] vK, double[] vL)
		{
			int length = vK.length;
			int co = 0;
			int wK = 0;
			int wL = 0;
			for (int i = 0; i < length; i++)
			{
				if (vK[i] != 0 && vL[i] != 0)
				{
					co++;
				}
				if (vK[i] != 0)
				{
					wK++;
				}
				if (vL[i] != 0)
				{
					wL++;
				}
			}

			return co / (double) (wK + wL - co);
		}
	}

	public static class C_CalculateHadoop
	{
		// user hadoop to create an itembased recommender

		@Test
		public void test()
		{

		}
	}

	public static class D_CompareResults
	{
		// compare the manual matrix with the itembased recommender

		@Test
		public void test()
		{

		}
	}

}
