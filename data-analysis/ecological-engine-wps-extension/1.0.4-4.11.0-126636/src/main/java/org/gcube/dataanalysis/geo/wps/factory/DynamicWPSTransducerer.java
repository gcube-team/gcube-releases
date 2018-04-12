package org.gcube.dataanalysis.geo.wps.factory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.opengis.wps.x100.ProcessBriefType;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.DynamicTransducer;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.geo.wps.client.WPSClient;
import org.gcube.dataanalysis.geo.wps.interfaces.WPSProcess;

public class DynamicWPSTransducerer implements DynamicTransducer {

	public Map<String, Transducerer> getTransducers(AlgorithmConfiguration config) {
		if (transducerersP != null && !isTooMuchTime())
			return transducerersP;
		Map<String, Transducerer> transducerers = new LinkedHashMap<String, Transducerer>();
		// get the list of endpoints from the IS
		List<String> wpsendpoints = getWPSendpoints(config);
		try {
			for (String wpsendpoint : wpsendpoints) {
				try {
					WPSClient client = new WPSClient(wpsendpoint);
					client.requestGetCapabilities();
					// get the list of available processes for this
					ProcessBriefType[] wpsProcesses = client.getProcessesList();
					for (ProcessBriefType processInfo : wpsProcesses) {
						// prepare a generic wps process according to these
						WPSProcess process = new WPSProcess(wpsendpoint, processInfo.getIdentifier().getStringValue());
						process.setConfiguration(config);
						transducerers.put(processInfo.getTitle().getStringValue(), process);
						// break;
					}
				} catch (Throwable e) {
					AnalysisLogger.getLogger().debug("Error in connecting to: "+wpsendpoint+" .. skipping connection");
					AnalysisLogger.getLogger().debug("Error is "+e.getLocalizedMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("Error in retrieving information by WPS Server: " + e.getLocalizedMessage());
		}

		if (transducerers.size() > 0)
			transducerersP = transducerers;

		return transducerers;
	}

	static Map<String, Transducerer> transducerersP = null;

	static long t0 = System.currentTimeMillis();
	static long maxtime = 2 * 60 * 60 * 1000; // 1h

	public static boolean isTooMuchTime() {
		if (System.currentTimeMillis() - t0 > maxtime) {
			t0 = System.currentTimeMillis();
			return true;
		} else
			return false;
	}

	// gets the list of endpoints from the IS
	public static List<String> getWPSendpoints(AlgorithmConfiguration config) {

		List<String> wps = new ArrayList<String>();
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		// wps.add("http://wps01.i-marine.d4science.org/wps/WebProcessingService");
//		AnalysisLogger.getLogger().debug("WPS: searching for wps servers in the scope: " + config.getGcubeScope());
		wps = org.gcube.dataanalysis.executor.util.InfraRetrieval.retrieveAddresses("WPS", config.getGcubeScope(), "StatisticalManager");
		if (wps != null && wps.size() > 0) {
			AnalysisLogger.getLogger().debug("WPS: found " + wps.size() + " wps instances");
		} else{
//			AnalysisLogger.getLogger().debug("WPS: found NO wps instances");
		}
		return wps;
	}

}
