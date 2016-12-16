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
	
//	
//	/**
//	 * Constructor without optional argument created for gcube infrastructure internal use
//	 * @param ServiceClass 
//	 * @param ServiceName
//	 * @param owner
//	 * @param typeAccess
//	 * @param scope
//	 */
//	@Deprecated
//	public StorageClient(String serviceClass, String serviceName, String owner, AccessType accessType, String scope){
//		checkScopeProvider();
//		this.currentScope=ScopeProvider.instance.get();
//		ScopeProvider.instance.set(scope);
//		String id=owner;
//		this.owner=owner;
//		this.scopeString=ScopeProvider.instance.get();
//		if(accessType!=null)
//			this.typeAccess=accessType;
//		else throw new RuntimeException("AccessType parameter must be not null");
//		this.memoryType=MemoryType.BOTH;
//		this.serviceClass=serviceClass;
//		this.serviceName=serviceName;
//		setClientId(serviceClass, serviceName, id);
//		
//	}

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
	
	

//	/**
//	 *  Constructor with optional argument server 
//	 * @param ServiceClass 
//	 * @param ServiceName
//	 * @param owner
//	 * @param typeAccess
//	 * @param scope
//	 * @param server: define the mongoDBserver
//	 */
//	@Deprecated
//	public StorageClient(String serviceClass, String serviceName, String owner, AccessType accessType, String scope, String server){
//		checkScopeProvider();
//		this.currentScope=ScopeProvider.instance.get();
//		ScopeProvider.instance.set(scope);
//		String id=owner;
//		this.owner=owner;
//		this.scopeString=ScopeProvider.instance.get();
//		if(accessType!=null)
//			this.typeAccess=accessType;
//		else throw new RuntimeException("AccessType parameter must be not null");
//		this.memoryType=MemoryType.BOTH;
//		this.serviceClass=serviceClass;
//		this.serviceName=serviceName;
//		this.server=server;
//		setClientId(serviceClass, serviceName, id);
//	}
//
//	
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
	
//	/**
//	 * Constructor with optional argument memoryType
//	 * @param ServiceClass 
//	 * @param ServiceName
//	 * @param owner
//	 * @param typeAccess
//	 * @param memory defines the kind of memory: VOLATILE or PERSISTENT
//	 * @param scope
//	 */
//	@Deprecated
//	public StorageClient(String serviceClass, String serviceName, String owner, AccessType accessType, String scope, MemoryType memory){
//		checkScopeProvider();
//		this.currentScope=ScopeProvider.instance.get();
//		ScopeProvider.instance.set(scope);
//		String id=owner;
//		this.owner=owner;
//		this.scopeString=ScopeProvider.instance.get();
//		if(accessType!=null)
//			this.typeAccess=accessType;
//		else throw new RuntimeException("AccessType parameter must be not null");
//		if(memoryType!=null)
//			this.memoryType=memory;
//		else throw new RuntimeException("MemoryType parameter must be not null");
//		this.serviceClass=serviceClass;
//		this.serviceName=serviceName;
//		setClientId(serviceClass, serviceName, id);
//	}


	
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

	
//	/**
//	 * Constructor with optional arguments server and memory 
//	 * @param ServiceClass 
//	 * @param ServiceName
//	 * @param owner
//	 * @param typeAccess
//	 * @param memory defines the kind of memory: VOLATILE or PERSISTENT
//	 * @param server: define the mongoDBserver
//	 * @param scope
//	 */
//	@Deprecated
//	public StorageClient(String serviceClass, String serviceName, String owner, AccessType accessType, String scope, String server, MemoryType memory){
//		checkScopeProvider();
//		this.currentScope=ScopeProvider.instance.get();
//		ScopeProvider.instance.set(scope);
//		String id=owner;
//		this.owner=owner;
//		this.scopeString=ScopeProvider.instance.get();
//		if(accessType!=null)
//			this.typeAccess=accessType;
//		else throw new RuntimeException("AccessType parameter must be not null");
//		if(memory!=null)
//			this.memoryType=memory;
//		else throw new RuntimeException("MemoryType parameter must be not null");
//		this.serviceClass=serviceClass;
//		this.serviceName=serviceName;
//		this.server=server;
//		setClientId(serviceClass, serviceName, id);
//	}
	
	
	
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
			engine.setServiceClass(serviceClass);
			engine.setServiceName(serviceName);
			engine.setGcubeAccessType(typeAccess.toString());
			engine.setBackendType(backendType);
			engine.setBackendUser(cfg.getUser());
			engine.setBackendPassword(cfg.getPassword());
			engine.setResolverHost(cfg.getResolverHost());
			if(cfg.getPassPhrase()!=null)
				engine.setPassPhrase(cfg.getPassPhrase());
			if(memoryType!=null)
				engine.setGcubeMemoryType(memoryType.toString());
			engine.setGcubeScope(ScopeProvider.instance.get());
			engine.setOwnerGcube(owner);
			cfg.readWRPropertiesFromRR( currentScope, engine);
			if(currentScope!=null)
				ScopeProvider.instance.set(currentScope);
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
		}
	}
}