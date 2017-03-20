/**
 *
 */
package org.gcube.spatial.data.geoutility;

import org.gcube.spatial.data.geoutility.wms.WmsUrlValidator;
import org.junit.Test;



/**
 * The Class GeoJUnitTest.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 22, 2016
 */
public class GeoJUnitTest {

	/**
	 * Test get styles.
	 */
	@Test
	public void testGetStyles() {
//		String wmsRequest = "http://repoigg.services.iit.cnr.it:8080/geoserver/IGG/ows?service=wms&version=1.1.0&request=GetMap&layers==IGG:area_temp_1000&width=676&height=330&srs=EPSG:4326&crs=EPSG:4326&format=application/openlayers&bbox=-85.5,-180.0,90.0,180.0";
		String wmsRequest = "http://thredds-d-d4s.d4science.org/thredds/wms/public/netcdf/test20.nc?service=wms&version=1.3.0&request=GetMap&layers=analyzed_field&styles=&width=640&height=480&srs=EPSG:4326&CRS=EPSG:4326&format=image/png&COLORSCALERANGE=auto&bbox=-85.0,-180.0,85.0,180.0";
//		String wmsRequest = "http://www.fao.org/figis/geoserver/species?SERVICE=WMS&BBOX=-176.0,-90.0,180.0,90&styles=&layers=layerName&FORMAT=image/gif";
		GeoNcWMSMetadataUtility geo;
		try {
			geo = new GeoNcWMSMetadataUtility(wmsRequest);
			System.out.println("Returned styles: "+geo.loadStyles());
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/**
	 * Test get styles.
	 */
	@Test
	public void testLoadZAxis() {
//		String wmsRequest = "http://repoigg.services.iit.cnr.it:8080/geoserver/IGG/ows?service=wms&version=1.1.0&request=GetMap&layers==IGG:area_temp_1000&width=676&height=330&srs=EPSG:4326&crs=EPSG:4326&format=application/openlayers&bbox=-85.5,-180.0,90.0,180.0";
		String wmsRequest = "http://thredds-d-d4s.d4science.org/thredds/wms/public/netcdf/test20.nc?service=wms&version=1.3.0&request=GetMap&layers=analyzed_field&styles=&width=640&height=480&srs=EPSG:4326&CRS=EPSG:4326&format=image/png&COLORSCALERANGE=auto&bbox=-85.0,-180.0,85.0,180.0";
//		String wmsRequest = "http://www.fao.org/figis/geoserver/species?SERVICE=WMS&BBOX=-176.0,-90.0,180.0,90&styles=&layers=layerName&FORMAT=image/gif";
		GeoNcWMSMetadataUtility geo;
		try {
			geo = new GeoNcWMSMetadataUtility(wmsRequest);
			System.out.println("Returned Z-Axis: "+geo.loadZAxis());
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Test wms url validator.
	 */
	@Test
	public void testWMSUrlValidator() {
		String wmsRequest = "http://thredds-d-d4s.d4science.org/thredds/wms/public/netcdf/test20.nc?service=wms&version=1.3.0&request=GetMap&layers=analyzed_field&styles=&width=640&height=480&srs=EPSG:4326&CRS=EPSG:4326&format=image/png&COLORSCALERANGE=auto&bbox=-85.0,-180.0,85.0,180.0";
		WmsUrlValidator wms;
		try {
			wms = new WmsUrlValidator(wmsRequest);
			System.out.println("Returned wms: "+wms.toString());
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
