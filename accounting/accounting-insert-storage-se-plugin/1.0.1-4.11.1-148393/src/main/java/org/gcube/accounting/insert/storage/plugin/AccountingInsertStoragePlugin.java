package org.gcube.accounting.insert.storage.plugin;

import java.util.Map;
import java.util.UUID;

import org.gcube.accounting.insert.storage.persistence.AggregatorPersistenceBackendQueryConfiguration;
import org.gcube.accounting.insert.storage.utils.ConfigurationServiceEndpoint;
import org.gcube.accounting.insert.storage.utils.Constant;
import org.gcube.accounting.insert.storage.utils.DiscoveryListUser;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.documentstore.persistence.PersistenceCouchBase;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;



/**
 * @author Alessandro Pieve (ISTI - CNR) 
 *
 */
public class AccountingInsertStoragePlugin extends Plugin<AccountingInsertStoragePluginDeclaration> {

	private static Logger logger = LoggerFactory.getLogger(AccountingInsertStoragePlugin.class);

	public AccountingInsertStoragePlugin(AccountingInsertStoragePluginDeclaration pluginDeclaration) {
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

		String context=ScopeProvider.instance.get();
		logger.debug("AccountingInsertStoragePlugin: launch on context:{}",context);
		String url=null;
		String password =null;
		String bucket=null;
		AggregatorPersistenceBackendQueryConfiguration configuration;
		try{
			configuration =	new AggregatorPersistenceBackendQueryConfiguration(PersistenceCouchBase.class);
			url = configuration.getProperty(ConfigurationServiceEndpoint.URL_PROPERTY_KEY);
			password = configuration.getProperty(ConfigurationServiceEndpoint.PASSWORD_PROPERTY_KEY);			
			bucket=configuration.getProperty(ConfigurationServiceEndpoint.BUCKET_STORAGE_NAME_PROPERTY_KEY);

		}
		catch (Exception e) {
			logger.error("AccountingInsertStoragePlugin: launch",e.getLocalizedMessage());
			throw e;
		}
		Cluster cluster = CouchbaseCluster.create(ENV, url);
		logger.debug("AccountingInsertStoragePlugin: create cluster");
		Bucket accountingBucket = cluster.openBucket(bucket,password);

		String urlService=null;
		if (inputs.containsKey("urlService"))
			urlService=(String) inputs.get("urlService");

		String dataServiceClass="content-management";
		if (inputs.containsKey("dataServiceClass"))
			dataServiceClass=(String) inputs.get("dataServiceClass");

		String dataServiceName="storage-manager";	
		if (inputs.containsKey("dataServiceName"))
			dataServiceName=(String) inputs.get("dataServiceName");

		String dataServiceId="identifier";
		if (inputs.containsKey("dataServiceId"))
			dataServiceId=(String) inputs.get("dataServiceId");

		String uri="MongoDb";
		if (inputs.containsKey("uri"))
			uri=(String) inputs.get("uri");

		String dataType="STORAGE";
		if (inputs.containsKey("dataType"))
			dataType=(String) inputs.get("dataType");

		Integer timeWait=1000;
		if (inputs.containsKey("timeWait"))
			timeWait=(Integer) inputs.get("timeWait");

		DiscoveryListUser discoveryListUser= new DiscoveryListUser(context,urlService);			
		//list user
		JsonDocument document=null;
		JsonObject content=null;
		for (String consumerId:discoveryListUser.getListUser()){
			//for each user call homelibrary and insert
			IClient client=new StorageClient("", "", consumerId, AccessType.PUBLIC, MemoryType.PERSISTENT).getClient();
			try{
				String docId=UUID.randomUUID().toString();

				Long dataVolume= Long.parseLong(client.getTotalUserVolume());
				Long dataCount=Long.parseLong(client.getUserTotalItems());
				Long timeStamp= System.currentTimeMillis();

				content = JsonObject.empty().put("scope", context);
				content.put("operationCount", 1);
				content.put("dataCount", dataCount);
				content.put("endTime", timeStamp);
				content.put("consumerId", consumerId);
				content.put("startTime", timeStamp);
				content.put("id", docId);
				content.put("dataVolume", dataVolume);
				content.put("dataType", dataType);
				content.put("operationResult", "SUCCESS");
				content.put("dataServiceClass", dataServiceClass);
				content.put("dataServiceName", dataServiceName);
				content.put("dataServiceId", dataServiceId);
				content.put("aggregated", true);
				content.put("providerId", uri);
				content.put("creationTime", timeStamp);
				content.put("recordType", "StorageStatusRecord");
				document = JsonDocument.create("docId", content);

				JsonDocument doc = JsonDocument.create(docId, content);

				JsonDocument response = accountingBucket.upsert(doc);
				logger.debug("AccountingInsertStoragePlugin: upsert doc:{}",doc.toString());
				Thread.sleep(timeWait);


			}
			catch(Exception e){
				logger.error("AccountingInsertStoragePlugin:  doc:{} not insert ({}), problem with exist bucket",document.id(),document.toString(),e);
				logger.error("AccountingInsertStoragePlugin:  force insert into list for insert");		
			}


		}
		cluster.disconnect();
		logger.debug("AccountingInsertStoragePlugin:  insert complete");
	}





	/**{@inheritDoc}*/
	@Override
	protected void onStop() throws Exception {
		logger.trace("AccountingInsertStoragePlugin: {} onStop() function", this.getClass().getSimpleName());
		Thread.currentThread().interrupt();
	}









}



