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
 * The Class PopulationType.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jan 11, 2019
 */
@Entity
@CascadeOnDelete
public class PopulationType implements GenericDao, Serializable {
	
	/**
	 *
	 */
	private static final long serialVersionUID = -1053734960180011850L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId; // PRIMARY KEY
	private String id;
	private String name;
	private String type;
	@Lob
	private String description;
	@CascadeOnDelete
	private Population population;

	@OneToMany(mappedBy = "populationType", orphanRemoval = true, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@CascadeOnDelete
	@OrderColumn
	private List<Species> listSpecies = new ArrayList<Species>();

	@OneToMany(mappedBy = "populationType", orphanRemoval = true, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@CascadeOnDelete
	@OrderColumn
	private List<Quarter> listQuarter = new ArrayList<Quarter>();

	@OneToMany(mappedBy = "populationType", orphanRemoval = true, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@CascadeOnDelete
	@OrderColumn
	private List<Area> listArea = new ArrayList<Area>();

	@OneToMany(mappedBy = "populationType", orphanRemoval = true, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@CascadeOnDelete
	@OrderColumn
	private List<Period> listPeriod = new ArrayList<Period>();

	@OneToMany(mappedBy = "populationType", orphanRemoval = true, cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	@CascadeOnDelete
	@OrderColumn
	private List<KPI> listKPI = new ArrayList<KPI>();
	
	@OneToMany(mappedBy = "populationType", orphanRemoval = true, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@CascadeOnDelete
	@OrderColumn
	private List<Year> listYears = new ArrayList<Year>();

	/**
	 * Instantiates a new population type.
	 */
	public PopulationType() {

	}

	/**
	 * Instantiates a new population type.
	 *
	 * @param id
	 *            the id
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param description
	 *            the description
	 * @param population
	 *            the population
	 */
	public PopulationType(
		String id, String name, String type, String description,
		Population population) {

		this.id = id;
		this.name = name;
		this.type = type;
		this.description = description;
		this.population = population;
	}

	/**
	 * Instantiates a new population type.
	 *
	 * @param id
	 *            the id
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param description
	 *            the description
	 * @param population
	 *            the population
	 * @param listQuarter
	 *            the list quarter
	 * @param listArea
	 *            the list area
	 * @param listPeriod
	 *            the list period
	 * @param listKPI
	 *            the list kpi
	 */
	public PopulationType(
		String id, String name, String type, String description,
		Population population, ArrayList<Quarter> listQuarter,
		ArrayList<Area> listArea, ArrayList<Period> listPeriod,
		ArrayList<KPI> listKPI) {

		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.description = description;
		this.population = population;
		this.listQuarter = listQuarter;
		this.listArea = listArea;
		this.listPeriod = listPeriod;
		this.listKPI = listKPI;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.gcube.portlets.user.performfishanalytics.shared.dao.GenericDao#getId
	 * ()
	 */
	@Override
	public String getId() {

		return id;
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
	 * Sets the id.
	 *
	 * @param id
	 *            the id to set
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
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {

		return type;
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
	 * Gets the population.
	 *
	 * @return the population
	 */
	public Population getPopulation() {

		return population;
	}

	/**
	 * Gets the list quarter.
	 *
	 * @return the listQuarter
	 */
	public List<Quarter> getListQuarter() {

		return listQuarter;
	}

	/**
	 * Gets the list area.
	 *
	 * @return the listArea
	 */
	public List<Area> getListArea() {

		return listArea;
	}

	/**
	 * Gets the list period.
	 *
	 * @return the listPeriod
	 */
	public List<Period> getListPeriod() {

		return listPeriod;
	}

	/**
	 * Gets the list kpi.
	 *
	 * @return the listKPI
	 */
	public List<KPI> getListKPI() {

		return listKPI;
	}

	/**
	 * Gets the list species.
	 *
	 * @return the listSpecies
	 */
	public List<Species> getListSpecies() {

		return listSpecies;
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
	 * Sets the type.
	 *
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {

		this.type = type;
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
	 * Sets the population.
	 *
	 * @param population
	 *            the population to set
	 */
	public void setPopulation(Population population) {

		this.population = population;
	}

	/**
	 * Sets the list quarter.
	 *
	 * @param listQuarter
	 *            the listQuarter to set
	 */
	public void setListQuarter(List<Quarter> listQuarter) {

		this.listQuarter = listQuarter;
	}

	/**
	 * Sets the list area.
	 *
	 * @param listArea
	 *            the listArea to set
	 */
	public void setListArea(List<Area> listArea) {

		this.listArea = listArea;
	}

	/**
	 * Sets the list period.
	 *
	 * @param listPeriod
	 *            the listPeriod to set
	 */
	public void setListPeriod(List<Period> listPeriod) {

		this.listPeriod = listPeriod;
	}

	/**
	 * Sets the list kpi.
	 *
	 * @param listKPI
	 *            the listKPI to set
	 */
	public void setListKPI(List<KPI> listKPI) {

		this.listKPI = listKPI;
	}

	/**
	 * Sets the list species.
	 *
	 * @param listSpecies
	 *            the listSpecies to set
	 */
	public void setListSpecies(List<Species> listSpecies) {

		this.listSpecies = listSpecies;
	}
	

	/**
	 * Gets the list years.
	 *
	 * @return the list years
	 */
	public List<Year> getListYears() {
		return listYears;
	}

	/**
	 * Sets the list years.
	 *
	 * @param listYears the new list years
	 */
	public void setListYears(List<Year> listYears) {
		this.listYears = listYears;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("PopulationType [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", type=");
		builder.append(type);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}
}
