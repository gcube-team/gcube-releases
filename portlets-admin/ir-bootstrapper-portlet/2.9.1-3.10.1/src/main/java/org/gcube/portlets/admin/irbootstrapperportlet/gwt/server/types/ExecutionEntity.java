/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types;

import java.util.UUID;

import gr.uoa.di.madgik.taskexecutionlogger.model.WorkflowLogEntry;

//import org.apache.axis.components.uuid.UUIDGen;
//import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging.TaskExecutionLogger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityExecutionData;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public abstract class ExecutionEntity implements Cloneable, Evaluable {
	
	public enum ExecutionState { NOT_STARTED, RUNNING, COMPLETED_SUCCESS, COMPLETED_FAILURE, COMPLETED_WARNINGS, CANCELLED }
	
	/** A generator of UUIDs */
	//private static UUIDGen UIDgen = UUIDGenFactory.getUUIDGen();
	
	/** The parent execution entity in the execution hierarchy */
	protected ExecutionEntity parent;
	
	/** The name of this entity instance */
	protected String entityName;
	
	/** Specifies whether the goal of this execution entity has been fulfilled or not */
	protected boolean isGoalFulfilled;
	
	/** The unique identifier of this entity */
	protected String UID;
	
	/** The state of execution of this {@link ExecutionEntity} */
	protected ExecutionState execState;
	
	/** The scope that this execution entity belongs to */
	protected String scope;
	
	/** The execution logger object */
	private TaskExecutionLogger executionLogger;
	
	/** The workflow logger object */
	private WorkflowLogEntry workflowLogger;
	
	/**
	 * Class constructor
	 */
	public ExecutionEntity() {
		this.parent = null;
		this.entityName = null;
		this.isGoalFulfilled = false;
		this.assignRandomUID();
		this.execState = ExecutionState.NOT_STARTED;
		this.executionLogger = null;
	}
	
	/**
	 * Returns the parent of this execution entity
	 * @return the parent entity
	 */
	public ExecutionEntity getParent() {
		return this.parent;
	}
	
	/**
	 * Sets the parent of this execution entity
	 * @param parent the parent
	 */
	public void setParent(ExecutionEntity parent) {
		this.parent = parent;
	}
	
	/**
	 * Returns the name of this execution entity
	 * @return the entity's name
	 */
	public String getName() {
		return entityName;
	}
	
	/**
	 * Returns the entity's UID
	 * @return the UID
	 */
	public String getUID() {
		return this.UID;
	}
	
	/**
	 * Assigns a new, randomly generated UID to this {@link ExecutionEntity}
	 */
	public void assignRandomUID() {
		this.UID = UUID.randomUUID().toString();//UIDgen.nextUUID();
	}
	
	/**
	 * Returns true if this entity's goal is fulfilled, or false otherwise
	 * @return
	 */
	public boolean isFulfilled() {
		return isGoalFulfilled;
	}
	
	/**
	 * Sets this execution entity as 'fulfilled', or not 'fulfilled'
	 * @param isFulfilled
	 */
	public void setIsFulfilled(boolean isFulfilled) {
		this.isGoalFulfilled = isFulfilled;
	}
	
	/**
	 * Sets the execution state of this entity
	 * @param state the execution state to set
	 */
	synchronized public void setExecutionState(ExecutionState state) {
		this.execState = state;
	}
	
	/**
	 * Returns the current execution state of this entity
	 * @return the execution state
	 */
	synchronized public ExecutionState getExecutionState() {
		return this.execState;
	}
	
	/**
	 * Sets the scope that this execution entity belongs to
	 * @param scope the entity's scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	/**
	 * Returns the scope that this execution entity belongs to
	 * @return the entity's scope
	 */
	public String getScope() {
		return this.scope;
	}
	
	/**
	 * Creates a new, empty execution logger
	 * @param serverLogger the server-side logger currently in use by this execution entity
	 */
	protected void createExecutionLog(Logger serverLogger, ASLSession session) {
		this.executionLogger = new TaskExecutionLogger(serverLogger, session);
	}
	
	/**
	 * Returns the execution logger
	 * @return
	 */
	public TaskExecutionLogger getExecutionLogger() {
		return this.executionLogger;
	}
	
	protected void setWorkflowLogger(WorkflowLogEntry logger) {
		this.workflowLogger = logger;
	}
	
	/**
	 * Returns the workflow logger
	 * @return
	 */
	public WorkflowLogEntry getWorkflowLogger() {
		return this.workflowLogger;
	}
	
	/**
	 * Returns the name of this {@link ExecutionEntity}'s type
	 * @return the type name
	 */
	public abstract String getTypeName();
	
	/**
	 * Executes the logic of this execution entity. This method cannot
	 * throw an exception. In the case of errors, appropriate messages must
	 * be logged in the execution log.
	 * @param execData object containing useful data for the execution of the entity
	 */
	public abstract String execute(EntityExecutionData execData);
	
	/**
	 * Cancels the execution of this execution entity. If the entity is not
	 * currently executing, calling this method has no effect.
	 */
	public abstract void cancel();
	
	/**
	 * Instantiates the execution entity type represented by this object.
	 * The instance will have the given name.
	 * @param name the new instance name
	 * @return the new instance
	 */
	public abstract ExecutionEntity newInstance(String name);
	
	/**
	 * Evaluates the given expression on the execution tree rooted at the
	 * current object and returns a Node representing the result
	 * @param expression the expression to evaluate, in "a.b.c..." notation
	 * @return the Node object that represents the result
	 */
	public abstract EvaluationResult evaluate(String expression);

	/**
	 * Finds the DataTypes that could be possibly used as inputs for this entity
	 * as well as each sub-entity contained in it. The possible inputs for each
	 * entity are stored in a list inside the entity itself, so this method does
	 * not need to return anything. Furthermore, this method checks if the desirable
	 * output of this entity already exists in the given scope, and if it does, the
	 * entity's goal is defined as being already 'fulfilled' (the entity does not
	 * need to be executed). 
	 * @param scope the scope in which the entity will be executed
	 */
	public abstract void initializeWithDataInScope(String scope) throws Exception;

	/**
	 * Returns a string which will be displayed in the user interface as a
	 * description for this entity.
	 * @return the entity description
	 */
	public abstract String getUIDescription();
	
	/**
	 * Outputs an XML description of the {@link ExecutionEntity} to the given
	 * {@link StringBuilder} object.
	 * @param output the {@link StringBuilder} to write to
	 */
	public abstract void toXML(StringBuilder output);
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		ExecutionEntity ee = (ExecutionEntity) super.clone();
		ee.assignRandomUID();
		return ee;
	}
}
