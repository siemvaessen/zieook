// ---------------------------------------------------------
// Copyright, all rights reserved 2012 GridLine Amsterdam
// ---------------------------------------------------------
package nl.gridline.zieook.configuration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.bind.JAXBException;

import nl.gridline.config.model.Configuration;
import nl.gridline.config.model.Property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic key value thread safe configuration object.
 * <p />
 * Project zieook-configuration<br />
 * AbstractConfig.java created 1 mrt. 2012
 * <p />
 * Copyright, all rights reserved 2012 GridLine Amsterdam
 * @author <a href="mailto:job@gridline.nl">Job</a>
 * @version $Revision:$, $Date:$
 */
public class GridlineConfig
{
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

	/**
	 * Home folder of the project
	 */
	public static final String HOME = "project.home";

	public static final String DEFAULT_FILE;
	public static final String DEFAULT_RESOURCE;

	protected static GridlineConfig instance;

	private final Map<String, String> properties;
	private final Map<String, String> descriptions;

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

	static
	{
		String projectName = System.getProperty("gridline.project.name");
		if (projectName != null)
		{
			DEFAULT_FILE = projectName + "-default.xml";
			DEFAULT_RESOURCE = projectName + "-default.xml";
		}
		else
		{
			LOG.warn("The system property <gridline.project.name> is set to 'project' you may want to update this to a real name");
			DEFAULT_FILE = "project-default.xml";
			DEFAULT_RESOURCE = "project-default.xml";
		}
	}

	/**
	 * Private constructor, to ensure singleton instance
	 */
	protected GridlineConfig()
	{
		writeLock.lock();
		try
		{
			properties = new HashMap<String, String>();
			descriptions = new HashMap<String, String>();
		}
		finally
		{
			writeLock.unlock();
		}

		try
		{
			loadResources(); // check resources first
			loaddefault(); // load from file
		}
		catch (Exception e) // hm... not very nice, but quite handy
		{
			LOG.error("No default properties loaded, this may lead to an ill-configured system!", e);
		}
	}

	public Map<String, String> getMap()
	{

		final Map<String, String> result = new HashMap<String, String>();

		readLock.lock();
		try
		{
			Set<String> keys = properties.keySet();
			for (Object key : keys)
			{
				Object value = properties.get(key);
				result.put(key.toString(), value != null ? value.toString() : "");
			}
		}
		finally
		{
			readLock.unlock();
		}
		return result;
	}

	public String getHome()
	{
		return get(HOME);
	}

	/**
	 * Loads properties from resources
	 * @throws JAXBException
	 */
	protected void loadResources() throws JAXBException
	{
		URL url = Config.class.getResource("project-defaults.xml");
		if (url != null)
		{
			int size = 0;
			// load configuration from file:
			Configuration configuration = Configuration.create(url);
			// check if it has a configuration element:
			List<Property> list = configuration.getProperties();
			if (list != null && list.size() > 0)
			{
				writeLock.lock();
				try
				{
					// write the properties to the set:
					for (Property property : list)
					{
						size++;
						properties.put(property.getName(), property.getValue());
						descriptions.put(property.getName(), property.getDescription());
					}
				}
				finally
				{
					writeLock.unlock();
				}
			}
			LOG.info("loaded {} properties from '{}'", size, url);
		}
		else
		{
			LOG.warn("No settings in resources");
		}
	}

	/**
	 * Load default settings from some common locations: <br />
	 * <tt>etc/</tt><br />
	 * <tt>../etc</tt><br />
	 * <tt>./</tt><br />
	 * We have to add some more locations in the near future
	 * @throws XmlException
	 * @throws IOException
	 */
	protected void loaddefault() throws IOException, JAXBException
	{
		LOG.info("trying to load default settings: '" + DEFAULT_FILE + "' from: 'etc/' '../etc' './'");

		File f = new File("./etc/" + DEFAULT_FILE).getCanonicalFile();

		if (f.exists() && f.canRead() && f.isFile())
		{
			loadFromfile(f);
			LOG.info("loaded: {}", f.toString());
			return;
		}
		LOG.info("failed loading {}", f.toString());

		f = new File("../etc/" + DEFAULT_FILE).getCanonicalFile();
		if (f.exists() && f.canRead() && f.isFile())
		{
			loadFromfile(f);
			LOG.info("loaded: {}", f.toString());
			return;
		}
		LOG.info("failed loading {}", f.toString());

		f = new File(DEFAULT_FILE).getCanonicalFile();
		if (f.exists() && f.canRead() && f.isFile())
		{
			loadFromfile(f);
			LOG.info("loaded: {}", f.toString());
			return;
		}
		LOG.info("failed loading {}", f.toString());

		LOG.error("No default properties loaded, this may lead to an ill configuration system!");
	}

	/**
	 * Get the instance of the configuration manager - only loaded with properties given through the system properties
	 * @return the instance of the configuration manager.
	 */
	public static synchronized GridlineConfig getInstance()
	{
		if (instance == null)
		{
			instance = new GridlineConfig();
		}
		return instance;
	}

	/**
	 * Get the instance of the configuration manager, loaded with the system properties and the properties from the
	 * filename
	 * @param filename - a filename.
	 * @return the instance of the configuration manager
	 */
	public static synchronized GridlineConfig getInstance(String filename)
	{
		if (instance == null)
		{
			getInstance();
			instance.load(filename);
		}
		else
		{
			instance.load(filename);
		}
		return instance;
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}

	/**
	 * Check if a key exists
	 * @param key
	 * @return true if exists
	 */
	public boolean exists(String key)
	{
		readLock.lock();
		try
		{
			final boolean result = properties.containsKey(key);
			if (result)
			{
				LOG.debug("key exists '{}'", key);
			}
			else
			{
				LOG.debug("key does not exist '{}'", key);
			}
			return result;
		}
		finally
		{
			readLock.unlock();
		}
	}

	/**
	 * Returns the value for the given key, or the default if it did not existed.
	 * @param key
	 * @param def
	 * @return
	 */
	public String get(String key, String def)
	{
		readLock.lock();
		try
		{
			final String result = properties.get(key);

			if (result == null)
			{
				LOG.debug("no value for key '{}' returning default '{}'", key, def);
				return def;
			}

			LOG.debug("returning value '{}' for key '{}'", result, key);
			return result;
		}
		finally
		{
			readLock.unlock();
		}
	}

	/**
	 * Get the value for the given key
	 * @param key
	 * @return
	 */
	public String get(String key)
	{
		readLock.lock();
		try
		{
			final String result = properties.get(key);

			LOG.debug("getting '{}' returned '{}'", key, result);
			return result;
		}
		finally
		{
			readLock.unlock();
		}
	}

	public Property getProperty(String key)
	{
		final String value = get(key);
		final String description = descriptions.get(key);
		return new Property(key, value, description);
	}

	public String getDescription(String key)
	{
		return descriptions.get(key);
	}

	public long getLong(String key, long defaultValue)
	{
		final String value = get(key);
		try
		{
			return Long.parseLong(value);
		}
		catch (NumberFormatException e)
		{
			// failed:
			LOG.warn("failed to read long value from <{}> with value '{}' returning default '" + defaultValue + "'", key,
					value);
		}
		return defaultValue;
	}

	public boolean getBoolean(String key, boolean defaultValue)
	{
		final String value = get(key);
		if (value == null)
		{
			LOG.warn("failed to read boolean value from <{}> returning default '{}' ", key, defaultValue);
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}

	public int getInt(String key, int defaultValue)
	{
		String value = get(key);
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			// failed:
			LOG.warn("failed to read long value from <{}> with value '{}' returning default '" + defaultValue + "'", key,
					value);
		}
		return defaultValue;
	}

	/**
	 * Set the value for the given key
	 * @param key
	 * @param value
	 * @return the previous value associated with this key
	 */
	public String setProperty(String key, String value)
	{
		if (value != null)
		{
			writeLock.lock();
			try
			{
				return properties.put(key, value);
			}
			finally
			{
				writeLock.unlock();
			}
		}
		else
		{
			LOG.warn("value for key '{}' was null, skipping", key);
		}
		return null;
	}

	public String setDescription(String key, String value)
	{
		if (value != null)
		{
			return descriptions.put(key, value);
		}
		return null;
	}

	/**
	 * Shorthand for setProperty
	 * @param key
	 * @param value
	 * @return the previous value associated with this key
	 */
	public String set(String key, String value)
	{
		return setProperty(key, value);
	}

	/**
	 * Remove the key and value
	 * @param key
	 * @return the value associated with the key
	 */
	public String remove(String key)
	{
		writeLock.lock();
		try
		{
			final String result = properties.remove(key);
			LOG.debug("value '{}' removed for key '{}'", result, key);
			return result;
		}
		finally
		{
			writeLock.unlock();
		}
	}

	/**
	 * Parse commandline arguments and add them to the config properties
	 * all key values should have the form: <tt>--key=value</tt><br />
	 * all switches only need <tt>--key</tt><br />
	 * some shorthand for switches are set by <tt>-k</tt><br />
	 * All keys in the configuration files are accepted in a form of <tt>keypart-keypart</tt> or <tt>keypart.keypart</tt>
	 * e.g. <tt>--zieook.zookeeper.port</tt> or <tt>--zieook-zookeeper-port</tt> are treated as the same value.
	 * @param args argument array
	 * @return message - that shows the success or failure of the arguments given.
	 */
	public String parseArguments(String[] args)
	{
		StringBuilder message = new StringBuilder();
		for (String arg : args)
		{
			if (arg.startsWith("--"))
			{
				// put directly into the map, replace - with .
				String[] keyvalue = arg.split("=");
				if (keyvalue.length == 2)
				{
					writeLock.lock();
					try
					{
						properties.put(keyvalue[0].substring(2), keyvalue[1].trim());
					}
					finally
					{
						writeLock.unlock();
					}
				}
				else if (keyvalue.length == 1)
				{
					writeLock.lock();
					try
					{
						properties.put(arg.substring(2), "true");
					}
					finally
					{
						writeLock.unlock();
					}
				}
				else
				{
					message.append("\n the argument: '").append(arg).append("' is not processed");
				}
			}
			// else if (arg.startsWith("-"))
			// {
			// // no short hand arguments yet...
			// }
		}
		return message.toString();
	}

	/**
	 * Load properties from an xml file
	 * @param filename
	 */
	protected void load(String filename)
	{
		// load from file...
		File file = new File(filename);
		try
		{
			file = file.getCanonicalFile();
			loadFromfile(file);
		}
		catch (JAXBException e)
		{
			LOG.error("Failed  to load '" + file.toString() + "'", e);
		}
		catch (IOException e)
		{
			LOG.error("Failed to load '" + file.toString() + "' ", e);
		}
	}

	/**
	 * Read all system properties that are used in ZieOok and add them to the preferences
	 */
	public void setSystemProperties(String[] keyset)
	{
		// read system properties:
		if (keyset != null)
		{
			writeLock.lock();
			try
			{
				for (String key : keyset)
				{
					String value = System.getProperty(key);
					if (value != null)
					{
						properties.put(key, value);
					}
				}
			}
			finally
			{
				writeLock.unlock();
			}
		}

		// write system properties:
		System.setProperty("net.sf.ehcache.skipUpdateCheck", "false");
	}

	/**
	 * Load a Configuration file from disk
	 * @param file
	 * @throws JAXBException
	 * @throws XmlException
	 */
	final void loadFromfile(File file) throws IOException, JAXBException
	{
		if (file == null)
		{
			LOG.error("no file to load, ignoring");
			return;
		}
		int size = 0;
		if (file.exists())
		{
			// load configuration from file:
			Configuration configuration = Configuration.create(file);
			// check if it has a configuration element:
			List<Property> list = configuration.getProperties();
			if (list != null && list.size() > 0)
			{
				writeLock.lock();
				try
				{
					// write the properties to the set:
					for (Property property : list)
					{
						size++;
						properties.put(property.getName(), property.getValue());
						descriptions.put(property.getName(), property.getDescription());
					}
				}
				finally
				{
					writeLock.unlock();
				}
			}

			LOG.info("loaded {} properties from '{}'", size, file);
		}
		else
		{
			LOG.error("Failed to load '" + file.toString() + "' - file not found");
		}
	}

	@Override
	public String toString()
	{
		final StringBuilder b = new StringBuilder("\n");
		final TreeMap<String, String> sorted = new TreeMap<String, String>();
		readLock.lock();
		try
		{
			sorted.putAll(properties);
		}
		finally
		{
			readLock.unlock();
		}

		for (Map.Entry<String, String> entry : sorted.entrySet())
		{
			b.append(entry.getKey()).append("='").append(entry.getValue()).append("'\n");
		}

		return b.toString();
	}

}
