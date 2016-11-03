package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(namespace=aquamapsTypesNS)
@XmlEnum(String.class)
public enum AnalysisType {

	/**
	 * NB HSPEC-INVOLVING ANALYSIS REQUIRE FLOAT THRESHOLD PARAMETER
	 * 
	 * 
	 */
	
	
	
	HCAF, 						// OVERALL HCAF ANALYSIS	
	HSPEC,						// OVERALL HSPEC ANALYSIS 
	MIXED, 						// OVERALL HSPEC AND HCAF ANALYSIS
	GEOGRAPHIC_HCAF, 			// HCAF ANALYSIS BY AREA
	HSPEN, 						// HSPEN ANALYSIS ON FEATURE
	GEOGRAPHIC_HSPEC 			// HSPEC ANALYSIS BY AREA
	
}
