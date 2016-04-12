/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.client.uploader;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.widgets.workspaceuploader.client.ClosableDialog;
import org.gcube.portlets.widgets.workspaceuploader.client.ConstantsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploaderListenerController;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploaderServiceAsync;
import org.gcube.portlets.widgets.workspaceuploader.client.events.CancelUploadEvent;
import org.gcube.portlets.widgets.workspaceuploader.client.events.CancelUploadEventHandler;
import org.gcube.portlets.widgets.workspaceuploader.client.events.HideMonitorEvent;
import org.gcube.portlets.widgets.workspaceuploader.client.events.HideMonitorEventHandler;
import org.gcube.portlets.widgets.workspaceuploader.client.events.NotifyUploadEvent;
import org.gcube.portlets.widgets.workspaceuploader.client.events.NotifyUploadEvent.UPLOAD_EVENT_TYPE;
import org.gcube.portlets.widgets.workspaceuploader.client.resource.WorkspaceUploaderResources;
import org.gcube.portlets.widgets.workspaceuploader.shared.HandlerResultMessage;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem.UPLOAD_STATUS;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The Class UploaderMonitor.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 7, 2015
 */
public class UploaderMonitor {
	
	private static UploaderMonitor instance;
	private final static HandlerManager monitorEventBus = new HandlerManager(null);
	private MonitorPanel monitorPanel = new MonitorPanel(monitorEventBus);
	private static Map<String, Timer> mapTimer = new HashMap<String, Timer>();
	private ClosableDialog dialogUploadMonitor = new ClosableDialog(monitorPanel, false, ConstantsWorkspaceUploader.MY_UPLOADS);
	
	/**
	 * Gets the single instance of UploaderMonitor.
	 *
	 * @return single instance of UploaderMonitor
	 */
	public static synchronized UploaderMonitor getInstance() {
		if (instance == null){
			instance = new UploaderMonitor();
		}
		return instance;
	}
	
	private UploaderMonitor(){
		dialogUploadMonitor.setWidth("400px");
		bindEvents();
	}

	/**
	 * Bind events.
	 */
	private void bindEvents() {
		
		monitorEventBus.addHandler(CancelUploadEvent.TYPE, new CancelUploadEventHandler() {
			
			@Override
			public void onCancelUpload(final CancelUploadEvent deleteTimerEvent) {

				if(deleteTimerEvent.getUploader()==null)
					return;
				
				String clientKey = deleteTimerEvent.getUploader().getClientUploadKey();
				Timer timer = mapTimer.get(clientKey);
				GWT.log("Delete timer "+timer);
				if(timer!=null && timer.isRunning()){
					timer.cancel();
					mapTimer.put(clientKey, null);
				}
				
				RequestBuilder request = new RequestBuilder(RequestBuilder.GET, 
						ConstantsWorkspaceUploader.WORKSPACE_UPLOADER_SERVLET_STREAM+"?"+ConstantsWorkspaceUploader.CLIENT_UPLOAD_KEYS+"="+clientKey+"&"+ConstantsWorkspaceUploader.CANCEL_UPLOAD+"=true");
				try {
					request.sendRequest(null, new RequestCallback() {
						
						@SuppressWarnings("incomplete-switch")
						@Override
						public void onResponseReceived(Request request, Response response) {
							String strippedResult = new HTML(response.getText()).getText();
							final HandlerResultMessage resultMessage = HandlerResultMessage.parseResult(strippedResult);

							switch (resultMessage.getStatus()) {
							case ERROR: {
								GWT.log("ERROR: Error during stop upload "+resultMessage.getMessage());
								Window.alert("Sorry, An error occurred during upload abort!");
								return;
								}	
							case OK: {
								UploaderProgressView upv = deleteTimerEvent.getProgessView();
								VerticalPanel vp = upv.getPanel();
								if(vp!=null){
									vp.clear();
									HTML html = new HTML();
									vp.add(html);
									String text = "<div><img src='"+WorkspaceUploaderResources.getImageCancelRed().getUrl()+"'>";
									text+="<span style='margin-left:5px; vertical-align: top;'>Upload "+deleteTimerEvent.getFileName()+ " aborted</span></div>";
									html.setHTML(text);
									deleteTimerEvent.getUploader().setUploadStatus(UPLOAD_STATUS.ABORTED);
									notifyUploadAborted(deleteTimerEvent.getUploader().getFile().getParentId(), null);
								}
								break;	
								}
							}	
						}
						
						@Override
						public void onError(Request request, Throwable exception) {
							GWT.log("ERROR: Error during stop upload "+exception.getMessage()); 
							
						}
					});
				} catch (RequestException e) {
					GWT.log("RequestException: Error during stop upload "+e.getMessage()); 
				}
			}
		});
		
		
		monitorEventBus.addHandler(HideMonitorEvent.TYPE, new HideMonitorEventHandler() {
			
			@Override
			public void onHideMonitor(HideMonitorEvent hideMonitorEvent) {
				dialogUploadMonitor.hide();
			}
		});
	}
	
	/**
	 * Show monitor.
	 */
	public void showMonitor(){
		if(!dialogUploadMonitor.isShowing())
			dialogUploadMonitor.center();
	}



	/**
	 * Adds the new submit.
	 *
	 * @param uploader the uploader
	 * @param fileName the file name
	 */
	public void addNewSubmit(WorkspaceUploaderItem uploader, String fileName){
		if(!dialogUploadMonitor.isShowing())
			dialogUploadMonitor.center();
		
		monitorPanel.newWorkspaceUploder(uploader, fileName);
	}



	/**
	 * Poll workspace uploader.
	 *
	 * @param workspaceUploader the workspace uploader item
	 */
	public void pollWorkspaceUploader(final WorkspaceUploaderItem workspaceUploader){

		Timer timer = new Timer() {
			
			@Override
			public void run() {
				WorkspaceUploaderServiceAsync.Util.getInstance().getUploadStatus(workspaceUploader.getClientUploadKey(), new AsyncCallback<WorkspaceUploaderItem>() {
					
					@Override
					public void onSuccess(WorkspaceUploaderItem result) {
//						GWT.log("onSuccess: "+result.toString());
						synchronized(this){
							Timer tmn = mapTimer.get(workspaceUploader.getClientUploadKey());
							if(tmn!=null && tmn.isRunning()){
								if(result.getUploadStatus().equals(UPLOAD_STATUS.COMPLETED)){
									GWT.log("Upload Completed "+result.getFile().getItemId() +" name: "+result.getFile().getFileName());
									mapTimer.remove(tmn);
									
									if(!result.getIsOverwrite())
										notifyUploadCompleted(result.getFile().getParentId(), result.getFile().getItemId());
									else
										notifyOverwriteCompleted(result.getFile().getParentId(), result.getFile().getItemId());
									
									cancel();
								}else if(result.getUploadStatus().equals(UPLOAD_STATUS.FAILED)){
									GWT.log("Upload Failed "+result.getFile().getItemId() +" name: "+result.getFile().getFileName());
									mapTimer.remove(tmn);
									notifyUploadError(result.getFile().getParentId(), null, null);
//									new DialogResult(null, "Upload Failed!!", result.getStatusDescription()).center();
									cancel();
								}
								monitorPanel.updateWorkspaceUploder(result);
//								GWT.log(result.toString());
							}else{
								GWT.log("Timer is null or already closed or completed");
								cancel();
							}
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("onFailure: "+caught.getMessage());
						notifyUploadError(workspaceUploader.getFile().getParentId(), null, caught);
						cancel();
					}
				});
				
			}
		};
		mapTimer.put(workspaceUploader.getClientUploadKey(), timer);
		timer.scheduleRepeating(2000);
	}
	
	/**
	 * Notify upload completed.
	 *
	 * @param parentId the parent id
	 * @param itemId the item id
	 */
	private void notifyOverwriteCompleted(String parentId, String itemId){
		GWT.log("notifyOverwriteCompleted in monitor");
		WorkspaceUploaderListenerController.getEventBus().fireEvent(new NotifyUploadEvent(UPLOAD_EVENT_TYPE.OVERWRITE_COMPLETED, parentId, itemId));
	}
	
	/**
	 * Notify upload completed.
	 *
	 * @param parentId the parent id
	 * @param itemId the item id
	 */
	private void notifyUploadCompleted(String parentId, String itemId){
		GWT.log("notifyUploadCompleted in monitor");
		WorkspaceUploaderListenerController.getEventBus().fireEvent(new NotifyUploadEvent(UPLOAD_EVENT_TYPE.UPLOAD_COMPLETED, parentId, itemId));
	}
	
	/**
	 * Notify upload aborted.
	 *
	 * @param parentId the parent id
	 * @param itemId the item id
	 */
	private void notifyUploadAborted(String parentId, String itemId){
		WorkspaceUploaderListenerController.getEventBus().fireEvent(new NotifyUploadEvent(UPLOAD_EVENT_TYPE.ABORTED, parentId, itemId));
	}
	
	/**
	 * Notify upload error.
	 *
	 * @param parentId the parent id
	 * @param itemId the item id
	 * @param t the t
	 */
	private void notifyUploadError(String parentId, String itemId, Throwable t){
		WorkspaceUploaderListenerController.getEventBus().fireEvent(new NotifyUploadEvent(UPLOAD_EVENT_TYPE.FAILED, parentId, itemId, t));
	}
}
