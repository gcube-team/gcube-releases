package org.gcube.application.perform.service.engine.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.gcube.application.perform.service.engine.model.importer.AnalysisType;

public class CSVExportRequest {

	private AnalysisType type;
	private Set<Long> farmIds=new HashSet<>();
	
	private Set<String> quarters=new HashSet<>();
	private Set<String> areas=new HashSet<>();
	private Set<String> speciesIds=new HashSet<>();
	private Set<String> periods=new HashSet<>();
	
	public Set<String> getAreas() {
		return areas;
	}
	public Set<String> getQuarters() {
		return quarters;
	}
	
	public Set<String> getPeriods() {
		return periods;
	}
	
	public Set<String> getSpeciesIds() {
		return speciesIds;
	}
	
	public CSVExportRequest(AnalysisType type) {
		super();
		this.type = type;
	}
	public AnalysisType getType() {
		return type;
	}
	public void setType(AnalysisType type) {
		this.type = type;
	}
	public Set<Long> getFarmIds() {
		return farmIds;
	}
	public CSVExportRequest setFarmIds(Set<Long> farmIds) {
		this.farmIds = farmIds;
		return this;
	}
	
	public CSVExportRequest addFarmId(Long farmid) {
		farmIds.add(farmid);
		return this;
	}
	
	public CSVExportRequest addAreas(Collection<String> toAdd) {
		areas.addAll(toAdd);
		return this;
	}
	
	public CSVExportRequest addQuarters(Collection<String> toAdd) {
		quarters.addAll(toAdd);
		return this;
	}
	
	public CSVExportRequest addSpecies(Collection<String> toAdd) {
		speciesIds.addAll(toAdd);
		return this;
	}


	public CSVExportRequest addPeriods(Collection<String> toAdd) {
		periods.addAll(toAdd);
		return this;
	}
	@Override
	public String toString() {
		return "CSVExportRequest [type=" + type + ", farmIds=" + farmIds + ", quarters=" + quarters + ", areas=" + areas
				+ ", speciesIds=" + speciesIds + ", periods=" + periods + "]";
	}
	
	
	
}
