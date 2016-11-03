package org.gcube.contentmanagement.blobstorage.service;


import org.gcube.contentmanagement.blobstorage.service.impl.AmbiguousResource;
import org.gcube.contentmanagement.blobstorage.service.impl.LocalResource;
import org.gcube.contentmanagement.blobstorage.service.impl.RemoteResource;
import org.gcube.contentmanagement.blobstorage.service.impl.RemoteResourceComplexInfo;
import org.gcube.contentmanagement.blobstorage.service.impl.RemoteResourceFolderInfo;
import org.gcube.contentmanagement.blobstorage.service.impl.RemoteResourceInfo;
import org.gcube.contentmanagement.blobstorage.service.impl.RemoteResourceSource;


/**
 * User interface.
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public interface IClient {
/**
 * 
 * Method for upload
 */
 public abstract LocalResource get();

	
/**
 * 
 * Method for locking a remote resource (file)
 */
public abstract AmbiguousResource lock();
/**
 * 
 * Method for ask file dimension
 */
public abstract RemoteResourceInfo getSize();
	
	
/**
 * Method for the download
 * @param replace indicates if the file must be replaced if this is present in the storage
 * @return LocalResource object
 */
public abstract LocalResource put(boolean replace);
	

/**
* Method for the download
* @param replace indicates if the file must be replaced if this is present in the storage
* @param file mimetype
* @return LocalResource object
*/
public abstract LocalResource put(boolean replace, String mimeType);

	
/**
 * 
 * Method for unlocking a remote resource 
 */
public abstract AmbiguousResource unlock(String key);
	
/**
 * TTl query	
 * @return the TTL left in ms for a remote resource if it is locked 
 */
public abstract RemoteResourceInfo getTTL();
	
/**
 * 
 * Remove a remote resource from the storage Sytem
 * @return RemoteResource object
 */
public abstract RemoteResource remove();

/**
 *Show all the objects in a specified remote folder
 * @return RemoteResource object
 */
public RemoteResource showDir();

/**
 * 	
 * remove a folder from the storage System
 * @return RemoteResource object
 */
public RemoteResource removeDir();
	

/**
 * 	renew a TTL for a specific resource. This operation is allowed a limited number of times
 * @return RemoteResourceInfo object
 */
public RemoteResourceInfo renewTTL(String key);

/**
 * 
 * @return RemoteResource object
 */
RemoteResource getUrl();

/**
 * Link a file from remote resource to another new remote resource. If the new remote resource exist, 
 * this resource will be removed and replaced with the new resource
 * @return RemoteResource object
 */
public RemoteResourceSource linkFile();


/**
 * Copy a file from remote resource to another new remote resource. If the new remote resource exist, 
 * this resource will be removed and replaced with the new resource
 * @return RemoteResource object
 */
public RemoteResourceSource copyFile();


/**
 * Move a file from remote resource to another new remote resource. If the new remote resource exist, 
 * this resource will be removed and replaced with the new resource
 * @return RemoteResource object
 */
public RemoteResourceSource moveFile();


public LocalResource get(String backendType);


public RemoteResourceInfo getSize(String backendType);

public RemoteResourceFolderInfo getFolderTotalVolume();

public RemoteResourceFolderInfo getFolderTotalItems();

public String getTotalUserVolume();

public String getUserTotalItems();

public RemoteResourceFolderInfo getFolderLastUpdate();

public RemoteResource remove(String backendType);


public RemoteResource showDir(String backendType);


public RemoteResource removeDir(String backendType);


public RemoteResource getUrl(String backendType);


public RemoteResourceInfo getTTL(String backendType);


public AmbiguousResource unlock(String key, String backendType);


public RemoteResourceInfo renewTTL(String key, String backendType);


public RemoteResourceSource linkFile(String backendType);


public RemoteResourceSource copyFile(String backendType);


public RemoteResourceSource moveFile(String backendType);


public RemoteResourceSource moveDir(String backendType);


public RemoteResourceSource moveDir();


public RemoteResourceSource copyDir(String backendType);


public RemoteResourceSource copyDir();


public RemoteResourceComplexInfo getMetaFile();

/**
 * close the connections to backend storage system
 */
public void close();


public RemoteResource getUrl(boolean forceCreation);

public RemoteResource getUrl(String backendType, boolean forceCreation);

public RemoteResource getMetaInfo(String field);

public RemoteResource getMetaInfo(String field, String backendType);

public RemoteResource setMetaInfo(String field, String value);

public RemoteResource setMetaInfo(String field, String value, String backendType);

public String getId(String id);


public RemoteResource getHttpUrl(boolean forceCreation);

public RemoteResource getHttpUrl(String backendType, boolean forceCreation);

public RemoteResource getHttpUrl(String backendType);

public RemoteResource getHttpUrl();

public RemoteResource getHttpsUrl(boolean forceCreation);

public RemoteResource getHttpsUrl(String backendType, boolean forceCreation);

public RemoteResource getHttpsUrl(String backendType);

public RemoteResource getHttpsUrl();


}