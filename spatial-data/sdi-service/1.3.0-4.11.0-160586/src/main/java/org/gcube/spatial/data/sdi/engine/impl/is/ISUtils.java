package org.gcube.spatial.data.sdi.engine.impl.is;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.ByteArrayOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.NetUtils;
import org.gcube.spatial.data.sdi.ScopeUtils;
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

	public static List<ServiceEndpoint> queryForServiceEndpoints(String category, String platformName){
		log.debug("Querying for Service Endpoints [category : {} , platformName : {}, currentScope : {} ]",category,platformName,ScopeUtils.getCurrentScope());
				
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq '"+category+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+platformName+"'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		return client.submit(query);
	}
	
	public static List<GCoreEndpoint> queryForGCoreEndpoint(String serviceClass,String serviceName){
		log.debug("Querying for GCore Endpoints [ServiceClass : {} , ServiceName : {}, currentScope : {} ]",serviceClass,serviceName,ScopeUtils.getCurrentScope());
		
		
		SimpleQuery query =queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq '"+serviceClass+"'")
		.addCondition("$resource/Profile/ServiceName/text() eq '"+serviceName+"'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		return client.submit(query);
	}
	
	
	public static <T extends Resource> T getByHostnameInCollection(String hostname, Collection<T> toCheckList) throws UnknownHostException {		
		for(T gc:toCheckList) {
			String currentHostToCheck=getHost(gc);			
			if(NetUtils.isSameHost(currentHostToCheck, hostname)) return gc;
		}
		return null;
	}
	
	
	public static String getHost(Resource res) {
		if(res instanceof GCoreEndpoint) 
			return (((GCoreEndpoint)res).profile().endpoints().iterator().next().uri().getHost());
		else return (((ServiceEndpoint)res).profile().runtime().hostedOn());
	}
	
	public static List<ServiceEndpoint> querySEByHostname(String category,String platformName,String hostname){
		log.debug("Querying Service Endpoints by hostname [category : {} , platformName : {}, currentScope : {}, hostname {} ]",category,platformName,ScopeUtils.getCurrentScope(),hostname);
		
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq '"+category+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+platformName+"'")
		.addCondition("$resource/Profile/Runtime/HostedOn/text() eq '"+hostname+"'");
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		return client.submit(query);
	}
	
			
	public static List<String> queryById(String id) {
		DiscoveryClient<String> client = client();
		String queryString ="declare namespace ic = 'http://gcube-system.org/namespaces/informationsystem/registry'; "+
				"for $profiles in collection('/db/Profiles')//Document/Data/ic:Profile/Resource "+
				"where $profiles/ID/text() eq '"+id+"'"+				
				" return $profiles";
		return client.submit(new QueryBox(queryString));		
	}
	
	
	public static ServiceEndpoint querySEById(String id) {
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/ID/text() eq '"+id+"'");
		
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		return client.submit(query).get(0);
	}
	
	public static String registerService(ServiceEndpoint toRegister) {
		 RegistryPublisher rp=RegistryPublisherFactory.create();
		 Resource r=rp.create(toRegister);
		 return r.id();
	}
	
	
	public static String addToScope(ServiceEndpoint se,GCoreEndpoint gc, String targetScope) throws ResourcesCreationException, InvalidScopeException {
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
	
	
	public static String decryptString(String toDecrypt){
		try{
			return StringEncrypter.getEncrypter().decrypt(toDecrypt);
		}catch(Exception e) {
			throw new RuntimeException("Unable to decrypt : "+toDecrypt,e);
		}
	}
	
	
	public static ServiceEndpoint update(ServiceEndpoint toUpdate) {
		RegistryPublisher rp=RegistryPublisherFactory.create();
		return rp.update(toUpdate);		
	}
	
	public static ServiceEndpoint updateAndWait(ServiceEndpoint toUpdate) {		
		boolean equals=true;
		boolean timeoutReached=false;
		long timeout=LocalConfiguration.getTTL(LocalConfiguration.IS_REGISTRATION_TIMEOUT);
		log.trace("Going to update {}. Timeout is {} ",toUpdate.id(),timeout);
		String toUpdateString=marshal(toUpdate);
		update(toUpdate);
		long updateTime=System.currentTimeMillis();
		String updatedString=null;
		do {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
			updatedString=queryById(toUpdate.id()).get(0);
			equals=toUpdateString.equals(updatedString);
			timeoutReached=(System.currentTimeMillis()-updateTime)>timeout;
		}while(equals&&(!timeoutReached));
		if(timeoutReached) log.warn("Timeout reached. Check if {} is updated ",toUpdate.id());
		return querySEById(toUpdate.id());
	}
	
	
	public static String marshal(Resource res) {
		ByteArrayOutputStream stream=new ByteArrayOutputStream();
		Resources.marshal(res, stream);
		return stream.toString();
	}
	
	
	public static HashSet<String> getSiblingsScopesInResource(Resource res,String scope){
		HashSet<String> toReturn=new HashSet<String>();
		String parent=ScopeUtils.getParentScope(scope);
		if (parent!=null)
			for(String resourceScope:res.scopes().asCollection()) 
				if(ScopeUtils.getParentScope(resourceScope).equals(parent)) toReturn.add(resourceScope);
		return toReturn;
	}
	
}
