package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */

@Entity
public class DataSource implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int internalId;
	
	private String id;
	private String name;
	private String description;
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param description
	 */
	public DataSource(String id, String name, String description) {
		setId(id);
		setName(name);
		setDescription(description);
	}

	//Used in Data Source advanced option to create the check list
	public DataSource(String id, String name){
		setId(id);
		setName(name);
	}
	
	public DataSource() {}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataSource [internalId=");
		builder.append(internalId);
		builder.append(", id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}

}
