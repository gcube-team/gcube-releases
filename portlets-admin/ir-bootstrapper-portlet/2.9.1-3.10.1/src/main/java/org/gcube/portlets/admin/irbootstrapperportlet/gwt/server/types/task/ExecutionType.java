/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.BootstrappingConfiguration;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.IRBootstrapperData;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.EvaluationResult;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.ExecutionEntity;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public abstract class ExecutionType extends ExecutionEntity implements Cloneable {

	/** Logger */
	private static Logger logger = Logger.getLogger(ExecutionType.class);
	
	/** The list of execution entities contained inside this execution type */
	protected List<ExecutionEntity> executionEntities;
		
	/**
	 * Class constructor
	 */
	public ExecutionType() {
		this.UID = getTypeName();
		executionEntities = new LinkedList<ExecutionEntity>();
	}
	
	/**
	 * Adds an ExecutionEntity in the list of entities to be executed
	 * @param entity the ExecutionEntity to add
	 */
	public void addExecutionEntity(ExecutionEntity entity) {
		executionEntities.add(entity);
	}
	
	/**
	 * Sets the execution entities contained inside this ExecutionType object. The entities are given
	 * in the form of an XML document fragment, which is parsed and transformed to objects.
	 * @param xml the XML describing the execution entities wrapped into this ExecutionType
	 * @param conf the active {@link BootstrappingConfiguration} object
	 * @throws Exception
	 */
	public void initialize(Element xml, BootstrappingConfiguration conf) throws Exception {
		
		/* Retrieve the child elements of the current node, which describe
		 * the execution entities wrapped inside this ExecutionType .
		 */
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nl = null;
		try {
			nl = (NodeList) xpath.evaluate("*", xml, XPathConstants.NODESET);
		} catch (Exception e) {
			logger.error("Failed to initialize execution construct: " + this.getClass().getSimpleName(), e);
			throw new Exception("Failed to initialize execution construct: " + this.getClass().getSimpleName());
		}
		
		/* Now parse each child element, getting back a ExecutionEntity object
		 * for each one of them. These objects are added to the executionEntities
		 * list.
		 */
		try {
			for (int i=0; i<nl.getLength(); i++) {
				ExecutionEntity ee = conf.parseJobExecutionEntity((Element) nl.item(i));
				ee.setParent(this);
				this.addExecutionEntity(ee);
			}
		} catch (Exception e) {
			logger.error("Error while parsing ExecutionEntities inside an execution construct: " + this.getClass().getSimpleName(), e);
			throw new Exception("Error while parsing ExecutionEntities inside an execution construct: " + this.getClass().getSimpleName());
		}
	}
	
	/**
	 * Returns the list of {@link ExecutionEntity}s contained in this {@link ExecutionType}
	 * @return the list of entities to be executed by this {@link ExecutionType}
	 */
	public List<ExecutionEntity> getEntitiesToBeExecuted() {
		return executionEntities;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#evaluate(java.lang.String)
	 */
	public EvaluationResult evaluate(String expression) {
		for (ExecutionEntity child : executionEntities) {
			EvaluationResult ret = child.evaluate(expression);
			if (ret != null)
				return ret;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#findMatchingInputsInScope(org.gcube.common.core.scope.GCUBEScope)
	 */
	public void initializeWithDataInScope(String scope) throws Exception {
		/* Initially consider this ExecutionType's goal as fulfilled */
		this.setIsFulfilled(true);
		
		for (ExecutionEntity ee : executionEntities) {	
			/* - If the current execution entity is a task, then find the matching
			 * inputs for it.
			 * - If the entity is an ExecutionType, recursively invoke 
			 * 'initializeWithDataInScope' on it.
			 * - If the entity is a AssignTaskType, just execute it 
			 * (assignments must be executed so that input values are 
			 * assigned to tasks in order to be able to find possible
			 * matches for these tasks' inputs)
			 * 
			 * In any case, check if the entity's goal is already fulfilled
			 * and if at least one entity's goal is not fulfilled, then
			 * the whole ExecutionType's goal is considered as 'not fulfilled'.
			 * 
			 */
			if ((ee instanceof CustomTaskType) || (ee instanceof ExecutionType)) /* Task or ExecutionType */
				ee.initializeWithDataInScope(scope);
			else if (ee instanceof AssignTaskType)/* AssignTaskType */
				((AssignTaskType) ee).doAssignment();
			
			if (!ee.isFulfilled())
				this.setIsFulfilled(false);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#cancel()
	 */
	@Override
	public void cancel() {
		for (ExecutionEntity ee : this.getEntitiesToBeExecuted())
			ee.cancel();
		ArrayList<String> taskUIDs = new ArrayList<String>(1);
		IRBootstrapperData.getInstance().cancelTasks(taskUIDs);
		
		for (ExecutionEntity ee : this.getEntitiesToBeExecuted()) {
			ExecutionState execState = ee.getExecutionState();
			if (execState==ExecutionState.CANCELLED)
				setExecutionState(ExecutionState.CANCELLED);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		ExecutionType et = (ExecutionType) super.clone();
		et.executionEntities = new LinkedList<ExecutionEntity>();
		for (ExecutionEntity e : executionEntities) {
			ExecutionEntity ee = (ExecutionEntity) e.clone();
			ee.setParent(et);
			et.addExecutionEntity(ee);
		}
		return et;
	}
}
