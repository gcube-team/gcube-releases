package org.gcube.data.publishing.gCatFeeder.service.engine;

import java.util.Collection;

import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBQueryDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.ElementNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InvalidRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.PersistenceError;

public interface PersistenceManager {

	public ExecutionDescriptor create(ExecutionRequest request) throws PersistenceError,InvalidRequest;
	public Collection<ExecutionDescriptor> get(DBQueryDescriptor filter)throws PersistenceError,InvalidRequest;
	

	// DIRECT QUERIES
	public boolean update(ExecutionDescriptor toUpdate)throws PersistenceError,ElementNotFound,InvalidRequest;
	
	/**
	 * Updates status only if current status value is PENDING 
	 * 
	 * @param id
	 * @return
	 * @throws PersistenceError
	 * @throws ElementNotFound
	 */
	public boolean acquire(Long id)throws PersistenceError,ElementNotFound,InvalidRequest;
	public ExecutionDescriptor getById(Long id) throws PersistenceError, ElementNotFound, InvalidRequest;
	
}
