/**
 *
 */

package org.gcube.portlets.user.workspaceexplorerapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class WorkspaceExplorerApp.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Feb 19, 2016
 */
public class WorkspaceExplorerApp implements EntryPoint {


	private WorkspaceExplorerAppController appController;
	private WorkspaceExplorerAppMainPanel mainPanel;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {


		/*boolean jQueryLoaded = isjQueryLoaded();
//		GWT.log("Injected : "+Resources.RESOURCES.jquery().getText());
		GWT.log("jQueryLoaded: "+jQueryLoaded);*/

		/*if (!jQueryLoaded) {
			ScriptInjector.fromString(Resources.RESOURCES.jquery().getText())
			.setWindow(ScriptInjector.TOP_WINDOW)
			.inject();
		}*/

			/*ScriptInjector.fromString(Resources.RESOURCES.jquery().getText())
			.setWindow(ScriptInjector.TOP_WINDOW)
			.inject();*/

		appController = new WorkspaceExplorerAppController();
		mainPanel = new WorkspaceExplorerAppMainPanel(appController.getEventBus(), appController.getDisplayFields());
		appController.go(this);
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				updateSize();
			}
		});

		RootPanel.get(WorkspaceExplorerAppConstants.APPLICATION_DIV).add(mainPanel);

//		WorkspaceExplorerFoooterPanel footerPanel = new WorkspaceExplorerFoooterPanel();
//		RootPanel.get("footer_we").add(footerPanel);

		updateSize();
	}

	/**
	 * Checks if is j query loaded.
	 *
	 * @return true, if is j query loaded
	 */
	 private native boolean isjQueryLoaded() /*-{
		return (typeof $wnd['jQuery'] !== 'undefined');
	}-*/;

	/**
	 * Update window size.
	 */
	public static void updateSize() {

		int headerH = DOM.getElementById("we_nav_bar").getClientHeight();
		GWT.log("headerH " + headerH);
		int footerH = DOM.getElementById("footer_we").getClientHeight();
		GWT.log("footerH " + footerH);

		com.google.gwt.user.client.Element eBread = DOM.getElementById("breadcrumbs_we");
		int breadcrumbsH = 0;
		if(eBread!=null){
			breadcrumbsH = eBread.getClientHeight();
			GWT.log("breadcrumbs_we " + breadcrumbsH);
		}

		int windowHeight = Window.getClientHeight();
		GWT.log("rootHeight " + windowHeight);
		int diff = windowHeight - (headerH+footerH+breadcrumbsH)-10;
		int containerH = diff>0?diff:50;
		NodeList<Element> listE = DOM.getElementById(WorkspaceExplorerAppConstants.APPLICATION_DIV).getElementsByTagName("main");

		if(listE!=null && listE.getLength()>0){
			Element el = listE.getItem(0);
			el.getStyle().setHeight(containerH, Unit.PX);
	//		DOM.getElementById(WorkspaceExplorerAppConstants.APPLICATION_DIV).getStyle().setHeight(containerH, Unit.PX);
			GWT.log("containerH " + containerH);

			Element table = DOM.getElementById("data_grid_explorer");
			if(table!=null){
				int headerTableH = 0;
				table.getStyle().setHeight(containerH-headerTableH, Unit.PX);
			}

		}
	}


	/**
	 * Update explorer panel.
	 *
	 * @param workspaceExplorerAppPanel the workspace explorer app panel
	 */
	public void updateExplorerPanel(WorkspaceExplorerAppPanel workspaceExplorerAppPanel){
		mainPanel.updateToExplorerPanel(workspaceExplorerAppPanel);
	}


	/**
	 * Update to error.
	 *
	 * @param widget the widget
	 */
	public void updateToError(Widget widget){
		mainPanel.updateToError(widget);
	}
}
