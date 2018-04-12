/**
 *
 */

package org.gcube.datatransfer.resolver.gis.util;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNSearchResponse.GNMetadata;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HttpStatus;
import org.gcube.datatransfer.resolver.gis.GeonetworkInstance;
import org.gcube.datatransfer.resolver.gis.exception.GeonetworkInstanceException;
import org.gcube.datatransfer.resolver.gis.geonetwork.GNAuthentication;
import org.gcube.datatransfer.resolver.gis.geonetwork.HTTPCallsUtils;
import org.gcube.datatransfer.resolver.gis.geonetwork.HTTPCallsUtils.HttpResponse;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * The Class GetResponseRecordFilter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 5, 2017
 */
public class GetResponseRecordFilter {

	/**
	 *
	 */
	private static final String XML_METADATA_GET_SERVICE = "srv/en/xml.metadata.get";
	/**
	 *
	 */
	private static final String UTF_8 = "UTF-8";
	/**
	 *
	 */
	private static final String MESSAGE_TO_REPLACED_ID = "Replaced ID please ignore";
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
	 * Gets the text content strings for tag name.
	 *
	 * @param doc the doc
	 * @param tagName the tag name
	 * @return the text content strings for tag name
	 */
	public static List<String> getTextContentStringsForTagName(Document doc, String tagName) {

		// <csw:SummaryRecord> list
		NodeList nodes = doc.getElementsByTagName(tagName);
		List<String> fileIds = new ArrayList<String>(nodes.getLength());
		logger.trace(tagName +" is/are: " + nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			Node mdId = nodes.item(i);
			mdId.getTextContent();
			fileIds.add(mdId.getTextContent().trim());
		}
		logger.trace("Returning "+fileIds.size()+" item/s for tagname: "+tagName);
		return fileIds;
	}


	/**
	 * Override summary record.
	 *
	 * @param doc the doc
	 * @param identifier the identifier
	 * @param messageToReplace the message to replace
	 * @return true, if successful
	 */
	private static boolean overrideSummaryRecord(Document doc, String identifier, String messageToReplace) {

		// <csw:SummaryRecord> list
		NodeList nodes = doc.getElementsByTagName("gmd:MD_Metadata");
		logger.trace("gmd:MD_Metadata are: " + nodes.getLength());
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
				String msg = messageToReplace==null?MESSAGE_TO_REPLACED_ID:messageToReplace;
				gco.setTextContent(msg);
				logger.debug("Overrided child " + idValue);
				return true;
			}
		}
		return false;
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
			logger.error("Error converting input stream to W3C document", e);
			throw new Exception(e);
		}
	}



	/**
	 * Override response ids by list ids.
	 *
	 * @param getRecordsResponse the get records response
	 * @param idsToRemove the ids to remove
	 * @param messageToWrite the message to replace the right ID in the response.
	 * @return the input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static InputStream overrideResponseIdsByListIds(InputStream getRecordsResponse, List<String> idsToRemove, String messageToWrite) throws IOException {

		try {
			Document doc = inputStreamToW3CDocument(getRecordsResponse);
			int override = 0;
			for (String identifier : idsToRemove) {
				if(overrideSummaryRecord(doc, identifier, messageToWrite))
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
			return w3CDocumentToInputStream(doc);
		}
		catch (Exception e) {
			logger.error("An error occurred during removing IDS by List: ", e);
			return getRecordsResponse;
		}
	}

	/**
	 * W3 c document to input stream.
	 *
	 * @param xml the xml
	 * @return the input stream
	 * @throws Exception the exception
	 */
	public static final InputStream w3CDocumentToInputStream(Document xml) throws Exception {
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
	 * File identifiers to ids.
	 *
	 * @param UUIDs the UUI ds
	 * @param scope the scope
	 * @return the list
	 * @throws GeonetworkInstanceException the geonetwork instance exception
	 */
	public static List<Long> fileIdentifiersToIds(List<String> UUIDs, String scope) throws GeonetworkInstanceException{

		GNSearchRequest req=new GNSearchRequest();
		GeonetworkInstance gIntance = new GeonetworkInstance(scope, true, LoginLevel.ADMIN, Type.SCOPE);
		List<Long> ids = new ArrayList<Long>();
		for (String uuid : UUIDs) {
			logger.debug("Searching metadata id for UUID: "+uuid);
			try {
				req.addParam(GNSearchRequest.Param.any,uuid);
				GNSearchResponse resp = gIntance.getGeonetworkPublisher().query(req);
				Iterator<GNMetadata> iterator=resp.iterator();
				boolean found = false;
				while(iterator.hasNext()){
					Long metaId = iterator.next().getId();
					logger.debug("MedataID found: "+metaId + " (for the UUID: "+uuid+")");
					found = true;
					//System.out.println(metaId);
					ids.add(metaId);
				}

				if(!found)
					logger.warn("Metadata ID not found for UUID: "+uuid);
			}
			catch (GNLibException | GNServerException
							| MissingServiceEndpointException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ids;
	}



	/**
	 * Perform the request xml metadata get.
	 *
	 * @param fileIdentifier the file identifier is the UUID
	 * @param geonetworkURL the geonetwork url
	 * @param user the user
	 * @param pwd the pwd
	 * @return the response as string
	 */
	public static String requestXmlMetadataGet(String fileIdentifier, String geonetworkURL, String user, String pwd){

		if(fileIdentifier==null || fileIdentifier.isEmpty())
			return null;

		HTTPCallsUtils httpCall = new HTTPCallsUtils();
		String queryURL = geonetworkURL.endsWith("/")?geonetworkURL:geonetworkURL+"/";
		try {
			queryURL+=XML_METADATA_GET_SERVICE+"?uuid="+URLEncoder.encode(fileIdentifier, "UTF-8");
		}
		catch (UnsupportedEncodingException e1) {
			queryURL+=XML_METADATA_GET_SERVICE+"?uuid="+fileIdentifier;
		}

		String theResponse = null;
		try {
			logger.trace("Performing query: "+queryURL);
			//IT IS NEED ADMIN LOGIN IN ON GN TO PERFORM SUCH QUERY
			boolean authorized = GNAuthentication.login(httpCall, geonetworkURL, user, pwd);
			HttpResponse response = httpCall.get(queryURL);
			if(response.getStatus()==HttpStatus.SC_OK){
				theResponse = response.getResponse();
			}
		}
		catch (MalformedURLException e) {
			logger.debug("MalformedURLException error on getting the tag 'ownername' for the file identifier: "+fileIdentifier, e);
		}
		catch (Exception e) {
			logger.debug("Error on getting the tag 'ownername' for the file identifier: "+fileIdentifier, e);
		}

		return theResponse;

	}


	/**
	 * Gets the meta category by file identifier.
	 *
	 * @param fileIdentifier the file identifier
	 * @param geonetworkURL the geonetwork url
	 * @param user the user
	 * @param pwd the pwd
	 * @return the meta category by file identifier
	 */
	public static String getMetaCategoryByFileIdentifier(String fileIdentifier, String geonetworkURL, String user, String pwd){

		return getMetadataValueByFileIdentifier(fileIdentifier, geonetworkURL, user, pwd, "category");
	}

	/**
	 * Gets the meta owner name by file identifier.
	 *
	 * @param fileIdentifier the file identifier
	 * @param geonetworkURL the geonetwork url
	 * @param user the user
	 * @param pwd the pwd
	 * @return the meta owner name by file identifier
	 */
	public static String getMetaOwnerNameByFileIdentifier(String fileIdentifier, String geonetworkURL, String user, String pwd){

		return getMetadataValueByFileIdentifier(fileIdentifier, geonetworkURL, user, pwd, "ownername");

	}


	/**
	 * Gets the metadata value by file identifier.
	 *
	 * @param fileIdentifier the file identifier
	 * @param geonetworkURL the geonetwork url
	 * @param user the user
	 * @param pwd the pwd
	 * @param metadataName the metadata name
	 * @return the metadata value by file identifier
	 */
	private static String getMetadataValueByFileIdentifier(String fileIdentifier, String geonetworkURL, String user, String pwd, String metadataName){

		String response = requestXmlMetadataGet(fileIdentifier, geonetworkURL, user, pwd);

		if(response==null || response.isEmpty())
			return null;

		InputStream stream = new ByteArrayInputStream(response.getBytes());
		Document doc;
		try {

			doc = GetResponseRecordFilter.inputStreamToW3CDocument(stream);
			List<String> metadataValues = GetResponseRecordFilter.getTextContentStringsForTagName(doc, metadataName);

			if(metadataValues==null || metadataValues.isEmpty()){
				logger.debug("No "+metadataName+" found, returning null");
				return null;
			}

			logger.debug("found and returning value of "+metadataName+": '"+metadataValues.get(0) + "' for file identifier: "+fileIdentifier);
			return metadataValues.get(0);
		}
		catch (Exception e) {
			logger.debug("Error: ",e);
			return null;
		}
	}

//	/**
//	 * The main method.
//	 *
//	 * @param args the arguments
//	 */
//	public static void main(String[] args) {
//
//		File file = new File("GetResponseRecords.xml");
//		List<String> idsToRemove = new ArrayList<String>();
//		idsToRemove.add("39d7207e-3bec-4086-98c4-8d9c787db9c4");
//		idsToRemove.add("geo_fea_trenches");
//
//		String scope = "/d4science.research-infrastructures.eu";
////		try {
////			InputStream is = GetResponseRecordFilter.overrideResponseIdsByListIds(new FileInputStream(file), idsToRemove);
////			System.out.println(IOUtils.toString(is));
////		}
////		catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////
//		try {
//
//			long start = System.currentTimeMillis();
//			Document doc = GetResponseRecordFilter.inputStreamToW3CDocument(new FileInputStream(file));
//			List<String> fileIdentifiers = GetResponseRecordFilter.getTextContentStringsForTagName(doc, "gmd:fileIdentifier");
//			System.out.println("UUIDs: "+fileIdentifiers);
//			HTTPCallsUtils httpUtils = new HTTPCallsUtils();
//
//			ScopeProvider.instance.set(scope);
//			GeonetworkInstance gIntance = new GeonetworkInstance(scope, true, LoginLevel.ADMIN, Type.SCOPE);
//			Configuration config = gIntance.getGeonetworkPublisher().getConfiguration();
//
//			//System.out.println(GetResponseRecordFilter.getMetaOwnerNameByFileIdentifier(fileIdentifiers.get(0), config.getGeoNetworkEndpoint(),config.getAdminAccount().getUser(), config.getAdminAccount().getPassword()));
//
//			List<String> owners = new ArrayList<String>();
//
//			for (String fileId : fileIdentifiers) {
//				owners.add(GetResponseRecordFilter.getMetaOwnerNameByFileIdentifier(fileId, config.getGeoNetworkEndpoint(),config.getAdminAccount().getUser(), config.getAdminAccount().getPassword()));
//			}
//
//			for (String ow : owners) {
//				System.out.println(ow);
//			}
//
//			System.out.println("End in ms: "+(System.currentTimeMillis()-start));
//
////			System.out.println("IDs: "+ids.toString());
//		}
//		catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
