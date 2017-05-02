package org.gcube.spatial.data.geonetwork.test;

public class OGCFormatter {
	public static String getWfsUrl(String geoServerUrl, String layerName, String bbox, int limit, String format) {
		return geoServerUrl + "/wfs?service=wfs&version=1.1.0&REQUEST=GetFeature" + "&TYPENAME=" + layerName + "&BBOX=" + bbox + (limit == 0 ? "" : "&MAXFEATURES=" + limit) + (format == null ? "" : "&OUTPUTFORMAT=" + format);
	}

	public static String getWmsUrl(String geoServerUrl, String layerName, String style, String bbox) {
		return geoServerUrl + "/wms?service=wms&version=1.1.0" + "&request=GetMap&layers=" + layerName + "&styles=" + (style == null ? "" : style) + "&bbox=" + bbox + "&width=676&height=330&srs=EPSG:4326&format=application/openlayers";
	}

	public static String getWcsUrl(String geoServerUrl, String layerName, String bbox) {
		return geoServerUrl + "/wcs?service=wcs&version=1.0.0" + "&request=GetCoverage&coverage=" + layerName + "&CRS=EPSG:4326" + "&bbox=" + bbox + "&width=676&height=330&format=geotiff";
	}

	public static String getWmsNetCDFUrl(String fileUrl, String layerName, String bbox) {
		return fileUrl.replace("dodsC", "wms") + "?service=wms&version=1.3.0" + "&request=GetMap&layers=" + layerName + "&bbox=" + bbox + "&styles=&width=676&height=330&srs=EPSG:4326&CRS=EPSG:4326&format=image/png";
	}

	public static String getWcsNetCDFUrl(String fileUrl, String layerName, String bbox) {
		return fileUrl.replace("dodsC", "wcs") + "?service=wcs&version=1.0.0" + "&request=GetCoverage&coverage=" + layerName + "&CRS=EPSG:4326" + "&bbox=" + bbox + "&width=676&height=330&format=geotiff";
	}

	public static String buildBoundingBox(double x1, double y1, double x2, double y2) {
		// note: the bounding box is left,lower,right,upper
		return (x1 + "," + y1 + "," + x2 + "," + y2);
	}
	
	public static String pointToBoundingBox(double x1, double y1, double tolerance) {
		// note: the bounding box is left,lower,right,upper
		double x11 = x1 - tolerance;
		double y11 = y1 - tolerance;
		double x22 = x1 + tolerance;
		double y22 = y1 + tolerance;
		return OGCFormatter.buildBoundingBox(x11, y11, x22, y22);
	}
	
	
	public static void main(String [] args){
		//http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/wcs?service=WCS&version=1.0.0&request=GetCoverage&COVERAGE=aquamaps:WorldClimBio2&CRS=EPSG:4326&BBOX=-180,-90,180,90&WIDTH=640&HEIGHT=480&FORMAT=geotiff
		String wcs = getWcsUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver", "aquamaps:WorldClimBio2", buildBoundingBox(-180, -85.5,180, 90));
		System.out.println(wcs);
	}
}
