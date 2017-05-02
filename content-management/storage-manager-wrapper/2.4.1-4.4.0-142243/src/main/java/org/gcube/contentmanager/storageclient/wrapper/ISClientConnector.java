package org.gcube.contentmanager.storageclient.wrapper;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;
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
	private static HashMap<String, Object> isCache;
	
	public ISClientConnector(){
	}
	
	public String[] getPrimaryServer(List<ServiceEndpoint> resources){
		logger.trace("get server from IS");
		if(isCache!=null){
			server=(String[]) isCache.get("MongoDBServer");
			username=(String)isCache.get("username");
			password=(String)isCache.get("password");
		}
		if(server==null){
			getServerRRFws(resources);
			if(server!= null){
				if(isCache==null)
					isCache=new HashMap<String, Object>();
				isCache.put("MongoDBServer", server);
				if(username != null && username.length() > 0){
					isCache.put("username", username);
					isCache.put("password", password);
				}
				logger.info("ISCACHE: ELEMENT INSERTED ");
			}
				
		}else{
			logger.info("ISCACHE: ELEMENT EXTRACTED");
		}
		return server;
	}

	
	public String[] getServerRRFws(List<ServiceEndpoint> resources){
		if(resources.size() > 1){
			logger.info("found "+resources.size()+" RR ");
			// take the RR with property priority setted to DEFAULT
			//take servers take RR name
			return getServers(resources);
		}else if(resources.size() == 1){
			logger.info("found only one RR, take it"); 
			return getServers(resources.get(0));
			//take RR name
		}else{
			logger.error("RUNTIME RESOURCE NOT FOUND IN THIS SCOPE "+ScopeProvider.instance.get());
			throw new RuntimeException("RUNTIME RESOURCE NOT FOUND IN SCOPE: "+ScopeProvider.instance.get());
		}
	}

	public List<ServiceEndpoint> getServiceEndpoint(String category, String name){
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+category+"' and $resource/Profile/Name eq '"+name+"' ");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> resources = client.submit(query);
		return resources;
	}
	
	public List<ServiceEndpoint> getStorageEndpoint(String scope) {
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
		return resources;
	}
	

	private String[] getServers(ServiceEndpoint res) {
		server=new String[res.profile().accessPoints().size()];
		int i=0;
		for (AccessPoint ap:res.profile().accessPoints()) {
			if (ap.name().equals("server"+(i+1))) {
				server[i] = ap.address();
	// if presents, try to get user and password			
				username = ap.username();	
				if(username != null && username.length() > 0){
					try {
						password = StringEncrypter.getEncrypter().decrypt(ap.password());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				i++;
			}
		}
		Iterator<AccessPoint> it= res.profile().accessPoints().iterator();
		AccessPoint ap=(AccessPoint)it.next();
		Map<String, Property>map= ap.propertyMap();
		Property type=map.get("type");
		backendType=type.value();
		logger.info("Type of backend found "+backendType);
		return server;
	}
	
	private String[] getServers(List<ServiceEndpoint> resources) {
		ServiceEndpoint defaultResource=null;
		logger.info("search RR with priority ");
	// search RR with property DEFAULT	
		for (ServiceEndpoint res : resources){
			String priority=retrievePropertyValue(res, "priority");
			if (priority!=null){
				defaultResource=res;
				logger.info("found a RR with priority: ");
				break;
			}
		}
		if(defaultResource!=null){
			server=new String[defaultResource.profile().accessPoints().size()];
			int i=0;
			for (AccessPoint ap:defaultResource.profile().accessPoints()) {
				if (ap.name().equals("server"+(i+1))) {
					server[i] = ap.address();
					// if presents, try to get user and password			
					username = ap.username();
					password="";
					if(username != null && username.length() > 0){
						try {
							password = StringEncrypter.getEncrypter().decrypt(ap.password());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					i++;
				}
			}
			backendType=retrievePropertyValue(defaultResource, "type");
			volatileHost= new String [1];
			volatileHost[0]=retrievePropertyValue(defaultResource, "volatile");
			logger.info("Type of backend found in RR is "+backendType);
			return server;

		}else{
			throw new IllegalStateException("Runtime Resource found are more than 1 but all without default priority setted");
		}
	}


	public String getBackendType(List<ServiceEndpoint> resources) {
		if(backendType!=null) return backendType;
		backendType = retrievePropertyValue(resources.get(0), "type");
		return backendType;
	}

	public String[] getVolatileHost(List<ServiceEndpoint> resources) {
		if(volatileHost!=null) return volatileHost;
		volatileHost= new String[1];
		volatileHost[0] = retrievePropertyValue(resources.get(0), "volatile");
		return volatileHost;
	}
	
	
	public String retrievePropertyValue(String name, String scope) {
		List<ServiceEndpoint> resources = getStorageEndpoint(scope);
		ServiceEndpoint res=resources.get(0);
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

	
}