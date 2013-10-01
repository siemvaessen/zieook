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

import java.util.Collection;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

/**
 * This class implements a recommender and is reading it's data from directly from the ZieOok data store.
 * <p />
 * Project zieook-runner<br />
 * ZieOokRecommener.java created 7 feb. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class ZieOokRecommener implements Recommender
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mahout.cf.taste.common.Refreshable#refresh(java.util.Collection)
	 */
	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed)
	{
		// TODO Auto-generated method stub
		// probably do nothing -

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mahout.cf.taste.recommender.Recommender#recommend(long, int)
	 */
	@Override
	public List<RecommendedItem> recommend(long userID, int howMany) throws TasteException
	{
		// make a call to the user table and get 'howMany' recommendations for this userID

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mahout.cf.taste.recommender.Recommender#recommend(long, int,
	 * org.apache.mahout.cf.taste.recommender.IDRescorer)
	 */
	@Override
	public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer) throws TasteException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mahout.cf.taste.recommender.Recommender#estimatePreference(long, long)
	 */
	@Override
	public float estimatePreference(long userID, long itemID) throws TasteException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mahout.cf.taste.recommender.Recommender#setPreference(long, long, float)
	 */
	@Override
	public void setPreference(long userID, long itemID, float value) throws TasteException
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mahout.cf.taste.recommender.Recommender#removePreference(long, long)
	 */
	@Override
	public void removePreference(long userID, long itemID) throws TasteException
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mahout.cf.taste.recommender.Recommender#getDataModel()
	 */
	@Override
	public DataModel getDataModel()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
