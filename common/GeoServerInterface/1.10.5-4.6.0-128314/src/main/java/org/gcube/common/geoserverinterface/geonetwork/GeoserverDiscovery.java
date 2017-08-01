package org.gcube.common.geoserverinterface.geonetwork;

//import it.cnr.isti.geoserverInteraction.GeonetworkCaller.GeoserverType;
import java.util.ArrayList;

import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeoserverType;
import org.gcube.common.geoserverinterface.cxml.CXml;
import org.gcube.common.geoserverinterface.cxml.CXmlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author francesco-mangiacrapa
 *
 */
public class GeoserverDiscovery {

	private static final Logger logger = LoggerFactory.getLogger(GeoserverDiscovery.class);
	
	
	/**
	 * @uml.property  name="wmsGeoserverList"
	 */
	private ArrayList<String> wmsGeoserverList = null;
	/**
	 * @uml.property  name="wfsGeoserverList"
	 */
	private ArrayList<String> wfsGeoserverList = null;
	/**
	 * @uml.property  name="mostUnloadWmsGeoserver"
	 */
	private String mostUnloadWmsGeoserver = null;
	/**
	 * @uml.property  name="indexMostUnloadGeoserver"
	 */
	private int indexMostUnloadGeoserver;
	/**
	 * @uml.property  name="sorter"
	 * @uml.associationEnd  
	 */
	private GeoserverSortInterface sorter = null;
	/**
	 * @uml.property  name="xmlAllHarvestings"
	 */
	private String xmlAllHarvestings = null;
	/**
	 * @uml.property  name="xmlWmsHarvestings"
	 */
	private String xmlWmsHarvestings = null;

	public GeoserverDiscovery(String xmlAllHarvestings) {

		this.loadGeoserverListsByHarvestings(xmlAllHarvestings);
	}

	/**
	 * 
	 * @param list
	 * @return ordered list by sorter
	 */
	public ArrayList<String> sortGeoserver(ArrayList<String> list) {
		
		ArrayList<String> orderList = new ArrayList<String>();
		orderList = sorter.sortGeoserverList(list);
		
		return orderList;
	}

	/**
	 * @return
	 * @uml.property  name="sorter"
	 */
	public GeoserverSortInterface getSorter() {
		return sorter;
	}

	/**
	 * @param sorter  type GeoserverSortInterface
	 * @uml.property  name="sorter"
	 */
	public void setSorter(GeoserverSortInterface sorter) {
		this.sorter = sorter;
	}

	private ArrayList<String> loadGeoserverList(GeoserverType serverType, String xmlHarvestings) {

		final ArrayList<String> list = new ArrayList<String>();
		final String serverTypeLC = serverType.toString().toLowerCase();
		
		logger.trace("loadGeoserverList: Parsing Harvestings XML File...\n"+xmlHarvestings);

		CXml root = new CXml(xmlHarvestings);
		root.find("url").each(new CXmlManager() {
			@Override
			public void manage(int index, CXml cXml) {
				
				String url = cXml.text();
				
				if (url.endsWith("/" + serverTypeLC)) {
					// remove suffix "/wms" or "/wms/"
					url = url.replaceFirst("(/" + serverTypeLC + ")$", "").replaceFirst("(/" + serverTypeLC + "/)$", "");
					list.add(url);
				}
			}
		});
		
//		logger.info("loadGeoserverList: Parsing Harvestings XML File...\n"+xmlHarvestings);
//		try {
//
//			XPath xpath = XPathFactory.newInstance().newXPath();
//			String expression = "//url/text()[contains(.,'" + serverTypeLC + "')]";
//			logger.info("Expression: "+expression);
//			// String expression = "//url";
//
////			System.out.println(xmlHarvestings);
//			
//			XPathExpression xPathExpression = xpath.compile(expression);
//			InputSource inputSource = new InputSource(InputStreamUtil.stringToInputStream(xmlHarvestings));
//			NodeList nodes = (NodeList) xPathExpression.evaluate(inputSource, XPathConstants.NODESET);
//
//			for (int i = 0; i<nodes.getLength(); i++) {
//				Node node = nodes.item(i);
//				String[] spl = node.getTextContent().split("/" + serverTypeLC); //CUT STRING WMS/WFS TO URL
				// for(String tmp : spl){
				// System.out.println(tmp);
				// }
//				list.add(spl[0]);
//				System.out.println("Found "+ serverTypeLC.toUpperCase() +" Geoserver string url: " + spl[0]);
//			}
//			
//			logger.debug("");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return list;
	}

	public void reloadWmsGeoserverList(String xmlWmsHarvestings) {

		this.wmsGeoserverList = loadGeoserverList(GeoserverType.WMS, xmlWmsHarvestings);
	}

	public void reloadWfsGeoserverList(String xmlWfsHarvestings) {

		this.wfsGeoserverList = loadGeoserverList(GeoserverType.WFS, xmlWfsHarvestings);
	}

	public void loadGeoserverListsByHarvestings(String xmlAllHarvestings) {

		reloadWmsGeoserverList(xmlAllHarvestings);
		reloadWfsGeoserverList(xmlAllHarvestings);
		this.xmlAllHarvestings = xmlAllHarvestings;
	}

	public ArrayList<String> getWmsGeoserverList() {
		return wmsGeoserverList;
	}

	public ArrayList<String> getWfsGeoserverList() {
		return wfsGeoserverList;
	}
}
