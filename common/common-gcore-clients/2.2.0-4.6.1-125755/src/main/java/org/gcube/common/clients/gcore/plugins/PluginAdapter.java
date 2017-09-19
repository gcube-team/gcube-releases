package org.gcube.common.clients.gcore.plugins;

import java.rmi.Remote;

import org.apache.axis.client.Stub;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@link Plugin} that adapts an existing {@link Plugin}.
 * 
 * @author Fabio Simeoni
 * 
 * @param <S> the type of service stubs
 * @param <P> the type of service proxies
 *
 */
public final class PluginAdapter<S,P> implements Plugin<S,P> {
	
	private static Logger log = LoggerFactory.getLogger(PluginAdapter.class);
	
	private final Plugin<S,P> plugin;
	
	public PluginAdapter(Plugin<S,P> plugin) {
		this.plugin=plugin;
	}
	
	/**
	 * Returns the name of the service porttype.
	 * @return the name
	 */
	public String name() {
		return plugin.name();
	}
	
	/**
	 * Returns the gCube name of the service.
	 * @return the name
	 */
	public String serviceClass() {
		return plugin.serviceClass();
	}
	
	/**
	 * Returns the gCube class of the service.
	 * @return the class
	 */
	public String serviceName() {
		return plugin.serviceName();
	}
	
	/**
	 * Returns the namespace of the service
	 * @return the namespace
	 */
	public String namespace() {
		return plugin.namespace();
	}
	
	@Override
	public S resolve(EndpointReferenceType address, ProxyConfig<?,?> config) throws Exception {
		
		S stub = plugin.resolve(address,config);
		
		Remote remote = null;
		//add scope
		if (stub instanceof Remote) {
			try {
				remote = GCUBERemotePortTypeContext.getProxy((Remote) stub);
				
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		
			//set timeout
			if (remote instanceof Stub)
				((Stub) remote).setTimeout((int)config.timeout());
			else
				log.warn("could not set timeout on stub of {} as {} does not implement the org.apache.axis.client.Stub interface",name(),stub);
		}
		else
			log.error("could not set scope and timeout on stub of {} as {} does not implement the java.rmi.Remote interface",name(),stub);
		
		return stub;
	}
	
	@Override
	public Exception convert(Exception fault,ProxyConfig<?,?> config) {
		
		// TODO gcore-specific fault analysis
		
		return plugin.convert(fault,config);
	}
	
	@Override
	public P newProxy(ProxyDelegate<S> config) {
		return plugin.newProxy(config);
	}
}
