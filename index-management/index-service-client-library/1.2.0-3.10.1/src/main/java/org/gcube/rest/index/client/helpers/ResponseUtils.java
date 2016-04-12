package org.gcube.rest.index.client.helpers;

import javax.ws.rs.core.Response;

import org.gcube.rest.index.client.exceptions.IndexException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseUtils {
	private static final Logger logger = LoggerFactory
			.getLogger(ResponseUtils.class);
	
	public static void checkResponse(Response response, String methodName) throws IndexException {
		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				String error = response.readEntity(String.class);
				logger.info(error);
				response.close();
				throw new IndexException(methodName + " error : " + error);
		}
	}
	
}
