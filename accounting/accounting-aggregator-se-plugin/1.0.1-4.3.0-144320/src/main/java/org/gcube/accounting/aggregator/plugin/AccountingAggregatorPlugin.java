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
import org.gcube.documentstore.records.Record;
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
	public static Integer recoveryMode=0;
	public Boolean backup=true;
	//value if 0 PersistTo.MASTER if 1 PersistTo.ONE
	public static Integer typePersisted=0;


	protected PersistTo persisted ;
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
			.maxRequestLifetime(Constant.MAX_REQUEST_LIFE_TIME * 1000)
			.queryTimeout(Constant.CONNECTION_TIMEOUT * 1000)	//15 Seconds in milliseconds
			.viewTimeout(Constant.VIEW_TIMEOUT_BUCKET * 1000)//120 Seconds in milliseconds			
			.keepAliveInterval(3600 * 1000) // 3600 Seconds in milliseconds
			.kvTimeout(5000) //in ms
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

		//new feature for not elaborate the full range but a set of small intervals
		if (inputs.containsKey("intervalStep"))
			interval=(Integer)inputs.get("intervalStep");

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
			recoveryMode=(Integer)inputs.get("recovery");

		if (inputs.containsKey("backup"))
			backup=(Boolean)inputs.get("backup");

		if (inputs.containsKey("typePersisted"))
			typePersisted=(Integer)inputs.get("typePersisted");
		switch(typePersisted) {
		case 0:
			persisted=PersistTo.MASTER;
			break;
		case 1:
			persisted=PersistTo.ONE;
			break;
		default:
			persisted=PersistTo.MASTER;
		}

		logger.debug("-Launch with Type:{}, Interval:{}, startTime:{}, Scope:{}, Recovery:{}",aggType.toString(),interval,inputStartTime,scope,recoveryMode);
		logger.debug("persist:{} backup:{}",persisted.toString(),backup);
		if(!backup){
			logger.warn("Attention backup disabled");
			Thread.sleep(20000);
		}
		if (inputs.containsKey("intervalStep")){
			logger.debug("Interval is not considered, aggregate only :{} step",interval);
		}

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


		if ((recoveryMode==2)||(recoveryMode==0)){
			logger.debug("Recovery mode enabled");
			RecoveryRecord.searchFile(cluster,configuration);
		}

		if (recoveryMode!=2){
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
		if (backup){
			logger.debug("Start Backup");
			WorkSpaceManagement.onSaveBackupFile(accountingBucket,bucket,scope,startAllKeyString, endAllKeyString,aggType);
			//logger.debug("Backup complete startKeyString{}, endKeyString{}",startAllKeyString,endAllKeyString);
		}
		else
			logger.debug("No Backup required");

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
			logger.debug("--{}/{} View Query: startKey:{} - endKey:{} designDocId:{} - viewName:{}",i,interval,startKey, endKey,designDocId,viewName);
			ViewResult viewResult = null;
			try {
				viewResult = accountingBucket.query(query);

			} catch (Exception e) {
				logger.error("Exception error VIEW",e.getLocalizedMessage(),e);
				//throw e;
			}

			// Iterate through the returned ViewRows
			aggregate = new Aggregation();
			documentElaborate.clear();
			logger.debug("Start elaborate row");
			Boolean resultElaborate=false;
			for (ViewRow row : viewResult) 
				resultElaborate=elaborateRow(row,documentElaborate);
			logger.debug("End elaborate row");
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
	protected Boolean elaborateRow(ViewRow row ,List<JsonDocument>  documentElaborate) throws Exception{
		int i=0;	
		JsonDocument documentJson = null;
		try {
			//patch for field of long type  
			String document=row.value().toString().replace("\":", "=").replace("\"", "");
			i=1;//1
			Map<String,? extends Serializable> map = getMapFromString(document);
			i=2;//2
			//prepare a document for elaborate
			String identifier=(String) row.document().content().get("id");
			i=3;//3
			documentJson = JsonDocument.create(identifier, row.document().content());
			i=4;//4

			@SuppressWarnings("rawtypes")			
			AggregatedRecord record = (AggregatedRecord)RecordUtility.getRecord(map);
			i=5;//5
			aggregate.aggregate(record);
			i=6;//6			
			//insert an elaborate row into list JsonDocument for memory document elaborate
			documentElaborate.add(documentJson);
			i=7;//7
			return true;
		} 
		catch(InvalidValueException ex){
			logger.warn("InvalidValueException - Record is not valid. Anyway, it will be persisted i:{}",i);
			logger.warn("Runtime Exception ex",ex);
			if ((i==5)&&(documentJson!=null)){
				documentElaborate.add(documentJson);
			}
			return false;
		}
		catch(RuntimeException exr){
			logger.warn("Runtime Exception -Record is not valid. Anyway, it will be persisted i:{}",i);		
			logger.warn("Runtime Exception exr",exr);			
			if ((i==5)&&(documentJson!=null)){
				documentElaborate.add(documentJson);
				logger.debug("Record is elaborate");
			}
			return false;
		}
		catch (Exception e) {
			logger.error("record is not elaborated:"+row.toString()+" but it will be persisted");
			logger.error("error elaborateRow", e);
			logger.error("i:{}",i);
			if ((i==5)&&(documentJson!=null)){
				documentElaborate.add(documentJson);
				logger.debug("Record is elaborate");
			}
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
			logger.trace("Start a delete document:{}",docs.size());
			//before elaborate a record, create a backup file 
			List<JsonDocument> notDeleted = docs;
			List<JsonDocument> notInserted = aggregate.reallyFlush();

			nameFile =nameFile+"-"+UUID.randomUUID();
			ManagementFileBackup.getInstance().onCreateStringToFile(notDeleted,Constant.FILE_RECORD_NO_AGGREGATE+"_"+nameFile,false);
			ManagementFileBackup.getInstance().onCreateStringToFile(notInserted,Constant.FILE_RECORD_AGGREGATE+"_"+nameFile,true);
			List<JsonDocument> notDeletedTemp = null;
			while ((index < Constant.NUM_RETRY) && !succesfulDelete){
				notDeletedTemp = new ArrayList<JsonDocument>();
				for (JsonDocument doc: notDeleted){	
					if (index>0){
						logger.trace("delete Start {} pass",index);
					}
					countDelete ++;
					try{
						accountingBucket.remove(doc.id(),persisted,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
					}
					catch(Exception e){
						logger.warn("doc:{} not deleted retry:{} for error:{}",doc.id(),index,e);
						Thread.sleep(1500);
						try{
							if (accountingBucket.exists(doc.id()))
								notDeletedTemp.add(doc);
						}
						catch(Exception ext){
							logger.warn("doc:{} not verify for delete because timeout, retry:{}",doc.id(),index,ext);
							Thread.sleep(3000);
							try{
								if (accountingBucket.exists(doc.id()))
									notDeletedTemp.add(doc);
							}
							catch(Exception ex)	{
								logger.error("doc:{} not delete ({}), problem with exist bucket",doc.id(),doc.toString(),ex);
								logger.error("force insert into list for delete");
								notDeletedTemp.add(doc);
							}
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
					logger.trace("First pass no delete all succesfulDelete:{} index:{}",succesfulDelete,index);
				}
			}
			if (!succesfulDelete){
				logger.error("Error Delete record");
			}
			
			logger.debug("notDeletedTemp size:{} notDeleted:{}",notDeletedTemp.size(),notDeleted.size());
			logger.debug("Delete complete:{}, Start a insert aggregated document:{}",countDelete,notInserted.size());
			// delete all record and ready for insert a new aggregated record			 
			if (succesfulDelete){
				//if successful record delete, delete backup file 
				ManagementFileBackup.getInstance().onDeleteFile(Constant.FILE_RECORD_NO_AGGREGATE+"_"+nameFile,false);
				index=0;
				boolean succesfulInsert=false;
				while ((index < Constant.NUM_RETRY) && !succesfulInsert){
					List<JsonDocument> notInsertedTemp = new ArrayList<JsonDocument>();
					for (JsonDocument document: notInserted){
						if (index>0){
							logger.trace("insert Start {} pass for document:{}",index,document.toString());
						}
						countInsert ++;
						try{
							//JsonDocument response = accountingBucket.upsert(document,PersistTo.MASTER,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
							JsonDocument response = accountingBucket.upsert(document,persisted,Constant.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
						}
						catch(Exception e){
							logger.warn("record:{} not insert retry:{}  for error:{}",document.id(),index,e);
							Thread.sleep(1500);
							try{

								if (!accountingBucket.exists(document.id()))
									notInsertedTemp.add(document);									
							}
							catch(Exception ext){
								logger.warn("doc:{} not verify for insert because timeout, retry",document.id(),ext);
								Thread.sleep(3000);
								try{
									if (!accountingBucket.exists(document.id()))
										notInsertedTemp.add(document);
								}
								catch(Exception ex)	{
									logger.error("doc:{} not insert ({}), problem with exist bucket",document.id(),document.toString(),ex);
									logger.error("force insert into list for insert");									
									notInsertedTemp.add(document);
								}
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
						logger.trace("First pass no insert all succesfulInsert:{} index:{}",succesfulInsert,index);
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
			logger.trace("Insert complete");
		}
		return true;
	}
}

