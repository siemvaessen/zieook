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
package nl.gridline.zieook.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.NotImplementedException;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properly handles exceptions, sends a http response code to the client, and writes the stacktrace + message if in
 * debug is enabled
 * <p />
 * Project zieook-backend-data<br />
 * ZieOokExceptionMapper.java created 11 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@Provider
public class ZieOokExceptionMapper implements ExceptionMapper<Exception>
{
	private static final Logger LOG = LoggerFactory.getLogger(ZieOokExceptionMapper.class);

	@Override
	public Response toResponse(Exception exception)
	{
		LOG.debug("http exception thrown", exception);

		if (exception instanceof DoesNotExists)
		{
			return getResponseBuilder(404, exception).build();
		}
		else if (exception instanceof NullPointerException)
		{
			return getResponseBuilder(500, exception).build();
		}
		else if (exception instanceof BadRequestException)
		{
			return getResponseBuilder(400, exception).build();
		}
		else if (exception instanceof InternalServerErrorException)
		{
			return getResponseBuilder(500, exception).build();
		}
		else if (exception instanceof NotImplementedException)
		{
			return getResponseBuilder(501, exception).build();
		}
		else if (exception instanceof PropertyConfigurationException)
		{
			return getResponseBuilder(400, exception).build();
		}
		else if (exception instanceof RecommenderModuleUnknownException)
		{
			return getResponseBuilder(400, exception).build();
		}
		else if (exception instanceof TaskCreationFailure)
		{
			return getResponseBuilder(500, exception).build();
		}
		else if (exception instanceof BackEndException)
		{
			return getResponseBuilder(500, exception).build();
		}
		else
		{
			return Response.serverError().build();
		}
	}

	private ResponseBuilder getResponseBuilder(int status, Exception exception)
	{
		ResponseBuilder responseBuilder = Response.status(status);
		responseBuilder.type(MediaType.TEXT_HTML);
		StringBuilder b = new StringBuilder();
		b.append("<html><head><title>").append(exception.getClass().getCanonicalName()).append("</title></head><body>");
		b.append("<b>CODE</b>\t").append(status).append("<br />\n");
		b.append("<b>CLASS</b>\t").append(exception.getClass().getCanonicalName()).append("<br />\n");
		b.append("<b>MESSAGE</b>:\t<span style='color:red'>").append(exception.getMessage()).append("</span><br />\n");
		b.append("<b>STACKTRACE</b><br />\t\n");
		if (LOG.isDebugEnabled())
		{
			StackTraceElement[] ste = exception.getStackTrace();
			for (StackTraceElement s : ste)
			{
				b.append(s.toString()).append("<br />\n");
			}
			b.append("end<br />");
		}
		b.append("</body></html>");
		return responseBuilder.entity(b.toString());
	}
}
