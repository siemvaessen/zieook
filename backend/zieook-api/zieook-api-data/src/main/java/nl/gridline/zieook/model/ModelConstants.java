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
package nl.gridline.zieook.model;

import java.util.Map;
import java.util.NavigableMap;

import org.apache.hadoop.hbase.util.Bytes;

import com.google.gson.GsonBuilder;

/**
 * An extensive set of translate methods that translate between Map<byte[],byte[]> (used in HBase) and readable elements
 * for the POJO's, xml, json versions of the objects
 * <p />
 * Project zieook-api-data<br />
 * MapUtils.java created 3 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public final class ModelConstants
{

	public static final byte[] USER = Bytes.toBytes("user");
	public static final byte[] ITEM = Bytes.toBytes("item");
	public static final byte[] COLLECTIONID = Bytes.toBytes("collection-id");
	public static final byte[] SOURCEITEM = Bytes.toBytes("sourceItem");
	public static final byte[] RANK = Bytes.toBytes("rank");
	public static final byte[] TIMESTAMP = Bytes.toBytes("timestamp");
	public static final byte[] SOURCE = Bytes.toBytes("source");
	public static final byte[] RATING = Bytes.toBytes("rating");
	public static final byte[] CP = Bytes.toBytes("cp");
	public static final byte[] COLLECTION = Bytes.toBytes("collection");
	public static final byte[] RECOMMENDER = Bytes.toBytes("recommender");
	public static final byte[] CATEGORY = Bytes.toBytes("category");
	public static final byte[] TITLE = Bytes.toBytes("title");
	public static final byte[] DESCRIPTION = Bytes.toBytes("description");
	public static final byte[] KEYWORDS = Bytes.toBytes("keywords");
	public static final byte[] NAME = Bytes.toBytes("name");
	public static final byte[] AGE = Bytes.toBytes("age");
	public static final byte[] GENDER = Bytes.toBytes("gender");
	public static final byte[] OCCUPATION = Bytes.toBytes("occuptation");
	public static final byte[] ZIPCODE = Bytes.toBytes("zipcode");
	public static final byte[] CITY = Bytes.toBytes("city");
	public static final byte[] COUNTRY = Bytes.toBytes("country");
	public static final byte[] SIZE = Bytes.toBytes("size");
	public static final byte[] COUNT = Bytes.toBytes("count");
	public static final byte[] VIEW_COUNT = Bytes.toBytes("view_count");
	public static final byte[] RECOMMEND_COUNT = Bytes.toBytes("recommend_count");
	public static final byte[] LOCATION = Bytes.toBytes("location");
	public static final byte[] IMAGE = Bytes.toBytes("image");
	public static final byte[] RECOMMENDER_TYPE = Bytes.toBytes("recommender-type");
	public static final byte[] POPULARITY = Bytes.toBytes("popularity");

	public static final byte[] LAST_VIEW_DATE = Bytes.toBytes("last-view");
	public static final byte[] LAST_RECOMMENDED_DATE = Bytes.toBytes("last-recommended");
	public static final byte[] LAST_RATED_DATE = Bytes.toBytes("last-rated");

	private ModelConstants()
	{
		// hide constructor
	}

	/**
	 * Get a long from the given byte[]
	 * @param value
	 * @param defValue
	 * @return
	 */
	static final long getLong(byte[] value, long defValue)
	{
		if (value == null)
		{
			return defValue;
		}
		try
		{
			return Bytes.toLong(value);
		}
		catch (IllegalArgumentException e)
		{
			return defValue;
		}
	}

	static final Long getLong(byte[] value, Long defValue)
	{
		if (value == null)
		{
			return defValue;
		}
		try
		{
			return Bytes.toLong(value);
		}
		catch (IllegalArgumentException e)
		{
			return defValue;
		}
	}

	static final int getInteger(byte[] value, int defValue)
	{
		if (value == null)
		{
			return defValue;
		}
		try
		{
			return Bytes.toInt(value);
		}
		catch (IllegalArgumentException e)
		{
			return defValue;
		}
	}

	/**
	 * Get a double from the given byte[]
	 * @param value
	 * @param defValue
	 * @return
	 */
	static final double getDouble(byte[] value, double defValue)
	{
		if (value == null)
		{
			return defValue;
		}
		try
		{
			return Bytes.toDouble(value);
		}
		catch (IllegalArgumentException e)
		{
			return defValue;
		}
	}

	/**
	 * Get a String from the given byte[]
	 * @param value
	 * @param defValue
	 * @return
	 */
	static final String getString(byte[] value, String defValue)
	{
		if (value == null)
		{
			return defValue;
		}
		return Bytes.toString(value);

	}

	/**
	 * add a long to the map by the given key
	 * @param map
	 * @param key
	 * @param value
	 * @return the map
	 */
	static final Map<byte[], byte[]> putLong(Map<byte[], byte[]> map, byte[] key, long value)
	{
		map.put(key, Bytes.toBytes(value));
		return map;
	}

	/**
	 * add a long to the map by the given key
	 * @param map
	 * @param key
	 * @param value
	 * @return the map
	 */
	static final Map<byte[], byte[]> putLong(Map<byte[], byte[]> map, byte[] key, Long value)
	{
		if (value != null)
		{
			map.put(key, Bytes.toBytes(value.longValue()));
		}
		return map;
	}

	static final Map<byte[], byte[]> putInteger(Map<byte[], byte[]> map, byte[] key, int value)
	{
		map.put(key, Bytes.toBytes(value));
		return map;
	}

	/**
	 * add a String the to map by the given key
	 * @param map
	 * @param key
	 * @param value
	 * @return the map
	 */
	static final Map<byte[], byte[]> putString(Map<byte[], byte[]> map, byte[] key, String value)
	{
		if (value != null && !value.isEmpty())
		{
			map.put(key, Bytes.toBytes(value));
		}
		return map;
	}

	/**
	 * add a double to the map by the given key
	 * @param map
	 * @param key
	 * @param value
	 * @return the map
	 */
	static final Map<byte[], byte[]> putDouble(Map<byte[], byte[]> map, byte[] key, double value)
	{
		map.put(key, Bytes.toBytes(value));
		return map;
	}

	/**
	 * return the USER from the map
	 * @param map
	 * @return
	 */
	static final Long getUser(Map<byte[], byte[]> map)
	{
		return getLong(map.get(USER), null);
	}

	/**
	 * put a USER in the map
	 * @param map
	 * @param user
	 * @return
	 */
	static final Map<byte[], byte[]> putUser(Map<byte[], byte[]> map, Long user)
	{
		return putLong(map, USER, user);
	}

	/**
	 * return the USER from the map
	 * @param map
	 * @return
	 */
	static final Long getUserLong(Map<byte[], byte[]> map)
	{
		return getLong(map.get(USER), null);
	}

	/**
	 * put a USER in the map
	 * @param map
	 * @param user
	 * @return
	 */
	static final Map<byte[], byte[]> putUserLong(Map<byte[], byte[]> map, Long user)
	{
		return putLong(map, USER, user);
	}

	/**
	 * get ITEM from the map
	 * @param map
	 * @return
	 */
	static final Long getItem(Map<byte[], byte[]> map)
	{
		return getLong(map.get(ITEM), null);
	}

	static final Long getItemLong(Map<byte[], byte[]> map)
	{
		return getLong(map.get(ITEM), null);
	}

	/**
	 * put ITEM in the map
	 * @param map
	 * @param item
	 * @return
	 */
	static final Map<byte[], byte[]> putItem(Map<byte[], byte[]> map, Long item)
	{
		return putLong(map, ITEM, item);
	}

	static final Map<byte[], byte[]> putItemLong(Map<byte[], byte[]> map, Long item)
	{
		return putLong(map, ITEM, item);
	}

	/**
	 * the original collection id
	 * @param map
	 * @return
	 */
	static final String getCollectionId(Map<byte[], byte[]> map)
	{
		return getString(map.get(COLLECTIONID), null);
	}

	/**
	 * the original collection id
	 * @param map
	 * @param item
	 * @return
	 */
	static final Map<byte[], byte[]> putCollectionId(Map<byte[], byte[]> map, String item)
	{
		return putString(map, COLLECTIONID, item);
	}

	/**
	 * get SOURCEITEM from the map
	 * @param map
	 * @return
	 */
	static final long getSourceItem(Map<byte[], byte[]> map)
	{
		return getLong(map.get(SOURCEITEM), -1);
	}

	/**
	 * put SOURCEITEM in the map
	 * @param map
	 * @param sourceItem
	 * @return
	 */
	static final Map<byte[], byte[]> putSourceItem(Map<byte[], byte[]> map, long sourceItem)
	{
		return putLong(map, SOURCEITEM, sourceItem);
	}

	/**
	 * get RANK from the map
	 * @param map
	 * @return
	 */
	static final long getRank(Map<byte[], byte[]> map)
	{
		return getLong(map.get(RANK), -1);
	}

	/**
	 * put RANK in the map
	 * @param map
	 * @param rank
	 * @return
	 */
	static final Map<byte[], byte[]> putRank(Map<byte[], byte[]> map, long rank)
	{
		return putLong(map, RANK, rank);
	}

	/**
	 * get TIMESTAMP from the map
	 * @param map
	 * @return
	 */
	static final Long getStamp(Map<byte[], byte[]> map)
	{
		return getLong(map.get(TIMESTAMP), null);
	}

	/**
	 * put TIMESTEMP in the map
	 * @param map
	 * @param stamp
	 * @return
	 */
	static final Map<byte[], byte[]> putStamp(Map<byte[], byte[]> map, Long stamp)
	{
		if (stamp != null)
		{
			return putLong(map, TIMESTAMP, stamp);
		}
		return map;
	}

	/**
	 * get the SOURCE from the map
	 * @param map
	 * @return
	 */
	static final String getSource(Map<byte[], byte[]> map)
	{
		return getString(map.get(SOURCE), null);
	}

	/**
	 * put the SOURCE in the map
	 * @param map
	 * @param source
	 * @return
	 */
	static final Map<byte[], byte[]> putSource(Map<byte[], byte[]> map, String source)
	{
		return putString(map, SOURCE, source);
	}

	/**
	 * get the RATING from the map
	 * @param map
	 * @return
	 */
	static final double getRating(Map<byte[], byte[]> map)
	{
		return getDouble(map.get(RATING), Double.MIN_VALUE);
	}

	/**
	 * put the RATING in the map
	 * @param map
	 * @param rating
	 * @return
	 */
	static final Map<byte[], byte[]> putRating(Map<byte[], byte[]> map, double rating)
	{
		return putDouble(map, RATING, rating);
	}

	/**
	 * get the CP from the map
	 * @param map
	 * @return
	 */
	static final String getCp(Map<byte[], byte[]> map)
	{
		return getString(map.get(CP), null);
	}

	/**
	 * put the CP in the map
	 * @param map
	 * @param cp
	 * @return
	 */
	static final Map<byte[], byte[]> putCp(Map<byte[], byte[]> map, String cp)
	{
		return putString(map, CP, cp);
	}

	/**
	 * get the CATEGORY from the map
	 * @param map
	 * @return
	 */
	static final String getCategory(Map<byte[], byte[]> map)
	{
		return getString(map.get(CATEGORY), null);
	}

	/**
	 * put the CATEGORY in the map
	 * @param map
	 * @param category
	 * @return
	 */
	static final Map<byte[], byte[]> putCategory(Map<byte[], byte[]> map, String category)
	{
		return putString(map, CATEGORY, category);
	}

	/**
	 * get the TITLE from the map
	 * @param map
	 * @return
	 */
	static final String getTitle(Map<byte[], byte[]> map)
	{
		return getString(map.get(TITLE), null);
	}

	/**
	 * put the TITLE in the map
	 * @param map
	 * @param title
	 * @return
	 */
	static final Map<byte[], byte[]> putTitle(Map<byte[], byte[]> map, String title)
	{
		return putString(map, TITLE, title);
	}

	/**
	 * return the DESCRIPTION from the map
	 * @param map
	 * @return
	 */
	static final String getDescription(Map<byte[], byte[]> map)
	{
		return getString(map.get(DESCRIPTION), null);
	}

	/**
	 * put the DESCRIPTION in the map
	 * @param map
	 * @param description
	 * @return
	 */
	static final Map<byte[], byte[]> putDescription(Map<byte[], byte[]> map, String description)
	{
		return putString(map, DESCRIPTION, description);

	}

	/**
	 * get the KEYWORD from the map
	 * @param map
	 * @return
	 */
	static final String getKeyWords(Map<byte[], byte[]> map)
	{
		return getString(map.get(KEYWORDS), null);
	}

	/**
	 * put the KEYWORDS in the map
	 * @param map
	 * @param keywords
	 * @return
	 */
	static final Map<byte[], byte[]> putKeyWords(Map<byte[], byte[]> map, String keywords)
	{
		return putString(map, KEYWORDS, keywords);
	}

	/**
	 * get NAME from the map
	 * @param map
	 * @return
	 */
	static final String getName(Map<byte[], byte[]> map)
	{
		return getString(map.get(NAME), null);
	}

	static final Map<byte[], byte[]> putName(Map<byte[], byte[]> map, String name)
	{
		return putString(map, NAME, name);
	}

	/**
	 * get AGE from the map
	 * @param map
	 * @return
	 */
	static final String getAge(Map<byte[], byte[]> map)
	{
		return getString(map.get(AGE), null);

	}

	/**
	 * put AGE in the map
	 * @param map
	 * @param age
	 * @return
	 */
	static final Map<byte[], byte[]> putBirthday(Map<byte[], byte[]> map, String age)
	{
		return putString(map, AGE, age);

	}

	/**
	 * get the GENDER from the map
	 * @param map
	 * @return
	 */
	static final String getGender(Map<byte[], byte[]> map)
	{
		return getString(map.get(GENDER), null);

	}

	/**
	 * put the GENDER in the map
	 * @param map
	 * @param gender
	 * @return
	 */
	static final Map<byte[], byte[]> putGender(Map<byte[], byte[]> map, String gender)
	{
		return putString(map, GENDER, gender);

	}

	/**
	 * get OCCUPATION from the map
	 * @param map
	 * @return
	 */
	static final String getOccupation(Map<byte[], byte[]> map)
	{
		return getString(map.get(OCCUPATION), null);

	}

	/**
	 * put {@code OCCUPATION} in the map
	 * @param map
	 * @param occupation
	 * @return
	 */
	static final Map<byte[], byte[]> putOccupation(Map<byte[], byte[]> map, String occupation)
	{
		return putString(map, OCCUPATION, occupation);

	}

	/**
	 * get {@code CITY} from the map
	 * @param map
	 * @return
	 */
	static final String getCity(Map<byte[], byte[]> map)
	{
		return getString(map.get(CITY), null);
	}

	/**
	 * put the {@code CITY} in the map
	 * @param map
	 * @param zipcode
	 * @return
	 */
	static final Map<byte[], byte[]> putCity(Map<byte[], byte[]> map, String zipcode)
	{
		return putString(map, CITY, zipcode);
	}

	/**
	 * get {@code COUNTRY} from the map
	 * @param map
	 * @return
	 */
	static final String getCountry(Map<byte[], byte[]> map)
	{
		return getString(map.get(COUNTRY), null);
	}

	/**
	 * put the {@code COUNTRY} in the map
	 * @param map
	 * @param zipcode
	 * @return
	 */
	static final Map<byte[], byte[]> putCountry(Map<byte[], byte[]> map, String zipcode)
	{
		return putString(map, COUNTRY, zipcode);
	}

	/**
	 * get {@code ZIPCODE} from the map
	 * @param map
	 * @return
	 */
	static final String getZipCode(Map<byte[], byte[]> map)
	{
		return getString(map.get(ZIPCODE), null);
	}

	/**
	 * put the {@code ZIPCODE} in the map
	 * @param map
	 * @param zipcode
	 * @return
	 */
	static final Map<byte[], byte[]> putZipCode(Map<byte[], byte[]> map, String zipcode)
	{
		return putString(map, ZIPCODE, zipcode);
	}

	/**
	 * get the {@code RECOMMENDER} from the map
	 * @param map
	 * @return
	 */
	static final String getRecommender(Map<byte[], byte[]> map)
	{
		return getString(map.get(RECOMMENDER), null);
	}

	/**
	 * put the {@code RECOMMENDER} in the map
	 * @param map
	 * @param recommender
	 * @return
	 */
	static final Map<byte[], byte[]> putRecommender(Map<byte[], byte[]> map, String recommender)
	{
		return putString(map, RECOMMENDER, recommender);
	}

	/**
	 * get the {@code SIZE} from the map
	 * @param map
	 * @return
	 */
	static final int getSize(Map<byte[], byte[]> map)
	{
		return getInteger(map.get(SIZE), 0);
	}

	/**
	 * put the {@code SIZE} in the map
	 * @param map
	 * @param size
	 * @return
	 */
	static final Map<byte[], byte[]> putSize(Map<byte[], byte[]> map, int size)
	{
		return putInteger(map, SIZE, size);
	}

	/**
	 * get the {@code COLLECTION} from the map
	 * @param map
	 * @return
	 */
	static final String getCollection(Map<byte[], byte[]> map)
	{
		return getString(map.get(COLLECTION), null);
	}

	/**
	 * put the {@code COLLECTION}
	 * @param map
	 * @param collection
	 * @return
	 */
	static final Map<byte[], byte[]> putCollection(Map<byte[], byte[]> map, String collection)
	{
		return putString(map, COLLECTION, collection);
	}

	static final Long getCount(Map<byte[], byte[]> map)
	{
		return getLong(map.get(COUNT), null);
	}

	static final Map<byte[], byte[]> putCount(Map<byte[], byte[]> map, Long count)
	{
		return putLong(map, COUNT, count);
	}

	static final Long getViewCount(Map<byte[], byte[]> map)
	{
		return getLong(map.get(VIEW_COUNT), null);
	}

	static final Map<byte[], byte[]> putViewCount(Map<byte[], byte[]> map, Long count)
	{
		return putLong(map, VIEW_COUNT, count);
	}

	static final Long getRecommendCount(Map<byte[], byte[]> map)
	{
		return getLong(map.get(RECOMMEND_COUNT), null);
	}

	static final Map<byte[], byte[]> putRecommendCount(Map<byte[], byte[]> map, Long count)
	{
		return putLong(map, RECOMMEND_COUNT, count);
	}

	static final Map<byte[], byte[]> putLocation(Map<byte[], byte[]> map, String location)
	{
		return putString(map, LOCATION, location);
	}

	static final String getLocation(Map<byte[], byte[]> map)
	{
		return getString(map.get(LOCATION), null);
	}

	static final Map<byte[], byte[]> putImage(Map<byte[], byte[]> map, String image)
	{
		return putString(map, IMAGE, image);
	}

	static final String getImage(Map<byte[], byte[]> map)
	{
		return getString(map.get(IMAGE), null);
	}

	/**
	 * @param map
	 * @param value
	 */
	static final Map<byte[], byte[]> putRecommenderType(NavigableMap<byte[], byte[]> map, String value)
	{
		return putString(map, RECOMMENDER_TYPE, value);
	}

	/**
	 * @param map
	 * @return
	 */
	static final String getRecommenderType(NavigableMap<byte[], byte[]> map)
	{
		return getString(map.get(RECOMMENDER_TYPE), null);
	}

	static final Map<byte[], byte[]> putPopularity(NavigableMap<byte[], byte[]> map, int value)
	{
		return putInteger(map, POPULARITY, value);
	}

	/**
	 * @param map
	 * @return
	 */
	static final int getPopularity(NavigableMap<byte[], byte[]> map)
	{
		return getInteger(map.get(POPULARITY), 0);
	}

	static final Long getLastViewedDate(NavigableMap<byte[], byte[]> map)
	{
		return getLong(map.get(LAST_VIEW_DATE), null);
	}

	static final Map<byte[], byte[]> putLastViewedDate(NavigableMap<byte[], byte[]> map, Long date)
	{
		return putLong(map, LAST_VIEW_DATE, date);
	}

	static final Long getLastRatedDate(NavigableMap<byte[], byte[]> map)
	{
		return getLong(map.get(LAST_RATED_DATE), null);
	}

	static final Map<byte[], byte[]> putLastRatedDate(NavigableMap<byte[], byte[]> map, Long date)
	{
		return putLong(map, LAST_RATED_DATE, date);
	}

	static final Long getLastRecommendDate(NavigableMap<byte[], byte[]> map)
	{
		return getLong(map.get(LAST_RECOMMENDED_DATE), null);
	}

	static final Map<byte[], byte[]> putLastRecommendDate(NavigableMap<byte[], byte[]> map, Long date)
	{
		return putLong(map, LAST_RECOMMENDED_DATE, null);
	}

	/**
	 * A naive JSON writer
	 * @return
	 */
	static final String toJSON(Object obj)
	{
		return new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create().toJson(obj);
	}

}
