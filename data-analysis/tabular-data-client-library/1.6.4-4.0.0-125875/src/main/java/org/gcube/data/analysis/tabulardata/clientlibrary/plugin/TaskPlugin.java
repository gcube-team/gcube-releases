package org.gcube.data.analysis.tabulardata.clientlibrary.plugin;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.clientlibrary.Constants;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.DefaultTaskManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TaskManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.TaskManager;

import static org.gcube.data.analysis.tabulardata.clientlibrary.Constants.taskManager;
import static org.gcube.common.calls.jaxws.StubFactory.stubFor;

public class TaskPlugin extends AbstractPlugin<TaskManager, TaskManagerProxy> {

	public TaskPlugin() {
		super(Constants.CONTEXT_SERVICE_NAME+"/"+TaskManager.SERVICE_NAME);
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> proxy) {
		return fault;
	}

	@Override
	public TaskManagerProxy newProxy(ProxyDelegate<TaskManager> delegate) {
		return new DefaultTaskManagerProxy(delegate);
	}

	@Override
	public TaskManager resolve(EndpointReference address,
			ProxyConfig<?, ?> config) throws Exception {
		return stubFor(taskManager).at(address);
	}
	
	
	
}

