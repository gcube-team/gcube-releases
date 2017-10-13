package org.gcube.accounting.insert.rstudio.plugin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

import org.gcube.accounting.insert.rstudio.persistence.AggregatorPersistenceBackendQueryConfiguration;
import org.gcube.accounting.insert.rstudio.utils.ConfigurationServiceEndpoint;
import org.gcube.accounting.insert.rstudio.utils.Constant;
import org.gcube.common.scope.api.ScopeProvider;
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
public class AccountingInsertRstudioPlugin extends Plugin<AccountingInsertRstudioPluginDeclaration> {

	private static Logger logger = LoggerFactory.getLogger(AccountingInsertRstudioPlugin.class);



	public AccountingInsertRstudioPlugin(AccountingInsertRstudioPluginDeclaration pluginDeclaration) {
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
		logger.debug("AccountingInsertRstudioPlugin launch on context:{}",context);
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
			logger.error("AccountingInsertRstudioPlugin launch",e.getLocalizedMessage());
			throw e;
		}

		logger.debug("AccountingInsertRstudioPlugin open cluster:{}",context);
		Cluster cluster = CouchbaseCluster.create(ENV, url);
		Bucket accountingBucket = cluster.openBucket(bucket,password);

		String dataServiceClass="content-management";
		if (inputs.containsKey("dataServiceClass"))
			dataServiceClass=(String) inputs.get("dataServiceClass");

		String dataServiceName="storage-manager";	
		if (inputs.containsKey("dataServiceName"))
			dataServiceName=(String) inputs.get("dataServiceName");

		String dataServiceId="";

		String uri="RStudio";
		if (inputs.containsKey("uri"))
			uri=(String) inputs.get("uri");

		String dataType="STORAGE";
		if (inputs.containsKey("dataType"))
			dataType=(String) inputs.get("dataType");

		Integer timeWait=1000;
		if (inputs.containsKey("timeWait"))
			timeWait=(Integer) inputs.get("timeWait");

		String pathFile=null;
		if (inputs.containsKey("pathFile"))
			pathFile=(String) inputs.get("pathFile");

		if (pathFile==null){
			throw new IllegalArgumentException(
					"Path File is null!!");
		}

		JsonDocument document=null;
		JsonObject content=null;

		BufferedReader reader = new BufferedReader(new FileReader(pathFile));
		String line;
		while ((line = reader.readLine()) != null)
		{
			line=line.trim();
			logger.debug("AccountingInsertRstudioPlugin line :{}",line);

			String[] infoUser=line.split("\\s+");
			String user=infoUser[1];
			Long dataVolume= Long.parseLong(infoUser[0]);
			if (inputs.containsKey("unitVolume")){
				
				switch ((String) inputs.get("unitVolume")) {
	            case "Kilobyte":
	            	dataVolume=kilToByte(dataVolume);
	            	break;
	            case "Megabyte":
	            	dataVolume=megaToByte(dataVolume);
	            	break;
	            	
				}
			}
			try{
				String docId=UUID.randomUUID().toString();
				logger.debug("AccountingInsertRstudioPlugin User:{} dataVolume:{} KB",user,bytesToKil(dataVolume));
				logger.debug("AccountingInsertRstudioPlugin User:{} dataVolume:{} bytes",user,dataVolume);
				dataServiceId=InetAddress.getLocalHost().getHostName();
				Long dataCount=0l;
				Long timeStamp= System.currentTimeMillis();
				content = JsonObject.empty().put("scope", context);
				content.put("operationCount", 1);
				content.put("dataCount", dataCount);
				content.put("endTime", timeStamp);
				content.put("consumerId", user);
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
				logger.debug("upsert doc:{}",doc.toString());
				Thread.sleep(timeWait);
			}
			catch(Exception e){
				logger.error("doc:{} not insert ({}), problem with exist bucket",document.id(),document.toString(),e);
				logger.error("force insert into list for insert");		
			}

		}
		logger.trace("AccountingInsertRstudioPlugin close");		
		reader.close();
		cluster.disconnect();
	}



	/**{@inheritDoc}*/
	@Override
	protected void onStop() throws Exception {
		logger.trace("{} onStop() function", this.getClass().getSimpleName());
		Thread.currentThread().interrupt();
	}

	private static long bytesToMeg(long bytes) {
		return bytes / Constant.MEGABYTE;
	}

	private static long bytesToKil(long bytes) {
		return bytes / Constant.KILOBYTE ;
	}

	private static long kilToByte(long kilobytes) {
		return kilobytes * Constant.KILOBYTE;
	}
	
	private Long megaToByte(Long megaToByte) {
		return megaToByte * Constant.MEGABYTE;
	}



}



