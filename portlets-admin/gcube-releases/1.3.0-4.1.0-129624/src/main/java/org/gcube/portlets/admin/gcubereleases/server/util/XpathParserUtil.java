/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.util;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 7, 2013
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * The Class XpathParserUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class XpathParserUtil {
	

	/**
	 * 
	 */
	private static final String UTF_8 = "UTF-8";

	/**
	 * Gets the text from x path expression.
	 *
	 * @param context the context
	 * @param source the source
	 * @param xpathExpression the xpath expression
	 * @return the text from x path expression
	 */
	public static List<String> getTextFromXPathExpression(NamespaceContextMap context, InputStream source, String xpathExpression){
		
		List<String> list = new ArrayList<String>();
		try {

			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(context);
			XPathExpression xPathExpression = xpath.compile(xpathExpression);
			InputSource inputSource = new InputSource(source);
			
//			System.out.println(xml);
//			System.out.println(xpathExpression);
//			System.out.println(inputSource.toString());
			
			NodeList nodes = (NodeList) xPathExpression.evaluate(inputSource, XPathConstants.NODESET);

			for (int i = 0; i<nodes.getLength(); i++) {
				Node node = nodes.item(i);
				list.add(node.getTextContent());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	
	/**
	 * Gets the text from x path expression.
	 *
	 * @param context the context
	 * @param source the source
	 * @param xpathExpression the xpath expression
	 * @return the text from x path expression
	 */
	public static List<String> getTextFromXPathExpression(NamespaceContextMap context, String source, String xpathExpression){
		
		List<String> list = new ArrayList<String>();
		try {

			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(context);
			XPathExpression xPathExpression = xpath.compile(xpathExpression);
			InputSource inputSource = new InputSource(IOUtils.toInputStream(source));

			NodeList nodes = (NodeList) xPathExpression.evaluate(inputSource, XPathConstants.NODESET);

			for (int i = 0; i<nodes.getLength(); i++) {
				Node node = nodes.item(i);
				list.add(node.getTextContent());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	
	
	
	/**
	 * String to input stream.
	 *
	 * @param text the text
	 * @return the input stream
	 */
	public static InputStream stringToInputStream(String text){
		return IOUtils.toInputStream(text);
	}
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		
		//UNCOMMENT THIS FOR TEST
	
//		HashMap<String, String> mappings = new HashMap<String, String>();
////		mappings.put("xmlns", "http://www.opengis.net/sld");
//		mappings.put("sld", "http://www.opengis.net/sld");
//		mappings.put("ogc", "http://www.opengis.net/ogc");
//		mappings.put("gml", "http://www.opengis.net/gml");
//		mappings.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
//		NamespaceContextMap context = new NamespaceContextMap(mappings);
//		
//		List<String> listStylesNames = new ArrayList<String>();
//		
//		InputStream inputStream =  XpathParserUtil.class.getResourceAsStream("styles.sld+xml");
//		
//		
//		String xmlGetStyles = IOUtils.toString(inputStream);
//		
//		String xpathExpression = "//sld:UserStyle[sld:IsDefault=1]/sld:Name"; //FIND DEFAULT STYLE NAME
//
//		List<String> defaultStylesList  = XpathParserUtil.getTextFromXPathExpression(context, xmlGetStyles, xpathExpression);
//		
//
//		LinkedHashMap<String, String> exclusiveStyles = new LinkedHashMap<String, String>();
//		
//		//DEFAULT STYLE IS FOUND
//		if(defaultStylesList.size()>0 || !defaultStylesList.get(0).isEmpty()){
//			
//			String defaultStyle = defaultStylesList.get(0);
//			exclusiveStyles.put(defaultStyle, defaultStyle);
//		}
//		
//		xpathExpression = "//sld:UserStyle/sld:Name"; //FIND OTHER STYLES NAMES AND ADD INTO LIST
//		List<String> allStyles = XpathParserUtil.getTextFromXPathExpression(context, xmlGetStyles, xpathExpression);
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
//		}
		
	}

}