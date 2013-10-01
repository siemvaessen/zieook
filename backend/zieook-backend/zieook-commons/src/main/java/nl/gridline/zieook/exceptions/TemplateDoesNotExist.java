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

/**
 * [purpose]
 * <p />
 * Project zieook-commons<br />
 * TemplateDoesNotExist.java created 30 mrt. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class TemplateDoesNotExist extends DoesNotExists
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5143765343800497759L;

	public TemplateDoesNotExist(String template)
	{
		super("The given template '" + template + "' does not exist");
	}

	public TemplateDoesNotExist(String template, Throwable e)
	{
		super("The given template '" + template + "' does not exist", e);
	}
}
