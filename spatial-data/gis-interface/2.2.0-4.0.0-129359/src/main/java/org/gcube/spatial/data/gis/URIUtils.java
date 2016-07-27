package org.gcube.spatial.data.gis;

import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.uriresolvermanager.UriResolverManager;
import org.gcube.portlets.user.uriresolvermanager.exception.IllegalArgumentException;
import org.gcube.portlets.user.uriresolvermanager.exception.UriResolverMapException;
import org.gcube.spatial.data.gis.model.BoundingBox;

public class URIUtils {

	
	public static final String CRS="EPSG:4326";
	public static final String WMS_PROTOCOL="OGC:WMS-1.3.0-http-get-map";
	public static final String WFS_PROTOCOL="OGC:WFS-1.0.0-http-get-feature";
	public static final String HTTP_PROTOCOL="WWW:LINK-1.0-http--link";
	
	
	public static String getWmsUrl(String geoServerUrl, String layerName,String style, BoundingBox bbox) {	
		if(bbox==null) bbox=BoundingBox.WORLD_EXTENT;
        return geoServerUrl + 
        		"/wms?service=wms&version=1.1.0" 
        		+ "&request=GetMap&layers=" + layerName 
        		+ "&styles=" + (style == null ? "" : style) 
        		+ "&bbox=" + bbox + "&width=676&height=330" +
        		"&srs=EPSG:4326&crs="+CRS+"&format=application/openlayers";
    }
	public static String getWfsUrl(String geoServerUrl, String layerName) {		
        return geoServerUrl + 
        		"/ows?service=wfs&version=1.0.0" 
        		+ "&request=GetFeature&typeName=" + layerName 
        		+"&format=json";
    }
	public static String getWcsUrl(String geoServerUrl, String layerName,BoundingBox bbox) {		
		if(bbox==null) bbox=BoundingBox.WORLD_EXTENT;
		return geoServerUrl + "/wcs?service=wcs&version=1.0.0" + "&request=GetCoverage&coverage=" + 
				layerName + "&CRS=EPSG:4326" + "&bbox=" + bbox + "&width=676&height=330&format=geotiff"; 
    }
	
	public static String getGisLinkByUUID(String uuid) throws UriResolverMapException, IllegalArgumentException{
		Map<String,String> params=new HashMap();
		params.put("scope", ScopeProvider.instance.get());
		params.put("gis-UUID", uuid);
		UriResolverManager resolver = new UriResolverManager("GIS");
		return resolver.getLink(params, true);
	}
	
	public static String getStyleFromGSLayerEncoder(GSLayerEncoder encoder){
		return encoder.getRoot().getChildText("defaultStyle");
	}
	
	public static final String getProtocol(String uri){
		uri=uri.toLowerCase();
		if(uri.contains(("service=wms"))) return WMS_PROTOCOL;
		else if(uri.contains("service=wfs")) return WFS_PROTOCOL;
		else return HTTP_PROTOCOL;
	}
	
}
