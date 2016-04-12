/**
 *
 */

package org.gcube.portlets.user.gisviewer.server.datafeature;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.gcube.portlets.user.gisviewer.client.Constants;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;
import org.gcube.portlets.user.gisviewer.client.commons.beans.WebFeatureTable;
import org.gcube.portlets.user.gisviewer.server.util.WPSXYResolutionFunction;
import org.json.JSONException;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.extjs.gxt.ui.client.data.BaseModel;


/**
 * The Class FeatureWPSRequest.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 3, 2016
 */
public class FeatureWPSRequest {

	private static Logger log = Logger.getLogger(FeatureWPSRequest.class);

	/**
 * Gets the table from wps service.
 *
 * @param layerItem the layer item
 * @param bbox the bbox
 * @param maxWFSFeature the max wfs feature
 * @param gCubeSecurityToken the g cube security token
 * @param dataMinerURL the data miner url
 * @param zoomLevel the zoom level
 * @return the table from wps service
 */
	public static WebFeatureTable getTableFromWPSService(LayerItem layerItem, String bbox, int maxWFSFeature, String gCubeSecurityToken, String dataMinerURL, int zoomLevel) {
		final WebFeatureTable table = new WebFeatureTable();
		table.setTitle(layerItem.getName());
		log.info("WPSGetData -> Creating WebFeatureTable for layerItem:  "+layerItem.getLayer());
		log.info("UUID:  "+layerItem.getUUID() +", BBOX (lat,long): "+bbox + ", Token: "+gCubeSecurityToken +", zoom: "+zoomLevel);

		if(gCubeSecurityToken==null|| gCubeSecurityToken.isEmpty()){
			log.warn("Overriding gCube token which is null or empty with a hard-code token: "+Constants.GCUBE_TOKEN +". Is it in dev mode?");
			gCubeSecurityToken = Constants.GCUBE_TOKEN;
		}

		//TODO CALCULATE RESOLUTION AS FUNCTION OF
		Float resolution =  null;
		try {
			resolution = WPSXYResolutionFunction.getInstance().getXYResolutionOf(zoomLevel);
		}
		catch (Exception e1) {
			log.warn(e1);
			resolution = Constants.DEFAULT_XY_WPS_RESOLUTION;
		}

		log.debug("zoom:  "+zoomLevel +", using resolution: "+resolution);

		try {

			String[] splitted = bbox.split(",");
			//WPS call
			String url = dataMinerURL+"?" +
				"request=Execute" +
				"&service=WPS" +
				"&Version=1.0.0" +
				"&gcube-token=" +gCubeSecurityToken+
				"&lang=en-US" +
				"&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.XYEXTRACTOR" +
				"&DataInputs=OutputTableLabel=wps_xy_extractor;" +
				"Layer="+layerItem.getUUID()+";" +
				"YResolution="+resolution+";" +
				"XResolution="+resolution+";" +
				"BBox_LowerLeftLong=" +splitted[1]+";"+
				"BBox_UpperRightLat="+splitted[2] +";"+
				"BBox_LowerLeftLat="+splitted[0] +";"+
				"BBox_UpperRightLong="+splitted[3] +";"+
				"Z="+(layerItem.getZAxisSelected()==null || layerItem.getZAxisSelected().isNaN()?"0":layerItem.getZAxisSelected())+";"+
				"TimeIndex=0";

			log.info("WPS request: "+url);
			URL wpsURL = new URL(url);
			HashMap<String, String> mappings = new HashMap<String, String>();
			mappings.put("wps", "http://www.opengis.net/wps/1.0.0");
			mappings.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			mappings.put("ows", "http://www.opengis.net/ows/1.1");
			mappings.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);

			NamespaceContextMap nsContect = new NamespaceContextMap(mappings);
			String wpsData = getTextFromXPathExpression(nsContect, wpsURL.openStream(), "//wps:ComplexData[@mimeType='text/csv']");

//			log.trace(wpsData);

			final StringReader reader = new StringReader(wpsData);
			final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());

			List<String> listHeaders = new ArrayList<String>(parser.getHeaderMap().keySet().size());
			for (String header : parser.getHeaderMap().keySet()) {
				listHeaders.add(header);
			}
			try {
			    for (final CSVRecord record : parser) {
			    	final BaseModel row = new BaseModel();
//			    	log.trace("# "+record.getRecordNumber());
			    	for (String header : listHeaders) {
			    		row.set(header, record.get(header));
//			    		log.trace(record.get(header));
					}
			    	table.addRow(row);
			    }
			} finally {
			    parser.close();
			    reader.close();
			}
		} catch (IOException e) {
			table.setError(true);
			table.setErrorMsg("WPS request not available for this layer: "+layerItem.getName());
			log.error("IOException in getTableFromWPSService for layerItem UUID: "+layerItem.getUUID() + ", name: "+layerItem.getName(),e);
		} catch (JSONException e) {
			table.setError(true);
			table.setErrorMsg("WPS request not available for this layer: "+layerItem.getName());
			log.error("JSONException in getTableFromWPSService for layerItem UUID: "+layerItem.getUUID() + ", name: "+layerItem.getName(),e);
		}

		return table;
	}

	/**
	 * Gets the text from x path expression.
	 *
	 * @param nsContext the ns context
	 * @param source the source
	 * @param xpathExpression the xpath expression
	 * @return the text from x path expression
	 */
	public static String getTextFromXPathExpression(NamespaceContextMap nsContext, InputStream source, String xpathExpression){

		try {

			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(nsContext);
			XPathExpression xPathExpression = xpath.compile(xpathExpression);
			InputSource inputSource = new InputSource(source);
			NodeList nodes = (NodeList) xPathExpression.evaluate(inputSource, XPathConstants.NODESET);

			if(nodes.item(0)==null) {
				return null;
			}
			return nodes.item(0).getTextContent();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// MAIN TEST
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	/*public static void main(String args[]) {
		try {

			String url = "http://dataminer-d-d4s.d4science.org/wps/WebProcessingService?request=Execute&service=WPS&Version=1.0.0&gcube-token=d7a4076c-e8c1-42fe-81e0-bdecb1e8074a&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.XYEXTRACTOR&DataInputs=OutputTableLabel=wps_xy_extractor;Layer=7bc119c2-5018-4363-a9d4-70f106f4b43d;YResolution=0.5;XResolution=0.5;BBox_LowerLeftLong=-5.625;BBox_UpperRightLat=50.09765625;BBox_LowerLeftLat=46.58203125;BBox_UpperRightLong=-2.109375;Z=0;TimeIndex=0";
			URL wpsURL = new URL(url);

			HashMap<String, String> mappings = new HashMap<String, String>();
//			mappings.put("xmlns", "http://www.opengis.net/sld");
			mappings.put("wps", "http://www.opengis.net/wps/1.0.0");
//			mappings.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			mappings.put("ows", "http://www.opengis.net/ows/1.1");
			mappings.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);

			NamespaceContextMap nsContect = new NamespaceContextMap(mappings);
			String wpsData = getTextFromXPathExpression(nsContect, wpsURL.openStream(), "//wps:ComplexData[@mimeType='text/csv']");

			System.out.println(wpsData);

			final StringReader reader = new StringReader(wpsData);
			final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());

			List<String> listHeaders = new ArrayList<String>(parser.getHeaderMap().keySet().size());
			for (String header : parser.getHeaderMap().keySet()) {
				listHeaders.add(header);
			}
			try {
			    for (final CSVRecord record : parser) {

			    	System.out.println("# "+record.getRecordNumber());
			    	for (String header : listHeaders) {
			    		System.out.println(header +"/"+record.get(header));
					}
			    }
			} finally {
			    parser.close();
			    reader.close();
			}


		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}*/
}
