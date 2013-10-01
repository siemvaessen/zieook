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
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.gridline.zieook.model.CollectionItem;
import nl.gridline.zieook.oai.UtilsXml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * Amsterdam Museaum offers it's metadata over OAI in a dc format, an example url is this:
 * <em><a href="http://ahm.adlibsoft.com/oai/oaiserver.exe?verb=ListRecords&metadataPrefix=oai_dc">http://ahm.adlibsoft.com/oai/oaiserver.exe?verb=ListRecords&metadataPrefix=oai_dc</a></em>
 * </p>
 * <p>
 * The records look like this:
 * </p>
 * <p>
 * &lt;record&gt;<br />
 * &lt;header&gt;<br />
 * &lt;identifier&gt;oai:AHM:2&lt;/identifier&gt; &lt;datestamp&gt;2009-12-21&lt;/datestamp&gt; &lt;setSpec&gt;
 * collect:(intern) &lt;/setSpec&gt;<br />
 * &lt;/header&gt;<br />
 * &lt;metadata&gt;<br />
 * &lt;oai_dc:dc&gt;<br />
 * &lt;dc:title&gt;Monument voor Amsterdam&lt;/dc:title&gt;<br />
 * &lt;dc:creator&gt;Sallieth, Mathias de&lt;/dc:creator&gt;<br />
 * &lt;dc:description&gt;<br />
 * &lt;text&gt;In verschillende taferelen wordt in deze prent herinnerd aan de hoogtijdagen van de patriotten in
 * Amsterdam. Bijeenkomsten van patriotse vrijcorpsen, de strijd van de Oranjegezinde bewoners van Kattenburg, dit alles
 * onder het toeziend oog van burgemeester Hooft, de bewierookte gunsteling van de patriotten. Na de overgave aan de
 * Pruisische troepen werd hij afgezet.&lt;/text&gt;<br />
 * &lt;type&gt;zaaltekst NL&lt;/type&gt;<br />
 * &lt;/dc:description&gt;<br />
 * &lt;dc:description&gt;<br />
 * &lt;text&gt;The different scenes in this print commemorate the heyday of the Patriots in Amsterdam - meetings of the
 * Patriot volunteer corps, the skirmishes of the Orangeist residents of Kattenburg, all under the watchful eye of
 * burgomaster Hooft, the darling of the Patriots. He was removed from office after the surrender to the Prussian
 * troops&lt;/text&gt;<br />
 * &lt;type&gt;zaaltekst ENG&lt;/type&gt;<br />
 * &lt;/dc:description&gt;<br />
 * &lt;dc:date&gt;1787&lt;/dc:date&gt;<br />
 * &lt;dc:date&gt;1787&lt;/dc:date&gt;<br />
 * &lt;dc: format&gt;hoogte a 42 cm</dc:format&gt;<br />
 * &lt;dc:format&gt;breedte a 49 cm&lt;/dc:format&gt;<br />
 * &lt;dc:format&gt;hoogte c 32 cm&lt;/dc:format&gt;<br />
 * &lt;dc:format&gt;breedte c 40 cm&lt;/dc:format&gt;<br />
 * &lt;dc:type&gt; prent&lt;/dc:type&gt;<br />
 * &lt;dc:type&gt; papier&lt;dc:type&gt;<br />
 * &lt;dc:identifier&gt;A 54&lt;/dc:identifier&gt;<br />
 * &lt;dc:language>NL&lt;/dc:language&gt;<br />
 * &lt;dc:source>Amsterdam Museum, Amsterdam&lt;/dc:source&gt;<br />
 * &lt;dc:rights>Amsterdam Museum &lt;/dc:rights&gt;<br />
 * &lt;priref&gt;2&lt;/priref&gt;<br />
 * &lt;image>http ://ahm.adlibsoft.com/ahmimages/a_54.jpg&lt;/image&gt;<br />
 * &lt;/oai_dc:dc&gt;<br />
 * &lt;/metadata&gt;<br />
 * &lt;/record&gt;
 * </p>
 * Project zieook-dc<br />
 * DCRecordParseXML.java created Sep 1, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class DCRecordParseXML
{

	private static final String RECORD = "record";
	private static final String IDENTIFIER = "identifier";

	private static final String TITLE = "dc:title";
	private static final String SUBJECT = "dc:subject";
	private static final String DCTYPE = "dc:type";
	private static final String DCSOURCE = "dc:source";

	private static final String DESCRIPTION = "dc:description";

	private static final String DESCR_LANGTYPE = "type";
	private static final String DESCR_CONTENT = "text";
	private static final String LANGUAGE_NL = "zaaltekst NL";

	private static final String IMAGE = "image";

	private static final String DATE = "datestamp";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * @param xmlString
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws ParseException
	 */
	public static CollectionItem getRecordFromXmlString(String xmlString) throws ParserConfigurationException,
			SAXException, IOException, ParseException
	{
		List<CollectionItem> list = getRecordsFromXmlString(xmlString);

		return (list.get(0));
	}

	/**
	 * Processes a list of records, e.g.
	 * @param object
	 * @return
	 */
	public static List<CollectionItem> getRecordsFromXmlString(String object) throws ParserConfigurationException,
			SAXException, IOException, ParseException
	{

		// read xml string
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(object));
		Document doc = builder.parse(is);

		// normalize text representation
		doc.getDocumentElement().normalize();

		return getDCItemsFromXmlDcoument(doc);

	}

	/**
	 * @param doc
	 * @return
	 */
	private static List<CollectionItem> getDCItemsFromXmlDcoument(Document doc) throws ParserConfigurationException,
			SAXException, IOException, ParseException
	{
		List<CollectionItem> result = new ArrayList<CollectionItem>();
		NodeList listOfRecordElements = doc.getElementsByTagName(RECORD);

		// list the records, convert them to CollectionItem's:
		int numberOfRecords = listOfRecordElements.getLength();

		for (int s = 0; s < numberOfRecords; s++)
		{

			Node recordNode = listOfRecordElements.item(s);

			// This is how the identifier looks like: we need the number:
			// although we can also get the the number from the 'priref' tag directly - the 'identifier' tag is part of
			// oai, so we rather use this known tag
			// <identifier>oai:AHM:3</identifier>
			long id = Long.MIN_VALUE;

			String idStr = getRecordId(recordNode);
			String[] idArr = idStr.split(":");
			if (idArr.length == 3)
			{
				id = Long.parseLong(idArr[2]);
			}
			else if (idArr.length == 2)
			{
				id = Long.parseLong(idArr[1]);
			}

			String description = getNLDescription(recordNode);
			String title = getTitle(recordNode);
			String image = getImage(recordNode);
			List<String> categories = getKeyWords(recordNode);

			String dateString = getDateString(recordNode);

			Date date;
			// simple data format is not multithreaded, so wrap it
			synchronized (DATE_FORMAT)
			{
				date = DATE_FORMAT.parse(dateString);
			}
			long dateSeconds = date.getTime() / 1000;

			CollectionItem item = new CollectionItem(id, idStr, "cp", categories, title, description, dateSeconds,
					categories, null, image);

			result.add(item);
		}

		return result;
	}

	/**
	 * @param recordElement
	 * @return
	 */
	public static String getRecordId(Node recordElement)
	{
		List<Element> resultList = UtilsXml.findAllElementsByTagName((Element) recordElement, IDENTIFIER);
		if (resultList.size() > 0)
		{
			return getTextValue(resultList.get(0));
		}
		else
		{
			return null;
		}
	}

	/**
	 * returns the date as string
	 * @param recordElement
	 * @return date as string
	 */
	public static String getDateString(Node recordElement)
	{
		//
		//
		List<Element> resultList = UtilsXml.findAllElementsByTagName((Element) recordElement, DATE);

		String result = getTextValue(resultList.get(0));

		return result;

	}

	public static String getTitle(Node recordElement)
	{
		List<Element> titles = UtilsXml.findAllElementsByTagName((Element) recordElement, TITLE);
		// <dc:title>Monument voor Amsterdam</dc:title><dc:title>Monument voor Amsterdam</dc:title>
		if (titles.size() > 0)
		{
			return getTextValue(titles.get(0));
		}
		else
		{
			return null;
		}
	}

	public static String getImage(Node recordElement)
	{
		List<Element> images = UtilsXml.findAllElementsByTagName((Element) recordElement, IMAGE);
		if (images.size() > 0)
		{
			return getTextValue(images.get(0));
		}
		else
		{
			return null;
		}
	}

	/**
	 * Get all <em>keywords</em> we map: <tt>dc:subject</tt>, <tt>dc:type</tt> and <tt>dc:source</tt> to keywords.
	 * @param recordElement
	 * @return
	 */
	public static List<String> getKeyWords(Node recordElement)
	{
		List<Element> keywords = UtilsXml.findAllElementsByTagName((Element) recordElement, SUBJECT);
		keywords.addAll(UtilsXml.findAllElementsByTagName((Element) recordElement, DCTYPE));
		keywords.addAll(UtilsXml.findAllElementsByTagName((Element) recordElement, DCSOURCE));
		if (keywords.size() > 0)
		{
			Set<String> result = new HashSet<String>();
			for (Element keyword : keywords)
			{
				String text = getTextValue(keyword);
				if (text != null)
				{
					result.add(text);
				}
			}
			return new ArrayList<String>(result);
		}
		return null;
	}

	/**
	 * return all descriptions with 'zaaltekst NL' - if there's more than one, they will be concatenated, with a line end
	 * as separator.
	 * @param recordElement - the record xml element
	 * @return description text string.
	 */
	public static String getNLDescription(Node recordElement)
	{
		List<Element> descriptions = UtilsXml.findAllElementsByTagName((Element) recordElement, DESCRIPTION);

		if (!descriptions.isEmpty())
		{
			// get the NL part
			StringBuilder result = new StringBuilder();

			for (Node description : descriptions)
			{
				// get type, get description - only return the NL descriptions...
				List<Element> languages = UtilsXml.findAllElementsByTagName((Element) description, DESCR_LANGTYPE);
				String language = null;
				String content = null;
				if (languages.size() > 0)
				{
					language = getTextValue(languages.get(0));
				}
				List<Element> contents = UtilsXml.findAllElementsByTagName((Element) description, DESCR_CONTENT);
				if (contents.size() > 0)
				{
					content = getTextValue(contents.get(0));
				}

				if (LANGUAGE_NL.equals(language))
				{
					result.append(content).append('\n');
				}
			}
			return result.toString().trim();
		}
		return null;
	}

	/**
	 * Returns element value
	 * @param elem element (it is XML tag)
	 * @return Element value otherwise empty String
	 */
	public final static String getTextValue(Element elem)
	{
		if (elem != null)
		{
			NodeList children = elem.getChildNodes();

			if (children.getLength() == 1)
			{
				Node child = children.item(0);
				return child.getTextContent();
			}
		}

		return null;
	}

}
