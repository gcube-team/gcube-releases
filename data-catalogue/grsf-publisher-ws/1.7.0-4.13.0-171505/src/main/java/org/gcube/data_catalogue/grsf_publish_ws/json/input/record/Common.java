package org.gcube.data_catalogue.grsf_publish_ws.json.input.record;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CkanResource;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CustomField;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Group;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Tag;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.TimeSeries;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.AnnotationBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.RefersToBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.Resource;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.SimilarRecordBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.TimeSeriesBean;
import org.gcube.datacatalogue.common.Constants;
import org.gcube.datacatalogue.common.enums.Sources;
import org.gcube.datacatalogue.common.enums.Status;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information that both Stock and Fishery records must contain.
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public abstract class Common extends Base{

	@JsonProperty(Constants.DATA_OWNER_JSON_KEY)
	@CustomField(key=Constants.DATA_OWNER_CUSTOM_KEY)
	private List<String> dataOwner;

	@JsonProperty(Constants.DATABASE_SOURCES_JSON_KEY)
	@CkanResource
	@Valid
	private List<Resource<Sources>> databaseSources;

	@JsonProperty(Constants.SOURCES_OF_INFORMATION_JSON_KEY)
	@CkanResource
	@Valid
	private List<Resource<String>> sourceOfInformation;

	@JsonProperty(Constants.REFERS_TO_JSON_KEY)
	@Valid
	private List<RefersToBean> refersTo;

	@JsonProperty(Constants.SHORT_NAME_JSON_KEY)
	@CustomField(key=Constants.SHORT_NAME_CUSTOM_KEY)
	private String shortName;

	@JsonProperty(Constants.TRACEABILITY_FLAG_JSON_KEY)
	@CustomField(key=Constants.TRACEABILITY_FLAG_CUSTOM_KEY)
	@Group(condition="true", groupNameOverValue=Constants.TRACEABILITY_FLAG_GROUP_NAME) // record is added to group grsf-traceability-flag if Traceability Flag is true
	private Boolean traceabilityFlag;

	@JsonProperty(Constants.SDG_FLAG_JSON_KEY)
	@CustomField(key=Constants.SDG_FLAG_CUSTOM_KEY)
	@Group(condition="true", groupNameOverValue=Constants.SDG_FLAG_GROUP_NAME) // record is added to group grsf-sdg-flag if sdg Flag is true
	private Boolean sdgFlag;

	@JsonProperty(Constants.STATUS_OF_THE_GRSF_RECORD_JSON_KEY)
	@CustomField(key=Constants.STATUS_OF_THE_GRSF_RECORD_CUSTOM_KEY)
	private Status status;

	// automatically compiled
	@CustomField(key=Constants.SYSTEM_TYPE_CUSTOM_KEY)
	private String systemType;

	@JsonProperty(Constants.CATCHES_JSON_KEY)
	@CustomField(key=Constants.CATCHES_CUSTOM_KEY)
	@TimeSeries
	@Group(groupNameOverValue=Constants.CATCHES_CUSTOM_KEY, prependSourceToGroupName=false)
	@Valid
	private List<TimeSeriesBean<String, String>> catches;

	@JsonProperty(Constants.LANDINGS_JSON_KEY)
	@CustomField(key=Constants.LANDINGS_CUSTOM_KEY)
	@TimeSeries
	@Group(groupNameOverValue=Constants.LANDINGS_CUSTOM_KEY, prependSourceToGroupName=false)
	@Valid
	private List<TimeSeriesBean<String, String>> landings;

	@JsonProperty(Constants.SPECIES_JSON_KEY)
	@CustomField(key=Constants.SPECIES_CUSTOM_KEY)
	@Tag
	private List<String> species;

	@JsonProperty(Constants.SIMILAR_GRSF_RECORDS_JSON_KEY)
	@CustomField(key=Constants.SIMILAR_GRSF_RECORDS_CUSTOM_KEY)
	private List<SimilarRecordBean> similarGRSFRecords;

	@JsonProperty(Constants.SIMILAR_SOURCE_RECORDS_JSON_KEY)
	@CustomField(key=Constants.SIMILAR_SOURCE_RECORDS_CUSTOM_KEY)
	private List<SimilarRecordBean> similarSourceRecords;

	// automatically set
	@CustomField(key=Constants.DOMAIN_CUSTOM_KEY)
	private String domain;

	@JsonProperty(Constants.UUID_KB_JSON_KEY)
	@CustomField(key=Constants.UUID_KB_CUSTOM_KEY)
	@NotNull(message="grsf_uuid cannot be null")
	@Size(min=1, message="grsf_uuid cannot be empty")
	private String uuid;

	@JsonProperty(Constants.MANAGEMENT_ENTITIES_JSON_KEY)
	@CustomField(key=Constants.MANAGEMENT_ENTITIES_CUSTOM_KEY)
	private List<String> managementBodyAuthorities;

	@JsonProperty(Constants.SPATIAL_GEOJSON_JSON_KEY)
	@CustomField(key=Constants.SPATIAL_GEOJSON_CUSTOM_KEY)
	private String spatial;

	@JsonProperty(Constants.CONNECTED_JSON_KEY)
	@CustomField(key=Constants.CONNECTED_CUSTOM_KEY)
	private List<String> connectedBeans;

	@JsonProperty(Constants.ANNOTATION_PUBLISHER_JSON_KEY)
	@CustomField(key=Constants.ANNOTATION_PUBLISHER_CUSTOM_KEY)
	private List<AnnotationBean> annotations;
	
	@JsonProperty(Constants.SIMILARITIES_INDICATOR)
	@Tag
	private String similaritiesIndicator;
	
	@JsonProperty(Constants.CONNECTIONS_INDICATOR)
	@Tag
	private String connectionsIndicator;
	
	@JsonProperty(Constants.CITATION_JSON_KEY)
	@CustomField(key=Constants.CITATION_CUSTOM_KEY)
	private String citation;
	
	
	public Common() {
		super();
	}

	/**
	 * @param dataOwner
	 * @param databaseSources
	 * @param sourceOfInformation
	 * @param refersTo
	 * @param shortName
	 * @param traceabilityFlag
	 * @param status
	 * @param systemType
	 * @param catches
	 * @param landings
	 * @param species
	 * @param similarGRSFRecords
	 * @param similarSourceRecords
	 * @param domain
	 * @param uuid
	 * @param managementBodyAuthorities
	 * @param spatial
	 * @param connectedBeans
	 * @param annotations
	 */
	public Common(List<String> dataOwner,
			List<Resource<Sources>> databaseSources,
			List<Resource<String>> sourceOfInformation,
			List<RefersToBean> refersTo, String shortName,
			Boolean traceabilityFlag, Status status, String systemType,
			List<TimeSeriesBean<String, String>> catches,
			List<TimeSeriesBean<String, String>> landings,
			List<String> species, List<SimilarRecordBean> similarGRSFRecords,
			List<SimilarRecordBean> similarSourceRecords, String domain,
			String uuid, List<String> managementBodyAuthorities,
			String spatial, List<String> connectedBeans,
			List<AnnotationBean> annotations) {
		super();
		this.dataOwner = dataOwner;
		this.databaseSources = databaseSources;
		this.sourceOfInformation = sourceOfInformation;
		this.refersTo = refersTo;
		this.shortName = shortName;
		this.traceabilityFlag = traceabilityFlag;
		this.status = status;
		this.systemType = systemType;
		this.catches = catches;
		this.landings = landings;
		this.species = species;
		this.similarGRSFRecords = similarGRSFRecords;
		this.similarSourceRecords = similarSourceRecords;
		this.domain = domain;
		this.uuid = uuid;
		this.managementBodyAuthorities = managementBodyAuthorities;
		this.spatial = spatial;
		this.connectedBeans = connectedBeans;
		this.annotations = annotations;
	}

	public List<String> getManagementBodyAuthorities() {
		return managementBodyAuthorities;
	}

	public void setManagementBodyAuthorities(List<String> managementBodyAuthorities) {
		this.managementBodyAuthorities = managementBodyAuthorities;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public Boolean getTraceabilityFlag() {
		return traceabilityFlag;
	}

	public List<RefersToBean> getRefersTo() {
		return refersTo;
	}

	public void setRefersTo(List<RefersToBean> refersTo) {
		this.refersTo = refersTo;
	}
	public List<Resource<Sources>> getDatabaseSources() {
		return databaseSources;
	}

	public void setDatabaseSources(List<Resource<Sources>> databaseSources) {
		this.databaseSources = databaseSources;
	}

	public List<Resource<String>> getSourceOfInformation() {
		return sourceOfInformation;
	}

	public void setSourceOfInformation(List<Resource<String>> sourceOfInformation) {
		this.sourceOfInformation = sourceOfInformation;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Boolean isTraceabilityFlag() {
		return traceabilityFlag;
	}

	public void setTraceabilityFlag(Boolean traceabilityFlag) {
		this.traceabilityFlag = traceabilityFlag;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<String> getSpecies() {
		return species;
	}

	public void setSpecies(List<String> species) {
		this.species = species;
	}

	public List<TimeSeriesBean<String, String>> getCatches() {
		return catches;
	}

	public void setCatches(List<TimeSeriesBean<String, String>> catches) {
		this.catches = catches;
	}

	public List<TimeSeriesBean<String, String>> getLandings() {
		return landings;
	}

	public void setLandings(List<TimeSeriesBean<String, String>> landings) {
		this.landings = landings;
	}

	public List<String> getDataOwner() {
		return dataOwner;
	}

	public void setDataOwner(List<String> dataOwner) {
		this.dataOwner = dataOwner;
	}

	public List<SimilarRecordBean> getSimilarGRSFRecords() {
		return similarGRSFRecords;
	}

	public void setSimilarGRSFRecords(List<SimilarRecordBean> similarGRSFRecords) {
		this.similarGRSFRecords = similarGRSFRecords;
	}

	public List<SimilarRecordBean> getSimilarSourceRecords() {
		return similarSourceRecords;
	}

	public void setSimilarSourceRecords(List<SimilarRecordBean> similarSourceRecords) {
		this.similarSourceRecords = similarSourceRecords;
	}

	public String getSpatial() {
		return spatial;
	}

	public void setSpatial(String spatial) {
		this.spatial = spatial;
	}

	public List<String> getConnectedBeans() {
		return connectedBeans;
	}

	public void setConnectedBeans(List<String> connectedBeans) {
		this.connectedBeans = connectedBeans;
	}


	public List<AnnotationBean> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<AnnotationBean> annotations) {
		this.annotations = annotations;
	}

	public Boolean getSdgFlag() {
		return sdgFlag;
	}

	public void setSdgFlag(Boolean sdgFlag) {
		this.sdgFlag = sdgFlag;
	}

	public String getSimilaritiesIndicator() {
		return similaritiesIndicator;
	}

	public void setSimilaritiesIndicator(String similaritiesIndicator) {
		this.similaritiesIndicator = similaritiesIndicator;
	}

	public String getConnectionsIndicator() {
		return connectionsIndicator;
	}

	public void setConnectionsIndicator(String connectionsIndicator) {
		this.connectionsIndicator = connectionsIndicator;
	}

	public String getCitation() {
		return citation;
	}

	public void setCitation(String citation) {
		this.citation = citation;
	}

	@Override
	public String toString() {
		return "Common [dataOwner=" + dataOwner + ", databaseSources="
				+ databaseSources + ", sourceOfInformation="
				+ sourceOfInformation + ", refersTo=" + refersTo
				+ ", shortName=" + shortName + ", traceabilityFlag="
				+ traceabilityFlag + ", sdgFlag=" + sdgFlag + ", status="
				+ status + ", systemType=" + systemType + ", catches="
				+ catches + ", landings=" + landings + ", species=" + species
				+ ", similarGRSFRecords=" + similarGRSFRecords
				+ ", similarSourceRecords=" + similarSourceRecords
				+ ", domain=" + domain + ", uuid=" + uuid
				+ ", managementBodyAuthorities=" + managementBodyAuthorities
				+ ", spatial=" + spatial + ", connectedBeans=" + connectedBeans
				+ ", annotations=" + annotations + "]";
	}
}