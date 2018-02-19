package org.gcube.portlets.user.geoexplorer.shared;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.gcube.portlets.user.geoexplorer.shared.metadata.ResponsiblePartyItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.distributioninfo.DistributionInfoItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.identification.DataIdentificationItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.quality.DataQualityItem;
import org.opengis.metadata.MetadataExtensionInformation;
import org.opengis.metadata.spatial.SpatialRepresentation;
import org.opengis.referencing.ReferenceSystem;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author Francesco Mangiacrapa ISTI-CNR francesco.mangiacrapa@isti.cnr.it
 *	
 * @date Apr 16, 2013
 *
 * An ISO 19115:2003/19139
 * @see https://gcube.wiki.gcube-system.org/gcube/index.php/ISO_19115:2003/19139
 */
public class MetadataItem implements IsSerializable{

	private String uuid;
	
	/**
	 * Unique identifier for this metadata file, or null.
	 */
	private String fileIdentifier;
	
	/**
	 * Language used for documenting metadata, or null.
	 */
	private String language;
	/**
	 * Character coding standard used for the metadata, or null.
	 */
	private String characterSet;
	
	/**
	 * Identifier of the metadata to which this metadata is a subset, or null.
	 */
	private String parentIdentifier;
	
	/**
	 * Hierarchy levels for which the metadata is provided.
	 */
	private List<String> hierarchyLevelName;
	
	/**
	 * Parties responsible for the metadata information.
	 */
	private List<ResponsiblePartyItem> contacts;
	
	/**
	 * Date that the metadata was created.
	 */
	private String dateStamp;
	
	/**
	 * Name of the metadata standard used, or null.
	 */
	private String metadataStandardName;
	
	/**
	 * Version of the metadata standard used, or null.
	 */
	private String metadataStandardVersion;
	
	/**
	 * Uniformed Resource Identifier of the dataset, or null.
	 */
	private String dataSetURI;

	/**
	 * Alternatively used localized character string for a linguistic extension.
	 */
	private List<Locale> locale;

	/**
	 * Information required to identify a dataset.
	 */
	private DataIdentificationItem identificationInfo;
	
    /**
     * Provides information about the distributor of and options for obtaining the resource(s).
     *
     */
    private DistributionInfoItem distributionInfo;
    
    /**
     * Provides overall assessment of quality of a resource(s).
     *
     * 
     */
    private List<DataQualityItem> dataQualityInfo;
    
    
    private Collection<? extends SpatialRepresentation> spatialRepresentation;
    
    
    private Collection<? extends ReferenceSystem> referenceSystems;
    
    
    private Collection<? extends MetadataExtensionInformation> extensionsInfo;
    
	public MetadataItem() {
	}
	
	/**
	 * 
	 * @param uuid
	 * @param fileIdentifier
	 */
	public MetadataItem(String uuid, String fileIdentifier) {
		this.uuid = uuid;
		this.fileIdentifier = fileIdentifier;
	}
	
	/**
	 * 
	 * @param uuid
	 */
	public MetadataItem(String uuid) {
		this.uuid = uuid;
	}


	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getFileIdentifier() {
		return fileIdentifier;
	}

	public void setFileIdentifier(String fileIdentifier) {
		this.fileIdentifier = fileIdentifier;
	}

	public String getUuid() {
		return uuid;
	}

	public String getCharacterSet() {
		return characterSet;
	}

	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}

	public String getParentIdentifier() {
		return parentIdentifier;
	}

	public void setParentIdentifier(String parentIdentifier) {
		this.parentIdentifier = parentIdentifier;
	}

	public List<String> getHierarchyLevelName() {
		return hierarchyLevelName;
	}

	public void setHierarchyLevelName(List<String> hierarchyLevelName) {
		this.hierarchyLevelName = hierarchyLevelName;
	}

	public List<ResponsiblePartyItem> getContacts() {
		return contacts;
	}

	public void setContacts(List<ResponsiblePartyItem> contacts) {
		this.contacts = contacts;
	}

	public String getDateStamp() {
		return dateStamp;
	}

	public void setDateStamp(String dateStamp) {
		this.dateStamp = dateStamp;
	}

	public String getMetadataStandardName() {
		return metadataStandardName;
	}

	public void setMetadataStandardName(String metadataStandardName) {
		this.metadataStandardName = metadataStandardName;
	}

	public String getMetadataStandardVersion() {
		return metadataStandardVersion;
	}

	public void setMetadataStandardVersion(String metadataStandardVersion) {
		this.metadataStandardVersion = metadataStandardVersion;
	}

	public String getDataSetURI() {
		return dataSetURI;
	}

	public void setDataSetURI(String dataSetURI) {
		this.dataSetURI = dataSetURI;
	}

	public List<Locale> getLocale() {
		return locale;
	}

	public void setLocale(List<Locale> locale) {
		this.locale = locale;
	}

	 /**
     * 
     * @return The distributor of and options for obtaining the resource(s).
     */
	public DistributionInfoItem getDistributionInfo() {
		return distributionInfo;
	}

	public void setDistributionInfo(DistributionInfoItem distributionInfo) {
		this.distributionInfo = distributionInfo;
	}


	/**
	 * @return Overall assessment of quality of a resource(s).
	 */
	
	public List<DataQualityItem> getDataQualityInfo() {
		return dataQualityInfo;
	}

	public void setDataQualityInfo(List<DataQualityItem> dataQualityInfo) {
		this.dataQualityInfo = dataQualityInfo;
	}

	public DataIdentificationItem getIdentificationInfo() {
		return identificationInfo;
	}

	public void setIdentificationInfo(DataIdentificationItem identificationInfo) {
		this.identificationInfo = identificationInfo;
	}


	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	
	
	//NEW
	public Collection<? extends SpatialRepresentation> getSpatialRepresentation() {
		return spatialRepresentation;
	}

	public void setSpatialRepresentation(
			Collection<? extends SpatialRepresentation> spatialRepresentation) {
		this.spatialRepresentation = spatialRepresentation;
	}

	/**
	 * @return the referenceSystems
	 */
	public Collection<? extends ReferenceSystem> getReferenceSystems() {
		return referenceSystems;
	}

	/**
	 * @param referenceSystems the referenceSystems to set
	 */
	public void setReferenceSystems(Collection<? extends ReferenceSystem> referenceSystems) {
		this.referenceSystems = referenceSystems;
	}

	/**
	 * @return the extensionsInfo
	 */
	public Collection<? extends MetadataExtensionInformation> getExtensionsInfo() {
		return extensionsInfo;
	}

	/**
	 * @param extensionsInfo the extensionsInfo to set
	 */
	public void setExtensionsInfo(Collection<? extends MetadataExtensionInformation> extensionsInfo) {
		this.extensionsInfo = extensionsInfo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MetadataItem [uuid=");
		builder.append(uuid);
		builder.append(", fileIdentifier=");
		builder.append(fileIdentifier);
		builder.append(", language=");
		builder.append(language);
		builder.append(", characterSet=");
		builder.append(characterSet);
		builder.append(", parentIdentifier=");
		builder.append(parentIdentifier);
		builder.append(", hierarchyLevelName=");
		builder.append(hierarchyLevelName);
		builder.append(", contacts=");
		builder.append(contacts);
		builder.append(", dateStamp=");
		builder.append(dateStamp);
		builder.append(", metadataStandardName=");
		builder.append(metadataStandardName);
		builder.append(", metadataStandardVersion=");
		builder.append(metadataStandardVersion);
		builder.append(", dataSetURI=");
		builder.append(dataSetURI);
		builder.append(", locale=");
		builder.append(locale);
		builder.append(", identificationInfo=");
		builder.append(identificationInfo);
		builder.append(", distributionInfo=");
		builder.append(distributionInfo);
		builder.append(", dataQualityInfo=");
		builder.append(dataQualityInfo);
		builder.append(", spatialRepresentation=");
		builder.append(spatialRepresentation);
		builder.append(", referenceSystems=");
		builder.append(referenceSystems);
		builder.append(", extensionsInfo=");
		builder.append(extensionsInfo);
		builder.append("]");
		return builder.toString();
	}
}
