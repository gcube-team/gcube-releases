package org.gcube.datatransformation.datatransformationlibrary.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

//import org.slf4j.Logger;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataBridge;
//import org.gcube.datatransformation.datatransformationlibrary.DTSCore;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataBridge;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Each transformation unit describes the way a program can be used in order to perform a transformation from a source to a target content type. 
 * The program is the one that the transformation program, in which the transformation unit belongs to, references. 
 * The transformation unit determines the behaviour of each program by passing to it the proper program parameters. 
 * </p>
 * <p>
 * Transformation units may reference other (external) transformation units and use them as �black-box� components in the transformation process. 
 * Each transformation unit is identified by the pair (transformation program id, transformation unit id).
 * </p>
 */
public class TransformationUnit {
	
//	private static Logger log = LoggerFactory.getLogger(Transformation.class);
	
	private String id=null;
	private boolean isComposite=false;
	private ArrayList<Source> sources = new ArrayList<Source>();
	private Target target=null;
	private ArrayList<ExtTransformationUnit> extTransformationList=null;
	private TransformationProgram transformationProgram;
	
	private ArrayList<Parameter> programParameters = new ArrayList<Parameter>();
	
	/**
	 * Returns the program parameters.
	 * @return the program parameters.
	 */
	public ArrayList<Parameter> getProgramParameters() {
		return programParameters;
	}

	/**
	 * Sets the program parameters.
	 * @param programParameters the program parameters.
	 */
	public void setProgramParameters(ArrayList<Parameter> programParameters) {
		this.programParameters = programParameters;
	}

	private ArrayList<HandlerDesc> ios = new ArrayList<HandlerDesc>();
	
	private HashSet<String> handledIOIDs = new HashSet<String>();
	private HashMap<String, DataHandler> handlers = new HashMap<String, DataHandler>();
	
	
	public void fromDOM(Element transformation){
		/* Setting the id of the transformationUnit */
		this.setId(transformation.getAttribute(XMLDefinitions.ATTRIBUTE_id));
		
		/* Checking if the transformationUnit is composite or not */
		if(transformation.getAttribute(XMLDefinitions.ATTRIBUTE_isComposite)!=null && 
				transformation.getAttribute(XMLDefinitions.ATTRIBUTE_isComposite).equals("true")){
			this.isComposite=true;
		}
		
		/* Parsing the Sources */
		Element sourceElm;
		int cnt=0;
		while((sourceElm=(Element)transformation.getElementsByTagName(XMLDefinitions.ELEMENT_source).item(cnt++))!=null){
			Source source = new Source();
			source.fromDOM(sourceElm);
			source.setTransformationUnit(this);
			sources.add(source);
			//IOs...
			HandlerDesc desc = new HandlerDesc(source.getInputID(), HandlerDesc.HandlerType.Input, source);
//			log.debug("Inserting "+desc.getID()+", "+desc.getType());
			ios.add(desc);
			handledIOIDs.add(source.getInputID());
		}
		
		/* Parsing the program parameters of the transformationUnit unit */
		Element programParameters;
		if((programParameters=(Element)transformation.getElementsByTagName(XMLDefinitions.ELEMENT_transformationunitprogramparams).item(0))!=null){
			Element parameter;int cnt2=0;
			while((parameter=(Element)programParameters.getElementsByTagName(XMLDefinitions.ELEMENT_parameter).item(cnt2++))!=null){
				Parameter param = new Parameter();
				param.setName(parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterName));
				param.setValue(parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterValue));
				this.programParameters.add(param);
			}
		}
		
		/* Parsing the Target */
		Element targetElm;
		if((targetElm=(Element)transformation.getElementsByTagName(XMLDefinitions.ELEMENT_target).item(0))!=null){
			target = new Target();
			target.fromDOM(targetElm);
			target.setTransformationUnit(this);
			//IOs...
			HandlerDesc desc = new HandlerDesc(target.getOutputID(), HandlerDesc.HandlerType.Output, target);
			ios.add(desc);
//			log.debug("Inserting "+desc.getID()+", "+desc.getType());
			handledIOIDs.add(target.getOutputID());
		}
		
		if(isComposite){
			/* Should parse the external transformations */
			extTransformationList = new ArrayList<ExtTransformationUnit>();
			Element exttransformation;cnt=0;
			while((exttransformation=(Element)transformation.getElementsByTagName(XMLDefinitions.ELEMENT_extTransformation).item(cnt++))!=null){
				ExtTransformationUnit exttransformationinst = new ExtTransformationUnit();
				exttransformationinst.setTransformationUnit(this);
				exttransformationinst.fromDOM(exttransformation);
				extTransformationList.add(exttransformationinst);
			}
		}
	}
	
	public void toDOM(Element tp){
		Document doc = tp.getOwnerDocument();
		Element transformationUnit = doc.createElement(XMLDefinitions.ELEMENT_transformationUnit);
		
		Attr id = doc.createAttribute(XMLDefinitions.ATTRIBUTE_id);
		id.setTextContent(this.id);
		transformationUnit.setAttributeNode(id);
		
		Attr iscomp = doc.createAttribute(XMLDefinitions.ATTRIBUTE_isComposite);
		iscomp.setTextContent(String.valueOf(this.isComposite));
		transformationUnit.setAttributeNode(iscomp);
		
		Element sourcesElm = doc.createElement(XMLDefinitions.ELEMENT_sources);
		for(Source source: sources){
			source.toDOM(sourcesElm);
		}
		transformationUnit.appendChild(sourcesElm);
		
		Element programParametersElm = doc.createElement(XMLDefinitions.ELEMENT_transformationunitprogramparams);
		if(this.programParameters!=null && this.programParameters.size()>0){
			for(Parameter param: this.programParameters){
				param.toDOM(programParametersElm);
			}
		}
		transformationUnit.appendChild(programParametersElm);
		
		target.toDOM(transformationUnit);
		
		if(isComposite){
			Element composition = doc.createElement(XMLDefinitions.ELEMENT_composition);
			for(ExtTransformationUnit exttr: extTransformationList){
				exttr.toDOM(composition);
			}
			transformationUnit.appendChild(composition);
		}
		
		tp.appendChild(transformationUnit);
	}

	/**
	 * Returns the references to external TUs.
	 * @return the references to external TUs.
	 */
	public ArrayList<ExtTransformationUnit> getExtTransformationList() {
		return extTransformationList;
	}

	/**
	 * Sets the references to external TUs.
	 * @param extTransformationList the references to external TUs.
	 */
	public void setExtTransformationList(
			ArrayList<ExtTransformationUnit> extTransformationList) {
		this.extTransformationList = extTransformationList;
	}

	/**
	 * Returns the id of the transformation unit.
	 * @return The id of the transformation unit.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of the transformation unit.
	 * @param id The id of the transformation unit.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return true if the TU is composite.
	 */
	public boolean isComposite() {
		return isComposite;
	}

	/**
	 * @param isComposite if the TU is composite.
	 */
	public void setComposite(boolean isComposite) {
		this.isComposite = isComposite;
	}
	
	/**
	 * Returns the sources of the transformation unit.
	 * @return the sources of the transformation unit.
	 */
	public ArrayList<Source> getSources(){
		return sources;
	}
	
	/**
	 * Sets the sources of the transformation unit.
	 * @param sources the sources of the transformation unit.
	 */
	public void setSources(ArrayList<Source> sources){
		if(sources!=null && sources.size()>0){
			for(Source source: sources){
				HandlerDesc desc = new HandlerDesc(source.getInputID(), HandlerDesc.HandlerType.Input, source);
				ios.add(desc);
				handledIOIDs.add(source.getInputID());
			}
		}
		this.sources=sources;
	}

	/**
	 * Returns the target of the transformation unit.
	 * @return the target of the transformation unit.
	 */
	public Target getTarget() {
		return target;
	}

	/**
	 * Sets the target of the transformation unit.
	 * @param target the target of the transformation unit.
	 */
	public void setTarget(Target target) {
		if(target!=null){
			HandlerDesc desc = new HandlerDesc(target.getOutputID(), HandlerDesc.HandlerType.Output, target);
			ios.add(desc);
			handledIOIDs.add(target.getOutputID());
		}
		this.target = target;
	}

	/**
	 * Returns the transformation program in which this TU is contained.
	 * @return the transformation program in which this TU is contained.
	 */
	public TransformationProgram getTransformationProgram() {
		return transformationProgram;
	}

	/**
	 * Sets the transformation program in which this TU is contained.
	 * @param transformationProgram the transformation program in which this TU is contained.
	 */
	public void setTransformationProgram(TransformationProgram transformationProgram) {
		this.transformationProgram = transformationProgram;
	}
	
	//Data Handlers....
	/**
	 * Returns all the handler descriptions of this TU.
	 * @return the handler descriptions of this TU.
	 */
	public ArrayList<HandlerDesc> getIOs(){
		return ios;
	}
	
	/**
	 * Adds a new data bridge to this TU.
	 * @param id The id of this bridge.
	 */
	public void addBridge(String id){
		HandlerDesc desc = new HandlerDesc(id, HandlerDesc.HandlerType.Bridge, null);
		ios.add(desc);
		handledIOIDs.add(id);
	}
	
	/**
	 * Checks if a handler with id <tt>id</tt> is contained in this TU.
	 * @param id The id to be checked.
	 * @return True if a handler with id <tt>id</tt> is contained in this TU.
	 */
	public boolean containsHandler(String id){
		return handledIOIDs.contains(id);
	}
	
	/**
	 * Binds an id of a data handler with a data handler instance.
	 * @param id The id of the handler.
	 * @param handler The data handler instance.
	 * @throws Exception If the id already exists.
	 */
	public void bindHandler(String id, DataHandler handler) throws Exception{
		if(!handledIOIDs.contains(id))
			throw new Exception("Transformation does not contain handler with id "+id);
		handlers.put(id, handler);
	}
	
	/**
	 * Returns the instance of a data handler by its id.
	 * @param id The id of the handler.
	 * @return The data handler instance.
	 * @throws Exception If the handler could not be found.
	 */
	public DataHandler getDataHandler(String id) throws Exception{
		if(!handledIOIDs.contains(id))
			throw new Exception("Transformation does not contain handler with id "+id);
		DataHandler handler = handlers.get(id);
		if(handler==null){
//			if(id.startsWith("TRBridge")){
//				DataBridge bridge = DTSCore.getDataBridge();
//				bindHandler(id, bridge);
//				return bridge;
//			}
			throw new Exception("Handler with id "+id+" is not bound to any DataHandler Object");
		}
		return handler;
	}
	
	/**
	 * <p>Merges the global and the program parameters contained in a {@link TransformationUnit} into one {@link List}.</p>
	 * <p>The program parameters of the {@link TransformationUnit} have higher priority in comparison with the grobalProgramParameters.</p>
	 *  
	 * @param globalProgramParameters The global program parameters.
	 * @param tUnitProgramParameters The program parameters set by the {@link TransformationUnit}.
	 * @return A {@link List} with the merged program parameters.
	 */
	public List<Parameter> mergeProgramParameters(){
		ArrayList<Parameter> globalProgramParameters = transformationProgram.getTransformer().getGlobalProgramParams();

		ArrayList<Parameter> finalProgramParameters = new ArrayList<Parameter>();
		if(globalProgramParameters!=null && globalProgramParameters.size()>0){
			for(Parameter globalProgramParameter: globalProgramParameters){
				finalProgramParameters.add(globalProgramParameter);
			}
		}
		if(programParameters!=null && programParameters.size()>0){
			for(Parameter tUnitProgramParameter: programParameters){
				for(Parameter globalProgramParameter: finalProgramParameters){
					if(globalProgramParameter.getName().equalsIgnoreCase(tUnitProgramParameter.getName())){
						finalProgramParameters.remove(globalProgramParameter);break;
					}
				}
				finalProgramParameters.add(tUnitProgramParameter);
			}
		}
		return finalProgramParameters;
	}
}
