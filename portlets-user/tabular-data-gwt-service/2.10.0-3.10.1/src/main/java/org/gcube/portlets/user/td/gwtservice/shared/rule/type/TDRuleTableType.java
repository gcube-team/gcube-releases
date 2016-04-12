package org.gcube.portlets.user.td.gwtservice.shared.rule.type;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleColumnPlaceHolderDescriptor;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TDRuleTableType extends TDRuleType {

	private static final long serialVersionUID = -5017575127171820493L;
	private ArrayList<RuleColumnPlaceHolderDescriptor> ruleColumnPlaceHolderDescriptors;

	public TDRuleTableType() {
		super();
	}

	public TDRuleTableType(ArrayList<RuleColumnPlaceHolderDescriptor> ruleColumnPlaceHolderDescriptors) {
		super();
		this.ruleColumnPlaceHolderDescriptors = ruleColumnPlaceHolderDescriptors;
	}

	public ArrayList<RuleColumnPlaceHolderDescriptor> getRuleColumnPlaceHolderDescriptors() {
		return ruleColumnPlaceHolderDescriptors;
	}

	public void setRuleColumnPlaceHolderDescriptors(
			ArrayList<RuleColumnPlaceHolderDescriptor> ruleColumnPlaceHolderDescriptors) {
		this.ruleColumnPlaceHolderDescriptors = ruleColumnPlaceHolderDescriptors;
	}

	@Override
	public String toString() {
		return "TDRuleTableType [ruleColumnPlaceHolderDescriptors="
				+ ruleColumnPlaceHolderDescriptors + "]";
	}

	
}
