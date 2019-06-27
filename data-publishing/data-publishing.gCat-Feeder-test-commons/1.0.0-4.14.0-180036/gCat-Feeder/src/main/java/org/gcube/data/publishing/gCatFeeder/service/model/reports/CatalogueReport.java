package org.gcube.data.publishing.gCatFeeder.service.model.reports;

import java.util.ArrayList;

import org.gcube.data.publishing.gCatFeeder.catalogues.model.PublishReport;

public class CatalogueReport{
	private GenericInfos genericInformations=new GenericInfos();
	private ArrayList<PublishReport> publishedRecords=new ArrayList<>();
	
	public GenericInfos getGenericInformations() {
		return genericInformations;
	}
	public void setGenericInformations(GenericInfos genericInformations) {
		this.genericInformations = genericInformations;
	}
	public ArrayList<PublishReport> getPublishedRecords() {
		return publishedRecords;
	}
	public void setPublishedRecords(ArrayList<PublishReport> publishedRecords) {
		this.publishedRecords = publishedRecords;
	}
	
	
	
}