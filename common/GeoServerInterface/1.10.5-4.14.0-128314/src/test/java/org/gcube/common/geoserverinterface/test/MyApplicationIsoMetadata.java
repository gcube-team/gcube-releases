package org.gcube.common.geoserverinterface.test;

import java.net.URISyntaxException;

import org.gcube.common.geoserverinterface.bean.iso.GcubeISOMetadata;
import org.gcube.common.geoserverinterface.bean.iso.MissingInformationException;
import org.opengis.metadata.Metadata;

//Extends org.gcube.common.geoserverinterface.bean.iso.GcubeISOMetadata
public class MyApplicationIsoMetadata extends GcubeISOMetadata {


	//We want this citation to be automatically added to credits
	private static final String applicationCitation="....";


	//Our mandatory field
	private String mandatoryField=null;

	/**
	 * @return the mandatoryField
	 */
	public String getMandatoryField() {
		return mandatoryField;
	}


	/**
	 * @param mandatoryField the mandatoryField to set
	 */
	public void setMandatoryField(String mandatoryField) {
		this.mandatoryField = mandatoryField;
	}


	public MyApplicationIsoMetadata() throws Exception {
		//Let the super class initialize itself
		super();
		//Let's add our citation to credits
		this.getCredits().add(applicationCitation);
	}


	@Override
	protected void checkConstraints() throws MissingInformationException {
		// We let the super class to perform its checks before continuing
		super.checkConstraints();
		// Custom check against mandatoryField
		if(mandatoryField==null) throw new MissingInformationException();
	}


	@Override
	public Metadata getMetadata() throws URISyntaxException,
	MissingInformationException {
		Metadata toReturn=super.getMetadata();
		//We set our mandatory field to the metadata
		return toReturn;
	}
}
