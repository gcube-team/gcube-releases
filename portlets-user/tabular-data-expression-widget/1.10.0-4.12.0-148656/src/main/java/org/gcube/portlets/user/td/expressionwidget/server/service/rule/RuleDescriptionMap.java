package org.gcube.portlets.user.td.expressionwidget.server.service.rule;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.RuleDescription;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.portlets.user.td.expressionwidget.server.C_ExpressionParser;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ExpressionParserException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDRuleType;
import org.gcube.portlets.user.td.gwtservice.shared.share.Contacts;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class RuleDescriptionMap {
	private static final Logger logger = LoggerFactory
			.getLogger(RuleDescriptionMap.class);

	private static final String SECURITY_EXCEPTION_RIGHTS = "Security exception, you don't have the required rights!";

	public static RuleDescriptionData map(RuleDescription ruleDescription)
			throws TDGWTServiceException, ExpressionParserException {
		RuleDescriptionData ruleDescriptionData = null;
		if (ruleDescription == null) {
			logger.error("Rule description is null");
			throw new TDGWTServiceException("Rule description is null");

		}

		if (ruleDescription.getScope() == null) {
			logger.error("Rule description has scope null");
			throw new TDGWTServiceException("Rule description has scope null");
		}

		ruleDescriptionData = createRuleDescriptionData(ruleDescription);

		return ruleDescriptionData;

	}

	private static RuleDescriptionData createRuleDescriptionData(
			RuleDescription ruleDescription) throws ExpressionParserException,
			TDGWTServiceException {

		Expression serviceExpression = ruleDescription.getRule();

		C_ExpressionParser parser = new C_ExpressionParser();

		C_Expression cexp = parser.parse(serviceExpression);
		RuleType ruleType = ruleDescription.getRuleType();

		TDRuleType tdRuleType = RuleTypeMap.map(ruleType);

		Contacts owner = new Contacts("", ruleDescription.getOwner(), false);
		ArrayList<Contacts> contacts = retrieveRuleShareInfo(ruleDescription);

		Date creationDate = null;
		try {
			creationDate = ruleDescription.getCreationDate().getTime();
		} catch (Throwable e) {
			logger.error("Error in rule description, creation date is not valid!"
					+ ruleDescription.getCreationDate());
			throw new TDGWTServiceException(
					"Error in rule description, creation date is not valid!");
		}
		RuleDescriptionData ruleData = new RuleDescriptionData(
				ruleDescription.getId(), ruleDescription.getName(),
				ruleDescription.getDescription(), creationDate, owner,
				contacts, RuleScopeMap.map(ruleDescription.getScope()), cexp,
				tdRuleType);

		return ruleData;
	}

	private static ArrayList<Contacts> retrieveRuleShareInfo(
			RuleDescription ruleDescription) throws TDGWTServiceException {
		try {

			ArrayList<Contacts> contacts = new ArrayList<Contacts>();
			List<String> sharedWithUsers = ruleDescription.getSharedWithUsers();
			logger.debug("Shared with Users: " + sharedWithUsers);
			if (sharedWithUsers != null) {
				for (String user : sharedWithUsers) {
					Contacts cont = new Contacts(user, user, false);
					contacts.add(cont);
				}
			}

			List<String> sharedWithGroups = ruleDescription
					.getSharedWithGroups();
			logger.debug("Shared with Groups: " + sharedWithUsers);
			if (sharedWithGroups != null) {
				for (String group : sharedWithGroups) {
					Contacts cont = new Contacts(group, group, true);
					contacts.add(cont);
				}
			}
			return contacts;
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new TDGWTServiceException(SECURITY_EXCEPTION_RIGHTS);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error in retrieveShareInfo: "
					+ e.getLocalizedMessage());
		}

	}

	public static RuleDescription map(RuleDescriptionData ruleDescriptionData)
			throws TDGWTServiceException {
		RuleDescription ruleDescription = null;
		if (ruleDescriptionData == null) {
			logger.error("Rule description data is null");
			throw new TDGWTServiceException("Rule description is null");

		}

		if (ruleDescriptionData.getScope() == null) {
			logger.error("Rule description data has scope null");
			throw new TDGWTServiceException("Rule description has scope null");
		}

		ruleDescription = createRuleDescription(ruleDescriptionData);

		return ruleDescription;
	}

	private static RuleDescription createRuleDescription(
			RuleDescriptionData ruleDescriptionData)
			throws TDGWTServiceException {

		C_ExpressionParser parser = new C_ExpressionParser();

		Expression conditionExpression = null;
		try {
			conditionExpression = parser.parse(ruleDescriptionData
					.getExpression());
		} catch (ExpressionParserException e) {
			logger.error(e.getLocalizedMessage());
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}

		logger.debug("Service Condition Expression:" + conditionExpression);

		RuleType ruleType = RuleTypeMap
				.map(ruleDescriptionData.getTdRuleType());

		if (ruleType == null) {
			logger.error("Error saving rule, rule column type is null!");
			throw new TDGWTServiceException(
					"Error saving rule, rule column type is null!");
		}

		GregorianCalendar creationDate = new GregorianCalendar();
		creationDate.setTime(ruleDescriptionData.getCreationDate());

		RuleDescription ruleDescription = new RuleDescription(
				ruleDescriptionData.getId(), ruleDescriptionData.getName(),
				ruleDescriptionData.getDescription(), creationDate,
				conditionExpression, RuleScopeMap.map(ruleDescriptionData
						.getScope()), ruleDescriptionData.getOwnerLogin(),
				ruleType, ruleDescriptionData.getContactsAsStringList());

		return ruleDescription;
	}

}
