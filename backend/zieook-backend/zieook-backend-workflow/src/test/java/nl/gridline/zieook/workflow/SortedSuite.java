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
package nl.gridline.zieook.workflow;

import java.util.Comparator;
import java.util.TreeSet;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

/**
 * [purpose]
 * <p />
 * Project zieook-data<br />
 * SortedSuite.java created 31 okt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class SortedSuite extends Suite
{
	public SortedSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError
	{
		super(builder, klass, getSortedClasses(klass));
	}

	private static Class<?>[] getSortedClasses(Class<?> klass) throws InitializationError
	{
		TreeSet<Class<?>> list = new TreeSet<Class<?>>(new Comparator<Class<?>>()
		{
			@Override
			public int compare(Class<?> o1, Class<?> o2)
			{
				return o1.getSimpleName().compareTo(o2.getSimpleName());
			}
		});
		for (Class<?> innerclass : klass.getClasses())
		{
			if (Object.class.equals(innerclass.getSuperclass()))
			{
				list.add(innerclass);
			}
		}
		return list.toArray(new Class<?>[0]);
	}
}
