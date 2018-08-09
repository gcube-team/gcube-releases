package org.gcube.contentmanagement.blobstorage.resource;

/**
 * Defines the identity of a remote operation.
 * The enumerations: OPERATION, LOCAL_RESOURCE and REMOTE_RESOURCE, contains all you need to identify the kind of operation
 * ex:
 * 
 * 
 * if the operation is defined in this way:
 * 
 * 
 * OPERATION: UPLOAD;
 * LOCAL_RESOURCE: PATH;
 * REMOTE_RESOURCE: PATH;
 * 
 * 
 * It means that the client would be upload a file that have an absolute local path defined in pathClient field,
 * on the remote location identifies by pathServer field of the resource MyFile
 * @see org.gcube.contentmanagement.blobstorage.resource.MyFile
 * 
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class OperationDefinition {
	
	/**
	 * Indicates the type of current operation 
	 *
	 */
	public enum OPERATION {UPLOAD,DOWNLOAD, REMOVE, REMOVE_DIR, SHOW_DIR, GET_URL, UNLOCK, GET_TTL, RENEW_TTL, GET_SIZE, VOID, LOCK, COPY, COPY_DIR, LINK, MOVE, MOVE_DIR, GET_META_FILE, GET_TOTAL_USER_VOLUME, GET_USER_TOTAL_ITEMS, GET_FOLDER_TOTAL_VOLUME, GET_FOLDER_TOTAL_ITEMS, GET_FOLDER_LAST_UPDATE, CLOSE, GET_META_INFO, SET_META_INFO, GET_HTTP_URL, GET_HTTPS_URL, GET_REMOTE_PATH, EXIST, DUPLICATE, SOFT_COPY}
	
	
	/**
	 * Indicates how the local resource is identifies
	 *
	 */
	public enum LOCAL_RESOURCE {INPUT_STREAM, OUTPUT_STREAM, PATH, VOID, ID}
	
	/**
	 * Indicates how the remote resource is identifies
	 *
	 */
	public enum REMOTE_RESOURCE {INPUT_STREAM, OUTPUT_STREAM, PATH, VOID, PATH_FOR_INPUT_STREAM, PATH_FOR_OUTPUTSTREAM, ID, DIR}

	/**
	 * Indicates the type of current operation 
	 */
	private OPERATION operation;	
	
	/**
	 * Indicates how the local resource is identifies
	 */
	private LOCAL_RESOURCE localResource;

	/**
	 * Indicates how the remote resource is identifies
	 */
	private REMOTE_RESOURCE remoteResource;

	
	/**
	 * Set the complete operation definition
	 * @param op operation type
	 * @param lr local resource type
	 * @param rr remote resource type
	 */
	public OperationDefinition(OPERATION op, LOCAL_RESOURCE lr, REMOTE_RESOURCE rr){
		setOperation(op);
		setLocalResource(lr);
		setRemoteResource(rr);
		
	}

	/**
	 * Set the operation definition without specifies the loal resource and the remote resource
	 * @param op operation type
	 */
	public OperationDefinition(OPERATION op){
		setOperation(op);
		setLocalResource(LOCAL_RESOURCE.VOID);
		setRemoteResource(REMOTE_RESOURCE.VOID);
	}


	/**
	 * Get the operation type
	 * @return the operation type
	 */
	public OPERATION getOperation() {
		return operation;
	}

	/**
	 * set the operation type
	 * @param operation operation type
	 */
	public void setOperation(OPERATION operation) {
		this.operation = operation;
	}

	/**
	 * get the local resource type 
	 * @return the local resource type
	 */
	public LOCAL_RESOURCE getLocalResource() {
		return localResource;
	}

	/**
	 * set the local resource type
	 * @param localResource local resource type
	 */
	public void setLocalResource(LOCAL_RESOURCE localResource) {
		this.localResource = localResource;
	}

	/**
	 * get the remote resource type
	 * @return the remote resource type
	 */
	public REMOTE_RESOURCE getRemoteResource() {
		return remoteResource;
	}

	/**
	 * set the remote resource type
	 * @param remoteResource
	 */
	public void setRemoteResource(REMOTE_RESOURCE remoteResource) {
		this.remoteResource = remoteResource;
	}

	@Override
	public String toString() {
		return "OperationDefinition [operation=" + operation
				+ ", localResource=" + localResource + ", remoteResource="
				+ remoteResource + "]";
	}

}
