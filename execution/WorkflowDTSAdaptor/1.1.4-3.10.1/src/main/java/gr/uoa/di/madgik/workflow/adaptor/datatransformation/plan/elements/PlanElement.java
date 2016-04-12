package gr.uoa.di.madgik.workflow.adaptor.datatransformation.plan.elements;

import java.util.HashMap;

/**
 * Represents a node of the plan that will be the input to the workflow layer
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public abstract class PlanElement {

	/**
	 * all the arguments needed from the corresponding instance(for determining
	 * the functional behavior) in order to execute this node of the plan.
	 */
	protected HashMap<String, String> functionalArgs = new HashMap<String, String>();

	/**
	 * Default constructor
	 * 
	 * @param functionalArgs
	 *            - all the arguments needed from the corresponding instance(for
	 *            determining the functional behavior) in order to execute this
	 *            node of the plan.
	 * @param projections
	 *            - projections that are provided by this node.
	 */
	public PlanElement(HashMap<String, String> functionalArgs) {
		super();
		this.functionalArgs = functionalArgs;
	}

	/**
	 * getter for the functional arguments field which defines all the arguments
	 * needed from the corresponding instance(for determining the functional
	 * behavior) in order to execute this node of the plan.
	 * 
	 * @return - the functionalArgs
	 */
	public HashMap<String, String> getFunctionalArgs() {
		return functionalArgs;
	}

	/**
	 * setter for the functional arguments field which defines all the arguments
	 * needed from the corresponding instance(for determining the functional
	 * behavior) in order to execute this node of the plan.
	 * 
	 * @param functionalArgs
	 */
	public void setFunctionalArgs(HashMap<String, String> functionalArgs) {
		this.functionalArgs = functionalArgs;
	}

}
