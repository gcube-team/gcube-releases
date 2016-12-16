package gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a node of the plan that will be the input to the workflow layer
 * @author vasilis verroios - DI NKUA
 *
 */
public abstract class PlanNode implements Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * all the arguments needed from the corresponding instance(for determining 
	 * the functional behavior) in order to execute this node of the plan.
	 */
	protected HashMap<String, String> functionalArgs = new HashMap<String, String>();
	
	/**
	 * The projections that are provided by this node
	 */
	protected Set<String> projections = new HashSet<String>();
	
	/**
	 * Default constructor
	 * @param functionalArgs - all the arguments needed from the corresponding 
	 * instance(for determining the functional behavior) in order to execute 
	 * this node of the plan.
	 * @param projections - projections that are provided by this node.
	 */
	public PlanNode(HashMap<String, String> functionalArgs, Set<String> projections) 
	{
		super();
		this.functionalArgs = functionalArgs;
		this.projections = projections;
	}

	/**
	 * getter for the projections that are provided by this node
	 * @return the set of projections
	 */
	public Set<String> getProjections() 
	{
		return projections;
	}

	/**
	 * setter for the projections that are provided by this node
	 * @param projections 
	 */
	public void setProjections(Set<String> projections) 
	{
		this.projections = projections;
	}

	/**
	 * getter for the functional arguments field which defines all the 
	 * arguments needed from the corresponding instance(for determining 
	 * the functional behavior) in order to execute this node of the plan.
	 * @return - the functionalArgs
	 */
	public HashMap<String, String> getFunctionalArgs() 
	{
		return functionalArgs;
	}

	/**
	 * setter for the functional arguments field which defines all the 
	 * arguments needed from the corresponding instance(for determining 
	 * the functional behavior) in order to execute this node of the plan.
	 * @param functionalArgs
	 */
	public void setFunctionalArgs(HashMap<String, String> functionalArgs) 
	{
		this.functionalArgs = functionalArgs;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((functionalArgs == null) ? 0 : functionalArgs.hashCode());
		result = prime * result
				+ ((projections == null) ? 0 : projections.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlanNode other = (PlanNode) obj;
		if (functionalArgs == null) {
			if (other.functionalArgs != null)
				return false;
		} else if (!functionalArgs.equals(other.functionalArgs))
			return false;
		if (projections == null) {
			if (other.projections != null)
				return false;
		} else if (!projections.equals(other.projections))
			return false;
		return true;
	}

	public abstract String myToString();
	
	@Override
	public abstract Object clone() throws CloneNotSupportedException;
}
