package org.gcube.rest.resourcemanager.is.discovery;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.clientWith;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.ServiceInstance;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.api.ResultParser;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.resourceawareservice.resources.HostNode;
import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.gcube.rest.commons.resourceawareservice.resources.RunInstance;
import org.gcube.rest.commons.resourceawareservice.resources.SerInstance;
import org.gcube.rest.commons.resourceawareservice.resources.SerInstance.NodeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.google.common.collect.Lists;

@SuppressWarnings("javadoc")
public class ISHelper<T> {
	private static final Logger logger = LoggerFactory.getLogger(ISHelper.class);

	private DiscoveryClient<T> client;
	private SimpleQuery query;

	private ISHelper(DiscoveryClient<T> client, SimpleQuery query) {
		this.client = client;
		this.query = query;
	}

	public List<T> submit() {
		return (List<T>) client.submit(query);
	}
	
	public static class ISInformationCollectorBuilder <T> {
		private final Class<?> clientFor;
		private final Class<T> returnType;
		private final String scope;
		private String conditions;
		private String result;

		public ISInformationCollectorBuilder(Class<?> clientFor, Class<T> returnType, String scope) {
			this.clientFor = clientFor;
			this.returnType = returnType;
			this.scope = scope;
		}

		public ISInformationCollectorBuilder<T> addCondition(String condition) {
			condition = "(" + condition + ")";
			if (conditions != null)
				conditions += " and " + condition;
			else
				conditions = condition;

			return this;
		}

		public ISInformationCollectorBuilder<T> setResult(String expression) {
			result = expression;
			return this;
		}

		public ISHelper<T> build() {
			logger.info("Building Information Collector for IS under scope: " + scope + 
					(conditions!=null? " with conditions: " + conditions : "") +
					(result != null? " and result: " + result : ""));
			ScopeProvider.instance.set(scope);
			SimpleQuery query = queryFor(clientFor);
			if (conditions != null)
				query.addCondition(conditions);

			if (result != null)
				query.setResult(result);

			DiscoveryClient<T> client;
			if (returnType.equals(String.class))
				client = clientWith(new ResultParser<T>() {

					@SuppressWarnings("unchecked")
					@Override
					public T parse(String result) throws Exception {
						return (T) result;
					}
					
				});
			else
				client = clientFor(returnType);

			return new ISHelper<T>(client, query);
		}
	}
	
	public static List<Resource> getGenericResourcesByID(String id, String scope) {
		ISHelper<GenericResource> ic = new ISHelper.ISInformationCollectorBuilder<GenericResource>(GenericResource.class, GenericResource.class, scope)
				.addCondition("$resource/ID/text() eq '" + id + "'")
				.build();
		
		List<GenericResource> result = ic.submit();
		
		List<Resource> resources = new ArrayList<Resource>();
		for (GenericResource gr : result) {
			Resource res = new Resource();
			res.setResourceID(gr.id());
			
			if (gr.scopes() != null && gr.scopes().asCollection() != null)
				res.setScopes(Lists.newArrayList(gr.scopes().asCollection()));
			res.setName(gr.profile().name());
			res.setDescription(gr.profile().description());
			res.setBody(gr.profile().body());
			resources.add(res);
		}
				
		return resources;
	}
	
	public static List<Resource> getGenericResourcesByName(String name, String scope) {
		ISHelper<GenericResource> ic = new ISHelper.ISInformationCollectorBuilder<GenericResource>(GenericResource.class, GenericResource.class, scope)
				.addCondition("$resource/Profile/Name/text() eq '" + name + "'")
				.build();
		
		List<GenericResource> result = ic.submit();
		
		List<Resource> resources = new ArrayList<Resource>();
		for (GenericResource gr : result) {
			resources.add(fromGenericResource(gr));
		}
				
		return resources;
	}
	
	public static List<Resource> getGenericResourcesByType(String type, String scope) {
		ISHelper<GenericResource> ic = new ISHelper.ISInformationCollectorBuilder<GenericResource>(GenericResource.class, GenericResource.class, scope)
				.addCondition("$resource/Profile/SecondaryType/text() eq '" + type + "'")
				.build();
		
		List<GenericResource> result = ic.submit();
		
		List<Resource> resources = new ArrayList<Resource>();
		for (GenericResource gr : result) {
			resources.add(fromGenericResource(gr));
		}
		
		return resources;
	}

	public static List<Resource> getGenericResourcesByTypeAndName(String type, String name, String scope) {
		ISHelper<GenericResource> ic = new ISHelper.ISInformationCollectorBuilder<GenericResource>(GenericResource.class, GenericResource.class, scope)
				.addCondition("$resource/Profile/SecondaryType/text() eq '" + type + "'")
				.addCondition("$resource/Profile/Name/text() eq '" + name + "'")
				.build();
		
		List<GenericResource> result = ic.submit();
		
		List<Resource> resources = new ArrayList<Resource>();
		for (GenericResource gr : result) {
			resources.add(fromGenericResource(gr));
		}
		
		return resources;
	}
	
	private static Resource fromGenericResource(GenericResource gr) {
		Resource res = new Resource();
		res.setResourceID(gr.id());
		res.setName(gr.profile().name());
		res.setType(gr.profile().type());
		if (gr.scopes() != null && gr.scopes().size()>0)
			res.setScopes(new ArrayList<>(gr.scopes().asCollection()));
		
		res.setDescription(gr.profile().description());
		res.setBody(gr.profile().body());
		
		return res;
	}

	
	public static List<String> listGenericResourceIDsByType(String type, String scope) {
		ISHelper<String> ic = new ISHelper.ISInformationCollectorBuilder<String>(GenericResource.class, String.class, scope)
				.addCondition("$resource/Profile/SecondaryType/text() eq '" + type + "'")
				.setResult("$resource/ID/text()")
				.build();
		
		List<String> result = ic.submit();
		
		return result;
	}

	public static Set<RunInstance> discoverRunningInstances(String serviceName, String serviceClass, String scope) {
		ISHelper<GCoreEndpoint> ic = new ISHelper.ISInformationCollectorBuilder<GCoreEndpoint>(GCoreEndpoint.class, GCoreEndpoint.class, scope)
				.addCondition("$resource/Profile/ServiceClass/text() eq '" + serviceClass + "'")
				.addCondition("$resource/Profile/ServiceName/text() eq '" + serviceName + "'")
				.build();
		
		List<GCoreEndpoint> result = ic.submit();
		
		Set<RunInstance> endpoints = new HashSet<RunInstance>();
		for (GCoreEndpoint se : result) {
			
			if (se != null && se.profile() != null && se.profile().endpointMap() != null){
				String status = se.profile().deploymentData().status();
				
				if (!status.equalsIgnoreCase("ready")){
					logger.debug("running instance : " + se.id() + " is NOT ready");
					continue;
				}
				
				try {
					endpoints.add(new RunInstance(se.id(),
							new HashSet<String>(se.scopes().asCollection()), 
							XMLConverter.fromXML(XMLConverter.convertToXML(se.profile()), RunInstance.Profile.class)));
				} catch (Exception e) {
					try {
						logger.warn("unable to parse running instance with profile: " + XMLConverter.convertToXML(se.profile()), e);
					} catch (JAXBException e1) {
						logger.warn("unable to parse running instance", e);
					}
				}
			}
		}
		logger.info("endpoints found in discovering: " + endpoints);

		return endpoints;
	}
	
	public static Set<RunInstance> discoverRunningInstancesFilteredByEndopointKey(String serviceName, String serviceClass, String endpointKey, String scope) {
		
		logger.trace("discovering RI for : " + serviceName + " " +  serviceClass + " " +  scope);
		
		Set<RunInstance> set = discoverRunningInstances(serviceName, serviceClass, scope);

		logger.trace("RIs : " + set);
		
		Iterator<RunInstance> setIterator = set.iterator();
		while (setIterator.hasNext()) {
			RunInstance currentElement = setIterator.next();
			if (!currentElement.getProfile().accessPoint.runningInstanceInterfaces.containsKey(endpointKey)) {
				setIterator.remove();
				continue;
			}

			URI uri = currentElement.getProfile().accessPoint.runningInstanceInterfaces.get(endpointKey);
			currentElement.getProfile().accessPoint.runningInstanceInterfaces = new HashMap<String, URI>();
			currentElement.getProfile().accessPoint.runningInstanceInterfaces.put(endpointKey, uri);
	    }
		
		logger.info("endpoints found in discovering: " + set);

		return set;
	}

	public static List<SerInstance> discoverServiceInstances(String serviceName, String serviceClass, String scope) {
		ISHelper<ServiceInstance> ic = new ISHelper.ISInformationCollectorBuilder<ServiceInstance>(ServiceInstance.class, ServiceInstance.class, scope)
				.addCondition("$resource/Data/gcube:ServiceClass/text() eq '" + serviceClass + "'")
				.addCondition("$resource/Data/gcube:ServiceName/text() eq '" + serviceName + "'")
				.build();
		
		List<ServiceInstance> result = ic.submit();
		
		List<SerInstance> serviceInstances = new ArrayList<SerInstance>();
		for (ServiceInstance si : result) {
			serviceInstances.add(new SerInstance(si.endpoint(), si.key(), serviceName, serviceClass, new NodeProperties(si.properties().nodeId(),
					new ArrayList<String>(si.properties().scopes()), si.properties().customProperties())));
		}
		logger.info("service instances found: " + serviceInstances);

		return serviceInstances;
	}
	
	public static List<HostNode> discoverHostingNodes(String scope) {
		ISHelper<HostingNode> ic = new ISHelper.ISInformationCollectorBuilder<HostingNode>(HostingNode.class, HostingNode.class, scope)
				.build();
		
		List<HostingNode> result = ic.submit();

		List<HostNode> hostingNodes = new ArrayList<HostNode>();
		for (HostingNode hn : result) {
			try {
				Node profile = XMLConverter.convertToXMLNode(hn.profile());
				
				HostNode hostnode = new HostNode(hn.id(), new ArrayList<String>(hn.scopes().asCollection()), XMLConverter.fromXML(profile, HostNode.Profile.class));
				
				hostingNodes.add(hostnode);
			} catch (ParserConfigurationException | JAXBException e) {
				logger.error("XML object marshal failed", e);
				return null;
			}
		}
		logger.info("hosting nodes found: " + hostingNodes);

		return hostingNodes;
	}
}
