/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 7, 2013
 *
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.portlets.admin.gcubereleases.server.util.HttpCallerUtil;
import org.gcube.portlets.admin.gcubereleases.server.util.NamespaceContextMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2015
 * 
 */
public class TestReportConnectionUtil {


	public static void main(String[] args) throws IOException {

		// UNCOMMENT THIS FOR TEST

		
		HashMap<String, String> mappings = new HashMap<String, String>();
		// // mappings.put("xmlns", "http://www.opengis.net/sld");
		// mappings.put("sld", "http://www.opengis.net/sld");
		// mappings.put("ogc", "http://www.opengis.net/ogc");
		// mappings.put("gml", "http://www.opengis.net/gml");
		mappings.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
		NamespaceContextMap context = new NamespaceContextMap(mappings);

		String url = "http://grids16.eng.it/BuildReport/bdownload/Recent_Builds/org.gcube.3-5-0/latest/reports/distribution/distribution.xml";
		HttpCallerUtil httpCaller = new HttpCallerUtil(url, "", "");
		String response = "";

		try {
			response = httpCaller.callGet("", null);
//			System.out.println(response);
			
			DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource inputSource = new InputSource(IOUtils.toInputStream(response));
			Node node = docBuilder.parse(inputSource).getDocumentElement();
			XPathHelper helper = new XPathHelper(node);
			
			List<String> packages = helper.evaluate("/Packages/Package");

			// String xpathExpression =
			// "//sld:UserStyle[sld:IsDefault=1]/sld:Name"; //FIND DEFAULT STYLE
			// NAME

			// List<String> defaultStylesList =
			// XpathParserUtil.getTextFromXPathExpression(context, xmlGetStyles,
			// xpathExpression);

			for (String string : packages) {
				System.out.println("pkg: " + string);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}