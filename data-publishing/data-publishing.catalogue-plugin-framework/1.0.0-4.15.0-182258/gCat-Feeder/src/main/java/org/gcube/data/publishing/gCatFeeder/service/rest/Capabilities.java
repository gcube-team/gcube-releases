package org.gcube.data.publishing.gCatFeeder.service.rest;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.data.publishing.gCatFeeder.catalogues.model.CataloguePluginDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.GCatFeederManager;
import org.gcube.data.publishing.gCatFeeder.service.ServiceConstants;
import org.gcube.data.publishing.gCatFeeder.service.engine.CatalogueControllersManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.CollectorsManager;
import org.gcube.data.publishing.gCatfeeder.collectors.model.PluginDescriptor;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBy(GCatFeederManager.class)
@Path(ServiceConstants.Capabilities.PATH)
public class Capabilities {

	private static final Logger log= LoggerFactory.getLogger(Capabilities.class);

	
	@Inject
	private CatalogueControllersManager catalogues;

	@Inject
	private CollectorsManager collectors;


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(ServiceConstants.Capabilities.COLLECTORS_PATH)
	public Response getCollectorCapabilities() {
		try {
			ArrayList<PluginDescriptor> toReturn=new ArrayList<>();
			for(String s:collectors.getAvailableCollectors()) {
				toReturn.add(collectors.getPluginById(s).getDescriptor());
			}
			GenericEntity<Collection<PluginDescriptor>> entity=new GenericEntity<Collection<PluginDescriptor>>(toReturn) {};
			return Response.ok(entity).build();
		}catch(Throwable t) {
			log.warn("Unexpected Exception ",t);
			throw new WebApplicationException("Unexpected Exception.", t,Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(ServiceConstants.Capabilities.CATALOGUES_PATH)
	public Response getCataloguesCapabilities() {
		try {
			ArrayList<CataloguePluginDescriptor> toReturn=new ArrayList<>();
			for(String s:catalogues.getAvailableControllers()) {
				toReturn.add(catalogues.getPluginById(s).getDescriptor());
			}
			GenericEntity<Collection<CataloguePluginDescriptor>> entity=new GenericEntity<Collection<CataloguePluginDescriptor>>(toReturn) {};
			return Response.ok(entity).build();
		}catch(Throwable t) {
			log.warn("Unexpected Exception ",t);
			throw new WebApplicationException("Unexpected Exception.", t,Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
}
