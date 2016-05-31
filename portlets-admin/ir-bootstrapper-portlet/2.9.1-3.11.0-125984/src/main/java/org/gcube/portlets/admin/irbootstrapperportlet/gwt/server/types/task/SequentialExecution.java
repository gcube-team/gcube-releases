/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.ExecutionEntity;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityExecutionData;


/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class SequentialExecution extends ExecutionType {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(SequentialExecution.class);
	
	public SequentialExecution() { }
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#newInstance(java.lang.String)
	 */
	@Override
	public SequentialExecution newInstance(String name) {
		SequentialExecution se = new SequentialExecution();
		se.entityName = name;
		return se;
	}

	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#execute(org.gcube.portlets.admin.irbootstrapperportlet.servlet.util.EntityExecutionData)
	 */
	public String execute(EntityExecutionData eed) {
		if (!this.isFulfilled()) {
			this.createExecutionLog(logger, eed.getSession());
			this.setWorkflowLogger(eed.getWorkflowLogLogger());
			execState = ExecutionState.RUNNING;
			
			/* Loop through the entities to execute and execute each them in sequence */
			for (ExecutionEntity ee : this.getEntitiesToBeExecuted()) {
				
				ee.execute(eed);
				logger.debug(getUID() + ": Sub-task " + ee.getUID() + " completed execution.");
				
				/* If the entity failed to complete, this SequentialExecution should also fail.
				 * If the entity completed with warnings, this SequentialExecution should also contain a warning about this. */
				if (ee.getExecutionState() == ExecutionState.COMPLETED_FAILURE) {
					this.getExecutionLogger().error("An error occured while executing sub-task '" + ee.getName() + "'. Please see the sub-task's log for details.", null, false);
					execState = ExecutionState.COMPLETED_FAILURE;
					return null;
				}
				else if (ee.getExecutionState() == ExecutionState.COMPLETED_WARNINGS) {
					this.getExecutionLogger().warn("Sub-task '" + ee.getName() + "' was completed with warnings. Please see the sub-task's log for details.");
					execState = ExecutionState.COMPLETED_WARNINGS;
				}
			}
			
			/* If the current state of this SequentialExecution is not "failed" or "completed with warnings", set it
			 * to "completed successfully" */
			if (execState == ExecutionState.RUNNING)
				execState = ExecutionState.COMPLETED_SUCCESS;
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
		return "Sequential execution";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#getTypeName()
	 */
	public String getTypeName() {
		return "SequentialExecution";
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.ExecutionEntity#toXML(java.lang.StringBuilder)
	 */
	@Override
	public void toXML(StringBuilder output) {
		output.append("<sequential>");
		for (ExecutionEntity ee : this.getEntitiesToBeExecuted()) {
			ee.toXML(output);
		}
		output.append("</sequential>");
	}
}
