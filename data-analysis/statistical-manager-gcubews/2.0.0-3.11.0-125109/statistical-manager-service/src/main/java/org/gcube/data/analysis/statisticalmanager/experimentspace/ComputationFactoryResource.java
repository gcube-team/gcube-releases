package org.gcube.data.analysis.statisticalmanager.experimentspace;

import java.util.HashMap;
import java.util.Map.Entry;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.Type;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.data.analysis.statisticalmanager.Configuration;
import org.gcube.data.analysis.statisticalmanager.ServiceContext;
import org.gcube.data.analysis.statisticalmanager.persistence.DataBaseManager;
import org.gcube.data.analysis.statisticalmanager.persistence.HibernateManager;
import org.gcube.data.analysis.statisticalmanager.persistence.ServiceQueueManager;
import org.gcube.data.analysis.statisticalmanager.persistence.algorithms.AlgorithmManager;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.ComputationalAgentClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComputationFactoryResource extends GCUBEWSResource {

	private static Logger logger = LoggerFactory.getLogger(ComputationFactoryResource.class);

	private static final float RESOURCES_RATE_DISTRIBUTIONS = 0.8f;
	private static final float RESOURCES_RATE_EVALUATORS = 0.1f;
	private static final int RESOURCES_CONSTANT_MODELS = 1;

	private static int D4SCIENCE_COMPUTATIONS = 0;
	// private static boolean LOCAL_COMPUTATIONS = true;
	private static int LOCAL_COMPUTATIONS = 0;



	private static HashMap<String, Integer> busyLocalResources=new HashMap<String, Integer>();;


	

	@Override
	protected void initialise(Object... arg0) throws Exception {
		try{
			logger.debug("Activating smp protocol..");
			Handler.activateProtocol();
			logger.debug("SMP Protocol activated.");
		}catch(Throwable t){
			logger.error("Unable to activate protocol",t);
			throw t;
		}

		try{
			String configPath=Configuration.getConfigPath();
			logger.trace("Calling init algorithms, with configuration "+configPath);
			AlgorithmManager.initInstalledAlgorithms(Configuration.getConfigPath(),
					Boolean.parseBoolean(Configuration.getProperty(Configuration.SKIP_FAULTY_ALGORITHMS)));

			// NB Queue only on VO scope

			for (GCUBEScope scope : ServiceContext.getContext().getStartScopes()) 
				try{
					logger.trace("Initializing environment in scope "+ scope.toString());
					ScopeProvider.instance.set(scope.toString());
					logger.debug("Initializing jms manager");
					ServiceQueueManager.initScope();
					logger.debug("Initializing dataSpace connection");
					DataBaseManager.get();
					logger.debug("Initializing service db connection");
					HibernateManager.get();
					if(scope.getType().equals(Type.VO))
						AlgorithmManager.publishMissingAlgorithms();
				}catch(Exception e){
					logger.warn("Unable to initialize under "+scope.toString(),e);
				}
		}catch(Throwable t){
			logger.error("Unable to init",t);
			throw t;
		}
	}





	public int getLocalResourcesNeeded(String algorithm, String category) {

		int resources = 0;
		if (category.equals(ComputationalAgentClass.DISTRIBUTIONS.toString())) {
			resources = (int) Math.ceil(getLocalResourcesFree()	* RESOURCES_RATE_DISTRIBUTIONS);
		} else if (category.equals(ComputationalAgentClass.EVALUATORS.toString())) {
			resources = (int) Math.ceil(getLocalResourcesFree()	* RESOURCES_RATE_EVALUATORS);
		} else {
			resources = RESOURCES_CONSTANT_MODELS;
		}
		return resources;
	}

	private int allLocalResources() {
		return Runtime.getRuntime().availableProcessors();
	}

	private int localResourcesBusy() {

		int result = 0;
		for (Entry<String, Integer> entry : busyLocalResources.entrySet()) {
			result += entry.getValue();
		}
		return result;
	}

	public synchronized int setLocalResourcesAvailable(String agentId,String algorithm, String category) {

		int resourcesNeeded = getLocalResourcesNeeded(algorithm, category);
		int resourcesBusy = localResourcesBusy();
		int resourcesFree = allLocalResources() - resourcesBusy;

		logger.debug("---------> Resources needed :" + resourcesNeeded);
		logger.debug("---------> Resources busy   :" + resourcesBusy);

		if ((resourcesNeeded == 0) || (resourcesFree < resourcesNeeded))
			return 0;

		busyLocalResources.put(agentId, resourcesNeeded);

		return resourcesNeeded;
	}

	public synchronized void cleanLocalResourcesComputational(String genId) {

		logger.debug(" ---------- Resources clean up called ----"+ busyLocalResources);
		busyLocalResources.remove(genId);
		logger.debug(" ---------- Resources busy " + busyLocalResources);
	}

	public synchronized int getLocalResourcesFree() {
		return allLocalResources() - localResourcesBusy();
	}






	public synchronized boolean setD4ScienceComputation() {
		if (D4SCIENCE_COMPUTATIONS < 1) {
			D4SCIENCE_COMPUTATIONS++;
			return true;
		}
		return false;
	}

	public synchronized void cleanD4ScienceComputation() {
		D4SCIENCE_COMPUTATIONS--;
	}

	public synchronized boolean getLocalComputation() {

		if (LOCAL_COMPUTATIONS < 3) {
			LOCAL_COMPUTATIONS++;
			return true;
		}
		return false;
	}

	public synchronized void cleanLocalComputation() {
		LOCAL_COMPUTATIONS--;
	}

}
