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
package nl.gridline.zieook.workflow.model;

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
	public static final byte[] SIZE = Bytes.toBytes("size");
	public static final byte[] LOCATION = Bytes.toBytes("location");
	public static final byte[] TYPE = Bytes.toBytes("type");
	public static final byte[] INTERVAL = Bytes.toBytes("interval");
	public static final byte[] OAISETS = Bytes.toBytes("oai-sets");
	public static final byte[] OAISET_NAMES = Bytes.toBytes("oai-set-names");
	public static final byte[] OAISETS_USE = Bytes.toBytes("oai-sets-use");
	public static final byte[] OAIMETADATA = Bytes.toBytes("oai-metadata");
	public static final byte[] OAIMETADATA_USE = Bytes.toBytes("oai-metadata-use");
	public static final byte[] TAALSERVER = Bytes.toBytes("taalserver");
	// public static final byte[] RATINGLOW = Bytes.toBytes("rating_low");
	// public static final byte[] RATINGHIGH = Bytes.toBytes("rating_high");
	public static final byte[] NEXT = Bytes.toBytes("next");

	private ModelConstants()
	{
		// hide constructor
	}

	/**
	 * A naive JSON writer
	 * @return
	 */
	final static String toJSON(Object obj)
	{
		return new GsonBuilder().setPrettyPrinting().create().toJson(obj);
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

	static final Map<byte[], byte[]> putArray(Map<byte[], byte[]> map, byte[] key, String[] value, char split)
	{
		if (value != null)
		{
			if (value.length > 0)
			{
				StringBuilder b = new StringBuilder();
				for (String s : value)
				{
					b.append(s).append(split);
				}
				b.deleteCharAt(b.length() - 1);
				map.put(key, Bytes.toBytes(b.toString()));
			}
			else
			{
				map.put(key, Bytes.toBytes(""));
			}
		}
		return map;
	}

	static final String[] getArray(byte[] value, String[] defValue, char split)
	{
		if (value == null)
		{
			return defValue;
		}
		if (value.length == 0)
		{
			return new String[0];
		}
		String data = getString(value, null);
		return data.split(String.valueOf(split));
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
	 * add a boolean to the map
	 * @param map
	 * @param key
	 * @param value
	 * @return
	 */
	static final Map<byte[], byte[]> putBoolean(Map<byte[], byte[]> map, byte[] key, boolean value)
	{
		map.put(key, Bytes.toBytes(value));
		return map;
	}

	/**
	 * get a boolean from the map
	 * @param value
	 * @param defValue
	 * @return
	 */
	static final boolean getBoolean(byte[] value, boolean defValue)
	{
		if (value == null)
		{
			return defValue;
		}
		return Bytes.toBoolean(value);
	}

	static final long getUser(Map<byte[], byte[]> map)
	{
		return getLong(map.get(USER), -1);
	}

	/**
	 * put USER in the map
	 * @param map
	 * @param user
	 * @return
	 */
	static final Map<byte[], byte[]> putUser(Map<byte[], byte[]> map, long user)
	{
		return putLong(map, USER, user);
	}

	static final long getItem(Map<byte[], byte[]> map)
	{
		return getLong(map.get(ITEM), -1);
	}

	/**
	 * put ITEM in the map
	 * @param map
	 * @param item
	 * @return
	 */
	static final Map<byte[], byte[]> putItem(Map<byte[], byte[]> map, long item)
	{
		return putLong(map, ITEM, item);
	}

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
	static final long getStamp(Map<byte[], byte[]> map)
	{
		// this is a long value, not a string:
		return getLong(map.get(TIMESTAMP), Long.MIN_VALUE);
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
	 * @param map
	 * @return
	 */
	static final String getSource(Map<byte[], byte[]> map)
	{
		return getString(map.get(SOURCE), null);
	}

	/**
	 * put SOURCE in the map
	 * @param map
	 * @param source
	 * @return
	 */
	static final Map<byte[], byte[]> putSource(Map<byte[], byte[]> map, String source)
	{
		return putString(map, SOURCE, source);
	}

	/**
	 * @param map
	 * @return
	 */
	static final double getRating(Map<byte[], byte[]> map)
	{
		return getDouble(map.get(RATING), Double.MIN_VALUE);
	}

	/**
	 * put RATING in the map
	 * @param map
	 * @param rating
	 * @return
	 */
	static final Map<byte[], byte[]> putRating(Map<byte[], byte[]> map, double rating)
	{
		return putDouble(map, RATING, rating);
	}

	/**
	 * @param map
	 * @return
	 */
	static String getCp(Map<byte[], byte[]> map)
	{
		return getString(map.get(CP), null);
	}

	/**
	 * put CP in the map
	 * @param map
	 * @param cp
	 * @return
	 */
	static Map<byte[], byte[]> putCp(Map<byte[], byte[]> map, String cp)
	{
		return putString(map, CP, cp);
	}

	/**
	 * @param map
	 * @return
	 */
	static final String getCategory(Map<byte[], byte[]> map)
	{
		return getString(map.get(CATEGORY), null);
	}

	/**
	 * put CATEGORY in the map
	 * @param map
	 * @param category
	 * @return
	 */
	static final Map<byte[], byte[]> putCategory(Map<byte[], byte[]> map, String category)
	{
		return putString(map, CATEGORY, category);
	}

	/**
	 * @param map
	 * @return
	 */
	static final String getTitle(Map<byte[], byte[]> map)
	{
		return getString(map.get(TITLE), null);
	}

	/**
	 * put TITLE in the map
	 * @param map
	 * @param title
	 * @return
	 */
	static final Map<byte[], byte[]> putTitle(Map<byte[], byte[]> map, String title)
	{
		return putString(map, TITLE, title);
	}

	/**
	 * @param map
	 * @return
	 */
	static final String getDescription(Map<byte[], byte[]> map)
	{
		return getString(map.get(DESCRIPTION), null);
	}

	/**
	 * put DESCRIPTION in the map
	 * @param map
	 * @param description
	 * @return
	 */
	static final Map<byte[], byte[]> putDescription(Map<byte[], byte[]> map, String description)
	{
		return putString(map, DESCRIPTION, description);

	}

	/**
	 * @param map
	 * @return
	 */
	static final String getKeyWords(Map<byte[], byte[]> map)
	{
		return getString(map.get(KEYWORDS), null);
	}

	/**
	 * put KEYWORDS in to the map
	 * @param map
	 * @param keywords
	 * @return
	 */
	static final Map<byte[], byte[]> putKeyWords(Map<byte[], byte[]> map, String keywords)
	{
		return putString(map, KEYWORDS, keywords);
	}

	/**
	 * @param map
	 * @return
	 */
	static final String getName(Map<byte[], byte[]> map)
	{
		return getString(map.get(NAME), null);
	}

	/**
	 * put NAME in to the map
	 * @param map
	 * @param name
	 * @return
	 */
	static final Map<byte[], byte[]> putName(Map<byte[], byte[]> map, String name)
	{
		return putString(map, NAME, name);
	}

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
	static final Map<byte[], byte[]> putAge(Map<byte[], byte[]> map, String age)
	{
		return putString(map, AGE, age);

	}

	/**
	 * @param map
	 * @return
	 */
	static final String getGender(Map<byte[], byte[]> map)
	{
		return getString(map.get(GENDER), null);

	}

	/**
	 * put GENDER into the map
	 * @param map
	 * @param gender
	 * @return
	 */
	static final Map<byte[], byte[]> putGender(Map<byte[], byte[]> map, String gender)
	{
		return putString(map, GENDER, gender);

	}

	/**
	 * @param map
	 * @return
	 */
	static final String getOccupation(Map<byte[], byte[]> map)
	{
		return getString(map.get(OCCUPATION), null);

	}

	/**
	 * put OCCUPATION in the map
	 * @param map
	 * @param occupation
	 * @return
	 */
	static final Map<byte[], byte[]> putOccupation(Map<byte[], byte[]> map, String occupation)
	{
		return putString(map, OCCUPATION, occupation);

	}

	/**
	 * @param map
	 * @return
	 */
	static final String getZipCode(Map<byte[], byte[]> map)
	{
		return getString(map.get(ZIPCODE), null);

	}

	/**
	 * put ZIPCODE in the map
	 * @param map
	 * @param zipcode
	 * @return
	 */
	static final Map<byte[], byte[]> putZipCode(Map<byte[], byte[]> map, String zipcode)
	{
		return putString(map, ZIPCODE, zipcode);
	}

	/**
	 * @param map
	 * @return
	 */
	static final String getRecommender(Map<byte[], byte[]> map)
	{
		return getString(map.get(RECOMMENDER), null);
	}

	/**
	 * put RECOMMENDER in the map
	 * @param map
	 * @param recommender
	 * @return
	 */
	static final Map<byte[], byte[]> putRecommender(Map<byte[], byte[]> map, String recommender)
	{
		return putString(map, RECOMMENDER, recommender);
	}

	/**
	 * @param map
	 * @return
	 */
	static final long getSize(Map<byte[], byte[]> map)
	{
		return getLong(map.get(SIZE), Long.MIN_VALUE);
	}

	/**
	 * put SIZE in the map
	 * @param map
	 * @param size
	 * @return
	 */
	static final Map<byte[], byte[]> putSize(Map<byte[], byte[]> map, long size)
	{
		return putLong(map, SIZE, size);
	}

	/**
	 * @param map
	 * @return
	 */
	static final String getType(Map<byte[], byte[]> map)
	{
		return getString(map.get(TYPE), null);
	}

	/**
	 * put TYPE in the map
	 * @param map
	 * @param type
	 * @return
	 */
	static final Map<byte[], byte[]> putType(Map<byte[], byte[]> map, String type)
	{
		return putString(map, TYPE, type);
	}

	/**
	 * @param map
	 * @return
	 */
	static final String getLocation(Map<byte[], byte[]> map)
	{
		return getString(map.get(LOCATION), null);
	}

	/**
	 * put LOCATION in the map
	 * @param map
	 * @param location
	 * @return
	 */
	static final Map<byte[], byte[]> putLocation(Map<byte[], byte[]> map, String location)
	{
		return putString(map, LOCATION, location);

	}

	/**
	 * @param map
	 * @return
	 */
	static final String getCollection(Map<byte[], byte[]> map)
	{
		return getString(map.get(COLLECTION), null);
	}

	/**
	 * put COLLECTION in the map
	 * @param map
	 * @param collection
	 * @return
	 */
	static final Map<byte[], byte[]> putCollection(Map<byte[], byte[]> map, String collection)
	{
		return putString(map, COLLECTION, collection);
	}

	/**
	 * @param map
	 * @return
	 */
	static final long getInterval(Map<byte[], byte[]> map)
	{
		return getLong(map.get(INTERVAL), -1);
	}

	/**
	 * put INTERVAL in the map
	 * @param map
	 * @param interval
	 * @return
	 */
	static final Map<byte[], byte[]> putInterval(Map<byte[], byte[]> map, long interval)
	{
		return putLong(map, INTERVAL, interval);
	}

	/**
	 * return NEXT value from the map
	 * @param map
	 * @return
	 */
	final static long getNext(NavigableMap<byte[], byte[]> map)
	{
		return getLong(map.get(NEXT), Long.MIN_VALUE);
	}

	/**
	 * put NEXT value in the map
	 * @param map
	 * @param next
	 * @return
	 */
	final static Map<byte[], byte[]> putNext(NavigableMap<byte[], byte[]> map, long next)
	{
		return putLong(map, NEXT, next);
	}

	final static Map<byte[], byte[]> putOAISets(NavigableMap<byte[], byte[]> map, String[] set, char split)
	{
		return putArray(map, OAISETS, set, split);
	}

	final static String[] getOAISets(NavigableMap<byte[], byte[]> map, char split)
	{
		return getArray(map.get(OAISETS), null, split);
	}

	final static Map<byte[], byte[]> putOAISetsUse(NavigableMap<byte[], byte[]> map, String[] setUse, char split)
	{
		return putArray(map, OAISETS_USE, setUse, split);
	}

	final static String[] getOAISetsUse(NavigableMap<byte[], byte[]> map, char split)
	{
		return getArray(map.get(OAISETS_USE), null, split);
	}

	final static Map<byte[], byte[]> putOAIMetadata(NavigableMap<byte[], byte[]> map, String[] metadata, char split)
	{
		return putArray(map, OAIMETADATA, metadata, split);
	}

	final static String[] getOAIMetadata(NavigableMap<byte[], byte[]> map, char split)
	{
		return getArray(map.get(OAIMETADATA), null, split);
	}

	final static Map<byte[], byte[]> putOAIMetadataUse(NavigableMap<byte[], byte[]> map, String metadataUse)
	{
		return putString(map, OAIMETADATA_USE, metadataUse);
	}

	final static String getOAIMetadataUse(NavigableMap<byte[], byte[]> map)
	{
		return getString(map.get(OAIMETADATA_USE), null);
	}

	/**
	 * @param map
	 * @param c
	 * @return
	 */
	final static String[] getOAISetNames(NavigableMap<byte[], byte[]> map, char split)
	{
		return getArray(map.get(OAISET_NAMES), null, split);
	}

	/**
	 * @param map
	 * @param array
	 * @param c
	 */
	final static void putOAISetNames(NavigableMap<byte[], byte[]> map, String[] setnames, char split)
	{
		putArray(map, OAISET_NAMES, setnames, split);
	}

	/**
	 * @param map
	 * @return
	 */
	final static boolean getTaalserver(NavigableMap<byte[], byte[]> map)
	{
		return getBoolean(map.get(TAALSERVER), false);
	}

	/**
	 * @param map
	 * @param taalserver2
	 */
	final static Map<byte[], byte[]> putTaalserver(NavigableMap<byte[], byte[]> map, boolean taalserver)
	{
		return putBoolean(map, TAALSERVER, taalserver);
	}

}
