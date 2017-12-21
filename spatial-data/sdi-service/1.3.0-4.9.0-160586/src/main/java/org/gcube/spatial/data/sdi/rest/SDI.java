package org.gcube.spatial.data.sdi.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.gcube.smartgears.annotations.ManagedBy;
import org.gcube.spatial.data.sdi.SDIServiceManager;
import org.gcube.spatial.data.sdi.engine.SDIManager;
import org.gcube.spatial.data.sdi.model.ScopeConfiguration;
import org.gcube.spatial.data.sdi.model.ServiceConstants;
import org.gcube.spatial.data.sdi.model.health.HealthReport;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Path(ServiceConstants.INTERFACE)
@Api(value=ServiceConstants.INTERFACE)
@ManagedBy(SDIServiceManager.class)
@Slf4j
public class SDI {

	@Inject
	private SDIManager sdiManager;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public ScopeConfiguration getConfiguration(){
		
		return sdiManager.getContextConfiguration();
	}

	
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	@Path("status")
	public HealthReport getReport() {
		try{
			return sdiManager.getHealthReport();
		}catch(Throwable t) {
			log.error("Unabel to get Health Report ",t);
			throw new WebApplicationException("Unable to check Health. Contact administrator.",t);
		}
	}

	
	
}
