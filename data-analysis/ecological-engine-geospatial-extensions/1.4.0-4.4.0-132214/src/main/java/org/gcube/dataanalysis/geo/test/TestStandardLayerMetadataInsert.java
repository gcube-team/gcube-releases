package org.gcube.dataanalysis.geo.test;

import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;

public class TestStandardLayerMetadataInsert {
	
	public static void main(String[] args) throws Exception{
		GenericLayerMetadata metadataInserter = new GenericLayerMetadata();
		
		metadataInserter.setGeonetworkUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geonetwork/");
//		metadataInserter.setGeonetworkUrl("http://geoserver.d4science-ii.research-infrastructures.eu/geonetwork/");
		metadataInserter.setGeonetworkUser("admin");
		metadataInserter.setGeonetworkPwd("admin");
		metadataInserter.setGeoserverUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		metadataInserter.setTitle("Biodiversity according to LME - Obis");
		metadataInserter.setLayerName("aquamaps:biodiversity_lme_geo");
		metadataInserter.setCategoryTypes("_BIOTA_");
		metadataInserter.setAbstractField("Biodiversity according to LME - Obis");
		metadataInserter.setCustomTopics("Obis","Large Marine Ecosystems");
		metadataInserter.setResolution(0.5);
		metadataInserter.setXLeftLow(-180);
		metadataInserter.setYLeftLow(-85.5);
		metadataInserter.setXRightUpper(180);
		metadataInserter.setYRightUpper(85.5);
		
		//metadataInserter.insertMetaData("");
	}
}
