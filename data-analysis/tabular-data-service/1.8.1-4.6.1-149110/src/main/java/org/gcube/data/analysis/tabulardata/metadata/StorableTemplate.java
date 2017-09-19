package org.gcube.data.analysis.tabulardata.metadata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;

@Entity
@Table
@NamedQueries({
	@NamedQuery(name="Template.getAll", query="SELECT DISTINCT str FROM StorableTemplate str LEFT JOIN str.sharedWith s WHERE  "
			+ "((CONCAT('u(',:user,')') = s) or (CONCAT('g(',:group,')') = s) or str.owner = :user) and :scope MEMBER OF str.scopes " +
			"ORDER BY str.creationDate DESC"),
	@NamedQuery(name="Template.getById",query="SELECT DISTINCT str FROM StorableTemplate str LEFT JOIN str.sharedWith s WHERE  "
			+ "str.id = :id and ((CONCAT('u(',:user,')') = s) or (CONCAT('g(',:group,')') = s) or str.owner = :user) and :scope MEMBER OF str.scopes")
})
public class StorableTemplate implements Identifiable {
	
	
	@SuppressWarnings("unused")
	private StorableTemplate(){}
	
	public StorableTemplate(String name, String description, String agency, String owner, String scope, Template template){
		this.name = name;
		this.description = description;
		this.agency = agency;
		this.owner = owner;
		this.template = template;
		this.scopes.add(scope);
	}
	
	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable=false)
	private String name;
	@Column(nullable=false)
	private String description;
	
	@Column
	private String agency;
	
	@Column(nullable=false)
	private String owner;
	
	@Column(nullable=false)
	@Temporal(value=TemporalType.TIMESTAMP)
	private Calendar creationDate = Calendar.getInstance();
	
	@ElementCollection(targetClass=String.class,  fetch = FetchType.EAGER)
	private List<String> sharedWith = new ArrayList<String>();
	
	@ElementCollection 
	private List<String> scopes = new ArrayList<String>();;
		
	private Template template;
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the agency
	 */
	public String getAgency() {
		return agency;
	}

	/**
	 * @param agency the agency to set
	 */
	public void setAgency(String agency) {
		this.agency = agency;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the creationDate
	 */
	public Calendar getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the sharedWith
	 */
	public List<String> getSharedWith() {
		return sharedWith;
	}

	/**
	 * @param sharedWith the sharedWith to set
	 */
	public void setSharedWith(List<String> sharedWith) {
		this.sharedWith = sharedWith;
	}

	/**
	 * @return the scopes
	 */
	public List<String> getScopes() {
		return scopes;
	}

	/**
	 * @param scopes the scopes to set
	 */
	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}

	/**
	 * @return the template
	 */
	public Template getTemplate() {
		return template;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(Template template) {
		this.template = template;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

}
