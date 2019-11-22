package org.gcube.portlets.user.performfishanalytics.shared;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.eclipse.persistence.annotations.CascadeOnDelete;

/**
 * The Class Year.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * May 6, 2019
 */
@Entity
@CascadeOnDelete
public class Year implements GenericDao, ReferencePopulationType, PopulationTypeProperties, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId; // PRIMARY KEY
	private String id;
	private String value;
	
	@CascadeOnDelete
	private PopulationType populationType;
	
	
	/**
	 * Instantiates a new year.
	 */
	public Year() {
	}


	/**
	 * Instantiates a new year.
	 *
	 * @param id the id
	 * @param value the value
	 * @param populationType the population type
	 */
	public Year(String id, String value, PopulationType populationType) {
		super();
		this.id = id;
		this.value = value;
		this.populationType = populationType;
	}
	
	@Override
	public int getInternalId() {
		return internalId;
	}
	
	@Override
	public String getName() {
		return value;
	}
	
	@Override
	public String getDescription() {
		return value;
	}
	
	@Override
	public void setName(String name) {
		setValue(name);
	}


	@Override
	public void setDescription(String description) {
		//empty
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portles.user.performfishannual.shared.GenericDao#getId()
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
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	


	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.shared.ReferencePopulationType#getPopulationType()
	 */
	public PopulationType getPopulationType() {
		return populationType;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.shared.ReferencePopulationType#setPopulationType(org.gcube.portlets.user.performfishanalytics.shared.PopulationType)
	 */
	@Override
	public void setPopulationType(PopulationType populationType) {
		this.populationType = populationType;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Year [internalId=");
		builder.append(internalId);
		builder.append(", id=");
		builder.append(id);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
}
