/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.inputSpaceArea;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.statisticalmanager.client.Constants;
import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;
import org.gcube.portlets.user.statisticalmanager.client.bean.ImportStatus;
import org.gcube.portlets.user.statisticalmanager.client.events.ImportCreatedEvent;
import org.gcube.portlets.user.statisticalmanager.client.events.ImportRemovedEvent;
import org.gcube.portlets.user.statisticalmanager.client.events.ImportTerminatedEvent;
import org.gcube.portlets.user.statisticalmanager.client.events.SessionExpiredEvent;
import org.gcube.portlets.user.statisticalmanager.client.events.TablesGridGotDirtyEvent;
import org.gcube.portlets.user.statisticalmanager.client.rpc.StatisticalManagerPortletServiceAsync;
import org.gcube.portlets.user.statisticalmanager.client.util.EventBusProvider;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

/**
 * @author ceras
 *
 */
public class UploadMonitor extends LayoutContainer {
	
	private VerticalPanel vp = new VerticalPanel();
	
//	private List<ImportStatusPanel> panels = new ArrayList<ImportStatusPanel>();

	private StatisticalManagerPortletServiceAsync service;
	private Timer timer;
	private Date todayDate;
//	private boolean dirty = true;
	private List<ImportStatusPanel> statusPanels = new ArrayList<ImportStatusPanel>();
	private List<String> idsToAvoidCheckCompleted = new ArrayList<String>();

	public UploadMonitor() {
		bind();
		
		todayDate = new Date();
		this.service = StatisticalManager.getService();
		
		this.addStyleName("uploadMonitor");
		
		Image img = new Image(StatisticalManager.resources.inputSpaceMonitor());
		img.addStyleName("workflow-icon");
		this.add(img);

		Html title = new Html("Upload Monitor");
		title.addStyleName("uploadMonitor-title");
		this.add(title);

		Html description = new Html("This panel reports the status of the importing procedure for the data sets below. The system is performing a validation of the data sets, a check for template compliancy and the creation of a data set.");
		description.addStyleName("uploadMonitor-description");
		this.add(description);

		vp.setSize(360, 350);
		this.add(vp);
		vp.setStyleAttribute("overflow","auto");
		vp.setStyleAttribute("margin", "1px");
		vp.setStyleAttribute("margin-top", "10px");
		
		timer = new Timer() {			
			@Override
			public void run() {
				update();
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.LayoutContainer#onRender(com.google.gwt.user.client.Element, int)
	 */
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		updateAll(false);
	}

	/**
	 * 
	 */
	private void bind() {
		EventBusProvider.getInstance().addHandler(ImportCreatedEvent.getType(), new ImportCreatedEvent.ImportCreatedHandler() {			
			@Override
			public void onImportCreated(ImportCreatedEvent event) {
				updateAll(true);
//				dirty = true;
			}
		});
		
		EventBusProvider.getInstance().addHandler(ImportTerminatedEvent.getType(), new ImportTerminatedEvent.ImportTerminatedHandler() {
			@Override
			public void onImportTerminated(ImportTerminatedEvent event) {
				ImportStatus importStatus = event.getImportStatus();
				String importId = importStatus.getId();
				if (!idsToAvoidCheckCompleted.contains(importId)) {
					// in this case fire the event that shows message and possibly update the tables grid
					
					if (importStatus.isComplete()) {
						Info.display("Import Complete", "The data set associated with the file "+importStatus.getFileName()+" was successful imported.");
						// update tables grid
						EventBusProvider.getInstance().fireEvent(new TablesGridGotDirtyEvent(importStatus.getId()));
					} else{
						
//						MessageBox.alert("Error", " Failed to import the data set." +
//								" Maybe the file format is wrong or the File Name you " +
//								"assigned was yet used. Please check the DataSpace for the yet imported File Resources.", null);
					
						if(importStatus.getResoruce()!=null)
						MessageBox.alert("Import Error", " Failed to import the data set. Service Error:"  +
								importStatus.getResoruce().getDescription(), null);
						else
							MessageBox.alert("Error", " Failed to import the data set." +
									" Maybe the file format is wrong or the File Name you " +
									"assigned was yet used. Please check the DataSpace for the yet imported File Resources.", null);
						
					}
					idsToAvoidCheckCompleted.add(importId);					
				}
			}
		});
		
		EventBusProvider.getInstance().addHandler(ImportRemovedEvent.getType(), new ImportRemovedEvent.ImportRemovedHandler() {
			@Override
			public void onImportRemoved(ImportRemovedEvent event) {
				ImportStatusPanel importStatusPanel = event.getImportStatusPanel();
				vp.remove(importStatusPanel);
				vp.layout();
			}
		});
		
		EventBusProvider.getInstance().addHandler(SessionExpiredEvent.getType(), new SessionExpiredEvent.SessionExpiredHandler() {
			@Override
			public void onSessionExpired(SessionExpiredEvent event) {
				timer.cancel();
			}
		});
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		update();
		timer.scheduleRepeating(Constants.TIME_UPDATE_MONITOR);
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		timer.cancel();
	}
	
	protected void update() {
		updateOnlyRunning();
	}

	/**
	 * 
	 */
	private void updateOnlyRunning() {
//		System.out.println("[UPLOAD MONITOR] Update ONLY RUNNING...");
		for (ImportStatusPanel importStatusPanel : statusPanels) {
			ImportStatus importStatus = importStatusPanel.getImportStatus();
			if (!importStatus.isTerminated())
				importStatusPanel.updateStatus();
		}
	}

	public void updateAll(final boolean alertIfCompleted) {
//		System.out.println("[UPLOAD MONITOR] Update ALL...");
		service.getImportsStatus(todayDate, new AsyncCallback<List<ImportStatus>>() {
			@Override
			public void onSuccess(List<ImportStatus> result) {
				updateAll(result, alertIfCompleted);
			}
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Error", "Impossible to load imports status<br>Cause: "+caught.getCause()+"<br>Message: "+caught.getMessage(), null);
			}
		});
	}

	/**
	 * @param result
	 */
	protected void updateAll(List<ImportStatus> result, final boolean alertIfCompleted) {
		vp.removeAll();
		if (result.size()==0)
			vp.add(new Html("<div class='uploadMonitor-text'>No upload found.</div>"));
		else {
			statusPanels.clear();
			for (ImportStatus status: result) {
				//boolean checkIfCompleted;
				String id = status.getId();
				if (idsToAvoidCheckCompleted.contains(id)){
					// in this case the status don't fire the complete event 
					//checkIfCompleted = false;
					
				} else {
					if (!alertIfCompleted) {
						idsToAvoidCheckCompleted.add(status.getId());
						//checkIfCompleted = false;
					} else {
						//checkIfCompleted = true;
					}
				}
				ImportStatusPanel importStatusPanel = new ImportStatusPanel(status);
				statusPanels.add(importStatusPanel);
				vp.add(importStatusPanel);
			}
		}

		vp.layout();
	}

}
