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
package nl.gridline.zieook.commons;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * [purpose]
 * <p />
 * Project zieook-api-data<br />
 * ServerState.java created 3 feb. 2011
 * <p />
 * Copyright, all rights reserved 2011 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerState implements Serializable
{
	@XmlElement
	private final String javaVendor;
	@XmlElement
	private final String javaVersion;
	@XmlElement
	private final String javaHome;
	@XmlElement
	private final String currenUser;
	@XmlElement
	private final String userHome;
	@XmlElement
	private String currentDir;
	@XmlElement
	private final String tempDir;
	@XmlElement
	private final String osName;
	@XmlElement
	private String osVersion;
	@XmlElement
	private final String osArch;
	@XmlElement
	private final int numberOfProcessors;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6605947108643176912L;

	public ServerState()
	{
		File f = new File(".");
		try
		{
			currentDir = f.getCanonicalPath();
		}
		catch (IOException e)
		{
			currentDir = " could not be determined!";

		}

		javaVendor = System.getProperty("java.vendor");
		javaVersion = System.getProperty("java.version");
		javaHome = System.getProperty("java.home");
		currenUser = System.getProperty("user.name");
		userHome = System.getProperty("user.home");
		tempDir = System.getProperty("java.io.tmpdir");
		osName = System.getProperty("os.name");
		osArch = System.getProperty("os.arch");
		numberOfProcessors = Runtime.getRuntime().availableProcessors();
	}

	/**
	 * @return The javaVendor.
	 */
	public String getJavaVendor()
	{
		return javaVendor;
	}

	/**
	 * @return The javaVersion.
	 */
	public String getJavaVersion()
	{
		return javaVersion;
	}

	/**
	 * @return The javaHome.
	 */
	public String getJavaHome()
	{
		return javaHome;
	}

	/**
	 * @return The currenUser.
	 */
	public String getCurrenUser()
	{
		return currenUser;
	}

	/**
	 * @return The userHome.
	 */
	public String getUserHome()
	{
		return userHome;
	}

	/**
	 * @return The currentDir.
	 */
	public String getCurrentDir()
	{
		return currentDir;
	}

	/**
	 * @return The tempDir.
	 */
	public String getTempDir()
	{
		return tempDir;
	}

	/**
	 * @return The osName.
	 */
	public String getOsName()
	{
		return osName;
	}

	/**
	 * @return The osVersion.
	 */
	public String getOsVersion()
	{
		return osVersion;
	}

	/**
	 * @return The osArch.
	 */
	public String getOsArch()
	{
		return osArch;
	}

	/**
	 * @return The numberOfProcessors.
	 */
	public int getNumberOfProcessors()
	{
		return numberOfProcessors;
	}

}
