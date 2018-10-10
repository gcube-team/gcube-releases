/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ConfigurationLoader.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.server.gcube.services.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

import org.gcube.resourcemanagement.support.server.utils.ServerConsole;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public final class ConfigurationLoader {
	private static final String DEFAULT_PATH = "war/conf/resourcemanagement.properties";
	private static final ConfigurationLoader singleton = new ConfigurationLoader(DEFAULT_PATH);
	private static final String LOG_PREFIX = "[CONF_LOADER]";
	private Properties properties = null;
	private ConfigurationLoader(final String confFile) {
		if (this.properties == null) {
			this.properties = this.load(confFile);
		}
	}

	public static void setConfigurationFile(final String filename) {
		singleton.properties = singleton.load(filename);
	}

	/**
	 * Load a properties file from the classpath
	 *
	 * @param propsName
	 * @return Properties
	 * @throws Exception
	 */
	private Properties load(final String propsName) {
		ServerConsole.debug(LOG_PREFIX, "[CONF] loading configuration in file: " + propsName);

		FileInputStream fis = null;
		Properties props = new Properties();
		try {
			fis = new FileInputStream(propsName);
			props.load(fis);
		} catch (Exception e) {
			ServerConsole.debug(LOG_PREFIX, "[CONF] cannot find configuration file: " + propsName + " Trying in standalone mode.");
			try {
				fis = new FileInputStream(DEFAULT_PATH);
			} catch (Exception e1) {
				ServerConsole.debug(LOG_PREFIX, "[CONF] cannot find local standalone configuration file");
			}
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
			}
		}
		return props;
	}

	protected static ConfigurationLoader getInstance() {
		return ConfigurationLoader.singleton;
	}

	public static Properties getProperties() {
		return getInstance().properties;
	}

	public static String getProperty(final String key) throws Exception {
		// Getting the default value
		try {
			Class<?> c = Class.forName(DefaultConfiguration.class.getName());
			Field field = c.getField(key);
			String defaultValue = field.get(null).toString();
			if (getInstance().properties == null) {
				return defaultValue;
			}
			return getInstance().properties.getProperty(
					key.trim(),
					defaultValue);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static int getIntProperty(final String key) throws Exception {
		return Integer.parseInt(getProperty(key));
	}

	public static boolean getBoolProperty(final String key) throws Exception {
		return Boolean.parseBoolean(getProperty(key));
	}

}
