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
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Fishing_Pressure;
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

	@JsonProperty("assessment_distribution_area")
	@CustomField(key="Assessment distribution area")
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
	private List<String> assessmentMethods;

	@JsonProperty("state_of_marine_resource")
	@CustomField(key="State of marine resources")
	private String stateOfMarineResource;

	@JsonProperty("standard_abundance_level")
	@CustomField(key="Standard Abundance Level")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<Abundance_Level, Void>> abundanceLevelStandard;

	@JsonProperty("abundance_level")
	@CustomField(key="Abundance Level")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, String>> abundanceLevel;

	@JsonProperty("standard_fishing_pressure")
	@CustomField(key="Standard Fishing Pressure")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<Fishing_Pressure, Void>> fishingPressureStandard;

	@JsonProperty("fishing_pressure")
	@CustomField(key="Fishing Pressure")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, String>> fishingPressure;

	@JsonProperty("narrative_state")
	@CustomField(key="Narrative state")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, Void>> narrativeState;

	@JsonProperty("fao_state")
	@CustomField(key="Fao State")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, Void>> faoState;

	@JsonProperty("scientific_advice")
	@CustomField(key="Scientific advice")
	private List<String> scientificAdvice;

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
	@Group
	private Stock_Type type;

	public StockRecord() {
		super();
	}

	/**
	 * @param stockName
	 * @param stockId
	 * @param area
	 * @param exploitingFishery
	 * @param managementEntity
	 * @param assessmentMethods
	 * @param stateOfMarineResource
	 * @param abundanceLevelStandard
	 * @param abundanceLevel
	 * @param fishingPressureStandard
	 * @param fishingPressure
	 * @param narrativeState
	 * @param faoState
	 * @param scientificAdvice
	 * @param assessor
	 * @param stockUri
	 * @param waterArea
	 * @param type
	 */
	public StockRecord(
			String stockName,
			String stockId,
			List<String> area,
			List<String> exploitingFishery,
			String managementEntity,
			List<String> assessmentMethods,
			String stateOfMarineResource,
			List<TimeSeriesBean<Abundance_Level, Void>> abundanceLevelStandard,
			List<TimeSeriesBean<String, String>> abundanceLevel,
			List<TimeSeriesBean<Fishing_Pressure, Void>> fishingPressureStandard,
			List<TimeSeriesBean<String, String>> fishingPressure,
			List<TimeSeriesBean<String, Void>> narrativeState,
			List<TimeSeriesBean<String, Void>> faoState,
			List<String> scientificAdvice, String assessor, String stockUri,
			List<String> waterArea, Stock_Type type) {
		super();
		this.stockName = stockName;
		this.stockId = stockId;
		this.area = area;
		this.exploitingFishery = exploitingFishery;
		this.managementEntity = managementEntity;
		this.assessmentMethods = assessmentMethods;
		this.stateOfMarineResource = stateOfMarineResource;
		this.abundanceLevelStandard = abundanceLevelStandard;
		this.abundanceLevel = abundanceLevel;
		this.fishingPressureStandard = fishingPressureStandard;
		this.fishingPressure = fishingPressure;
		this.narrativeState = narrativeState;
		this.faoState = faoState;
		this.scientificAdvice = scientificAdvice;
		this.assessor = assessor;
		this.stockUri = stockUri;
		this.waterArea = waterArea;
		this.type = type;
	}

	public void setAssessmentMethods(List<String> assessmentMethods) {
		this.assessmentMethods = assessmentMethods;
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

	public List<String> getArea() {
		return area;
	}

	public List<TimeSeriesBean<Fishing_Pressure, Void>> getFishingPressureStandard() {
		return fishingPressureStandard;
	}

	public void setFishingPressureStandard(
			List<TimeSeriesBean<Fishing_Pressure, Void>> fishingPressureStandard) {
		this.fishingPressureStandard = fishingPressureStandard;
	}

	public List<TimeSeriesBean<String, String>> getFishingPressure() {
		return fishingPressure;
	}

	public void setFishingPressure(
			List<TimeSeriesBean<String, String>> fishingPressure) {
		this.fishingPressure = fishingPressure;
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

	public String getStateOfMarineResource() {
		return stateOfMarineResource;
	}

	public void setStateOfMarineResource(String stateOfMarineResource) {
		this.stateOfMarineResource = stateOfMarineResource;
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

	public List<TimeSeriesBean<String, Void>> getNarrativeState() {
		return narrativeState;
	}

	public void setNarrativeState(List<TimeSeriesBean<String, Void>> narrativeState) {
		this.narrativeState = narrativeState;
	}

	public List<String> getScientificAdvice() {
		return scientificAdvice;
	}

	public void setScientificAdvice(List<String> scientificAdvice) {
		this.scientificAdvice = scientificAdvice;
	}

	public String getAssessor() {
		return assessor;
	}

	public void setAssessor(String assessor) {
		this.assessor = assessor;
	}

	public List<TimeSeriesBean<String, Void>> getFaoState() {
		return faoState;
	}

	public void setFaoState(List<TimeSeriesBean<String, Void>> faoState) {
		this.faoState = faoState;
	}

	public List<String> getAssessmentMethods() {
		return assessmentMethods;
	}

	@Override
	public String toString() {
		return "StockRecord [stockName=" + stockName + ", stockId=" + stockId
				+ ", area=" + area + ", exploitingFishery=" + exploitingFishery
				+ ", managementEntity=" + managementEntity
				+ ", assessmentMethods=" + assessmentMethods
				+ ", stateOfMarineResource=" + stateOfMarineResource
				+ ", abundanceLevelStandard=" + abundanceLevelStandard
				+ ", abundanceLevel=" + abundanceLevel
				+ ", fishingPressureStandard=" + fishingPressureStandard
				+ ", fishingPressure=" + fishingPressure + ", narrativeState="
				+ narrativeState + ", scientificAdvice=" + scientificAdvice
				+ ", assessor=" + assessor + ", stockUri=" + stockUri
				+ ", waterArea=" + waterArea + ", type=" + type + ", faoState="
				+ faoState + "]";
	}

}
