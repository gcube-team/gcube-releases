package org.gcube.portlets.user.td.gwtservice.shared.monitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.ExportMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.operation.Job;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.operation.TaskResult;
import org.gcube.data.analysis.tabulardata.service.operation.ValidationJob;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.portlets.user.td.gwtservice.server.SessionUtil;
import org.gcube.portlets.user.td.gwtservice.server.storage.FilesStorage;
import org.gcube.portlets.user.td.gwtservice.server.trservice.JobClassifierMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.TabularResourceTypeMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.TaskStateMap;
import org.gcube.portlets.user.td.gwtservice.server.trservice.WorkerStateMap;
import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.json.JSONExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.JobS;
import org.gcube.portlets.user.td.gwtservice.shared.task.JobSClassifier;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskS;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskWrapper;
import org.gcube.portlets.user.td.gwtservice.shared.task.ValidationsJobS;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OperationMonitorCreator {
	private static final String SECURITY_EXCEPTION_RIGHTS = "Security exception, you don't have the required rights!";

	private static Logger logger = LoggerFactory.getLogger(OperationMonitorCreator.class);

	// private static SimpleDateFormat sdf = new SimpleDateFormat(
	// "yyyy-MM-dd HH:mm");

	@SuppressWarnings("unused")
	private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

	private HttpServletRequest httpRequest;
	private ServiceCredentials serviceCredentials;
	private TaskWrapper taskWrapper;
	private OperationMonitorSession operationMonitorSession;

	public OperationMonitorCreator(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TaskWrapper taskWrapper, OperationMonitorSession operationMonitorSession) {
		this.httpRequest = httpRequest;
		this.serviceCredentials = serviceCredentials;
		this.taskWrapper = taskWrapper;
		this.operationMonitorSession = operationMonitorSession;

	}

	public OperationMonitor create() throws TDGWTServiceException {
		OperationMonitor operationMonitor = new OperationMonitor(operationMonitorSession.getTaskId(),
				taskWrapper.getOperationId());

		if (taskWrapper == null || taskWrapper.getTask() == null || taskWrapper.getTask().getId() == null
				|| taskWrapper.getTask().getId().getValue() == null
				|| taskWrapper.getTask().getId().getValue().isEmpty()) {
			logger.debug("Task is null");
			throw new TDGWTServiceException("Error in Operation Monitor task is null");
		} else {
			operationMonitor.setTaskId(taskWrapper.getTask().getId().getValue());

			if (operationMonitorSession.isAbort()) {
				Task task = taskWrapper.getTask();
				task.abort();
				TaskS taskS = createTaskS();
				// taskS.setState(State.ABORTED);
				operationMonitor.setTask(taskS);
				// operationMonitor.setAbort(true);
				SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
				postOperation(operationMonitor);
			} else {
				if (operationMonitorSession.isInBackground()) {
					TaskS taskS = createTaskS();
					operationMonitor.setTask(taskS);
					operationMonitor.setInBackground(true);
					SessionUtil.setTaskInBackground(httpRequest, serviceCredentials, taskWrapper);
					SessionUtil.removeStartedTask(httpRequest, serviceCredentials, taskWrapper);
					postOperation(operationMonitor);
				} else {

					TaskStatus status = null;
					try {
						status = taskWrapper.getTask().getStatus();
					} catch (Throwable e) {
						logger.debug("Error retrieving task status: " + taskWrapper.getTask() + "  "
								+ e.getLocalizedMessage());
						e.printStackTrace();
					}
					if (status == null) {
						logger.debug("Services TaskStatus : null");
						throw new TDGWTServiceException("Error in OperationMonitor Status is null");
					} else {
						TaskS taskS = createTaskS();
						operationMonitor.setTask(taskS);
					}
					SessionUtil.setStartedTask(httpRequest, serviceCredentials, taskWrapper);
					postOperation(operationMonitor);
				}
			}

		}

		return operationMonitor;
	}

	protected TaskS createTaskS() throws TDGWTServiceException {
		TaskS taskS = new TaskS();

		ArrayList<JobS> jobSList = new ArrayList<JobS>();
		int i = 1;
		for (Job job : taskWrapper.getTask().getTaskJobs()) {

			ArrayList<ValidationsJobS> validationsJobS = new ArrayList<ValidationsJobS>();
			int j = 1;
			for (ValidationJob valJob : job.getValidationJobs()) {
				ValidationsJobS validationJ = new ValidationsJobS(String.valueOf(j),
						WorkerStateMap.map(valJob.getStatus()), valJob.getProgress(), valJob.getDescription(),
						valJob.getErrorMessage(), valJob.getHumaReadableStatus());
				validationsJobS.add(validationJ);
				j++;
			}

			JobSClassifier jobClassifier = JobClassifierMap.map(job.getJobClassifier());

			JobS jobS = new JobS(String.valueOf(i), job.getProgress(), job.getHumaReadableStatus(), jobClassifier,
					job.getDescription(), WorkerStateMap.map(job.getStatus()), job.getErrorMessage(), validationsJobS);

			jobSList.add(jobS);
			i++;

		}

		try {

			ArrayList<TRId> collateralTRIds = new ArrayList<TRId>();
			TaskResult taskResult = taskWrapper.getTask().getResult();
			if (taskResult != null) {
				List<TableId> collaterals = taskResult.getCollateralTables();
				for (TableId tId : collaterals) {
					String tabulRId = retrieveTabularResourceIdFromTable(tId);
					TRId tabularRId = new TRId(tabulRId);
					tabularRId.setTableId(String.valueOf(tId.getValue()));
					collateralTRIds.add(tabularRId);
				}

			}

			taskS = new TaskS(taskWrapper.getTask().getId().getValue(), taskWrapper.getTask().getProgress(),
					TaskStateMap.map(taskWrapper.getTask().getStatus()), taskWrapper.getTask().getErrorCause(),
					taskWrapper.getTask().getSubmitter(), taskWrapper.getTask().getStartTime(),
					taskWrapper.getTask().getEndTime(), jobSList, collateralTRIds,
					String.valueOf(taskWrapper.getTask().getTabularResourceId().getValue()));
		} catch (Throwable e) {
			logger.error("error retrieving information about the task, " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("error retrieving information about the task, " + e.getLocalizedMessage());
		}
		logger.debug("Retrieved task information");
		return taskS;
	}

	protected void postOperation(OperationMonitor operationMonitor) throws TDGWTServiceException {

		switch (operationMonitor.getTask().getState()) {
		case FAILED:
			Throwable errorCause = taskWrapper.getTask().getErrorCause();
			if (errorCause == null) {
				logger.error("Task Exception: task is failed");

			} else {
				logger.error("Task exception: " + errorCause.getLocalizedMessage());
				errorCause.printStackTrace();
			}
			break;
		case SUCCEDED:
			logger.debug("Task Result:" + taskWrapper.getTask().getResult());
			updateInformations(operationMonitor);
			break;
		case IN_PROGRESS:
			break;
		case VALIDATING_RULES:
			break;
		case GENERATING_VIEW:
			break;
		case ABORTED:
			operationMonitor.setAbort(true);
			break;
		case STOPPED:
			logger.debug("Task Result:" + taskWrapper.getTask().getResult());
			updateInformations(operationMonitor);
			break;
		case INITIALIZING:
			break;
		default:
			break;
		}
	}

	protected void updateInformations(OperationMonitor operationMonitor) throws TDGWTServiceException {
		TRId trId;
		TabResource tabResource;
		Table table;
		// TabExportMetadata trExportMetadata;
		ExportMetadata exportMetadata;

		switch (taskWrapper.getOperationId()) {
		case CSVExport:
			table = taskWrapper.getTask().getResult().getPrimaryTable();
			logger.debug("Table retrived: " + table.toString());
			exportMetadata = table.getMetadata(ExportMetadata.class);
			logger.debug("ExportMetadata: " + exportMetadata);

			operationMonitor.setTrId(SessionUtil.getTRId(httpRequest, serviceCredentials));

			/*
			 * trExportMetadata = new TabExportMetadata();
			 * trExportMetadata.setUrl(exportMetadata.getUri());
			 * trExportMetadata.setDestinationType(exportMetadata
			 * .getDestinationType());
			 * trExportMetadata.setExportDate(sdf.format(exportMetadata
			 * .getExportDate()));
			 */

			saveCSVExportInDestination(exportMetadata);
			break;
		case SDMXExport:
			table = taskWrapper.getTask().getResult().getPrimaryTable();
			logger.debug("Table retrived: " + table.toString());
			// exportMetadata = table.getMetadata(ExportMetadata.class);
			// logger.debug("ExportMetadata: " + exportMetadata);
			operationMonitor.setTrId(SessionUtil.getTRId(httpRequest, serviceCredentials));
			break;
		case JSONExport:
			table = taskWrapper.getTask().getResult().getPrimaryTable();
			logger.debug("Table retrived: " + table.toString());
			exportMetadata = table.getMetadata(ExportMetadata.class);
			logger.debug("ExportMetadata: " + exportMetadata);

			operationMonitor.setTrId(SessionUtil.getTRId(httpRequest, serviceCredentials));

			/*
			 * trExportMetadata = new TabExportMetadata();
			 * trExportMetadata.setUrl(exportMetadata.getUri());
			 * trExportMetadata.setDestinationType(exportMetadata
			 * .getDestinationType());
			 * trExportMetadata.setExportDate(sdf.format(exportMetadata
			 * .getExportDate()));
			 */

			saveJSONExportInDestination(exportMetadata);
			break;
		case CSVImport:
			trId = new TRId();
			trId.setId(taskWrapper.getTrId().getId());
			trId = retrieveTabularResourceBasicData(trId);

			operationMonitor.setTrId(trId);
			tabResource = SessionUtil.getCSVImportTabResource(httpRequest, serviceCredentials);
			if (tabResource != null) {
				tabResource.setTrId(trId);
				SessionUtil.setTabResource(httpRequest, serviceCredentials, tabResource);
			} else {
				tabResource = new TabResource();
				tabResource.setTrId(trId);
				SessionUtil.setTabResource(httpRequest, serviceCredentials, tabResource);
			}
			logger.debug("CurrentTabResource set:" + tabResource);
			SessionUtil.setTRId(httpRequest, serviceCredentials, trId);
			SessionUtil.addToCurrentTabularResourcesOpen(httpRequest, serviceCredentials, tabResource);
			break;
		case SDMXImport:
			trId = new TRId();
			trId.setId(taskWrapper.getTrId().getId());
			trId = retrieveTabularResourceBasicData(trId);

			operationMonitor.setTrId(trId);
			tabResource = SessionUtil.getSDMXImportTabResource(httpRequest, serviceCredentials);
			if (tabResource != null) {
				tabResource.setTrId(trId);
				SessionUtil.setTabResource(httpRequest, serviceCredentials, tabResource);
			} else {
				tabResource = new TabResource();
				tabResource.setTrId(trId);
				SessionUtil.setTabResource(httpRequest, serviceCredentials, tabResource);
			}
			logger.debug("CurrentTabResource set:" + tabResource);
			SessionUtil.setTRId(httpRequest, serviceCredentials, trId);
			SessionUtil.addToCurrentTabularResourcesOpen(httpRequest, serviceCredentials, tabResource);
			break;
		case Clone:
			trId = new TRId();
			trId.setId(taskWrapper.getTrId().getId());
			trId = retrieveTabularResourceBasicData(trId);
			operationMonitor.setTrId(trId);
			break;
		default:
			trId = new TRId(taskWrapper.getTrId().getId());
			trId = retrieveTabularResourceBasicData(trId);
			operationMonitor.setTrId(trId);
			tabResource = SessionUtil.getTabResource(httpRequest, serviceCredentials);
			if (tabResource != null) {
				tabResource.setTrId(trId);
				SessionUtil.setTabResource(httpRequest, serviceCredentials, tabResource);
			} else {
				tabResource = new TabResource();
				tabResource.setTrId(trId);
				SessionUtil.setTabResource(httpRequest, serviceCredentials, tabResource);
			}
			logger.debug("CurrentTabResource set:" + tabResource);
			SessionUtil.setTRId(httpRequest, serviceCredentials, trId);
			break;

		}

	}

	protected TRId retrieveTabularResourceBasicData(TRId trId) throws TDGWTServiceException {
		try {

			TabularDataService service = TabularDataServiceFactory.getService();
			TabularResourceId tabularResourceId = new TabularResourceId(new Long(trId.getId()));

			TabularResource tr = service.getTabularResource(tabularResourceId);
			if (tr == null) {
				logger.error("Tabular Resource Null [id=" + trId.getId() + "]");
				throw new TDGWTServiceException("Tabular Resource Null " + trId);
			}
			// logger.debug("Retrieve Tabular Resource Basic Data: "+tr);

			Table table = service.getLastTable(tabularResourceId);
			if (table == null) {
				logger.error("Last Table Null");
				throw new TDGWTServiceException("Last Table Null for " + tr);
			}

			Table viewTable = null;

			if (table.contains(DatasetViewTableMetadata.class)) {
				DatasetViewTableMetadata dwm = table.getMetadata(DatasetViewTableMetadata.class);
				try {
					viewTable = service.getTable(dwm.getTargetDatasetViewTableId());
				} catch (Exception e) {
					logger.error("view table not found");
				}
			}

			TRId newTRId;
			if (viewTable == null) {
				newTRId = new TRId(String.valueOf(tr.getId().getValue()),
						TabularResourceTypeMap.map(tr.getTabularResourceType()), tr.getTableType(),
						String.valueOf(table.getId().getValue()), table.getTableType().getName());

			} else {
				newTRId = new TRId(String.valueOf(tr.getId().getValue()),
						TabularResourceTypeMap.map(tr.getTabularResourceType()), tr.getTableType(),
						String.valueOf(viewTable.getId().getValue()), viewTable.getTableType().getName(),
						String.valueOf(table.getId().getValue()), true);

			}

			logger.debug("Retrieved TRId basic info:" + newTRId.toString());
			return newTRId;

		} catch (TDGWTSessionExpiredException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new TDGWTServiceException(SECURITY_EXCEPTION_RIGHTS);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TDGWTServiceException("Error Retrieving Basic Data: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Save export csv data on Workspace
	 * 
	 * 
	 * @param exportMetadata
	 *            Export Metadata
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	protected void saveCSVExportInDestination(ExportMetadata exportMetadata) throws TDGWTServiceException {
		CSVExportSession exportSession = SessionUtil.getCSVExportSession(httpRequest, serviceCredentials);
		String user = serviceCredentials.getUserName();

		logger.debug("Save Export In Destination");
		logger.debug("Destination: " + exportSession.getDestination().getId());

		if (exportSession.getDestination().getId().compareTo("Workspace") == 0) {
			logger.debug("Save on Workspace");
			boolean end = SessionUtil.getCSVExportEnd(httpRequest, serviceCredentials);
			if (end == false) {
				SessionUtil.setCSVExportEnd(httpRequest, serviceCredentials, true);

				logger.debug("Create Item On Workspace: [ storageId: " + exportMetadata.getUri() + " ,user: " + user
						+ " ,fileName: " + exportSession.getFileName() + " ,fileDescription: "
						+ exportSession.getFileDescription() + " ,mimetype: text/csv" + " ,folder: "
						+ exportSession.getItemId() + "]");

				FilesStorage storage = new FilesStorage();
				storage.createItemOnWorkspaceByStorageId(exportMetadata.getUri(), user,
						exportSession.getFileName() + ".csv", exportSession.getFileDescription(), "text/csv",
						exportSession.getItemId());

			} else {
				logger.debug("getCSVExportEnd(): true");
			}
		} else {
			logger.error("Destination No Present");
			throw new TDGWTServiceException("Error in export csv: no destination present");
		}
	}

	/**
	 * Save export json data on Workspace
	 * 
	 * 
	 * @param exportMetadata
	 *            Export Metadata
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	protected void saveJSONExportInDestination(ExportMetadata exportMetadata) throws TDGWTServiceException {
		JSONExportSession exportSession = SessionUtil.getJSONExportSession(httpRequest, serviceCredentials);
		String user = serviceCredentials.getUserName();

		logger.debug("Save Export In Destination");
		logger.debug("Destination: " + exportSession.getDestination().getId());

		if (exportSession.getDestination().getId().compareTo("Workspace") == 0) {
			logger.debug("Save on Workspace");
			boolean end = SessionUtil.getJSONExportEnd(httpRequest, serviceCredentials);
			if (end == false) {
				SessionUtil.setJSONExportEnd(httpRequest, serviceCredentials, true);

				logger.debug("Create Item On Workspace: [ storageId: " + exportMetadata.getUri() + " ,user: " + user
						+ " ,fileName: " + exportSession.getFileName() + " ,fileDescription: "
						+ exportSession.getFileDescription() + " ,mimetype: application/json" + " ,folder: "
						+ exportSession.getItemId() + "]");

				FilesStorage storage = new FilesStorage();

				storage.createItemOnWorkspaceByStorageId(exportMetadata.getUri(), user,
						exportSession.getFileName() + ".json", exportSession.getFileDescription(), "application/json",
						exportSession.getItemId());

			} else {
				logger.debug("getJSONExportEnd(): true");
			}
		} else {
			logger.error("Destination No Present");
			throw new TDGWTServiceException("Error in export json: no destination present");
		}
	}

	private String retrieveTabularResourceIdFromTable(TableId tableId) throws TDGWTServiceException {
		try {
			TabularDataService service = TabularDataServiceFactory.getService();

			Table table = service.getTable(tableId);

			if (table.contains(TableDescriptorMetadata.class)) {
				TableDescriptorMetadata tdm = table.getMetadata(TableDescriptorMetadata.class);
				return String.valueOf(tdm.getRefId());
			} else {
				throw new TDGWTServiceException("No TableDescriptorMetadata present in tableId: " + tableId);
			}

		} catch (Exception e) {
			throw new TDGWTServiceException(e.getLocalizedMessage());
		}

	}

}
