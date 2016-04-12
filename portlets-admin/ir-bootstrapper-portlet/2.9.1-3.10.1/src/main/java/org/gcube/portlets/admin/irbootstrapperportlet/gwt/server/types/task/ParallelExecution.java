/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.IRBootstrapperData;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.ExecutionEntity;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityExecutionData;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class ParallelExecution extends ExecutionType {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(ParallelExecution.class);
	
	public ParallelExecution() { }
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#newInstance(java.lang.String)
	 */
	@Override
	public ParallelExecution newInstance(String name) {
		ParallelExecution pe = new ParallelExecution();
		pe.entityName = name;
		return pe;
	}

	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#execute(org.gcube.portlets.admin.irbootstrapperportlet.servlet.util.EntityExecutionData)
	 */
	public String execute(final EntityExecutionData eed) {
		if (!this.isFulfilled()) {
			IRBootstrapperData irbd = IRBootstrapperData.getInstance();
			LinkedList<Runnable> rList = new LinkedList<Runnable>();
			LinkedList<String> sList = new LinkedList<String>();
			
			this.createExecutionLog(logger, eed.getSession());
			this.setWorkflowLogger(eed.getWorkflowLogLogger());
			for (ExecutionEntity ee : this.getEntitiesToBeExecuted()) {
				final ExecutionEntity entityToExecute = ee;
				rList.add(new Runnable() {
					public void run() {
						/* Execute the entity and in case of error set the execution status
						 * of this parallel execution element to "completed with failure"
						 */
						entityToExecute.execute(eed);
						logger.debug(getUID() + ": Sub-task " + entityToExecute.getUID() + " completed execution.");
						
						/* If the entity completed with warnings, this ParallelExecution should also contain a warning about this. */
						if (entityToExecute.getExecutionState() == ExecutionState.COMPLETED_FAILURE)
							getExecutionLogger().error("An error occured while executing sub-task '" + entityToExecute.getName() + "'. Please see the sub-task's log for details.", null, false);
						else if (entityToExecute.getExecutionState()==ExecutionState.COMPLETED_WARNINGS)
							getExecutionLogger().warn("Sub-task '" + entityToExecute.getName() + "' was completed with warnings. Please see the sub-task's log for details.");
					}
				});
				sList.add(ee.getUID());
			}
			
			/* Initialize the execution status to "running" */
			setExecutionState(ExecutionState.RUNNING);
			
			/* Submit the child tasks for execution and wait for their completion */
			irbd.submitRunnablesForExecutionAndBlock(rList, sList);
			
			/* If the execution state is still "running", it means that no errors occurred
			 * during the execution of the children. So, set the state to "completed successfully"
			 */
			if (getExecutionState() == ExecutionState.RUNNING)
				setExecutionState(ExecutionState.COMPLETED_SUCCESS);
			
			/* If at least one child has been cancelled, the whole parallel execution
			 * entity is marked as 'cancelled'.
			 * If not cancelled but at least one child has completed with errors,
			 * the whole parallel entity is marked as "completed with errors".
			 */
			for (ExecutionEntity ee : this.getEntitiesToBeExecuted()) {
				ExecutionState execState = ee.getExecutionState();
				if (execState == ExecutionState.CANCELLED) {
					setExecutionState(ExecutionState.CANCELLED);
					break;
				}
				else if (execState == ExecutionState.COMPLETED_FAILURE)
					setExecutionState(ExecutionState.COMPLETED_FAILURE);
				else if (execState == ExecutionState.COMPLETED_WARNINGS) {
					if (getExecutionState() == ExecutionState.COMPLETED_SUCCESS)
						setExecutionState(ExecutionState.COMPLETED_WARNINGS);
				}
			}
		}
		else
			execState = ExecutionState.COMPLETED_SUCCESS;
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#getUIDescription()
	 */
	@Override
	public String getUIDescription() {
		return "Parallel execution";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#getTypeName()
	 */
	public String getTypeName() {
		return "ParallelExecution";
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#toXML(java.lang.StringBuilder)
	 */
	@Override
	public void toXML(StringBuilder output) {
		output.append("<parallel>");
		for (ExecutionEntity ee : this.getEntitiesToBeExecuted()) {
			ee.toXML(output);
		}
		output.append("</parallel>");
	}
}
