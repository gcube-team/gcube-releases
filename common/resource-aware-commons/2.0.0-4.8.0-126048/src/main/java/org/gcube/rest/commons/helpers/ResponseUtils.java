package org.gcube.rest.commons.helpers;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseUtils {
	private static final Logger logger = LoggerFactory
			.getLogger(ResponseUtils.class);
	
	public static void checkResponse(Response response, String methodName) throws Exception {
		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				String error = response.readEntity(String.class);
				logger.warn(error);
				response.close();
				throw new Exception(methodName + " error : " + error);
		}
	}
	
}
