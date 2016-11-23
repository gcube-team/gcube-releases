/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.client.uploader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.gcube.portlets.widgets.workspaceuploader.client.ClosableDialog;
import org.gcube.portlets.widgets.workspaceuploader.client.ConstantsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploaderListenerController;
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
	private static ArrayList<LinkedHashMap<String, WorkspaceUploaderItem>> queueUploads = new ArrayList<LinkedHashMap<String,WorkspaceUploaderItem>>();

	private ClosableDialog dialogUploadMonitor = new ClosableDialog(monitorPanel, false, ConstantsWorkspaceUploader.MY_UPLOADS);
	private Map<String, TimerUpload> currentTimersRun = new HashMap<String, TimerUpload>();

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

	/**
	 * Instantiates a new uploader monitor.
	 */
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

				final String clientKey = deleteTimerEvent.getUploader().getClientUploadKey();
				final LinkedHashMap<String, WorkspaceUploaderItem> queue = getMapUploaderByClientUploadKey(clientKey);
				if(queue==null){
					GWT.log("onCancelUpload mapUpload is null for key: "+clientKey +", return");
					return;
				}

				final TimerUpload timer = getTimer(clientKey);
				GWT.log("Client abort for timer with clientKey "+clientKey);

				if(timer==null || !timer.isRunning()){
					GWT.log("Timer abort skipping, timer for clientKey "+clientKey+" is null or not running");
					return;
				}

				timer.cancel();

				RequestBuilder request = new RequestBuilder(RequestBuilder.GET, ConstantsWorkspaceUploader.WORKSPACE_UPLOADER_SERVLET_STREAM+"?"+ConstantsWorkspaceUploader.CLIENT_UPLOAD_KEYS+"="+clientKey+"&"+ConstantsWorkspaceUploader.CANCEL_UPLOAD+"=true");
				try {
					request.sendRequest(null, new RequestCallback() {

						@Override
						public void onResponseReceived(Request request, Response response) {
							String result = response.getText();
							GWT.log("onResponseReceived "+result);

							final HandlerResultMessage resultMessage = HandlerResultMessage.parseResult(result);
							GWT.log("Cancel Upload Stream result: "+resultMessage);
							if(resultMessage==null)
								return;

							switch(resultMessage.getStatus()){
							case ERROR:{
								GWT.log("ERROR: Error during stop upload "+resultMessage.getMessage());
								Window.alert("Sorry, An error occurred during upload abort! "+resultMessage.getMessage());
								goNextUploaderAfterKey(clientKey);
								break;
								}
							case OK:{
								UploaderProgressView upv = deleteTimerEvent.getProgessView();
								VerticalPanel vp = upv!=null?upv.getPanel():null;
								if(vp!=null){
									vp.clear();
									HTML html = new HTML();
									vp.add(html);
									String text = "<div><img src='"+WorkspaceUploaderResources.getImageCancelRed().getUrl()+"'>";
									text+="<span style='margin-left:5px; vertical-align: top;'>Upload "+deleteTimerEvent.getFileName()+ " aborted</span></div>";
									html.setHTML(text);

									//UPDATING STATUS AS ABORTED IN ORDER TO STOP THE TIMER
									GWT.log("Upload Aborted is OK");
									goNextUploaderAfterKey(clientKey);

									deleteTimerEvent.getUploader().setUploadStatus(UPLOAD_STATUS.ABORTED);
									notifyUploadAborted(deleteTimerEvent.getUploader().getFile().getParentId(), null);
								}
								break;
								}
							case UNKNOWN:
							case WARN:{
								GWT.log("UNKNOWN/WARN CASE: Error during stop upload "+resultMessage.getMessage());
								goNextUploaderAfterKey(clientKey);
								break;

								}
							}
						}

						@Override
						public void onError(Request request, Throwable exception) {
							GWT.log("ERROR: Error during stop upload "+exception.getMessage());
							goNextUploaderAfterKey(clientKey);

						}
					});
				} catch (RequestException e) {
					GWT.log("RequestException: Error during stop upload "+e.getMessage());
					goNextUploaderAfterKey(clientKey);
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
	 * Gets the monitor panel.
	 *
	 * @return the monitorPanel
	 */
	public MonitorPanel getMonitorPanel() {

		return monitorPanel;
	}


	/**
	 * Show monitor.
	 */
	public void showMonitor(){
		if(!dialogUploadMonitor.isShowing())
			dialogUploadMonitor.center();
	}


	/**
	 * Adds the new uploader to monitor panel.
	 *
	 * @param uploader the uploader
	 * @param fileName the file name
	 */
	public void addNewUploaderToMonitorPanel(WorkspaceUploaderItem uploader, String fileName){
		if(!dialogUploadMonitor.isShowing())
			dialogUploadMonitor.center();

		monitorPanel.newWorkspaceUploder(uploader, fileName);
	}


	/**
	 * Start timer.
	 *
	 * @param workspaceUploader the workspace uploader
	 */
	public void startTimer(final WorkspaceUploaderItem workspaceUploader){

		TimerUpload timer = new TimerUpload(workspaceUploader);
		GWT.log("Starting new timer for key: "+workspaceUploader.getClientUploadKey());
		saveTimer(timer, workspaceUploader.getClientUploadKey());
		timer.scheduleRepeating(2000);
	}


	/**
	 * Removes the timer.
	 *
	 * @param clientKey the client key
	 */
	private synchronized void removeTimer(String clientKey){
		Timer timer = currentTimersRun.get(clientKey);
		if(timer!=null){
			currentTimersRun.remove(clientKey);
			GWT.log("Remove timer for key: "+clientKey+", performed correctly");
		}else
			GWT.log("Remove timer for key: "+clientKey+", skypped, already removed?");
	}

	/**
	 * Save timer.
	 *
	 * @param timerUpload the timer upload
	 * @param clientKey the client key
	 */
	private synchronized void saveTimer(TimerUpload timerUpload, String clientKey){
		currentTimersRun.put(clientKey, timerUpload);
	}


	/**
	 * Gets the timer.
	 *
	 * @param clientKey the client key
	 * @return the timer
	 */
	private TimerUpload getTimer(String clientKey){
		return currentTimersRun.get(clientKey);
	}

	/**
	 * Notify upload completed.
	 *
	 * @param parentId the parent id
	 * @param itemId the item id
	 */
	protected void notifyOverwriteCompleted(String parentId, String itemId){
		GWT.log("notifyOverwriteCompleted in monitor");
		WorkspaceUploaderListenerController.getEventBus().fireEvent(new NotifyUploadEvent(UPLOAD_EVENT_TYPE.OVERWRITE_COMPLETED, parentId, itemId));
	}

	/**
	 * Notify upload completed.
	 *
	 * @param parentId the parent id
	 * @param itemId the item id
	 */
	protected void notifyUploadCompleted(String parentId, String itemId){
		GWT.log("notifyUploadCompleted in monitor");
		WorkspaceUploaderListenerController.getEventBus().fireEvent(new NotifyUploadEvent(UPLOAD_EVENT_TYPE.UPLOAD_COMPLETED, parentId, itemId));
	}

	/**
	 * Notify upload aborted.
	 *
	 * @param parentId the parent id
	 * @param itemId the item id
	 */
	protected void notifyUploadAborted(String parentId, String itemId){
		WorkspaceUploaderListenerController.getEventBus().fireEvent(new NotifyUploadEvent(UPLOAD_EVENT_TYPE.ABORTED, parentId, itemId));
	}

	/**
	 * Notify upload error.
	 *
	 * @param parentId the parent id
	 * @param itemId the item id
	 * @param t the t
	 */
	protected void notifyUploadError(String parentId, String itemId, Throwable t){
		WorkspaceUploaderListenerController.getEventBus().fireEvent(new NotifyUploadEvent(UPLOAD_EVENT_TYPE.FAILED, parentId, itemId, t));
	}


	/**
	 * New queue.
	 *
	 * @return the int
	 */
	public synchronized int newQueue() {

		int freeIndex = -1;
		GWT.log("Current queue upload size is: "+queueUploads.size());
		for(int i = 0; i < queueUploads.size(); i++){
		    if(queueUploads.get(i) == null){
		    	freeIndex = i;
		    	break;
		    }
		}

		GWT.log("Index for new queue uploads is: "+freeIndex);

		if(freeIndex>=0 && freeIndex<queueUploads.size()){
			GWT.log("Using set index: "+freeIndex);
			queueUploads.set(freeIndex, new LinkedHashMap<String,WorkspaceUploaderItem>());
		}else{
			freeIndex= queueUploads.size();
			GWT.log("Using add index: "+freeIndex);
			queueUploads.add(freeIndex, new LinkedHashMap<String,WorkspaceUploaderItem>());
		}
		GWT.log("Returning new queue index: "+freeIndex);
		return freeIndex;
	}


	/**
	 * Delete queue.
	 *
	 * @param index the index
	 */
	public synchronized void deleteQueue(int index) {

		if(index<0){
			GWT.log("Delete queue at invalid index: "+index +", skipping delete");
			return;
		}

		if(queueUploads.get(index)!=null){
			queueUploads.set(index, null);
			GWT.log("Deleted queue at index: "+index);
		}else
			GWT.log("Queue at index: "+index +", not found, already deleted?, skipping");
	}



	/**
	 * Adds the new uploader to queue.
	 *
	 * @param indexQueue the index queue
	 * @param workspaceUploaderItem the workspace uploader item
	 * @return the linked hash map
	 */
	public LinkedHashMap<String, WorkspaceUploaderItem> addNewUploaderToQueue(int indexQueue, WorkspaceUploaderItem workspaceUploaderItem) {
		LinkedHashMap<String, WorkspaceUploaderItem> queue = queueUploads.get(indexQueue);
		queue.put(workspaceUploaderItem.getClientUploadKey(), workspaceUploaderItem);
		return queue;
	}


	/**
	 * Do start polling queue.
	 *
	 * @param index the index
	 */
	public synchronized void doStartPollingQueue(int index) {
		LinkedHashMap<String, WorkspaceUploaderItem> queue = queueUploads.get(index);

		if(queue.size()<0){
			GWT.log("doStartPollingQueue return, queue size is < 0");
			return;
		}

		Set<String> keys = queue.keySet();

		if(keys.size()==0){
			GWT.log("no keys for queue index: "+index+", return");
			return;
		}

		WorkspaceUploaderItem workspaceUploader = getNextUploader(queue);
		startTimer(workspaceUploader);
	}

	/**
	 * Go next uploader after key.
	 *
	 * @param clientKey the client key
	 */
	protected void goNextUploaderAfterKey(String clientKey){
		removeTimer(clientKey);
		LinkedHashMap<String, WorkspaceUploaderItem> mapUploader = deleteUploaderByClientKey(clientKey);
		nextUploader(mapUploader, clientKey);
	}


	/**
	 * Gets the next uploader.
	 *
	 * @param mapUploaders the map uploaders
	 * @return the next uploader
	 */
	protected WorkspaceUploaderItem getNextUploader(LinkedHashMap<String, WorkspaceUploaderItem> mapUploaders){
		Iterator<String> it = mapUploaders.keySet().iterator();
		String firstKey = it.next();

		if(firstKey==null || firstKey.isEmpty()){
			GWT.log("getNextUploader return empty or null key, returning null");
			return null;
		}

		return mapUploaders.get(firstKey);
	}


	/**
	 * Next uploader.
	 *
	 * @param mapUploaders the map uploaders
	 * @param clientKey the client key
	 */
	protected synchronized void nextUploader(LinkedHashMap<String, WorkspaceUploaderItem> mapUploaders, String clientKey){

		int index = getQueueIndexFromClientUploadKey(clientKey);

		if(mapUploaders==null || mapUploaders.keySet().size()==0){
			GWT.log("nextUploader return, mapUploaders is null or size is = 0, deleting queue");
			deleteQueue(index);
			return;
		}

		WorkspaceUploaderItem workspaceUploader = getNextUploader(mapUploaders);

		if(workspaceUploader==null){
			GWT.log("nextUploader return a null uploader, deleting queue");
			deleteQueue(index);
			return;
		}

		startTimer(workspaceUploader);
	}

	/**
	 * Gets the map uploader by client upload key.
	 *
	 * @param clientKey the client key
	 * @return the map uploader by client upload key
	 */
	protected LinkedHashMap<String, WorkspaceUploaderItem> getMapUploaderByClientUploadKey(String clientKey){
		for (LinkedHashMap<String, WorkspaceUploaderItem> queue : queueUploads) {
			if(queue!=null){
				WorkspaceUploaderItem tm = queue.get(clientKey);
				if(tm != null)
					return queue;
			}
		}
		return null;
	}

	/**
	 * Gets the uploader by client upload key.
	 *
	 * @param clientKey the client key
	 * @return the uploader by client upload key
	 */
	protected WorkspaceUploaderItem getUploaderByClientUploadKey(String clientKey){
		LinkedHashMap<String, WorkspaceUploaderItem> map = getMapUploaderByClientUploadKey(clientKey);
		if(map!=null)
			return map.get(clientKey);

		return null;
	}


	/**
	 * Delete uploader by client key.
	 *
	 * @param clientKey the client key
	 * @return the linked hash map
	 */
	protected LinkedHashMap<String, WorkspaceUploaderItem> deleteUploaderByClientKey(String clientKey){
		GWT.log("deleteUploaderByClientKey by key: "+clientKey);
		LinkedHashMap<String, WorkspaceUploaderItem> map = getMapUploaderByClientUploadKey(clientKey);
		if(map!=null){
			GWT.log("Deleted uploader with key: "+clientKey);
			map.remove(clientKey);
		}
		return map;
	}

	/**
	 * Gets the queue index from client upload key.
	 *
	 * @param clientKey the client key
	 * @return the queue index from client upload key, otherwise -1;
	 */
	public int getQueueIndexFromClientUploadKey(String clientKey){
		int index = -1;
		for (LinkedHashMap<String, WorkspaceUploaderItem> queue : queueUploads) {
			if(queue!=null){
				index++;
				WorkspaceUploaderItem tm = queue.get(clientKey);
				if(tm != null)
					return index;
				else
					index = -1;
			}
		}
		return index;
	}

}
