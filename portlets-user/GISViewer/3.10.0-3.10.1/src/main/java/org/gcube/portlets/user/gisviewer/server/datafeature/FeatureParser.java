/**
 *
 */
package org.gcube.portlets.user.gisviewer.server.datafeature;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.gcube.portlets.user.gisviewer.client.Constants;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;
import org.gcube.portlets.user.gisviewer.client.commons.beans.WebFeatureTable;
import org.gcube.portlets.user.gisviewer.client.commons.utils.URLMakers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.extjs.gxt.ui.client.data.BaseModel;


/**
 * The Class FeatureParser.
 *
 * @author Ceras
 * updated by Francesco Mangiacrapa
 *         francesco.mangiacrapa@isti.cnr.it Jan 21, 2016
 *
 *         <wfs:FeatureCollection
 *         xmlns:aquamaps="http://www.aquamaps.org"
 *         xmlns:ogc="http://www.opengis.net/ogc"
 *         xmlns:gml="http://www.opengis.net/gml"
 *         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *         xmlns:xlink="http://www.w3.org/1999/xlink"
 *         xmlns:ows="http://www.opengis.net/ows"
 *         xmlns:wfs="http://www.opengis.net/wfs" numberOfFeatures="12"
 *         timeStamp="2012-04-20T18:17:40.319+02:00" xsi:schemaLocation=
 *         "http://www.aquamaps.org http://geoserver2.d4science.research-infrastructures.eu:80/geoserver/wfs?service=WFS&version=1.1.0&request=DescribeFeatureType&typeName=aquamaps%3AdepthMean http://www.opengis.net/wfs http://geoserver2.d4science.research-infrastructures.eu:80/geoserver/schemas/wfs/1.1.0/wfs.xsd"
 *         > <gml:featureMembers> <aquamaps:depthMean gml:id="depthMean.168267">
 *         <aquamaps:csquarecode>7700:390:3</aquamaps:csquarecode>
 *         <aquamaps:the_geom> </aquamaps:the_geom>
 *         <aquamaps:DepthMean>2732.0</aquamaps:DepthMean> </aquamaps:depthMean>
 *         </gml:featureMembers>
 */
public class FeatureParser {

	private static final String ROW_TAG = "gml:featureMembers";
	private static String EXCEPTION_TAG_RESPONSE = "ows:ExceptionReport";

	private static Logger log = Logger.getLogger(FeatureParser.class);

	/**
	 * Gets the data results.
	 *
	 * @param layerItems the layer items
	 * @param bbox the bbox
	 * @param maxWFSFeature the max wfs feature
	 * @param gCubeSecurityToken the g cube security token
	 * @param dataMinerURL
	 * @param zoomLevel
	 * @return the data results
	 */
	public static List<WebFeatureTable> getDataResults(List<LayerItem> layerItems, String bbox, int maxWFSFeature, String gCubeSecurityToken, String dataMinerURL, int zoomLevel) {
		List<WebFeatureTable> results = new ArrayList<WebFeatureTable>();

		if(maxWFSFeature<0) {
			maxWFSFeature = Constants.MAX_WFS_FEATURES;
		}

		//IF WFS IS AVAILABLE USE WFS REQUEST OTHERWHISE TRY TO USE WPS SERVICE
		for (LayerItem layerItem : layerItems){
			WebFeatureTable table = getTableFromJson(layerItem, bbox, maxWFSFeature);

			if(table.isError()){
				if(layerItem.getUUID()!=null) {
					results.add(FeatureWPSRequest.getTableFromWPSService(layerItem, bbox, maxWFSFeature, gCubeSecurityToken, dataMinerURL, zoomLevel));
				}else{
					results.add(table);
				}
			}else
				results.add(table);
		}
		return results;
	}

	/**
	 * Gets the table.
	 *
	 * @param layerItem the layer item
	 * @param bbox the bbox
	 * @return the table
	 */
	private static WebFeatureTable getTable(LayerItem layerItem, String bbox) {
		final WebFeatureTable table = new WebFeatureTable();
		table.setTitle(layerItem.getName());

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			String url = URLMakers.getWfsFeatureUrl(layerItem, bbox, Constants.MAX_WFS_FEATURES, null);
			log.info("URL getFeature: "+url);

			Document doc = db.parse(new URL(url).openStream());

			// parsing with CXML
			CXml root = new CXml(doc);

			if (root.tagName().equals(EXCEPTION_TAG_RESPONSE )) {
				;
			}

			//System.out.println("ROOT:" + root.tagName());

			// iterate all data rows
			root.child(ROW_TAG).children().each(new CXmlManager() {

				@Override
				public void manage(int index, CXml cxml) {

					final BaseModel row = new BaseModel();

					// iterate all elements in row (columns)
					cxml.children().each(new CXmlManager() {
						@Override
						public void manage(int index, CXml cxml) {
							String tagName = cxml.tagName();
							String value = cxml.getText();
							if (value!=null) {
								if (tagName.contains(":")) {
									tagName = tagName.split(":", 2)[1];
								}
								row.set(tagName, value);
								//System.out.println("\t"+tagName+" = "+value);
							}
						}
					});
					table.addRow(row);
					//System.out.println();
				}
			});

		} catch (FileNotFoundException e) {
			table.setError(true);
			e.printStackTrace();
		} catch (Exception e) {
			table.setError(true);
			e.printStackTrace();
		}

		return table;
	}

	/**
	 * Gets the table from json.
	 *
	 * @param layerItem the layer item
	 * @param bbox the bbox
	 * @param maxWFSFeature the max wfs feature
	 * @return the table from json
	 */
	@SuppressWarnings("unchecked")
	private static WebFeatureTable getTableFromJson(LayerItem layerItem, String bbox, int maxWFSFeature) {
		final WebFeatureTable table = new WebFeatureTable();
		table.setTitle(layerItem.getName());

		log.info("getTableFromJson -> Creating WfsTable to layerItem:  "+layerItem.getLayer());
		log.info("getTableFromJson -> Creating WfsTable to layerItem:  "+layerItem.getGeoserverUrl());

		try {
			String url = URLMakers.getWfsFeatureUrl(layerItem, bbox, maxWFSFeature, Constants.JSON);
			InputStream is = new URL(url).openStream();
			String jsonTxt = IOUtils.toString(is);

			if(jsonTxt==null || jsonTxt.isEmpty()){
				jsonTxt = "{\"type\":\"FeatureCollection\",\"features\":[]}";
			}

			// get json object
			JSONObject json = new JSONObject(jsonTxt);

			// iterate features
			JSONArray features = json.getJSONArray("features");

			for (int i=0; i<features.length(); i++) {
				final BaseModel row = new BaseModel();

				// iterate properties
				JSONObject properties = ((JSONObject)features.get(i)).getJSONObject("properties");
				@SuppressWarnings("unchecked")
				Iterator<String> ii = properties.keys();
				while (ii.hasNext()) {
					String key = ii.next();
					String value = properties.getString(key);

					row.set(key, value);
				}
				table.addRow(row);
			}

		} catch (IOException e) {
			table.setError(true);
			table.setErrorMsg("WFS request not available for this layer: "+layerItem.getName());
			log.error("IOException in getTableFromJson for layerItem UUID: "+layerItem.getUUID() + ", name: "+layerItem.getName(),e);
		} catch (JSONException e) {
			table.setError(true);
			table.setErrorMsg("WFS request not available for this layer: "+layerItem.getName());
			log.error("JSONException in getTableFromJson for layerItem UUID: "+layerItem.getUUID() + ", name: "+layerItem.getName(),e);
		}

		return table;
	}

//	/**
//	 * @param layerItem
//	 * @return
//	 */
//	private static WfsTable getTableFromJson(LayerItem layerItem, String bbox) {
//		return getTableFromJson(layerItem, bbox, Constants.MAX_WFS_FEATURES);
//	}

	// PARSE TEST
	/**
 * Parses the.
 */
private static void parse() {
		String layer = "aquamaps:environments";
		String url = "http://geoserver2.d4science.research-infrastructures.eu/geoserver/wfs?service=wfs&version=1.1.0&request=GetFeature&typeName="+layer+"&bbox=0,0,180,1";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new URL(url).openStream());

			CXml root = new CXml(doc);

			if (root.tagName().equals(EXCEPTION_TAG_RESPONSE )) {
				;
			}

			System.out.println("ROOT:" + root.tagName());

			root.child("gml:featureMembers").children().each(new CXmlManager() {
				@Override
				public void manage(int index, CXml cxml) {
					cxml.children().each(new CXmlManager() {
						@Override
						public void manage(int index, CXml cxml) {
							String tagName = cxml.tagName();
							String value = cxml.getText();
							if (value!=null) {
								if (tagName.contains(":")) {
									tagName = tagName.split(":", 2)[1];
								}
								System.out.println("\t"+tagName+" = "+value);
							}
						}
					});
					System.out.println();
				}
			});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// MAIN TEST
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]) {
		/*List<LayerItem> layerItems = new ArrayList<LayerItem>();
		LayerItem l;

		l = new LayerItem();
		l.setLayer("aquamaps:depthMean");
		l.setGeoserverUrl("http://geoserver.d4science-ii.research-infrastructures.eu/geoserver");
		l.setName("Depth Mean");
		layerItems.add(l);

		l = new LayerItem();
		l.setLayer("aquamaps:environments");
		l.setGeoserverUrl("http://geoserver2.d4science.research-infrastructures.eu/geoserver");
		l.setName("Environments");
		layerItems.add(l);

		l = new LayerItem();
		l.setLayer("aquamaps:eezall");
		l.setGeoserverUrl("http://geoserver.d4science-ii.research-infrastructures.eu/geoserver");
		l.setName("Eezall");
		layerItems.add(l);

		List<WfsTable> list = FeatureParser.getDataResults(layerItems, "86,0,180,1", 5);

		for (WfsTable table: list) {
			System.out.println("TABLE \""+ table.getTitle() +"\"");

			List<String> columnNames = table.getColumnNames();
			for (String columnName : columnNames)
				System.out.print(columnName+", ");
			System.out.println();

			for (BaseModel row : table.getRows()) {
				for (String columnName : columnNames)
					System.out.print(row.get(columnName)+", ");
				System.out.println();
			}
		}*/

		LayerItem l = new LayerItem();
		l.setLayer("HeatFlowUnit");
		l.setGeoserverUrl("http://egip.brgm-rec.fr/wxs/");
		l.setName("HeatFlowUnit");

		FeatureParser.getTableFromJson(l, "-90,-180,90,180", 1);
	}
}
