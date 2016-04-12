/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.client.uploader;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.widgets.workspaceuploader.client.events.HideMonitorEvent;
import org.gcube.portlets.widgets.workspaceuploader.client.resource.ProgressBarCssAndImages;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem.UPLOAD_STATUS;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * The Class MonitorPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 24, 2015
 */
public class MonitorPanel extends ScrollPanel{
	
	private VerticalPanel vpListUpload = new VerticalPanel();
	private VerticalPanel mainPanel = new VerticalPanel();
	private Map<String, WorkspaceUploaderItem> mapUploading = new HashMap<String, WorkspaceUploaderItem>();
	private Map<String, UploaderProgressView> mapPanels = new HashMap<String, UploaderProgressView>();
	private HandlerManager eventBus;
	private HorizontalPanel hpNoUploads = new HorizontalPanel();
	
	private HTML labelNumbUpload = new HTML("Uploaded 0 of 0");
	private HorizontalPanel hpBottomPanel = new HorizontalPanel();
	
	private HorizontalPanel hpCloseMonitor = new HorizontalPanel();
	
	static {
		ProgressBarCssAndImages.INSTANCE.css().ensureInjected();
	}
	/**
	 * Instantiates a new monitor panel.
	 *
	 * @param bus the event bus
	 */
	public MonitorPanel(HandlerManager bus) {
		this.eventBus = bus;
		initPanelNoUploads();
		addCloseHandler();

		//NUMBER OF UPLOADS
		hpBottomPanel.getElement().setId("hpBottomPanel");
//		hpNumbUpload.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		hpBottomPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpBottomPanel.setWidth("100%");
		hpBottomPanel.add(labelNumbUpload);
		
//		mainPanel.add(hpNumbUpload);
//		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		//LIST OF UPLOADS
		vpListUpload.getElement().getStyle().setMargin(5, Unit.PX);
//		setHeight("500px");
		vpListUpload.add(hpNoUploads);
		ScrollPanel scroll = new ScrollPanel();
		scroll.add(vpListUpload);
		scroll.setHeight("200px");
//		scroll.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
//		scroll.getElement().getStyle().setBorderWidth(1.0, Unit.PX);
		
		mainPanel.add(scroll);
		
		//CLOSE MONITOR
//		hpCloseMonitor.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
//		hpCloseMonitor.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		Button bClose = new Button("Close");
		bClose.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new HideMonitorEvent());
			}
		});
		
//		hpNumbUpload.add(bClose);
		
		hpCloseMonitor.add(bClose);
		hpCloseMonitor.addStyleName("align-right-close");
		hpBottomPanel.add(hpCloseMonitor);
		hpBottomPanel.getElement().getStyle().setMarginTop(5.0, Unit.PX);
		mainPanel.setWidth("100%");
		mainPanel.add(hpBottomPanel);
		
		add(mainPanel);
//		RootPanel.get("workspace-uploader").add(verticalPanel);
	}
	
	
	private void updateNumberUpload(){
		int numCompleted = 0;
	    for (String uploadKey : mapUploading.keySet()) {
        	WorkspaceUploaderItem uploader = mapUploading.get(uploadKey);
        	if(uploader.getUploadStatus().equals(UPLOAD_STATUS.COMPLETED))
        		numCompleted++;
		} 
	    
	    labelNumbUpload.setHTML("Uploaded "+numCompleted +" of "+mapUploading.size());
	}
	
	private void initPanelNoUploads(){
		hpNoUploads = new HorizontalPanel();
		hpNoUploads.addStyleName("noUploads");
		hpNoUploads.add(new HTML("No Uploads"));
	}
	
	private void removeNoUploads(){
		
		try{
			vpListUpload.remove(hpNoUploads);
		}catch(Exception e){
			
		}
	}
	
	/**
	 * Update workspace uploder.
	 *
	 * @param uploader the uploader
	 */
	public void updateWorkspaceUploder(WorkspaceUploaderItem uploader){

		mapUploading.put(uploader.getClientUploadKey(), uploader);
		
		if(mapPanels.get(uploader.getClientUploadKey())!=null){
			UploaderProgressView upv = mapPanels.get(uploader.getClientUploadKey());
			upv.update(uploader);
		}else{
			GWT.log("Skip update, uploader: "+uploader.getClientUploadKey() +" "+uploader.getFile().toString()+", not found!");
		}
		
		updateNumberUpload();
	}
	
	/**
	 * New workspace uploder.
	 *
	 * @param uploader the uploader
	 * @param fileName the file name
	 */
	public void newWorkspaceUploder(WorkspaceUploaderItem uploader, final String fileName){
		removeNoUploads();
		mapUploading.put(uploader.getClientUploadKey(), uploader);
		UploaderProgressView upv = new UploaderProgressView(uploader, fileName, eventBus);
		mapPanels.put(uploader.getClientUploadKey(), upv);
		VerticalPanel upvPanel = upv.getPanel();
		upvPanel.addStyleName("upladerMargin");
//		vpListUpload.insert(upvPanel, 0);
		vpListUpload.add(upvPanel);
		
		updateNumberUpload();
	}

	/**
	 * Adds the close handler.
	 */
	private void addCloseHandler(){
		
	    Window.addWindowClosingHandler(new Window.ClosingHandler() {
	        @Override
	        public void onWindowClosing(ClosingEvent event) {
//	        	GWT.log("Closing");
	            String msg = "";
	            for (String uploadKey : mapUploading.keySet()) {
	            	WorkspaceUploaderItem uploader = mapUploading.get(uploadKey);
	            	GWT.log("Closing uploder status: "+uploader.getFile().getFileName() +" status: "+uploader.getUploadStatus());
	            	if(uploader.getUploadStatus().equals(UPLOAD_STATUS.WAIT) || uploader.getUploadStatus().equals(UPLOAD_STATUS.IN_PROGRESS))
	            		msg+=uploader.getFile().getFileName()+";";
				}
	            
	            if(msg!=null && !msg.isEmpty()){
	            	event.setMessage("Abort upload? "+msg);
	            }
	        }
	    });

	    Window.addCloseHandler(new CloseHandler<Window>() {
	        @Override
	        public void onClose(CloseEvent<Window> event) {
//	        	Window.alert("bye bye Closed");
	        }       
	    });
	}

}
