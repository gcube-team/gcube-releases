package org.gcube.spatial.data.gis;

import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;


public class StringTest {

	
	public static void main (String[] args){		
		String style="myDefaultStyle09";
		GSLayerEncoder lenc=new GSLayerEncoder();
		lenc.setDefaultStyle(style);
		lenc.setEnabled(true);
		lenc.setQueryable(true);
		
		
		String res=URIUtils.getStyleFromGSLayerEncoder(lenc);
		System.out.println(style+(style.equals(res)?" = ":" != ")+res);
		
		
	}
	
}
