package org.gcube.data.transfer.plugins.sis;

public class TestGetCatalog {

	public static void main(String[] args) {
		String path="/home/gcube/tomcat/content/thredds/public/netcdf/myCatalog/CERSAT-GLO-CLIM_WIND_L4-OBS_FULL_TIME_SERIE_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_1366217956317.nc";
		String publicLocation="/home/gcube/tomcat/content/thredds/public/netcdf/";
		System.out.println(SisPlugin.getCatalogFromPath(path, publicLocation));
	}

}
