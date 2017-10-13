package org.gcube.portlets.admin.vredeployment.client.presenter;

import java.util.ArrayList;

import org.gcube.portlets.admin.vredeployment.client.VREDeploymentServiceAsync;
import org.gcube.portlets.admin.vredeployment.client.view.Display;
import org.gcube.portlets.admin.vredeployment.shared.VREDefinitionBean;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ScrollPanel;
/**
 * 
 * @author Massimiliano Assante (assante@isti.cnr.it)
 *
 */
public class VREDeploymentPresenter implements Presenter {
	private final VREDeploymentServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;
	String location = null;
	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public VREDeploymentPresenter(VREDeploymentServiceAsync rpcService, HandlerManager eventBus, Display display) {
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = display;
	}
	@SuppressWarnings("unchecked")
	public void bind() {
		///*** BUTTONS & Menu
		//VIEW listener
		SelectionListener viewsl = new SelectionListener<ComponentEvent>() {  
			public void componentSelected(ComponentEvent ce) {  
				String vreEPR = display.getGridSelectionModel().getSelectedItem().getId();
				String vreName = display.getGridSelectionModel().getSelectedItem().getName();
				doViewDetails(vreEPR, vreName);
			}  
		};
		display.getViewButton().addSelectionListener(viewsl);
		display.getViewMenu().addSelectionListener(viewsl);

		//APPROVE
		SelectionListener approvesl = new SelectionListener<ComponentEvent>() {  
			public void componentSelected(ComponentEvent ce) {  
				String vreEPR = display.getGridSelectionModel().getSelectedItem().getId();
				String vreName = display.getGridSelectionModel().getSelectedItem().getName();
				doApprove(vreEPR, vreName);
			}  
		};
		display.getApproveButton().addSelectionListener(approvesl);
		display.getApproveMenu().addSelectionListener(approvesl);

		// EDIT
		SelectionListener editsl = new SelectionListener<ComponentEvent>() {  
			public void componentSelected(ComponentEvent ce) {  
				String vreEPR = display.getGridSelectionModel().getSelectedItem().getId();
				String vreName = display.getGridSelectionModel().getSelectedItem().getName();
				doEdit(vreEPR, vreName);
			}  
		};
		display.getEditButton().addSelectionListener(editsl);
		display.getEditMenu().addSelectionListener(editsl);

		// REFRESH
		display.getRefreshButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				fetchVREDefinitions();
			}  
		}); 

		// REMOVE
		SelectionListener removesl = new SelectionListener<ComponentEvent>() {  
			public void componentSelected(ComponentEvent ce) {  
				String vreEPR = display.getGridSelectionModel().getSelectedItem().getId();
				String vreName = display.getGridSelectionModel().getSelectedItem().getName();
				doRemove(vreEPR, vreName);
			}  
		};
		display.getRemoveButton().addSelectionListener(removesl);
		display.getRemoveMenu().addSelectionListener(removesl);

		// REMOVE
		@SuppressWarnings("rawtypes")
		SelectionListener undeploysl = new SelectionListener<ComponentEvent>() {  
			public void componentSelected(ComponentEvent ce) {  
				String vreEPR = display.getGridSelectionModel().getSelectedItem().getId();
				String vreName = display.getGridSelectionModel().getSelectedItem().getName();
				doUndeploy(vreEPR, vreName);
			}  
		};
		display.getUndeployButton().addSelectionListener(undeploysl);
	

		// VIEW REPORT
		SelectionListener viewreportSL = new SelectionListener<ComponentEvent>() {  
			public void componentSelected(ComponentEvent ce) {  
				String vreEPR = display.getGridSelectionModel().getSelectedItem().getId();
				String vreName = display.getGridSelectionModel().getSelectedItem().getName();
				doViewReport(vreEPR, vreName);
			}  
		};
		display.getViewReportButton().addSelectionListener(viewreportSL);
		display.getViewReportMenu().addSelectionListener(viewreportSL);

		// VIEW TEXTUAL REPORT
		SelectionListener viewTreportSL = new SelectionListener<ComponentEvent>() {  
			public void componentSelected(ComponentEvent ce) {  
				String vreEPR = display.getGridSelectionModel().getSelectedItem().getId();
				String vreName = display.getGridSelectionModel().getSelectedItem().getName();
				doViewTextualReport(vreEPR, vreName);
			}  
		};
		display.getViewTextualReportButton().addSelectionListener(viewTreportSL);

		//POSTPONE
		SelectionListener postPonesl = new SelectionListener<ComponentEvent>() {  
			public void componentSelected(ComponentEvent ce) {  
				String vreId = display.getGridSelectionModel().getSelectedItem().getId();
				String vreName = display.getGridSelectionModel().getSelectedItem().getName();
				doPostPone(vreId, vreName);
			}  
		};
		display.getPostPoneButton().addSelectionListener(postPonesl);



		///*** GRID
		display.getGridSelectionModel().addSelectionChangedListener(new SelectionChangedListener<VREDefinitionBean>() {			
			public void selectionChanged(SelectionChangedEvent<VREDefinitionBean> event) {
				if (event.getSelectedItem() != null)
					display.setGridContextMenu(event.getSelectedItem().getStatus());
				display.enableActionButtons(event.getSelectedItem());
			}			
		});

	}

	
	/**
	 * go method
	 */
	@Override
	public void go(HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
		fetchVREDefinitions();
	}
	/**
	 * fetch all the workflow documents belongin to this user
	 */
	private void fetchVREDefinitions() {
		display.maskCenterPanel("Loading VRE definitions, please wait", true);
		rpcService.getVREDefinitions(new AsyncCallback<ArrayList<VREDefinitionBean>>() {			
			@Override
			public void onSuccess(ArrayList<VREDefinitionBean> docs) {
				display.maskCenterPanel("", false);
				display.setData(docs);				
			}			
			@Override
			public void onFailure(Throwable arg0) {	
				display.maskCenterPanel("", false);
				Window.alert("Failed to get VRE list from service " + arg0.getMessage());				
			}
		});
	}

	private void doApprove(String vreEPR, String vreName) {
		display.maskCenterPanel("Approving VRE " + vreName +", please wait", true);
		rpcService.doApprove(vreEPR, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean arg0) {
				display.maskCenterPanel("", false);
				loadDeployer();
			}
			@Override
			public void onFailure(Throwable arg0) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to approve VRE" + arg0.getMessage());				
			}
		});
	}  
	private void doRemove(final String vreEPR, final String vreName) {
		MessageBox.confirm("Confirm", "Are you sure you want to remove "+ vreName +"?", new Listener<MessageBoxEvent>() {  
			public void handleEvent(MessageBoxEvent ce) {  
				if (ce.getButtonClicked().getText().equals("Yes")) {
					display.maskCenterPanel("Removing VRE " + vreName +", please wait", true);
					rpcService.doRemove(vreEPR, new AsyncCallback<Boolean>() {
						@Override
						public void onSuccess(Boolean arg0) {
							display.maskCenterPanel("", false);
							fetchVREDefinitions();
							Info.display("VRE Removal", "The '{0}' VRE was successfully removed", vreName);  
						}
						@Override
						public void onFailure(Throwable arg0) {
							display.maskCenterPanel("", false);
							Window.alert("Failed to remove VRE" + arg0.getMessage());				
						}
					});
				}

			}  
		});
	}  
	
	protected void doUndeploy(final String vreEPR, final String vreName) {
		MessageBox.confirm("Confirm", "Are you REALLY REALLY sure you want to undeploy "+ vreName +"?", new Listener<MessageBoxEvent>() {  
			public void handleEvent(MessageBoxEvent ce) {  
				if (ce.getButtonClicked().getText().equals("Yes")) {
					display.maskCenterPanel("Undeploying VRE " + vreName +", please wait", true);
					rpcService.doUndeploy(vreEPR, new AsyncCallback<Boolean>() {
						@Override
						public void onSuccess(Boolean arg0) {
							display.maskCenterPanel("", false);
							fetchVREDefinitions();
							Info.display("VRE Removal", "The '{0}' VRE undeploy has been successfully triggered. "
									+ "The infrastructure feels now relieved by having these resources back available. Thank you", vreName);  
						}
						@Override
						public void onFailure(Throwable arg0) {
							display.maskCenterPanel("", false);
							Window.alert("Failed to remove VRE" + arg0.getMessage());				
						}
					});
				}

			}  
		});
		
	}


	private void doViewReport(String vreEPR, String vreName) {
		display.maskCenterPanel("Switching to VRE report for " + vreName +", please wait", true);
		rpcService.doViewReport(vreEPR, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean arg0) {
				display.maskCenterPanel("", false);
				loadDeployer();
			}
			@Override
			public void onFailure(Throwable arg0) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to view report VRE" + arg0.getMessage());				
			}
		});

	}  

	private void doViewTextualReport(String vreEPR, String vreName) {
		display.maskCenterPanel("Fetching textual VRE report for " + vreName +", please wait", true);
		rpcService.getHTMLReport(vreEPR, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String reportHTML) {			
				final com.extjs.gxt.ui.client.widget.Window window = new com.extjs.gxt.ui.client.widget.Window();  
				window.setSize(700, 550);  
				window.setPlain(true);  
				window.setModal(true);  
				window.setBlinkModal(true);  
				window.setHeading("Textual Report");  

				ContentPanel cp = new ContentPanel();
				cp.setHeaderVisible(false);

				ScrollPanel scroller = new ScrollPanel();
				scroller.setSize("700", "550");
				scroller.add(new Html(reportHTML));

				cp.add(scroller);

				cp.setLayout(new FitLayout());

				window.add(cp);
				window.setLayout(new FitLayout());  
				window.addButton(new Button("Close", new SelectionListener<ButtonEvent>() {  
					@Override  
					public void componentSelected(ButtonEvent ce) {  
						window.hide();  
					}  
				}));  

				cp.layout();
				display.maskCenterPanel("", false);
				window.show();
			}
			@Override
			public void onFailure(Throwable arg0) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to view report VRE" + arg0.getMessage());				
			}
		});

	}  

	private void doEdit(String vreEPR, String vreName) {
		display.maskCenterPanel("Switching to edit mode for " + vreName +", please wait", true);
		rpcService.doEdit(vreEPR, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean arg0) {
				display.maskCenterPanel("", false);
				loadDefinition();
			}
			@Override
			public void onFailure(Throwable arg0) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to edit VRE" + arg0.getMessage());				
			}
		});
	}  

	private void doPostPone(String vreId, String vreName) {
		Window.confirm("You are going to postpone of 6 months the expiration date of " + vreName);
		rpcService.postPone(vreId, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to postpone VRE of 6 months" + caught.getMessage());				
			}

			@Override
			public void onSuccess(Boolean result) {
				display.maskCenterPanel("", false);
				fetchVREDefinitions();
			}
		});
	}


	private void doViewDetails(String vreEPR, String vreName) {
		display.maskCenterPanel("Retrieving details for VRE " + vreName +", please wait", true);
		rpcService.doViewDetails(vreEPR, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String html2Show) {
				display.maskCenterPanel("", false);
				display.showDetailsDialog(html2Show);
			}
			@Override
			public void onFailure(Throwable arg0) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to get VRE details" + arg0.getMessage());				
			}
		});

	}  

	/**
	 * Redirect to VRE Deployer Portlet
	 */
	public void loadDeployer(){
		getUrl();
		location += "/../vre-deployer";
		Window.open(location, "_self", "");		
	}
	/**
	 * Redirect to VRE Definition Portlet
	 */
	public void loadDefinition(){
		getUrl();
		location += "/../vre-definition";
		Window.open(location, "_self", "");		
	}
	/**
	 * Get URL from browser
	 */
	public native void getUrl()/*-{
			this.@org.gcube.portlets.admin.vredeployment.client.presenter.VREDeploymentPresenter::location = $wnd.location.href;
	}-*/;


}

