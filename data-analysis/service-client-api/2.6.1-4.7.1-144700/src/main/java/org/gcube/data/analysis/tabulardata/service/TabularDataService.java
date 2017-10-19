package org.gcube.data.analysis.tabulardata.service;

import org.gcube.data.analysis.tabulardata.service.operation.OperationInterface;
import org.gcube.data.analysis.tabulardata.service.query.QueryInterface;
import org.gcube.data.analysis.tabulardata.service.rules.RuleInterface;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceInterface;
import org.gcube.data.analysis.tabulardata.service.template.TemplateInterface;

public interface TabularDataService extends TabularResourceInterface, QueryInterface, OperationInterface, TemplateInterface, RuleInterface {

	//TODO-LF Add more capabilities (as extended interfaces) 
	
}
