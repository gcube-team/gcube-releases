package org.gcube.portlets.user.td.expressionwidget.server.service.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.AppliedRulesResponse;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.RuleDescription;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ExpressionParserException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.rule.AppliedRulesResponseData;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AppliedRulesResponseMap {
	private static Logger logger = LoggerFactory
			.getLogger(AppliedRulesResponseMap.class);

	public static AppliedRulesResponseData map(
			AppliedRulesResponse appliedRulesResponse)
			throws TDGWTServiceException, ExpressionParserException {
		AppliedRulesResponseData appliedRulesResponseData = null;
		if (appliedRulesResponse == null) {
			logger.debug("Applied Rules: " + appliedRulesResponse);
			throw new TDGWTServiceException("Applied Rules Response is null!");
		}

		ArrayList<RuleDescriptionData> tableRulesData = new ArrayList<RuleDescriptionData>();
		List<RuleDescription> tableRules = appliedRulesResponse.getTableRules();
		if (tableRules != null && tableRules.size() > 0) {
			for (RuleDescription ruleDescription : tableRules) {
				RuleDescriptionData ruleDescriptionData = RuleDescriptionMap
						.map(ruleDescription);
				if (ruleDescriptionData != null) {
					tableRulesData.add(ruleDescriptionData);
				} else {
					logger.error("Invalid Rule Description(Rule is discarded): "
							+ ruleDescription);

				}

			}
		}

		HashMap<String, ArrayList<RuleDescriptionData>> columnRuleMappingData = new HashMap<String, ArrayList<RuleDescriptionData>>();
		Map<String, List<RuleDescription>> columnRuleMapping = appliedRulesResponse
				.getColumnRuleMapping();
		if (columnRuleMapping != null && columnRuleMapping.size() > 0) {
			for (Map.Entry<String, List<RuleDescription>> entry : columnRuleMapping
					.entrySet()) {
				ArrayList<RuleDescriptionData> columnRules = new ArrayList<RuleDescriptionData>();
				for (RuleDescription ruleDescription : entry.getValue()) {
					RuleDescriptionData ruleDescriptionData = RuleDescriptionMap
							.map(ruleDescription);
					if (ruleDescriptionData != null) {
						columnRules.add(ruleDescriptionData);
					} else {
						logger.error("Invalid Rule Description(Rule is discarded): "
								+ ruleDescription);

					}
				}
				columnRuleMappingData.put(entry.getKey(), columnRules);

			}
		}

		appliedRulesResponseData = new AppliedRulesResponseData(tableRulesData,
				columnRuleMappingData);

		logger.debug("Applied Rules: " + appliedRulesResponseData);
		return appliedRulesResponseData;

	}

}
