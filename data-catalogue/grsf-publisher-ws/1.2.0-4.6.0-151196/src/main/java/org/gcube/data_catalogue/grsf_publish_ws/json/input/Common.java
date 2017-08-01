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

	public static final String GRSF_TYPE_KEY = "GRSF type"; // stock, fishery
	public static final String SOURCE_KEY = "Source"; // in case it is a RAM/FIRMS/FishSource record it is not added

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

	@JsonProperty("short_title")
	@CustomField(key="Short Title")
	private String shortTitle;

	@JsonProperty("traceability_flag")
	@CustomField(key="Traceability Flag")
	@Group(condition="true", groupNameOverValue="traceability-flag") // record is added to group traceability-flag if Traceability Flag is true
	private Boolean traceabilityFlag;

	@JsonProperty("status")
	@CustomField(key="Status")
	@Group
	private Status status;

	@JsonProperty("reporting_year")
	@CustomField(key="Reporting year")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<Void, Void>> reportingYear;

	@JsonProperty("reference_year")
	@CustomField(key="Reference year")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<Void, Void>> referenceYear;

	// automatically compiled
	@JsonProperty("grsf_type")
	@CustomField(key=GRSF_TYPE_KEY)
	private String grsfType;

	// automatically compiled
	@JsonProperty("source")
	@CustomField(key=SOURCE_KEY)
	private String sourceType;

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

	@JsonProperty("similar_records")
	@CustomField(key="Similar Records")
	private List<SimilarRecordBean> similarRecords;

	public Common() {
		super();
	}

	/**
	 * @param dataOwner
	 * @param databaseSources
	 * @param sourceOfInformation
	 * @param refersTo
	 * @param shortTitle
	 * @param traceabilityFlag
	 * @param status
	 * @param reportingYear
	 * @param referenceYear
	 * @param grsfType
	 * @param sourceType
	 * @param catches
	 * @param landings
	 * @param species
	 * @param similarRecords
	 */
	public Common(List<String> dataOwner,
			List<Resource<Sources>> databaseSources,
			List<Resource<String>> sourceOfInformation,
			List<RefersToBean> refersTo, String shortTitle,
			Boolean traceabilityFlag, Status status,
			List<TimeSeriesBean<Void, Void>> reportingYear,
			List<TimeSeriesBean<Void, Void>> referenceYear, String grsfType,
			String sourceType, List<TimeSeriesBean<String, String>> catches,
			List<TimeSeriesBean<String, String>> landings,
			List<String> species, List<SimilarRecordBean> similarRecords) {
		super();
		this.dataOwner = dataOwner;
		this.databaseSources = databaseSources;
		this.sourceOfInformation = sourceOfInformation;
		this.refersTo = refersTo;
		this.shortTitle = shortTitle;
		this.traceabilityFlag = traceabilityFlag;
		this.status = status;
		this.reportingYear = reportingYear;
		this.referenceYear = referenceYear;
		this.grsfType = grsfType;
		this.sourceType = sourceType;
		this.catches = catches;
		this.landings = landings;
		this.species = species;
		this.similarRecords = similarRecords;
	}

	public String getGrsfType() {
		return grsfType;
	}

	public void setGrsfType(String grsfType) {
		this.grsfType = grsfType;
	}

	public String getSourceType() {
		return sourceType;	
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
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

	public List<TimeSeriesBean<Void, Void>> getReportingYear() {
		return reportingYear;
	}

	public void setReportingYear(List<TimeSeriesBean<Void, Void>> reportingYear) {
		this.reportingYear = reportingYear;
	}

	public void setReferenceYear(List<TimeSeriesBean<Void, Void>> referenceYear) {
		this.referenceYear = referenceYear;
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

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
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

	public List<TimeSeriesBean<Void, Void>> getReferenceYear() {
		return referenceYear;
	}

	public List<SimilarRecordBean> getSimilarRecords() {
		return similarRecords;
	}

	public void setSimilarRecords(List<SimilarRecordBean> similarRecords) {
		this.similarRecords = similarRecords;
	}

	@Override
	public String toString() {
		return "Common ["
				+ (dataOwner != null ? "dataOwner=" + dataOwner + ", " : "")
				+ (databaseSources != null ? "databaseSources="
						+ databaseSources + ", " : "")
						+ (sourceOfInformation != null ? "sourceOfInformation="
								+ sourceOfInformation + ", " : "")
								+ (refersTo != null ? "refersTo=" + refersTo + ", " : "")
								+ (shortTitle != null ? "shortTitle=" + shortTitle + ", " : "")
								+ (traceabilityFlag != null ? "traceabilityFlag="
										+ traceabilityFlag + ", " : "")
										+ (status != null ? "status=" + status + ", " : "")
										+ (reportingYear != null ? "reportingYear=" + reportingYear
												+ ", " : "")
												+ (referenceYear != null ? "referenceYear=" + referenceYear
														+ ", " : "")
														+ (grsfType != null ? "grsfType=" + grsfType + ", " : "")
														+ (sourceType != null ? "sourceType=" + sourceType + ", " : "")
														+ (catches != null ? "catches=" + catches + ", " : "")
														+ (landings != null ? "landings=" + landings + ", " : "")
														+ (species != null ? "species=" + species + ", " : "")
														+ (similarRecords != null ? "similarRecords=" + similarRecords
																: "") + "]";
	}

}