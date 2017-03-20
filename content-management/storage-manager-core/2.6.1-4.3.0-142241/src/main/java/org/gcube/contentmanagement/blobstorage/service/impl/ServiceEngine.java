package org.gcube.contentmanagement.blobstorage.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.contentmanagement.blobstorage.resource.AccessType;
import org.gcube.contentmanagement.blobstorage.resource.MemoryType;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.OPERATION;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.Encrypter;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.Encrypter.EncryptionException;
import org.gcube.contentmanagement.blobstorage.service.operation.*;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Base64;
import org.bson.types.ObjectId;



/**
 * This is the client's engine, implements the IClient interface 
 * and  starts the build's operations for the transport manager
 * 
 * @author Roberto Cirillo (ISTI - CNR)
 */

public class ServiceEngine implements IClient {
	
	/**
	 * Logger for this class
	 */
	
	final Logger logger = LoggerFactory.getLogger(ServiceEngine.class);
	public static final String FILE_SEPARATOR = "/";
	public static final int CONNECTION_RETRY_THRESHOLD=5;
	public String[] primaryBackend;
	public String[] volatileBackend;
	protected OperationManager service;
	protected MyFile file;
	protected String bucket;
	protected String bucketID;
	protected String author;
	protected String owner;
// root Directory server side	
	protected String environment;
	protected String currentOperation;
	protected boolean replaceOption;
	public static final String DEFAULT_SCOPE = "private";
	public static final long TTL=180000;
	public static final boolean DEFAULT_CHUNK_OPTION=false;
	public static final int TTL_RENEW = 5;
	protected String publicArea;
	protected String homeArea;
	protected Map<String, String> attributes;
//identifies the scope : public, private or group
	private AccessType accessType;
// parameters for GCube instance
	private String serviceName;
	private String ownerGcube;
	private String gcubeScope;
	private AccessType gcubeAccessType;
	private MemoryType gcubeMemoryType;
	private String serviceClass;
// identifies the backend server type eg MongoDB, UStore	
	private String backendType;
// backend server username
	private String user;
//backend server password	
	private String password;
	private String passPhrase;
    private String resolverHost;
    private String DEFAULT_RESOLVER_HOST= "data.d4science.org";
    private String write;
    private String read;
    
	public ServiceEngine(String[] server){
		this.primaryBackend=server;
	}
	
	/**
	 * Constructor for version 2.0.0: The object stored from version 2.0.0 are incompatibily with previous version @ 
	 * @param server List of servers
	 * @param environment root directory in the remote storage
	 * @param accessType type of sharing: private, shared or public
	 * @param owner the owner of the file
	 * 
	 */
	public ServiceEngine(String[] server, String environment, String accessType, String owner){
		this.primaryBackend=server;
		setOwner(owner);
		this.setEnvironment(environment);
		setAccessType(accessType);
		if(accessType.equalsIgnoreCase("public"))
			this.setPublicArea(FILE_SEPARATOR+getEnvironment()+FILE_SEPARATOR+"public"+FILE_SEPARATOR);
		else
			this.setPublicArea(FILE_SEPARATOR+getEnvironment()+FILE_SEPARATOR);
		this.setHomeArea(FILE_SEPARATOR+getEnvironment()+FILE_SEPARATOR);//+"home"+FILE_SEPARATOR+owner+FILE_SEPARATOR);
	}

	
	
	public ServiceEngine(String[] server, String [] volatileBackend, String environment, String accessType, String owner, String memory){
		if(memory.equals(MemoryType.VOLATILE.toString())&& (volatileBackend[0]!=null)){
			this.primaryBackend=volatileBackend;
		}else if(memory.equals(MemoryType.VOLATILE.toString())&& (volatileBackend[0]==null)){
			this.primaryBackend=server;
			this.volatileBackend=null;
		}else{
			this.primaryBackend=server;
			this.volatileBackend=volatileBackend;
		}
		setOwner(owner);
		this.setEnvironment(environment);
		setAccessType(accessType);
		if(accessType.equalsIgnoreCase("public"))
			this.setPublicArea(FILE_SEPARATOR+getEnvironment()+FILE_SEPARATOR+"public"+FILE_SEPARATOR);
		else
			this.setPublicArea(FILE_SEPARATOR+getEnvironment()+FILE_SEPARATOR);
		this.setHomeArea(FILE_SEPARATOR+getEnvironment()+FILE_SEPARATOR);//+"home"+FILE_SEPARATOR+owner+FILE_SEPARATOR);
	}
	
	
	/**
	 * Constructor for version < 2.0.0
	 * @param server backend server list
	 * @param id complete client id
	 * @param environment scope and root directory in the cluster
	 */
	@Deprecated
	public ServiceEngine(String[] server, String id, String environment, String scope, String owner){
		this.primaryBackend=server;
		setOwner(owner);
		this.setEnvironment(environment);
		setAccessType(scope);
		this.setPublicArea(FILE_SEPARATOR+environment);
		this.setHomeArea(FILE_SEPARATOR+environment);
	}
	
	
	public String getPublicArea() {
		return publicArea;
	}

	public void setPublicArea(String publicArea) {
		logger.trace("public area is "+publicArea);
		this.publicArea = publicArea;
	}

	public String getHomeArea() {
		return homeArea;
	}

	public void setHomeArea(String rootPath) {
		this.homeArea = rootPath;
	}

	public String getEnvironment() {
		return environment;
	}

	/**
	 * set the remote root path
	 * @param environment
	 */
	public void setEnvironment(String environment) {
//		delete initial / from variable environment
		String newEnv=environment;
		int ind=newEnv.indexOf('/');
		while(ind == 0){
			newEnv=environment.substring(1);
			environment=newEnv;
			ind=newEnv.indexOf('/');
		}
		this.environment = newEnv;
	}

	public String getBucketID() {
		return bucketID;
	}

	public void setBucketID(String bucketID) {
		this.bucketID=bucketID;
		
	}

	/** 
	 * download operation
	 * (non-Javadoc)
	 */
	@Override
	public LocalResource get(){
		return get(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}

	/**
	 * 
	 * @param backendType if specified it identifies the type of backend servers. eg. MongoDB, Ustore
	 * @return
	 */
	@Override
	public LocalResource get(String backendType){
		file=null;
		backendType=setBackendType(backendType);
		if (logger.isDebugEnabled()) {
			logger.debug("get() - start");
		}
		setCurrentOperation("download");
		this.service=new OperationManager(primaryBackend, user, password, getCurrentOperation(), file, backendType);
		file=setOperationInfo(file, OPERATION.DOWNLOAD);
		return new LocalResource(file, this);	
	}

	
	/**
	 * getSize operation: return the size of a remote file
	 */
	@Override
	public RemoteResourceInfo getSize(){
		return getSize(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}
	
	/**
	 * @param backendType if specified it identifies the type of backend servers. eg. MongoDB, Ustore
	 */
	@Override
	public RemoteResourceInfo getSize(String backendType){
		file=null;
		backendType=setBackendType(backendType);
		if (logger.isDebugEnabled()) {
			logger.debug("get() - start");
		}
		setCurrentOperation("getSize");
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		file=setOperationInfo(file, OPERATION.GET_SIZE);
		return new RemoteResourceInfo(file, this);	
	}

	
	@Override
	public RemoteResourceComplexInfo getMetaFile(){
		file=null;
		if (logger.isDebugEnabled()) {
			logger.debug("get() - start");
		}
		setCurrentOperation("getMetaFile");
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		file=setOperationInfo(file, OPERATION.GET_META_FILE);
		return new RemoteResourceComplexInfo(file, this);	
	}
	
	public String getTotalUserVolume(){
		file=null;
		if (logger.isDebugEnabled()) {
			logger.debug("get() - start");
		}
		setCurrentOperation("getTotalUserVolume");
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		file=setOperationInfo(file, OPERATION.GET_TOTAL_USER_VOLUME);
		file = new Resource(file, this).setGenericProperties(getContext(), owner, null, "remote");
		file.setRemotePath("/");
		file.setOwner(owner);
		getMyFile().setRemoteResource(REMOTE_RESOURCE.PATH);
		setMyFile(file);
		service.setResource(getMyFile());
		Object info=null;
		try {
			if(((file.getInputStream() != null) || (file.getOutputStream()!=null)) || ((file.getLocalPath() != null) || (file.getRemotePath() != null)))
				info=(String)service.startOperation(file,file.getRemotePath(), owner, primaryBackend, DEFAULT_CHUNK_OPTION, getContext(), isReplaceOption());
			else{
				logger.error("parameters incompatible ");
			}

		} catch (Throwable t) {
			logger.error("get()", t.getCause());
			throw new RemoteBackendException(" Error in "+currentOperation+" operation ", t.getCause());
		}
		if(info!=null)
			return info.toString();
		else
			return null;
	}

	public String getUserTotalItems(){
		file=null;
		if (logger.isDebugEnabled()) {
			logger.debug("get() - start");
		}
		setCurrentOperation("getTotalUserItems");
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		file=setOperationInfo(file, OPERATION.GET_USER_TOTAL_ITEMS);
		file = new Resource(file, this).setGenericProperties(getContext(), owner, "", "remote");
		file.setRemotePath("/");
		file.setOwner(owner);
		getMyFile().setRemoteResource(REMOTE_RESOURCE.PATH);
		setMyFile(file);
		service.setResource(getMyFile());
		Object info=null;
		try {
			if(((file.getInputStream() != null) || (file.getOutputStream()!=null)) || ((file.getLocalPath() != null) || (file.getRemotePath() != null)))
				info=(String)service.startOperation(file,file.getRemotePath(), owner, primaryBackend, DEFAULT_CHUNK_OPTION, getContext(), isReplaceOption());
			else{
				logger.error("parameters incompatible ");
			}

		} catch (Throwable t) {
			logger.error("get()", t.getCause());
			throw new RemoteBackendException(" Error in "+currentOperation+" operation ", t.getCause());
		}
		if(info!=null)
			return info.toString();
		else
			return null;
	}

	public RemoteResourceFolderInfo getFolderTotalVolume(){
		file=null;
		if (logger.isDebugEnabled()) {
			logger.debug("get() - start");
		}
		setCurrentOperation("getFolderSize");
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		file=setOperationInfo(file, OPERATION.GET_FOLDER_TOTAL_VOLUME);
		return new RemoteResourceFolderInfo(file, this);	
	}

	public RemoteResourceFolderInfo getFolderTotalItems(){
		file=null;
		if (logger.isDebugEnabled()) {
			logger.debug("get() - start");
		}
		setCurrentOperation("getFolderCount");
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		file=setOperationInfo(file, OPERATION.GET_FOLDER_TOTAL_ITEMS);
		return new RemoteResourceFolderInfo(file, this);	
	}

	public RemoteResourceFolderInfo getFolderLastUpdate(){
		file=null;
		if (logger.isDebugEnabled()) {
			logger.debug("get() - start");
		}
		setCurrentOperation("getFolderLastUpdate");
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		file=setOperationInfo(file, OPERATION.GET_FOLDER_LAST_UPDATE);
		return new RemoteResourceFolderInfo(file, this);	
	}
	

	/** 
	 * upload operation
 	 * path nome bucket
 	 * @param replace true if the remote file will be replaced
 	 * @return LocalResource object
	 */
	@Override
	public LocalResource put(boolean replace){
		file=null;
		if (logger.isDebugEnabled()) {
			logger.debug("put() - start");
		}
		setCurrentOperation("upload");
		setReplaceOption(replace);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), getMyFile(), backendType);
		file=setOperationInfo(file, OPERATION.UPLOAD);
		file.setReplaceOption(replace);
		return new LocalResource(file, this);
	}
	
	
	/** 
	 * upload operation
 	 * path nome bucket
 	 * @param replace true if the remote file will be replaced
 	 * @param mimeType: the file mimeType
 	 * @return LocalResource object
	 */
	@Override
	public LocalResource put(boolean replace, String mimeType){
		file=null;
		if (logger.isDebugEnabled()) {
			logger.debug("put() - start");
		}
		setCurrentOperation("upload");
		setReplaceOption(replace);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), getMyFile(), backendType);
		file=setOperationInfo(file, OPERATION.UPLOAD);
		file=setMimeType(file, mimeType);
		file.setReplaceOption(replace);
		return new LocalResource(file, this);
	}

	/** 
	 * remove operation
	 * @return RemoteResource object
	 */
	@Override
	public RemoteResource remove(){
		return remove(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}

	
	@Override
	public RemoteResource remove(String backendType){
		backendType=setBackendType(backendType);
		file=new MyFile(getGcubeMemoryType());
		file.setGcubeAccessType(this.getGcubeAccessType());
		file.setGcubeScope(this.getGcubeScope());
		file.setOwnerGcube(this.getOwnerGcube());
		file.setServiceName(this.getServiceName());
		file.setServiceClass(this.getServiceClass());
			// remove object operation		
		setCurrentOperation("remove");
		file=setOperationInfo(file, OPERATION.REMOVE);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		return new RemoteResource(file, this);
	}

	
	public MyFile getMyFile() {
		return file;
	}

	public void setMyFile(MyFile myFile) {
		this.file = myFile;
	}
	
	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	/**
	 * show the content of the remote directory
	 */
	@Override
	public RemoteResource showDir(){
		return showDir(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}


	/**
	 * show the content of the remote directory
	 */
	@Override
	public RemoteResource showDir(String backendType){
		backendType=setBackendType(backendType);
		file=new MyFile(this.getGcubeMemoryType());
		file.setGcubeAccessType(this.getGcubeAccessType());
		file.setGcubeScope(this.getGcubeScope());
		file.setOwnerGcube(this.getOwnerGcube());
		file.setServiceName(this.getServiceName());
		file.setServiceClass(this.getServiceClass());
		setCurrentOperation("showDir");
		file=setOperationInfo(file, OPERATION.SHOW_DIR);
		return new RemoteResource(file, this);
	}

	
	
	/**
	 * @param mapDirs
	 * @return
	 */
	List<StorageObject> addObjectsDirBucket(Map<String, StorageObject> mapDirs) {
		List<StorageObject> dirs;
		Set<String> dirsKeys=mapDirs.keySet();
		dirs= new ArrayList<StorageObject> (dirsKeys.size());
		for(java.util.Iterator<String> it=dirsKeys.iterator();it.hasNext(); ){
			String key=it.next();
			key =new BucketCoding().bucketDirDecoding(key, getContext());
			logger.debug("add "+key);
			dirs.add(mapDirs.get(key));
		}
		return dirs;
	}
	
	@Override
	public RemoteResource removeDir(){
		return removeDir(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}
	
	@Override
	public RemoteResource removeDir(String backendType){
		backendType=setBackendType(backendType);
		file=new MyFile(this.getGcubeMemoryType());
		file.setGcubeAccessType(this.getGcubeAccessType());
		file.setGcubeScope(this.getGcubeScope());
		file.setOwnerGcube(this.getOwnerGcube());
		file.setServiceName(this.getServiceName());
		file.setServiceClass(this.getServiceClass());
		setCurrentOperation("removedir");
		file=setOperationInfo(file, OPERATION.REMOVE_DIR);
		return new RemoteResource(file, this);
	}

	
	@Override
	public RemoteResource getUrl(){
		return getUrl(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}
	
	@Override
	public RemoteResource getUrl(boolean forceCreation){
		return getUrl(TransportManager.DEFAULT_TRANSPORT_MANAGER, forceCreation);
	}
	

	@Override
	public RemoteResource getUrl(String backendType){
		return getUrl(backendType, false);
	}
	
	
	@Override
	public RemoteResource getUrl(String backendType, boolean forceCreation){
		backendType=setBackendType(backendType);
		file=new MyFile(this.getGcubeMemoryType());
		file.setGcubeAccessType(this.getGcubeAccessType());
		file.setGcubeScope(this.getGcubeScope());
		file.setOwnerGcube(this.getOwnerGcube());
		file.setServiceName(this.getServiceName());
		file.setServiceClass(this.getServiceClass());
		file.setResolverHost(getResolverHost());
		file.forceCreation(forceCreation);
		file.setPassPhrase(passPhrase);
		setCurrentOperation("getUrl");
		file=setOperationInfo(file, OPERATION.GET_URL);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		RemoteResource resource=new RemoteResource(file, this);
		return resource;
	}
	
	@Override
	public RemoteResource getHttpUrl(){
		return getHttpUrl(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}
	
	@Override
	public RemoteResource getHttpUrl(boolean forceCreation){
		return getHttpUrl(TransportManager.DEFAULT_TRANSPORT_MANAGER, forceCreation);
	}
	
	@Override
	public RemoteResource getHttpUrl(String backendType){
		return getHttpUrl(backendType, false);
	}
	
	
	@Override
	public RemoteResource getHttpUrl(String backendType, boolean forceCreation){
		backendType=setBackendType(backendType);
		file=new MyFile(this.getGcubeMemoryType());
		file.setGcubeAccessType(this.getGcubeAccessType());
		file.setGcubeScope(this.getGcubeScope());
		file.setOwnerGcube(this.getOwnerGcube());
		file.setServiceName(this.getServiceName());
		file.setServiceClass(this.getServiceClass());
		file.setResolverHost(getResolverHost());
		file.forceCreation(forceCreation);
		file.setPassPhrase(passPhrase);
		setCurrentOperation("getHttpUrl");
		file=setOperationInfo(file, OPERATION.GET_HTTP_URL);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		RemoteResource resource=new RemoteResource(file, this);
		return resource;
	}
	
	/*HTTPS URL BEGIN*/
	
	@Override
	public RemoteResource getHttpsUrl(){
		return getHttpsUrl(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}
	
	@Override
	public RemoteResource getHttpsUrl(boolean forceCreation){
		return getHttpsUrl(TransportManager.DEFAULT_TRANSPORT_MANAGER, forceCreation);
	}
	
	@Override
	public RemoteResource getHttpsUrl(String backendType){
		return getHttpsUrl(backendType, false);
	}
	
	
	@Override
	public RemoteResource getHttpsUrl(String backendType, boolean forceCreation){
		backendType=setBackendType(backendType);
		file=new MyFile(this.getGcubeMemoryType());
		file.setGcubeAccessType(this.getGcubeAccessType());
		file.setGcubeScope(this.getGcubeScope());
		file.setOwnerGcube(this.getOwnerGcube());
		file.setServiceName(this.getServiceName());
		file.setServiceClass(this.getServiceClass());
		file.setResolverHost(getResolverHost());
		file.forceCreation(forceCreation);
		file.setPassPhrase(passPhrase);
		setCurrentOperation("getHttpsUrl");
		file=setOperationInfo(file, OPERATION.GET_HTTPS_URL);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		RemoteResource resource=new RemoteResource(file, this);
		return resource;
	}	
	
	/*HTTPS URL END*/
	


	/**
	 * 
	 * @return private o public
	 */
	public String getContext(){
		if(isPublic()){
			return getPublicArea();
		}
		return getHomeArea();
	}


	public boolean isPublic(){
		if(getScope()!=null)
			return getScope().equalsIgnoreCase("public");
		return DEFAULT_SCOPE.equalsIgnoreCase("public");
	}

	public String getScope() {
		return accessType.toString();
	}

//	public void setScope(String scope) {
//		if(scope.equalsIgnoreCase("public") || scope.equalsIgnoreCase("private") || scope.equalsIgnoreCase("group"))
//			this.accessType = scope;
//		else
//			throw new IllegalArgumentException("bad scope usage: public | group | private ");
//	}

	
	public String getCurrentOperation() {
		return currentOperation;
	}

	public void setCurrentOperation(String currentOperation) {
		this.currentOperation = currentOperation;
	}
	
	public boolean isReplaceOption() {
		return replaceOption;
	}
	
	public void setReplaceOption(boolean replaceOption) {
		this.replaceOption = replaceOption;
	}
	

	public AmbiguousResource lock() {
		return lock(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}

	public AmbiguousResource lock(String backendType) {
		backendType=setBackendType(backendType);
		file = new MyFile(true);
		setCurrentOperation("lock");
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		file=setOperationInfo(file, OPERATION.LOCK);
		return new AmbiguousResource(file, this);	
	}

	
	@Override
	public AmbiguousResource unlock(String key) {
		return unlock(key, TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}

	@Override
	public AmbiguousResource unlock(String key, String backendType) {
		backendType=setBackendType(backendType);
		file=new MyFile(this.getGcubeMemoryType());
		file.setLockedKey(key);
//		put(true);
		setCurrentOperation("unlock");
		file=setOperationInfo(file, OPERATION.UNLOCK);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		return new AmbiguousResource(file, this);
	}

	
	@Override
	public RemoteResourceInfo getTTL() {
		return getTTL(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}

	@Override
	public RemoteResourceInfo getTTL(String backendType) {
		backendType=setBackendType(backendType);
		file=new MyFile(this.getGcubeMemoryType());
//		put(true);
		setCurrentOperation("getTTL");
		file=setOperationInfo(file, OPERATION.GET_TTL);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		return new RemoteResourceInfo(file, this);
	}


	@Override
	public RemoteResource getMetaInfo(String field) {
		return getMetaInfo(field, TransportManager.DEFAULT_TRANSPORT_MANAGER);
		
	}
	
	@Override
	public RemoteResource getMetaInfo(String field, String backendType) {
		backendType=setBackendType(backendType);
		file=new MyFile(this.getGcubeMemoryType());
		file.setGenericPropertyField(field);
		setCurrentOperation("getMetaInfo");
		file=setOperationInfo(file, OPERATION.GET_META_INFO);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		return new RemoteResource(file, this);
	}

	@Override
	public RemoteResource setMetaInfo(String field, String value) {
		return setMetaInfo(field, value, TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}

	@Override
	public RemoteResource setMetaInfo(String field, String value, String backendType) {
		backendType=setBackendType(backendType);
		file=new MyFile(this.getGcubeMemoryType());
		file.setGenericPropertyField(field);
		file.setGenericPropertyValue(value);
		setCurrentOperation("setMetaInfo");
		file=setOperationInfo(file, OPERATION.SET_META_INFO);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		return new RemoteResource(file, this);
	}
	
	@Override
	public RemoteResourceInfo renewTTL(String key) {
		return renewTTL(key, TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}

	@Override
	public RemoteResourceInfo renewTTL(String key, String backendType) {
		backendType=setBackendType(backendType);
		file=new MyFile(this.getGcubeMemoryType());
		file.setLockedKey(key);
//		put(true);
		setCurrentOperation("renewTTL");
		file=setOperationInfo(file, OPERATION.RENEW_TTL);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		return new RemoteResourceInfo(file, this);
	}

	
	
	@Override
	public RemoteResourceSource linkFile() {
		return linkFile(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}

	@Override
	public RemoteResourceSource linkFile(String backendType) {
		backendType=setBackendType(backendType);
		file=null;
		if (logger.isDebugEnabled()) {
			logger.debug("put() - start");
		}
		setCurrentOperation("link");
		file=setOperationInfo(file, OPERATION.LINK);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), getMyFile(), backendType);
		return new RemoteResourceSource(file, this);
	}

	
	
	@Override
	public RemoteResourceSource copyFile() {
		return copyFile(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}


	@Override
	public RemoteResourceSource copyFile(String backendType) {
		backendType=setBackendType(backendType);
		file=null;
		if (logger.isDebugEnabled()) {
			logger.debug("put() - start");
		}
		setCurrentOperation("copy");
		file=setOperationInfo(file, OPERATION.COPY);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), getMyFile(), backendType);
		return new RemoteResourceSource(file, this);
	}
	
	@Override
	public RemoteResourceSource moveFile() {
		return moveFile(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}

	@Override
	public RemoteResourceSource moveFile(String backendType) {
		backendType=setBackendType(backendType);
		file=null;
		if (logger.isDebugEnabled()) {
			logger.debug("put() - start");
		}
		setCurrentOperation("move");
		file=setOperationInfo(file, OPERATION.MOVE);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), getMyFile(), backendType);
		return new RemoteResourceSource(file, this);
	}


	@Override
	public RemoteResourceSource copyDir() {
		return copyDir(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}


	@Override
	public RemoteResourceSource copyDir(String backendType) {
		backendType=setBackendType(backendType);
		file=null;
		if (logger.isDebugEnabled()) {
			logger.debug("put() - start");
		}
		setCurrentOperation("copy_dir");
		file=setOperationInfo(file, OPERATION.COPY_DIR);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), getMyFile(), backendType);
		return new RemoteResourceSource(file, this);
	}

	
	@Override
	public RemoteResourceSource moveDir() {
		return moveDir(TransportManager.DEFAULT_TRANSPORT_MANAGER);
	}

	
	@Override
	public RemoteResourceSource moveDir(String backendType) {
		backendType=setBackendType(backendType);
		file=null;
		if (logger.isDebugEnabled()) {
			logger.debug("put() - start");
		}
		setCurrentOperation("move_dir");
		file=setOperationInfo(file, OPERATION.MOVE_DIR);
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), getMyFile(), backendType);
		return new RemoteResourceSource(file, this);
	}
	
	@Override
	public void close(){
		currentOperation="close";
		file.setOwner(owner);
		getMyFile().setRemoteResource(REMOTE_RESOURCE.PATH);
		setMyFile(file);
		service.setResource(getMyFile());
		service.setTypeOperation("close");
		try {
			if(((file.getInputStream() != null) || (file.getOutputStream()!=null)) || ((file.getLocalPath() != null) || (file.getRemotePath() != null)))
				service.startOperation(file,file.getRemotePath(), owner, primaryBackend, DEFAULT_CHUNK_OPTION, getContext(), isReplaceOption());
			else{
				logger.error("parameters incompatible ");
			}

		} catch (Throwable t) {
			logger.error("get()", t.getCause());
			throw new RemoteBackendException(" Error in "+currentOperation+" operation ", t.getCause());
		}
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
	
	public String getOwnerGcube() {
		return ownerGcube;
	}
	
	public void setOwnerGcube(String ownerGcube) {
		this.ownerGcube = ownerGcube;
	}
	
	public String getGcubeScope() {
		return gcubeScope;
	}
	
	public void setGcubeScope(String gcubeScope) {
		this.gcubeScope = gcubeScope;
	}
	
	public AccessType getGcubeAccessType() {
		return gcubeAccessType;
	}
	
	public void setGcubeAccessType(String gcubeAccessType) {
		if(gcubeAccessType.equals(AccessType.PUBLIC.toString())){
			this.gcubeAccessType=AccessType.PUBLIC;
		}else if(gcubeAccessType.equals(AccessType.SHARED.toString())){
			this.gcubeAccessType=AccessType.SHARED;
		}else if(gcubeAccessType.equals(AccessType.PRIVATE.toString())){
//	the shared scope is a private scope. 
			this.gcubeAccessType=AccessType.PRIVATE;
		}else{
			throw new RuntimeException("invalid AccessType");
		}
//		this.gcubeAccessType = gcubeAccessType;
	}
	
	public MemoryType getGcubeMemoryType() {
		return gcubeMemoryType;
	}
	
	public void setGcubeMemoryType(String gcubeMemoryType) {
		if(gcubeMemoryType.equals(MemoryType.PERSISTENT.toString())){
			this.gcubeMemoryType=MemoryType.PERSISTENT;
		}else if(gcubeMemoryType.equals(MemoryType.VOLATILE.toString())){
			this.gcubeMemoryType=MemoryType.VOLATILE;
		}else if(gcubeMemoryType.equals(MemoryType.BOTH.toString())){
//	the shared scope is a private scope. 
			this.gcubeMemoryType=MemoryType.BOTH;
		}else{
			throw new RuntimeException("invalid MemoryType");
		}
//		this.gcubeMemoryType = gcubeMemoryType;
	}

	private MyFile setOperationInfo(MyFile file, OPERATION op) {
		if(file==null)
			file=new MyFile(this.getGcubeMemoryType());
		file.setOperation(op);
		if(getWriteConcern() != null)
			file.setWriteConcern(getWriteConcern());
		if(getReadConcern() != null)
			file.setReadPreference(getReadConcern());
		return file;
	}
	
	private MyFile setMimeType(MyFile file, String  mime) {
		if(file==null)
			file=new MyFile(this.getGcubeMemoryType());
		file.setMimeType(mime);
		return file;
	}

	public String setBackendType(String backendType) {
		if(backendType!=null)
			this.backendType=backendType; 
		return this.backendType;
	}
	
	public String getBackendType(){
		return backendType;
	}
	
	public String getBackendUser(){
		return this.user;
	}

	public void setBackendUser(String user) {
		if(user!=null)
			this.user=user;
		
	}
	
	public String getBackendPassword(){
		return this.password;
	}

	public void setBackendPassword(String password) {
		if(password != null)
			this.password=password;
		
	}

	public void setResolverHost(String resolverHost) {
		this.resolverHost=resolverHost;
		
	}
	
	public String getResolverHost(){
		if (resolverHost != null)
			return resolverHost;
		return DEFAULT_RESOLVER_HOST;
	}

	public String getPassPhrase() {
		return passPhrase;
	}

	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}

	
	public String getId(String id){
		if(ObjectId.isValid(id))
			return id;
		try {
			if(Base64.isBase64(id)){
				byte[] valueDecoded= Base64.decodeBase64(id);
				String encryptedID = new String(valueDecoded);
				return new Encrypter("DES", getPassPhrase()).decrypt(encryptedID);
			}else{
				return new Encrypter("DES", getPassPhrase()).decrypt(id);
			}
		} catch (EncryptionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public RemoteResource getRemotePath(){
		backendType=setBackendType(backendType);
		file=new MyFile(this.getGcubeMemoryType());
//		put(true);
		setCurrentOperation("getRemotePath");
		file=setOperationInfo(file, OPERATION.GET_REMOTE_PATH);
		file.setRootPath(this.getPublicArea());
		this.service=new OperationManager(primaryBackend, user, password,  getCurrentOperation(), file, backendType);
		return new RemoteResource(file, this);
	}

	public String getWriteConcern() {
		return write;
	}

	public void setWriteConcern(String write) {
		this.write = write;
	}

	public String getReadConcern() {
		return read;
	}

	public void setReadConcern(String read) {
		this.read = read;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		if (owner == null)
			throw new RuntimeException("The owner cannot be null");
		this.owner = owner;
	}
	
	private void setAccessType(String accessType) {
		if(accessType.equals(AccessType.PUBLIC.toString())){
			this.accessType=AccessType.PUBLIC;
		}else{
//	the shared scope is a private scope. 
			this.accessType=AccessType.PRIVATE;
		}
	}
	
}