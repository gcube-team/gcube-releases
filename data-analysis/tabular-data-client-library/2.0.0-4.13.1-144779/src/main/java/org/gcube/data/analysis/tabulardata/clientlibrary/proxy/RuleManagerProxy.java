package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.rules.RuleScope;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleType;
import org.gcube.data.analysis.tabulardata.commons.webservice.Sharable;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchRuleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.AppliedRulesResponse;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.RuleDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

public interface RuleManagerProxy extends Sharable<Long, RuleDescription, NoSuchRuleException>{

	long saveRule(String name, String description, Expression rule,
			RuleType type);
		
	List<RuleDescription> getRules();

	List<RuleDescription> getRulesByScope(RuleScope scope);
	
	void remove(Long id) throws NoSuchRuleException;
	
	void updateColumnRule(RuleDescription descriptor) throws NoSuchRuleException;

	List<RuleDescription> getApplicableBaseColumnRules(Class<? extends DataType> dataTypeClass);
		
	TaskInfo applyColumnRule(Long tabularResourceId, String columnId,
			List<Long> ruleIds) throws NoSuchRuleException,
			NoSuchTabularResourceException;
	
	AppliedRulesResponse getAppliedRulesByTabularResourceId(Long id) throws NoSuchTabularResourceException;
		
	void detachColumnRules(Long tabularResourceId, String columnId,  List<Long> ruleIds) throws NoSuchTabularResourceException;	
		
	void detachTableRules(Long tabularResourceId,  List<Long> ruleIds) throws NoSuchTabularResourceException;

	TaskInfo applyTableRule(Long tabularResourceId,
			Map<String, String> mappingPlacelhoderIdColumnId, Long ruleId)
			throws NoSuchRuleException, NoSuchTabularResourceException;	
}
