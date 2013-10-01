// ---------------------------------------------------------
// Copyright, all rights reserved 2011 GridLine Amsterdam
// ---------------------------------------------------------
package nl.gridline.zieook;

import org.openarchives.oai._2.OAIPMHerrorType;
import org.openarchives.oai._2.OAIPMHerrorcodeType;

/**
 * [purpose]
 * <p />
 * Project oaipmh-harvester<br />
 * IdDoesNotExistException.java created 30 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class OAIException extends Exception
{

	private OAIPMHerrorcodeType type;
	private String url;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7886099518109452196L;

	public OAIException()
	{
		super();
	}

	public OAIException(Throwable e)
	{
		super(e);
	}

	public OAIException(String url, OAIPMHerrorType type)
	{
		super("oai-error = '" + type.getCode() + " url=<" + url + "> message = '" + type.getValue() + "'");
		this.type = type.getCode();
		this.url = url;
	}

	public OAIPMHerrorcodeType getType()
	{
		return type;
	}

	public String getUrl()
	{
		return url;
	}

}
