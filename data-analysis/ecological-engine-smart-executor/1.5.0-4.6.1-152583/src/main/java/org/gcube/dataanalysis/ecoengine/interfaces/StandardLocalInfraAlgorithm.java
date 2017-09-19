package org.gcube.dataanalysis.ecoengine.interfaces;

import java.net.URLEncoder;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.dataanalysis.executor.util.InfraRetrieval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StandardLocalInfraAlgorithm extends StandardLocalExternalAlgorithm {

	private Logger LOGGER = LoggerFactory.getLogger(StandardLocalInfraAlgorithm.class);
	
		
	public void sendNotification(String subject, String body) throws Exception {
		
		LOGGER.debug("Emailing System->Starting request of email in scope "+config.getGcubeScope());
		
		String serviceAddress = InfraRetrieval.findEmailingSystemAddress(config.getGcubeScope());
		
		if (!serviceAddress.endsWith("/"))
			serviceAddress = serviceAddress+"/";
		
		String requestForMessage = serviceAddress  + "messages/writeMessageToUsers" + "?gcube-token=" + config.getGcubeToken();
		requestForMessage = requestForMessage.replace("http://", "https://").replace(":80", ""); // remove the port (or set it to 443) otherwise you get an SSL error

		LOGGER.debug("Emailing System->Request url is going to be " + requestForMessage);

		// put the sender, the recipients, subject and body of the mail here
		subject=URLEncoder.encode(subject,"UTF-8");
		body=URLEncoder.encode(body,"UTF-8");
		String requestParameters = "sender=dataminer&recipients="+config.getGcubeUserName()+"&subject="+subject+"&body="+body;

		String response = HttpRequest.sendPostRequest(requestForMessage, requestParameters);
		LOGGER.debug("Emailing System->Emailing response OK ");
		
		if (response==null){
			Exception e = new Exception("Error in email sending response");
			throw e;
		}
		
	}
	

}
