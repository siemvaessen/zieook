// ---------------------------------------------------------
// Copyright, all rights reserved 2010 GridLine Amsterdam
// ---------------------------------------------------------
package nl.gridline.zieook;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.openarchives.oai._2.GetRecordType;
import org.openarchives.oai._2.IdentifyType;
import org.openarchives.oai._2.ListIdentifiersType;
import org.openarchives.oai._2.ListMetadataFormatsType;
import org.openarchives.oai._2.ListRecordsType;
import org.openarchives.oai._2.ListSetsType;
import org.openarchives.oai._2.OAIPMHerrorType;
import org.openarchives.oai._2.OAIPMHerrorcodeType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.ResumptionTokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that acts as a facade for an OAI-PMH server. Has methods
 * that corresponds to the different verbs in the OAI-PMH specification
 * and that will return appropriate objects based on the response.
 * <p>
 * For more about the different verbs, requests and responses in the OAI-PMH specification, see <a
 * href="http://www.openarchives.org/OAI/openarchivesprotocol.html">
 * http://www.openarchives.org/OAI/openarchivesprotocol.html</a>.
 * <p />
 * Project oaipmh-harvester<br />
 * Harvester.java created 3 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @author Oskar Grenholm, National Library of Sweden
 * @version $Revision:$, $Date:$
 */
public class Harvester
{

	private static final Logger LOG = LoggerFactory.getLogger(Harvester.class);

	private static final int RETRIES = 5;
	private static final int BACKOFF_INC = 500;
	private static final int SLOWDOWN_TIME = 1000;
	private static final int SLOWDOWN_THRESHOLD = 30;

	// count errors, if there are many we try to slow down a bit
	private static int errorCount = 0;
	private static boolean slowdown = false;
	private final Object readLock = new Object();

	private final QueryBuilder builder;
	private final JAXBContext jc;

	/**
	 * Creates an <code>OaiPmhServer</code> with the given base URL.
	 * @param url base URL that points to an OAI-PMH server
	 * @throws JAXBException
	 */
	public Harvester(String url) throws JAXBException
	{
		builder = new QueryBuilder(url);
		jc = JAXBContext.newInstance(OAIPMHtype.class);
	}

	/**
	 * Creates an <code>OaiPmhServer</code> with the given base URL.
	 * @param url base URL that points to an OAI-PMH server
	 * @throws JAXBException
	 */
	public Harvester(URL url) throws JAXBException
	{
		this(url.toString());
	}

	/**
	 * Get the base URL to the OAI-PMH server.
	 * @return the base URL
	 */
	public String getBaseUrl()
	{
		return builder.getBaseUrl();
	}

	/**
	 * Send a GetRecord request to the OAI-PMH server with
	 * the specified parameters.
	 * @param identifier id to get a Record for
	 * @param metadataPrefix which metadata format
	 * @return the response from the server
	 * @throws IOException
	 * @throws JAXBException
	 * @throws OAIException
	 * @throws XmlException
	 */
	public GetRecordType getRecord(String identifier, String metadataPrefix) throws IOException, JAXBException,
			OAIException
	{
		String query = builder.buildGetRecordQuery(identifier, metadataPrefix);
		return unMarshal(query).getGetRecord();
	}

	/**
	 * Send a request for the OAI-PMH server to Identify it self.
	 * @return the response from the server
	 * @throws JAXBException
	 * @throws OAIException
	 */
	public IdentifyType identify() throws IOException, JAXBException, OAIException
	{

		String query = builder.buildIdentifyQuery();
		return unMarshal(query).getIdentify();
	}

	/**
	 * Send a request to the OAI-PMH server that it should list all
	 * identifiers that has metadata in the specified format.
	 * @param metadataPrefix which metadata format
	 * @return a list of identifiers
	 * @throws OAIException
	 */
	public ListIdentifiersType listIdentifiers(String metadataPrefix) throws JAXBException, IOException, OAIException
	{
		return listIdentifiers(metadataPrefix, null, null, null);
	}

	/**
	 * Send a request to the OAI-PMH server that it should list all
	 * identifiers that matches the given parameters.
	 * @param metadataPrefix which metadata format
	 * @param from a start date, optional (may be <code>null</code>)
	 * @param until a stop date, optional (may be <code>null</code>)
	 * @param set a specific set, optional (may be <code>null</code>)
	 * @return a list of identifiers
	 * @throws JAXBException
	 * @throws OAIException
	 */
	public ListIdentifiersType listIdentifiers(String metadataPrefix, String from, String until, String set)
			throws IOException, JAXBException, OAIException
	{

		String query = builder.buildListIdentifiersQuery(metadataPrefix, from, until, set);
		return unMarshal(query).getListIdentifiers();
	}

	/**
	 * List next set of identifiers not returned in the previous response from
	 * a call to listIdentifiers().
	 * @param resumptionToken a resumption token returned from a previous call
	 * @return a list of identifiers
	 * @throws JAXBException
	 * @throws OAIException
	 */
	public ListIdentifiersType listIdentifiers(ResumptionTokenType resumptionToken) throws IOException, JAXBException,
			OAIException
	{
		if (resumptionToken == null)
		{
			return null;
		}
		String query = builder.buildListIdentifiersQuery(resumptionToken);
		return unMarshal(query).getListIdentifiers();
	}

	/**
	 * Send a request for the OAI-PMH server to return a list of Records.
	 * @param metadataPrefix which metadata format
	 * @return a list of records
	 * @throws JAXBException
	 * @throws OAIException
	 */
	public ListRecordsType listRecords(String metadataPrefix) throws IOException, JAXBException, OAIException
	{
		return listRecords(metadataPrefix, null, null, null);
	}

	/**
	 * Send a request for the OAI-PMH server to return a list of Records.
	 * @param metadataPrefix which metadata format
	 * @param set a specific set, optional (may be <code>null</code>)
	 * @return a list of records
	 * @throws JAXBException
	 * @throws OAIException
	 */
	public ListRecordsType listRecords(String metadataPrefix, String set) throws IOException, JAXBException,
			OAIException
	{
		String query = builder.buildListRecordsQuery(metadataPrefix, null, null, set);
		return unMarshal(query).getListRecords();
	}

	/**
	 * Send a request for the OAI-PMH server to return a list of Records.
	 * @param metadataPrefix which metadata format
	 * @param from a start date, optional (may be <code>null</code>)
	 * @param until a stop date, optional (may be <code>null</code>)
	 * @param set a specific set, optional (may be <code>null</code>)
	 * @return a list of records
	 * @throws JAXBException
	 * @throws OAIException
	 */
	public ListRecordsType listRecords(String metadataPrefix, String from, String until, String set) throws IOException,
			JAXBException, OAIException
	{
		String query = builder.buildListRecordsQuery(metadataPrefix, from, until, set);
		return unMarshal(query).getListRecords();
	}

	/**
	 * List next set of records not returned in the previous response from
	 * a call to listRecords().
	 * @param resumptionToken a resumption token returned from a previous call
	 * @return a list of records
	 * @throws JAXBException
	 * @throws OAIException
	 */
	public ListRecordsType listRecords(ResumptionTokenType resumptionToken) throws IOException, JAXBException,
			OAIException
	{
		String query = builder.buildListRecordsQuery(resumptionToken);
		return unMarshal(query).getListRecords();
	}

	/**
	 * Ask the OAI-PMH server to list all metadata formats it holds.
	 * @return a list of available metadata formats
	 * @throws JAXBException
	 * @throws OAIException
	 */
	public ListMetadataFormatsType listMetadataFormats() throws IOException, JAXBException, OAIException
	{
		return listMetadataFormats(null);
	}

	/**
	 * Ask the OAI-PMH server to list all metadata formats it holds
	 * for the specified identifier.
	 * @return a list of available metadata formats
	 * @throws JAXBException
	 * @throws OAIException
	 */
	public ListMetadataFormatsType listMetadataFormats(String identifier) throws IOException, OAIException,
			JAXBException
	{
		String query = builder.buildListMetadataFormatsQuery(identifier);
		return unMarshal(query).getListMetadataFormats();
	}

	/**
	 * List all sets the OAI-PMH server has.
	 * @return a list of sets
	 * @throws JAXBException
	 * @throws OAIException
	 */
	public ListSetsType listSets() throws IOException, JAXBException, OAIException
	{
		String query = builder.buildListSetsQuery();
		return unMarshal(query).getListSets();
	}

	/**
	 * List next set of sets not returned in the previous response from
	 * a call to listSets().
	 * @param resumptionToken
	 * @return a list of sets
	 * @throws JAXBException
	 * @throws OAIException
	 */
	public ListSetsType listSets(ResumptionTokenType resumptionToken) throws IOException, JAXBException, OAIException
	{
		String query = builder.buildListSetsQuery(resumptionToken);
		return unMarshal(query).getListSets();
	}

	// private URLConnection createConnection(String query) throws IOException
	// {
	// URL url = new URL(query);
	// URLConnection connection = url.openConnection();
	// connection.setConnectTimeout(CONNECT_TIMEOUT);
	// connection.setDoInput(true);
	// connection.setDoOutput(false);
	// connection.setReadTimeout(READ_TIMEOUT);
	// return connection;
	// }

	/**
	 * unmarshal an oai document.<br />
	 * <ol>
	 * <li>retry {@code RETRIES} times if failed, unless we get an {@code OAIPMHerrorcodeType.ID_DOES_NOT_EXIST}
	 * exception.</li>
	 * <li></li>
	 * </ol>
	 * @param query
	 * @return
	 * @throws OAIException
	 * @throws IOException
	 * @throws JAXBException
	 */
	private OAIPMHtype unMarshal(String query) throws OAIException, IOException, JAXBException
	{
		OAIPMHtype result = null;
		long sleep = 0;
		long retried = 0;

		Throwable error = null;

		while (result == null && retried < RETRIES)
		{
			try
			{
				synchronized (readLock)
				{
					if (slowdown)
					{
						// only a single thread allowed to do http requests:
						slowdown();
						result = simpleUnmarshal(query);
					}
					else
					{
						result = simpleUnmarshal(query);
					}
				}
				if (LOG.isDebugEnabled() && retried > 0)
				{
					LOG.debug("succes after {} retries", retried);
				}
				return result;
			}
			catch (OAIException e)
			{
				if (e.getType() == OAIPMHerrorcodeType.ID_DOES_NOT_EXIST)
				{
					throw e;
				}
				error = e;
				LOG.error("failed to get data", e);
			}
			catch (IOException e)
			{
				// increase the amount or errors:
				setError();
				// show error:
				error = e;
				LOG.error("failed with message: '{}'", e.getMessage());

			}

			retried++;
			// incremental back-off
			sleep += sleep + BACKOFF_INC;
			LOG.debug("retry {} more times, but wait {}", (RETRIES - retried), sleep);
			// back-off and try again:
			try
			{
				Thread.sleep(sleep);
			}
			catch (InterruptedException e)
			{
				LOG.error("sleep interrupted", e);
				Thread.currentThread().interrupt();
			}
		}
		if (result == null)
		{
			// rethrow the error:
			if (error instanceof IOException)
			{
				throw (IOException) error;
			}
			else
			{
				throw (OAIException) error;
			}
		}
		return result;
	}

	/**
	 * read data from url, having a back-off, slow-down and some other more or less intelligent ways to cope with flacky
	 * connections and badly implemented OAI servers.
	 * @param query
	 * @return
	 * @throws OAIException
	 * @throws IOException
	 * @throws JAXBException
	 */
	private OAIPMHtype simpleUnmarshal(String query) throws OAIException, IOException, JAXBException
	{
		JAXBElement<?> result = (JAXBElement<?>) jc.createUnmarshaller().unmarshal(new URL(query));
		if (isType(OAIPMHtype.class, result))
		{
			OAIPMHtype object = (OAIPMHtype) result.getValue();
			isError(object, query);
			return object;
		}
		else
		{
			throw new IOException("response is not of the expected type, expected: " + OAIPMHtype.class.getCanonicalName()
					+ " got: " + result != null ? result.getDeclaredType().getCanonicalName() : "null");
		}
	}

	/**
	 * test the declaredtype of the given jaxbelement.
	 * @param type
	 * @param response
	 * @return
	 */
	private boolean isType(Class<?> type, JAXBElement<?> response)
	{
		Class<?> declared = response.getDeclaredType();
		return declared == type;
	}

	/**
	 * tests is the oai results is an oai error, if so throw it!
	 * @param response
	 * @return
	 * @throws OAIException
	 */
	private boolean isError(OAIPMHtype response, String requestUrl) throws OAIException
	{
		List<OAIPMHerrorType> errors = response.getError();
		if (errors != null && !errors.isEmpty())
		{
			for (OAIPMHerrorType e : errors)
			{
				throw new OAIException(requestUrl, e);
			}
		}
		return false;
	}

	/**
	 * Increase the error count, if the threshold is reached, we go in slow down mode
	 */
	private synchronized void setError()
	{
		if (!slowdown)
		{
			errorCount++;
			slowdown = errorCount >= SLOWDOWN_THRESHOLD;
			if (slowdown)
			{
				LOG.info("Counted {} errors, entering slowdown mode, only one request per {}ms is executed", errorCount,
						SLOWDOWN_TIME);
			}
		}
	}

	/**
	 * Put the thread to sleep if in slowdown mode - and allows only one call
	 * making the harvester only processing one http request at the time.
	 */
	private void slowdown()
	{
		if (slowdown)
		{
			try
			{
				Thread.sleep(SLOWDOWN_TIME);
			}
			catch (InterruptedException e)
			{
				LOG.info("slowdown sleep interrupted", e);
				Thread.currentThread().interrupt();
			}
		}
	}
}
