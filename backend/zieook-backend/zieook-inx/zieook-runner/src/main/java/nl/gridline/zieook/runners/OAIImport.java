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
package nl.gridline.zieook.runners;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import nl.gridline.zieook.Harvester;
import nl.gridline.zieook.OAIException;
import nl.gridline.zieook.tasks.ZieOokTask;
import nl.gridline.zieook.tasks.ZieOokTask.Level;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openarchives.oai._2.IdentifyType;
import org.openarchives.oai._2.ListRecordsType;
import org.openarchives.oai._2.RecordType;
import org.openarchives.oai._2.RecordWrapper;
import org.openarchives.oai._2.ResumptionTokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-oaipmh<br />
 * OAIImport.java created 2 dec. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class OAIImport
{
	private static final Logger LOG = LoggerFactory.getLogger(OAIImport.class);

	// read oai from server
	// put every record on a single line in a text file.
	// this file can be written directly into hdfs,
	// the map function should be able to read this.

	private String metadatPrefix;
	private long wait;
	private long limit;
	private String source;
	private Long startdate;
	private Long enddate;
	private String sets;
	private String dateformat;
	private final Path targetPath;
	private String fsDefaultName;

	private Configuration hdfsConfig;

	private final JAXBContext jc;
	private final Marshaller marshaller;

	private DateTimeFormatter dateTimeFormat;

	private String earliest;

	private final ZieOokTask task;

	public OAIImport(ZieOokTask task, Path output) throws JAXBException
	{
		targetPath = output;
		jc = JAXBContext.newInstance(RecordWrapper.class);
		marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);

		this.task = task;
	}

	public synchronized void start() throws JAXBException, IOException, OAIException, InterruptedException
	{
		initHdfs();
		Harvester harvester = new Harvester(source);
		findOutGranularity(harvester);

		String from = getFrom();
		String until = getUntil();
		String[] sets = getSetsArray();

		long total = 0;
		long count = 0;

		BufferedWriter out = getOutputStream(targetPath);
		try
		{
			task.setMessage(Level.INFO, "importing oai: <" + source + ">");
			for (String set : sets)
			{
				// check for empty sets... but leave 'null' - that if no sets are selected!
				if (set != null && set.trim().isEmpty())
				{
					LOG.warn("given set is empty, skipped");
					continue;
				}

				ListRecordsType recordsType = harvester.listRecords(metadatPrefix, from, until, set);

				ResumptionTokenType token = recordsType.getResumptionToken();
				LOG.info("importing set <{}> from <{}> to <{}>", new Object[]{set, from, until});
				total += token.getCompleteListSize() != null ? token.getCompleteListSize().longValue() : 0;
				do
				{
					List<RecordType> records = recordsType.getRecord();
					for (RecordType record : records)
					{
						String s = getAsString(record);
						s = s.replaceAll("\\r\\n|\\r|\\n", " ");

						out.write(s);
						out.write("\n");

						count++;
						if (isLimit(count))
						{
							LOG.info("limit: {} reached, stopping", limit);
							break;
						}

					}
					LOG.info("processed: {}/{} records", count, total);

					if (task.isCancelled() || isLimit(count))
					{
						break;
					}

					// next set:
					recordsType = harvester.listRecords(token);
					token = recordsType.getResumptionToken();
					if (token == null || token.getValue() == null || token.getValue().isEmpty())
					{
						LOG.error("empty resumption token returned, stopping import");
						token = null;
					}
				}
				while (token != null);
				out.flush();
				if (task.isCancelled() || isLimit(count))
				{
					break;
				}
			}
			LOG.info("done importing: {} sets, {} records", sets.length, count);
		}
		finally
		{
			out.close();
		}
	}

	// -----------------------------
	// ---------- helpers ----------
	// -----------------------------

	private boolean isLimit(long count)
	{
		return limit > -1 && count >= limit;
	}

	private String[] getSetsArray()
	{
		if (sets != null)
		{
			return sets.split("\n");
		}
		else
		{
			return new String[]{null}; // all sets
		}
	}

	private String getFrom()
	{
		if (startdate != null)
		{
			return dateTimeFormat.print(startdate);
		}
		else
		{
			return earliest;
		}
	}

	private String getUntil()
	{
		if (enddate != null)
		{
			return dateTimeFormat.print(enddate);
		}
		return null;
	}

	private void findOutGranularity(Harvester harvester) throws OAIException
	{
		/*
		 * OAI only has two date types: YYYY-MM-DD or YYYY-MM-DDThh:mm:ssZ
		 * (see GranularityType) but Joda && java have yyyy instead of YYYY so, we need a little bit of conversion here)
		 */
		try
		{
			IdentifyType identifier = harvester.identify();
			if (identifier != null)
			{
				earliest = identifier.getEarliestDatestamp();

				if (dateformat != null)
				{
					dateTimeFormat = DateTimeFormat.forPattern(dateformat);
				}
				else
				{
					if (identifier.getGranularity() != null)
					{
						String dtf = identifier.getGranularity().value();
						// These date are not ISO8601 but still occur in the case we have:
						if ("YYYY-MM-DD".equals(dtf))
						{
							dateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
							LOG.warn("granularity of <{}> is set to yyyy-MM-DD from '{}'", harvester.getBaseUrl(), dtf);
						}
						else
						{
							dateTimeFormat = ISODateTimeFormat.date();
							LOG.info("granularity of <{}> is set to 'YYYY-MM-DDThh:mm:ssZ' from '{}'", harvester.getBaseUrl(),
									dtf);
						}
					}
					else
					{
						dateTimeFormat = ISODateTimeFormat.date();
						LOG.info("granularity of <{}> is set to 'YYYY-MM-DDThh:mm:ssZ' from no granularity",
								harvester.getBaseUrl());
					}
				}
			}
		}
		catch (IOException e)
		{
			LOG.error("failed to determine server granularity", e);
		}
		catch (JAXBException e)
		{
			LOG.error("failed to determine server granularity", e);
		}
	}

	private void initHdfs()
	{
		hdfsConfig = new Configuration();
		if (fsDefaultName == null)
		{
			LOG.error("failed to retrieve HDFS setting for <fs.default.name> - please set it in you configuration");
		}
		else
		{
			hdfsConfig.set("fs.default.name", fsDefaultName);
		}
	}

	private BufferedWriter getOutputStream(Path output) throws IOException
	{
		LOG.info("writing date to <{}>", output);
		FileSystem hdfs = FileSystem.get(hdfsConfig);
		FSDataOutputStream out = hdfs.create(output);

		return new BufferedWriter(new OutputStreamWriter(out));
	}

	private String getAsString(RecordType record) throws JAXBException
	{
		StringWriter writer = new StringWriter();
		marshaller.marshal(new RecordWrapper(record), writer);
		return writer.toString().trim();
	}

	// private Path upload(InputStream stream, Path output) throws IOException
	// {
	//
	// // dos.writeUTF("Hello World"); dos.close();
	// FileSystem hdfs = FileSystem.get(hdfsConfig);
	// FSDataOutputStream out = hdfs.create(output);
	// try
	// {
	// // 5: upload the data
	// int len = 0;
	// while ((len = stream.read(buffer)) != -1)
	// {
	// out.write(buffer, 0, len);
	// size += len;
	// }
	// }
	// finally
	// {
	// out.flush();
	// out.close();
	// }
	// LOG.debug("written <{}> bytes to <{}>", size, output);
	// return new Path(hdfsConfig.get("fs.default.name"), output.toString());
	// }

	// -----------------------------
	// --- getters and settings ----
	// -----------------------------

	/**
	 * @return The metadatPrefix.
	 */
	public String getMetadatPrefix()
	{
		return metadatPrefix;
	}

	/**
	 * @param metadatPrefix The metadatPrefix to set.
	 */
	public void setMetadatPrefix(String metadatPrefix)
	{
		this.metadatPrefix = metadatPrefix;
	}

	/**
	 * @return The wait.
	 */
	public long getWait()
	{
		return wait;
	}

	/**
	 * @param wait The wait to set.
	 */
	public void setWait(long wait)
	{
		this.wait = wait;
	}

	/**
	 * @return The limit.
	 */
	public long getLimit()
	{
		return limit;
	}

	/**
	 * @param limit The limit to set.
	 */
	public void setLimit(long limit)
	{
		this.limit = limit;
	}

	/**
	 * @return The source.
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * @param source The source to set.
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 * @return The startdate.
	 */
	public Long getStartdate()
	{
		return startdate;
	}

	/**
	 * @param startdate The startdate to set.
	 */
	public void setStartdate(Long startdate)
	{
		this.startdate = startdate;
	}

	/**
	 * @return The enddate.
	 */
	public Long getEnddate()
	{
		return enddate;
	}

	/**
	 * @param enddate The enddate to set.
	 */
	public void setEnddate(Long enddate)
	{
		this.enddate = enddate;
	}

	/**
	 * @return The sets.
	 */
	public String getSets()
	{
		return sets;
	}

	/**
	 * @param sets The sets to set.
	 */
	public void setSets(String sets)
	{
		this.sets = sets;
	}

	/**
	 * @return The dateformat.
	 */
	public String getDateformat()
	{
		return dateformat;
	}

	/**
	 * @param dateformat The dateformat to set.
	 */
	public void setDateformat(String dateformat)
	{
		this.dateformat = dateformat;
	}

	/**
	 * @return The fsDefaultName.
	 */
	public String getFsDefaultName()
	{
		return fsDefaultName;
	}

	/**
	 * @param fsDefaultName The fsDefaultName to set.
	 */
	public void setFsDefaultName(String fsDefaultName)
	{
		this.fsDefaultName = fsDefaultName;
	}

	/**
	 * @return The targetPath.
	 */
	public String getTargetPath()
	{
		return targetPath.toString();
	}

	/**
	 * @return The earliest.
	 */
	public String getEarliest()
	{
		return earliest;
	}

	/**
	 * @param earliest The earliest to set.
	 */
	public void setEarliest(String earliest)
	{
		this.earliest = earliest;
	}
}
