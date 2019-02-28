package org.gcube.portlets.user.td.gwtservice.shared.rule.description;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleScopeType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDRuleType;
import org.gcube.portlets.user.td.gwtservice.shared.share.Contacts;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class RuleDescriptionData implements Serializable {

	private static final long serialVersionUID = 2238695825091799598L;
	private long id;
	private String name;
	private String description;
	private Date creationDate;
	private Contacts owner;
	private ArrayList<Contacts> contacts;
	private RuleScopeType scope;
	private C_Expression expression;
	private TDRuleType tdRuleType;

	public RuleDescriptionData() {
		super();
	}

	public RuleDescriptionData(long id, String name, String description,
			Date creationDate, Contacts owner, ArrayList<Contacts> contacts,
			RuleScopeType scope, C_Expression expression, TDRuleType tdRuleType) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.creationDate = creationDate;
		this.owner = owner;
		this.contacts = contacts;
		this.scope = scope;
		this.expression = expression;
		this.tdRuleType = tdRuleType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Contacts getOwner() {
		return owner;
	}

	public void setOwner(Contacts owner) {
		this.owner = owner;
	}

	public String getOwnerLogin() {
		String login = null;
		if (owner != null) {
			login = owner.getLogin();
		}
		return login;
	}

	public ArrayList<Contacts> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<Contacts> contacts) {
		this.contacts = contacts;
	}

	public ArrayList<String> getContactsAsStringList() {
		ArrayList<String> contactsList = new ArrayList<String>();
		for (Contacts contact : contacts) {
			contactsList.add(contact.getLogin());
		}
		return contactsList;
	}

	public RuleScopeType getScope() {
		return scope;
	}

	public void setScope(RuleScopeType scope) {
		this.scope = scope;
	}

	public String getScopeLabel() {
		if (scope == null) {
			return "";
		} else {
			return scope.getLabel();
		}
	}

	public C_Expression getExpression() {
		return expression;
	}

	public void setExpression(C_Expression expression) {
		this.expression = expression;
	}

	public String getReadableExpression() {
		if (expression != null) {
			return expression.getReadableExpression();
		} else {
			return "";
		}
	}

	public TDRuleType getTdRuleType() {
		return tdRuleType;
	}

	public void setTdRuleType(TDRuleType tdRuleType) {
		this.tdRuleType = tdRuleType;
	}

	public boolean equals(RuleDescriptionData ruleDescriptionData) {
		if (ruleDescriptionData == null) {
			return false;
		} else {
			if (id - ruleDescriptionData.getId() == 0) {
				return true;
			} else {
				return false;
			}
		}

	}

	@Override
	public String toString() {
		return "RuleDescriptionData [id=" + id + ", name=" + name
				+ ", description=" + description + ", creationDate="
				+ creationDate + ", owner=" + owner + ", contacts=" + contacts
				+ ", scope=" + scope + ", expression=" + expression
				+ ", tdRuleType=" + tdRuleType + "]";
	}

}
