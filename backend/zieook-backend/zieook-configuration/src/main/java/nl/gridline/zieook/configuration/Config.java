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
package nl.gridline.zieook.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ZieOok configuration tries to sort off mimic the way hadoop configures it's system, which should make it less
 * confusing
 * <p />
 * FIXME: this class will still have synchronization issues, the used hashmap is not synchronzed at all!
 * <p />
 * Project zieook-configuration<br />
 * ConfigurationManager.java created 9 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class Config extends GridlineConfig
{

	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

	/**
	 * Zie ook home folder
	 */
	public static final String ZIEOOK_HOME = "zieook.home";
	/**
	 * ZooKeeper host name
	 */
	public static final String ZIEOOK_ZOOKEEPER_HOST = "zieook.zookeeper.host";

	/**
	 * ZooKeeper port, usually 2181
	 */
	public static final String ZIEOOK_ZOOKEEPER_PORT = "zieook.zookeeper.port";

	/**
	 * maximum number of categories returned from a query
	 */
	public static final String ZIEOOK_COLLECTION_CATEGORIES_MAX_SIZE = "zieook.categories.search.maxsize";

	/**
	 * Categories split: we are able to read nested categories iff the are seperated with the given character, by default
	 * a /
	 */
	public static final String ZIEOOK_COLLECTION_CATEGORIES_SPLIT = "/";

	/**
	 * default configuration file name - contains all default settings
	 */
	public static final String ZIEOOK_DEFAULT_FILE = "zieook-default.xml";

	/**
	 * zieook workflow specific settings
	 */
	public static final String ZIEOOK_WORKFLOW_FILE = "zieook-workflow.xml";

	/**
	 * zieook datamanager specific settings
	 */
	public static final String ZIEOOK_DATAMANAGER_FILE = "zieook-datamanager.xml";

	/**
	 * Set to false to skip EHCache update checks -> default is false
	 */
	public static final String EHCACHE_UPDATE_CHECKER = "net.sf.ehcache.skipUpdateCheck";

	public static final String ZIEOOK_HDFS_SERVER = "fs.default.name";

	/**
	 * the default temp directory within hdfs that will be used to store all kinds of data
	 */
	public static final String ZIEOOK_HDFS_TEMP = "zieook.hdfs.temp";

	/**
	 * Read buffer size for uploading files, this is only the memory buffer between inputstream and hdfs outputstream,
	 * default is 4096 bytes
	 */
	public static final String ZIEOOK_READBUFFER = "zieook.hdfs.readbuffer.bytes";

	/**
	 * The root path that all zieook data [should] use: e.g. /zieook
	 */
	public static final String ZIEOOK_HDFS_PATH = "zieook.hdfs.path";

	/**
	 * Scheduler heartbeat in se2011-09-16 15:18:04,104 [main] WARN nl.gridline.config.Config[64]- The system property
	 * <gridline.project.name> is set to 'project' you may want to update this to a real name
	 * conds: the interval on which the queue is checked for tasks that need to be executed
	 */
	public static final String ZIEOOK_HEARTBEAT = "zieook.scheduler.heartbeat.s";

	/**
	 * Session timeout for zieook workflow scheduler, in ms
	 */
	public static final String ZIEOOK_SCHEDULER_SESSIONTIMEOUT = "zieook.scheduler.timeout";

	/**
	 * Number of threads in the threadpool executor
	 */
	public static final String ZIEOOK_SCHEDULER_TASKTHREADS = "zieook.scheduler.threadcount";

	/**
	 * The map reduce job tracker address
	 */
	public static final String ZIEOOK_JOBTRACKER = "mapred.job.tracker";

	/**
	 * limits the amount of items imported, -1 or missing is no limit
	 */
	public static final String OAI_GLOBAL_ITEMLIMIT = "oai.global.itemlimit";

	/**
	 * limits the global from date for oai imports: should be iso date as defined in the <a
	 * href="http://www.openarchives.org/OAI/openarchivesprotocol.html">oai-pmh protocol</a>
	 */
	public static final String OAI_GLOBAL_FROM = "oai.global.from";

	/**
	 * limits the global until date for oai imports: should be an iso date as defined in te <a
	 * href="http://www.openarchives.org/OAI/openarchivesprotocol.html">oai-pmh protocol</a>
	 */
	public static final String OAI_GLOBAL_UNTIL = "oai.global.until";

	/**
	 * overrides the granularity date of the oai server - some of them don't report the correct format.
	 */
	public static final String OAI_GLOBAL_FORMATOVERRIDE = "oai.global.dateformat.override";

	/**
	 * reserved for embedded client server
	 */
	public static final String CLIENT_SERVER = "zieook.client.server";
	/**
	 * reserved for embedded startup
	 */
	public static final String WORKFLOW_SERVER = "zieook.workflow.server";
	/**
	 * reserved for embedded start-up
	 */
	public static final String DATA_SERVER = "zieook.data.server";

	/**
	 * The edit server might not like if we slurp all items through a lot of simultaneous connections, if this value is
	 * set, it will wait the given ms after every request.
	 */
	public static final String EDIT_WAIT_MS = "edit.wait.ms";

	/**
	 * The number of retries when trying to retrieve a unique id
	 */
	public static final String ZIEOOKID_RETRIES = "zieook.scheduler.retries";

	/**
	 * The movielens jar <br />
	 * map-reduce classes jar, the one that runs on hadoop
	 */
	public static final String MAPRED_MOVIELENS = "zieook.mapred.movielens";

	public static final String MAPRED_BASE = "zieook.mapred";

	/**
	 * The OAI-CZP jar <br />
	 * map-reduce classes jar, the one that runs on hadoop - reading czp (content zoek profiel) from the <em>edit</em>
	 * platform
	 */
	public static final String MAPRED_CZP = MAPRED_BASE + ".czp";

	/**
	 * The OAI-ADLIBDC jar <br />
	 * map-reduce classes jar, the one that runs on hadoop - reading oai_dc xml format (from Amsterdam Museum)
	 */
	public static final String MAPRED_OAI_DC = MAPRED_BASE + ".oai_dc";

	/**
	 * taalserver mapreduce classes jar, the one that runs on hadoop
	 */
	public static final String MAPRED_TAALSERVER = "zieook.mapred.taalserver";

	/**
	 * free taalserver mapreduce classes jar, the one that run on hadoop - but does no fancy gridline stuff
	 */
	public static final String MAPRED_FREETAALSERVER = "zieook.mapred.freetaalserver";

	/**
	 * If set to true the system will try to use the MAPRED_TAALSERVER, otherwise the MAPRED_FREETAALSERVER will be used.
	 * if the former is missing the MAPRED_FREETAALSERVER will be used always.
	 */
	public static final String USE_TAALSERVER = "zieook.mapred.usetaalserver";

	/**
	 * The next values for the scheduling of statistics
	 */
	public static final String STATISTICS_NEXT = "zieook.scheduler.statistics.next";
	/**
	 * the interval to run the statistics on.
	 */
	public static final String STATISTICS_INTERVAL = "zieook.scheduler.statistics.interval";

	public static final String STATISTICS_POPULARITY_TYPES = "statistics.popularity.types";

	/**
	 * Some extra tools specifically designed for Amsterdam Museum.
	 */
	public static final String MAPRED_AMSTERDAMMUSEUM = "zieook.mapred.amsterdammuseum";

	/**
	 * the default setting for text analysis, if non is passed in the recommender configuration.
	 * This will include/exclude the description in the analysis for text recommenders
	 */
	public static final String TEXT_ANALYSIS_USE_DESCRIPTION_DEFAULT = "taalserver.text.analysis.description";

	/**
	 * the default setting for text analysis if non is passed in the recommender configuration
	 * This will include/exclude the title in the analysis for text recommenders
	 */
	public static final String TEXT_ANALYSIS_USE_TITLE_DEFAULT = "taalserver.text.analysis.title";

	/**
	 * the default setting for text analysis if non is passed in the recommender configuration
	 * This will include/exclude the keywords in the analysis for text recommenders
	 */
	public static final String TEXT_ANALYSIS_USE_KEYWORDS_DEFAULT = "taalserver.text.analysis.keywords";

	/**
	 * Key array, these will be read from System.getProperty and override the configuration file setting.
	 */
	public static final String[] ZIEOOK_KEYS = {ZIEOOK_HOME, ZIEOOK_ZOOKEEPER_HOST, ZIEOOK_ZOOKEEPER_PORT,
			ZIEOOK_COLLECTION_CATEGORIES_MAX_SIZE, ZIEOOK_COLLECTION_CATEGORIES_SPLIT, ZIEOOK_DEFAULT_FILE,
			ZIEOOK_WORKFLOW_FILE, ZIEOOK_DATAMANAGER_FILE, EHCACHE_UPDATE_CHECKER, ZIEOOK_HDFS_SERVER, ZIEOOK_HDFS_TEMP,
			ZIEOOK_READBUFFER, ZIEOOK_HDFS_PATH, ZIEOOK_HEARTBEAT, ZIEOOK_SCHEDULER_SESSIONTIMEOUT,
			ZIEOOK_SCHEDULER_TASKTHREADS, ZIEOOK_JOBTRACKER, CLIENT_SERVER, WORKFLOW_SERVER, DATA_SERVER,
			ZIEOOKID_RETRIES, MAPRED_MOVIELENS};

	/**
	 * Convenience method to get the zookeeper host, this host is used alot to get all kinds of data.
	 * @return the zookeeper host if defined in the config file, null otherwise
	 */
	public String getZooKeeperHost()
	{
		String result = get(ZIEOOK_ZOOKEEPER_HOST);
		if (result == null)
		{
			LOG.error("Configuration error missing {}", ZIEOOK_ZOOKEEPER_HOST);
		}

		return result;
	}

	public String getZooKeeperPort()
	{
		String result = get(ZIEOOK_ZOOKEEPER_PORT);
		if (result == null)
		{
			LOG.error("Configuration error missing {}", ZIEOOK_ZOOKEEPER_PORT);
		}

		return result;
	}

	@Override
	public String getHome()
	{
		return get(ZIEOOK_HOME);
	}

	/**
	 * Get the instance of the configuration manager - only loaded with properties given through the system properties
	 * @return the instance of the configuration manager.
	 */
	public static synchronized Config getInstance()
	{
		if (instance == null)
		{
			instance = new Config();
		}
		return (Config) instance;
	}

	public static Config getInstance(String filename)
	{
		if (instance == null)
		{
			getInstance();
			((Config) instance).load(filename);
		}
		else
		{
			((Config) instance).load(filename);
		}
		return (Config) instance;
	}

}
