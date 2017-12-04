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
public class ResultRow implements FetchingElement, SelectableElement, Serializable, TaxonomyProvider {

	private static final long serialVersionUID = 1028839024130840026L;

	public final static String ID_FIELD = "id";
	public final static String SERVICE_ID_FIELD = "serviceIdField";
	public final static String PARENT_FOREIGN_KEY_TAXON = "matchingTaxon";
	public final static String DATAPROVIDER_NAME = "dataProviderName";
	public final static String DATASOURCE_NAME = "dataSourceName";
	public final static String SELECTED = "selected";
	public final static String DATASET_NAME = "dataSetName";
	public final static String DATASET_ID = "dataSetId";
	public final static String DATASET_CITATION = "dataSetCitation";
	public final static String MATCHING_CREDITS = "matchingCredits";
	public final static String EXISTS_COMMONNAME = "existsCommonName";
	public final static String IMAGE_COUNT = "imagesCount";
	public static final String IMAGE_KEY = "imagesKey";
	public static final String LAYERS_COUNT = "layersCount";
	public static final String LAYERS_KEY = "layersKey";
	public static final String OCCURRENCES_COUNT = "occurencesCount";
	public static final String OCCURRENCES_KEY = "occurencesKey";
	public static final String MAP_COUNT = "mapsCount";
	public static final String MAP_KEY = "mapsKey";
	public static final String BASE_TAXON_VALUE = "baseTaxonValue";
	public static final String DATAPROVIDER_ID = "dataProviderId";
	public static final String DATASOURCE_ID = "dataSourceId";

	public static final String KINGDOM_ID = "kingdomID";
	public static final String PHYLUM_ID =  "phylumID";
	public static final String CLASS_ID = "classID";
	public static final String ORDER_ID = "orderID";
	public static final String FAMILY_ID = "familyID";
	public static final String GENUS_ID = "genusID";
	public static final String SPECIES_ID = "speciesID";

	public final static String SCIENTIFICNAMEAUTHORSHIP = "scientificNameAuthorship";  //USED
	public final static String CREDITS = "credits";  //USED
	public final static String LSID = "lsid";  //USED
	public final static String PROPERTIES = "properties"; //USED
	public static final String EXISTS_PROPERTIES = "existsProperties"; //USED


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int id;

	protected boolean selected = false;
	protected String dataSourceId;
	protected String dataSourceName;
	protected String dataProviderId;
	protected String dataProviderName;
	protected String serviceIdField;
	protected String dataSetId;
	protected String dataSetName;
	protected String dataSetCitation;
	protected String matchingCredits;
	protected boolean existsCommonName = false;

	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	protected List<CommonName> commonNames = new ArrayList<CommonName>();

	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	protected List<Taxon> matchingTaxon = new ArrayList<Taxon>();

	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	protected List<ItemParameter> properties = new ArrayList<ItemParameter>();

	protected int imagesCount;
	protected String imagesKey;
	protected int layersCount;
	protected String layersKey;
	protected int occurencesCount;
	protected String occurencesKey;
	protected int mapsCount;
	protected String mapsKey;
	protected String baseTaxonValue;
	protected String kingdomID;
	protected String phylumID;
	protected String classID;
	protected String orderID;
	protected String familyID;
	protected String genusID;
	protected String speciesID;
	protected String scientificNameAuthorship;
	protected String credits;
	protected String lsid;

	protected boolean existsProperties = false;

	protected ResultRow() {
	}


	/**
	 * @param id
	 */
	public ResultRow(int id) {
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
	 * @return the dataSourceId
	 */
	public String getDataSourceId() {
		return dataSourceId;
	}


	/**
	 * @param dataSourceId
	 *            the dataSourceId to set
	 */
	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	/**
	 * @return the dataSourceName
	 */
	public String getDataSourceName() {
		return dataSourceName;
	}

	/**
	 * @param dataSourceName
	 *            the dataSourceName to set
	 */
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
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

	/**
	 * @return the dataSetId
	 */
	public String getDataSetId() {
		return dataSetId;
	}

	/**
	 * @param dataSetId
	 *            the dataSetId to set
	 */
	public void setDataSetId(String dataSetId) {
		this.dataSetId = dataSetId;
	}

	/**
	 * @return the dataSetName
	 */
	public String getDataSetName() {
		return dataSetName;
	}

	/**
	 * @param dataSetName
	 *            the dataSetName to set
	 */
	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
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

	/**
	 * @return the imagesCount
	 */
	public int getImagesCount() {
		return imagesCount;
	}

	/**
	 * @param imagesCount
	 *            the imagesCount to set
	 */
	public void setImagesCount(int imagesCount) {
		this.imagesCount = imagesCount;
	}

	/**
	 * @return the layersCount
	 */
	public int getLayersCount() {
		return layersCount;
	}

	/**
	 * @param layersCount
	 *            the layersCount to set
	 */
	public void setLayersCount(int layersCount) {
		this.layersCount = layersCount;
	}

	/**
	 * @return the occurencesCount
	 */
	public int getOccurencesCount() {
		return occurencesCount;
	}

	/**
	 * @param occurencesCount
	 *            the occurencesCount to set
	 */
	public void setOccurencesCount(int occurencesCount) {
		this.occurencesCount = occurencesCount;
	}

	/**
	 * @return the mapsCount
	 */
	public int getMapsCount() {
		return mapsCount;
	}

	/**
	 * @param mapsCount
	 *            the mapsCount to set
	 */
	public void setMapsCount(int mapsCount) {
		this.mapsCount = mapsCount;
	}

	/**
	 * @return the imagesKey
	 */
	public String getImagesKey() {
		return imagesKey;
	}

	/**
	 * @param imagesKey
	 *            the imagesKey to set
	 */
	public void setImagesKey(String imagesKey) {
		this.imagesKey = imagesKey;
	}

	/**
	 * @return the layersKey
	 */
	public String getLayersKey() {
		return layersKey;
	}

	/**
	 * @param layersKey
	 *            the layersKey to set
	 */
	public void setLayersKey(String layersKey) {
		this.layersKey = layersKey;
	}

	/**
	 * @return the occurencesKey
	 */
	public String getOccurencesKey() {
		return occurencesKey;
	}

	/**
	 * @param occurencesKey
	 *            the occurencesKey to set
	 */
	public void setOccurencesKey(String occurencesKey) {
		this.occurencesKey = occurencesKey;
	}

	/**
	 * @return the mapsKey
	 */
	public String getMapsKey() {
		return mapsKey;
	}

	/**
	 * @param mapsKey
	 *            the mapsKey to set
	 */
	public void setMapsKey(String mapsKey) {
		this.mapsKey = mapsKey;
	}

	/**
	 * @return the matchingCredits
	 */
	public String getMatchingCredits() {
		return matchingCredits;
	}

	/**
	 * @param matchingCredits
	 *            the matchingCredits to set
	 */
	public void setMatchingCredits(String matchingCredits) {
		this.matchingCredits = matchingCredits;
	}

	public void setBaseTaxonValue(String taxonomyValue) {
		this.baseTaxonValue = taxonomyValue;

	}

	public String getBaseTaxonValue() {
		return baseTaxonValue;
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

	public List<CommonName> getCommonNames() {
		return commonNames;
	}

	public void setCommonNames(List<CommonName> commonNames) {
		this.commonNames = commonNames;
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

	public String getServiceId() {
		return serviceIdField;
	}

	public void setServiceId(String serviceIdField) {
		this.serviceIdField = serviceIdField;
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

	public boolean existsProperties() {
		return existsProperties;
	}


	public List<Taxon> getParents() {

		Collections.sort(matchingTaxon);
//		for (Taxon t : matchingTaxon) {
//			System.out.println("+++ Parent :" +t.getId() + ", name: "+t.getName() +", rank: "+t.getRank());
//		}
		return matchingTaxon;
	}


	public void setMatchingTaxon(List<Taxon> matchingTaxon) {
		this.matchingTaxon = matchingTaxon;
	}


	public String getServiceIdField() {
		return serviceIdField;
	}


	public boolean isExistsCommonName() {
		return existsCommonName;
	}


	public List<ItemParameter> getProperties() {
		return properties;
	}


	public boolean isExistsProperties() {
		return existsProperties;
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


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResultRow [id=");
		builder.append(id);
		builder.append(", selected=");
		builder.append(selected);
		builder.append(", dataSourceId=");
		builder.append(dataSourceId);
		builder.append(", dataSourceName=");
		builder.append(dataSourceName);
		builder.append(", dataProviderId=");
		builder.append(dataProviderId);
		builder.append(", dataProviderName=");
		builder.append(dataProviderName);
		builder.append(", serviceIdField=");
		builder.append(serviceIdField);
		builder.append(", dataSetId=");
		builder.append(dataSetId);
		builder.append(", dataSetName=");
		builder.append(dataSetName);
		builder.append(", dataSetCitation=");
		builder.append(dataSetCitation);
		builder.append(", matchingCredits=");
		builder.append(matchingCredits);
		builder.append(", existsCommonName=");
		builder.append(existsCommonName);
		builder.append(", commonNames=");
		builder.append(commonNames);
		builder.append(", matchingTaxon=");
		builder.append(matchingTaxon);
		builder.append(", properties=");
		builder.append(properties);
		builder.append(", imagesCount=");
		builder.append(imagesCount);
		builder.append(", imagesKey=");
		builder.append(imagesKey);
		builder.append(", layersCount=");
		builder.append(layersCount);
		builder.append(", layersKey=");
		builder.append(layersKey);
		builder.append(", occurencesCount=");
		builder.append(occurencesCount);
		builder.append(", occurencesKey=");
		builder.append(occurencesKey);
		builder.append(", mapsCount=");
		builder.append(mapsCount);
		builder.append(", mapsKey=");
		builder.append(mapsKey);
		builder.append(", baseTaxonValue=");
		builder.append(baseTaxonValue);
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
		builder.append(", scientificNameAuthorship=");
		builder.append(scientificNameAuthorship);
		builder.append(", credits=");
		builder.append(credits);
		builder.append(", lsid=");
		builder.append(lsid);
		builder.append(", existsProperties=");
		builder.append(existsProperties);
		builder.append("]");
		return builder.toString();
	}


}
