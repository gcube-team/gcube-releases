package org.gcube.contentmanagement.blobstorage.transport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.blobstorage.resource.MemoryType;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.DirectoryEntity;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;

/**
 * The Transport Manager presents the methods for the connection to the remote system. This class should be instantiated for connection on remote backend
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public abstract class TransportManager {
	
	
	public static final String DEFAULT_TRANSPORT_MANAGER="MongoDB"; 
	
	/**
	 * This method specifies the type of the backend for dynamic loading
	 * For mongoDB, default backend, the name is MongoDB
	 * @return the backend name
	 */
	public abstract String getName();
	
	/**
	 * This method set initialize and configure the backend servers
	 * @param server array that contains ip of backend server
	 * @param pass 
	 * @param user 
	 */
	public abstract void initBackend(String[] server, String user, String pass, MemoryType memoryType); 
	
	
	/**
	 * Start the download operation. It contains logic to determine the correct operation based on the input parameters
	 * @param myFile object that contains the resource coordinates
	 * @param key remote path or objectId
	 * @param type class type of myFile object
	 * @return the key of remote resource
	 * @throws IOException if there are IO problems
	 */
	public String downloadManager(MyFile myFile, String key, Class <? extends Object> type) throws IOException{
		String key4lock=null;
		if(myFile.isLock()){
			key4lock=lock(myFile, key, type);
			return key4lock;
		}else{
			return get(myFile, key, type).toString();
		}
	}
	
	/**
	 * Start the upload operation. It contains logic to determine the correct operation based on the input parameters
	 * @param resource object that contains the resource coordinates
	 * @param bucket remote path or objectId
	 * @param key used only for chunk index operation
	 * @param replace if is true the file will be replaced
	 * @return the id of the remote resource
	 * @throws FileNotFoundException
	 * @throws UnknownHostException
	 */
	public String uploadManager(Object resource, String bucket, String key, boolean replace) throws FileNotFoundException, UnknownHostException{
		String id=null;
		MyFile file=(MyFile)resource;
		if((file.getLockedKey()!=null) && (!file.getLockedKey().isEmpty())){
			id=unlock(resource, bucket, key, file.getLockedKey());
		}else{
			id=put(resource, bucket, key, replace);
		}
		return id;
	}
	/**
	 * get a object from the cluster
	 * @param myFile object that contains the resource coordinates
	 * @param key identifies a server location object: 
	 *            in Terrastore correspond to a key, in Mongo correspond to a objectid or a remote path
	 * @param type class type definition for casting operation
	 * @return generic object that identifies a remote resource
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public abstract Object get(MyFile myFile, String key, Class <? extends Object> type) throws FileNotFoundException, IOException;
	
	
	/**
	 * put a object on the cluster
	 * @param resource object that contains the resource coordinates
	 * @param bucket remote path or objectId
	 * @param key used for chunk file index or for unlock operation
	 * @throws MongoException 
	 * @throws UnknownHostException 
	 */
	public abstract String put(Object resource, String bucket, String key, boolean replace) throws UnknownHostException;
	

	/**
	 * get all values contained in a remote bucket (or remote directory) 
	 * @param bucket remote path or objectId
	 * @param type class type of myFile object
	 * @return map that contains the object in the direcotry
	 * @throws UnknownHostException 
	 */
	public abstract Map<String, StorageObject> getValues(MyFile resource, String bucket, Class< ? extends Object> type);
	
	/**
	 * delete a remote file
	 * @param bucket identifies the remote file 
	 * @throws UnknownHostException 
	 */
	public abstract void removeRemoteFile(String bucket, MyFile resource) throws UnknownHostException;
	
	/**
	 * delete a remote directory
	 * @param remoteDir remote Directory path 
	 * @param myFile 
	 * @throws IllegalStateException 
	 * @throws UnknownHostException 
	 * 
	 */
	public abstract void removeDir(String remoteDir, MyFile myFile) throws UnknownHostException;
	

	/**
	 * get the size of the remote file
	 * @param bucket identifies the remote file path
	 * @return the size of the remote file
	 * @throws UnknownHostException
	 */
	public abstract long getSize(String bucket); 

	/**
	 * lock a remote file
	 * @param resource  object that contains the resource coordinates
	 * @param serverLocation remote path
	 * @param type class of resource
	 * @return the key that permits the object's unlock
	 * @throws IOException
	 */
	public abstract String lock(MyFile resource, String serverLocation,
			Class<? extends Object> type) throws IOException;

	/**
	 * unlock a remote file
	 * @param resource object that contains the resource coordinates
	 * @param bucket remote path
	 * @param key used only for chunk identifications
	 * @param key4unlock key used for unlock the remote file
	 * @return key for lock or null
	 * @throws FileNotFoundException
	 * @throws UnknownHostException
	 * @throws MongoException
	 */
	public abstract String unlock(Object resource, String bucket, String key,
			String key4unlock) throws FileNotFoundException,
			UnknownHostException, MongoException;

	/**
	 * returns the TTL associated with a remote file
	 * @param pathServer file remote path
	 * @return the time of ttl
	 * @throws UnknownHostException
	 */
	public abstract long getTTL(String pathServer) throws UnknownHostException;
	
	/**
	 * renew the TTL associated with a remote file
	 * @param resource
	 * @return the TTL time left
	 * @throws UnknownHostException
	 * @throws IllegalAccessException
	 */
	public abstract long renewTTL(MyFile resource) throws UnknownHostException, IllegalAccessException;
	
	
	/**
	 * link the destination resource to the source resource. In this operation the payload of the file is the same. The metadata will be changed
	 * @param resource resource object
	 * @param source complete path of the source resource
	 * @param destination complete path of the destination resource
	 * @return id of the new resource
	 * @throws UnknownHostException
	 */
	public abstract String link(MyFile resource, String source, String destination) throws UnknownHostException;
	
	/**
	 * copy a remote resource from source path to destination path. In this case the payload will be duplicated
	 * @param resource resource object
	 * @param source complete path of the source resource
	 * @param destination complete path of the destination resource
	 * @return id of the new resource
	 * @throws UnknownHostException
	 */
	public abstract String copy(MyFile resource, String source, String destination) throws UnknownHostException;
	
	/**
	 * Move a remote resource from source path to destination path 
	 * @param resource resource object
	 * @param source complete path of the source resource
	 * @param destination complete path of the destination resource
	 * @return id of the new resource
	 * @throws UnknownHostException
	 */
	public abstract String move(MyFile resource, String source, String destination) throws UnknownHostException;
	
	/**
	 * copy a remote folder from source path to destination path.
	 * @param resource resource object
	 * @param source complete path of the source resource
	 * @param destination complete path of the destination resource
	 * @return id of the new resource
	 * @throws UnknownHostException
	 */
	public abstract List<String> copyDir(MyFile resource, String source, String destination) throws UnknownHostException;
	
	/**
	 * Move a remote folder from source path to destination path 
	 * @param resource resource object
	 * @param source complete path of the source resource
	 * @param destination complete path of the destination resource
	 * @return id of the new resource
	 * @throws UnknownHostException
	 */
	public abstract List<String> moveDir(MyFile resource, String source, String destination) throws UnknownHostException;

	/**
	 * Get a generic metadata from a remote file ex: owner, creationDate, link
	 * @param remotePath remote file path
	 * @param property property key
	 * @return property value
	 * @throws UnknownHostException 
	 */
	public abstract String getFileProperty(String remotePath, String property);
	
	/**
	 * Get the number of files in a folder
	 * @param folderPath: the folder path
	 * @return the number of files contained in the folder
	 * @throws UnknownHostException
	 */
	public abstract long getFolderTotalItems(String folderPath);
	
	/**
	 * Get the total Volume in the folder specified by input parameter folderPath
	 * @param folderPath: the path of the folder
	 * @return the folder size
	 * @throws UnknownHostException
	 */
	public abstract long getFolderTotalVolume(String folderPath);

	/**
	 * Get the total Volume of files uploaded by a user specified in input parameter user
	 * @param user: the username
	 * @return the total
	 * @throws UnknownHostException
	 */
	public abstract String getUserTotalVolume(String user);

	/**
	 * Get the number of files uploaded by a user
	 * @param user: username
	 * @return the total
	 * @throws UnknownHostException
	 */
	public abstract String getUserTotalItems(String user);

	public abstract boolean isValidId(String id);
	
	public abstract String getId(String remoteIdentifier, boolean forceCreation);
	
	public abstract String getField(String remoteIdentifier, String fieldName) throws UnknownHostException ;
	
	public abstract void close();

	public String setFileProperty(String remotePath, String propertyField,
			String propertyValue) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
