package org.gcube.data.publishing.gCatFeeder.service.model.reports;

import java.util.ArrayList;

public class ExecutionReport {

	private GenericInfos genericInformations=new GenericInfos();
	
	private String startingScope;
	
	private ArrayList<CollectorReport> collectorReports=new ArrayList<>();

	public GenericInfos getGenericInformations() {
		return genericInformations;
	}

	public void setGenericInformations(GenericInfos genericInformations) {
		this.genericInformations = genericInformations;
	}

	public String getStartingScope() {
		return startingScope;
	}

	public void setStartingScope(String startingScope) {
		this.startingScope = startingScope;
	}

	public ArrayList<CollectorReport> getCollectorReports() {
		return collectorReports;
	}

	public void setCollectorReports(ArrayList<CollectorReport> collectorReports) {
		this.collectorReports = collectorReports;
	}
	
	
}
