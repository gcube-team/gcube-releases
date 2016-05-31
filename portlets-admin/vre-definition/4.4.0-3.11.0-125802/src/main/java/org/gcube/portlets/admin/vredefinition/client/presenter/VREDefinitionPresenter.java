package org.gcube.portlets.admin.vredefinition.client.presenter;

import org.gcube.portlets.admin.vredefinition.client.VREDefinitionServiceAsync;
import org.gcube.portlets.admin.vredefinition.client.presenter.VREDescriptionPresenter.Display;
import org.gcube.portlets.admin.vredefinition.client.view.WizardMenuView;
import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class VREDefinitionPresenter {
	
	public interface Display {
		LayoutContainer getUpCenterPanel();  
		LayoutContainer getwestPanel();
		ToolBar getBottomCenterPanel();
		LayoutContainer getUpContainer();
		ContentPanel getEastPanel();
		Widget asWidget();
	}

	 public final Display display;
	 private RootPanel container;
	
	 public VREDefinitionPresenter(Display display) {
		  this.display = display;
		 
	 }
	 
	 
	 public void go(RootPanel container) {
		 this.container = container;
		 
		 container.clear();
		 GCubePanel mainPanel = new GCubePanel("Defining the Virtual Research Environment",
		 "https://gcube.wiki.gcube-system.org/gcube/index.php/VRE_Administration#VRE_Definition");
		// updateSize();
		 mainPanel.add(display.asWidget());
		 container.add(mainPanel);
		 
	 }

	
}
