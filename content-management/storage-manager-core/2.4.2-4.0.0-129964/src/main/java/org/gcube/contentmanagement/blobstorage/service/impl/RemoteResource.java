package org.gcube.contentmanagement.blobstorage.service.impl;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.DirectoryBucket;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.DirectoryEntity;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.Encrypter;
import org.gcube.contentmanagement.blobstorage.service.operation.OperationManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;


/**
 * Defines the operations for selecting a remote resource.
 * ex. a remote path for a download operation. 
 * This selection is made for all types of operation 
 *   
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class RemoteResource extends Resource{
	
	private static final String BACKEND_STRING_SEPARATOR="%";

	public RemoteResource(MyFile file, ServiceEngine engine) {
		super(file, engine);
		logger.info("file gCube parameter costructor: "+file.getGcubeAccessType()+" "+file.getGcubeScope());
	}

/**
 * identify a remote resource by path (a file or a directory) 
 * @param path the remote path
 * @return remote resource id
 * @throws RemoteBackendException if there are runtime exception from the remote backend
 */
	public String RFile(String path) throws RemoteBackendException{
		logger.info("file gCube parameter before: "+file.getGcubeAccessType()+" "+file.getGcubeScope());
		file = setGenericProperties(engine.getContext(), engine.owner, path, "remote");
		file.setRemotePath(path);
		file.setOwner(engine.owner);
		getMyFile().setRemoteResource(REMOTE_RESOURCE.PATH);
		setMyFile(file);
		engine.service.setResource(getMyFile());
		String id=null;
		logger.info("file gCube parameter after: "+file.getGcubeAccessType()+" "+file.getGcubeScope());
		try {
			if(((file.getInputStream() != null) || (file.getOutputStream()!=null)) || ((file.getLocalPath() != null) || (file.getRemotePath() != null)))
				id=(String)engine.service.startOperation(file,file.getRemotePath(), engine.owner, engine.server, engine.DEFAULT_CHUNK_OPTION, engine.getContext(), engine.isReplaceOption());
			else{
				logger.error("parameters incompatible ");
			}
		} catch (Exception t) {
			logger.error("get()", t.getCause());
			t.printStackTrace();
			throw new RemoteBackendException(" Error in "+engine.currentOperation+" operation: "+t.getMessage(), t.getCause());
		}
		return id;
	}

	
	/**
	 * identify a remote resource by path (a file or a directory) 
	 * @param path the remote path
	 * @return remote resource id
	 * @throws RemoteBackendException if there are runtime exception from the remote backend
	 */
		public String RFile(String path, boolean backendTypeReturned) throws RemoteBackendException{
			logger.info("file gCube parameter before: "+file.getGcubeAccessType()+" "+file.getGcubeScope());
			file = setGenericProperties(engine.getContext(), engine.owner, path, "remote");
			file.setRemotePath(path);
			file.setOwner(engine.owner);
			getMyFile().setRemoteResource(REMOTE_RESOURCE.PATH);
			setMyFile(file);
			engine.service.setResource(getMyFile());
			String id=null;
			logger.info("file gCube parameter after: "+file.getGcubeAccessType()+" "+file.getGcubeScope());
			try {
				if(((file.getInputStream() != null) || (file.getOutputStream()!=null)) || ((file.getLocalPath() != null) || (file.getRemotePath() != null)))
					id=(String)engine.service.startOperation(file,file.getRemotePath(), engine.owner, engine.server, ServiceEngine.DEFAULT_CHUNK_OPTION, engine.getContext(), engine.isReplaceOption());
				else{
					logger.error("parameters incompatible ");
				}

			} catch (Throwable t) {
				logger.error("get()", t.getCause());
				throw new RemoteBackendException(" Error in "+engine.currentOperation+" operation: "+t.getMessage(), t.getCause());
			}
			if (backendTypeReturned)
				return id+BACKEND_STRING_SEPARATOR+engine.getBackendType();
			return id;
		}

	
/**
 * identify a remote resource by object id
 * @param id that identifies a remote resource
 * @return remote resource id
 * @throws RemoteBackendException if there are runtime exception from the remote backend
 * @deprecated this method could be replace with RFile method 
 */
	@Deprecated	
	public String RFileById(String id) throws RemoteBackendException{
		getMyFile().setRemoteResource(REMOTE_RESOURCE.ID);
		getMyFile().setOwner(engine.owner);
		engine.service.setResource(getMyFile());
		String idReturned=null;
		try {
			if(engine.getCurrentOperation().equalsIgnoreCase("getUrl") ||engine.getCurrentOperation().equalsIgnoreCase("upload") || engine.getCurrentOperation().equalsIgnoreCase("download") || engine.getCurrentOperation().equalsIgnoreCase("remove") || engine.getCurrentOperation().equalsIgnoreCase("unlock") || engine.getCurrentOperation().equalsIgnoreCase("lock"))
				idReturned=(String)engine.service.startOperation(getMyFile(), id, engine.owner, engine.server, ServiceEngine.DEFAULT_CHUNK_OPTION, "", engine.isReplaceOption());
			else 
				throw new IllegalArgumentException("Input Parameters incompatible");
		} catch (Throwable t) {
			logger.error("get()", t.getCause());
			throw new RemoteBackendException(" Error in "+engine.currentOperation+" operation: "+t.getMessage(), t.getCause());
		}
		return idReturned;
	}

	
	/**
	 * identify a remote resource by object id
	 * @param id that identifies a remote resource
	 * @return remote resource id
	 * @throws RemoteBackendException if there are runtime exception from the remote backend
	 * @deprecated this method could be replace with RFile method 
	 */
	@Deprecated	
	public String RFileById(String id, boolean backendTypeReturned) throws RemoteBackendException{
			getMyFile().setRemoteResource(REMOTE_RESOURCE.ID);
			getMyFile().setOwner(engine.owner);
			engine.service.setResource(getMyFile());
			String idReturned=null;
			try {
				if(engine.getCurrentOperation().equalsIgnoreCase("getUrl") || engine.getCurrentOperation().equalsIgnoreCase("upload") || engine.getCurrentOperation().equalsIgnoreCase("download") || engine.getCurrentOperation().equalsIgnoreCase("remove") || engine.getCurrentOperation().equalsIgnoreCase("unlock") || engine.getCurrentOperation().equalsIgnoreCase("lock"))
					idReturned=(String)engine.service.startOperation(getMyFile(), id, engine.owner, engine.server, ServiceEngine.DEFAULT_CHUNK_OPTION, "", engine.isReplaceOption());
				else
					throw new IllegalArgumentException("Input Parameters incompatible");
			} catch (Throwable t) {
				logger.error("get()", t.getCause());
				throw new RemoteBackendException(" Error in "+engine.currentOperation+" operation ", t.getCause());
			}
			if (backendTypeReturned)
				return id+BACKEND_STRING_SEPARATOR+engine.getBackendType();
			return idReturned;
		}

	
	
/**
 * Identify a remote folder by path
 * @param dir dir remote path
 * @return list of object contained in the remote dir
 */
   public List<StorageObject> RDir(String dir){
	  getMyFile().setRemoteResource(REMOTE_RESOURCE.DIR);
	  getMyFile().setOwner(engine.owner);
	  if(engine.getCurrentOperation().equalsIgnoreCase("showdir")){
		  dir = new BucketCoding().bucketDirCoding(dir, engine.getContext());
		  TransportManagerFactory tmf= new TransportManagerFactory(engine.server, engine.getBackendUser(), engine.getBackendPassword());
		  TransportManager tm=tmf.getTransport(engine.getBackendType());
		  Map<String, StorageObject> mapDirs=null;
		  try {
		    	mapDirs = tm.getValues(getMyFile(), dir, DirectoryEntity.class);
		  } catch (RemoteBackendException e) {
			e.printStackTrace();
		  }
		  List<StorageObject> dirs=null;
		  if(mapDirs!=null){
			 dirs = engine.addObjectsDirBucket(mapDirs);
	      }
		  if(dirs==null)
			dirs=Collections.emptyList();
		  return dirs;
	  }else if(engine.getCurrentOperation().equalsIgnoreCase("removedir")){
			if((dir != null) && (engine.owner != null)){
				DirectoryBucket dirBuc=new DirectoryBucket(engine.server, engine.getBackendUser(), engine.getBackendPassword(), dir, engine.owner);
				if(!OperationManager.CLIENT_TYPE.equalsIgnoreCase("mongo"))
					dirBuc.removeDirBucket(getMyFile(), dir, engine.getContext(), engine.getBackendType());
				else{
					TransportManagerFactory tmf=new TransportManagerFactory(engine.server, engine.getBackendUser(), engine.getBackendPassword());
					TransportManager tm=tmf.getTransport(OperationManager.CLIENT_TYPE);
					dir=new BucketCoding().bucketFileCoding(dir, engine.getContext());
					try {
						tm.removeDir(dir, getMyFile());
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (RemoteBackendException e) {
						e.printStackTrace();
					}
				}
			}else{
				logger.error("REMOVE Operation not valid:\n\t specify a valid bucketID or an author and a path on the cluster ");
	   	    }
		    return null;

	 }else{
		throw new IllegalArgumentException("The method RDir is not applicable for the operation selected");
	 }
   }
}
