package org.gcube.portlets.widgets.workspaceuploader.client;

import org.gcube.portlets.widgets.workspaceuploader.client.uploader.DialogUpload.UPLOAD_TYPE;
import org.gcube.portal.clientcontext.client.GCubeClientContext;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WorkspaceUploader implements EntryPoint {

	String headerTitle = "Upload in your Workspace";
	String parentId;
	private Button buttonUploader;
	private Button buttonDirect;

	public void onModuleLoad() {

		boolean jQueryLoaded = isjQueryLoaded();
		GWT.log("jQueryLoaded: "+jQueryLoaded);

		GWT.log("Injected : http://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js");

		if (!jQueryLoaded) {
			ScriptInjector.fromUrl("http://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js")
			.setWindow(ScriptInjector.TOP_WINDOW)
			.inject();
		}

//			ScriptInjector.fromUrl("http://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js")
//			.setWindow(ScriptInjector.TOP_WINDOW)
//			.inject();

		buttonUploader = new Button("Upload in your Workspace");
		buttonDirect = new Button("Upload (Stream) in your Workspace");
		enableUpload(false);

		WorkspaceUploaderServiceAsync.Util.getInstance().getWorkspaceId(
				new AsyncCallback<String>() {

					@Override
					public void onSuccess(String result) {

						if (result != null) {
							parentId = result;
							enableUpload(true);
						} else {
							Window.alert("An error occurred on recovering workspace, try again later");
							enableUpload(true);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("An error occurred on recovering workspace, try again later");
						enableUpload(true);
					}
				});

		buttonDirect.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				MultipleDilaogUpload uploadStream = new MultipleDilaogUpload(headerTitle, parentId, org.gcube.portlets.widgets.workspaceuploader.client.uploader.DialogUpload.UPLOAD_TYPE.File);
//				uploadStream.initFileReader();
				uploadStream.center();

			}
		});

		final MultipleDNDUpload dnd = new MultipleDNDUpload(parentId, UPLOAD_TYPE.File);

		buttonDirect.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
//				MultipleDilaogUpload uploadStream = new MultipleDilaogUpload(headerTitle, parentId, org.gcube.portlets.widgets.workspaceuploader.client.uploader.DialogUpload.UPLOAD_TYPE.File);
//				uploadStream.initFileReader();
//				uploadStream.center();
				dnd.setParameters(parentId, UPLOAD_TYPE.File);

			}
		});

		RootPanel.get("workspace-uploader").add(dnd);
		RootPanel.get("workspace-uploader").add(buttonDirect);
	}

	private void enableUpload(boolean bool){
		buttonUploader.setEnabled(bool);
		buttonDirect.setEnabled(bool);
	}

	/**
	 * Checks if is j query loaded.
	 *
	 * @return true, if is j query loaded
	 */
	 private native boolean isjQueryLoaded() /*-{
		return (typeof $wnd['jQuery'] !== 'undefined');
	}-*/;
}
