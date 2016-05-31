package org.gcube.data.analysis.tabulardata.clientlibrary;

import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.gcube.common.calls.jaxws.GcubeService;
import org.gcube.data.analysis.tabulardata.commons.webservice.ExternalResourceManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.HistoryManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.OperationManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.QueryManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.RuleManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TabularResourceManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TaskManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TemplateManager;

import static org.gcube.common.calls.jaxws.GcubeService.service;

public class Constants {

	/** Service name. */
	public static final String SERVICE_NAME = "TabularData";

	/** Service class. */
	public static final String SERVICE_CLASS = "DataAnalysis";
	
	public static final String CONTEXT_SERVICE_NAME="tabular-data-manager";
	
	public static final int DEFAULT_TIMEOUT= (int) TimeUnit.SECONDS.toMillis(10);

	public static final String NAMESPACE = "http://gcube-system.org/namespaces/data/tabulardata";

	public static final QName TABULAR_RESOURCE_QNAME = new QName(org.gcube.data.analysis.tabulardata.commons.utils.Constants.TABULAR_RESOURCE_TNS,TabularResourceManager.SERVICE_NAME);
	
	public static final QName OPERATION_QNAME = new QName(org.gcube.data.analysis.tabulardata.commons.utils.Constants.OPERATION_TNS,OperationManager.SERVICE_NAME);
	
	public static final QName HISTORY_QNAME = new QName(org.gcube.data.analysis.tabulardata.commons.utils.Constants.HISTORY_TNS,HistoryManager.SERVICE_NAME);
	
	public static final QName QUERY_QNAME = new QName(org.gcube.data.analysis.tabulardata.commons.utils.Constants.QUERY_TNS,QueryManager.SERVICE_NAME);
	
	public static final QName TASK_QNAME = new QName(org.gcube.data.analysis.tabulardata.commons.utils.Constants.TASK_TNS,TaskManager.SERVICE_NAME);
	
	public static final QName TEMPLATE_QNAME = new QName(org.gcube.data.analysis.tabulardata.commons.utils.Constants.TEMPLATE_TNS,TemplateManager.SERVICE_NAME);
	
	public static final QName RULE_QNAME = new QName(org.gcube.data.analysis.tabulardata.commons.utils.Constants.RULE_TNS,RuleManager.SERVICE_NAME);
	
	public static final QName EXT_RES_QNAME = new QName(org.gcube.data.analysis.tabulardata.commons.utils.Constants.EXT_RESOURCE_TNS ,ExternalResourceManager.SERVICE_NAME);

	
	public static final GcubeService<TabularResourceManager> tabularResourceManager = service().withName(TABULAR_RESOURCE_QNAME).andInterface(TabularResourceManager.class);
	
	public static final GcubeService<OperationManager> operationManager =service().withName(OPERATION_QNAME).andInterface(OperationManager.class);
	
	public static final GcubeService<HistoryManager> historyManager =service().withName(HISTORY_QNAME).andInterface(HistoryManager.class);
	
	public static final GcubeService<QueryManager> queryManager =service().withName(QUERY_QNAME).andInterface(QueryManager.class);

	public static final GcubeService<TaskManager> taskManager =service().withName(TASK_QNAME).andInterface(TaskManager.class);
	
	public static final GcubeService<TemplateManager> templateManager =service().withName(TEMPLATE_QNAME).andInterface(TemplateManager.class);
	
	public static final GcubeService<RuleManager> ruleManager =service().withName(RULE_QNAME).andInterface(RuleManager.class);
	
	public static final GcubeService<ExternalResourceManager> externalResourceManager =service().withName(EXT_RES_QNAME).andInterface(ExternalResourceManager.class);

}
