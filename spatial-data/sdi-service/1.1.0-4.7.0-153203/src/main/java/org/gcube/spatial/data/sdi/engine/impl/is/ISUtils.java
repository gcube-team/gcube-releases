package org.gcube.spatial.data.sdi.engine.impl.is;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.vremanagement.resourcemanager.client.RMBinderLibrary;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.ResourcesCreationException;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.AddResourcesParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.ResourceItem;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.ResourceList;
import org.gcube.vremanagement.resourcemanager.client.proxies.Proxies;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ISUtils {

	static List<ServiceEndpoint> queryForServiceEndpoints(String category, String platformName){
		log.debug("Querying for Service Endpoints [category : {} , platformName : {}, currentScope : {} ]",category,platformName,ScopeUtils.getCurrentScope());
				
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq '"+category+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+platformName+"'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		return client.submit(query);
	}
	
	static List<GCoreEndpoint> queryForGCoreEndpoint(String serviceClass,String serviceName){
		log.debug("Querying for GCore Endpoints [ServiceClass : {} , ServiceName : {}, currentScope : {} ]",serviceClass,serviceName,ScopeUtils.getCurrentScope());
		
		
		SimpleQuery query =queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq '"+serviceClass+"'")
		.addCondition("$resource/Profile/ServiceName/text() eq '"+serviceName+"'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		return client.submit(query);
	}
	
	
	static <T> T getByHostnameInCollection(String hostname, Collection<T> toCheckList) {		
		for(T gc:toCheckList)
			if(gc instanceof GCoreEndpoint) {
				if(((GCoreEndpoint)gc).profile().endpoints().iterator().next().uri().getHost().equals(hostname)) return gc;}
			else if(((ServiceEndpoint)gc).profile().runtime().hostedOn().equals(hostname)) return gc;
		
		return null;
	}
	
	
	static List<ServiceEndpoint> querySEByHostname(String category,String platformName,String hostname){
		log.debug("Querying Service Endpoints by hostname [category : {} , platformName : {}, currentScope : {}, hostname {} ]",category,platformName,ScopeUtils.getCurrentScope(),hostname);
		
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq '"+category+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+platformName+"'")
		.addCondition("$resource/Profile/Runtime/HostedOn/text() eq '"+hostname+"'");
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		return client.submit(query);
	}
	
			
	static List<String> queryById(String id) {
		DiscoveryClient<String> client = client();
		String queryString ="declare namespace ic = 'http://gcube-system.org/namespaces/informationsystem/registry'; "+
				"for $profiles in collection('/db/Profiles')//Document/Data/ic:Profile/Resource "+
				"where $profiles/ID/text() eq '"+id+"'"+				
				" return $profiles";
		return client.submit(new QueryBox(queryString));
		
	}
	
	
	static String registerService(ServiceEndpoint toRegister) {
		 RegistryPublisher rp=RegistryPublisherFactory.create();
		 Resource r=rp.create(toRegister);
		 return r.id();
	}
	
	
	static String addToScope(ServiceEndpoint se,GCoreEndpoint gc, String targetScope) throws ResourcesCreationException, InvalidScopeException {
		log.trace("Publishing GC [ID : {}, Sc : {}, Sn {}, GHN-ID : {} ], SE [ID : {}, name : {}] to Scope {} from Scope {}",
				gc.id(), gc.profile().serviceClass(),gc.profile().serviceName(),gc.profile().ghnId(),
				se.id(),se.profile().name(),targetScope,ScopeUtils.getCurrentScope());
		
		
		AddResourcesParameters params=new AddResourcesParameters();
		ResourceList resourceList=new ResourceList();
		ArrayList<ResourceItem> list=new ArrayList<>();
		
		ResourceItem ghnItem=new ResourceItem();
		ghnItem.id=gc.profile().ghnId();
		ghnItem.type="GHN";
		list.add(ghnItem);
		
		ResourceItem geItem=new ResourceItem();
		geItem.id=gc.id();
		geItem.type="RunningInstance";
		list.add(geItem);
		
		ResourceItem seItem=new ResourceItem();
		seItem.id=se.id();
		seItem.type="RuntimeResource";
		list.add(seItem);
		
		resourceList.setResource(list);
		params.setTargetScope(targetScope);
		params.setResources(resourceList);
		
		RMBinderLibrary library=Proxies.binderService().build();
		return library.addResources(params);
	}
	
	
	static String decryptString(String toDecrypt){
		try{
			return StringEncrypter.getEncrypter().decrypt(toDecrypt);
		}catch(Exception e) {
			throw new RuntimeException("Unable to decrypt.",e);
		}
	}
	
}
