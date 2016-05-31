package org.gcube.datatransformation.datatransformationlibrary.model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * <tt>ExtTransformationUnit</tt> is a transformationUnit unit which contains references to one or more external <tt>TransformationUnits</tt>.
 * </p>
 */
public class ExtTransformationUnit {//TODO: Maybe extend the transformationUnit unit object.
	
	private TransformationUnit transformationUnit;
	
	/**
	 * Returns the transformationUnit unit. 
	 * @return the transformationUnit unit.
	 */
	public TransformationUnit getTransformationUnit() {
		return transformationUnit;
	}

	/**
	 * Sets the transformationUnit unit.
	 * @param transformationUnit the transformationUnit unit.
	 */
	public void setTransformationUnit(TransformationUnit transformationUnit) {
		this.transformationUnit = transformationUnit;
	}

	private String referencedTransformationProgramID=null;
	private String referencedTransformationUnitID=null;
	private List<Parameter> unboundContentTypeParameters=null;
	private ArrayList<TargetHandlerDesc> targetIOs;
	
	protected void fromDOM(Element extTransformationElm){
		//IDs
		this.referencedTransformationProgramID=extTransformationElm.getAttribute(XMLDefinitions.ATTRIBUTE_transformationprogramid);
		this.referencedTransformationUnitID=extTransformationElm.getAttribute(XMLDefinitions.ATTRIBUTE_transformationunitid);
		
		//IOs
		Element handler;
		targetIOs = new ArrayList<TargetHandlerDesc>();int cnt=0;
		while((handler=(Element)extTransformationElm.getElementsByTagName(XMLDefinitions.ELEMENT_TargetInput).item(cnt++))!=null){
			String htarget = handler.getAttribute(XMLDefinitions.ATTRIBUTE_IOTargetID);
			String hthis = handler.getAttribute(XMLDefinitions.ATTRIBUTE_IOThisID);
			String htype = XMLDefinitions.ELEMENT_TargetInput;
			TargetHandlerDesc hdesc = new TargetHandlerDesc(htarget, hthis, htype);
			targetIOs.add(hdesc);
			if(hthis.startsWith("TRBridge") && !this.transformationUnit.containsHandler(hthis)){
				this.transformationUnit.addBridge(hthis);
			}
		}cnt=0;
		while((handler=(Element)extTransformationElm.getElementsByTagName(XMLDefinitions.ELEMENT_TargetOutput).item(cnt++))!=null){
			String htarget = handler.getAttribute(XMLDefinitions.ATTRIBUTE_IOTargetID);
			String hthis = handler.getAttribute(XMLDefinitions.ATTRIBUTE_IOThisID);
			String htype = XMLDefinitions.ELEMENT_TargetOutput;
			TargetHandlerDesc hdesc = new TargetHandlerDesc(htarget, hthis, htype);
			targetIOs.add(hdesc);
			if(hthis.startsWith("TRBridge") && !this.transformationUnit.containsHandler(hthis)){
				this.transformationUnit.addBridge(hthis);
			}
		}
		
		//Unbound Parameters
		Element unboundparamselm;
		if((unboundparamselm=(Element)extTransformationElm.getElementsByTagName(XMLDefinitions.ELEMENT_unboundparams).item(0))!=null){
			ArrayList <Parameter> unboundParametersList = new ArrayList<Parameter>();
			Element parameter;cnt=0;
			while((parameter=(Element)unboundparamselm.getElementsByTagName(XMLDefinitions.ELEMENT_parameter).item(cnt++))!=null){
				Parameter param = new Parameter();
				param.setName(parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterName));
				param.setValue(parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterValue));
				unboundParametersList.add(param);
			}
			this.unboundContentTypeParameters=unboundParametersList;
		}
	}
	
	protected void toDOM(Element parent){
		Document doc = parent.getOwnerDocument();
		Element exttransformationElm = doc.createElement(XMLDefinitions.ELEMENT_extTransformation);
		
		Attr tpid = doc.createAttribute(XMLDefinitions.ATTRIBUTE_transformationprogramid);
		tpid.setTextContent(this.referencedTransformationProgramID);
		exttransformationElm.setAttributeNode(tpid);
		
		Attr transformationid = doc.createAttribute(XMLDefinitions.ATTRIBUTE_transformationunitid);
		transformationid.setTextContent(this.referencedTransformationUnitID);
		exttransformationElm.setAttributeNode(transformationid);
		
		//IOs
		for(TargetHandlerDesc thdesc: targetIOs){
			Element datahandlerElm = doc.createElement(thdesc.getType());
			Attr iotarget = doc.createAttribute(XMLDefinitions.ATTRIBUTE_IOTargetID);
			iotarget.setTextContent(thdesc.getTargetID());
			Attr iothis = doc.createAttribute(XMLDefinitions.ATTRIBUTE_IOThisID);
			iothis.setTextContent(thdesc.getThisID());
			datahandlerElm.setAttributeNode(iotarget);
			datahandlerElm.setAttributeNode(iothis);
			
			exttransformationElm.appendChild(datahandlerElm);
		}
		
		//Unbound params
		Element unboundparams = doc.createElement(XMLDefinitions.ELEMENT_unboundparams);
		if(this.unboundContentTypeParameters!=null && this.unboundContentTypeParameters.size()>0){
			for(Parameter unparam: this.unboundContentTypeParameters){
				unparam.toDOM(unboundparams);
			}
		}
		exttransformationElm.appendChild(unboundparams);
		parent.appendChild(exttransformationElm);
	}
	
	/**
	 * Returns the referenced transformationUnit program id.
	 * @return The referenced transformationUnit program id.
	 */
	public String getReferencedTransformationProgramID() {
		return referencedTransformationProgramID;
	}
	
	/**
	 * Sets the referenced transformationUnit program id.
	 * @param referencedTransformationProgramID The referenced transformationUnit program id.
	 */
	public void setReferencedTransformationProgramID(String referencedTransformationProgramID) {
		this.referencedTransformationProgramID = referencedTransformationProgramID;
	}
	
	/**
	 * Returns the referenced transformationUnit unit id.
	 * @return The referenced transformationUnit unit id.
	 */
	public String getReferencedTransformationUnitID() {
		return referencedTransformationUnitID;
	}
	
	/**
	 * Sets the referenced transformationUnit unit id.
	 * @param referencedTransformationID The referenced transformationUnit unit id.
	 */
	public void setReferencedTransformationUnitID(String referencedTransformationID) {
		this.referencedTransformationUnitID = referencedTransformationID;
	}
	
	/**
	 * Returns the unbound content type parameters.
	 * @return the unbound content type parameters.
	 */
	public List<Parameter> getUnboundContentTypeParameters() {
		return unboundContentTypeParameters;
	}
	
	/**
	 * Sets the unbound content type parameters.
	 * @param unboundparams the unbound content type parameters.
	 */
	public void setUnboundContentTypeParameters(List<Parameter> unboundparams) {
		this.unboundContentTypeParameters = unboundparams;
	}

	/**
	 * Returns the references to the io handlers of the referenced transformationUnit unit.
	 * @return the references to the io handlers of the referenced transformationUnit unit.
	 */
	public ArrayList<TargetHandlerDesc> getTargetIOs() {
		return targetIOs;
	}
	
	/**
	 * Sets the references to the io handlers of the referenced transformationUnit unit.
	 * @param targetIOs the references to the io handlers of the referenced transformationUnit unit.
	 */
	public void setTargetIOs(ArrayList<TargetHandlerDesc> targetIOs) {
		for(TargetHandlerDesc desc: targetIOs){
			if(desc.getThisID().startsWith("TRBridge") && !this.transformationUnit.containsHandler(desc.getThisID())){
				this.transformationUnit.addBridge(desc.getThisID());
			}
		}
		this.targetIOs = targetIOs;
	}
}
