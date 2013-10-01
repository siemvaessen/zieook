// ---------------------------------------------------------
// Copyright, all rights reserved 2011 GridLine Amsterdam
// ---------------------------------------------------------
package nl.gridline.zieook;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.openarchives.oai._2.IdentifyType;
import org.openarchives.oai._2.ListMetadataFormatsType;
import org.openarchives.oai._2.MetadataFormatType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tools class, some handy methods that can do some generic oai queries to get the basic information from the server.
 * <p />
 * Project oaipmh-harvester<br />
 * Tools.java created Sep 14, 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class OAITools
{
	private static final Logger LOG = LoggerFactory.getLogger(OAITools.class);

	private OAITools()
	{
		// utility class: hide constructor
	}

	public static String getRepositoryName(URL url) throws OAIException
	{
		try
		{
			final IdentifyType result = new Harvester(url).identify();
			return result.getRepositoryName();
		}
		catch (IOException e)
		{
			LOG.error("failed to process request", e);
		}
		catch (JAXBException e)
		{
			LOG.error("failed to process request", e);
		}
		return null;
	}

	public static boolean hasMetadataPrefix(URL url, final String prefix) throws OAIException
	{
		if (prefix == null)
		{
			return false;
		}
		List<String> prefixes = getMetadataPrefixes(url);
		if (prefixes != null)
		{
			for (String p : prefixes)
			{
				if (prefix.equals(p))
				{
					return true;
				}
			}
		}
		return false;
	}

	public static List<String> getMetadataPrefixes(URL url) throws OAIException
	{
		try
		{
			ListMetadataFormatsType format = new Harvester(url).listMetadataFormats();
			List<MetadataFormatType> formats = format.getMetadataFormat();
			List<String> result = new ArrayList<String>(formats.size());
			for (MetadataFormatType f : formats)
			{
				result.add(f.getMetadataPrefix());
			}
			return result;
		}
		catch (IOException e)
		{
			LOG.error("failed to process request", e);
		}
		catch (JAXBException e)
		{
			LOG.error("failed to process request", e);
		}
		return null;
	}
}
