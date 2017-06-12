package org.gcube.spatial.data.sdi.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gcube.spatial.data.sdi.Constants;
import org.gcube.spatial.data.sdi.engine.SDIManager;
import org.gcube.spatial.data.sdi.model.ScopeConfiguration;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;

import io.swagger.annotations.Api;

@Path(Constants.SDI_INTERFACE)
@Api(value=Constants.SDI_INTERFACE)
public class SDI {

	@Inject
	private SDIManager sdiManager;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public ScopeConfiguration getConfiguration(){
		
		return sdiManager.getContextConfiguration();
	}
	
}
