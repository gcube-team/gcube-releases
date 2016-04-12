package org.gcube.data.analysis.tabulardata.service.impl.rules;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.RuleManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.rules.RuleScope;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleType;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchRuleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.AppliedRulesResponse;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.RuleDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.service.impl.operation.tasks.TaskFactory;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.rules.RuleId;
import org.gcube.data.analysis.tabulardata.service.rules.RuleInterface;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;

public class RuleInterfaceImpl implements RuleInterface{

	private RuleManagerProxy manager = rule().build();

	@Override
	public RuleId saveRule(String name, String description,
			Expression rule, RuleType type) {
		return new RuleId(manager.saveRule(name, description, rule, type));
	}

	@Override
	public List<RuleDescription> getRules() {
		return manager.getRules();
	}

	@Override
	public List<RuleDescription> getRulesByScope(RuleScope scope) {
		return manager.getRulesByScope(scope);
	}

	@Override
	public void removeRuleById(RuleId id) throws NoSuchRuleException {
		manager.remove(id.getValue());
	}

	@Override
	public void updateColumnRule(RuleDescription descriptor) throws NoSuchRuleException{
		manager.updateColumnRule(descriptor);		
	}

	@Override
	public List<RuleDescription> getApplicableBaseColumnRules(
			Class<? extends DataType> dataTypeClass) {
		return manager.getApplicableBaseColumnRules(dataTypeClass);
	}

	@Override
	public Task applyColumnRule(TabularResourceId tabularResourceId, ColumnLocalId columnId,
			List<RuleId> ruleIds) throws NoSuchRuleException,
			NoSuchTabularResourceException {
		ArrayList<Long> ruleIdsTranformed = new ArrayList<>(ruleIds.size());
		for (RuleId id: ruleIds) ruleIdsTranformed.add(id.getValue());
		
		TaskInfo taskInfo = manager.applyColumnRule(tabularResourceId.getValue(), columnId.getValue(), ruleIdsTranformed);
		return TaskFactory.getFactory().createTask(taskInfo);
	}


	@Override
	public void detachColumnRules(TabularResourceId tabularResourceId,
			ColumnLocalId columnId, List<RuleId> ruleIds)
			throws NoSuchTabularResourceException {
		ArrayList<Long> ruleIdsTranformed = new ArrayList<>(ruleIds.size());
		for (RuleId id: ruleIds) ruleIdsTranformed.add(id.getValue());
		manager.detachColumnRules(tabularResourceId.getValue(), columnId.getValue(), ruleIdsTranformed);
	}
	
	
	@Override
	public AppliedRulesResponse getAppliedRulesByTabularResourceId(TabularResourceId id)
			throws NoSuchTabularResourceException {
		return manager.getAppliedRulesByTabularResourceId(id.getValue());
	}

	@Override
	public RuleDescription share(RuleId ruleId, AuthorizationToken... tokens)
			throws NoSuchRuleException, SecurityException {
		try {
			if (tokens==null || tokens.length ==0) throw new RuntimeException("list of users is empty");
			return manager.share(ruleId.getValue(), tokens);
		} catch (InternalSecurityException e) {
			throw new SecurityException(e.getMessage());
		}
	}

	@Override
	public RuleDescription unshare(RuleId ruleId, AuthorizationToken... tokens)
			throws NoSuchRuleException, SecurityException {
		try {
			if (tokens==null || tokens.length ==0) throw new RuntimeException("list of users is empty");
			return manager.unshare(ruleId.getValue(), tokens);
		} catch (InternalSecurityException e) {
			throw new SecurityException(e.getMessage());
		}
	}

	@Override
	public void detachTableRules(TabularResourceId tabularResourceId, List<RuleId> ruleIds)
			throws NoSuchTabularResourceException {
		ArrayList<Long> ruleIdsTranformed = new ArrayList<>(ruleIds.size());
		for (RuleId id: ruleIds) ruleIdsTranformed.add(id.getValue());
		manager.detachTableRules(tabularResourceId.getValue(), ruleIdsTranformed);
		
	}

	@Override
	public Task applyTableRule(TabularResourceId tabularResourceId,
			Map<String, String> mappingPlacelhoderIdColumnId, RuleId ruleId)
			throws NoSuchRuleException, NoSuchTabularResourceException {
		TaskInfo taskInfo = manager.applyTableRule(tabularResourceId.getValue(), mappingPlacelhoderIdColumnId, ruleId.getValue());
		return TaskFactory.getFactory().createTask(taskInfo);
	}


}
