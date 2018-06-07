package org.gcube.datacatalogue.grsf_manage_widget.server.manage;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.Iterator;
import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.common.Constants;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ConnectedBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.SimilarGRSFRecord;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpResponse;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpPost;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.StringEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.util.EntityUtils;

/**
 * Exploits the grsf-services-updater service's methods https://app.swaggerhub.com/apis/ymark/grsf-services-updater/1.1.0
 * @author Costantino Perciante at ISTI-CNR  (costantino.perciante@isti.cnr.it)
 */
public class GRSFUpdaterServiceClient {

	private static final Logger logger = LoggerFactory.getLogger(GRSFUpdaterServiceClient.class);

	/**
	 * Discover the service endpoint of the GRSF Updater service and return its url
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String discoverEndPoint(String context) throws Exception{

		String oldContext = ScopeProvider.instance.get();
		ScopeProvider.instance.set(context);
		String toReturn = null;
		try{
			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/text() eq '"+ Constants.SERVICE_NAME +"'");
			query.addCondition("$resource/Profile/Category/text() eq '"+ Constants.SERVICE_CATEGORY +"'");
			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
			List<ServiceEndpoint> resources = client.submit(query);

			if (resources.size() == 0){
				logger.error("There is no Runtime Resource having name " + Constants.SERVICE_NAME +" and Category " + Constants.SERVICE_CATEGORY + " in this scope.");
				throw new Exception("There is no Runtime Resource having name " + Constants.SERVICE_NAME +" and Category " + Constants.SERVICE_CATEGORY + " in this scope.");
			}
			else {

				for (ServiceEndpoint res : resources) {
					Iterator<AccessPoint> accessPointIterator = res.profile().accessPoints().iterator();

					while (accessPointIterator.hasNext()) {
						ServiceEndpoint.AccessPoint accessPoint = (ServiceEndpoint.AccessPoint) accessPointIterator
								.next();

						// return the path
						toReturn = accessPoint.address();
					}
				}
			}
		}catch(Exception e){
			logger.error("Unable to retrieve such service endpoint information!", e);
			throw e;
		}finally{
			if(oldContext != null && !oldContext.equals(context))
				ScopeProvider.instance.set(oldContext);
		}
		return toReturn;
	}

	/**
	 * Send updates to the knowledge base
	 * @param httpClient 
	 * @param serviceUrl
	 * @param bean
	 * @param catalogue
	 * @param username
	 * @param fullName
	 */
	@SuppressWarnings("unchecked")
	public static void updateKB(CloseableHttpClient httpClient, String serviceUrl, ManageProductBean bean, 
			DataCatalogue catalogue, String username, String fullName) throws Exception{

		JSONObject obj = new JSONObject();
		obj.put(Constants.ADMINISTRATOR_FULLNAME, fullName);
		obj.put(Constants.CATALOGUE_ID, bean.getCatalogueIdentifier());
		obj.put(Constants.KB_ID, bean.getKnowledgeBaseId());
		obj.put(Constants.NEW_STATUS, bean.getNewStatus().toString().toLowerCase());
		obj.put(Constants.OLD_STATUS, bean.getCurrentStatus().toString().toLowerCase());
		obj.put(Constants.TRACEABILITY_FLAG, bean.isTraceabilityFlag());
		obj.put(Constants.SDG_FLAG, bean.isSdgFlag());
		obj.put(Constants.GRSF_TYPE_OLD, bean.getCurrentGrsfType());
		obj.put(Constants.GRSF_TYPE_NEW, bean.getNewGrsfType());

		String annotation = bean.getAnnotation() != null ? bean.getAnnotation(): "";
		obj.put(Constants.ANNOTATION, annotation.replaceAll("\"", ""));

		obj.put(Constants.SHORT_NAME_OLD, bean.getShortName());

		if(bean.getShortNameUpdated() == null || bean.getShortNameUpdated().isEmpty())
			bean.setShortNameUpdated(bean.getShortName());

		obj.put(Constants.SHORT_NAME_NEW, bean.getShortNameUpdated());
		obj.put(Constants.OLD_STATUS, bean.getCurrentStatus().toString().toLowerCase());

		// prepare connections 
		List<ConnectedBean> connections = bean.getConnections();
		JSONArray connectionsJson = new JSONArray();

		for(ConnectedBean c: connections){
			JSONObject cc = new JSONObject();
			if(c.isRemove() || (c.isConnect() && !c.isRemove())){ // do not send it if it needs to be unconnected but not removed
				cc.put(Constants.SOURCE_KNOWLEDGE_BASE_ID, bean.getKnowledgeBaseId());
				cc.put(Constants.DEST_KNOWLEDGE_BASE_ID, c.getKnowledgeBaseId());
				cc.put(Constants.SOURCE_DOMAIN, bean.getDomain());
				cc.put(Constants.CONNECTION_TO_REMOVE, c.isRemove());
				connectionsJson.add(cc);
			}
		}
		obj.put(Constants.CONNECTIONS, connectionsJson);

		// prepare similar grsf records
		List<SimilarGRSFRecord> similarRecords = bean.getSimilarGrsfRecords();
		JSONArray similarRecordsJson = new JSONArray();
		for(SimilarGRSFRecord s: similarRecords){
			JSONObject ss = new JSONObject();
			ss.put(Constants.KB_ID, s.getKnowledgeBaseId());
			ss.put(Constants.MERGE, s.isSuggestedMerge());
			similarRecordsJson.add(ss);
		}
		obj.put(Constants.SIMILAR_GRSF_RECORDS, similarRecordsJson);

		logger.info("Update request looks like " + obj.toJSONString());

		logger.info("Sending request to " + serviceUrl + Constants.SERVICE_POST_UPDATER_METHOD);

		HttpPost request = new HttpPost(serviceUrl + Constants.SERVICE_POST_UPDATER_METHOD);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
		StringEntity params = new StringEntity(obj.toJSONString());
		request.setEntity(params);
		HttpResponse response = httpClient.execute(request);

		logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

		String result = EntityUtils.toString(response.getEntity());
		JSONObject parsedJSON = null;
		try{
			JSONParser parser = new JSONParser();
			parsedJSON = (JSONObject)parser.parse(result);
		}catch(Exception e){
			logger.error("Failed to parse response from knowledge base", e);
		}

		if(parsedJSON == null)
			throw new Exception("There was a problem while performing this operation at knowledge base side");

		if(response.getStatusLine().getStatusCode() == 200){
			logger.info("Record updated ");
		}else if(!(boolean) parsedJSON.get(Constants.UPDATE_RESULT))
			throw new IllegalArgumentException(
					"Update failed for the following reason " + parsedJSON.get(Constants.ERROR_MESSAGE));

	}

	/**
	 * Send updates to the knowledge base
	 * @param httpClient 
	 * @param serviceUrl
	 * @param bean
	 * @param catalogue
	 * @param username
	 * @param fullName
	 */
	@SuppressWarnings("unchecked")
	public static void revertOperation(CloseableHttpClient httpClient, String serviceUrl, String fullName, String uuid) throws Exception{

		JSONObject obj = new JSONObject();
		obj.put(Constants.ADMINISTRATOR_FULLNAME, fullName);
		obj.put(Constants.KB_ID, uuid);

		logger.info("Update request looks like " + obj.toJSONString());

		HttpPost request = new HttpPost(serviceUrl + Constants.SERVICE_POST_REVERT_METHOD);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
		StringEntity params = new StringEntity(obj.toJSONString());
		request.setEntity(params);
		HttpResponse response = httpClient.execute(request);

		logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + 
				response.getStatusLine().getReasonPhrase());

		String result = EntityUtils.toString(response.getEntity());
		JSONObject parsedJSON = null;
		try{
			JSONParser parser = new JSONParser();
			parsedJSON = (JSONObject)parser.parse(result);
		}catch(Exception e){
			logger.error("Failed to parse response from knowledge base", e);
		}

		if(parsedJSON == null)
			throw new Exception("There was a problem while performing this operation at knowledge base side");
		
		if(response.getStatusLine().getStatusCode() == 200){
			logger.info("Request has been submitted");
		}else if(!(boolean) parsedJSON.get(Constants.UPDATE_RESULT))
			throw new IllegalArgumentException(
					"Request failed for the following reason " + parsedJSON.get(Constants.ERROR_MESSAGE));

	}

}
