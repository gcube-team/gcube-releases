/**
 * 
 */
package org.gcube.vremanagement.executor.persistence;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.executor.persistence.couchdb.CouchDBPersistenceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public abstract class SmartExecutorPersistenceFactory {

	private static final Logger logger = LoggerFactory.getLogger(SmartExecutorPersistenceFactory.class);
	
	private static Map<String, SmartExecutorPersistenceConnector> persistenceConnectors;
	
	static {
		persistenceConnectors = new HashMap<String, SmartExecutorPersistenceConnector>();
	}
	
	private static SmartExecutorPersistenceConnector getPersistenceConnector(String scope){
		if(scope==null){
			String error = "No Scope available.";
			logger.error(error);
			throw new RuntimeException(error); 
		}

		logger.trace("Retrieving {} for scope {}", 
				SmartExecutorPersistenceConnector.class.getSimpleName(), scope);
		
		return persistenceConnectors.get(scope);
	}
	
	/**
	 * @return the persistenceConnector
	 */
	public static synchronized SmartExecutorPersistenceConnector getPersistenceConnector() throws Exception {
		String scope = ScopeProvider.instance.get();
		SmartExecutorPersistenceConnector persistence = getPersistenceConnector(scope);
		
		if(persistence==null){
			logger.trace("Retrieving {} for scope {} not found on internal {}. Intializing it.", 
					SmartExecutorPersistenceConnector.class.getSimpleName(), scope, Map.class.getSimpleName());
			
			SmartExecutorPersistenceConfiguration configuration = 
					new SmartExecutorPersistenceConfiguration(CouchDBPersistenceConnector.class.getSimpleName());
			persistence = new CouchDBPersistenceConnector(configuration);
			persistenceConnectors.put(scope, persistence);
		}
		
		return persistence;
	}
	
	public static synchronized void closePersistenceConnector() throws Exception {
		String scope = ScopeProvider.instance.get();
		SmartExecutorPersistenceConnector persistence = getPersistenceConnector(scope);
		if(persistence!=null){
			persistence.close();
			persistenceConnectors.remove(scope);
		}
	}

}
