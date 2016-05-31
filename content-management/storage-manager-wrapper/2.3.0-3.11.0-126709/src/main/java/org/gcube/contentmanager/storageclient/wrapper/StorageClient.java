package org.gcube.contentmanager.storageclient.wrapper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanager.storageclient.protocol.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Define the parameters for invoke the storage-manager-core library
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class StorageClient {

	private String clientID;
// public | private | shared. If shared the rwx permits are extended to all services of the same type
	private String typeAccess;
	private String memoryType;
	private String serviceClass;
	private String serviceName;
	private String owner;
	private String server;
	private String scopeString;
	private String currentScope;
// the scope used for discovering the runtimeResource	
	private String RRScope;
	private String backendType;
	
	private static final Logger logger = LoggerFactory.getLogger(StorageClient.class);
	private static final String DEFAULT_SERVICE_CLASS="ExternalApplication";
	private static final String DEFAULT_SERVICE_NAME="Default";
	private static final String DEFAULT_BACKEND_TYPE="MongoDB";
	
	
	
	/**
	 * Constructor without optional argument created for gcube infrastructure internal use
	 * @param ServiceClass 
	 * @param ServiceName
	 * @param owner
	 * @param typeAccess
	 * @param scope
	 */
	@Deprecated
	public StorageClient(String serviceClass, String serviceName, String owner, AccessType accessType, String scope){
		checkScopeProvider();
		this.currentScope=ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		String id=owner;
		this.owner=owner;
		this.scopeString=ScopeProvider.instance.get();
		this.typeAccess=accessType.toString();
		this.serviceClass=serviceClass;
		this.serviceName=serviceName;
		setClientId(serviceClass, serviceName, id);
		
	}

	/**
	 * Constructor without optional argument created for gcube infrastructure internal use
	 * @param ServiceClass 
	 * @param ServiceName
	 * @param owner
	 * @param typeAccess
	 * @param scope scope identifier
	 * @param forceScope if true the scope used is the scope specified in scope parameter, else the scope specified in the parameter scope is used only for discovering the RuntimeResource
	 */
	public StorageClient(String serviceClass, String serviceName, String owner, AccessType accessType, String scope, boolean forceScope){
		checkScopeProvider();
		if(forceScope){
			this.currentScope=ScopeProvider.instance.get();
			ScopeProvider.instance.set(scope);
			this.scopeString=ScopeProvider.instance.get();
		}else{
			this.RRScope=scope;
			this.scopeString=ScopeProvider.instance.get();
		}
		String id=owner;
		this.owner=owner;
		this.typeAccess=accessType.toString();
		this.serviceClass=serviceClass;
		this.serviceName=serviceName;
		setClientId(serviceClass, serviceName, id);
		
	}

	
	
	/**
	 * Constructor without optional argument created for gcube infrastructure internal use
	 * @param ServiceClass 
	 * @param ServiceName
	 * @param owner
	 * @param typeAccess
	 * @param scope
	 */
	public StorageClient(String serviceClass, String serviceName, String owner, AccessType accessType){
		checkScopeProvider();
		String id=owner;
		this.owner=owner;
		this.scopeString=ScopeProvider.instance.get();
		this.typeAccess=accessType.toString();
		this.serviceClass=serviceClass;
		this.serviceName=serviceName;
		setClientId(serviceClass, serviceName, id);
		
	}
	
	
	/**
	 * Constructor created for external use
	 * @param owner
	 * @param typeAccess
	 * @param memory defines the kind of memory: VOLATILE or PERSISTENT
	 * @param scope
	 */
	public StorageClient(String owner, AccessType accessType, MemoryType memory){
		checkScopeProvider();
		String id=owner;
		this.owner=owner;
		this.scopeString=ScopeProvider.instance.get();
		this.typeAccess=accessType.toString();
		this.memoryType= memory.toString();
		this.serviceClass=DEFAULT_SERVICE_CLASS;
		this.serviceName=DEFAULT_SERVICE_NAME;
		setClientId(serviceClass, serviceName, id);
	}
	
	

	/**
	 *  Constructor with optional argument server 
	 * @param ServiceClass 
	 * @param ServiceName
	 * @param owner
	 * @param typeAccess
	 * @param scope
	 * @param server: define the mongoDBserver
	 */
	@Deprecated
	public StorageClient(String serviceClass, String serviceName, String owner, AccessType accessType, String scope, String server){
		checkScopeProvider();
		this.currentScope=ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		String id=owner;
		this.owner=owner;
		this.scopeString=ScopeProvider.instance.get();
		this.typeAccess=accessType.toString();
		this.serviceClass=serviceClass;
		this.serviceName=serviceName;
		this.server=server;
		setClientId(serviceClass, serviceName, id);
	}

	
	/**
	 *  Constructor with optional argument server 
	 * @param ServiceClass 
	 * @param ServiceName
	 * @param owner
	 * @param typeAccess
	 * @param scope
	 * @param server: define the mongoDBserver
	 */
	public StorageClient(String serviceClass, String serviceName, String owner, String server, AccessType accessType){
		checkScopeProvider();
		String id=owner;
		this.owner=owner;
		this.scopeString=ScopeProvider.instance.get();
		this.typeAccess=accessType.toString();
		this.serviceClass=serviceClass;
		this.serviceName=serviceName;
		this.server=server;
		setClientId(serviceClass, serviceName, id);
	}
	
	/**
	 * Constructor with optional argument memoryType
	 * @param ServiceClass 
	 * @param ServiceName
	 * @param owner
	 * @param typeAccess
	 * @param memory defines the kind of memory: VOLATILE or PERSISTENT
	 * @param scope
	 */
	@Deprecated
	public StorageClient(String serviceClass, String serviceName, String owner, AccessType accessType, String scope, MemoryType memory){
		checkScopeProvider();
		this.currentScope=ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		String id=owner;
		this.owner=owner;
		this.scopeString=ScopeProvider.instance.get();
		this.typeAccess=accessType.toString();
		this.memoryType= memory.toString();
		this.serviceClass=serviceClass;
		this.serviceName=serviceName;
		setClientId(serviceClass, serviceName, id);
	}


	
	/**
	 * Constructor with optional argument memoryType
	 * @param ServiceClass 
	 * @param ServiceName
	 * @param owner
	 * @param typeAccess
	 * @param memory defines the kind of memory: VOLATILE or PERSISTENT
	 * @param scope
	 */
	public StorageClient(String serviceClass, String serviceName, String owner, AccessType accessType, MemoryType memory){
		checkScopeProvider();
		String id=owner;
		this.owner=owner;
		this.scopeString=ScopeProvider.instance.get();
		this.typeAccess=accessType.toString();
		this.memoryType= memory.toString();
		this.serviceClass=serviceClass;
		this.serviceName=serviceName;
		setClientId(serviceClass, serviceName, id);
	}

	
	/**
	 * Constructor with optional arguments server and memory 
	 * @param ServiceClass 
	 * @param ServiceName
	 * @param owner
	 * @param typeAccess
	 * @param memory defines the kind of memory: VOLATILE or PERSISTENT
	 * @param server: define the mongoDBserver
	 * @param scope
	 */
	@Deprecated
	public StorageClient(String serviceClass, String serviceName, String owner, AccessType accessType, String scope, String server, MemoryType memory){
		checkScopeProvider();
		this.currentScope=ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		String id=owner;
		this.owner=owner;
		this.scopeString=ScopeProvider.instance.get();
		this.typeAccess=accessType.toString();
		this.memoryType=memory.toString();
		this.serviceClass=serviceClass;
		this.serviceName=serviceName;
		this.server=server;
		setClientId(serviceClass, serviceName, id);
	}
	
	
	
	/**
	 * Constructor with optional arguments server and memory 
	 * @param ServiceClass 
	 * @param ServiceName
	 * @param owner
	 * @param typeAccess
	 * @param memory defines the kind of memory: VOLATILE or PERSISTENT
	 * @param server: define the mongoDBserver
	 * @param scope
	 */
	public StorageClient(String serviceClass, String serviceName, String owner, AccessType accessType, MemoryType memory, String server){
		checkScopeProvider();
		String id=owner;
		this.owner=owner;
		this.scopeString=ScopeProvider.instance.get();
		this.typeAccess=accessType.toString();
		this.memoryType=memory.toString();
		this.serviceClass=serviceClass;
		this.serviceName=serviceName;
		this.server=server;
		setClientId(serviceClass, serviceName, id);
	}
	
	/**
	 * Get Instance remote client - storage-manager-core
	 * @return
	 * @throws IllegalStateException if the resource is not found on the IS 
	 */
	public IClient getClient(){
		String[]  newServer=null;
		String user=null;
		String password=null;
		String passPhrase=null;
		ISClientConnector isclient=new ISClientConnector();
		String currentScope=ScopeProvider.instance.get();
		if(RRScope == null){
			if(new ScopeBean(currentScope).is(Type.VRE))
				RRScope=new ScopeBean(currentScope).enclosingScope().toString();
			else
				RRScope=currentScope;
		}
	// old method for retrieve hostedOn field in storage ServiceEndpoint resource	
		List<ServiceEndpoint> resources=isclient.getStorageEndpoint(RRScope);
		if(resources ==null )
			throw new IllegalStateException("the storage resource is not present on IS in scope: "+RRScope); 
		List<ServiceEndpoint> resolverResource =isclient.getServiceEndpoint(Utils.URI_RESOLVER_RESOURCE_CATEGORY, Utils.URI_RESOLVER_RESOURCE_NAME);
		String resolverHost=null;
		if(resolverResource !=null && resolverResource.size()> 0)
			resolverHost=isclient.getResolverHost(resolverResource.get(0));
		else
			throw new IllegalStateException("the uri resolver resource is not present on IS in scope: "+currentScope); 
		if(server==null){
			logger.debug("server not set. Try to query IS in scope: "+scopeString);
			String[] serverFound=checkVarEnvMongo();
			if(serverFound==null){
				serverFound=isclient.getServerAccess(resources);
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
			String environment=null;
			if((memoryType != null) && (memoryType.toString().equalsIgnoreCase("VOLATILE"))){
				environment="VOLATILE"+scopeString;
			}else{
//				String environment=scopeString.substring(scopeString.lastIndexOf("/"));
				environment=scopeString;
			}
			try {
				passPhrase=retrieveEncryptionPhrase(currentScope);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			environment=environment+clientID;
			ServiceEngine engine= new ServiceEngine(newServer, environment, typeAccess, owner);
			engine.setServiceClass(serviceClass);
			engine.setServiceName(serviceName);
			engine.setGcubeAccessType(typeAccess.toLowerCase());
			engine.setBackendType(backendType);
			engine.setBackendUser(user);
			engine.setBackendPassword(password);
			engine.setResolverHost(resolverHost);
			if(passPhrase!=null)
				engine.setPassPhrase(passPhrase);
			if(memoryType!=null)
				engine.setGcubeMemoryType(memoryType.toLowerCase());
			engine.setGcubeScope(ScopeProvider.instance.get());
			engine.setOwnerGcube(owner);
			if(currentScope!=null)
				ScopeProvider.instance.set(currentScope);
			return engine;
		}
	}	


	private void setClientId(String serviceClass, String serviceName, String id) {
		if(typeAccess.equalsIgnoreCase("public")){
			clientID="";
		}else if(typeAccess.equalsIgnoreCase("private")){
			clientID=ServiceEngine.FILE_SEPARATOR+"home"+ServiceEngine.FILE_SEPARATOR+serviceClass+ServiceEngine.FILE_SEPARATOR+serviceName+ServiceEngine.FILE_SEPARATOR+id;
		}else if(typeAccess.equalsIgnoreCase("shared")){
			clientID=ServiceEngine.FILE_SEPARATOR+"home"+ServiceEngine.FILE_SEPARATOR+serviceClass+ServiceEngine.FILE_SEPARATOR+serviceName;
		}else{
			throw new IllegalArgumentException("type is not correctly: public, private or shared");
		}
	}
	
	/**
	 * Check environmental variable called : "STORAGE_MANAGER_MONGO_SERVER" for retrieving server list
	 * @return
	 */
	private String[] checkVarEnvMongo(){
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
	private String checkVarEnvBackendType(){
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
	private String checkVarEnvUser(){
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
	private String checkVarEnvPassword(){
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
	
	
	/**
	 * if the scope provider is not set then check if the env variable: SCOPE is set and set the scopeProvider
	 */
	private void checkScopeProvider(){
		String scopeProvided=ScopeProvider.instance.get();
		if (scopeProvided==null){
			scopeProvided=Utils.checkVarEnv("SCOPE");
			if (scopeProvided != null){
				ScopeProvider.instance.set(scopeProvided);
			}else{
				throw new RuntimeException("Scope not set ");
			}
		}
	}
	
	private String retrieveEncryptionPhrase(String rootScope) throws Exception {
		String currentScope=ScopeProvider.instance.get();
		String scope=rootScope;
		ScopeProvider.instance.set(scope);
		logger.debug("set scope: "+scope);
		String encryptedKey=null;
		ISClientConnector isclient=new ISClientConnector();
		encryptedKey=isclient.retrievePropertyValue("PassPhrase", scope);
		String decryptString=org.gcube.common.encryption.StringEncrypter.getEncrypter().decrypt(encryptedKey);
		ScopeProvider.instance.set(currentScope);
		return decryptString;
	}
	
}