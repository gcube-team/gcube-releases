package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

import java.util.List;

public class EnvironmentVariableManager {
	
	public EnvironmentVariableManager(int maxComputation, boolean saveOnStorage, boolean simulationMode, List<String> shubUsersExcluded) {
		super();
		this.maxComputation = maxComputation;
		this.saveOnStorage = saveOnStorage;
		this.simulationMode = simulationMode;
		this.shubUsersExcluded = shubUsersExcluded;
	}
	private int maxComputation;
	private boolean saveOnStorage;
	private boolean  simulationMode;
	
	//null: all users will write on SHub
	//empty: no one will write on Shub
	//filled: users reported will not write on Shub
	private List<String> shubUsersExcluded;
	
	public int getMaxComputation() {
		return maxComputation;
	}
	public boolean isSaveOnStorage() {
		return saveOnStorage;
	}
	public boolean isSimulationMode() {
		return simulationMode;
	}
	
	public List<String> getShubUsersExcluded() {
		return shubUsersExcluded;
	}
	
	
}
