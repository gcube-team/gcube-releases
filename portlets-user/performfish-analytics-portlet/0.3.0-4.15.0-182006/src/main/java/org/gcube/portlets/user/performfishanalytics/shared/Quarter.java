/**
 *
 */

package org.gcube.portlets.user.performfishanalytics.shared;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.eclipse.persistence.annotations.CascadeOnDelete;

/**
 * The Class Quarter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jan 11, 2019
 */
@Entity
@CascadeOnDelete
public class Quarter implements GenericDao, ReferencePopulationType, PopulationTypeProperties, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -4234722040817405025L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId; // PRIMARY KEY
	private String id;
	private String name;
	private String description;

	@CascadeOnDelete
	private PopulationType populationType;

	/**
	 * Instantiates a new quarter.
	 */
	public Quarter() {

		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new quarter.
	 *
	 * @param id
	 *            the id
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @param populationType
	 *            the population type
	 */
	public Quarter(
		String id, String name, String description,
		PopulationType populationType) {

		this.id = id;
		this.name = name;
		this.description = description;
		this.populationType = populationType;
	}

	/**
	 * Gets the internal id.
	 *
	 * @return the internalId
	 */
	public int getInternalId() {

		return internalId;
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
	 * Gets the population type.
	 *
	 * @return the populationType
	 */
	public PopulationType getPopulationType() {

		return populationType;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.shared.RefencePopulationType#setPopulationType(org.gcube.portlets.user.performfishanalytics.shared.PopulationType)
	 */
	@Override
	public void setPopulationType(PopulationType populationType) {

		this.populationType = populationType;

	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.shared.PopulationTypeProperties#setId(java.lang.String)
	 */
	public void setId(String id) {

		this.id = id;
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
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("Quarter [internalId=");
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
