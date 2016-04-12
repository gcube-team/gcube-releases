package gr.uoa.di.madgik.workflow.client.library.plugins;

import gr.uoa.di.madgik.workflow.client.library.proxies.WorkflowEngineCLDefaultProxy;
import gr.uoa.di.madgik.workflow.client.library.proxies.WorkflowEngineCLProxyI;
import gr.uoa.di.madgik.workflow.client.library.stubs.WorkflowEngineStub;
import gr.uoa.di.madgik.workflow.client.library.utils.WorkflowEngineCLConstants;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.fw.plugin.Plugin;
import org.gcube.common.clients.stubs.jaxws.StubFactory;

public class WorkflowEngineCLPlugin implements Plugin<WorkflowEngineStub, WorkflowEngineCLProxyI> {

	/**
	 * Implementation of Plugin operations. With these callbacks the library
	 * describes Search to the framework.
	 */

	@Override
	public String name() {
		return WorkflowEngineCLConstants.NAME;
	}

	@Override
	public String namespace() {
		return WorkflowEngineCLConstants.NAMESPACE;
	}

	@Override
	public String serviceClass() {
		return WorkflowEngineCLConstants.gcubeClass;
	}

	@Override
	public String serviceName() {
		return WorkflowEngineCLConstants.gcubeName;
	}

	@Override
	public Exception convert(Exception fault, ProxyConfig<?, ?> config) {
		return fault;
	}

	@Override
	public WorkflowEngineStub resolve(EndpointReference reference,
			ProxyConfig<?, ?> proxyConfig) throws Exception {
		return StubFactory.stubFor(WorkflowEngineCLConstants.workflowEngine).at(reference);
	}

	@Override
	public WorkflowEngineCLDefaultProxy newProxy(ProxyDelegate<WorkflowEngineStub> delegate) {
		return new WorkflowEngineCLDefaultProxy(delegate);
	}

}
