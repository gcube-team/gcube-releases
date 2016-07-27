package org.gcube.informationsystem.publisher.utils;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.URI;
import java.util.List;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.cache.RegistryCache;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.informationsystem.publisher.stubs.registry.RegistryConstants;
import org.gcube.informationsystem.publisher.stubs.registry.RegistryStub;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.api.ResultParser;
import org.gcube.resources.discovery.client.impl.DelegateClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryStubs {
	
	private RegistryCache cache = new RegistryCache(10);
	private List<URI> endpoints;
	private static final Logger log = LoggerFactory.getLogger(RegistryStubs.class);
	
	
	
	public List<URI> getEndPoints(){
		String scope=ScopeProvider.instance.get();
// able/disable cache
		endpoints=(List<URI>)cache.get(scope);
		if(endpoints==null){
			SimpleQuery query = queryFor(GCoreEndpoint.class);
			ResultParser<URI> uriParser =new ResultParser<URI>() {

				@Override
				public URI parse(String result) throws Exception {
					return new URI(result.replaceAll("\n", ""));
				}
				
			};
			DiscoveryClient<URI> client = new DelegateClient<URI>(uriParser, new ICClient());
			query.addCondition("$resource/Profile/ServiceClass/text() eq '"+RegistryConstants.service_class+"'")
				.addCondition("$resource/Profile/ServiceName/text() eq '"+RegistryConstants.service_name+"'")
				.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint[string(@EntryName) eq '"+RegistryConstants.service_entrypoint+"']/string()");
			endpoints = client.submit(query);
			if (endpoints.size()==0){
				throw new IllegalArgumentException("No registry endpoint founded");
			}
// able/disable cache			
			cache.put(scope, endpoints);
		}
		return endpoints;
	}
	
	public RegistryStub getStubs() throws RegistryNotFoundException{
		URI endpoint=null;
		//use another method to cache epr
		endpoint = getEndPoints().get(0);
		log.debug("get stubs from endpoint: "+ endpoint);
		return stubFor(RegistryConstants.registry).at(endpoint);
	}
	
	public RegistryStub getStubs(URI endpoint) throws RegistryNotFoundException{
		log.debug("get stubs from endpoint: "+ endpoint);
		return stubFor(RegistryConstants.registry).at(endpoint);
	}

}
