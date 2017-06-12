package org.gcube.data.spd.model.products;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.annotations.IsValid;
import org.gcube.common.validator.annotations.NotEmpty;
import org.gcube.common.validator.annotations.NotNull;
import org.gcube.data.spd.model.BasisOfRecord;
import org.gcube.data.spd.model.util.ElementProperty;


@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class OccurrencePoint implements ResultElement{
		
	@NotNull
	@NotEmpty
	@XmlAttribute
	private String id;
	
	@XmlAttribute
	private String provider;
	
	@XmlAttribute
	private String scientificNameAuthorship;
	
	@XmlAttribute
	private String identifiedBy;
	
	@XmlAttribute
	private String recordedBy;
	
	@XmlAttribute
	private String credits;
	
	@XmlAttribute
	private String institutionCode;
	
	@XmlAttribute
	private String collectionCode;
	
	@XmlAttribute
	private String catalogueNumber;
	

	
	@XmlAttribute
	private Calendar eventDate;
	
	@XmlAttribute
	private Calendar modified;
	
	@NotNull
	@NotEmpty
	@XmlAttribute
	private String scientificName;
	
	@XmlAttribute
	private String kingdom;
	
	@XmlAttribute
	private String family;
	
	@XmlAttribute
	private String locality;
	
	@XmlAttribute
	private String country;
	
	@XmlAttribute
	private String citation;
	
	@XmlAttribute
	private double decimalLatitude;
	
	@XmlAttribute
	private double decimalLongitude;
	
	@XmlAttribute
	private String coordinateUncertaintyInMeters;
	
	@XmlAttribute
	private double maxDepth;
	
	@XmlAttribute
	private double minDepth;
	
	@NotNull
	@XmlAttribute
	private BasisOfRecord basisOfRecord;
	
	@IsValid
	@XmlElement
	private DataSet dataSet;
	
	@XmlElement
	private List<ElementProperty> properties = new ArrayList<ElementProperty>() ;
	
	
	protected OccurrencePoint(){}
		
	public OccurrencePoint(String id) {
		super();
		this.id = id;
		this.basisOfRecord = BasisOfRecord.HumanObservation;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCitation() {
		return citation;
	}
	public void setCitation(String citation) {
		this.citation = citation;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getInstitutionCode() {
		return institutionCode;
	}
	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}
	public String getCollectionCode() {
		return collectionCode;
	}
	public void setCollectionCode(String collectionCode) {
		this.collectionCode = collectionCode;
	}
	public String getRecordedBy() {
		return recordedBy;
	}
	public void setRecordedBy(String recordedBy) {
		this.recordedBy = recordedBy;
	}
	public Calendar getEventDate() {
		return eventDate;
	}
	public void setEventDate(Calendar eventDate) {
		this.eventDate = eventDate;
	}
	public String getScientificName() {
		return scientificName;
	}
	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}
	public String getKingdom() {
		return kingdom;
	}
	public void setKingdom(String kingdom) {
		this.kingdom = kingdom;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public double getDecimalLatitude() {
		return decimalLatitude;
	}
	public void setDecimalLatitude(double decimalLatitude) {
		this.decimalLatitude = decimalLatitude;
	}
	public double getDecimalLongitude() {
		return decimalLongitude;
	}
	public void setDecimalLongitude(double decimalLongitude) {
		this.decimalLongitude = decimalLongitude;
	}
	public String getCoordinateUncertaintyInMeters() {
		return coordinateUncertaintyInMeters;
	}
	public void setCoordinateUncertaintyInMeters(String coordinateUncertaintyInMeters) {
		this.coordinateUncertaintyInMeters = coordinateUncertaintyInMeters;
	}
	public BasisOfRecord getBasisOfRecord() {
		return basisOfRecord;
	}
	public void setBasisOfRecord(BasisOfRecord basisOfRecord) {
		this.basisOfRecord = basisOfRecord;
	}
	public double getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(double maxDepth) {
		this.maxDepth = maxDepth;
	}
	public double getMinDepth() {
		return minDepth;
	}
	public void setMinDepth(double minDepth) {
		this.minDepth = minDepth;
	}
	
	public Calendar getModified() {
		return modified;
	}
	public void setModified(Calendar modified) {
		this.modified = modified;
	}
	
	public String getCatalogueNumber() {
		return catalogueNumber;
	}
	public void setCatalogueNumber(String catalogueNumber) {
		this.catalogueNumber = catalogueNumber;
	}
	
	public DataSet getDataSet() {
		return dataSet;
	}
	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}
	
	
	
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public ResultType getType() {
		return ResultType.OCCURRENCEPOINT;
	}

	/**
	 * @return the scientificNameAuthorship
	 */
	public String getScientificNameAuthorship() {
		return scientificNameAuthorship;
	}

	/**
	 * @param scientificNameAuthorship the scientificNameAuthorship to set
	 */
	public void setScientificNameAuthorship(String scientificNameAuthorship) {
		this.scientificNameAuthorship = scientificNameAuthorship;
	}

	/**
	 * @return the identifiedBy
	 */
	public String getIdentifiedBy() {
		return identifiedBy;
	}

	/**
	 * @param identifiedBy the identifiedBy to set
	 */
	public void setIdentifiedBy(String identifiedBy) {
		this.identifiedBy = identifiedBy;
	}

	public String getCredits() {
		return credits;
	}

	public void setCredits(String credits) {
		this.credits = credits;
	}

	public void addProperty(ElementProperty property){
		this.properties.add(property);
	}
	
	public void resetProperties(){
		this.properties = new ArrayList<ElementProperty>();
	}
	
	
	public List<ElementProperty> getProperties() {
		return Collections.unmodifiableList(properties);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OccurrencePoint [id=" + id + ", provider=" + provider
				+ ", scientificNameAuthorship=" + scientificNameAuthorship
				+ ", identifiedBy=" + identifiedBy + ", recordedBy="
				+ recordedBy + ", credits=" + credits + ", institutionCode="
				+ institutionCode + ", collectionCode=" + collectionCode
				+ ", catalogueNumber=" + catalogueNumber + ", eventDate="
				+ eventDate + ", modified=" + modified + ", scientificName="
				+ scientificName + ", kingdom=" + kingdom + ", family="
				+ family + ", locality=" + locality + ", country=" + country
				+ ", citation=" + citation + ", decimalLatitude="
				+ decimalLatitude + ", decimalLongitude=" + decimalLongitude
				+ ", coordinateUncertaintyInMeters="
				+ coordinateUncertaintyInMeters + ", maxDepth=" + maxDepth
				+ ", minDepth=" + minDepth + ", basisOfRecord=" + basisOfRecord
				+ ", dataSet=" + dataSet + ", properties=" + properties + "]";
	}

	
	
}
