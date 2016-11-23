package org.gcube.portlets.user.statisticalmanager.client.bean;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;

import com.extjs.gxt.ui.client.data.BeanModel;


public class ResourceItem extends BeanModel {

	private static final long serialVersionUID = 2814548224382024267L;
	private List<String> columnNames = new ArrayList<String>();
	
	// redundancy for correct gwt serialization
	private Provenance provenance;
	private Type type;
	
	public enum Provenance {
		IMPORTED,
		COMPUTED,
		SYSTEM
	};
	
	public enum Type {
		TABLE,
		FILE
	}
	
	public ResourceItem() {
		super();
	}
	

	public ResourceItem(Type type, String id, String name, String description, String template, Provenance provenance, Date creationDate, String operatorId, String url) {
		this.set("type", type);
		this.set("id", id);
		this.set("name", name);
		this.set("description", description);
		this.set("template", template);
		this.set("provenance", provenance);
		this.set("creationDate", creationDate);
		this.set("operatorId", operatorId);
		this.set("url", url);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return get("name");
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.set("name", name);
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return get("description");
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.set("description", description);
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return get("type");
	}
	
	/**
	 * @param type the type to set
	 */
	public void setTemplate(String template) {
		this.set("template", template);
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return get("id");
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {		
		this.set("id", id);
	}

	/**
	 * @param columnNames
	 */
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}
	
	/**
	 * @return the columnNames
	 */
	public List<String> getColumnNames() {
		return columnNames;
	}

	/**
	 * @param columnName
	 */
	public void addColumnName(String columnName) {
		this.columnNames.add(columnName);
	}

	public Operator getOperator() {
		if (get("operator")==null) {
			OperatorsClassification classification = StatisticalManager.getDefaultOperatorsClassification();
			if (classification!=null)
				set("operator", classification.getOperatorById(getOperatorId()));
		}
		return get("operator");
	}
	
	public String getOperatorId() {
		return get("operatorId");
	}
	
	public Provenance getProvenance() {
		return get("provenance");
	}
	
	public boolean isFile() {
		return (get("type")==Type.FILE);
	}
	
	public boolean isTable() {
		return (get("type")==Type.TABLE);
	}
	
	public String getTemplate() {
		return get("template");
	}

	public String getUrl() {
		return get("url");
	}
}
