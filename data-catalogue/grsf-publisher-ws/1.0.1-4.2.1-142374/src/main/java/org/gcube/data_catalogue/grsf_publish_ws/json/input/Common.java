package org.gcube.data_catalogue.grsf_publish_ws.json.input;

import java.util.List;

import javax.validation.Valid;

import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CkanResource;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CustomField;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Group;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Tag;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Record_Type;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Sources;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information that both Stock and Fishery records must contain.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@JsonIgnoreProperties(value = {"product_type"})
public class Common extends Base{

	public static final String PRODUCT_TYPE_KEY = "Product type"; // stock, fishery
	public static final String RECORD_TYPE_KEY = "Record type"; // aggregated, source

	@JsonProperty("data_owner")
	@CustomField(key="Data owner")
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
	private Boolean traceabilityFlag;

	@JsonProperty("status")
	@CustomField(key="Status")
	@Group
	@Tag
	private Status status;

	@JsonProperty("reporting_year")
	@CustomField(key="Reporting year")
	private Long reportingYear;

	// automatically compiled
	@JsonProperty("product_type")
	@CustomField(key=PRODUCT_TYPE_KEY)
	@Tag
	@Group
	private String productType;

	// automatically compiled
	@JsonProperty("record_type")
	@CustomField(key=RECORD_TYPE_KEY)
	private Record_Type recordType;

	public Common() {
		super();
	}


	public Common(String dataOwner, List<Resource<Sources>> databaseSources,
			List<Resource<String>> sourceOfInformation,
			List<RefersToBean> refersTo, String shortTitle,
			Boolean traceabilityFlag, Status status, Long reportingYear,
			String productType, Record_Type recordType) {
		super();
		this.dataOwner = dataOwner;
		this.databaseSources = databaseSources;
		this.sourceOfInformation = sourceOfInformation;
		this.refersTo = refersTo;
		this.shortTitle = shortTitle;
		this.traceabilityFlag = traceabilityFlag;
		this.status = status;
		this.reportingYear = reportingYear;
		this.productType = productType;
		this.recordType = recordType;
	}

	public Record_Type getRecordType() {
		return recordType;
	}

	public void setRecordType(Record_Type recordType) {
		this.recordType = recordType;
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

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
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

	@Override
	public String toString() {
		return "Common [dataOwner=" + dataOwner + ", databaseSources="
				+ databaseSources + ", sourceOfInformation="
				+ sourceOfInformation + ", refersTo=" + refersTo
				+ ", shortTitle=" + shortTitle + ", traceabilityFlag="
				+ traceabilityFlag + ", status=" + status + ", reportingYear="
				+ reportingYear + ", productType=" + productType
				+ ", recordType=" + recordType + "]";
	}

}
