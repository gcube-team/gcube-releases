package gr.uoa.di.madgik.hive.plan;

import gr.uoa.di.madgik.hive.utils.XMLBeautifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Represents an operator node of the plan that will be the input to the
 * workflow layer
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class OperatorNode extends PlanNode {

	/**
	 * the functionality of this operation
	 */
	private Functionality functionality = null;

	/**
	 * the nodes that provide the data input to the operator
	 */
	private ArrayList<PlanNode> children = new ArrayList<PlanNode>();

	/**
	 * Default Constructor
	 * 
	 * @param functionality
	 *            - the functionality of the operation
	 * @param functionalArgs
	 *            - all the arguments needed from the corresponding operator(for
	 *            determining the functional behavior) in order to execute this
	 *            node of the plan.
	 * @param children
	 *            - the nodes that provide the data input to the operator
	 */
	public OperatorNode(Functionality functionality, HashMap<String, String> functionalArgs, ArrayList<PlanNode> children) {
		super(functionalArgs);
		this.children = children;
		this.functionality = functionality;
		if (children != null)
			for (PlanNode ch : children) {
				ch.setParent(this);
			}
	}
	
	public OperatorNode(OperatorNode node) {
		this(node.functionality, node.functionalArgs, new ArrayList<PlanNode>());
		for(PlanNode c : node.getChildren()) {
			if (c instanceof OperatorNode)
				c = new OperatorNode((OperatorNode) c);
			children.add(c);
		}
	}

	/**
	 * setter for the children field, which defines the nodes that provide the
	 * data input to the operator
	 * 
	 * @param children
	 *            the children
	 */
	public void setChildren(ArrayList<PlanNode> children) {
		this.children = children;
		for (PlanNode ch : children) {
			ch.setParent(this);
		}
	}

	/**
	 * getter for the children field, which defines the nodes that provide the
	 * data input to the operator.
	 * 
	 * @return the children
	 */
	public ArrayList<PlanNode> getChildren() {
		return children;
	}

	/**
	 * adds a child in the children field
	 * 
	 * @param child
	 *            the child to be added
	 */
	public void addChild(PlanNode child) {
		this.children.add(child);
		child.setParent(this);
	}

	/**
	 * getter for the functionality of this operation
	 * 
	 * @return the functionality
	 */
	public Functionality getFunctionality() {
		return functionality;
	}

	/**
	 * getter for the functionality of this operation
	 * 
	 * @param functionality
	 *            the functionality
	 * 
	 */
	public void setFunctionality(Functionality functionality) {
		this.functionality = functionality;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OperatorNode other = (OperatorNode) obj;
		if (!functionality.equals(other.functionality))
			return false;
		if (!functionalArgs.equals(other.functionalArgs))
			return false;
		Set<PlanNode> childrenSet = new HashSet<PlanNode>(this.children);
		Set<PlanNode> otherChildrenSet = new HashSet<PlanNode>(other.children);
		if (!childrenSet.equals(otherChildrenSet))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (functionality == null ? 0 : functionality.hashCode());
		hash = 31 * hash + (functionalArgs == null ? 0 : functionalArgs.hashCode());
		hash = 31 * hash + (children == null ? 0 : children.hashCode());
		return hash;
	}

	@Override
	public String toString() {
		Pattern INVALID_XML_CHARS = Pattern.compile("[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\uD800\uDC00-\uDBFF\uDFFF]");

		StringBuilder result = new StringBuilder();
		result.append("<Operation>");
		result.append("<Functionality>" + functionality + "</Functionality>");
		result.append("<Indications>");
		for (Entry<String, String> entry : functionalArgs.entrySet())
			result.append(entry.getKey() + "-" + StringEscapeUtils.escapeXml(INVALID_XML_CHARS.matcher(entry.getValue().length() < 256? entry.getValue() : entry.getValue().substring(0, 255) + "...").replaceAll("")) + ", ");
		if (!functionalArgs.isEmpty())
			result.delete(result.length()-2, result.length());
		result.append("</Indications>");
		result.append("<Children>");
		if (children != null)
			for (PlanNode child : children)
				result.append(child.toString());
		result.append("</Children>");
		result.append("</Operation>");
		
		return XMLBeautifier.prettyPrintXml(result.toString());
	}
}
