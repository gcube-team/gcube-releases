package gr.uoa.di.madgik.workflow.client.library.proxies;

import gr.uoa.di.madgik.workflow.client.library.plugins.WorkflowEngineCLPlugin;
import gr.uoa.di.madgik.workflow.client.library.stubs.WorkflowEngineStub;

import org.gcube.common.clients.fw.builders.StatelessBuilder;
import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;

public class WorkflowEngineDSL {

	public static final WorkflowEngineCLPlugin workflowEngine_plugin = new WorkflowEngineCLPlugin();

	public static StatelessBuilder<WorkflowEngineCLProxyI> getWorkflowEngineProxyBuilder() {
		return new StatelessBuilderImpl<WorkflowEngineStub, WorkflowEngineCLProxyI>(workflowEngine_plugin);
	}

}
