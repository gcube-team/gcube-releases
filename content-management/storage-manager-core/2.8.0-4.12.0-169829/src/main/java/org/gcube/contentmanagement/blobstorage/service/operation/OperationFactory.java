package org.gcube.contentmanagement.blobstorage.service.operation;

import org.gcube.contentmanagement.blobstorage.transport.backend.operation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import terrastore.client.TerrastoreClient;

/**
 * 
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class OperationFactory {
	/**
	 * Logger for this class
	 */
//	private static final GCUBELog logger = new GCUBELog(OperationFactory.class);
	final Logger logger=LoggerFactory.getLogger(OperationFactory.class);
//	TerrastoreClient client;
	String[] server;
	String bucket;
	String user;
	String password;
	String[] dbNames;
	Monitor monitor;
	boolean isChunk;
	private String backendType;
	
	public OperationFactory(String server[], String user, String pwd, String bucket, Monitor monitor2, boolean isChunk, String backendType, String[] dbs){
		this.server=server;
		this.user=user;
		this.password=pwd;
		this.bucket=bucket;
		this.monitor=monitor2;
		this.isChunk=isChunk;
		this.backendType=backendType;
		this.dbNames=dbs;
	}
	
	public Operation getOperation(String operation){
		if (logger.isInfoEnabled()) {
			logger.info("getOperation(String) - start "+operation);
		}
		Operation op=null;
		if(operation.equalsIgnoreCase("upload")){
			op=new UploadOperator(server, user, password, bucket , monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("download")){
			op= new DownloadOperator(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("remove")){
			op=new Remove(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("getSize")){
			op=new GetSize(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("duplicate")){
			op=new DuplicateOperator(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("softcopy")){
			op=new SoftCopyOperator(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("getFolderSize")){
			op=new GetFolderSize(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("getFolderCount")){
			op=new GetFolderCount(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("getFolderLastUpdate")){
			op=new GetFolderLastUpdate(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("getTotalUserItems")){
			op=new GetUserTotalItems(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("getTotalUserVolume")){
			op=new GetUserTotalVolume(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("download+lock")){
			op=new DownloadAndLock(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("upload+unlock")){
			op=new UploadAndUnlock(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("lock")){
			op=new LockOperator(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("unlock")){
			op=new UnlockOperator(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("getTTL")){
			op=new GetTTL(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("renewTTL")){
			op=new RenewTTL(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("getUrl")){
			op=new GetUrl(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("getHttpUrl")){
			op=new GetHttpUrl(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("getHttpsUrl")){
			op=new GetHttpsUrl(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("link")){
			op=new LinkOperator(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("copy")){
			op=new SoftCopyOperator(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("move")){
			op=new MoveOperator(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("copy_dir")){
			op=new CopyDirOperator(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("move_dir")){
			op=new MoveDirOperator(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("getMetaFile")){
			op=new GetMetaFile(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("getMetaInfo")){
			op=new GetMetaInfo(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("setMetaInfo")){
			op=new SetMetaInfo(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("exist")){
			op=new Exist(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else if(operation.equalsIgnoreCase("getRemotePath")){
			op=new GetRemotePath(server, user, password,  bucket, monitor, isChunk, backendType, dbNames);
		}else{
			logger.error("getOperation(String) - Invalid Operation");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getOperation(String) - end");
		}
		return op;
	}

}
