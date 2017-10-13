package org.gcube.contentmanagement.blobstorage.service.impl;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MemoryType;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
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
		file=f;
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
				file.setId(path);
			} else if(type.equalsIgnoreCase("remote") && (context!=null) && context.length()>0){
				file.setRootPath(context);
				path=new BucketCoding().bucketFileCoding(path, context);
				file.setAbsoluteRemotePath(path);
			}
			String[] dirs= path.split(ServiceEngine.FILE_SEPARATOR);
			String name=dirs[dirs.length-1];
			if (logger.isDebugEnabled()) {
				logger.debug("path(String) - name: " + name);
			}
			if(file == null){
				file= new MyFile(name, engine.getGcubeMemoryType());
			}else{
				file.setName(name);
			}
			file.setOwner(owner);
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
		}else{
			file.setOwner(owner);
		}
		
		return file;
	}
	
	protected Object getRemoteObject(MyFile file, String[] backend, String[] vltBackend){
		Object obj=null;
		obj=retrieveRemoteObject(file, backend);
		if((obj == null) && (vltBackend !=null && vltBackend.length>0))
			obj=retrieveRemoteObject(file, vltBackend);
		return obj;
	}
	
	protected Object retrieveRemoteObject(MyFile file, String[] backend) {
		Object obj=null;
		try {
			if(((file.getInputStream() != null) || (file.getOutputStream()!=null)) || ((file.getLocalPath() != null) || (file.getRemotePath() != null)))
				obj=engine.service.startOperation(file,file.getRemotePath(), file.getOwner(), backend, ServiceEngine.DEFAULT_CHUNK_OPTION, file.getRootPath(), file.isReplace());
			else{
				logger.error("parameters incompatible ");
			}
		} catch (Exception e) {
			logger.error("get()", e);
		}
		return obj;
	}
}
