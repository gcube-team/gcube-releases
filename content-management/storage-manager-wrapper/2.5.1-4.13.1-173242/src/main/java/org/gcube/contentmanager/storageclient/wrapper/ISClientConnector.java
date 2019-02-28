package org.gcube.contentmanager.storageclient.wrapper;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Define the queries for IS-Collector service
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class ISClientConnector {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ISClientConnector.class);
	private String[] server;
	private String backendType;
	private String[] volatileHost;
	protected String username;
	protected String password;
	protected ServiceEndpoint storageResource;
	private static HashMap<String, Object> isCache;
	
	public ISClientConnector(){
	}
	
	public String[] retrieveConnectionInfo(ServiceEndpoint resource){
			return fillConnectionFields(resource);
	}

	public List<ServiceEndpoint> getServiceEndpoint(String category, String name){
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+category+"' and $resource/Profile/Name eq '"+name+"' ");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> resources = client.submit(query);
		return resources;
	}
	
	public ServiceEndpoint getStorageEndpoint(String scope) {
	//if the serviceEndpoint has been already discovered and selected, I'm going to use that 
   //		otherwise I'm going to discovered it from IS	
		if(getStorageResource() == null){
			logger.debug("discovering service endpoint");
			String savedScope=null;
			if(scope!=null){
				savedScope=ScopeProvider.instance.get();
				logger.debug("set scopeProvider to scope "+scope+" scope provider scope is "+savedScope);
				ScopeProvider.instance.set(scope);
			}
			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Category/text() eq 'DataStorage' and $resource/Profile/Name eq 'StorageManager' ");
			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
			List<ServiceEndpoint> resources = client.submit(query);
			if(scope!=null){
				logger.debug("reset scopeProvider to scope "+scope);
				ScopeProvider.instance.set(savedScope);
			}
			if (resources.size()>0){
				ServiceEndpoint storageResource = getPriorityResource(resources);
				setStorageResource(storageResource);
				return storageResource;
			}else
				throw new RuntimeException("Storage ServiceEndpoint not found under scope: "+scope);
		}else{
			logger.debug("service endpoint already discovered");
			return getStorageResource();
		}
	}
	
	
	private String[] fillConnectionFields(ServiceEndpoint resource) {
		if(resource!=null){
			String [] server=new String[resource.profile().accessPoints().size()];
			int i=0;
			for (AccessPoint ap:resource.profile().accessPoints()) {
				if (ap.name().equals("server"+(i+1))) {
					server[i] = ap.address();
					// if presents, try to get user and password			
					setUsername(ap.username());
					// set password default value to empty string
					setPassword("");
					if(getUsername() != null && getUsername().length() > 0){
						try {
							setPassword(StringEncrypter.getEncrypter().decrypt(ap.password()));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					i++;
				}
			}
			setBackendType(retrievePropertyValue(resource, "type"));
			String [] volatileHost= new String [1];
			volatileHost[0]=retrievePropertyValue(resource, "volatile");
			setVolatileHost(volatileHost);
			logger.info("Type of backend found in RR is "+backendType);
			return server;

		}else{
			throw new IllegalStateException("Runtime Resource found are more than 1 but all without default priority setted");
		}
	}

	private ServiceEndpoint getPriorityResource(List<ServiceEndpoint> resources) {
		ServiceEndpoint defaultResource=null;
		logger.info("search RR with priority ");
	// search RR with property DEFAULT	
		for (ServiceEndpoint res : resources){
			String priority=retrievePropertyValue(res, "priority");
			if (priority!=null){
				defaultResource=res;
				setStorageResource(res);
				logger.info("found a RR with priority: ");
				break;
			}
		}
		return defaultResource;
	}

	public String getBackendType(ServiceEndpoint resource) {
		if(getBackendType() !=null) return getBackendType();
		setBackendType(retrievePropertyValue(resource, "type"));
		return getBackendType();
	}

	public String[] getVolatileHost(ServiceEndpoint resource) {
		if(getVolatileHost() !=null) return getVolatileHost();
		String [] volatileHost= new String[1];
		volatileHost[0] = retrievePropertyValue(resource, "volatile");
		setVolatileHost(volatileHost);
		return volatileHost;
	}
	
	
	public String retrievePropertyValue(String name, String scope) {
		ServiceEndpoint res = getStorageEndpoint(scope);
		Iterator<AccessPoint> it= res.profile().accessPoints().iterator();
		String value=null;
		while(it.hasNext()){
			AccessPoint ap=(AccessPoint)it.next();
			Map<String, Property>map= ap.propertyMap();
			Property type=map.get(name);
			if(type!=null){
				value=type.value();
				if(value!= null) break;
			}

		}
		return value;
	}

	private String retrievePropertyValue(ServiceEndpoint res, String name) {
		Iterator<AccessPoint> it= res.profile().accessPoints().iterator();
		AccessPoint ap=(AccessPoint)it.next();
		Map<String, Property>map= ap.propertyMap();
		Property type=map.get(name);
		if (type!=null)
			return type.value();
		else
			return null;
	}
	
	public String getResolverHost(ServiceEndpoint serviceEndpoint) {
		return serviceEndpoint.profile().runtime().hostedOn();
		
	}
	
	public String[] getServer() {
		return server;
	}

	public void setServer(String[] server) {
		this.server = server;
	}

	public String getBackendType() {
		return backendType;
	}

	public void setBackendType(String backendType) {
		this.backendType = backendType;
	}

	public String[] getVolatileHost() {
		return volatileHost;
	}

	public void setVolatileHost(String[] volatileHost) {
		this.volatileHost = volatileHost;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ServiceEndpoint getStorageResource() {
		return storageResource;
	}

	public void setStorageResource(ServiceEndpoint storageResource) {
		this.storageResource = storageResource;
	}


	
}