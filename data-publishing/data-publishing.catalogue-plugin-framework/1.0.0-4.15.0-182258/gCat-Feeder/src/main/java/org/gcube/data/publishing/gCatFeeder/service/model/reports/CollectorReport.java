package org.gcube.data.publishing.gCatFeeder.service.model.reports;

import java.util.ArrayList;

public class CollectorReport{
	private GenericInfos genericInformations=new GenericInfos();
	
	private String source;
	private long collectedItems;
			
	private ArrayList<CatalogueReport> publisherReports=new ArrayList<>();

	public GenericInfos getGenericInformations() {
		return genericInformations;
	}

	public void setGenericInformations(GenericInfos genericInformations) {
		this.genericInformations = genericInformations;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public long getCollectedItems() {
		return collectedItems;
	}

	public void setCollectedItems(long collectedItems) {
		this.collectedItems = collectedItems;
	}

	public ArrayList<CatalogueReport> getPublisherReports() {
		return publisherReports;
	}

	public void setPublisherReports(ArrayList<CatalogueReport> publisherReports) {
		this.publisherReports = publisherReports;
	}
	
	
	
}