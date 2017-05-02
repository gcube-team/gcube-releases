package org.gcube.portlets.widgets.ckandatapublisherwidget.shared;

import java.io.Serializable;

/**
 * The Class MetadataTypeWrapper
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class MetaDataTypeWrapper implements Serializable{

	private static final long serialVersionUID = 2609935614105035447L;
	private String id;
	private String name;
	private String description;

	/**
	 * Instantiates a new metadata type.
	 */
	public MetaDataTypeWrapper() {

	}

	/**
	 * Instantiates a new metadata type.
	 *
	 * @param id the id
	 * @param name the name
	 * @param description the description
	 */
	public MetaDataTypeWrapper(String id, String name, String description) {

		super();
		this.id = id;
		this.name = name;
		this.description = description;
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {

		return name;
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
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(String id) {

		this.id = id;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description) {

		this.description = description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MetaDataTypeWrapper [id=" + id + ", name=" + name
				+ ", description=" + description + "]";
	}

}
