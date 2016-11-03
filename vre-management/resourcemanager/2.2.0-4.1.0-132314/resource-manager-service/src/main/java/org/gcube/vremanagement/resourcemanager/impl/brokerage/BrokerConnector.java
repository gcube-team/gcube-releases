package org.gcube.vremanagement.resourcemanager.impl.brokerage;

import java.lang.reflect.Constructor;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;

/**
 * Locates and initializes the Broker to use for services' deployment. A Broker is a component capable to create a plan
 * for allocating gCube packages to gHNs 
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class BrokerConnector {
	
	private static final String DEFAULT_BROKER = "org.gcube.vremanagement.resourcemanager.impl.brokerage.InternalBroker";
	
	private  static final GCUBELog logger = new GCUBELog(BrokerConnector.class);

	/**
	 * Returns the broker to use in the given scope
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public static Broker getBroker(ScopeState scopeState) throws Exception {
		Broker broker = null;
		try  {
			broker = load((String) ServiceContext.getContext().getProperty("resourceBrokerClass", true));
		} catch (Exception e) {
			logger.error("Unable to load the configured broker", e);
			try {
				logger.info("Trying to load the internal broker");
				broker = load(DEFAULT_BROKER);
			} catch (Exception e1) {
				logger.error("Unable to load the internal broker", e1);
				throw new Exception("unable to find a broker to use");
			}			
		}
		if (broker != null) broker.initialize(scopeState);
		return broker;
	}
	
	/**
	 * Loads a broker instance 
	 * @param className the full name of the Class implementing the broker to load
	 * @return the broker instance
	 * @throws Exception
	 */
	private static Broker load(String className) throws Exception {
		if (className==null) return null;	    
		Class<?> clazz = (Class<?>) Class.forName(className);			
		if (!Broker.class.isAssignableFrom(clazz)) 
			throw new Exception(className+" does not implement "+Broker.class.getName());
		Constructor<?> constructor = clazz.getConstructor(new Class[] {});
		return (Broker) constructor.newInstance(new Object[]{}); 
	}
}