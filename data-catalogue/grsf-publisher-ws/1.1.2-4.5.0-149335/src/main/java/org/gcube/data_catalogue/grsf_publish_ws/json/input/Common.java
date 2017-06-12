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
	private String dataOwner;

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
	@Group(condition="true", groupNameOverValue="traceability-flag") // record is added to group if Traceability Flag is true
	private Boolean traceabilityFlag;

	@JsonProperty("status")
	@CustomField(key="Status")
	@Group
	//	@Tag
	private Status status;

	@JsonProperty("reporting_year")
	@CustomField(key="Reporting year")
	private Long reportingYear;

	// automatically compiled
	@JsonProperty("grsf_type")
	@CustomField(key=GRSF_TYPE_KEY)
	//@Tag
	//@Group
	private String grsfType;

	// automatically compiled
	@JsonProperty("source")
	@CustomField(key=SOURCE_KEY)
	private String sourceType;

	@JsonProperty("catches_or_landings")
	@CustomField(key="Catches or landings")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, String>> catchesOrLandings;

	@JsonProperty("species_name")
	@CustomField(key="Species Scientific Name")
	@Tag
	private List<String> speciesName;

	@JsonProperty("species_code")
	@CustomField(key="Species code")
	@Tag
	private List<String> speciesCode;

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
	 * @param grsfType
	 * @param sourceType
	 * @param catchesOrLandings
	 * @param speciesName
	 * @param speciesCode
	 */
	public Common(String dataOwner, List<Resource<Sources>> databaseSources,
			List<Resource<String>> sourceOfInformation,
			List<RefersToBean> refersTo, String shortTitle,
			Boolean traceabilityFlag, Status status, Long reportingYear,
			String grsfType, String sourceType,
			List<TimeSeriesBean<String, String>> catchesOrLandings,
			List<String> speciesName, List<String> speciesCode) {
		super();
		this.dataOwner = dataOwner;
		this.databaseSources = databaseSources;
		this.sourceOfInformation = sourceOfInformation;
		this.refersTo = refersTo;
		this.shortTitle = shortTitle;
		this.traceabilityFlag = traceabilityFlag;
		this.status = status;
		this.reportingYear = reportingYear;
		this.grsfType = grsfType;
		this.sourceType = sourceType;
		this.catchesOrLandings = catchesOrLandings;
		this.speciesName = speciesName;
		this.speciesCode = speciesCode;
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

	public Long getReportingYear() {
		return reportingYear;
	}

	public void setReportingYear(Long reportingYear) {
		this.reportingYear = reportingYear;
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

	public String getDataOwner() {
		return dataOwner;
	}

	public void setDataOwner(String dataOwner) {
		this.dataOwner = dataOwner;
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


	public List<TimeSeriesBean<String, String>> getCatchesOrLandings() {
		return catchesOrLandings;
	}

	public void setCatchesOrLandings(
			List<TimeSeriesBean<String, String>> catchesOrLandings) {
		this.catchesOrLandings = catchesOrLandings;
	}

	public List<String> getSpeciesName() {
		return speciesName;
	}

	public void setSpeciesName(List<String> speciesName) {
		this.speciesName = speciesName;
	}

	public List<String> getSpeciesCode() {
		return speciesCode;
	}

	public void setSpeciesCode(List<String> speciesCode) {
		this.speciesCode = speciesCode;
	}

	@Override
	public String toString() {
		return "Common [dataOwner=" + dataOwner + ", databaseSources="
				+ databaseSources + ", sourceOfInformation="
				+ sourceOfInformation + ", refersTo=" + refersTo
				+ ", shortTitle=" + shortTitle + ", traceabilityFlag="
				+ traceabilityFlag + ", status=" + status + ", reportingYear="
				+ reportingYear + ", grsfType=" + grsfType + ", sourceType="
				+ sourceType + ", catchesOrLandings=" + catchesOrLandings
				+ ", speciesName=" + speciesName + ", speciesCode="
				+ speciesCode + "]";
	}

}
