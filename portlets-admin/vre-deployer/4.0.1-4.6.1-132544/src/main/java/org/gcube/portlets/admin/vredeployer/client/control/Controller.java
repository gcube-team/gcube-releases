package org.gcube.portlets.admin.vredeployer.client.control;

import org.gcube.portlets.admin.vredeployer.client.VREDeployerConstants;
import org.gcube.portlets.admin.vredeployer.client.Vredeployer;
import org.gcube.portlets.admin.vredeployer.client.model.AtomicTreeNode;
import org.gcube.portlets.admin.vredeployer.client.view.panels.CenterPanel;
import org.gcube.portlets.admin.vredeployer.shared.GHNBean;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.GWT;

public class Controller {	

	private Vredeployer vreDeployer;
	
	public Controller(final Vredeployer vreDeployer) {
		this.vreDeployer = vreDeployer;
	}

	public void treeItemClicked(AtomicTreeNode selectedModel) {
		CenterPanel cp = vreDeployer.getMainContainer().getCenterPanel();
		switch (selectedModel.getType()) {
		case INFO:
			cp.setHeading("VRE Overall Information");
			cp.setIconStyle("images/icons/information.png");
			cp.emptyPanel();
			showLoading();
			vreDeployer.getModel().getVREInitialInfo();
			vreDeployer.getMainContainer().getEastPanel().collapse();
			vreDeployer.getMainContainer().showEastPanel(false);
			break;
		case FUNCTIONALITY:
			cp.setHeading("VRE Selected Functionality");
			cp.setIconStyle("images/icons/functionality.png");
			cp.emptyPanel();
			showLoading();
			vreDeployer.getModel().getFunctionality();
			vreDeployer.getMainContainer().showEastPanel(true);
			break;
		case ARCHITECTURE:
			cp.setHeading("Select nodes to deploy VRE Services");
			cp.setIconStyle("images/icons/architecture.png");
			cp.emptyPanel();
			showLoading();
			showEastPanelLoading();
			vreDeployer.getModel().getAvailableGhnList();
			vreDeployer.getModel().checkCloudSelected();
			vreDeployer.getMainContainer().showEastPanel(true);
			break;
		case REPORT:
			cp.setHeading("Finalize");
			cp.setIconStyle("images/icons/play.png");
			cp.emptyPanel();
			vreDeployer.getMainContainer().getCenterPanel().mask("Step Architecture Missing, define nodes first", "");		
			vreDeployer.getMainContainer().getEastPanel().hide();
			vreDeployer.getModel().getSummary();
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 */
	private void showLoading() {
		vreDeployer.getMainContainer().getCenterPanel().mask(VREDeployerConstants.LOADING_TEXT, "loading-indicator");		
	}
	
	/**
	 * 
	 */
	private void showEastPanelLoading() {
		vreDeployer.getMainContainer().getEastPanel().setIconStyle("images/icons/cloud.png");
		vreDeployer.getMainContainer().getEastPanel().mask("Checking cloud availability please wait...", "loading-indicator");		
	}
	
	/**
	 * 
	 */
	public void setDefaultTreeItemSelected() {
		CenterPanel cp = vreDeployer.getMainContainer().getCenterPanel();
		cp.setHeading("VRE Overall Information");
		cp.setIconStyle("images/icons/information.png");	
	}
	
	/**
	 * 
	 * @param toShow
	 */
	public void setEastPanelContent(ContentPanel toShow) {
		vreDeployer.getMainContainer().getEastPanel().removeAll();
		vreDeployer.getMainContainer().getEastPanel().add(toShow);
		vreDeployer.getMainContainer().getEastPanel().layout();
	}
	
	/**
	 * 
	 * @param selectedGHNIds
	 * @param idCandidateGHN
	 */
	public void setGHNsSelected(GHNBean[] selectedGHNIds) {
		vreDeployer.getModel().setGHNsSelected(selectedGHNIds);
	}
	
	public void maskCenterPanel(boolean mask) {
		if (mask) 
			vreDeployer.getMainContainer().getCenterPanel().mask();		
		else
			vreDeployer.getMainContainer().getCenterPanel().unmask();
	}
	
	public void setCloudSelected(boolean selected, int vMachines) {
		vreDeployer.getModel().setCloudSelected(selected, vMachines);
	}
	
	public void createVreButtonClicked() {
		vreDeployer.getModel().startVREDeploymentStatus();
	}
	
}
