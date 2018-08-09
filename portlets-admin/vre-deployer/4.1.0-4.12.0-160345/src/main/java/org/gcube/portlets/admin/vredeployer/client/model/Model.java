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
	public void getSummary() {
		mainContainer.getCenterPanel().showFinalize(vreDesc);
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
	 * 
	 * @return -
	 */
	public VREDescrBean getVreDesc() {
		return vreDesc;
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
