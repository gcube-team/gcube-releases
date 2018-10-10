/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.operation;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.OPERATION;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.service.operation.CopyDir;
import org.gcube.contentmanagement.blobstorage.service.operation.Monitor;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoOperationManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class CopyDirOperator extends CopyDir {

	Logger logger=LoggerFactory.getLogger(CopyDirOperator.class);
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
	public CopyDirOperator(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk,
			String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.gcube.contentmanagement.blobstorage.service.operation.CopyDir#execute(org.gcube.contentmanagement.blobstorage.transport.backend.MongoIO, org.gcube.contentmanagement.blobstorage.resource.MyFile, java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> execute(MongoIOManager mongoPrimaryInstance, MyFile resource, String sourcePath, String destinationPath)
			throws UnknownHostException {
		String source=sourcePath;
		source = appendFileSeparator(source);
		String destination=destinationPath;
		destination = appendFileSeparator(destination);
		String parentFolder=extractParent(source);
		String destinationId=null;
		List<String> idList=null;
		logger.debug("copyDir operation on Mongo backend, parameters: source path: "+source+" destination path: "+destination);
		if((source != null) && (!source.isEmpty()) && (destination != null) && (!destination.isEmpty())){
			DB db = mongoPrimaryInstance.getConnectionDB(MongoOperationManager.getPrimaryCollectionName(), true);// getDB(resource);
			GridFS gfs = mongoPrimaryInstance.getGfs();
//// create query for dir field
			BasicDBObject query = new BasicDBObject();
			query.put( "dir" , new BasicDBObject("$regex", source+"*"));
			List<GridFSDBFile> folder = gfs.find(query);
			if(folder!=null){
				idList=new ArrayList<String>(folder.size());
				for(GridFSDBFile f : folder){
					if(f.get("type").equals("file")){
						String oldFilename=(String)f.get("filename");
						String oldDir=(String)f.get("dir");
						f=mongoPrimaryInstance.retrieveLinkPayload(f);
						InputStream is= f.getInputStream();
						int relativePathIndex=source.length();
						String relativeDirTree=parentFolder+ServiceEngine.FILE_SEPARATOR+oldDir.substring(relativePathIndex);
						String relativePath=parentFolder+ServiceEngine.FILE_SEPARATOR+oldFilename.substring(relativePathIndex);
						String filename=destination+relativePath;
						String dir=destination+relativeDirTree;
						GridFSInputFile destinationFile=gfs.createFile(is);
						destinationFile.put("filename", filename);  
						destinationFile.put("type", "file");
						destinationFile.put("dir", dir);
						mongoPrimaryInstance.updateCommonFields(destinationFile, resource, OPERATION.COPY_DIR);
						idList.add(destinationFile.getId().toString());
						if(logger.isDebugEnabled())
							logger.debug("ObjectId: "+destinationId);
						mongoPrimaryInstance.buildDirTree(mongoPrimaryInstance.getMetaDataCollection(db), dir);
						destinationFile.save();
					}
				}
			}
			mongoPrimaryInstance.close();
		}
		return idList;
	}

}
