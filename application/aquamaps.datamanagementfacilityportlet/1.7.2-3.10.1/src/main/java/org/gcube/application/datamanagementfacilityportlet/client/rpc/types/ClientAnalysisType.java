package org.gcube.application.datamanagementfacilityportlet.client.rpc.types;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum ClientAnalysisType implements IsSerializable {
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
