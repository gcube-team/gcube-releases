package org.gcube.portlets.admin.vredeployer.client.model;

import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.admin.vredeployer.client.VredeployerService;
import org.gcube.portlets.admin.vredeployer.client.VredeployerServiceAsync;
import org.gcube.portlets.admin.vredeployer.client.view.panels.MainContainer;
import org.gcube.portlets.admin.vredeployer.shared.GHNBean;
import org.gcube.portlets.admin.vredeployer.shared.GHNMemory;
import org.gcube.portlets.admin.vredeployer.shared.GHNProfile;
import org.gcube.portlets.admin.vredeployer.shared.GHNSite;
import org.gcube.portlets.admin.vredeployer.shared.RunningInstance;
import org.gcube.portlets.admin.vredeployer.shared.VREDeployerStatusType;
import org.gcube.portlets.admin.vredeployer.shared.VREDescrBean;

import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class Model {


	private VREDescrBean vreDesc;

	private GHNBean[] selectedGHNs;	
	private boolean isCloudSelected = false;
	private boolean isCloudAvailable = true;



	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final VredeployerServiceAsync deployerService = GWT.create(VredeployerService.class);
	;
	private MainContainer mainContainer;

	public Model(MainContainer mainContainer) {
		super();
		this.mainContainer = mainContainer;
	}

	/**
	 * 
	 */
	public void checkApproveModeEnabled() {
		deployerService.isApprovingModeEnabled(new AsyncCallback<VREDeployerStatusType>() {
			public void onSuccess(VREDeployerStatusType result) {
				if (result == VREDeployerStatusType.APPROVE) {			
					getVREInitialInfo();
				} 
				else if (result == VREDeployerStatusType.NON_APPROVE) {		
					mainContainer.getWestPanel().disable();
					mainContainer.getCenterPanel().unmask();
					mainContainer.getCenterPanel().addText("Approve mode was not enabled");
					mainContainer.getCenterPanel().layout();
				}
				else if (result == VREDeployerStatusType.DEPLOYING) {	
					mainContainer.getWestPanel().disable();
					mainContainer.getCenterPanel().unmask();
					mainContainer.getCenterPanel().showReport();	
				}

			}

			public void onFailure(Throwable caught) {	
				mainContainer.getWestPanel().disable();
				mainContainer.getCenterPanel().unmask();
				mainContainer.getCenterPanel().addText("There was an error on server: " + caught.getMessage());
				mainContainer.getCenterPanel().layout();
			}
		});
	}

	/**
	 * load the initial info for the VRE
	 */
	public void getVREInitialInfo() {
		deployerService.getVRE(new AsyncCallback<VREDescrBean>() {

			public void onFailure(Throwable caught) {
				applyActionsOnException(caught);
			}

			public void onSuccess(VREDescrBean vremodel) {
				vreDesc = vremodel;
				mainContainer.getCenterPanel().showVreDescription(vremodel);
				mainContainer.getCenterPanel().layout();
				mainContainer.getCenterPanel().unmask();
			}
		});
	}



	/**
	 * 
	 */
	public void getFunctionality()  {
		deployerService.getFunctionality(new AsyncCallback<VREFunctionalityModel>() {
			public void onFailure(Throwable caught) {
				applyActionsOnException(caught);
			}
			public void onSuccess(VREFunctionalityModel root) {
				if (root!= null) {
					mainContainer.getCenterPanel().showFunctionality(root);
					mainContainer.getCenterPanel().layout();
					mainContainer.getCenterPanel().unmask();
				} else
					applyActionsOnException("");
			}
		});
	}

	/**
	 * 
	 */
	public void getAvailableGhnList() {
		deployerService.getAvailableGHNs(new AsyncCallback<List<GHNProfile>>() {

			public void onFailure(Throwable caught) { 
				applyActionsOnException(caught); 
			}

			public void onSuccess(List<GHNProfile> result) {
				GHNMemory mem = new GHNMemory("9900000000", "990000000");
				GHNSite site = new GHNSite("Lucca", "Italy", "d4science.org");
				List<RunningInstance> run = new LinkedList<RunningInstance>();
				List<String> libs = new LinkedList<String>();
				GHNProfile dummy = new GHNProfile(
						"13ea2882-97f7-4961-bae1-872fa2ff7157", "node-dummy", 
						run, false, mem, site, libs, false);
				result.add(dummy);
				mainContainer.getCenterPanel().showGHNList(result);
				mainContainer.getCenterPanel().layout();
				if (! isCloudSelected)
					mainContainer.getCenterPanel().unmask();

			}
		});
	}

	/**
	 * 
	 * @param selectedGHNIds
	 * @param idCandidateGHN
	 * @return
	 */
	public void setGHNsSelected(GHNBean[] selectedGHNs) {
		this.selectedGHNs = selectedGHNs;
		isCloudSelected = false;
		String[] selectedIds = new String[selectedGHNs.length];
		for (int i = 0; i < selectedGHNs.length; i++) {
			selectedIds[i] = selectedGHNs[i].getId();
		}

		deployerService.setGHNsSelected(selectedIds, new AsyncCallback<Boolean>() {
			public void onSuccess(Boolean result) {
				Info.display("Saving Operation", "The ghn selection was saved successfully");
			}

			public void onFailure(Throwable caught) {
				MessageBox.alert("Error", "Error while saving ghn list: " + caught.getMessage(), null);  
			}
		});
	}


	/**
	 * 
	 */
	public void checkCloudSelected() {
		deployerService.isCloudSelected(new AsyncCallback<Integer>() {

			public void onFailure(Throwable caught) {
				mainContainer.getEastPanel().addText("There was an error on server: " + caught.getMessage());
				mainContainer.getEastPanel().unmask();
				mainContainer.getEastPanel().layout();				
			}

			public void onSuccess(Integer result) {
				if (result != -1) {
					isCloudSelected = true;
					mainContainer.getEastPanel().showCloudPanel(true, result);
				}
				else {
					isCloudSelected = false;
					mainContainer.getEastPanel().showCloudPanel(false, -1);
				}
			}
		});
	}

	/**
	 * 
	 */
	public void getSummary() {
		mainContainer.getCenterPanel().showFinalize(vreDesc, selectedGHNs, isCloudSelected());
		mainContainer.getCenterPanel().layout();		
	}

	/**
	 * take care of showing ens user the error cause and remove the mask
	 * @param caught
	 */
	private void applyActionsOnException(Throwable caught) {
		mainContainer.getCenterPanel().addText("There was an error on server: " + caught.getMessage());
		mainContainer.getCenterPanel().layout();
		mainContainer.getCenterPanel().unmask();
	}
	
	/**
	 * take care of showing ens user the error cause and remove the mask
	 * @param caught
	 */
	private void applyActionsOnException(String message) {
		mainContainer.getCenterPanel().addText("There was an error on server, please try again ina ashort while.");
		mainContainer.getCenterPanel().layout();
		mainContainer.getCenterPanel().unmask();
	}


	/**
	 * @return -
	 */
	public boolean isCloudSelected() {
		return isCloudSelected;
	}

	/**
	 * 
	 * @param isCloudSelected .
	 * @param virtualMachines .
	 */
	public void setCloudSelected(boolean isCloudSelected, int virtualMachines) {
		this.isCloudSelected = isCloudSelected;

		mainContainer.getEastPanel().mask("Applying settings, please hold", "loading-indicator");	

		deployerService.setCloudDeploy(virtualMachines, new AsyncCallback<Boolean>() {
			public void onSuccess(Boolean result) {
				mainContainer.getEastPanel().unmask();
				Info.display("Saving Operation", "The Cloud selection was saved successfully");
			}
			public void onFailure(Throwable caught) {
				mainContainer.getEastPanel().unmask();
			}
		});
	}

	/**
	 * 
	 * @return -
	 */
	public VREDescrBean getVreDesc() {
		return vreDesc;
	}

	/**
	 * 
	 * @return -
	 */
	public GHNBean[] getSelectedGHNs() {
		return selectedGHNs;
	}

	/**
	 * 
	 */
	public void startVREDeploymentStatus() {
		mainContainer.getCenterPanel().mask("VRE Deploying, please hold", "loading-indicator");	
		deployerService.deployVRE(new AsyncCallback<Boolean>() {
			public void onFailure(Throwable caught) {
				applyActionsOnException(caught);
			}

			public void onSuccess(Boolean result) {
				mainContainer.getCenterPanel().unmask();
				mainContainer.getCenterPanel().showReport();	

			}
		});

	}

}
