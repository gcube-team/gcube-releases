package org.gcube.vremanagement.executor.client.plugins;

import org.gcube.common.clients.Plugin;
import org.gcube.vremanagement.executor.api.SmartExecutor;
import org.gcube.vremanagement.executor.client.Constants;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 * @param <S>
 * @param <P>
 */
@Deprecated
public abstract class AbstractPlugin<S,P> implements Plugin<S,P> {

	public final String name;
	
	public AbstractPlugin(String name) {
		this.name=name;
	}
	
	@Override
	public String serviceClass() {
		return Constants.GCUBE_SERVICE_CLASS;
	}
	
	@Override
	public String serviceName() {
		return Constants.GCUBE_SERVICE_NAME;
	}
	
	@Override
	public String namespace() {
		return SmartExecutor.TARGET_NAMESPACE;
	}
	
	@Override
	public String name() {
		return name;
	}
	
}