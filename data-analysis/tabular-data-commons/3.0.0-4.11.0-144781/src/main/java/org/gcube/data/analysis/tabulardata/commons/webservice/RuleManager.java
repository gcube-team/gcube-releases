package org.gcube.data.analysis.tabulardata.commons.webservice;

import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.gcube.data.analysis.tabulardata.commons.rules.RuleScope;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleType;
import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchRuleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.AppliedRulesResponse;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.MapObject;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.RuleDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@WebService(targetNamespace=Constants.RULE_TNS)
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface RuleManager extends Sharable<Long, RuleDescription, NoSuchRuleException>{

	public static final String SERVICE_NAME = "rulemanager";

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	long saveRule(String name, String description, Expression rule, RuleType type ) throws InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<RuleDescription> getRules() throws InternalSecurityException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<RuleDescription> getRulesByScope(RuleScope scope) throws InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	void remove(Long id) throws InternalSecurityException, NoSuchRuleException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	void updateColumnRule(RuleDescription descriptor)
			throws InternalSecurityException, NoSuchRuleException;

	List<RuleDescription> getApplicableBaseColumnRules(Class<? extends DataType> dataTypeClass)
			throws InternalSecurityException;

	TaskInfo applyColumnRule(Long tabularResourceId, String columnId,
			List<Long> ruleIds) throws InternalSecurityException, NoSuchRuleException,
			NoSuchTabularResourceException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	AppliedRulesResponse getAppliedRulesByTabularResourceId(Long id)  throws NoSuchTabularResourceException, InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	void detachColumnRules(Long tabularResourceId, String columnId,
			List<Long> ruleIds) throws InternalSecurityException, NoSuchTabularResourceException;
	

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	TaskInfo applyTableRule(Long tabularResourceId,
			MapObject<String, String> mappingPlaceholderIDToColumnId,
			Long ruleId) throws InternalSecurityException,
			NoSuchRuleException, NoSuchTabularResourceException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	void detachTableRules(Long tabularResourceId, List<Long> ruleIds)
			throws InternalSecurityException, NoSuchTabularResourceException;
}
