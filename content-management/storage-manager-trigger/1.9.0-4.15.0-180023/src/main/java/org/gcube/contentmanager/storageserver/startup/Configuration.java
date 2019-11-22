package org.gcube.contentmanager.storageserver.startup;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanager.storageserver.parse.utils.ValidationUtils;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
	
	private String scope;
	private String[] server;
	private String username;
	private String password;
	private String backendType;
	private ArrayList<String> dtsHosts;
	private boolean activeDTSFilter;
	private static final String SE_CATEGORY="DataStorage";
	private static final String SE_NAME="StorageManager";
	private static final String ACCOUNTING_USERNAME="accounting_user";
	private static final String ACCOUNTING_PASSWORDNAME="accounting_pwd";
	Logger logger= LoggerFactory.getLogger(Configuration.class);
	
	public Configuration(String scope, String user, String password, boolean dtsFilter){
		this.activeDTSFilter=dtsFilter;
		this.scope=scope;
		if(!ValidationUtils.validationScope(scope))
			throw new IllegalArgumentException("invalid scope exception: "+scope);
		
	}
	
	public Configuration(String scope, boolean dtsFilter){
		this.activeDTSFilter=dtsFilter;
		this.scope=scope;
		if(!ValidationUtils.validationScope(scope))
			throw new IllegalArgumentException("invalid scope exception: "+scope);
		ScopeProvider.instance.set(scope);
		
	}
	
	public String[] getServerAccess(List<ServiceEndpoint> resources){
		String savedScope=null;
		if(scope!=null){
			savedScope=ScopeProvider.instance.get();
			ScopeProvider.instance.set(scope);
		}
		logger.debug("get server from IS ");
		getServerRRFws(resources);
		if(scope!=null){
			ScopeProvider.instance.set(savedScope);
		}
		logger.info("server found {} ", server);
		return server;
	}
	
	public String[] getServerRRFws(List<ServiceEndpoint> resources){
//		List<ServiceEndpoint> resources = getStorageServiceEndpoint();
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
	
	/**
	 * The accounting user is retrieved from the first serviceEndpoint found in the scope and the first accessPoint inside the serviceEndpoint
	 * @param resources the serviceEndpoint list
	 * @return accounting username
	 */
	protected String getAccountingUser(List<ServiceEndpoint> resources){
		logger.trace("retrieving access point");
		for (AccessPoint ap:resources.get(0).profile().accessPoints()) {
			Map<String, Property>map= ap.propertyMap();
			Property user=map.get(ACCOUNTING_USERNAME);
			if (user!=null){
				logger.debug("accounting user found on SE");
				return user.value();				
			}
		}
		return null;
	}

	/**
	 * The accounting password is retrieved from the first serviceEndpoint found in the scope and the first accessPoint inside the serviceEndpoint
	 * @param resources the serviceEndpoint list
	 * @return accounting password
	 */
	protected String getAccountingPassword(List<ServiceEndpoint> resources){
		for (AccessPoint ap:resources.get(0).profile().accessPoints()) {
			Map<String, Property>map= ap.propertyMap();
			Property pwd=map.get(ACCOUNTING_PASSWORDNAME);
			if (pwd!=null){
				logger.debug("password field found on SE");
				try {
					return StringEncrypter.getEncrypter().decrypt(pwd.value());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	protected List<ServiceEndpoint> getStorageServiceEndpoint() {
		logger.debug("query for serviceEndpoint ongoing...");
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+SE_CATEGORY+"' and $resource/Profile/Name eq '"+SE_NAME+"' ");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> resources = client.submit(query);
		if (resources.size() > 0)
			logger.debug("resource found on IS");
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
		logger.debug("Type of backend found "+backendType);
		return server;
	}
	
	private String[] getServers(List<ServiceEndpoint> resources) {
		ServiceEndpoint defaultResource=null;
		logger.debug("search RR with priority ");
	// search RR with property DEFAULT	
		for (ServiceEndpoint res : resources){
			String priority=retrievePropertyValue(res, "priority");
			if (priority!=null){
				defaultResource=res;
				logger.debug("found a RR with priority: ");
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
			logger.debug("Type of backend found in RR is "+backendType);
			return server;

		}else{
			throw new IllegalStateException("Runtime Resource found are more than 1 but all without default priority setted");
		}
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
	
	public List<String> retrieveDTSHosts(){
		if(activeDTSFilter){
			ArrayList<String> scopes=ValidationUtils.getVOScopes(scope);
			dtsHosts= new ArrayList<String>();
			for(String currentScope:scopes){
				String host=getHosts("DataTransformation", "DataTransformationService", currentScope);
				logger.debug("host found: "+host+ " in scope: "+currentScope);
				if(host!=null)
					dtsHosts.add(host);

			}
			for(String host : dtsHosts){
				logger.debug("DTS host: "+host);
			}
			return dtsHosts;
		}else return null;
	}
	
	public String getHosts(String serviceClass, String serviceName) {
		return getHosts(serviceClass, serviceName, scope);
	}
	
	public String getHosts(String serviceClass, String serviceName, String scope) {
		String host=null;
		String currentScope=ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass eq '"+serviceClass+"' and $resource/Profile/ServiceName eq '"+serviceName+"' ");
		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		try{
			List<GCoreEndpoint> resources = client.submit(query);
			if(resources.size()>0){
				GCoreEndpoint res=resources.get(0);
				Iterator<Endpoint> it=res.profile().endpoints().iterator();
				if(it.hasNext()){
					Endpoint endpoint=it.next();
					host=endpoint.uri().toString();
					if(host.contains("//")){
						int begin=host.indexOf("//");
						host=host.substring(begin+2);
						logger.debug("phase#1 "+ host );
						String [] uris=host.split(":");
						logger.debug("phase#2 "+uris[0]);
						host=uris[0];
					}
				}
				ScopeProvider.instance.set(currentScope);
			}
		}catch(Exception e){
			logger.error("FAIL to retrieve resource from scope "+scope+" cause: "+e.getMessage());
			ScopeProvider.instance.set(currentScope);
		}

		return host;
	}
	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
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

	public ArrayList<String> getDtsHosts() {
		return dtsHosts;
	}

	public void setDtsHosts(ArrayList<String> dtsHosts) {
		this.dtsHosts = dtsHosts;
	}

	public boolean isActiveDTSFilter() {
		return activeDTSFilter;
	}

	public void setActiveDTSFilter(boolean activeDTSFilter) {
		this.activeDTSFilter = activeDTSFilter;
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
	
	


}
