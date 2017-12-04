package org.gcube.portlets.user.reportgenerator.client;



import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateModel;
import org.gcube.portlets.user.reportgenerator.client.targets.DoubleColumnPanel;
import org.gcube.portlets.user.reportgenerator.client.uibinder.OpenOptions;
import org.gcube.portlets.user.reportgenerator.client.uibinder.OpenOptionsVME;
import org.gcube.portlets.user.reportgenerator.client.uibinder.ShowLoading;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * <code> WorkspacePanel </code> class is the UI Component for displaying the template
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */

public class WorkspacePanel extends Composite {

	/**
	 * the model
	 */
	private TemplateModel templateModel;
	
	/**
	 * the controller
	 */
	private Presenter presenter;
	/**
	 * 
	 */
	public static WorkspacePanel singleton = null;

	
	/**
	 * used to place two compoenents on the same Y
	 */
	private boolean waitForNextOne = false;
	
	/**
	 * elements arrive one by one, to place two on the same Y this bufferedWidget is used;
	 */
	private Widget bufferedWidget;
	/**
	 * 
	 * @return .
	 */
	public static WorkspacePanel get() {
		return singleton;
	}
	/**
	 * the panel for the layout of the working space
	 */
	private FlowPanel mainLayout = new FlowPanel();
	
	/**
	 * 
	 * @param c the controller instance
	 */
	public WorkspacePanel(Presenter c) {
		singleton = this;
		presenter = c;
		
		templateModel = presenter.getModel();
		mainLayout.setStyleName("wpFlow");	
		
		initWidget(mainLayout);	
		//showOpenOptions();
	}
	
	public void showOpenOptions(boolean isVME, String rsgURL) {
		if (isVME) {
			AlertBlock connectInfo = new AlertBlock(AlertType.SUCCESS);
			connectInfo.setHeading("Succesfully connected to: " + rsgURL);
			mainLayout.add(connectInfo);
			mainLayout.add(new OpenOptionsVME(presenter));
		}
		else
			mainLayout.add(new OpenOptions(presenter));
	}
	
	public void showLoading() {
		mainLayout.clear();
		mainLayout.add(new ShowLoading());
	}
	
	/**
	 * 
	 * @param w .
	 * @param isDoubleColumnLayout .
	 */
	public void addComponentToLayout(Widget w, boolean isDoubleColumnLayout) {
		waitForNextOne = isDoubleColumnLayout;		
		
		if (! waitForNextOne) {
			mainLayout.add(w);
			bufferedWidget = null;
		}
		else {
			if (bufferedWidget == null)
				bufferedWidget = w;
			else {
				DoubleColumnPanel toAdd = new DoubleColumnPanel(bufferedWidget, w);
				mainLayout.add(toAdd);
				bufferedWidget = null;				
			}
		}
		//mainLayout.add(w, x, y);
	}
	
	/**
	 * 
	 * @param w the widget to remove
	 * @return true if the romove is successfull
	 */
	public boolean removeComponentFromLayout(Widget w) {
		return mainLayout.remove(w);
	}
		
	/**
	 * 
	 * @param model .
	 */
	public void setModel(TemplateModel model ) {
		this.templateModel = model;
	}

	/**
	 * resizes the workspace panel 
	 * @param width .
	 * @param height .
	 */
	public void resizeWorkspace(int width, int height) {
		mainLayout.setWidth(""+width);
	}
	
	/**
	 * 
	 * @param toMove .
	 * @param left . 
	 * @param top .
	 */
	public void moveWidget(Widget toMove, int left, int top) {
	//		mainLayout.setWidgetPosition(toMove, left, top);	
//			GWT.log("MOVED? " + top, null);
	}
	
	/**
	 * 
	 * @param toResize the widget to resize
	 * @param newWidth .
	 * @param newHeight .
	 */
	public void resizeWidget(Widget toResize, int newWidth, int newHeight) {
		toResize.setPixelSize(newWidth, newHeight);
	}

	/**
	 * 
	 * @return .
	 */
	public FlowPanel getMainLayout() {
		return mainLayout;
	}
	
}

