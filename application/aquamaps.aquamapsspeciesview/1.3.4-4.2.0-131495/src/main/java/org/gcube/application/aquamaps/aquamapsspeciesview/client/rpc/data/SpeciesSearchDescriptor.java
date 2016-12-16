package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SpeciesSearchDescriptor implements IsSerializable{

	private String genericSearchFieldValue=null;
	private ArrayList<SpeciesFilter> advancedFilterList=new ArrayList<SpeciesFilter>();
	 
	
	public SpeciesSearchDescriptor() {
	}
	
	public SpeciesSearchDescriptor(String genericSearchFieldValue, List<SpeciesFilter> advancedFilters) {
		this.genericSearchFieldValue = genericSearchFieldValue;
		this.advancedFilterList.addAll(advancedFilters);
	}
	
	public void setGenericSearchFieldValue(String genericSearchFieldValue) {
		this.genericSearchFieldValue = genericSearchFieldValue;
	}
	
	public String getGenericSearchFieldValue() {
		return genericSearchFieldValue;
	}
	public ArrayList<SpeciesFilter> getAdvancedFilterList() {
		return advancedFilterList;
	}
	public void setAdvancedFilterList(
			ArrayList<SpeciesFilter> advancedFilterList) {
		this.advancedFilterList = advancedFilterList;
	}
}
