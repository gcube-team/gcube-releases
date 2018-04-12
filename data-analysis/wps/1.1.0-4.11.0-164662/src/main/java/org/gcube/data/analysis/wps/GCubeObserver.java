package org.gcube.data.analysis.wps;

import org.gcube.data.analysis.wps.processes.Processes;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.AbstractEcologicalEngineMapper;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.utils.Observable;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.utils.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCubeObserver implements Observer{

	private static final Logger LOGGER = LoggerFactory.getLogger(GCubeObserver.class);
	
	public static GCubeObserver getObserver(){
		return instance;
	}
	
	private static GCubeObserver instance = new GCubeObserver();
		
	private GCubeObserver(){}
	
	@Override
	public void isFinished(Observable o) {
		AbstractEcologicalEngineMapper algorithm = (AbstractEcologicalEngineMapper)o;
		LOGGER.debug("computation with id {} finished",algorithm.wpsExternalID);
		Processes.getRunningProcesses().remove(algorithm.wpsExternalID);		
	}

	@Override
	public void isStarted(Observable o) {
		AbstractEcologicalEngineMapper algorithm = (AbstractEcologicalEngineMapper)o;
		LOGGER.debug("computation with id {} started",algorithm.wpsExternalID);
		Processes.getRunningProcesses().put(algorithm.wpsExternalID,algorithm);	
	}

	
	
}
