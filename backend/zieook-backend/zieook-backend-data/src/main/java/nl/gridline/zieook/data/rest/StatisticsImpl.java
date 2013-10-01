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
package nl.gridline.zieook.data.rest;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-data<br />
 * StatisticsImpl.java created 22 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:daryl@gridline.nl">Daryl</a>
 * @version $Revision$, $Date$
 */
public class StatisticsImpl
{

	// @Override
	// public RecommenderItemCount topRecommendedItems(String cp, String recommender, int size, Long startDate, Long
	// endDate)
	// {
	// LOG.debug("get top recommended [items]: <cp,recommender> = <{},{}>", cp, recommender);
	//
	// long start = checkStartDate(startDate);
	// long end = checkEndDate(endDate);
	//
	// StatisticsDataController controller = (StatisticsDataController) context
	// .getAttribute(StatisticsDataController.STATISTICS_DATACONTROLLER);
	//
	// controller.checkCpExists(cp);
	// controller.checkRecommenderExists(cp, recommender);
	//
	// return controller.topRecommendedItems(cp, recommender, size, start, end);
	// }

	// @Override
	// public UserItemCount topRecommended(String cp, String recommender, Long user, Integer size)
	// {
	// LOG.debug("top recommended [user]  <cp,recommender> = <{},{}>", cp, recommender);
	//
	// nullPointerCheck(user, "User cannot be null.");
	//
	// StatisticsDataController controller = (StatisticsDataController) context
	// .getAttribute(StatisticsDataController.STATISTICS_DATACONTROLLER);
	//
	// controller.checkCpExists(cp);
	// controller.checkUserExists(cp, user);
	// controller.checkRecommenderExists(cp, recommender);
	//
	// return controller.topRecommended(cp, recommender, user, size);
	// }
	//
	// @Override
	// public Sources getSources(String cp, String recommender)
	// {
	// LOG.debug("get sources <cp,recommender> = <{},{}>", cp, recommender);
	//
	// StatisticsDataController controller = (StatisticsDataController) context
	// .getAttribute(StatisticsDataController.STATISTICS_DATACONTROLLER);
	//
	// controller.checkCpExists(cp);
	// controller.checkRecommenderExists(cp, recommender);
	//
	// return controller.getSources(cp, recommender);
	// }

	// @Override
	// public TopSources topSources(String cp, String recommender, Integer limit, Long startDate, Long endDate)
	// {
	// LOG.debug("top sources <cp,recommender> = <{},{}>", cp, recommender);
	//
	// long start = checkStartDate(startDate);
	// long end = checkEndDate(endDate);
	//
	// StatisticsDataController controller = (StatisticsDataController) context
	// .getAttribute(StatisticsDataController.STATISTICS_DATACONTROLLER);
	//
	// controller.checkCpExists(cp);
	// controller.checkRecommenderExists(cp, recommender);
	//
	// if (limit == null)
	// {
	// limit = -1;
	// }
	//
	// return controller.topSources(cp, recommender, limit, start, end);
	// }

	// @Override
	// public Count numberOfRequests(String cp, String recommender, String source, Long startDate, Long endDate)
	// {
	// LOG.debug("number of requests <cp,recommender> = <{},{}>", cp, recommender);
	//
	// nullPointerCheck(source, "Source cannot be null.");
	// long start = checkEndDate(endDate);
	// long end = checkStartDate(startDate);
	//
	// StatisticsDataController controller = (StatisticsDataController) context
	// .getAttribute(StatisticsDataController.STATISTICS_DATACONTROLLER);
	//
	// controller.checkCpExists(cp);
	// controller.checkRecommenderExists(cp, recommender);
	//
	// return controller.numberOfRequests(cp, recommender, source, start, end);
	// }

}
