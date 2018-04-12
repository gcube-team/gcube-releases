package org.gcube.portlets.user.td.expressionwidget.server.service.rule;

import org.gcube.data.analysis.tabulardata.commons.rules.RuleScope;
import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleScopeType;


/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class RuleScopeMap {
	public static RuleScopeType map(RuleScope ruleScope) {
		if(ruleScope==null){
			return null;
		}
		
		switch (ruleScope) {
		case COLUMN:
			return RuleScopeType.COLUMN;
		case TABLE:
			return RuleScopeType.TABLE;
		default:
			return null;
		
		}
	}
	
	public static RuleScope map(RuleScopeType ruleScopeType) {
		if(ruleScopeType==null){
			return null;
		}
		
		switch (ruleScopeType) {
		case COLUMN:
			return RuleScope.COLUMN;
		case TABLE:
			return RuleScope.TABLE;
		default:
			return null;
		
		}
	}
	
}
