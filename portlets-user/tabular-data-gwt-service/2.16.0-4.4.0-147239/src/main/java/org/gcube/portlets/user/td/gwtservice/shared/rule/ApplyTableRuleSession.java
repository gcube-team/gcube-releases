package org.gcube.portlets.user.td.gwtservice.shared.rule;

import java.io.Serializable;
import java.util.HashMap;

import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ApplyTableRuleSession implements Serializable {

	private static final long serialVersionUID = -1922171869337643740L;
	private TRId trId;
	private RuleDescriptionData ruleDescriptionData;
	private HashMap<String, String> placeHolderToColumnMap;

	public ApplyTableRuleSession() {
		super();
	}

	public ApplyTableRuleSession(TRId trId,
			RuleDescriptionData ruleDescriptionData,
			HashMap<String, String> placeHolderToColumnMap) {
		super();
		this.trId = trId;
		this.ruleDescriptionData = ruleDescriptionData;
		this.placeHolderToColumnMap = placeHolderToColumnMap;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public RuleDescriptionData getRuleDescriptionData() {
		return ruleDescriptionData;
	}

	public void setRuleDescriptionData(RuleDescriptionData ruleDescriptionData) {
		this.ruleDescriptionData = ruleDescriptionData;
	}

	public HashMap<String, String> getPlaceHolderToColumnMap() {
		return placeHolderToColumnMap;
	}

	public void setPlaceHolderToColumnMap(
			HashMap<String, String> placeHolderToColumnMap) {
		this.placeHolderToColumnMap = placeHolderToColumnMap;
	}

	@Override
	public String toString() {
		return "ApplyTableRuleSession [trId=" + trId + ", ruleDescriptionData="
				+ ruleDescriptionData + ", placeHolderToColumnMap="
				+ placeHolderToColumnMap + "]";
	}

}
