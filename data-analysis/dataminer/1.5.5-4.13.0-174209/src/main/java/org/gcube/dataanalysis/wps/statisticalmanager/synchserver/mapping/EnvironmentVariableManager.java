package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

public class EnvironmentVariableManager {
	
	public EnvironmentVariableManager(int maxComputation, boolean saveOnStorage, boolean simulationMode) {
		super();
		this.maxComputation = maxComputation;
		this.saveOnStorage = saveOnStorage;
		this.simulationMode = simulationMode;
	}
	private int maxComputation;
	private boolean saveOnStorage;
	private boolean  simulationMode;
	
	public int getMaxComputation() {
		return maxComputation;
	}
	public boolean isSaveOnStorage() {
		return saveOnStorage;
	}
	public boolean isSimulationMode() {
		return simulationMode;
	}
	
	
}
