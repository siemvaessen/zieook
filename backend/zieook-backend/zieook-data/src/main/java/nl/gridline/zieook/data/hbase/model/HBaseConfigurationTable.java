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
package nl.gridline.zieook.data.hbase.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.TemplateTable;
import nl.gridline.zieook.mapreduce.HBaseTableConstants;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.workflow.model.Module;
import nl.gridline.zieook.workflow.model.Property;
import nl.gridline.zieook.workflow.model.Template;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

/**
 * This table contains all zieook online configuration related data.
 * e.g:
 * <ul>
 * <li>Templates, a configuration of map-reduce algorithms to run against a dataset. (a Job Config, using JobClient)</li>
 * e.g. row-key- template-name { column- modules { qualifier- modulename : settings }}
 * <li>Filters: a filter on some dataset, based on it's category field
 * <li>Recommender: a complete recommender configuration, that contains the interval, collection id, etc.</li>
 * </ul>
 * Different uses:
 * <p>
 * <b>Recommender:</b> configuration: row key: <tt>cp.recommender<tt>
 * </p>
 * <p>
 * <b>Templates:</b> A generic set of algorithms that can be used by a recommender row key:
 * <tt>template.template-name</tt> or in case of an instance used in combination with a <tt>recommender</tt> (
 * <tt>template</tt> is a fixed constant, <tt>template-name</tt> a unique id)
 * </p>
 * <p>
 * <b>Filters:</b> Only used in combination with a <tt>recommender</tt> and filter out of a configuration
 * </p>
 * <p />
 * Project zieook-data<br />
 * RecommenderConfig.java created 16 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class HBaseConfigurationTable extends AbstractHBaseTable
{

	// private static final Logger LOG = LoggerFactory.getLogger(HBaseConfigurationTable.class);

	private static final String NAME = HBaseTableConstants.CONFIGURATION_NAME;
	private static final String COLUMN_FILTER = HBaseTableConstants.CONFIGURATION_COLUMN_FILTER;
	private static final String COLUMN_CONFIG = HBaseTableConstants.CONFIGURATION_COLUMN_RECOMMENDER;
	private static final String COLUMN_TEMPLATE = HBaseTableConstants.CONFIGURATION_COLUMN_TEMPLATE;
	private static final String COLUMN_COLLECTIONMETA = HBaseTableConstants.CONFIGURATION_COLUMN_COLLECTIONMETA;
	private static final String COLUMN_CONTENTPROVIDER = HBaseTableConstants.CONFIGURATION_COLUMN_CONTENTPROVIDER;
	private static final String COLUMN_ZIEOOK_TASK = HBaseTableConstants.CONFIGURATION_COLUMN_ZIEOOK_TASK;

	public HBaseConfigurationTable(HBaseManager manager)
	{
		super(manager);
	}

	@Override
	public String getTableName(String name)
	{
		return NAME;
	}

	public HTableInterface getTable()
	{
		return getTable(null);
	}

	public String getTableName()
	{
		final String name = getTableName(null);
		return name;
	}

	@Override
	public boolean create(String name)
	{
		return create();
	}

	public boolean tableExists()
	{
		return tableExists(null);
	}

	public boolean update() throws IOException
	{
		return update(null);
	}

	public boolean drop()
	{
		return drop(null);
	}

	public Result get(Get get)
	{
		return get(null, get);
	}

	public void put(Put put)
	{
		put(null, put);
	}

	public boolean exists(Get get)
	{
		return exists(null, get);
	}

	public void delete(Delete delete)
	{
		delete(null, delete);
	}

	public void delete(List<Delete> deletes)
	{
		delete(null, deletes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.gridline.zieook.data.hbase.TableOperations#getCurrentDescriptor(java.lang.String)
	 */
	@Override
	public HTableDescriptor getCurrentDescriptor(String name)
	{
		// table descriptor:
		HTableDescriptor descriptor = new HTableDescriptor(NAME);

		// column family: the filter of a recommender configuration
		HColumnDescriptor filter = new HColumnDescriptor(COLUMN_FILTER);

		// the base item of a recommender config (interval, collection, etc)
		HColumnDescriptor configuration = new HColumnDescriptor(COLUMN_CONFIG);

		// the template - modules & settings associated with the recommender has qualifier- name : value settings for
		// all modules should be prefixed with an int to keep them in order.
		HColumnDescriptor template = new HColumnDescriptor(COLUMN_TEMPLATE);

		HColumnDescriptor collection = new HColumnDescriptor(COLUMN_COLLECTIONMETA);

		HColumnDescriptor contentProvider = new HColumnDescriptor(COLUMN_CONTENTPROVIDER);

		HColumnDescriptor zieooktask = new HColumnDescriptor(COLUMN_ZIEOOK_TASK);

		// the interpretation does change
		// rawdata.setMaxVersions(3); <- this is the default

		// It is probably handy to have this information available through an extra
		// index. Although it only(!) determines if this column is available, which is nice if we do interpretation
		// after import

		descriptor.addFamily(filter);
		descriptor.addFamily(template);
		descriptor.addFamily(configuration);
		descriptor.addFamily(collection);
		descriptor.addFamily(contentProvider);
		descriptor.addFamily(zieooktask);

		return descriptor;
	}

	public synchronized boolean create()
	{

		if (super.create(NAME))
		{
			// init default data:
			TemplateTable templates = new TemplateTable(this);
			templates.putTemplate(getDefaultUserTemplate());
			templates.putTemplate(getDefaultItemTemplate());
			templates.putTemplate(getDefaultTextTemplate());
			templates.putTemplate(getDefaultStandardTemplate());
			return true;
		}

		return false;
	}

	public static Template getDefaultUserTemplate()
	{
		List<Property> properties = new ArrayList<Property>();
		properties.add(new Property(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME, "SIMILARITY_TANIMOTO_COEFFICIENT"));
		properties.add(new Property(TaskConfig.NUM_RECOMMENDATIONS, "100"));
		properties.add(new Property(TaskConfig.BOOLEAN_DATA, "true"));
		properties.add(new Property(TaskConfig.MAX_PREFS_PER_USER, "10"));
		properties.add(new Property(TaskConfig.MAX_SIMILARITRIES_PER_ITEM, "100"));
		properties.add(new Property(TaskConfig.MAX_CO_OCCURRENCES_PER_ITEM, "100"));
		properties.add(new Property(TaskConfig.NUM_RECOMMENDATIONS, "100"));
		Module module = new Module(TaskConfig.RECOMMENDER_TYPE_USERBASED, properties);
		List<Module> modules = new ArrayList<Module>(1);
		modules.add(module);
		return new Template("userbased", modules);
	}

	public static Template getDefaultItemTemplate()
	{
		List<Property> properties = new ArrayList<Property>();
		properties.add(new Property(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME, "SIMILARITY_TANIMOTO_COEFFICIENT"));
		properties.add(new Property(TaskConfig.NUM_RECOMMENDATIONS, "100"));
		properties.add(new Property(TaskConfig.BOOLEAN_DATA, "true"));
		properties.add(new Property(TaskConfig.MAX_PREFS_PER_USER, "10"));
		properties.add(new Property(TaskConfig.MAX_SIMILARITRIES_PER_ITEM, "100"));
		properties.add(new Property(TaskConfig.MAX_CO_OCCURRENCES_PER_ITEM, "100"));
		Module module = new Module(TaskConfig.RECOMMENDER_TYPE_ITEMBASED, properties);
		List<Module> modules = new ArrayList<Module>(1);
		modules.add(module);
		return new Template("itembased", modules);
	}

	public static Template getDefaultStandardTemplate()
	{
		List<Property> properties = new ArrayList<Property>();
		properties.add(new Property(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME, "SIMILARITY_TANIMOTO_COEFFICIENT"));
		properties.add(new Property(TaskConfig.NUM_RECOMMENDATIONS, "100"));
		properties.add(new Property(TaskConfig.BOOLEAN_DATA, "true"));
		properties.add(new Property(TaskConfig.MAX_PREFS_PER_USER, "10"));
		properties.add(new Property(TaskConfig.MAX_SIMILARITRIES_PER_ITEM, "100"));
		properties.add(new Property(TaskConfig.MAX_CO_OCCURRENCES_PER_ITEM, "100"));
		Module module = new Module(TaskConfig.RECOMMENDER_TYPE_ITEMBASED, properties);
		List<Module> modules = new ArrayList<Module>(1);
		modules.add(module);
		return new Template("standard", modules);
	}

	public static Template getDefaultTextTemplate()
	{
		List<Property> properties = new ArrayList<Property>();
		properties.add(new Property(TaskConfig.DISTRIBUTED_SIMILARITY_CLASSNAME, "SIMILARITY_TANIMOTO_COEFFICIENT"));
		properties.add(new Property(TaskConfig.NUM_RECOMMENDATIONS, "100"));
		properties.add(new Property(TaskConfig.BOOLEAN_DATA, "true"));
		properties.add(new Property(TaskConfig.MAX_PREFS_PER_USER, "10"));
		properties.add(new Property(TaskConfig.MAX_SIMILARITRIES_PER_ITEM, "100"));
		properties.add(new Property(TaskConfig.MAX_CO_OCCURRENCES_PER_ITEM, "100"));
		properties.add(new Property(TaskConfig.NUM_RECOMMENDATIONS, "100"));
		Module module = new Module(TaskConfig.RECOMMENDER_TYPE_TEXTBASED, properties);
		List<Module> modules = new ArrayList<Module>(1);
		modules.add(module);
		return new Template("textbased", modules);
	}
}
