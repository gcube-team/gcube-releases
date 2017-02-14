package org.gcube.accounting.aggregator.recovery;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.gcube.accounting.aggregator.configuration.ConfigurationServiceEndpoint;
import org.gcube.accounting.aggregator.configuration.Constant;
import org.gcube.accounting.aggregator.persistence.AggregatorPersistenceBackendQueryConfiguration;
import org.gcube.accounting.aggregator.plugin.AccountingAggregatorPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.PersistTo;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.error.DocumentDoesNotExistException;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;


/**
 * @author Alessandro Pieve (ISTI - CNR) 
 *
 */
public class RecoveryRecord {

	private static Logger logger = LoggerFactory.getLogger(AccountingAggregatorPlugin.class);


	protected static Cluster cluster = null;

	/* One Bucket for type*/
	protected static Bucket bucketStorage;
	protected static String bucketNameStorage;
	protected static Bucket bucketService;
	protected static String bucketNameService;
	protected static Bucket bucketPortlet;
	protected static String bucketNamePortlet;
	protected static Bucket bucketJob;
	protected static String bucketNameJob;
	protected static Bucket bucketTask;
	protected static String bucketNameTask;

	private static Map <String, Bucket> connectionMap;

	/**
	 * {@inheritDoc}
	 */
	protected static void prepareConnection(Cluster cluster,AggregatorPersistenceBackendQueryConfiguration configuration) throws Exception {

		String url = configuration.getProperty(ConfigurationServiceEndpoint.URL_PROPERTY_KEY);
		String password = configuration.getProperty(ConfigurationServiceEndpoint.PASSWORD_PROPERTY_KEY);
		try {

			bucketNameStorage = configuration.getProperty(ConfigurationServiceEndpoint.BUCKET_STORAGE_NAME_PROPERTY_KEY);
			bucketNameService = configuration.getProperty(ConfigurationServiceEndpoint.BUCKET_SERVICE_NAME_PROPERTY_KEY);
			bucketNameJob = configuration.getProperty(ConfigurationServiceEndpoint.BUCKET_JOB_NAME_PROPERTY_KEY);
			bucketNamePortlet = configuration.getProperty(ConfigurationServiceEndpoint.BUCKET_PORTLET_NAME_PROPERTY_KEY);
			bucketNameTask = configuration.getProperty(ConfigurationServiceEndpoint.BUCKET_TASK_NAME_PROPERTY_KEY);
			connectionMap = new HashMap<String, Bucket>();

			bucketStorage = cluster.openBucket( bucketNameStorage,password);
			connectionMap.put(ConfigurationServiceEndpoint.BUCKET_STORAGE_TYPE, bucketStorage);

			bucketService = cluster.openBucket( bucketNameService,password);
			connectionMap.put(ConfigurationServiceEndpoint.BUCKET_SERVICE_TYPE, bucketService);

			bucketJob = cluster.openBucket( bucketNameJob,password);
			connectionMap.put(ConfigurationServiceEndpoint.BUCKET_JOB_TYPE, bucketJob);

			bucketPortlet = cluster.openBucket( bucketNamePortlet,password);			
			connectionMap.put(ConfigurationServiceEndpoint.BUCKET_PORTLET_TYPE, bucketPortlet);

			bucketTask = cluster.openBucket( bucketNameTask,password);		
			connectionMap.put(ConfigurationServiceEndpoint.BUCKET_TASK_TYPE, bucketTask);

		} catch(Exception e) {
			logger.error("Bucket connection error");
			throw e;
		} 

	}


	@SuppressWarnings("null")
	public static void searchFile(Cluster cluster,AggregatorPersistenceBackendQueryConfiguration configuration) throws Exception{

		try{
			prepareConnection(cluster,configuration); 			
			File folderDelete = new File(Constant.PATH_DIR_BACKUP_DELETE);		
			if (folderDelete.exists() && folderDelete.isDirectory()) {
				File[] listOfFilesDelete = folderDelete.listFiles();				
				for (int i = 0; i < listOfFilesDelete.length; i++) {			
					if (listOfFilesDelete[i].isFile()){
						Boolean result=ElaborateDeleteFile(Constant.PATH_DIR_BACKUP_DELETE+"/"+listOfFilesDelete[i].getName());
						if (result){
							logger.trace("Recovery delete complete.. Delete a file");
							File file = new File(Constant.PATH_DIR_BACKUP_DELETE+"/"+listOfFilesDelete[i].getName());
							file.delete();
						}
					}
				}
			}
			else
				logger.trace("not found files delete");

			//search for insert file
			File folderInsert= new File(Constant.PATH_DIR_BACKUP_INSERT);
			if (folderInsert.exists() && folderInsert.isDirectory()) {
				File[] listOfFilesInsert = folderInsert.listFiles();
				for (int i = 0; i < listOfFilesInsert.length; i++) {
					if (listOfFilesInsert[i].isFile()) {
						Boolean result=ElaborateInsertFile(Constant.PATH_DIR_BACKUP_INSERT+"/"+listOfFilesInsert[i].getName());
						if (result){
							logger.trace("Recovery insert complete.. Delete a file");
							File file= new File(Constant.PATH_DIR_BACKUP_INSERT+"/"+listOfFilesInsert[i].getName());
							file.delete();
						}
					}
				}
			}
			else
				logger.trace("not found files insert");

		}
		catch(Exception e){
			logger.error("Error for list file:{}",e);
		}

		//cluster.disconnect();
	}
	public static boolean ElaborateDeleteFile(String nameFile) throws IOException{
		HashMap<String, Object> mapper = new Gson().fromJson(new FileReader(new File(nameFile)),  HashMap.class);
		List<LinkedTreeMap<String, Object>> docs = (List<LinkedTreeMap<String, Object>>) mapper.get("docs");	

		String recordType="";
		String usageRecordType="";
		for (LinkedTreeMap<String, Object> doc: docs){
			String identifier=(String) doc.get("id");

			try{
				JsonObject accounting = JsonObject.empty();
				for (String key : doc.keySet()){
					accounting.put(key, doc.get(key));	
				}

				if (accounting.containsKey("usageRecordType"))
					usageRecordType=(String) doc.get("usageRecordType");
				else
					usageRecordType="";
				if (accounting.containsKey("recordType"))
					recordType=(String) doc.get("recordType");
				else
					recordType="";

				if  ((recordType.equals("ServiceUsageRecord")) || (usageRecordType.equals("ServiceUsageRecord")))
					bucketService.remove(identifier,PersistTo.MASTER,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
				if  ((recordType.equals("StorageUsageRecord")) || (usageRecordType.equals("StorageUsageRecord")))
					bucketStorage.remove(identifier,PersistTo.MASTER,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
				if  ((recordType.equals("JobUsageRecord")) || (usageRecordType.equals("JobUsageRecord")))
					bucketJob.remove(identifier,PersistTo.MASTER,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
				if  ((recordType.equals("TaskUsageRecord")) || (usageRecordType.equals("TaskUsageRecord")))
					bucketTask.remove(identifier,PersistTo.MASTER,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
				if  ((recordType.equals("PortletUsageRecord")) || (usageRecordType.equals("PortletUsageRecord")))
					bucketPortlet.remove(identifier,PersistTo.MASTER,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);

			}catch(DocumentDoesNotExistException d){
				logger.trace("Document id:{} not Exist",identifier);
			}
			catch(Exception e){
				logger.error("Problem with recovery file and delete record excepiton:{}",e.getLocalizedMessage());						
				throw e;
			}
		}
		return true;
	}

	public static boolean ElaborateInsertFile(String nameFile)throws IOException{
		HashMap<String, Object> mapper = new Gson().fromJson(new FileReader(new File(nameFile)),  HashMap.class);
		List<LinkedTreeMap<String, Object>> docs = (List<LinkedTreeMap<String, Object>>) mapper.get("docs");	
		String recordType="";
		String usageRecordType="";
		for (LinkedTreeMap<String, Object> doc: docs){
			String identifier=(String) doc.get("id");
			try{
				JsonObject accounting = JsonObject.empty();
				for (String key : doc.keySet()){
					accounting.put(key, doc.get(key));	
				}
				if (accounting.containsKey("usageRecordType"))
					usageRecordType=(String) doc.get("usageRecordType");
				else
					usageRecordType="";
				if (accounting.containsKey("recordType"))
					recordType=(String) doc.get("recordType");
				else
					recordType="";
				if (usageRecordType==null)
					usageRecordType="";
				if (recordType==null)
					recordType="";						

				if  ((recordType.equals("ServiceUsageRecord")) || (usageRecordType.equals("ServiceUsageRecord"))){
					JsonDocument document = JsonDocument.create(identifier, accounting);
					JsonDocument response = bucketService.upsert(document,PersistTo.MASTER,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
				}
				if  ((recordType.equals("StorageUsageRecord")) || (usageRecordType.equals("StorageUsageRecord"))){
					JsonDocument document = JsonDocument.create(identifier, accounting);					
					JsonDocument response = bucketStorage.upsert(document,PersistTo.MASTER,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
				}
				if  ((recordType.equals("JobUsageRecord")) || (usageRecordType.equals("JobUsageRecord"))){
					JsonDocument document = JsonDocument.create(identifier, accounting);
					JsonDocument response = bucketJob.upsert(document,PersistTo.MASTER,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
				}
				if  ((recordType.equals("TaskUsageRecord")) || (usageRecordType.equals("TaskUsageRecord"))){
					JsonDocument document = JsonDocument.create(identifier, accounting);
					JsonDocument response = bucketTask.upsert(document,PersistTo.MASTER,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);					
				}
				if  ((recordType.equals("PortletUsageRecord")) || (usageRecordType.equals("PortletUsageRecord"))){
					JsonDocument document = JsonDocument.create(identifier, accounting);
					JsonDocument response = bucketPortlet.upsert(document,PersistTo.MASTER,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);					
				}
			}catch(Exception e){
				logger.error("Problem with recovery file and insert record excepiton:{}",e.getLocalizedMessage());						
				throw e;
			}

		}
		return true;

	}

}
