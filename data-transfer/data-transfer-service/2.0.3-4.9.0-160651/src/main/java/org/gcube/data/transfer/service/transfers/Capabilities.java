package org.gcube.data.transfer.service.transfers;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.data.transfer.model.ServiceConstants;
import org.gcube.data.transfer.model.TransferCapabilities;
import org.gcube.data.transfer.service.DTServiceAppManager;
import org.gcube.data.transfer.service.transfers.engine.CapabilitiesProvider;
import org.gcube.data.transfer.service.transfers.engine.PluginManager;
import org.gcube.data.transfer.service.transfers.engine.faults.PluginNotFoundException;
import org.gcube.smartgears.annotations.ManagedBy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path(ServiceConstants.CAPABILTIES_SERVLET_NAME)
@ManagedBy(DTServiceAppManager.class)
public class Capabilities {

	@Inject
	CapabilitiesProvider provider;
	
	
	PluginManager plugins=PluginManager.get();
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TransferCapabilities getCapabilities(){
		log.debug("Serving get capabilities");
		try{
			TransferCapabilities toReturn=provider.get();
			log.debug("No exceptions here.. returning "+toReturn);
			return toReturn;
		}catch(Exception e){
			log.debug("Unable to return capabilities.",e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GET
	@Path("pluginInfo/{pluginId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPluginInfo(@PathParam("pluginId") String pluginID) {
		log.trace("Getting plugin info for plugin ID : {} ",pluginID);
		try{
			return Response.ok(plugins.getPluginInfo(pluginID),MediaType.APPLICATION_JSON).build();
		}catch(PluginNotFoundException e) {
			throw new WebApplicationException("Plugin "+pluginID+" has not been found",Status.NOT_FOUND);
		}catch(Throwable t) {
			throw new WebApplicationException("Unexpected error. ",t,Status.INTERNAL_SERVER_ERROR);
		}
	}
}
