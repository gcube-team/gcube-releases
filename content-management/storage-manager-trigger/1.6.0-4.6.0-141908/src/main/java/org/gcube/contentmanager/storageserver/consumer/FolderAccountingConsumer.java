package org.gcube.contentmanager.storageserver.consumer;

import org.gcube.contentmanager.storageserver.accounting.Report;
import org.gcube.contentmanager.storageserver.accounting.ReportConfig;
import org.gcube.contentmanager.storageserver.accounting.ReportException;
import org.gcube.contentmanager.storageserver.accounting.ReportFactory;
import org.gcube.contentmanager.storageserver.data.CubbyHole;
import org.gcube.contentmanager.storageserver.parse.utils.ValidationUtils;
import org.gcube.contentmanager.storageserver.store.FolderStatusOperationManager;
import org.gcube.contentmanager.storageserver.store.FolderStatusObject;
import org.gcube.contentmanager.storageserver.store.MongoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.DBObject;

public class FolderAccountingConsumer extends Thread{
	
	final static Logger logger=LoggerFactory.getLogger(FolderAccountingConsumer.class);
	final static int MINUTE_DECREMENT=-2;
	private CubbyHole c;
	private int number;
	private Report report;
// object fields	
	private String op;
	private String filename;
	private String type;
	private String name;
	private String owner;
	private String creationTime;
	private String lastAccess;
	private String lastOperation;
	private String lastUser;
	private int linkCount;
	private String delete;
	private String callerIp;
	private String user;
	private String password;
	String[] server;
	private String from;

	public FolderAccountingConsumer(String[] srvs, CubbyHole c, int number){
		this.c=c;
		this.number=number;
		this.server=srvs;
		// init the accounting report
		try {
			init();
		} catch (ReportException e) {
			throw new RuntimeException("Accounting report Exception initialization");
		}
	}
	
	public FolderAccountingConsumer(String[] srvs, String user, String password, CubbyHole c, int number){
		this.c=c;
		this.number=number;
		this.server=srvs;
		this.user=user;
		this.password=password;
		// init the accounting report
		try {
			init();
		} catch (ReportException e) {
			throw new RuntimeException("Accounting report Exception initialization");
		}
	}
	

	private void init() throws ReportException{
		report=ReportFactory.getReport(ReportConfig.ACCOUNTING_TYPE);
		report.init();
	}
	
	public void run() {
		while(true){
			DBObject x=null;
			MongoDB mongo=null;
			try{
				x=c.get();
				logger.debug("[FolderACCOUNTING] Consumer #" + this.number + " got: " + x);
		    	op = (String) x.get("op");
		// retrieve object fields		
		    	DBObject obj=(DBObject)x.get("o");
		    	retrieveObjectFields(obj);
		   // set object dimension 	
		        long length=-1;
		        if(obj.get("length")!=null) length=(long)obj.get("length");
		        logger.info("[FolderACCOUNTING][recordCheck] operation: "+op+" name: "+name+" type: "+type+" path: "+filename+" length: "+length+" owner: "+owner+"\n\t cretionTime: "+creationTime+ " lastOperation "+lastOperation+" lastUser: "+lastUser+" lastAccess: "+lastAccess);
		        if(((length >0) && (((filename!=null) && (filename.contains("/"))) || (linkCount > 0)))){
		        	//convert from byte to kb	
		    		length=length/1024;
		    	// check scope	
		        	String scope=null;
		        	if((filename!=null)&& (filename.contains("/"))){
		        		scope=retrieveScopeFromRemoteFilePath(filename);
		        	}else{
			        // field added on storage manager library for retrieve scope. Used only if it is a link delete
			        		String pathString=(String)obj.get("onScope");
			        		scope=retrieveScopeFromRemoteFilePath(pathString);
		        	}
	        		logger.info("[FolderACCOUNTING] update edge: "+filename);
//	        		logger.info("\n[FolderACCOUNTING] operation: "+lastOperation+"\n\t name: "+name+"\n\t type: "+type+"\n\t path: "+filename+"\n\t length: "+length+"\n\t owner: "+owner+"\n\t cretionTime: "+creationTime+"\n\t scope: "+scope+"\n\t lastOperation "+lastOperation+"\n\t lastUser: "+lastUser+"\n\t lastAccess: "+lastAccess+"\n\t callerIp: "+callerIp);
	        		boolean validScope=ValidationUtils.validationScope(scope);
	        		if(validScope){
			        	if(delete!=null){
			        		lastOperation="DELETE";
			        	}else if ((lastOperation != null) && (op != null) && (lastOperation.equalsIgnoreCase("LINK")) && (op.equalsIgnoreCase("u"))){
			        // it is an update on a link object this operation doesn't be accounted
			        		logger.info("[FolderACCOUNTING] update on link object is not accounted. Skip next ");
			        		continue;
			        	}else if((lastOperation==null) || (lastUser==null)){
			        		logger.warn("[FolderACCOUNTING] lastOperation: "+lastOperation+" lastUser: "+lastUser+". These values cannot be null. Skip next ");
			        		continue;
			        	}//else{
			       // the record is a valid record for folder accounting  		
			        		logger.info("[FolderACCOUNTING] this is a valid record for folder accounting. build folder record  ");
			        		print(scope, length);
			        		FolderStatusObject fsr=null;
			        		if(isNeedFSReport(lastOperation)){
			        			try{
						        	mongo=new MongoDB(server, user, password);
						        	if(lastOperation.equalsIgnoreCase("COPY"))
						        		owner=lastUser;
						//extract folder from filename field
						        	String folder=filename.substring(0, filename.lastIndexOf("/"));
						        	fsr=new FolderStatusObject(folder, length, 1, lastAccess, from);
					        		fsr=mongo.updateFolderVolume(fsr, lastOperation);
				// if it is a Move operation  it is need to update the original folder.	        		
					        		if(lastOperation.equalsIgnoreCase("MOVE")){
					        			if((from != null)){
					        				fsr=new FolderStatusObject(from, length, 1, lastAccess, null);
					        				fsr= mongo.updateFolderVolume(fsr, "DELETE");
					        			}else{
					        				logger.error("this is a move operation but the original path folder is missing. Maybe the storage-manager library is not update on the client. skip next");
					        			}
					        		}
						        	mongo.close();
					        	}catch(Exception e){
					        		logger.error("Problem in updating folder status record: "+e.getMessage());
					        	}
			        		}
//			        	}
			        }else{
			        	logger.error("invalid scope found"+scope+" skip next record" );
			        }
		        }else{
		        	logger.info("operation "+lastOperation+" is not accounted");
		        }

			}catch(Exception e){
				e.printStackTrace();
				logger.error("ERROR Processing record: "+x+" Exception throws: "+e.getStackTrace());
				logger.info("skip to next record ");
				if(mongo!=null)
					mongo.close();
			}
		}
	}
	
	
	

	private boolean isNeedSSReport(String lastOperation) {
		if(lastOperation.equalsIgnoreCase("UPLOAD") || lastOperation.equalsIgnoreCase("COPY") || lastOperation.equalsIgnoreCase("DELETE"))
			return true;
		return false;
	}

	public void runWithoutThread(DBObject x){
		try {
			report=ReportFactory.getReport(ReportConfig.ACCOUNTING_TYPE);
		} catch (ReportException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		report.init();
		run();
	}

	private void retrieveObjectFields(DBObject obj) {
    	filename = (String) obj.get("filename");
        type = (String) obj.get("type");
        name = (String) obj.get("name");
        owner = (String) obj.get("owner");
        creationTime = (String) obj.get("creationTime");
        lastAccess = null;
   // only for move operation is needded this field: from     
       	from=(String)obj.get("from");
        if(obj.get("lastAccess") != null) lastAccess=(String)obj.get("lastAccess");
        callerIp = null;
        if(obj.get("callerIP") != null) callerIp=(String)obj.get("callerIP");
        lastOperation = null;
        if(obj.get("lastOperation") != null) lastOperation=(String)obj.get("lastOperation");
        lastUser = null;
        if(obj.get("lastUser") != null) lastUser=(String)obj.get("lastUser");
        linkCount = 0;
        if(obj.get("linkCount") != null) linkCount=(int)obj.get("linkCount");
        delete = null;
        if(obj.get("onDeleting") != null) delete=(String)obj.get("onDeleting");
//        ObjectId objectId=(ObjectId)obj.get("_id");
//        id = objectId.toString();
	}
	
	private void print(String scope, long length) {
		logger.info(" Folder accounting properties: " +
				"\n\t owner: "+owner+
				"\n\t scope: "+scope+
				"\n\t type  folder-status"+
				"\n\t file  "+filename+
				"\n\t size  "+length+
				"\n\t caller  "+callerIp+
				"\n\t consumer  "+lastUser+
				"\n\t lastAccess  "+lastAccess+
				"\n\t operation "+lastOperation);
		
	}

	private String retrieveScopeFromRemoteFilePath(String filename) {
		String[] split=filename.split("/");
		if(split.length>0){
			String scope=null;
			int i=1;
			if(split[1].equals("VOLATILE")){
				i=2;
			}
			scope="/"+split[i];
			i++;
			while((!split[i].equals("home")) && (!split[i].equals("public"))){
				scope=scope+"/"+split[i];
				i++;
			}
			logger.info("retieved scope: "+scope);
		 	return scope;
		}else logger.error("Scope bad format: scope not retrieved from string: "+filename);
		return null;
	}
	
	/**
	 * check if the lastOperation is a valid operation for folder status information record
	 * @param lastOperation
	 * @return
	 */
	private boolean isNeedFSReport(String lastOperation) {
		if(lastOperation.equalsIgnoreCase("UPLOAD") || lastOperation.equalsIgnoreCase("COPY") || lastOperation.equalsIgnoreCase("DELETE") || lastOperation.equalsIgnoreCase("MOVE"))
			return true;
		return false;
	}
		
}
