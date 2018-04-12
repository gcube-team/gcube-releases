package org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SettingsDescriptor implements IsSerializable {

	private int speciesInBasket=0;
	private int biodiversityObjectsCount=0;
	private int speciesDistributionObjectCount=0;
	private int hspecId=0;
	private String hspecTitle="";
	private String toSubmitName=null;
	private int numberOfCustomizedSpecies=0;
	private int selectedAreas=0;
	private boolean createGroup=false;
	
	private Msg submittable=new Msg(false,"INIT");
	
	public SettingsDescriptor() {
		// TODO Auto-generated constructor stub
	}

	public int getSpeciesInBasket() {
		return speciesInBasket;
	}

	public void setSpeciesInBasket(int speciesInBasket) {
		this.speciesInBasket = speciesInBasket;
	}

	public int getBiodiversityObjectsCount() {
		return biodiversityObjectsCount;
	}

	public void setBiodiversityObjectsCount(int biodiversityObjectsCount) {
		this.biodiversityObjectsCount = biodiversityObjectsCount;
	}

	public int getSpeciesDistributionObjectCount() {
		return speciesDistributionObjectCount;
	}

	public void setSpeciesDistributionObjectCount(int speciesDistributionObjectCount) {
		this.speciesDistributionObjectCount = speciesDistributionObjectCount;
	}


	public int getHspecId() {
		return hspecId;
	}

	public void setHspecId(int hspecId) {
		this.hspecId = hspecId;
	}

	public String getHspecTitle() {
		return hspecTitle;
	}

	public void setHspecTitle(String hspecTitle) {
		this.hspecTitle = hspecTitle;
	}

	public String getToSubmitName() {
		return toSubmitName;
	}

	public void setToSubmitName(String toSubmitName) {
		this.toSubmitName = toSubmitName;
	}

	public int getNumberOfCustomizedSpecies() {
		return numberOfCustomizedSpecies;
	}

	public void setNumberOfCustomizedSpecies(int numberOfCustomizedSpecies) {
		this.numberOfCustomizedSpecies = numberOfCustomizedSpecies;
	}

	public int getSelectedAreas() {
		return selectedAreas;
	}

	public void setSelectedAreas(int selectedAreas) {
		this.selectedAreas = selectedAreas;
	}

	public boolean isCreateGroup() {
		return createGroup;
	}

	public void setCreateGroup(boolean createGroup) {
		this.createGroup = createGroup;
	}

	public Msg getSubmittable() {
		return submittable;
	}

	public void setSubmittable(Msg submittable) {
		this.submittable = submittable;
	}

	@Override
	public String toString() {
		return "SettingsDescriptor [speciesInBasket=" + speciesInBasket
				+ ", biodiversityObjectsCount=" + biodiversityObjectsCount
				+ ", speciesDistributionObjectCount="
				+ speciesDistributionObjectCount + ", hspecId=" + hspecId
				+ ", hspecTitle=" + hspecTitle + ", toSubmitName="
				+ toSubmitName + ", numberOfCustomizedSpecies="
				+ numberOfCustomizedSpecies + ", selectedAreas="
				+ selectedAreas + ", createGroup=" + createGroup
				+ ", submittable=" + submittable + "]";
	}



}
