/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class BsonOperator {
	
	private GridFS gfs;
//	private GridFSDBFile gfsFile;
//	private String dbName;
//	private BasicDBObject dbObject;
	private Logger logger = LoggerFactory.getLogger(BsonOperator.class);
	
	public BsonOperator(GridFS gfs){
		this.gfs=gfs;
//		this.dbName=dbName;
		
	}
	
	protected List<GridFSDBFile> getFilesOnFolder(String folderPath) {
		BasicDBObject queryFile = new BasicDBObject();
		queryFile.put("dir", java.util.regex.Pattern.compile(folderPath+"*"));
		List<GridFSDBFile> list=gfs.find(queryFile);
		logger.info("retrieveRemoteFileObject found "+list.size()+" objects ");
		return list;		
	}
	
	protected List<GridFSDBFile> getOwnedFiles(String username){
		BasicDBObject queryFile = new BasicDBObject();
		queryFile.put("owner", username);
		List<GridFSDBFile> list=gfs.find(queryFile);
		logger.info("retrieveUsersFileObjectfound "+list.size()+" objects ");
		return list;		
	}

}
