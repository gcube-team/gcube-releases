package org.gcube.documentstore.persistence;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.documentstore.persistence.HTTPCall.HTTPMETHOD;
import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 * @author Luca Frosini (ISTI - CNR) luca.frosini@isti.cnr.it
 */
public class PersistenceAccountingService extends PersistenceBackend {

	private static final Logger logger = LoggerFactory.getLogger(PersistenceAccountingService.class);

	public static final String PATH_SERVICE_INSERT_ACCOUNTING = "/record";

	public static final String URL_PROPERTY_KEY = "URL";

	public static final String SERVICE_CLASS = "Accounting";
	public static final String SERVICE_NAME = "AccountingService";
	public static final String SERVICE_ENTRY_NAME = "org.gcube.accounting.service.AccountingResource";

	private static final String USER_AGENT = "document-store-lib-accounting-service";

	private static String FORCED_URL;
	
	private HTTPCall httpCall; 

	private static String classFormat = "$resource/Profile/ServiceClass/text() eq '%1s'";
	private static String nameFormat = "$resource/Profile/ServiceName/text() eq '%1s'";
	private static String statusFormat = "$resource/Profile/DeploymentData/Status/text() eq 'ready'";
	private static String containsFormat = "$entry/@EntryName eq '%1s'";

	private static SimpleQuery queryForService() {
		logger.trace("going to query GCoreEndpoint of {}", SERVICE_NAME);
		return ICFactory.queryFor(GCoreEndpoint.class).addCondition(String.format(classFormat, SERVICE_CLASS))
				.addCondition(String.format(nameFormat, SERVICE_NAME)).addCondition(String.format(statusFormat))
				.addVariable("$entry", "$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint")
				.addCondition(String.format(containsFormat, SERVICE_ENTRY_NAME)).setResult("$entry/text()");
	}

	protected static void forceURL(String url) {
		FORCED_URL = url;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareConnection(PersistenceBackendConfiguration configuration) throws Exception {
		String url = null;
		if(FORCED_URL!=null) {
			url = FORCED_URL;
		} else {
			url = configuration.getProperty(URL_PROPERTY_KEY);
			if (url == null || url.compareTo("") == 0) {
				logger.debug("Invalid URL provided from Configuration. Looking for RunningInstance.");
				SimpleQuery serviceQuery = queryForService();
				List<String> addresses = ICFactory.client().submit(serviceQuery);
				if (addresses == null || addresses.isEmpty()) {
					String error = String.format("No Running Instance %s:%s found in the current context", SERVICE_CLASS,
							SERVICE_NAME);
					throw new Exception(error);
				}
				Random random = new Random();
				int index = random.nextInt(addresses.size());
	
				url = addresses.get(index);
			}
		}

		logger.debug("Accounting Service URL to be contacted is {}", url);
		httpCall = new HTTPCall(url, USER_AGENT);
	}

	protected void send(Record... records) throws Exception {
		List<Record> list = Arrays.asList(records);
		String body = DSMapper.marshal(list);
		
		logger.trace("Going to persist {}s {}", Record.class.getSimpleName(), body);
		httpCall.call(String.class, PATH_SERVICE_INSERT_ACCOUNTING, HTTPMETHOD.POST, body);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reallyAccount(Record record) throws Exception {
		send(record);
	}

	@Override
	protected void accountWithFallback(Record... records) throws Exception {
		try {
			send(records);
		} catch (Throwable e) {
			super.accountWithFallback(records);
		}
	}

	@Override
	public void close() throws Exception {}

	@Override
	protected void openConnection() throws Exception {}

	@Override
	protected void closeConnection() throws Exception {}

	@Override
	public boolean isConnectionActive() throws Exception {
		return true;
	}

	@Override
	protected void clean() throws Exception {
		// TODO Auto-generated method stub
	}
	
}
