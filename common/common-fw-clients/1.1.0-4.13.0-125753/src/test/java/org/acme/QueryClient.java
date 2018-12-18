package org.acme;

import static org.junit.Assert.*;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.clients.fw.plugin.Plugin;
import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.common.clients.fw.queries.StatelessQuery;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.BeforeClass;
import org.junit.Test;

public class QueryClient {

	@BeforeClass
	public static void setup() {
		
		ScopeProvider.instance.set("/gcube/devsec");
	}
	
	static Plugin<Object,Object> plugin = new Plugin<Object, Object>() {

		@Override
		public String name() {
			return "gcube/data/tm/binder";
		}

		@Override
		public String namespace() {
			return "http://gcube-system.org/namespaces/data/tm";
		}

		@Override
		public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
			return fault;
		}

		@Override
		public Object resolve(EndpointReference address, ProxyConfig<?, ?> config) throws Exception {
			return null;
		}

		@Override
		public Object newProxy(ProxyDelegate<Object> delegate) {
			return null;
		}

		@Override
		public String serviceClass() {
			return "DataAccess";
		}

		@Override
		public String serviceName() {
			return "tree-manager-service";
		}
		
	};
	
	@Test
	public void stateless() {
		
		StatelessQuery query = new StatelessQuery(plugin);
		System.out.println(query);
		assertFalse(query.fire().isEmpty());
	}
	
	@Test
	public void stateful() {
		
		StatefulQuery query = new StatefulQuery(plugin);
		System.out.println(query);
		assertFalse(query.fire().isEmpty());
	}
	
	@Test(expected=DiscoveryException.class)
	public void statelessError() {
		
		assertFalse(new StatelessQuery(plugin).addCondition("malformed").fire().isEmpty());
	}
}
