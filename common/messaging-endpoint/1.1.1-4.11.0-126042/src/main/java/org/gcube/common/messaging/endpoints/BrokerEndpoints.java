package org.gcube.common.messaging.endpoints;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Andrea
 *
 */
public class BrokerEndpoints {

	static Logger logger = LoggerFactory.getLogger(BrokerEndpoints.class);

	private static Map<String,ScheduledRetriever> map= new HashMap<String,ScheduledRetriever>();
	

	/**
	 * Instantiate a ScheduledRetriever with the given waitingtime and refreshtime
	 * @param waitingTime
	 * @param refreshTime
	 * @return
	 * @throws Exception
	 * @throws BrokerNotConfiguredInScopeException
	 */
	public static ScheduledRetriever getRetriever(long waitingTime, long refreshTime) throws Exception,BrokerNotConfiguredInScopeException {

		ScheduledRetriever ret = null;

		if (ScopeProvider.instance.get()== null)
			throw new Exception ("Scope not set in current Thread");

		if (map.containsKey(ScopeProvider.instance.get()))
			ret = map.get(ScopeProvider.instance.get());
		else {

			ret = new ScheduledRetriever(ScopeProvider.instance.get(),refreshTime);

			long startTime = System.currentTimeMillis();

			while(ret.getEndpoints().size()==0 && (System.currentTimeMillis() -startTime < waitingTime*1000))
			{
				logger.debug("Waiting to retrieve MessageBroker endpoints from IS");

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (ret.getEndpoints().size()==0) {
				ret.scheduler.shutdownNow();
				throw new BrokerNotConfiguredInScopeException("A MessageBroker RuntimeResource for the scope "+ ScopeProvider.instance.get() + " was not found on the IS after the given interval" );
			}
			map.put(ScopeProvider.instance.get(), ret);


		}
		
		
		return ret;


	}
	
	/**
	 * Get an already instantiated retriever for the scope passed trough ScopeProvider
	 * @return
	 * @throws Exception
	 */
	public static ScheduledRetriever getRetriever() throws Exception {
		
		if (ScopeProvider.instance.get()== null)
			throw new Exception ("Scope not set in current Thread");

		if (map.containsKey(ScopeProvider.instance.get()))
			return map.get(ScopeProvider.instance.get());
		else 
			throw new Exception ("Retriever not instantiated");
		
	}


}
