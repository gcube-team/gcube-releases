package org.gcube.vremanagement.softwaregateway.client.plugins;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.fw.plugin.Plugin;
import org.gcube.vremanagement.softwaregateway.client.Constants;
import org.gcube.vremanagement.softwaregateway.client.exceptions.ServiceNotAvaiableException;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.*;


/**
 * 
 * @author Roberto Cirillo (ISTI -CNR)
 *
 * @param <S>
 * @param <P>
 */
public abstract class AbstractPlugin<S,P> implements Plugin<S,P> {

	public final String name;
	
	
	AbstractPlugin(String name) {
		this.name=name;
	}
	
	@Override
	public String serviceClass() {
		return Constants.SERVICE_CLASS;
	}
	
	@Override
	public String serviceName() {
		return Constants.SERVICE_NAME;
	}
	
	
	@Override
	public String name() {
		return name;
	}
	
	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		if (fault instanceof ServiceNotAvaiableFault)
			return new ServiceNotAvaiableException(fault);
		return fault;
	}
	
}

