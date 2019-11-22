package org.gcube.data.analysis.tabulardata.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gcube.data.analysis.tabulardata.commons.rules.Rule;
import org.gcube.data.analysis.tabulardata.commons.rules.RuleScope;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleType;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.RuleMapping;

@NamedQueries({
	@NamedQuery(name="RULE.getAll", query="SELECT DISTINCT str FROM StorableRule str LEFT JOIN str.sharedWith s WHERE  "
			+ "((CONCAT('u(',:user,')') = s) or (CONCAT('g(',:group,')') = s) or str.owner = :user) and NOT(str.deleted) and :scope MEMBER OF str.scopes " +
			" ORDER BY str.creationDate DESC"),
	@NamedQuery(name="RULE.getAllByScope", query="SELECT DISTINCT str FROM StorableRule str LEFT JOIN str.sharedWith s WHERE  "
			+ "((CONCAT('u(',:user,')') = s) or (CONCAT('g(',:group,')') = s) or str.owner = :user) and NOT(str.deleted) and :scope MEMBER OF str.scopes and str.ruleScope =:ruleScope " +
			" ORDER BY str.creationDate DESC"),
	@NamedQuery(name="RULE.getById",query="SELECT DISTINCT str FROM StorableRule str LEFT JOIN str.sharedWith s WHERE  "
			+ "str.id = :id and ((CONCAT('u(',:user,')') = s) or (CONCAT('g(',:group,')') = s) or str.owner = :user) and :scope MEMBER OF str.scopes")
})
@Entity
public class StorableRule implements Identifiable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5452438822480495963L;

	public StorableRule(){}
	
	public StorableRule(String name, String description, Rule rule,  String owner, RuleType ruleType) {
		super();
		this.name = name;
		this.description = description;
		this.owner = owner;
		this.rule = rule;
		this.ruleScope = rule.getScope();
		this.ruleType = ruleType;
	}

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column
	private String name;
	
	@Column
	private String description;
	
	private boolean deleted= false;
	
	@Column(nullable=false)
	@Temporal(value=TemporalType.TIMESTAMP)
	private Calendar creationDate = Calendar.getInstance();
	
	@Column
	private String owner;
	
	private Rule rule;
		
	private RuleType ruleType;
	
	@OneToMany(cascade={CascadeType.ALL})
	private List<RuleMapping> ruleMappings = new ArrayList<>();
	
	@Column
	private RuleScope ruleScope;
		
	@Column(nullable=false)
	@ElementCollection(targetClass=String.class,  fetch = FetchType.EAGER)
	private List<String> sharedWith = new ArrayList<String>();

	@ElementCollection(targetClass=String.class)
	private List<String> scopes = new ArrayList<String>();
	
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
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @return the rule
	 */
	public Rule getRule() {
		return rule;
	}

	/**
	 * @return the ruleScope
	 */
	public RuleScope getRuleScope() {
		return ruleScope;
	}
		
	/**
	 * @return the ruleColumnType
	 */
	public RuleType getRuleType() {
		return ruleType;
	}

	/**
	 * @return the scopes
	 */
	public List<String> getScopes() {
		return scopes;
	}

	/**
	 * @return the sharedWith
	 */
	public List<String> getSharedWith() {
		return sharedWith;
	} 
	
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public Calendar getCreationDate() {
		return creationDate;
	}

	/**
	 * @param rule the rule to set
	 */
	public void setRule(Rule rule) {
		this.rule = rule;
	}

	/**
	 * @param ruleScope the ruleScope to set
	 */
	public void setRuleScope(RuleScope ruleScope) {
		this.ruleScope = ruleScope;
	}

	/**
	 * @param sharedWith the sharedWith to set
	 */
	public void addSharedWith(String entityRepresentation) {
		this.sharedWith.add(entityRepresentation);
	}

	public void removeSharedWith(String entityRepresentation) {
		this.sharedWith.remove(entityRepresentation);
	}
	
	/**
	 * @param scopes the scopes to set
	 */
	public void addScope(String scope) {
		this.scopes.add(scope);
	}
	
	public void removeScope(String scope){
		this.scopes.remove(scope);
	}

	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public List<RuleMapping> getRuleMappings() {
		return ruleMappings;
	}
	
}
