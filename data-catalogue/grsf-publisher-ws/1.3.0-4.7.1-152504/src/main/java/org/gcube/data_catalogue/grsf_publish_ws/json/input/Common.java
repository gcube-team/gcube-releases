package org.gcube.data_catalogue.grsf_publish_ws.json.input;

import java.util.List;

import javax.validation.Valid;

import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CkanResource;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CustomField;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Group;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Tag;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.TimeSeries;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Sources;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information that both Stock and Fishery records must contain.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@JsonIgnoreProperties(value = {"grsf_type", "source"})
public class Common extends Base{

	public static final String GRSF_DOMAIN_KEY = "GRSF Domain"; // stock, fishery
	public static final String GRSF_DATABASE_SOURCE = "Database Source";

	// it is added in case of GRSF record
	@JsonProperty("data_owner")
	@CustomField(key="Data owner")
	@Tag
	private List<String> dataOwner;

	@JsonProperty("database_sources")
	@CkanResource
	@Valid
	private List<Resource<Sources>> databaseSources;

	@JsonProperty("source_of_information")
	@CkanResource
	@Valid
	private List<Resource<String>> sourceOfInformation;

	@JsonProperty("refers_to")
	@Valid
	private List<RefersToBean> refersTo;

	@JsonProperty("short_name")
	@CustomField(key="Short Name")
	private String shortName;

	@JsonProperty("traceability_flag")
	@CustomField(key="Traceability Flag")
	@Group(condition="true", groupNameOverValue="traceability-flag") // record is added to group traceability-flag if Traceability Flag is true
	private Boolean traceabilityFlag;

	@JsonProperty("status_grsf_record")
	@CustomField(key="Status of the GRSF record")
	@Group
	private Status status;

	// automatically compiled
	@JsonProperty("grsf_domain")
	@CustomField(key=GRSF_DOMAIN_KEY)
	private String grsfType;

	@JsonProperty("catches")
	@CustomField(key="Catches")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, String>> catches;

	@JsonProperty("landings")
	@CustomField(key="Landings")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, String>> landings;

	@JsonProperty("species")
	@CustomField(key="Species")
	private List<String> species;

	@JsonProperty("similar_grsf_records")
	@CustomField(key="Similar GRSF Records")
	private List<SimilarRecordBean> similarGRSFRecords;
	
	@JsonProperty("similar_source_records")
	@CustomField(key="Similar Source Records")
	private List<SimilarRecordBean> similarSourceRecords;

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
	 * @param grsfType
	 * @param catches
	 * @param landings
	 * @param species
	 * @param similarGRSFRecords
	 * @param similarSourceRecords
	 */
	public Common(List<String> dataOwner,
			List<Resource<Sources>> databaseSources,
			List<Resource<String>> sourceOfInformation,
			List<RefersToBean> refersTo, String shortName,
			Boolean traceabilityFlag, Status status, String grsfType,
			List<TimeSeriesBean<String, String>> catches,
			List<TimeSeriesBean<String, String>> landings,
			List<String> species, List<SimilarRecordBean> similarGRSFRecords,
			List<SimilarRecordBean> similarSourceRecords) {
		super();
		this.dataOwner = dataOwner;
		this.databaseSources = databaseSources;
		this.sourceOfInformation = sourceOfInformation;
		this.refersTo = refersTo;
		this.shortName = shortName;
		this.traceabilityFlag = traceabilityFlag;
		this.status = status;
		this.grsfType = grsfType;
		this.catches = catches;
		this.landings = landings;
		this.species = species;
		this.similarGRSFRecords = similarGRSFRecords;
		this.similarSourceRecords = similarSourceRecords;
	}

	public String getGrsfType() {
		return grsfType;
	}

	public void setGrsfType(String grsfType) {
		this.grsfType = grsfType;
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

	@Override
	public String toString() {
		return "Common [dataOwner=" + dataOwner + ", databaseSources="
				+ databaseSources + ", sourceOfInformation="
				+ sourceOfInformation + ", refersTo=" + refersTo
				+ ", shortName=" + shortName + ", traceabilityFlag="
				+ traceabilityFlag + ", status=" + status + ", grsfType="
				+ grsfType + ", catches=" + catches + ", landings=" + landings
				+ ", species=" + species + ", similarGRSFRecords="
				+ similarGRSFRecords + ", similarSourceRecords="
				+ similarSourceRecords + "]";
	}
}