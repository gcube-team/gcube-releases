package org.gcube.portlets.admin.vredeployer.client.control;

import org.gcube.portlets.admin.vredeployer.client.VREDeployerConstants;
import org.gcube.portlets.admin.vredeployer.client.Vredeployer;
import org.gcube.portlets.admin.vredeployer.client.model.AtomicTreeNode;
import org.gcube.portlets.admin.vredeployer.client.view.panels.CenterPanel;

import com.extjs.gxt.ui.client.widget.ContentPanel;

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
		case REPORT:
			cp.setHeading("Finalize");
			cp.setIconStyle("images/icons/play.png");
			cp.emptyPanel();
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
	
	public void maskCenterPanel(boolean mask) {
		if (mask) 
			vreDeployer.getMainContainer().getCenterPanel().mask();		
		else
			vreDeployer.getMainContainer().getCenterPanel().unmask();
	}

	public void createVreButtonClicked() {
		vreDeployer.getModel().startVREDeploymentStatus();
	}
	
}
