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

	@JsonProperty("grsf_semantic_identifier")
	@CustomField(key="GRSF Semantic identifier")
	private String stockId;

	@JsonProperty("assessment_area")
	@CustomField(key="Assessment Area")
	private List<String> area;

	@JsonProperty("exploiting_fishery")
	@CustomField(key="Exploiting Fishery")
	private List<String> exploitingFishery;

	@JsonProperty("management_body_authorities")
	@CustomField(key="Management Body/Authority(ies)")
	@Tag
	private String managementEntity;

	@JsonProperty("assessment_methods")
	@CustomField(key="Assessment Methods")
	private List<String> assessmentMethods;

	@JsonProperty("firms_standard_abundance_level")
	@CustomField(key="Abundance Level (FIRMS Standard)")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<Abundance_Level, Void>> abundanceLevelStandard;

	@JsonProperty("abundance_level")
	@CustomField(key="Abundance Level")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, String>> abundanceLevel;

	@JsonProperty("firms_standard_fishing_pressure")
	@CustomField(key="Fishing Pressure (FIRMS Standard)")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<Fishing_Pressure, Void>> fishingPressureStandard;

	@JsonProperty("fishing_pressure")
	@CustomField(key="Fishing Pressure")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, String>> fishingPressure;

	@JsonProperty("state_and_trend_of_marine_resources")
	@CustomField(key="State and trend of Marine Resource")
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, Void>> narrativeState;

	@JsonProperty("fao_categories")
	@CustomField(key="Fao Categories")
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

	@JsonProperty("grsf_type")
	@CustomField(key="GRSF Type")
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
	 * @param abundanceLevelStandard
	 * @param abundanceLevel
	 * @param fishingPressureStandard
	 * @param fishingPressure
	 * @param narrativeState
	 * @param faoState
	 * @param scientificAdvice
	 * @param assessor
	 * @param type
	 */
	public StockRecord(
			String stockName,
			String stockId,
			List<String> area,
			List<String> exploitingFishery,
			String managementEntity,
			List<String> assessmentMethods,
			List<TimeSeriesBean<Abundance_Level, Void>> abundanceLevelStandard,
			List<TimeSeriesBean<String, String>> abundanceLevel,
			List<TimeSeriesBean<Fishing_Pressure, Void>> fishingPressureStandard,
			List<TimeSeriesBean<String, String>> fishingPressure,
			List<TimeSeriesBean<String, Void>> narrativeState,
			List<TimeSeriesBean<String, Void>> faoState,
			List<String> scientificAdvice, String assessor, Stock_Type type) {
		super();
		this.stockName = stockName;
		this.stockId = stockId;
		this.area = area;
		this.exploitingFishery = exploitingFishery;
		this.managementEntity = managementEntity;
		this.assessmentMethods = assessmentMethods;
		this.abundanceLevelStandard = abundanceLevelStandard;
		this.abundanceLevel = abundanceLevel;
		this.fishingPressureStandard = fishingPressureStandard;
		this.fishingPressure = fishingPressure;
		this.narrativeState = narrativeState;
		this.faoState = faoState;
		this.scientificAdvice = scientificAdvice;
		this.assessor = assessor;
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
				+ ", abundanceLevelStandard=" + abundanceLevelStandard
				+ ", abundanceLevel=" + abundanceLevel
				+ ", fishingPressureStandard=" + fishingPressureStandard
				+ ", fishingPressure=" + fishingPressure + ", narrativeState="
				+ narrativeState + ", faoState=" + faoState
				+ ", scientificAdvice=" + scientificAdvice + ", assessor="
				+ assessor + ", type=" + type + "]";
	}

}
