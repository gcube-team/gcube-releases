package org.gcube.application.aquamaps.images.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class MapItem {

	public static final String HSPEC_ID="hspecId";
	public static final String SPECIES_ID="speciesId";
	public static final String SCIENTIFIC_NAME="scientificName";

	public static final String STATIC_IMAGE="staticImage";
	public static final String GEO="geo";
	
	@DatabaseField(generatedId = true)
	private Integer id;
	
	@DatabaseField(canBeNull= false,columnName=HSPEC_ID,index=true,uniqueCombo=true)
	private int hspecId;
	@DatabaseField(canBeNull= false,columnName=SPECIES_ID,index=true,uniqueCombo=true)
	private String speciesId;
	@DatabaseField(canBeNull= false,columnName=SCIENTIFIC_NAME,index=true,uniqueCombo=true)
	private String scientificName;

	@DatabaseField(columnName=STATIC_IMAGE)
	private String staticImageUri;
	@DatabaseField(columnName=GEO)
	private String geoId;
	
	
	MapItem() {

	}


	public MapItem(int hspecId, String speciesId, String scientificName,
			String staticImageUri, String geoId) {
		super();		
		this.hspecId = hspecId;
		this.speciesId = speciesId;
		this.scientificName = scientificName;
		this.staticImageUri = staticImageUri;
		this.geoId = geoId;
	}


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the hspecId
	 */
	public int getHspecId() {
		return hspecId;
	}


	/**
	 * @param hspecId the hspecId to set
	 */
	public void setHspecId(int hspecId) {
		this.hspecId = hspecId;
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


	


	/**
	 * @return the staticImageUri
	 */
	public String getStaticImageUri() {
		return staticImageUri;
	}


	/**
	 * @param staticImageUri the staticImageUri to set
	 */
	public void setStaticImageUri(String staticImageUri) {
		this.staticImageUri = staticImageUri;
	}


	/**
	 * @return the geoId
	 */
	public String getGeoId() {
		return geoId;
	}

	
	/**
	 * 
	 * @param geoId the geoId to set
	 */
	public void setGeoId(String geoId) {
		this.geoId = geoId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hspecId;
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
		MapItem other = (MapItem) obj;
		if (hspecId != other.hspecId)
			return false;
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
		builder.append("MapItem [hspecId=");
		builder.append(hspecId);
		builder.append(", speciesId=");
		builder.append(speciesId);
		builder.append(", scientificName=");
		builder.append(scientificName);
		builder.append(", staticImageUri=");
		builder.append(staticImageUri);
		builder.append(", geoId=");
		builder.append(geoId);
		builder.append("]");
		return builder.toString();
	}
	
	

}
