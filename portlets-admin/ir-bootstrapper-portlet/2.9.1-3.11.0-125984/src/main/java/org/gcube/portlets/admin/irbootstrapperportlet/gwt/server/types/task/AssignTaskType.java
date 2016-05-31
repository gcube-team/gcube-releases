/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task;

import gr.uoa.di.madgik.commons.utils.XMLUtils;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.ConfigurationParserConstants;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.Evaluable;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.EvaluationResult;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.ExecutionEntity;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.DataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityExecutionData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class AssignTaskType extends ExecutionEntity implements Cloneable {
	
	private static final String USER_INPUT_VALUE = "%userInput";
	
	/** Logger */
	protected static Logger logger = Logger.getLogger(AssignTaskType.class);
	
	/** The expression representing the assignment target */
	private String assignTo;
	
	/** The expression representing the assignment source */
	private String assignValue;
	
	/** The text to display to the user when asking for input, if this assignment requires user input */
	private String userInputLabel;
	
	/**
	 * Class constructor
	 */
	public AssignTaskType() { 
		this.UID = getTypeName();
	}
	
	/**
	 * Initializes this object, passing the XML element that contains the assignment information
	 * @param xml the XML element describing this assignment
	 */
	public void initialize(Element xml) {
		this.assignTo = xml.getAttribute(ConfigurationParserConstants.ASSIGNTO_ATTRIBUTE);
		this.assignValue = xml.getAttribute(ConfigurationParserConstants.ASSIGNVALUE_ATTRIBUTE);
		this.userInputLabel = xml.getAttribute(ConfigurationParserConstants.ASSIGNVALUE_USERINPUTLABEL);
	}
	
	public String getAssignFrom() {
		return assignValue;
	}
	
	public void setAssignFrom(String assignFrom) {
		this.assignValue = assignFrom;
	}
	
	public String getAssignTo() {
		return assignTo;
	}
	
	public boolean requiresUserInput() {
		return assignValue.equalsIgnoreCase(USER_INPUT_VALUE);
	}

	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}

	public void setUserInputLabel(String label) {
		this.userInputLabel = label;
	}
	
	public String getUserInputLabel() {
		return this.userInputLabel;
	}

	/**
	 * Checks if the given node has child elements or not.
	 * @param n the node to check
	 * @return true if the node contains child elements, false otherwise
	 * @throws Exception
	 */
	private boolean hasChildElements(Node n) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nl = (NodeList) xpath.evaluate("*", n, XPathConstants.NODESET);
		return (nl.getLength() > 0);
	}
	
	/**
	 * Performs the assignment that this task must execute
	 * @throws Exception
	 */
	public void doAssignment() throws Exception {
		boolean isToNodeString = true;
		boolean isFromNodeString = true;
		if (this.requiresUserInput())
			return;

		try {
			/* If trying to assign something to a non-variable, throw an exception */
			if (!assignTo.startsWith(ConfigurationParserConstants.VARIABLE_START))
				throw new Exception("Illegal assignment: '" + assignTo + "' is not a variable. Assignment targets cannot be literals.");
			
			/* Locate the root entity of the execution hierarchy that this object belongs to */
			ExecutionEntity rootEntity = this.getParent();
			while (rootEntity.getParent() != null)
				rootEntity = rootEntity.getParent();
			
			/* Strip off the variable symbol and evaluate the "assign-to" expression */
			EvaluationResult toResult = rootEntity.evaluate(assignTo.substring(ConfigurationParserConstants.VARIABLE_START.length()));
			if (toResult == null)
				throw new Exception("Cannot evaluate expression: " + assignTo);
			Node toNode = toResult.getEvaluatedNode();
			
			/* Check if the "assign-to" expression is a string or a subtree */
			if (hasChildElements(toNode))
				isToNodeString = false;
			
			EvaluationResult fromResult = null;
			Node fromNode = null;
			if (assignValue.startsWith(ConfigurationParserConstants.VARIABLE_START)) {
				/* Strip off the variable symbol and evaluate the "assign-from" expression */
				fromResult = rootEntity.evaluate(assignValue.substring(ConfigurationParserConstants.VARIABLE_START.length()));
				if (fromResult == null)
					throw new Exception("Cannot evaluate expression: " + assignValue);
				fromNode = fromResult.getEvaluatedNode();
				
				/* Check if the "assign-from" expression is a string or a subtree */
				if (hasChildElements(fromNode))
					isFromNodeString = false;
			}
			
			/* Check for compatibility between the two operands */
			if (isToNodeString != isFromNodeString)
				throw new Exception("Incompatible operands in assignment: [ " + assignValue + " , " + assignTo + " ]");
			
			if (isToNodeString) {
				/* Copy the value of the source node, if such a node is referenced, else copy
				 * the given string literal
				 */
				if (fromResult != null) {
					toNode.setTextContent(fromNode.getTextContent());
					logger.debug("Assigned value: '" + fromNode.getTextContent() + "' to: " + assignTo);
				} else {
					toNode.setTextContent(assignValue);
					logger.debug("Assigned value: '" + assignValue + "' to: " + assignTo);
				}
			}
			else {
				/* Import the whole subtree rooted at "fromResult" into the document that contains "toResult"
				 * and replace "toResult" with it.
				 */
				Node newChild = toNode.getOwnerDocument().importNode(fromResult.getEvaluatedNode(), true);
				toNode.getParentNode().replaceChild(newChild, toNode);
				logger.debug("Assigned node tree: "  + assignValue + " to: " + assignTo);
			}
			
			/* If this assignment task modified a DataType object, its associated resource must be also be
			 * updated with the new data */
			Evaluable toObj = toResult.getEvaluatedObject();
			if (toObj instanceof DataType)
				((DataType) toObj).copyDataToResource();
		} catch (Exception e) {
			logger.error("Failed to perform value assignment.", e);
			throw new Exception("Failed to perform value assignment.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#execute(org.gcube.portlets.admin.irbootstrapperportlet.servlet.util.EntityExecutionData)
	 */
	public String execute(EntityExecutionData eed) {
		this.createExecutionLog(logger, eed.getSession());
		this.setWorkflowLogger(eed.getWorkflowLogLogger());
		execState = ExecutionState.RUNNING;
		try {
			doAssignment();
		} catch (Exception e) {
			this.getExecutionLogger().error(e.getCause().getMessage(), e);
			execState = ExecutionState.COMPLETED_FAILURE;
			return null;
		}
		execState = ExecutionState.COMPLETED_SUCCESS;
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#cancel()
	 */
	@Override
	public void cancel() { }
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#getTypeName()
	 */
	public String getTypeName() {
		return "AssignTaskType";
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		return (AssignTaskType) super.clone();
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#newInstance(java.lang.String)
	 */
	@Override
	public AssignTaskType newInstance(String name) {
		AssignTaskType att = null;
		try {
			att = (AssignTaskType) this.clone();
			att.entityName = name;
		} catch (Exception e) { return null; }
		return att;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#evaluate(java.lang.String)
	 */
	@Override
	public EvaluationResult evaluate(String expression) {
		/* no expressions can be evaluated on an AssignTaskType */
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#findMatchingInputsInScope(org.gcube.common.core.scope.GCUBEScope)
	 */
	@Override
	public void initializeWithDataInScope(String scope) throws Exception { 
		this.setIsFulfilled(false);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#getUIDescription()
	 */
	@Override
	public String getUIDescription() {
		return "Assignment of:\n" + assignValue + "\nto:\n" + assignTo;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#toXML(java.lang.StringBuilder)
	 */
	@Override
	public void toXML(StringBuilder output) {
		output.append("<assign to=\"");
		output.append(this.assignTo);
		output.append("\" value=\"");
		//output.append(this.assignValue);
		output.append(XMLUtils.DoReplaceSpecialCharachters(this.assignValue));
		if (this.requiresUserInput()) {
			logger.debug("Assigning input label -> " + this.userInputLabel);
			output.append("\" userInputLabel=\"");
			output.append(this.userInputLabel);
		}
		output.append("\"/>");
	}
}
