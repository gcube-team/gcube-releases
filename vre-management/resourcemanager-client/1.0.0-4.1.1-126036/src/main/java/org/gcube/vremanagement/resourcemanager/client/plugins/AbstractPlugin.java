package org.gcube.vremanagement.resourcemanager.client.plugins;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.fw.plugin.Plugin;
import org.gcube.vremanagement.resourcemanager.client.Constants;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidOptionsException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.NoSuchReportException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.ResourcesCreationException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.ResourcesRemovalException;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.*;


/**
 * 
 * @author Andrea Manzi(CERN)
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

		if (fault instanceof InvalidScopeFault)
			return new InvalidScopeException(fault);
		if (fault instanceof InvalidOptionsFault)
			return new InvalidOptionsException(fault);
		if (fault instanceof NoSuchReportFault)
			return new NoSuchReportException(fault);
		if (fault instanceof ResourcesCreationFault)
			return new ResourcesCreationException(fault);
		if (fault instanceof ResourcesRemovalFault)
			return new ResourcesRemovalException(fault);
		
		return fault;
	}
	
}

