package org.gcube.spatial.data.sdi.rest;

import javax.ws.rs.Path;

import org.gcube.spatial.data.sdi.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path(Constants.GEONETWORK_INTERFACE)
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
	
	
	
	
}
