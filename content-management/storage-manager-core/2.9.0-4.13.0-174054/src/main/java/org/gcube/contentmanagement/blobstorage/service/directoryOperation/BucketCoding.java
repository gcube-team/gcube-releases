package org.gcube.contentmanagement.blobstorage.service.directoryOperation;

import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains all the coding and decoding for a bucket name: 
 * bucketId: TO DO
 * bucketName (if is a dir):
 * bucketName (if is a file): 	
 * 
 * @author Roberto Cirillo (ISTI - CNR)
 */
public class BucketCoding {

	/**
	 * Logger for this class
	 */
    final Logger logger=LoggerFactory.getLogger(BucketCoding.class);	
	
	/**
	 * Coding the name of a file object in a file-type bucket
	 * @param path the path on the cluster
	 * @param author the file's owner
	 * @return the bucketName coded
	 */
	public String bucketFileCoding(String path, String rootArea) {
		logger.debug("Coding name: path:  "+path+" rootArea "+rootArea);
//		if(!ObjectId.isValid(path)){
			String absolutePath =path;
			if(rootArea.length()>0){
				absolutePath = mergingPathAndFile(rootArea, path);
				path=absolutePath;
			}
			if(!Costants.CLIENT_TYPE.equalsIgnoreCase("mongo")){
				absolutePath=absolutePath.replaceAll(Costants.FILE_SEPARATOR, Costants.SEPARATOR);
			}
		logger.debug("coding name done");	
//		}
		return path;
	}

	/**
	 * rootArea + path formed an absolute path
	 * 
	 * @param path remote relative path
	 * @param rootArea remote root path
	 * @return absolute remote path
	 */
	public String mergingPathAndDir(String rootArea, String path ) {
		char c=rootArea.charAt(rootArea.length()-1);
		if((c+"").equalsIgnoreCase(Costants.FILE_SEPARATOR)){
			rootArea=rootArea.substring(0, rootArea.length()-1);
		}
		c=path.charAt(0);
		if(!(c+"").equalsIgnoreCase(Costants.FILE_SEPARATOR)){
			path=Costants.FILE_SEPARATOR+path;
		}
		c=path.charAt(path.length()-1);
		if(!(c+"").equalsIgnoreCase(Costants.FILE_SEPARATOR)){
			path=path+Costants.FILE_SEPARATOR;
		}
		String bucketName=rootArea+path;
		return bucketName;
	}

	/**
	 * check and correct the directory format
	 * @param path remote dir path
	 * @return remote dir path
	 */
	public String checkSintaxDir(String path ) {
		char c=path.charAt(0);
		if(!(c+"").equalsIgnoreCase(Costants.FILE_SEPARATOR)){
			path=Costants.FILE_SEPARATOR+path;
		}
		c=path.charAt(path.length()-1);
		if(!(c+"").equalsIgnoreCase(Costants.FILE_SEPARATOR)){
			path=path+Costants.FILE_SEPARATOR;
		}
		String bucketName=path;
		return bucketName;
	}

	/**
	 * rootArea + path formed an absolute path
	 * 
	 * @param path relative path
	 * @param rootArea root path
	 * @return complete path
	 */
	private String mergingPathAndFile(String rootArea, String path ) {
		char c=rootArea.charAt(rootArea.length()-1);
		if((c+"").equalsIgnoreCase(Costants.FILE_SEPARATOR)){
			rootArea=rootArea.substring(0, rootArea.length()-1);
		}
		if(path == null) return null;
		c=path.charAt(0);
		if(!(c+"").equalsIgnoreCase(Costants.FILE_SEPARATOR)){
			path=Costants.FILE_SEPARATOR+path;
		}
		c=path.charAt(path.length()-1);
		if((c+"").equalsIgnoreCase(Costants.FILE_SEPARATOR)){
			path=path.substring(0, path.length()-1);
		}
		String bucketName=rootArea+path;
		return bucketName;
	}
	
	/**
	 * 
	 * Decoding the name of a file object in a file-type bucket  
	 * @param key relative remote path
	 * @return complete remote path
	 */
	public String bucketFileDecoding(String key, String rootArea) {
		String nameDecoded=key;
		if(!Costants.CLIENT_TYPE.equalsIgnoreCase("mongo")){
			String[] splits=key.split(Costants.SEPARATOR);
			nameDecoded=splits[splits.length-1];
		}
		if (logger.isDebugEnabled()) {
			logger.debug("decodeBucketFile(String) - end");
		}
		return nameDecoded;
	}
	
	/**
	 * Coding the name of a directory object in a directory-type bucket  
	 * @param author file owner
	 * @param dir remote directory
	 * @return the complete remote path
	 */
	public String bucketDirCoding(String dir, String rootArea) {
		if(!Costants.CLIENT_TYPE.equalsIgnoreCase("mongo")){
			dir=dir.replaceAll(Costants.FILE_SEPARATOR, Costants.SEPARATOR);
		}
		dir=mergingPathAndDir(rootArea, dir);
		return dir;
	}

	/**
	 * Decoding the name in a directory-type bucket.  
	 * In a directory type bucket you can found or a file object or a directory object
	 * @param key remote path
	 * @return the remote path
	 */
	public String bucketDirDecoding(String key, String rootArea) {
		if(!Costants.CLIENT_TYPE.equalsIgnoreCase("mongo")){
			String lastChar=key.substring(key.length()-3);
			// if is a dir object	
				if(lastChar.equalsIgnoreCase(Costants.SEPARATOR)){
					String[] extractPath=key.split(Costants.SEPARATOR);
					String[] rootPath= rootArea.split(Costants.FILE_SEPARATOR);
					key="";
					for(int i=rootPath.length;i<extractPath.length;i++){
						key=key+Costants.FILE_SEPARATOR+extractPath[i];
					}
					key=key+Costants.FILE_SEPARATOR;
					if(logger.isInfoEnabled())
						logger.info("found directory: "+key);
			// if is a file object		
				}else{
					if(logger.isDebugEnabled())
						logger.debug("found object coded: "+key);
					key=bucketFileDecoding(key, rootArea);
					if(logger.isInfoEnabled())
						logger.info("found object: "+key);
				}

		}
		return key;
	}

	/**
	 * Return true if key is a file-bucket object else (if is a directory-bucket object) return false
	 * @param key remote path
	 * @return remote path
	 */
	public boolean isFileObject(String key) {
		String lastChar=key.substring(key.length()-3);
		if(lastChar.equalsIgnoreCase(Costants.SEPARATOR))
			return false;
		return true;
	}
	
}
