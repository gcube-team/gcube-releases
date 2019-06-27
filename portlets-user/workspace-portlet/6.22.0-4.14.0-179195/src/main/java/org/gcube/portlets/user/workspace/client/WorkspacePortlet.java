package org.gcube.portlets.user.workspace.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class WorkspacePortlet implements EntryPoint {
	/**
	 * This is the entry point method.
	 */

	AppController appController;

	public void onModuleLoad() {

		AppControllerExplorer appControllerExplorer = new AppControllerExplorer();
		appController = new AppController(appControllerExplorer);
		appController.go(RootPanel.get(ConstantsPortlet.WORKSPACEDIV));

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				GWT.log("onWindowResized width: "+event.getWidth()+" height: "+event.getHeight());
				updateSize();
				updateTreePanelSize();
			}
		});

		updateSize();
	}

	private void updateTreePanelSize() {
		int rootWidth = Window.getClientWidth() - 2* RootPanel.get(ConstantsPortlet.WORKSPACEDIV).getAbsoluteLeft();
		if (rootWidth < ConstantsPortlet.HIDE_TREE_PANEL_WHEN_WIDTH_LESS_THAN) {
			appController.getMainPanel().collapseTreePanel();
			GWT.log("Collapses tree panel");
			appController.getMainPanel().layout(true);
		} 
	}
	
	/**
	 * Update window size
	 */
	public void updateSize(){

		RootPanel workspace = RootPanel.get(ConstantsPortlet.WORKSPACEDIV);
		int topBorder = workspace.getAbsoluteTop();
		int leftBorder = workspace.getAbsoluteLeft();
		int footer = 85; //footer is bottombar + sponsor

		int rootHeight = Window.getClientHeight() - topBorder - 4 - footer;// - ((footer == null)?0:(footer.getOffsetHeight()-15));
		if (rootHeight < 550)
			rootHeight = 550;

		int rootWidth = Window.getClientWidth() - 2* leftBorder; //- rightScrollBar;
		GWT.log("New workspace dimension Height: "+rootHeight+" Width: "+rootWidth);
		appController.getMainPanel().setHeight(rootHeight);
		appController.getMainPanel().setWidth(rootWidth);
		
	}

	
	//	private void showGuidedTour() {
	//		GWT.runAsync(GCUBEGuidedTour.class, new RunAsyncCallback() {
	//			public void onSuccess() {
	//
	//				GCUBEGuidedTour.showTour(WorkspacePortlet.class.getName(), new GuidedTourResourceProvider() {
	//					@Override
	//					public GuidedTourResource getResource() {
	//						TourResources resources = GWT.create(TourResources.class);
	//						return resources.quickTour();
	//					}
	//				});
	//			}
	//			public void onFailure(Throwable caught) {
	//				Window.alert("Could not check show tour");
	//			}
	//		});
	//	}
}
