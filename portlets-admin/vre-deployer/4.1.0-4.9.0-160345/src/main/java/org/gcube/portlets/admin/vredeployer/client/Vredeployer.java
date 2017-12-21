package org.gcube.portlets.admin.vredeployer.client;

import org.gcube.portlets.admin.vredeployer.client.control.Controller;
import org.gcube.portlets.admin.vredeployer.client.model.Model;
import org.gcube.portlets.admin.vredeployer.client.view.panels.MainContainer;
import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Vredeployer implements EntryPoint {
	/**
	 * 
	 */
	public static final String CONTAINER_DIV = "DeployerDIV";
	
	private MainContainer mainContainer;
	private Controller controller;
	private Model model; 

	/**
	 * This is the entry point method.
	 */	
	public void onModuleLoad() {
		GCubePanel gcubePanel = new GCubePanel("VRE Deployer","https://gcube.wiki.gcube-system.org/gcube/index.php/VRE_Administration#VRE_Approval");
		
		controller = new Controller(this);
		mainContainer = new MainContainer(controller);		
		mainContainer.getWestPanel().setDefaultSelected();	
		model = new Model(mainContainer);		
			
		gcubePanel.add(mainContainer); 
		RootPanel.get(CONTAINER_DIV).add(gcubePanel);
		
		//enable the portlet if there isa VRE to approve
		model.checkApproveModeEnabled();
		
		Window.addResizeHandler(new ResizeHandler() {
			
			public void onResize(ResizeEvent event) {
				updateSize();
				
			}
		});
		updateSize();
	}
	/**
	 * updateSize
	 */
	public void updateSize() {
		RootPanel workspace = RootPanel.get(CONTAINER_DIV);
		int topBorder = workspace.getAbsoluteTop();
		int leftBorder = workspace.getAbsoluteLeft();
		int rightScrollBar = 17;
		int rootHeight = Window.getClientHeight() - topBorder - 4;
		int rootWidth = Window.getClientWidth() - 2* leftBorder - rightScrollBar;
		mainContainer.setHeight(rootHeight-30);
		mainContainer.setWidth(rootWidth);

	}
	/**
	 * 
	 * @return the main container
	 */
	public MainContainer getMainContainer() {
		return mainContainer;
	}

	/**
	 * 
	 * @return
	 */
	public Model getModel() {
		return model;
	}

}

