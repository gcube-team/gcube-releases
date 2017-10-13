package org.gcube.portlets.user.td.gwtservice.shared.rule;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class DetachTableRulesSession implements Serializable {

	private static final long serialVersionUID = -7746819321348425711L;
	private TRId trId;
	private ArrayList<RuleDescriptionData> rules;

	public DetachTableRulesSession() {
		super();
	}

	public DetachTableRulesSession(TRId trId,
			ArrayList<RuleDescriptionData> rules) {
		super();
		this.trId = trId;
		this.rules = rules;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ArrayList<RuleDescriptionData> getRules() {
		return rules;
	}

	public void setRules(ArrayList<RuleDescriptionData> rules) {
		this.rules = rules;
	}

	@Override
	public String toString() {
		return "DetachTableRulesSession [trId=" + trId + ", rules=" + rules
				+ "]";
	}

}
