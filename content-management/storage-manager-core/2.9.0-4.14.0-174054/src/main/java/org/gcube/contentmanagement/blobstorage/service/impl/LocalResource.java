package org.gcube.contentmanagement.blobstorage.service.impl;

import java.io.InputStream;
import java.io.OutputStream;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.LOCAL_RESOURCE;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;


/**
 * Defines the operations for selecting a local resource.
 * ex. a local path for a download operation, or a inputStream
 *   
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class LocalResource extends Resource{

	
	public LocalResource(MyFile file, ServiceEngine engine) {
		super(file, engine);
	}

	/**
	 * define local resource
	 * @param path : local absolute path of resource
	 * @return remoteResource object
	 */
	public RemoteResource LFile(String path){
		if(getMyFile() != null){
			getMyFile().setLocalPath(path);
		}else{
			setMyFile(setGenericProperties("", "", path, "local"));
			getMyFile().setLocalPath(path);
		}
		getMyFile().setLocalResource(LOCAL_RESOURCE.PATH);
		return new RemoteResource(getMyFile(), getEngine());
	}

	/**
	 * define local resource
	 * @param is : inputStream of resource
	 * @return remoteResource object
	 */
	public RemoteResource LFile(InputStream is) {
		if(getMyFile() != null){
			getMyFile().setInputStream(is);
		}else{
			setMyFile(new MyFile(engine.getGcubeMemoryType()));
			getMyFile().setInputStream(is);
		}
		getMyFile().setLocalResource(LOCAL_RESOURCE.INPUT_STREAM);
		return new RemoteResource(getMyFile(), getEngine());
	}
	
	/**
	 * define local resource
	 * @param os output stream of resource
	 * @return remoteResource object
	 */
	public RemoteResource LFile(OutputStream os) {
		if(getMyFile() != null){
			getMyFile().setOutputStream(os);
		}else{
			setMyFile(new MyFile(engine.getGcubeMemoryType()));
			getMyFile().setOutputStream(os);
		}
		getMyFile().setLocalResource(LOCAL_RESOURCE.OUTPUT_STREAM);
		return new RemoteResource(getMyFile(), getEngine());
	}

	/**
	 * Method that returns an inputStream of a remote resource
	 * @param path remote path of remote resource
	 * @return inputStream of remote resource identifies by path argument
	 * 
	 */
	public InputStream RFileAsInputStream(String path){
		file = setGenericProperties(engine.getContext(), engine.owner, path, "remote");
		file.setRemotePath(path);
		file.setOwner(engine.owner);
		file.setType("input");
		file.setLocalResource(LOCAL_RESOURCE.VOID);
		file.setRemoteResource(REMOTE_RESOURCE.PATH_FOR_INPUT_STREAM);
		setMyFile(file);
		engine.service.setResource(getMyFile());
		getRemoteObject(file, engine.primaryBackend, engine.volatileBackend);
		InputStream is= file.getInputStream();
		file.setInputStream(null);
		return is;
	}

	
	
	
	/**
	 * Method that returns an inputStream of a remote resource
	 * @param path remote path of remote resource
	 * @return inputStream of remote resource identifies by path argument
	 * 
	 */
	@Deprecated
	public InputStream RFileAStream(String path){
		file = setGenericProperties(engine.getContext(), engine.owner, path, "remote");
		file.setRemotePath(path);
		file.setOwner(engine.owner);
		file.setType("input");
		file.setLocalResource(LOCAL_RESOURCE.VOID);
		file.setRemoteResource(REMOTE_RESOURCE.PATH_FOR_INPUT_STREAM);
		setMyFile(file);
		engine.service.setResource(getMyFile());
		getRemoteObject(file, engine.primaryBackend, engine.volatileBackend);
		InputStream is= file.getInputStream();
		file.setInputStream(null);
		return is;
	}
	
	/**
	 * Method that returns an outputStream of a remote resource, used for upload operation
	 * @param path remote path of remote resource
	 * @return outputStream of remote resource identifies by path argument
	 * 
	 */
	public OutputStream RFileAsOutputStream(String path){
		file = setGenericProperties(engine.getContext(), engine.owner, path, "remote");
		file.setRemotePath(path);
		file.setOwner(engine.owner);
		file.setType("output");
		file.setLocalResource(LOCAL_RESOURCE.VOID);
		file.setRemoteResource(REMOTE_RESOURCE.PATH_FOR_OUTPUTSTREAM);
		setMyFile(file);
		engine.service.setResource(getMyFile());
//		retrieveRemoteObject(engine.primaryBackend);
		getRemoteObject(file, engine.primaryBackend, engine.volatileBackend);
		OutputStream os=file.getOutputStream();
		file.setOutputStream(null);
		return os;
	}
}
