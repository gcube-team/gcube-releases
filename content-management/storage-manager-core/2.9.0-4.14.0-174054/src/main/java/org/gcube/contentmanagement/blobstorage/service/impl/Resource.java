package org.gcube.contentmanagement.blobstorage.service.impl;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * defines a common set of operations to identify a remote resource or a local resource
 * 
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class Resource {

	final Logger logger = LoggerFactory.getLogger(ServiceEngine.class);
	protected static final String BACKEND_STRING_SEPARATOR="%";
	protected MyFile file;
	protected ServiceEngine engine;
	
	
	public Resource(MyFile file, ServiceEngine engine){
		setMyFile(file);
		setEngine(engine);
	}
	
	protected ServiceEngine getEngine() {
		return engine;
	}

	protected void setEngine(ServiceEngine engine) {
		this.engine = engine;
	}

	protected MyFile getMyFile(){
		return file;
	}
	
	protected void setMyFile(MyFile f){
		if (f!=null)
			file=f;
		else
			logger.warn("instantiated an empty file object");
	}

	
	/**
	 * Set generic properties on MyFile object
	 * @param context remote root path
	 * @param owner file author
	 * @param path remote/local relative path
	 * @param type remote or local
	 * @return the current resource
	 */
	protected MyFile setGenericProperties(String context, String owner, String path, String type) {
		if((path != null) && (path.length()>0)){
			if(ObjectId.isValid(path)){
				if(file==null)
					file= new MyFile(path, engine.getGcubeMemoryType());
				String id = file.getId();
				if((id != null) && (!id.isEmpty()))
					file.setId2(path);
				else
					file.setId(path);
				file.setRootPath(context);
				file.setAbsoluteRemotePath(context);
			} else{
				String[] dirs= path.split(Costants.FILE_SEPARATOR);
				String name=dirs[dirs.length-1];
				if (logger.isDebugEnabled()) {
					logger.debug("path(String) - name: " + name);
				}
				if(file == null){
					file= new MyFile(name, engine.getGcubeMemoryType());
				}else{
					file.setName(name);
				}
				if(type.equalsIgnoreCase("remote") && (context!=null) && context.length()>0){
					file.setRootPath(context);
					path=new BucketCoding().bucketFileCoding(path, context);
					file.setAbsoluteRemotePath(path);
				}
				String dir=path.substring(0, (path.length()-name.length()));
				if (logger.isDebugEnabled()) {
					logger.debug("path(String) - path: " + dir);
				}
				if(type.equalsIgnoreCase("local")){
					if(file.getLocalDir()== null)
						file.setLocalDir(dir);
				}else{
					if(file.getRemoteDir()== null)
						file.setRemoteDir(dir);
				}

			}
			file.setOwner(owner);
		}else{
			file.setOwner(owner);
			file.setRootPath(context);
			file.setAbsoluteRemotePath(context);
		}
		
		return file;
	}
	
	protected Object getRemoteObject(MyFile file, String[] backend, String[] vltBackend)throws RemoteBackendException {
		Object obj=null;
		try{
			obj=retrieveRemoteObject(file, backend);
		}catch(RemoteBackendException e){
			logger.warn("Object not found on persistent area. ");
			if((obj == null) && (vltBackend !=null && vltBackend.length>0)){
				logger.warn("trying on the volatile area");
				obj=retrieveRemoteObject(file, vltBackend);
				logger.info("object found in volatile area "+obj);
			}
		}
		return obj;
	}
	
	protected Object retrieveRemoteObject(MyFile file, String[] backend) throws RemoteBackendException {
		Object obj=null;
			if(((file.getInputStream() != null) || (file.getOutputStream()!=null)) || ((file.getLocalPath() != null) || (file.getRemotePath() != null)))
				obj=engine.service.startOperation(file,file.getRemotePath(), file.getOwner(), backend, Costants.DEFAULT_CHUNK_OPTION, file.getRootPath(), file.isReplace());
			else{
				logger.error("parameters incompatible ");
			}
		return obj;
	}
	
	protected Object executeOperation(String path) {
		logger.info("file gCube parameter before: "+file.getGcubeAccessType()+" "+file.getGcubeScope());
		file = setGenericProperties(engine.getContext(), engine.owner, path, "remote");
		file.setRemotePath(path);
		file.setOwner(engine.owner);
		setMyFile(file);
		engine.service.setResource(getMyFile());
		Object obj=getRemoteObject(getMyFile(),engine.primaryBackend,engine.volatileBackend);
		return obj;
	}
}
