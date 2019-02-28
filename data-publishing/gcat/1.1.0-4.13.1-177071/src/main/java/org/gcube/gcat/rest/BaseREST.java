package org.gcube.gcat.rest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseREST {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Context
	private UriInfo uriInfo;

	protected static final String LOCATION_HEADER = "Location";
	
	protected void setCalledMethod(String method) {
		CalledMethodProvider.instance.set(method);
		logger.info("{}", uriInfo.getAbsolutePath());
	}
	
	protected ResponseBuilder addLocation(ResponseBuilder responseBuilder, String id) {
		return responseBuilder.header(LOCATION_HEADER,
				String.format("%s/%s", uriInfo.getAbsolutePath().toString(), id)
		);
	}
	
}
