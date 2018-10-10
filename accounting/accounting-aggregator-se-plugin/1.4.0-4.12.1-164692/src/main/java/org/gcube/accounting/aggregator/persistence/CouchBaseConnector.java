package org.gcube.accounting.aggregator.persistence;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.gcube.accounting.aggregator.aggregation.AggregationType;
import org.gcube.accounting.aggregator.status.AggregationState;
import org.gcube.accounting.aggregator.status.AggregationStatus;
import org.gcube.accounting.aggregator.utility.Constant;
import org.gcube.accounting.aggregator.utility.Utility;
import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.accounting.datamodel.UsageRecord;
import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.PersistTo;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.error.DocumentAlreadyExistsException;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class CouchBaseConnector {
	
	private static Logger logger = LoggerFactory.getLogger(CouchBaseConnector.class);
	
	public static final long MAX_REQUEST_LIFE_TIME = TimeUnit.SECONDS.toMillis(120);
	public static final long KEEP_ALIVE_INTERVAL = TimeUnit.HOURS.toMillis(1);
	public static final long AUTO_RELEASE_AFTER = TimeUnit.HOURS.toMillis(1);
	public static final long VIEW_TIMEOUT_BUCKET = TimeUnit.SECONDS.toMillis(120);
	public static final long CONNECTION_TIMEOUT_BUCKET = TimeUnit.SECONDS.toMillis(15);
	public static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(15);
	
	private static final String URL_PROPERTY_KEY = "URL";
	private static final String PASSWORD_PROPERTY_KEY = "password";
	
	public final static String ACCOUNTING_MANAGER_BUCKET_NAME = "AccountingManager";
	
	/* The environment configuration */
	protected static final CouchbaseEnvironment ENV;
	protected static final PersistTo PERSIST_TO;
	
	static {
		ENV = DefaultCouchbaseEnvironment.builder()
			.connectTimeout(CouchBaseConnector.CONNECTION_TIMEOUT)
			.maxRequestLifetime(CouchBaseConnector.MAX_REQUEST_LIFE_TIME)
			.queryTimeout(CouchBaseConnector.CONNECTION_TIMEOUT)
			.viewTimeout(CouchBaseConnector.VIEW_TIMEOUT_BUCKET)
			.keepAliveInterval(CouchBaseConnector.KEEP_ALIVE_INTERVAL)
			.kvTimeout(5000)
			.autoreleaseAfter(CouchBaseConnector.AUTO_RELEASE_AFTER).build();

		PERSIST_TO = PersistTo.MASTER;

	}
	
	protected static CouchBaseConnector couchBaseConnector;
	
	protected AggregatorPersitenceConfiguration configuration;
	protected Cluster cluster;
	protected Map<String,Bucket> connectionMap;
	protected Map<String, Class<? extends Record>> recordTypeMap;
	
	public synchronized static CouchBaseConnector getInstance() throws Exception{
		if(couchBaseConnector==null){
			couchBaseConnector = new CouchBaseConnector();
		}
		return couchBaseConnector;
	}
	
	protected CouchBaseConnector() throws Exception {
		this.configuration = new AggregatorPersitenceConfiguration(AggregatorPersistence.class);
		this.cluster = getCluster();
		createConnectionMap();
	}
	
	
	private Cluster getCluster() throws Exception {
		try {
			String url = configuration.getProperty(URL_PROPERTY_KEY);
			return CouchbaseCluster.create(ENV, url);
		} catch (Exception e) {
			throw e;
		}
	}

	public static enum SUFFIX {
		src, dst
	};
	
	private static String getBucketKey(String recordType, AggregationType aggregationType, SUFFIX suffix){
		return recordType + "-" + aggregationType.name() + "-" + suffix.name();
	}
	
	private Map<String,Bucket> createConnectionMap() throws Exception {
		connectionMap = new HashMap<>();
		recordTypeMap = new HashMap<>();
		
		try {
			Bucket b = cluster.openBucket(
					ACCOUNTING_MANAGER_BUCKET_NAME,
					configuration.getProperty(PASSWORD_PROPERTY_KEY));
			connectionMap.put(ACCOUNTING_MANAGER_BUCKET_NAME, b);
		}catch (Exception e) {
			logger.error("Unable to open Bucket used for Accounting Aggregation Management", e);
			throw e;
		}
		
		Map<String, Class<? extends Record>> recordClasses = RecordUtility.getRecordClassesFound();
		for (Class<? extends Record> recordClass : recordClasses.values()) {
			Record recordInstance = recordClass.newInstance(); 
			if (recordInstance instanceof UsageRecord && !(recordInstance instanceof AggregatedUsageRecord<?,?>)) {
				String recordType = recordInstance.getRecordType();
				recordTypeMap.put(recordType, recordClass);
				
				for(AggregationType aggregationType : AggregationType.values()){
					for(SUFFIX suffix : SUFFIX.values()){
						logger.debug("Trying to get the Bucket for {} {} {}", suffix, recordType, aggregationType);
						String bucketKey = getBucketKey(recordType, aggregationType, suffix);
						String bucketName = configuration.getProperty(bucketKey);
						logger.debug("Bucket for {} {} {} is {}. Going to open it.", suffix, recordType, aggregationType, bucketName);
						try {
							Bucket bucket = cluster.openBucket(bucketName, configuration.getProperty(PASSWORD_PROPERTY_KEY));
							connectionMap.put(bucketKey, bucket);
						}catch (Exception e) {
							logger.warn("Unable to open Bucket {} for {} {} {}. This normally means that is not configured.", bucketName, suffix, recordType, aggregationType, recordClass);
						}
					}
				}
			}
		}
		
		return connectionMap;
	}
	
	public Set<String> getConnectionMapKeys(){
		return connectionMap.keySet();
	}
	
	public Set<String> getRecordTypes(){
		return recordTypeMap.keySet();
	}
	
	public Bucket getBucket(String recordType, AggregationType aggregationType, SUFFIX suffix){
		return connectionMap.get(getBucketKey(recordType, aggregationType, suffix));
	}
	
	public static AggregationStatus getLast(String recordType, AggregationType aggregationType, Date aggregationStartDate, Date aggregationEndDate) throws Exception{
		Bucket bucket = CouchBaseConnector.getInstance().connectionMap.get(CouchBaseConnector.ACCOUNTING_MANAGER_BUCKET_NAME);
		
		/*
		 * SELECT * 
		 * FROM AccountingManager
		 * WHERE 
		 * 		`aggregationInfo`.`recordType` = "ServiceUsageRecord" AND 
		 * 		`aggregationInfo`.`aggregationType` = "DAILY" AND
		 * 		`aggregationInfo`.`aggregationStartDate` >= "2017-05-01 00:00:00.000 +0000"
		 * 		`aggregationInfo`.`aggregationStartDate` <= "2017-05-31 00:00:00.000 +0000"
		 * ORDER BY `aggregationInfo`.`aggregationStartDate` DESC LIMIT 1
		 */
		
		Expression expression = x("`aggregationInfo`.`recordType`").eq(s(recordType));
		expression = expression.and(x("`aggregationInfo`.`aggregationType`").eq(s(aggregationType.name())));
		
		
		String aggregationStartDateField = "`aggregationInfo`.`aggregationStartDate`";
		if(aggregationStartDate!=null){
			expression = expression.and(x(aggregationStartDateField).gte(s(Constant.DEFAULT_DATE_FORMAT.format(aggregationStartDate))));
		}
		
		if(aggregationEndDate!=null){
			expression = expression.and(x(aggregationStartDateField).lte(s(Constant.DEFAULT_DATE_FORMAT.format(aggregationEndDate))));
		}
		
		Sort sort = Sort.desc(aggregationStartDateField);
		
		Statement statement = select("*").from(bucket.name()).where(expression).orderBy(sort).limit(1);
		
		logger.trace("Going to query : {}", statement.toString());
		
		N1qlQueryResult result = bucket.query(statement);
		if (!result.finalSuccess()) {
			logger.debug("{} failed : {}", N1qlQueryResult.class.getSimpleName(), result.errors());
			return null;
		}
		
		List<N1qlQueryRow> rows = result.allRows();
		
		if(rows.size()>1){
			String error = String.format("More than one Document found for query %. This is really strange and should not occur. Please contact the Administrator.", statement.toString());
			logger.error(error);
			throw new Exception(error);
		}

		if(rows.size()==1){
			N1qlQueryRow row = rows.get(0);
			try {
				JsonObject jsonObject = row.value().getObject(bucket.name());
				logger.trace("JsonObject : {}", jsonObject.toString());
				return DSMapper.getObjectMapper().readValue(jsonObject.toString(), AggregationStatus.class);
			} catch (Exception e) {
				logger.warn("Unable to elaborate result for {}", row.toString());
			}
		}
		
		return null;
	}
	
	public static List<AggregationStatus> getUnterminated(Date aggregationStartDate, Date aggregationEndDate) throws Exception{
		return getUnterminated(null, null, aggregationStartDate, aggregationEndDate);
	}
	
	public static List<AggregationStatus> getUnterminated(String recordType, AggregationType aggregationType, Date aggregationStartDate, Date aggregationEndDate) throws Exception{
		Bucket bucket = CouchBaseConnector.getInstance().connectionMap.get(CouchBaseConnector.ACCOUNTING_MANAGER_BUCKET_NAME);
		
		/*
		 * SELECT * 
		 * FROM AccountingManager
		 * WHERE 
		 * 		`aggregationState` != "COMPLETED" AND
		 * 		`lastUpdateTime` < "2017-07-31 09:31:10.984 +0000" AND
		 * 		`aggregationInfo`.`recordType` = "ServiceUsageRecord" AND 
		 * 		`aggregationInfo`.`aggregationType` = "DAILY" AND
		 * 		`aggregationInfo`.`aggregationStartDate` >= "2017-05-01 00:00:00.000 +0000"
		 * 		`aggregationInfo`.`aggregationStartDate` <= "2017-05-31 00:00:00.000 +0000"
		 * 		
		 * ORDER BY `aggregationInfo`.`aggregationStartDate` ASC
		 */
		
		Calendar now = Utility.getUTCCalendarInstance();
		now.add(Constant.CALENDAR_FIELD_TO_SUBSTRACT_TO_CONSIDER_UNTERMINATED, -Constant.UNIT_TO_SUBSTRACT_TO_CONSIDER_UNTERMINATED);
		
		Expression expression = x("`aggregationState`").ne(s(AggregationState.COMPLETED.name())); 
		expression = expression.and(x("`lastUpdateTime`").lt(s(Constant.DEFAULT_DATE_FORMAT.format(now.getTime()))));
		
		if(recordType!=null){
			expression = expression.and(x("`aggregationInfo`.`recordType`").eq(s(recordType)));
		}
		
		if(aggregationType!=null){
			expression = expression.and(x("`aggregationInfo`.`aggregationType`").eq(s(aggregationType.name())));
		}
		
		String aggregationStartDateField = "`aggregationInfo`.`aggregationStartDate`";
		if(aggregationStartDate!=null){
			expression = expression.and(x(aggregationStartDateField).gte(s(Constant.DEFAULT_DATE_FORMAT.format(aggregationStartDate))));
		}
		
		if(aggregationEndDate!=null){
			expression = expression.and(x(aggregationStartDateField).lte(s(Constant.DEFAULT_DATE_FORMAT.format(aggregationEndDate))));
		}
		
		Sort sort = Sort.asc(aggregationStartDateField);
		
		Statement statement = select("*").from(bucket.name()).where(expression).orderBy(sort);
		
		logger.trace("Going to query : {}", statement.toString());
		
		N1qlQueryResult result = bucket.query(statement);
		if (!result.finalSuccess()) {
			logger.debug("{} failed : {}", N1qlQueryResult.class.getSimpleName(), result.errors());
			return null;
		}
		
		List<N1qlQueryRow> rows = result.allRows();
		List<AggregationStatus> aggregationStatuses = new ArrayList<>(rows.size());
		for(N1qlQueryRow row: rows){
			try {
				JsonObject jsonObject = row.value().getObject(bucket.name());
				logger.trace("JsonObject : {}", jsonObject.toString());
				AggregationStatus aggregationStatus = DSMapper.getObjectMapper().readValue(jsonObject.toString(), AggregationStatus.class);
				aggregationStatuses.add(aggregationStatus);
			} catch (Exception e) {
				logger.warn("Unable to elaborate result for {}", row.toString());
			}
		}
		
		return aggregationStatuses;
		
	}
	
	public static AggregationStatus getAggregationStatus(String recordType, AggregationType aggregationType, Date aggregationStartDate) throws Exception{
		Bucket bucket = CouchBaseConnector.getInstance().connectionMap.get(CouchBaseConnector.ACCOUNTING_MANAGER_BUCKET_NAME);
		
		/*
		 * SELECT * 
		 * FROM AccountingManager 
		 * WHERE 
		 * 		`aggregationInfo`.`recordType` = "ServiceUsageRecord" AND 
		 * 		`aggregationInfo`.`aggregationType` = "DAILY" AND 
		 * 		`aggregationInfo`.`aggregationStartDate` = "2017-06-24 00:00:00.000 +0000"
		 */
		
		Expression expression = x("`aggregationInfo`.`recordType`").eq(s(recordType));
		expression = expression.and(x("`aggregationInfo`.`aggregationType`").eq(s(aggregationType.name())));
		
		expression = expression.and(x("`aggregationInfo`.`aggregationStartDate`").eq(s(Constant.DEFAULT_DATE_FORMAT.format(aggregationStartDate))));

		Statement statement = select("*").from(bucket.name()).where(expression);
		
		logger.trace("Going to query : {}", statement.toString());
		
		N1qlQueryResult result = bucket.query(statement);
		if (!result.finalSuccess()) {
			logger.debug("{} failed : {}", N1qlQueryResult.class.getSimpleName(), result.errors());
			return null;
		}
		
		List<N1qlQueryRow> rows = result.allRows();
		
		if(rows.size()>1){
			String error = String.format("More than one Document found for query %s. This is really strange and should not occur. Please contact the Administrator.", statement.toString());
			logger.error(error);
			throw new Exception(error);
		}

		if(rows.size()==1){
			N1qlQueryRow row = rows.get(0);
			try {
				JsonObject jsonObject = row.value().getObject(bucket.name());
				logger.trace("JsonObject : {}", jsonObject.toString());
				return DSMapper.getObjectMapper().readValue(jsonObject.toString(), AggregationStatus.class);
			} catch (Exception e) {
				logger.warn("Unable to elaborate result for {}", row.toString());
			}
		}
		
		return null;
	}
	
	
	public static void upsertAggregationStatus(AggregationStatus aggregationStatus) throws Exception{
		Bucket bucket = CouchBaseConnector.getInstance().connectionMap.get(CouchBaseConnector.ACCOUNTING_MANAGER_BUCKET_NAME);
		JsonObject jsonObject = JsonObject.fromJson(DSMapper.getObjectMapper().writeValueAsString(aggregationStatus));
		JsonDocument jsonDocument = JsonDocument.create(aggregationStatus.getUUID().toString(), jsonObject);
		try{
			bucket.upsert(jsonDocument, PersistTo.MASTER, CouchBaseConnector.CONNECTION_TIMEOUT_BUCKET, TimeUnit.SECONDS);
		}catch (DocumentAlreadyExistsException e) {
			// OK it can happen when the insert procedure were started but was interrupted
		}
	}
}
