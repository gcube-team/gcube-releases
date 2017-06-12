package org.gcube.data.analysis.tabulardata.service.rules;

import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.rules.RuleScope;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleType;
import org.gcube.data.analysis.tabulardata.commons.utils.SharingEntity;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchRuleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.AppliedRulesResponse;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.RuleDescription;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;

public interface RuleInterface {

	RuleId saveRule(String name, String description, Expression rule, RuleType type );
	
	List<RuleDescription> getRules();
	
	List<RuleDescription> getRulesByScope(RuleScope scope);

	void removeRuleById(RuleId id) throws NoSuchRuleException;
	
	void updateColumnRule(RuleDescription descriptor) throws NoSuchRuleException;
	
	List<RuleDescription> getApplicableBaseColumnRules(Class<? extends DataType> dataTypeClass);
	
	Task applyColumnRule(TabularResourceId tabularResourceId, ColumnLocalId columnId,
			List<RuleId> ruleIds) throws NoSuchRuleException,
			NoSuchTabularResourceException;
	
	AppliedRulesResponse getAppliedRulesByTabularResourceId(TabularResourceId id) throws NoSuchTabularResourceException;
	
	RuleDescription share(RuleId ruleId, SharingEntity... entities) throws NoSuchRuleException, SecurityException;
	
	RuleDescription unshare(RuleId ruleId, SharingEntity... entities) throws NoSuchRuleException, SecurityException;
	
	void detachColumnRules(TabularResourceId tabularResourceId, ColumnLocalId columnId,
			final List<RuleId> ruleIds) throws NoSuchTabularResourceException;
	
	void detachTableRules(TabularResourceId tabularResourceId,  List<RuleId> ruleIds) throws NoSuchTabularResourceException;

	Task applyTableRule(TabularResourceId tabularResourceId,
			Map<String, String> mappingPlacelhoderIdColumnId, RuleId ruleId)
			throws NoSuchRuleException, NoSuchTabularResourceException;	
}
