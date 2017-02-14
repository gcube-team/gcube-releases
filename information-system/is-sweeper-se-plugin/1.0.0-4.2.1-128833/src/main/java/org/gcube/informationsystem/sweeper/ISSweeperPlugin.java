package org.gcube.informationsystem.sweeper;

import java.util.Calendar;
import java.util.Map;


import org.gcube.common.scope.api.ScopeProvider;
/*
import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
*/
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class ISSweeperPlugin extends Plugin<ISSweeperPluginDeclaration> {

	public static final String EXPIRING_MINUTES_TIMEOUT = "expiringMinutesTimeout";
	/** 
	 * Indicate the default minutes timeout so that Hosting Node is 
	 * considered expired and can be set to {@link Sweeper#UNREACHABLE}
	 */
	public static final int DEFAULT_EXPIRING_MINUTES_TIMEOUT = 30;
	
	public static final String DEAD_DAYS_TIMEOUT = "deadDaysTimeout";
	/** 
	 * Indicate the default days timeout so that Hosting Node is considered 
	 * dead and can be removed from IS
	 */
	public static final int DEFAULT_DEAD_DAYS_TIMEOUT = 15;

	
	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(ISSweeperPlugin.class);
	
	public ISSweeperPlugin(ISSweeperPluginDeclaration pluginDeclaration) {
		super(pluginDeclaration);
		logger.debug("contructor");
	}
	
	/**{@inheritDoc}*/
	@Override
	public void launch(Map<String, Object> inputs) throws Exception {
		// No inputs needed
		/*
		String token = SecurityTokenProvider.instance.get();
		AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
		String scope = authorizationEntry.getContext();
		*/
		String scope = ScopeProvider.instance.get();
		logger.debug("Launching {} execution on scope {}", 
				ISSweeperPluginDeclaration.NAME, scope);
		
		
		int expiringMinutesTimeout = DEFAULT_EXPIRING_MINUTES_TIMEOUT;
		int deadDaysTimeout = DEFAULT_DEAD_DAYS_TIMEOUT;
		
		if(inputs != null) {
			if(inputs.containsKey(EXPIRING_MINUTES_TIMEOUT)){
				try {
					expiringMinutesTimeout = new Integer(inputs.get(EXPIRING_MINUTES_TIMEOUT).toString());
				} catch(Exception e){
					logger.warn("The provided value {} for {} is not an int. Default value {} will be used", 
							inputs.get(EXPIRING_MINUTES_TIMEOUT), EXPIRING_MINUTES_TIMEOUT, DEFAULT_EXPIRING_MINUTES_TIMEOUT, e);
				}
			} else {
				logger.debug("No provided value for {}. Default value {} will be used", 
						EXPIRING_MINUTES_TIMEOUT, DEFAULT_EXPIRING_MINUTES_TIMEOUT);
			}
			
			if(inputs.containsKey(DEAD_DAYS_TIMEOUT)){
				try {
					deadDaysTimeout = new Integer(inputs.get(DEAD_DAYS_TIMEOUT).toString());
				} catch(Exception e){
					logger.warn("The provided value {} for {} is not an int. Default value {} will be used", 
							inputs.get(DEAD_DAYS_TIMEOUT), DEAD_DAYS_TIMEOUT, DEFAULT_DEAD_DAYS_TIMEOUT, e);
				}
			} else {
				logger.debug("No provided value for {}. Default value {} will be used", 
						DEAD_DAYS_TIMEOUT, DEFAULT_DEAD_DAYS_TIMEOUT);
			}
			
		}
		
		
		
		
		
		
		
		Sweeper sweeper = new Sweeper();
		
		try {
			sweeper.sweepDeadGHNs(Calendar.DAY_OF_YEAR, -deadDaysTimeout);
		} catch(Exception e){
			logger.error("Error removing Dead HostingNodes", e);
		}
		logger.trace("---------------------------------\n\n");
		
		try {
			sweeper.sweepExpiredGHNs(Calendar.MINUTE, -expiringMinutesTimeout);
			logger.trace("---------------------------------\n\n");
		} catch(Exception e){
			logger.error("Error sweeping Expired HostingNodes", e);
		}
		logger.trace("---------------------------------\n\n");
		
		Thread.sleep(1000*90); // Waiting 90 sec
		
		try {
			sweeper.sweepOrphanRI();
			logger.trace("---------------------------------\n\n");
		} catch(Exception e){
			logger.error("Error sweeping Orphan RunningInstances", e);
		}
		
		logger.debug("{} execution finished", ISSweeperPluginDeclaration.NAME);
	}

	/**{@inheritDoc}*/
	@Override
	protected void onStop() throws Exception {
		logger.debug("onStop()");
		
		Thread.currentThread().interrupt();
	}

}
