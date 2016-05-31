package org.gcube.data.tml.plugins;

import static org.gcube.data.tml.Constants.*;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.exceptions.InvalidRequestException;
import org.gcube.common.clients.exceptions.UnsupportedRequestException;
import org.gcube.common.clients.fw.plugin.Plugin;
import org.gcube.data.tml.stubs.Types.InvalidRequestFault;
import org.gcube.data.tml.stubs.Types.UnsupportedOperationFault;
import org.gcube.data.tml.stubs.Types.UnsupportedRequestFault;

/**
 * Base class for proxy {@link Plugin}s
 * 
 * @author Fabio Simeoni
 *
 * @param <S> the type of service stubs
 * @param <P> the type of service proxies
 */
public abstract class AbstractPlugin<S,P> implements Plugin<S,P> {

	public final String name;
	
	
	AbstractPlugin(String name) {
		this.name=name;
	}
	
	@Override
	public String serviceClass() {
		return gcubeClass;
	}
	
	@Override
	public String serviceName() {
		return gcubeName;
	}
	
	@Override
	public String namespace() {
		return namespace;
	}
	
	@Override
	public String name() {
		return name;
	}
	
	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		
		if (fault instanceof InvalidRequestFault)
			return new InvalidRequestException(fault);
		if (fault instanceof UnsupportedOperationFault)
			return new UnsupportedOperationException(fault);
		if (fault instanceof UnsupportedRequestFault)
			return new UnsupportedRequestException(fault);
		
		return fault;
	}
	
}
