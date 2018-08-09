package org.gcube.data_catalogue.grsf_publish_ws.json.input.record;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CustomField;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Group;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Tag;
import org.gcube.datacatalogue.common.Constants;
import org.gcube.datacatalogue.common.enums.Fishery_Type;
import org.gcube.datacatalogue.common.enums.Production_System_Type;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A fishery record bean.
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class FisheryRecord extends Common {

	
	@JsonProperty(Constants.FISHERY_NAME_JSON_KEY)
	@NotNull(message="fishery_name cannot be null")
	@Size(min=1, message="fishery_name cannot be empty")
	@CustomField(key=Constants.FISHERY_NAME_CUSTOM_KEY)
	private String fisheryName;

	@JsonProperty(Constants.GRSF_SEMANTIC_IDENTIFIER_JSON_KEY)
	@CustomField(key=Constants.GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY)
	private String fisheryId;

	@JsonProperty(Constants.FISHING_AREA_JSON_KEY)
	@CustomField(key=Constants.FISHING_AREA_CUSTOM_KEY)
	@Tag
	private List<String> fishingArea;

	@JsonProperty(Constants.RESOURCES_EXPLOITED_JSON_KEY)
	@CustomField(key=Constants.RESOURCES_EXPLOITED_CUSTOM_KEY)
	private List<String> resourcesExploited;

	@JsonProperty(Constants.JURISDICTION_AREA_JSON_KEY)
	@CustomField(key=Constants.JURISDICTION_AREA_CUSTOM_KEY)
	private List<String> jurisdictionArea;

	@JsonProperty(Constants.PRODUCTION_SYSTEM_TYPE_JSON_KEY)
	@CustomField(key=Constants.PRODUCTION_SYSTEM_TYPE_CUSTOM_KEY)
	private List<Production_System_Type> productionSystemType;

	@JsonProperty(Constants.FLAG_STATE_JSON_KEY)
	@CustomField(key=Constants.FLAG_STATE_CUSTOM_KEY)
	@Tag
	private List<String> flagState;

	@JsonProperty(Constants.FISHING_GEAR_JSON_KEY)
	@CustomField(key=Constants.FISHING_GEAR_CUSTOM_KEY)
	@Tag
	private List<String> fishingGear;

	@JsonProperty(Constants.GRSF_TYPE_JSON_KEY)
	@CustomField(key=Constants.GRSF_TYPE_CUSTOM_KEY)
	@Group
	@Tag
	private Fishery_Type type;

	public FisheryRecord() {
		super();
	}

	/**
	 * @param fisheryName
	 * @param fisheryId
	 * @param fishingArea
	 * @param resourcesExploited
	 * @param managementBodyAuthorities
	 * @param jurisdictionArea
	 * @param productionSystemType
	 * @param flagState
	 * @param fishingGear
	 * @param type
	 */
	public FisheryRecord(String fisheryName, String fisheryId,
			List<String> fishingArea, List<String> resourcesExploited,
			List<String> jurisdictionArea,
			List<Production_System_Type> productionSystemType,
			List<String> flagState, List<String> fishingGear, Fishery_Type type) {
		super();
		this.fisheryName = fisheryName;
		this.fisheryId = fisheryId;
		this.fishingArea = fishingArea;
		this.resourcesExploited = resourcesExploited;
		this.jurisdictionArea = jurisdictionArea;
		this.productionSystemType = productionSystemType;
		this.flagState = flagState;
		this.fishingGear = fishingGear;
		this.type = type;
	}

	public String getFisheryName() {
		return fisheryName;
	}

	public void setFisheryName(String fisheryName) {
		this.fisheryName = fisheryName;
	}

	public String getFisheryId() {
		return fisheryId;
	}

	public void setFisheryId(String fisheryId) {
		this.fisheryId = fisheryId;//super.cleanSemanticId(fisheryId);
	}

	public List<String> getFishingArea() {
		return fishingArea;
	}

	public void setFishingArea(List<String> fishingArea) {
		this.fishingArea = fishingArea;
	}

	public List<String> getJurisdictionArea() {
		return jurisdictionArea;
	}

	public void setJurisdictionArea(List<String> jurisdictionArea) {
		this.jurisdictionArea = jurisdictionArea;
	}

	public Fishery_Type getType() {
		return type;
	}

	public void setType(Fishery_Type type) {
		this.type = type;
	}

	public List<String> getResourcesExploited() {
		return resourcesExploited;
	}

	public void setResourcesExploited(List<String> resourcesExploited) {
		this.resourcesExploited = resourcesExploited;
	}

	public List<Production_System_Type> getProductionSystemType() {
		return productionSystemType;
	}

	public void setProductionSystemType(
			List<Production_System_Type> productionSystemType) {
		this.productionSystemType = productionSystemType;
	}

	public List<String> getFlagState() {
		return flagState;
	}

	public void setFlagState(List<String> flagState) {
		this.flagState = flagState;
	}

	public List<String> getFishingGear() {
		return fishingGear;
	}

	public void setFishingGear(List<String> fishingGear) {
		this.fishingGear = fishingGear;
	}

	@Override
	public String toString() {
		return "FisheryRecord [fisheryName=" + fisheryName + ", fisheryId="
				+ fisheryId + ", fishingArea=" + fishingArea
				+ ", resourcesExploited=" + resourcesExploited
				+ ", jurisdictionArea=" + jurisdictionArea
				+ ", productionSystemType=" + productionSystemType
				+ ", flagState=" + flagState + ", fishingGear=" + fishingGear
				+ ", type=" + type + "]";
	}

}