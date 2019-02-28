package org.gcube.data.analysis.tabulardata.clientlibrary.plugin;

import org.gcube.common.clients.Plugin;
import org.gcube.common.clients.ProxyBuilder;
import org.gcube.common.clients.ProxyBuilderImpl;
import org.gcube.data.analysis.tabulardata.clientlibrary.Constants;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.ExternalResourceManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.HistoryManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.OperationManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.QueryManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.RuleManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TabularResourceManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TaskManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TemplateManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.ExternalResourceManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.HistoryManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.OperationManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.QueryManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.RuleManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TabularResourceManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TaskManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TemplateManager;



public abstract class AbstractPlugin<S,P> implements Plugin<S,P> {

	private static final TabularResourcePlugin manager_plugin = new TabularResourcePlugin();
	
	private static final OperationPlugin operation_plugin = new OperationPlugin();
	
	private static final QueryPlugin query_plugin = new QueryPlugin();
	
	private static final HistoryPlugin history_plugin = new HistoryPlugin();
	
	private static final TaskPlugin task_plugin = new TaskPlugin();
	
	private static final TemplatePlugin template_plugin = new TemplatePlugin();
	
	private static final ExternalResourcePlugin externalresource_plugin = new ExternalResourcePlugin();
	
	private static final RulePlugin rule_plugin = new RulePlugin();
	
	public static ProxyBuilder<TabularResourceManagerProxy> tabularResource() {
		return new ProxyBuilderImpl<TabularResourceManager,TabularResourceManagerProxy>(manager_plugin);
	}

	public static ProxyBuilder<OperationManagerProxy> operation() {
		return new ProxyBuilderImpl<OperationManager,OperationManagerProxy>(operation_plugin);
	}
	
	public static ProxyBuilder<QueryManagerProxy> query() {
		return new ProxyBuilderImpl<QueryManager,QueryManagerProxy>(query_plugin);
	}
	
	public static ProxyBuilder<HistoryManagerProxy> history() {
		return new ProxyBuilderImpl<HistoryManager,HistoryManagerProxy>(history_plugin);
	}
	
	public static ProxyBuilder<TaskManagerProxy> tasks() {
		return new ProxyBuilderImpl<TaskManager,TaskManagerProxy>(task_plugin);
	}
	
	public static ProxyBuilder<TemplateManagerProxy> template() {
		return new ProxyBuilderImpl<TemplateManager,TemplateManagerProxy>(template_plugin);
	}
	
	public static ProxyBuilder<ExternalResourceManagerProxy> externalResource() {
		return new ProxyBuilderImpl<ExternalResourceManager,ExternalResourceManagerProxy>(externalresource_plugin);
	}
	
	public static ProxyBuilder<RuleManagerProxy> rule() {
		return new ProxyBuilderImpl<RuleManager,RuleManagerProxy>(rule_plugin);
	}
	
	public final String name;
	
	public AbstractPlugin(String name) {
		this.name=name;
	}
	
	@Override
	public String serviceClass() {
		return Constants.SERVICE_CLASS;
	}
	
	@Override
	public String serviceName() {
		return Constants.SERVICE_NAME;
	}
	
	@Override
	public String namespace() {
		return Constants.NAMESPACE;
	}
	
	@Override
	public String name() {
		return name;
	}
	
}