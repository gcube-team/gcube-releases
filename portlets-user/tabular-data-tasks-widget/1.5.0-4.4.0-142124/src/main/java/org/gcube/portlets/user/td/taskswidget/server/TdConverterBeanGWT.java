/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchOperationException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.WorkerStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.service.operation.Job;
import org.gcube.data.analysis.tabulardata.service.operation.JobClassifier;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.operation.TaskResult;
import org.gcube.data.analysis.tabulardata.service.operation.ValidationJob;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.portlets.user.td.taskswidget.client.ConstantsTdTasks;
import org.gcube.portlets.user.td.taskswidget.server.exception.TdConverterException;
import org.gcube.portlets.user.td.taskswidget.server.service.TaskTabularDataService;
import org.gcube.portlets.user.td.taskswidget.server.util.TdUserUtil;
import org.gcube.portlets.user.td.taskswidget.shared.TdTableModel;
import org.gcube.portlets.user.td.taskswidget.shared.TdTabularResourceModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdJobClassifierType;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdJobModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdJobStatusType;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdOperationModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 18, 2013
 *
 */
public class TdConverterBeanGWT {

	/**
	 * 
	 */

	public static Logger logger = LoggerFactory.getLogger(TdConverterBeanGWT.class);
	
	public static TdOperationModel operationDefinitionToOperationModel(OperationDefinition opd) throws TdConverterException{
		

		TdOperationModel om = new TdOperationModel();
		
		if(opd==null)
			throw new TdConverterException("OperationDefinition id is null");
		
//		if(opd.getOperationId()<0)
//			throw new TdConverterException("Operation id is less than zero");
		
		om.setOperationId(opd.getOperationId()+"");
		om.setName(opd.getName());
		om.setDescription(opd.getDescription());
		
		if(opd.getParameters()!=null){
			for (Parameter param: opd.getParameters()) {
				om.addParameter(param.toString());
			}
		}
//		om.setScope(getOperationScopeToOperationScopeModel(opd.getScope()));
//		om.setType(getOperationTyeToOperationTypeModel(opd.getType()));
		
		return om;

	}
	
	
	public static TdOperationModel operationExecutionToOperationModel(OperationExecution opd) throws TdConverterException{
		
		TdOperationModel om = new TdOperationModel();
		
		if(opd.getOperationId()<0)
			throw new TdConverterException("Operation id is less than zero");
		
		om.setOperationId(opd.getOperationId()+"");

//		om.setDescription(opd.get);
//		om.setName(opd.getName());
//		om.setScope(getOperationScopeToOperationScopeModel(opd.getScope()));
//		om.setType(getOperationTyeToOperationTypeModel(opd.getType()));
		
		return om;

	}
	
	public static TdTaskModel taskToTdTaskModel(Task task, TabularResourceId trId, TaskTabularDataService taskTabularDataService, Map<Long, OperationDefinition> mapOperationDefinition) throws TdConverterException{
		
		TdTaskModel tm = new TdTaskModel();
		
		if(task==null || task.getId()==null)
			throw new TdConverterException("Task or TaskId is null");
		
		tm.setId(task.getId().getValue());
		tm.setStartTime(task.getStartTime());
		tm.setEndTime(task.getEndTime());
		tm.setPercentage(task.getProgress());
		tm.setStatus(convetToTdTaskStatusType(task.getStatus()));
		tm.setCompleted(taskIsCompleted(task.getStatus()));
		
		
		String fullName = TdUserUtil.getUserFullName(task.getSubmitter());
		
		if(fullName.isEmpty())
			fullName = task.getSubmitter();
		
		tm.setSubmitter(fullName);
		
		tm.setTabularResourceId(trId.getValue());

		if(task.getTaskType()!=null && task.getTaskType().name()!=null){
			tm.setName(task.getTaskType().name());
		}else
			tm.setName("Not defined");
		


		//Converting task result
		TaskResult result = task.getResult();

		logger.trace("Converting Task Result..");
		
		List<TdTabularResourceModel> listCollateralTabulaResourceModel = null;
		
		if(result!=null){
			logger.trace("TaskResult is not null");
			listCollateralTabulaResourceModel = convertToListCollateralTabularResourceModel(result.getCollateralTables());
		}else
			logger.trace("TaskResult is NULL");
		
		
		tm.setListCollateralTRModel((ArrayList<TdTabularResourceModel>) listCollateralTabulaResourceModel);
		logger.trace("Converting Task Result, completed");

		logger.trace("Converting tabular resource ids...");
		//Converting tabular resource ids
		TdTableModel tdTableModel = convertToTdTableModel(result.getPrimaryTable());
		
		tm.setTdTableModel(tdTableModel);
		logger.trace("Converting tabular resource ids, completed");
		
		List<Job> jobs = task.getTaskJobs();
		
		List<TdJobModel> listJobModel = new ArrayList<TdJobModel>();
		
		int i = 0;
		if(jobs!=null){
			logger.info("List Job size is: "+jobs.size(), " converting job..");
			for (Job job : jobs) {
	//			TdJobModel jobModel = convertJobToTdJobModel(job);	
	//			tm.updateName(jobModel.getOpdModel().getName());
				String jobId = task.getId().getValue()+"-"+(i++); //creating unique Job Id
				TdJobModel jobModel = convertJobToTdJobModel(job, jobId,taskTabularDataService, mapOperationDefinition);
				
				if(job.getValidationJobs()!=null && job.getValidationJobs().size()>0){
					List<TdJobModel> listValidationJobModel = new ArrayList<TdJobModel>();
					logger.trace("Validation Job available: "+job.getValidationJobs().size(), " converting validation job..");
					
					for (ValidationJob validationJob : job.getValidationJobs()) {
						listValidationJobModel.add(fillBaseJobValuesToTdJobModel(null, UUID.randomUUID().toString(), validationJob));
					}
					
					logger.trace("Validation Job converted is/are: "+listValidationJobModel.size());
					jobModel.setListValidationJobs(listValidationJobModel);
				}
				
				listJobModel.add(jobModel);
			}
			
			//EXTENDING TASK NAME WITH FIRST JOB TYPE
			if(listJobModel.size()>0){
				TdJobModel tdJob = listJobModel.get(0);
	
				if(tdJob.getOpdModel()!=null && tdJob.getOpdModel().getName()!=null && !tdJob.getOpdModel().getName().isEmpty()){
					
					String jobType = tdJob.getOpdModel().getName();
					
					int max = jobType.length();
					boolean cutted = false;
					if(max>ConstantsTdTasks.MAX_CHARS_EXTENDED_TASK_NAME){
						max = ConstantsTdTasks.MAX_CHARS_EXTENDED_TASK_NAME;
						cutted = true;
					}
	
					String taskName = tm.getName()+" ["+jobType.substring(0, max);
					if(cutted)
						taskName+="..., etc.";
					
					taskName+="]";
					
					tm.setName(taskName); //SETTING NEW TASK NAME
				}
			}
		}else
			logger.info("List Job is null, returning empty list");
		
		tm.setListJobs((ArrayList<TdJobModel>) listJobModel);
		logger.trace("Converting List Job with size "+listJobModel.size()+", completed");
		
//		System.out.println("Converting List Job with size "+listJobModel.size()+", completed");
		logger.trace("Task Result converted in TdTaskModel, return");
	
		return tm;
		
	}
	
	/**
	 * 
	 * @param job
	 * @param jobId 
	 * @param taskTabularDataService 
	 * @param mapOperationDefinition 
	 * @return
	 * @throws NoSuchOperationException 
	 */
	public static TdJobModel convertJobToTdJobModel(Job job, String jobId, TaskTabularDataService taskTabularDataService, Map<Long, OperationDefinition> mapOperationDefinition) {
		logger.info("Converting job to job model...");
		TdJobModel jobModel = new TdJobModel();
//		jobModel.setJobIdentifier(jobId);
		
		jobModel = fillBaseJobValuesToTdJobModel(jobModel, jobId, job);

		/* NOT USED AT MOMENT
		List<ValidationDescriptor> validations = job.getValidations();
		
		if(validations!=null && validations.size()>0){
			List<TdValidationDescription> lstVD = new ArrayList<TdValidationDescription>(validations.size());
			for (ValidationDescriptor vd : validations) {
				lstVD.add(new TdValidationDescription(vd.getDescription(), vd.getValidationColumn(), vd.getConditionCode()));
			}
			
			jobModel.setValidations(lstVD);
		}
		*/
		
//		TdJobClassifierType classifierType = convertClassifierToTdJobClassifierType(job.getClassifier());
		
		//THIS IS STATIC
		jobModel.setClassifierType(TdJobClassifierType.PROCESSING);

		long jobServcerId = -1;
		if(job.getInvocation()!=null){
			try {
//				TdOperationModel opdModel = operationExecutionToOperationModel(job.getInvocation());
				
				jobServcerId = job.getInvocation().getOperationId();

				OperationDefinition opd = mapOperationDefinition.get(jobServcerId);
				logger.trace("Operation definition read from map to jobServcerId: "+jobServcerId +" is null? "+(opd==null));
				if(opd == null){
					logger.trace("Operation definition read from map is null, trying to read from service");
					opd = taskTabularDataService.getOperationDescriptionById(jobServcerId);
					logger.trace("Operation definition read from service to jobServcerId: "+jobServcerId +" is null? "+(opd==null));
				}
//				OperationDefinition opd = taskTabularDataService.getOperationDescriptionById(jobServcerId);
				jobModel.setOpdModel(operationDefinitionToOperationModel(opd));
				
			}catch (NoSuchOperationException e) {
				logger.warn("No such operation for id: "+jobServcerId, e);
			} catch (TdConverterException e) {
				logger.warn("Error on converting operation model into job convert: ",e);
			}
		}
		logger.trace("Returning job model: "+jobModel);
		return jobModel;
		
	}
	
	/**
	 * 
	 * @param jobId 
	 * @param job
	 * @param jobId 
	 * @param taskTabularDataService 
	 * @param mapOperationDefinition 
	 * @return
	 * @throws NoSuchOperationException 
	 */
	public static TdJobModel fillBaseJobValuesToTdJobModel(TdJobModel jobModel,String jobId, ValidationJob job) {
		logger.info("Converting validation job to job model...");

		if(jobModel==null)
			 jobModel = new TdJobModel();
//		
		jobModel.setJobIdentifier(jobId);
		jobModel.setHumanReadableStatus(job.getHumaReadableStatus());
		jobModel.setProgressPercentage(job.getProgress());
		jobModel.setDescription(job.getDescription());
		
		if(job.getErrorMessage()!=null){
			jobModel.setErrorMessage(job.getErrorMessage().getMessage().toString());
		}
	
		TdJobStatusType jobStatus = convetToTdJobStatusType(job.getStatus());
		jobModel.setStatus(jobStatus);
		
		logger.trace("Returning job model: "+jobModel);
		return jobModel;
		
	}
	
	
	/**
	 * @param classifier
	 */
	public static TdJobClassifierType convertClassifierToTdJobClassifierType(JobClassifier classifier) {
		
		if(classifier==null)
			return TdJobClassifierType.CLASSIFIER_UNKNOWN;
		
		switch (classifier) {
//		case FALLBACK: return TdJobClassifierType.FALLBACK;
		case POSTPROCESSING: return TdJobClassifierType.POSTPROCESSING;
		case PREPROCESSING:  return TdJobClassifierType.PREPROCESSING;
		case PROCESSING: return TdJobClassifierType.PROCESSING;
		case DATAVALIDATION: return TdJobClassifierType.DATAVALIDATION;
		default:
			return TdJobClassifierType.CLASSIFIER_UNKNOWN;
		}
		
	}

	/**
	 * Use for task
	 * @param taskStatus
	 * @return
	 */
	public static TdTaskStatusType convetToTdTaskStatusType(TaskStatus taskStatus){
		
		switch (taskStatus) {
			case IN_PROGRESS: return TdTaskStatusType.RUNNING;
			case ABORTED: return TdTaskStatusType.ABORTED;
			case INITIALIZING: return TdTaskStatusType.INITIALIZING;
			case FAILED: return TdTaskStatusType.FAILED;
//			case FALLBACK: return TdTaskStatusType.FALLBACK;
			case STOPPED : return TdTaskStatusType.STOPPED;
			case SUCCEDED: return TdTaskStatusType.COMPLETED;
			case VALIDATING_RULES: return TdTaskStatusType.VALIDATING_RULES;
			case GENERATING_VIEW: return TdTaskStatusType.GENERATING_VIEW;
		default:
			return TdTaskStatusType.STATUS_UNKNOWN;
		}
		
	}
	
	/**
	 * Use for job
	 * @param workerStatus
	 * @return
	 */
	public static TdJobStatusType convetToTdJobStatusType(WorkerStatus workerStatus){
		
		if(workerStatus==null)
			return TdJobStatusType.STATUS_UNKNOWN;
		
		switch (workerStatus) {
			case IN_PROGRESS: return TdJobStatusType.RUNNING;
			case INITIALIZING: return TdJobStatusType.INITIALIZING;
			case FAILED: return TdJobStatusType.FAILED;
//			case FALLBACK: return TdJobStatusType.FALLBACK;
			case VALIDATING_DATA: return TdJobStatusType.VALIDATING;
			case PENDING: return TdJobStatusType.PENDING;
			case SUCCEDED: return TdJobStatusType.COMPLETED;
			
		default:
			return TdJobStatusType.STATUS_UNKNOWN;
		}
		
	}
	
	public static boolean taskIsCompleted(TaskStatus taskStatus){

		return taskStatus.equals(TaskStatus.SUCCEDED) || taskStatus.equals(TaskStatus.STOPPED) || taskStatus.equals(TaskStatus.FAILED);
	}
	
	
	public static TdTableModel convertToTdTableModel(Table table){
		

		if(table==null){
			logger.trace("Get Primary Table from result. is null, returning null");
			return null;
		}
		
		logger.trace("Get id Primary Table from result: "+table.getId());
		
		TdTableModel tmModel = null;
		
		if(table.getId()!=null){
			tmModel = new TdTableModel(table.getId().getValue()+"", table.getName());
		}

		
		return tmModel;
	}
		
		
	public static List<TdTabularResourceModel> convertToListCollateralTabularResourceModel(List<TableId> result){
		
		List<TdTabularResourceModel> listTabulaResourceModel = new ArrayList<TdTabularResourceModel>();
		
		if(result==null){
			logger.trace("Convert Tabular Resource from CollateralTabularResources..param is null, return");
			return listTabulaResourceModel;
		}
		/*
		logger.trace("Converting.. List of TabularResource to TdTabularResourceModel, size: "+result.size());
		for (TableId  tableId : result) {
			if(tableId!=null){
				long id = tableId.getValue();
				//TODO 
				
			
				listTabulaResourceModel.add(new TdTabularResourceModel(id+""));
				logger.trace("added id: "+id);
			}
		}
		*/
		return listTabulaResourceModel;
	}
	

	
	
}
