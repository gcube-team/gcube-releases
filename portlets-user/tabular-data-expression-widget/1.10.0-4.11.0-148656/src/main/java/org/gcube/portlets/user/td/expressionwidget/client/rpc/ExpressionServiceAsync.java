/**
 * 
 */
package org.gcube.portlets.user.td.expressionwidget.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.rule.AppliedRulesResponseData;
import org.gcube.portlets.user.td.gwtservice.shared.rule.ApplyAndDetachColumnRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.ApplyTableRuleSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.DetachColumnRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.DetachTableRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleScopeType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.AddColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.FilterColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnByExpressionSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */

public interface ExpressionServiceAsync {

	public static ExpressionServiceAsync INSTANCE = (ExpressionServiceAsync) GWT
			.create(ExpressionService.class);

	void startFilterColumn(FilterColumnSession filterColumnSession,
			AsyncCallback<String> callback);

	void startReplaceColumnByExpression(
			ReplaceColumnByExpressionSession replaceColumnByExpressionSession,
			AsyncCallback<String> callback);

	void startAddColumn(AddColumnSession addColumnSession,
			AsyncCallback<String> callback);

	void getRules(AsyncCallback<ArrayList<RuleDescriptionData>> callback);

	void getRules(RuleScopeType scope,
			AsyncCallback<ArrayList<RuleDescriptionData>> callback);

	void getApplicableBaseColumnRules(ColumnData columnData,
			AsyncCallback<ArrayList<RuleDescriptionData>> callback);

	void saveRule(RuleDescriptionData ruleDescriptionData,
			AsyncCallback<String> callback);

	
	void updateColumnRule(RuleDescriptionData ruleDescriptionData,
			AsyncCallback<Void> callback);

	void removeRulesById(ArrayList<RuleDescriptionData> rules,
			AsyncCallback<Void> callback);

	void startApplyAndDetachColumnRules(
			ApplyAndDetachColumnRulesSession ruleOnColumnApplySession,
			AsyncCallback<String> callback);

	void setDetachColumnRules(
			DetachColumnRulesSession detachColumnRulesSession,
			AsyncCallback<Void> callback);
	
	void setDetachTableRules(
			DetachTableRulesSession detachTableRulesSession,
			AsyncCallback<Void> callback);

	void startApplyTableRule(
			ApplyTableRuleSession applyTableRuleSession,
			AsyncCallback<String> callback);

	
	
	void getActiveRulesByTabularResourceId(TRId trId,
			AsyncCallback<AppliedRulesResponseData> callback);

}
