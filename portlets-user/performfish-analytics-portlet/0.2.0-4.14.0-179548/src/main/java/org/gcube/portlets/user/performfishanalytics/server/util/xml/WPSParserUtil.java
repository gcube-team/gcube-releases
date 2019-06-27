/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util.xml;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.gcube.portlets.user.performfishanalytics.server.util.dataminer.DataMinerOutputData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * The Class WPSParserUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 24, 2019
 */
public class WPSParserUtil {

	protected static Logger log = LoggerFactory.getLogger(WPSParserUtil.class);

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
				System.out.println(node.getNodeName() + " " +node.getTextContent());
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
			log.error("Parsing error: ",e);
		}

		return list;
	}



	/**
	 * Gets the list data miner output data from wps response.
	 *
	 * @param doc the doc
	 * @return the list data miner output data from wps response
	 */
	public static List<DataMinerOutputData> getListDataMinerOutputDataFromWPSResponse(Document doc) {

		List<DataMinerOutputData> listOutput = new ArrayList<DataMinerOutputData>();

		// <csw:SummaryRecord> list
		NodeList nodes = doc.getElementsByTagName("ogr:Result");
		log.info("ogr:Result are: " + nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			Element mdMetadata = (Element) nodes.item(i);

			DataMinerOutputData dmOutput = new DataMinerOutputData();
			Map<String, String> map = new HashMap<String, String>();

			String tagSearch = "d4science:Data";
			NodeList nodeList = mdMetadata.getElementsByTagName(tagSearch);
			if(nodeList==null || nodeList.getLength()==0 || nodeList.item(0)==null){
				log.info("not found "+tagSearch);
			}else{
				dmOutput.setPublicURL(nodeList.item(0).getTextContent());
				map.put(tagSearch, nodeList.item(0).getTextContent());
			}

			tagSearch = "d4science:Description";
			nodeList = mdMetadata.getElementsByTagName(tagSearch);
			if(nodeList==null || nodeList.getLength()==0 || nodeList.item(0)==null){
				log.info("not found "+tagSearch);
			}else{
				dmOutput.setFileDescription(nodeList.item(0).getTextContent());
				map.put(tagSearch, nodeList.item(0).getTextContent());
			}

			tagSearch = "d4science:MimeType";
			nodeList = mdMetadata.getElementsByTagName(tagSearch);
			if(nodeList==null || nodeList.getLength()==0 || nodeList.item(0)==null){
				log.info("not found "+tagSearch);
			}else{
				dmOutput.setMimeType(nodeList.item(0).getTextContent());
				map.put(tagSearch, nodeList.item(0).getTextContent());
			}

			dmOutput.setDataMinerOutputData(map);
			listOutput.add(dmOutput);


		}
		return listOutput;
	}



	/**
	 * Input stream to w3 c document.
	 *
	 * @param getRecordsResponse the get records response
	 * @return the document
	 * @throws Exception the exception
	 */
	public static Document inputStreamToW3CDocument(InputStream getRecordsResponse) throws Exception{

		try {
			BufferedInputStream bis = new BufferedInputStream(getRecordsResponse);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(bis);
		}
		catch (Exception e) {
			log.error("Error converting input stream to W3C document", e);
			throw new Exception(e);
		}
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

}
