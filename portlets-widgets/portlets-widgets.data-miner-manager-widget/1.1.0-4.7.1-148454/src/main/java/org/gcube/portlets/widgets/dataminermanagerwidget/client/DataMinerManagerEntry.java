package org.gcube.portlets.widgets.dataminermanagerwidget.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DataMinerManagerEntry implements EntryPoint {

	/**
	 * {@inheritDoc}
	 */
	public void onModuleLoad() {

		/*
		 * Install an UncaughtExceptionHandler which will produce
		 * <code>FATAL</code> log messages
		 */
		Log.setUncaughtExceptionHandler();

		// use deferred command to catch initialization exceptions in
		// onModuleLoad
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				// loadScope();
				loadMainPanel();
			}
		});

	}

	private void loadMainPanel() {
		DataMinerManagerDialog dataMinerManagerDialog = new DataMinerManagerDialog();
		dataMinerManagerDialog.show();

	}
}
