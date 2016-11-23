package org.gcube.portlets.user.td.expressionwidget.server;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleType;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.AppliedRulesResponse;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.RuleDescription;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.rules.RuleId;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.portlets.user.td.expressionwidget.client.rpc.ExpressionService;
import org.gcube.portlets.user.td.expressionwidget.server.service.rule.AppliedRulesResponseMap;
import org.gcube.portlets.user.td.expressionwidget.server.service.rule.RuleDescriptionMap;
import org.gcube.portlets.user.td.expressionwidget.server.service.rule.RuleScopeMap;
import org.gcube.portlets.user.td.expressionwidget.server.service.rule.RuleTypeMap;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ExpressionParserException;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ExpressionServiceException;
import org.gcube.portlets.user.td.gwtservice.server.SessionUtil;
import org.gcube.portlets.user.td.gwtservice.server.TDGWTServiceImpl;
import org.gcube.portlets.user.td.gwtservice.server.TDGWTServiceMessagesConstants;
import org.gcube.portlets.user.td.gwtservice.server.trservice.ColumnDataTypeMap;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
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
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ExpressionServiceImpl extends TDGWTServiceImpl implements
		ExpressionService {

	private static final long serialVersionUID = 4632292751581364137L;

	private static Logger logger = LoggerFactory
			.getLogger(ExpressionServiceImpl.class);

	// private static SimpleDateFormat sdf = new SimpleDateFormat(
	// "yyyy-MM-dd HH:mm");

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startFilterColumn(FilterColumnSession filterColumnSession)
			throws TDGWTServiceException {
		HttpSession session=null;
		try {
			logger.debug("ExpressionService submitColumnFilter");
			session = this.getThreadLocalRequest().getSession();
			logger.debug("Session: " + session);

			if (filterColumnSession == null) {
				logger.error("FilterColumnSession is null");
				new ExpressionServiceException("FilterColumnSession is null");
			}
			ExpressionSession.setColumnFilterSession(session,
					filterColumnSession);

			C_ExpressionParser parser = new C_ExpressionParser();
			Expression expression = null;
			try {
				expression = parser.parse(filterColumnSession.getCexpression());
			} catch (ExpressionParserException e) {
				logger.debug(e.getLocalizedMessage());
				throw new TDGWTServiceException(e.getLocalizedMessage());
			}
			logger.debug("Service Expression:" + expression);

			// TDGWTServiceImpl gwtService = new TDGWTServiceImpl();

			String taskId = startFilterColumn(filterColumnSession, expression,
					session);

			return taskId;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(session);
			throw new TDGWTServiceException(
					messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));		
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */

	public String startAddColumn(AddColumnSession addColumnSession)
			throws TDGWTServiceException {
		HttpSession session=null;
		try {
			logger.debug("AddColumn");
			session = this.getThreadLocalRequest().getSession();
			logger.debug("Session: " + session);

			if (addColumnSession == null) {
				logger.error("AddColumnSession is null");
				new ExpressionServiceException("AddColumnSession is null");
			}

			ColumnMockUp columnMockUp = addColumnSession.getColumnMockUp();

			Expression expression = null;
			if (columnMockUp.hasExpression()) {

				C_ExpressionParser parser = new C_ExpressionParser();

				try {
					expression = parser.parse(columnMockUp.getExpression());
				} catch (ExpressionParserException e) {
					logger.debug(e.getLocalizedMessage());
					throw new TDGWTServiceException(e.getLocalizedMessage());
				}
				logger.debug("Service Expression:" + expression);

			}

			// TDGWTServiceImpl gwtService = new TDGWTServiceImpl();

			String taskId = startAddColumn(addColumnSession, expression,
					session);

			return taskId;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(session);
			throw new TDGWTServiceException(
					messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));	
		
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startReplaceColumnByExpression(
			ReplaceColumnByExpressionSession replaceColumnByExpressionSession)
			throws TDGWTServiceException {

		HttpSession session=null;
		try {
			logger.debug("ExpressionService submitReplaceColumnByExpression");
			session = this.getThreadLocalRequest().getSession();
			logger.debug("Session: " + session);

			if (replaceColumnByExpressionSession == null) {
				logger.error("ReplaceColumnByExpressionSession is null");
				new ExpressionServiceException(
						"ReplaceColumnByExpressionSession is null");
			}
			ExpressionSession.setReplaceColumnByExpressionSession(session,
					replaceColumnByExpressionSession);

			C_ExpressionParser parser = new C_ExpressionParser();

			Expression conditionExpression = null;
			if (!replaceColumnByExpressionSession.isAllRows()) {
				try {
					conditionExpression = parser
							.parse(replaceColumnByExpressionSession
									.getcConditionExpression());
				} catch (ExpressionParserException e) {
					logger.debug(e.getLocalizedMessage());
					throw new TDGWTServiceException(e.getLocalizedMessage());
				}

				logger.debug("Service Condition Expression:"
						+ conditionExpression);
			}

			Expression replaceExpression = null;
			if (!replaceColumnByExpressionSession.isReplaceByValue()) {
				replaceExpression = parser
						.parse(replaceColumnByExpressionSession
								.getcReplaceExpression());
				logger.debug("Service Replace Expression:" + replaceExpression);
			}

			// TDGWTServiceImpl gwtService = new TDGWTServiceImpl();

			String taskId = startReplaceColumnByExpression(
					replaceColumnByExpressionSession, conditionExpression,
					replaceExpression, session);
			return taskId;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (SecurityException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(session);
			throw new TDGWTServiceException(
					messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));	
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<RuleDescriptionData> getRules()
			throws TDGWTServiceException {
		HttpSession session=null;
		try {
			session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getAslSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);
			
			logger.debug("GetRules()");
			AuthorizationProvider.instance.set(new AuthorizationToken(
					aslSession.getUsername(), aslSession.getScope()));
			TabularDataService service = TabularDataServiceFactory.getService();

			List<RuleDescription> rules = service.getRules();
			logger.debug("Service Rules: "
					+ ((rules == null) ? "null" : rules.size()));

			ArrayList<RuleDescriptionData> rulesDes = new ArrayList<RuleDescriptionData>();

			for (RuleDescription ruleDescription : rules) {
				RuleDescriptionData ruleData = RuleDescriptionMap
						.map(ruleDescription);
				logger.debug("Rule: " + ruleData);
				rulesDes.add(ruleData);
			}

			logger.debug("Rules: " + rulesDes.size());
			return rulesDes;

		} catch (TDGWTServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (SecurityException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(session);
			throw new TDGWTServiceException(
					messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
	
		} catch (Throwable e) {
			logger.error("Error in getRules(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving the rules: "
					+ e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<RuleDescriptionData> getRules(RuleScopeType scope)
			throws TDGWTServiceException {
		HttpSession session=null;
		try {
			session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getAslSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);

			logger.debug("GetRules(): " + scope);
			AuthorizationProvider.instance.set(new AuthorizationToken(
					aslSession.getUsername(), aslSession.getScope()));
			TabularDataService service = TabularDataServiceFactory.getService();

			List<RuleDescription> rules = service.getRulesByScope(RuleScopeMap
					.map(scope));
			logger.debug("Service Rules: "
					+ ((rules == null) ? "null" : rules.size()));

			ArrayList<RuleDescriptionData> rulesDes = new ArrayList<RuleDescriptionData>();

			for (RuleDescription ruleDescription : rules) {
				RuleDescriptionData ruleData = RuleDescriptionMap
						.map(ruleDescription);
				logger.debug("Rule: " + ruleData);
				rulesDes.add(ruleData);

			}
			logger.debug("Rules: " + rulesDes.size());
			return rulesDes;

		} catch (TDGWTServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (SecurityException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(session);
			throw new TDGWTServiceException(
					messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
		} catch (Throwable e) {
			logger.error("Error in getRules(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving the rules: "
					+ e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<RuleDescriptionData> getApplicableBaseColumnRules(
			ColumnData columnData) throws TDGWTServiceException {
		HttpSession session=null;
		try {
			session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getAslSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);

			logger.debug("GetApplicableBaseColumnRules(): " + columnData);
			AuthorizationProvider.instance.set(new AuthorizationToken(
					aslSession.getUsername(), aslSession.getScope()));
			TabularDataService service = TabularDataServiceFactory.getService();

			if (columnData == null) {
				logger.error("Error in getApplicableBaseColumnRules(): No valid column, "
						+ columnData);
				throw new TDGWTServiceException(
						"Error in getRules: no valid column set");
			}

			ColumnDataType columnDataType = ColumnDataType
					.getColumnDataTypeFromId(columnData.getDataTypeName());
			Class<? extends DataType> dataType = ColumnDataTypeMap
					.mapToDataTypeClass(columnDataType);

			List<RuleDescription> rules = service
					.getApplicableBaseColumnRules(dataType);
			if (rules == null) {
				logger.error("Invalid applicable base column rules from service: null");
				throw new TDGWTServiceException(
						"Error retrieving the rules: invalid applicable base column rules from service(null)");
			} else {
				logger.debug("Applicable Base Column Rules: " + rules.size());
			}
			ArrayList<RuleDescriptionData> rulesDes = new ArrayList<RuleDescriptionData>();

			for (RuleDescription ruleDescription : rules) {
				RuleDescriptionData ruleData = RuleDescriptionMap
						.map(ruleDescription);
				logger.debug("Rule:" + ruleData);
				rulesDes.add(ruleData);

			}
			logger.debug("Rules: " + rulesDes.size());
			return rulesDes;

		} catch (TDGWTServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (SecurityException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(session);
			throw new TDGWTServiceException(
					messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
	
		} catch (Throwable e) {
			logger.error("Error in getApplicableBaseColumnRules(): "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving the rules: "
					+ e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public AppliedRulesResponseData getActiveRulesByTabularResourceId(TRId trId)
			throws TDGWTServiceException {
		HttpSession session=null;
		try {
			session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getAslSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);

			logger.debug("GetAppliedRulesByTabularResourceId(): " + trId);
			AuthorizationProvider.instance.set(new AuthorizationToken(
					aslSession.getUsername(), aslSession.getScope()));
			TabularDataService service = TabularDataServiceFactory.getService();

			if (trId == null || trId.getId() == null || trId.getId().isEmpty()) {
				logger.error("Error in getActiveRulesByTabularResourceId(): No valid tabular resource id!");
				throw new TDGWTServiceException(
						"Error in get active rules: invalid tabular resource id!");
			}

			TabularResourceId tabularResourceId = new TabularResourceId(
					Long.valueOf(trId.getId()));

			AppliedRulesResponse appliedRulesResponse = service
					.getAppliedRulesByTabularResourceId(tabularResourceId);
			if (appliedRulesResponse == null) {
				logger.error("Invalid active rules response from service: null");
				throw new TDGWTServiceException(
						"Invalid active rules response from service: null");
			} else {
				logger.debug("Active Rules Response: " + appliedRulesResponse);
			}

			AppliedRulesResponseData appliedRulesResponseData = AppliedRulesResponseMap
					.map(appliedRulesResponse);

			return appliedRulesResponseData;
		} catch (TDGWTServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (SecurityException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(session);
			throw new TDGWTServiceException(
					messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
	
		} catch (Throwable e) {
			logger.error("Error in getActiveRulesByTabularResourceId(): "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving active rules: "
					+ e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String saveRule(
			RuleDescriptionData ruleDescriptionData)
			throws TDGWTServiceException {
		HttpSession session=null;
		try {
			session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getAslSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);

			logger.debug("SaveRule() :" + ruleDescriptionData);
			AuthorizationProvider.instance.set(new AuthorizationToken(
					aslSession.getUsername(), aslSession.getScope()));
			TabularDataService service = TabularDataServiceFactory.getService();

			C_ExpressionParser parser = new C_ExpressionParser();

			Expression conditionExpression = null;
			try {
				conditionExpression = parser.parse(ruleDescriptionData
						.getExpression());
			} catch (ExpressionParserException e) {
				logger.debug(e.getLocalizedMessage());
				e.printStackTrace();
				throw new TDGWTServiceException(e.getLocalizedMessage());
			}

			logger.debug("Service Condition Expression:" + conditionExpression);

			RuleType ruleType = RuleTypeMap
					.map(ruleDescriptionData.getTdRuleType());
			if (ruleType == null) {
				logger.error("Error saving rule, rule type is null!");
				throw new TDGWTServiceException(
						"Error saving rule, rule type is null!");
			}
			
			

			RuleId ruleId = service.saveRule(
					ruleDescriptionData.getName(),
					ruleDescriptionData.getDescription(),
					conditionExpression, ruleType);
			
			logger.debug("RuleId: " + ruleId);
		
			String ruleIdent = null;
			
			if (ruleId != null) {
				ruleIdent = String.valueOf(ruleId.getValue());
			}

			return ruleIdent;

		} catch (TDGWTServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (SecurityException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(session);
			throw new TDGWTServiceException(
					messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
	
		} catch (Throwable e) {
			logger.error("Error in save rule: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error in save rule: "
					+ e.getLocalizedMessage());
		}

	}


	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void updateColumnRule(RuleDescriptionData ruleDescriptionData)
			throws TDGWTServiceException {
		HttpSession session=null;
		try {
			session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getAslSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);

			logger.debug("UpdateColumnRule() :" + ruleDescriptionData);
			AuthorizationProvider.instance.set(new AuthorizationToken(
					aslSession.getUsername(), aslSession.getScope()));
			TabularDataService service = TabularDataServiceFactory.getService();

			RuleDescription ruleDescription = RuleDescriptionMap
					.map(ruleDescriptionData);

			if (ruleDescription == null) {
				logger.error("Error in rule description: null!");
				throw new TDGWTServiceException(
						"Error in rule description: null!");
			}

			service.updateColumnRule(ruleDescription);

			logger.debug("Rule updated");

			return;

		} catch (TDGWTServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (SecurityException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(session);
			throw new TDGWTServiceException(
					messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
	
		} catch (Throwable e) {
			logger.error("Error in getRule(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error retrieving the rules: "
					+ e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void removeRulesById(ArrayList<RuleDescriptionData> rules)
			throws TDGWTServiceException {
		HttpSession session=null;
		try {
			session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getAslSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);

			logger.debug("RemoveRuleById() :" + rules);
			if (rules == null || !(rules.size() > 0)) {
				throw new TDGWTServiceException(
						"Error removing the rule, no rules selected");
			}

			AuthorizationProvider.instance.set(new AuthorizationToken(
					aslSession.getUsername(), aslSession.getScope()));
			TabularDataService service = TabularDataServiceFactory.getService();

			for (RuleDescriptionData ruleDescriptionData : rules) {
				RuleId id = new RuleId(ruleDescriptionData.getId());
				service.removeRuleById(id);
			}
			return;

		} catch (TDGWTServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (SecurityException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			ResourceBundle messages = getResourceBundle(session);
			throw new TDGWTServiceException(
					messages.getString(TDGWTServiceMessagesConstants.securityExceptionRights));
	
		} catch (Throwable e) {
			logger.error("Error in removeRuleById(): "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error removing the rules: "
					+ e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String startApplyAndDetachColumnRules(
			ApplyAndDetachColumnRulesSession applyAndDetachColumnRulesSession)
			throws TDGWTServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			String taskId = startApplyAndDetachColumnnRules(
					applyAndDetachColumnRulesSession, session);
			return taskId;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}

	}
	
	
	@Override
	public String startApplyTableRule(
			ApplyTableRuleSession applyTableRuleSession)
			throws TDGWTServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			String taskId = startApplyTableRule(
					applyTableRuleSession, session);
			return taskId;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}

	}
	

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setDetachColumnRules(
			DetachColumnRulesSession detachColumnRulesSession)
			throws TDGWTServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			setDetachColumnnRules(detachColumnRulesSession, session);
			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}

	}

	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setDetachTableRules(
			DetachTableRulesSession detachTableRulesSession)
			throws TDGWTServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			setDetachTableRules(detachTableRulesSession, session);
			return;

		} catch (TDGWTServiceException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}

	}

	
}