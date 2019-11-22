package org.gcube.data.analysis.tabulardata.task;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.OnRowErrorAction;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStepClassifier;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.WorkerStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskStep;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ValidationStep;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.gcube.data.analysis.tabulardata.utils.InternalInvocation;


@Entity
public class TaskContext {

	//private static Logger logger = LoggerFactory.getLogger(TaskContext.class);
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;


	private List<InternalInvocation> invocationCouples;

	private List<InternalInvocation> postValidations = new ArrayList<>();
	
	private List<InternalInvocation> postOperations = new ArrayList<>();
	
	private List<TaskStep> tasks ;
	
	private List<TaskStep> postValidationTasks = new ArrayList<>();
	
	private List<TaskStep> postOperationTasks = new ArrayList<>();
	

	private TableId currentTable;

	private TableId startingTable;
	
	@Column
	private int step;


	@Column
	private OnRowErrorAction behaviour;

	@SuppressWarnings("unused")
	private TaskContext(){}
	
	public TaskContext(List<InternalInvocation> invocationCouples, OnRowErrorAction behaviour){
		this.step = -1;
		this.invocationCouples = new ArrayList<InternalInvocation>(invocationCouples);
		this.behaviour = behaviour;
		this.tasks = getTaskSteps(invocationCouples, TaskStepClassifier.PROCESSING);
	}

	public void addPostValidations(List<InternalInvocation> postValidations){
		this.postValidations = postValidations;
		this.postValidationTasks = getTaskSteps(postValidations, TaskStepClassifier.DATAVALIDATION);
	}
	
	public void addPostOperations(List<InternalInvocation> postOperations){
		this.postOperations = postOperations;
		this.postOperationTasks = getTaskSteps(postOperations, TaskStepClassifier.POSTPROCESSING);
	}
	
	public List<InternalInvocation> getPostValidations() {
		return postValidations;
	}

	public List<InternalInvocation> getPostOperations() {
		return postOperations;
	}

	public List<TaskStep> getPostValidationTasks() {
		return postValidationTasks;
	}

	public List<TaskStep> getPostOperationTasks() {
		return postOperationTasks;
	}

	

	/**
	 * @return the invocationCouple
	 */
	public List<InternalInvocation> getInvocationCouples() {
		return Collections.unmodifiableList(invocationCouples);
	}

	public boolean isParallelizableExecution(){
		for (InternalInvocation invocation : getInvocationCouples())
			if (invocation.isNop() || !invocation.getWorkerFactory().getWorkerType().equals(ResourceCreatorWorker.class))
				return false;
		return true;
	}
	
	
	/**
	 * @return the tasks
	 */
	public List<TaskStep> getTasks() {
		List<TaskStep> steps = new ArrayList<>(this.tasks);
		steps.addAll(this.postValidationTasks);
		steps.addAll(this.postOperationTasks);
		return Collections.unmodifiableList(steps);
	}
	

	public void insertRecoveryInvocation(InternalInvocation invocation){
		if (!isFirst()){
			invocationCouples.add(step, invocation);
			tasks.add(step,retrieveSingleTaskStep(invocation, TaskStepClassifier.PROCESSING));
		}else{
			invocationCouples.add(step, invocation);
			tasks.add(0,retrieveSingleTaskStep(invocation, TaskStepClassifier.PROCESSING));
		}
	}

	public boolean isFirst(){
		return step==0;
	}

	public boolean isBeforeFirst(){
		return step==-1;
	}
	
	public InternalInvocation getCurrentInvocation(){
		return invocationCouples.get(step);
	}

	public TaskStep getCurrentTask(){
		return tasks.get(step);
	}

	public boolean hasNext(){
		return step<invocationCouples.size()-1;
	}

	public boolean moveNext(){
		if (!hasNext()) return false;
		step++;
		return true;
	}

	public boolean movePrevious(){
		if (isBeforeFirst()) return false;
		this.resetCurrentStep();
		step--;
		return true;
	}

	private void resetCurrentStep() {
		TaskStep taskStep = retrieveSingleTaskStep(this.getCurrentInvocation(), TaskStepClassifier.PROCESSING);	
		this.tasks.set(step, taskStep );
	}

	public void cleanValidationsOnCurrentStep(){
		getCurrentTask().cleanValidations();
	}
	
	private List<TaskStep> getTaskSteps(List<InternalInvocation> invocations, TaskStepClassifier classifier) {
		List<TaskStep> workerSteps = new ArrayList<TaskStep>();
		for (InternalInvocation invocation : invocations)
			workerSteps.add(retrieveSingleTaskStep(invocation, classifier));
		return workerSteps;
	}

	private TaskStep retrieveSingleTaskStep(InternalInvocation invocation, TaskStepClassifier classifier){

		if (invocation.isNop()){
			return new TaskStep(null, TaskStepClassifier.PROCESSING);
		}else{
			OperationExecution opExecution= new OperationExecution(invocation.getWorkerFactory().getOperationDescriptor().getOperationId().getValue(), invocation.getParameters());
			if (invocation.getColumnId()!=null)
				opExecution.setColumnId(invocation.getColumnId().getValue());
			
			TaskStep toReturn = new TaskStep(opExecution, classifier);
			toReturn.setExecutionDescription(invocation.getWorkerFactory().getOperationDescriptor().getDescription());
			
			Collection<WorkerFactory<ValidationWorker>> precoditions = invocation.getWorkerFactory().getPreconditionValidationMap().values();
			
			if (!precoditions.isEmpty()){
				List<ValidationStep> validationSteps = new ArrayList<ValidationStep>(precoditions.size());
				for (WorkerFactory<?> preconditionFactory: precoditions){
					ValidationStepUpdater validationStep = new ValidationStepUpdater();
					validationStep.setExecutionDescription(preconditionFactory.getOperationDescriptor().getDescription());
					validationStep.setStatus(WorkerStatus.PENDING);
					validationSteps.add(validationStep);
				}
				toReturn.setValidationSteps(validationSteps);
			} else if (invocation.getWorkerFactory().getWorkerType().equals(ValidationWorker.class)){
				ValidationStepUpdater validationStep = new ValidationStepUpdater();
				validationStep.setExecutionDescription(invocation.getWorkerFactory().getOperationDescriptor().getDescription());
				validationStep.setStatus(WorkerStatus.PENDING);
				toReturn.setValidationSteps(Collections.singletonList((ValidationStep)validationStep));				
			}
			return toReturn;
		}
		
	}


	public TableId getCurrentTable() {
		return currentTable;
	}


	public void setCurrentTable(TableId currentTable) {
		this.currentTable = currentTable;
	}


	public TableId getStartingTable() {
		return startingTable;
	}


	public void setStartingTable(TableId startingTable) {
		this.startingTable = startingTable;
	}

	/**
	 * @return the behaviour
	 */
	public OnRowErrorAction getBehaviour() {
		return behaviour;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TaskContext [id=" + id + ", invocationCouples="
				+ invocationCouples + ",  tasks=" + tasks
				+ ", step=" + step
				+ ", behaviour=" + behaviour + "]";
	}


	public void addParametersOnNextOperation(
			Map<String, Object> instanceParametersToChange) {
		if (!hasNext()) return;
		tasks.get(step+1).getSourceInvocation().getParameters().putAll(instanceParametersToChange);
		invocationCouples.get(step+1).getParameters().putAll(instanceParametersToChange);
	}

	public void resetPostOperationsForResume(){
		this.postValidationTasks = getTaskSteps(postValidations, TaskStepClassifier.POSTPROCESSING);
		this.postOperationTasks = getTaskSteps(postOperations, TaskStepClassifier.POSTPROCESSING);
	}
		

}
