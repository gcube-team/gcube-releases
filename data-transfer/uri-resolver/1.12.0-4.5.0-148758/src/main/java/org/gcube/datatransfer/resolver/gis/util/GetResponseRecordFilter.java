/**
 *
 */

package org.gcube.datatransfer.resolver.gis.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The Class GetResponseRecordFilter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jun 16, 2016
 */
public class GetResponseRecordFilter {

	/**
	 *
	 */
	private static final String UTF_8 = "UTF-8";
	/**
	 *
	 */
	private static final String A_PUBLIC_ID_PLEASE_IGNORE = "A public id please ignore";
	public static Logger logger = LoggerFactory.getLogger(GetResponseRecordFilter.class);

	/**
	 * Delete summary record.
	 *
	 * @param doc            the doc
	 * @param identifier            the identifier
	 * @return true, if successful
	 */
	private static boolean deleteSummaryRecord(Document doc, String identifier) {

		// <csw:SummaryRecord> list
		NodeList nodes = doc.getElementsByTagName("gmd:MD_Metadata");
		logger.trace("gmd:MD_Metadata are: " + nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			Element mdMetadata = (Element) nodes.item(i);
			// <dc:identifier>
			Element id = (Element) mdMetadata.getElementsByTagName("gmd:fileIdentifier").item(0);
			Element gco = (Element) id.getElementsByTagName("gco:CharacterString").item(0);
			String idValue = gco.getTextContent();
			logger.trace("Summary gmd:fileIdentifier is: " + idValue);
			if (idValue.equals(identifier)) {
				mdMetadata.getParentNode().removeChild(mdMetadata);
				logger.trace("Removed child " + idValue);
				return true;
			}
		}
		return false;
	}

	/**
	 * Override summary record.
	 *
	 * @param doc the doc
	 * @param identifier the identifier
	 * @return true, if successful
	 */
	private static boolean overrideSummaryRecord(Document doc, String identifier) {

		// <csw:SummaryRecord> list
		NodeList nodes = doc.getElementsByTagName("gmd:MD_Metadata");
		logger.debug("gmd:MD_Metadata are: " + nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			Element mdMetadata = (Element) nodes.item(i);

			// <dc:identifier>
			NodeList fileIdentifierLst = mdMetadata.getElementsByTagName("gmd:fileIdentifier");
			if(fileIdentifierLst==null || fileIdentifierLst.getLength()==0 || fileIdentifierLst.item(0)==null){
				logger.info("skipping identifier: " + identifier +" it has not fileidentifier");
				return false;
			}

			Element id = (Element) fileIdentifierLst.item(0);

			NodeList gcoLst =  id.getElementsByTagName("gco:CharacterString");
			if(gcoLst==null || gcoLst.getLength()==0 || gcoLst.item(0)==null){
				logger.info("skipping identifier: " + identifier +" it has not gco:CharacterString");
				return false;
			}

			Element gco = (Element) gcoLst.item(0);
			String idValue = gco.getTextContent();
			logger.trace("Summary gmd:fileIdentifier is: " + idValue);
			if (idValue!=null && idValue.equals(identifier)) {
				gco.setTextContent(A_PUBLIC_ID_PLEASE_IGNORE);
				logger.debug("Overrided child " + idValue);
				return true;
			}
		}
		return false;
	}


	/**
	 * Removes the summary ids by list ids.
	 *
	 * @param getRecordsResponse the get records response
	 * @param idsToRemove the ids to remove
	 * @return the input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static InputStream overrideResponseIdsByListIds(InputStream getRecordsResponse, List<String> idsToRemove) throws IOException {

		try {
			// logger.trace("getRecordsResponse is: "+IOUtils.toString(getRecordsResponse));
			BufferedInputStream bis = new BufferedInputStream(getRecordsResponse);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(bis);
			int override = 0;
			for (String identifier : idsToRemove) {
				if(overrideSummaryRecord(doc, identifier))
					override++;
			}
			logger.info("Overrided "+override +" node/s");

			//TODO IS IT POSSIBLE TO REMOVE?
			/*NodeList nodeList = doc.getElementsByTagName("csw:SearchResults");
			if(nodeList!=null && nodeList.item(0)!=null){
				Node nd = nodeList.item(0);
				// update staff attribute
				NamedNodeMap attr = nd.getAttributes();
				Node numberOfRecordsMatched = attr.getNamedItem("numberOfRecordsMatched");
				Node numberOfRecordsReturned = attr.getNamedItem("numberOfRecordsReturned");
				logger.trace("Old numberOfRecordsMatched: "+numberOfRecordsMatched.getTextContent());
				logger.trace("Old numberOfRecordsReturned: "+numberOfRecordsReturned.getTextContent());
				try{
					int oldValueM = Integer.parseInt(numberOfRecordsMatched.getTextContent());
					int oldValueR = Integer.parseInt(numberOfRecordsReturned.getTextContent());
					int newValueM = oldValueM-removed;
					int newValueR = oldValueR-removed;
					logger.trace("Updating numberOfRecordsMatched at: "+newValueM);
					logger.trace("Updating numberOfRecordsReturned at: "+newValueR);
					numberOfRecordsMatched.setTextContent(newValueM+"");
					numberOfRecordsReturned.setTextContent(newValueR+"");
				}catch (Exception e) {
					logger.warn("An error occurred during attribe numberOfRecordsMatched updating, skipping operation");
				}
			}*/
			return documentToInputStream(doc);
		}
		catch (Exception e) {
			logger.error("An error occurred during removing IDS by List: ", e);
			return getRecordsResponse;
		}
	}


	/**
	 * Document to input stream.
	 *
	 * @param xml the xml
	 * @return the input stream
	 * @throws Exception the exception
	 */
	private static final InputStream documentToInputStream(Document xml) throws Exception {
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		if(xml.getXmlEncoding()!=null && !xml.getXmlEncoding().isEmpty()){
			logger.info("Using encoding: "+xml.getXmlEncoding());
			tf.setOutputProperty(OutputKeys.ENCODING, xml.getXmlEncoding());
		}else{
			logger.info("Using default encoding: "+UTF_8);
			tf.setOutputProperty(OutputKeys.ENCODING, UTF_8);
		}
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StreamResult outputTarget = new StreamResult(outputStream);
		tf.transform(new DOMSource(xml), outputTarget);
		return new ByteArrayInputStream(outputStream.toByteArray());
//		return out.toString();
	}


	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		File file = new File("GetResponseRecords.xml");
		List<String> idsToRemove = new ArrayList<String>();
		idsToRemove.add("fao-species-map-sol");
		idsToRemove.add("fao-species-map-sop");
		try {
			InputStream is = GetResponseRecordFilter.overrideResponseIdsByListIds(new FileInputStream(file), idsToRemove);

			System.out.println(IOUtils.toString(is));
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
