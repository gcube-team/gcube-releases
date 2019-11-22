package org.gcube.data.publishing.gCatFeeder.service.rest;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.data.publishing.gCatFeeder.service.GCatFeederManager;
import org.gcube.data.publishing.gCatFeeder.service.ServiceConstants;
import org.gcube.data.publishing.gCatFeeder.service.engine.FeederEngine;
import org.gcube.data.publishing.gCatFeeder.service.engine.Infrastructure;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBQueryDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.ElementNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InvalidRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.PersistenceError;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBy(GCatFeederManager.class)
@Path(ServiceConstants.Executions.PATH)
public class Executions {

	private static final Logger log= LoggerFactory.getLogger(Executions.class);


	@Inject
	private FeederEngine engine;

	@Inject 
	private Infrastructure infrastructure;
	

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public ExecutionDescriptor submit(@Context UriInfo info) {

		try {			
			ExecutionRequest request=new ExecutionRequest();
			if(info.getQueryParameters().containsKey(ServiceConstants.Executions.COLLECTOR_ID_PARAMETER))				
				for(String collector:info.getQueryParameters().get(ServiceConstants.Executions.COLLECTOR_ID_PARAMETER))
					request.addCollectorId(collector);
			else request.addCollectorId(ServiceConstants.Executions.DEFAULT_VALUE);

			
			if(info.getQueryParameters().containsKey(ServiceConstants.Executions.CATALOGUE_ID_PARAMETER))
			for(String catalogue:info.getQueryParameters().get(ServiceConstants.Executions.CATALOGUE_ID_PARAMETER))
				request.addControllerId(catalogue);
			else request.addControllerId(ServiceConstants.Executions.DEFAULT_VALUE);
			
			String token=infrastructure.getCurrentToken();
			request.setCallerID(infrastructure.getClientID(token));
			request.setContext(infrastructure.getCurrentContext());
			request.setEncryptedToken(infrastructure.encrypt(token));
			
			log.trace("Submitting request {} ",request);

			ExecutionDescriptor toReturn= engine.submit(request);
			
			log.debug("Returning {} ",toReturn);
			return toReturn;
		} catch (PersistenceError e) {
			log.warn("Unexpected Exception while talking to persistnce",e);
			throw new WebApplicationException("Invalid Request.", e,Response.Status.INTERNAL_SERVER_ERROR);
		} catch (InvalidRequest e) {
			log.warn("Unexpected Exception ",e);
			throw new WebApplicationException("Invalid Request.", e,Response.Status.BAD_REQUEST);
		}catch(Throwable t) {
			log.warn("Unexpected Exception ",t);
			throw new WebApplicationException("Unexpected Exception.", t,Response.Status.INTERNAL_SERVER_ERROR);
		}		
	}


	@GET
	@Produces(MediaType.APPLICATION_JSON)	
	public Response getAll() {
		try {
			String token=SecurityTokenProvider.instance.get();			
			
			log.debug("Requested getALL with token {} ",token);
			DBQueryDescriptor filter=new DBQueryDescriptor();
			Collection<ExecutionDescriptor> toReturn=engine.get(filter);
			GenericEntity<Collection<ExecutionDescriptor>> entity=new GenericEntity<Collection<ExecutionDescriptor>>(toReturn) {};
			return Response.ok(entity).build();
		} catch (PersistenceError e) {
			log.warn("Unexpected Exception while talking to persistnce",e);
			throw new WebApplicationException("Invalid Request.", e,Response.Status.INTERNAL_SERVER_ERROR);
		} catch (InvalidRequest e) {
			log.warn("Unexpected Exception ",e);
			throw new WebApplicationException("Invalid Request.", e,Response.Status.BAD_REQUEST);
		}catch(Throwable t) {
			log.warn("Unexpected Exception ",t);
			throw new WebApplicationException("Unexpected Exception.", t,Response.Status.INTERNAL_SERVER_ERROR);
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{"+ServiceConstants.Executions.EXECUTION_ID_PARAMETER+"}")
	public ExecutionDescriptor get(@PathParam(ServiceConstants.Executions.EXECUTION_ID_PARAMETER) Long executionId) {
		try {
			return engine.getById(executionId);
		} catch (PersistenceError e) {
			log.warn("Unexpected Exception while talking to persistnce",e);
			throw new WebApplicationException("Invalid Request.", e,Response.Status.INTERNAL_SERVER_ERROR);
		} catch (ElementNotFound e) {
			log.warn("Unexpected Exception ",e);
			throw new WebApplicationException("Descriptor not found for "+executionId, e,Response.Status.NOT_FOUND);
		} catch (InvalidRequest e) {
			log.warn("Unexpected Exception ",e);
			throw new WebApplicationException("Invalid Request.", e,Response.Status.BAD_REQUEST);
		}catch(Throwable t) {
			log.warn("Unexpected Exception ",t);
			throw new WebApplicationException("Unexpected Exception.", t,Response.Status.INTERNAL_SERVER_ERROR);
		}

	}
}
