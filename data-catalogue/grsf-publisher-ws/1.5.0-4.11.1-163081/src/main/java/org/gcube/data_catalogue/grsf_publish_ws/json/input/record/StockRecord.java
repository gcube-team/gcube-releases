package org.gcube.data_catalogue.grsf_publish_ws.json.input.record;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CustomField;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Group;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Tag;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.TimeSeries;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.TimeSeriesBean;
import org.gcube.datacatalogue.common.Constants;
import org.gcube.datacatalogue.common.enums.Abundance_Level;
import org.gcube.datacatalogue.common.enums.Fishing_Pressure;
import org.gcube.datacatalogue.common.enums.Stock_Type;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A stock record bean.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class StockRecord extends Common{

	@JsonProperty(Constants.STOCK_NAME_JSON_KEY)
	@NotNull(message=Constants.STOCK_NAME_JSON_KEY + " cannot be null")
	@Size(min=2, message=Constants.STOCK_NAME_JSON_KEY + " cannot be empty")
	@CustomField(key=Constants.STOCK_NAME_CUSTOM_KEY)
	private String stockName;

	@JsonProperty(Constants.GRSF_SEMANTIC_IDENTIFIER_JSON_KEY)
	@CustomField(key=Constants.GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY)
	private String stockId;

	@JsonProperty(Constants.ASSESSMENT_AREA_JSON_KEY)
	@CustomField(key=Constants.ASSESSMENT_AREA_CUSTOM_KEY)
	@Tag
	private List<String> area;

	@JsonProperty(Constants.EXPLOITING_FISHERY_JSON_KEY)
	@CustomField(key=Constants.EXPLOITING_FISHERY_CUSTOM_KEY)
	private List<String> exploitingFishery;

	@JsonProperty(Constants.ASSESSMENT_METHODS_JSON_KEY)
	@CustomField(key=Constants.ASSESSMENT_METHODS_CUSTOM_KEY)
	private List<String> assessmentMethods;

	@JsonProperty(Constants.FIRMS_ABUNDANCE_LEVEL_JSON_KEY)
	@CustomField(key=Constants.FIRMS_ABUNDANCE_LEVEL_CUSTOM_KEY)
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<Abundance_Level, Void>> abundanceLevelStandard;

	@JsonProperty(Constants.ABUNDANCE_LEVEL_JSON_KEY)
	@CustomField(key=Constants.ABUNDANCE_LEVEL_CUSTOM_KEY)
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, String>> abundanceLevel;

	@JsonProperty(Constants.FISHING_PRESSURE_FIRMS_JSON_KEY)
	@CustomField(key=Constants.FISHING_PRESSURE_FIRMS_CUSTOM_KEY)
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<Fishing_Pressure, Void>> fishingPressureStandard;

	@JsonProperty(Constants.FISHING_PRESSURE_JSON_KEY)
	@CustomField(key=Constants.FISHING_PRESSURE_CUSTOM_KEY)
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, String>> fishingPressure;

	@JsonProperty(Constants.STATE_AND_TREND_MARINE_RESOURCE_JSON_KEY)
	@CustomField(key=Constants.STATE_AND_TREND_MARINE_RESOURCE_CUSTOM_KEY)
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, Void>> narrativeState;

	@JsonProperty(Constants.FAO_CATEGORIES_JSON_KEY)
	@CustomField(key=Constants.FAO_CATEGORIES_CUSTOM_KEY)
	@TimeSeries
	@Valid
	private List<TimeSeriesBean<String, Void>> faoState;

	@JsonProperty(Constants.SCIENTIFIC_ADVICE_JSON_KEY)
	@CustomField(key=Constants.SCIENTIFIC_ADVICE_CUSTOM_KEY)
	private List<String> scientificAdvice;

	@JsonProperty(Constants.ASSESSOR_JSON_KEY)
	@CustomField(key=Constants.ASSESSOR_CUSTOM_KEY)
	private String assessor;

	@JsonProperty(Constants.GRSF_TYPE_JSON_KEY)
	@CustomField(key=Constants.GRSF_TYPE_CUSTOM_KEY)
	@Group
	@Tag
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
		this.stockId = stockId;//super.cleanSemanticId(stockId);	
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
