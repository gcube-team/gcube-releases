package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import java.util.List;
import java.util.Map;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.commons.webservice.TaskManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ResumeOperationRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

public class DefaultTaskManagerProxy implements TaskManagerProxy {

	ProxyDelegate<TaskManager> delegate;
	
	private static Logger logger = LoggerFactory.getLogger(DefaultTaskManagerProxy.class); 
	
	public DefaultTaskManagerProxy(ProxyDelegate<TaskManager> config) {
		this.delegate = config;
	}

	@Override
	public TaskInfo remove(final String identifier) throws NoSuchTaskException {
		Call<TaskManager, TaskInfo> call = new Call<TaskManager, TaskInfo>() {

			@Override
			public TaskInfo call(TaskManager endpoint) throws Exception {
				return endpoint.remove(identifier);
			}
		};
		try{
			return delegate.make(call);
		}catch (NoSuchTaskException e) {
			logger.error("no task found with id {}",identifier);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public List<TaskInfo> get(final String ... identifiers) {
		Call<TaskManager, List<TaskInfo>> call = new Call<TaskManager, List<TaskInfo>>() {

			@Override
			public List<TaskInfo> call(TaskManager endpoint) throws Exception {
				return endpoint.get(identifiers);
			}
		};
		
		try{
			return delegate.make(call);
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}
	
	@Override
	public List<TaskInfo> getTasksByTabularResource(final long tabularResourceId) throws NoSuchTabularResourceException {
		Call<TaskManager, List<TaskInfo>> call = new Call<TaskManager, List<TaskInfo>>() {

			@Override
			public List<TaskInfo> call(TaskManager endpoint) throws Exception {
				return endpoint.getTasksByTabularResource(tabularResourceId);
			}
		};
		
		try{
			return delegate.make(call);
		}catch (NoSuchTabularResourceException e) {
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public TaskInfo abort(final String identifier) throws NoSuchTaskException {
		Call<TaskManager, TaskInfo> call = new Call<TaskManager, TaskInfo>() {

			@Override
			public TaskInfo call(TaskManager endpoint) throws Exception {
				return endpoint.abort(identifier);
			}
		};
		try{
			return delegate.make(call);
		}catch (NoSuchTaskException e) {
			logger.error("no task found with id {}",identifier);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public List<TaskInfo> getTasksByTabularResource(final long tabularResourceId,
			final TaskStatus status) throws NoSuchTabularResourceException {
		Call<TaskManager, List<TaskInfo>> call = new Call<TaskManager, List<TaskInfo>>() {

			@Override
			public List<TaskInfo> call(TaskManager endpoint) throws Exception {
				return endpoint.getTasksByStatusAndTabularResource(tabularResourceId, status);
			}
		};
		
		try{
			return delegate.make(call);
		}catch (NoSuchTabularResourceException e) {
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public TaskInfo resubmit(final String identifier) throws NoSuchTaskException {
		Call<TaskManager, TaskInfo> call = new Call<TaskManager, TaskInfo>() {

			@Override
			public TaskInfo call(TaskManager endpoint) throws Exception {
				return endpoint.resubmit(identifier);
			}
		};
		try{
			return delegate.make(call);
		}catch (NoSuchTaskException e) {
			logger.error("no task found with id {}",identifier);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}
	
	@Override
	public TaskInfo resume(final String identifier, final Map<String, Object> operationInvocationParameter) throws NoSuchTaskException {
		Call<TaskManager, TaskInfo> call = new Call<TaskManager, TaskInfo>() {

			@Override
			public TaskInfo call(TaskManager endpoint) throws Exception {
				return endpoint.resume(new ResumeOperationRequest(identifier, operationInvocationParameter));
			}
		};
		try{
			return delegate.make(call);
		}catch (NoSuchTaskException e) {
			logger.error("no task found with id {}",identifier);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}
	
	@Override
	public TaskInfo resume(final String identifier) throws NoSuchTaskException {
		Call<TaskManager, TaskInfo> call = new Call<TaskManager, TaskInfo>() {

			@Override
			public TaskInfo call(TaskManager endpoint) throws Exception {
				return endpoint.resume(new ResumeOperationRequest(identifier));
			}
		};
		try{
			return delegate.make(call);
		}catch (NoSuchTaskException e) {
			logger.error("no task found with id {}",identifier);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}
	
}
