package org.gcube.data.publishing.gCatFeeder.service.engine.impl;

import java.util.Collection;
import java.util.Set;

import javax.inject.Inject;

import org.gcube.data.publishing.gCatFeeder.service.engine.CatalogueControllersManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.CollectorsManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.ExecutionManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.FeederEngine;
import org.gcube.data.publishing.gCatFeeder.service.engine.PersistenceManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBQueryDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.CollectorNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.DescriptorNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.ElementNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InvalidRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.PersistenceError;

public class FeederEngineImpl implements FeederEngine {

	@Inject
	private ExecutionManager executions;
	@Inject
	private CollectorsManager collectors;
	@Inject 
	private CatalogueControllersManager catalogues;
	@Inject 
	private PersistenceManager persistence;

	@Override
	public ExecutionDescriptor submit(ExecutionRequest req) throws InternalError, PersistenceError, InvalidRequest {
		try{
			verifyRequest(req);
			ExecutionDescriptor related=persistence.create(req);
			executions.submit(related);
			return related;
		}catch(CollectorNotFound | DescriptorNotFound e) {
			throw new InvalidRequest(e);
		}
	}

	@Override
	public Collection<ExecutionDescriptor> get(DBQueryDescriptor filter) throws PersistenceError, InvalidRequest {
		return persistence.get(filter);
	}

	@Override
	public ExecutionDescriptor getById(Long id) throws PersistenceError, ElementNotFound, InvalidRequest {
		return persistence.getById(id);
	}


	private void verifyRequest(ExecutionRequest request) throws InternalError,CollectorNotFound,DescriptorNotFound{

		Set<String> availableControllers=catalogues.getAvailableControllers();
		if(request.getToInvokeControllers().size()==1&&request.getToInvokeControllers().contains("ALL")) {
			request.setToInvokeControllers(availableControllers);
		}
		for(String requestedCatalogue:request.getToInvokeControllers())
			if(!availableControllers.contains(requestedCatalogue))
				throw new DescriptorNotFound("Requested catalogue controller "+requestedCatalogue+" not found.");


		Set<String> availableCollectors=collectors.getAvailableCollectors();
		if(request.getToInvokeCollectors().size()==1&&request.getToInvokeCollectors().contains("ALL")) {
			request.setToInvokeCollectors(availableCollectors);
		} 
		for(String requestedCollector:request.getToInvokeCollectors())
			if(!availableCollectors.contains(requestedCollector))
				throw new DescriptorNotFound("Requested collector "+requestedCollector+" not found.");

	}

}
