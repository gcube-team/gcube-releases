package org.gcube.contentmanager.storageclient.wrapper;

//import org.gcube.contentmanagement.blobstorage.resource.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
//import org.gcube.contentmanagement.blobstorage.resource.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.common.scope.api.ScopeProvider;
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
	private AccessType typeAccess;
	private MemoryType memoryType;
	private String serviceClass;
	private String serviceName;
	private String owner;
	private String server;
	private String scopeString;
	private String currentScope;
	private String backendType;
	private String volatileHost;
	private String RRScope;
	
	private static final Logger logger = LoggerFactory.getLogger(StorageClient.class);
	private static final String DEFAULT_SERVICE_CLASS="ExternalApplication";
	private static final String DEFAULT_SERVICE_NAME="Default";
	private static final MemoryType DEFAULT_MEMORY_TYPE=MemoryType.PERSISTENT;
	
	/**
	 * Constructor without optional argument created for gcube infrastructure internal use
	 * @param ServiceClass 
	 * @param ServiceName
	 * @param owner
	 * @param typeAccess
	 * @param scope scope identifier
	 * @param forceScope if true the scope used is the scope specified in scope parameter, else the scope specified in the parameter scope is used only for discovering the RuntimeResource
	 */
	@Deprecated
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
		if(accessType!=null)
			this.typeAccess=accessType;
		else throw new RuntimeException("AccessType parameter must be not null");
		this.memoryType=MemoryType.BOTH;
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
		if(accessType!=null)
			this.typeAccess=accessType;
		else throw new RuntimeException("AccessType parameter must be not null");
		this.memoryType=MemoryType.BOTH;
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
		if(accessType!=null)
			this.typeAccess=accessType;
		else throw new RuntimeException("AccessType parameter must be not null");
		if(memory!=null)
			this.memoryType=memory;
		else throw new RuntimeException("MemoryType parameter must be not null");
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
	public StorageClient(String serviceClass, String serviceName, String owner, String server, AccessType accessType){
		checkScopeProvider();
		String id=owner;
		this.owner=owner;
		this.scopeString=ScopeProvider.instance.get();
		if(accessType!=null)
			this.typeAccess=accessType;
		else throw new RuntimeException("AccessType parameter must be not null");
		this.memoryType=MemoryType.BOTH;
		this.serviceClass=serviceClass;
		this.serviceName=serviceName;
		this.server=server;
		this.memoryType=DEFAULT_MEMORY_TYPE;
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
		if(accessType!=null)
			this.typeAccess=accessType;
		else throw new RuntimeException("AccessType parameter must be not null");
		if(memory!=null)
			this.memoryType=memory;
		else throw new RuntimeException("MemoryType parameter must be not null");
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
	public StorageClient(String serviceClass, String serviceName, String owner, AccessType accessType, MemoryType memory, String server){
		checkScopeProvider();
		String id=owner;
		this.owner=owner;
		this.scopeString=ScopeProvider.instance.get();
		if(accessType!=null)
			this.typeAccess=accessType;
		else throw new RuntimeException("AccessType parameter must be not null");
		if(memoryType!=null)
			this.memoryType=memory;
		else throw new RuntimeException("MemoryType parameter must be not null");
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
			Configuration cfg= new Configuration(serviceClass, serviceName, scopeString, owner, clientID, typeAccess.toString(), memoryType.toString());
			cfg.getConfiguration();
			ServiceEngine engine= new ServiceEngine(cfg.getPersistentHosts(), cfg.getVolatileHost(), cfg.getEnvironment(), cfg.getTypeAccess(), cfg.getOwner(), cfg.getMemoryType());
	// set additional fields for the new engine object		
			engine.setServiceClass(getServiceClass());
			engine.setServiceName(getServiceName());
			engine.setGcubeAccessType(getTypeAccess().toString());
			engine.setBackendType(getBackendType());
			engine.setBackendUser(cfg.getUser());
			engine.setBackendPassword(cfg.getPassword());
			engine.setResolverHost(cfg.getResolverHost());
			if(cfg.getPassPhrase()!=null)
				engine.setPassPhrase(cfg.getPassPhrase());
			if(getMemoryType() !=null)
				engine.setGcubeMemoryType(getMemoryType().toString());
			engine.setGcubeScope(ScopeProvider.instance.get());
			engine.setOwnerGcube(owner);
			cfg.getOptionalPropertiesFromRR( getCurrentScope(), engine);
			if(getCurrentScope()!=null)
				ScopeProvider.instance.set(getCurrentScope());
			return engine;
	}

	
	private void setClientId(String serviceClass, String serviceName, String id) {
		if(typeAccess == AccessType.PUBLIC){
			clientID="";
		}else if(typeAccess == AccessType.PRIVATE){
			clientID=ServiceEngine.FILE_SEPARATOR+"home"+ServiceEngine.FILE_SEPARATOR+serviceClass+ServiceEngine.FILE_SEPARATOR+serviceName+ServiceEngine.FILE_SEPARATOR+id;
		}else if(typeAccess==AccessType.SHARED){
			clientID=ServiceEngine.FILE_SEPARATOR+"home"+ServiceEngine.FILE_SEPARATOR+serviceClass+ServiceEngine.FILE_SEPARATOR+serviceName;
		}else{
			throw new IllegalArgumentException("type is not correctly: public, private or shared");
		}
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
		}else setCurrentScope(scopeProvided);
	}



	public String getClientID() {
		return clientID;
	}



	public void setClientID(String clientID) {
		this.clientID = clientID;
	}



	public AccessType getTypeAccess() {
		return typeAccess;
	}



	public void setTypeAccess(AccessType typeAccess) {
		this.typeAccess = typeAccess;
	}



	public MemoryType getMemoryType() {
		return memoryType;
	}



	public void setMemoryType(MemoryType memoryType) {
		this.memoryType = memoryType;
	}



	public String getServiceClass() {
		return serviceClass;
	}



	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}



	public String getServiceName() {
		return serviceName;
	}



	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
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



	public String getScopeString() {
		return scopeString;
	}



	public void setScopeString(String scopeString) {
		this.scopeString = scopeString;
	}



	public String getCurrentScope() {
		return currentScope;
	}



	public void setCurrentScope(String currentScope) {
		this.currentScope = currentScope;
	}



	public String getBackendType() {
		return backendType;
	}



	public void setBackendType(String backendType) {
		this.backendType = backendType;
	}



	public String getVolatileHost() {
		return volatileHost;
	}



	public void setVolatileHost(String volatileHost) {
		this.volatileHost = volatileHost;
	}



	public String getRRScope() {
		return RRScope;
	}



	public void setRRScope(String rRScope) {
		RRScope = rRScope;
	}
	
	
}