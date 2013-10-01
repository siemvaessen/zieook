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

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import nl.gridline.zieook.model.CollectionItem;

import org.imsglobal.xsd.imsmd_v1p2.DescriptionType;
import org.imsglobal.xsd.imsmd_v1p2.KeywordType;
import org.imsglobal.xsd.imsmd_v1p2.LangstringType;
import org.imsglobal.xsd.imsmd_v1p2.LocationType;
import org.imsglobal.xsd.imsmd_v1p2.LomType;
import org.imsglobal.xsd.imsmd_v1p2.TitleType;
import org.openarchives.oai._2.RecordType;
import org.openarchives.oai._2.RecordWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-czp<br />
 * CzpRecordParseJAXB.java created 24 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class CzpRecordParseJAXB
{
	final static SimpleDateFormat CZP_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private static final Logger LOG = LoggerFactory.getLogger(CzpRecordParseJAXB.class);
	private final Unmarshaller unmarshal;
	private final String cp;
	private final String language;

	public CzpRecordParseJAXB(String cp, String language) throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance("org.imsglobal.xsd.imsmd_v1p2:org.openarchives.oai._2");
		unmarshal = jc.createUnmarshaller();
		this.cp = cp;
		this.language = language;
	}

	public CollectionItem getCZPfromXML(String xmlData) throws ParseException, JAXBException
	{

		// probably we only recieve the header and need to do a get record?

		StringReader reader = new StringReader(xmlData);
		try
		{
			final RecordWrapper record = (RecordWrapper) unmarshal.unmarshal(reader);
			if (record.getValue() == null)
			{
				LOG.error("record has no data: {}", xmlData);
				return null;
			}

			final RecordType recordType = record.getValue();

			if (recordType.getMetadata() == null)
			{
				LOG.error("record has no metadata: {}", xmlData);
				return null;
			}

			JAXBElement<?> any = (JAXBElement<?>) recordType.getMetadata().getAny();
			if (any == null)
			{
				LOG.error("record has no metadata: {}", xmlData);
				return null;
			}
			if (any.getDeclaredType() != LomType.class)
			{
				LOG.error("record metadata is not of czp type: {}", xmlData);
				return null;
			}

			LomType czp = (LomType) any.getValue();
			List<Object> content = czp.getGeneral().getContent();

			String collectionId = record.getValue().getHeader().getIdentifier();
			long date = getDate(record.getValue().getHeader().getDatestamp());
			long id = getCollectionId(collectionId);
			String location = null;
			String title = null;
			String description = null;
			String image = null;
			List<String> keywords = new ArrayList<String>();
			List<String> categories = new ArrayList<String>();

			for (Object el : content)
			{
				Class<?> type = ((JAXBElement<?>) el).getDeclaredType();
				if (type == TitleType.class)
				{
					@SuppressWarnings("unchecked")
					TitleType titleType = ((JAXBElement<TitleType>) el).getValue();
					title = getContent(titleType.getLangstring());
				}
				else if (type == DescriptionType.class)
				{
					@SuppressWarnings("unchecked")
					DescriptionType descriptionType = ((JAXBElement<DescriptionType>) el).getValue();
					description = getContent(descriptionType.getLangstring());
				}
				else if (type == KeywordType.class)
				{
					@SuppressWarnings("unchecked")
					KeywordType descriptionType = ((JAXBElement<KeywordType>) el).getValue();
					String keyword = getContent(descriptionType.getLangstring());
					if (keyword != null)
					{
						keywords.add(keyword);
						categories.add(keyword);
					}
				}
				else
				{
					LOG.debug("skipped content: {}", ((JAXBElement<?>) el).getDeclaredType());
				}
			}

			for (Object el : czp.getTechnical().getContent())
			{
				Class<?> type = ((JAXBElement<?>) el).getDeclaredType();
				if (type == LocationType.class)
				{
					@SuppressWarnings("unchecked")
					LocationType locationType = ((JAXBElement<LocationType>) el).getValue();
					location = locationType.getValue();
				}
			}

			return new CollectionItem(id, collectionId, cp, categories, title, description, date, keywords, location,
					image);
		}
		finally
		{
			reader.close();
		}

	}

	private long getDate(String dateString) throws ParseException
	{
		Date date;
		synchronized (CZP_DATE_FORMAT)
		{
			date = CZP_DATE_FORMAT.parse(dateString);
		}
		return date.getTime() / 1000;
	}

	private long getCollectionId(String identifier)
	{
		long id = Long.MIN_VALUE;
		String[] idArr = identifier.split(":");
		if (idArr.length == 3)
		{
			id = Long.parseLong(idArr[2]);
		}
		else if (idArr.length == 2)
		{
			id = Long.parseLong(idArr[1]);
		}
		else if (idArr.length == 1)
		{
			id = Long.parseLong(identifier);
		}
		else
		{
			LOG.error("failed to extract id, returning default: {}", identifier);
		}

		return id;
	}

	/**
	 * Get the text content of a languageType object
	 * @param languageType
	 * @return
	 */
	private String getContent(List<LangstringType> languageType)
	{
		String result = null;

		if (languageType == null)
		{
			return result;
		}

		for (LangstringType lang : languageType)
		{
			if (language.equals(lang.getLang()))
			{
				result = lang.getValue();
				break;
			}
		}
		return result;
	}
}
