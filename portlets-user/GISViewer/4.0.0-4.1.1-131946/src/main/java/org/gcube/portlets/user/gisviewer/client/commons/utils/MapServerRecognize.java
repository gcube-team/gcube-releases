/**
 * 
 */
package org.gcube.portlets.user.gisviewer.client.commons.utils;

import org.gcube.portlets.user.gisviewer.client.Constants;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;

import com.google.gwt.core.shared.GWT;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Aug 29, 2014
 * 
 *THIS CLASS DISCRIMINATES BETWEEN GEOSERVER AND MAPSERVER
 *ACCORDING TO THE VALUE OF GEOSERVER URL
 */
public class MapServerRecognize {

	public static enum SERVERTYPE{GEOSEVER, MAPSERVER};
	
	public static SERVERTYPE recongnize(LayerItem l){
		
		if(l==null)
			return null;
		
		return recongnize(l.getGeoserverUrl());
	}
	
	public static SERVERTYPE recongnize(String baseServerUrl){
		
		if(baseServerUrl==null || baseServerUrl.isEmpty())
			return null;
		
		//CASE MAP SERVER
		if(baseServerUrl.contains(Constants.WXS)){
			GWT.log("wms url contains 'wxs' returning "+SERVERTYPE.MAPSERVER);
			return SERVERTYPE.MAPSERVER;
		}else{
			GWT.log("wms url doesn't contains 'wxs' returning "+SERVERTYPE.GEOSEVER);
		//CASE GEOSEVER
			return SERVERTYPE.GEOSEVER;
		}
	}
	
	/**
	 * 
	 * @param serverType
	 * @param output
	 * @return
	 */
	public static String outputFormatRecognize(SERVERTYPE serverType, String output){
		
		if(output==null || output.isEmpty())
			return output;
		
		if(serverType==null)
			return output;
		
		switch (serverType) {
		
			case GEOSEVER:
				if(output.contains(Constants.JSON))
					return "json";
				else if(output.contains(Constants.CSV))
					return "csv";
			break;

			case MAPSERVER:
				
				if(output.contains(Constants.JSON))
					return "application/json;%20subtype=geojson";
				else if(output.contains(Constants.CSV))
					return "csv";
			break;
		}
		
		return output;
		
	}
}
