package gr.uoa.di.madgik.commons.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class ConnectionManager extends Thread {
	protected static ConnectionManagerConfig Config = null;
	private static String CONNECTION_MANAGER_CONFIG_VAR = "CONNECTION_MANAGER_CONFIG_VAR";

	protected static void initializeConfigFromFile() throws FileNotFoundException, IOException {
		String propertyFilename = System.getenv(CONNECTION_MANAGER_CONFIG_VAR);

		Properties properties = new Properties();
		properties.load(new FileInputStream(propertyFilename));
		String hostname = properties.getProperty("hostname");
		String startPort = properties.getProperty("startPort");
		String endPort = properties.getProperty("endPort");
		String random = properties.getProperty("random");

		List<PortRange> lpr = new ArrayList<PortRange>();
		if (startPort != null && endPort != null) {
			PortRange pr = new PortRange(Integer.valueOf(startPort), Integer.valueOf(endPort));
			lpr.add(pr);
		}

		if (hostname != null && random != null)
			Config = new ConnectionManagerConfig(hostname, lpr, Boolean.valueOf(random));
		else
			Config = new ConnectionManagerConfig(lpr);

	}
}
