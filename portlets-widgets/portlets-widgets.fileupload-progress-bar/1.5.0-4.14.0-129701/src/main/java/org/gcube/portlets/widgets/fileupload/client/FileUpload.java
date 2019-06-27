package org.gcube.portlets.widgets.fileupload.client;

import org.gcube.portlets.widgets.fileupload.client.events.FileUploadCompleteEvent;
import org.gcube.portlets.widgets.fileupload.client.events.FileUploadCompleteEventHandler;
import org.gcube.portlets.widgets.fileupload.client.events.FileUploadSelectedEvent;
import org.gcube.portlets.widgets.fileupload.client.events.FileUploadSelectedEventHandler;
import org.gcube.portlets.widgets.fileupload.client.view.UploadProgressDialog;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * 
 * Use this widget to display a dialog containing the possibility to upload a file on server with an actual progress of the current uploaded file in percentage;
 * uncomment //showSample() in the onModuleLoad() to see it working
 * 
 * To get to know which file was uploaded listen for the {@link FileUploadCompleteEvent} on the {@link HandlerManager} instance you pass to this widget.
 *
 */
public class FileUpload implements EntryPoint {

	@Override
	public void onModuleLoad() {	
//				Button showDlg = new Button("Open Dialog");
//				showDlg.addClickHandler(new ClickHandler() {			
//					@Override
//					public void onClick(ClickEvent event) {
//						showSample();				
//					}
//				});
//				RootPanel.get().add(showDlg);	
	}

	private void showSample() {
		HandlerManager eventBus = new HandlerManager(null);

		final UploadProgressDialog dlg = new UploadProgressDialog("Share File",  eventBus, true);
		dlg.center();
		dlg.show();

		eventBus.addHandler(FileUploadSelectedEvent.TYPE, new FileUploadSelectedEventHandler() {

			@Override
			public void onFileSelected(FileUploadSelectedEvent event) {
				String fileName = event.getSelectedFileName();
				GWT.log("selected file name: " + fileName);
				//pretend you do sth with the uploaded file, wait 3 seconds
				Timer t = new Timer() {

					@Override
					public void run() {
						try {
							dlg.submitForm();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				t.schedule(1000);
			}
		});


		/**
		 * get the uploaded file result
		 */
		eventBus.addHandler(FileUploadCompleteEvent.TYPE, new FileUploadCompleteEventHandler() {

			@Override
			public void onUploadComplete(FileUploadCompleteEvent event) {

				//the filename and its path on server are returned to the client
				String fileName = event.getUploadedFileInfo().getFilename();
				String absolutePathOnServer = event.getUploadedFileInfo().getAbsolutePath();
				GWT.log(fileName + " uploaded on Server here: " + absolutePathOnServer);
				//pretend you do sth with the uploaded file, wait 3 seconds
				Timer t = new Timer() {

					@Override
					public void run() {
						dlg.showRegisteringResult(true); //or false if an error occurred						
					}
				};
				t.schedule(3000);
			}
		});

	}
}
