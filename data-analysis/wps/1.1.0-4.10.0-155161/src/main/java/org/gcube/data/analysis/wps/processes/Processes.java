package org.gcube.data.analysis.wps.processes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.utils.Cancellable;

public class Processes {

	private static Map<String, Cancellable> runningProcesses = Collections.synchronizedMap(new HashMap<String, Cancellable>());

	public static Map<String, Cancellable> getRunningProcesses() {
		return runningProcesses;
	}

}
