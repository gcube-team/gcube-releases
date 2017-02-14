package org.gcube.informationsystem.resourceregistry.publisher.plugin;


import org.gcube.common.clients.fw.plugin.Plugin;
import org.gcube.informationsystem.resourceregistry.Constants;

/**
 * 
 * @author Luca Frosini (ISTI - CNR)
 *
 * @param <S>
 * @param <P>
 */
public abstract class AbstractPlugin<S, P> implements Plugin<S, P> {

	public final String name;

	public AbstractPlugin(String name) {
		this.name = name;
	}

	public String serviceClass() {
		return Constants.SERVICE_CLASS;
	}

	public String serviceName() {
		return Constants.SERVICE_NAME;
	}

	public String name() {
		return name;
	}
	
}
