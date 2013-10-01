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
package nl.gridline.zieook.inx.czp;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import nl.gridline.zieook.Harvester;
import nl.gridline.zieook.OAIException;
import nl.gridline.zieook.model.CollectionItem;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openarchives.oai._2.GetRecordType;
import org.openarchives.oai._2.HeaderType;
import org.openarchives.oai._2.ListIdentifiersType;
import org.openarchives.oai._2.ListRecordsType;
import org.openarchives.oai._2.ListSetsType;
import org.openarchives.oai._2.RecordType;
import org.openarchives.oai._2.RecordWrapper;
import org.openarchives.oai._2.ResumptionTokenType;
import org.openarchives.oai._2.SetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-czp<br />
 * CzpRecordParseJAXBTest.java created 24 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class CzpRecordParseJAXBTest
{
	private static final Logger LOG = LoggerFactory.getLogger(CzpRecordParseJAXBTest.class);

	static String url = "http://oai2.edit-extra.nu/request";
	static List<RecordType> records;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		// get some data:
		Harvester harvester = new Harvester(new URL(url));

		ListRecordsType recordsType = harvester.listRecords("czp", "Beeld en Geluid");
		records = recordsType.getRecord();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Test
	@Ignore
	public void getRecords() throws IOException, JAXBException, OAIException
	{
		JAXBContext jc = JAXBContext.newInstance(RecordWrapper.class);
		Marshaller marshaller = jc.createMarshaller();
		int count = 0, max = 10;
		Harvester harvester = new Harvester(new URL(url));
		ListIdentifiersType identifiers = harvester.listIdentifiers("czp", null, null, "Amsterdam Museum");
		do
		{
			count++;
			if (count == max)
			{
				break;
			}
			for (HeaderType t : identifiers.getHeader())
			{
				GetRecordType record = harvester.getRecord(t.getIdentifier(), "czp");
				assertTrue(record != null);
				if (record != null)
				{
					RecordWrapper jaxb = new RecordWrapper(record);
					StringWriter writer = new StringWriter();
					marshaller.marshal(jaxb, writer);
					LOG.info("id: {} - {}", t.getIdentifier(), writer.toString());
				}
				else
				{
					LOG.info("id: {} - <empty>", t.getIdentifier());
				}
			}
			LOG.info("next...");
		}
		while ((identifiers = harvester.listIdentifiers(identifiers.getResumptionToken())) != null);
	}

	@Test
	public void testMarshaller() throws JAXBException, ParseException
	{
		JAXBContext jc = JAXBContext.newInstance(RecordWrapper.class);
		Marshaller marshaller = jc.createMarshaller();
		CzpRecordParseJAXB parser = new CzpRecordParseJAXB("cp", "nl");
		for (RecordType record : records)
		{

			RecordWrapper jaxb = new RecordWrapper(record);
			StringWriter writer = new StringWriter();
			marshaller.marshal(jaxb, writer);

			LOG.info("{}", jaxb.getClass());

			CollectionItem item = parser.getCZPfromXML(writer.toString());

			assertTrue(item != null);

			System.out.println(item.toJSON());
		}
	}

	@Test
	public void testAllSets() throws JAXBException, IOException, OAIException
	{
		Harvester harvester = new Harvester(new URL(url));
		ListSetsType sets = harvester.listSets();

		for (SetType type : sets.getSet())
		{
			String spectype = type.getSetSpec();

			if (spectype != null)
			{
				ListIdentifiersType identifiers = harvester.listIdentifiers("czp", null, null, spectype);
				if (identifiers != null)
				{
					ResumptionTokenType resumption = identifiers.getResumptionToken();
					if (resumption != null)
					{
						System.out.println(type.getSetSpec() + " " + resumption.getCompleteListSize());
					}
					else
					{
						System.out.println(type.getSetSpec() + " " + identifiers.getHeader().size());
					}
				}
				else
				{
					System.out.println(type.getSetSpec() + " 0");
				}
			}
			else
			{
				System.out.println("no set spec:" + type);
			}
		}
	}

}
