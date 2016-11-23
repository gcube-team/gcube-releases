package org.gcube.datatransformation.datatransformationlibrary.model;

import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * <tt>Source</tt> of a transformationUnit unit is the composition of the content type that the transformationUnit unit expects as well as the input data which will be transformed.
 * </p>
 */
public class Source extends TransformationRuleElement {

	private String inputID;
	
	/**
	 * Sets the input data handler id.
	 * @param id The input data handler id.
	 */
	public void setInputID(String id){
		this.inputID=id;
	}
	
	/**
	 * Returns the input data handler id.
	 * @return The input data handler id.
	 */
	public String getInputID(){
		return inputID;
	}
	
	/**
	 * Returns the input data handler.
	 * @return The input data handler.
	 * @throws Exception If the does not exist.
	 */
	public DataSource getInput() throws Exception{
		return (DataSource)transformationUnit.getDataHandler(this.inputID);
	}
	
	protected void fromDOM(Element sourceElm) {
		Element outputElm;
		if((outputElm=(Element)sourceElm.getElementsByTagName(XMLDefinitions.ELEMENT_input).item(0))!=null){
			this.inputID=outputElm.getAttribute(XMLDefinitions.ATTRIBUTE_IOID);
		}
		
		Element contentformatElm;
		if((contentformatElm=(Element)sourceElm.getElementsByTagName(XMLDefinitions.ELEMENT_contenttype).item(0))!=null){
			contentType = new ContentType();
			contentType.fromDOM(contentformatElm);
		}
	}

	protected void toDOM(Element sourcesElm) {
		Document doc = sourcesElm.getOwnerDocument();
		
		Element sourceElm = doc.createElement(XMLDefinitions.ELEMENT_source);
		
		Element inputElm = doc.createElement(XMLDefinitions.ELEMENT_input);
		Attr ioID = doc.createAttribute(XMLDefinitions.ATTRIBUTE_IOID);
		ioID.setTextContent(this.inputID);
		inputElm.setAttributeNode(ioID);
		sourceElm.appendChild(inputElm);
		
		contentType.toDOM(sourceElm);
		
		sourcesElm.appendChild(sourceElm);
	}
}
