package gr.uoa.di.madgik.gcubesearchlibrary.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.XMLParsingException;
import gr.uoa.di.madgik.gcubesearchlibrary.model.beans.CollectionBean;
import gr.uoa.di.madgik.gcubesearchlibrary.model.beans.ObjectInfoBean;
import gr.uoa.di.madgik.gcubesearchlibrary.model.beans.ResultBean;
import gr.uoa.di.madgik.gcubesearchlibrary.model.beans.SearchStatusBean;
import gr.uoa.di.madgik.gcubesearchlibrary.model.beans.FieldBean;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.FileUtils;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.PropertiesConstants;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.XMLUtils;

/**
 * XML Parser class
 * Parses the responses from the ASL servlet requests
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class XMLParser {

	/**
	 * Parses the SignIn response
	 * 
	 * @param response The XML response
	 * @return The session ID or null
	 */
	public static String parseSignInResponse(String response) {
		String sessionID = null;
		try {
			Document doc = XMLUtils.parseXMLFileToDOM(response);
			XPath xpath = XPathFactory.newInstance().newXPath();
			sessionID = xpath.evaluate("//jsessionid/text()", doc);
		} catch (Exception e) {
		} 
		return sessionID;
	}

	/**
	 * Parses the VREs list response
	 * @param response The XML response
	 * @return A list with the VREs
	 */
	public static List<String> parseVREsResponse(String response) {
		List<String> VREs = new ArrayList<String>();
		try {
			Document doc = XMLUtils.parseXMLFileToDOM(response);
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList list = (NodeList)xpath.evaluate("//VRE/text()", doc, XPathConstants.NODESET);
			if (list != null) {
				for(int i=0; i < list.getLength(); i++ ) {
					VREs.add(XMLUtils.createStringFromDomTree(list.item(i)));
				}
			}
		} catch (Exception e) {

		} 
		return VREs;
	}

	/**
	 * Parses the Collections response
	 * 
	 * @param response The XML response
	 * @return A HashMap with the collections in groups
	 * @throws XMLParsingException 
	 */
	public static HashMap<String, List<CollectionBean>> parseCollectionsResponse(String response) throws XMLParsingException {
		HashMap<String, List<CollectionBean>> collections = new HashMap<String, List<CollectionBean>>();

		try {
			Document doc = XMLUtils.parseXMLFileToDOM(response);
			NodeList list = doc.getElementsByTagName("CollectionGroup");
			if (list != null) {
				for(int i=0; i < list.getLength(); i++ ) {
					Element collectionGroupNode = (Element)list.item(i);
					String groupName = collectionGroupNode.getAttribute("name");
					NodeList colList = collectionGroupNode.getElementsByTagName("Collection");
					List<CollectionBean> collectionsList = new ArrayList<CollectionBean>();
					if (colList != null) {

						for(int j=0; j < colList.getLength(); j++ ) {
							Element collectionNode = (Element)colList.item(j);
							String collectionName = null;
							String collectionID = null;
							NodeList nl = collectionNode.getElementsByTagName("name");
							if(nl != null && nl.getLength() > 0) {
								Element el = (Element)nl.item(0);
								collectionName = el.getFirstChild().getNodeValue();
							}
							nl = collectionNode.getElementsByTagName("colId");
							if(nl != null && nl.getLength() > 0) {
								Element el = (Element)nl.item(0);
								collectionID = el.getFirstChild().getNodeValue();
							}
							//System.out.println("Collections name and ID -> " + collectionName + " - " + collectionID);
							collectionsList.add(new CollectionBean(collectionName, collectionID));
						}
					}
					collections.put(groupName, collectionsList);
				}
			}
		} catch (Exception e) {
			throw new XMLParsingException(e.getMessage(), e.getCause());
		}
		return collections;
	}

	/**
	 * Parses the response of the CollectionInfos
	 * 
	 * @param response The XML response
	 * @return A @SearchStatusBean object containing all the needed information
	 * @throws XMLParsingException 
	 */
	public static SearchStatusBean parseCollectionInfoResponse(String response) throws XMLParsingException {
		boolean isFullTextSupported = false;
		FieldBean fullTextField = null;
		List<FieldBean> searchableFields = new ArrayList<FieldBean>(); 

		try {
			Document doc = XMLUtils.parseXMLFileToDOM(response);
			NodeList list = doc.getElementsByTagName("searchField");
			if (list != null) {
				for (int i=0; i<list.getLength(); i++) {
					Element searchFieldElement = (Element)list.item(i);
					String name = null;
					String id = null;

					NodeList nl = searchFieldElement.getElementsByTagName("name");
					if(nl != null && nl.getLength() > 0) {
						Element el = (Element)nl.item(0);
						name = el.getFirstChild().getNodeValue();
					}
					nl = searchFieldElement.getElementsByTagName("id");
					if(nl != null && nl.getLength() > 0) {
						Element el = (Element)nl.item(0);
						id = el.getFirstChild().getNodeValue();
					}

					//System.out.println("NAME AND ID --> " + name + " - " + id);
					FieldBean field = new FieldBean(name, id, false);
					searchableFields.add(field);

					if (name.trim().equals(PropertiesConstants.FULL_TEXT_FIELD)) {
						isFullTextSupported = true;
						fullTextField = new FieldBean(name, id, false);
					}
				}
				SearchStatusBean searchStatus = new SearchStatusBean(isFullTextSupported, fullTextField, searchableFields);
				return searchStatus;
			}
		} catch (Exception e) {
			throw new XMLParsingException(e.getMessage(), e.getCause());
		}
		return null;
	}

	/**
	 * Parses the Search results
	 * 
	 * @param results The XML response with the results
	 * @return A list with @ResultBean objects containing the results
	 * @throws XMLParsingException 
	 */
	public static List<ResultBean> parseSearchResults(String results) throws XMLParsingException {
		List<ResultBean> resultsRecords = new ArrayList<ResultBean>();
		try {
			Document doc = XMLUtils.parseXMLFileToDOM(results);
			NodeList list = doc.getElementsByTagName("RSRecord");
			if (list != null) {

				List<String> shortRRFields = getShortResultRecordFields();

				// for each result record get the fields
				for (int i=0; i<list.getLength(); i++) {
					List<FieldBean> resultRecordFields = new ArrayList<FieldBean>();
					String objectID = null;
					String collectionID = null;
					//System.out.println("Result Record " + (i+1));
					Element rsRecord = (Element)list.item(i);
					NodeList fieldsList = rsRecord.getElementsByTagName("field");
					if (fieldsList != null) {
						for (int j=0; j<fieldsList.getLength(); j++) {

							Element fieldNode = (Element)fieldsList.item(j);

							String name = null;
							String value = null;
							boolean includeThisFieldInResult = true;

							NodeList nl = fieldNode.getElementsByTagName("fieldName");
							if(nl != null && nl.getLength() > 0) {
								Element el = (Element)nl.item(0);
								name = el.getFirstChild().getNodeValue();
							}
							nl = fieldNode.getElementsByTagName("fieldValue");
							if(nl != null && nl.getLength() > 0) {
								Element el = (Element)nl.item(0);
								if (el.getFirstChild() != null)
									value = el.getFirstChild().getNodeValue();
								else
									includeThisFieldInResult = false;
							}
							//System.out.println("Got NAME --> " + name + " and VALUE ---> " + value);

							if (name != null && !name.trim().equals("null")) {
								// The objectID is the URI: cms://a/b
								// Do not display it as metadata
								if (name.equals(PropertiesConstants.OBJECT_ID_FIELD)) {
									objectID = value;
									includeThisFieldInResult = false;
								}
								else if (name.equals(PropertiesConstants.COLLECTION_ID_FIELD)) {
									collectionID = value;
								}
								// Do not display it as metadata
								else if (name.equals(PropertiesConstants.SNIPPET)) {
									//includeThisFieldInResult = false;
									value = StringEscapeUtils.unescapeHtml(value);
								}

								boolean isPartOfShortRR = false;
								if (shortRRFields.contains(name))
									isPartOfShortRR = true;

								// Include in the result record only the needed fields
								if (includeThisFieldInResult) {
									FieldBean f = new FieldBean(name, value, isPartOfShortRR);
									resultRecordFields.add(f);
								}
								
							}
							
						}
					}
					resultsRecords.add(new ResultBean(resultRecordFields, objectID, collectionID));
				}
				
			}
		} catch (Exception e) {
			throw new XMLParsingException(e.getMessage(), e.getCause());
		} 
		return resultsRecords;
	}

	/**
	 * Parses the Object's Info response to get the mime type
	 * 
	 * @param response The XML response
	 * @return Object's mime type
	 * @throws XMLParsingException
	 */
	public static String parseObjectInfoResponseToGetMime(String response) throws XMLParsingException {
		String mime = null;
		Document doc;
		try {
			doc = XMLUtils.parseXMLFileToDOM(response);
			NodeList nl = doc.getElementsByTagName("MimeType");
			if(nl != null && nl.getLength() > 0) {
				Element el = (Element)nl.item(0);
				mime = el.getFirstChild().getNodeValue();
			}
		} catch (Exception e) {
			throw new XMLParsingException(e.getMessage(), e.getCause());
		}

		return mime;
	}

	/**
	 * Parses the Object's Info response
	 * 
	 * @param response The XML response
	 * @return @ObjectInfoBean
	 * @throws XMLParsingException
	 */
	public static ObjectInfoBean parseObjectInfoResponseToGetObjectInfo(String response) throws XMLParsingException {
		String mime = null;
		String length = null;
		String name = null;
		String objectID = null;
		Document doc;
		try {
			doc = XMLUtils.parseXMLFileToDOM(response);
			NodeList nl = doc.getElementsByTagName("MimeType");
			if(nl != null && nl.getLength() > 0) {
				Element el = (Element)nl.item(0);
				mime = el.getFirstChild().getNodeValue();
			}
			nl = doc.getElementsByTagName("Length");
			if(nl != null && nl.getLength() > 0) {
				Element el = (Element)nl.item(0);
				length = el.getFirstChild().getNodeValue();
			}
			nl = doc.getElementsByTagName("Name");
			if(nl != null && nl.getLength() > 0) {
				Element el = (Element)nl.item(0);
				name = el.getFirstChild().getNodeValue();
			}
			nl = doc.getElementsByTagName("ObjectId");
			if(nl != null && nl.getLength() > 0) {
				Element el = (Element)nl.item(0);
				objectID = el.getFirstChild().getNodeValue();
			}
			ObjectInfoBean objectBean = new ObjectInfoBean(name, objectID, mime, length);
			return objectBean;
		} catch (Exception e) {
			throw new XMLParsingException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Retrieves the names of the fields to be used in the short result record
	 * 
	 * @return A list with the fields' names
	 */
	private static List<String> getShortResultRecordFields() {
		List<String> fields = new ArrayList<String>();;
		try {
			int count = Integer.parseInt(FileUtils.getPropertyValue(PropertiesConstants.FieldsPropertiesFileName, PropertiesConstants.FIELDS_COUNT));
			for(int i=0; i<count; i++)
				fields.add(FileUtils.getPropertyValue(PropertiesConstants.FieldsPropertiesFileName, PropertiesConstants.FIELD + i));
		} catch (Exception e) {

		}	
		return fields;
	}

}
