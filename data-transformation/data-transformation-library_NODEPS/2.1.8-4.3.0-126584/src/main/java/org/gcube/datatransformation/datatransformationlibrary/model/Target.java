package org.gcube.datatransformation.datatransformationlibrary.model;

import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * <tt>Target</tt> of a transformationUnit unit is the composition of the content type that the transformationUnit unit produces as well as the output in which data will be stored.
 * </p>
 */
public class Target extends TransformationRuleElement {
	private String outputID;
	
	/**
	 * Sets the id of the output data handler.
	 * @param id The id of the output data handler.
	 */
	public void setOutputID(String id){
		this.outputID=id;
	}
	
	/**
	 * Returns the id of the output data handler.
	 * @return The id of the output data handler.
	 */
	public String getOutputID(){
		return outputID;
	}
	
	/**
	 * Returns the output of the transformationUnit unit.
	 * @return The output of the transformationUnit unit.
	 * @throws Exception If the output does not exist.
	 */
	public DataSink getOutput() throws Exception {
		return (DataSink)transformationUnit.getDataHandler(this.outputID);
	}
	
	protected void fromDOM(Element targetElm) {
		Element outputElm;
		if((outputElm=(Element)targetElm.getElementsByTagName(XMLDefinitions.ELEMENT_output).item(0))!=null){
			this.outputID=outputElm.getAttribute(XMLDefinitions.ATTRIBUTE_IOID);
		}
		
		Element contentformatElm;
		if((contentformatElm=(Element)targetElm.getElementsByTagName(XMLDefinitions.ELEMENT_contenttype).item(0))!=null){
			contentType = new ContentType();
			contentType.fromDOM(contentformatElm);
		}
	}

	protected void toDOM(Element transformationElm) {
		Document doc = transformationElm.getOwnerDocument();
		
		Element targetElm = doc.createElement(XMLDefinitions.ELEMENT_target);
		
		Element outputElm = doc.createElement(XMLDefinitions.ELEMENT_output);
		Attr ioID = doc.createAttribute(XMLDefinitions.ATTRIBUTE_IOID);
		ioID.setTextContent(this.outputID);
		outputElm.setAttributeNode(ioID);
		
		targetElm.appendChild(outputElm);
		
		contentType.toDOM(targetElm);
		
		transformationElm.appendChild(targetElm);
	}
}
