package org.gcube.data_catalogue.grsf_publish_ws.json.input;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CustomField;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Group;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Tag;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.TimeSeries;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Abundance_Level;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Exploitation_Rate;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Stock_Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A stock record bean
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockRecord extends Common{

	@JsonProperty("stock_name")
	@NotNull(message="stock_name cannot be null")
	@Size(min=2, message="stock_name cannot be empty")
	@CustomField(key="Stock Name")
	private String stockName;

	@JsonProperty("stock_id")
	@CustomField(key="Stock id")
	private String stockId;

	@JsonProperty("species")
	@CustomField(key="Species")
	@Tag
	private List<String> species;

	@JsonProperty("assessment_distribution_area")
	@CustomField(key="Assessment distribution area")
	@Tag
	private List<String> area;

	@JsonProperty("exploiting_fishery")
	@CustomField(key="Exploiting fishery")
	private List<String> exploitingFishery;

	@JsonProperty("management_entity")
	@CustomField(key="Management entity")
	@Tag
	private String managementEntity;

	@JsonProperty("assessment_methods")
	@CustomField(key="Assessment methods")
	private String assessmentMethods;

	@JsonProperty("state_of_marine_resource")
	@CustomField(key="State of marine resources")
	private String stateOfMarineResource;

	@JsonProperty("standard_exploitation_rate")
	@CustomField(key="Standard Exploitation Rate")
	//@Tag
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<Exploitation_Rate, Void>> exploitationRateStandard;

	@JsonProperty("exploitation_rate")
	@CustomField(key="Exploitation Rate")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, String>> exploitationRate;

	@JsonProperty("standard_abundance_level")
	@CustomField(key="Standard Abundance Level")
	//@Tag
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<Abundance_Level, Void>> abundanceLevelStandard;

	@JsonProperty("abundance_level")
	@CustomField(key="Abundance Level")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, String>> abundanceLevel;

	@JsonProperty("narrative_state_and_trend")
	@CustomField(key="Narrative state and trend")
	private String narrativeStateAndTrend;

	@JsonProperty("scientific_advice")
	@CustomField(key="Scientific advice")
	private String scientificAdvice;

	@JsonProperty("assessor")
	@CustomField(key="Assessor")
	@Tag
	private String assessor;

	@JsonProperty("stock_uri")
	@CustomField(key="Stock Uri")
	private String stockUri;

	@JsonProperty("water_area")
	@CustomField(key="Water Area")
	@Tag
	private List<String> waterArea;

	@JsonProperty("type")
	@CustomField(key="Type")
	//@Tag
	@Group
	private Stock_Type type;

	public StockRecord() {
		super();
	}

	/**
	 * Create a Stock element.
	 * @param stockName
	 * @param stockID
	 * @param species
	 * @param area
	 * @param exploitingFishery
	 * @param managementEntity
	 * @param assessmentMethods
	 * @param stateOfMarineResource
	 * @param exploitationRateStandard
	 * @param exploitationRate
	 * @param abundanceLevelStandard
	 * @param abundanceLevel
	 * @param narrativeStateAndTrend
	 * @param scientificAdvice
	 * @param reportingEntity
	 * @param reportingYear
	 * @param stockUri
	 * @param waterArea
	 * @param type
	 */
	public StockRecord(
			String stockName,
			String stockId,
			List<String> species,
			List<String> area,
			List<String> exploitingFishery,
			String managementEntity,
			String assessmentMethods,
			String stateOfMarineResource,
			List<TimeSeriesBean<Exploitation_Rate, Void>> exploitationRateStandard,
			List<TimeSeriesBean<String, String>> exploitationRate,
			List<TimeSeriesBean<Abundance_Level, Void>> abundanceLevelStandard,
			List<TimeSeriesBean<String, String>> abundanceLevel,
			String narrativeStateAndTrend, String scientificAdvice,
			String assessor, Long reportingYear, String stockUri,
			List<String> waterArea, Stock_Type type) {
		super();
		this.stockName = stockName;
		this.stockId = stockId;
		this.species = species;
		this.area = area;
		this.exploitingFishery = exploitingFishery;
		this.managementEntity = managementEntity;
		this.assessmentMethods = assessmentMethods;
		this.stateOfMarineResource = stateOfMarineResource;
		this.exploitationRateStandard = exploitationRateStandard;
		this.exploitationRate = exploitationRate;
		this.abundanceLevelStandard = abundanceLevelStandard;
		this.abundanceLevel = abundanceLevel;
		this.narrativeStateAndTrend = narrativeStateAndTrend;
		this.scientificAdvice = scientificAdvice;
		this.assessor = assessor;
		this.stockUri = stockUri;
		this.waterArea = waterArea;
		this.type = type;
	}

	public Stock_Type getType() {
		return type;
	}

	public void setType(Stock_Type type) {
		this.type = type;
	}

	public String getStockUri() {
		return stockUri;
	}

	public void setStockUri(String stockUri) {
		this.stockUri = stockUri;
	}

	public List<String> getWaterArea() {
		return waterArea;
	}

	public void setWaterArea(List<String> waterArea) {
		this.waterArea = waterArea;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public List<String> getSpecies() {
		return species;
	}

	public void setSpecies(List<String> species) {
		this.species = species;
	}

	public List<String> getArea() {
		return area;
	}

	public void setArea(List<String> area) {
		this.area = area;
	}

	public List<String> getExploitingFishery() {
		return exploitingFishery;
	}

	public void setExploitingFishery(List<String> exploitingFishery) {
		this.exploitingFishery = exploitingFishery;
	}

	public String getManagementEntity() {
		return managementEntity;
	}

	public void setManagementEntity(String managementEntity) {
		this.managementEntity = managementEntity;
	}

	public String getAssessmentMethods() {
		return assessmentMethods;
	}

	public void setAssessmentMethods(String assessmentMethods) {
		this.assessmentMethods = assessmentMethods;
	}

	public String getStateOfMarineResource() {
		return stateOfMarineResource;
	}

	public void setStateOfMarineResource(String stateOfMarineResource) {
		this.stateOfMarineResource = stateOfMarineResource;
	}

	public List<TimeSeriesBean<Exploitation_Rate, Void>> getExploitationRateStandard() {
		return exploitationRateStandard;
	}

	public void setExploitationRateStandard(
			List<TimeSeriesBean<Exploitation_Rate, Void>> exploitationRateStandard) {
		this.exploitationRateStandard = exploitationRateStandard;
	}

	public List<TimeSeriesBean<Abundance_Level, Void>> getAbundanceLevelStandard() {
		return abundanceLevelStandard;
	}

	public void setAbundanceLevelStandard(
			List<TimeSeriesBean<Abundance_Level, Void>> abundanceLevelStandard) {
		this.abundanceLevelStandard = abundanceLevelStandard;
	}

	public List<TimeSeriesBean<String, String>> getAbundanceLevel() {
		return abundanceLevel;
	}

	public void setAbundanceLevel(
			List<TimeSeriesBean<String, String>> abundanceLevel) {
		this.abundanceLevel = abundanceLevel;
	}

	public String getNarrativeStateAndTrend() {
		return narrativeStateAndTrend;
	}

	public void setNarrativeStateAndTrend(String narrativeStateAndTrend) {
		this.narrativeStateAndTrend = narrativeStateAndTrend;
	}

	public String getScientificAdvice() {
		return scientificAdvice;
	}

	public void setScientificAdvice(String scientificAdvice) {
		this.scientificAdvice = scientificAdvice;
	}

	public String getAssessor() {
		return assessor;
	}

	public void setAssessor(String assessor) {
		this.assessor = assessor;
	}

	public List<TimeSeriesBean<String, String>> getExploitationRate() {
		return exploitationRate;
	}

	public void setExploitationRate(
			List<TimeSeriesBean<String, String>> exploitationRate) {
		this.exploitationRate = exploitationRate;
	}
	
	@Override
	public String toString() {
		return "StockRecord [stockName=" + stockName + ", stockId=" + stockId
				+ ", species=" + species + ", area=" + area
				+ ", exploitingFishery=" + exploitingFishery
				+ ", managementEntity=" + managementEntity
				+ ", assessmentMethods=" + assessmentMethods
				+ ", stateOfMarineResource=" + stateOfMarineResource
				+ ", exploitationRateStandard=" + exploitationRateStandard
				+ ", exploitationRate=" + exploitationRate
				+ ", abundanceLevelStandard=" + abundanceLevelStandard
				+ ", abundanceLevel=" + abundanceLevel
				+ ", narrativeStateAndTrend=" + narrativeStateAndTrend
				+ ", scientificAdvice=" + scientificAdvice
				+ ", assessor=" + assessor + ", stockUri="
				+ stockUri + ", waterArea=" + waterArea + ", type=" + type
				+ "]";
	}

}
