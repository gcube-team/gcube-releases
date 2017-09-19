package org.gcube.data.analysis.tabulardata.metadata.tabularresource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.metadata.Identifiable;
import org.gcube.data.analysis.tabulardata.metadata.StorableHistoryStep;
import org.gcube.data.analysis.tabulardata.metadata.notification.StorableNotification;
import org.gcube.data.analysis.tabulardata.metadata.resources.StorableResource;
import org.gcube.data.analysis.tabulardata.metadata.task.StorableTask;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;



@NamedQueries({
	@NamedQuery(name="TR.getAll", query="SELECT DISTINCT str FROM StorableTabularResource str LEFT JOIN str.sharedWith s WHERE  "
			+ "((CONCAT('u(',:user,')') = s) or (CONCAT('g(',:group,')') = s) or str.owner = :user) and :scope MEMBER OF str.scopes and str.hidden=false and str.deleted=false ORDER BY str.creationDate DESC"),

			@NamedQuery(name="TR.getAllByType", query="SELECT DISTINCT str FROM StorableTabularResource str LEFT JOIN str.sharedWith s WHERE  "
					+ "((CONCAT('u(',:user,')') = s) or (CONCAT('g(',:group,')') = s) or str.owner = :user) and :scope MEMBER OF str.scopes and str.tableType =:type and str.hidden=false and str.deleted=false ORDER BY str.creationDate DESC"),

					@NamedQuery(name="TR.getById",query="SELECT DISTINCT str FROM StorableTabularResource str LEFT JOIN str.sharedWith s WHERE  "
							+ "str.id = :id and ((CONCAT('u(',:user,')') = s) or (CONCAT('g(',:group,')') = s) or str.owner = :user) and :scope MEMBER OF str.scopes and str.hidden=false and str.deleted=false"),
							@NamedQuery(name="TR.getByIdWithoutAuth",query="SELECT DISTINCT str FROM StorableTabularResource str WHERE  "
									+ "str.id = :id and str.hidden=false and str.deleted=false"),
									@NamedQuery(name="TR.getAllWithoutAuth",query="SELECT DISTINCT str FROM StorableTabularResource str WHERE  "
											+ "str.hidden=false and str.deleted=false")

})
@Entity
public class StorableTabularResource implements Serializable, Identifiable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String GENERIC_TYPE_NAME= new GenericTableType().getName();

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;

	@Column
	private Long tableId = null;

	@Column
	private boolean hidden= false;
	
	@Column
	private boolean deleted= false;

	@Column
	private boolean locked = false;

	@Column
	private TabularResourceType tabularResourceType = TabularResourceType.STANDARD;
	
	
	@Column
	private String tableType = GENERIC_TYPE_NAME;

	@Column(nullable=false)
	@Temporal(value=TemporalType.TIMESTAMP)
	private Calendar creationDate = Calendar.getInstance();

	@Column(nullable=false)
	private String owner;

	@OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	private List<RuleMapping> rules = new ArrayList<RuleMapping>();

	@Column
	private boolean finalized= false;

	@Column
	private String name;

	@Column
	private String tabularResourceVersion = "0.0.1-0";

	@Column
	private boolean valid = true;

	@Column(nullable=false)
	@ElementCollection(targetClass=String.class,  fetch = FetchType.EAGER)
	private List<String> sharedWith = new ArrayList<String>(); 

	@Lob 
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(joinColumns=@JoinColumn(name="prop_id"))
	private Map<String, Serializable> properties = new HashMap<String, Serializable>();

	@ManyToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	@OrderBy("date ASC")
	private List<StorableHistoryStep> historySteps = new ArrayList<StorableHistoryStep>();

	@OneToMany(cascade = CascadeType.ALL)
	@OrderBy("startTime DESC")
	private List<StorableTask> tasks = new ArrayList<StorableTask>();

	@ElementCollection(targetClass=String.class)
	private List<String> scopes = new ArrayList<String>();

	@OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY, mappedBy="linkedTabularResource")
	private List<RelationLink> linkedBy = new ArrayList<RelationLink>();	

	@OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY, mappedBy="linksTotabularResource")
	private List<RelationLink> linksTo = new ArrayList<RelationLink>();	

	@OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	private List<EditEntry> editEntries = new ArrayList<EditEntry>();

	@OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	private List<StorableNotification> notifications = new ArrayList<StorableNotification>();

	@OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	private List<StorableResource> resources = new ArrayList<StorableResource>();

	@OneToOne(cascade={CascadeType.MERGE, CascadeType.PERSIST})
	private StorableTabularResource newVersion = null;

	@OneToOne(mappedBy="newVersion", cascade={CascadeType.MERGE, CascadeType.PERSIST})
	private StorableTabularResource oldVersion = null;

	
	
	public StorableTabularResource(){};

	public StorableTabularResource(TabularResourceType tabularResourceType, String owner, String name,
			List<StorableHistoryStep> historySteps, List<StorableTask> tasks,
			List<String> scopes) {
		super();
		this.name = name;
		this.owner = owner;
		this.historySteps = historySteps;
		this.tasks = tasks;
		this.scopes = scopes;
		this.tabularResourceType = tabularResourceType;
	}

	public StorableTabularResource(TabularResourceType tabularResourceType,String owner, String name,String 
			scope) {
		super();
		this.name = name;
		this.owner = owner;
		this.scopes.add(scope);
		this.tabularResourceType = tabularResourceType;
	}

	public long getId() {
		return id;
	}

	public List<StorableHistoryStep> getHistorySteps() {
		return Collections.unmodifiableList(historySteps);
	}

	public void removeHistoryStep(StorableHistoryStep step){
		this.historySteps.remove(step);
	}

		
	public void addHistorySteps(List<StorableHistoryStep> steps){
		this.historySteps.addAll(steps);
	}

	public void addHistoryStep(StorableHistoryStep step){
		this.historySteps.add(step);
	}
	
	public void setHistorySteps(LinkedList<StorableHistoryStep> historySteps) {
		this.historySteps = historySteps;
	}

	public List<StorableTask> getTasks() {
		return Collections.unmodifiableList(tasks);
	}

	public void addTask(StorableTask task) {
		this.tasks.add(task);
	}

	public void setTasks(List<StorableTask> tasks) {
		this.tasks = tasks;
	}

	public boolean isLocked() {
		return locked;
	}

	public void lock() {
		this.locked = true;
	}

	public void unlock(){
		this.locked = false;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}

	public List<String> getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(List<String> sharedWith) {
		this.sharedWith = sharedWith;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public TabularResourceType getTabularResourceType() {
		return tabularResourceType;
	}

	public Map<String, Serializable> getProperties() {
		return properties;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setProperties(Map<String, Serializable> properties) {
		this.properties = properties;
	}

	public void addRule(RuleMapping rule) {
		this.rules.add(rule);		
	}

	public void addRules(List<RuleMapping> rules) {
		this.rules.addAll(rules);		
	}

	public StorableTabularResource getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(StorableTabularResource newVersion) {
		this.newVersion = newVersion;
	}

	public StorableTabularResource getOldVersion() {
		return oldVersion;
	}

	public List<StorableNotification> getNotifications() {
		return notifications;
	}

	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * @param hidden the hidden to set
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}


	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return the editEntries
	 */
	public List<EditEntry> getEditEntries() {
		return Collections.unmodifiableList(editEntries);
	}

	/**
	 * @param editEntries the editEntries to set
	 */
	public void addEditEntries(EditEntry editEntry) {
		this.editEntries.add(editEntry);
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return tabularResourceVersion;
	}

	public RuleMapping removeColumnRuleMapping(long id, String columnLocalId) {
		int index = -1;
		for (int i=0; i<rules.size(); i++)
			if (rules.get(i).getStorableRule().getId()==id && rules.get(i).getColumnLocalId().equals(columnLocalId)){
				index =i;
				break;
			}
		if (index!=-1)
			return rules.remove(index);
		else return null;
	}

	public RuleMapping removeTableRuleMapping(long id) {
		int index = -1;
		for (int i=0; i<rules.size(); i++)
			if (rules.get(i).getStorableRule().getId()==id){
				index =i;
				break;
			}
		if (index!=-1)
			return rules.remove(index);
		else return null;
	}
	
	public void removeAllRules() {
		rules = new ArrayList<RuleMapping>();		
	}

	public List<RuleMapping> getRules(){
		return rules;
	}

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public List<RelationLink> getLinkedBy() {
		return linkedBy;
	}

	public void setLinkedBy(List<RelationLink> linkedBy) {
		this.linkedBy = linkedBy;
	}

	public List<RelationLink> getLinksTo() {
		return linksTo;
	}

	public void setLinksTo(List<RelationLink> linksTo) {
		this.linksTo = linksTo;
	}

	/**
	 * @return the type
	 */
	public String getTableType() {
		return tableType;
	}

	/**
	 * @param type the type to set
	 */
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public void addResource(StorableResource resource){
		this.resources.add(resource);
	}

	public void removeResource(StorableResource resource){
		this.resources.remove(resource);
	}
	
	public List<StorableResource> getResources() {
		return resources;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean isFinalized() {
		return finalized;
	}

	public void finalize(boolean finalize) {
		this.finalized = finalize;
	}

	
	
	public void setTabularResourceType(TabularResourceType tabularResourceType) {
		this.tabularResourceType = tabularResourceType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StorableTabularResource [id=" + id + ", owner = "+owner+", locked=" + locked
				+", historySteps=" + historySteps
				+ ", scopes=" + scopes + ", sharedWith= "+sharedWith+" ]";
	}
}
