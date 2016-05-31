/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.portlets.admin.gcubereleases.server.util.NamespaceContextMap;
import org.gcube.portlets.admin.gcubereleases.server.util.XpathParserUtil;
import org.gcube.portlets.admin.gcubereleases.shared.AccountingPackage;
import org.gcube.portlets.admin.gcubereleases.shared.Package;
import org.gcube.portlets.admin.gcubereleases.shared.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The Class EticsReportConverter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class EticsReportConverter {

	DocumentBuilder docBuilder;
	private Element root;
	private XPathHelper xpathHelper;
	private NamespaceContextMap nsContext;
	private String releaseId;
	private String input;

	public static Logger logger = LoggerFactory.getLogger(EticsReportConverter.class);
	
	/**
	 * Instantiates a new etics report converter.
	 *
	 * @param input the input
	 * @param releaseId the release id
	 */
	public EticsReportConverter(String input, String releaseId) {
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.input = input;
			InputSource inputSource = new InputSource(IOUtils.toInputStream(input));
			this.root = docBuilder.parse(inputSource).getDocumentElement();
			this.xpathHelper = new XPathHelper(root);
			HashMap<String, String> mappings = new HashMap<String, String>();
			mappings.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
			this.nsContext = new NamespaceContextMap(mappings);
			this.releaseId = releaseId;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Convert to list package.
	 *
	 * @param release the release
	 * @return the list
	 */
	public List<Package> convertToListPackage(Release release) {

		List<String> packages = xpathHelper.evaluate("/Packages/Package");
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(nsContext);
		
		List<Package> listPackage = new ArrayList<Package>();
		
		String xpathExpression = "/Package/";
		for (String pck : packages) {
			
			Package packageDao = new Package();
			packageDao.setReleaseIdRef(releaseId);
//			//System.out.println(pck);
			//System.out.println("\n\n");
			// String value = (String) xpath.evaluate("/groupID/text()", pck,
			// XPathConstants.STRING);
			String value = getFirstValue(pck, xpathExpression+"groupID");
			packageDao.setGroupID(value);
			//System.out.println("groupID: " + value);

			value = getFirstValue(pck, xpathExpression+"artifactID");
			packageDao.setArtifactID(value);
			//System.out.println("artifactID: " + value);
			
			value = getFirstValue(pck, xpathExpression+"version");
			packageDao.setVersion(value);
			//System.out.println("version: " + value);
			
			value = getFirstValue(pck, xpathExpression+"ID");
			packageDao.setID(value);
			//System.out.println("ID: " + value);
			
			value = getFirstValue(pck, xpathExpression+"URL");
			packageDao.setURL(value);
			//System.out.println("URL: " + value);
			
			value = getFirstValue(pck, xpathExpression+"javadoc");
			packageDao.setJavadoc(value);
			//System.out.println("javadoc: " + value);
			
			value = getFirstValue(pck, xpathExpression+"Status");
			packageDao.setStatus(value);
			//System.out.println("Status: " + value);
			
			value = getFirstValue(pck, xpathExpression+"Operation");
			packageDao.setOperation(value);
			//System.out.println("Operation: " + value);
			
			value = getFirstValue(pck, xpathExpression+"Timestamp");
			packageDao.setTimestamp(value);
			//System.out.println("Timestamp: " + value);
			
			value = getFirstValue(pck, xpathExpression+"wikidoc");
			if(value!=null && value.startsWith("https")){
				value = value.replaceFirst("https", "http");
			}
			packageDao.setWikidoc(value);
			//System.out.println("wikidoc: " + value);
			
			value = getFirstValue(pck, xpathExpression+"svnpath");
			packageDao.setSvnpath(value);
			//System.out.println("svnpath: " + value);
			
			value = getFirstValue(pck, xpathExpression+"ETICSRef");
			packageDao.setEticsRef(value);
			
			packageDao.setRelease(release);
			
			packageDao.setAccouting(new AccountingPackage(packageDao.getID(), 0, 0, 0, 0));
			//System.out.println("ETICSRef: " + value);
			
			listPackage.add(packageDao);
		}
		
		release.setPackagesNmb(listPackage.size());
		
		return listPackage;
	}

	/**
	 * Gets the first value.
	 *
	 * @param source the source
	 * @param xPathExpression the x path expression
	 * @return the first value
	 */
	private String getFirstValue(String source, String xPathExpression) {
		List<String> values = XpathParserUtil.getTextFromXPathExpression(
				nsContext, source, xPathExpression);

		if (values != null && values.size() > 0)
			return values.get(0);

		logger.info("value for " + xPathExpression + " is null or empty");
		return null;
	}

}
