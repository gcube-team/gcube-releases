package org.gcube.dataanalysis.geo.test;

import org.gcube.dataanalysis.geo.meta.NetCDFMetadata;

public class TestNetCDFMetadataInsert {
	
	public static void main(String[] args) throws Exception{
		NetCDFMetadata metadataInserter = new NetCDFMetadata();
		metadataInserter.setGeonetworkUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geonetwork/");
		metadataInserter.setGeonetworkUser("admin");
		metadataInserter.setGeonetworkPwd("admin");
		metadataInserter.setThreddsCatalogUrl("http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
		metadataInserter.setLayerUrl("http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/04091217_ruc.nc");
		metadataInserter.setTitle("temperature (04091217ruc.nc)");
		metadataInserter.setLayerName("T");
		metadataInserter.setSourceFileName("04091217_ruc.nc");
		metadataInserter.setAbstractField("T: temperature (degK) from 04091217ruc.nc resident on a THREDDS instance");
		metadataInserter.setResolution(0.5);
		metadataInserter.setXLeftLow(-180);
		metadataInserter.setYLeftLow(-85.5);
		metadataInserter.setXRightUpper(180);
		metadataInserter.setYRightUpper(85.5);
		
		metadataInserter.insertMetaData();
	}
}
