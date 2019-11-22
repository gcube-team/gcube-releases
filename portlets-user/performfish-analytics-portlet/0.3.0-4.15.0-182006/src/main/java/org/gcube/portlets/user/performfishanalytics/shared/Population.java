/**
 *
 */

package org.gcube.portlets.user.performfishanalytics.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import org.eclipse.persistence.annotations.CascadeOnDelete;


/**
 * The Class Population.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 17, 2019
 */
@Entity
@CascadeOnDelete
public class Population implements GenericDao, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1919520988595801648L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId; // PRIMARY KEY
	private String id;
	private String name;
	private String level;
	@Lob
	private String description;

	@OneToMany(mappedBy = "population", orphanRemoval = true, cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	@CascadeOnDelete
	@OrderColumn
	private List<PopulationType> listPopulationType = new ArrayList<PopulationType>();

	/**
	 * Instantiates a new population.
	 */
	public Population() {

	}

	/**
	 * Instantiates a new population.
	 *
	 * @param id
	 *            the id
	 * @param name
	 *            the name
	 * @param level
	 *            the level
	 * @param description
	 *            the description
	 * @param listPopulationType
	 *            the list population type
	 */
	public Population(
		String id, String name, String level, String description,
		ArrayList<PopulationType> listPopulationType) {

		super();
		this.id = id;
		this.name = name;
		this.level = level;
		this.description = description;
		this.listPopulationType = listPopulationType;
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
	 * Gets the level.
	 *
	 * @return the level
	 */
	public String getLevel() {

		return level;
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
	 * Gets the list population type.
	 *
	 * @return the listPopulationType
	 */
	public List<PopulationType> getListPopulationType() {

		return listPopulationType;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {

		this.id = id;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * Sets the level.
	 *
	 * @param level
	 *            the level to set
	 */
	public void setLevel(String level) {

		this.level = level;
	}

	/**
	 * Sets the description.
	 *
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {

		this.description = description;
	}

	/**
	 * Sets the list population type.
	 *
	 * @param listPopulationType
	 *            the listPopulationType to set
	 */
	public void setListPopulationType(List<PopulationType> listPopulationType) {

		this.listPopulationType = listPopulationType;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("Population [internalId=");
		builder.append(internalId);
		builder.append(", id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", level=");
		builder.append(level);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}
}
