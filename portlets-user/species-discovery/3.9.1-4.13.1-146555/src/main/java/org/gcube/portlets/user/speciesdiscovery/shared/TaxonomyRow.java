/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
//@Customizer(org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.TaxonomyCustomizer.class)
public class TaxonomyRow implements FetchingElement, Serializable, TaxonomyInterface, TaxonomyProvider, SelectableElement, Comparable<TaxonomyRow> {

	private static final long serialVersionUID = 1028839024130840026L;

	public final static String ID_FIELD = "id";
	public final static String DATAPROVIDER_NAME = "dataProviderName";
	public static final String BASE_TAXON_VALUE = "baseTaxonValue";
	public static final String DATAPROVIDER_ID = "dataProviderId";
	public final static String RANK = "rank";
	public static final String NAME = "name";
	public static final String STATUSREFID = "statusRefId";
	public static final String STATUSNAME = "statusName";
	public static final String DATEMODIFIED = "dateModified";
	public final static String PARENT_ID = "parentID";
	public final static String IS_PARENT = "isParent";
	public final static String DATASET_CITATION = "dataSetCitation";
	public final static String EXISTS_COMMONNAME = "existsCommonName";
	public final static String SELECTED = "selected";
	public final static String MATCHING_ACCORDING_TO = "matchingAccording";
	public final static String SERVICE_ID_FIELD = "serviceIdField";
	public final static String STATUS_REMARKS = "statusRemarks";

	public final static String SCIENTIFICNAMEAUTHORSHIP = "scientificNameAuthorship";  //USED
	public final static String CREDITS = "credits";  //USED
	public final static String LSID = "lsid";  //USED
	public final static String PROPERTIES = "properties"; //TODO 
	public static final String PROPERTIESASXML = "propertiesAsXml";
	
	public static final String KINGDOM_ID = "kingdomID";
	public static final String PHYLUM_ID =  "phylumID";
	public static final String CLASS_ID = "classID";
	public static final String ORDER_ID = "orderID";
	public static final String FAMILY_ID = "familyID";
	public static final String GENUS_ID = "genusID";
	public static final String SPECIES_ID = "speciesID";
	
	public static final String EXISTS_PROPERTIES = "existsProperties"; //USED

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int id;
	protected String scientificNameAuthorship;
	protected String credits;
	protected String lsid;

	protected String name;
	protected String serviceIdField;
	protected boolean selected = false;
	protected String dataProviderId;
	protected String dataProviderName;
	protected String dataSetCitation;
	protected String matchingAccording;
	protected String baseTaxonValue;
	protected String rank;
	private String statusRefId;
	private String statusName;
	private String dateModified;
	protected String kingdomID;
	protected String phylumID;
	protected String classID;
	protected String orderID;
	protected String familyID;
	protected String genusID;
	protected String speciesID;
	protected boolean existsCommonName = false;
	protected String statusRemarks;
	protected boolean existsProperties = false;
	
	private boolean isParent = false;

	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	protected List<CommonName> commonNames = new ArrayList<CommonName>();
	
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	protected List<ItemParameter> properties = new ArrayList<ItemParameter>();
	
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	protected List<TaxonomyRow> parents;
	
	//USED FOR PARENT
	protected String parentID;
	protected int parentIndex = -1;

	protected TaxonomyRow() {
		
		
	}

	/**
	 * @param id
	 */
	public TaxonomyRow(int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the id
	 */
	public String getIdToString() {
		return id+"";
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the dataProviderId
	 */
	public String getDataProviderId() {
		return dataProviderId;
	}

	/**
	 * @param dataProviderId
	 *            the dataProviderId to set
	 */
	public void setDataProviderId(String dataProviderId) {
		this.dataProviderId = dataProviderId;
	}

	/**
	 * @return the dataProviderName
	 */
	public String getDataProviderName() {
		return dataProviderName;
	}

	/**
	 * @param dataProviderName
	 *            the dataProviderName to set
	 */
	public void setDataProviderName(String dataProviderName) {
		this.dataProviderName = dataProviderName;
	}


	public List<CommonName> getCommonNames() {
//		return new ArrayList<CommonName>(commonNames);
		return commonNames;
	}

	public void setCommonNames(List<CommonName> commonNames) {
		this.commonNames = commonNames;
	}
	
	/**
	 * @return the dataSetCitation
	 */
	public String getDataSetCitation() {
		return dataSetCitation;
	}

	/**
	 * @param dataSetCitation
	 *            the dataSetCitation to set
	 */
	public void setDataSetCitation(String dataSetCitation) {
		this.dataSetCitation = dataSetCitation;
	}


	public void setBaseTaxonValue(String taxonomyValue) {
		this.baseTaxonValue = taxonomyValue;

	}

	public String getBaseTaxonValue() {
		return baseTaxonValue;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStatusRefId(String statusRefId) {
		this.statusRefId = statusRefId;
		
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
		
	}

	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
		
	}
	
	public void setParent(List<TaxonomyRow> parent) {
		this.parents = parent;
	}

	@Override
	public List<TaxonomyRow> getParents() {
		Collections.sort(parents);
		return parents;
	}

	public String getStatusRefId() {
		return statusRefId;
	}

	public String getStatusName() {
		return statusName;
	}

	public String getDateModified() {
		return dateModified;
	}

	public String getKingdomID() {
		return kingdomID;
	}

	public void setKingdomID(String kingdomID) {
		this.kingdomID = kingdomID;
	}

	public String getPhylumID() {
		return phylumID;
	}

	public void setPhylumID(String phylumID) {
		this.phylumID = phylumID;
	}

	public String getClassID() {
		return classID;
	}

	public void setClassID(String classID) {
		this.classID = classID;
	}

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public String getFamilyID() {
		return familyID;
	}

	public void setFamilyID(String familyID) {
		this.familyID = familyID;
	}

	public String getGenusID() {
		return genusID;
	}

	public void setGenusID(String genusID) {
		this.genusID = genusID;
	}

	public String getSpeciesID() {
		return speciesID;
	}

	public void setSpeciesID(String speciesID) {
		this.speciesID = speciesID;
	}

	@Override
	public String getTaxonId() {
		// FIXME
		return ""+id;
	}

	@Override
	public String getAccordingTo() {
		return this.matchingAccording;
	}

	/**
	 * 
	 * @return true if there are common names
	 */
	public boolean existsCommonName() {
		return existsCommonName;
	}

	/**
	 * 
	 * @param the
	 *            boolean to set
	 */
	public void setExistsCommonName(boolean existsCommonName) {
		this.existsCommonName = existsCommonName;
	}

	public void setMatchingAccording(String matchingAccording) {
		this.matchingAccording = matchingAccording;
	}

	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	public String getServiceId() {
		return serviceIdField;
	}

	public void setServiceId(String serviceIdField) {
		this.serviceIdField = serviceIdField;
	}

	public String getStatusRemarks() {
		return statusRemarks;
	}

	public void setStatusRemarks(String statusRemarks) {
		this.statusRemarks = statusRemarks;
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

	public List<ItemParameter> getProperties() {
//		return new ArrayList<ItemParameter>(properties);
		return properties;
	}

	public void setProperties(List<ItemParameter> properties) {
		this.properties = properties;
	}

	public void setExistsProperties(boolean b) {
		this.existsProperties = b;
	}
	public boolean existsProperties() {
		return existsProperties;
	}
	
	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	public int getParentIndex() {
		return parentIndex;
	}

	public void setParentIndex(int parentIndex) {
		this.parentIndex = parentIndex;
	}


	/* (non-Javadoc)
	 * Comparable on insertion order
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TaxonomyRow o) {
		return parentIndex - o.getParentIndex();
	}

	public String getScientificNameAuthorship() {
		return scientificNameAuthorship;
	}

	public void setScientificNameAuthorship(String scientificNameAuthorship) {
		this.scientificNameAuthorship = scientificNameAuthorship;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TaxonomyRow [id=");
		builder.append(id);
		builder.append(", scientificNameAuthorship=");
		builder.append(scientificNameAuthorship);
		builder.append(", credits=");
		builder.append(credits);
		builder.append(", lsid=");
		builder.append(lsid);
		builder.append(", name=");
		builder.append(name);
		builder.append(", serviceIdField=");
		builder.append(serviceIdField);
		builder.append(", selected=");
		builder.append(selected);
		builder.append(", dataProviderId=");
		builder.append(dataProviderId);
		builder.append(", dataProviderName=");
		builder.append(dataProviderName);
		builder.append(", dataSetCitation=");
		builder.append(dataSetCitation);
		builder.append(", matchingAccording=");
		builder.append(matchingAccording);
		builder.append(", baseTaxonValue=");
		builder.append(baseTaxonValue);
		builder.append(", rank=");
		builder.append(rank);
		builder.append(", statusRefId=");
		builder.append(statusRefId);
		builder.append(", statusName=");
		builder.append(statusName);
		builder.append(", dateModified=");
		builder.append(dateModified);
		builder.append(", kingdomID=");
		builder.append(kingdomID);
		builder.append(", phylumID=");
		builder.append(phylumID);
		builder.append(", classID=");
		builder.append(classID);
		builder.append(", orderID=");
		builder.append(orderID);
		builder.append(", familyID=");
		builder.append(familyID);
		builder.append(", genusID=");
		builder.append(genusID);
		builder.append(", speciesID=");
		builder.append(speciesID);
		builder.append(", existsCommonName=");
		builder.append(existsCommonName);
		builder.append(", statusRemarks=");
		builder.append(statusRemarks);
		builder.append(", existsProperties=");
		builder.append(existsProperties);
		builder.append(", isParent=");
		builder.append(isParent);
		builder.append(", commonNames=");
		builder.append(commonNames);
		builder.append(", properties=");
		builder.append(properties);
		builder.append(", parents=");
		builder.append(parents);
		builder.append(", parentID=");
		builder.append(parentID);
		builder.append(", parentIndex=");
		builder.append(parentIndex);
		builder.append("]");
		return builder.toString();
	}
	
}
