package org.gcube.vremanagement.resourcemanager.impl.resources.software;

import java.io.BufferedReader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.Type;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;
import org.gcube.vremanagement.softwaregateway.stubs.AccessPortType;
import org.gcube.vremanagement.softwaregateway.stubs.DependenciesCoordinates;
import org.gcube.vremanagement.softwaregateway.stubs.service.AccessServiceAddressingLocator;
import org.kxml2.io.KXmlParser;

/**
 * Request for the Software Repository service to solve the service2package
 * relationships
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public final class SoftwareGatewayRequest {

	static Map<GCUBEScope, Set<EndpointReferenceType>> scopeMap = new HashMap<GCUBEScope, Set<EndpointReferenceType>>();
	
	private Map<ScopedDeployedSoftware, DependenciesCoordinates> requests = new HashMap<ScopedDeployedSoftware, DependenciesCoordinates>();

	protected static final String RESOLVED_DEPS_KEY = "listResolvedDeps";
	
	protected static final String MISSING_DEPS_KEY = "listMissingDeps";
	/** Target port-type name. */
	protected static final String REPOSITORY_ENDPOINT = "gcube/vremanagement/softwaregateway/Access";
	/** Target service name. */
	protected static final String REPOSITORY_NAME = "SoftwareGateway";
	/** Target service class. */
	protected static final String REPOSITORY_CLASS = "VREManagement";
	/** Timeout for contacting the target service. */
	protected static final int TIMEOUT = 600000; //in milliseconds
	/** Object logger */
	protected final GCUBELog logger = new GCUBELog(this);
	
	

	/**
	 * Adds a new service to the request
	 * 
	 * @param service
	 */
	public void addSoftware(ScopedDeployedSoftware service)  {
		if ((service == null) || (service.getSourcePackage() == null)) 
			throw new IllegalArgumentException("Software cannot be null");
		DependenciesCoordinates message = new DependenciesCoordinates();
		message.setServiceClass(service.getSourcePackage().getClazz());
		message.setServiceName(service.getSourcePackage().getName());
		message.setServiceVersion(service.getSourcePackage().getVersion());
		if (service.getSourcePackage().getPackageName() != null 
				&& service.getSourcePackage().getPackageName().length()>0)
			message.setPackageName(service.getSourcePackage().getPackageName());
		if (service.getSourcePackage().getPackageVersion() != null 
				&& service.getSourcePackage().getPackageVersion().length()>0)
			message.setPackageVersion(service.getSourcePackage().getPackageVersion());
		requests.put(service, message);
	}

	/**
	 * Sends the request to the appropriate SG
	 * 
	 * @throws Exception
	 */
	public List<ScopedDeployedSoftware> send(GCUBEScope scope) throws Exception {
		List<ScopedDeployedSoftware> service2package = new ArrayList<ScopedDeployedSoftware>();		
		Map<GCUBEScope, Set<EndpointReferenceType>> eprs = this.findInstances();
		boolean found = false;
		for (ScopedDeployedSoftware service : requests.keySet()) {
			DependenciesCoordinates packageCoordinates = requests.get(service);
			logger.trace("Checking deps for " + packageCoordinates.getServiceName());
			//look for service deps in one of the SG instances found
			for (EndpointReferenceType epr : eprs.get(scope)) {			
					try {
						logger.trace("Trying Service Gateway located at "+ epr);
						AccessServiceAddressingLocator locator = new AccessServiceAddressingLocator();
						AccessPortType pt = locator.getAccessPortTypePort(epr);											
						String report = GCUBERemotePortTypeContext.getProxy(pt, (scope.getType() == Type.VRE?scope.getEnclosingScope():scope), TIMEOUT, ServiceContext.getContext()).getDependencies(packageCoordinates);
						service2package.add(parseDependenciesReport(service, report));
						found = true;
						break;//no need for looping on the other instances, we just need that one satisfies the request
					} catch (Exception e) {
						logger.warn("Failed to check deps for "	+ packageCoordinates.getServiceName() + " at Service Gateway located at  " + epr.getAddress().toString(), e);						
					}
					
			}
			if (!found) {	
				// if we are here, no SG instance was able to resolve the dependencies
				//ResolvedService s = new ResolvedService(service);
				logger.error("Unable to check deps for "	+ packageCoordinates.getServiceName() + " from Service Gateway in " + scope );						
				service.setErrorMessage("Unable to check deps for this service in any of the Service Gateway instances in scope");
				//service.setStatus();
				service2package.add(service);	
				throw new Exception("Unable to check deps for this service in any of the Service Gateway instances in scope");
			}
		}
		return service2package;
	}

	/**
	 * Parses the dependencies report 
	 * @param report
	 * @return
	 */
	private ScopedDeployedSoftware parseDependenciesReport(ScopedDeployedSoftware service, String report) throws Exception {
		//ResolvedService depsMap = new ResolvedService(service);
		KXmlParser parser = new KXmlParser();
		parser.setInput(new BufferedReader(new StringReader(report)));
		logger.trace("Parsing: " + report);
		try {
			loop: while (true) {
				switch (parser.next()) {
				case KXmlParser.START_TAG:
					if (parser.getName().equals("ResolvedDependencies")) this.parseResolvedDependencies(parser, service);
					if (parser.getName().equals("MissingDependencies"))	this.parseMissingDependencies(parser, service);
					break;
				case KXmlParser.END_DOCUMENT:
					break loop;
				}
			}
		} catch (Exception e) {
			logger.error("Unable to parse the deployment report returned by the Software Repository", e);
		}
		return service;
	}

	/**
	 * Parses the service section of a dependency
	 * 
	 * @param parser the XML parser
	 * @param s the service to populate
	 * @throws Exception
	 */
	private void parseService(KXmlParser parser, GCUBEPackage s) throws Exception {
		inloop: while (true) {
			switch (parser.next()) {
			case KXmlParser.START_TAG:
				if (parser.getName().equals("Class"))
					s.setClazz(parser.nextText());
				if (parser.getName().equals("Name"))
					s.setName(parser.nextText());
				if (parser.getName().equals("Version"))
					s.setVersion(parser.nextText());
				break;
			case KXmlParser.END_TAG:
				if (parser.getName().equals("Service"))
					break inloop;
				break;
			case KXmlParser.END_DOCUMENT:
				throw new Exception("Parsing failed at Service");
			}
		}
	}

	/**
	 * Parses a generic dependency section (both missing and resolved)
	 * 
	 * @param parser the XML parser
	 * @return the dependency
	 * @throws Exception
	 */
	private Dependency parseDependency(KXmlParser parser) throws Exception {	
		Dependency p = new Dependency();		
		loop: while (true) {
			switch (parser.next()) {
				case KXmlParser.START_TAG:
					if (parser.getName().equals("Service"))	parseService(parser, p.getService());
					if (parser.getName().equals("Package"))	p.setName(parser.nextText());
					if (parser.getName().equals("Version"))	p.setVersion(parser.nextText());
					break;
				case KXmlParser.END_TAG:
					if ((parser.getName().equals("Dependency")) || (parser.getName().equals("MissingDependency"))) break loop;
					break;
				case KXmlParser.END_DOCUMENT:
					throw new Exception("Parsing failed at Dependency");
			}
		}	
		return p;
	}

	private void parseResolvedDependencies(KXmlParser parser, ScopedDeployedSoftware service) throws Exception {
		logger.trace("Parsing resolved dependencies...");
		List<Dependency> resolved = new ArrayList<Dependency>();
		loop: while (true) {
				switch (parser.next()) {
					case KXmlParser.START_TAG:
						if (parser.getName().equals("Dependency")) {
							logger.trace("Dependency found");
							resolved.add(this.parseDependency(parser)); 
						} break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("ResolvedDependencies")) break loop;
						break;
					case KXmlParser.END_DOCUMENT:
						throw new Exception("Parsing failed at ResolvedDependencies");
				}
		}
		logger.trace("Number of deps found: " + resolved.size());
		service.setResolvedDependencies(resolved);
	}

	private void parseMissingDependencies(KXmlParser parser, ScopedDeployedSoftware service) throws Exception {
		logger.trace("Parsing missing dependencies...");
		List<Dependency> missing = new ArrayList<Dependency>();
		loop: while (true) {
			switch (parser.next()) {
				case KXmlParser.START_TAG:
					if (parser.getName().equals("MissingDependency")) {
						logger.trace("Dependency found");
						missing.add(this.parseDependency(parser)); 
					} break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("MissingDependencies")) break loop;
					break;
				case KXmlParser.END_DOCUMENT:
					throw new Exception("Parsing failed at MissingDependencies");
			}
		}
		logger.trace("Number of deps found: " + missing.size());
		service.setMissingDependencies(missing);
	}

	/**
	 * Finds instances of the Software Repository service in the current scope(s)
	 * @return the list of scopes and the EPR of the available SR
	 * @throws Exception
	 */
	protected Map<GCUBEScope, Set<EndpointReferenceType>> findInstances()throws Exception {
		for (GCUBEScope scope : ServiceContext.getContext().getInstance().getScopes().values()) {
			logger.debug("Looking for SG instances in " + scope.toString());
			/*
			 * if (scope.isInfrastructure()) //TODO: deploy only in a PublishedScopeResource
			 * continue;
			 */
			if ((!scopeMap.containsKey(scope)) || (scopeMap.get(scope).size() == 0)) {
				ISClient client = GHNContext.getImplementation(ISClient.class);
				GCUBERIQuery lookupQuery = client.getQuery(GCUBERIQuery.class);
				lookupQuery.addAtomicConditions(new AtomicCondition("//ServiceName",REPOSITORY_NAME));
				lookupQuery.addAtomicConditions(new AtomicCondition("//ServiceClass",REPOSITORY_CLASS));
				logger.debug("Quering in scope " + scope);
				List<GCUBERunningInstance> list = client.execute(lookupQuery, scope);
				logger.debug("Found N." + list.size() + " instances");
				for (GCUBERunningInstance instance : list) {
					logger.trace("Found instance " + instance.toString());
					EndpointReferenceType epr = instance.getAccessPoint().getEndpoint(REPOSITORY_ENDPOINT);
					logger.trace("Found EPR " + epr.toString());
					if (!scopeMap.keySet().contains(scope)) {
						Set<EndpointReferenceType> l = new HashSet<EndpointReferenceType>();					
						scopeMap.put(scope, l);
					} 
					scopeMap.get(scope).add(epr);				
				}
			}
		}		
		return scopeMap;
	}

}
