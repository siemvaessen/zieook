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
package nl.gridline.zieook.inx.dc;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import nl.gridline.zieook.Harvester;
import nl.gridline.zieook.OAIException;
import nl.gridline.zieook.model.CollectionItem;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openarchives.oai._2.GetRecordType;
import org.openarchives.oai._2.ListIdentifiersType;
import org.openarchives.oai._2.RecordWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * http://ahm.adlibsoft.com/oai/oaiserver.exe?verb=ListRecords&metadataPrefix=oai_dc
 * <p />
 * Project zieook-dc<br />
 * DCRecordParseXMLTest.java created Sep 1, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class DCRecordParseXMLTest
{
	private static final Logger LOG = LoggerFactory.getLogger(DCRecordParseXMLTest.class);

	private final String source = "http://ahm.adlibsoft.com/oaix/oai.ashx";
	private Harvester harvester;
	private static JAXBContext context;

	@BeforeClass
	public static void init() throws JAXBException
	{
		context = JAXBContext.newInstance(RecordWrapper.class);;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		harvester = new Harvester(source);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{

	}

	@Test
	public void granularity() throws JAXBException, IOException, OAIException
	{
		LOG.info("identify: {}", harvester.identify().getGranularity());
		LOG.info("list harvester sets: {}", harvester.listSets().getSet().size());
	}

	@Test
	public void listIds() throws JAXBException, IOException, OAIException
	{

		ListIdentifiersType items = harvester.listIdentifiers("oai_dc");
		LOG.info("listIds: {}", items.getHeader().size());

	}

	@Test
	public void getRecordsOnePageTest() throws ParserConfigurationException, SAXException, ParseException,
			JAXBException, IOException, OAIException
	{

		LOG.info("test if page one contains 'oai_dc' identifiers");

		ListIdentifiersType result = harvester.listIdentifiers("oai_dc");
		for (int i = 0; i < result.getHeader().size(); i++)
		{
			LOG.info("i: " + i + ", id: " + result.getHeader().get(i).getIdentifier());
			GetRecordType recordType = harvester.getRecord(result.getHeader().get(i).getIdentifier(), "oai_dc");
			// System.out.println(recordType.toString());
			CollectionItem item = DCRecordParseXML.getRecordFromXmlString(toString(recordType));
			LOG.info(item.toJSON());

			// hard break after 10, that should be enough for a quick parse test...
			if (i == 10)
			{
				break;
			}
		}

	}

	private String toString(GetRecordType record) throws JAXBException
	{
		StringWriter writer = new StringWriter();
		context.createMarshaller().marshal(new RecordWrapper(record), writer);
		return writer.toString();
	}

}
