package org.gcube.portlets.user.td.gwtservice.shared.rule;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class DetachColumnRulesSession implements Serializable {

	private static final long serialVersionUID = -7746819321348425711L;
	private TRId trId;
	private ColumnData column;
	private ArrayList<RuleDescriptionData> rules;

	public DetachColumnRulesSession() {
		super();
	}

	public DetachColumnRulesSession(TRId trId, ColumnData column,
			ArrayList<RuleDescriptionData> rules) {
		super();
		this.trId = trId;
		this.column = column;
		this.rules = rules;
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

	public ArrayList<RuleDescriptionData> getRules() {
		return rules;
	}

	public void setRules(ArrayList<RuleDescriptionData> rules) {
		this.rules = rules;
	}

	@Override
	public String toString() {
		return "DetachColumnRulesSession [trId=" + trId + ", column=" + column
				+ ", rules=" + rules + "]";
	}

	

	
	
	
}
