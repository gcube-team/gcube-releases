package org.gcube.search.sru.consumer.service;

import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.gcube.search.sru.consumer.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

public class ServiceContext {

	private static final Logger logger = LoggerFactory
			.getLogger(ServiceContext.class);

	public static final String PROPERTIES_FILE = "deploy.properties";
	public static final String ServiceClass = Constants.SERVICE_CLASS;
	public static final String ServiceName = Constants.SERVICE_NAME;
	
	void initialize() throws Exception {
		Map<String, String> properties = readProperties();

		initializeTCPManager(properties);
	}
	
	static private String getHostName(Map<String, String> map) {
		return map.get("hostname").trim();
	}
	
	static private boolean getUseRandomPort(Map<String, String> map) {
		return Boolean.valueOf(map.get("useRandomPort").trim());
	}
	
	private static List<PortRange> getPortRanges(Map<String, String> map) {
		Object portRangesObj = map.get("portRanges");
		logger.info("port ranges retrieved is " + portRangesObj);
		if (portRangesObj == null)
			return new ArrayList<PortRange>();
		String portRangesStr = portRangesObj.toString();
		String[] rangePairs = portRangesStr.trim().split("-");
		List<PortRange> ranges = new ArrayList<PortRange>();
		for (String pair : rangePairs) {
			String[] pairSplit = pair.trim().split(",");
			if (pairSplit.length != 2)
				return new ArrayList<PortRange>();
			String pairStart = pairSplit[0].trim();
			if (!pairStart.startsWith("{"))
				return new ArrayList<PortRange>();
			pairStart = pairStart.substring(1).trim();
			String pairEnd = pairSplit[1].trim();
			if (!pairEnd.endsWith("}"))
				return new ArrayList<PortRange>();
			pairEnd = pairEnd.substring(0, pairEnd.length() - 1).trim();
			int beginPair = 0;
			int endPair = 0;
			try {
				beginPair = Integer.parseInt(pairStart.trim());
			} catch (Exception ex) {
				return new ArrayList<PortRange>();
			}
			try {
				endPair = Integer.parseInt(pairEnd.trim());
			} catch (Exception ex) {
				return new ArrayList<PortRange>();
			}
			ranges.add(new PortRange(beginPair, endPair));
		}
		return ranges;
	}
	
	private static void initializeTCPManager(Map<String, String> props) {
		logger.info("Initalizing TCPManager");
		List<PortRange> range = getPortRanges(props);
		for (PortRange r : range)
			logger.info("port range : " + r.GetStart() + "-" + r.GetEnd());
		TCPConnectionManager.Init(new TCPConnectionManagerConfig(getHostName(props), range, getUseRandomPort(props)));
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
	}
	
	private static Map<String, String> readProperties() throws FileNotFoundException, IOException {
		Map<String, String> map = new HashMap<String, String>();

		Properties prop = new Properties();
		
		try (InputStream is = Resources.getResource(PROPERTIES_FILE).openStream()) {
			prop.load(is);
		} catch (Exception e) {
			throw new IllegalArgumentException("could not load property file  : " + PROPERTIES_FILE);
		}
		
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			if (value != null)
				map.put(key, value.trim());
		}
		logger.info("properties read : " + map);
		return map;
	}
}
