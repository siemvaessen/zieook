// ---------------------------------------------------------
// Copyright, all rights reserved 2011 GridLine Amsterdam
// ---------------------------------------------------------
package org.openarchives.oai._2;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * [purpose]
 * <p />
 * Project oaipmh-schema<br />
 * ListIdentifiersWrapper.java created 8 nov. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "identifiers-wrapper")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListIdentifiersWrapper implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5312500579406978183L;

	@XmlElement
	private final ListIdentifiersType identifiers;

	public ListIdentifiersWrapper()
	{
		identifiers = null;
	}

	public ListIdentifiersWrapper(ListIdentifiersType identifiers)
	{
		this.identifiers = identifiers;
	}

	public ListIdentifiersType getIdentifier()
	{
		return identifiers;
	}

	public ListIdentifiersType getValue()
	{
		return identifiers;
	}

	/**
	 * @return The identifiers.
	 */
	public ListIdentifiersType getIdentifiers()
	{
		return identifiers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		ListIdentifiersWrapper other = (ListIdentifiersWrapper) obj;
		if (identifiers == null)
		{
			if (other.identifiers != null)
			{
				return false;
			}
		}
		else if (!identifiers.equals(other.identifiers))
		{
			return false;
		}
		return true;
	}

}
