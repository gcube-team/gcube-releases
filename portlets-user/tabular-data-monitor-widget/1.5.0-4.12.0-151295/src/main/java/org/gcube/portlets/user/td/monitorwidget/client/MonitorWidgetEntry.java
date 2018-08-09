package org.gcube.portlets.user.td.monitorwidget.client;

import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.user.td.gwtservice.shared.monitor.BackgroundOperationMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.task.JobS;
import org.gcube.portlets.user.td.gwtservice.shared.task.JobSClassifier;
import org.gcube.portlets.user.td.gwtservice.shared.task.State;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskS;
import org.gcube.portlets.user.td.gwtservice.shared.task.ValidationsJobS;
import org.gcube.portlets.user.td.gwtservice.shared.task.WorkerState;
import org.gcube.portlets.user.td.monitorwidget.client.background.MonitorBackgroundDialog;
import org.gcube.portlets.user.td.monitorwidget.client.background.MonitorBackgroundInfoDialog;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.UIOperationsId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class MonitorWidgetEntry implements EntryPoint {

	private MonitorDialog monitorWidget;
	private MonitorBackgroundDialog monitorBackgroundWidget;
	private MonitorBackgroundInfoDialog monitorBackgroundInfoWidget;
	private SimpleEventBus eventBus;

	protected enum TestShow {
		MONITORWIDGET, MONITORBACKGROUNDWIDGET, MONITORBACKGROUNDINFOWIDGET
	};

	public void onModuleLoad() {
		TestShow testShow = TestShow.MONITORBACKGROUNDINFOWIDGET;
		Timer elapsedTimer;

		switch (testShow) {
		case MONITORWIDGET:
			Log.debug("Test MonitorWidget");
			eventBus = new SimpleEventBus();
			monitorWidget = new MonitorDialog("1", eventBus);
			monitorWidget.show();

			elapsedTimer = new Timer() {
				public void run() {
					showElapsed();
				}
			};

			Log.debug("Start Time:" + System.currentTimeMillis());

			// Schedule the timer for every 1/2 second (500 milliseconds)
			elapsedTimer.scheduleRepeating(3000);
			break;
		case MONITORBACKGROUNDWIDGET:
			Log.debug("Test MonitorBackgroundWidget");
			eventBus = new SimpleEventBus();
			monitorBackgroundWidget = new MonitorBackgroundDialog(eventBus);
			monitorBackgroundWidget.show();

			elapsedTimer = new Timer() {
				public void run() {
					showBackgroundElapsed();
				}
			};

			Log.debug("Start Time:" + System.currentTimeMillis());

			// Schedule the timer for every 1/2 second (500 milliseconds)
			elapsedTimer.scheduleRepeating(3000);
			break;
		case MONITORBACKGROUNDINFOWIDGET:
			eventBus = new SimpleEventBus();
			showBackgroundInfoWidget();
			break;

		}
	}

	private void showElapsed() {
		OperationMonitor operationMonitor = new OperationMonitor("1",
				UIOperationsId.EditRow);
		operationMonitor.setTask(createTaskForTest("1"));
		monitorWidget.updateOperationMonitor(operationMonitor);
	}

	private void showBackgroundElapsed() {
		ArrayList<BackgroundOperationMonitor> backgroundOperationMonitorList = new ArrayList<BackgroundOperationMonitor>();

		for (int i = 0; i < 20; i++) {

			BackgroundOperationMonitor backgroundOperationMonitor=new 
					BackgroundOperationMonitor();
			backgroundOperationMonitor.setTaskId(String.valueOf(i));
			backgroundOperationMonitor.setOperationId(UIOperationsId.EditRow);
			backgroundOperationMonitor.setTabularResourceId("1");
			backgroundOperationMonitor.setTabularResourceName("TestTR");
			backgroundOperationMonitor.setState(State.IN_PROGRESS);
			backgroundOperationMonitor.setProgress(genProgress());

			backgroundOperationMonitorList.add(backgroundOperationMonitor);
		}

		monitorBackgroundWidget
				.updateBackgroundOperationMonitor(backgroundOperationMonitorList);
	}

	private void showBackgroundInfoWidget() {
		BackgroundOperationMonitor backgroundOperationMonitor=new 
				BackgroundOperationMonitor();
		backgroundOperationMonitor.setTaskId("1");
		backgroundOperationMonitor.setOperationId(UIOperationsId.EditRow);
		backgroundOperationMonitor.setTabularResourceId("1");
		backgroundOperationMonitor.setTabularResourceName("TestTR");
		backgroundOperationMonitor.setState(State.IN_PROGRESS);
		backgroundOperationMonitor.setProgress(genProgress());
		monitorBackgroundInfoWidget = new MonitorBackgroundInfoDialog(
				backgroundOperationMonitor, eventBus);
		monitorBackgroundInfoWidget.show();
	}

	private TaskS createTaskForTest(String taskId) {

		ArrayList<ValidationsJobS> validationsJobs = new ArrayList<ValidationsJobS>();
		ValidationsJobS validationJobS = new ValidationsJobS("1",
				WorkerState.IN_PROGRESS, genProgress(), "Row Validate", null,
				"Validation in progress");
		validationsJobs.add(validationJobS);

		ArrayList<JobS> jobs = new ArrayList<JobS>();
		JobS job1 = new JobS("1", genProgress(), "Edit Row Job for Human",JobSClassifier.PROCESSING,
				"Edit Row Job", WorkerState.IN_PROGRESS, null, validationsJobs);
		jobs.add(job1);

		JobS job2 = new JobS("2", genProgress(), "Add Row Job for Human",JobSClassifier.PROCESSING,
				"Add Row Job", WorkerState.IN_PROGRESS, null, validationsJobs);
		jobs.add(job2);

		JobS job3 = new JobS("3", genProgress(), "Delete Job for Human",JobSClassifier.PROCESSING,
				"Delete Row Job", WorkerState.IN_PROGRESS, null,
				validationsJobs);
		jobs.add(job3);

		JobS job4 = new JobS("4", genProgress(), "Edit Row Job for Human",JobSClassifier.PROCESSING,
				"Edit Row Job", WorkerState.IN_PROGRESS, null, validationsJobs);
		jobs.add(job4);

		JobS job5 = new JobS("5", genProgress(), "Add Row Job for Human",JobSClassifier.PROCESSING,
				"Add Row Job", WorkerState.IN_PROGRESS, null, validationsJobs);
		jobs.add(job5);

		JobS job6 = new JobS("6", genProgress(), "Delete Job for Human",JobSClassifier.PROCESSING,
				"Delete Row Job", WorkerState.IN_PROGRESS, null,
				validationsJobs);
		jobs.add(job6);

		TaskS task = new TaskS(taskId, genProgress(), State.IN_PROGRESS, null,
				"default", new Date(), null, jobs, null, null);

		return task;

	}

	private float genProgress() {
		
		float fperc=20;
	
		int perc = Random.nextInt(100);
		if (perc == 0) {
			fperc = 0f;
		} else {
			fperc = new Float(perc) / 100;
		}
		// Log.debug("ProgressSet: " + fperc);
		return fperc;
	}

}
