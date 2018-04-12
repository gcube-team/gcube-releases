/**
 * 
 */
package org.gcube.portlets.user.td.expressionwidget.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.rule.AppliedRulesResponseData;
import org.gcube.portlets.user.td.gwtservice.shared.rule.ApplyTableRuleSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.DetachColumnRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.DetachTableRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleScopeType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.ApplyAndDetachColumnRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.AddColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.FilterColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnByExpressionSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * Implements the basic interfaces generate and submit Expression on service.
 * 
 * Allows Generate Expression
 * 
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
@RemoteServiceRelativePath("ExpressionService")
public interface ExpressionService extends RemoteService {

	public String startFilterColumn(FilterColumnSession filterColumnSession) throws TDGWTServiceException;

	public String startReplaceColumnByExpression(
			ReplaceColumnByExpressionSession replaceColumnByExpressionColumnSession) throws TDGWTServiceException;

	public String startAddColumn(AddColumnSession addColumnSession) throws TDGWTServiceException;

	public ArrayList<RuleDescriptionData> getRules() throws TDGWTServiceException;

	public ArrayList<RuleDescriptionData> getRules(RuleScopeType scope) throws TDGWTServiceException;

	public ArrayList<RuleDescriptionData> getApplicableBaseColumnRules(ColumnData columnData)
			throws TDGWTServiceException;

	public String saveRule(RuleDescriptionData ruleDescriptionData) throws TDGWTServiceException;

	public void updateColumnRule(RuleDescriptionData ruleDescriptionData) throws TDGWTServiceException;

	public void removeRulesById(ArrayList<RuleDescriptionData> rules) throws TDGWTServiceException;

	public String startApplyAndDetachColumnRules(ApplyAndDetachColumnRulesSession applyColumnRulesSession)
			throws TDGWTServiceException;

	public void setDetachColumnRules(DetachColumnRulesSession detachColumnRulesSession) throws TDGWTServiceException;

	public void setDetachTableRules(DetachTableRulesSession detachTableRulesSession) throws TDGWTServiceException;

	public String startApplyTableRule(ApplyTableRuleSession applyTableRuleSession) throws TDGWTServiceException;

	public AppliedRulesResponseData getActiveRulesByTabularResourceId(TRId trId) throws TDGWTServiceException;

}
