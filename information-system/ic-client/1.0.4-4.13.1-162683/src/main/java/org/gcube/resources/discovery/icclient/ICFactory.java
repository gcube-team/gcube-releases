package org.gcube.resources.discovery.icclient;

import static java.lang.String.*;
import static org.gcube.resources.discovery.client.queries.impl.XQuery.*;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceInstance;
import org.gcube.common.resources.gcore.Software;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.api.ResultParser;
import org.gcube.resources.discovery.client.impl.DelegateClient;
import org.gcube.resources.discovery.client.impl.JAXBParser;
import org.gcube.resources.discovery.client.queries.impl.XQuery;

/**
 * Factory of {@link XQuery}s and {@link DiscoveryClient}s for the Information Collector service.
 * 
 * @author Fabio Simeoni
 * 
 */
public class ICFactory {

	// known query parameters, indexed by resource type
	private static Map<Class<?>, Map<String, String>> registry = new HashMap<Class<?>, Map<String, String>>();

	public static final String nsDeclaration = "declare namespace ic = 'http://gcube-system.org/namespaces/informationsystem/registry';";
	
	public static final String instancesNSDeclaration = nsDeclaration+
											"declare namespace gcube = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider';";

	private final static String profile_range = "collection('/db/Profiles/%1$s')//Document/Data/ic:Profile/Resource";

	// registers parameters for known queries
	static {

		register(GenericResource.class,
				params().add(ns, nsDeclaration).add(range, format(profile_range, "GenericResource")).build());
		register(ServiceEndpoint.class,
				params().add(ns, nsDeclaration).add(range, format(profile_range, "RuntimeResource")).build());
		register(ServiceInstance.class,
				params().add(ns, instancesNSDeclaration).add(range, "collection('/db/Properties')//Document").build());
		register(GCoreEndpoint.class,
				params().add(ns, nsDeclaration).add(range, format(profile_range, "RunningInstance")).build());
		register(Software.class,
				params().add(ns, nsDeclaration).add(range, format(profile_range, "Service")).build());
		register(HostingNode.class,
				params().add(ns, nsDeclaration).add(range, format(profile_range, "GHN")).build());

	}

	// helper
	private static void register(Class<?> type, Map<String, String> parameters) throws IllegalStateException {
		if (registry.containsKey(type))
			throw new IllegalStateException("a query is already registered for type" + type);
		else
			registry.put(type, parameters);
	}

	/**
	 * Returns a {@link XQuery} for a given resource type.
	 * 
	 * @param type the resource type
	 * @return the query
	 * @throws IllegalStateException if a query for the resource type has not been previously registered
	 */
	public static XQuery queryFor(Class<?> type) throws IllegalStateException {

		if (registry.containsKey(type))
			return new XQuery(registry.get(type));
		else
			throw new IllegalStateException("no query registered for " + type);
	}

	/**
	 * Returns a {@link DiscoveryClient} that submits queries to the Information Collector service and parses query results with a
	 * given resource type.
	 * 
	 * @param type the resource type
	 * @return the client
	 */
	public static <R> DiscoveryClient<R> clientFor(Class<R> type) {
		return new DelegateClient<R>(new JAXBParser<R>(type), new ICClient());
	}
	
	/**
	 * Returns a {@link DiscoveryClient} that submits queries to the Information Collector service without
	 * parsing the results.
	 * 
	 * @return the client
	 */
	public static DiscoveryClient<String> client() {
		return new ICClient();
	}

	/**
	 * Returns a {@link DiscoveryClient} that submits queries to the Information Collector service and parses query results with a
	 * given {@link ResultParser}.
	 * 
	 * @param parser the resource parser
	 * @return the client
	 */
	public static <R> DiscoveryClient<R> clientWith(ResultParser<R> parser) {
		return new DelegateClient<R>(parser, new ICClient());
	}

	// utils

	public static ParameterBuilder params() {
		return new ParameterBuilder();
	}

	public static class ParameterBuilder {

		private Map<String, String> params = new HashMap<String, String>();

		public ParameterBuilder add(String name, String value) {
			params.put(name, value);
			return this;
		}

		public Map<String, String> build() {
			return params;
		}
	}
}
