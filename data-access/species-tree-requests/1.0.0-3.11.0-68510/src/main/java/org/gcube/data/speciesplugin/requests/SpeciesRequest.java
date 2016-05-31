/**
 * 
 */
package org.gcube.data.speciesplugin.requests;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SpeciesRequest {

	@XmlElement
	private String id;
	
	@XmlElement
	private String name;
	
	@XmlElement
	private String description;
	
	@XmlElement
	private List<String> scientificNames;
	
	@XmlElement
	private List<String> datasources;
	
	@XmlElement
	private boolean strictMatch = true;
	
	@XmlElement
	private int refreshPeriod;
	
	@XmlElement
	private TimeUnit timeUnit;
	
	public SpeciesRequest() {
	}
	
	public SpeciesRequest(String id) {
		this.id=id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the scientificNames
	 */
	public List<String> getScientificNames() {
		return scientificNames;
	}

	/**
	 * @param scientificNames the scientificNames to set
	 */
	public void setScientificNames(List<String> scientificNames) {
		this.scientificNames = scientificNames;
	}

	/**
	 * @return the datasources
	 */
	public List<String> getDatasources() {
		return datasources;
	}

	/**
	 * @param datasources the datasources to set
	 */
	public void setDatasources(List<String> datasources) {
		this.datasources = datasources;
	}

	/**
	 * @return the strictMatch
	 */
	public boolean isStrictMatch() {
		return strictMatch;
	}

	/**
	 * @param strictMatch the strictMatch to set
	 */
	public void setStrictMatch(boolean strictMatch) {
		this.strictMatch = strictMatch;
	}

	/**
	 * @return the refreshPeriod
	 */
	public int getRefreshPeriod() {
		return refreshPeriod;
	}

	/**
	 * @param refreshPeriod the refreshPeriod to set
	 */
	public void setRefreshPeriod(int refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
	}

	/**
	 * @return the timeUnit
	 */
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	/**
	 * @param timeUnit the timeUnit to set
	 */
	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SpeciesRequest [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", scientificNames=");
		builder.append(scientificNames);
		builder.append(", datasources=");
		builder.append(datasources);
		builder.append(", strictMatch=");
		builder.append(strictMatch);
		builder.append(", refreshPeriod=");
		builder.append(refreshPeriod);
		builder.append(", timeUnit=");
		builder.append(timeUnit);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpeciesRequest other = (SpeciesRequest) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
	
}
