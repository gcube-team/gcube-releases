package org.gcube.portlets.user.td.gwtservice.shared.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class AppliedRulesResponseData implements Serializable {

	private static final long serialVersionUID = -8194665246008663941L;

	private ArrayList<RuleDescriptionData> tableRules;
	//Map of <ColumnId, Rules>
	private HashMap<String, ArrayList<RuleDescriptionData>> columnRuleMapping;

	public AppliedRulesResponseData() {
		super();
	}

	public AppliedRulesResponseData(ArrayList<RuleDescriptionData> tableRules,
			HashMap<String, ArrayList<RuleDescriptionData>> columnRuleMapping) {
		super();
		this.tableRules = tableRules;
		this.columnRuleMapping = columnRuleMapping;
	}

	public ArrayList<RuleDescriptionData> getTableRules() {
		return tableRules;
	}

	public void setTableRules(ArrayList<RuleDescriptionData> tableRules) {
		this.tableRules = tableRules;
	}

	public HashMap<String, ArrayList<RuleDescriptionData>> getColumnRuleMapping() {
		return columnRuleMapping;
	}

	public void setColumnRuleMapping(
			HashMap<String, ArrayList<RuleDescriptionData>> columnRuleMapping) {
		this.columnRuleMapping = columnRuleMapping;
	}

	@Override
	public String toString() {
		return "AppliedRulesResponseData [tableRules=" + tableRules
				+ ", columnRuleMapping=" + columnRuleMapping + "]";
	}

}
