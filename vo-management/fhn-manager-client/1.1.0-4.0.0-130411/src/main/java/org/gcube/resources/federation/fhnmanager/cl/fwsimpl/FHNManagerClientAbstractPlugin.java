package org.gcube.resources.federation.fhnmanager.cl.fwsimpl;

import static org.gcube.resources.federation.fhnmanager.api.Constants.*;

import org.gcube.common.clients.fw.plugin.Plugin;


public abstract class FHNManagerClientAbstractPlugin<S, P> implements Plugin<S, P> {

	public final String name;

	public FHNManagerClientAbstractPlugin(String name) {
		this.name = name;
	}

	public String serviceClass() {
		return SERVICE_CLASS;
	}

	public String serviceName() {
		return SERVICE_NAME;
	}

	public String name() {
		return name;
	}
}
