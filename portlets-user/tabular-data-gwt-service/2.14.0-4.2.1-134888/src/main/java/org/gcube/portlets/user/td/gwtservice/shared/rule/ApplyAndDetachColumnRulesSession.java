package org.gcube.portlets.user.td.gwtservice.shared.rule;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ApplyAndDetachColumnRulesSession implements Serializable {

	private static final long serialVersionUID = -7746819321348425711L;
	private TRId trId;
	private ColumnData column;
	private ArrayList<RuleDescriptionData> rulesThatWillBeApplied;
	private ArrayList<RuleDescriptionData> rulesThatWillBeDetached;

	public ApplyAndDetachColumnRulesSession() {
		super();
	}

	/**
	 * 
	 * @param trId
	 * @param column
	 * @param rulesThatWillBeApplied
	 * @param rulesThatWillBeDetached
	 */
	public ApplyAndDetachColumnRulesSession(TRId trId, ColumnData column,
			ArrayList<RuleDescriptionData> rulesThatWillBeApplied,
			ArrayList<RuleDescriptionData> rulesThatWillBeDetached) {
		super();
		this.trId = trId;
		this.column = column;
		this.rulesThatWillBeApplied = rulesThatWillBeApplied;
		this.rulesThatWillBeDetached = rulesThatWillBeDetached;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ColumnData getColumn() {
		return column;
	}

	public void setColumn(ColumnData column) {
		this.column = column;
	}

	public ArrayList<RuleDescriptionData> getRulesThatWillBeApplied() {
		return rulesThatWillBeApplied;
	}

	public void setRulesThatWillBeApplied(
			ArrayList<RuleDescriptionData> rulesThatWillBeApplied) {
		this.rulesThatWillBeApplied = rulesThatWillBeApplied;
	}

	public ArrayList<RuleDescriptionData> getRulesThatWillBeDetached() {
		return rulesThatWillBeDetached;
	}

	public void setRulesThatWillBeDetached(
			ArrayList<RuleDescriptionData> rulesThatWillBeDetached) {
		this.rulesThatWillBeDetached = rulesThatWillBeDetached;
	}

	@Override
	public String toString() {
		return "ApplyAndDetachColumnRulesSession [trId=" + trId + ", column="
				+ column + ", rulesThatWillBeApplied=" + rulesThatWillBeApplied
				+ ", rulesThatWillBeDetached=" + rulesThatWillBeDetached + "]";
	}

	

}
