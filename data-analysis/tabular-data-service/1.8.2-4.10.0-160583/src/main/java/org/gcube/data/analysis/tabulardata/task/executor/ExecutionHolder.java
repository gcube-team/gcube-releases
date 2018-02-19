package org.gcube.data.analysis.tabulardata.task.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.data.analysis.tabulardata.metadata.StorableHistoryStep;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;
import org.gcube.data.analysis.tabulardata.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionHolder {

	private Logger logger = LoggerFactory.getLogger(ExecutionHolder.class);
	
	private Set<TableId> toRemoveOnFinish = new HashSet<TableId>();
	
	
	private List<StorableHistoryStep> stepsToAddOnSuccess = new ArrayList<StorableHistoryStep>();
	
	private List<ResourceHolder> createdResources = new ArrayList<ResourceHolder>();
	
	//maintains mapping for dinamically created column (key is InovactionID+index)
	private Map<String, ColumnLocalId> createdColumnMapping = new HashMap<String, ColumnLocalId>();
	
	public void removeOnFinish(TableId tableId){
		logger.trace("added to RemoveOnFinish "+tableId);
		toRemoveOnFinish.add(tableId);
	}
	
	public void addCreatedResource(ResourceHolder resource){
		createdResources.add(resource);
	}
	
	public void createStep(WorkerFactory<?> factory,
			OperationInvocation sourceInvocation, WorkerResult finalResult, TableId tableBeforeOperationId) {
		StorableHistoryStep step;
		String operationDescription = factory.getOperationDescriptor().getDescription();
		try {
			operationDescription = factory.describeInvocation(sourceInvocation);
		} catch (Exception e) {
			logger.warn("operation description not found");
		} 
		
		if (factory.isRollbackable() && finalResult.getDiffTable()!=null){ 
			step = new StorableHistoryStep(finalResult.getDiffTable().getId().getValue(), Util.toOperationExecution( sourceInvocation), operationDescription);
			step.setContainsDiff(true);
		}else 
			step = new StorableHistoryStep(tableBeforeOperationId==null?null:tableBeforeOperationId.getValue(), Util.toOperationExecution( sourceInvocation), operationDescription);
		stepsToAddOnSuccess.add(step);
	}

	protected Set<TableId> getToRemoveOnFinish() {
		return toRemoveOnFinish;
	}

	protected List<StorableHistoryStep> getStepsToAddOnSuccess() {
		return stepsToAddOnSuccess;
	}

	protected List<ResourceHolder> getCreatedResources() {
		return createdResources;
	}
	
	public void addColumnCreatedMapping(String invocationId, ColumnLocalId columnId){
		this.createdColumnMapping.put(invocationId, columnId);
	}
	
	public ColumnLocalId getColumnCorrespondance(String invocationId){
		return this.createdColumnMapping.get(invocationId);
	}
	
	public boolean areNewColumnsBeenCreated(){
		return !createdColumnMapping.isEmpty();
	}
	
	public static class ResourceHolder{
		
		private ResourceDescriptorResult resourceDescriptor;
		private long creatorId;
				
		public ResourceHolder(ResourceDescriptorResult resourceDescriptor,
				long creatorId) {
			super();
			this.resourceDescriptor = resourceDescriptor;
			this.creatorId = creatorId;
		}
		
		public ResourceDescriptorResult getResourceDescriptor() {
			return resourceDescriptor;
		}
		public long getCreatorId() {
			return creatorId;
		}
	}
	
	
}
