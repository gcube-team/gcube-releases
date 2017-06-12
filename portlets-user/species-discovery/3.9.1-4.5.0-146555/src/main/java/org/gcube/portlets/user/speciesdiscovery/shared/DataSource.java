package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * The Class DataSource.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 10, 2017
 */
@Entity
public class DataSource implements IsSerializable, Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -7978955895523864898L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int internalId;

	private String id;
	private String name;
	private String description;

	/**
	 * Instantiates a new data source.
	 */
	public DataSource() {}

	/**
	 * Instantiates a new data source.
	 *
	 * @param id the id
	 * @param name the name
	 * @param description the description
	 */
	public DataSource(String id, String name, String description) {
		setId(id);
		setName(name);
		setDescription(description);
	}

	//Used in Data Source advanced option to create the check list
	/**
	 * Instantiates a new data source.
	 *
	 * @param id the id
	 * @param name the name
	 */
	public DataSource(String id, String name){
		setId(id);
		setName(name);
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
