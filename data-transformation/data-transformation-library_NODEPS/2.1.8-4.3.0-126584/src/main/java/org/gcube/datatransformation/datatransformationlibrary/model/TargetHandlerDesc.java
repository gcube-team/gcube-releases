package org.gcube.datatransformation.datatransformationlibrary.model;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * <tt>TargetHandlerDesc</tt> describes a reference to a data handler of external transformationUnit unit.
 * </p>
 */
public class TargetHandlerDesc {

	private String targetID;
	private String thisID;
	private String type;//TargetInput || TargetOutput
	
	/**
	 * Instantiates a new <tt>TargetHandlerDesc</tt>.
	 */
	public TargetHandlerDesc(){}
	
	/**
	 * Instantiates a new <tt>TargetHandlerDesc</tt>.
	 * @param targetID The target handler id.
	 * @param thisID This handler id.
	 * @param type The type of the handler.
	 */
	public TargetHandlerDesc(String targetID, String thisID, String type) {
		this.targetID = targetID;
		this.thisID = thisID;
		this.type = type;
	}
	
	/**
	 * Returns the id of the target data handler.
	 * @return The id of the target data handler.
	 */
	public String getTargetID() {
		return targetID;
	}
	
	/**
	 * Sets the id of the target data handler.
	 * @param targetID the id of the target data handler.
	 */
	public void setTargetID(String targetID) {
		this.targetID = targetID;
	}
	
	/**
	 * Returns the id of this transformationUnit units data handler.
	 * @return The id of this transformationUnit units data handler.
	 */
	public String getThisID() {
		return thisID;
	}
	
	/**
	 * Sets the id of this transformationUnit units data handler.
	 * @param thisID The id of this transformationUnit units data handler.
	 */
	public void setThisID(String thisID) {
		this.thisID = thisID;
	}
	
	/**
	 * Returns the type of the data handler.
	 * @return The type of the data handler.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type of the data handler.
	 * @param type the type of the data handler.
	 */
	public void setType(String type) {
		this.type = type;
	}
}
