/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.gridfs.GridFS;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class CollectionOperator {
	
	private GridFS gfs;
	private DBCollection collection;
	private BasicDBObject dbObject;
	
	public CollectionOperator(GridFS gfs){
		setGfs(gfs);
	}

	public GridFS getGfs() {
		return gfs;
	}

	public void setGfs(GridFS gfs) {
		this.gfs = gfs;
	}

	public DBCollection getCollection() {
		return collection;
	}

	public void setCollection(DBCollection collection) {
		this.collection = collection;
	}

	public BasicDBObject getDbObject() {
		return dbObject;
	}

	public void setDbObject(BasicDBObject dbObject) {
		this.dbObject = dbObject;
	}
	
	

}
