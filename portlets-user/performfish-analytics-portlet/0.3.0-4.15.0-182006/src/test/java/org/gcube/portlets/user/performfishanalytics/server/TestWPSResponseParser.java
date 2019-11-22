/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.gcube.portlets.user.performfishanalytics.server.util.DataMinerUtil;
import org.gcube.portlets.user.performfishanalytics.server.util.dataminer.DMServiceResponse;
import org.gcube.portlets.user.performfishanalytics.server.util.xml.WPSParserUtil;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 24, 2019
 */
public class TestWPSResponseParser {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

//		HashMap<String, String> mappings = new HashMap<String, String>();
////			mappings.put("xmlns", "http://www.opengis.net/sld");
//		mappings.put("wps", "http://www.opengis.net/wps/1.0.0");
//		mappings.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
//		mappings.put("ows", "http://www.opengis.net/ows/1.1");
//		mappings.put("lang", "en-US");
//		mappings.put("ogr", "http://ogr.maptools.org/");
//		mappings.put("gml", "http://www.opengis.net/gml");
//		mappings.put("d4science", "http://www.d4science.org");
//		mappings.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
//		NamespaceContextMap context = new NamespaceContextMap(mappings);

		InputStream wpsResponseStream =  WPSParserUtil.class.getResourceAsStream("WebProcessingService.xml");
		String wpsRS = IOUtils.toString(wpsResponseStream);
		DMServiceResponse resp = DataMinerUtil.parseResult(null, wpsRS);

		System.out.println(resp);

		//System.out.println(wpsRS);

		//String xpathExpression = "//sld:UserStyle[sld:IsDefault=1]/sld:Name"; //FIND DEFAULT STYLE NAME

		//String xpathExpression = "//wps:ProcessOutputs/wps:Output/wps:Data/wps:ComplexData/ogr:FeatureCollection/gml:featureMember/ogr:Result";

		//List<String> listWPSResult  = XpathParserUtil.getTextFromXPathExpression(context, wpsRS, xpathExpression);

//		Document doc = WPSParserUtil.inputStreamToW3CDocument(wpsResponseStream);
//		List<DataMinerOutputData> listResponse = WPSParserUtil.getListDataMinerOutputDataFromWPSResponse(doc);
//
//		for (DataMinerOutputData dataMinerOutputData : listResponse) {
//			System.out.println(dataMinerOutputData);
//		}

//		for (String result : listWPSResult) {
//			System.out.println("Result: "+result);
//		}


//		LinkedHashMap<String, String> exclusiveStyles = new LinkedHashMap<String, String>();
//
//		//DEFAULT STYLE IS FOUND
//		if(listWPSResult.size()>0 || !listWPSResult.get(0).isEmpty()){
//
//			String defaultStyle = listWPSResult.get(0);
//			exclusiveStyles.put(defaultStyle, defaultStyle);
//		}
//
//		xpathExpression = "//sld:UserStyle/sld:Name"; //FIND OTHER STYLES NAMES AND ADD INTO LIST
//		List<String> allStyles = XpathParserUtil.getTextFromXPathExpression(context, wpsResponse, xpathExpression);
//
//		for (String style : allStyles) {
//			exclusiveStyles.put(style, style);
//		}
//
//		listStylesNames.addAll(exclusiveStyles.keySet());
//
//
//		for (String string : listStylesNames) {
//			System.out.println("style: "+string);
		}
}
