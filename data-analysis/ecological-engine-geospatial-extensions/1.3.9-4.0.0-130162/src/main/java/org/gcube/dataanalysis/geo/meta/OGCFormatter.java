package org.gcube.dataanalysis.geo.meta;

import java.util.HashMap;

public class OGCFormatter {
	public static String getWfsUrl(String geoServerUrl, String layerName, String bbox, int limit, String format) {
		int idx = -1;
		if ((idx = geoServerUrl.indexOf("?"))>0){
			geoServerUrl = geoServerUrl.substring(0,idx);
		}
		else
			geoServerUrl = geoServerUrl + "/wfs";
		
		String srsString = "srsName=urn:x-ogc:def:crs:EPSG:4326";
		
		//patch for Map Server
			if (geoServerUrl.contains("/wxs") && format.equalsIgnoreCase("json")){
				format = "application/json;%20subtype=geojson";
				srsString = "srsName=EPSG:4326";
			}

	    //the srsName keeps lat,long output constant
		return geoServerUrl+"?service=wfs&version=1.1.0&REQUEST=GetFeature" + "&"+srsString+"&TYPENAME=" + layerName + (bbox==null? "":"&BBOX=" + bbox) + (limit == 0 ? "" : "&MAXFEATURES=" + limit) + (format == null ? "" : "&OUTPUTFORMAT=" + format);
	}

	public static String getWmsUrl(String geoServerUrl, String layerName, String style, String bbox) {
		return geoServerUrl + "/wms?service=wms&version=1.1.0" + "&request=GetMap&layers=" + layerName + "&styles=" + (style == null ? "" : style) + "&bbox=" + bbox + "&width=676&height=330&srs=EPSG:4326&format=application/openlayers";
	}

	public static String getWcsUrl(String geoServerUrl, String layerName, String bbox) {
		return geoServerUrl + "/wcs?service=wcs&version=1.0.0" + "&request=GetCoverage&coverage=" + layerName + "&CRS=EPSG:4326" + "&bbox=" + bbox + "&width=676&height=330&format=geotiff";
	}

	public static String getWcsUrl(String baseUrl, String coverage, String crs, String responsecrs, String boundingbox, String width, String height, String depth, String format, String resx,String resy,String resz,String time, HashMap<String,String> parameters ) {
		String wcsURL = baseUrl;
		if (!wcsURL.endsWith("?"))
			wcsURL+="/wcs?";
		
		wcsURL+="service=wcs&version=1.0.0"+"&request=GetCoverage&coverage=" + coverage+"&CRS="+crs+ "&bbox=" + boundingbox+"&format="+format;
		
		if (width!=null && width.trim().length()>0)
			wcsURL+="&width="+width;
		if (height!=null && height.trim().length()>0)
			wcsURL+="&height="+height;
		if (responsecrs!=null && responsecrs.trim().length()>0)
			wcsURL+="&RESPONSE_CRS="+responsecrs;
		if (depth!=null && depth.trim().length()>0)
			wcsURL+="&DEPTH="+depth;
		if (resx!=null && resx.trim().length()>0)
			wcsURL+="&RESX="+resx;
		if (resy!=null && resy.trim().length()>0)
			wcsURL+="&RESY="+resy;
		if (resz!=null && resz.trim().length()>0)
			wcsURL+="&RESZ="+resz;
		if (time!=null && time.trim().length()>0)
			wcsURL+="&TIME="+time;
		
		for (String key:parameters.keySet()){
			String value = parameters.get(key);
			wcsURL+="&"+key+"="+value;
		}
		
		return wcsURL;
	}
 	
	public static String getWmsNetCDFUrl(String fileUrl, String layerName, String bbox) {
		return fileUrl.replace("dodsC", "wms") + "?service=wms&version=1.3.0" + "&request=GetMap&layers=" + layerName + "&bbox=" + bbox + "&styles=&width=676&height=330&srs=EPSG:4326&CRS=EPSG:4326&format=image/png&COLORSCALERANGE=auto";
	}

	public static String getWcsNetCDFUrl(String fileUrl, String layerName, String bbox) {
		return fileUrl.replace("dodsC", "wcs") + "?service=wcs&version=1.0.0" + "&request=GetCoverage&coverage=" + layerName + "&CRS=EPSG:4326" + "&bbox=" + bbox + "&width=676&height=330&format=geotiff";
	}

	public static String getOpenDapURL(String threddsCatalog, String filename) {
		return threddsCatalog.replace("catalog.xml",filename).replace("catalog","dodsC");
	}
	
	public static String buildBoundingBox(double x1, double y1, double x2, double y2) {
		// note: the bounding box is left,lower,right,upper
		return (y1 + "," + x1 + "," + y2 + "," + x2);
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
