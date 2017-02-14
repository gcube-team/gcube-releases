package gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan;

import gr.uoa.di.madgik.workflow.adaptor.search.utils.FunctionalArgumentParser;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.exception.MalformedFunctionalArgumentException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Represents an operator node of the plan that will be the input 
 * to the workflow layer
 * 
 * @author vasilis verroios - DI NKUA
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class OperatorNode extends PlanNode
{
	
	private static final long serialVersionUID = 1L;

	/**
	 * the name for the functionality of this operation
	 */
	private String functionality = null;
	
	/**
	 * the nodes that provide the data input to the operator
	 */
	private ArrayList<PlanNode> children = new ArrayList<PlanNode>();

	/**
	 * Default Constructor
	 * @param instanceIds - identifiers(provided by the registry) of all the 
	 * possible operator instances that can answer this node of the plan.
	 * @param functionalArgs - all the arguments needed from the corresponding 
	 * operator(for determining the functional behavior) in order to execute 
	 * this node of the plan.
	 * @param children - the nodes that provide the data input to the operator
	 * @param projections - projections that are provided by this node.
	 */
	public OperatorNode(String functionality,
			HashMap<String, String> functionalArgs, ArrayList<PlanNode> children, Set<String> projections) 
	{
		super(functionalArgs, projections);
		this.children = children;
		this.functionality = functionality;
	}

	/**
	 * setter for the children field, which defines the nodes that provide 
	 * the data input to the operator
	 * @param children
	 */
	public void setChildren(ArrayList<PlanNode> children) 
	{
		this.children = children;
	}

	/**
	 * getter for the children field, which defines the nodes that provide 
	 * the data input to the operator.
	 * @return the children
	 */
	public ArrayList<PlanNode> getChildren() 
	{
		return children;
	}

	/**
	 * adds a child in the children field
	 * @param child
	 */
	public void addChild(PlanNode child) 
	{
		this.children.add(child);
	}
	
	/**
	 * getter for the functionality of this operation
	 * @return the functionality name
	 */
	public String getFunctionality() 
	{
		return functionality;
	}

	/**
	 * getter for the functionality of this operation
	 * @param the functionality name
	 */
	public void setFunctionality(String functionality) 
	{
		this.functionality = functionality;
	}
	
	public float calculateCost(boolean deep) throws Exception
	{
		float cost = 0.0f;
		float shallowCost = 0.0f;
		
		if(!deep)
		{
			if(this.functionality.equals(Constants.MERGE) || this.functionality.equals(Constants.MERGESORT)) cost =  children.size() + 1.0f;
			else if(this.functionality.equals(Constants.FUSE)) cost =  children.size() + 1.0f;
			else if(this.functionality.equals(Constants.JOIN) || this.functionality.equals(Constants.JOINSORT)) cost = 3.0f;
			else if(this.functionality.equals(Constants.EXCEPT)) cost = 3.0f;	
			else throw new MalformedFunctionalArgumentException("Invalid functionality");
		}else
		{
			if(this.functionality.equals(Constants.MERGE) || this.functionality.equals(Constants.MERGESORT)) shallowCost =  1.0f;
			else if(this.functionality.equals(Constants.FUSE)) shallowCost =  1.0f;
			else if(this.functionality.equals(Constants.JOIN) || this.functionality.equals(Constants.JOINSORT)) shallowCost = 3.0f;
			else if(this.functionality.equals(Constants.EXCEPT)) shallowCost = 3.0f;
			else throw new MalformedFunctionalArgumentException("Invalid functionality");	
			
			for(PlanNode c : children)
				cost += (c instanceof OperatorNode) ? ((OperatorNode)c).calculateCost(deep) : 1.0f;
			cost+=shallowCost;
		}
		
		if(FunctionalArgumentParser.getDuplicateEliminationStatus(this.functionalArgs)) cost += 1.0f;
		return cost;
	}
	
	public int maxChildrenForCost(float cost) throws Exception
	{
		if(FunctionalArgumentParser.getDuplicateEliminationStatus(this.functionalArgs)) cost-=1.0f;
		if(this.functionality.equals(Constants.MERGE) || this.functionality.equals(Constants.MERGESORT))
			return (int)Math.floor(cost-1.0f);
		else if(this.functionality.equals(Constants.JOIN) || this.functionality.equals(Constants.JOINSORT)) return 2;
		else if(this.functionalArgs.equals(Constants.EXCEPT)) return 2;
		throw new MalformedFunctionalArgumentException("Invalid functionality");
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(this == obj) return true;
		if (obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		OperatorNode other = (OperatorNode)obj;
		if(!functionality.equals(other.functionality)) return false;
		if(projections == null && other.projections != null) return false;
		if(projections != null && !projections.equals(other.projections)) return false;
		if(!functionalArgs.equals(other.functionalArgs)) return false;
		Set<PlanNode> childrenSet = new HashSet<PlanNode>(this.children);
		Set<PlanNode> otherChildrenSet = new HashSet<PlanNode>(other.children);
		if(!childrenSet.equals(otherChildrenSet)) return false;
		return true;
	}
	
	@Override
	public int hashCode() 
	{
		int hash = 7;
		hash = 31*hash+(functionality == null ? 0 : functionality.hashCode());
		hash = 31*hash+(functionalArgs == null ? 0 : functionalArgs.hashCode());
		hash = 31*hash+(projections == null ? 0 : projections.hashCode());
		hash = 31*hash+(children == null ? 0 : children.hashCode());
		return hash;
	}

	@Override
	public String myToString() {
		return "OperatorNode [functionality=" + functionality + ", children="
				+ children + ", functionalArgs=" + functionalArgs
				+ ", projections=" + projections + "]";
	}
	
	
	@Override
	public String toString() 
	{
		StringBuilder result = new StringBuilder();
		result.append("<Operation>\n");
		result.append("		<Functionality>" + functionality + "</Functionality>\n");
		result.append("		<Indications>");
		for(Entry<String, String> entry : functionalArgs.entrySet())
			result.append(entry.getKey() + "-" + entry.getValue() + ", ");
		result.append("		</Indications>\n");
		result.append("		<Children>\n");
		for(PlanNode child : children)
			result.append(child.toString());
		result.append("		</Children>\n");
		result.append("</Operation>\n");
				
		return result.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ArrayList<PlanNode> planNodes = new ArrayList<PlanNode>();
		for (PlanNode pn : this.children){
			planNodes.add((PlanNode) pn.clone());
		}
		
		OperatorNode cloned = new OperatorNode(this.functionality, new HashMap<String, String>(functionalArgs), planNodes, new HashSet<String>(this.projections));
		return cloned;
	}
	
}
