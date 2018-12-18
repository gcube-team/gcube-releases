package org.gcube.common.calls.jaxrs;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.xml.ws.EndpointReference;

import org.gcube.common.gxrest.request.GXWebTargetAdapterRequest;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetFactory implements TargetFactoryDSL.AtClause{

	private static Logger log = LoggerFactory.getLogger(TargetFactory.class);
	
	private GcubeService target;
	
	public static TargetFactory stubFor(GcubeService target){
		return new TargetFactory(target);
	}
	
			
	private TargetFactory(GcubeService target) {
		this.target = target;
	}


	public GXWebTargetAdapterRequest getAsGxRest(String address) {

		try{
					
						
			/*
			String resourceAddress = address.substring(0, address.indexOf("/service"));
			
			WebTarget resourcetarget = client.target(resourceAddress).path("/resource/");
			
			int status = resourcetarget.request().get().getStatus();
				
			if (status!=200)
				throw new Exception();*/
			
			//WebTarget webTarget = client.target(address).path(target.path());
			
			
			
			GXWebTargetAdapterRequest webTarget = GXWebTargetAdapterRequest.newRequest(address).path(target.path());
			
			webTarget.configProperty(ClientProperties.REQUEST_ENTITY_PROCESSING, "CHUNKED");
			
			webTarget.register(new JaxRSRequestFilter(target));
						
			return webTarget;

		}catch (Exception e) {
			log.error("error building service",e);
			throw new RuntimeException("error building service",e);
		}

	}

	public WebTarget at(String address) {

		try{
					
			Client client = ClientBuilder.newClient()
					.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
					.property(ClientProperties.CHUNKED_ENCODING_SIZE, 1024*1024).property(ClientProperties.OUTBOUND_CONTENT_LENGTH_BUFFER, -1)
					.property(ClientProperties.REQUEST_ENTITY_PROCESSING, "CHUNKED");
						
			
			WebTarget webTarget = client.target(address).path(target.path());
					
			webTarget.register(new JaxRSRequestFilter(target));
						
			return webTarget;

		}catch (Exception e) {
			log.error("error building service",e);
			throw new RuntimeException("error building service",e);
		}

	}

	
	public WebTarget at(EndpointReference endpoint){
		return at(new JaxRSEndpointReference(endpoint).address);
	}
	

}
