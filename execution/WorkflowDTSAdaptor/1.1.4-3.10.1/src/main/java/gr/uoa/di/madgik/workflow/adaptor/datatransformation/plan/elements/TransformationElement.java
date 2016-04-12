package gr.uoa.di.madgik.workflow.adaptor.datatransformation.plan.elements;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Represents a transformation node of the transformation plan
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class TransformationElement extends PlanElement {

	/**
	 * the name for the functionality of this operation
	 */
	private String functionality = null;

	/**
	 * the nodes that provide the data input to the operator
	 */
	private PlanElement previous = null;

	/**
	 * Default Constructor
	 * 
	 * @param functionality
	 * @param functionalArgs
	 * @param previous
	 * @param projections
	 */
	public TransformationElement(String functionality, HashMap<String, String> functionalArgs, PlanElement previous) {
		super(functionalArgs);
		this.previous = previous;
		this.functionality = functionality;
	}

	/**
	 * getter for the previous field, which defines the node that proceeds in the transformation plan
	 * 
	 * @return the children
	 */
	public PlanElement getPrevious() {
		return previous;
	}

	/**
	 * setter for the previous field, which defines the node that proceeds in the transformation plan
	 * 
	 * @param previous
	 */
	public void setPrevious(PlanElement previous) {
		this.previous = previous;
	}

	/**
	 * getter for the functionality of this operation
	 * 
	 * @return the functionality name
	 */
	public String getFunctionality() {
		return functionality;
	}

	/**
	 * getter for the functionality of this operation
	 * 
	 * @param the
	 *            functionality name
	 */
	public void setFunctionality(String functionality) {
		this.functionality = functionality;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("<Operation>\n");
		result.append("		<Functionality>" + functionality + "</Functionality>\n");
		result.append("		<Indications>");
		for (Entry<String, String> entry : functionalArgs.entrySet())
			result.append(entry.getKey() + "-" + entry.getValue() + ", ");
		result.append("		</Indications>\n");
		result.append("		<Previous>\n");
		result.append(previous.toString());
		result.append("		</Previous>\n");
		result.append("</Operation>\n");

		return result.toString();
	}

}