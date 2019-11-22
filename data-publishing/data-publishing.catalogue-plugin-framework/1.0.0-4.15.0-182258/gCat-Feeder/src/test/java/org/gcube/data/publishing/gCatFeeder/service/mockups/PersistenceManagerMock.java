package org.gcube.data.publishing.gCatFeeder.service.mockups;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.gcube.data.publishing.gCatFeeder.service.engine.Infrastructure;
import org.gcube.data.publishing.gCatFeeder.service.engine.PersistenceManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBQueryDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionStatus;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.ElementNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InvalidRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.PersistenceError;


public class PersistenceManagerMock implements PersistenceManager {

	private static AtomicLong idCounter=new AtomicLong(Long.MIN_VALUE);
	
	@Inject
	private Infrastructure infrastructure;
	
	
	@Override
	public ExecutionDescriptor create(ExecutionRequest request) throws PersistenceError, InvalidRequest {
		return getEquivalent(request);
	}

	@Override
	public ExecutionDescriptor getById(Long id) throws PersistenceError, ElementNotFound{
		if(theMap.containsKey(id))
			return theMap.get(id);
		else throw new ElementNotFound("Unable to find request with id "+id); 
	}

	@Override
	public Collection<ExecutionDescriptor> get(DBQueryDescriptor filter)
			throws PersistenceError, InvalidRequest {
		return theMap.values();
	}

	@Override
	public boolean update(ExecutionDescriptor toUpdate) throws PersistenceError, ElementNotFound {
		if(theMap.containsKey(toUpdate.getId())) {			
			theMap.replace(toUpdate.getId(), toUpdate);			
			return true;
		}
		else throw new ElementNotFound("Unable to find request with id "+toUpdate.getId());
	}

	@Override
	public boolean acquire(Long id) throws PersistenceError, ElementNotFound {
		ExecutionDescriptor desc=getById(id);
		if(desc.getStatus().equals(ExecutionStatus.PENDING))
			desc.setStatus(ExecutionStatus.RUNNING);
		return update(desc);
	}

	// Actual persistence in map
	
	private ConcurrentHashMap<Long,ExecutionDescriptor> theMap=new ConcurrentHashMap<>();
	
	private ExecutionDescriptor getEquivalent(ExecutionRequest req) {
		ExecutionDescriptor toReturn=new ExecutionDescriptor();
		toReturn.setCallerContext(infrastructure.getCurrentContextName());
		toReturn.setCallerIdentity(infrastructure.getClientID(infrastructure.getCurrentToken()));
		toReturn.setCallerEncryptedToken(infrastructure.encrypt("TEST"));
		
		toReturn.setCatalogues(req.getToInvokeControllers());
		toReturn.setCollectors(req.getToInvokeCollectors());
		
		toReturn.setId(idCounter.incrementAndGet());
		
		toReturn.setStatus(ExecutionStatus.PENDING);
		theMap.put(toReturn.getId(), toReturn);
		return toReturn;
	}
	
}
