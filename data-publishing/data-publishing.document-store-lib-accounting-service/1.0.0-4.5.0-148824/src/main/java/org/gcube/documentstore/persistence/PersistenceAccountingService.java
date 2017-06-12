/**
 * 
 */
package org.gcube.documentstore.persistence;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.DataOutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXBContext;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.SerializableList;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 *
 */
public class PersistenceAccountingService extends PersistenceBackend {

	private static final Logger logger = LoggerFactory.getLogger(PersistenceAccountingService.class);

	public static final String URL_SERVICE_ACCOUNTING_KEY = "UrlAccountingService";

	public static final String PATH_SERVICE_ACCOUNTING = "/accounting-service/gcube/service";

	public static final String PATH_SERVICE_INSERT_ACCOUNTING = "/insert/record";
	public static final String PATH_SERVICE_INSERTS_ACCOUNTING = "/insert/records";
	public static final String PATH_SERVICE_STATUS_ACCOUNTING = "insert/getStatus";
	
	public static final String GCORE_END_POINT_NAME="AccountService webapp";
	public static final String GCORE_END_RUNNING="RunningInstance";
	
	public static final String RESOURCE_ACCOUNTING="org.gcube.data.publishing.accounting.service.AccountingResource";
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final String CONTENT_TYPE_JSON = "application/json";
	private static final String CONTENT_TYPE_XML = "application/xml";
	
	protected String urlService;
	protected String context;
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareConnection(PersistenceBackendConfiguration configuration) throws Exception {		 

		try{
			context = ScopeProvider.instance.get();
			logger.debug("prepareConnection context:{}",context);
			ScopeProvider.instance.set(context);
			SimpleQuery query = queryFor(GCoreEndpoint.class);
			query.addCondition("$resource/Profile/Description/text() eq '"+ GCORE_END_POINT_NAME +"'");
			query.addCondition("$resource/Type/text() eq '"+ GCORE_END_RUNNING +"'");
			
			DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
			List<GCoreEndpoint> toReturn = client.submit(query);
			
			GCoreEndpoint endpoint=toReturn.get(0);
			Profile profile =endpoint.profile();
			Endpoint url =profile.endpointMap().get(RESOURCE_ACCOUNTING);
			
			urlService=url.uri().toString();
			logger.debug("urlService from GcoreEndPoint:{}",urlService);
		
			
		}catch(Exception e){			
			try{
				urlService= configuration.getProperty(URL_SERVICE_ACCOUNTING_KEY)+PATH_SERVICE_ACCOUNTING+"/";
				logger.debug("urlService from Service End Point:{}",urlService);
			}catch(Exception ex){
				logger.error("Url service not found into configuration from service point");
				throw new IllegalStateException("Url service has a null property", ex);
			
			}
			
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reallyAccount(Record record) throws Exception {
		
		logger.trace("init reallyAccount");
		String path=urlService+PATH_SERVICE_INSERT_ACCOUNTING;
		
		URL obj = new URL(path);
		logger.trace("reallyAccount path:{}",path);
		String recordMarshal=DSMapper.marshal(record);
		int responseCode;
		if (path.indexOf("http") != -1){ 
			logger.trace("accountWithFallback http path:{}",path);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Content-type", CONTENT_TYPE_JSON);
			con.setRequestProperty("gcube-scope", context);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(recordMarshal);
			wr.flush();
			wr.close();
			responseCode = con.getResponseCode();
		}
		else{
			logger.trace("accountWithFallback https path:{}",path);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Content-type", CONTENT_TYPE_JSON);
			con.setRequestProperty("gcube-scope", context);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(recordMarshal);
			wr.flush();
			wr.close();
			responseCode = con.getResponseCode();
		}
		//logger.debug("reallyAccount Post parameters : " + recordMarshal);
		logger.debug("reallyAccount Response Code : " + responseCode);
		switch (responseCode) {
			case HttpURLConnection.HTTP_OK:
				logger.trace("accountWithFallback - Send records to service:{}");
				break; 
			case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
				throw new TimeoutException("Time out for call service accounting");		
		
			default: 
				throw new Exception("Generic error for service accounting");
	       
		}	
	
	}

	
	@Override
	protected void accountWithFallback(Record... records) throws Exception {

		logger.trace("init accountWithFallback");
		String path=urlService+PATH_SERVICE_INSERTS_ACCOUNTING;
		List<String> valuesList=new ArrayList<String>();
		for(Record record:records){
			//logger.trace("add record:{}",record);
			valuesList.add(DSMapper.marshal(record));
		}
		SerializableList list=new SerializableList(valuesList);

		JAXBContext contextRecord = JAXBContext.newInstance(SerializableList.class);
		StringWriter writer =new StringWriter();
		contextRecord.createMarshaller().marshal(list, writer);
		URL obj = new URL(path);
		//check if http or https
		int responseCode;
		if (path.indexOf("http") != -1){ 
			logger.trace("accountWithFallback http path:{}",path);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();//add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Content-type", CONTENT_TYPE_XML);
			con.setRequestProperty("gcube-scope", context);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(writer.toString());
			wr.flush();
			wr.close();
			responseCode = con.getResponseCode();
		}
		else{
			logger.trace("accountWithFallback https path:{}",path);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Content-type", CONTENT_TYPE_XML);
			con.setRequestProperty("gcube-scope", context);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(writer.toString());
			wr.flush();
			wr.close();
			responseCode = con.getResponseCode();
		}
		logger.debug("accountWithFallback gcube-scope : " +context);
		//logger.debug("accountWithFallback Post parameters : " + writer.toString());
		logger.debug("accountWithFallback Response Code : " + responseCode);
		logger.trace("accountWithFallback - Send records to service");
		switch (responseCode) {
			case HttpURLConnection.HTTP_OK:
				logger.trace("accountWithFallback - Service respond ok :{}");
				break; 
			case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
				throw new TimeoutException("Time out for call service accounting");		
		
			default: 
				throw new Exception("Generic error for service accounting");
	       
		}	
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws Exception {
		//TODO
	}

	@Override
	protected void openConnection() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void closeConnection() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void closeAndClean() throws Exception {
		// TODO Auto-generated method stub
	}

}
