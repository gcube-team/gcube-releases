package org.gcube.data.analysis.tabulardata.service.impl;

import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.rules.RuleScope;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleType;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.ExecutionFailedException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.HistoryNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchOperationException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchRuleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTemplateException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.TemplateNotCompatibleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.AppliedRulesResponse;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.RuleDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.resources.ResourceDescriptor;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.operation.OperationInterfaceImpl;
import org.gcube.data.analysis.tabulardata.service.impl.query.QueryInterfaceImpl;
import org.gcube.data.analysis.tabulardata.service.impl.rules.RuleInterfaceImpl;
import org.gcube.data.analysis.tabulardata.service.impl.tabular.TabularResourceImpl;
import org.gcube.data.analysis.tabulardata.service.impl.template.TemplateInterfaceImpl;
import org.gcube.data.analysis.tabulardata.service.operation.OperationInterface;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.operation.TaskId;
import org.gcube.data.analysis.tabulardata.service.query.QueryInterface;
import org.gcube.data.analysis.tabulardata.service.rules.RuleId;
import org.gcube.data.analysis.tabulardata.service.rules.RuleInterface;
import org.gcube.data.analysis.tabulardata.service.tabular.HistoryStepId;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceInterface;
import org.gcube.data.analysis.tabulardata.service.template.TemplateId;
import org.gcube.data.analysis.tabulardata.service.template.TemplateInterface;

public class TabularDataServiceImpl implements TabularDataService{

	//private Logger logger = LoggerFactory.getLogger(TabularDataServiceImpl.class);
	
	
	private QueryInterface queryInterface = new QueryInterfaceImpl();

	private TabularResourceInterface tabularResourceInterface = new TabularResourceImpl();

	private OperationInterface operationInterface = new OperationInterfaceImpl();
	
	private TemplateInterface templateInterface = new TemplateInterfaceImpl();
	
	private RuleInterface ruleInterface = new RuleInterfaceImpl();

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceInterface#getTabularResources()
	 */
	public List<TabularResource> getTabularResources() {
		return tabularResourceInterface.getTabularResources();
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceInterface#getTabularResource(org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId)
	 */
	public TabularResource getTabularResource(TabularResourceId id)
			throws NoSuchTabularResourceException {
		return tabularResourceInterface.getTabularResource(id);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceInterface#removeTabularResource(org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId)
	 */
	public void removeTabularResource(TabularResourceId id)
			throws NoSuchTabularResourceException {
		tabularResourceInterface.removeTabularResource(id);		
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceInterface#cloneTabularResource(org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId)
	 */
/*	public TabularResource cloneTabularResource(TabularResourceId id)
			throws NoSuchTabularResourceException {
		return tabularResourceInterface.cloneTabularResource(id);
	}*/
	
	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterface#getTable(org.gcube.data.analysis.tabulardata.model.table.TableId)
	 */
	public Table getTable(TableId tableId) throws NoSuchTableException {
		return queryInterface.getTable(tableId);
	}
	
	public Table getTimeTable(PeriodType period){
		return queryInterface.getTimeTable(period);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceInterface#createTabularResource()
	 */
	public TabularResource createTabularResource() {
		return tabularResourceInterface.createTabularResource();
	}
	
	public TabularResource createFlow() {
		return tabularResourceInterface.createFlow();
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceInterface#getLastTable(org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId)
	 */
	public Table getLastTable(TabularResourceId id)
			throws NoSuchTabularResourceException, NoSuchTableException {
		return tabularResourceInterface.getLastTable(id);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.OperationInterface#getCapabilities()
	 */
	public List<OperationDefinition> getCapabilities() {
		return operationInterface.getCapabilities();
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.OperationInterface#getCapability(long)
	 */
	public OperationDefinition getCapability(long operationId)
			throws NoSuchOperationException {
		return operationInterface.getCapability(operationId);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.OperationInterface#execute(org.gcube.data.td.commons.webservice.types.operations.OperationExecution, org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId)
	 */
	public Task execute(OperationExecution invocation,
			TabularResourceId tabularResourceId)
			throws NoSuchTabularResourceException, NoSuchOperationException {
		return operationInterface.execute(invocation, tabularResourceId);
	}
	
	
	
	@Override
	public void executeSynchMetadataOperation(OperationExecution invocation,
			TabularResourceId tabularResourceId)
			throws NoSuchTabularResourceException, NoSuchOperationException,
			ExecutionFailedException {
		operationInterface.executeSynchMetadataOperation(invocation, tabularResourceId);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.OperationInterface#executeBatch(java.util.List, org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId)
	 */
	public Task executeBatch(List<OperationExecution> invocations,
			TabularResourceId tabularResourceId)
			throws NoSuchTabularResourceException, NoSuchOperationException {
		return operationInterface.executeBatch(invocations, tabularResourceId);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.OperationInterface#rollbackToTable(org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId, org.gcube.data.analysis.tabulardata.model.table.TableId)
	 */
	public Task rollbackTo(TabularResourceId tabularResourceId,
			HistoryStepId historyStepId) throws NoSuchTabularResourceException,
			HistoryNotFoundException {
		return operationInterface.rollbackTo(tabularResourceId, historyStepId);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.OperationInterface#getTasks(org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId)
	 */
	public List<Task> getTasks(TabularResourceId tabulaResourceId)
			throws NoSuchTabularResourceException {
		return operationInterface.getTasks(tabulaResourceId);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.OperationInterface#getTask(org.gcube.data.analysis.tabulardata.service.operation.TaskId)
	 */
	public Task getTask(TaskId taskId) throws NoSuchTaskException {
		return operationInterface.getTask(taskId);
	}

	@Override
	public List<Task> getTasks(TabularResourceId tabulaResourceId,
			TaskStatus status) throws NoSuchTabularResourceException {
		return operationInterface.getTasks(tabulaResourceId, status);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterface#getQueryLenght(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter)
	 */
	public int getQueryLenght(TableId tableId, QueryFilter filter)
			throws NoSuchTableException {
		return queryInterface.getQueryLenght(tableId, filter);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter, org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder)
	 */
	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter, QueryOrder order) throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, filter, order);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter)
	 */
	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter) throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, filter);
	}
		

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect)
	 */
	public String queryAsJson(TableId tableId, QueryPage page,
			QuerySelect select) throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, select);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup)
	 */
	public String queryAsJson(TableId tableId, QueryPage page, QueryGroup group)
			throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, group);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter, org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder, org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect)
	 */
	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter, QueryOrder order, QuerySelect select)
			throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, filter, order, select);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter, org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder, org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect, org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup)
	 */
	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter, QueryOrder order, QuerySelect select,
			QueryGroup group) throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, filter, order, select, group);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter, org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect)
	 */
	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter, QuerySelect select) throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, filter, select);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter, org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect, org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup)
	 */
	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter, QuerySelect select, QueryGroup group)
			throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, filter, select, group);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter, org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup)
	 */
	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter, QueryGroup group) throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, filter, group);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder, org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect, org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup)
	 */
	public String queryAsJson(TableId tableId, QueryPage page,
			QueryOrder order, QuerySelect select, QueryGroup group)
			throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, order, select, group);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder, org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup)
	 */
	public String queryAsJson(TableId tableId, QueryPage page,
			QueryOrder order, QueryGroup group) throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, order, group);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder, org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect)
	 */
	public String queryAsJson(TableId tableId, QueryPage page,
			QueryOrder order, QuerySelect select) throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, order, select);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect, org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup)
	 */
	public String queryAsJson(TableId tableId, QueryPage page,
			QuerySelect select, QueryGroup group) throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, select, group);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage, org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder)
	 */
	public String queryAsJson(TableId tableId, QueryPage page, QueryOrder order)
			throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page, order);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.query.QueryInterfaceJson#queryAsJson(org.gcube.data.analysis.tabulardata.model.table.TableId, org.gcube.data.analysis.tabulardata.query.parameters.QueryPage)
	 */
	public String queryAsJson(TableId tableId, QueryPage page)
			throws NoSuchTableException {
		return queryInterface.queryAsJson(tableId, page);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceInterface#share(org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId, java.lang.String[])
	 */
	public TabularResource share(TabularResourceId tabularResourceId,
			AuthorizationToken... tokens) throws NoSuchTabularResourceException, SecurityException {
		return tabularResourceInterface.share(tabularResourceId, tokens);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceInterface#unshare(org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId, java.lang.String[])
	 */
	public TabularResource unshare(TabularResourceId tabularResourceId,
			AuthorizationToken... tokens) throws NoSuchTabularResourceException, SecurityException {
		return tabularResourceInterface.unshare(tabularResourceId, tokens);
	}

	public List<TabularResource> getTabularResourcesByType(String type) {
		return tabularResourceInterface.getTabularResourcesByType(type);
	}

	public TemplateDescription getTemplate(TemplateId id) throws NoSuchTemplateException {
		return templateInterface.getTemplate(id);
	}

	public List<TemplateDescription> getTemplates() {
		return templateInterface.getTemplates();
	}

	public TemplateId saveTemplate(String name, String description,
			String agency, Template template) {
		return templateInterface.saveTemplate(name, description, agency, template);
	}
	
	public TemplateDescription update(TemplateId id, Template template)
			throws NoSuchTemplateException {
		return templateInterface.update(id, template);
	}

	public Task applyTemplate(TemplateId templateId,
			TabularResourceId tabularResourceId)
			throws NoSuchTemplateException, NoSuchTabularResourceException, TemplateNotCompatibleException {
		return templateInterface.applyTemplate(templateId, tabularResourceId);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.template.TemplateInterface#share(org.gcube.data.analysis.tabulardata.service.template.TemplateId, java.lang.String[])
	 */
	public TemplateDescription share(TemplateId templateId,
			AuthorizationToken... tokens)
			throws NoSuchTemplateException {
		return templateInterface.share(templateId, tokens);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.template.TemplateInterface#unshare(org.gcube.data.analysis.tabulardata.service.template.TemplateId, java.lang.String[])
	 */
	public TemplateDescription unshare(TemplateId templateId,
			AuthorizationToken... tokens)
			throws NoSuchTemplateException {
		return templateInterface.unshare(templateId, tokens);
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.template.TemplateInterface#remove(org.gcube.data.analysis.tabulardata.service.template.TemplateId)
	 */
	public TemplateDescription remove(TemplateId id)
			throws NoSuchTemplateException {
		return templateInterface.remove(id);
	}

	@Override
	public Task resubmit(TaskId taskId) throws NoSuchTaskException {
		return operationInterface.resubmit(taskId);
	}

	@Override
	public Task resume(TaskId taskId, Map<String, Object> operationInvocationParameter) throws NoSuchTaskException {
		return operationInterface.resume(taskId, operationInvocationParameter);
	}

	@Override
	public Task resume(TaskId taskId) throws NoSuchTaskException {
		return operationInterface.resume(taskId);
	}

	@Override
	public List<ResourceDescriptor> getResources(TabularResourceId id)
			throws NoSuchTabularResourceException {
		return tabularResourceInterface.getResources(id);
	}
	
	@Override
	public List<ResourceDescriptor> getResourcesByType(TabularResourceId id, ResourceType type)
			throws NoSuchTabularResourceException {
		return tabularResourceInterface.getResourcesByType(id, type);
	}

	@Override
	public ResourceDescriptor removeResurce(long resourceId) {
		return tabularResourceInterface.removeResurce(resourceId);
	}

	@Override
	public List<RuleDescription> getRules() {
		return ruleInterface.getRules();
	}

	@Override
	public List<RuleDescription> getRulesByScope(RuleScope scope) {
		return ruleInterface.getRulesByScope(scope);
	}

	@Override
	public void removeRuleById(RuleId id) throws NoSuchRuleException {
		ruleInterface.removeRuleById(id);		
	}
	

	@Override
	public void updateColumnRule(RuleDescription descriptor)
			throws NoSuchRuleException {
		ruleInterface.updateColumnRule(descriptor);
		
	}

	@Override
	public List<RuleDescription> getApplicableBaseColumnRules(
			Class<? extends DataType> dataTypeClass) {
		return ruleInterface.getApplicableBaseColumnRules(dataTypeClass);
	}

	@Override
	public Task applyColumnRule(TabularResourceId tabularResourceId, ColumnLocalId columnId,
			List<RuleId> ruleIds)
			throws NoSuchRuleException, NoSuchTabularResourceException {
		return ruleInterface.applyColumnRule(tabularResourceId, columnId, ruleIds);
	}

	@Override
	public AppliedRulesResponse getAppliedRulesByTabularResourceId(TabularResourceId id)
			throws NoSuchTabularResourceException {
		return ruleInterface.getAppliedRulesByTabularResourceId(id);
	}

	@Override
	public RuleDescription share(RuleId ruleId, AuthorizationToken... tokens)
			throws NoSuchRuleException, SecurityException {
		return ruleInterface.share(ruleId, tokens);
	}

	@Override
	public RuleDescription unshare(RuleId ruleId, AuthorizationToken... tokens)
			throws NoSuchRuleException, SecurityException {
		return ruleInterface.unshare(ruleId, tokens);
	}


	@Override
	public void detachColumnRules(TabularResourceId tabularResourceId,
			ColumnLocalId columnId, List<RuleId> ruleIds)
			throws NoSuchTabularResourceException {
		ruleInterface.detachColumnRules(tabularResourceId, columnId, ruleIds);		
	}

	@Override
	public RuleId saveRule(String name, String description, Expression rule,
			RuleType type) {
		return ruleInterface.saveRule(name, description, rule, type);
	}

	@Override
	public void detachTableRules(TabularResourceId tabularResourceId, List<RuleId> ruleIds)
			throws NoSuchTabularResourceException {
		ruleInterface.detachTableRules(tabularResourceId, ruleIds);
		
	}

	@Override
	public Task applyTableRule(TabularResourceId tabularResourceId,
			Map<String, String> mappingPlacelhoderIdColumnId, RuleId ruleId)
			throws NoSuchRuleException, NoSuchTabularResourceException {
		return ruleInterface.applyTableRule(tabularResourceId, mappingPlacelhoderIdColumnId, ruleId);
	}

	@Override
	public Task removeValidations(TabularResourceId tabularResourceId)
			throws NoSuchTabularResourceException {
		return operationInterface.removeValidations(tabularResourceId);
	}

	
	
}
