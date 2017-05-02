package org.gcube.portlets.user.td.gwtservice.shared.share;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ShareRule implements Serializable {
	private static final long serialVersionUID = -8823413380379929739L;
	private RuleDescriptionData ruleDescriptionData;
	private ArrayList<Contacts> contacts;

	public ShareRule() {
		super();
	}

	public ShareRule(RuleDescriptionData ruleDescriptionData,
			ArrayList<Contacts> contacts) {
		super();
		this.ruleDescriptionData = ruleDescriptionData;
		this.contacts = contacts;
	}

	public RuleDescriptionData getRuleDescriptionData() {
		return ruleDescriptionData;
	}

	public void setRuleDescriptionData(RuleDescriptionData ruleDescriptionData) {
		this.ruleDescriptionData = ruleDescriptionData;
	}

	public ArrayList<Contacts> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<Contacts> contacts) {
		this.contacts = contacts;
	}

	@Override
	public String toString() {
		return "ShareRule [ruleDescriptionData=" + ruleDescriptionData
				+ ", contacts=" + contacts + "]";
	}

}
