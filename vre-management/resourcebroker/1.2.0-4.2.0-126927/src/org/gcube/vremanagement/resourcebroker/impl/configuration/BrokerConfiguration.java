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
 * Filename: BrokerConfiguration.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Adds support for accessing configuration properties.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public final class BrokerConfiguration {
	private GCUBELog logger = new GCUBELog(this, Configuration.LOGGING_PREFIX);
	private static BrokerConfiguration singleton = new BrokerConfiguration(Configuration.BROKER_CONF_FILE);
	private Properties properties = null;

	private BrokerConfiguration(final String confFile) {
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
		logger.debug("[CONF] loading configuration");

		Properties props = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(propsName);
			props.load(fis);
		} catch (Exception e) {
			logger.warn("[CONF] cannot find configuration file. Trying in standalone mode.");
			try {
				fis = new FileInputStream(Configuration.BROKER_LOCAL_CONF_FILE);
				props.load(fis);
			} catch (Exception e1) {
				logger.warn("[CONF] cannot find local standalone configuration file");
			}
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return props;
	}

	protected static BrokerConfiguration getInstance() {
		return BrokerConfiguration.singleton;
	}

	public static Properties getProperties() {
		return getInstance().properties;
	}

	public static String getProperty(final String key) throws RuntimeException {
		// Getting the default value
		try {
			//GCUBEStatefulPortTypeContext pctx = StatefulBrokerContext.getContext();
			//String configDir = (String) pctx.getProperty("configDir");
			//getInstance().logger.debug("[CONF] configDir: " + configDir);

			Class<?> c = Class.forName(Configuration.class.getName());
			Field field = c.getField(key);
			String defaultValue = field.get(null).toString();
			return getInstance().properties.getProperty(
					key,
					defaultValue);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static int getIntProperty(final String key) throws RuntimeException {
		return Integer.parseInt(getProperty(key));
	}

	public static boolean getBoolProperty(final String key) throws RuntimeException {
		return Boolean.parseBoolean(getProperty(key));
	}

	public static Object getRawProperty(final String key) throws RuntimeException {
		try {
			Class<?> c = Class.forName(Configuration.class.getName());
			Field field = c.getField(key);
			Object defaultValue = field.get(null);
			if (!getInstance().properties.containsKey(key)) {
				return defaultValue;
			}
			return getInstance().properties.get(key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}