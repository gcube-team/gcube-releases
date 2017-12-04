package org.gcube.common.vremanagement.ghnmanager.client.plugins;

import org.gcube.common.clients.fw.plugin.Plugin;
import org.gcube.common.vremanagement.ghnmanager.client.Constants;

public abstract class AbstractPlugin<S,P> implements Plugin<S,P> {

		public final String name;
		
		public AbstractPlugin(String name) {
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
		public String namespace() {
			return Constants.NAMESPACE;
		}
		
		@Override
		public String name() {
			return Constants.PORT_TYPE_NAME;
		}
		
}
