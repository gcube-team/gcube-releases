package org.gcube.portlets.admin.vredefinition.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.vredefinition.client.event.BackButtonEvent;
import org.gcube.portlets.admin.vredefinition.client.event.BackButtonEventHandler;
import org.gcube.portlets.admin.vredefinition.client.event.ExternalResourceSelectionEvent;
import org.gcube.portlets.admin.vredefinition.client.event.ExternalResourceSelectionEventHandler;
import org.gcube.portlets.admin.vredefinition.client.event.NextButtonEvent;
import org.gcube.portlets.admin.vredefinition.client.event.NextButtonEventHandler;
import org.gcube.portlets.admin.vredefinition.client.event.TreeNodeFunctionalityEvent;
import org.gcube.portlets.admin.vredefinition.client.event.TreeNodeFunctionalityEventHandler;
import org.gcube.portlets.admin.vredefinition.client.event.TreeNodeWizardMenuEvent;
import org.gcube.portlets.admin.vredefinition.client.event.TreeNodeWizardMenuEventHandler;
import org.gcube.portlets.admin.vredefinition.client.model.VREDefinitionModel;
import org.gcube.portlets.admin.vredefinition.client.model.VREFunctionalityModel;
import org.gcube.portlets.admin.vredefinition.client.model.WizardStepType;
import org.gcube.portlets.admin.vredefinition.client.presenter.Presenter;
import org.gcube.portlets.admin.vredefinition.client.presenter.VREDefinitionPresenter;
import org.gcube.portlets.admin.vredefinition.client.presenter.VREDescriptionPresenter;
import org.gcube.portlets.admin.vredefinition.client.presenter.VREFinishWizardPresenter;
import org.gcube.portlets.admin.vredefinition.client.presenter.VREFunctionalitiesPresenter;
import org.gcube.portlets.admin.vredefinition.client.presenter.VREInfoFunctionalitiesPresenter;
import org.gcube.portlets.admin.vredefinition.client.presenter.WizardActionsPresenter;
import org.gcube.portlets.admin.vredefinition.client.presenter.WizardMenuViewPresenter;
import org.gcube.portlets.admin.vredefinition.client.view.VREDefinitionView;
import org.gcube.portlets.admin.vredefinition.client.view.VREDescriptionView;
import org.gcube.portlets.admin.vredefinition.client.view.VREFinishWizardView;
import org.gcube.portlets.admin.vredefinition.client.view.VREFunctionalitiesView;
import org.gcube.portlets.admin.vredefinition.client.view.VREInfoFunctionalitiesView;
import org.gcube.portlets.admin.vredefinition.client.view.WizardActionsView;
import org.gcube.portlets.admin.vredefinition.client.view.WizardMenuView;
import org.gcube.portlets.admin.vredefinition.shared.ExternalResourceModel;
import org.gcube.portlets.admin.vredefinition.shared.VREDescriptionBean;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;


public class AppController {

	private final VREDefinitionModel vreModel = new VREDefinitionModel();
	
	private final HandlerManager eventBus;
	
	private VREDefinitionPresenter vreDefPresenter;
	private Presenter upCenterPanelPresenter;
	private WizardActionsPresenter wizardActionsPresenter;
	private WizardMenuViewPresenter wizardMenuPresenter;
	
	private RootPanel container;
	
	private HashMap<String, List<ExternalResourceModel>> funcToExternalResources = new HashMap<String, List<ExternalResourceModel>>();
	
	public List<ExternalResourceModel> getExtResourcesFromApp(String funcId) {
		return funcToExternalResources.get(funcId);
	}
	
	public HandlerManager getBus() {
		return eventBus;
	}
	
	private static AppController singleton;
	
	public static AppController getAppController() {
		return singleton;
	}
	
	private final VREDefinitionServiceAsync rpcService;
	
	public AppController(VREDefinitionServiceAsync rpcService, HandlerManager eventBus) {
		singleton = this;
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		bind();
	}
	
	public void go(RootPanel container) {
		
		this.container = container;
		
		final VREDefinitionView mainView = new VREDefinitionView();
		vreDefPresenter = new VREDefinitionPresenter(mainView);
		mainView.mask("Loading data ....");
		vreDefPresenter.go(container);
		updateSize();
		
		rpcService.isEditMode(new AsyncCallback<Map<String,Object>>() {
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
				
			}
			public void onSuccess(Map<String, Object> map) {
				
				if(map != null) {					
					vreModel.setEditMode();
					VREFunctionalityModel functionalities = (VREFunctionalityModel)map.get("functionalities");
					vreModel.setVREFunctionalities(functionalities.getChildren());	
				}
				
				WizardActionsView bottomView = new WizardActionsView(mainView.getBottomCenterPanel());
				wizardActionsPresenter = new WizardActionsPresenter(eventBus, bottomView);
				wizardActionsPresenter.go(mainView.getBottomCenterPanel());
				wizardActionsPresenter.setStepWizard(vreModel.getWizardStep());
				
				mainView.unmask();
				goStepWizard();
			}
		});

	}
	
	public void bind() {
		eventBus.addHandler(ExternalResourceSelectionEvent.TYPE, new ExternalResourceSelectionEventHandler() {

			public void onSelectedExternalResources(ExternalResourceSelectionEvent event) {
				funcToExternalResources.put(event.getFunctionalityModel().getId(), event.getAssociatedExternalResources());	
				GWT.log("PUT: " + event.getFunctionalityModel()+ " ID->" + event.getFunctionalityModel().getId());
				for (ExternalResourceModel extRes : event.getAssociatedExternalResources()) {
					GWT.log(extRes.getName()+extRes.getId()+extRes.isSelected());
				}
			}			
		});
		
		eventBus.addHandler(NextButtonEvent.TYPE, new NextButtonEventHandler() {

			public void onNextButton(NextButtonEvent event) {
				
				if(vreModel.getWizardStep() == WizardStepType.VREDefinitionEnd)
					showCreateVREConfirm();
				else
					goNextStepWizard();	
			}
		});
		
		eventBus.addHandler(BackButtonEvent.TYPE, new BackButtonEventHandler(){

			public void onBackButton(BackButtonEvent event) {
				goBackStepWizard();
			}
			
		});
		
		eventBus.addHandler(TreeNodeWizardMenuEvent.TYPE, new TreeNodeWizardMenuEventHandler() {
			
			public void onClick(TreeNodeWizardMenuEvent event) {

				if(upCenterPanelPresenter.doSave())
					vreModel.setWizardStep(event.getStepModel().getType());
				goStepWizard();
			}
		});
		
		eventBus.addHandler(TreeNodeFunctionalityEvent.TYPE, new TreeNodeFunctionalityEventHandler() {
			public void onClick(TreeNodeFunctionalityEvent event) {
				VREFunctionalityModel functionality = event.getFunctionalityModel();
				vreDefPresenter.display.getEastPanel().setExpanded(true);
				vreDefPresenter.display.getEastPanel().setVisible(true);

				
				VREInfoFunctionalitiesPresenter infoPresenter = new VREInfoFunctionalitiesPresenter(
						new VREInfoFunctionalitiesView(functionality, rpcService));
				
				infoPresenter.go(vreDefPresenter.display.getEastPanel());
				
			}
		});
		
		 Window.addResizeHandler(new ResizeHandler() {
				
				public void onResize(ResizeEvent event) {
					
					updateSize();
					
					WizardActionsView bottomView = new WizardActionsView(vreDefPresenter.display.getBottomCenterPanel());
					wizardActionsPresenter = new WizardActionsPresenter(eventBus, bottomView);
					wizardActionsPresenter.go(vreDefPresenter.display.getBottomCenterPanel());
				
					goStepWizard();	
				}
		 });
		 
	}	
	
	public void updateSize() {
		 
		 int topBorder = container.getAbsoluteTop();
		 int leftBorder = container.getAbsoluteLeft();
		
		 int rootHeight = Window.getClientHeight() - topBorder;
		 int rootWidth = Window.getClientWidth() - 2* leftBorder;
		 vreDefPresenter.display.asWidget().setHeight(String.valueOf(rootHeight - 40));
		 vreDefPresenter.display.asWidget().setWidth(String.valueOf(rootWidth - 20));
		 vreDefPresenter.display.getUpContainer().setSize(rootWidth - 25, rootHeight - 80);
		 vreDefPresenter.display.getBottomCenterPanel().setSize(rootWidth - 25, 48);
		 
		 
	 }
	
	private void goNextStepWizard(){
	
		if(upCenterPanelPresenter.doSave())
			vreModel.setNextStep();

		goStepWizard();		
	}
	
	private void goStepWizard() {
		
		WizardStepType step = vreModel.getWizardStep();
		vreDefPresenter.display.getEastPanel().setVisible(false);
		
		switch (step) {
		case VREDescription:
			upCenterPanelPresenter = new VREDescriptionPresenter(rpcService,
					vreModel,new VREDescriptionView());
			
			break;
		case VREFunctionalities: {
				upCenterPanelPresenter = new VREFunctionalitiesPresenter(rpcService,
					vreModel, eventBus,new VREFunctionalitiesView());
			
			
			VREInfoFunctionalitiesPresenter infoPresenter = new VREInfoFunctionalitiesPresenter(
					new VREInfoFunctionalitiesView());
			
			infoPresenter.go(vreDefPresenter.display.getEastPanel());
		}	
		break;
		case VREDefinitionEnd:
			upCenterPanelPresenter = new VREFinishWizardPresenter(new VREFinishWizardView(vreModel.getVREDescriptionBean(), vreModel.getVREFunctionalities()));
			break;
		default:
			break;
		}
		
		upCenterPanelPresenter.go(vreDefPresenter.display.getUpCenterPanel());
		
		wizardMenuPresenter = new WizardMenuViewPresenter(eventBus, 
				new WizardMenuView(vreModel.getWizardStepModel()));
		wizardMenuPresenter.go(vreDefPresenter.display.getwestPanel());
		
		wizardActionsPresenter.setStepWizard(step);
	
	}
	
	private void goBackStepWizard(){
		vreModel.setBackStep();
		goStepWizard();
	}
	
	private void showCreateVREConfirm() {
			     
		 MessageBox.confirm("Confirm",
				 " The  current VRE Definition will be stored in the infrastructure waiting for approval",new Listener<MessageBoxEvent>() {  
		       public void handleEvent(MessageBoxEvent ce) {  
			         Button btn = ce.getButtonClicked();  
			         if(btn.getText().equals("Yes"))
			        	 createVRE(); 
			       }  
			     });
	}
	
	private void createVRE() {
		
		if(!validateVREDescription())
			return;
		
		List<ModelData> items = validateVREFunctionalitiesSelected();
		if(items == null || items.isEmpty()) 
			return;
		
		List<String> categoriesIDs = new ArrayList<String>();
		List<String> functionalitiesIDs = new ArrayList<String>();
		for(ModelData item : items) {
			VREFunctionalityModel func = (VREFunctionalityModel)item;
			if(func.getChildCount() > 0) {
				
				categoriesIDs.add(func.getId());
			} else {
				functionalitiesIDs.add(func.getId());
			}
		}
		String[] functionalities = functionalitiesIDs.toArray(new String[functionalitiesIDs.size()]);
		
		rpcService.setVRE(vreModel.getVREDescriptionBean(), functionalities, funcToExternalResources, new AsyncCallback<String>() {
	
			public void onSuccess(String arg0) {
				
				MessageBox.info("VRE Definition", "The VRE is stored on Service for approving", null);
			
				vreDefPresenter.display.getBottomCenterPanel().setVisible(false);
				vreDefPresenter.display.getwestPanel().setVisible(false);
				
				int topBorder = container.getAbsoluteTop();
				int leftBorder = container.getAbsoluteLeft();

				int rootHeight = Window.getClientHeight() - topBorder;
				int rootWidth = Window.getClientWidth() - 2* leftBorder;
				vreDefPresenter.display.asWidget().setHeight(String.valueOf(rootHeight - 40));
				vreDefPresenter.display.asWidget().setWidth(String.valueOf(rootWidth - 20));
				vreDefPresenter.display.getUpContainer().setSize(rootWidth - 25, rootHeight - 40);
				upCenterPanelPresenter.display().setSize(String.valueOf(rootWidth - 35), String.valueOf(rootHeight - 80));
			}
	
			public void onFailure(Throwable arg0) {
				MessageBox.alert("VRE Definition", "Error ...", null);
			}
		});
	}
	
	private boolean validateVREDescription(){
		
		VREDescriptionBean bean = vreModel.getVREDescriptionBean();
		boolean check = true;
		if(bean.getName() == null || bean.getName().trim().isEmpty())
			check = false;
		if(bean.getDescription() == null || bean.getDescription().trim().isEmpty())
			check = false;
		if(bean.getDesigner() == null || bean.getDesigner().trim().isEmpty())
			check = false;
		if(bean.getManager() == null || bean.getManager().trim().isEmpty())
			check = false;
		if(bean.getStartTime() == null)
			check = false;
		if(bean.getEndTime() == null)
			check = false;
		
		if(!check) {
			vreModel.setWizardStep(WizardStepType.VREDescription);
			goStepWizard();
			
			
		}
		
		return check;
	}

	private List<ModelData> validateVREFunctionalitiesSelected() {
		
		List<ModelData> items = new ArrayList<ModelData>();
		List<ModelData> categories = vreModel.getVREFunctionalities();
		for(ModelData category : categories) {
			for(ModelData func : ((VREFunctionalityModel)category).getChildren())
			if (((VREFunctionalityModel)func).isSelected()){
				items.add(func);
			}
		}
		
		if(items.isEmpty()) {
			vreModel.setWizardStep(WizardStepType.VREFunctionalities);
			goStepWizard();
			MessageBox.alert("Alert", "Please, select at least one Functionality", null);
		}
		return items;
	}
}

