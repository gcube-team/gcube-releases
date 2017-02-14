package org.gcube.contentmanager.storageclient.wrapper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanager.storageclient.protocol.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
	
	private String clientID;
// public | private | shared. If shared the rwx permits are extended to all services of the same type
	private String typeAccess;
	private String memoryType;
	private String owner;
	private String scopeString;
	private String server;
	private String environment;
	private String sc;
	private String sn;
	private String user;
	private String password;
	private String passPhrase;
	private ISClientConnector isclient;
// the scope used for discovering the runtimeResource	
	private String RRScope;
	private String backendType;
	private String[] volatileHost;
	private String[] persistentHosts;
	private String resolverHost;
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	private static final String DEFAULT_BACKEND_TYPE="MongoDB";
	private static final String WRITE_CONCERN_PROPERTY_NAME="write_concern";
	private static final String READ_PREFERENCE_PROPERTY_NAME="read_preference";
	private static final String HOME_LIBRARY_SERVICE_CLASS="org.gcube.portlets.user";
	private static final String HOME_LIBRARY_SERVICE_NAME_DEV="test-home-library";
	private static final String HOME_LIBRARY_SERVICE_NAME_PROD="home-library";
	private static final String HL_CONTEXT = "/d4science.research-infrastructures.eu";
	private static final String HL_CONTEXT_DEV = "/gcube";
		
		
		
	public Configuration(String sc, String sn, String scopeString, String owner, String clientID, String accessType, String memory){
		this.sc=sc;
		this.sn=sn;
		this.owner=owner;
		this.clientID=clientID;
		this.typeAccess=accessType;
		this.memoryType=memory;
		setScopeString(scopeString);
	}
	
	/**
	 * Retrieve a valid configuration from IS for instantiating the engine
	 */
	public void getConfiguration(){
		String[]  newServer=null;
		ISClientConnector isclient=new ISClientConnector();
		String currentScope=ScopeProvider.instance.get();
		logger.debug("Scope found on ScopeProvider instance is "+currentScope);
		if(RRScope == null){
			if(new ScopeBean(currentScope).is(Type.VRE)){
				logger.debug("If ScopeProvider scope is VRE scope RR scope became VO scope");
				RRScope=new ScopeBean(currentScope).enclosingScope().toString();
			}else{
				logger.debug("If ScopeProvider scope is not a VRE scope RR scope is ScopeProvider scope");
				RRScope=currentScope;
			}
		}
		logger.debug("RuntimeResource scope "+RRScope);
		List<ServiceEndpoint> resources=isclient.getStorageEndpoint(RRScope);
		if(resources ==null )
			throw new IllegalStateException("the storage resource is not present on IS in scope: "+RRScope); 
		List<ServiceEndpoint> resolverResource =isclient.getServiceEndpoint(Utils.URI_RESOLVER_RESOURCE_CATEGORY, Utils.URI_RESOLVER_RESOURCE_NAME);
		if(resolverResource !=null && resolverResource.size()> 0)
			setResolverHost(isclient.getResolverHost(resolverResource.get(0)));
		else
			throw new IllegalStateException("the uri resolver resource is not present on IS in scope: "+currentScope); 
	// old method for retrieve hostedOn field in storage ServiceEndpoint resource	
		if(server==null){
			logger.debug("server not set. Try to query IS in scope: "+scopeString);
			String[] serverFound=checkVarEnvMongo();
			if(serverFound==null){
				serverFound=isclient.getPrimaryServer(resources);
				user=isclient.username;
				password=isclient.password;
				backendType=isclient.getBackendType(resources);
			}else{
				backendType=checkVarEnvBackendType();
				if(backendType == null) backendType=DEFAULT_BACKEND_TYPE;
				user=checkVarEnvUser();
				password=checkVarEnvPassword();
			}
			newServer=serverFound;
		}else{
			logger.debug("server found: "+server);
			String[] serverPassed={server};
			newServer=serverPassed;
			if(backendType == null) backendType=DEFAULT_BACKEND_TYPE;
		}
		if(newServer==null){
			throw new IllegalStateException("Resource not found on Information System");
		}else{
			environment=setAreaStorage(sc, sn);
			setServerHosts(newServer, isclient, resources);
			try {
				passPhrase=retrieveEncryptionPhrase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setServerHosts(String[] newServer, ISClientConnector isclient, List<ServiceEndpoint> resources) {
		if((memoryType != null) && ((memoryType.equals(MemoryType.VOLATILE.toString()) || (memoryType.equals(MemoryType.BOTH.toString()))))){
			volatileHost= isclient.getVolatileHost(resources);
			logger.info("backend host is the volatile server"+volatileHost[0]);
		}
		persistentHosts=newServer;
	}


	private String  setAreaStorage(String sc, String sn) {
		String area=null;
		if(isHomeLibrary(sc, sn))
			return getHomeLibraryContext();
		if((memoryType != null) && (memoryType.equals(MemoryType.VOLATILE.toString()))){
			area="VOLATILE"+scopeString+clientID;
		}else{
			area=scopeString+clientID;
		}
		return area;
	}
	
	private boolean isHomeLibrary(String sc, String sn){
		if(((sc.equals(HOME_LIBRARY_SERVICE_CLASS) && sn.equals(HOME_LIBRARY_SERVICE_NAME_DEV)))||((sc.equals(HOME_LIBRARY_SERVICE_CLASS) && sn.equals(HOME_LIBRARY_SERVICE_NAME_PROD))))
			return true;
		return false;
	}
	
	private String getHomeLibraryContext(){
		String area=null;
		String scope=ScopeProvider.instance.get();
		String context=null;
		if (scope.startsWith("/gcube"))
			context= HL_CONTEXT_DEV;
		else if(scope.startsWith("/d4science.research-infrastructures.eu"))
			context=HL_CONTEXT;
		else{
			throw new RuntimeException("Unrecognized scope: "+scope);
		}
		area=context+clientID;
		if((memoryType != null) && (memoryType.equals(MemoryType.VOLATILE.toString())))
			area="VOLATILE"+area;
		return area;
	}

	
	protected void readWRPropertiesFromRR( String currentScope, ServiceEngine engine) {
		String write=null;
		String read=null;
		if((memoryType != null) && (!(memoryType.equals(MemoryType.VOLATILE.toString())))){
			write=getISClient().retrievePropertyValue(WRITE_CONCERN_PROPERTY_NAME, currentScope);
			read=isclient.retrievePropertyValue(READ_PREFERENCE_PROPERTY_NAME, currentScope);
			if((write!=null) && (read!=null)){
				engine.setWriteConcern(write);
				engine.setReadConcern(read);
			}
		}
	}	

	/**
	 * Check environmental variable called : "STORAGE_MANAGER_MONGO_SERVER" for retrieving server list
	 * @return
	 */
	private static String[] checkVarEnvMongo(){
		Map<String, String> env = System.getenv();
        TreeSet<String> keys = new TreeSet<String>(env.keySet());
        
        Iterator<String> iter = keys.iterator();
        String server=null;
        while(iter.hasNext())
        {
            String key = iter.next();
            if(key.equalsIgnoreCase("STORAGE_MANAGER_MONGO_SERVER")){
            	server=env.get(key);
            	break;
            }
        }
        if(server!=null){
        	 String [] servers={server};
        	 return servers;
        }
        return null;
	}

	/**
	 * Check environmental variable called : "STORAGE_MANAGER_BACKEND_TYPE" for retrieving server list
	 * @return
	 */
	private static String checkVarEnvBackendType(){
		Map<String, String> env = System.getenv();
        TreeSet<String> keys = new TreeSet<String>(env.keySet());
        
        Iterator<String> iter = keys.iterator();
        String type=null;
        while(iter.hasNext())
        {
            String key = iter.next();
            if(key.equalsIgnoreCase("STORAGE_MANAGER_BACKEND_TYPE")){
            	type=env.get(key);
            	break;
            }
        }
        if(type!=null){
        	 return type;
        }
        return null;
	}

	/**
	 * Check environmental variable called : "STORAGE_MANAGER_USER" for retrieving server list
	 * @return
	 */
	private static String checkVarEnvUser(){
		Map<String, String> env = System.getenv();
        TreeSet<String> keys = new TreeSet<String>(env.keySet());
        
        Iterator<String> iter = keys.iterator();
        String type=null;
        while(iter.hasNext())
        {
            String key = iter.next();
            if(key.equalsIgnoreCase("STORAGE_MANAGER_USER")){
            	type=env.get(key);
            	break;
            }
        }
        if(type!=null){
        	 return type;
        }
        return null;
	}
	
	/**
	 * Check environmental variable called : "STORAGE_MANAGER_PASSWORD" for retrieving server list
	 * @return
	 */
	private static String checkVarEnvPassword(){
		Map<String, String> env = System.getenv();
        TreeSet<String> keys = new TreeSet<String>(env.keySet());
        
        Iterator<String> iter = keys.iterator();
        String type=null;
        while(iter.hasNext())
        {
            String key = iter.next();
            if(key.equalsIgnoreCase("STORAGE_MANAGER_PASSWORD")){
            	type=env.get(key);
            	break;
            }
        }
        if(type!=null){
        	 return type;
        }
        return null;
	}
	
	
	private static String retrieveEncryptionPhrase() throws Exception {
		String currentScope=ScopeProvider.instance.get();
		logger.debug("retrieve encryption prhase on scope: "+currentScope);
		String encryptedKey=null;
		ISClientConnector isclient=new ISClientConnector();
		encryptedKey=isclient.retrievePropertyValue("PassPhrase", currentScope);
		String decryptString=org.gcube.common.encryption.StringEncrypter.getEncrypter().decrypt(encryptedKey);
		return decryptString;
	}


	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getTypeAccess() {
		return typeAccess.toString();
	}

	public void setTypeAccess(String typeAccess) {
		this.typeAccess = typeAccess;
	}

	public String getMemoryType() {
		return memoryType.toString();
	}

	public void setMemoryType(String memoryType) {
		this.memoryType = memoryType;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String server) {
		this.environment = server;
	}

	public String getScopeString() {
		return scopeString;
	}

	public void setScopeString(String scopeString) {
		this.scopeString = scopeString;
	}

	public String getRRScope() {
		return RRScope;
	}

	public void setRRScope(String rRScope) {
		RRScope = rRScope;
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
	
	public String[] getPersistentHosts() {
		return persistentHosts;
	}

	public void setPersistentHosts(String[] hosts) {
		this.persistentHosts = hosts;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassPhrase() {
		return passPhrase;
	}

	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}

	public String getResolverHost() {
		return resolverHost;
	}

	public void setResolverHost(String resolverHost) {
		this.resolverHost = resolverHost;
	}
	
	public ISClientConnector getISClient(){
		if (isclient == null)
			isclient=new ISClientConnector();
		return isclient;
	}

}