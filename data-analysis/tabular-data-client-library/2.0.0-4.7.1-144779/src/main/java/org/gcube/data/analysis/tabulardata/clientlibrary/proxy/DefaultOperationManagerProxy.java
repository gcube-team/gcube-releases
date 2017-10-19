package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import java.util.List;

import org.gcube.common.calls.jaxws.JAXWSUtils.Empty;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.commons.webservice.OperationManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.ExecutionFailedException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.HistoryNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.OperationNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.BatchExecuteRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.BatchOption;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.ExecuteRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultOperationManagerProxy implements OperationManagerProxy {

	private static Logger logger = LoggerFactory.getLogger(DefaultOperationManagerProxy.class);
	
	private final ProxyDelegate<OperationManager> delegate;
		
	public DefaultOperationManagerProxy(ProxyDelegate<OperationManager> config){
		this.delegate = config;
	}

	
	@Override
	public List<OperationDefinition> getCapabilities() {
		Call<OperationManager, List<OperationDefinition>> call = new Call<OperationManager, List<OperationDefinition>>() {

			@Override
			public List<OperationDefinition> call(OperationManager endpoint) throws Exception {
				return endpoint.getCapabilities();
			}
		};
		try {
			return delegate.make(call);
		} catch (Exception e) {
			logger.error("error calling getCapabilities",e);
			throw again(e).asServiceException();
		}
	}

	@Override
	public OperationDefinition getCapabilities(final long operationId) throws OperationNotFoundException {
		Call<OperationManager, OperationDefinition> call = new Call<OperationManager, OperationDefinition>() {

			@Override
			public OperationDefinition call(OperationManager endpoint) throws Exception {
				return endpoint.getOperationDescriptor(operationId);
			}
		};
		try {
			return delegate.make(call);
		}catch (OperationNotFoundException e) {
			logger.error("operation with id {} not found", operationId);
			throw e;
		} catch (Exception e) {
			logger.error("error calling getCapabilities",e);
			throw again(e).asServiceException();
		}
	}

	@Override
	public TaskInfo execute(final long targetTabularResourceId, final OperationExecution  invocation)
			throws NoSuchTabularResourceException, 
			OperationNotFoundException {
		Call<OperationManager, TaskInfo> call = new Call<OperationManager, TaskInfo>() {

			@Override
			public TaskInfo call(OperationManager endpoint) throws Exception {
				return endpoint.execute(new ExecuteRequest(targetTabularResourceId, invocation));
			}
		};
		try {
			return delegate.make(call);
		} catch (NoSuchTabularResourceException | OperationNotFoundException e) {
			logger.error("error executing operation",e);
			throw e;
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		} catch (Exception e) {
			logger.error("service error",e);
			throw again(e).asServiceException();
		}
	}


	@Override
	public TaskInfo rollbackTo(final long tabularResourceId, final long historyStepId)
			throws NoSuchTabularResourceException, HistoryNotFoundException{
		Call<OperationManager, TaskInfo> call = new Call<OperationManager, TaskInfo>() {

			@Override
			public TaskInfo call(OperationManager endpoint) throws Exception {
				return endpoint.rollbackTo(tabularResourceId, historyStepId);
			}
		};
		try {
			return delegate.make(call);
		} catch (NoSuchTabularResourceException | HistoryNotFoundException  e) {
			logger.error("error rollbacking",e);
			throw e;
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		} catch (Exception e) {
			logger.error("service error",e);
			throw again(e).asServiceException();
		}
	}

	@Override
	public TaskInfo execute(final long targetTabularResourceId,
			final List<OperationExecution> invocations, final BatchOption option)
			throws NoSuchTabularResourceException, OperationNotFoundException {
		Call<OperationManager, TaskInfo> call = new Call<OperationManager, TaskInfo>() {

			@Override
			public TaskInfo call(OperationManager endpoint) throws Exception {
				return endpoint.batchExecute(new BatchExecuteRequest(targetTabularResourceId, invocations, option));
			}
		};
		try {
			return delegate.make(call);
		} catch (NoSuchTabularResourceException | OperationNotFoundException e) {
			logger.error("error executing batch operation",e);
			throw e;
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		
		} catch (Exception e) {
			logger.error("service error",e);
			throw again(e).asServiceException();
		}
	}


	@Override
	public void executeSynchMetadataOperation(final long targetTabularResourceId,
			final OperationExecution invocation)
			throws NoSuchTabularResourceException, OperationNotFoundException,
			ExecutionFailedException {
		Call<OperationManager, Empty> call = new Call<OperationManager, Empty>() {

			@Override
			public Empty call(OperationManager endpoint) throws Exception {
				endpoint.executeSynchMetadataOperation(new ExecuteRequest(targetTabularResourceId, invocation));
				return new Empty();
			}
		};
		try {
			delegate.make(call);
		} catch (NoSuchTabularResourceException | OperationNotFoundException | ExecutionFailedException e) {
			logger.error("error executing operation",e);
			throw e;
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		} catch (Exception e) {
			logger.error("service error",e);
			throw again(e).asServiceException();
		}
	}
	
	@Override
	public TaskInfo removeValidations(final long tabularResourceId)
			throws NoSuchTabularResourceException {
		Call<OperationManager, TaskInfo> call = new Call<OperationManager, TaskInfo>() {

			@Override
			public TaskInfo call(OperationManager endpoint) throws Exception {
				return endpoint.removeValidations(tabularResourceId);
			}
		};
		try {
			return delegate.make(call);
		} catch (NoSuchTabularResourceException e) {
			logger.error("error executing operation",e);
			throw e;
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		} catch (Exception e) {
			logger.error("service error",e);
			throw again(e).asServiceException();
		}
	}
	
}
