package org.gcube.data.analysis.tabulardata.commons.webservice.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.data.analysis.tabulardata.commons.utils.EntityList;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.adapters.MapAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TabularResource {
	
	private long id;
				
	private List<String> sharedWithUser = new ArrayList<String>();
	private List<String> sharedWithGroup = new ArrayList<String>();
	
	@XmlJavaTypeAdapter(MapAdapter.class)
	private Map<String, Serializable> properties = new HashMap<String, Serializable>();
	
	private Calendar creationDate;
	
	private String name;
	
	private String owner;
	
	private String tableType;
	
	private TabularResourceType tabularResourceType;
	
	private boolean finalized;
	
	private boolean locked;
	
	private boolean valid = true;
	
	private Long newVersionId = null;
	
	private List<HistoryData> history =  new ArrayList<HistoryData>();
	
	@SuppressWarnings("unused")
	private TabularResource(){}
	
	public TabularResource(long id, TabularResourceType tabularResourceType, String name, String owner, Calendar creationDate, String tableType, boolean locked) {
		this.id = id;
		this.tableType = tableType;
		this.locked = locked;
		this.tabularResourceType = tabularResourceType;
		this.name = name;
		this.owner = owner;
		this.creationDate = creationDate;
		this.locked = locked;
	}
	
	public TabularResource(long id, TabularResourceType tabularResourceType,  String name, String owner, Calendar creationDate, String tableType, List<String> sharedWith,
			Map<String, Serializable> properties, List<HistoryData> history, boolean valid, boolean locked) {
		this(id, tabularResourceType, name, owner, creationDate, tableType, locked);
		this.sharedWithUser = EntityList.getUserList(sharedWith);
		this.sharedWithGroup = EntityList.getGroupList(sharedWith);
		this.properties = properties;
		this.history = history;
		this.valid = valid;
		
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
	 * @return the properties
	 */
	public Map<String, Serializable> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Map<String,Serializable> properties) {
		this.properties = properties;
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

	public Calendar getCreationDate() {
		return creationDate;
	}

	/**
	 * @return the history
	 */
	public List<HistoryData> getHistory() {
		return history;
	}

	/**
	 * @param history the history to set
	 */
	public void setHistory(List<HistoryData> history) {
		this.history = history;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getTableType() {
		return tableType;
	}
	
	public TabularResourceType getTabularResourceType() {
		return tabularResourceType;
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
		
	public boolean isFinalized(){
		return finalized;
	}
	
	public void finalize(boolean finalize){
		this.finalized = finalize;
	}
	
	public boolean isLocked() {
		return locked;
	}

	public Long getNewVersionId() {
		return newVersionId;
	}

	public void setNewVersionId(Long newVersionId) {
		this.newVersionId = newVersionId;
	}

	@Override
	public String toString() {
		return "TabularResource [id=" + id + ", sharedWithUser="
				+ sharedWithUser + ", sharedWithGroup=" + sharedWithGroup
				+ ", properties=" + properties + ", creationDate="
				+ creationDate + ", name=" + name + ", owner=" + owner
				+ ", tableType=" + tableType + ", tabularResourceType=" + tabularResourceType +", finalized=" + finalized + ", valid="
				+ valid + "]";
	}
	
}
