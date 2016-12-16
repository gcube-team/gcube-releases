package org.gcube.spatial.data.gis;

import org.gcube.portlets.user.uriresolvermanager.exception.IllegalArgumentException;
import org.gcube.portlets.user.uriresolvermanager.exception.UriResolverMapException;
import org.gcube.spatial.data.geonetwork.iso.BoundingBox;
import org.gcube.spatial.data.geonetwork.iso.ISOMetadataFactory;
import org.gcube.spatial.data.geonetwork.iso.Protocol;

import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;

public class URIUtils {

	
	
	public static final String CRS="EPSG:4326";
	
	@Deprecated
	public static String getWmsUrl(String geoServerUrl, String layerName,String style, BoundingBox bbox) {	
		return ISOMetadataFactory.getWmsUrl(geoServerUrl, layerName, style, bbox.toString(), style);
    }
	
	@Deprecated
	public static String getWfsUrl(String geoServerUrl, String layerName) {
		return ISOMetadataFactory.getWfsUrl(geoServerUrl, layerName);        
    }
	
	@Deprecated
	public static String getWcsUrl(String geoServerUrl, String layerName,BoundingBox bbox) {		
		return ISOMetadataFactory.getWcsUrl(geoServerUrl, layerName, bbox.toString()); 
    }
	
	@Deprecated
	public static String getGisLinkByUUID(String uuid) throws UriResolverMapException, IllegalArgumentException{
//		Map<String,String> params=new HashMap();
//		params.put("scope", ScopeUtils.getCurrentScope());
//		params.put("gis-UUID", uuid);
//		UriResolverManager resolver = new UriResolverManager("GIS");
//		return resolver.getLink(params, true);
		return ISOMetadataFactory.getGisLinkByUUID(uuid);
	}
	
	public static String getStyleFromGSLayerEncoder(GSLayerEncoder encoder){
		return encoder.getRoot().getChildText("defaultStyle");
	}
	
	
	@Deprecated
	public static final String getProtocol(String uri){
		return Protocol.getByURI(uri).getDeclaration();
	}
}
