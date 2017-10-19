package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SettingsDescriptor implements IsSerializable{

	private SpeciesSearchDescriptor speciesSearchDescriptor=new SpeciesSearchDescriptor();
	private Integer biodiversityCount=0;
	private Integer distributionCount=0;
	private Integer selectedSpeciesCount=0;
	
	private ClientResource selectedHspen=null;
	
	
	
	public SettingsDescriptor() {
		// TODO Auto-generated constructor stub
	}


	public SpeciesSearchDescriptor getSpeciesSearchDescriptor() {
		return speciesSearchDescriptor;
	}


	public void setSpeciesSearchDescriptor(
			SpeciesSearchDescriptor speciesSearchDescriptor) {
		this.speciesSearchDescriptor = speciesSearchDescriptor;
	}


	public Integer getBiodiversityCount() {
		return biodiversityCount;
	}


	public void setBiodiversityCount(Integer biodiversityCount) {
		this.biodiversityCount = biodiversityCount;
	}


	public Integer getDistributionCount() {
		return distributionCount;
	}


	public void setDistributionCount(Integer distributionCount) {
		this.distributionCount = distributionCount;
	}


	public Integer getSelectedSpeciesCount() {
		return selectedSpeciesCount;
	}


	public void setSelectedSpeciesCount(Integer selectedSpeciesCount) {
		this.selectedSpeciesCount = selectedSpeciesCount;
	}
	
	public ClientResource getSelectedHspen() {
		return selectedHspen;
	}
	
	public void setSelectedHspen(ClientResource selectedHspen) {
		this.selectedHspen = selectedHspen;
	}
}
