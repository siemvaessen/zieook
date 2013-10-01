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

import static org.junit.Assert.assertEquals;

import java.util.List;

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.model.HBaseRecommendationTable;
import nl.gridline.zieook.model.Recommend;
import nl.gridline.zieook.model.Recommendations;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * RecommendationTableTest.java created 3 jan. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class RecommendationTableTest
{

	static
	{
		System.setProperty("gridline.project.name", "zieook");
	}

	private static final Logger LOG = LoggerFactory.getLogger(RecommendationTableTest.class);

	// String cp, String collection, String recommender, long id, int size, String spread)

	// private static final String CP = "testCP006";
	// private static final String COLLECTION = "am2";
	// private static final String RECOMMENDER = "textbased";
	//

	private static final String CP = "AmsterdamMuseum3";
	private static final String COLLECTION = "am2";
	private static final String ZOOKEEPER = "hm.gridline.nl";
	private static final String RECOMMENDER = "textbased";

	private static RecommendationTable recommendations;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		// create table connection
		// Config config = Config.getInstance();
		// String zookeeper = config.getZooKeeperHost();

		HBaseManager manager = HBaseManager.getInstance(ZOOKEEPER);

		HBaseRecommendationTable table = new HBaseRecommendationTable(manager);
		recommendations = new RecommendationTable(table);

	}

	@Test
	public void recommenderNames()
	{

	}

	@Test
	@Ignore
	public void testRecommendationSize()
	{
		// front-end sometimes only shows 3 items, when 4 should be shown. This tests requests
		// 100x the same random recommendations if that passes we are quite sure it's not the backend causing the problem.
		for (int i = 0; i < 100; i++)
		{
			Recommendations random = recommendations.getRecommendationsFor(CP, COLLECTION, RECOMMENDER, 50817, 4, null);
			assertEquals(4, random.getRecommended().size());
		}
	}

	@Test
	public void testRecommendations()
	{
		Recommendations nospread = recommendations.getRecommendationsFor(CP, COLLECTION, RECOMMENDER, 1, 10, null);
		List<Recommend> recommendedLst = nospread.getRecommended();

		LOG.info("NOSPREAD\n{}", nospread.toJSON());
		LOG.info("{}", recommendedLst.size());

		Recommendations random = recommendations.getRecommendationsFor(CP, COLLECTION, RECOMMENDER, 1, 10, "random");
		List<Recommend> recommendedRnd = random.getRecommended();
		LOG.info("RANDOM\n{}", random.toJSON());
		LOG.info("{}", recommendedRnd.size());

		Recommendations gauss = recommendations.getRecommendationsFor(CP, COLLECTION, RECOMMENDER, 1, 10, "gauss");
		List<Recommend> recommendedGauss = gauss.getRecommended();
		LOG.info("GAUSS\n{}", gauss.toJSON());
		LOG.info("{}", recommendedGauss.size());

	}
}
