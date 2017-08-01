package org.gcube.portlets.widgets.fileupload.client.controller;

import java.util.List;

import org.gcube.portlets.widgets.fileupload.client.UploadProgressService;
import org.gcube.portlets.widgets.fileupload.client.UploadProgressServiceAsync;
import org.gcube.portlets.widgets.fileupload.client.events.FileUploadCompleteEvent;
import org.gcube.portlets.widgets.fileupload.client.state.UploadProgressState;
import org.gcube.portlets.widgets.fileupload.shared.event.Event;
import org.gcube.portlets.widgets.fileupload.shared.event.UploadProgressChangeEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;


public final class ProgressController {
	
	protected static final UploadProgressServiceAsync SERVICE = GWT.create(UploadProgressService.class);
	
	private static ProgressController singleton;
	
	private static HandlerManager eventBus;

	public static ProgressController start(HandlerManager handlerManager) {
		eventBus = handlerManager;
		if (singleton == null) {
			singleton = new ProgressController();
			singleton.initialize(handlerManager);
		}
		return singleton;
	}
	
	private ProgressController() {  }

	private void getEvents() {

		SERVICE.getEvents(new AsyncCallback<List<Event>>() {

			@Override
			public void onFailure(final Throwable t) {
				GWT.log("error get events", t);
			}

			@Override
			public void onSuccess(final List<Event> events) {

				for (Event event : events) {
					handleEvent(event);
				}
				SERVICE.getEvents(this);
			}

			private void handleEvent(final Event event) {

				if (event instanceof UploadProgressChangeEvent) {
					UploadProgressChangeEvent uploadPercentChangeEvent = (UploadProgressChangeEvent) event;
					String fileName = uploadPercentChangeEvent.getFilename();
					Integer percentage = uploadPercentChangeEvent.getPercentage();

					UploadProgressState.INSTANCE.setUploadProgress(fileName, percentage);
					if (percentage == 100) {
						uploadCompleted(uploadPercentChangeEvent);
					}
				}
			}
		});
	}

	private void initialize(HandlerManager eventBus) {
		
		SERVICE.initialise(new AsyncCallback<Void>() {

			@Override
			public void onFailure(final Throwable t) {
				GWT.log("error initialise", t);
			}

			@Override
			public void onSuccess(final Void result) {
				getEvents();
			}
		});
	}
		
	/**
	 * here we fire the finished uploadedFile event
	 * @param uploadedFile
	 */
	private void uploadCompleted(UploadProgressChangeEvent uploadedFile) {
		GWT.log("Finito");
		eventBus.fireEvent(new FileUploadCompleteEvent(uploadedFile));
	}
}
