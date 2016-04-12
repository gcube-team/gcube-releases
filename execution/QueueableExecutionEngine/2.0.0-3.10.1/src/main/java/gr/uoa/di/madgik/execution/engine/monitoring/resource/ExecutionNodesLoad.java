package gr.uoa.di.madgik.execution.engine.monitoring.resource;

import java.util.concurrent.ConcurrentHashMap;

public class ExecutionNodesLoad {
	private final Object updateAvailable = new String("Notify when update available");
	private ConcurrentHashMap<String, Float> hostingNodesLoad;

	public ExecutionNodesLoad() {
		hostingNodesLoad = new ConcurrentHashMap<String, Float>();
	}
	public Float get(String hostName) {
		return hostingNodesLoad.get(hostName);
	}

	public Float put(String hostName, Float load) {
		synchronized (updateAvailable) {
			updateAvailable.notify();
		}
		return hostingNodesLoad.put(hostName, load);
	}
	
	/**
	 * @return the updateAvailable
	 */
	public Object getUpdateAvailable() {
		return updateAvailable;
	}
}
