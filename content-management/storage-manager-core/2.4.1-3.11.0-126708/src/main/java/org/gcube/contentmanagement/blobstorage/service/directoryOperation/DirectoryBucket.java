package org.gcube.contentmanagement.blobstorage.service.directoryOperation;


import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.MongoException;

/**
 * 
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class DirectoryBucket {
	/**
	 * Logger for this class
	 */
    final Logger logger=LoggerFactory.getLogger(DirectoryBucket.class);	
	public String file_separator = ServiceEngine.FILE_SEPARATOR; //System.getProperty("file.separator");
	String author;
	String fileName;
	String path;
	String[] server;
	String user, password;
	public DirectoryBucket(String[] server, String user, String password, String path, String author){
		if(logger.isDebugEnabled())
			logger.debug("DirectoryBucket PATH: "+path);
	//coding the path	
		this.path=path;
		this.author=author;
		this.server=server;
		this.user=user;
		this.password=password;
	}


/**
 * generate the names of the upper tree directory buckets
 * @return The list of tree directory buckets: ex: if the path is /this/is/my/path/myFile.txt
 * the list will contains: /this, /this/is, /this/is/my, this/is/my/path
 */
	public String[] retrieveBucketsName(String path, String rootArea){
		if (logger.isDebugEnabled()) {
			logger.debug("retrieveBucketsName() - start");
		}
		String pathCoded=new BucketCoding().mergingPathAndDir(rootArea, path);
		String[] splits=pathCoded.split(file_separator);
		String[] buckets=new String[splits.length];
		for(int i=0;i<splits.length;i++){
			if(logger.isDebugEnabled())
				logger.debug("splits["+i+"] = "+splits[i]);
			if(i>0){
				if(i==(splits.length-1)){
					if(logger.isDebugEnabled())
						logger.debug("splits["+i+"]= "+splits[i]);
					fileName=buckets[i-1]+splits[i];
					buckets[i]=buckets[i-1]+splits[i];
					if(logger.isDebugEnabled())
						logger.debug("fileName: "+fileName);
					break;	
				}else{
					buckets[i]=buckets[i-1]+splits[i]+BucketCoding.SEPARATOR;
				}
			}else{
				buckets[i]=BucketCoding.SEPARATOR;
			}	
			if (logger.isDebugEnabled())
				logger.debug("buckets["+i+"]= "+buckets[i]);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("retrieveBucketsName() - end");
		}
		return buckets;
	}
	
/**
 * remove a file on a remote directory
 * @param bucket remote file to remove
 */
	@Deprecated
	public void removeKeysOnDirBucket(MyFile resource, String bucket, String rootArea, String backendType){
		if(logger.isDebugEnabled())
			logger.debug("CHECK REMOVE: "+bucket);
		String[] bucketList=null;
		bucketList=retrieveBucketsName(path, rootArea);
		TransportManagerFactory tmf=new TransportManagerFactory(server, user, password);
		TransportManager tm=tmf.getTransport(backendType);
//		TerrastoreClient client=new TerrastoreClient( new OrderedHostManager(Arrays.asList(server)), new HTTPConnectionFactory());
		for(int i=0;i<bucketList.length;i++){
			if(logger.isDebugEnabled())
				logger.debug("REMOVE: check "+bucketList[i]);
			if(bucketList[i].equalsIgnoreCase(bucket)){
				if(logger.isDebugEnabled())
					logger.debug("Removing key file: "+bucketList[i]+" from dir: "+bucketList[i-1]);
				try {
					tm.getValues(resource, bucketList[i-1], DirectoryEntity.class);
				} catch (MongoException e) {
					tm.close();
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * remove a remote directory and all the files that the remote directory contains
	 * @param bucket
	 */
	public String removeDirBucket(MyFile resource, String bucket, String rootArea, String backendType){
		if(logger.isDebugEnabled())
			logger.debug("CHECK REMOVE: "+bucket);
		String[] bucketList=null;
		BucketCoding bc=new BucketCoding();
		String bucketDirCoded =bc.bucketDirCoding(bucket, rootArea);
		if(logger.isDebugEnabled())
			logger.debug("bucketDir Coded: "+bucketDirCoded);
		bucketList=retrieveBucketsName(bucket, rootArea);
		TransportManagerFactory tmf=new TransportManagerFactory(server, user, password);
		TransportManager tm=tmf.getTransport(backendType);
		for(int i=0;i<bucketList.length;i++){
			if(logger.isDebugEnabled())
				logger.debug("REMOVE: check "+bucketList[i]+" bucketDirCoded: "+bucketDirCoded );
			if(bucketDirCoded.contains(bucketList[i])){
				Map<String, StorageObject> map=null;
				try {
					map = tm.getValues(resource, bucketList[i], DirectoryEntity.class);
				} catch (MongoException e) {
					tm.close();
					e.printStackTrace();
				}
				Set<String> keys=map.keySet();
				for(Iterator<String> it=keys.iterator(); it.hasNext();){
					String key=(String)it.next();
					if(key.equalsIgnoreCase(bucketDirCoded)){
						if(logger.isDebugEnabled())
							logger.debug("key matched: "+key+" remove");
					//recursively remove
						try {
							map=tm.getValues(resource, key, DirectoryEntity.class);
						} catch (MongoException e) {
							tm.close();
							e.printStackTrace();
						}
						keys=map.keySet();
						for(Iterator<String> it2=keys.iterator(); it2.hasNext();){
							String key2=(String)it2.next();
							if(logger.isDebugEnabled())
								logger.debug("the bucket: "+key+" have a son: "+key2);
							if(bc.isFileObject(key2)){
								if(logger.isDebugEnabled()){
									logger.debug("remove "+key2+" in the bucket: "+key);
								}
								if(logger.isDebugEnabled())
									logger.debug("remove all keys in the bucket: "+key2);
								try {
									tm.removeRemoteFile(key2, resource);
								} catch (UnknownHostException e) {
									tm.close();
									e.printStackTrace();
								} catch (MongoException e) {
									tm.close();
									e.printStackTrace();
								}
							}else{
								if(logger.isDebugEnabled())
									logger.debug(key2+" is a directory");
								String bucketDecoded=bc.bucketDirDecoding(key2, rootArea);
								removeDirBucket(resource, bucketDecoded, rootArea, backendType);
							}
							
						}
						if(logger.isDebugEnabled())
							logger.debug("remove "+key+" in the bucket: "+bucketList[i]);
						if(logger.isDebugEnabled())
							logger.debug("remove all keys in the bucket: "+key);
						try {
							tm.removeRemoteFile(key, resource);
						} catch (UnknownHostException e) {
							tm.close();
							e.printStackTrace();
						} catch (MongoException e) {
							tm.close();
							e.printStackTrace();
						}
					}
				}
			}
		}
		return bucketDirCoded;
	}


	/**
	 *  recursively search on directories buckets, return a key if found else return null 
	 * @param name fileName
	 * @param bucketCoded bucketName coded
	 * @param tm a client for the cluster
	 */
	public String searchInBucket(MyFile resource, String name, String bucketCoded,
			TransportManager tm, String rootArea) {
		Map <String, StorageObject> dirs=null;
		try{
			dirs=tm.getValues(resource, bucketCoded, DirectoryEntity.class);
		}catch(Exception e){
			tm.close();
			logger.info("object not found");
			return null;
		}
	
		Set<String> set=dirs.keySet();
		for(Iterator<String> it= set.iterator(); it.hasNext();){
			String key=(String)it.next();
			if(logger.isDebugEnabled())
				logger.debug("try in "+key);
			String nameDecoded = new BucketCoding().bucketFileDecoding(key, rootArea);
			if(logger.isDebugEnabled())
				logger.debug("name decoded: "+nameDecoded+" name searched is: "+name);
			if((nameDecoded!=null ) && (nameDecoded.equalsIgnoreCase(name))){
				if(logger.isDebugEnabled())
					logger.debug("FOUND in "+bucketCoded+" objectId returned: "+key);
				return key;
			}else{
				searchInBucket(resource,name, key, tm, rootArea);
			}
		}
		return null;
	}

}
