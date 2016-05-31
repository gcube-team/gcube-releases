/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.client.uploader;

import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploaderServiceAsync;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem.UPLOAD_STATUS;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Class TimerUpload.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 11, 2016
 */
public class TimerUpload extends Timer{

	/** The workspace uploader. */
	private WorkspaceUploaderItem workspaceUploader;
	private UploaderMonitor monitor;
	private TimerUpload INSTANCE = this;

	/**
	 * Instantiates a new timer upload.
	 *
	 * @param workspaceUploader the workspace uploader
	 * @param queue the queue
	 * @param indexQueue the index queue
	 */
	public TimerUpload(WorkspaceUploaderItem workspaceUploader) {
		this.workspaceUploader = workspaceUploader;
		this.monitor = UploaderMonitor.getInstance();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.Timer#run()
	 */
	@Override
	public void run() {

		WorkspaceUploaderServiceAsync.Util.getInstance().getUploadStatus(workspaceUploader.getClientUploadKey(), new AsyncCallback<WorkspaceUploaderItem>() {

			@Override
			public void onSuccess(WorkspaceUploaderItem result) {
				try{
					synchronized(this){
	//					Timer tmn = queue.get(workspaceUploader.getClientUploadKey());
						if(INSTANCE!=null && INSTANCE.isRunning()){
							if(result==null || result.getUploadStatus()==null){
								GWT.log("Upload or status its status is null for: "+workspaceUploader.getClientUploadKey());
								return;
							}
							if(result.getUploadStatus().equals(UPLOAD_STATUS.COMPLETED)){
								GWT.log("Upload Completed "+result.getFile().getItemId() +" name: "+result.getFile().getFileName());
	//							queue.remove(workspaceUploader.getClientUploadKey());
	//							monitor.deleteUploaderByClientKey(workspaceUploader.getClientUploadKey());

								if(!result.getIsOverwrite())
									monitor.notifyUploadCompleted(result.getFile().getParentId(), result.getFile().getItemId());
								else
									monitor.notifyOverwriteCompleted(result.getFile().getParentId(), result.getFile().getItemId());

								cancel();
								monitor.goNextUploaderAfterKey(workspaceUploader.getClientUploadKey());
							}else if(result.getUploadStatus().equals(UPLOAD_STATUS.FAILED)){
								GWT.log("Upload Failed "+result.getFile().getItemId() +" name: "+result.getFile().getFileName());
	//							monitor.deleteUploaderByClientKey(workspaceUploader.getClientUploadKey());
	//							queue.remove(workspaceUploader.getClientUploadKey());
								monitor.notifyUploadError(result.getFile().getParentId(), null, null);
	//								new DialogResult(null, "Upload Failed!!", result.getStatusDescription()).center();

								cancel();
								monitor.goNextUploaderAfterKey(workspaceUploader.getClientUploadKey());
							}else if(result.getUploadStatus().equals(UPLOAD_STATUS.ABORTED)){
	//							monitor.deleteUploaderByClientKey(workspaceUploader.getClientUploadKey());
								GWT.log("Upload Aborted "+result.getFile().getItemId() +" name: "+result.getFile().getFileName());
								cancel();
								monitor.goNextUploaderAfterKey(workspaceUploader.getClientUploadKey());
							}

							monitor.getMonitorPanel().updateWorkspaceUploder(result);
	//							GWT.log(result.toString());
						}else{
	//						monitor.deleteUploaderByClientKey(workspaceUploader.getClientUploadKey());
							GWT.log("Timer is null or already closed or completed");
							cancel();
							monitor.goNextUploaderAfterKey(workspaceUploader.getClientUploadKey());
						}
					}
				}catch(Exception e){
					GWT.log("getUploadStatus exception "+e.getMessage());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("onFailure: "+caught.getMessage());
				cancel();
//				monitor.deleteUploaderByClientKey(workspaceUploader.getClientUploadKey());
				monitor.goNextUploaderAfterKey(workspaceUploader.getClientUploadKey());
				monitor.notifyUploadError(workspaceUploader.getFile().getParentId(), null, caught);
//				removeTimer(workspaceUploader.getClientUploadKey());
			}
		});

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.Timer#cancel()
	 */
	@Override
	public void cancel() {
		GWT.log("Cancelling timer for "+workspaceUploader);
		super.cancel();
	}



}
