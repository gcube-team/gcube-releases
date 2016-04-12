/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.ConfigurationParserConstants;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.IRBootstrapperData;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.Util;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging.LogEntry;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.EvaluationResult;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.ExecutionEntity;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.DataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityExecutionData;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.TaskExecutionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public abstract class CustomTaskType extends ExecutionEntity implements Cloneable {

	/** Logger */
	private Logger logger;
		
	/** The name of this taskType */
	private String taskTypeName;
	
	/** The input dataType class */
	private Class<? extends DataType> inputClass;
	
	/** The input dataType*/
	private DataType input;

	/** The output dataType class */
	private Class<? extends DataType> outputClass;

	/** The output dataType */
	private DataType output;
	
	/** The XML node that specifies whether this task will be executed or not */
	private Node willRun;
	
	/** The XML document that contains the task definition */
	private Document taskDefinition;
	
	/**
	 * Class constructor
	 */
	CustomTaskType(Class<? extends DataType> inputClass, Class<? extends DataType> outputClass, Class<? extends CustomTaskType> taskClass) {
		try {
			logger = Logger.getLogger(taskClass);
			this.inputClass = inputClass;
			this.outputClass = outputClass;
			this.taskDefinition = Util.copyNodeAsNewDocument(this.getXMLTaskDefinitionDocument().getDocumentElement());
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	/**
	 * Initializes this TaskType
	 * @param taskTypeName the name of the task type
	 * @param inputDataType the task's input data type
	 * @param outputDataType the task's output data type
	 */
	public void initialize(String taskTypeName, DataType inputDataType, DataType outputDataType, Element willRun) throws Exception {
		if (inputDataType.getClass() != inputClass)
			throw new Exception("The given input is invalid for this type of task.");
		if (outputDataType.getClass() != outputClass)
			throw new Exception("The given output is invalid for this type of task.");
		this.taskTypeName = taskTypeName;
		this.input = inputDataType;
		this.output = outputDataType;
		this.willRun = willRun.cloneNode(true);
	}
	
	/**
	 * Returns the current input object
	 * @return the input object
	 */
	public DataType getInput() {
		return input;
	}
	
	/**
	 * Returns the current output object
	 * @return the output object
	 */
	public DataType getOutput() {
		return output;
	}

	/**
	 * Returns the value of the flag that specifies whether this task will be executed or not
	 * @return the value of the 'run' flag
	 */
	public boolean willRun() {
		return Boolean.valueOf(willRun.getTextContent());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#evaluate(java.lang.String)
	 */
	public EvaluationResult evaluate(String expression) {
		try {
			/* Split the expression around the first dot */
			String[] parts = expression.split("\\.", 2);
			if (parts[0].equals(this.getName())) {
				String tmp = parts[1];
				parts = parts[1].split("\\.", 2);
				if (parts[0].equals(ConfigurationParserConstants.INPUT_ELEMENT)) {
					if (parts.length == 1)
						return new EvaluationResult(input, input.getTypeDefinition());
					return input.evaluate(parts[1]);
				}
				else if (parts[0].equals(ConfigurationParserConstants.OUTPUT_ELEMENT)) {
					if (parts.length == 1)
						return new EvaluationResult(output, output.getTypeDefinition());
					return output.evaluate(parts[1]);
				}
				else if (parts[0].equals(ConfigurationParserConstants.RUN_ELEMENT)) {
					return new EvaluationResult(this, willRun);
				}
				else {
					XPath xpath = XPathFactory.newInstance().newXPath();
					String xpathExpr = tmp.replace('.', '/');					
					Node n = (Node) xpath.evaluate(xpathExpr, taskDefinition, XPathConstants.NODE);
					return new EvaluationResult(this, n);
				}
			}
		} catch (Exception e) { 
			logger.error("Failed to evaluate expression: " + expression, e);
		}
		return null;
	}
	
	/**
	 * Utility evaluation function, used internally in order to avoid the overhead of
	 * creating {@link EvaluationResult} objects that are not needed.
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private Node _evaluate(String expression) throws Exception {
		try {
			/* Split the expression around the first dot */
			String[] parts = expression.split("\\.", 2);
			if (parts[0].equals(this.getName())) {
				String tmp = parts[1];
				parts = parts[1].split("\\.", 2);
				if (parts[0].equals(ConfigurationParserConstants.INPUT_ELEMENT)) {
					if (parts.length == 1)
						return input.getTypeDefinition();
					return input.evaluate(parts[1]).getEvaluatedNode();
				}
				else if (parts[0].equals(ConfigurationParserConstants.OUTPUT_ELEMENT)) {
					if (parts.length == 1)
						return output.getTypeDefinition();
					return output.evaluate(parts[1]).getEvaluatedNode();
				}
				else if (parts[0].equals(ConfigurationParserConstants.RUN_ELEMENT)) {
					return willRun;
				}
				else {
					XPath xpath = XPathFactory.newInstance().newXPath();
					String xpathExpr = tmp.replace('.', '/');
					Node n = (Node) xpath.evaluate(xpathExpr, taskDefinition, XPathConstants.NODE);
					return n;
				}
			}
		} catch (Exception e) { }
		return null;
	}
	
	/**
	 * Gets the value of an attribute defined in the XML definition of this task type.
	 * Works only for attributes represented by "leaf" nodes in the XML definition,
	 * whose value is only a string and not a node tree. If the requested attribute is 
	 * not currently set, returns null.
	 * @param expression the expression to evaluate, in "a.b.c..." notation
	 * @return the value of the attribute identified by the given expression
	 */
	public String getAttributeValue(String expression) throws Exception {
		try {
			if (this instanceof FullTextIndexNodeGenerationTaskType) {
				String nodeString = null;
				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer();
				transformer.setOutputProperty("omit-xml-declaration", "yes");
				StringWriter sw = new StringWriter();
				StreamResult result = new StreamResult(sw);
				DOMSource source = new DOMSource(this.taskDefinition);
				transformer.transform( source, result );
				nodeString = sw.getBuffer().toString();
				logger.debug("Task definition is: " + nodeString);
			}
			
			Node n = this._evaluate(expression);
			String s = n.getTextContent();
			if (s.trim().length() == 0)
				return null;
			return s;
		} catch (Exception e) {
			logger.error("Failed to evaluate attribute: " + expression, e);
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#getTypeName()
	 */
	public String getTypeName() {
		return this.taskTypeName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#newInstance(java.lang.String)
	 */
	public CustomTaskType newInstance(String name) {
		CustomTaskType tt = null;
		try {
			tt = (CustomTaskType) this.clone();
		} catch (Exception e) { }
		tt.entityName = name;

		return tt;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#findMatchingInputsInScope(org.gcube.common.core.scope.GCUBEScope)
	 */
	public void initializeWithDataInScope(String scope) throws Exception {
		boolean bIsFulfilled = false;
		
		/* Find all the matches for this task's input DataType in the given scope. If only one match
		 * is found AND the input DataType is able to uniquely identify one and only one resource,
		 * then set the 'bIsFulfilled' variable to true, indicating that the desired input for this
		 * task has been found.
		 */
		try {
			if (this.input.doesIdentifyUniqueResource()) {
				List<DataType> applicableInputResources = this.input.findMatchesInScope(scope);
				if (applicableInputResources.size() == 1)
					bIsFulfilled = true;
			}
		} catch (Exception e) {
			logger.error("Error while searching for applicable inputs for the task: " + this.getName(), e);
			throw new Exception("Error while searching for applicable inputs for the task: " + this.getName());
		}
		
		/* If we did not manage to find an input for this task, there is no point to also try
		 * to find a match for the output. So the task's goal is considered as not being fulfilled
		 * and the method terminates. */
		if (!bIsFulfilled) {
			this.setIsFulfilled(false);
			return;
		}
		
		/* Now check if there is a resource in the given scope that matches the output DataType
		 * of this task. If there is one AND the output DataType is able to uniquely identify
		 * one and only one resource, then the goal of this task is considered to be 'fulfilled'.
		 */
		try {
			if (this.output.doesIdentifyUniqueResource()) {
				
				List<DataType> applicableOutputs = this.output.findMatchesInScope(scope);
				
				if (applicableOutputs.size() != 1)
					bIsFulfilled = false;
				else {
					this.output = (DataType) applicableOutputs.get(0).clone();
				}
			}
			else
				bIsFulfilled = false;
		} catch (Exception e) {
			logger.error("Error while searching for applicable outputs for the task: " + this.getName(), e);
			throw new Exception("Error while searching for applicable outputs for the task: " + this.getName());
		}
		
		this.setIsFulfilled(bIsFulfilled);
	}
	
	/**
	 * This method is called just before a custom task is executed, in order to check if the task's
	 * goal is already fulfilled at that point. If this is the case, the task should not be executed.
	 * @throws Exception
	 */
	private void checkIfGoalFulfilled() {
		this.setIsFulfilled(false);
		
		if (this.output.doesIdentifyUniqueResource()) {
			try {
				List<DataType> applicableOutputs = this.output.findMatchesInScope(scope);
				if (applicableOutputs.size() == 1)
					this.setIsFulfilled(true);
			} catch (Exception e) {
				logger.error("Error while searching for applicable outputs for the task: " + this.getName(), e);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		try {
			CustomTaskType tt = (CustomTaskType) super.clone();
			tt.input = (DataType) input.clone();
			tt.output = (DataType) output.clone();
			tt.taskDefinition = Util.copyNodeAsNewDocument(taskDefinition.getDocumentElement());
			return tt;
		} catch (Exception e) {
			logger.error("Error while cloning TaskType object", e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#execute(org.gcube.portlets.admin.irbootstrapperportlet.servlet.util.EntityExecutionData)
	 */
	public String execute(EntityExecutionData eed) {
		
		/* Check if the task's goal has been fulfilled already */
		checkIfGoalFulfilled();
		
		/* Execute the task only if its goal is not fulfilled yet */
		if (!this.isFulfilled()) {
			
			/* Create an execution log */
			this.createExecutionLog(logger, eed.getSession());
			this.setWorkflowLogger(eed.getWorkflowLogLogger());
			/* Construct a TaskExecutionData object */
			TaskExecutionData ted = new TaskExecutionData(eed);
			ted.setExecutionLogger(this.getExecutionLogger());
			ted.setWorkflowLogger(eed.getWorkflowLogLogger());
			
			/* Set the initial execution state to "running" and execute the task's implementation */
			setExecutionState(ExecutionState.RUNNING);
			this.executeTask(ted);
			
			synchronized (this) {
				if (getExecutionState() != ExecutionState.CANCELLED) {
					/* Now examine the execution log. If it contains nothing, then the task completed
					 * successfully. If it contains any entries describing errors, set the execution state
					 * to "failed to complete". Else, set the state to "completed with warnings".  */
					LinkedList<LogEntry> log = this.getExecutionLogger().getLogEntries();
					if (log.size() > 0) {
						LogEntry.LogEntryLevel maxLogLevel = this.getExecutionLogger().getHighestLogLevelContained();
						if (maxLogLevel == LogEntry.LogEntryLevel.TYPE_ERROR)
							execState = ExecutionState.COMPLETED_FAILURE;
						else if (maxLogLevel == LogEntry.LogEntryLevel.TYPE_WARNING)
							execState = ExecutionState.COMPLETED_WARNINGS;
						else
							execState = ExecutionState.COMPLETED_SUCCESS;
					}
					else
						execState = ExecutionState.COMPLETED_SUCCESS;
					
					logger.debug("Execution state is --> " + execState);
				}
			}
		}
		else
			execState = ExecutionState.COMPLETED_SUCCESS;
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#cancel()
	 */
	@Override
	public void cancel() {
		ExecutionState execState = this.getExecutionState();
		if (execState==ExecutionState.RUNNING)
			setExecutionState(ExecutionState.CANCELLED);
		ArrayList<String> taskUIDs = new ArrayList<String>(1);
		taskUIDs.add(this.getUID());
		IRBootstrapperData.getInstance().cancelTasks(taskUIDs);
	}

	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#toXML(java.lang.StringBuilder)
	 */
	public void toXML(StringBuilder output) {
		output.append("<task tasktype=\"");
		output.append(this.taskTypeName);
		output.append("\" name=\"");
		output.append(this.entityName);
		output.append("\"/>");
	}
	
	/**
	 * Executes the task. No exceptions can be thrown by this method. In case
	 * of errors or warnings, the task implementation should log an appropriate
	 * message to the execution log.
	 * @param execData a {@link TaskExecutionData} object containing useful information that
	 * can be used during the task's execution 
	 */
	public abstract void executeTask(TaskExecutionData execData);
	
	/**
	 * Returns a {@link Document} object defining the XML structure of this task's contents.
	 * @return the XML task definition document
	 */
	public abstract Document getXMLTaskDefinitionDocument() throws Exception;
}
