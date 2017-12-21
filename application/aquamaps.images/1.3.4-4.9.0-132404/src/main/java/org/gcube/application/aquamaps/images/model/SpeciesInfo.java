package org.gcube.application.aquamaps.images.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class SpeciesInfo {
	
	public static final String SPECIES_ID="speciesId";
	public static final String SPECIES_PIC="pic";
	public static final String SCIENTIFIC_NAME="scientificName";
	
	@DatabaseField(id=true,columnName=SPECIES_ID)
	private String speciesId;
	
	@DatabaseField(columnName=SPECIES_PIC)
	private String pic;
	
	@DatabaseField(columnName=SCIENTIFIC_NAME,unique=true)
	private String scientificName;
	
	
	SpeciesInfo() {
	}


	public SpeciesInfo(String speciesId, String pic, String scientificName) {
		super();
		this.speciesId = speciesId;
		this.pic = pic;
		this.scientificName = scientificName;
	}


	/**
	 * @return the speciesId
	 */
	public String getSpeciesId() {
		return speciesId;
	}


	/**
	 * @param speciesId the speciesId to set
	 */
	public void setSpeciesId(String speciesId) {
		this.speciesId = speciesId;
	}


	/**
	 * @return the pic
	 */
	public String getPic() {
		return pic;
	}


	/**
	 * @param pic the pic to set
	 */
	public void setPic(String pic) {
		this.pic = pic;
	}


	/**
	 * @return the scientificName
	 */
	public String getScientificName() {
		return scientificName;
	}


	/**
	 * @param scientificName the scientificName to set
	 */
	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((speciesId == null) ? 0 : speciesId.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpeciesInfo other = (SpeciesInfo) obj;
		if (speciesId == null) {
			if (other.speciesId != null)
				return false;
		} else if (!speciesId.equals(other.speciesId))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SpeciesInfo [speciesId=");
		builder.append(speciesId);
		builder.append(", pic=");
		builder.append(pic);
		builder.append(", scientificName=");
		builder.append(scientificName);
		builder.append("]");
		return builder.toString();
	}
	
	
}
