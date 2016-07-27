package org.gcube.portlets.user.reportgenerator.client;

import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.widgets.fileupload.client.view.UploadProgressDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */

public class ToolboxPanel extends VerticalPanel {
	/**
	 * 
	 */
	public static final int TOOLBOX_WIDTH = 235;
	/**
	 * 
	 */
	public static final int TOOLBOX_HEIGHT= 800;

	public ToolboxPanel() {
	}
	
	public void showStructure(ReportStructurePanel panel) {		
		clear();
		setWidth("240px");
		add(panel);
	}
	
	public void collapse() {
		clear();
		GWT.log("collapse");
		setWidth("20px");
	}

	public void showExportedVersion(String id, String fileName) {
		GWT.log("showExportedVersion");
		//AppControllerExplorer.getEventBus().fireEvent(new FileDownloadEvent(id, fileName, DownloadType.SHOW));
	}
	/**
	 * refresh the root
	 */
	public void refreshRoot() {		
		//appController.refreshRoot();
	}
	/**
	 * lalala
	 * @return the toolbox height
	 */
	public int getTreePanelHeight() {
		return TOOLBOX_HEIGHT;
	}
}
