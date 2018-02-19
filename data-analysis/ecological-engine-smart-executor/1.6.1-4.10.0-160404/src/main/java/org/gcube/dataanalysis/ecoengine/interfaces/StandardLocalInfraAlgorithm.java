package org.gcube.dataanalysis.ecoengine.interfaces;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StandardLocalInfraAlgorithm extends StandardLocalExternalAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(StandardLocalInfraAlgorithm.class);
	
	private static final String SEND_MESSAGE_METHOD ="/messages/write-message";
		
	public void sendNotification(String subject, String body) throws Exception {
		
		LOGGER.debug("Emailing System->Starting request of email in scope "+config.getGcubeScope());
		
		if (!sendMessage(config.getGcubeToken(), config.getGcubeUserName(), subject, body)){
			LOGGER.error("error sending message to {}",config.getGcubeUserName());
			throw new Exception("error sending message to "+config.getGcubeUserName());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private boolean sendMessage(String token, String email, String subject, String body) throws Exception {

		String socialServiceEnpoint = retrieveSocialServiceEnpoint();

		LOGGER.debug("contacting social service endpoint {}",socialServiceEnpoint+SEND_MESSAGE_METHOD+"?gcube-token="+token);
		
		PostMethod putMessage = new PostMethod(socialServiceEnpoint+SEND_MESSAGE_METHOD+"?gcube-token="+token);
		
		JSONObject obj = new JSONObject();
		obj.put("subject", subject);
		obj.put("body", body);
		JSONArray list = new JSONArray();
        list.add(email);
        obj.put("recipients", list);
		        
		//String jsonRequest = String.format("{\"subject\":\"%s\", \"body\":\"%s\", \"recipients\":[{\"id\":\"%s\"}]}",subject,body, email);

		putMessage.setRequestEntity(new StringRequestEntity(obj.toJSONString(), "application/json" , "UTF-8"));
		LOGGER.debug("json request is {}", obj.toJSONString());
		
		HttpClient httpClient = new HttpClient();

		int returnedStatus = -1;
		try {
			returnedStatus = httpClient.executeMethod(putMessage);
			LOGGER.info("response from social networking service is {}",returnedStatus);
			return returnedStatus>=200 && returnedStatus<=205;
		} catch (Exception e) {
			LOGGER.error("error trying to send invitation",e);
			throw new Exception("error trying to send invitation",e);
		} 


	}
	
	private String retrieveSocialServiceEnpoint() throws Exception {
		XQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq 'Portal'");
		query.addCondition("$resource/Profile/ServiceName/text() eq 'SocialNetworking'");
		query.addVariable("$entry","$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint");
		query.addCondition("$entry/@EntryName/string() eq 'jersey-servlet'");
		query.setResult("$entry/text()");
		DiscoveryClient<String> client = client();

		List<String> socialServiceEnpoints = client.submit(query);

		if (socialServiceEnpoints.size()==0) throw new Exception("Social servioce enpooint not found in the current scope "+ScopeProvider.instance.get()); 

		String socialServiceEnpoint = socialServiceEnpoints.get(0);

		return socialServiceEnpoint+"/2";
	}

}
