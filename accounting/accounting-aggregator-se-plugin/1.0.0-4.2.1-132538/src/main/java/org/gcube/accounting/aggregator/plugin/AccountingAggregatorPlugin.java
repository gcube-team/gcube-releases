package org.gcube.accounting.aggregator.plugin;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.gcube.accounting.aggregator.configuration.ConfigurationServiceEndpoint;
import org.gcube.accounting.aggregator.configuration.Constant;
import org.gcube.accounting.aggregator.configuration.ManagementFileBackup;
import org.gcube.accounting.aggregator.madeaggregation.Aggregation;
import org.gcube.accounting.aggregator.madeaggregation.AggregationType;
import org.gcube.accounting.aggregator.persistence.AggregatorPersistenceBackendQueryConfiguration;
import org.gcube.accounting.aggregator.recovery.RecoveryRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.persistence.PersistenceCouchBase;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.RecordUtility;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.PersistTo;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;


/**
 * @author Alessandro Pieve (ISTI - CNR) 
 *
 */
public class AccountingAggregatorPlugin extends Plugin<AccountingAggregatorPluginDeclaration> {


	private static Logger logger = LoggerFactory.getLogger(AccountingAggregatorPlugin.class);

	public Bucket accountingBucket;
	protected Cluster cluster;

	public Aggregation aggregate; 

	public static final String AGGREGATED = "aggregated";
	private final static String LINE_FREFIX = "{";
	private final static String LINE_SUFFIX = "}";
	private final static String KEY_VALUE_PAIR_SEPARATOR = ",";
	private final static String KEY_VALUE_LINKER = "=";
	
	public static Integer countInsert=0;
	public static Integer countDelete=0;
	public static Integer RecoveryMode=0;
	
	/**
	 * @param runningPluginEvolution
	 */
	public AccountingAggregatorPlugin(AccountingAggregatorPluginDeclaration pluginDeclaration) {
		super(pluginDeclaration);		
	}

	/* The environment configuration */
	protected static final CouchbaseEnvironment ENV = 
			DefaultCouchbaseEnvironment.builder()
			.connectTimeout(Constant.CONNECTION_TIMEOUT * 1000) 
			.queryTimeout(Constant.CONNECTION_TIMEOUT * 1000)
			.keepAliveInterval(3600 * 1000) // 3600 Seconds in milliseconds			
			.build();

	/**{@inheritDoc}*/
	@Override
	public void launch(Map<String, Object> inputs) throws Exception {
		countInsert=0;
		countDelete=0;
		if(inputs == null || inputs.isEmpty()){
			logger.debug("{} inputs {}", this.getClass().getSimpleName(), inputs);
			throw new Exception("Inputs null");
		}
		//Type :HOURLY,DAILY,MONTHLY,YEARLY
		//Interval: Number of hour,day,month,year
		if (!inputs.containsKey("type") || !inputs.containsKey("interval"))
			throw new IllegalArgumentException("Interval and type must be defined");

		AggregationType aggType =AggregationType.valueOf((String)inputs.get("type"));
		Integer interval=(Integer)inputs.get("interval")* aggType.getMultiplierFactor();

		Integer inputStartTime=null;
		if (inputs.containsKey("startTime"))
			inputStartTime=(Integer)inputs.get("startTime");

		Boolean currentScope =false;
		String scope=null;
		if (inputs.containsKey("currentScope"))
			currentScope=(Boolean)inputs.get("currentScope");
		
		if (currentScope)
			scope=ScopeProvider.instance.get();
		
		if (inputs.containsKey("user"))
			Constant.user=(String)inputs.get("user");
		else
			Constant.user="service.aggregatorAccounting";

		if (inputs.containsKey("recovery"))
			RecoveryMode=(Integer)inputs.get("recovery");

		logger.debug("Launch with Type:{}, Interval:{}, startTime:{}, Scope:{}, Recovery:{}",aggType.toString(),interval,inputStartTime,scope,RecoveryMode);
		
		//Get Configuration from service end point
		String url=null;
		String password =null;
		List<String> listBucket=new ArrayList<String>();
		AggregatorPersistenceBackendQueryConfiguration configuration;
		try{
			configuration =	new AggregatorPersistenceBackendQueryConfiguration(PersistenceCouchBase.class);
			url = configuration.getProperty(ConfigurationServiceEndpoint.URL_PROPERTY_KEY);
			password = configuration.getProperty(ConfigurationServiceEndpoint.PASSWORD_PROPERTY_KEY);			
			if (inputs.containsKey("bucket"))
				listBucket.add(inputs.get("bucket").toString());
			else{
				listBucket.add(configuration.getProperty(ConfigurationServiceEndpoint.BUCKET_STORAGE_NAME_PROPERTY_KEY));
				listBucket.add(configuration.getProperty(ConfigurationServiceEndpoint.BUCKET_SERVICE_NAME_PROPERTY_KEY));
				listBucket.add(configuration.getProperty(ConfigurationServiceEndpoint.BUCKET_JOB_NAME_PROPERTY_KEY));
				listBucket.add(configuration.getProperty(ConfigurationServiceEndpoint.BUCKET_PORTLET_NAME_PROPERTY_KEY));
				listBucket.add(configuration.getProperty(ConfigurationServiceEndpoint.BUCKET_TASK_NAME_PROPERTY_KEY));
			}
		}
		catch (Exception e) {
			logger.error("launch",e.getLocalizedMessage());
			throw e;
		}
		Cluster cluster = CouchbaseCluster.create(ENV, url);
	
		//Define a type for aggregate
		RecordUtility.addRecordPackage(ServiceUsageRecord.class.getPackage());
		RecordUtility.addRecordPackage(AggregatedServiceUsageRecord.class.getPackage());

		initFolder();		
		
		
		if ((RecoveryMode==2)||(RecoveryMode==0)){
			logger.debug("Recovery mode enabled");
			RecoveryRecord.searchFile(cluster,configuration);
		}
		
		if (RecoveryMode!=2){
			for (String bucket:listBucket){
				logger.trace("OpenBucket:{}",bucket);
				accountingBucket = cluster.openBucket(bucket,password);
				//elaborate bucket, with scope, type aggregation and interval 
				elaborateBucket(bucket,scope, inputStartTime, interval, aggType);
			}

			logger.debug("Complete countInsert{}, countDelete{}",countInsert,countDelete);
		}
		
	}


	/**{@inheritDoc}*/
	@Override
	protected void onStop() throws Exception {
		logger.trace("{} onStop() function", this.getClass().getSimpleName());
		Thread.currentThread().interrupt();
	}


	/**
	 * Init folder for backup file
	 */
	public void initFolder(){
		Constant.PATH_DIR_BACKUP=System.getProperty(Constant.HOME_SYSTEM_PROPERTY)+"/"+Constant.NAME_DIR_BACKUP;
		Constant.PATH_DIR_BACKUP_INSERT=Constant.PATH_DIR_BACKUP+"/insert";
		Constant.PATH_DIR_BACKUP_DELETE=Constant.PATH_DIR_BACKUP+"/delete";
		File DirRoot = new File(Constant.PATH_DIR_BACKUP);
		if (!DirRoot.exists()) {
			DirRoot.mkdir();
		}
		logger.debug("init folder:{}",Constant.PATH_DIR_BACKUP);
		
		
	}
	
	/**
	 * Elaborate a Bucket from startTime to interval 
	 * @param bucket
	 * @param inputStartTime
	 * @param interval
	 * @param aggType
	 * @return
	 * @throws Exception
	 */
	protected boolean elaborateBucket(String bucket,String scope ,Integer inputStartTime,Integer interval,AggregationType aggType) throws Exception{

		SimpleDateFormat format = new SimpleDateFormat(aggType.getDateformat());
		//calculate a start time and end time for map reduce key
		Calendar now, nowTemp;
		if (inputStartTime==null){
			now= Calendar.getInstance();
			nowTemp= Calendar.getInstance();
		}else{
			now=Calendar.getInstance();
			nowTemp= Calendar.getInstance();			
			switch (aggType.name()) {
			case "YEARLY":
				now.add( Calendar.YEAR, -inputStartTime );
				nowTemp.add( Calendar.YEAR, -inputStartTime );
				break;
			case "MONTHLY":
				now.add( Calendar.MONTH, -inputStartTime );
				nowTemp.add( Calendar.MONTH, -inputStartTime );
				break;
			case "DAILY":
				now.add( Calendar.DATE, -inputStartTime );
				nowTemp.add( Calendar.DATE, -inputStartTime );
				break;
			case "HOURLY":
				now.add( Calendar.HOUR, -inputStartTime );
				nowTemp.add( Calendar.HOUR, -inputStartTime );
				break;
			}

		}
		String endAllKeyString = format.format(now.getTime());
		String endKeyString = format.format(now.getTime());


		//save a record modified into a file and save into a workspace 
		nowTemp.add(aggType.getCalendarField(), -1*interval);
		String startAllKeyString = format.format(nowTemp.getTime());
		WorkSpaceManagement.onSaveBackupFile(accountingBucket,bucket,scope,startAllKeyString, endAllKeyString,aggType);
		//logger.debug("Backup complete startKeyString{}, endKeyString{}",startAllKeyString,endAllKeyString);

		List<JsonDocument> documentElaborate=new ArrayList<JsonDocument>();

		for (int i=0; i<interval; i++){
			now.add(aggType.getCalendarField(), -1);
			String startKeyString = format.format(now.getTime());

			//init a json start,end key 
			JsonArray startKey = Utility.generateKey(scope,startKeyString);
			JsonArray endKey = Utility.generateKey(scope,endKeyString);

			DesignID designid=DesignID.valueOf(bucket);		
			String designDocId=designid.getNameDesign();

			String viewName="";
			if (scope!=null)
				viewName=designid.getNameViewScope();
			else
				viewName=designid.getNameView();

			ViewQuery query = ViewQuery.from(designDocId, viewName);
			query.startKey(startKey);
			query.endKey(endKey);
			query.reduce(false);
			query.inclusiveEnd(false);
			logger.debug("View Query: startKey:{} - endKey:{} designDocId:{} - viewName:{}",startKey, endKey,designDocId,viewName);

			ViewResult viewResult = null;
			try {
				viewResult = accountingBucket.query(query);

			} catch (Exception e) {
				logger.error("ERROR VIEW",e.getLocalizedMessage());
				//throw e;
			}

			// Iterate through the returned ViewRows
			aggregate = new Aggregation();
			documentElaborate.clear();

			for (ViewRow row : viewResult) 
				elaborateRow(row,documentElaborate);


			//File backup have a name with scope e 
			String nameFileBackup="";
			if (scope!=null)
				nameFileBackup=scope.replace("/", "")+"-"+startKeyString+"-"+endKeyString;
			else
				nameFileBackup=startKeyString+"-"+endKeyString;
			//save into db (delete no aggregate record and insert a record aggregate)
			reallyFlush(aggregate,documentElaborate,nameFileBackup);
			endKeyString = startKeyString;
		}
		return true;
	}



	/**
	 * Elaborate row for aggregate
	 * elaborateRow
	 * @param row
	 * @return
	 * @throws Exception
	 */	
	protected boolean elaborateRow(ViewRow row ,List<JsonDocument>  documentElaborate) throws Exception{

		try {
			//patch for field of long type  
			String document=row.value().toString().replace("\":", "=").replace("\"", "");
			
			Map<String,? extends Serializable> map = getMapFromString(document);
			
			@SuppressWarnings("rawtypes")			
			AggregatedRecord record = (AggregatedRecord)RecordUtility.getRecord(map);
			
			aggregate.aggregate(record);
			
			//insert an elaborate row into list JsonDocument for memory document elaborate
			
			String identifier=(String) row.document().content().get("id");
			
			JsonDocument documentJson = JsonDocument.create(identifier, row.document().content());
			
			documentElaborate.add(documentJson);
			
			return true;
		} 
		catch(InvalidValueException ex){
			logger.warn("Record is not valid. Anyway, it will be persisted");
			return true;
		}
		catch (Exception e) {
			
			logger.error("Error elaborateRow", e,e.getLocalizedMessage());			
			//throw e;
			return false;
		}

	}


	/**
	 * getMapFromString
	 * @param serializedMap
	 * @return
	 */
	protected static Map<String, ? extends Serializable> getMapFromString(String serializedMap){
		/* Checking line sanity */
    	if(!serializedMap.startsWith(LINE_FREFIX) && !serializedMap.endsWith(LINE_SUFFIX)){
    		return null;
    	}
    	
    	/* Cleaning prefix and suffix to parse line */
    	serializedMap = serializedMap.replace(LINE_FREFIX, "");
    	serializedMap = serializedMap.replace(LINE_SUFFIX, "");
    	
    	Map<String, Serializable> map = new HashMap<String,Serializable>();
    	
        String[] pairs = serializedMap.split(KEY_VALUE_PAIR_SEPARATOR);
        for (int i=0;i<pairs.length;i++) {
            String pair = pairs[i];
            pair.trim();
            
            String[] keyValue = pair.split(KEY_VALUE_LINKER);
            String key = keyValue[0].trim();
            Serializable value = keyValue[1].trim();
            map.put(key, value);           
        }       
        return map;
	}
	
	
	/**
	 * Delete a record not aggregate and insert a new record aggregate
	 * If a problem with delete record, not insert a new record and save a backupfile
	 * reallyFlush
	 * @param aggregate
	 * @param docs
	 * @param nameFile
	 * @return
	 * @throws Exception
	 */
	protected boolean reallyFlush(Aggregation aggregate,  List<JsonDocument> docs,String nameFile) throws Exception{
		if (docs.size()!=0){
			Integer index=0;
			boolean succesfulDelete=false;

			//before elaborate a record, create a backup file 
			List<JsonDocument> notDeleted = docs;
			List<JsonDocument> notInserted = aggregate.reallyFlush();

			nameFile =nameFile+"-"+UUID.randomUUID();
			ManagementFileBackup.getInstance().onCreateStringToFile(notDeleted,Constant.FILE_RECORD_NO_AGGREGATE+"_"+nameFile,false);
			ManagementFileBackup.getInstance().onCreateStringToFile(notInserted,Constant.FILE_RECORD_AGGREGATE+"_"+nameFile,true);

			while ((index < Constant.NUM_RETRY) && !succesfulDelete){
				List<JsonDocument> notDeletedTemp = new ArrayList<JsonDocument>();
				for (JsonDocument doc: notDeleted){	
					countDelete ++;
					try{
						accountingBucket.remove(doc.id(),PersistTo.MASTER,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
					}catch(Exception e){
						logger.trace("doc:{} not deleted retry:{}",doc.id(),index);
						try{
							if (accountingBucket.exists(doc.id()))
								notDeletedTemp.add(doc);
						}
						catch(Exception ex){
							logger.warn("doc:{} not verify for delete",doc.id());
						}
					}

				}
				if (notDeletedTemp.isEmpty()){
					succesfulDelete=true;					
				}
				else {
					index++;
					notDeleted = new ArrayList<JsonDocument>(notDeletedTemp);					
					Thread.sleep(1000);
				}
			}
			if (!succesfulDelete){
				logger.error("Error Delete record");
			} 
			logger.debug("Delete complete {}, Start a insert aggregated document",countDelete);
			/**
			 * delete all record and ready for insert a new aggregated record
			 */
			if (succesfulDelete){
				//if successful record delete, delete backup file 
				ManagementFileBackup.getInstance().onDeleteFile(Constant.FILE_RECORD_NO_AGGREGATE+"_"+nameFile,false);
				index=0;
				boolean succesfulInsert=false;
				while ((index < Constant.NUM_RETRY) && !succesfulInsert){
					List<JsonDocument> notInsertedTemp = new ArrayList<JsonDocument>();
					for (JsonDocument document: notInserted){
						countInsert ++;
						try{
							@SuppressWarnings("unused")
							JsonDocument response = accountingBucket.upsert(document,PersistTo.MASTER,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
						}
						catch(Exception e){
							logger.trace("record:{} not insert retry:{} ",document.id(),index);
							try{
								if (!accountingBucket.exists(document.id()))
									notInsertedTemp.add(document);									
							}
							catch(Exception ex){
								logger.warn("doc:{} not verify for inset",document.id());
							}
						}
					}
					if (notInsertedTemp.isEmpty()){							
							succesfulInsert=true;
					}
					else {
						index++;														
						notInserted = new ArrayList<JsonDocument>(notInsertedTemp);						
						Thread.sleep(1000);							
					}						
				}
				if (!succesfulInsert){
					//do something clever with the exception
					logger.error("Error Insert record{}");
				} else{
					logger.debug("elaborate record aggregate:{} and record not aggregate:{}",countInsert, countDelete);	
					ManagementFileBackup.getInstance().onDeleteFile(Constant.FILE_RECORD_AGGREGATE+"_"+nameFile,true);
				}

			}		
		}
		return true;
	}
}

