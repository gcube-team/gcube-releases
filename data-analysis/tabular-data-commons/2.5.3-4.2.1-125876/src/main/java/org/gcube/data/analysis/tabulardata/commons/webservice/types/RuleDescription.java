package org.gcube.data.analysis.tabulardata.commons.webservice.types;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.rules.RuleScope;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleType;
import org.gcube.data.analysis.tabulardata.commons.utils.EntityList;
import org.gcube.data.analysis.tabulardata.expression.Expression;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RuleDescription {

	@XmlElement
	private long id;
	@XmlElement
	private String name;
	@XmlElement
	private String description;
	@XmlElement
	private RuleScope scope;
	
	private Calendar creationDate;
	
	@XmlElement
	private Expression rule;
	
	private List<String> sharedWithUser = new ArrayList<String>();
	private List<String> sharedWithGroup = new ArrayList<String>();
	
	@XmlElement
	private RuleType ruleType; 
	
	@XmlElement
	private String owner;
	
	@SuppressWarnings("unused")
	private RuleDescription(){}
	
	public RuleDescription(long id, String name, String description, Calendar creationDate, Expression rule, RuleScope scope, String owner, RuleType ruleType, List<String> sharedWith) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.creationDate = creationDate;
		this.rule = rule;
		this.scope = scope;
		this.owner = owner;
		this.ruleType = ruleType;
		this.sharedWithUser = EntityList.getUserList(sharedWith);
		this.sharedWithGroup = EntityList.getGroupList(sharedWith);
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the rule
	 */
	public Expression getRule() {
		return rule;
	}
	
	public List<String> getSharedWithUsers() {
		return sharedWithUser;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.tabular.TabularResource#getSharedWithGroup()
	 */
	public List<String> getSharedWithGroups() {
		return sharedWithGroup;
	}
	
	/**
	 * @return the scope
	 */
	public RuleScope getScope() {
		return scope;
	}

	public String getOwner() {
		return owner;
	}

	public RuleType getRuleType() {
		return ruleType;
	}
	
	public Calendar getCreationDate() {
		return creationDate;
	}

	@Override
	public String toString() {
		return "RuleDescription [id=" + id + ", name=" + name
				+ ", description=" + description + ", scope=" + scope
				+ ", rule=" + rule + ", ruleColumnType=" + ruleType
				+ ", owner=" + owner + "]";
	}
	
	
}
