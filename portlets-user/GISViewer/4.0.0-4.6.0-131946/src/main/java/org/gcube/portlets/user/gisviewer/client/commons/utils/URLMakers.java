package org.gcube.portlets.user.gisviewer.client.commons.utils;


import java.util.List;
import java.util.Vector;

import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;
import org.gcube.portlets.user.gisviewer.client.commons.utils.MapServerRecognize.SERVERTYPE;

import com.google.gwt.core.shared.GWT;


/**
 * The Class URLMakers.
 *
 * @author Ceras. Updated by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 28, 2016
 */
public class URLMakers {


	/**
	 * Encoding layer.
	 *
	 * @param l the l
	 * @return the string
	 */
	private static String encodingLayer(String l){
		String result = l.replace(":", "%3A");

		return result;
	}


	/**
	 * Gets the url.
	 *
	 * @param projection the projection
	 * @param width the width
	 * @param height the height
	 * @param x the x
	 * @param y the y
	 * @param bbox the bbox
	 * @param layers the layers
	 * @return the url
	 */
	public static String getURL(String projection, String width, String height,int x, int y,String bbox, Vector<String> layers){

		String layer="";
		boolean first=true;
		for(String s : layers){
			if(!first) {
				layer=layer+"%2C"+encodingLayer(s);
			}
			if(first) {
				layer=encodingLayer(s);
			}
			first=false;
		}

		String [] _bbox=bbox.split(",");

		projection = encodingLayer(projection);
		String result="?REQUEST=GetFeatureInfo&EXCEPTIONS=application%2Fvnd.ogc.se_xml&BBOX="+_bbox[0]+"%2C"+_bbox[1]+"%2C"+_bbox[2]+"%2C"+_bbox[3]+"&X="+x+"&Y="+y+"&INFO_FORMAT=text%2Fhtml&QUERY_LAYERS="+layer+"&FEATURE_COUNT=50&Layers="+layer+"&Styles=&Srs="+projection+"&WIDTH="+width+"&HEIGHT="+height+"&format=image%2Fpng";

		return result;
	}

	/**
	 * Gets the url.
	 *
	 * @param projection the projection
	 * @param width the width
	 * @param height the height
	 * @param x the x
	 * @param y the y
	 * @param bbox the bbox
	 * @param layerItems the layer items
	 * @return the url
	 */
	public static String getURL(String projection, int width, int height,int x, int y,String bbox, List<LayerItem> layerItems){
		String strListLayers="";
		boolean first=true;
		for(LayerItem layerItem : layerItems){
			if (layerItem.isClickData()) {
				String strLayer = encodingLayer(layerItem.getName());
				strListLayers = first ? strLayer : strListLayers+"%2C"+strLayer;
				first=false;
			}
		}

		String [] _bbox=bbox.split(",");

		projection = encodingLayer(projection);
		String result="?REQUEST=GetFeatureInfo&EXCEPTIONS=application%2Fvnd.ogc.se_xml&BBOX="+_bbox[0]+"%2C"+_bbox[1]+"%2C"+_bbox[2]+"%2C"+_bbox[3]+"&X="+x+"&Y="+y+"&INFO_FORMAT=text%2Fhtml&QUERY_LAYERS="+strListLayers+"&FEATURE_COUNT=50&Layers="+strListLayers+"&Styles=&Srs="+projection+"&WIDTH="+width+"&HEIGHT="+height+"&format=image%2Fpng";

		return result;
	}


//	public static String getURLFeatureInfo(ClickDataInfo clickDataInfo, String projection, String bbox) {
//		return getURL(projection, clickDataInfo.getW(), clickDataInfo.getH(), clickDataInfo.getX(), clickDataInfo.getY(), bbox, clickDataInfo.getLayers());
//	}

	/**
	 * Gets the geoserver wms url.
	 *
	 * @param geoserverUrl the geoserver url
	 * @return the geoserver wms url
	 */
	public static String getGeoserverWmsUrl(String geoserverUrl) {
			return geoserverUrl + "/wms";
	}

	// COMMENTED By Francesco M. Fixed for Aquamaps
	// public static String getGeoserverGwcUrl(String geoserverUrl) {
	// return geoserverUrl + "/gwc/service/wms";
	// }


	/**
	 * Gets the url.
	 *
	 * @param clickDataInfo the click data info
	 * @param projection the projection
	 * @param layerItems the layer items
	 * @return the url
	 */
	public static String getURL(ClickDataInfo clickDataInfo, String projection, List<LayerItem> layerItems) {
		return getURL(projection, clickDataInfo.getW(), clickDataInfo.getH(), clickDataInfo.getX(), clickDataInfo.getY(), clickDataInfo.getBbox(), layerItems);
	}

	/**
	 * Gets the wfs feature url.
	 *
	 * @param l the l
	 * @param bbox the bbox
	 * @param limit the limit
	 * @param format the format
	 * @return the wfs feature url
	 */
	public static String getWfsFeatureUrl(LayerItem l, String bbox, int limit, String format) {

		// COMMENTED 26/06/2014
		// String link = l.getGeoserverUrl() +
		// "/wfs?service=wfs&version=1.1.0&REQUEST=GetFeature" +
		// "&TYPENAME=" + l.getLayer() +
		// "&BBOX=" + bbox +
		// (limit==0 ? "" : "&MAXFEATURES="+limit) +
		// (format==null ? "" : "&OUTPUTFORMAT="+format);

		String link = l.getGeoserverUrl();
		GWT.log("GeoserverUrl is: "+link);

		String outputformat = null;
		String srsName = null;
		String boundingBox = bbox;

		//CASE MAP SERVER
		SERVERTYPE mapserverType = MapServerRecognize.recongnize(l);
		GWT.log("Recongnized SERVERTYPE: "+mapserverType);

		if(mapserverType!=null){
			if(mapserverType.equals(SERVERTYPE.MAPSERVER)){
				GWT.log("wms url contains wxs is a map server? no appending /wfs ");
				outputformat = MapServerRecognize.outputFormatRecognize(SERVERTYPE.MAPSERVER, format);
				srsName = "EPSG:4326";
				boundingBox = reverseCoordinate(bbox, ","); //USE AXIS XY
				//TODO DEBUG
				System.out.println("SERVERTYPE.MAPSERVER outputformat: "+outputformat);
				System.out.println("SERVERTYPE.MAPSERVER srsName: "+srsName);
				System.out.println("SERVERTYPE.MAPSERVER boundingBox: "+boundingBox);
			}else {
				GWT.log("is geoserver append /wfs");
				link+="/wfs";
				outputformat =  MapServerRecognize.outputFormatRecognize(SERVERTYPE.GEOSEVER, format);
				srsName = "urn:x-ogc:def:crs:EPSG:4326"; //USE AXIS YX
				//TODO DEBUG
				System.out.println("SERVERTYPE.GEOSEVER outputformat: "+outputformat);
				System.out.println("SERVERTYPE.GEOSEVER srsName: "+srsName);
			}
		}

		link +="?service=wfs&version=1.1.0"
				+ "&REQUEST=GetFeature"
				+ "&srsName="+srsName
				+ "&TYPENAME=" + l.getLayer()+
				(boundingBox==null ? "" : "&BBOX="+boundingBox)+
//				+ "&BBOX=" + boundingBox +
				(limit==0 ? "" : "&MAXFEATURES="+limit) +
				(outputformat==null ? "" : "&OUTPUTFORMAT="+outputformat);

		GWT.log("WFS: "+link);
		return link;
	}

	static String[][] a = {
		{"\\?","%3F"},
		{"&","%26"},
	};

	/**
	 * Reverse coordinate.
	 *
	 * @param BBOX the bbox
	 * @param split eg. ,
	 * @return a BBOX with reverse x and y coordinate
	 */
	public static String reverseCoordinate(String BBOX, String split){

		if(BBOX==null || BBOX.isEmpty()) {
			return BBOX;
		}

		String[] splitted = BBOX.split(split);

		for (String string : splitted) {
			System.out.println(string);
		}

		if(splitted.length==4){
			return splitted[1]+split+splitted[0]+split+splitted[3]+split+splitted[2];
		}
		else {
			return null;
		}
	}


	/**
	 * Encode url.
	 *
	 * @param url the url
	 * @return the string
	 */
	public static String encodeUrl(String url) {
		String urlNew = url;
		for (String[] s: a) {
			urlNew = urlNew.replaceAll(s[0], s[1]);
		}
		return urlNew;
	}

	/**
	 * Decode url.
	 *
	 * @param url the url
	 * @return the string
	 */
	public static String decodeUrl(String url) {
		String urlNew = url;
		for (String[] s: a) {
			urlNew = urlNew.replaceAll(s[1], s[0]);
		}
		return urlNew;
	}

	/**
	 * Gets the geoserver url from wms url.
	 *
	 * @param wmsUrl the wms url
	 * @return the geoserver url from wms url
	 */
	public static String getGeoserverUrlFromWmsUrl(String wmsUrl) {
		String gsUrl = new String(wmsUrl);
		// remove each string after "?"
		int index = gsUrl.indexOf("?");
		if (index!=-1) {
			gsUrl = gsUrl.substring(0, gsUrl.indexOf("?"));
		}
		// remove suffix "/wms" or "/wms/"
		gsUrl = gsUrl.replaceFirst("(/wms)$", "").replaceFirst("(/wms/)$", "");

		return gsUrl;
	}


	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	/*public static void main(String[] args) {

		String split =",";
		String BBOX = "47.13134765625,2.87841796875,47.57080078125,3.31787109375";
		System.out.println(URLMakers.reverseCoordinate(BBOX, split));
	}*/

}
