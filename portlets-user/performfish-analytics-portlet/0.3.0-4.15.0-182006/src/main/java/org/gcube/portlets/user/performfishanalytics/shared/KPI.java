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
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.CascadeOnDelete;


/**
 * The Class KPI.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 11, 2019
 */
@Entity
@CascadeOnDelete
public class KPI implements GenericDao, Comparable<KPI>, ReferencePopulationType, PopulationTypeProperties, Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -7903059379223156950L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId; //PRIMARY KEY

	private String id;
	private String code;
	private String name;

	private String description;

	//THESE ARE THE DESCENDANT
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	@OrderColumn
	private List<KPI> listKPI = new ArrayList<KPI>();

	private int deepIndex = -1;

	//HERE STORE ONLY THE DIRECT CHILD OF POPULATION_TYPE, THAT IS THE FIRST LEVEL OF KPIs
	@CascadeOnDelete
	private PopulationType populationType;

	@Transient
	private boolean isLeaf = false;


	/**
	 * Instantiates a new kpi.
	 */
	public KPI() {
	}

	/**
	 * Instantiates a new kpi.
	 *
	 * @param id the id
	 * @param code the code
	 * @param name the name
	 * @param description the description
	 * @param listKPI the list kpi
	 * @param populationType the population type
	 * @param deepIndex the deep index
	 */
	public KPI(
		String id, String code, String name, String description,
		ArrayList<KPI> listKPI, PopulationType populationType, int deepIndex) {

		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.description = description;
		this.listKPI = listKPI;
		this.populationType = populationType;
		this.deepIndex = deepIndex;
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
	 * @return the code
	 */
	public String getCode() {

		return code;
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
	 * @return the listKPI
	 */
	public List<KPI> getListKPI() {

		return listKPI;
	}


	/**
	 * @return the deepIndex
	 */
	public int getDeepIndex() {

		return deepIndex;
	}


	/**
	 * @return the populationType
	 */
	public PopulationType getPopulationType() {

		return populationType;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {

		this.id = id;
	}


	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {

		this.code = code;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {

		this.description = description;
	}


	/**
	 * @param listKPI the listKPI to set
	 */
	public void setListKPI(List<KPI> listKPI) {

		this.listKPI = listKPI;
	}


	/**
	 * @param deepIndex the deepIndex to set
	 */
	public void setDeepIndex(int deepIndex) {

		this.deepIndex = deepIndex;
	}


	/**
	 * @param populationType the populationType to set
	 */
	public void setPopulationType(PopulationType populationType) {

		this.populationType = populationType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(KPI o) {
		return deepIndex - o.getDeepIndex();
	}

	/**
	 * @return the isLeaf
	 */
	public boolean isLeaf() {

		return isLeaf;
	}


	/**
	 * @param isLeaf the isLeaf to set
	 */
	public void setLeaf(boolean isLeaf) {

		this.isLeaf = isLeaf;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("KPI [internalId=");
		builder.append(internalId);
		builder.append(", id=");
		builder.append(id);
		builder.append(", code=");
		builder.append(code);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", deepIndex=");
		builder.append(deepIndex);
		builder.append(", isLeaf=");
		builder.append(isLeaf);
		builder.append("]");
		return builder.toString();
	}
}


