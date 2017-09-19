package org.gcube.spatial.data.sdi.rest;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gcube.spatial.data.sdi.model.ServiceConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path(ServiceConstants.GeoNetwork.INTERFACE)
//@Api(value="GeoNetwork")
public class GeoNetwork {
//
//	@Inject
//	GeoNetworkProvider geonetworkProvider;
//	
//	
//	@GET
//	@Path(Constants.GEONETWORK_CONFIGURATION_PATH)
//	@Produces(MediaType.APPLICATION_JSON)
//	public ScopeConfiguration getConfiguration(){
//		try {
//			return geonetworkProvider.getGeoNetwork().getConfiguration().getScopeConfiguration();
//		} catch (MissingConfigurationException | MissingServiceEndpointException e) {
//			log.warn("Unable to get GeoNetwork configuration. Current scope is {} ",ScopeUtils.getCurrentScope(),e);
//			throw new WebApplicationException("Scope is not well configured. Please contact administrator.", e, Status.PRECONDITION_FAILED);
//		} catch (ClientInitializationException e) {
//			log.warn("Unable to get GN Client",e);
//			throw new WebApplicationException("Internal Error. Please contact administrator.", e, Status.INTERNAL_SERVER_ERROR);
//		}
//	}
//	
//	@GET
//	@Path(Constants.GEONETWORK_GROUPS_PATH)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Collection<Group> getGroups(){
//		try {
//			GeoNetworkAdministration admin=geonetworkProvider.getGeoNetwork();
//			admin.login(LoginLevel.ADMIN);
//			return admin.getGroups();
//		} catch (MissingConfigurationException | MissingServiceEndpointException e) {
//			log.warn("Unable to get GeoNetwork configuration. Current scope is {} ",ScopeUtils.getCurrentScope(),e);
//			throw new WebApplicationException("Scope is not well configured. Please contact administrator.", e, Status.PRECONDITION_FAILED);
//		} catch (AuthorizationException e) {
//			log.warn("Unable to use Admin rights.",e);
//			throw new WebApplicationException("Unable to use Admin rights on GeoNetwork. Please contact administrator.", e, Status.INTERNAL_SERVER_ERROR);
//		} catch (GNLibException e) {
//			log.warn("Internal library exception.",e);
//			throw new WebApplicationException("Internal library exception.", e, Status.INTERNAL_SERVER_ERROR);
//		} catch (GNServerException e) {
//			log.warn("GeoNEtwork service exception.",e);
//			throw new WebApplicationException("GeoNetwork service exception.", e, Status.INTERNAL_SERVER_ERROR);
//		} catch (ClientInitializationException e) {
//			log.warn("Unable to get GN Client",e);
//			throw new WebApplicationException("Internal Error. Please contact administrator.", e, Status.INTERNAL_SERVER_ERROR);
//		}
//	}
	

//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	public Collection<GeoNetworkDescriptor> getList(){
//		
//	}
//	
//	@GET
//	@Path("/{host}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public GeoNetworkDescriptor getById() {
//		
//	}
//	
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	public GeoNetworkDescriptor register() {
//		
//	}
//	
//	@PUT
//	@Path("/{host}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public GeoNetworkDescriptor modify() {
//		
//	}
//	
//	
	
}
