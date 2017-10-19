package org.gcube.portlets.user.td.monitorwidget.client.details.tree;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.task.JobS;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskS;
import org.gcube.portlets.user.td.gwtservice.shared.task.ValidationsJobS;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class MonitorTreeDataGenerator {
	private DateTimeFormat sdf = DateTimeFormat
			.getFormat("yyyy-MM-dd HH:mm:ss");

	private MonitorFolderDto root;
	private TaskS taskSCache;

	public MonitorTreeDataGenerator() {
		Log.debug("MonitorTreeDataGenerator");
	}

	public boolean isCacheValid(OperationMonitor operationMonitor) {
		if (operationMonitor == null || operationMonitor.getTask() == null) {
			taskSCache = null;
			return false;
		}

		TaskS taskS = operationMonitor.getTask();
		if (taskSCache == null) {
			taskSCache = taskS;
			return false;
		}

		if (taskS.getId().compareTo(taskSCache.getId()) == 0) {
			if (taskS.getProgress() == taskSCache.getProgress()
					&& taskS.getState().compareTo(taskSCache.getState()) == 0) {
				ArrayList<JobS> listJobS = taskS.getJobs();
				ArrayList<JobS> listJobSCache = taskSCache.getJobs();
				if (listJobS.size() == listJobSCache.size()) {
					for (int i = 0; i < listJobS.size(); i++) {
						JobS jobS = listJobS.get(i);
						JobS jobSCache = listJobSCache.get(i);
						if (jobS.getId() == jobSCache.getId()
								&& jobS.getProgress() == jobSCache
										.getProgress()
								&& jobS.getWorkerState().compareTo(
										jobSCache.getWorkerState()) == 0) {
							ArrayList<ValidationsJobS> listValidationsJobS = jobS
									.getValidationsJobS();
							ArrayList<ValidationsJobS> listValidationsJobSCache = jobSCache
									.getValidationsJobS();
							if (listValidationsJobS.size() == listValidationsJobSCache
									.size()) {
								for (int j = 0; j < listValidationsJobS.size(); j++) {
									ValidationsJobS validationsJobS = listValidationsJobS
											.get(j);
									ValidationsJobS validationsJobSCache = listValidationsJobSCache
											.get(j);

									if (validationsJobS.getId() == validationsJobSCache
											.getId()
											&& validationsJobS.getProgress() == validationsJobSCache
													.getProgress()
											&& validationsJobS
													.getWorkerState()
													.compareTo(
															validationsJobSCache
																	.getWorkerState()) == 0) {

									} else {
										taskSCache = taskS;
										return false;
									}
								}
							} else {
								taskSCache = taskS;
								return false;
							}
						} else {
							taskSCache = taskS;
							return false;
						}
					}
				} else {
					taskSCache = taskS;
					return false;
				}

			} else {
				taskSCache = taskS;
				return false;
			}
		} else {
			taskSCache = taskS;
			return false;
		}

		return true;

	}

	public MonitorFolderDto getRoot(OperationMonitor operationMonitor) {
		root = null;

		try {

			ArrayList<MonitorBaseDto> childrens = new ArrayList<MonitorBaseDto>();

			if (operationMonitor != null && operationMonitor.getTask() != null) {
				TaskS task = operationMonitor.getTask();
				ArrayList<MonitorBaseDto> jobs = new ArrayList<MonitorBaseDto>();
				for (JobS job : task.getJobs()) {
					ArrayList<MonitorBaseDto> validations = new ArrayList<MonitorBaseDto>();

					for (ValidationsJobS v : job.getValidationsJobS()) {
						MonitorValidationJobSDto validationDto = new MonitorValidationJobSDto(
								task.getId() + "-" + job.getId() + "-"
										+ v.getId(), v.getWorkerState(),
								v.getProgress(), v.getDescription(),
								v.getErrorMessage(),
								v.getHumanReadableStatus(), job.getInvocation());

						validations.add(validationDto);
					}
					MonitorJobSDto foldJob = new MonitorJobSDto("job",
							task.getId() + "-" + job.getId(),
							job.getJobClassifier(), job.getDescription(), job
									.getWorkerState().toString(),
							job.getHumaReadableStatus(), job.getProgress(),
							validations);
					jobs.add(foldJob);
				}
				MonitorTaskSDto foldTask = new MonitorTaskSDto("task",
						task.getId(), operationMonitor.getOperationId()
								.toString(), task.getState().toString(), "",
						task.getProgress(), jobs);
				if (task.getStartTime() != null) {
					foldTask.setStartTime(sdf.format(task.getStartTime()));
				}
				if (task.getEndTime() != null) {
					foldTask.setEndTime(sdf.format(task.getEndTime()));
				}

				childrens.add(foldTask);
				root = new MonitorFolderDto("root", "-1", "root", "root", "",
						0, childrens);
				Log.debug("Generated root");
				// printRecorsive(root);

			} else {
				root = new MonitorFolderDto("root", "-1", "root", "root", "",
						0, childrens);
				Log.debug("Generated root without childrens");
			}

		} catch (Throwable e) {
			Log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

		return root;
	}

	public MonitorFolderDto getRootNoTask(OperationMonitor operationMonitor) {
		root = null;

		try {

			ArrayList<MonitorBaseDto> jobs = new ArrayList<MonitorBaseDto>();
			if (operationMonitor != null && operationMonitor.getTask() != null) {
				TaskS task = operationMonitor.getTask();

				for (JobS job : task.getJobs()) {
					ArrayList<MonitorBaseDto> validations = new ArrayList<MonitorBaseDto>();
					for (ValidationsJobS v : job.getValidationsJobS()) {
						MonitorValidationJobSDto validationDto = new MonitorValidationJobSDto(
								task.getId() + "-" + job.getId() + "-"
										+ v.getId(), v.getWorkerState(),
								v.getProgress(), v.getDescription(),
								v.getErrorMessage(),
								v.getHumanReadableStatus(), job.getInvocation());

						validations.add(validationDto);
					}
					MonitorJobSDto foldJob = new MonitorJobSDto("job",
							task.getId() + "-" + job.getId(),
							job.getJobClassifier(), job.getDescription(), job
									.getWorkerState().toString(),
							job.getHumaReadableStatus(), job.getProgress(),
							validations);
					jobs.add(foldJob);
				}

				root = new MonitorFolderDto("root", "-1", "root", "root", "",
						0, jobs);
				Log.debug("Generated root");
				// printRecorsive(root);

			} else {
				root = new MonitorFolderDto("root", "-1", "root", "root", "",
						0, jobs);
				Log.debug("Generated root without childrens");
			}

		} catch (Throwable e) {
			Log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

		return root;
	}

	public MonitorFolderDto getRoot(
			ArrayList<OperationMonitor> operationMonitorList) {
		root = null;

		try {
			ArrayList<MonitorBaseDto> childrens = new ArrayList<MonitorBaseDto>();
			if (operationMonitorList != null && operationMonitorList.size() > 0) {
				Log.debug("getRoot: " + operationMonitorList.size());
				for (OperationMonitor operationMonitor : operationMonitorList) {
					if (operationMonitor != null
							&& operationMonitor.getTask() != null) {
						Log.debug("getRoot: [TaskId="
								+ operationMonitor.getTaskId() + "]");
						TaskS task = operationMonitor.getTask();
						ArrayList<MonitorBaseDto> jobs = new ArrayList<MonitorBaseDto>();
						for (JobS job : task.getJobs()) {
							ArrayList<MonitorBaseDto> validations = new ArrayList<MonitorBaseDto>();

							for (ValidationsJobS v : job.getValidationsJobS()) {
								MonitorValidationJobSDto validationDto = new MonitorValidationJobSDto(
										task.getId() + "-" + job.getId() + "-"
												+ v.getId(),
										v.getWorkerState(), v.getProgress(),
										v.getDescription(),
										v.getErrorMessage(),
										v.getHumanReadableStatus(),
										job.getInvocation());

								validations.add(validationDto);
							}
							MonitorJobSDto foldJob = new MonitorJobSDto("job",
									task.getId() + "-" + job.getId(),
									job.getJobClassifier(),
									job.getDescription(), job.getWorkerState()
											.toString(),
									job.getHumaReadableStatus(),
									job.getProgress(), validations);
							jobs.add(foldJob);
						}
						MonitorTaskSDto foldTask = new MonitorTaskSDto("task",
								task.getId(), operationMonitor.getOperationId()
										.toString(),
								task.getState().toString(), "",
								task.getProgress(), jobs);
						if (task.getStartTime() != null) {
							foldTask.setStartTime(sdf.format(task
									.getStartTime()));
						}
						if (task.getEndTime() != null) {
							foldTask.setEndTime(sdf.format(task.getEndTime()));
						}
						childrens.add(foldTask);
					} else {
						Log.debug("getRoot: operation null");
					}
				}
				Log.debug("childrens size:" + childrens.size());
				root = new MonitorFolderDto("root", "-1", "root", "root", "",
						0, childrens);
				Log.debug("Generated root");

			} else {
				root = new MonitorFolderDto("root", "-1", "root", "root", "",
						0, childrens);
				Log.debug("Generated root without childrens");
			}

		} catch (Throwable e) {
			Log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

		return root;

	}

	protected void printRecorsive(MonitorFolderDto root) {
		for (MonitorBaseDto base : root.getChildrens()) {
			Log.debug("+++");
			Log.debug("Children:[id=" + base.getId() + ", toString="
					+ base.toString() + "]");
			if (base instanceof MonitorFolderDto) {
				printRecorsive((MonitorFolderDto) base);
			}
			Log.debug("---");

		}
	}

}
