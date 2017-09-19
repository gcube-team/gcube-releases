/**
 *
 */
package org.gcube.portlets.admin.gcubereleases;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.portlets.admin.gcubereleases.server.converter.EticsReportConverter;
import org.gcube.portlets.admin.gcubereleases.server.util.HttpCallerUtil;
import org.gcube.portlets.admin.gcubereleases.shared.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 15, 2016
 */
public class ConvertingPackage {

	static Logger logger = LoggerFactory.getLogger(ConvertingPackage.class);
	static String releaseID = "org.gcube.4-1-0";
	static String releaseName = "gCube 4.1.0";
	static String releaseURL = "http://eticsbuild2.research-infrastructures.eu/BuildReport/bdownload/AllBuilds/org.gcube.4-1-0/BUILD_51/reports/distribution/distribution.xml";

	public static void main(String[] args) {

		// UNCOMMENT THIS FOR TEST


//		HashMap<String, String> mappings = new HashMap<String, String>();
		// // mappings.put("xmlns", "http://www.opengis.net/sld");
		// mappings.put("sld", "http://www.opengis.net/sld");
		// mappings.put("ogc", "http://www.opengis.net/ogc");
		// mappings.put("gml", "http://www.opengis.net/gml");
//		mappings.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
//		NamespaceContextMap context = new NamespaceContextMap(mappings);

		//String url = "http://grids16.eng.it/BuildReport/bdownload/Recent_Builds/org.gcube.3-5-0/latest/reports/distribution/distribution.xml";
		HttpCallerUtil httpCaller = new HttpCallerUtil(releaseURL, "", "");
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

//			for (String string : packages) {
//				System.out.println("pkg: " + string);
//			}

			logger.info("Converting packages...");
			Release release = new Release(releaseID, releaseName, releaseURL, null);
			EticsReportConverter converter = new EticsReportConverter(response, release.getId());
			List<org.gcube.portlets.admin.gcubereleases.shared.Package> listPackage = converter.convertToListPackage(release);

			for (org.gcube.portlets.admin.gcubereleases.shared.Package package1 : listPackage) {
				logger.info(package1.getGitHubPath());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}
}
