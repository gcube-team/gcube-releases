package org.gcube.datatransformation.datatransformationlibrary.model;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Describes an IO Data Handler.
 * </p>
 */
public class HandlerDesc {
	
	/**
	 * @author Dimitris Katris, NKUA
	 * <p>
	 * The type of the handler.
	 * </p>
	 */
	public enum HandlerType{
		/**
		 * Input handler. 
		 */
		Input, 
		/**
		 * Output handler.
		 */
		Output, 
		/**
		 * Bridge handler.
		 */
		Bridge
	};
	private String id;
	private HandlerType type;
	private TransformationRuleElement ruleElement;
	
	/**
	 * Instantiates a new handler description.
	 * 
	 * @param id The id of the handler.
	 * @param type The type of the handler.
	 * @param ruleElement The transformationUnit rule element.
	 */
	public HandlerDesc(String id, HandlerType type, TransformationRuleElement ruleElement){
		this.id=id;
		this.type=type;
		this.ruleElement=ruleElement;
	}
	/**
	 * Instantiates a new handler description.
	 */
	public HandlerDesc(){
	}
	/**
	 * Returns the id of the handler.
	 * @return The id of the handler.
	 */
	public String getID() {
		return id;
	}
	/**
	 * Sets the id of the handler.
	 * @param id The id of the handler.
	 */
	public void setID(String id) {
		this.id = id;
	}
	/**
	 * Returns the type of the handler.
	 * @return The type of the handler.
	 */
	public HandlerType getType() {
		return type;
	}
	/**
	 * Sets the type of the handler.
	 * @param type The type of the handler.
	 */
	public void setType(HandlerType type) {
		this.type = type;
	}
	/**
	 * Returns the transformationUnit rule element. 
	 * @return The transformationUnit rule element.
	 */
	public TransformationRuleElement getRuleElement() {
		return ruleElement;
	}
	/**
	 * Sets the transformationUnit rule element.
	 * @param ruleElement the transformationUnit rule element.
	 */
	public void setRuleElement(TransformationRuleElement ruleElement) {
		this.ruleElement = ruleElement;
	}
}
