/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.portlets.user.geoexplorer.shared.metadata.identification.KeywordsItem;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 3, 2013
 *
 */
public class HandleKeywordHtml {
	
	Document document;
	
	private Collection<KeywordsItem> collectionKeywords;

	private boolean isEmpty;
	
	public HandleKeywordHtml(Collection<KeywordsItem> collection){
		this.setCollectionKeywords(collection);
		this.isEmpty = handleKeywords()==null?true:false;
	}
	
	/**
	 * Creat a table element with two columns. The left column represents the
	 * name of a property/attribute. Iterates over all properties, and gets an
	 * element for each property. Stores this element in the right column of the
	 * table.
	 * 
	 * 
	 * @param object
	 *            The object
	 * @param level
	 *            The level of recursion
	 * @return An element representing this object
	 * @throws ParserConfigurationException 
	 */
	private Element handleKeywords() {

		try {
			document = setUpDocument();
			
		} catch (ParserConfigurationException e) {
			return null;
		}
		
		// Table element
		Element table = createElement(document, "table");
		table.setAttribute("id", "one-column-emphasis");
		Element colgroup = createElement(document, "colgroup");
		Element col = createElement(document, "col", "oce-first");
		colgroup.appendChild(col);
		table.appendChild(colgroup);

		Element tbody = createElement(document, "tbody");
		table.appendChild(tbody);

		if(collectionKeywords.size()==0){
			return null;
		}
		
		for (KeywordsItem   key: collectionKeywords) {
			// Table row element
			Element tr = createElement(document, "tr");
			Element td = createElement(document, "td");
			
			Element bold = createElement(document, "b");
			bold.setTextContent("Descriptive Keywords:");
			
			td.appendChild(bold);
			tr.appendChild(td);
			
			td = createElement(document, "td");
			td.setTextContent(getStringSeparator(key.getKeywords(), ','));
			tr.appendChild(td);
			
			tbody.appendChild(tr);

			if(key.getType()!=null){
				Element trType = createElement(document, "tr");
				Element tdType = createElement(document, "td");
				
				tdType.setTextContent("Type:");
				trType.appendChild(tdType);
				
				tdType = createElement(document, "td");
				tdType.setTextContent(key.getType());
				trType.appendChild(tdType);
				
				
				tbody.appendChild(trType);
			}
			
			if(key.getThesaurusName()!=null){
				
				if(key.getThesaurusName().getTitle()!=null){
					
					Element trThesaurus = createElement(document, "tr");
					Element tdThesaurus = createElement(document, "td");
					
					tdThesaurus.setTextContent("Thesaurus Title:");
					trThesaurus.appendChild(tdThesaurus);
					
					tdThesaurus = createElement(document, "td");
					tdThesaurus.setTextContent(key.getThesaurusName().getTitle());
					trThesaurus.appendChild(tdThesaurus);
					
					tbody.appendChild(trThesaurus);
				}
				
			}
			
		}
		
		table.appendChild(tbody);
		document.appendChild(table);
		return table;
	}
	
	private String getStringSeparator(Collection<String> listString, char separator) {

		String keysString = "";
		for (String key : listString) {
			keysString += key + separator + " ";
		}

		if (keysString.length() > 2)
			return keysString.substring(0, keysString.length() - 2);

		return keysString;
	}
	
	 /* Set up the DOM document. It is used to build the html document.
	 * 
	 * @return the document
	 * @throws ParserConfigurationException
	 */
	private Document setUpDocument() throws ParserConfigurationException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		documentBuilder = builderFactory.newDocumentBuilder();

		DOMImplementation implementation = documentBuilder.getDOMImplementation();

		return implementation.createDocument(null, null, null);
	}
	
	/**
	 * @throws TransformerException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public String getTable() throws TransformerException, IOException, ParserConfigurationException {

		if(isEmpty)
			return "";
		
		// transform the Document into a String
		DOMSource domSource = new DOMSource(document);
		StringWriter writer = new StringWriter();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult streamResult = new StreamResult(writer);
		transformer.transform(domSource, streamResult);
		writer.close();

		return writer.toString();
	}
	
	/**
	 * Helper method to create an element in document.
	 * 
	 * @param tagName
	 *            Tag name
	 * @return The created element
	 */
	private Element createElement(Document document,String tagName) {
		return document.createElement(tagName);
	}

	/**
	 * Helper method to create an element in document with the given class
	 * attribute. This is used to be able to style the html output.
	 * 
	 * @param tagName
	 *            Tag name
	 * @param cssClass
	 *            CSS class name
	 * @return The created element
	 */
	private Element createElement(Document document, String tagName, String cssClass) {
		Element element = document.createElement(tagName);
		element.setAttribute("class", cssClass);
		return element;
	}

	/**
	 * @return the collectionKeywords
	 */
	public Collection<KeywordsItem> getCollectionKeywords() {
		return collectionKeywords;
	}

	/**
	 * @param collectionKeywords the collectionKeywords to set
	 */
	public void setCollectionKeywords(Collection<KeywordsItem> collectionKeywords) {
		this.collectionKeywords = collectionKeywords;
	}

}
