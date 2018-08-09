package org.gcube.contentmanagement.blobstorage.resource;

import java.io.InputStream;
import java.io.OutputStream;

import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.LOCAL_RESOURCE;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.OPERATION;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class that define a file entity object. This entity, contains file properties and metadata. 
 * This type of resource is builded by ServiceEngine class and used by the TransportManager for requests to the remote System
 * This class contains also the definition of the current operation:
 * @see org.gcube.contentmanagement.blobstorage.resource.OperationDefinition
 *
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class MyFile {
	
// file name	
	private String name;
// owner	
	private String owner;
// payload	Terrastore
	private byte[] content;
// local path	
	private String localPath;
// remote path	
	private String remotePath;
// absolute remote path
	private String absoluteRemotePath;
// num of chunks client side	Terrastore
	private int numChunks;
// name of the key in the remote bucket Terrastore
	private String key;
// local directory
	private String localDir;
// inputStream of the resource	
	private InputStream inputStream;
//outputStream of the resource
	private OutputStream outputStream;
//	type of stream
	private String type;
//if true the file is locked	
	private boolean lock;
// the key for unlocked	the file 
	private String lockedKey;

	private String remoteDir;
	
	private long lifeTime;
	
	private String id;
	
	private String id2;
	
	private long size;
	
	private String extension;
	
	private String creationTime;
	
	// parameters for GCube instance Url calculation

	private String serviceName;
	private String serviceClass;
	private String ownerGcube;
	private String gcubeScope;
	private AccessType gcubeAccessType;
	private MemoryType gcubeMemoryType;
	
	/**
	 * define the operation type on the current resource 
	 */
	private OperationDefinition operation;	
	private String resolverHost;
	private boolean forceCreation;
	private String mimeType;
	private String genericPropertyField;	
	private String genericPropertyValue;
	private String passPhrase;
	
	private String writeConcern;
	private String readPreference;
	private String rootPath;
	private boolean replace=false;
	public static final boolean DEFAULT_REPLACE_OPTION=false;
	final Logger logger = LoggerFactory.getLogger(MyFile.class);
	
	public MyFile(boolean lock){
		setLock(lock);
	}
	
	/**
	 * set some properties on the current resource
	 * @param author author name
	 * @param name name of the file
	 * @param pathClient local path of the file
	 */
	public MyFile(String author, String name, String pathClient, MemoryType memoryType){
		this.setOwner(author);
		this.setName(name);
		this.setLocalPath(pathClient);
		setGcubeMemoryType(memoryType);
	}

	/**
	 * set some properties on the current resource
	 * @param author author name
	 * @param name name of the file
	 * @param pathClient local path of the file
	 * @param pathServer remote path of the file
	 */

	public MyFile(String author, String name, String pathClient, String pathServer, MemoryType memoryType){
		this.setOwner(author);
		this.setName(name);
		this.setLocalPath(pathClient);
		this.setRemotePath(pathServer);
		setGcubeMemoryType(memoryType);
	}
	
	public MyFile(MemoryType memoryType) {
		setGcubeMemoryType(memoryType);
		
	}
	
	/**
	 * build a new object with only the name setted
	 * @param name file name
	 */
	public MyFile(String name, MemoryType memoryType){
		setName(name);
		setGcubeMemoryType(memoryType);
	}

	/**
	 * get number of chunks if the file is splitted in chunks
	 * @return number of chunks
	 */
	public int getNumChunks() {
		return numChunks;
	}

	/**
	 * set the number of file chunks. default is 1
	 * @param numChunks
	 */
	public void setNumChunks(int numChunks) {
		this.numChunks = numChunks;
	}	
	
	/**
	 * get the local path of the resource
	 * @return local path
	 */
	public String getLocalPath() {
		return localPath;
	}
	
	/**
	 * set the local path of the resource
	 * @param path the absolute path of the resource
	 */
	public void setLocalPath(String path) {
		this.localPath = path;
	}
	
	/**
	 * get the file name
	 * @return file name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * set the file name
	 * @param name file name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * get the file owner
	 * @return file owner
	 */
	public String getOwner() {
		return owner;
	}
	
	/**
	 * set the file owner
	 * @param author file owner
	 */
	public void setOwner(String author) {
		this.owner = author;
	}
	
	/**
	 * get the file payload or null
	 * 
	 * @return a byte array that contains the file payload
	 */
	public byte[] getContent() {
		return content;
	}
	
	/**
	 * set the payload file
	 * @param currentChunk payload file
	 */
	public void setContent(byte[] currentChunk) {
		this.content = currentChunk;
	}
	/**
	 * used only for chunk files. indicates the name of the current chunk
	 * @return the name of the current chunk
	 */
	public String getKey() {
		return key;
	}

	/**
	 * used only for chunk files. indicates the name of the current chunk
	 * @param key chunk name
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * returns a copy of the current resource
	 * @return the file copy
	 */
	public MyFile copyProperties(){
		MyFile dest=new MyFile(getGcubeMemoryType());
		dest.setOwner(getOwner());
		dest.setLocalDir(this.getLocalDir());
		dest.setRemoteDir(this.getRemoteDir());
		dest.setKey(this.key);
		dest.setName(this.name);
		dest.setNumChunks(this.numChunks);
		dest.setLocalPath(this.localPath);
		dest.setRemotePath(this.remotePath);
		return dest;
	}

	/**
	 * get the remote path of the resource
	 * @return remote path
	 */
	public String getRemotePath() {
		return remotePath;
	}

	/**
	 * set the remote path of the resource
	 * @param pathServer remote path
	 */
	public void setRemotePath(String pathServer) {
		this.remotePath = pathServer;
	}

	/**
	 * get the inputStream of the resource
	 * @return inputStream of the resource
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * set the inputStream of the resource
	 * @param inputStream inputStream of the resource
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * get the outputStream of the resource
	 * @return outputStream associated to the resource
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * set the outputStream of the resource
	 * @param outputStream outputstream associated to the resource
	 */
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	/**
	 * if the resource will be locked
	 * @return true if is lock
	 */
	public boolean isLock() {
		return lock;
	}

	/**
	 * set locking on the resource
	 * @param lock
	 */
	public void setLock(boolean lock) {
		this.lock = lock;
	}

	/**
	 * get the object type of the resource
	 * @return the class type of the resource
	 */
	public String getType() {
		return type;
	}

	/**
	 * set the object type of the resource
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * get the local direcotry where is the file 
	 * @return the local directory 
	 */
	public String getLocalDir() {
		return localDir;
	}

	/**
	 * set the local direcotry where is the file or the origin directory in case of move or copy operations
	 * @param localDir
	 */
	public void setLocalDir(String localDir) {
		this.localDir = localDir;
	}

	/**
	 * get the remote directory where the resource will be stored or the destination directory in case of copy, move operations
	 * @return the remote directory
	 */
	public String getRemoteDir() {
		return remoteDir;
	}

	/**
	 * set the remote directory where the resource will be stored
	 * @param remoteDir the remote directory
	 */
	public void setRemoteDir(String remoteDir) {
		this.remoteDir = remoteDir;
	}

	/**
	 * get the lock key or null
	 * @return the lock key
	 */
	public String getLockedKey() {
		return lockedKey;
	}

	/**
	 * set the lock key
	 * @param lockedKey lock key
	 */
	public void setLockedKey(String lockedKey) {
		this.lockedKey = lockedKey;
	}

	/**
	 * get the serviceName associated to the resource. This is need for build the remote root path.
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * set the serviceName associated to the resource. This is need for build the remote root path.
	 * @param serviceName serviceName associated to the resource
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * get the serviceClass associated to the resource. This is need for build the remote root path. 
	 * @return service class
	 */
	public String getServiceClass() {
		return serviceClass;
	}

	/**
	 * set the serviceClass associated to the resource. This is need for build the remote root path. 
	 * @param serviceClass serviceClass associated to the resource
	 */
	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	/**
	 * get the file owner
	 * @return the file owner
	 */
	public String getOwnerGcube() {
		return ownerGcube;
	}

	/**
	 * set the file owner
	 * @param ownerGcube file owner
	 */
	public void setOwnerGcube(String ownerGcube) {
		this.ownerGcube = ownerGcube;
	}

	/**
	 * get gCube scope, is need for build the remote root path
	 * @return gcube scope string
	 */
	public String getGcubeScope() {
		return gcubeScope;
	}

	/**
	 * set the gCube scope
	 * @param gcubeScope gcube scope
	 */
	public void setGcubeScope(String gcubeScope) {
		this.gcubeScope = gcubeScope;
	}

	/**
	 * get the gcube accessType: PRIVATE, SHARED, PUBLIC
	 * @return gcube access type 
	 * 
	 */
	public AccessType getGcubeAccessType() {
		return gcubeAccessType;
	}

	/**
	 * set the gcube accessType: PRIVATE, SHARED, PUBLIC
	 * @param gcubeAccessType
	 */
	public void setGcubeAccessType(AccessType gcubeAccessType) {
		this.gcubeAccessType = gcubeAccessType;
	}
	
	/**
	 * get the gcube memoryType: PERSISTENT, VOLATILE
	 * @return the memory type
	 */
	public MemoryType getGcubeMemoryType() {
		return gcubeMemoryType;
	}

	/**
	 * set the gcube memoryType: PERSISTENT, VOLATILE
	 * @param gcubeMemoryType
	 */
	public void setGcubeMemoryType(MemoryType gcubeMemoryType) {
		this.gcubeMemoryType = gcubeMemoryType;
	}

	/**
	 * set the kind of operation
	 * @see org.gcube.contentmanagement.blobstorage.resource.OperationDefinition
	 * @param operation operation type
	 */
	public void setOperation(OperationDefinition operation) {
		this.operation = operation;
	}

	/**
	 * set the kind of operation
	 * @see org.gcube.contentmanagement.blobstorage.resource.OperationDefinition#setOperation(OPERATION)
	 * @param operation
	 */
	public void setOperation(OPERATION operation) {
		this.operation = new OperationDefinition(operation);
	}

	/**
	 * get the kind of operation
	 * @see org.gcube.contentmanagement.blobstorage.resource.OperationDefinition
	 * @return the operation definition on this resource
	 */
	public OperationDefinition getOperationDefinition(){
		return operation;
	}

	/**
	 * get the local resource identifier
	 * @see org.gcube.contentmanagement.blobstorage.resource.OperationDefinition#getLocalResource()
	 * @return the local Resource identifier
	 */
	public LOCAL_RESOURCE getLocalResource() {
		return operation.getLocalResource();
	}

	/**
     * set the local resource identifier
	 * @see org.gcube.contentmanagement.blobstorage.resource.OperationDefinition#setLocalResource(LOCAL_RESOURCE)	
	 * @param localResource local resource identifier
	 */
	public void setLocalResource(LOCAL_RESOURCE localResource) {
		if(operation==null)
			operation=new OperationDefinition(OPERATION.VOID);
		operation.setLocalResource(localResource);
	}

	/**
	 * get the remote resource identifier
	 * @see org.gcube.contentmanagement.blobstorage.resource.OperationDefinition#getRemoteResource()
	 * @return the remote Resource identifier
	 */
	public REMOTE_RESOURCE getRemoteResource() {
		return operation.getRemoteResource();
	}

	/**
     * set the remote resource identifier
	 * @see org.gcube.contentmanagement.blobstorage.resource.OperationDefinition#setRemoteResource(REMOTE_RESOURCE)	
	 * @param remoteResource local resource identifier	 */
	public void setRemoteResource(REMOTE_RESOURCE remoteResource) {
		if(operation==null)
			operation=new OperationDefinition(OPERATION.VOID);
		operation.setRemoteResource(remoteResource);
	}

	public String getAbsoluteRemotePath() {
		return absoluteRemotePath;
	}

	public void setAbsoluteRemotePath(String absoluteRemotePath) {
		this.absoluteRemotePath = absoluteRemotePath;
	}

	public long getLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(long lifeTime) {
		this.lifeTime = lifeTime;
	}

	public OperationDefinition getOperation() {
		return operation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public void setResolverHost(String resolverHost) {
		this.resolverHost=resolverHost;
		
	}

	public String getResolverHOst() {
		return resolverHost;
	}

	public void forceCreation(boolean forceCreation) {
		this.forceCreation=forceCreation;
		
	}
	
	public boolean isForceCreation(){
		return this.forceCreation;
	}

	public String getMimeType(){
		return this.mimeType;
	}
	
	public void setMimeType(String mime) {
		this.mimeType=mime;
		
	}

	public String getGenericPropertyField() {
		return genericPropertyField;
	}

	public void setGenericPropertyField(String genericPropertyField) {
		this.genericPropertyField = genericPropertyField;
	}

	public String getGenericPropertyValue() {
		return genericPropertyValue;
	}

	public void setGenericPropertyValue(String genericPropertyValue) {
		this.genericPropertyValue = genericPropertyValue;
	}

	public String getPassPhrase() {
		return passPhrase;
	}

	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}

	public String getWriteConcern() {
		return writeConcern;
	}

	public void setWriteConcern(String writeConcern) {
		this.writeConcern = writeConcern;
	}

	public String getReadPreference() {
		return readPreference;
	}

	public void setReadPreference(String readConcern) {
		this.readPreference = readConcern;
	}

	public void setRootPath(String rootPath) {
		this.rootPath=rootPath;
		
	}
	
	public String getRootPath(){
		return rootPath;
	}

	public void setReplaceOption(boolean replace) {
		this.replace=replace;
		
	}

	public boolean isReplace(){
		return replace;
	}
	
	public void print(){
		logger.info("\n Object: \n\t path: "+this.getRemotePath()+ "\n\t id: "+this.getId());
	}

	public String getId2() {
		return id2;
	}

	public void setId2(String id2) {
		this.id2 = id2;
	}
	
	
}
