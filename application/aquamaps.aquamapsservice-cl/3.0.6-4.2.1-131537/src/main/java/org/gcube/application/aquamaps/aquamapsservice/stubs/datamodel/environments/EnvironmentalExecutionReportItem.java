package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.DataModel;

public class EnvironmentalExecutionReportItem extends DataModel{

	private Double percent;
	private String resourceLoad;
	private String resourcesMap;
	private String elaboratedSpecies;
	
	public EnvironmentalExecutionReportItem() {
	}

	
	
	
	public EnvironmentalExecutionReportItem(Double percent,
			String resourceLoad, String resourcesMap, String elaboratedSpecies) {
		super();
		this.percent = percent;
		this.resourceLoad = resourceLoad;
		this.resourcesMap = resourcesMap;
		this.elaboratedSpecies = elaboratedSpecies;
	}




	public Double getPercent() {
		return percent;
	}

	public void setPercent(Double percent) {
		this.percent = percent;
	}

	public String getResourceLoad() {
		return resourceLoad;
	}

	public void setResourceLoad(String resourceLoad) {
		this.resourceLoad = resourceLoad;
	}

	public String getResourcesMap() {
		return resourcesMap;
	}

	public void setResourcesMap(String resourcesMap) {
		this.resourcesMap = resourcesMap;
	}

	public String getElaboratedSpecies() {
		return elaboratedSpecies;
	}

	public void setElaboratedSpecies(String elaboratedSpecies) {
		this.elaboratedSpecies = elaboratedSpecies;
	}
	
	
}
