package org.gcube.spatial.data.sdi.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.gcube.smartgears.annotations.ManagedBy;
import org.gcube.spatial.data.sdi.NetUtils;
import org.gcube.spatial.data.sdi.SDIServiceManager;
import org.gcube.spatial.data.sdi.engine.RoleManager;
import org.gcube.spatial.data.sdi.engine.SDIManager;
import org.gcube.spatial.data.sdi.model.ServiceConstants;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.service.GeoNetworkDescriptor;
import org.gcube.spatial.data.sdi.model.services.GeoNetworkServiceDefinition;
import org.gcube.spatial.data.sdi.model.services.ServiceDefinition.Type;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Path(ServiceConstants.GeoNetwork.INTERFACE)
@Api(value=ServiceConstants.GeoNetwork.INTERFACE)
@Slf4j
@ManagedBy(SDIServiceManager.class)
public class GeoNetwork {

	private final static String HOST_PATH_PARAM="host";




	@Inject
	private SDIManager sdi;
	@Inject
	private RoleManager roleManager;
	
	
	
	@GET
	@Path("configuration/{"+HOST_PATH_PARAM+"}")
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public GeoNetworkDescriptor getInstanceConfiguration(@PathParam(HOST_PATH_PARAM) String host){
		try{
			log.trace("Serving credentials for host {} ",host);
			host=NetUtils.getHost(host);

			return sdi.getGeoNetworkManager().getDescriptorByHostname(host);	

		}catch(WebApplicationException e){
			log.warn("Unable to serve request",e);
			throw e;
		}catch(Exception e){
			log.warn("Unable to serve request",e);
			throw new WebApplicationException("Unable to serve request", e);
		}
	}


	@GET
	@Path("credentials/{"+HOST_PATH_PARAM+"}")
	@Produces(MediaType.APPLICATION_JSON)	
	@JacksonFeatures(serializationEnable =  { SerializationFeature.INDENT_OUTPUT })
	public Credentials getInstanceCredentials(@PathParam(HOST_PATH_PARAM) String host){
		try{
			log.trace("Serving credentials for host {} ",host);
			host=NetUtils.getHost(host);
			return roleManager.getMostAccessible(sdi.getGeoNetworkManager().getDescriptorByHostname(host).getAccessibleCredentials(), false);
//			return .get(0);
		}catch(WebApplicationException e){
			log.warn("Unable to serve request",e);
			throw e;
		}catch(Exception e){
			log.warn("Unable to serve request",e);
			throw new WebApplicationException("Unable to serve request", e);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_XML)
	public String register(GeoNetworkServiceDefinition toRegister) {
		try {
			return sdi.registerService(toRegister);
		}catch(WebApplicationException e) {
			log.warn("Unable to serve request",e);
			throw e;
		}catch(Exception e) {
			log.warn("Unable to serve request",e);
			throw new WebApplicationException("Unable to serve request",e);
		}
	}

	@POST
	@Path("import/{"+HOST_PATH_PARAM+"}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_XML)
	public String importFromScope(@QueryParam("sourceToken") String sourceToken,@PathParam(HOST_PATH_PARAM) String host) {
		try {
			return sdi.importService(sourceToken, host, Type.GEONETWORK);
		}catch(WebApplicationException e) {
			log.warn("Unable to serve request",e);
			throw e;
		}catch(Exception e) {
			log.warn("Unable to serve request",e);
			throw new WebApplicationException("Unable to serve request",e);
		}
	}


}
