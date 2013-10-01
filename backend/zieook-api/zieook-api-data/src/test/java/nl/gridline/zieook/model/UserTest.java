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
package nl.gridline.zieook.model;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.io.Writer;
import java.util.NavigableMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * UserTest.java created 16 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class UserTest
{

	private static final Logger LOG = LoggerFactory.getLogger(UserTest.class);

	/**
	 * Test method for {@link nl.gridline.zieook.model.User#User(java.util.Map)}.
	 */
	@Test
	public final void testUserMapOfStringString()
	{
		User user = new User(100, "name", "age");

		NavigableMap<byte[], byte[]> map = user.toMap();

		// check if they are written to the map:
		LOG.info("equals? {}={}", user.getId(), ModelConstants.getUser(map));
		assertEquals(user.getId(), ModelConstants.getUser(map));
		LOG.info("equals? {}={}", user.getName(), ModelConstants.getName(map));
		assertEquals(user.getName(), ModelConstants.getName(map));
		LOG.info("equals? {}={}", user.getBirthday(), ModelConstants.getAge(map));
		assertEquals(user.getBirthday(), ModelConstants.getAge(map));
		User userNew = new User(map);
		// check if the objects are equal - all items should have been in the map
		assertEquals(user, userNew);
	}

	@Test
	public final void testUserMarshal() throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(User.class);

		Configuration config = new Configuration();
		MappedNamespaceConvention con = new MappedNamespaceConvention(config);
		Writer writer = new StringWriter();
		XMLStreamWriter xmlStreamWriter = new MappedXMLStreamWriter(con, writer);

		Marshaller marshaller = jc.createMarshaller();
		marshaller.marshal(new User(), xmlStreamWriter);

		System.out.println(writer.toString());

	}
}
