package org.gcube.data.publishing.gCatFeeder.service.engine;

import java.util.Collection;

import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBQueryDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.CollectorNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.DescriptorNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.ElementNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InvalidRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.PersistenceError;

public interface FeederEngine {

	public Collection<ExecutionDescriptor> get(DBQueryDescriptor filter) throws PersistenceError, InvalidRequest;
	public ExecutionDescriptor getById(Long id) throws PersistenceError, ElementNotFound, InvalidRequest;
	public ExecutionDescriptor submit(ExecutionRequest req) throws InternalError, CollectorNotFound, DescriptorNotFound, PersistenceError, InvalidRequest;
	
	
}
