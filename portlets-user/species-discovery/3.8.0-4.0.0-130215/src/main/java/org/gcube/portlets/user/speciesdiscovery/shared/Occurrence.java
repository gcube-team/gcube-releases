/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

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


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
@Entity
public class Occurrence implements Serializable, FetchingElement {

	protected static final long serialVersionUID = -8668653643227636763L;

	public final static String ID_FIELD = "id";
	public final static String INSTITUTE_CODE = "institutionCode";
	public final static String COLLECTION_CODE = "collectionCode";
	public final static String CATALOGUE_NUMBER = "catalogueNumber";
	public final static String RECORD_BY = "recordedBy";
	public final static String IDENTIFIED_BY = "identifiedBy";
	
	public final static String EVENT_DATE = "eventDate";
	public final static String MODIFIED = "modified";
	public final static String SCIENTIFICNAME = "scientificName";
	public final static String KINGDOM = "kingdom";
	
	public final static String FAMILY = "family";
	public final static String LOCALITY = "locality";
	public final static String COUNTRY = "country";
	public final static String CITATION = "citation";
	public final static String DECIMAL_LATITUDE = "decimalLatitude";
	
	public final static String DECIMAL_LONGITUDE = "decimalLongitude";
	public final static String BASIS_OF_RECORD = "basisOfRecord";
	public final static String COORDINATE_INMETERS = "coordinateUncertaintyInMeters";
	public final static String MAX_DEPTH = "maxDepth";
	public final static String MIN_DEPTH = "minDepth";
	
	public final static String DATAPROVIDER = "dataProvider";
	public final static String DATASET = "dataSet";
	public final static String DATASOURCE = "dataSource";
	
	public final static String SERVICE_ID_FIELD = "serviceIdField";
	
	public final static String SCIENTIFICNAMEAUTHORSHIP = "scientificNameAuthorship";  //USED
	public final static String CREDITS = "credits";  //USED
	public final static String LSID = "lsid";  //USED
	public final static String PROPERTIES = "properties"; //TODO 
	
	public static final String EXISTS_PROPERTIES = "existsProperties"; //USED
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int id;
	protected String institutionCode;
	protected String collectionCode;
	protected String catalogueNumber;
	protected String recordedBy;
	protected String identifiedBy;
	protected String eventDate;
	protected String modified;
	protected String scientificName;
	protected String kingdom;
	protected String family;
	protected String locality;
	protected String country;
	protected String citation;
	protected String decimalLatitude;
	protected String decimalLongitude;
	protected String coordinateUncertaintyInMeters;
	protected String maxDepth;
	protected String minDepth;
	protected String basisOfRecord;
	private String serviceIdField;
	protected String dataProvider;
	protected String dataSet;
	protected String dataSource;
	protected String scientificNameAuthorship;
	protected String credits;
	protected String lsid;

	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	protected List<ItemParameter> properties = new ArrayList<ItemParameter>();
	
	protected boolean existsProperties = false;

	/**
	 * 
	 */
	public Occurrence() {
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
	 * @return the institutionCode
	 */
	public String getInstitutionCode() {
		return institutionCode;
	}

	/**
	 * @param institutionCode the institutionCode to set
	 */
	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	/**
	 * @return the collectionCode
	 */
	public String getCollectionCode() {
		return collectionCode;
	}

	/**
	 * @param collectionCode the collectionCode to set
	 */
	public void setCollectionCode(String collectionCode) {
		this.collectionCode = collectionCode;
	}

	/**
	 * @return the catalogueNumber
	 */
	public String getCatalogueNumber() {
		return catalogueNumber;
	}

	/**
	 * @param catalogueNumber the catalogueNumber to set
	 */
	public void setCatalogueNumber(String catalogueNumber) {
		this.catalogueNumber = catalogueNumber;
	}

	/**
	 * @return the recordedBy
	 */
	public String getRecordedBy() {
		return recordedBy;
	}

	/**
	 * @param recordedBy the recordedBy to set
	 */
	public void setRecordedBy(String recordedBy) {
		this.recordedBy = recordedBy;
	}

	/**
	 * @return the eventDate
	 */
	public String getEventDate() {
		return eventDate;
	}

	/**
	 * @param eventDate the eventDate to set
	 */
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	/**
	 * @return the modified
	 */
	public String getModified() {
		return modified;
	}

	/**
	 * @param modified the modified to set
	 */
	public void setModified(String modified) {
		this.modified = modified;
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
	 * @return the kingdom
	 */
	public String getKingdom() {
		return kingdom;
	}

	/**
	 * @param kingdom the kingdom to set
	 */
	public void setKingdom(String kingdom) {
		this.kingdom = kingdom;
	}

	/**
	 * @return the family
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * @param family the family to set
	 */
	public void setFamily(String family) {
		this.family = family;
	}

	/**
	 * @return the locality
	 */
	public String getLocality() {
		return locality;
	}

	/**
	 * @param locality the locality to set
	 */
	public void setLocality(String locality) {
		this.locality = locality;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the citation
	 */
	public String getCitation() {
		return citation;
	}

	/**
	 * @param citation the citation to set
	 */
	public void setCitation(String citation) {
		this.citation = citation;
	}

	/**
	 * @return the decimalLatitude
	 */
	public String getDecimalLatitude() {
		return decimalLatitude;
	}

	/**
	 * @param decimalLatitude the decimalLatitude to set
	 */
	public void setDecimalLatitude(String decimalLatitude) {
		this.decimalLatitude = decimalLatitude;
	}

	/**
	 * @return the decimalLongitude
	 */
	public String getDecimalLongitude() {
		return decimalLongitude;
	}

	/**
	 * @param decimalLongitude the decimalLongitude to set
	 */
	public void setDecimalLongitude(String decimalLongitude) {
		this.decimalLongitude = decimalLongitude;
	}

	/**
	 * @return the coordinateUncertaintyInMeters
	 */
	public String getCoordinateUncertaintyInMeters() {
		return coordinateUncertaintyInMeters;
	}

	/**
	 * @param coordinateUncertaintyInMeters the coordinateUncertaintyInMeters to set
	 */
	public void setCoordinateUncertaintyInMeters(String coordinateUncertaintyInMeters) {
		this.coordinateUncertaintyInMeters = coordinateUncertaintyInMeters;
	}

	/**
	 * @return the maxDepth
	 */
	public String getMaxDepth() {
		return maxDepth;
	}

	/**
	 * @param maxDepth the maxDepth to set
	 */
	public void setMaxDepth(String maxDepth) {
		this.maxDepth = maxDepth;
	}

	/**
	 * @return the minDepth
	 */
	public String getMinDepth() {
		return minDepth;
	}

	/**
	 * @param minDepth the minDepth to set
	 */
	public void setMinDepth(String minDepth) {
		this.minDepth = minDepth;
	}

	/**
	 * @return the basisOfRecord
	 */
	public String getBasisOfRecord() {
		return basisOfRecord;
	}

	/**
	 * @param basisOfRecord the basisOfRecord to set
	 */
	public void setBasisOfRecord(String basisOfRecord) {
		this.basisOfRecord = basisOfRecord;
	}
	
	public String getServiceId() {
		return serviceIdField;
	}

	public void setServiceId(String serviceIdField) {
		this.serviceIdField = serviceIdField;
	}
	
	public void setDataProvider(String dataProvider) {
		this.dataProvider = dataProvider;
	}

	public String getDataSet() {
		return dataSet;
	}

	public void setDataSet(String dataSet) {
		this.dataSet = dataSet;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getDataProvider() {
		return dataProvider;
	}

	public String getCredits() {
		return credits;
	}

	public void setCredits(String credits) {
		this.credits = credits;
	}

	public String getLsid() {
		return lsid;
	}

	public void setLsid(String lsid) {
		this.lsid = lsid;
	}

	public void setExistsProperties(boolean b) {
		this.existsProperties = b;
		
	}

	public List<ItemParameter> getProperties() {
		return properties;
	}

	public void setProperties(List<ItemParameter> properties) {
		this.properties = properties;
	}

	public String getScientificNameAuthorship() {
		return scientificNameAuthorship;
	}

	public void setScientificNameAuthorship(String scientificNameAuthorship) {
		this.scientificNameAuthorship = scientificNameAuthorship;
	}


	public String getIdentifiedBy() {
		return identifiedBy;
	}

	public void setIdentifiedBy(String identifiedBy) {
		this.identifiedBy = identifiedBy;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Occurrence [id=");
		builder.append(id);
		builder.append(", institutionCode=");
		builder.append(institutionCode);
		builder.append(", collectionCode=");
		builder.append(collectionCode);
		builder.append(", catalogueNumber=");
		builder.append(catalogueNumber);
		builder.append(", recordedBy=");
		builder.append(recordedBy);
		builder.append(", identifiedBy=");
		builder.append(identifiedBy);
		builder.append(", eventDate=");
		builder.append(eventDate);
		builder.append(", modified=");
		builder.append(modified);
		builder.append(", scientificName=");
		builder.append(scientificName);
		builder.append(", kingdom=");
		builder.append(kingdom);
		builder.append(", family=");
		builder.append(family);
		builder.append(", locality=");
		builder.append(locality);
		builder.append(", country=");
		builder.append(country);
		builder.append(", citation=");
		builder.append(citation);
		builder.append(", decimalLatitude=");
		builder.append(decimalLatitude);
		builder.append(", decimalLongitude=");
		builder.append(decimalLongitude);
		builder.append(", coordinateUncertaintyInMeters=");
		builder.append(coordinateUncertaintyInMeters);
		builder.append(", maxDepth=");
		builder.append(maxDepth);
		builder.append(", minDepth=");
		builder.append(minDepth);
		builder.append(", basisOfRecord=");
		builder.append(basisOfRecord);
		builder.append(", serviceIdField=");
		builder.append(serviceIdField);
		builder.append(", dataProvider=");
		builder.append(dataProvider);
		builder.append(", dataSet=");
		builder.append(dataSet);
		builder.append(", dataSource=");
		builder.append(dataSource);
		builder.append(", scientificNameAuthorship=");
		builder.append(scientificNameAuthorship);
		builder.append(", credits=");
		builder.append(credits);
		builder.append(", lsid=");
		builder.append(lsid);
		builder.append(", properties=");
		builder.append(properties);
		builder.append(", existsProperties=");
		builder.append(existsProperties);
		builder.append("]");
		return builder.toString();
	}
	
}
