/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean;

import java.io.Serializable;



/**
 * The Class MetadataProfile.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 19, 2017
 */
public class MetadataProfile implements Serializable{


	/**
	 *
	 */
	private static final long serialVersionUID = 8829173012445264057L;
	private String id;
	private String name;
	private String description;
	private String metadataType;

	/**
	 * Instantiates a new metadata type.
	 */
	public MetadataProfile() {

	}

	/**
	 * Instantiates a new metadata type.
	 *
	 * @param id the id
	 * @param name the name
	 * @param description the description
	 * @param metadataType the metadata type
	 */
	public MetadataProfile(String id, String name, String description, String metadataType) {

		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.metadataType = metadataType;
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

	/**
	 * Gets the metadata type.
	 *
	 * @return the metadataType
	 */
	public String getMetadataType() {

		return metadataType;
	}


	/**
	 * Sets the metadata type.
	 *
	 * @param metadataType the metadataType to set
	 */
	public void setMetadataType(String metadataType) {

		this.metadataType = metadataType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("MetadataProfile [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", metadataType=");
		builder.append(metadataType);
		builder.append("]");
		return builder.toString();
	}


}
