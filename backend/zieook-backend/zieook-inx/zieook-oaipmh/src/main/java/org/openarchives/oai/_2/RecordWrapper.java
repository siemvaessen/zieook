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
 * RecordWrapper.java created 26 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
@XmlRootElement(name = "record-wrapper")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecordWrapper implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7721018606113534162L;

	@XmlElement
	private final RecordType record;

	public RecordWrapper()
	{
		record = null;
	}

	public RecordWrapper(RecordType record)
	{
		this.record = record;
	}

	public RecordWrapper(GetRecordType record)
	{
		this.record = record.getRecord();
	}

	public RecordType getValue()
	{
		return record;
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
		result = prime * result + ((record == null) ? 0 : record.hashCode());
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
		RecordWrapper other = (RecordWrapper) obj;
		if (record == null)
		{
			if (other.record != null)
			{
				return false;
			}
		}
		else if (!record.equals(other.record))
		{
			return false;
		}
		return true;
	}

}
