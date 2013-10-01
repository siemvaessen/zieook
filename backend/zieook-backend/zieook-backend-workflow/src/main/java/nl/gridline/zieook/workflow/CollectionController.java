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
package nl.gridline.zieook.workflow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import nl.gridline.zieook.Harvester;
import nl.gridline.zieook.OAIException;
import nl.gridline.zieook.commons.ZieOokCommons;
import nl.gridline.zieook.commons.ZieOokManager;
import nl.gridline.zieook.configuration.Config;
import nl.gridline.zieook.data.hbase.HBaseManager;
import nl.gridline.zieook.data.hbase.dao.CollectionMetaTable;
import nl.gridline.zieook.data.hbase.dao.CollectionTable;
import nl.gridline.zieook.data.hbase.dao.ContentProviderTable;
import nl.gridline.zieook.data.hbase.dao.UserTable;
import nl.gridline.zieook.data.hbase.model.HBaseCollectionTable;
import nl.gridline.zieook.data.hbase.model.HBaseConfigurationTable;
import nl.gridline.zieook.data.hbase.model.HBaseUserTable;
import nl.gridline.zieook.mapreduce.TaskConfig;
import nl.gridline.zieook.workflow.model.Collection;
import nl.gridline.zieook.workflow.model.CollectionControllerState;
import nl.gridline.zieook.workflow.model.Collections;
import nl.gridline.zieook.workflow.model.OAIMetadata;
import nl.gridline.zieook.workflow.model.OAISets;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.openarchives.oai._2.ListMetadataFormatsType;
import org.openarchives.oai._2.ListSetsType;
import org.openarchives.oai._2.MetadataFormatType;
import org.openarchives.oai._2.ResumptionTokenType;
import org.openarchives.oai._2.SetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-backend-workflow<br />
 * CollectionController.java created 8 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class CollectionController implements ZieOokManager
{
	private static final Logger LOG = LoggerFactory.getLogger(CollectionController.class);

	public static final String NAME = "collection-controller";

	private HBaseManager manager;
	private Config config;
	private Configuration hdfsConfig;
	private boolean state;

	private CollectionMetaTable collectionMetaTable;
	private UserTable userTable;

	// private CollectionTable collectionTable;
	private ContentProviderTable cpTable;

	private final long start;

	private int readbuffer;

	private final WorkflowScheduler scheduler;

	public CollectionController(WorkflowScheduler scheduler)
	{
		start = System.currentTimeMillis();
		this.scheduler = scheduler;
	}

	@Override
	public void startup() throws Exception
	{
		LOG.info(ZieOokCommons.someinformation("CollectionController"));

		// open an hbase connection - only to table names and get collection meta data: the actual import will be done
		// using hadoop job
		// keep a list of collections in cache
		// LOG.info(ZieOokCommons.someinformation());
		config = Config.getInstance(Config.ZIEOOK_WORKFLOW_FILE);
		String zookeeper = config.getZooKeeperHost();
		// fs.default.name

		// set the readbuffer to some sensible size:
		String buffersize = config.get(Config.ZIEOOK_READBUFFER);
		if (buffersize == null)
		{
			readbuffer = 4096;
		}
		else
		{
			try
			{
				readbuffer = Integer.parseInt(buffersize);
			}
			catch (NumberFormatException e)
			{
				LOG.error("failed to read {} from {} - this value should be a number > 0", buffersize,
						Config.ZIEOOK_READBUFFER);
				readbuffer = 4096;
			}
		}

		hdfsConfig = new Configuration();
		String hdfsname = config.get("fs.default.name");
		if (hdfsname == null)
		{
			LOG.error("failed to retrieve HDFS setting for <fs.default.name> - please set it in you configuration");
		}
		else
		{
			hdfsConfig.set("fs.default.name", hdfsname);
		}

		state = zookeeper != null;
		if (state)
		{
			LOG.info("creating hbase manager using: {}", zookeeper);
			manager = HBaseManager.getInstance(zookeeper);

			HBaseConfigurationTable table = new HBaseConfigurationTable(manager);
			if (!table.tableExists())
			{
				state = table.create();
				if (state)
				{
					LOG.info("Configuration table did not exists, and was created");
				}
				else
				{
					LOG.info("Configuration table did not exist, and could not be created");
				}
			}

			collectionMetaTable = new CollectionMetaTable(table);
			// collectionTable = new CollectionTable(new HBaseCollectionTable(manager));
			cpTable = new ContentProviderTable(table);
			userTable = new UserTable(new HBaseUserTable(manager));
		}

	}

	@Override
	public void shutdown() throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean state()
	{
		return state;
	}

	@Override
	public CollectionControllerState getState()
	{
		return new CollectionControllerState(new Date(start).toString(), 0, 0);
	}

	/**
	 * <b>C</b>RUD
	 * @param cp - content provider
	 * @param collection collection meta data
	 */
	public void createCollection(String cp, Collection collection)
	{

		if (!collectionMetaTable.exists(cp, collection.getName()))
		{
			// clean-up old data, sometime deletion fails - because an import job is still executing:
			// at this point, that should not be the case: so run a delete first...
			deleteCollection(cp, collection.getName());

			collectionMetaTable.putCollection(cp, collection);
			// and create the table:

		}
	}

	public Collections getCollections(String cp)
	{
		return new Collections(collectionMetaTable.getCollections(cp));
	}

	public Collections getCollections()
	{
		return new Collections(collectionMetaTable.getCollections(null));
	}

	public Collection getCollection(String cp, String collection)
	{
		return collectionMetaTable.getCollection(cp, collection);
	}

	/**
	 * CR<b>U</b>D
	 * @param cp
	 * @param collectionname
	 * @param collection
	 */
	public void updateCollectionMeta(String cp, String collectionname, Collection collection)
	{

		if (Collection.Type.OAI.toString().equals(collection.getType())
				|| Collection.Type.MOVIELENS.toString().equals(collection.getType()))
		{

			if (!collection.getName().equals(collectionname) || !collection.getCp().endsWith(collectionname))
			{
				// name changed!
				collectionMetaTable.deleteCollection(cp, collectionname);
			}
			LOG.info("updating collection <{}>  collection:\n {}", cp, collection.toJSON());

			collectionMetaTable.putCollection(cp, collection);
		}
		else
		{
			LOG.debug("{} is not recognized to be a correct type, throwing a baddrequest", collection.getType());
			throw new BadRequestException("the given type '" + collection.getType() + "' is not known to the system");
		}
	}

	/**
	 * CR<b>U</b>D
	 * @param cp
	 * @param collection
	 * @param filename
	 * @param stream
	 */
	public void updateCollectionData(String cp, String collection, String part, InputStream stream)
	{

		// Steps:
		// 1: check if collection exists
		// 2: location should be null, otherwise the location is a url
		// 3: create a hdfs filename
		// 4: upload input stream in hdfs
		// 5: start map-reduce to import data in hbase

		// 1: check existence, if not exists getCollection throws 404
		Collection metadata = getCollection(cp, collection);

		LOG.debug("location should be null, but not enforced: {}", metadata.getLocation());
		LOG.debug("type should be movielens, but not enforced: {}", metadata.getType());

		// 2: the location cannot be set, otherwise it's a bad request.
		// if (metadata.getLocation() != null)
		// {
		// throw new BadRequestException("this collection '" + cp + "/" + collection
		// + " does not allow data to be uploaded - the 'location' cannot be set");
		// }

		// 3:
		String name = cp + "/" + collection + "/movielens/" + part + ".dat";

		// 4: process input stream:
		Path path = new Path(config.get(Config.ZIEOOK_HDFS_SERVER) + config.get(Config.ZIEOOK_HDFS_PATH), name);
		try
		{
			// update path with hdfs path
			upload(stream, path);
		}
		catch (IOException e)
		{
			LOG.error("data upload failed for {}/{}", cp, collection);
			throw new InternalServerErrorException("data upload failed for: " + cp + "/" + collection, e);
		}
	}

	/**
	 * CRU<b>D</b>
	 * @param cp
	 * @param collection
	 */
	public void deleteCollection(String cp, String collection)
	{
		LOG.info("delete collection metadata for <{},{}>", cp, collection);

		// stop active tasks
		// We need the workflow scheduler to do that...
		scheduler.cancelCollectionTask(cp, collection);

		// delete all old tasks
		scheduler.deleteCollectionTasks(cp, collection);

		// delete ratings:
		userTable.deleteRatings(cp, collection);

		// eventtable:
		// eventLogTable.deleteViews(cp, recommender);
		// eventLogTable.deleteRecommend(cp, recommender);

		// delete collection table..
		LOG.info("delete collection data for <{},{}>", cp, collection);
		CollectionTable collectionTable = new CollectionTable(new HBaseCollectionTable(manager));
		collectionTable.deleteCollection(cp, collection);

		// check hdfs if files need to be deleted
		LOG.info("cleaning up hdfs temp folders for <{},{}>", cp, collection);
		String name = cp + "/" + collection;
		Path path = new Path(config.get(Config.ZIEOOK_HDFS_SERVER) + config.get(Config.ZIEOOK_HDFS_PATH), name);
		try
		{
			FileSystem hdfs = FileSystem.get(hdfsConfig);
			if (hdfs.exists(path))
			{
				boolean isDeleted = hdfs.delete(path, true);
				if (!isDeleted)
				{
					LOG.error("failed to delete file path: " + path.toString());
				}
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to delete file path: " + path.toString(), e);
		}

		// finally delete the collection meta data:
		collectionMetaTable.deleteCollection(cp, collection);

		LOG.info("collection for <{},{}> removed", cp, collection);
	}

	public boolean scheduleCollection(String cp, Collection collection, long date)
	{

		boolean result = false;

		LOG.debug("trying to cancel old tasks for <{},{}>", cp, collection);
		scheduler.cancelCollectionTask(cp, collection.getName());
		LOG.debug("creating collection import task for <{},{}>", cp, collection);

		try
		{
			// handle movielens separately (it's upload)
			if (TaskConfig.DATA_TYPE_MOVIELENS.equals(collection.getType()))
			{
				// 4: process input stream:

				TaskConfig task = scheduler.createCollectionTask(cp, collection, TaskConfig.DATA_PART_COLLECTION,
						new Path(config.get(Config.ZIEOOK_HDFS_SERVER) + config.get(Config.ZIEOOK_HDFS_PATH), cp + "/"
								+ collection.getName() + "/movielens/" + TaskConfig.DATA_PART_COLLECTION + ".dat").toString(),
						date);
				scheduler.schedule(task);
				task = scheduler.createCollectionTask(cp, collection, TaskConfig.DATA_PART_RATINGS,
						new Path(config.get(Config.ZIEOOK_HDFS_SERVER) + config.get(Config.ZIEOOK_HDFS_PATH), cp + "/"
								+ collection.getName() + "/movielens/" + TaskConfig.DATA_PART_RATINGS + ".dat").toString(),
						date);
				scheduler.schedule(task);
				task = scheduler.createCollectionTask(cp, collection, TaskConfig.DATA_PART_USERS,
						new Path(config.get(Config.ZIEOOK_HDFS_SERVER) + config.get(Config.ZIEOOK_HDFS_PATH), cp + "/"
								+ collection.getName() + "/movielens/" + TaskConfig.DATA_PART_USERS + ".dat").toString(), date);
				scheduler.schedule(task);
			}
			else
			{
				// the rest is just download OAI
				TaskConfig task = scheduler.createCollectionTask(cp, collection, date);
				scheduler.schedule(task);
			}

			result = true;
		}
		catch (MasterNotRunningException e)
		{
			LOG.error("failed to schedule import: <cp,collection> = <" + cp + "," + collection.getName() + ">", e);
			result = false;
		}
		catch (ZooKeeperConnectionException e)
		{
			LOG.error("failed to schedule import: <cp,collection> = <" + cp + "," + collection.getName() + ">", e);
			result = false;
		}
		catch (IOException e)
		{
			LOG.error("failed to schedule import: <cp,collection> = <" + cp + "," + collection.getName() + ">", e);
			result = false;
		}

		return result;
	}

	/**
	 * Checks if the content provider exists
	 * @param cp content provider name
	 * @return {@code true} if found, {@code false} otherwise
	 */
	public boolean isContentProvider(String cp)
	{
		return cpTable.isContentProvider(cp);
	}

	/**
	 * Checks if the collection exists
	 * @param cp content provider name
	 * @param collection collection name
	 * @return {@code true} if found, {@code false} otherwise
	 */
	public boolean isCollection(String cp, String collection)
	{
		return collectionMetaTable.exists(cp, collection);
	}

	private Path upload(InputStream stream, Path output) throws IOException
	{
		byte buffer[] = new byte[readbuffer]; // 1024 byte buffer ->
		long size = 0;
		// dos.writeUTF("Hello World"); dos.close();
		FileSystem hdfs = FileSystem.get(hdfsConfig);
		FSDataOutputStream out = hdfs.create(output);
		try
		{
			// 5: upload the data
			int len = 0;
			while ((len = stream.read(buffer)) != -1)
			{
				out.write(buffer, 0, len);
				size += len;
			}
		}
		finally
		{
			out.flush();
			out.close();
		}
		LOG.debug("written <{}> bytes to <{}>", size, output);
		return new Path(hdfsConfig.get("fs.default.name"), output.toString());
	}

	/**
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws OAIException
	 * @throws XmlException
	 */
	public OAIMetadata getMetaData(String url) throws JAXBException, IOException, OAIException
	{
		// do some oai the magic and return the object
		Harvester harvester = new Harvester(url);
		ListMetadataFormatsType format = harvester.listMetadataFormats();
		List<MetadataFormatType> metadataArray = format.getMetadataFormat();
		List<String> metadataList = new ArrayList<String>(metadataArray.size());
		for (MetadataFormatType metadata : metadataArray)
		{
			metadataList.add(metadata.getMetadataPrefix());
		}
		return new OAIMetadata(metadataList, null);
	}

	/**
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws OAIException
	 * @throws XmlException
	 */
	public OAISets getOAISets(String url) throws JAXBException, IOException, OAIException
	{

		// cut off the final part of the url, this will cause problems:
		// failed to get sets for <http://62.221.199.163:4296/?verb=ListRecords&metadataPrefix=oai_dc>

		// do some oai magic an return the object
		Harvester harvester = new Harvester(urlClean(url));
		List<String> setList = new ArrayList<String>();
		List<String> setnamesList = new ArrayList<String>();

		// first:
		ListSetsType sets = harvester.listSets();
		if (sets == null)
		{
			// No sets! - that is possible,
			return new OAISets(setList, null, setnamesList);
		}

		ResumptionTokenType resumptionToken;
		do
		{
			// get resumptiontoken:
			resumptionToken = sets.getResumptionToken();

			List<SetType> setArray = sets.getSet();
			for (SetType set : setArray)
			{
				String name = set.getSetName();
				String spec = set.getSetSpec();
				if (name == null && spec == null)
				{
					LOG.error("The url <{}> contains empty sets, ignoring some", url);
					continue;
				}
				if (name != null)
				{
					setnamesList.add(name.replaceAll("\n", " "));
				}
				else
				{
					setnamesList.add(spec.replaceAll("\n", " "));
				}
				setList.add(spec);

			}

			// list next if there is a resumption token:
			if (resumptionToken != null)
			{
				sets = harvester.listSets(resumptionToken);
			}
		}
		while (resumptionToken != null);

		return new OAISets(setList, null, setnamesList);
	}

	private String urlClean(String url)
	{
		int querystart = url.indexOf('?');

		if (querystart > 0)
		{
			return url.substring(0, querystart);
		}
		else
		{
			return url;
		}
	}
}
