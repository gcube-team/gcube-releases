package org.gcube.contentmanager.storageserver.consumer;

import java.util.List;

import org.bson.types.ObjectId;
import org.gcube.accounting.datamodel.usagerecords.StorageUsageRecord;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanager.storageserver.accounting.Report;
import org.gcube.contentmanager.storageserver.accounting.ReportConfig;
import org.gcube.contentmanager.storageserver.accounting.ReportException;
import org.gcube.contentmanager.storageserver.accounting.ReportFactory;
import org.gcube.contentmanager.storageserver.data.CubbyHole;
import org.gcube.contentmanager.storageserver.data.OpLogRemoteObject;
import org.gcube.contentmanager.storageserver.parse.utils.ValidationUtils;
import org.gcube.contentmanager.storageserver.store.MongoDB;
import org.gcube.contentmanager.storageserver.store.StorageStatusObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBObject;

public class UserAccountingConsumer extends Thread{
	
	final static Logger logger=LoggerFactory.getLogger(UserAccountingConsumer.class);
	final static int MINUTE_DECREMENT=-2;
	private CubbyHole c;
	private int number;
	private Report report;
// object fields	
	private String op;
	private String user;
	private String password;
	String[] server;
	List<String> dtsHosts;
//	private String id;

	public UserAccountingConsumer(String[] srvs, CubbyHole c, int number,List<String> dtsHosts){
		this.c=c;
		this.number=number;
		this.server=srvs;
		this.dtsHosts=dtsHosts;
		// init the accounting report
		try {
			init();
		} catch (ReportException e) {
			throw new RuntimeException("Accounting report Exception initialization");
		}
	}
	
	public UserAccountingConsumer(String[] srvs, String user, String password, CubbyHole c, int number, List<String> dtsHosts){
		this.c=c;
		this.number=number;
		this.server=srvs;
		this.dtsHosts=dtsHosts;
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
				logger.debug("Consumer #" + this.number + " got: " + x);
		    	op = (String) x.get("op");
		// retrieve object fields		
		    	DBObject obj=(DBObject)x.get("o");
		    	OpLogRemoteObject record=retrieveObjectFields(obj);
		   // set object dimension 	
		        
		        logger.debug("[recordCheck] operation: "+op+" name: "+record.getName()+" type: "+record.getType()+" path: "+record.getFilename()+" dir Path: "+record.getDir()+" length: "+record.getLength()+" owner: "+record.getOwner()+ " lastOperation "+record.getLastOperation()+" lastUser: "+record.getLastUser()+" lastAccess: "+record.getLastAccess());  
		        if(((record.getLength() >0) && (((record.getFilename() !=null) && (record.getFilename().length()>0) && (record.getDir().length()>0)&& (record.getDir().contains("/"))) || (record.getLinkCount()  > 0)))){
		        	//convert from byte to kb	
		    		record.setLength(record.getLength()/1024);
		    	// check scope	
		        	String scope=null;
		        	String pathString=(String)obj.get("onScope");
		        	logger.debug("pathString value: "+pathString);
		        	if((record.getDir()!=null)&& (record.getDir().contains("/"))){
		        		scope=retrieveScopeFromRemoteFilePath(record.getDir());
		        		logger.debug("scope retrieved: "+scope);
		        	}else{
		        // field added on storage manager library for retrieve scope. Used only if it is a link delete
		        		scope=retrieveScopeFromRemoteFilePath(pathString);
		        	}
		        	boolean validScope=ValidationUtils.validationScope(scope);
		        	if(validScope){
			        	if(record.getDelete() != null){
			        		record.setLastOperation("DELETE");
			        	}else if ((record.getLastOperation() != null) && (op != null) && (record.getLastOperation().equalsIgnoreCase("LINK")) && (op.equalsIgnoreCase("u"))){
			        // it is an update on a link object this operation doesn't be accounted
			        		logger.info("update on link object is not accounted. Skip next ");
			        		continue;
			        	}else if((record.getLastOperation()==null) || (record.getLastUser()==null)){
			        		logger.warn("lastOperation: "+record.getLastOperation()+" lastUser: "+record.getLastUser()+". These values cannot be null. Skip next ");
			        		continue;
			        	}else{
			        		// if the lastoperation is download and the caller contains a dts host then it isn't a real user but it is a workspace accounting record		        	
				        	if((dtsHosts != null) && (record.getLastOperation() != null) && (record.getLastOperation().equalsIgnoreCase("DOWNLOAD"))){
				        		logger.debug("check if the caller is from dts. CallerIP: "+record.getCallerIp());
				        		for(String host: dtsHosts){
				        			logger.debug("scan "+host);
				        			if(record.getCallerIp().contains(host)){
				        				record.setLastOperation("workspace.accounting");
				        				logger.info("the caller is dts service:  caller "+record.getCallerIp()+ " dts host: "+host+" the new user is: "+record.getLastUser());
				        			}
				        		}
				        	}
			        	}
			        	logger.debug(" operation accounted "+record.getLastOperation());
			        	StorageStatusObject ssr=null;
			        	if(isNeedSSReport(record.getLastOperation())){
				        	try{
				        		logger.info("build ss record");
					        	mongo=new MongoDB(server, user, password);
					        	if(record.getLastOperation().equalsIgnoreCase("COPY"))
					        		record.setOwner(record.getLastUser());
					        	ssr=new StorageStatusObject(record.getOwner(), record.getLength(), 1);
				        		ssr=mongo.updateUserVolume(ssr, record.getLastOperation());
					        	mongo.close();
				        	}catch(Exception e){
				        		mongo.close();
				        		logger.error("Problem in updating storage status record: "+e.getMessage());
				        	}
			        	}
			        	try{
			       // scope set add in 1.3 for new accounting libs 		
				        	ScopeProvider.instance.set(scope);
				        	//call to the accounting library	
				        	if (ssr !=null)
				        		report( record, scope, ssr.getVolume()+"", ssr.getCount()+"");
				        	else
				        		report( record, scope, null, null);
			        	}catch(Exception e){
			        		logger.error("Problem sending accounting report. Exception message: "+e.getMessage());
			        	}
		        	}else{
		        		logger.info("operation "+record.getLastOperation()+" is not accounted: invalid scope: "+scope);
		        	}
		        }else{
		        	logger.info("operation "+record.getLastOperation()+" is not accounted");
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
	
	/**
	 * check if the lastOperation is a valid operation for storage status information record
	 * @param lastOperation
	 * @return
	 */
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

	private OpLogRemoteObject retrieveObjectFields(DBObject obj) {
		OpLogRemoteObject record = new OpLogRemoteObject();
    	record.setFilename((String) obj.get("filename"));
        record.setType((String) obj.get("type"));
        record.setName( (String) obj.get("name"));
        record.setOwner((String) obj.get("owner"));
        record.setCreationTime((String) obj.get("creationTime"));
        record.setDir((String) obj.get("dir"));
        if(obj.get("lastAccess") != null) record.setLastAccess((String)obj.get("lastAccess"));
        if(obj.get("callerIP") != null) record.setCallerIp((String)obj.get("callerIP"));
        if(obj.get("lastOperation") != null) record.setLastOperation((String)obj.get("lastOperation"));
        if(obj.get("lastUser") != null) record.setLastUser((String)obj.get("lastUser"));
        if(obj.get("linkCount") != null) record.setLinkCount((int)obj.get("linkCount"));
        if(obj.get("onDeleting") != null) record.setDelete((String)obj.get("onDeleting"));
        if(obj.get("_id") != null){
        	ObjectId id= (ObjectId)obj.get("_id");
        	if(ObjectId.isValid(id.toString()))
        		record.setId(id.toString());
        }
        long length=-1;
        if(obj.get("length")!=null) record.setLength((long)obj.get("length"));
        return record;
        
	}

	private void report(OpLogRemoteObject record, String scope, String totVolume, String totCount) {
// ACCOUNTING CALL TYPE: STORAGE USAGE		
		StorageUsageRecord sur=report.setGenericProperties(null, "storage-usage", record.getLastUser(), scope, record.getCreationTime(), record.getLastAccess(), record.getOwner(), record.getLastOperation(), record.getLength()+"");
		sur=report.setSpecificProperties(sur, record.getFilename(), "STORAGE", record.getCallerIp(), record.getId());
		logger.info("[accounting call] type: storage usage ");
		report.printRecord(sur);
		report.send(sur);
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
			logger.debug("retieved scope: "+scope);
		 	return scope;
		}else logger.error("Scope bad format: scope not retrieved from string: "+filename);
		return null;
	}
		
}
