/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.operation;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MemoryType;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.OPERATION;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.service.operation.Monitor;
import org.gcube.contentmanagement.blobstorage.service.operation.MoveDir;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoOperationManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class MoveDirOperator extends MoveDir {

	Logger logger=LoggerFactory.getLogger(MoveDirOperator.class);
	/**
	 * @param server
	 * @param user
	 * @param pwd
	 * @param bucket
	 * @param monitor
	 * @param isChunk
	 * @param backendType
	 * @param dbs
	 */
	public MoveDirOperator(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk,
			String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.gcube.contentmanagement.blobstorage.service.operation.MoveDir#execute(org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO, org.gcube.contentmanagement.blobstorage.resource.MyFile, java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> execute(MongoIOManager mongoPrimaryInstance, MyFile resource, String sourcePath,
			String destinationPath, MemoryType memoryType) throws UnknownHostException {
		String source=sourcePath;
		source = appendFileSeparator(source);
		String parentFolder=extractParent(source);
		String destination=destinationPath;
		destination = appendFileSeparator(destination);
		List<String> idList=null;
		logger.debug("moveDir operation on Mongo backend, parameters: source path: "+source+" destination path: "+destination);
		if((source != null) && (!source.isEmpty()) && (destination != null) && (!destination.isEmpty())){
			DB db=mongoPrimaryInstance.getConnectionDB(MongoOperationManager.getPrimaryCollectionName(), true);
//			GridFS meta = new GridFS(db); 
			DBCollection meta=mongoPrimaryInstance.getMetaDataCollection(db);
// create query for dir field
			BasicDBObject query = new BasicDBObject();
			query.put( "dir" , new BasicDBObject("$regex", source+"*"));
			DBCursor folderCursor = meta.find(query);
			if((folderCursor !=null)){
				idList=new ArrayList<String>();
				while(folderCursor.hasNext()){//GridFSDBFile f : folder){
					DBObject f=folderCursor.next();
					if(f.get("type").equals("file")){
						String oldFilename=(String)f.get("filename");
						String oldDir=(String)f.get("dir");
						int relativePathIndex=source.length();
						String relativeDirTree=parentFolder+ServiceEngine.FILE_SEPARATOR+oldDir.substring(relativePathIndex);
						String relativePath=parentFolder+ServiceEngine.FILE_SEPARATOR+oldFilename.substring(relativePathIndex);
						String filename=destination+relativePath;
						String dir=destination+relativeDirTree;
						f.put("filename", filename);
						f.put("dir", dir);
						mongoPrimaryInstance.updateCommonFields(f, resource, OPERATION.MOVE_DIR);
						String id=f.get("_id").toString();
						idList.add(id);
						query = new BasicDBObject();
						query.put( "_id" , new ObjectId(id));
						if(!(memoryType== MemoryType.VOLATILE))
							meta.update(query, f, true, false, MongoIOManager.DEFAULT_WRITE_TYPE);
						else
							meta.update(query, f, true, false);
//						meta.update(query, f, true, true);
						mongoPrimaryInstance.buildDirTree(meta, dir);
					}
				}
			}
		}else{
			mongoPrimaryInstance.close();
			throw new IllegalArgumentException("parameters not completed, source: "+source+", destination: "+destination);
		}
		mongoPrimaryInstance.close();
		return idList;

	}

}
