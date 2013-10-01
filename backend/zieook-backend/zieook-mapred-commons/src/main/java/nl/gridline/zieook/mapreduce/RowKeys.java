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
package nl.gridline.zieook.mapreduce;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import nl.gridline.zieook.model.UserView;

import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * HBaseRowKeys.java created 4 jan. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public final class RowKeys
{

	private static final Logger LOG = LoggerFactory.getLogger(RowKeys.class);

	private static final String SPLITSTR = "/";
	private static final byte[] SPLIT = Bytes.toBytes(SPLITSTR);

	private static final String LOCATION = "nl";
	private static final String RATING = "R";

	private static final String USER_VIEW_PREFIX = "user";
	private static final String USER_VIEW_INDEX = "user_t";

	// TODO update these prefixes (shorten)
	private static final String STATS_RECOMMENDED_ITEM = "recommended-item";
	private static final String STATS_RECOMMENDED = "recommended";
	private static final String STATS_SOURCES_PREFIX = "stat-sources";
	private static final String STATS_SOURCES_COLLLECTION_PREFIX = "stat-sources_col";
	private static final String STATS_RATINGS_PERITEM_PREFIX = "stat-rating-item";
	private static final String STATS_RATINGS_USER_PREFIX = "stat-rating-user";
	private static final String STATS_VIEWED = "stat-viewed";
	private static final String STATS_POPULARITY = "stat_popularity";
	private static final String METADATA_PREFIX = "collection/metadata";

	private static final String RECOMMENDER_PREFIX = "recommender";
	private static final String TASK = "task";

	/**
	 * Concatenates the given parameters in a single byte array
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param e
	 * @param f
	 * @return
	 */
	private static byte[] getRowKey(final byte[] a, final byte[] b, final byte[] c, final byte[] d, byte[] e, byte[] f,
			byte[] g)
	{
		final byte[] result = new byte[a.length + b.length + c.length + d.length + e.length + f.length + g.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		System.arraycopy(c, 0, result, a.length + b.length, c.length);
		System.arraycopy(d, 0, result, a.length + b.length + c.length, d.length);
		System.arraycopy(e, 0, result, a.length + b.length + c.length + d.length, e.length);
		System.arraycopy(f, 0, result, a.length + b.length + c.length + d.length + e.length, f.length);
		System.arraycopy(g, 0, result, a.length + b.length + c.length + d.length + e.length + f.length, g.length);
		return result;
	}

	/**
	 * Concatenates the given parameters in a single byte array
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param e
	 * @param f
	 * @return
	 */
	private static byte[] getRowKey(final byte[] a, final byte[] b, final byte[] c, final byte[] d, byte[] e, byte[] f)
	{
		final byte[] result = new byte[a.length + b.length + c.length + d.length + e.length + f.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		System.arraycopy(c, 0, result, a.length + b.length, c.length);
		System.arraycopy(d, 0, result, a.length + b.length + c.length, d.length);
		System.arraycopy(e, 0, result, a.length + b.length + c.length + d.length, e.length);
		System.arraycopy(f, 0, result, a.length + b.length + c.length + d.length + e.length, f.length);
		return result;
	}

	/**
	 * Concatenates the given parameters in a single byte array
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param e
	 * @return
	 */
	private static byte[] getRowKey(final byte[] a, final byte[] b, final byte[] c, final byte[] d, byte[] e)
	{

		final byte[] result = new byte[a.length + b.length + c.length + d.length + e.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		System.arraycopy(c, 0, result, a.length + b.length, c.length);
		System.arraycopy(d, 0, result, a.length + b.length + c.length, d.length);
		System.arraycopy(e, 0, result, a.length + b.length + c.length + d.length, e.length);
		return result;
	}

	/**
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	private static byte[] getRowKey(final byte[] a, final byte[] b, final byte[] c, final byte[] d)
	{

		final byte[] result = new byte[a.length + b.length + c.length + d.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		System.arraycopy(c, 0, result, a.length + b.length, c.length);
		System.arraycopy(d, 0, result, a.length + b.length + c.length, d.length);
		return result;
	}

	private static byte[] getRowKey(final byte[] a, final byte[] b, final byte[] c)
	{
		final byte[] result = new byte[a.length + b.length + c.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		System.arraycopy(c, 0, result, a.length + b.length, c.length);
		return result;
	}

	private static byte[] getRowKey(final byte[] a, final byte[] b)
	{
		final byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	/**
	 * Get the row key, uses SPLIT character / in between the string and url encodes the arguments.
	 * @param a
	 * @param b
	 * @param c
	 * @return <tt>key1/key2/key3</tt>
	 */
	protected static byte[] getRowKey(final String aStr, final String bStr, final String cStr)
	{
		try
		{
			final byte[] a = Bytes.toBytes(URLEncoder.encode(aStr, HConstants.UTF8_ENCODING));
			final byte[] b = Bytes.toBytes(URLEncoder.encode(bStr, HConstants.UTF8_ENCODING));
			final byte[] c = Bytes.toBytes(URLEncoder.encode(cStr, HConstants.UTF8_ENCODING));

			return getRowKey(a, SPLIT, b, SPLIT, c);
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	/**
	 * Get the row key, uses SPLIT character / in between the strings and url encodes the arguments
	 * @param aStr
	 * @param bStr
	 * @return
	 */
	protected static byte[] getRowKey(String aStr, String bStr)
	{
		try
		{
			final byte[] a = Bytes.toBytes(URLEncoder.encode(aStr, HConstants.UTF8_ENCODING));
			final byte[] b = Bytes.toBytes(URLEncoder.encode(bStr, HConstants.UTF8_ENCODING));

			return getRowKey(a, SPLIT, b);
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static String[] getStrRowKey(byte[] row)
	{
		String result = Bytes.toString(row);
		try
		{
			return new String[]{URLDecoder.decode(result, HConstants.UTF8_ENCODING)};
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static byte[] getRowKey(String aStr)
	{
		try
		{
			return Bytes.toBytes(URLEncoder.encode(aStr, HConstants.UTF8_ENCODING));
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static String[] getStrStrRowKey(byte[] row)
	{
		String s = Bytes.toString(row);
		String[] ss = s.split(SPLITSTR);
		String[] result = new String[2];
		try
		{
			result[1] = URLDecoder.decode(ss[1], HConstants.UTF8_ENCODING);
			result[0] = URLDecoder.decode(ss[0], HConstants.UTF8_ENCODING);
			return result;
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	/**
	 * Never ever use this method, it has a bug - a missing SPLIT between the string and the long part.
	 * This is not a problem for the existing callers, so we let them be for now.
	 * @param aStr
	 * @param bLng
	 * @return
	 */
	@Deprecated
	protected static byte[] getRowKeyOld(String aStr, long bLng)
	{
		try
		{
			final byte[] a = Bytes.toBytes(URLEncoder.encode(aStr, HConstants.UTF8_ENCODING));
			final byte[] b = Bytes.toBytes(bLng);
			return getRowKey(a, b);
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static byte[] getRowKeyNew(String aStr, long bLng)
	{
		try
		{
			final byte[] a = Bytes.toBytes(URLEncoder.encode(aStr, HConstants.UTF8_ENCODING));
			final byte[] b = Bytes.toBytes(bLng);
			return getRowKey(a, SPLIT, b);
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	public static Object[] getStrLngRowKey(byte[] row)
	{
		Object[] result = new Object[2];
		result[1] = Bytes.toLong(row, row.length - Bytes.SIZEOF_LONG);
		result[0] = Bytes.toString(row, 0, row.length - Bytes.SIZEOF_LONG);
		return result;
	}

	protected static byte[] getRowKey(String aStr, long bLng, long cLng)
	{
		try
		{
			final byte[] a = Bytes.toBytes(URLEncoder.encode(aStr, HConstants.UTF8_ENCODING));
			final byte[] b = Bytes.toBytes(bLng);
			final byte[] c = Bytes.toBytes(cLng);
			return getRowKey(a, b, c);
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static Object[] getStrLngLng(byte[] row)
	{
		Object[] result = new Object[3];
		result[2] = Bytes.toLong(row, row.length - Bytes.SIZEOF_LONG);
		result[1] = Bytes.toLong(row, row.length - (Bytes.SIZEOF_LONG * 2), Bytes.SIZEOF_LONG);
		result[0] = Bytes.toString(row, 0, row.length - (Bytes.SIZEOF_LONG * 2));
		return result;
	}

	protected static byte[] getRowKey(String aStr, String bStr, long cLng)
	{
		try
		{
			final byte[] a = Bytes.toBytes(URLEncoder.encode(aStr, HConstants.UTF8_ENCODING));
			final byte[] b = Bytes.toBytes(URLEncoder.encode(bStr, HConstants.UTF8_ENCODING));
			final byte[] c = Bytes.toBytes(cLng);
			return getRowKey(a, SPLIT, b, c);
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static Object[] getStrStrLng(byte[] row)
	{
		Object[] result = new Object[3];
		result[2] = Bytes.toLong(row, row.length - Bytes.SIZEOF_LONG);
		String s = Bytes.toString(row, 0, row.length - Bytes.SIZEOF_LONG);
		String[] ss = s.split(SPLITSTR);
		try
		{
			result[1] = URLDecoder.decode(ss[1], HConstants.UTF8_ENCODING);
			result[0] = URLDecoder.decode(ss[0], HConstants.UTF8_ENCODING);
			return result;
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static byte[] getRowKey(String aStr, String bStr, long cLng, long dLng)
	{
		try
		{
			final byte[] a = Bytes.toBytes(URLEncoder.encode(aStr, HConstants.UTF8_ENCODING));
			final byte[] b = Bytes.toBytes(URLEncoder.encode(bStr, HConstants.UTF8_ENCODING));
			final byte[] c = Bytes.toBytes(cLng);
			final byte[] d = Bytes.toBytes(dLng);
			return getRowKey(a, SPLIT, b, c, d);
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	public static Object[] getStrStrLngLng(byte[] row)
	{
		Object[] result = new Object[4];

		result[3] = Bytes.toLong(row, row.length - Bytes.SIZEOF_LONG);
		result[2] = Bytes.toLong(row, row.length - (2 * Bytes.SIZEOF_LONG), Bytes.SIZEOF_LONG);

		String s = Bytes.toString(row, 0, row.length - (2 * Bytes.SIZEOF_LONG));
		String[] ss = s.split(SPLITSTR);
		try
		{
			result[1] = URLDecoder.decode(ss[1], HConstants.UTF8_ENCODING);
			result[0] = URLDecoder.decode(ss[0], HConstants.UTF8_ENCODING);
			return result;
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static byte[] getRowKey(String aStr, String bStr, String cStr, long dLng)
	{
		try
		{
			final byte[] a = Bytes.toBytes(URLEncoder.encode(aStr, HConstants.UTF8_ENCODING));
			final byte[] b = Bytes.toBytes(URLEncoder.encode(bStr, HConstants.UTF8_ENCODING));
			final byte[] c = Bytes.toBytes(URLEncoder.encode(cStr, HConstants.UTF8_ENCODING));
			final byte[] d = Bytes.toBytes(dLng);
			return getRowKey(a, SPLIT, b, SPLIT, c, d);
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static Object[] getStrStrStrLng(byte[] row)
	{
		Object[] result = new Object[4];
		result[3] = Bytes.toLong(row, row.length - Bytes.SIZEOF_LONG);
		String s = Bytes.toString(row, 0, row.length - Bytes.SIZEOF_LONG);
		String[] ss = s.split(SPLITSTR);
		try
		{
			result[2] = URLDecoder.decode(ss[2], HConstants.UTF8_ENCODING);
			result[1] = URLDecoder.decode(ss[1], HConstants.UTF8_ENCODING);
			result[0] = URLDecoder.decode(ss[0], HConstants.UTF8_ENCODING);
			return result;
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static byte[] getRowKey(String aStr, String bStr, long cLng, long dLng, long eLng)
	{
		try
		{
			final byte[] a = Bytes.toBytes(URLEncoder.encode(aStr, HConstants.UTF8_ENCODING));
			final byte[] b = Bytes.toBytes(URLEncoder.encode(bStr, HConstants.UTF8_ENCODING));
			final byte[] c = Bytes.toBytes(cLng);
			final byte[] d = Bytes.toBytes(dLng);
			final byte[] e = Bytes.toBytes(eLng);

			return getRowKey(a, SPLIT, b, c, d, e);
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	public static Object[] getStrStrLngLngLng(byte[] row)
	{
		Object[] result = new Object[5];
		result[4] = Bytes.toLong(row, row.length - Bytes.SIZEOF_LONG, Bytes.SIZEOF_LONG);
		result[3] = Bytes.toLong(row, row.length - (Bytes.SIZEOF_LONG * 2), Bytes.SIZEOF_LONG);
		result[2] = Bytes.toLong(row, row.length - (Bytes.SIZEOF_LONG * 3), Bytes.SIZEOF_LONG);
		String s = Bytes.toString(row, 0, row.length - (Bytes.SIZEOF_LONG * 3));
		String[] ss = s.split(SPLITSTR);
		try
		{
			result[1] = URLDecoder.decode(ss[1], HConstants.UTF8_ENCODING);
			result[0] = URLDecoder.decode(ss[0], HConstants.UTF8_ENCODING);
			return result;
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static byte[] getRowKey(String aStr, long bLng, String cStr, long dLng, long eLng)
	{
		try
		{
			final byte[] a = Bytes.toBytes(URLEncoder.encode(aStr, HConstants.UTF8_ENCODING));
			final byte[] b = Bytes.toBytes(bLng);
			final byte[] c = Bytes.toBytes(URLEncoder.encode(cStr, HConstants.UTF8_ENCODING));
			final byte[] d = Bytes.toBytes(dLng);
			final byte[] e = Bytes.toBytes(eLng);

			return getRowKey(a, SPLIT, b, SPLIT, c, d, e);
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static Object[] getStrLngStrLngLng(byte[] row)
	{
		Object[] result = new Object[5];
		result[4] = Bytes.toLong(row, row.length - Bytes.SIZEOF_LONG, Bytes.SIZEOF_LONG);
		result[3] = Bytes.toLong(row, row.length - (2 * Bytes.SIZEOF_LONG), Bytes.SIZEOF_LONG);
		String s = Bytes.toString(row, 0, row.length - (Bytes.SIZEOF_LONG * 2));
		String[] ss = s.split(SPLITSTR);
		result[1] = Bytes.toLong(row, (ss[0].length()) + SPLIT.length, Bytes.SIZEOF_LONG);
		try
		{
			result[0] = URLDecoder.decode(ss[0], HConstants.UTF8_ENCODING);
			result[2] = URLDecoder.decode(ss[2], HConstants.UTF8_ENCODING);
			return result;
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static byte[] getRowKey(String aStr, long bLng, String cStr, long dLng)
	{
		try
		{
			final byte[] a = Bytes.toBytes(URLEncoder.encode(aStr, HConstants.UTF8_ENCODING));
			final byte[] b = Bytes.toBytes(bLng);
			final byte[] c = Bytes.toBytes(URLEncoder.encode(cStr, HConstants.UTF8_ENCODING));
			final byte[] d = Bytes.toBytes(dLng);

			return getRowKey(a, SPLIT, b, SPLIT, c, d);
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static Object[] getStrLngStrLng(byte[] row)
	{

		Object[] result = new Object[4];
		result[3] = Bytes.toLong(row, row.length - Bytes.SIZEOF_LONG, Bytes.SIZEOF_LONG);

		String s = Bytes.toString(row, 0, row.length - Bytes.SIZEOF_LONG);
		String[] ss = s.split(SPLITSTR);
		result[1] = Bytes.toLong(row, (ss[0].length()) + SPLIT.length, Bytes.SIZEOF_LONG);
		try
		{
			result[0] = URLDecoder.decode(ss[0], HConstants.UTF8_ENCODING);
			result[2] = URLDecoder.decode(ss[2], HConstants.UTF8_ENCODING);
			return result;
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static byte[] getRowKey(String aStr, long bLng, String cStr)
	{
		try
		{
			final byte[] a = Bytes.toBytes(URLEncoder.encode(aStr, HConstants.UTF8_ENCODING));
			final byte[] b = Bytes.toBytes(bLng);
			final byte[] c = Bytes.toBytes(URLEncoder.encode(cStr, HConstants.UTF8_ENCODING));

			return getRowKey(a, SPLIT, b, SPLIT, c);
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	protected static Object[] getStrLngStr(byte[] row)
	{
		Object[] result = new Object[3];
		String s = Bytes.toString(row, 0, row.length);
		String[] ss = s.split(SPLITSTR);
		result[1] = Bytes.toLong(row, (ss[0].length()) + SPLIT.length, Bytes.SIZEOF_LONG);
		try
		{
			result[0] = URLDecoder.decode(ss[0], HConstants.UTF8_ENCODING);
			result[2] = URLDecoder.decode(ss[2], HConstants.UTF8_ENCODING);
			return result;
		}
		catch (UnsupportedEncodingException e)
		{
			LOG.error("fatal: UTF-8 not supported?", e);
			return null;
		}
	}

	// --------------------------
	// -- Implementations -------
	// --------------------------

	public static byte[] getCollectionKey(String collection)
	{
		return getRowKey(collection);
	}

	/**
	 * return a row key
	 * @param collection
	 * @param item
	 * @return <tt>collection/item</tt>
	 */
	public static byte[] getCollectionKey(String collection, long item)
	{
		return getRowKeyOld(collection, item);
	}

	/**
	 * return a row key
	 * @param cp
	 * @param collection
	 * @return <tt>cp/collection</tt>
	 */
	public static byte[] getCollectionMetaKey(String cp, String collection)
	{
		return getRowKey(METADATA_PREFIX, cp, collection);
	}

	/**
	 * return a row key
	 * @param cp
	 * @param collection
	 * @return <tt>cp/collection</tt>
	 */
	public static byte[] getCollectionMetaKey(String cp)
	{
		return getRowKey(METADATA_PREFIX, cp);
	}

	/**
	 * return a row key
	 * @param cp
	 * @param collection
	 * @return <tt>cp/collection</tt>
	 */
	public static byte[] getCollectionMetaKey()
	{
		return getRowKey(METADATA_PREFIX);
	}

	/**
	 * return a row key
	 * @param collection
	 * @param item
	 * @return <tt>collection/item</tt>
	 */
	public static byte[] getRecommendationKey(String collection, String recommender, long item)
	{
		return getRowKey(collection, recommender, item);
	}

	/**
	 * return the rating prefix: {@link RowKeys#RATING}
	 * @return
	 */
	public static byte[] getRatingKey()
	{
		return getRowKey(RATING);
	}

	/**
	 * return the rating prefix & collection: {@link RowKeys#RATING}/<tt>collection</tt>
	 * @param collection
	 * @return
	 */
	public static byte[] getRatingKey(String collection)
	{
		return getRowKey(RATING, collection);
	}

	/**
	 * return the rating key for given prefix & collection & user & item
	 * @param collection
	 * @param user
	 * @param item
	 * @return
	 */
	public static byte[] getRatingKey(String collection, long user, long item)
	{
		return getRowKey(RATING, collection, user, item);
	}

	public static byte[] getRatingKey(String collection, long user)
	{
		return getRowKey(RATING, collection, user);
	}

	public static byte[] getUserViewKey(String recommender, UserView view)
	{
		return getUserViewKey(recommender, view.getUser(), view.getItem(), view.getDate());
	}

	public static byte[] getUserViewIndexKey(String recommender, UserView view)
	{
		return getUserViewKey(recommender, view.getUser(), view.getDate(), view.getItem());
	}

	public static byte[] getUserViewKey(String recommender, long user, long item, long stamp)
	{
		return getRowKey(USER_VIEW_PREFIX, recommender, user, item, Long.MAX_VALUE - stamp);
	}

	public static byte[] getUserViewIndexKey(String recommender, long user, long item, long stamp)
	{
		return getRowKey(USER_VIEW_INDEX, recommender, user, Long.MAX_VALUE - stamp, item);
	}

	public static byte[] getUserViewIndexKeyNoInvert(String recommender, long user, long item, long stamp)
	{
		return getRowKey(USER_VIEW_INDEX, recommender, user, stamp, item);
	}

	public static byte[] getUserViewIndexKey(String recommender, long user, long stamp)
	{
		return getRowKey(USER_VIEW_INDEX, recommender, user, Long.MAX_VALUE - stamp);
	}

	public static byte[] getUserViewKey(String recommender, long user)
	{
		return getRowKey(USER_VIEW_PREFIX, recommender, user);
	}

	public static byte[] getUserViewIndexKey(String recommender, long user)
	{
		return getRowKey(USER_VIEW_INDEX, recommender, user);
	}

	public static byte[] getUserViewKey(String recommender)
	{
		return getRowKey(USER_VIEW_PREFIX, recommender);
	}

	public static byte[] getUserViewIndexKey(String recommender)
	{
		return getRowKey(USER_VIEW_INDEX, recommender);
	}

	public static byte[] getUserViewKey()
	{
		return Bytes.toBytes(USER_VIEW_PREFIX);
	}

	public static byte[] getUserViewIndexKey()
	{
		return Bytes.toBytes(USER_VIEW_INDEX);
	}

	public static byte[] getRecommendedItemKey()
	{
		return getRowKey(STATS_RECOMMENDED_ITEM);
	}

	public static byte[] getRecommendedKey()
	{
		return getRowKey(STATS_RECOMMENDED);
	}

	public static byte[] getRecommendedItemKey(String recommender)
	{
		return getRowKey(STATS_RECOMMENDED_ITEM, recommender);
	}

	public static byte[] getRecommendedItemKey(String recommender, long user)
	{
		return getRowKey(STATS_RECOMMENDED_ITEM, recommender, user);
	}

	public static byte[] getRecommendedItemKey(String recommender, long user, long item)
	{
		return getRowKey(STATS_RECOMMENDED_ITEM, recommender, user, item);
	}

	public static byte[] getRecommendedItemKey(String recommender, long user, long item, long stamp)
	{
		return getRowKey(STATS_RECOMMENDED_ITEM, recommender, user, item, Long.MAX_VALUE - stamp);
	}

	public static byte[] getStatRecommendedKey(String collection)
	{
		return getRowKey(STATS_RECOMMENDED, collection);
	}

	public static byte[] getStatRecommendedKey(String collection, long user)
	{
		return getRowKey(STATS_RECOMMENDED, collection, user);
	}

	public static byte[] getStatRecommendedKey(String collection, long user, long stamp)
	{
		return getRowKey(STATS_RECOMMENDED, collection, user, Long.MAX_VALUE - stamp);
	}

	/**
	 * get a key for a content provider / recommender
	 * @param cp content provider
	 * @param recommender
	 * @return
	 */
	public static byte[] getRecommenderKey(String cp, String recommender)
	{
		return getRowKey(RECOMMENDER_PREFIX, cp, recommender);
	}

	/**
	 * get a key prefix for a content provider
	 * @param cp content provider
	 * @return
	 */
	public static byte[] getRecommenderKey(String cp)
	{
		return getRowKey(RECOMMENDER_PREFIX, cp);
	}

	/**
	 * @return
	 */
	public static byte[] getRecommenderKey()
	{
		return getRowKey(RECOMMENDER_PREFIX);
	}

	/**
	 * @param collection
	 * @param category
	 * @return
	 */
	public static byte[] getCategoriesKey(String collection, String category)
	{
		return getRowKey("CATEGORIES", collection, category);
	}

	/**
	 * @param collection
	 * @return
	 */
	public static byte[] getCategoriesPrefix(String collection)
	{
		return getRowKey("CATEGORIES", collection);
	}

	/**
	 * @param id
	 * @return
	 */
	public static byte[] getTaskRow(long id)
	{
		return getRowKeyOld(TASK, Long.MAX_VALUE - id);
	}

	/**
	 * @return
	 */
	public static byte[] getTaskRowPrefix()
	{
		return getRowKey(TASK);
	}

	/**
	 * @param collection
	 * @param item
	 * @param stamp
	 * @return
	 */
	public static byte[] getStatRatingsPerItemKey(String collection, long item, long stamp)
	{
		return getRowKey(STATS_RATINGS_PERITEM_PREFIX, collection, item, Long.MAX_VALUE - stamp);
	}

	/**
	 * @param collection
	 * @param item
	 * @return
	 */
	public static byte[] getStatRatingsPerItemKey(String collection, long item)
	{
		return getRowKey(STATS_RATINGS_PERITEM_PREFIX, collection, item);
	}

	/**
	 * @param collection
	 * @return
	 */
	public static byte[] getStatRatingsPerItemKey(String collection)
	{
		return getRowKey(STATS_RATINGS_PERITEM_PREFIX, collection);
	}

	/**
	 * @param collection
	 * @param item
	 * @param stamp
	 * @return
	 */
	public static byte[] getStatRatingsPerUserKey(String collection, long user, long stamp)
	{
		return getRowKey(STATS_RATINGS_USER_PREFIX, collection, user, Long.MAX_VALUE - stamp);
	}

	/**
	 * @param collection
	 * @param item
	 * @return
	 */
	public static byte[] getStatRatingsPerUserKey(String collection, long user)
	{
		return getRowKey(STATS_RATINGS_USER_PREFIX, collection, user);
	}

	/**
	 * @param collection
	 * @return
	 */
	public static byte[] getStatRatingsPerUserKey(String collection)
	{
		return getRowKey(STATS_RATINGS_USER_PREFIX, collection);
	}

	/**
	 * @param recommender
	 * @param source
	 * @param date
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] getStatSourcesKey(String recommender, String source, long stamp)
	{
		return getRowKey(STATS_SOURCES_PREFIX, recommender, source, Long.MAX_VALUE - stamp);
	}

	/**
	 * @param recommender
	 * @param source
	 * @return
	 */
	public static byte[] getStatSourcesKey(String recommender, String source)
	{
		return getRowKey(STATS_SOURCES_PREFIX, recommender, source);
	}

	/**
	 * @param recommender
	 * @return
	 */
	public static byte[] getStatSourcesKey(String recommender)
	{
		return getRowKey(STATS_SOURCES_PREFIX, recommender);
	}

	/**
	 * @param recommender
	 * @param source
	 * @param date
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] getStatSourcesCollectionKey(String collection, String source, long stamp)
	{
		return getRowKey(STATS_SOURCES_COLLLECTION_PREFIX, collection, source, Long.MAX_VALUE - stamp);
	}

	/**
	 * @param recommender
	 * @param source
	 * @return
	 */
	public static byte[] getStatSourcesCollectionKey(String collection, String source)
	{
		return getRowKey(STATS_SOURCES_COLLLECTION_PREFIX, collection, source);
	}

	/**
	 * @param recommender
	 * @return
	 */
	public static byte[] getStatSourcesCollectionKey(String collection)
	{
		return getRowKey(STATS_SOURCES_COLLLECTION_PREFIX, collection);
	}

	public static byte[] getStatViewedKey(String recommender, long item, long stamp)
	{
		return getRowKey(STATS_VIEWED, recommender, item, Long.MAX_VALUE - stamp);
	}

	public static byte[] getStatViewedKey(String recommender, long item)
	{
		return getRowKey(STATS_VIEWED, recommender, item);
	}

	public static byte[] getStatViewedKey(String recommender)
	{
		return getRowKey(STATS_VIEWED, recommender);
	}

	/**
	 * get a key for a user
	 * @param id
	 * @return
	 */
	public static byte[] getUserKey(long id)
	{
		return getRowKeyOld(LOCATION, id);
	}

	/**
	 * @param location
	 * @param id
	 * @return
	 */
	public static byte[] getPersonKey(String location, String id)
	{
		return getRowKey(LOCATION, id);
	}

	/**
	 * @param lOCATION2
	 * @return
	 */
	public static byte[] getPersonKey(String location)
	{
		return getRowKey(LOCATION);
	}

	/**
	 * @param collection
	 * @param user
	 * @return
	 */
	public static byte[] getStatRecommendedItemKey(String collection, long user)
	{
		return getRowKey(STATS_RECOMMENDED, collection, user);
	}

	/**
	 * @param collection
	 * @param user
	 * @param from
	 * @return
	 */
	public static byte[] getStatRecommendedItemKey(String collection, long user, Long from)
	{
		return getRowKey(STATS_RECOMMENDED, collection, user, from);
	}

	/**
	 * @param collection
	 * @param user
	 * @param item
	 * @param date
	 * @return
	 */
	public static byte[] getStatRecommendedItemKey(String collection, long user, long item, long stamp)
	{
		return getRowKey(STATS_RECOMMENDED, collection, user, item, Long.MAX_VALUE - stamp);
	}

	public static byte[] getVisitEvent(long user, String recommender)
	{
		return getRowKey(LOCATION, user, recommender);
	}

	public static byte[] getVisitEvent(long user)
	{
		return getRowKeyNew(LOCATION, user);
	}

	public static byte[] getVisitEvent()
	{
		return getRowKey(LOCATION);
	}

	/**
	 * @param name
	 * @param collection
	 * @param item
	 * @return
	 */
	public static byte[] getStatsPopularity(String name, String collection, long item)
	{
		return getRowKey(STATS_POPULARITY, name, collection, item);
	}

	/**
	 * @param name
	 * @param collection
	 * @param item
	 * @return
	 */
	public static byte[] getStatsPopularity(String name, String collection)
	{
		return getRowKey(STATS_POPULARITY, name, collection);
	}

	public static byte[] getAMDCIdentifier(String dcIdentifier)
	{
		return getRowKey("DCID", dcIdentifier);
	}

	public static byte[] getAMRelation(long id)
	{
		return getRowKeyNew("RELATION", id);
	}

}
