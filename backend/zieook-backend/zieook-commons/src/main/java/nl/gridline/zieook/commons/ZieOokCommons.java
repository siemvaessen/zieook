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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [purpose]
 * <p />
 * Project zieook-commons<br />
 * ZieOokCommons.java created 15 dec. 2010
 * <p />
 * Copyright, all rights reserved 2010 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision$, $Date$
 */
public class ZieOokCommons
{

	private static final Logger logger = LoggerFactory.getLogger(ZieOokCommons.class);

	/**
	 * Get the jar MANIFEST.MF for the given class file
	 * @param clazz a class file
	 * @return the manifest object, if it was found
	 */
	public static Manifest getVersionInfo(Class<?> clazz)
	{
		Manifest result = null;
		try
		{
			URI uri = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
			logger.info("found: '" + uri.toString() + "' library, loading manifest");
			JarFile jar = new JarFile(new File(uri));
			result = jar.getManifest();
		}
		catch (ZipException e)
		{
			logger.error("failed to open the zip file for: " + clazz.toString());
		}
		catch (IOException e)
		{
			logger.error("failed to load manifest file for: " + clazz.toString());
		}
		catch (URISyntaxException e)
		{
			logger.error("failed to extract library uri for: " + clazz.toString());
		}

		return result;
	}

	/**
	 * Checks java version, should be at least 1.5
	 * @return true of Java version >= 1.5, false otherwise
	 */
	public static boolean checkVersion()
	{
		String vers = System.getProperty("java.version");
		return (vers.compareTo("1.5") < 0);
	}

	public static String someinformation(String name)
	{
		File f = new File(".");
		String currentDir;
		try
		{
			currentDir = f.getCanonicalPath();
		}
		catch (IOException e)
		{
			currentDir = " could not be determined!";

		}

		StringBuilder result = new StringBuilder();
		result.append("\tRunning ZieOok - ").append(name).append(" - on '").append(System.getProperty("java.vendor"))
				.append(" v").append(System.getProperty("java.version")).append("' JVM").append("\n\t");
		result.append("JAVA_HOME=").append(System.getProperty("java.home")).append("\n\t");
		result.append("Current user: ").append(System.getProperty("user.name")).append("\n\t");
		result.append("Running in dir: ").append(System.getProperty("user.home")).append("\n\t");
		result.append("Current dir: ").append(currentDir).append("\n\t");
		result.append("Temp dir: ").append(System.getProperty("java.io.tmpdir")).append("\n\t");
		result.append("OS '").append(System.getProperty("os.name")).append(" v").append(System.getProperty("os.version"))
				.append("' on a '").append(System.getProperty("os.arch")).append("' system\n\t");
		result.append("Number of processors: ").append(Runtime.getRuntime().availableProcessors());

		// TODO add stuf to fully read a Manifest file!
		/**
		 * Manifest-Version: 1.0
		 * Archiver-Version: Plexus Archiver
		 * Created-By: Apache Maven
		 * Built-By: job
		 * Build-Jdk: 1.6.0_22
		 * Implementation-Build: 2010-12-14 16:31:35
		 */

		// Manifest manifest = ConfigurationManager.getVersionInfo(WorkflowEngine.class);
		// if (manifest != null)
		// {
		// Map<String, Attributes> map = manifest.getEntries();
		// for (Map.Entry<String, Attributes> entry : map.entrySet())
		// {
		// System.out.println(entry.getKey());
		// for (Map.Entry<Object, Object> e : entry.getValue().entrySet())
		// {
		// System.out.println(e.getKey() + " =" + e.getValue());
		// }
		// }
		// }

		return result.toString();
	}
}
