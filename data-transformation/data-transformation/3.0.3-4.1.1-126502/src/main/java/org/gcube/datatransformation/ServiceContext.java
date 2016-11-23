package org.gcube.datatransformation;

import gr.uoa.di.madgik.commons.channel.proxy.tcp.ChannelTCPConnManagerEntry;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.environment.accounting.AccountingSystem;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.execution.engine.ExecutionEngineConfig;
import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngCallbackTCPConnManagerEntry;
import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngTCPConnManagerEntry;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.rr.ResourceRegistry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.gcube.datatransformation.datatransformationlibrary.datahandlers.IOHandler;
import org.gcube.datatransformation.datatransformationlibrary.security.DTSSManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceContext {
	private static final Logger logger = LoggerFactory.getLogger(ServiceContext.class);

	public static final String PROPERTIES_FILE = "deploy.properties";
	public static final String ServiceClass = "DataTransformation";
	public static final String ServiceName = "DataTransformationService";

	public static String GRS2_PORT = "gRS2Port";
	public static String HOSTNAME = "hostname";

	private String scopes[] = null;

	private boolean isLocal = false;

	public ServiceContext() throws Exception {
		initialize();
	}

	public String[] getScope() {
		return scopes;
	}

	void initialize() throws Exception {
		Map<String, String> map = readProperties();
		this.scopes = map.get("scope").split(",\\s*");

		initgRS2(map);

		DTSSManager.init(scopes);

		IOHandler.init(null);

		logger.info("Initializing Resource Registry");
		ResourceRegistry.startBridging();

		logger.info("Initializing Accoutning System Provider");
		AccountingSystem.init(map.get("providerAccountingName"));

		logger.info("Initializing Execution Engine");
		ExecutionEngineConfig conf = new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans, getHostName(map), Integer.valueOf(map.get("port")));
		ExecutionEngine.Init(conf);
		
		isLocal = isLocal(map);
		logger.info("Run under local mode: " + isLocal);

	}

	private Map<String, String> readProperties() throws FileNotFoundException, IOException {
		Map<String, String> map = new HashMap<String, String>();

		Properties prop = new Properties();
		InputStream is = ServiceContext.class.getResourceAsStream("/" + PROPERTIES_FILE);
		prop.load(is);
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			if (value != null)
				map.put(key, value.trim());
		}
		is.close();
		logger.info("properties read : " + map);
		return map;
	}

	private static void initgRS2(Map<String, String> map) {
		logger.info("Initalizing TCPManager");
		List<PortRange> range = GetPortRanges(map);
		for (PortRange r : range)
			logger.info("port range : " + r.GetStart() + "-" + r.GetEnd());
		TCPConnectionManager.Init(new TCPConnectionManagerConfig(getHostName(map), range, GetUseRandomPort(map)));
		logger.info("Registering entries");
		TCPConnectionManager.RegisterEntry(new ExecEngTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new ExecEngCallbackTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
	}

	private static List<PortRange> GetPortRanges(Map<String, String> map) {
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

	private static String getHostName(Map<String, String> map) {
		return map.get(HOSTNAME);
	}

	private static Boolean GetUseRandomPort(Map<String, String> map) {
		Object portRandomObj = map.get("useRandomPort");
		if (portRandomObj == null)
			return false;
		try {
			return Boolean.parseBoolean(portRandomObj.toString().trim());
		} catch (Exception ex) {
			return false;
		}
	}

	private boolean isLocal(Map<String, String> map) {
		try {
			return Boolean.valueOf(map.get("isLocal"));
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isLocal() {
		return isLocal;
	}
}