package org.gcube.data_catalogue.grsf_publish_ws.json.input;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CustomField;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Group;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Tag;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Fishery_Type;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Production_System_Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A  record bean
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FisheryRecord extends Common{

	@JsonProperty("fishery_name")
	@NotNull(message="fishery_name cannot be null")
	@Size(min=1, message="fishery_name cannot be empty")
	@CustomField(key="Fishery Name")
	private String fisheryName;

	@JsonProperty("grsf_semantic_identifier")
	@CustomField(key="GRSF Semantic identifier")
	private String fisheryId;

	@JsonProperty("fishing_area")
	@CustomField(key="Fishing area")
	private List<String> fishingArea;

	@JsonProperty("resources_exploited")
	@CustomField(key="Resources Exploited")
	private List<String> resourcesExploited;

	@JsonProperty("management_body_authorities")
	@CustomField(key="Management Body/Authority(ies)")
	private List<String> managementBodyAuthorities;

	@JsonProperty("jurisdiction_area")
	@CustomField(key="Jurisdiction Area")
	@Tag
	private List<String> jurisdictionArea;

	@JsonProperty("production_system_type")
	@CustomField(key="Type of Production System")
	@Tag
	private List<Production_System_Type> productionSystemType;

	@JsonProperty("flag_state")
	@CustomField(key="Flag State")
	@Tag
	private List<String> flagState;

	@JsonProperty("fishing_gear")
	@CustomField(key="Fishing gear")
	private List<String> fishingGear;

	@JsonProperty("grsf_type")
	@CustomField(key="GRSF Type")
	@Group
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
			List<String> managementBodyAuthorities,
			List<String> jurisdictionArea,
			List<Production_System_Type> productionSystemType,
			List<String> flagState, List<String> fishingGear, Fishery_Type type) {
		super();
		this.fisheryName = fisheryName;
		this.fisheryId = fisheryId;
		this.fishingArea = fishingArea;
		this.resourcesExploited = resourcesExploited;
		this.managementBodyAuthorities = managementBodyAuthorities;
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
		this.fisheryId = fisheryId;
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

	public List<String> getManagementBodyAuthorities() {
		return managementBodyAuthorities;
	}

	public void setManagementBodyAuthorities(List<String> managementBodyAuthorities) {
		this.managementBodyAuthorities = managementBodyAuthorities;
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
				+ ", managementBodyAuthorities=" + managementBodyAuthorities
				+ ", jurisdictionArea=" + jurisdictionArea
				+ ", productionSystemType=" + productionSystemType
				+ ", flagState=" + flagState + ", fishingGear=" + fishingGear
				+ ", type=" + type + "]";
	}

}