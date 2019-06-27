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
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jan 17, 2019
 */
@Entity
@CascadeOnDelete
public class Species implements GenericDao, ReferencePopulationType, PopulationTypeProperties, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5501396332502088292L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId; // PRIMARY KEY
	private String id;
	private String name;
	private String description;

	@CascadeOnDelete
	private PopulationType populationType;

	/**
	 * Instantiates a new species.
	 */
	public Species() {

		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new species.
	 *
	 * @param id the id
	 * @param name the name
	 * @param description the description
	 * @param populationType the population type
	 */
	public Species(
		String id, String name, String description,
		PopulationType populationType) {

		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.populationType = populationType;
	}



	/**
	 * @return the internalId
	 */
	public int getInternalId() {

		return internalId;
	}

	/**
	 * @return the id
	 */
	public String getId() {

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
	 * @return the populationType
	 */
	public PopulationType getPopulationType() {

		return populationType;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {

		this.id = id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {

		this.description = description;
	}

	/**
	 * @param populationType
	 *            the populationType to set
	 */
	public void setPopulationType(PopulationType populationType) {

		this.populationType = populationType;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("Species [internalId=");
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
