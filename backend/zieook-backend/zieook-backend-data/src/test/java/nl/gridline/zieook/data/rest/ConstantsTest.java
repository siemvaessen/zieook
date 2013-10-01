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

import org.junit.Ignore;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-data<br />
 * ConstantsTest.java created 11 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Ignore
public final class ConstantsTest
{
	private ConstantsTest()
	{

	}

	public static final String CP = "gridline-movielens";
	public static final String RECOMMENDER = "movielens-tanimo";
	public static final String COLLECTION = "movielens";
	public static final long USER = 50;
	public static final long ITEM = 100;
	public static final String SOURCE = "theSource";
	public static final double RATING = 1.0;
	public static final long RANK = 2;
	public static final String KEY = "myCpMetaDatakey";
	public static final long TIME = System.currentTimeMillis() / 1000;
	public static final long TIMESTR = TIME;
	public static final long ENDTIME = System.currentTimeMillis() / 1000;
	public static final long STARTTIME = ENDTIME - (60 * 60 * 24);
	public static final int SIZE = 10;

}
